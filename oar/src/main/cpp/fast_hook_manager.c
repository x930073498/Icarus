#include "jni.h"
#include <string.h>
#include <sys/mman.h>
#include <stdlib.h>
#include <stdbool.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <pthread.h>
#include <signal.h>
#include <ucontext.h>

#include "enhanced_dlfcn.h"
#include "fast_hook_manager.h"

static inline void InitJit() {
    int max_units = 0;
    void *jit_lib = NULL;
    void *art_lib = NULL;

    if(pointer_size_ == kPointerSize32) {
        jit_lib = enhanced_dlopen("/system/lib/libart-compiler.so", RTLD_NOW);
    }else {
        jit_lib = enhanced_dlopen("/system/lib64/libart-compiler.so", RTLD_NOW);
    }

    if(pointer_size_ == kPointerSize32) {
        art_lib = enhanced_dlopen("/system/lib/libart.so", RTLD_NOW);
    }else {
        art_lib = enhanced_dlopen("/system/lib64/libart.so", RTLD_NOW);
    }

    jit_compile_method_ = (bool (*)(void *, void *, void *, bool)) enhanced_dlsym(jit_lib, "jit_compile_method");
    jit_load_ = (void* (*)(bool*))(enhanced_dlsym(jit_lib, "jit_load"));
    bool will_generate_debug_symbols = false;
    jit_compiler_handle_ = (jit_load_)(&will_generate_debug_symbols);

    art_jit_compiler_handle_ = enhanced_dlsym(art_lib, "_ZN3art3jit3Jit20jit_compiler_handle_E");

    void *compiler_options = (void *)ReadPointer((unsigned char *)jit_compiler_handle_ + pointer_size_);
    memcpy((unsigned char *)compiler_options + 6 * pointer_size_,&max_units,pointer_size_);

    runtime_ = (void *)ReadPointer((unsigned char *)jvm_ + pointer_size_);
}

static inline void *EntryPointToCodePoint(void *entry_point) {
    long code = (long)entry_point;
    code &= ~0x1;

    return (void *)code;
}

static inline void *CodePointToEntryPoint(void *code_point) {
    long entry = (long)code_point;
    entry &= ~0x1;
    entry += 1;

    return (void *)entry;
}

static inline uint16_t GetArtMethodHotnessCount(void *art_method) {
    return ReadInt16((unsigned char *)art_method + kArtMethodHotnessCountOffset);
}

static inline void *GetArtMethodEntryPoint(void *art_method) {
    return (void *)ReadPointer((unsigned char *)art_method + kArtMethodQuickCodeOffset);
}

static inline void *GetArtMethodProfilingInfo(void *art_method) {
    return (void *)ReadPointer((unsigned char *) art_method + kArtMethodProfilingOffset);
}

static inline void *GetProfilingSaveEntryPoint(void *profiling) {
    return (void *)ReadPointer((unsigned char *) profiling + kProfilingSavedEntryPointOffset);
}

static inline bool GetProfilingCompileState(void *profiling) {
    return (bool)ReadInt8((unsigned char *) profiling + kProfilingCompileStateOffset);
}

static inline void SetProfilingSaveEntryPoint(void *profiling, void *entry_point) {
    memcpy((unsigned char *) profiling + kProfilingSavedEntryPointOffset, &entry_point, pointer_size_);
}

static inline void AddArtMethodAccessFlag(void *art_method, uint32_t flag) {
    uint32_t new_flag = ReadInt32((unsigned char *)art_method + kArtMethodAccessFlagsOffset);
    new_flag |= flag;

    memcpy((unsigned char *) art_method + kArtMethodAccessFlagsOffset,&new_flag,4);
}

static inline void ClearArtMethodAccessFlag(void *art_method, uint32_t flag) {
    uint32_t new_flag = ReadInt32((unsigned char *)art_method + kArtMethodAccessFlagsOffset);
    new_flag &= ~flag;

    memcpy((unsigned char *) art_method + kArtMethodAccessFlagsOffset,&new_flag,4);
}

static inline void *CurrentThread() {
    return __get_tls()[kTLSSlotArtThreadSelf];
}

static inline void *CreatTrampoline(int type) {
    void *trampoline = NULL;
    uint32_t size = 0;
    switch(type) {
        case kJumpTrampoline:
            size = RoundUp(sizeof(jump_trampoline_),pointer_size_);
            break;
        case kQuickHookTrampoline:
            size = RoundUp(sizeof(quick_hook_trampoline_),pointer_size_);
            break;
        case kQuickOriginalTrampoline:
        case kQuickTargetTrampoline:
            size = RoundUp(sizeof(quick_target_trampoline_),pointer_size_);
            break;
        case kHookTrampoline:
            size = RoundUp(sizeof(hook_trampoline_),pointer_size_);
            break;
        case kTargetTrampoline:
            size = RoundUp(sizeof(target_trampoline_),pointer_size_);
            break;
    }

    if(size == 0) {
        return 0;
    }

    trampoline = mmap(NULL, size, PROT_READ | PROT_WRITE | PROT_EXEC, MAP_ANON | MAP_PRIVATE, -1, 0);

    switch(type) {
        case kJumpTrampoline:
            memcpy(trampoline, jump_trampoline_, sizeof(jump_trampoline_));
            break;
        case kQuickHookTrampoline:
            memcpy(trampoline, quick_hook_trampoline_, sizeof(quick_hook_trampoline_));
            break;
        case kQuickOriginalTrampoline:
        case kQuickTargetTrampoline:
            memcpy(trampoline, quick_target_trampoline_, sizeof(quick_target_trampoline_));
            break;
        case kHookTrampoline:
            memcpy(trampoline, hook_trampoline_, sizeof(hook_trampoline_));
            break;
        case kTargetTrampoline:
            memcpy(trampoline, target_trampoline_, sizeof(target_trampoline_));
            break;
    }

    LOGI("Type:%d Trampoline:%p",type,trampoline);

    if(trampoline == MAP_FAILED) {
        return 0;
    }

    return trampoline;
}

void SignalHandle(int signal, siginfo_t *info, void *reserved) {
    ucontext_t* context = (ucontext_t*)reserved;
    void *addr = (void *)context->uc_mcontext.fault_address;
    LOGI("Signal:%d FaultAddress:%p TargetCode:%p",signal,addr,sigaction_info_->addr);

    if(sigaction_info_->addr == addr) {
        void *target_code = sigaction_info_->addr;
        int len = sigaction_info_->len;

        long page_size = sysconf(_SC_PAGESIZE);
        unsigned alignment = (unsigned)((unsigned long long)target_code % page_size);
        int ret = mprotect((void *) (target_code - alignment), (size_t) (alignment + len),
                           PROT_READ | PROT_WRITE | PROT_EXEC);
        LOGI("Mprotect:%d Pagesize:%d Alignment:%d",ret,page_size,alignment);
    }
}

void static inline InitTrampoline(int version) {
#if defined(__arm__)
    switch(version) {
        case kAndroidP:
            hook_trampoline_[22] = 0x18;
            break;
        case kAndroidOMR1:
        case kAndroidO:
            hook_trampoline_[22] = 0x1c;
            break;
        case kAndroidNMR1:
        case kAndroidN:
            hook_trampoline_[22] = 0x20;
            break;
        case kAndroidM:
            hook_trampoline_[22] = 0x24;
            break;
        case kAndroidLMR1:
            hook_trampoline_[22] = 0x2c;
            break;
        case kAndroidL:
            hook_trampoline_[22] = 0x28;
            break;
    }
#elif defined(__aarch64__)
    switch(version) {
        case kAndroidP:
            hook_trampoline_[17] = 0x10;
            break;
        case kAndroidOMR1:
        case kAndroidO:
            hook_trampoline_[17] = 0x14;
            break;
        case kAndroidNMR1:
        case kAndroidN:
            hook_trampoline_[17] = 0x18;
            break;
        case kAndroidM:
            hook_trampoline_[17] = 0x18;
            break;
        case kAndroidLMR1:
            hook_trampoline_[17] = 0x1c;
            break;
        case kAndroidL:
            hook_trampoline_[17] = 0x14;
            break;
    }
#endif
}

void DisableHiddenApiCheck(JNIEnv *env, jclass clazz) {
    if(kHiddenApiPolicyOffset > 0) {
        int offset = 0;
        int no_check = 0;

        int policy = ReadInt32((unsigned char *)runtime_ + kHiddenApiPolicyOffset);
        if(policy != kDarkGreyAndBlackList) {
            int index = 0;
            for(index = 0; index < kHiddenApiPolicyScope; index++) {
                if(ReadInt32((unsigned char *)runtime_ + kHiddenApiPolicyOffset + index) == kDarkGreyAndBlackList) {
                    offset = index;
                }else if(ReadInt32((unsigned char *)runtime_ + kHiddenApiPolicyOffset - index) == kDarkGreyAndBlackList) {
                    offset = -index;
                }
            }
        }
        memcpy((unsigned char *)runtime_ + kHiddenApiPolicyOffset + offset,&no_check,4);
        LOGI("Disable Hidden Api Check:%d",ReadInt32((unsigned char *)runtime_ + kHiddenApiPolicyOffset + offset));
    }
}

jint Init(JNIEnv *env, jclass clazz, jint version) {
    int ret = 0;

    pointer_size_ = sizeof(long);

    switch(version) {
        case kAndroidP:
            if(pointer_size_ == kPointerSize32) {
                kHiddenApiPolicyOffset = 840;
            }else {
                kHiddenApiPolicyOffset = 1364;
            }
            kTLSSlotArtThreadSelf = 7;
            kAccCompileDontBother = 0x02000000;
            kArtMethodHotnessCountOffset = 18;
            kArtMethodProfilingOffset = RoundUp(4 * 4 +  2* 2,pointer_size_);
            kArtMethodQuickCodeOffset = RoundUp(4 * 4 +  2* 2,pointer_size_) + pointer_size_;
            kProfilingCompileStateOffset = 4 + pointer_size_;
            kProfilingSavedEntryPointOffset = 4 + pointer_size_ + sizeof(bool) * 2 + 2;
            kHotMethodThreshold = 10000;
            kHotMethodMaxCount = 50;
            kClassSuperOffset = 4 * 2 + 4 * 6;
            break;
        case kAndroidOMR1:
            kAccCompileDontBother = 0x02000000;
        case kAndroidO:
            kTLSSlotArtThreadSelf = 7;
            kArtMethodHotnessCountOffset = 18;
            kArtMethodProfilingOffset = RoundUp(4 * 4 +  2* 2,pointer_size_) + pointer_size_;
            kArtMethodQuickCodeOffset = RoundUp(4 * 4 +  2* 2,pointer_size_) + pointer_size_ * 2;
            kProfilingCompileStateOffset = 4 + pointer_size_;
            kProfilingSavedEntryPointOffset = 4 + pointer_size_ + sizeof(bool) * 2 + 2;
            kHotMethodThreshold = 10000;
            kHotMethodMaxCount = 50;
            kClassSuperOffset = 4 * 2 + 4 * 6;
            break;
        case kAndroidNMR1:
        case kAndroidN:
            kTLSSlotArtThreadSelf = 7;
            kAccCompileDontBother = 0x01000000;
            kArtMethodHotnessCountOffset = 18;
            kArtMethodProfilingOffset = RoundUp(4 * 4 +  2* 2,pointer_size_) + pointer_size_ * 2;
            kArtMethodQuickCodeOffset = RoundUp(4 * 4 +  2* 2,pointer_size_) + pointer_size_ * 3;
            kProfilingCompileStateOffset = 4 + pointer_size_;
            kProfilingSavedEntryPointOffset = 4 + pointer_size_ + sizeof(bool) * 2 + 2;
            kHotMethodThreshold = 10000;
            kHotMethodMaxCount = 50;
            kClassSuperOffset = 4 * 2 + 4 * 6;
            break;
        case kAndroidM:
	        kArtMethodAccessFlagsOffset = 4 * 3;
            kArtMethodInterpreterEntryOffset = RoundUp(4 * 7,pointer_size_);
            kArtMethodQuickCodeOffset = RoundUp(4 * 7,pointer_size_) + pointer_size_ * 2;
            kClassSuperOffset = 4 * 2 + 4 * 6;
            break;
        case kAndroidLMR1:
	        kArtMethodAccessFlagsOffset = 4 * 2 + 4 * 3;
            kArtMethodInterpreterEntryOffset = RoundUp(4 * 2 + 4 * 7,pointer_size_);
            kArtMethodQuickCodeOffset = RoundUp(4 * 2 + 4 * 7,pointer_size_) + pointer_size_ * 2;
            kClassSuperOffset = 4 * 2 + 4 * 9;
            break;
        case kAndroidL:
	        kArtMethodAccessFlagsOffset = 4 * 2 + 4 * 4 + 8 * 4;
            kArtMethodInterpreterEntryOffset = 4 * 2 + 4 * 4;
            kArtMethodQuickCodeOffset = 4 * 2 + 4 * 4 + 8 * 2;
            kClassSuperOffset = 4 * 2 + 4 * 9;
            break;
    }

    InitTrampoline(version);

    sigaction_info_ = (struct SigactionInfo *)malloc(sizeof(struct SigactionInfo));
    sigaction_info_->addr = NULL;
    sigaction_info_->len = 0;

    void *art_lib = NULL;

    if(pointer_size_ == kPointerSize32) {
        art_lib = enhanced_dlopen("/system/lib/libart.so", RTLD_NOW);
    }else {
        art_lib = enhanced_dlopen("/system/lib64/libart.so", RTLD_NOW);
    }

    art_quick_to_interpreter_bridge_ = enhanced_dlsym(art_lib, "art_quick_to_interpreter_bridge");
    new_local_ref_ = (jobject (*)(void*,void*))enhanced_dlsym(art_lib, "_ZN3art9JNIEnvExt11NewLocalRefEPNS_6mirror6ObjectE");
    decode_jobject_ = enhanced_dlsym(art_lib, "_ZNK3art6Thread13DecodeJObjectEP8_jobject");

    if(kTLSSlotArtThreadSelf > 0) {
        InitJit();
    }

    return ret;
}

jobject Is32bit(JNIEnv *env, jclass clazz) {
    if(sizeof(long) == kPointerSize32) {
        return true;
    }
    return false;
}

jobject GetReflectedMethod(JNIEnv *env, jclass clazz, jlong art_method) {
    jobject result = (*env)->ToReflectedMethod(env,clazz,(void *)art_method,JNI_FALSE);
    return result;
}

jboolean GetBooleanParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jboolean result = (jboolean)ReadInt32((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jbyte GetByteParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jbyte result = (jbyte)ReadInt32((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jchar GetCharParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jchar result = (jchar)ReadInt32((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jshort GetShortParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jshort result = (jshort)ReadInt32((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jint GetIntParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jint result = (jint)ReadInt32((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jlong GetLongParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jlong result = (jlong)ReadInt64((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jfloat GetFloatParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jfloat result = (jfloat)ReadFloat((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jdouble GetDoubleParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    jdouble result = (jdouble)ReadDouble((unsigned char *)sp + pointer_size_ + offset);
    return result;
}

jobject GetObjectParam(JNIEnv *env, jclass clazz, jlong sp, jint offset) {
    void *obj = (void *)ReadInt32((unsigned char *)sp + pointer_size_ + offset);
    jobject result = new_local_ref_(env,obj);
    return result;
}

jobject GetReflectedMethod32(JNIEnv *env, jclass clazz, jint art_method) {
    jobject result = (*env)->ToReflectedMethod(env,clazz,(void *)art_method,JNI_FALSE);
    return result;
}

jboolean GetBooleanParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    jboolean result = (jboolean)ReadInt32((unsigned char *)param_array + offset);
    return result;
}

jbyte GetByteParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    jbyte result = (jbyte)ReadInt32((unsigned char *)param_array + offset);
    return result;
}

jchar GetCharParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    jchar result = (jchar)ReadInt32((unsigned char *)param_array + offset);
    return result;
}

jshort GetShortParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    jshort result = (jshort)ReadInt32((unsigned char *)param_array + offset);
    return result;
}

jint GetIntParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    jint result = (jint)ReadInt32((unsigned char *)param_array + offset);
    return result;
}

jlong GetLongParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    jlong result = (jlong)ReadInt64((unsigned char *)param_array + offset);
    return result;
}

jfloat GetFloatParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kFpRegArgs:
            param_array = ReadPointer((unsigned char *)address + kFloatParamArrayOffset);
            break;
    }
    jfloat result = (jfloat)ReadFloat((unsigned char *)param_array + offset);
    return result;
}

jdouble GetDoubleParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kFpRegArgs:
            param_array = ReadPointer((unsigned char *)address + kFloatParamArrayOffset);
            break;
    }
    jdouble result = (jdouble)ReadDouble((unsigned char *)param_array + offset);
    return result;
}

jobject GetObjectParam32(JNIEnv *env, jclass clazz, jint address, jint offset, jint type) {
    void* param_array = NULL;
    switch (type) {
        case kSp:
            param_array = address;
            break;
        case kCoreRegArgs:
            param_array = ReadPointer((unsigned char *)address + kCoreParamArrayOffset);
            break;
    }
    void *obj = (void *)ReadInt32((unsigned char *)param_array + offset);
    jobject result = new_local_ref_(env,obj);
    return result;
}

void PoseAsObject(JNIEnv *env, jclass clazz, jclass target_class, jlong thread) {
    int super_class = 0;
    void *art_target_class = decode_jobject_(thread,target_class);
    memcpy((unsigned char *)art_target_class + kClassSuperOffset,&super_class,4);
}

jobject ConstructorToMethod(JNIEnv *env, jclass clazz, jobject method) {
    jobject result = NULL;
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    ClearArtMethodAccessFlag(art_method,kAccConstructor);
    result = (*env)->ToReflectedMethod(env,clazz,(void *)art_method,JNI_FALSE);

    return result;
}

void MethodToConstructor(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    AddArtMethodAccessFlag(art_method,kAccConstructor);
}

void DisableJITInline(JNIEnv *env, jclass clazz) {
    int max_units = 0;

    void *art_compiler_options = (void *)ReadPointer((unsigned char *)(*art_jit_compiler_handle_) + pointer_size_);

    memcpy((unsigned char *)art_compiler_options + 6 * pointer_size_,&max_units,pointer_size_);
    LOGI("DisableJITInline %d",ReadInt32((unsigned char *)art_compiler_options + 6 * pointer_size_));
}

long GetMethodEntryPoint(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    long entry_point = ReadPointer((unsigned char *)art_method + kArtMethodQuickCodeOffset);

    return entry_point;
}

bool CompileMethod(JNIEnv *env, jclass clazz, jobject method) {
    bool ret = false;

    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);
    void *thread = CurrentThread();
    int old_flag_and_state = ReadInt32(thread);

    ret = jit_compile_method_(jit_compiler_handle_, art_method, thread, false);
    memcpy(thread,&old_flag_and_state,4);
    LOGI("CompileMethod:%d",ret);

    return ret;
}

bool IsCompiled(JNIEnv *env, jclass clazz, jobject method) {
    bool ret = false;

    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);
    void *method_entry = (void *)ReadPointer((unsigned char *)art_method + kArtMethodQuickCodeOffset);
    int hotness_count = GetArtMethodHotnessCount(art_method);

    if(method_entry != art_quick_to_interpreter_bridge_)
        ret = true;

    if(kTLSSlotArtThreadSelf > 0) {
        if(!ret && hotness_count >= kHotMethodThreshold)
            ret = true;
    }

    LOGI("IsCompiled:%d",ret);
    return ret;
}

bool DoRewriteHookCheck(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);
    void *method_entry = (void *)ReadPointer((unsigned char *)art_method + kArtMethodQuickCodeOffset);
#if defined(__arm__)
    void *method_code = EntryPointToCodePoint(method_entry);
#elif defined(__aarch64__)
    void *method_code = method_entry;
#endif

    uint32_t size = ReadInt32((unsigned char *)method_code - 4) & kCodeSizeMask;

    if(size < sizeof(jump_trampoline_)) {
        return false;
    }

#if defined(__arm__)

    int index = 0;
    while(index < sizeof(jump_trampoline_)) {
        uint16_t inst = ReadInt16((unsigned char *)method_code + index);
        if(IsThumb32(inst,IsLittleEnd())) {
            uint32_t inst_32 = ReadInt32((unsigned char *)method_code + index + 4);
            if(HasThumb32PcRelatedInst(inst_32))
                return false;

            index += 4;
        }else {
            if(HasThumb16PcRelatedInst(inst))
                return false;

            index += 2;
        }
    }

    return true;
#elif defined(__aarch64__)

    int index = 0;
    while(index < sizeof(jump_trampoline_)) {
        uint32_t inst = ReadInt32((unsigned char *)method_code + index);
        if(HasArm64PcRelatedInst(inst))
            return false;

        index += 4;
    }

    return true;
#endif
}

bool IsNativeMethod(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    uint32_t flag = ReadPointer((unsigned char *)art_method + kArtMethodAccessFlagsOffset);
    if(flag & kAccNative) {
        return true;
    }

    return false;
}

bool IsStaticMethod(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    uint32_t flag = ReadPointer((unsigned char *)art_method + kArtMethodAccessFlagsOffset);
    if(flag & kAccStatic) {
        return true;
    }

    return false;
}

void SetNativeMethod(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    AddArtMethodAccessFlag(art_method,kAccNative);
}

void SetDirectMethod(JNIEnv *env, jclass clazz, jobject method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, method);

    AddArtMethodAccessFlag(art_method,kAccPrivate);
}

int CheckJitState(JNIEnv *env, jclass clazz, jobject target_method) {
    void *art_method = (void *)(*env)->FromReflectedMethod(env, target_method);

    AddArtMethodAccessFlag(art_method, kAccCompileDontBother);

    uint32_t hotness_count = GetArtMethodHotnessCount(art_method);

    LOGI("TargetMethod:%p QuickToInterpreterBridge:%p hotness_count:%hd",art_method,art_quick_to_interpreter_bridge_,hotness_count);
    if(hotness_count >= kHotMethodThreshold) {
        long entry_point = (long)GetArtMethodEntryPoint(art_method);
        if((void *)entry_point == art_quick_to_interpreter_bridge_) {
            void *profiling = GetArtMethodProfilingInfo(art_method);
            void *save_entry_point = GetProfilingSaveEntryPoint(profiling);
            if(save_entry_point) {
                return kCompile;
            }else {
                bool being_compiled = GetProfilingCompileState(profiling);
                if(being_compiled) {
                    return kCompiling;
                }else {
                    return kCompilingOrFailed;
                }
            }
        }

        return kCompile;
    }else {
        uint32_t assumed_hotness_count = hotness_count + kHotMethodMaxCount;
        if(assumed_hotness_count > kHotMethodThreshold) {
            return kCompiling;
        }
    }

    return kNone;
}

jint DoFullRewriteHook(JNIEnv *env, jclass clazz, jobject target_method, jobject hook_method, jobject forward_method, jobject head_record, jobject target_record, jobject tail_record) {
    void *art_target_method = (void *)(*env)->FromReflectedMethod(env, target_method);
    void *art_hook_method = (void *)(*env)->FromReflectedMethod(env, hook_method);
    void *art_forward_method = NULL;
    if(forward_method != NULL) {
        art_forward_method = (void *)(*env)->FromReflectedMethod(env, forward_method);
    }

    void *jump_trampoline = CreatTrampoline(kJumpTrampoline);
    void *quick_hook_trampoline = CreatTrampoline(kQuickHookTrampoline);
    void *quick_original_trampoline = CreatTrampoline(kQuickOriginalTrampoline);
    void *quick_target_trampoline = NULL;
    if(art_forward_method) {
        quick_target_trampoline = CreatTrampoline(kQuickTargetTrampoline);
    }

#if defined(__arm__)

    void *target_entry = (void *)ReadPointer((unsigned char *) art_target_method + kArtMethodQuickCodeOffset);
    void *target_code = EntryPointToCodePoint(target_entry);
    void *quick_hook_trampoline_entry = CodePointToEntryPoint(quick_hook_trampoline);
    void *hook_entry = (void *)ReadPointer((unsigned char *) art_hook_method + kArtMethodQuickCodeOffset);
    void *next_entry = CodePointToEntryPoint(quick_original_trampoline);
    void *forward_entry = CodePointToEntryPoint(quick_target_trampoline);

    int jump_trampoline_len = 2 * 4;
    int quick_hook_trampoline_len = 16 * 4;
    int quick_target_trampoline_len = 7 * 4;
    int quick_original_trampoline_len = 7 * 4;
    int original_prologue_len = 0;
    while(original_prologue_len < jump_trampoline_len) {
        if(IsThumb32(ReadInt16((unsigned char *)target_code + original_prologue_len),IsLittleEnd())) {
            original_prologue_len += 4;
        }else {
            original_prologue_len += 2;
        }
    }
    LOGI("OriginalPrologueLen:%d",original_prologue_len);

    int jump_trampoline_entry_index = 4;

    int quick_hook_trampoline_target_index = 48;
    int quick_hook_trampoline_hook_index = 52;
    int quick_hook_trampoline_hook_entry_index = 56;
    int quick_hook_trampoline_next_entry_index = 60;

    int quick_target_trampoline_original_index = 4;
    int quick_target_trampoline_target_index = 20;
    int quick_target_trampoline_original_next_entry = 24;

    int quick_original_trampoline_target_index = 4;
    int quick_original_trampoline_original_next_entry = 24;

    void *original_next_entry = CodePointToEntryPoint((void *)((long)target_entry + original_prologue_len));

    unsigned char *original_code = (unsigned char *) quick_original_trampoline;
    original_code[0] = 0x00;
    original_code[1] = 0xbf;
    original_code[2] = 0x00;
    original_code[3] = 0xbf;

#elif defined(__aarch64__)

    void *target_entry = (void *)ReadPointer((unsigned char *) art_target_method + kArtMethodQuickCodeOffset);
	void *target_code = target_entry;
	void *quick_hook_trampoline_entry = quick_hook_trampoline;
	void *hook_entry = (void *)ReadPointer((unsigned char *) art_hook_method + kArtMethodQuickCodeOffset);
	void *next_entry = quick_original_trampoline;
	void *forward_entry = quick_target_trampoline;

	int jump_trampoline_len = 4 * 4;
	int quick_hook_trampoline_len = 19 * 4;
	int quick_target_trampoline_len = 11 * 4;
	int quick_original_trampoline_len = 11 * 4;
	int original_prologue_len = 16;

	int jump_trampoline_entry_index = 8;

	int quick_hook_trampoline_target_index = 44;
	int quick_hook_trampoline_hook_index = 52;
	int quick_hook_trampoline_hook_entry_index = 60;
	int quick_hook_trampoline_next_entry_index = 68;

	int quick_target_trampoline_original_index = 4;
	int quick_target_trampoline_target_index = 28;
	int quick_target_trampoline_original_next_entry = 36;

	int quick_original_trampoline_target_index = 4;
	int quick_original_trampoline_original_next_entry = 36;

	void *original_next_entry = (void *)((long)target_entry + original_prologue_len);

	unsigned char *original_code = (unsigned char *) quick_original_trampoline;
	original_code[0] = 0x1f;
	original_code[1] = 0x20;
	original_code[2] = 0x03;
	original_code[3] = 0xd5;

#endif

    unsigned char original_prologue[original_prologue_len];
    memcpy(original_prologue,(unsigned char *)target_code,original_prologue_len);
    for(int i = 0;i < 3;i++) {
        LOGI("OriginalPrologue[%d] %x %x %x %x",i,((unsigned char*)original_prologue)[i*4+0],((unsigned char*)original_prologue)[i*4+1],((unsigned char*)original_prologue)[i*4+2],((unsigned char*)original_prologue)[i*4+3]);
    }

    memcpy(jump_trampoline + jump_trampoline_entry_index, &quick_hook_trampoline_entry, pointer_size_);
    for(int i = 0;i < jump_trampoline_len/4;i++) {
        LOGI("JumpTrampoline[%d] %x %x %x %x",i,((unsigned char*)jump_trampoline)[i*4+0],((unsigned char*)jump_trampoline)[i*4+1],((unsigned char*)jump_trampoline)[i*4+2],((unsigned char*)jump_trampoline)[i*4+3]);
    }


    memcpy(quick_hook_trampoline + quick_hook_trampoline_target_index, &art_target_method, pointer_size_);
    memcpy(quick_hook_trampoline + quick_hook_trampoline_hook_index, &art_hook_method, pointer_size_);
    memcpy(quick_hook_trampoline + quick_hook_trampoline_hook_entry_index, &hook_entry, pointer_size_);
    memcpy(quick_hook_trampoline + quick_hook_trampoline_next_entry_index, &next_entry, pointer_size_);
    for(int i = 0;i < quick_hook_trampoline_len/4;i++) {
        LOGI("QuickHookTrampoline[%d] %x %x %x %x",i,((unsigned char*)quick_hook_trampoline)[i*4+0],((unsigned char*)quick_hook_trampoline)[i*4+1],((unsigned char*)quick_hook_trampoline)[i*4+2],((unsigned char*)quick_hook_trampoline)[i*4+3]);
    }

    if(art_forward_method) {
        memcpy(quick_target_trampoline + quick_target_trampoline_original_index, original_prologue, original_prologue_len);
        memcpy(quick_target_trampoline + quick_target_trampoline_target_index, &art_target_method, pointer_size_);
        memcpy(quick_target_trampoline + quick_target_trampoline_original_next_entry, &original_next_entry, pointer_size_);
        for(int i = 0;i < quick_target_trampoline_len/4;i++) {
            LOGI("QuickTargetTrampoline[%d] %x %x %x %x",i,((unsigned char*)quick_target_trampoline)[i*4+0],((unsigned char*)quick_target_trampoline)[i*4+1],((unsigned char*)quick_target_trampoline)[i*4+2],((unsigned char*)quick_target_trampoline)[i*4+3]);
        }
    }

    memcpy(quick_original_trampoline + quick_original_trampoline_target_index, original_prologue, original_prologue_len);
    memcpy(quick_original_trampoline + quick_original_trampoline_original_next_entry, &original_next_entry, pointer_size_);
    for(int i = 0;i < quick_original_trampoline_len/4;i++) {
        LOGI("QuickOriginalTrampoline[%d] %x %x %x %x",i,((unsigned char*)quick_original_trampoline)[i*4+0],((unsigned char*)quick_original_trampoline)[i*4+1],((unsigned char*)quick_original_trampoline)[i*4+2],((unsigned char*)quick_original_trampoline)[i*4+3]);
    }

    if(art_forward_method) {
        memcpy((unsigned char *) art_forward_method + kArtMethodQuickCodeOffset,&forward_entry,pointer_size_);
        LOGI("Forward NewEntry:%p",ReadPointer((unsigned char *) art_forward_method + kArtMethodQuickCodeOffset));
    }

    __builtin___clear_cache(quick_hook_trampoline, quick_hook_trampoline + quick_hook_trampoline_len);
    __builtin___clear_cache(quick_original_trampoline, quick_original_trampoline + quick_original_trampoline_len);
    if(art_forward_method) {
        __builtin___clear_cache(quick_target_trampoline, quick_target_trampoline + quick_target_trampoline_len);
    }

    sigaction_info_->addr = target_code;
    sigaction_info_->len = jump_trampoline_len;
    if(current_handler_ == NULL) {
        default_handler_ = (struct sigaction *)malloc(sizeof(struct sigaction));
        current_handler_ = (struct sigaction *)malloc(sizeof(struct sigaction));
        memset(default_handler_, 0, sizeof(sigaction));
        memset(current_handler_, 0, sizeof(sigaction));

        current_handler_->sa_sigaction = SignalHandle;
        current_handler_->sa_flags = SA_SIGINFO;

        sigaction(SIGSEGV, current_handler_, default_handler_);
    }else {
        sigaction(SIGSEGV, current_handler_, NULL);
    }

    long page_size = sysconf(_SC_PAGESIZE);
    unsigned alignment = (unsigned)((unsigned long long)target_code % page_size);
    int ret = mprotect((void *) (target_code - alignment), (size_t) (alignment + jump_trampoline_len),
                       PROT_READ | PROT_WRITE | PROT_EXEC);
    LOGI("Mprotect:%d Pagesize:%d Alignment:%d",ret,page_size,alignment);

    memcpy((unsigned char *) art_target_method + kArtMethodQuickCodeOffset,&art_quick_to_interpreter_bridge_,pointer_size_);
    memcpy(target_code, jump_trampoline, jump_trampoline_len);
    for(int i = 0;i < jump_trampoline_len/4;i++) {
        LOGI("TargetCode[%d] %x %x %x %x",i,((unsigned char *)target_code)[i*4+0],((unsigned char *)target_code)[i*4+1],((unsigned char *)target_code)[i*4+2],((unsigned char *)target_code)[i*4+3]);
    }

    sigaction_info_->addr = NULL;
    sigaction_info_->len = 0;
    sigaction(SIGSEGV, default_handler_, NULL);

    memcpy((unsigned char *) art_target_method + kArtMethodQuickCodeOffset,&target_entry,pointer_size_);

    __builtin___clear_cache(target_code, target_code + jump_trampoline_len);

    (*env)->SetLongField(env, head_record, kHookRecordClassInfo.jump_trampoline_, (long)jump_trampoline);
    (*env)->SetLongField(env, target_record, kHookRecordClassInfo.quick_hook_trampoline_, (long)quick_hook_trampoline);
    if(art_forward_method) {
        (*env)->SetLongField(env, target_record, kHookRecordClassInfo.quick_target_trampoline_, (long)quick_target_trampoline);
    }
    (*env)->SetLongField(env, tail_record, kHookRecordClassInfo.quick_original_trampoline_, (long)quick_original_trampoline);

    return 0;
}

jint DoPartRewriteHook(JNIEnv *env, jclass clazz, jobject target_method, jobject hook_method, jobject forward_method, jlong quick_original_trampoline, jlong prev_quick_hook_trampoline, jobject target_record) {
    void *art_target_method = (void *)(*env)->FromReflectedMethod(env, target_method);
    void *art_hook_method = (void *)(*env)->FromReflectedMethod(env, hook_method);
    void *art_forward_method = NULL;
    if(forward_method != NULL) {
        art_forward_method = (void *)(*env)->FromReflectedMethod(env, forward_method);
    }

    void *quick_hook_trampoline = CreatTrampoline(kQuickHookTrampoline);
    void *quick_target_trampoline = NULL;
    if(art_forward_method) {
        quick_target_trampoline = CreatTrampoline(kQuickTargetTrampoline);
    }

#if defined(__arm__)

    void *target_entry = (void *)ReadPointer((unsigned char *) art_target_method + kArtMethodQuickCodeOffset);
    void *hook_entry = (void *)ReadPointer((unsigned char *) art_hook_method + kArtMethodQuickCodeOffset);
    void *next_entry = CodePointToEntryPoint((void *)quick_original_trampoline);
    void *prev_next_entry = CodePointToEntryPoint(quick_hook_trampoline);
    void *forward_entry = CodePointToEntryPoint(quick_target_trampoline);

    int quick_hook_trampoline_len = 16 * 4;
    int quick_target_trampoline_len = 7 * 4;

    int quick_hook_trampoline_target_index = 48;
    int quick_hook_trampoline_hook_index = 52;
    int quick_hook_trampoline_hook_entry_index = 56;
    int quick_hook_trampoline_next_entry_index = 60;

    int original_prologue_len = 12;
    int quick_original_trampoline_original_index = 4;
    int quick_original_trampoline_next_entry_index = 24;

    int quick_target_trampoline_original_index = 4;
    int quick_target_trampoline_target_index = 20;
    int quick_target_trampoline_next_entry_index = 24;

#elif defined(__aarch64__)

    void *target_entry = (void *)ReadPointer((unsigned char *) art_target_method + kArtMethodQuickCodeOffset);
	void *hook_entry = (void *)ReadPointer((unsigned char *) art_hook_method + kArtMethodQuickCodeOffset);
	void *next_entry = (void *)quick_original_trampoline;
	void *prev_next_entry = quick_hook_trampoline;
	void *forward_entry = quick_target_trampoline;

	int quick_hook_trampoline_len = 16 * 4;
	int quick_target_trampoline_len = 11 * 4;

	int quick_hook_trampoline_target_index = 32;
	int quick_hook_trampoline_hook_index = 40;
	int quick_hook_trampoline_hook_entry_index = 48;
	int quick_hook_trampoline_next_entry_index = 56;

	int original_prologue_len = 16;
	int quick_original_trampoline_original_index = 4;
	int quick_original_trampoline_next_entry_index = 36;

	int quick_target_trampoline_original_index = 4;
	int quick_target_trampoline_target_index = 28;
	int quick_target_trampoline_next_entry_index = 36;

#endif

    memcpy(quick_hook_trampoline + quick_hook_trampoline_target_index, &art_target_method, pointer_size_);
    memcpy(quick_hook_trampoline + quick_hook_trampoline_hook_index, &art_hook_method, pointer_size_);
    memcpy(quick_hook_trampoline + quick_hook_trampoline_hook_entry_index, &hook_entry, pointer_size_);
    memcpy(quick_hook_trampoline + quick_hook_trampoline_next_entry_index, &next_entry, pointer_size_);
    for(int i = 0;i < quick_hook_trampoline_len/4;i++) {
        LOGI("QuickHookTrampoline[%d] %x %x %x %x",i,((unsigned char*)quick_hook_trampoline)[i*4+0],((unsigned char*)quick_hook_trampoline)[i*4+1],((unsigned char*)quick_hook_trampoline)[i*4+2],((unsigned char*)quick_hook_trampoline)[i*4+3]);
    }

    if(art_forward_method) {
        unsigned char original_prologue[original_prologue_len];
        memcpy(original_prologue,(unsigned char *)quick_original_trampoline + quick_original_trampoline_original_index,original_prologue_len);
        for(int i = 0;i < original_prologue_len/4;i++) {
            LOGI("OriginalPrologue[%d] %x %x %x %x",i,((unsigned char*)original_prologue)[i*4+0],((unsigned char*)original_prologue)[i*4+1],((unsigned char*)original_prologue)[i*4+2],((unsigned char*)original_prologue)[i*4+3]);
        }

        void *original_next_entry = (unsigned char *)quick_original_trampoline + quick_original_trampoline_next_entry_index;
        memcpy(quick_target_trampoline + quick_target_trampoline_original_index, original_prologue, original_prologue_len);
        memcpy(quick_target_trampoline + quick_target_trampoline_target_index, &art_target_method, pointer_size_);
        memcpy(quick_target_trampoline + quick_target_trampoline_next_entry_index, &original_next_entry, pointer_size_);
        for(int i = 0;i < quick_target_trampoline_len/4;i++) {
            LOGI("QuickTargetTrampoline[%d] %x %x %x %x",i,((unsigned char*)quick_target_trampoline)[i*4+0],((unsigned char*)quick_target_trampoline)[i*4+1],((unsigned char*)quick_target_trampoline)[i*4+2],((unsigned char*)quick_target_trampoline)[i*4+3]);
        }

        void *forward_entry = CodePointToEntryPoint(quick_target_trampoline);
        memcpy((char *) art_forward_method + kArtMethodQuickCodeOffset,&forward_entry,pointer_size_);
        LOGI("Forward NewEntry:%p",ReadPointer((unsigned char *) art_forward_method + kArtMethodQuickCodeOffset));
    }

    __builtin___clear_cache(quick_hook_trampoline, quick_hook_trampoline + quick_hook_trampoline_len);
    if(art_forward_method) {
        __builtin___clear_cache(quick_target_trampoline, quick_target_trampoline + quick_target_trampoline_len);
    }

    memcpy((char *) art_target_method + kArtMethodQuickCodeOffset,&art_quick_to_interpreter_bridge_,pointer_size_);
    memcpy((void *)(prev_quick_hook_trampoline + quick_hook_trampoline_next_entry_index), &prev_next_entry, pointer_size_);
    for(int i = 0;i < quick_hook_trampoline_len/4;i++) {
        LOGI("PrevQuickHookTrampoline[%d] %x %x %x %x",i,((unsigned char*)prev_quick_hook_trampoline)[i*4+0],((unsigned char*)prev_quick_hook_trampoline)[i*4+1],((unsigned char*)prev_quick_hook_trampoline)[i*4+2],((unsigned char*)prev_quick_hook_trampoline)[i*4+3]);
    }
    memcpy((char *) art_target_method + kArtMethodQuickCodeOffset,&target_entry,pointer_size_);

    __builtin___clear_cache((void *)prev_quick_hook_trampoline, (unsigned char*)prev_quick_hook_trampoline + quick_hook_trampoline_len);

    (*env)->SetLongField(env, target_record, kHookRecordClassInfo.quick_hook_trampoline_, (long)quick_hook_trampoline);
    if(art_forward_method) {
        (*env)->SetLongField(env, target_record, kHookRecordClassInfo.quick_target_trampoline_, (long)quick_target_trampoline);
    }

    return 0;
}

jint DoReplaceHook(JNIEnv *env, jclass clazz, jobject target_method, jobject hook_method, jobject forward_method, jboolean is_native, jobject target_record) {
    void *art_target_method = (void *)(*env)->FromReflectedMethod(env, target_method);
    void *art_hook_method = (void *)(*env)->FromReflectedMethod(env, hook_method);
    void *art_forward_method = NULL;
    if(forward_method != NULL) {
        art_forward_method = (void *)(*env)->FromReflectedMethod(env, forward_method);
    }

    void *target_entry = NULL;
    if(is_native) {
        target_entry = GetArtMethodEntryPoint(art_target_method);
    }else {
        target_entry = art_quick_to_interpreter_bridge_;
    }

    void *hook_trampoline = CreatTrampoline(kHookTrampoline);
    void *target_trampoline = CreatTrampoline(kTargetTrampoline);

#if defined(__arm__)

    int hook_trampoline_len = 7 * 4;
    int target_trampoline_len = 4 * 4;

    int hook_trampoline_target_index = 24;
    int target_trampoline_target_index = 8;
    int target_trampoline_target_entry_index = 12;

    void *new_target_entry = CodePointToEntryPoint(hook_trampoline);
    void *new_forward_entry =  CodePointToEntryPoint(target_trampoline);

#elif defined(__aarch64__)

    int hook_trampoline_len = 8 * 4;
	int target_trampoline_len = 7 * 4;

	int hook_trampoline_target_index = 24;
	int target_trampoline_target_index = 12;
	int target_trampoline_target_entry_index = 20;

	void *new_target_entry = hook_trampoline;
	void *new_forward_entry =  target_trampoline;

#endif

    memcpy((unsigned char *) hook_trampoline + hook_trampoline_target_index, &art_hook_method, pointer_size_);

    LOGI("HookTrampoline:%p HookMethod:%p TargetMethod:%p ForwardMethod:%p",hook_trampoline,art_hook_method,art_target_method,art_forward_method);
    for(int i = 0; i < hook_trampoline_len/4; i++) {
        LOGI("HookTrampoline[%d] %x %x %x %x",i,((unsigned char*)hook_trampoline)[i*4+0],((unsigned char*)hook_trampoline)[i*4+1],((unsigned char*)hook_trampoline)[i*4+2],((unsigned char*)hook_trampoline)[i*4+3]);
    }

    if(art_forward_method) {
        memcpy((unsigned char *) target_trampoline + target_trampoline_target_index, &art_target_method, pointer_size_);
        memcpy((unsigned char *) target_trampoline + target_trampoline_target_entry_index, &target_entry, pointer_size_);

        if(kTLSSlotArtThreadSelf) {
            uint32_t hotness_count = GetArtMethodHotnessCount(art_target_method);
            LOGI("TargetTrampoline:%p TargetMethod:%p QuickToInterpreterBridge:%p",target_trampoline,art_target_method,art_quick_to_interpreter_bridge_);
            if(hotness_count >= kHotMethodThreshold) {
                void *profiling = GetArtMethodProfilingInfo(art_target_method);
                void *save_entry_point = GetProfilingSaveEntryPoint(profiling);
                if(save_entry_point) {
                    SetProfilingSaveEntryPoint(profiling,art_quick_to_interpreter_bridge_);
                }
            }

        }

        for(int i = 0; i < target_trampoline_len/4; i++) {
            LOGI("TargetTrampoline[%d] %x %x %x %x",i,((unsigned char*)target_trampoline)[i*4+0],((unsigned char*)target_trampoline)[i*4+1],((unsigned char*)target_trampoline)[i*4+2],((unsigned char*)target_trampoline)[i*4+3]);
        }
    }

    memcpy((unsigned char *) art_target_method + kArtMethodQuickCodeOffset,&new_target_entry,pointer_size_);
    LOGI("Target NewEntry:%p",ReadPointer((unsigned char *) art_target_method + kArtMethodQuickCodeOffset));
    if(art_forward_method) {
        memcpy((unsigned char *) art_forward_method + kArtMethodQuickCodeOffset,&new_forward_entry,pointer_size_);
        LOGI("Forward NewEntry:%p",ReadPointer((unsigned char *) art_forward_method + kArtMethodQuickCodeOffset));
    }

    if(kArtMethodInterpreterEntryOffset > 0) {
        if(art_forward_method) {
            memcpy((unsigned char *) art_forward_method + kArtMethodInterpreterEntryOffset,(unsigned char *) art_target_method + kArtMethodInterpreterEntryOffset,pointer_size_);
        }
        memcpy((unsigned char *) art_target_method + kArtMethodInterpreterEntryOffset,(unsigned char *) art_hook_method + kArtMethodInterpreterEntryOffset,pointer_size_);
    }

    (*env)->SetLongField(env, target_record, kHookRecordClassInfo.hook_trampoline_, (long)hook_trampoline);
    if(art_forward_method) {
        (*env)->SetLongField(env, target_record, kHookRecordClassInfo.target_trampoline_, (long)target_trampoline);
    }

    __builtin___clear_cache(hook_trampoline, hook_trampoline + hook_trampoline_len);
    if(art_forward_method) {
        __builtin___clear_cache(target_trampoline, target_trampoline + target_trampoline_len);
    }

    return 0;
}

static JNINativeMethod JniMethods[] = {

        {"disableHiddenApiCheck",             "()V",                                                          (void *) DisableHiddenApiCheck},
        {"init",               				  "(I)V",                                                         (void *) Init},
        {"is32bit",               			   "()Z",                                                         (void *) Is32bit},
        {"getReflectedMethod",                "(J)Ljava/lang/reflect/Member;",                                (void *) GetReflectedMethod},
        {"getReflectedMethod",                "(I)Ljava/lang/reflect/Member;",                                (void *) GetReflectedMethod32},
        {"getBooleanParam",                   "(JI)Z",                                                        (void *) GetBooleanParam},
        {"getBooleanParam",                   "(III)Z",                                                        (void *) GetBooleanParam32},
        {"getByteParam",                      "(JI)B",                                                        (void *) GetByteParam},
        {"getByteParam",                      "(III)B",                                                        (void *) GetByteParam32},
        {"getCharParam",                      "(JI)C",                                                        (void *) GetCharParam},
        {"getCharParam",                      "(III)C",                                                        (void *) GetCharParam32},
        {"getShortParam",                     "(JI)S",                                                        (void *) GetShortParam},
        {"getShortParam",                     "(III)S",                                                        (void *) GetShortParam32},
        {"getIntParam",                       "(JI)I",                                                        (void *) GetIntParam},
        {"getIntParam",                       "(III)I",                                                        (void *) GetIntParam32},
        {"getLongParam",                      "(JI)J",                                                        (void *) GetLongParam},
        {"getLongParam",                      "(III)J",                                                        (void *) GetLongParam32},
        {"getFloatParam",                     "(JI)F",                                                        (void *) GetFloatParam},
        {"getFloatParam",                     "(III)F",                                                        (void *) GetFloatParam32},
        {"getDoubleParam",                    "(JI)D",                                                        (void *) GetDoubleParam},
        {"getDoubleParam",                    "(III)D",                                                        (void *) GetDoubleParam32},
        {"getObjectParam",                    "(JI)Ljava/lang/Object;",                                       (void *) GetObjectParam},
        {"getObjectParam",                    "(III)Ljava/lang/Object;",                                       (void *) GetObjectParam32},
        {"poseAsObject",                      "(Ljava/lang/Class;J)V",                                         (void *) PoseAsObject},
        {"constructorToMethod",               "(Ljava/lang/reflect/Member;)Ljava/lang/reflect/Method;",       (void *) ConstructorToMethod},
        {"methodToConstructor",               "(Ljava/lang/reflect/Member;)V",                                (void *) MethodToConstructor},
        {"disableJITInline",               	  "()V",                                                          (void *) DisableJITInline},
        {"getMethodEntryPoint",               "(Ljava/lang/reflect/Member;)J",                                (void *) GetMethodEntryPoint},
        {"compileMethod",            		  "(Ljava/lang/reflect/Member;)Z",                                (void *) CompileMethod},
        {"isCompiled",            		      "(Ljava/lang/reflect/Member;)Z",                               (void *) IsCompiled},
        {"doRewriteHookCheck",                "(Ljava/lang/reflect/Member;)Z",                                (void *) DoRewriteHookCheck},
        {"isNativeMethod",                    "(Ljava/lang/reflect/Member;)Z",                                (void *) IsNativeMethod},
        {"isStaticMethod",                    "(Ljava/lang/reflect/Member;)Z",                                (void *) IsStaticMethod},
        {"setNativeMethod",                    "(Ljava/lang/reflect/Member;)V",                               (void *) SetNativeMethod},
        {"setDirectMethod",                    "(Ljava/lang/reflect/Member;)V",                               (void *) SetDirectMethod},
        {"checkJitState",                     "(Ljava/lang/reflect/Member;)I",                               (void *) CheckJitState},
        {"doFullRewriteHook",                 "(Ljava/lang/reflect/Member;Ljava/lang/reflect/Member;Ljava/lang/reflect/Member;Lcom/x930073498/oar/FastHookManager$HookRecord;Lcom/x930073498/oar/FastHookManager$HookRecord;Lcom/x930073498/oar/FastHookManager$HookRecord;)I",
                (void *) DoFullRewriteHook},
        {"doPartRewriteHook",                 "(Ljava/lang/reflect/Member;Ljava/lang/reflect/Member;Ljava/lang/reflect/Member;JJLcom/x930073498/oar/FastHookManager$HookRecord;)I",
                (void *) DoPartRewriteHook},
        {"doReplaceHook",                     "(Ljava/lang/reflect/Member;Ljava/lang/reflect/Member;Ljava/lang/reflect/Member;ZLcom/x930073498/oar/FastHookManager$HookRecord;)I",
                (void *) DoReplaceHook}
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;

    if ((*vm)->GetEnv(vm,(void**)&env,JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    jvm_ = vm;

    jclass class = (*env)->FindClass(env,kClassName);

    if(class == NULL)
    {
        LOGI( "%s: Can't find class %s", __FUNCTION__, kClassName );
        return -1;
    }

    if ((*env)->RegisterNatives(env, class, JniMethods,
                                sizeof(JniMethods) / sizeof(JniMethods[0])) != JNI_OK) {
        return -1;
    }

    jclass hook_record_class = (*env)->FindClass(env, kHookRecordClassName);
    kHookRecordClassInfo.jump_trampoline_ = (*env)->GetFieldID(env,hook_record_class, "mJumpTrampoline", "J");
    kHookRecordClassInfo.quick_hook_trampoline_ = (*env)->GetFieldID(env,hook_record_class, "mQuickHookTrampoline", "J");
    kHookRecordClassInfo.quick_original_trampoline_ = (*env)->GetFieldID(env,hook_record_class, "mQuickOriginalTrampoline", "J");
    kHookRecordClassInfo.quick_target_trampoline_ = (*env)->GetFieldID(env,hook_record_class, "mQuickTargetTrampoline", "J");
    kHookRecordClassInfo.hook_trampoline_ = (*env)->GetFieldID(env,hook_record_class, "mHookTrampoline", "J");
    kHookRecordClassInfo.target_trampoline_ = (*env)->GetFieldID(env,hook_record_class, "mTargetTrampoline", "J");

    return JNI_VERSION_1_6;
}

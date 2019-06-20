package com.x930073498.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.x930073498.adapter.BaseItemWrapper;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.boat.BoatManager;
import com.x930073498.boat.State;
import com.x930073498.boat.StateListener;
import com.x930073498.island.IslandManager;
import com.x930073498.island.permission.MultiplePermissionCallback;
import com.x930073498.island.permission.MultiplePermissionResult;
import com.x930073498.island.result.ActivityResultCallback;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private CommonAdapter adapter = new CommonAdapter();
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        BoatManager.registerStateChangeListener(this, new StateListener() {
            @Override
            public void onState(Activity activity, State state) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                //        Intent intent = new Intent();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                IslandManager.guess().request(intent).onResult(new ActivityResultCallback() {
                    @Override
                    public void call(int resultCode, Intent data) {
                        if (resultCode == RESULT_OK)
                            Log.e(TAG, "call: data=" + data.getData());
                        else if (resultCode == RESULT_CANCELED) {
                            Log.e(TAG, "isCanceled");
                        }
                    }
                });
                IslandManager.guess().request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
//                .forEach(new SinglePermissionCallback() {
//                    @Override
//                    public boolean intercept(SinglePermissionResult result) {
//                        Log.e(TAG, "name=" + result.getName() + "   isGranted=" + result.isGranted());
//                        return !result.isGranted();
//                    }
//                })
                        .forAll(new MultiplePermissionCallback() {
                            @Override
                            public void call(MultiplePermissionResult result) {
                                Log.e(TAG, "call: result="+result );
                            }
                        });
            }
        }).start();


        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.insert(BaseItemWrapper.wrap(new TestItem(), TestData.create()));
                adapter.notifyDataSetChanged();
                recycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.move(10, 7, 5);
                        adapter.notifyItemRangeChanged(7, 15);
                    }
                }, 1000);
            }
        }, 2000);
    }
}

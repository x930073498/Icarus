package com.x930073498.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.x930073498.adapter.BaseItemWrapper;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.boat.BoatManager;
import com.x930073498.island.ActivityResultCallback;
import com.x930073498.island.IslandLoader;
import com.x930073498.island.MultiplePermissionCallback;
import com.x930073498.island.MultiplePermissionResult;
import com.x930073498.island.SinglePermissionCallback;
import com.x930073498.island.SinglePermissionResult;

import java.util.logging.Logger;

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

        IslandLoader.guess().request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
//                .forAll(new MultiplePermissionCallback() {
//                    @Override
//                    public void call(MultiplePermissionResult multiplePermissionResult) {
//                        Log.e("TAG", "isGranted=" + multiplePermissionResult.isGranted());
//                    }
//                })
                .forEach(new SinglePermissionCallback() {
                    @Override
                    public boolean intercept(SinglePermissionResult result) {
                        Log.e(TAG, "name=" + result.getName() + "   isGranted=" + result.isGranted());
                        return !result.isGranted();
                    }
                })
        ;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        IslandLoader.guess().request(intent).onResult(new ActivityResultCallback() {
            @Override
            public void call(int resultCode, Intent data) {
                if (resultCode == RESULT_OK)
                    Log.e(TAG, "call: data=" + data.getData());
            }
        });
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

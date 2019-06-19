package com.x930073498.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private CommonAdapter adapter = new CommonAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        IslandLoader.guess().request(new Intent()).onResult(new ActivityResultCallback() {
            @Override
            public void call(int i, int i1, Intent intent) {

            }
        });
        IslandLoader.guess().request("").forAll(new MultiplePermissionCallback() {
            @Override
            public void call(MultiplePermissionResult multiplePermissionResult) {

            }
        });
        Log.e("tag", "state=" + BoatManager.getTopState());
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

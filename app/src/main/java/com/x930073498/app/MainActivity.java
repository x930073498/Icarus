package com.x930073498.app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.CommonAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private CommonAdapter adapter = new CommonAdapter();
    private String TAG = "MainActivity";
    private MainViewModel viewModel;


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("resume", "enter this line 3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        getData();
        register();
    }

    private void getData() {
        viewModel.getData();
    }

    private void register() {
        viewModel.result.observe(this, new Observer<List<BaseItem>>() {
            @Override
            public void onChanged(List<BaseItem> items) {
                adapter.replace(items);
                adapter.notifyDataSetChanged();
            }
        });
    }


}

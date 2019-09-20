package com.x930073498.app;

import android.os.Bundle;
import android.util.Log;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.CommonAdapter;
import com.x930073498.adapter.HolderFactory;
import com.x930073498.adapter.Source;
import com.x930073498.adapter.SourceItem;
import com.x930073498.adapter.TypeProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private String TAG = "MainActivity";
    private MainViewModel viewModel;
    private Source source = Source.create();


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
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        source.bind(recycler);
        source.insert();
        TestBaseItem item = new TestBaseItem();
        source
                .add(SourceItem.createDelegate(item,  item,"测试",
                        "测试1",
                        "测试2",
                        "测试3",
                        "测试4",
                        "测试5",
                        "测试6",
                        "测试7",
                        "测试8",
                        "测试9",
                        "测试10",
                        "测试11",
                        "测试12",
                        "测试13",
                        "测试14",
                        "测试15",
                        "测试16",
                        "测试17",
                        "测试18",
                        "测试19",
                        "测试20",
                        "测试21",
                        "测试22",
                        "测试23",
                        "测试24",
                        "测试25"
                ))
                .notifyDataSetChanged();
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
            }
        });
    }


}

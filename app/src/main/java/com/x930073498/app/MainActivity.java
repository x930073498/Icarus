package com.x930073498.app;

import android.util.Log;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.Bundle;
import com.x930073498.adapter.RecyclerCallback;
import com.x930073498.adapter.Source;
import com.x930073498.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private MainViewModel viewModel;

    AtomicInteger ai = new AtomicInteger();

    private String createData() {
        return "测试" + ai.incrementAndGet();
    }

    private List<String> createList() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            result.add(createData());
        }
        return result;
    }

    private Source source = Source.create().add(Bundle.create(new TestItem(), createList()))
            .add(Bundle.create(new TestBaseItem(), createList()));


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        long time = System.currentTimeMillis();
        source.bind(recycler);
        Log.e("tag", "time=" + (System.currentTimeMillis() - time));
        getData();
        register();
        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                source.moveSource("测试1","测试6").notifyItemRangeChanged(0, 10);
            }
        }, 3000);
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

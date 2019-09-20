package com.x930073498.app;

import com.x930073498.adapter.BaseItem;
import com.x930073498.adapter.Bundle;
import com.x930073498.adapter.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private String TAG = "MainActivity";
    private MainViewModel viewModel;

    AtomicInteger ai = new AtomicInteger();

    private String createData() {
        return "测试" + ai.incrementAndGet();
    }

    private List<String> createList() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
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
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        long time = System.currentTimeMillis();
        source.bind(recycler);
        source.notifyDataSetChanged();
//        Log.e("tag", "time=" + (System.currentTimeMillis() - time));
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

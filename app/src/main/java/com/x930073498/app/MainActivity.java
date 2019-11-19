package com.x930073498.app;

import android.util.Log;

import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelperEx;
import com.alibaba.android.vlayout.layout.ScrollFixLayoutHelper;
import com.x930073498.recycler.Bundle;
import com.x930073498.recycler.ItemLinker;
import com.x930073498.recycler.Source;
import com.x930073498.recycler.SourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;
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

    private List<String> createList(int size) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(createData());
        }
        return result;
    }

    private Source<LinearLayoutHelper> source1;


    private Source<ScrollFixLayoutHelper> source2;
    private Source<GridLayoutHelper> source3;

    private Source<OnePlusNLayoutHelperEx> source4;


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSource();
        setContentView(R.layout.activity_main);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        long time = System.currentTimeMillis();
        Log.e(TAG, "time=" + (System.currentTimeMillis() - time));
        SourceManager.create(this)
//                .addSource(
//                        source4,
//                        source3,
//                        source2,
//                        source1,
//                        null
//                )
                .addSource(
//                        null,
                        source1,
                        source2,
                        source3,
                        source4
                )
                .bind(recycler);
        getData();
        register();
//        recycler.postDelayed(() -> source1.clear().notifyDataSetChanged(), 3000);
    }

    ItemLinker<String> itemLinker = new TestItemLinker();

    private void initSource() {
        source1 = Source.create(new LinearLayoutHelper())
                .add(Bundle.create(itemLinker, createList(40)));
        source1.getLayoutHelper().setDividerHeight(10);
        source2 = Source.create(new ScrollFixLayoutHelper(0, 0)).add(Bundle.create(itemLinker, createList(1)));
        source2.getLayoutHelper().setShowType(ScrollFixLayoutHelper.SHOW_ON_LEAVE);
        source3 = Source.create(new GridLayoutHelper(5))
                .add(Bundle.create(itemLinker, createList(100)));
        source3.getLayoutHelper().setMargin(0, 10, 0, 10);
        source3.getLayoutHelper().setGap(5);
        source4 = Source.create(new OnePlusNLayoutHelperEx()).add(Bundle.create(new TestPlusItemLinker(), createList(6)));
        source4.getLayoutHelper().setMargin(10, 10, 10, 10);
        source4.getLayoutHelper().setPadding(10, 10, 10, 10);
//        onePlusNLayoutHelper.setHasFooter(true);
//        onePlusNLayoutHelper.setHasHeader(true);
        source4.getLayoutHelper().setAspectRatio(2.5f);
        source4.getLayoutHelper().setRowWeight(50);
    }

    private void getData() {
        viewModel.getData();
    }

    private void register() {
        viewModel.result.observe(this, items -> {
        });
    }


}

package com.x930073498.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ReportFragment;

import android.os.Bundle;
import android.util.Log;

import com.squareup.leakcanary.ActivityRefWatcher;
import com.squareup.leakcanary.LeakCanary;
import com.x930073498.thread.WindManager;
import com.x930073498.thread.core.IExecutorService;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";

    private IExecutorService service = WindManager.getBackgroundExecutorService(this);
    private IExecutorService mainService = WindManager.getMainExecutorService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        run();
    }


    private int index = 0;


    private void log() {
        Log.e(TAG, "" + (index++));
    }

    private void run() {
        service.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "" + (index++));
                try {
                synchronized (this){
                    wait(1000);
                }
                    service.execute(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        service.cancel();
//        mainService.cancel();
        WindManager.cancel(this);
    }
}

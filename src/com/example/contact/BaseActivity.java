package com.example.contact;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BaseActivity extends Activity {

    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(finshReceiver, filter);

    }

    BroadcastReceiver finshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            finish();
        }

    };

    protected void onDestroy() {
        this.unregisterReceiver(finshReceiver);
        super.onDestroy();
    }
}

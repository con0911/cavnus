package com.android.keepfocus.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.server.request.controllers.SchedulerRequestController;
import com.android.keepfocus.utils.MainUtils;

public class SettingsActivity extends Activity {

    private TextView mTextDesciption;
    private Switch mSwitch;
    private BroadcastReceiver getDatabaseReceiver;
    private IntentFilter intentFilter;
    private SchedulerRequestController schedulerRequestController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        schedulerRequestController = new SchedulerRequestController(this);
        mTextDesciption = (TextView) findViewById(R.id.text_description);
        mSwitch = (Switch) findViewById(R.id.switch_settings);
        if (MainUtils.memberItem.getIs_blocksettings() == 0) {
            mSwitch.setChecked(false);
        } else {
            mSwitch.setChecked(true);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    schedulerRequestController.testBlockSettingsRequest(MainUtils.memberItem);
                } else {
                    schedulerRequestController.testUnBlockSettingsRequest(MainUtils.memberItem);
                }
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.BLOCK_SETTINGS);
        intentFilter.addAction(MainUtils.UN_BLOCK_SETTINGS);
        getDatabaseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (MainUtils.BLOCK_SETTINGS.equals(action)) {
                    mSwitch.setChecked(true);
                } else if (MainUtils.UN_BLOCK_SETTINGS.equals(action)) {
                    mSwitch.setChecked(false);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getDatabaseReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getDatabaseReceiver);
    }
}

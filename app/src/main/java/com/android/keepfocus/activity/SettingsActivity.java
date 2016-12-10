package com.android.keepfocus.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.android.keepfocus.R;

public class SettingsActivity extends Activity {

    private TextView mTextDesciption;
    private Switch mSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mTextDesciption = (TextView) findViewById(R.id.text_description);
        mSwitch = (Switch) findViewById(R.id.switch_settings);
    }
}

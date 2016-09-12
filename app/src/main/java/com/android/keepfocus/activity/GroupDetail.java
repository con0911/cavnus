package com.android.keepfocus.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.keepfocus.R;

/**
 * Created by Sion on 9/12/2016.
 */
public class GroupDetail extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_management);
        setTitle("Group Detail");
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }
}

package com.android.keepfocus.activity;

import android.app.Activity;
import android.os.Bundle;

import com.android.keepfocus.R;

/**
 * Created by Sion on 9/6/2016.
 */
public class ShowQRCodeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_member_1);
        setTitle(R.string.add_member);
    }

}

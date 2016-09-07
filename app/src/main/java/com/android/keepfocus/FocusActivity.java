package com.android.keepfocus;

import android.app.Activity;
import android.os.Bundle;

import com.android.keepfocus.data.MainDatabaseHelper;

public class FocusActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        //This comment create by thong.nv, try make zero to hero =))
        //2
        MainDatabaseHelper db = new MainDatabaseHelper(this);
    }
}

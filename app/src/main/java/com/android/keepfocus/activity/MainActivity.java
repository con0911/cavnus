package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.keepfocus.R;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private Button activity1;
    private Button activity2;
    private Button activity3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);


        //show class
        activity1 = (Button) findViewById(R.id.activity1);
        activity2 = (Button) findViewById(R.id.activity2);
        activity3 = (Button) findViewById(R.id.activity3);
        activity1.setOnClickListener(this);
        activity2.setOnClickListener(this);
        activity3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity1:
                Intent intent1 = new Intent(getApplicationContext(), ShowQRCodeScreen.class);
                startActivity(intent1);
                break;

            case R.id.activity2:
                Intent intent2 = new Intent(getApplicationContext(), JoinGroupActivity.class);
                startActivity(intent2);
                break;

            case R.id.activity3:
                Intent intent3 = new Intent(getApplicationContext(), GroupManagermentActivity.class);
                startActivity(intent3);
                break;

            default: break;
        }
    }
}

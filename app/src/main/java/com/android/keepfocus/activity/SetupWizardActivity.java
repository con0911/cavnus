package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.keepfocus.R;

public class SetupWizardActivity extends Activity {
    private Button btnNext;
    private SharedPreferences agreeTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (agreeTerm.getBoolean("terms_and_conditions", false)) {
            setContentView(R.layout.terms_and_conditions);
        }else {
            setContentView(R.layout.set_up_mode);
        }

        btnNext = (Button)findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = agreeTerm.edit();
                editor.putBoolean("terms_and_conditions", true);
            }
        });

    }
}

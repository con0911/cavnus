package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.keepfocus.R;
import com.android.keepfocus.utils.MainUtils;

public class SetupWizardActivity extends Activity {
    private Button btnNext;
    private SharedPreferences agreeTerm;
    private CheckBox mCheckboxTerm;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        agreeTerm = PreferenceManager.getDefaultSharedPreferences(this);
        if (agreeTerm.getBoolean(MainUtils.TERMS_AND_CONDITIONS, false)) {
            setContentView(R.layout.terms_and_conditions);
            btnNext = (Button)findViewById(R.id.btn_next);
            mCheckboxTerm = (CheckBox) findViewById(R.id.checkbox_agree);
            if (!mCheckboxTerm.isChecked()){
                btnNext.setCursorVisible(false);
            }
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = agreeTerm.edit();
                    if (mCheckboxTerm.isChecked()) {
                        editor.putBoolean(MainUtils.TERMS_AND_CONDITIONS, true);
                    }
                }
            });
        }else {
            setContentView(R.layout.set_up_mode);
            btnSkip = (Button) findViewById(R.id.btn_skip);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent logIn = new Intent(SetupWizardActivity.this, LoginActivity.class);
                    startActivity(logIn);
                }
            });
        }



    }
}

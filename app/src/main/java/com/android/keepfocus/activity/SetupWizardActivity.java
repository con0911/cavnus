package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.keepfocus.R;
import com.android.keepfocus.utils.MainUtils;

public class SetupWizardActivity extends Activity {
    private Button btnNext;
    private SharedPreferences agreePref;
    private CheckBox mCheckboxTerm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        agreePref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checkAgree = agreePref.getBoolean(MainUtils.TERMS_AND_CONDITIONS, false);
        if (!checkAgree) {
            setContentView(R.layout.terms_and_conditions);
            btnNext = (Button) findViewById(R.id.btn_next);
            mCheckboxTerm = (CheckBox) findViewById(R.id.checkbox_agree);
            mCheckboxTerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked){
                        mCheckboxTerm.setChecked(true);
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences.Editor editor = agreePref.edit();
                                editor.putBoolean(MainUtils.TERMS_AND_CONDITIONS, true);
                                editor.commit();
                                setContentView(R.layout.set_up_mode);
                            }
                        });
                    }else {
                        mCheckboxTerm.setChecked(false);
                        SharedPreferences.Editor editor = agreePref.edit();
                        editor.putBoolean(MainUtils.TERMS_AND_CONDITIONS, false);
                        editor.commit();
                    }
                }
            });

        }else {
            setContentView(R.layout.set_up_mode);
            Button btnSkip = (Button) findViewById(R.id.btn_skip);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent login = new Intent(SetupWizardActivity.this, LoginActivity.class);
                    startActivity(login);
                }
            });
        }
    }
}

package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.android.keepfocus.R;
import com.android.keepfocus.utils.MainUtils;

public class SetupWizardActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SetupWizardActivity";
    private Button btnNext;
    private SharedPreferences agreePref;
    private static SharedPreferences modeDevice;
    private CheckBox mCheckboxTerm;
    private Button btnSkip;
    private Button btnJoinGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        super.onCreate(savedInstanceState);
        // set mode device is default
        modeDevice = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = modeDevice.edit();
        editor.putInt(MainUtils.MODE_DEVICE, MainUtils.MODE_DEFAULT);
        editor.commit();

        agreePref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checkAgree = agreePref.getBoolean(MainUtils.TERMS_AND_CONDITIONS, false);
        if (!checkAgree) {
            setContentView(R.layout.terms_and_conditions);
            setTitle("Cavnus");

            btnNext = (Button) findViewById(R.id.btn_next);
            btnNext.setTextColor(Color.parseColor("#808080"));
            mCheckboxTerm = (CheckBox) findViewById(R.id.checkbox_agree);
            mCheckboxTerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked){
                        mCheckboxTerm.setChecked(true);
                        btnNext.setTextColor(Color.parseColor("#499ebd"));
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences.Editor editor = agreePref.edit();
                                editor.putBoolean(MainUtils.TERMS_AND_CONDITIONS, true);
                                editor.commit();
                                setContentView(R.layout.set_up_mode);
                                setTitle("Setup mode");
                                btnSkip = (Button) findViewById(R.id.btn_skip);
                                btnJoinGroup = (Button) findViewById(R.id.btn_join_group);
                                //btnSkip.setOnClickListener(this);
                                //btnJoinGroup.setOnClickListener(this);
                                btnSkip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent login = new Intent(SetupWizardActivity.this, LoginActivity.class);
                                        startActivity(login);
                                    }
                                });
                                Button btnJoin = (Button) findViewById(R.id.btn_join_group);
                                btnJoin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent join = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                                        startActivity(join);
                                    }
                                });
                            }
                        });
                    }else {
                        mCheckboxTerm.setChecked(false);
                        btnNext.setTextColor(Color.parseColor("#808080"));
                        btnNext.setClickable(false);
                        SharedPreferences.Editor editor = agreePref.edit();
                        editor.putBoolean(MainUtils.TERMS_AND_CONDITIONS, false);
                        editor.commit();
                    }
                }
            });

        }else {
            setContentView(R.layout.set_up_mode);
            setTitle("Setup mode");
            btnSkip = (Button) findViewById(R.id.btn_skip);
            btnJoinGroup = (Button) findViewById(R.id.btn_join_group);
            //btnSkip.setOnClickListener(this);
            //btnJoinGroup.setOnClickListener(this);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent login = new Intent(SetupWizardActivity.this, LoginActivity.class);
                    startActivity(login);
                }
            });
            Button btnJoin = (Button) findViewById(R.id.btn_join_group);
            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent join = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                    startActivity(join);
                }
            });
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_skip :
                Intent login = new Intent(SetupWizardActivity.this, LoginActivity.class);
                startActivity(login);
            case R.id.btn_join_group :
                Intent joinGroup = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                startActivity(joinGroup);
        }
    }

    public static void setModeDevice(int mode, Context context){
        modeDevice = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = modeDevice.edit();
        editor.putInt(MainUtils.MODE_DEVICE, mode);
        editor.commit();
    }

    public static int getModeDevice(Context context){
        modeDevice = PreferenceManager.getDefaultSharedPreferences(context);
        return modeDevice.getInt(MainUtils.MODE_DEVICE, 0);
    }

}

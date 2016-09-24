package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.gcm.GcmIntentService;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.request.controllers.DeviceRequestController;
import com.android.keepfocus.server.request.controllers.LoginRequestController;
import com.android.keepfocus.server.request.model.DeviceRequest;
import com.android.keepfocus.server.request.model.LoginRequest;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.google.gson.Gson;

public class SetupWizardActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SetupWizardActivity";
    private Button btnNext;
    private SharedPreferences agreePref;
    private static SharedPreferences modeDevice;
    private CheckBox mCheckboxTerm;
    private Button btnSkip;
    private Button btnJoinGroup;

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    private boolean checkAgree;

    private Context mContext;
    private GcmIntentService gcmIntentService;
    private DeviceRequestController mDeviceRequestController;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        super.onCreate(savedInstanceState);
        // set mode device is default
        mContext = this;
        modeDevice = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = modeDevice.edit();
        editor.putInt(MainUtils.MODE_DEVICE, MainUtils.MODE_DEFAULT);
        editor.commit();

        agreePref = PreferenceManager.getDefaultSharedPreferences(this);
        checkAgree = agreePref.getBoolean(MainUtils.TERMS_AND_CONDITIONS, false);
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
                                mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                                            token = intent.getStringExtra("token");
                                        }
                                    }
                                };
                                Intent intent = new Intent(SetupWizardActivity.this, GcmIntentService.class);
                                intent.putExtra("key", "register");
                                startService(intent);
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
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                        token = intent.getStringExtra("token");
                    }
                }
            };
            Intent intent = new Intent(this, GcmIntentService.class);
            intent.putExtra("key", "register");
            startService(intent);
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
    protected void onResume() {
        super.onResume();
        if (checkAgree){
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(REGISTRATION_COMPLETE));
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(PUSH_NOTIFICATION));
             sendJsonDeviceRequest();
        }

    }

    public void sendJsonDeviceRequest(){

        String registrationId = token;
        String deviceCode = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Device device = new Device(deviceCode, "", "", registrationId, "");
        DeviceRequest loginRequest = new DeviceRequest(Constants.ActionTypeCreate,device);
        Gson gson = new Gson();
        String deviceJsonObject = gson.toJson(loginRequest);
        Log.e("Device", "URL : "+ "http://104.156.224.47/api/device?pRequest=" + deviceJsonObject);
        mDeviceRequestController =  new DeviceRequestController(mContext);
        boolean isSuccess = mDeviceRequestController.checkDeviceRequest(deviceJsonObject);
        //Log.e("Login", "isSuccess : " + isSuccess);
        if (isSuccess){
            Toast.makeText(SetupWizardActivity.this, "Send request device success", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(SetupWizardActivity.this, "Send request device false", Toast.LENGTH_SHORT).show();
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

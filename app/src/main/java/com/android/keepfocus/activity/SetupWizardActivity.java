package com.android.keepfocus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.gcm.GcmIntentService;
import com.android.keepfocus.server.request.controllers.DeviceRequestController;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;

public class SetupWizardActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SetupWizardActivity";
    private Button btnNext;
    private SharedPreferences agreePref;
    private static SharedPreferences modeDevice, typeJoin, nameDevice;
    private CheckBox mCheckboxTerm;
    private Button btnParent, btnChild, btnAddParent;

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private final String TERMS_URL = "http://mycavnus.com/terms-and-conditions/";
    private TextView mTerms;

    private Context mContext;
    private GcmIntentService gcmIntentService;
    private DeviceRequestController mDeviceRequestController;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String token = "";
    private ImageView imgCavnus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        super.onCreate(savedInstanceState);
        // set mode device is default
        mContext = this;
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        modeDevice = PreferenceManager.getDefaultSharedPreferences(this);
        if (getModeDevice(mContext) == Constants.Admin /*|| getModeDevice(mContext) == MainUtils.MODE_ADDITION_PARENT*/) {
            Intent groupManagement = new Intent(this, FamilyManagerment.class);
            startActivity(groupManagement);
            //setContentView(R.layout.activity_group_management);
            return;
        }
        if (getTypeJoin(mContext) == Constants.JoinSuccess){
            if (getModeDevice(mContext) == Constants.Manager){
                Intent familyManagement = new Intent(this, FamilyManagerment.class);
                startActivity(familyManagement);
                return;
            }else if (getModeDevice(mContext) == Constants.Children){
                Intent childSchedule = new Intent(this, ChildSchedulerActivity.class);
                startActivity(childSchedule);
                return;
            }
        }
        //SharedPreferences.Editor editor = modeDevice.edit();
        //editor.putInt(MainUtils.MODE_DEVICE, MainUtils.MODE_DEFAULT);
        //editor.commit();
        setContentView(R.layout.set_up_mode);
        setTitle("Cavnus");
        btnParent = (Button) findViewById(R.id.btn_parent);
        btnAddParent = (Button) findViewById(R.id.btn_additional_parent);
        btnChild = (Button) findViewById(R.id.btn_child);
        mTerms = (TextView) findViewById(R.id.terms_link);
        imgCavnus = (ImageView) findViewById(R.id.img_cavnus_setup_mode);

        int display_mode = getResources().getConfiguration().orientation;

        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            imgCavnus.setVisibility(View.VISIBLE);
        } else {
            imgCavnus.setVisibility(View.GONE);
        }
        //mTerms.setTextColor(Color.BLUE);
        btnParent.setOnClickListener(this);
        //btnAddParent.setOnClickListener(this);
        //btnChild.setOnClickListener(this);
        mTerms.setOnClickListener(this);



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
        //startService(intent);

        btnChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinGroupChild = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                setModeDevice(Constants.Children, mContext);
                startActivity(joinGroupChild);
            }
        });

        btnAddParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinGroupAddParent = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                setModeDevice(Constants.Manager, mContext);
                startActivity(joinGroupAddParent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        sendJsonDeviceRequest();

        if (getModeDevice(mContext) == Constants.Admin /*|| getModeDevice(mContext) == MainUtils.MODE_ADDITION_PARENT*/) {
            finish();
        }
    }

    public void sendJsonDeviceRequest() {

        String registrationId = token;
        String deviceCode = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        /*Device device = new Device(deviceCode, "", "", registrationId, "");
        DeviceRequest loginRequest = new DeviceRequest(Constants.ActionTypeCreate, device);
        Gson gson = new Gson();
        String deviceJsonObject = gson.toJson(loginRequest);
        Log.e("Device", "URL : " + "http://104.156.224.47/api/device?pRequest=" + deviceJsonObject);
        mDeviceRequestController = new DeviceRequestController(mContext);
        boolean isSuccess = mDeviceRequestController.checkDeviceRequest(deviceJsonObject);
        //Log.e("Login", "isSuccess : " + isSuccess);
        if (isSuccess) {
            Toast.makeText(SetupWizardActivity.this, "Send request device success", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(SetupWizardActivity.this, "Send request device false", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_parent:
                //setModeDevice(MainUtils.MODE_PARENT, mContext);
                Intent login = new Intent(SetupWizardActivity.this, LoginActivity.class);
                startActivity(login);
                break;

            case R.id.btn_additional_parent:
                Intent joinGroupAddParent = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                startActivity(joinGroupAddParent);
                break;
            case R.id.btn_child:
                Intent joinGroupChild = new Intent(SetupWizardActivity.this, JoinGroupActivity.class);
                startActivity(joinGroupChild);
                break;
            case R.id.terms_link:
                //mTerms.setTextColor(Color.parseColor("#660066"));
                Uri uriTerms = Uri.parse(TERMS_URL);
                Intent terms = new Intent(Intent.ACTION_VIEW, uriTerms);
                startActivity(terms);
                break;
            default:
                break;
        }
    }

    public static void setModeDevice(int mode, Context context) {
        modeDevice = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = modeDevice.edit();
        editor.putInt(MainUtils.MODE_DEVICE, mode);
        editor.commit();
    }

    public static void setTypeJoin(int type, Context context){
        typeJoin = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = modeDevice.edit();
        editor.putInt(MainUtils.TYPE_JOIN, type);
        editor.commit();
    }

    public static void setNameDevice(String name, Context context){
        nameDevice = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = nameDevice.edit();
        editor.putString(MainUtils.NAME_DEVICE, name);
        editor.commit();
    }

    public static int getModeDevice(Context context) {
        modeDevice = PreferenceManager.getDefaultSharedPreferences(context);
        return modeDevice.getInt(MainUtils.MODE_DEVICE, 0);
    }

    public static int getTypeJoin(Context context){
        typeJoin = PreferenceManager.getDefaultSharedPreferences(context);
        return typeJoin.getInt(MainUtils.TYPE_JOIN, 0);
    }

    public static String getNameDevice(Context context){
        nameDevice = PreferenceManager.getDefaultSharedPreferences(context);
        return nameDevice.getString(MainUtils.NAME_DEVICE, "");
    }


}

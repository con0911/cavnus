package com.android.keepfocus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
    private static SharedPreferences modeDevice, typeJoin, typeRestore, nameDevice;
    private CheckBox mCheckboxTerm;
    private Button btnParent, btnChild, btnAddParent;

    private final String TERMS_URL = "http://setlimitz.com/terms-and-conditions/";
    private TextView mTerms;

    private Context mContext;
    private String token = "";
    private ImageView imgCavnus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        super.onCreate(savedInstanceState);
        mContext = this;
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        modeDevice = PreferenceManager.getDefaultSharedPreferences(this);
        if (getModeDevice(mContext) == Constants.Admin /*|| getModeDevice(mContext) == MainUtils.MODE_ADDITION_PARENT*/) {
            Intent groupManagement = new Intent(this, FamilyManagerment.class);
            startActivity(groupManagement);
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
        setContentView(R.layout.set_up_mode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        btnParent.setOnClickListener(this);
        mTerms.setOnClickListener(this);

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
        if (getModeDevice(mContext) == Constants.Admin /*|| getModeDevice(mContext) == MainUtils.MODE_ADDITION_PARENT*/) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
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
        SharedPreferences.Editor editor = typeJoin.edit();
        editor.putInt(MainUtils.TYPE_JOIN, type);
        editor.commit();
    }

    public static void setTypeRestoreFamily(int type, Context context){
        typeRestore = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = typeRestore.edit();
        editor.putInt(MainUtils.TYPE_LOGIN, type);
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

    public static int getTypeRestoreFamily(Context context){
        typeRestore = PreferenceManager.getDefaultSharedPreferences(context);
        return typeRestore.getInt(MainUtils.TYPE_LOGIN, 0);
    }

    public static String getNameDevice(Context context){
        nameDevice = PreferenceManager.getDefaultSharedPreferences(context);
        return nameDevice.getString(MainUtils.NAME_DEVICE, "");
    }


}

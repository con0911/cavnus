package com.android.keepfocus.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.gcm.GcmIntentService;
import com.android.keepfocus.receive.DevicePolicyReceiver;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.GroupUser;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.server.request.model.JoinGroupRequest;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class JoinGroupActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "JoinGroupActivity";
    private Button btnImageDone;
    private String[] listMemberType;
    private Spinner spinnerType;
    private static final int REQUEST_ADMIN_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    private final int SHOW_DIALOG_REQUEST_ADMIN_PERMISSION = 1;
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private boolean isTurnOnAdmin = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String token = "";
    private SharedPreferences joinPref;
    private static SharedPreferences childServerId;
    private EditText joinFamilyIDText, nameDevice;
    private Spinner mActiveCode;
    private static boolean checkValidID, checkValidActiveCode, checkValidName;
    private LinearLayout layoutChooseMode;
    private LinearLayout layoutChooseLicense;
    private RadioButton mRBtnManage, mRBtnChild;
    private RadioButton mRBtnUse, mRBtnUnUse;
    public String deviceCode;
    private GroupRequestController groupRequestController;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    public static String MANAGER = "manager";
    public static String CHILDREN = "child";
    private int typeJoin = 0;
    public static boolean isJoinSuccess;
    private ChildKeepFocusItem childKeepFocusItem;

    private Switch launchSwitch, notiSwitch;
    private boolean isTurnUsageAccess = false;
    private boolean isTurnNotificationAccess = false;
    private final int SHOW_DIALOG_USAGE_ACCESS_ID = 1;
    private final int SHOW_DIALOG_NOTIFICATION_ACCESS_ID = 2;
    private static String mPopupContentMgs = "";
    private AlertDialog mEnableNotiDialog, mEnableDataAccessDialog;

    public static Bundle bundle;
    private CountDownTimer mCDT = null;

    private ArrayList<String> listLicenses;
    private ArrayAdapter licenseAdapter;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_member_qr_code);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DevicePolicyReceiver.class);
        mContext = this;


        joinFamilyIDText = (EditText) findViewById(R.id.familyId);
        mActiveCode = (Spinner) findViewById(R.id.activeCode);
        nameDevice = (EditText) findViewById(R.id.deviceName);
        btnImageDone = (Button) findViewById(R.id.doneImageBtn);
        layoutChooseMode = (LinearLayout) findViewById(R.id.layout_choose_mode);
        layoutChooseLicense = (LinearLayout) findViewById(R.id.layout_choose_license);
        btnImageDone.setClickable(false);
        btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
        btnImageDone.setTextColor(Color.parseColor("#808080"));
        //layoutChooseMode.setAlpha((float) 0.9);
        layoutChooseMode.setClickable(false);
        layoutChooseMode.setEnabled(false);
        mRBtnManage = (RadioButton) findViewById(R.id.rbtn_manage);
        mRBtnChild = (RadioButton) findViewById(R.id.rbtn_child);
        //Add code for license
        mRBtnUse = (RadioButton) findViewById(R.id.license_used);
        mRBtnUnUse = (RadioButton) findViewById(R.id.license_not_used);
        mRBtnUse.setChecked(false);
        mRBtnUnUse.setChecked(false);
        mRBtnUse.setOnCheckedChangeListener(this);
        mRBtnUnUse.setOnCheckedChangeListener(this);

        createTestSpinner();

        mActiveCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



/*        if (SetupWizardActivity.getModeDevice(getApplicationContext()) == MainUtils.MODE_ADDITION_PARENT){
            Intent familyManagement = new Intent(JoinGroupActivity.this, FamilyManagerment.class);
            startActivity(familyManagement);
            return;
        }
        if (SetupWizardActivity.getModeDevice(getApplicationContext()) == MainUtils.MODE_CHILD ){
            Intent childSchedule = new Intent(JoinGroupActivity.this, ChildSchedulerActivity.class);
            startActivity(childSchedule);
            return;
        }*/

        if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
            mActiveCode.setVisibility(View.GONE);
            mRBtnManage.setChecked(true);
            mRBtnChild.setChecked(false);
            layoutChooseLicense.setVisibility(View.GONE);
        } else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children) {
            mRBtnManage.setChecked(false);
            mRBtnChild.setChecked(true);
        }
        mRBtnManage.setClickable(false);
        mRBtnManage.setEnabled(false);
        mRBtnChild.setClickable(false);
        mRBtnChild.setEnabled(false);
/*        mRBtnChild.setChecked(true);
        //SetupWizardActivity.setModeDevice(MainUtils.MODE_MEMBER, getApplicationContext());
        mRBtnManage.setChecked(false);
        mRBtnManage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    typeJoin = 1;
                    mRBtnChild.setChecked(false);
                    //SetupWizardActivity.setModeDevice(MainUtils.MODE_ADMIN, getApplicationContext());
                }
            }
        });
        mRBtnChild.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    typeJoin = 0;
                    mRBtnManage.setChecked(false);
                    //SetupWizardActivity.setModeDevice(MainUtils.MODE_ADMIN, getApplicationContext());
                }
            }
        });*/
        joinFamilyIDText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    //checkValidID = true;
                    if(SetupWizardActivity.getModeDevice(mContext) == Constants.Children) {
                        if (!nameDevice.getText().toString().isEmpty() && mActiveCode.getSelectedItem()!=null) {
                            btnImageDone.setClickable(true);
                            btnImageDone.setBackgroundColor(Color.parseColor("#3B5998"));
                            btnImageDone.setTextColor(Color.parseColor("#fafafa"));
                        } else {
                            btnImageDone.setClickable(false);
                            btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"/*String.valueOf(getResources().getColor(R.color.gray_press))*/));
                            btnImageDone.setTextColor(Color.parseColor("#808080"/*String.valueOf(getResources().getColor(R.color.grey))*/));
                        }
                    }else if(SetupWizardActivity.getModeDevice(mContext) == Constants.Manager){
                        if (!nameDevice.getText().toString().isEmpty()) {
                            btnImageDone.setClickable(true);
                            btnImageDone.setBackgroundColor(Color.parseColor("#3B5998"));
                            btnImageDone.setTextColor(Color.parseColor("#fafafa"));
                        } else {
                            btnImageDone.setClickable(false);
                            btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"/*String.valueOf(getResources().getColor(R.color.gray_press))*/));
                            btnImageDone.setTextColor(Color.parseColor("#808080"/*String.valueOf(getResources().getColor(R.color.grey))*/));
                        }
                    }
                }else{
                    btnImageDone.setClickable(false);
                    btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    btnImageDone.setTextColor(Color.parseColor("#808080"));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nameDevice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    //checkValidName = true;
                    if(SetupWizardActivity.getModeDevice(mContext) == Constants.Children) {
                        if (!joinFamilyIDText.getText().toString().isEmpty() && mActiveCode.getSelectedItem()!=null) {
                            btnImageDone.setClickable(true);
                            btnImageDone.setBackgroundColor(Color.parseColor("#3B5998"));
                            btnImageDone.setTextColor(Color.parseColor("#fafafa"));
                        } else {
                            btnImageDone.setClickable(false);
                            btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"/*String.valueOf(getResources().getColor(R.color.gray_press))*/));
                            btnImageDone.setTextColor(Color.parseColor("#808080"/*String.valueOf(getResources().getColor(R.color.grey))*/));
                        }
                    }else if (SetupWizardActivity.getModeDevice(mContext) == Constants.Manager){
                        if (!joinFamilyIDText.getText().toString().isEmpty()) {
                            btnImageDone.setClickable(true);
                            btnImageDone.setBackgroundColor(Color.parseColor("#3B5998"));
                            btnImageDone.setTextColor(Color.parseColor("#fafafa"));
                        } else {
                            btnImageDone.setClickable(false);
                            btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"/*String.valueOf(getResources().getColor(R.color.gray_press))*/));
                            btnImageDone.setTextColor(Color.parseColor("#808080"/*String.valueOf(getResources().getColor(R.color.grey))*/));
                        }
                    }
                }else {
                    btnImageDone.setClickable(false);
                    btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    btnImageDone.setTextColor(Color.parseColor("#808080"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
/*        mActiveCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    //checkValidActiveCode = true;
                    if (!nameDevice.getText().toString().isEmpty() && !joinFamilyIDText.getText().toString().isEmpty()){
                        btnImageDone.setClickable(true);
                        btnImageDone.setBackgroundColor(Color.parseColor("#3B5998"));
                        btnImageDone.setTextColor(Color.parseColor("#fafafa"));
                    }else {
                        btnImageDone.setClickable(false);
                        btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
                        btnImageDone.setTextColor(Color.parseColor("#808080"));
                    }
                }else{
                    btnImageDone.setClickable(false);
                    btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    btnImageDone.setTextColor(Color.parseColor("#808080"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        joinPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    token = intent.getStringExtra("token");
                    Log.d(TAG, "REGISTATION_ID = " + token);
                    SharedPreferences.Editor editor = joinPref.edit();
                    editor.putString(MainUtils.REGISTATION_ID, token);
                    editor.commit();
                }
            }
        };
        Intent intent = new Intent(mContext, GcmIntentService.class);//send intent to get token
        intent.putExtra("key", "register");
        startService(intent);
        groupRequestController = new GroupRequestController(mContext);
        mDataHelper = new MainDatabaseHelper(mContext);
        deviceCode = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
/*        if (SetupWizardActivity.getModeDevice(mContext) == Constants.Children){
            if (joinFamilyIDText.getText().toString().isEmpty() || nameDevice.getText().toString().isEmpty()
                    || mActiveCode.getText().toString().isEmpty()){
                btnImageDone.setClickable(false);
                btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
                btnImageDone.setTextColor(Color.parseColor("#808080"));
            }
        }
        if (SetupWizardActivity.getModeDevice(mContext) == Constants.Manager){
            if (joinFamilyIDText.getText().toString().isEmpty() || nameDevice.getText().toString().isEmpty()){
                btnImageDone.setClickable(false);
                btnImageDone.setBackgroundColor(Color.parseColor("#E0E0E0"));
                btnImageDone.setTextColor(Color.parseColor("#808080"));
            }
        }*/
        btnImageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createRequestDialog();
                String registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
                if (registationId.equals("")) {
                    Intent intent = new Intent(mContext, GcmIntentService.class);//send intent to get token
                    intent.putExtra("key", "register");
                    startService(intent);
                    Toast.makeText(JoinGroupActivity.this, "Please check the internet connection!", Toast.LENGTH_LONG).show();
                } else if (SetupWizardActivity.getModeDevice(mContext) == Constants.Children
                        && !(joinFamilyIDText.getText().toString().isEmpty() || nameDevice.getText().toString().isEmpty()
                            || mActiveCode.getSelectedItem().toString().equals(" "))) {
                    if (isNameInValid(joinFamilyIDText.getText().toString()) || isNameInValid(nameDevice.getText().toString())
                            || isNameInValid(mActiveCode.getSelectedItem().toString())){
                        final Toast errorName = Toast.makeText(mContext, "The name, FamilyID or Activation code cannot contain space", Toast.LENGTH_SHORT);
                        errorName.show();
                        MainUtils.extendDisplayTimeOfToast(errorName);
                    }else {
                        typeJoin = 0;
                        JoinGroupAsynTask joinAsyn = new JoinGroupAsynTask();
                        joinAsyn.execute();
                    }
                } else if (SetupWizardActivity.getModeDevice(mContext) == Constants.Manager
                        && !(joinFamilyIDText.getText().toString().isEmpty() || nameDevice.getText().toString().isEmpty())) {
                    if (isNameInValid(joinFamilyIDText.getText().toString()) || isNameInValid(nameDevice.getText().toString())){
                        final Toast errorInput = Toast.makeText(mContext, "The name or FamilyIDcannot contain space", Toast.LENGTH_SHORT);
                        errorInput.show();
                        MainUtils.extendDisplayTimeOfToast(errorInput);
                    }
                    typeJoin = 1;
                    JoinGroupAsynTask joinAsyn = new JoinGroupAsynTask();
                    joinAsyn.execute();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        try {

            if (SetupWizardActivity.getModeDevice(getApplicationContext()) == MainUtils.MODE_CHILD) {//if children -> request admin permission
                if (!isTurnOnAdmin) {//if not enable yet
                    if (!mDPM.isAdminActive(mAdminName)) {
                        showAdminDialog(SHOW_DIALOG_REQUEST_ADMIN_PERMISSION);
                    } else {
                        //mDPM.lockNow();
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (!isTurnUsageAccess && !isOnUsageAccess()) {
                        showDialogGotoAccess(SHOW_DIALOG_USAGE_ACCESS_ID);
                    }
                    if (!isTurnNotificationAccess && !isOnNotificationAccessPermission()) {
                        showDialogGotoAccess(SHOW_DIALOG_NOTIFICATION_ACCESS_ID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isTurnOnAdmin = true;
        isTurnNotificationAccess = true;
        isTurnUsageAccess = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private boolean isNameInValid(String name){
        return name.contains(" ");
    }

    private void showAdminDialog(int dialogId) {
        View view = getLayoutInflater().inflate(R.layout.request_admin_popup, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.request_text);
        switch (dialogId) {
            case SHOW_DIALOG_REQUEST_ADMIN_PERMISSION:
                txtMsg.setText(R.string.request_admin_text);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle(getString(R.string.title_request_admin))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isTurnOnAdmin = true;
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to enable admin permission.");
                                startActivityForResult(intent, REQUEST_ADMIN_CODE);

                            }
                        })
                        /*.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        })*/.create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }

    private void showReplaceDeviceDialog() {
        View view = getLayoutInflater().inflate(R.layout.replace_child_device_popup, null);
        TextView txtMsg = (TextView) view.findViewById(R.id.replace_text);
        txtMsg.setText(R.string.replace_device_text);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(getString(R.string.title_replace_device))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReplaceDeviceAsynTask replaceAsyn = new ReplaceDeviceAsynTask();
                        replaceAsyn.execute();
                    }
                })
                .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }).create();
        alertDialog.show();


    }

    private void turnOnUsageAccess() {
        Intent usageAccessIntent = new Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(usageAccessIntent);
    }

    private void showDialogGotoAccess(int dialogId) {
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup,
                null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        switch (dialogId) {
            case SHOW_DIALOG_USAGE_ACCESS_ID:
                mTextMsg.setText(R.string.popup_usage_access);
                mEnableDataAccessDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle(getString(R.string.title_usage_access))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        isTurnUsageAccess = true;
                                        turnOnUsageAccess();
                                    }
                                })
                        /*.setNegativeButton(getString(R.string.cancel_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dialog.cancel();
                                    }
                                })*/.create();
                mEnableDataAccessDialog.show();
                break;
            case SHOW_DIALOG_NOTIFICATION_ACCESS_ID:
                mTextMsg.setText(mPopupContentMgs);
                mEnableNotiDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle(getString(R.string.title_notification_access))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        isTurnNotificationAccess = true;
                                        turnOnNotificationAccess();
                                    }
                                })
                        /*.setNegativeButton(getString(R.string.cancel_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dialog.cancel();
                                    }
                                })*/.create();
                mEnableNotiDialog.show();
                break;
            default:
                break;
        }

    }

    //Code part handle notifications access permission
    private boolean isOnNotificationAccessPermission() {
        boolean isEnable = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mPopupContentMgs = (String) getString(R.string.popup_notification_access);
            ContentResolver contentResolver = mContext.getContentResolver();
            String enableNotificationListener = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
            String packageName = mContext.getPackageName();
            if (enableNotificationListener == null || !enableNotificationListener.contains(packageName)) {
                Log.d(TAG, "The Notification Permission not enable");
                isEnable = false;
            } else {
                Log.d(TAG, "The Notification Permission enable");
                isEnable = true;
            }
        } else {
            mPopupContentMgs = (String) getString(R.string.popup_notification_access_not_support);
            isEnable = false;
        }

        return isEnable;
    }

    @SuppressLint("NewApi")
    private boolean isOnUsageAccess() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long toTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long fromTime = calendar.getTimeInMillis();
        final List<UsageStats> queryUsageStats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_YEARLY, fromTime, toTime);
        boolean granted = queryUsageStats != null
                && queryUsageStats != Collections.EMPTY_LIST;
        return granted;
    }

    private void turnOnNotificationAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Intent intent = new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {
            mEnableNotiDialog.dismiss();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADMIN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Administration is enabled");
            } else {

            }
        }
    }

    private String checkType() {
        if (typeJoin == 0) return CHILDREN;
        else return MANAGER;
    }

    public String joinGroup() {
        String registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
        //Header headerItem = new Header("testlogin2@gmail.com",deviceCode,registationId,"testpass");
        Group groupItem = new Group("", joinFamilyIDText.getText().toString());
        Device deviceItem = new Device(0, nameDevice.getText().toString(), "ss", "android", registationId, "", checkType());
        GroupUser groupUser = new GroupUser(0, 0, 0, mActiveCode.getSelectedItem().toString());
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest(3, groupItem, deviceItem, groupUser);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(joinGroupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        SharedPreferences.Editor editor = joinPref.edit();
        editor.putString(MainUtils.GROUP_ID, joinFamilyIDText.getText().toString());
        editor.commit();
        return jsonRequest;
    }

    public String replaceDevice() {
        String registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
        //Header headerItem = new Header("testlogin2@gmail.com",deviceCode,registationId,"testpass");
        Group groupItem = new Group("", joinFamilyIDText.getText().toString());
        Device deviceItem = new Device(0, nameDevice.getText().toString(), "ss", "android", registationId, "", checkType());
        GroupUser groupUser = new GroupUser(0, 0, 0, mActiveCode.getSelectedItem().toString());
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest(4, groupItem, deviceItem, groupUser);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(joinGroupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        SharedPreferences.Editor editor = joinPref.edit();
        editor.putString(MainUtils.GROUP_ID, joinFamilyIDText.getText().toString());
        editor.commit();
        return jsonRequest;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {//license
        switch (buttonView.getId()) {
            case R.id.license_used :
                if(isChecked) {
                    mRBtnUnUse.setChecked(false);
                    //get data license not used
                    //groupRequestController.getListLicense(Constants.ActionTypeGetLicenseUsed, joinFamilyIDText.getText().toString());
                }
                break;
            case R.id.license_not_used :
                if(isChecked) {
                    mRBtnUse.setChecked(false);
                    //get data license  used
                    //groupRequestController.getListLicense(Constants.ActionTypeGetLicenseUnUsed, joinFamilyIDText.getText().toString());
                }

                break;
        }
    }


    private class JoinGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = groupRequestController.BASE_URL + joinGroup();
            Log.d(TAG, "link: " + link);
            result = groupRequestController.connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    String status_result = message.getString("Status");
                    if (status_result.equals("1")) {
                        if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children) {
                            if (description_result.equals("Existed")) {
                                showReplaceDeviceDialog();
                            } else {
                                isJoinSuccess = true;
                                SetupWizardActivity.setTypeJoin(Constants.JoinSuccess, mContext);
                                Log.e(TAG, "isJoinSuccess" + isJoinSuccess);
                                groupRequestController.updateSuccess();
                                //Toast.makeText(JoinGroupActivity.this, "Success join", Toast.LENGTH_SHORT).show();
                                SetupWizardActivity.setNameDevice(nameDevice.getText().toString(), mContext);
                                setBundle("join");
                                Intent childSchedule = new Intent(JoinGroupActivity.this, ChildSchedulerActivity.class);
                                childSchedule.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(childSchedule);
                            }
                        } else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
                            Log.e(TAG, "isJoinSuccess Manager" + isJoinSuccess);
                            //Additional Parent
                            Intent familyManagement = new Intent(JoinGroupActivity.this, FamilyManagerment.class);
                            startActivity(familyManagement);
                        }

                    } else if (status_result.equals("2")) {
                        if(SetupWizardActivity.getModeDevice(mContext) == Constants.Children) {
                            final Toast wrongActiveCode = Toast.makeText(mContext, "The activation code is incorrect, please check Activation code and try again.", Toast.LENGTH_SHORT);
                            wrongActiveCode.show();
                            MainUtils.extendDisplayTimeOfToast(wrongActiveCode);
                        }else if(SetupWizardActivity.getModeDevice(mContext) == Constants.Manager){
                            final Toast toastWrongFamilyID = Toast.makeText(mContext, "The Family ID entered is incorrect, please check the ID and try again.", Toast.LENGTH_SHORT);
                            toastWrongFamilyID.show();
                            MainUtils.extendDisplayTimeOfToast(toastWrongFamilyID);
                        }
                        Intent intent = new Intent(mContext, GcmIntentService.class);//send intent to get token
                        intent.putExtra("key", "register");
                        startService(intent);
                        //SetupWizardActivity.setModeDevice(MainUtils.MODE_DEFAULT, mContext);
                        //SetupWizardActivity.setTypeJoin(Constants.JoinFail, mContext);
                        //Intent joinActivity = new Intent(getApplicationContext(), JoinGroupActivity.class);
                        //startActivity(joinActivity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(JoinGroupActivity.this);
            mDialog.setCancelable(false);
            mDialog.setInverseBackgroundForced(false);
            if (SetupWizardActivity.getModeDevice(mContext) == Constants.Children) {
                mDialog.setMessage("Please wait while a connection is made with parent device...");
            }else {
                mDialog.setMessage("Request to server...");
            }
            mDialog.show();
        }
    }

    public static void setBundle(String type){
         bundle = new Bundle();
         bundle.putString("title", type);
    }

    public static String getBundleString(){
        if (bundle == null){
            return null;
        }
        return bundle.getString("title");
    }

    public static void setChildProfileServerId(int id, Context context){
        childServerId = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = childServerId.edit();
        editor.putInt(MainUtils.CHILD_PROFILE_SERVER_ID, id);
        editor.commit();
    }

    public static int getChildProfileIdServer(Context context){
        childServerId = PreferenceManager.getDefaultSharedPreferences(context);
        return childServerId.getInt(MainUtils.CHILD_PROFILE_SERVER_ID, 0);
    }

    private class ReplaceDeviceAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;

        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = groupRequestController.BASE_URL + replaceDevice();
            Log.d(TAG, "link: " + link);
            result = groupRequestController.connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG, "onPostExecute" + result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    String status_result = message.getString("Status");
                    JSONObject data = jsonObj.getJSONObject("Data");
                    int id_profile_server = data.getInt("id");
                    Log.e(TAG, "id_profile_server : "+id_profile_server);
                    //ArrayList<ChildKeepFocusItem> listChild =  mDataHelper.getAllKeepFocusFromDb();
                    //MainUtils.childKeepFocusItem.setId_profile_server(id_profile_server);
                    //mDataHelper.updateFocusItem(MainUtils.childKeepFocusItem);
                    setChildProfileServerId(id_profile_server, mContext);
                    if (status_result.equals("1")) {
                        Toast.makeText(mContext, "Replace device successfully", Toast.LENGTH_SHORT).show();
                        groupRequestController.updateSuccess();
                        SetupWizardActivity.setTypeJoin(Constants.JoinSuccess, mContext);
                        SetupWizardActivity.setNameDevice(nameDevice.getText().toString(), mContext);
                        setBundle("replace");
                        Intent childSchedule = new Intent(JoinGroupActivity.this, ChildSchedulerActivity.class);
                        childSchedule.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(childSchedule);
                    } else {
                        Toast.makeText(mContext, "Replace device failure", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(JoinGroupActivity.this);
            mDialog.setCancelable(false);
            mDialog.setInverseBackgroundForced(false);
            mDialog.setMessage("Request to server...");
            mDialog.show();
        }
    }

    public void setLicenseList(ArrayList<String> list){
        Log.d(GroupRequestController.class.getName(),"setLicenseList");
        listLicenses = list;
        ArrayAdapter licenseAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                listLicenses);
        licenseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mActiveCode.setAdapter(licenseAdapter);

    }

    private void createTestSpinner(){
        ArrayList <String> list = new ArrayList<String>();
        for (int i=0; i < 1000; i++) {
            list.add(i+"");
        }
        ArrayAdapter licenseAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                list);
        licenseAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mActiveCode.setAdapter(licenseAdapter);
    }

}
package com.android.keepfocus.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ChildAppItem;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.gcm.GcmIntentService;
import com.android.keepfocus.receive.DevicePolicyReceiver;
import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.GroupUser;
import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.server.request.model.JoinGroupRequest;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinGroupActivity extends Activity {
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
    private EditText joinFamilyIDText, mActiveCode;
    private RelativeLayout layoutChooseMode;
    private RadioButton mRBtnManage, mRBtnChild;
    public String deviceCode;
    private GroupRequestController groupRequestController;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    public static String MANAGER = "manager";
    public static String CHILDREN = "child";
    private int typeJoin = 0;
    private EditText nameDevice;
    public static boolean isJoinSuccess;
    private ChildKeepFocusItem childKeepFocusItem;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_member_qr_code);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DevicePolicyReceiver.class);
        mContext = this;


        joinFamilyIDText = (EditText) findViewById(R.id.familyId);
        mActiveCode = (EditText) findViewById(R.id.activeCode);
        nameDevice = (EditText) findViewById(R.id.deviceName);
        btnImageDone = (Button) findViewById(R.id.doneImageBtn);
        layoutChooseMode = (RelativeLayout) findViewById(R.id.layout_choose_mode);
        //layoutChooseMode.setAlpha((float) 0.9);
        layoutChooseMode.setClickable(false);
        layoutChooseMode.setEnabled(false);
        mRBtnManage = (RadioButton) findViewById(R.id.rbtn_manage);
        mRBtnChild = (RadioButton) findViewById(R.id.rbtn_child);
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

        if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager){
            mActiveCode.setVisibility(View.GONE);
            mRBtnManage.setChecked(true);
            mRBtnChild.setChecked(false);
        }else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children){
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
        joinPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    token = intent.getStringExtra("token");
                    Log.d(TAG,"REGISTATION_ID = "+token);
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
        btnImageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createRequestDialog();
                if (!mActiveCode.getText().toString().equals("")) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isTurnOnAdmin = true;
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

    public void createRequestDialog() {
        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(
                JoinGroupActivity.this);
        requestBuilder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)
                JoinGroupActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.request_join_dialog, null);
        spinnerType = (Spinner) view.findViewById(R.id.spinerType);
        listMemberType = new String[2];
        listMemberType[0] = "Manager";
        listMemberType[1] = "Children";
        ArrayAdapter adapterType = new ArrayAdapter(this, android.R.layout.simple_spinner_item,listMemberType);
        spinnerType.setAdapter(adapterType);

        final LinearLayout linceseLayout = (LinearLayout) view.findViewById(R.id.linceseLayout);
        linceseLayout.setVisibility(View.GONE);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    linceseLayout.setVisibility(View.GONE);
                } else if (position==1) {
                    linceseLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        requestBuilder.setView(view);
        requestBuilder.setMessage("Request Join Family");
        requestBuilder.setCancelable(true);
        requestBuilder.setPositiveButton("Accept",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        requestBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog requestDialog = requestBuilder.create();
        requestDialog.show();

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

    private String checkType(){
        if (typeJoin == 0) return CHILDREN;
        else return MANAGER;
    }

    public String joinGroup(){
        String registationId = joinPref.getString(MainUtils.REGISTATION_ID, "");
        //Header headerItem = new Header("testlogin2@gmail.com",deviceCode,registationId,"testpass");
        Group groupItem = new Group("", joinFamilyIDText.getText().toString());
        Device deviceItem = new Device(0,nameDevice.getText().toString(),"ss","android",registationId,"",checkType());
        GroupUser groupUser = new GroupUser(0,0,0,mActiveCode.getText().toString());
        JoinGroupRequest joinGroupRequest = new JoinGroupRequest(3, groupItem, deviceItem, groupUser);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(joinGroupRequest);
        Log.d(TAG, "jsonRequest: " + jsonRequest);
        return jsonRequest;
    }

    private class JoinGroupAsynTask extends AsyncTask<ParentGroupItem, Void, String> {
        ProgressDialog mDialog;
        @Override
        protected String doInBackground(ParentGroupItem... params) {
            String result = "";
            String link;
            link = groupRequestController.BASE_URL + joinGroup();
            Log.d(TAG,"link: "+link);
            result = groupRequestController.connectToServer(link);
            //result = serverUtils.postData(BASE_URL,getListGroup());
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            String jsonStr = result;
            Log.d(TAG,"onPostExecute"+result);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject message = jsonObj.getJSONObject("Message");
                    String description_result = message.getString("Description");
                    String status_result = message.getString("Status");
                    if(status_result.equals("1")) {
                        isJoinSuccess = true;
                        SetupWizardActivity.setTypeJoin(Constants.JoinSuccess, mContext);
                        Log.e("vinh", "isJoinSuccess" + isJoinSuccess);
                        Toast.makeText(JoinGroupActivity.this, "Success join", Toast.LENGTH_SHORT).show();
                        groupRequestController.updateSuccess();
                        if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Children) {
                            Log.e("vinh", "isJoinSuccess Child" + isJoinSuccess);
                            SetupWizardActivity.setNameDevice(nameDevice.getText().toString(), mContext);
                            Intent childSchedule = new Intent(JoinGroupActivity.this, ChildSchedulerActivity.class);
                            startActivity(childSchedule);
                        } else if (SetupWizardActivity.getModeDevice(getApplicationContext()) == Constants.Manager) {
                            Log.e("vinh", "isJoinSuccess Manager" + isJoinSuccess);
                            Intent familyManagement = new Intent(JoinGroupActivity.this, FamilyManagerment.class);
                            startActivity(familyManagement);
                        }

                    } else {
                        SetupWizardActivity.setModeDevice(MainUtils.MODE_DEFAULT, mContext);
                        SetupWizardActivity.setTypeJoin(Constants.JoinFail, mContext);

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
}
package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.fancycoverflow.FancyCoverFlow;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.settings.CircleGroupAdapter;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

public class FamilyManagerment extends Activity{

    private static final String TAG = FamilyManagerment.class.getSimpleName();
    private CircleGroupAdapter adapterGroup;
    private FancyCoverFlow fancyCoverFlowGroup;
    private boolean lable[];

    private final static long DURATION_SHORT = 200;
    private LinearLayout btnOrange;
    private LinearLayout btnYellow;
    private LinearLayout btnGreen;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    private TextView mTextNoGroup;
    public ArrayList<ParentGroupItem> listFamily;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    static Button notifCount;
    static int mNotifCount = 0;
    private GroupRequestController groupRequestController;
    private IntentFilter intentFilter;
    private LinearLayout detailLayout;
    private Animation bottomUp;
    private Animation bottomDown;
    private RelativeLayout layoutList;
    private ArrayList<ParentGroupItem> listDefault;
    private HorizontalListView listTwoFamily;
    private TextView textName;
    private TextView nameFamily;
    private TextView listDeviceName;
    private TextView textFamilyID;
    private static int PICK_IMAGE = 1;
    private static int positionNow = 0;
    private Typeface mTextFamilyIDFace;
    private boolean isFirstTime = false;

    private BroadcastReceiver getDatabaseReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MainUtils.mIsEditNameGroup) {
                  MainUtils.mIsEditNameGroup = false;
                  nameFamily.setText(MainUtils.parentGroupItem.getGroup_name() + " Family");
            } else {
                displayProfile();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_family_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initFontStyle();
        fancyCoverFlowGroup = (FancyCoverFlow) findViewById(R.id.fancyCoverFlow);
        fancyCoverFlowGroup.setUnselectedAlpha(1.0f);
        fancyCoverFlowGroup.setUnselectedSaturation(0.0f);
        fancyCoverFlowGroup.setUnselectedScale(0.5f);
        fancyCoverFlowGroup.setSpacing(50);
        fancyCoverFlowGroup.setMaxRotation(0);
        fancyCoverFlowGroup.setScaleDownGravity(0.2f);
        fancyCoverFlowGroup.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
        mContext = this;
        mDataHelper = new MainDatabaseHelper(mContext);
        ParentGroupItem defaultParent = new ParentGroupItem();
        defaultParent.setGroup_name("Default");//care null parent
        listDefault = new ArrayList<ParentGroupItem>(1);
        listDefault.add(defaultParent);

        listTwoFamily = (HorizontalListView) findViewById(R.id.listTwoFamily);
        listTwoFamily.setVisibility(View.GONE);

        setTitle(R.string.family_management);
        layoutList = (RelativeLayout) findViewById(R.id.layout_list);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        nameFamily = (TextView) findViewById(R.id.nameFamily);
        listDeviceName = (TextView) findViewById(R.id.listDeviceName);
        textFamilyID = (TextView) findViewById(R.id.family_id);
        detailLayout.setVisibility(View.GONE);
        //layoutClose = (ImageButton) findViewById(R.id.layoutClose);
        bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this,
                R.anim.bottom_down);

        displayProfile();

        groupRequestController = new GroupRequestController(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);

        fancyCoverFlowGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("thong.nv", " position : " + position);
                lable[position] = true;
                MainUtils.parentGroupItem = adapterGroup.getItem(position);
                showTextOfChild();
                showAddMember(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showTextOfChild() {
        if (MainUtils.parentGroupItem == null) {
            return;
        }
        nameFamily.setText(MainUtils.parentGroupItem.getGroup_name() + " Family");
        ArrayList<ParentMemberItem> listDevice = MainUtils.parentGroupItem.getListMember();
        if (listDevice.size() > 0) {
            String listDeviceText = " ";
            for (int i =0; i < listDevice.size(); i++){
                listDeviceText += listDevice.get(i).getName_member() + ", ";
            }
            listDeviceText = listDeviceText.substring(0,listDeviceText.length()-2);
            listDeviceName.setText(listDeviceText);
        } else {
            listDeviceName.setText(" ");
        }
    }

    private void initFontStyle() {
        Log.d(TAG, "initFontStyle");
        mTextFamilyIDFace = Typeface.createFromAsset(getAssets(),
                "fonts/DroidSerif-Bold.ttf");
    }

    public void displayProfile() {
        listFamily = mDataHelper.getAllGroupItemParent();
        if (listFamily.size() == 0){
            if(SetupWizardActivity.getModeDevice(mContext) == Constants.Admin && !isFirstTime){
                isFirstTime = true;
                createNewGroup();
            }
            if (nameFamily != null && nameFamily.getVisibility() == View.VISIBLE) {
                nameFamily.setVisibility(View.GONE);
            }
            if (listDeviceName != null && listDeviceName.getVisibility() == View.VISIBLE) {
                listDeviceName.setVisibility(View.GONE);
            }

            mTextNoGroup.setText(R.string.tap_add_to_begin_setup);
            adapterGroup = new CircleGroupAdapter(this, listDefault);
            layoutList.setVisibility(View.GONE);
            listTwoFamily.setVisibility(View.GONE);
            detailLayout.setVisibility(View.GONE);
            if(detailLayout.getVisibility() == View.VISIBLE) {
                detailLayout.setVisibility(View.GONE);
            }
        } else {
            if (nameFamily != null) {
                nameFamily.setVisibility(View.VISIBLE);
            }
            if (listDeviceName != null && listDeviceName.getVisibility() == View.GONE) {
                listDeviceName.setVisibility(View.VISIBLE);
            }
            mTextNoGroup.setText(" ");
            listTwoFamily.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            adapterGroup = new CircleGroupAdapter(this, listFamily);
            if(detailLayout.getVisibility() == View.VISIBLE) {
                detailLayout.setVisibility(View.GONE);
            }
        }
        fancyCoverFlowGroup.setAdapter(adapterGroup);
        lable = new boolean[adapterGroup.getCount()];
    }

    private View getItemId(int position) {
        return adapterGroup.getView(position, null, null);
    }

    View preView;

    public void onMainButtonClicked(View btn) {
        btnGreen = (LinearLayout) btn.findViewById(R.id.btn_left_side);
        btnOrange = (LinearLayout) btn.findViewById(R.id.btn_right_side);
        btnYellow = (LinearLayout) btn.findViewById(R.id.btn_center_side);
        textName = (TextView) btn.findViewById(R.id.family_name);

        if (btnGreen.getVisibility() != View.VISIBLE && btnOrange.getVisibility() != View.VISIBLE && btnYellow.getVisibility() != View.VISIBLE) {
            show(btnYellow, 1, 0);
            show(btnGreen, 2, 0);
            show(btnOrange, 3, 0);
            btn.playSoundEffect(SoundEffectConstants.CLICK);
            textName.setVisibility(View.GONE);
        }

        if (preView != null && preView != btn) {
            btnGreen = (LinearLayout) preView.findViewById(R.id.btn_left_side);
            btnOrange = (LinearLayout) preView.findViewById(R.id.btn_right_side);
            btnYellow = (LinearLayout) preView.findViewById(R.id.btn_center_side);
            textName = (TextView) preView.findViewById(R.id.family_name);
            hide(btnOrange);
            hide(btnYellow);
            hide(btnGreen);
            textName.setVisibility(View.VISIBLE);
        }
        preView = btn;
    }

    private final void hide(final View child) {
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(0)
                .translationY(0)
                .start();
        child.setVisibility(View.INVISIBLE);
    }

    private final void show(final View child, final int position, final int radius) {
        float angleDeg = 180.f;
        int dist = radius;
        switch (position) {
            case 1:
                angleDeg += 0.f;
                child.setVisibility(View.VISIBLE);
                break;
            case 2:
                angleDeg += 90.f;
                child.setVisibility(View.VISIBLE);
                break;
            case 3:
                angleDeg += 180.f;
                child.setVisibility(View.VISIBLE);
                break;
        }

        final float angleRad = (float) (angleDeg * Math.PI / 180.f);

        final Float x = dist * (float) Math.cos(angleRad);
        final Float y = dist * (float) Math.sin(angleRad);
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(x)
                .translationY(y)
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult= " + data);
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data!=null) {
            Uri selectedImage = data.getData();
            boolean success = true;
            Bitmap bitmap = null;
            try
            {
                success = true;
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver() , selectedImage);
            }
            catch (Exception e)
            {
                success = false;
            }
            if(success) {
                MainUtils.parentGroupItem.setIcon_uri(selectedImage.toString());
                mDataHelper.updateGroupItem(MainUtils.parentGroupItem);
            }
            //familyIconEdit.setImageBitmap(bitmap);
//            if(MainUtils.parentGroupItem!=null){
//                coverFlow.scrollToPosition(positionNow);
//            }
            adapterGroup.notifyDataSetChanged();
           // displayProfile();


            //familyIconEdit.setImageURI(selectedImage);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        showTextOfChild();
        //displayProfile();
        registerReceiver(getDatabaseReceiver, intentFilter);
        if (!isFirstTime &&
                (getIntent().getBooleanExtra("NotificationAccept",false) ||// manager click notification
                (SetupWizardActivity.getModeDevice(mContext) == Constants.Manager &&//manager first resume
                SetupWizardActivity.getTypeJoin(mContext) == Constants.JoinSuccess))) {
            isFirstTime = true;
            getAllGroupInServer();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (SetupWizardActivity.getModeDevice(mContext) == Constants.Manager){
            if(SetupWizardActivity.getTypeJoin(mContext) == Constants.JoinSuccess) {
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        }else if(SetupWizardActivity.getModeDevice(mContext) == Constants.Admin) {
            finishAffinity();
        }
    }

    public void getAllGroupInServer(){
        groupRequestController.getGroupInServer();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getDatabaseReceiver);
    }

    public void createNewGroup() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new family").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isFirstTime = true;
                        String name = mEditText.getText().toString();
                        mEditText.setError(null);
                        View focusView = null;
                        if (!name.equals("")) {
                            //ParentGroupItem parentItem = new ParentGroupItem();
                            //parentItem.setGroup_name(mEditText.getText().toString());
                            //mDataHelper.addGroupItemParent(parentItem);
                            if (isNameInValid(name)){
                                mEditText.setError("The Family name cannot contain space.");
                                final Toast familyNameError = Toast.makeText(FamilyManagerment.this, "The Family name cannot contain space.", Toast.LENGTH_LONG);
                                familyNameError.show();
                                MainUtils.extendDisplayTimeOfToast(familyNameError);
                                focusView = mEditText;
                                focusView.requestFocus();
                            }else {
                                displayProfile();
                                MainUtils.parentGroupItem = new ParentGroupItem(mContext);
                                MainUtils.parentGroupItem.setGroup_name(mEditText.getText().toString());
                                //MainUtils.parentGroupItem.setGroup_code("registationId");
                                groupRequestController.testAddGroupInServer();
                            }
                        } else {
                            //dialog.cancel();
                            mEditText.setError("The Family name cannot empty");
                            Toast.makeText(FamilyManagerment.this, "The Family name cannot empty", Toast.LENGTH_LONG).show();
                            final Toast familyNameError = Toast.makeText(FamilyManagerment.this, "The Family name cannot contain space.", Toast.LENGTH_LONG);
                            familyNameError.show();
                            MainUtils.extendDisplayTimeOfToast(familyNameError);
                            focusView = mEditText;
                            focusView.requestFocus();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();

        mAlertDialog.show();
    }

    private boolean isNameInValid(String name){
        return name.contains(" ");
    }


    public void changeIcon(int position) {
        MainUtils.parentGroupItem = adapterGroup.getItem(position);
        Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        positionNow = position;
        startActivityForResult(i, PICK_IMAGE);
    }


    public void showDetail(int position) {
        if (MainUtils.parentGroupItem.getIs_restore() == 1) {
           // groupRequestController = new GroupRequestController(activity);
            groupRequestController.updateListDevice();
            MainUtils.parentGroupItem.setIs_restore(0);
            mDataHelper.updateGroupItem(MainUtils.parentGroupItem);
        }
        MainUtils.parentGroupItem = adapterGroup.getItem(position);
        Intent intent = new Intent(this, DeviceMemberManagerment.class);
        startActivity(intent);
    }

    public void showAddMember(int position) {
        MainUtils.parentGroupItem = adapterGroup.getItem(position);


        if(detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }

        textFamilyID.setText(MainUtils.parentGroupItem.getGroup_code());
        textFamilyID.setTypeface(mTextFamilyIDFace);
    }


    public void onItemLongClick(int position) {
        if(SetupWizardActivity.getModeDevice(mContext) == Constants.Admin) {
            deleteProfile(position);
        } else {
            Toast.makeText(mContext, "You don't have delete permission.", Toast.LENGTH_SHORT).show();
        }
    }



    public void deleteProfile(final int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to remove this family?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Remove family")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainUtils.parentGroupItem =listFamily.get(position);
                        //mDataHelper.deleteGroupItemById(MainUtils.parentGroupItem.getId_group());
                        //displayProfile();
                        groupRequestController.deleteGroupInServer();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mDeleteDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (SetupWizardActivity.getTypeRestoreFamily(mContext) == Constants.RestoreFamilySuccess){
            menu.getItem(2).setVisible(false);
        }
        Log.d(TAG, "onPrepareOptionsMenu listFamily.size() " + listFamily.size());
        if (listFamily.size() == 0) {
            menu.getItem(1).setVisible(false);
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global, menu);
       /* if (SetupWizardActivity.getTypeRestoreFamily(mContext) == Constants.RestoreFamilySuccess){
            menu.getItem(2).setVisible(false);
        }
        Log.d(TAG, "onCreateOptionsMenu listFamily.size() " + listFamily.size());
        if (listFamily.size() == 0) {
            menu.getItem(1).setVisible(false);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(SetupWizardActivity.getModeDevice(mContext) == Constants.Admin) {
            switch (item.getItemId()) {
                case R.id.action_add :
                    createNewGroup();
                    break;
                case R.id.rename:
                    renameGroup();
                    break;
                case R.id.action_update :
                    SetupWizardActivity.setTypeRestoreFamily(Constants.RestoreFamilySuccess, mContext);
                    item.setVisible(false);
                    groupRequestController.getGroupInServer();
                    break;
            }
            return super.onOptionsItemSelected(item);
        } else {
            Toast.makeText(mContext, "You don't have this permission.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void addNewMember(int position) {
        detailLayout.startAnimation(bottomUp);
        detailLayout.setVisibility(View.VISIBLE);
        textFamilyID.setText(listFamily.get(position).getGroup_code());
        textFamilyID.setTypeface(mTextFamilyIDFace);
    }

    public void renameGroup() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);
        mEditText.setText(MainUtils.parentGroupItem.getGroup_name());
        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Edit name : ")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            MainUtils.parentGroupItem.setGroup_name(mEditText.getText().toString());
                            MainUtils.mIsEditNameGroup = true;
                            groupRequestController.updateGroupInServer();
                        } else {
                            dialog.cancel();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();

        mAlertDialog.show();
    }
}

package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.FamilyViewAdapter;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;
/*
* For Family Managerment screen
* */
public class GroupManagermentActivity extends Activity implements OnClickListener {

    private HorizontalListView listProperties;
    private LinearLayout headerView;
    private ImageView mFABBtnCreate;
    private TextView mTextNoGroup;
    public ArrayList<ParentGroupItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    static Button notifCount;
    static int mNotifCount = 0;
    private FamilyViewAdapter mProfileAdapter;
    private GroupRequestController groupRequestController;
    private GetDatabaseReceiver getDatabaseReceiver;
    private IntentFilter intentFilter;
    private LinearLayout detailLayout;
    private ImageButton layoutClose;
    private Animation bottomUp;
    private Animation bottomDown;
    private EditText editName;
    private Button doneEdit;
    private ImageView familyIconEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.family_group_layout);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Family management");

        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;

        listProperties = (HorizontalListView) findViewById(R.id.listFamily);

        mFABBtnCreate = (ImageView) findViewById(R.id.createButton);

        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        editName = (EditText) findViewById(R.id.editFamilyName);
        familyIconEdit = (ImageView) findViewById(R.id.editFamilyIcon);
        familyIconEdit.setOnClickListener(this);

        doneEdit = (Button) findViewById(R.id.layoutEditDone);
        doneEdit.setOnClickListener(this);

        detailLayout.setVisibility(View.GONE);
        layoutClose = (ImageButton) findViewById(R.id.layoutClose);
        layoutClose.setOnClickListener(this);

        bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this,
                R.anim.bottom_down);

        mFABBtnCreate.setOnClickListener(this);
        mDataHelper = new MainDatabaseHelper(mContext);
        groupRequestController = new GroupRequestController(this);
        displayProfile();
        getDatabaseReceiver = new GetDatabaseReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                displayProfile();
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);

/*        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            startService(new Intent(this, KeepFocusMainService.class));
        }
        startService(new Intent(this, ServiceBlockApp.class));*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createButton:
                createNewGroup();
                break;

            case R.id.layoutClose:
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    detailLayout.setVisibility(View.GONE);
                }

            case R.id.layoutEditDone:
                String name = editName.getText().toString();
                MainUtils.parentGroupItem.setGroup_name(name);
                mDataHelper.updateGroupItem(MainUtils.parentGroupItem);
                displayProfile();
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    detailLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.editFamilyIcon:
                Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();

            default:
                break;
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayProfile();
        registerReceiver(getDatabaseReceiver,intentFilter);
        //groupRequestController.getGroupInServer();
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
        mTextMsg.setText("Name family :");

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new family")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            //ParentGroupItem parentItem = new ParentGroupItem();
                            //parentItem.setGroup_name(mEditText.getText().toString());
                            //mDataHelper.addGroupItemParent(parentItem);
                            MainUtils.parentGroupItem = new ParentGroupItem();
                            MainUtils.parentGroupItem.setGroup_name(mEditText.getText().toString());
                            //MainUtils.parentGroupItem.setGroup_code("registationId");
                            //displayProfile();
                            groupRequestController.testAddGroupInServer();
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

    public void displayProfile() {
        listBlockPropertiesArr = mDataHelper.getAllGroupItemParent();
        mProfileAdapter = new FamilyViewAdapter(this, R.layout.layout_family,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_group);
        }else{
            mTextNoGroup.setText("");
        }

    }


    public void showListMember(int position) {
        MainUtils.parentGroupItem = mProfileAdapter.getItem(position);
        Intent intent = new Intent(this, GroupDetail.class);
        startActivity(intent);
    }


    public void showDetail(int position) {
        MainUtils.parentGroupItem = mProfileAdapter.getItem(position);
        editName.setText(MainUtils.parentGroupItem.getGroup_name().toString());
        //Intent intent = new Intent(this, GroupDetail.class);
        //startActivity(intent);
        if(detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        } else {
            detailLayout.setVisibility(View.GONE);
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onItemLongClick(int position) {
        deleteProfile(position);
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
                        MainUtils.parentGroupItem =listBlockPropertiesArr.get(position);
                        groupRequestController.deleteGroupInServer();
                        //displayProfile();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mDeleteDialog.show();
    }

    public static class GetDatabaseReceiver extends BroadcastReceiver {//must be static
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_settings :
                groupRequestController.getGroupInServer();
                break;
            case R.id.menu_settings :
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewMember(int position) {
        mView = getLayoutInflater().inflate(R.layout.add_member_layout, null);
        TextView mIDText = (TextView) mView.findViewById(R.id.add_member_ID);
        mIDText.setText("ID Family : " + listBlockPropertiesArr.get(position).getGroup_code());
        mTextMsg = (TextView) mView.findViewById(R.id.add_member_text);
        mTextMsg.setText(getResources().getString(R.string.add_member_text));

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new device")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();
    }


}
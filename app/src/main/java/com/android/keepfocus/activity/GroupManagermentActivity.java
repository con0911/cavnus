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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.GroupAdapterView;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;
/*
* For Family Managerment screen
* */
public class GroupManagermentActivity extends Activity implements OnClickListener {

    private ListView listProperties;
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
    private GroupAdapterView mProfileAdapter;
    private GroupRequestController groupRequestController;
    private GetDatabaseReceiver getDatabaseReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Family management");

        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;

        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        mFABBtnCreate = (ImageView) findViewById(R.id.createButton);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);
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
            default:
                break;
        }
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
                            MainUtils.parentGroupItem.setGroup_code("registationId");
                            //Intent intent = new Intent(GroupManagermentActivity.this, GroupDetail.class);
                            //startActivity(intent);
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
        mProfileAdapter = new GroupAdapterView(this, R.layout.radial,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_group);
        }else{
            mTextNoGroup.setText("");
        }

    }

    public void onItemClick(int position) {
        MainUtils.parentGroupItem = mProfileAdapter.getItem(position);
        Intent intent = new Intent(this, GroupDetail.class);
        startActivity(intent);
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
}
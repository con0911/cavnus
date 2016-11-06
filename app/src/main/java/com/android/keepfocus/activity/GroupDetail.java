package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.ChildTimeListAdapter;
import com.android.keepfocus.controller.MemberViewAdapter;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

public class GroupDetail extends Activity implements View.OnClickListener{
    private HorizontalListView listProperties;
    private LinearLayout headerView;
    //private ImageView mFABBtnCreate;
    private TextView mTextNoGroup;
    public ArrayList<ParentMemberItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private TextView mIDText;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    private MemberViewAdapter mProfileAdapter;
    private GroupRequestController groupRequestController;
    private GroupManagermentActivity.GetDatabaseReceiver getDatabaseReceiver;
    private IntentFilter intentFilter;

    private ArrayList<ParentProfileItem> listProfileItem;
    private ListView listTime;
    private ChildTimeListAdapter timeListAdapter;


    private LinearLayout detailLayout;
    private ImageButton layoutClose;
    private Animation bottomUp;
    private Animation bottomDown;
    private EditText editName;
    private ImageButton doneEdit;

    private ArrayList<TextView> listStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_member_layout);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;
        listProperties = (HorizontalListView) findViewById(R.id.listMember);
        setTitle(MainUtils.parentGroupItem.getGroup_name());
        mDataHelper = new MainDatabaseHelper(mContext);
        groupRequestController = new GroupRequestController(this);
        getDatabaseReceiver = new GroupManagermentActivity.GetDatabaseReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                displayMember();
                setTitle(MainUtils.parentGroupItem.getGroup_name());
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);


        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        //editName = (EditText) findViewById(R.id.editMemberName);

        //doneEdit = (ImageButton) findViewById(R.id.layoutEditDone);
        //doneEdit.setOnClickListener(this);

        listTime = (ListView) findViewById(R.id.listTime);


        detailLayout.setVisibility(View.GONE);
        //layoutClose = (ImageButton) findViewById(R.id.layoutClose);
        //layoutClose.setOnClickListener(this);
        bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title +"'s Family");
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayMember();
        registerReceiver(getDatabaseReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(getDatabaseReceiver);
        if(detailLayout.getVisibility() == View.VISIBLE) {
            //hideKeyboard();
            //detailLayout.startAnimation(bottomDown);
            //detailLayout.setVisibility(View.GONE);
        }
    }


    public void addNewMember() {
        mView = getLayoutInflater().inflate(R.layout.add_member_layout, null);
        mIDText = (TextView) mView.findViewById(R.id.add_member_ID);
        mIDText.setText("ID Family : "+MainUtils.parentGroupItem.getGroup_code());
        mTextMsg = (TextView) mView.findViewById(R.id.add_member_text);
        mTextMsg.setText(getResources().getString(R.string.add_member_text));

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new device")
                .setPositiveButton("Create Test", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParentMemberItem childItem = new ParentMemberItem();
                        childItem.setName_member("Test device");
                        childItem.setType_member(0);
                        childItem.setId_member(mDataHelper.addMemberItemParent(childItem,MainUtils.parentGroupItem.getId_group()));
                        MainUtils.parentGroupItem.getListMember().add(childItem);

                        displayMember();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();
    }

    public void displayMember() {
        mDataHelper.makeDetailOneGroupItemParent(MainUtils.parentGroupItem);
        listBlockPropertiesArr = MainUtils.parentGroupItem.getListMember();
        mProfileAdapter = new MemberViewAdapter(this, R.layout.layout_member,
                0, listBlockPropertiesArr) ;
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_member);
        }else{
            mTextNoGroup.setText("");
        }

    }


    public void displayDetailTime(){
        mDataHelper.makeDetailOneMemberItemParent(MainUtils.memberItem);
        listProfileItem = MainUtils.memberItem.getListProfile();
        timeListAdapter = new ChildTimeListAdapter(this, R.layout.time_adapter, 0, listProfileItem);
        listTime.setAdapter(timeListAdapter);
    }

    public void goToScheduler(int position){
        MainUtils.parentProfile = timeListAdapter.getItem(position);
        Intent intent = new Intent(GroupDetail.this, SchedulerConfigActivity.class);
        startActivity(intent);
    }

    public void onItemClick(int position) {
        MainUtils.memberItem = mProfileAdapter.getItem(position);
        Intent intent = new Intent(GroupDetail.this, ParentSchedulerActivity.class);
        startActivity(intent);
    }

    public void listScheduler(int position) {
        MainUtils.memberItem = mProfileAdapter.getItem(position);
        Intent intent = new Intent(GroupDetail.this, ParentSchedulerActivity.class);
        startActivity(intent);
    }

    public void onItemLongClick(int position) {
        deleteMember(position);
    }

    public void deleteMember(int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to remove this device?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Delete device")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mDataHelper.deleteMemberItemById(mProfileAdapter.getItem(mPosition).getId_member());
                        MainUtils.parentGroupItem.getListMember().remove(mPosition);
                        displayMember();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.add :
                addNewMember();
                break;
            case R.id.rename :
                renameGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void renameGroup() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);
        mTextMsg.setText("Name family:");

            mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Edit name : ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (!mEditText.getText().toString().equals("")) {
                                MainUtils.parentGroupItem.setGroup_name(mEditText.getText().toString());
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

    public void showDetail(int position) {
        MainUtils.memberItem = mProfileAdapter.getItem(position);
        //editName.setText(MainUtils.memberItem.getName_member().toString());
        if(detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        } else {
            detailLayout.setVisibility(View.GONE);
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }
        displayDetailTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layoutClose:
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    detailLayout.setVisibility(View.GONE);
                }

            case R.id.layoutEditDone:
                String name = editName.getText().toString();
                MainUtils.memberItem.setName_member(name);
                mDataHelper.updateMemberItem(MainUtils.memberItem);
                displayMember();
                if(detailLayout.getVisibility() == View.VISIBLE) {
                    hideKeyboard();
                    detailLayout.startAnimation(bottomDown);
                    detailLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.editFamilyName:
                Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();

            default:
                break;
        }
    }



    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

}


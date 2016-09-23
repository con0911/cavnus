package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.AdapterMemberProfile;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

public class GroupDetail extends Activity {
    private ListView listProperties;
    private LinearLayout headerView;
    //private ImageView mFABBtnCreate;
    private TextView mTextNoGroup;
    public ArrayList<ParentMemberItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private TextView mIDText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    private AdapterMemberProfile mProfileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_group_management);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;
        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);
        setTitle(MainUtils.parentGroupItem.getGroup_name());
        mDataHelper = new MainDatabaseHelper(mContext);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title +"'s Family");
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayMember();
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
        mProfileAdapter = new AdapterMemberProfile(this, R.layout.tab_group,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_member);
        }else{
            mTextNoGroup.setText("");
        }

    }

    public void onItemClick(int position) {
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
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
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
        }
        return super.onOptionsItemSelected(item);
    }

}


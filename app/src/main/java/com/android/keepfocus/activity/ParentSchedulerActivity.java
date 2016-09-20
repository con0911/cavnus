package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.AdapterParentProfile;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

/**
 * Created by sev_user on 9/15/2016.
 */
public class ParentSchedulerActivity extends Activity {
    private TextView mTextNoMember;
    private ListView listProperties;
    private LinearLayout headerView;
    private ImageView mFABBtnCreate;
    private TextView mTextNoGroup;
    public ArrayList<ParentProfileItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    private AdapterParentProfile mProfileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);

        mFABBtnCreate = (ImageView) findViewById(R.id.createButton);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;
        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);
        setTitle(MainUtils.memberItem.getName_member());

        mFABBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewGroup();
            }
        });
        mDataHelper = new MainDatabaseHelper(mContext);
        displayProfile();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayProfile();
    }

    public void createNewGroup() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);
        mTextMsg.setText("Name profile:");

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add new profile")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            ParentProfileItem childItem = new ParentProfileItem();
                            childItem.setName_profile(mEditText.getText().toString());
                            childItem.setDay_profile("");
                            childItem.setId_profile(mDataHelper.addProfileItemParent(childItem,MainUtils.memberItem.getId_member()));
                            MainUtils.memberItem.getListProfile().add(childItem);

                            MainUtils.parentProfile = childItem;
                            Intent intent = new Intent(ParentSchedulerActivity.this, SchedulerConfigActivity.class);
                            startActivity(intent);
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
        String textNoProfile = String.format(getResources().getString(R.string.text_no_profile)
                ,""+MainUtils.memberItem.getName_member());
        mDataHelper.makeDetailOneMemberItemParent(MainUtils.memberItem);
        listBlockPropertiesArr = MainUtils.memberItem.getListProfile();
        mProfileAdapter = new AdapterParentProfile(this, R.layout.tab_group,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(textNoProfile);
        }else{
            mTextNoGroup.setText("");
        }

    }

    public void onItemClick(int position) {
        MainUtils.parentProfile = mProfileAdapter.getItem(position);
        Intent intent = new Intent(ParentSchedulerActivity.this, SchedulerConfigActivity.class);
        startActivity(intent);
    }

    public void onItemLongClick(int position) {
        deleteProfile(position);
    }

    public void deleteProfile(int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to delete this profile?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Delete profile")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mDataHelper.deleteProfileItemById(mProfileAdapter.getItem(mPosition).getId_profile());
                        MainUtils.memberItem.getListProfile().remove(mPosition);
                        displayProfile();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mDeleteDialog.show();
    }


}

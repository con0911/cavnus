package com.android.keepfocus.activity;

import java.util.ArrayList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.AdapterProfile;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.utils.MainUtils;

/**
 * Created by Sion on 9/12/2016.
 */
public class GroupManagermentActivity extends AppCompatActivity implements OnClickListener {

    private ListView listProperties;
    private LinearLayout headerView;
    //private ImageView mFABBtnCreate;
    private FloatingActionButton fab;
    private TextView mTextNoGroup;
    public GroupManagermentActivity CustomListView = null;
    public ArrayList<ParentGroupItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    static Button notifCount;
    static int mNotifCount = 0;
    private AdapterProfile mProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_management);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Group management");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;

        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        //mFABBtnCreate = (ImageView) findViewById(R.id.createButton);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);
        //mFABBtnCreate.setOnClickListener(this);
        fab.setOnClickListener(this);
        listProperties.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(GroupManagermentActivity.this, GroupDetail.class);
                startActivity(intent);
            }
        });
        mDataHelper = new MainDatabaseHelper(mContext);
        displayProfile();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            //startService(new Intent(this, KeepFocusMainService.class));
        }
        //startService(new Intent(this, BlockLaunchService.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                createNewGroup();
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            default:
                break;
        }
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
        mTextMsg.setText("Name group :");

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Create new group")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            ParentGroupItem parentItem = new ParentGroupItem();
                            parentItem.setGroup_name(mEditText.getText().toString());
                            parentItem.setGroup_code("");
                            mDataHelper.addGroupItemParent(parentItem);
                            MainUtils.parentGroupItem = parentItem;
                            Intent intent = new Intent(GroupManagermentActivity.this, GroupDetail.class);
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
        listBlockPropertiesArr = mDataHelper.getAllGroupItemParent();
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_group);
        }else{
            mProfileAdapter = new AdapterProfile(this, R.layout.tab_group,
                    0, listBlockPropertiesArr);
            listProperties.setAdapter(mProfileAdapter);
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

/*    public void longClickProfile(int position) {
        final int mPosition = position;
        mView = getLayoutInflater().inflate(R.layout.setup_profile_popup, null);
        Button btn_Delete = (Button) mView.findViewById(R.id.btnDelete);
        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle(mProfileAdapter.getItem(mPosition).getNameFocus()).create();
        btn_Delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile(mPosition);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }*/

    public void deleteProfile(int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to delete this group?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Delete group")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //mDataHelper.deleteFocusItemById(mProfileAdapter.getItem(mPosition).getKeepFocusId());
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
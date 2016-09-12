package com.android.keepfocus.activity;

import java.util.ArrayList;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.utils.MainUtils;

/**
 * Created by Sion on 9/12/2016.
 */
public class GroupManagermentActivity extends Activity implements OnClickListener {

    private ListView listProperties;
    private LinearLayout headerView;
    private ImageView mFABBtnCreate;
    public GroupManagermentActivity CustomListView = null;
    public ArrayList<ChildKeepFocusItem> listBlockPropertiesArr;
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
        setContentView(R.layout.group_management);
        mContext = this;

        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        mFABBtnCreate = (ImageView) findViewById(R.id.createButton);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);
        mFABBtnCreate.setOnClickListener(this);
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
            case R.id.createButton:
                createNewProfile();
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

    public void createNewProfile() {
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout, null);
        mEditText = (EditText) mView.findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);
        mTextMsg.setText("Create new group");

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Create new group")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            ChildKeepFocusItem keepFocus = new ChildKeepFocusItem();
                            keepFocus.setNameFocus(mEditText.getText().toString());
                            keepFocus.setDayFocus("");
                            mDataHelper.addNewFocusItem(keepFocus);
                            MainUtils.childKeepFocusItem = keepFocus;
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
        listBlockPropertiesArr = mDataHelper.getAllKeepFocusFromDb();
        mProfileAdapter = new AdapterProfile(this, R.layout.tab_group,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
    }

    public void onItemClick(int position) {
        MainUtils.childKeepFocusItem = mProfileAdapter.getItem(position);
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
        mTextMsg.setText("Delete?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Delete group")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mDataHelper.deleteFocusItemById(mProfileAdapter.getItem(mPosition).getKeepFocusId());
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
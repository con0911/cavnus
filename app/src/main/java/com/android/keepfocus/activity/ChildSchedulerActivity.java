package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.AdapterChildProfile;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ChildNotificationItemMissHistory;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.service.KeepFocusMainService;
import com.android.keepfocus.service.ServiceBlockApp;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

public class ChildSchedulerActivity extends Activity {
    private ListView listProperties;
    private LinearLayout headerView;
    private TextView mTextNoGroup;
    public ArrayList<ChildKeepFocusItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;
    private AdapterChildProfile mProfileAdapter;

    static int mNotifCount = 0;
    static Button notifCount;
    private DialogNotificationHistory mDialogNotiHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_group_management);
        setTitle("Your schedule");
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mContext = this;
        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);

        mDataHelper = new MainDatabaseHelper(mContext);
        displayProfile();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            startService(new Intent(this, KeepFocusMainService.class));
        }
        startService(new Intent(this, ServiceBlockApp.class));
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
        mTextMsg.setText("Name schedule:");

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Add test schedule")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!mEditText.getText().toString().equals("")) {
                            ChildKeepFocusItem childItem = new ChildKeepFocusItem();
                            childItem.setNameFocus(mEditText.getText().toString());
                            childItem.setDayFocus("");
                            mDataHelper.addNewFocusItem(childItem);
                            MainUtils.childKeepFocusItem = childItem;
                            Intent intent = new Intent(ChildSchedulerActivity.this, ChildSchedulerDetail.class);
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

    private ArrayList<ChildNotificationItemMissHistory> getListMissingNotification(){
        return mDataHelper.getListNotificaionHistoryItem();
    }

    private void updateMissingNotifications() {
        int size = getListMissingNotification().size();
        setNotifCount(size);
    }

    public void setNotifCount(int count){
        mNotifCount = count;
        invalidateOptionsMenu();
    }

    public void displayProfile() {
        listBlockPropertiesArr = mDataHelper.getAllKeepFocusFromDb();
        mProfileAdapter = new AdapterChildProfile(this, R.layout.tab_group,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText("No scheduler available");
        }else{
            mTextNoGroup.setText("");
        }

    }

    public void onItemClick(int position) {
        MainUtils.childKeepFocusItem = mProfileAdapter.getItem(position);
        Intent intent = new Intent(ChildSchedulerActivity.this, ChildSchedulerDetail.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.child_menu, menu);
        View count = menu.findItem(R.id.notification).getActionView();
        notifCount = (Button) count.findViewById(R.id.notif_count);
        notifCount.setText(mNotifCount+"");
        MenuItem item = menu.findItem(R.id.notification);
        if (mNotifCount == 0) {
            item.setVisible(false);
        }
        count.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialogNotiHistory = new DialogNotificationHistory(mContext, ChildSchedulerActivity.this);
                mDialogNotiHistory.show();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.add :
                createNewGroup();//add for test
                break;

            case R.id.notification :
                //show notification
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

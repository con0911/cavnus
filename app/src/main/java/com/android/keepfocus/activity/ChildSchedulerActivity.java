package com.android.keepfocus.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.ChildKeepFocusAdapter;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ChildNotificationItemMissHistory;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.server.request.controllers.SchedulerRequestController;
import com.android.keepfocus.service.KeepFocusMainService;
import com.android.keepfocus.service.ServiceBlockApp;
import com.android.keepfocus.utils.Constants;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

public class ChildSchedulerActivity extends Activity {
    private static final String TAG = "ChildSchedulerActivity";
    private ListView listProperties;
    private LinearLayout headerView;
    private TextView mTextNoGroup;
    private Button mBtnRestore;
    public ArrayList<ChildKeepFocusItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private ChildKeepFocusAdapter mProfileAdapter;

    static int mNotifCount = 0;
    static Button notifCount;
    private DialogNotificationHistory mDialogNotiHistory;
    private IntentFilter intentFilter;
    private SchedulerRequestController mSchedulerRequestController;

    private MainDatabaseHelper keepData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_group_management);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        mBtnRestore = (Button) findViewById(R.id.btn_restore);
        mContext = this;
        setTitle(SetupWizardActivity.getNameDevice(mContext));
        initIntentFilter();
        //keepData = new MainDatabaseHelper(this);
        listProperties = (ListView) findViewById(R.id.listP);
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_view_profile, null);
        listProperties.addHeaderView(headerView);
        listProperties.addFooterView(headerView);

        mDataHelper = new MainDatabaseHelper(mContext);
        displayProfile();
        mSchedulerRequestController = new SchedulerRequestController(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startService(new Intent(this, KeepFocusMainService.class));
        }
        startService(new Intent(this, ServiceBlockApp.class));

        String bundle = JoinGroupActivity.getBundleString();
        if (bundle != null && bundle.equals("replace") && listBlockPropertiesArr.size() == 0){
            mBtnRestore.setVisibility(View.VISIBLE);
        }else {
            mBtnRestore.setVisibility(View.GONE);
        }
        mBtnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mSchedulerRequestController.restoreSchedulerForChild();
            }
        });
    }

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_CHILD_SCHEDULER);
        intentFilter.addAction(MainUtils.EXIT_CHILD_TO_SETUPWIZARD);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive" + action);
            if (intent == null) {
                return;
            }

            if (MainUtils.UPDATE_CHILD_SCHEDULER.equals(action)) {
                displayProfile();
            }
            if (MainUtils.EXIT_CHILD_TO_SETUPWIZARD.equals(action)) {
                backToSetupWizard();
            }
        }
    };

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title +"'s schedule");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        //moveTaskToBack(true);
        //finish();
        Log.d(TAG, "onBackPressed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SetupWizardActivity.getTypeJoin(mContext) == Constants.JoinFail){
            backToSetupWizard();
        }
        displayProfile();
        registerReceiver(myReceiver, intentFilter);
        updateMissingNotifications();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    private void backToSetupWizard(){
        Intent intent = new Intent(ChildSchedulerActivity.this, SetupWizardActivity.class);
        startActivity(intent);
    }

    private ArrayList<ChildNotificationItemMissHistory> getListMissingNotification() {
        return mDataHelper.getListNotificaionHistoryItem();
    }

    private void updateMissingNotifications() {
        int size = getListMissingNotification().size();
        setNotifCount(size);
    }

    public void setNotifCount(int count) {
        mNotifCount = count;
        invalidateOptionsMenu();
    }

    public void displayProfile() {
        listBlockPropertiesArr = mDataHelper.getAllKeepFocusFromDb();
        mProfileAdapter = new ChildKeepFocusAdapter(this, R.layout.tab_group,
                0, listBlockPropertiesArr);
        listProperties.setAdapter(mProfileAdapter);
        if (listBlockPropertiesArr.size() == 0) {
            mTextNoGroup.setText("No schedule available");
        } else {
            mTextNoGroup.setText("");
        }

    }

    public void goToSchedule(int position) {
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
        notifCount.setText(mNotifCount + "");
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
            case R.id.notification:
                //show notification
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

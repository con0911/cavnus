package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.keepfocus.R;
import com.android.keepfocus.controller.ChildTimeListAdapter;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.server.request.controllers.GroupRequestController;
import com.android.keepfocus.server.request.controllers.SchedulerRequestController;
import com.android.keepfocus.settings.CoverFlowAdapterDevice;
import com.android.keepfocus.settings.CoverFlowAdapterDevice2;
import com.android.keepfocus.settings.CustomListView;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

/**
 * Created by Sion on 11/8/2016.
 */
public class DeviceMemberManagerment extends Activity implements View.OnClickListener{
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
    private GroupRequestController groupRequestController;
    private BroadcastReceiver getDatabaseReceiver;
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
    private RelativeLayout layoutList;
    private ArrayList<ParentMemberItem> listDefault;
    private FeatureCoverFlow coverFlow;
    private CoverFlowAdapterDevice adapter;
    private boolean lable[];
    private final static long DURATION_SHORT = 200;
    private LinearLayout btnOrange;
    private LinearLayout btnYellow;
    private LinearLayout btnGreen;

    private Button btnAddSchedule;
    private Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday,
            btnSaturday, btnSunday;
    private EditText mEditScheduleName;
    private LinearLayout statusBarTime, emptyTimeView;
    private String dayBlock;
    private String titleDayBlock;
    private Button mBtnAddTime;
    private CustomListView listTimer;
    private TimePicker timePickerFrom, timePickerTo;
    private Button fromBt, toBt;
    private MainDatabaseHelper keepData;
    private SchedulerRequestController schedulerRequestController;
    private TextView nameDevice;
    private TextView listScheduler;
    private TextView textName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_member_layout);
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        coverFlow = (FeatureCoverFlow) findViewById(R.id.coverflow);
        mContext = this;
        keepData = new MainDatabaseHelper(this);
        createDefault();//for 0 item
        listProperties = (HorizontalListView) findViewById(R.id.listMember);
        listProperties.setVisibility(View.GONE);
        setTitle(MainUtils.parentGroupItem.getGroup_name());
        mDataHelper = new MainDatabaseHelper(mContext);
        groupRequestController = new GroupRequestController(this);
        schedulerRequestController = new SchedulerRequestController(this);

        layoutList = (RelativeLayout) findViewById(R.id.layout_list);
        nameDevice = (TextView) findViewById(R.id.nameFamily);
        listScheduler = (TextView) findViewById(R.id.listDeviceName);

        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);
        intentFilter.addAction(MainUtils.UPDATE_SCHEDULER);


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

        displayMember();
        coverFlow.setOnScrollPositionListener(onScrollListener());
        coverFlow.setOnItemSelectedListener(onItemSelectedListener());
        getDatabaseReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (MainUtils.UPDATE_FAMILY_GROUP.equals(action)) {
                    displayMember();
                    setTitle(MainUtils.parentGroupItem.getGroup_name());
                } else if (MainUtils.UPDATE_SCHEDULER.equals(action)) {
                    displayDetailTime();
                }
            }
        };

        btnAddSchedule = (Button) findViewById(R.id.btn_add_schedule);
        btnAddSchedule.setOnClickListener(this);

    }

    public void createDefault(){
        ParentMemberItem defaultDevice = new ParentMemberItem();
        defaultDevice.setName_member("Default");//care null parent
        listDefault = new ArrayList<ParentMemberItem>(1);
        listDefault.add(defaultDevice);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title + "'s Family");
    }

    @Override
    protected void onResume() {
        //updateStatusTime(MainUtils.parentProfile.getListTimer());
        super.onResume();
        //groupRequestController.updateListDevice();
        //displayMember();
        registerReceiver(getDatabaseReceiver, intentFilter);
    }

    public void schedulerChecked(boolean isChecked, int position) {
        MainUtils.parentProfile = timeListAdapter.getItem(position);
        if (isChecked){
            MainUtils.parentProfile.setActive(true);
        }else {
            MainUtils.parentProfile.setActive(false);
        }
        schedulerRequestController.updateScheduler();
    }

    @Override
    protected void onPause() {
        super.onPause();

        formatStringDayBlock(dayBlock);
        if (MainUtils.parentProfile != null) {
            MainUtils.parentProfile.setDay_profile(dayBlock);
            MainUtils.parentProfile.setName_profile(setTitleDayBlock(dayBlock));
            keepData.updateProfileItem(MainUtils.parentProfile);
        }
        unregisterReceiver(getDatabaseReceiver);
        if(detailLayout.getVisibility() == View.VISIBLE) {
            //hideKeyboard();
            //detailLayout.startAnimation(bottomDown);
            //detailLayout.setVisibility(View.GONE);
        }
    }

    public void updateStatusTime(ArrayList<ParentTimeItem> listTime) {
        boolean chooseList[] = new boolean[145];
        // init
        for (int i = 0; i < 144; i++) {
            chooseList[i] = false;
        }
        if (listTime.size() == 0) {
            for (int i = 0; i < 144; i++) {
                listStatus.get(i).setBackgroundColor(
                        Color.parseColor("#3B5998"));
            }
            return;
        }
        for (int i = 0; i < listTime.size(); i++) {
            ParentTimeItem timeItem = listTime.get(i);
            if (timeItem.getTypeTime() == ParentTimeItem.INTIME_TYPE) {
                for (int j = timeItem.getHourBegin() * 60
                        + timeItem.getMinusBegin(); j <= timeItem.getHourEnd()
                        * 60 + timeItem.getMinusEnd(); j++) {
                    chooseList[j / 10] = true;
                }
            } else {
                for (int j = 1; j <= timeItem.getHourEnd() * 60
                        + timeItem.getMinusEnd(); j++) {
                    chooseList[j / 10] = true;
                }
                for (int j = timeItem.getHourBegin() * 60
                        + timeItem.getMinusBegin(); j <= 1440; j++) {
                    chooseList[j / 10] = true;
                }
            }
        }
        for (int i = 0; i < 144; i++) {
            if (chooseList[i]) {
                listStatus.get(i).setBackgroundColor(
                        Color.parseColor("#3B5998"));
            } else {
                listStatus.get(i).setBackgroundColor(
                        Color.parseColor("#BDBDBD"));
            }
        }

    }

    public void createNewSchedule(){
        mView = getLayoutInflater().inflate(R.layout.scheduler_edit, null);
        MainUtils.parentProfile = new ParentProfileItem();
        MainUtils.parentProfile.setDay_profile("");
        displayScreen(); // setup Button, image,...
        loadDayButton();

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Create new schedule")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        formatStringDayBlock(dayBlock);
                        MainUtils.parentProfile.setDay_profile(dayBlock);
                        MainUtils.parentProfile.setName_profile(setTitleDayBlock(dayBlock));
                        //displayDetailTime();
                        schedulerRequestController.addNewScheduler();
                        //keepData.updateProfileItem(MainUtils.parentProfile); // update
                    }
                }).setNegativeButton("CANCEL", new DatePickerDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();
    }

    public void editSchedule(){
        mView = getLayoutInflater().inflate(R.layout.scheduler_edit, null);
        displayScreen(); // setup Button, image,...
        loadDayButton(); // status of day button

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setTitle("Edit this schedule")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //MainUtils.parentProfile.setName_profile(mEditScheduleName.getText().toString()); // set name
                        // profile
                        formatStringDayBlock(dayBlock);
                        MainUtils.parentProfile.setDay_profile(dayBlock);
                        MainUtils.parentProfile.setName_profile(setTitleDayBlock(dayBlock));
                        displayDetailTime();
                        schedulerRequestController.updateScheduler();
                        //updateStatusTime(MainUtils.parentProfile.getListTimer());
                    }
                }).setNegativeButton("CANCEL", new DatePickerDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();

    }

    public void displayScreen() {
        btnMonday = (Button) mView.findViewById(R.id.details_day_monday);
        btnTuesday = (Button) mView.findViewById(R.id.details_day_tuesday);
        btnWednesday = (Button) mView.findViewById(R.id.details_day_wednesday);
        btnThursday = (Button) mView.findViewById(R.id.details_day_thursday);
        btnFriday = (Button) mView.findViewById(R.id.details_day_friday);
        btnSaturday = (Button) mView.findViewById(R.id.details_day_saturday);
        btnSunday = (Button) mView.findViewById(R.id.details_day_sunday);
        statusBarTime = (LinearLayout) mView.findViewById(R.id.statusBarTime);
        emptyTimeView = (LinearLayout) mView.findViewById(R.id.empyTimeView);
        //mEditScheduleName = (EditText) mView.findViewById(R.id.editScheduleName);
//        if (MainUtils.parentProfile != null){
//            mEditScheduleName.setText(MainUtils.parentProfile.getName_profile());
//        }
        addItemStatusTime();

        btnMonday.setOnClickListener(this);
        btnTuesday.setOnClickListener(this);
        btnWednesday.setOnClickListener(this);
        btnThursday.setOnClickListener(this);
        btnFriday.setOnClickListener(this);
        btnSaturday.setOnClickListener(this);
        btnSunday.setOnClickListener(this);
        if (MainUtils.parentProfile == null) return;
        //getActionBar().setTitle(MainUtils.parentProfile.getName_profile() + " schedule detail");
        dayBlock = MainUtils.parentProfile.getDay_profile();
        titleDayBlock = MainUtils.parentProfile.getName_profile();

        mBtnAddTime = (Button) mView.findViewById(R.id.details_time_add);
        listTimer = (CustomListView) findViewById(R.id.time_listview);
        mBtnAddTime.setOnClickListener(this);
    }


    public void addItemStatusTime() {
        listStatus = new ArrayList<TextView>();
        for (int i = 0; i < 144; i++) {
            TextView textItem = new TextView(mContext);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            textItem.setLayoutParams(param);
            textItem.setBackgroundColor(Color.parseColor("#3B5998"));
            //
            listStatus.add(textItem);
            statusBarTime.addView(textItem);
            //
        }
    }

    public void loadDayButton() {
        if (dayBlock.contains("Mon")) {
            btnMonday.setBackgroundResource(R.drawable.circle_red);
            btnMonday.setTextColor(Color.WHITE);
        } else {
            btnMonday.setBackgroundResource(R.drawable.circle_white);
            btnMonday.setTextColor(Color.BLACK);
        }
        if (dayBlock.contains("Tue")) {
            btnTuesday.setBackgroundResource(R.drawable.circle_red);
            btnTuesday.setTextColor(Color.WHITE);
        } else {
            btnTuesday.setBackgroundResource(R.drawable.circle_white);
            btnTuesday.setTextColor(Color.BLACK);
        }
        if (dayBlock.contains("Wed")) {
            btnWednesday.setBackgroundResource(R.drawable.circle_red);
            btnWednesday.setTextColor(Color.WHITE);
        } else {
            btnWednesday.setBackgroundResource(R.drawable.circle_white);
            btnWednesday.setTextColor(Color.BLACK);
        }
        if (dayBlock.contains("Thu")) {
            btnThursday.setBackgroundResource(R.drawable.circle_red);
            btnThursday.setTextColor(Color.WHITE);
        } else {
            btnThursday.setBackgroundResource(R.drawable.circle_white);
            btnThursday.setTextColor(Color.BLACK);
        }
        if (dayBlock.contains("Fri")) {
            btnFriday.setBackgroundResource(R.drawable.circle_red);
            btnFriday.setTextColor(Color.WHITE);
        } else {
            btnFriday.setBackgroundResource(R.drawable.circle_white);
            btnFriday.setTextColor(Color.BLACK);
        }
        if (dayBlock.contains("Sat")) {
            btnSaturday.setBackgroundResource(R.drawable.circle_red);
            btnSaturday.setTextColor(Color.WHITE);
        } else {
            btnSaturday.setBackgroundResource(R.drawable.circle_white);
            btnSaturday.setTextColor(Color.BLACK);
        }
        if (dayBlock.contains("Sun")) {
            btnSunday.setBackgroundResource(R.drawable.circle_red);
            btnSunday.setTextColor(Color.WHITE);
        } else {
            btnSunday.setBackgroundResource(R.drawable.circle_white);
            btnSunday.setTextColor(Color.BLACK);
        }
    }

    public String setTitleDayBlock(String day) { // sort day block
        titleDayBlock = "";
        if (day == null){
            return "";
        }
        if (day.contains("Sun"))
            titleDayBlock = "Sun";
        if (day.contains("Mon")) {
            if (titleDayBlock == "") {
                titleDayBlock = "Mon";
            } else {
                titleDayBlock += "-Mon";
            }
        }
        if (day.contains("Tue")) {
            if (titleDayBlock == "") {
                titleDayBlock = "Tue";
            } else {
                titleDayBlock += "-Tue";
            }
        }
        if (day.contains("Wed")) {
            if (titleDayBlock == "") {
                titleDayBlock = "Wed";
            } else {
                titleDayBlock += "-Wed";
            }
        }
        if (day.contains("Thu")) {
            if (titleDayBlock == "") {
                titleDayBlock = "Thu";
            } else {
                titleDayBlock += "-Thu";
            }
        }
        if (day.contains("Fri")) {
            if (titleDayBlock == "") {
                titleDayBlock = "Fri";
            } else {
                titleDayBlock += "-Fri";
            }
        }
        if (day.contains("Sat")) {
            if (titleDayBlock == "") {
                titleDayBlock = "Sat";
            } else {
                titleDayBlock += "-Sat";
            }
        }
        return titleDayBlock;
    }

    public void formatStringDayBlock(String day) { // sort day block
        dayBlock = "";
        if (day == null){
            return;
        }
        if (day.contains("Sun"))
            dayBlock = "Sun";
        if (day.contains("Mon")) {
            if (dayBlock == "") {
                dayBlock = "Mon";
            } else {
                dayBlock += ",Mon";
            }
        }
        if (day.contains("Tue")) {
            if (dayBlock == "") {
                dayBlock = "Tue";
            } else {
                dayBlock += ",Tue";
            }
        }
        if (day.contains("Wed")) {
            if (dayBlock == "") {
                dayBlock = "Wed";
            } else {
                dayBlock += ",Wed";
            }
        }
        if (day.contains("Thu")) {
            if (dayBlock == "") {
                dayBlock = "Thu";
            } else {
                dayBlock += ",Thu";
            }
        }
        if (day.contains("Fri")) {
            if (dayBlock == "") {
                dayBlock = "Fri";
            } else {
                dayBlock += ",Fri";
            }
        }
        if (day.contains("Sat")) {
            if (dayBlock == "") {
                dayBlock = "Sat";
            } else {
                dayBlock += ",Sat";
            }
        }
    }


    private FeatureCoverFlow.OnItemSelectedListener onItemSelectedListener() {
        return new FeatureCoverFlow.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("trungdh", "view : " + view + " pos : " + i + " id : " + l);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActivity", "position: " + position);
                View currentView = coverFlow.findViewWithTag(String.valueOf(position));
                if (currentView != null) {
                    lable[position] = true;
                    MainUtils.memberItem = adapter.getItem(position);
                    onMainButtonClicked(currentView);
                    showDetail(position);
                    nameDevice.setText(MainUtils.memberItem.getName_member());

                }

            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
            }
        };
    }

    private View getItemId(int position) {
        return adapter.getView(position, null, null);
    }

    View preView;

    public void onMainButtonClicked(View btn) {
        btnGreen = (LinearLayout) btn.findViewById(R.id.btn_green);
        btnOrange = (LinearLayout) btn.findViewById(R.id.btn_orange);
        btnYellow = (LinearLayout) btn.findViewById(R.id.btn_yellow);
        textName = (TextView) btn.findViewById(R.id.family_name);

        if (btnGreen.getVisibility() != View.VISIBLE && btnOrange.getVisibility() != View.VISIBLE && btnYellow.getVisibility() != View.VISIBLE) {
            show(btnYellow, 1, 0);
            show(btnGreen, 2, 0);
            show(btnOrange, 3, 0);
            btn.playSoundEffect(SoundEffectConstants.CLICK);
            textName.setVisibility(View.GONE);
        }

        if (preView != null && preView != btn) {
            btnGreen = (LinearLayout) preView.findViewById(R.id.btn_green);
            btnOrange = (LinearLayout) preView.findViewById(R.id.btn_orange);
            btnYellow = (LinearLayout) preView.findViewById(R.id.btn_yellow);
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
        if (listBlockPropertiesArr.size() == 0){
            mTextNoGroup.setText(R.string.text_no_group);
            adapter = new CoverFlowAdapterDevice(this, listDefault);
            layoutList.setVisibility(View.GONE);
            listProperties.setVisibility(View.GONE);
        }else if (listBlockPropertiesArr.size() > 0 && listBlockPropertiesArr.size() <= 3) {
            mTextNoGroup.setText(" ");
            layoutList.setVisibility(View.GONE);
            listProperties.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapterDevice(this, listBlockPropertiesArr);
            CoverFlowAdapterDevice2 adapter2 = new CoverFlowAdapterDevice2(this, listBlockPropertiesArr);
            listProperties.setAdapter(adapter2);
        } else if (listBlockPropertiesArr.size() > 3) {
            mTextNoGroup.setText(" ");
            listProperties.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            adapter = new CoverFlowAdapterDevice(this, listBlockPropertiesArr);
        }
        coverFlow.setAdapter(adapter);
        lable = new boolean[adapter.getCount()];

    }


    public void displayDetailTime(){
        mDataHelper.makeDetailOneMemberItemParent(MainUtils.memberItem);
        listProfileItem = MainUtils.memberItem.getListProfile();
        timeListAdapter = new ChildTimeListAdapter(this, R.layout.time_adapter, 0, listProfileItem);
        listTime.setAdapter(timeListAdapter);
    }

    public void goToScheduler(int position){
        MainUtils.parentProfile = timeListAdapter.getItem(position);
        //Intent intent = new Intent(DeviceMemberManagerment.this, SchedulerConfigActivity.class);
        //startActivity(intent);
        editSchedule();
    }

    public void onItemClick(int position) {
        //MainUtils.memberItem = adapter.getItem(position);
        //Intent intent = new Intent(DeviceMemberManagerment.this, ParentSchedulerActivity.class);
        //startActivity(intent);
        //editSchedule();
    }

    public void listScheduler(int position) {
        MainUtils.memberItem = adapter.getItem(position);
        //Intent intent = new Intent(DeviceMemberManagerment.this, ParentSchedulerActivity.class);
        //startActivity(intent);
        editSchedule();
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
                        mDataHelper.deleteMemberItemById(adapter.getItem(mPosition).getId_member());
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
        MainUtils.memberItem = adapter.getItem(position);
        //editName.setText(MainUtils.memberItem.getName_member().toString());
        if(detailLayout.getVisibility() == View.GONE) {
            //detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        } else {
            detailLayout.setVisibility(View.GONE);
            //detailLayout.startAnimation(bottomUp);
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
                break;

            case R.id.btn_add_schedule:
                createNewSchedule();
                break;

            case R.id.details_day_monday:
                if (!dayBlock.contains("Mon")) {
                    dayBlock = dayBlock + "Mon";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Mon", "");
                    loadDayButton();
                }
                break;
            case R.id.details_day_tuesday:
                if (!dayBlock.contains("Tue")) {
                    dayBlock = dayBlock + "Tue";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Tue", "");
                    loadDayButton();
                }
                break;
            case R.id.details_day_wednesday:
                if (!dayBlock.contains("Wed")) {
                    dayBlock = dayBlock + "Wed";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Wed", "");
                    loadDayButton();
                }
                break;
            case R.id.details_day_thursday:
                if (!dayBlock.contains("Thu")) {
                    dayBlock = dayBlock + "Thu";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Thu", "");
                    loadDayButton();
                }
                break;
            case R.id.details_day_friday:
                if (!dayBlock.contains("Fri")) {
                    dayBlock = dayBlock + "Fri";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Fri", "");
                    loadDayButton();
                }
                break;
            case R.id.details_day_saturday:
                if (!dayBlock.contains("Sat")) {
                    dayBlock = dayBlock + "Sat";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Sat", "");
                    loadDayButton();
                }
                break;
            case R.id.details_day_sunday:
                if (!dayBlock.contains("Sun")) {
                    dayBlock = dayBlock + "Sun";
                    loadDayButton();
                } else {
                    dayBlock = dayBlock.replace("Sun", "");
                    loadDayButton();
                }
                break;

            case R.id.details_time_add:
                showDetailsTime(new ParentTimeItem());
                break;

            default:
                break;
        }
    }

    private void showDetailsTime(final ParentTimeItem timeItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // Get the layout inflater
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.timepicker_dialog, null);
        timePickerFrom = (TimePicker) view.findViewById(R.id.timerPickerFrom);
        timePickerTo = (TimePicker) view.findViewById(R.id.timerPickerTo);
        fromBt = (Button) view.findViewById(R.id.fromBt);
        toBt = (Button) view.findViewById(R.id.toBt);
        // Get data TimeItems
        timePickerFrom.setCurrentHour(timeItem.getHourBegin());
        timePickerFrom.setCurrentMinute(timeItem.getMinusBegin());
        timePickerTo.setCurrentHour(timeItem.getHourEnd());
        timePickerTo.setCurrentMinute(timeItem.getMinusEnd());
        timePickerFrom.setIs24HourView(true);
        timePickerTo.setIs24HourView(true);
        fromBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fromBt.setBackgroundColor(Color.parseColor("#3B5998"));
                fromBt.setTextColor(Color.parseColor("#FFFFFF"));
                toBt.setTextColor(Color.parseColor("#000000"));
                toBt.setBackgroundColor(Color.TRANSPARENT);
                timePickerFrom.setVisibility(View.VISIBLE);
                timePickerTo.setVisibility(View.INVISIBLE);
            }
        });
        toBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toBt.setBackgroundColor(Color.parseColor("#3B5998"));
                fromBt.setBackgroundColor(Color.TRANSPARENT);
                toBt.setTextColor(Color.parseColor("#FFFFFF"));
                fromBt.setTextColor(Color.parseColor("#000000"));
                timePickerTo.setVisibility(View.VISIBLE);
                timePickerFrom.setVisibility(View.INVISIBLE);
            }
        });
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Get time by TimerPicker
                        timeItem.setHourBegin(timePickerFrom.getCurrentHour());
                        timeItem.setMinusBegin(timePickerFrom
                                .getCurrentMinute());
                        timeItem.setHourEnd(timePickerTo.getCurrentHour());
                        timeItem.setMinusEnd(timePickerTo.getCurrentMinute());
                        if (timeItem.getId_timer_parent() == -1) {
                            //MainUtils.parentProfile.setListTimer(null);
                            keepData.addTimeItemParent(timeItem,
                                    MainUtils.parentProfile.getId_profile());
                            MainUtils.parentProfile.getListTimer()
                                    .add(timeItem);

                        } else {
                            keepData.updateTimeParentItem(timeItem);
                        }
                        //updateTimerList();
                        //timeListAdapter.updateStatusTime(listStatus);
                        updateStatusTime(MainUtils.parentProfile.getListTimer());
                    }
                })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //
                            }
                        });
        builder.create().show();
    }

    public void deleteProfile(int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup,
                null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText(R.string.delete_profile_popup_msg);
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(getString(R.string.popup_setting_delete))
                .setPositiveButton(getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                MainUtils.parentProfile = timeListAdapter.getItem(mPosition);
                                schedulerRequestController.deleteScheduler();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.cancel();
                            }
                        }).create();
        mDeleteDialog.show();
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
    }

}

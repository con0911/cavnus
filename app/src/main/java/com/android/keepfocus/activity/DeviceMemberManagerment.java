package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.android.keepfocus.fancycoverflow.FancyCoverFlow;
import com.android.keepfocus.server.request.controllers.DeviceRequestController;
import com.android.keepfocus.server.request.controllers.SchedulerRequestController;
import com.android.keepfocus.settings.CircleMemberAdapter;
import com.android.keepfocus.settings.CustomListView;
import com.android.keepfocus.utils.HorizontalListView;
import com.android.keepfocus.utils.MainUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sion on 11/8/2016.
 */
public class DeviceMemberManagerment extends Activity implements View.OnClickListener {

    private HorizontalListView listProperties;
    private TextView mTextNoGroup;
    public ArrayList<ParentMemberItem> listBlockPropertiesArr;
    private MainDatabaseHelper mDataHelper;
    private Context mContext;
    private View mView;
    private TextView mIDText;
    private TimeListAdapter mTimeListAdapter;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;

    private DeviceRequestController deviceRequestController;
    private IntentFilter intentFilter;
    private ArrayList<ParentProfileItem> listProfileItem;
    private ListView listTime;
    private CustomListView listTimer;
    private ChildTimeListAdapter timeListAdapter;

    private LinearLayout detailLayout;
    private Animation bottomUp;

    private ArrayList<TextView> listStatus;
    private RelativeLayout layoutList;
    private ArrayList<ParentMemberItem> listDefault;
    private CircleMemberAdapter adapterMember;
    private FancyCoverFlow fancyCoverFlowMember;
    private boolean lable[];
    private final static long DURATION_SHORT = 200;

    private Button btnAddSchedule;
    private Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday,
            btnSaturday, btnSunday;
    private LinearLayout layoutTextHours;
    private String dayBlock;
    private String titleDayBlock;
    private Button mBtnAddTime;
    private TimePicker timePickerFrom, timePickerTo;
    private Button fromBt, toBt;
    private MainDatabaseHelper keepData;
    private SchedulerRequestController schedulerRequestController;
    private TextView nameDevice;
    private final static String TAG = "DeviceMemberManagerment";
    private static int positionNow = 0;
    private static int PICK_IMAGE = 1;
    private static int mCurrentHourOfDayFrom;
    private static int mCurrentMinuteFrom;
    private InputMethodManager mImm;

    private BroadcastReceiver getDatabaseReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MainUtils.UPDATE_FAMILY_GROUP.equals(action)) {
                displayMember();
                setTitle(MainUtils.parentGroupItem.getGroup_name() + "'s Family");
            } else if (MainUtils.UPDATE_CHILD_DEVICE.equals(action)) {
                displayMember();
                //setTitle(MainUtils.parentGroupItem.getGroup_name());
            } else if (MainUtils.UPDATE_SCHEDULER.equals(action)) {
                Log.e(TAG, "MainUtils.UPDATE_SCHEDULER ");
                displayDetailTime();
            } else if (MainUtils.BLOCK_ALL.equals(action)) {
                Log.d(TAG, "Success need handle BLOCK_ALL ");
                adapterMember.notifyDataSetChanged();
                //
            } else if (MainUtils.UNBLOCK_ALL.equals(action)) {
                Log.d(TAG, "Success need handle UNBLOCK_ALL ");
                adapterMember.notifyDataSetChanged();
                //
            } else if (MainUtils.ALLOW_ALL.equals(action)) {
                Log.d(TAG, "Success need handle ALLOW_ALL ");
                adapterMember.notifyDataSetChanged();
                //
            } else if (MainUtils.UNALLOW_ALL.equals(action)) {
                Log.d(TAG, "Success need handle UNALLOW_ALL  ");
                adapterMember.notifyDataSetChanged();
                //
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_member_layout);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        creatIntentForBroastcast();
        mTextNoGroup = (TextView) findViewById(R.id.text_no_group);
        initCoverFlowMember();
        keepData = new MainDatabaseHelper(this);
        createDefault();//for 0 item
        listProperties = (HorizontalListView) findViewById(R.id.listMember);
        listProperties.setVisibility(View.GONE);
        //Fix issue FC sometime
        if (MainUtils.parentGroupItem != null) {
            setTitle(MainUtils.parentGroupItem.getGroup_name() + "'s Family");
        } else {
            setTitle("Unknown Family");
        }
        mDataHelper = new MainDatabaseHelper(mContext);

        schedulerRequestController = new SchedulerRequestController(this);
        layoutList = (RelativeLayout) findViewById(R.id.layout_list);
        nameDevice = (TextView) findViewById(R.id.nameFamily);
        detailLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        listTime = (ListView) findViewById(R.id.listTime);
        //detailLayout.setVisibility(View.GONE);
        bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        displayMember();
        deviceRequestController = new DeviceRequestController(this);
        btnAddSchedule = (Button) findViewById(R.id.btn_add_schedule);
        btnAddSchedule.setOnClickListener(this);
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void creatIntentForBroastcast() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(MainUtils.UPDATE_FAMILY_GROUP);
        intentFilter.addAction(MainUtils.UPDATE_SCHEDULER);
        intentFilter.addAction(MainUtils.BLOCK_ALL);
        intentFilter.addAction(MainUtils.UNBLOCK_ALL);
        intentFilter.addAction(MainUtils.ALLOW_ALL);
        intentFilter.addAction(MainUtils.UNALLOW_ALL);
        intentFilter.addAction(MainUtils.UPDATE_CHILD_DEVICE);
    }

    private void initCoverFlowMember() {
        fancyCoverFlowMember = (FancyCoverFlow) findViewById(R.id.fancyCoverFlow);
        fancyCoverFlowMember.setUnselectedAlpha(1.0f);
        fancyCoverFlowMember.setUnselectedSaturation(0.0f);
        fancyCoverFlowMember.setUnselectedScale(0.5f);
        fancyCoverFlowMember.setSpacing(50);
        fancyCoverFlowMember.setMaxRotation(0);
        fancyCoverFlowMember.setScaleDownGravity(0.2f);
        fancyCoverFlowMember.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
        //
        fancyCoverFlowMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(TAG, " position : " + position);
                MainUtils.memberItem = adapterMember.getItem(position);
                showDetail(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void createDefault() {
        ParentMemberItem defaultDevice = new ParentMemberItem();
        defaultDevice.setName_member("Default");//care null parent
        listDefault = new ArrayList<ParentMemberItem>(1);
        listDefault.add(defaultDevice);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected void onResume() {
        //updateTimerList();
        super.onResume();
        //  displayMember();
        registerReceiver(getDatabaseReceiver, intentFilter);
    }

    private void hideKeyboard(View view) {
        //Log.d(TAG, "hideKeyboard() mImm.isInputMethodShown() " + mImm.isInputMethodShown());
        //if (mImm != null && mImm.isInputMethodShown()) {
            //mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                if (listBlockPropertiesArr.size() != 0) {
                    Intent settingIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingIntent);
                } else {
                    Log.d(TAG, "There are no any child added in your group");
                    final Toast noChildToast = Toast.makeText(this, getResources().getString(R.string.error_no_child_add), Toast.LENGTH_LONG);
                    noChildToast.show();
                    MainUtils.extendDisplayTimeOfToast(noChildToast);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void schedulerChecked(boolean isChecked, int position) {
        MainUtils.parentProfile = timeListAdapter.getItem(position);
        if (isChecked) {
            MainUtils.parentProfile.setActive(true);
        } else {
            MainUtils.parentProfile.setActive(false);
        }
        schedulerRequestController.updateScheduler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        formatStringDayBlock(dayBlock);
        unregisterReceiver(getDatabaseReceiver);
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
                        Color.parseColor("#ee5c42"));
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
                        Color.parseColor("#ee5c42"));
            } else {
                listStatus.get(i).setBackgroundColor(
                        Color.parseColor("#ff6bdc60"));
            }
        }

    }

    public boolean checkExistDaySchedule(String dayBlock) {

        return false;
    }

    public void createNewSchedule() {
        mView = getLayoutInflater().inflate(R.layout.scheduler_edit, null);
        MainUtils.parentProfile = new ParentProfileItem();
        MainUtils.parentProfile.setDay_profile("");
        displayScreen(); // setup Button, image,...
        loadDayButton();

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setCancelable(false)
                .setTitle("Create new schedule")
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

    public void editSchedule() {
        mView = getLayoutInflater().inflate(R.layout.scheduler_edit, null);
        displayScreen(); // setup Button, image,...
        loadDayButton(); // status of day button
        //updateStatusTime(MainUtils.parentProfile.getListTimer());
        updateTimerList();

        mAlertDialog = new AlertDialog.Builder(this).setView(mView).setCancelable(false)
                .setTitle("Edit this schedule")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        formatStringDayBlock(dayBlock);
                        MainUtils.parentProfile.setDay_profile(dayBlock);
                        MainUtils.parentProfile.setName_profile(setTitleDayBlock(dayBlock));
                        mDataHelper.updateProfileItem(MainUtils.parentProfile);
                        displayDetailTime();
                        schedulerRequestController.updateScheduler();
                    }
                }).setNegativeButton("CANCEL", new DatePickerDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        mAlertDialog.show();

    }

    public void updateTimerList() {
        mTimeListAdapter = new TimeListAdapter(this, R.layout.show_block_app,
                0, MainUtils.parentProfile.getListTimer());
        listTimer.setAdapter(mTimeListAdapter);
    }

    public void displayScreen() {
        btnMonday = (Button) mView.findViewById(R.id.details_day_monday);
        btnTuesday = (Button) mView.findViewById(R.id.details_day_tuesday);
        btnWednesday = (Button) mView.findViewById(R.id.details_day_wednesday);
        btnThursday = (Button) mView.findViewById(R.id.details_day_thursday);
        btnFriday = (Button) mView.findViewById(R.id.details_day_friday);
        btnSaturday = (Button) mView.findViewById(R.id.details_day_saturday);
        btnSunday = (Button) mView.findViewById(R.id.details_day_sunday);
        layoutTextHours = (LinearLayout) mView.findViewById(R.id.text_hours);
        layoutTextHours.setVisibility(View.INVISIBLE);
        //statusBarTime = (LinearLayout) mView.findViewById(R.id.statusBarTime);
        //emptyTimeView = (LinearLayout) mView.findViewById(R.id.empyTimeView);
        listTimer = (CustomListView) mView.findViewById(R.id.time_listview);
        listTimer.setFocusable(false);
        addItemStatusTime();

        btnMonday.setOnClickListener(this);
        btnTuesday.setOnClickListener(this);
        btnWednesday.setOnClickListener(this);
        btnThursday.setOnClickListener(this);
        btnFriday.setOnClickListener(this);
        btnSaturday.setOnClickListener(this);
        btnSunday.setOnClickListener(this);
        if (MainUtils.parentProfile == null) return;
        dayBlock = MainUtils.parentProfile.getDay_profile();
        titleDayBlock = MainUtils.parentProfile.getName_profile();

        mBtnAddTime = (Button) mView.findViewById(R.id.details_time_add);
        mBtnAddTime.setOnClickListener(this);
    }


    public void addItemStatusTime() {
        listStatus = new ArrayList<TextView>();
        for (int i = 0; i < 144; i++) {
            TextView textItem = new TextView(mContext);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            textItem.setLayoutParams(param);
            textItem.setBackgroundColor(Color.parseColor("#ee5c42"));
            listStatus.add(textItem);
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
        if (day == null) {
            return "";
        }
        if (day.contains("Sun"))
            titleDayBlock = "Sun";
        if (day.contains("Mon")) {
            if ("".equals(titleDayBlock)) {
                titleDayBlock = "Mon";
            } else {
                titleDayBlock += "-Mon";
            }
        }
        if (day.contains("Tue")) {
            if ("".equals(titleDayBlock)) {
                titleDayBlock = "Tue";
            } else {
                titleDayBlock += "-Tue";
            }
        }
        if (day.contains("Wed")) {
            if ("".equals(titleDayBlock)) {
                titleDayBlock = "Wed";
            } else {
                titleDayBlock += "-Wed";
            }
        }
        if (day.contains("Thu")) {
            if ("".equals(titleDayBlock)) {
                titleDayBlock = "Thu";
            } else {
                titleDayBlock += "-Thu";
            }
        }
        if (day.contains("Fri")) {
            if ("".equals(titleDayBlock)) {
                titleDayBlock = "Fri";
            } else {
                titleDayBlock += "-Fri";
            }
        }
        if (day.contains("Sat")) {
            if ("".equals(titleDayBlock)) {
                titleDayBlock = "Sat";
            } else {
                titleDayBlock += "-Sat";
            }
        }
        return titleDayBlock;
    }

    public void formatStringDayBlock(String day) { // sort day block
        dayBlock = "";
        if (day == null) {
            return;
        }
        if (day.contains("Sun"))
            dayBlock = "Sun";
        if (day.contains("Mon")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Mon";
            } else {
                dayBlock += ",Mon";
            }
        }
        if (day.contains("Tue")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Tue";
            } else {
                dayBlock += ",Tue";
            }
        }
        if (day.contains("Wed")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Wed";
            } else {
                dayBlock += ",Wed";
            }
        }
        if (day.contains("Thu")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Thu";
            } else {
                dayBlock += ",Thu";
            }
        }
        if (day.contains("Fri")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Fri";
            } else {
                dayBlock += ",Fri";
            }
        }
        if (day.contains("Sat")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Sat";
            } else {
                dayBlock += ",Sat";
            }
        }
    }

    private View getItemId(int position) {
        return adapterMember.getView(position, null, null);
    }

    public void displayMember() {
        if (MainUtils.parentGroupItem == null) {
            return;
        }
        mDataHelper.makeListMemberInGroup(MainUtils.parentGroupItem);
        listBlockPropertiesArr = MainUtils.parentGroupItem.getListMember();
        if (listBlockPropertiesArr.size() == 0) {
            Log.e(TAG, "listBlockPropertiesArr.size() = 0");
            mTextNoGroup.setText(R.string.text_no_group);
            adapterMember = new CircleMemberAdapter(this, listDefault);
            layoutList.setVisibility(View.GONE);
            listProperties.setVisibility(View.GONE);
            detailLayout.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "listBlockPropertiesArr.size() != 0");
            mTextNoGroup.setText(" ");
            listProperties.setVisibility(View.GONE);
            layoutList.setVisibility(View.VISIBLE);
            detailLayout.setVisibility(View.VISIBLE);
            adapterMember = new CircleMemberAdapter(this, listBlockPropertiesArr);
        }
        fancyCoverFlowMember.setAdapter(adapterMember);
        lable = new boolean[adapterMember.getCount()];

    }

    public void displayDetailTime() {
        if (MainUtils.memberItem == null) {
            return;
        }
        Log.e(TAG, "displayDetailTime ");
        mDataHelper.makeDetailOneMemberItemParent(MainUtils.memberItem);
        listProfileItem = MainUtils.memberItem.getListProfile();
        timeListAdapter = new ChildTimeListAdapter(this, R.layout.time_adapter, 0, listProfileItem);
        listTime.setAdapter(timeListAdapter);
    }

    public void goToScheduler(int position) {
        MainUtils.parentProfile = timeListAdapter.getItem(position);
        editSchedule();
    }

    public void changeIcon(int position) {
        MainUtils.memberItem = adapterMember.getItem(position);
        Toast.makeText(this, "Change avatar", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        positionNow = position;
        startActivityForResult(i, PICK_IMAGE);
    }


    public void showDetail(int position) {
        if (MainUtils.memberItem == null) {
            return;
        }
        MainUtils.memberItem = adapterMember.getItem(position);

        if (nameDevice != null) {
            nameDevice.setText(MainUtils.memberItem.getName_member());
        }
        /*if (detailLayout.getVisibility() == View.GONE) {
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        } else {
            detailLayout.setVisibility(View.GONE);
            detailLayout.startAnimation(bottomUp);
            detailLayout.setVisibility(View.VISIBLE);
        }*/
        displayDetailTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_add_schedule:
                createNewSchedule();
                break;

            case R.id.details_day_monday:
                if (!dayBlock.contains("Mon")) {
                    showDialogConfirm(1);
                } else {
                    dayBlock = dayBlock.replace("Mon", "");
                }
                loadDayButton();
                break;
            case R.id.details_day_tuesday:
                if (!dayBlock.contains("Tue")) {
                    showDialogConfirm(2);
                } else {
                    dayBlock = dayBlock.replace("Tue", "");
                }
                loadDayButton();
                break;
            case R.id.details_day_wednesday:
                if (!dayBlock.contains("Wed")) {
                    showDialogConfirm(3);
                } else {
                    dayBlock = dayBlock.replace("Wed", "");
                }
                loadDayButton();
                break;
            case R.id.details_day_thursday:
                if (!dayBlock.contains("Thu")) {
                    showDialogConfirm(4);
                } else {
                    dayBlock = dayBlock.replace("Thu", "");
                }
                loadDayButton();
                break;
            case R.id.details_day_friday:
                if (!dayBlock.contains("Fri")) {
                    showDialogConfirm(5);
                } else {
                    dayBlock = dayBlock.replace("Fri", "");
                }
                loadDayButton();
                break;
            case R.id.details_day_saturday:
                if (!dayBlock.contains("Sat")) {
                    showDialogConfirm(6);
                } else {
                    dayBlock = dayBlock.replace("Sat", "");
                }
                loadDayButton();
                break;
            case R.id.details_day_sunday:
                if (!dayBlock.contains("Sun")) {
                    showDialogConfirm(0);
                } else {
                    dayBlock = dayBlock.replace("Sun", "");
                }
                loadDayButton();
                break;

            case R.id.details_time_add:
                showDetailsTime(new ParentTimeItem(), false);
                break;

            default:
                break;
        }
    }

    private void showDialogConfirm(int day) {
        final String strDayWeek = MainUtils.DAY_OF_WEEK[day];
        if (MainUtils.memberItem.checkIsContainDay(day)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to continue?");
            builder.setTitle("You have already included this day in another schedule");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dayBlock = dayBlock + strDayWeek;
                    loadDayButton();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            dayBlock = dayBlock + strDayWeek;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult= " + data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            boolean success = true;
            Bitmap bitmap = null;
            Bitmap resizedBm = null;
            byte[] iconByte = null;
            try {
                success = true;
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImage);
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = this.getContentResolver().query(selectedImage, orientationColumn, null, null, null);
                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                Bitmap bmRotate = null;
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bmRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                //bitmap.recycle();
                bitmap = null;
                resizedBm = getResizedBitmap(bmRotate, 225, 225);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedBm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                iconByte = stream.toByteArray();
                resizedBm= null;
            } catch (Exception e) {
                success = false;
            }
            if (success) {
                MainUtils.memberItem.setIcon_array_byte(iconByte);
                mDataHelper.updateMemberItem(MainUtils.memberItem);
            }
            //familyIconEdit.setImageBitmap(bitmap);
            if (MainUtils.memberItem != null) {
                // coverFlow.scrollToPosition(positionNow);
            }
            //displayMember();
            adapterMember.notifyDataSetChanged();

        }
    }

    private void showDetailsTime(final ParentTimeItem timeItem, boolean isEdit) {
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

        timePickerFrom.clearFocus();
        timePickerTo.clearFocus();
        Calendar now = Calendar.getInstance();
        if (!isEdit) {
            timePickerFrom.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
            timePickerFrom.setCurrentMinute(now.get(Calendar.MINUTE));

            timePickerFrom.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    mCurrentHourOfDayFrom = hourOfDay;
                    mCurrentMinuteFrom = minute;
                    timePickerTo.setCurrentHour(mCurrentHourOfDayFrom);
                    timePickerTo.setCurrentMinute(mCurrentMinuteFrom);
                }
            });
        } else {
            timePickerFrom.setCurrentHour(timeItem.getHourBegin());
            timePickerFrom.setCurrentMinute(timeItem.getMinusBegin());
            timePickerTo.setCurrentHour(timeItem.getHourEnd());
            timePickerTo.setCurrentMinute(timeItem.getMinusEnd());
        }
        timePickerFrom.setIs24HourView(false);
        timePickerTo.setIs24HourView(false);
        fromBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(timePickerFrom);
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
                hideKeyboard(timePickerTo);
                toBt.setBackgroundColor(Color.parseColor("#3B5998"));
                fromBt.setBackgroundColor(Color.TRANSPARENT);
                toBt.setTextColor(Color.parseColor("#FFFFFF"));
                fromBt.setTextColor(Color.parseColor("#000000"));
                timePickerTo.setVisibility(View.VISIBLE);
                timePickerFrom.setVisibility(View.INVISIBLE);
            }
        });
        builder.setView(view)
                .setCancelable(false)
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
                        updateTimerList();
                        //timeListAdapter.updateStatusTime(listStatus);
                        updateStatusTime(MainUtils.parentProfile.getListTimer());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    public void onItemLongClick(int position) {
        deleteDevice(position);
    }

    public void deleteDevice(final int position) {
        final int mPosition = position;
        View view = getLayoutInflater().inflate(R.layout.delete_profile_popup, null);
        TextView mTextMsg = (TextView) view.findViewById(R.id.delete_text);
        mTextMsg.setText("Are you sure to remove this device from family?");
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this).setView(view).setTitle("Remove device")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainUtils.memberItem = listBlockPropertiesArr.get(position);
                        deviceRequestController.deleteDeviceInServer();
                        mDataHelper.deleteMemberItemById(adapterMember.getItem(mPosition).getId_member());
                        MainUtils.parentGroupItem.getListMember().remove(mPosition);
                        displayMember();
                        finish();
                        Intent deviceManagement = new Intent(mContext, DeviceMemberManagerment.class);
                        startActivity(deviceManagement);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                }).create();
        mDeleteDialog.show();
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

    public boolean blockAll(int positionMember) {
        if (adapterMember.getItem(positionMember).getIs_alowall() == 1) {
            schedulerRequestController.testUnAllowAllRequest(adapterMember.getItem(positionMember));
        }
        schedulerRequestController.testBlockAllRequest(adapterMember.getItem(positionMember));
        return true;
    }


    public boolean unBlockAll(int positionMember) {
        schedulerRequestController.testUnBlockAllRequest(adapterMember.getItem(positionMember));
        return true;
    }


    public boolean allowAll(int positionMember) {
        if (adapterMember.getItem(positionMember).getIs_blockall() == 1) {
            schedulerRequestController.testUnBlockAllRequest(adapterMember.getItem(positionMember));
        }
        schedulerRequestController.testAllowAllRequest(adapterMember.getItem(positionMember));
        return true;
    }


    public boolean unAllowAll(int positionMember) {
        schedulerRequestController.testUnAllowAllRequest(adapterMember.getItem(positionMember));
        return true;
    }


    public class TimeListAdapter extends ArrayAdapter<ParentTimeItem> {
        MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
                getContext());

        public TimeListAdapter(Context context, int resource,
                               int textViewResourceId, ArrayList<ParentTimeItem> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.show_block_app, null);

            TextView timerDetail = (TextView) convertView
                    .findViewById(R.id.app_block_name);
            ImageView timerIcon = (ImageView) convertView
                    .findViewById(R.id.app_block_icon);
            Button unBlock = (Button) convertView
                    .findViewById(R.id.list_item_button);

            final int mPosition = position;
            unBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParentTimeItem timeItem = MainUtils.parentProfile.getListTimer()
                            .get(mPosition);
                    MainUtils.parentProfile.getListTimer().remove(mPosition);
                    // delete in db
                    kFDHelper.deleteTimerParentItemById(timeItem.getId_timer_parent());
                    // update in view
                    updateTimerList();
                }
            });
            final ParentTimeItem item = getItem(mPosition);
            String textBegin, textEnd;
            textBegin = item.getStringHour(item.getHourBegin(), item.getMinusBegin());
            textEnd = item.getStringHour(item.getHourEnd(), item.getMinusEnd());
            timerDetail.setText(textBegin + " - " + textEnd);
            timerIcon.setImageResource(R.drawable.ic_clock);
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDetailsTime(item, true);
                }
            });
            return convertView;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

}

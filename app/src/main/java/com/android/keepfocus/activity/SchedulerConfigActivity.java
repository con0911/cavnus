package com.android.keepfocus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.keepfocus.R;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentAppItem;
import com.android.keepfocus.data.ParentTimeItem;
import com.android.keepfocus.settings.CustomListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;
import java.util.List;


public class SchedulerConfigActivity extends Activity implements
        OnClickListener {

    private Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday,
            btnSaturday, btnSunday;
    private String dayBlock;
    public Resources res;
    private MainDatabaseHelper keepData;
    private Button mBtnAddApplication;
    private Button mBtnAddTime;
    private AppListAdapter mAppListAdapter;
    private TimeListAdapter mTimeListAdapter;
    private CustomListView listTimer;
    private Context mContext;
    private TimePicker timePickerFrom, timePickerTo;
    private Button fromBt, toBt;
    private LinearLayout statusBarTime, emptyTimeView, emptyAppView;
    private ArrayList<TextView> listStatus;
    private View mView;
    private EditText mEditText;
    private AlertDialog mAlertDialog;
    private TextView mTextMsg;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        keepData = new MainDatabaseHelper(this);
        setContentView(R.layout.scheduler_edit);
        displayScreen(); // setup Button, image,...

        loadDayButton(); // status of day button
        // updateBlockAppList();
    }

/*    public void updateBlockAppList() {
        keepData.makeDetailOneMemberItemParent(MainUtils.memberItem);
        mAppListAdapter = new AppListAdapter(this, R.layout.show_block_app, 0,
                MainUtils.parentProfile.getListAppBlock());
        //listAppView.setAdapter(mAppListAdapter);
        if (MainUtils.parentProfile.getListAppBlock().size() == 0) {
            emptyAppView.setVisibility(View.VISIBLE);
        } else {
            emptyAppView.setVisibility(View.GONE);
        }
    }*/

    public void updateTimerList() {
        mTimeListAdapter = new TimeListAdapter(this, R.layout.show_block_app,
                0, MainUtils.parentProfile.getListTimer());
        listTimer.setAdapter(mTimeListAdapter);
        updateStatusTime(MainUtils.parentProfile.getListTimer());
        if (MainUtils.parentProfile.getListTimer().size() == 0) {
            emptyTimeView.setVisibility(View.VISIBLE);
        } else {
            emptyTimeView.setVisibility(View.GONE);
        }
    }

    public void displayScreen() {
        btnMonday = (Button) findViewById(R.id.details_day_monday);
        btnTuesday = (Button) findViewById(R.id.details_day_tuesday);
        btnWednesday = (Button) findViewById(R.id.details_day_wednesday);
        btnThursday = (Button) findViewById(R.id.details_day_thursday);
        btnFriday = (Button) findViewById(R.id.details_day_friday);
        btnSaturday = (Button) findViewById(R.id.details_day_saturday);
        btnSunday = (Button) findViewById(R.id.details_day_sunday);
        statusBarTime = (LinearLayout) findViewById(R.id.statusBarTime);
        emptyTimeView = (LinearLayout) findViewById(R.id.empyTimeView);
        emptyAppView = (LinearLayout) findViewById(R.id.empyAppView);
        addItemStatusTime();

        btnMonday.setOnClickListener(this);
        btnTuesday.setOnClickListener(this);
        btnWednesday.setOnClickListener(this);
        btnThursday.setOnClickListener(this);
        btnFriday.setOnClickListener(this);
        btnSaturday.setOnClickListener(this);
        btnSunday.setOnClickListener(this);

        getActionBar().setTitle(MainUtils.parentProfile.getName_profile() + " schedule detail");
        dayBlock = MainUtils.parentProfile.getDay_profile();

        //mBtnAddApplication = (Button) findViewById(R.id.selected_app_add);
        mBtnAddTime = (Button) findViewById(R.id.details_time_add);
        //listAppView = (CustomListView) findViewById(R.id.apps_list);
        //listAppView.setFocusable(false); // start in top
        listTimer = (CustomListView) findViewById(R.id.time_listview);
        listTimer.setFocusable(false);
        //mBtnAddApplication.setOnClickListener(this);
        mBtnAddTime.setOnClickListener(this);
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

    public void formatStringDayBlock(String day) { // sort day block
        dayBlock = "";
        if (day.contains("Sun"))
            dayBlock = "Sun";
        if (day.contains("Mon")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Mon";
            } else {
                dayBlock += ", Mon";
            }
        }
        if (day.contains("Tue")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Tue";
            } else {
                dayBlock += ", Tue";
            }
        }
        if (day.contains("Wed")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Wed";
            } else {
                dayBlock += ", Wed";
            }
        }
        if (day.contains("Thu")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Thu";
            } else {
                dayBlock += ", Thu";
            }
        }
        if (day.contains("Fri")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Fri";
            } else {
                dayBlock += ", Fri";
            }
        }
        if (day.contains("Sat")) {
            if ("".equals(dayBlock)) {
                dayBlock = "Sat";
            } else {
                dayBlock += ", Sat";
            }
        }
    }

    @Override
    protected void onResume() {
        //updateBlockAppList();
        updateTimerList();
        super.onResume();
    }

    @Override
    protected void onPause() { // update db profile
        formatStringDayBlock(dayBlock);
        MainUtils.parentProfile.setDay_profile(dayBlock);
        keepData.updateProfileItem(MainUtils.parentProfile);
        super.onPause();
    }

    private void editNamePopup() {// edit name profile
        mView = getLayoutInflater().inflate(R.layout.edit_name_popup_layout,
                null);
        mEditText = (EditText) mView
                .findViewById(R.id.edit_name_edittext_popup);
        mTextMsg = (TextView) mView.findViewById(R.id.edit_name_text);
        mTextMsg.setText(R.string.edit_name_popup_msg);
        mEditText.setText(MainUtils.parentProfile.getName_profile());
        mAlertDialog = new AlertDialog.Builder(this)
                .setView(mView)
                .setTitle(getString(R.string.edit_name))
                .setPositiveButton(getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                MainUtils.parentProfile.setName_profile(mEditText
                                        .getText().toString()); // set name
                                                                // profile
                                keepData.updateProfileItem(MainUtils.parentProfile); // update
                                                                               // db
                                                                               // profile
                                getActionBar().setTitle(
                                        MainUtils.parentProfile.getName_profile() + " schedule detail");// set
                                                                            // title
                                                                            // activity
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

        mAlertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting_properties, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int id = item.getItemId();
        if (id == R.id.delete) { // delete profile
            deleteProfile();
            return true;
        }
        if (id == R.id.edit) { // edit name
            editNamePopup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    public void deleteProfile() {
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
                                keepData.deleteProfileItemById(MainUtils.parentProfile
                                        .getId_profile());
                                finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        case R.id.selected_app_add:
            showInstalledApp();
            break;
        case R.id.details_time_add:
            showDetailsTime(new ParentTimeItem());
            break;
        }
    }

    /**
     * This method use to show all installed application in device
     * 
     * @param @null
     */
    private void showInstalledApp() {
        // TODO Auto-generated method stub
        Intent mShowInstalledAppIntent = new Intent(this,
                InstalledAppActivity.class);
        startActivity(mShowInstalledAppIntent);
    }

    /**
     * 
     */
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
        fromBt.setOnClickListener(new OnClickListener() {

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
        toBt.setOnClickListener(new OnClickListener() {

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
                            keepData.addTimeItemParent(timeItem,
                                    MainUtils.parentProfile.getId_profile());
                            MainUtils.parentProfile.getListTimer()
                                    .add(timeItem);
                        } else {
                            keepData.updateTimeParentItem(timeItem);
                        }
                        updateTimerList();
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

    public void addItemStatusTime() {
        listStatus = new ArrayList<TextView>();
        for (int i = 0; i < 144; i++) {
            TextView textItem = new TextView(mContext);
            LayoutParams param = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
            textItem.setLayoutParams(param);
            textItem.setBackgroundColor(Color.parseColor("#3B5998"));
            //
            listStatus.add(textItem);
            statusBarTime.addView(textItem);
            //
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

    public class AppListAdapter extends ArrayAdapter<ParentAppItem> {

        MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
                getContext());

        public AppListAdapter(Context context, int resource,
                int textViewResourceId, List<ParentAppItem> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.show_block_app, null);

            TextView appName = (TextView) convertView
                    .findViewById(R.id.app_block_name);
            ImageView appIcon = (ImageView) convertView
                    .findViewById(R.id.app_block_icon);
            Button unBlock = (Button) convertView
                    .findViewById(R.id.list_item_button);

            final int mPosition = position;
            unBlock.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParentAppItem appBlock = MainUtils.parentProfile.getListAppBlock()
                            .get(mPosition);
                    MainUtils.parentProfile.getListAppBlock().remove(appBlock);
                    // delete in db
                    kFDHelper.deleteAppParentItemById(appBlock.getId_app_parent());
                    // update in view
                    //updateBlockAppList();
                }
            });
            if (getItem(position).getNameApp() != null) {
                appName.setText(getItem(position).getNameApp());
            }
            try {
                if (getPackageManager().getApplicationIcon(
                        getItem(position).getNamePackage()) != null) {
                    appIcon.setImageDrawable(getPackageManager()
                            .getApplicationIcon(
                                    getItem(position).getNamePackage()));
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return convertView;
        }
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
            unBlock.setOnClickListener(new OnClickListener() {
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
            timerDetail.setText(item.getHourBegin() + " - "
                    + item.getHourEnd());
            timerIcon.setImageResource(R.drawable.ic_clock);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDetailsTime(item);
                }
            });
            return convertView;
        }
    }

}

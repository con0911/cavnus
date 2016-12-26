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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ChildAppItem;
import com.android.keepfocus.data.ChildTimeItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.settings.CustomListView;
import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;
import java.util.List;


public class ChildSchedulerDetail extends Activity implements
        OnClickListener {

    private Button btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday,
            btnSaturday, btnSunday;
    private String dayBlock;
    public Resources res;
    private MainDatabaseHelper keepData;
    private AppListAdapter mAppListAdapter;
    private TimeListAdapter mTimeListAdapter;
    private CustomListView listAppView, listTimer;
    private Context mContext;
    private TimePicker timePickerFrom, timePickerTo;
    private Button fromBt, toBt;
    private LinearLayout statusBarTime, emptyTimeView, emptyAppView;
    private ArrayList<TextView> listStatus;
    private Button mBtnAddApplication;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        keepData = new MainDatabaseHelper(this);
        setContentView(R.layout.scheduler_detail);
        displayScreen(); // setup Button, image,...

        loadDayButton(); // status of day button
        // updateBlockAppList();
    }

    public void updateBlockAppList() {
        mAppListAdapter = new AppListAdapter(this, R.layout.show_block_app, 0,
                MainUtils.childKeepFocusItem.getListAppFocus());
        listAppView.setAdapter(mAppListAdapter);
        if (MainUtils.childKeepFocusItem.getListAppFocus().size() == 0) {
            emptyAppView.setVisibility(View.VISIBLE);
        } else {
            emptyAppView.setVisibility(View.GONE);
        }
    }

    public void updateTimerList() {
        mTimeListAdapter = new TimeListAdapter(this, R.layout.show_block_app,
                0, MainUtils.childKeepFocusItem.getListTimeFocus());
        listTimer.setAdapter(mTimeListAdapter);
        updateStatusTime(MainUtils.childKeepFocusItem.getListTimeFocus());
        if (MainUtils.childKeepFocusItem.getListTimeFocus().size() == 0) {
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

        getActionBar().setTitle(MainUtils.childKeepFocusItem.getNameFocus());
        dayBlock = MainUtils.childKeepFocusItem.getDayFocus();

        listAppView = (CustomListView) findViewById(R.id.apps_list);
        listAppView.setFocusable(false); // start in top
        listTimer = (CustomListView) findViewById(R.id.time_listview);
        listTimer.setFocusable(false);
        mBtnAddApplication = (Button) findViewById(R.id.selected_app_add);
        mBtnAddApplication.setOnClickListener(this);
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
        updateBlockAppList();
        updateTimerList();
        super.onResume();
    }

    @Override
    protected void onPause() { // update db profile
        formatStringDayBlock(dayBlock);
        MainUtils.childKeepFocusItem.setDayFocus(dayBlock);
        keepData.updateFocusItem(MainUtils.childKeepFocusItem);
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    private void showDetailsTime(final ChildTimeItem timeItem) {
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
        timePickerFrom.setIs24HourView(false);
        timePickerTo.setIs24HourView(false);
        // Get data TimeItems
        timePickerFrom.setCurrentHour(timeItem.getHourBegin());
        timePickerFrom.setCurrentMinute(timeItem.getMinusBegin());
        timePickerTo.setCurrentHour(timeItem.getHourEnd());
        timePickerTo.setCurrentMinute(timeItem.getMinusEnd());

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
                        if (timeItem.getTimeId() == -1) {
                            keepData.addTimeItemToDb(timeItem,
                                    MainUtils.childKeepFocusItem.getKeepFocusId());
                            MainUtils.childKeepFocusItem.getListTimeFocus()
                                    .add(timeItem);
                        } else {
                            keepData.updateTimeItem(timeItem);
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

    public void updateStatusTime(ArrayList<ChildTimeItem> listTime) {
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
            ChildTimeItem timeItem = listTime.get(i);
            if (timeItem.getTypeTime() == ChildTimeItem.INTIME_TYPE) {
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
        }
    }

    public class AppListAdapter extends ArrayAdapter<ChildAppItem> {

        MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
                getContext());

        public AppListAdapter(Context context, int resource,
                              int textViewResourceId, List<ChildAppItem> objects) {
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
            unBlock.setVisibility(View.GONE);
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

    public class TimeListAdapter extends ArrayAdapter<ChildTimeItem> {
        MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
                getContext());

        public TimeListAdapter(Context context, int resource,
                               int textViewResourceId, ArrayList<ChildTimeItem> objects) {
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
                    ChildTimeItem timeItem = MainUtils.childKeepFocusItem.getListTimeFocus()
                            .get(mPosition);
                    MainUtils.childKeepFocusItem.getListTimeFocus().remove(mPosition);
                    // delete in db
                    kFDHelper.deleteTimeItemById(timeItem.getTimeId());
                    // update in view
                    updateTimerList();
                }
            });
            final ChildTimeItem item = getItem(mPosition);
            timerDetail.setText(item.getStringHourBegin() + " - "
                    + item.getStringHourEnd());
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

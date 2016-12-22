package com.android.keepfocus.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.keepfocus.R;
import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ChildTimeItem;
import com.android.keepfocus.data.MainDatabaseHelper;
import com.android.keepfocus.data.ParentTimeItem;

import java.util.ArrayList;

public class ChildKeepFocusAdapter extends ArrayAdapter<ChildKeepFocusItem> {
    MainDatabaseHelper kFDHelper = new MainDatabaseHelper(
            getContext());
    private Activity activity;
    private static LayoutInflater inflater = null;

    private TextView titleTime;
    private TextView dayScheduler;
    private Button btnDeleteSchedule;
    private LinearLayout statusTime;
    private Switch isActive;
    private ArrayList<TextView> listStatus;
    private Context mContext;



    public ChildKeepFocusAdapter(Activity activity, int resource,
                                 int textViewResourceId, ArrayList<ChildKeepFocusItem> objects) {
        super(activity, resource, textViewResourceId, objects);
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.time_adapter, null);
        titleTime = (TextView) convertView.findViewById(R.id.details_time_title);
        statusTime = (LinearLayout) convertView.findViewById(R.id.statusBarTime);
        dayScheduler = (TextView) convertView.findViewById(R.id.day_scheduler);
        isActive = (Switch) convertView.findViewById(R.id.is_active);
        isActive.setVisibility(View.GONE);
        dayScheduler.setVisibility(View.GONE);
        titleTime.setVisibility(View.VISIBLE);
        btnDeleteSchedule = (Button) convertView.findViewById(R.id.btn_delete_schedule);
        btnDeleteSchedule.setVisibility(View.GONE);


        final int mPosition = position;
        final ChildKeepFocusItem item = getItem(mPosition);
        titleTime.setText(item.getNameFocus());
        String day = item.getDayFocus();
        /*if(!day.equals("")) {
            dayScheduler.setText(item.getDayFocus());
            dayScheduler.setVisibility(View.VISIBLE);
            titleTime.setVisibility(View.VISIBLE);
        } else {
            dayScheduler.setVisibility(View.GONE);
        }*/
        isActive.setChecked(item.isActive());
        isActive.setEnabled(false);
        isActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kFDHelper.updateFocusItem(item);//issue scroll view lost checkbox
            }
        });

        addItemStatusTime();
        Log.d("contt","time "+item.getListTimeFocus());
        updateStatusTime(item.getListTimeFocus());
        //DeviceMemberManagerment deviceMemberManagerment = (DeviceMemberManagerment)activity;
        //deviceMemberManagerment.updateStatusTime(item.getListTimer());

        convertView.setOnClickListener(new OnItemClickListener( position ));

        return convertView;
    }


    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;
        OnItemClickListener(int position){
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
        }
    }

    public void addItemStatusTime() {
        listStatus = new ArrayList<TextView>();
        for (int i = 0; i < 144; i++) {
            TextView textItem = new TextView(activity);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            textItem.setLayoutParams(param);
            textItem.setBackgroundColor(Color.parseColor("#ee5c42"));
            //
            listStatus.add(textItem);
            statusTime.addView(textItem);
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
                        Color.parseColor("#ee5c42"));
            }
            return;
        }
        for (int i = 0; i < listTime.size(); i++) {
            ChildTimeItem timeItem = listTime.get(i);
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
}

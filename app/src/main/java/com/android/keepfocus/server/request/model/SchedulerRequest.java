package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Scheduler;
import com.android.keepfocus.server.model.TimeItems;

import java.util.ArrayList;

/**
 * Created by sev_user on 11/12/2016.
 */
public class SchedulerRequest {
    private Scheduler Scheduler;
    private Device Device;
    private ArrayList<TimeItems> TimeItems;
    private int Type;
    private int Action;


    public SchedulerRequest(com.android.keepfocus.server.model.Scheduler scheduler, com.android.keepfocus.server.model.Device device, ArrayList<com.android.keepfocus.server.model.TimeItems> timeItems, int type, int action) {
        Scheduler = scheduler;
        Device = device;
        TimeItems = timeItems;
        Type = type;
        Action = action;
    }
    public SchedulerRequest(com.android.keepfocus.server.model.Scheduler scheduler, com.android.keepfocus.server.model.Device device, int type, int action) {
        Scheduler = scheduler;
        Device = device;
        Type = type;
        Action = action;
    }

    public SchedulerRequest(int type, Device device){
        Type = type;
        Device = device;
    }
}

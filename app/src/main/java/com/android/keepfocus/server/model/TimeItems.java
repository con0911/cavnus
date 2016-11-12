package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/22/2016.
 */
public class TimeItems {

    private int id;
    private int start_hours;
    private int start_minutes;
    private int end_hours;
    private int end_minutes;
    private int scheduler_id;

    public TimeItems(int id, int start_hours, int start_minutes, int end_hours, int end_minutes, int scheduler_id) {
        this.id = id;
        this.start_hours = start_hours;
        this.start_minutes = start_minutes;
        this.end_hours = end_hours;
        this.end_minutes = end_minutes;
        this.scheduler_id = scheduler_id;
    }



}

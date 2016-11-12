package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/22/2016.
 */
public class Scheduler {

    private int id;
    private String scheduler_name;
    private String days;
    private int isActive;
    private int groupuser_id;

    public Scheduler(int id, String scheduler_name, String days, int isActive, int groupuser_id) {
        this.id = id;
        this.scheduler_name = scheduler_name;
        this.days = days;
        this.isActive = isActive;
        this.groupuser_id = groupuser_id;
    }

    public Scheduler() {

    }
}

package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Application;

import java.util.ArrayList;

/**
 * Created by sev_user on 9/23/2016.
 */
public class ApplicationRequest {
    private int Action;
    ArrayList <Application> Applications;


    public ApplicationRequest(int action, ArrayList<Application> applications) {
        Action = action;
        this.Applications = applications;
    }
}

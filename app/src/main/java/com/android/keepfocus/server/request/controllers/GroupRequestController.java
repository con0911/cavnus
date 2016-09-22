package com.android.keepfocus.server.request.controllers;

import android.util.Log;

import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.request.model.GroupRequest;
import com.android.keepfocus.utils.Constants;
import com.google.gson.Gson;

/**
 * Created by sev_user on 9/22/2016.
 */
public class GroupRequestController {

    private GroupRequest groupRequest;

    public void createGroup(){
        Header headerItem = new Header("test@gmail.com","devicecode1","registationId1","password1");//add data via contructor
        Group groupItem = new Group("groupname1","","","",0);
        groupRequest = new GroupRequest(headerItem, Constants.RequestTypeUpdate,Constants.ActionTypeCreate,groupItem);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(groupRequest);
        Log.d("trungdh", "jsonRequest: " + jsonRequest);
    }
}

package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.GroupUser;

/**
 * Created by sev_user on 11/9/2016.
 */
public class JoinGroupRequest {

    private int Type;
    private Group Group;
    private Device Device;
    private GroupUser GroupUser;


    public JoinGroupRequest(int type, Group group, Device device, GroupUser groupUser) {
        Type = type;
        Group = group;
        Device = device;
        GroupUser = groupUser;
    }
    public JoinGroupRequest(int type, Group group, Device device) {
        Type = type;
        Group = group;
        Device = device;
    }
}

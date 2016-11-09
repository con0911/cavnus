package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Device;
import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.GroupUser;
import com.android.keepfocus.server.model.Header;

/**
 * Created by sev_user on 11/9/2016.
 */
public class JoinGroupRequest {

    private Header Header;
    private int Type;
    private Group Group;
    private Device Device;
    private GroupUser GroupUser;


    public JoinGroupRequest(Header header, int type, Group group, Device device, GroupUser groupUser) {
        Header = header;
        Type = type;
        Group = group;
        Device = device;
        GroupUser = groupUser;
    }
}

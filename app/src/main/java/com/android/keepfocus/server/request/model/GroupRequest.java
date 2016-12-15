package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.Header;

/**
 * Created by sev_user on 9/22/2016.
 */
public class GroupRequest {
    private Header Header;
    private int Type;
    private int Action;
    private Group Group;

    public GroupRequest(Header header, int type, int action) {
        Header = header;
        Type = type;
        Action = action;
    }

    public GroupRequest(Group group, int type, int action) {
        this.Group = group;
        Type = type;
        Action = action;
    }

    public GroupRequest(Header header, int type, int action, Group group) {
        this.Header = header;
        Type = type;
        Action = action;
        this.Group = group;
    }

    public GroupRequest(Header header, int type) {
        this.Header = header;
        Type = type;
    }

}


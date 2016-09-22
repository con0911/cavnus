package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Group;
import com.android.keepfocus.server.model.Header;

/**
 * Created by sev_user on 9/22/2016.
 */
public class GroupRequest {
    private Header headerItem;
    private int Type;
    private int Action;
    private Group group;

    public GroupRequest(Header headerItem, int type, int action, Group group) {
        this.headerItem = headerItem;
        Type = type;
        Action = action;
        this.group = group;
    }

    public GroupRequest() {

    }
}

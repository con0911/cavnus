package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Header;

/**
 * Created by sev_user on 9/23/2016.
 */
public class GroupGet {
    private com.android.keepfocus.server.model.Header Header;
    private int Type;

    public GroupGet(Header header, int type) {
        this.Header = header;
        Type = type;
    }
}
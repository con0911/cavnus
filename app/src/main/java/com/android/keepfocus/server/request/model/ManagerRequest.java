package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Header;
import com.android.keepfocus.server.model.Manager;

/**
 * Created by sev_user on 12/26/2016.
 */
public class ManagerRequest {
    private Header Header;
    private Manager Manager;
    private int Type;

    public ManagerRequest(com.android.keepfocus.server.model.Header header, com.android.keepfocus.server.model.Manager manager, int type) {
        Header = header;
        Manager = manager;
        Type = type;
    }

    public com.android.keepfocus.server.model.Header getHeader() {
        return Header;
    }

    public ManagerRequest(int type) {
        Type = type;
    }

    public void setHeader(com.android.keepfocus.server.model.Header header) {
        Header = header;
    }

    public com.android.keepfocus.server.model.Manager getManager() {
        return Manager;
    }

    public void setManager(com.android.keepfocus.server.model.Manager manager) {
        Manager = manager;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}

package com.android.keepfocus.server.request.model;

import com.android.keepfocus.server.model.Device;

/**
 * Created by sev_user on 9/23/2016.
 */
public class DeviceRequest {
    private int Action;
    private Device Device;


    public DeviceRequest(int action, Device device) {
        Action = action;
        this.Device = device;
    }
}

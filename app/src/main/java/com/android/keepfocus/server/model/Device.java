package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/22/2016.
 */
public class Device {
    private int id;
    private String DeviceCode;
    private String DeviceName;
    private String DeviceModel;
    private String RegistationId;
    private String DeviceType;

    public Device(int id, String DeviceCode, String DeviceName, String DeviceModel, String RegistationId, String DeviceType) {
        this.id = id;
        this.DeviceCode = DeviceCode;
        this.DeviceName = DeviceName;
        this.DeviceModel = DeviceModel;
        this.RegistationId = RegistationId;
        this.DeviceType = DeviceType;

    }


}

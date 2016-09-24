package com.android.keepfocus.server.model;

/**
 * Created by sev_user on 9/22/2016.
 */

/**
 * Created by sev_user on 9/22/2016.
 */
public class Device {
    private int id;
    private String Device_Code;
    private String Device_Name;
    private String Device_Model;
    private String Registation_Id;
    private String Device_Type;

    public Device( String Device_Code, String Device_Name, String Device_Model, String Registation_Id, String Device_Type) {
        this.Device_Code = Device_Code;
        this.Device_Name = Device_Name;
        this.Device_Model = Device_Model;
        this.Registation_Id = Registation_Id;
        this.Device_Type = Device_Type;

    }


}


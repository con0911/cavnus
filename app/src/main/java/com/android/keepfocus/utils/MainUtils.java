package com.android.keepfocus.utils;

import android.provider.Settings;

import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class MainUtils {
    public static final String TERMS_AND_CONDITIONS = "terms_and_conditions";
    public static final String MODE_DEVICE = "device_mode";
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_ADMIN = 1;
    public static final int MODE_MEMBER = 2;
    public static final int NOTIFICATION_BLOCK = 1;
    public static final int LAUNCHER_APP_BLOCK = 2;
    public static final String REGISTATION_ID = "mode_device";
    public static String namePackageBlock;
    public static ParentGroupItem parentGroupItem;
    public static ParentProfileItem parentProfile;
    public static ParentMemberItem memberItem;
    public static ChildKeepFocusItem childKeepFocusItem;

    public static final String[] DAY_OF_WEEK = { "Sun", "Mon", "Tue", "Wed",
            "Thu", "Fri", "Sat" };
    public static final int timeSleep = 500;
    public static final String PACKET_APP = "com.android.keepfocus";
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_PACKAGE = "extra_package";
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_NOTI_CONTENT = "android.text";

    public static String UPDATE_CHILD_SCHEDULER = "com.android.keepfocus.UPDATE_CHILD_SCHEDULER";
    public static String UPDATE_FAMILY_GROUP = "com.android.keepfocus.UPDATE_FAMILY_GROUP";
}

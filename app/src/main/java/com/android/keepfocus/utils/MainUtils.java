package com.android.keepfocus.utils;

import com.android.keepfocus.data.ChildKeepFocusItem;
import com.android.keepfocus.data.ParentGroupItem;
import com.android.keepfocus.data.ParentMemberItem;
import com.android.keepfocus.data.ParentProfileItem;

/**
 * Created by nguyenthong on 9/6/2016.
 */
public class MainUtils {
    public static final String TERMS_AND_CONDITIONS = "terms_and_conditions";
    public static final int NOTIFICATION_BLOCK = 1;
    public static final int LAUNCHER_APP_BLOCK = 2;
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
}

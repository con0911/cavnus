package com.android.keepfocus.data;

/**
 * Created by nguyenthong on 9/6/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.keepfocus.utils.MainUtils;

import java.util.ArrayList;

public class MainDatabaseHelper extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "datadb";
    // Database Table for children
    private static final String TABLE_KEEPFOCUS = "tblKeepFocus";
    private static final String TABLE_APPITEM = "tblAppItem";
    private static final String TABLE_TIMEITEM = "tblTimeItem";
    private static final String TABLE_KEEPFOCUS_APPITEM = "tblKeepfocusApp";
    private static final String TABLE_NOTIFICATION_HISTORY = "tblNotificationHistory";
    // Database Table for children
    ////
    // Database Table for parent
    private static final String TABLE_GROUP_PARENT = "tblGroupParent";
    private static final String TABLE_MEMBER = "tblMemberParent";
    private static final String TABLE_GROUP_MEMBER_PARENT = "tblGroupMemberParent";
    private static final String TABLE_PROFILE_PARENT = "tblProfileParent";
    private static final String TABLE_MEMBER_PROFILE_PARENT = "tblMemberProfileParent";
    private static final String TABLE_APP_PARENT = "tblAppParent";
    private static final String TABLE_PROFILE_APP_PARENT = "tblProfileAppParent";
    private static final String TABLE_TIME_PARENT = "tblTimeParent";
    // Database Table for parent
    private SQLiteDatabase dbMain;

    public MainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Database create for children
        // tblKeepFocus
        String CREATE_TABLE_KEEPFOCUS = "CREATE TABLE " + TABLE_KEEPFOCUS + "("
                + "keep_focus_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " day_focus text not null," + " name_focus text not null,"
                + " is_active integer," + " id_profile_server integer" + ")";
        db.execSQL(CREATE_TABLE_KEEPFOCUS);
        // tblAppItem
        String CREATE_TABLE_APPITEM = "CREATE TABLE " + TABLE_APPITEM + "("
                + "app_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name_package text not null," + " name_app text not null"
                + ")";
        db.execSQL(CREATE_TABLE_APPITEM);
        // tblTimeItem
        String CREATE_TABLE_TIMEITEM = "CREATE TABLE " + TABLE_TIMEITEM + "("
                + "time_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " keep_focus_id integer not null," + " hour_begin integer,"
                + " minus_begin integer," + " hour_end integer,"
                + " minus_end integer" + ")";
        db.execSQL(CREATE_TABLE_TIMEITEM);
        // tblKeepfocusApp
        String CREATE_TABLE_KEEPFOCUS_APPITEM = "CREATE TABLE "
                + TABLE_KEEPFOCUS_APPITEM + "("
                + "id_keep_app INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " keep_focus_id integer not null,"
                + " app_id integer not null" + ")";
        db.execSQL(CREATE_TABLE_KEEPFOCUS_APPITEM);
        // tblNotificaionHistory
        String CREATE_TABLE_NOTIFICAION_HISTORY = "CREATE TABLE "
                + TABLE_NOTIFICATION_HISTORY + "("
                + "id_noti_history INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_app INTEGER NOT NULL, " + "noti_title text, "
                + "noti_sumary text, " + "packageName text not null, " + "noti_date INTEGER" + ")";
        db.execSQL(CREATE_TABLE_NOTIFICAION_HISTORY);
        // Database create for children
        ////////
        // Database create for parent
        // tblGroupParent
        String CREATE_TABLE_GROUP_PARENT = "CREATE TABLE " + TABLE_GROUP_PARENT + "("
                + "id_group INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " group_name text not null," + " group_code text not null,"
                + " create_date text not null," + " icon_array_byte BLOB," + " id_group_server INTEGER,"
                + " is_restore INTEGER"+ ")";
        db.execSQL(CREATE_TABLE_GROUP_PARENT);
        // tblMemberParent
        String CREATE_TABLE_MEMBER = "CREATE TABLE " + TABLE_MEMBER + "("
                + "id_member INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name_member text not null," + " type_member INTEGER,"
                + " icon_array_byte BLOB," + " id_member_server INTEGER,"
                + " is_blockall INTEGER," + " is_alowall INTEGER," + " is_blocksettings INTEGER" + ")";
        db.execSQL(CREATE_TABLE_MEMBER);
        // tblGroupMemberParent
        String CREATE_TABLE_GROUP_MEMBER_PARENT = "CREATE TABLE " + TABLE_GROUP_MEMBER_PARENT + "("
                + "id_group_member INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_group INTEGER,"
                + " id_member INTEGER" + ")";
        db.execSQL(CREATE_TABLE_GROUP_MEMBER_PARENT);
        // tblProfileParent
        String CREATE_TABLE_PROFILE_PARENT = "CREATE TABLE " + TABLE_PROFILE_PARENT + "("
                + "id_profile INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " day_profile text not null," + " name_profile text not null,"
                + " is_active integer," + " id_profile_server integer" + ")";
        db.execSQL(CREATE_TABLE_PROFILE_PARENT);
        // tblMemberProfileParent
        String CREATE_TABLE_MEMBER_PROFILE_PARENT = "CREATE TABLE " + TABLE_MEMBER_PROFILE_PARENT + "("
                + "id_member_profile INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_member integer,"
                + " id_profile integer" + ")";
        db.execSQL(CREATE_TABLE_MEMBER_PROFILE_PARENT);
        // tblAppParent
        String CREATE_TABLE_APP_PARENT = "CREATE TABLE " + TABLE_APP_PARENT + "("
                + "id_app_parent INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name_package text not null," + " name_app text not null,"
                + " image_app text not null" + ")";
        db.execSQL(CREATE_TABLE_APP_PARENT);
        // tblProfileAppParent
        String CREATE_TABLE_PROFILE_APP_PARENT = "CREATE TABLE " + TABLE_PROFILE_APP_PARENT + "("
                + "id_profile_app INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_profile integer,"
                + " id_app_parent integer" + ")";
        db.execSQL(CREATE_TABLE_PROFILE_APP_PARENT);
        // tblTimeParent
        String CREATE_TABLE_TIME_PARENT = "CREATE TABLE " + TABLE_TIME_PARENT + "("
                + "id_time_parent INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_profile integer not null," + " hour_begin integer,"
                + " minus_begin integer," + " hour_end integer,"
                + " minus_end integer," + " id_time_server integer" + ")";
        db.execSQL(CREATE_TABLE_TIME_PARENT);
        // Database create for parent
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        // Database drop for children
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEEPFOCUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMEITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEEPFOCUS_APPITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_HISTORY);
        // Database drop for children
        //////////
        // Database drop for parent
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MEMBER_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER_PROFILE_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_APP_PARENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME_PARENT);
        // Database drop for parent
        // Create tables again
        onCreate(db);
    }


    // Function for Children

    public ArrayList<ChildKeepFocusItem> getAllKeepFocusFromDb() {
        ArrayList<ChildKeepFocusItem> listKeepFocus = new ArrayList<ChildKeepFocusItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM tblKeepFocus";
        dbMain = this.getWritableDatabase();
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                // get data by cursor
                int keep_focus_id = Integer.parseInt(cursor.getString(0));
                String day_focus = cursor.getString(1);
                String name_focus = cursor.getString(2);
                int is_active = cursor.getInt(3);
                int id_profile_focus_server = cursor.getInt(4);
                // make keep focus item
                ChildKeepFocusItem focusItem = new ChildKeepFocusItem();
                focusItem.setKeepFocusId(keep_focus_id);
                focusItem.setDayFocus(day_focus);
                focusItem.setNameFocus(name_focus);
                if (is_active == 0) {
                    focusItem.setActive(false);
                } else {
                    focusItem.setActive(true);
                }
                focusItem.setId_profile_server(id_profile_focus_server);
                // Get ChildTimeItem for focusItem
                focusItem.setListTimeFocus(getListTimeById(focusItem
                        .getKeepFocusId()));
                // Get ChildAppItem for focusItem
                focusItem.setListAppFocus(getListAppById(focusItem
                        .getKeepFocusId()));
                listKeepFocus.add(focusItem);
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return listKeepFocus;
    }

    public ArrayList<ChildTimeItem> getListTimeById(int idFocus) {
        ArrayList<ChildTimeItem> listTime = new ArrayList<ChildTimeItem>();
        if (dbMain == null) {
            dbMain = this.getWritableDatabase();
        }
        String selectQuery = "SELECT * FROM tblTimeItem WHERE keep_focus_id = "
                + idFocus;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int time_id = Integer.parseInt(cursor.getString(0));
                int hour_begin = cursor.getInt(2);
                int minus_begin = cursor.getInt(3);
                int hour_end = cursor.getInt(4);
                int minus_end = cursor.getInt(5);
                //
                ChildTimeItem childTimeItem = new ChildTimeItem();
                childTimeItem.setTimeId(time_id);
                childTimeItem.setKeepFocusId(idFocus);
                childTimeItem.setHourBegin(hour_begin);
                childTimeItem.setMinusBegin(minus_begin);
                childTimeItem.setHourEnd(hour_end);
                childTimeItem.setMinusEnd(minus_end);
                listTime.add(childTimeItem);
            } while (cursor.moveToNext());
        }
        return listTime;
    }

    public ArrayList<ChildAppItem> getListAppById(int idFocus) {
        ArrayList<ChildAppItem> listApp = new ArrayList<ChildAppItem>();
        if (dbMain == null) {
            dbMain = this.getWritableDatabase();
        }
        String selectQuery = "SELECT * FROM tblKeepfocusApp WHERE keep_focus_id = "
                + idFocus;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int app_id = cursor.getInt(2);
                ChildAppItem item = getAppItemById(app_id);
                listApp.add(item);
            } while (cursor.moveToNext());
        }
        return listApp;
    }

    public ChildAppItem getAppItemById(int id) {
        ChildAppItem childAppItem = new ChildAppItem();
        if (dbMain == null) {
            dbMain = this.getWritableDatabase();
        }
        String selectQuery = "SELECT * FROM tblAppItem WHERE app_id = " + id;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String name_package = cursor.getString(1);
            String name_app = cursor.getString(2);
            childAppItem.setAppId(id);
            childAppItem.setNamePackage(name_package);
            childAppItem.setNameApp(name_app);
            return childAppItem;
        }
        return null;
    }

    public int addNewFocusItem(ChildKeepFocusItem keepFocus) {
        dbMain = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("day_focus", keepFocus.getDayFocus());
        values.put("name_focus", keepFocus.getNameFocus());
        if (keepFocus.isActive()) {
            values.put("is_active", 1);
        } else {
            values.put("is_active", 0);
        }
        values.put("id_profile_server", keepFocus.getId_profile_server());
        int keep_focus_id = (int) dbMain.insert("tblKeepFocus", null, values);
        keepFocus.setKeepFocusId(keep_focus_id);
        dbMain.close();
        for (int i = 0; i < keepFocus.getListTimeFocus().size(); i++) {
            addTimeItemToDb(keepFocus.getListTimeFocus().get(i), keep_focus_id);
        }
        return keep_focus_id;
    }

    public int addTimeItemToDb(ChildTimeItem childTimeItem, int keep_focus_id) {
        dbMain = this.getWritableDatabase();
        childTimeItem.setKeepFocusId(keep_focus_id);
        ContentValues values2 = new ContentValues();
        values2.put("keep_focus_id", childTimeItem.getKeepFocusId());
        values2.put("hour_begin", childTimeItem.getHourBegin());
        values2.put("minus_begin", childTimeItem.getMinusBegin());
        values2.put("hour_end", childTimeItem.getHourEnd());
        values2.put("minus_end", childTimeItem.getMinusEnd());
        int time_id = (int) dbMain.insert("tblTimeItem", null, values2);
        childTimeItem.setTimeId(time_id);
        dbMain.close();
        return time_id;
    }

    public int addAppItemToDb(ChildAppItem childAppItem, int keep_focus_id) {
        int app_id = -1;
        dbMain = this.getWritableDatabase();
        String selectQuery = "SELECT tblAppItem.app_id FROM tblAppItem WHERE name_package = '"
                + childAppItem.getNamePackage() + "'";
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            app_id = cursor.getInt(0);
        }
        if (app_id == -1) {
            ContentValues values3 = new ContentValues();
            values3.put("name_package", childAppItem.getNamePackage());
            values3.put("name_app", childAppItem.getNameApp());
            app_id = (int) dbMain.insert("tblAppItem", null, values3);
            childAppItem.setAppId(app_id);
        }
        // InsertTableTemp
        ContentValues values3 = new ContentValues();
        values3.put("keep_focus_id", keep_focus_id);
        values3.put("app_id", app_id);
        dbMain.insert("tblKeepfocusApp", null, values3);
        return app_id;
    }

    public void deleteFocusItemById(int keep_focus_id) {
        dbMain = this.getWritableDatabase();
        // Delete from tblKeepFocus
        String deleteKeepFocus = "DELETE FROM tblKeepFocus WHERE keep_focus_id = "
                + keep_focus_id;
        dbMain.execSQL(deleteKeepFocus);
        // Delete from tblTimeItem
        String deleteTimeItem = "DELETE FROM tblTimeItem WHERE keep_focus_id = "
                + keep_focus_id;
        dbMain.execSQL(deleteTimeItem);
        // Delete from tblKeepfocusApp
        String deleteKeepFocusApp = "DELETE FROM tblKeepfocusApp WHERE keep_focus_id = "
                + keep_focus_id;
        dbMain.execSQL(deleteKeepFocusApp);
        dbMain.close();
    }

    public void deleteTimeItemById(int time_id) {
        dbMain = this.getWritableDatabase();
        // Delete from tblTimeItem
        String deleteTimeItem = "DELETE FROM tblTimeItem WHERE time_id = "
                + time_id;
        dbMain.execSQL(deleteTimeItem);
        dbMain.close();
    }

    public void deleteAppFromKeepFocus(int app_id, int keep_focus_id) {
        dbMain = this.getWritableDatabase();
        // Delete from tblKeepfocusApp
        String deleteKeepFocusApp = "DELETE FROM tblKeepfocusApp WHERE app_id = "
                + app_id + " and keep_focus_id = " + keep_focus_id;
        dbMain.execSQL(deleteKeepFocusApp);
        dbMain.close();
    }

    public void deleteAppAndNotiByUninstall(String name_package) {
        int app_id = -1;
        dbMain = this.getWritableDatabase();
        // String selectQuery =
        // "SELECT tblAppItem.app_id FROM tblAppItem WHERE name_package = '"
        // + name_package + "'";
        // Cursor cursor = dbMain.rawQuery(selectQuery, null);
        // if (cursor.moveToFirst()) {
        // app_id = cursor.getInt(0);
        // }
        app_id = getAppItemIdByPackageName(name_package);
        if (app_id == -1) {
            return;// Return if don't find app in Db
        }
        // Delete from tblAppItem
        String deletetblAppItem = "DELETE FROM tblAppItem WHERE app_id = "
                + app_id;
        dbMain.execSQL(deletetblAppItem);
        // Delete from tblKeepfocusApp
        String deleteKeepFocusApp = "DELETE FROM tblKeepfocusApp WHERE app_id = "
                + app_id;
        dbMain.execSQL(deleteKeepFocusApp);
        // Delete form tblNotificationHistoryItem
        String query = "DELETE FROM " + TABLE_NOTIFICATION_HISTORY
                + " WHERE id_app = " + app_id;
        dbMain.execSQL(query);
        dbMain.close();
        //

    }

    public void updateFocusItem(ChildKeepFocusItem childKeepFocusItem) {
        int keep_focus_id = childKeepFocusItem.getKeepFocusId();
        String day_focus = "'" + childKeepFocusItem.getDayFocus() + "'";
        String name_focus = "'" + childKeepFocusItem.getNameFocus() + "'";
        int is_active = childKeepFocusItem.isActive() ? 1 : 0;
        int id_profile_server = childKeepFocusItem.getId_profile_server();
        //
        dbMain = this.getWritableDatabase();
        String update = "update tblKeepFocus set day_focus = " + day_focus
                + ", name_focus = " + name_focus + ", is_active = " + is_active
                + ", id_profile_server = " + id_profile_server
                + " where keep_focus_id = " + keep_focus_id;
        dbMain.execSQL(update);
        dbMain.close();
    }

    public void updateTimeItem(ChildTimeItem childTimeItem) {
        int time_id = childTimeItem.getTimeId();
        int hour_begin = childTimeItem.getHourBegin();
        int minus_begin = childTimeItem.getMinusBegin();
        int hour_end = childTimeItem.getHourEnd();
        int minus_end = childTimeItem.getMinusEnd();
        //
        dbMain = this.getWritableDatabase();
        String update = "update tblTimeItem set hour_begin = " + hour_begin
                + ", minus_begin = " + minus_begin + ", hour_end = " + hour_end
                + ", minus_end = " + minus_end + " where time_id = " + time_id;
        dbMain.execSQL(update);
        dbMain.close();
    }

    /*
     * Method check Notifications is block input: String package name, system
     * time of moment notifications fire output: Boolean value true if block
     * else false
     */
    public boolean isAppOrNotifiBlock(/*String packageName,*/ String day, int hour,
                                      int min, int flagBlock) {
        dbMain = this.getWritableDatabase();
//        int appId = getAppItemIdByPackageName(packageName);
//        if (appId == -1) {
//            return false;
//        }
        ArrayList<ChildKeepFocusItem> list_Child_KeepFocusItem = getAllKeepFocusFromDb();
        ArrayList<ChildTimeItem> list_Child_TimeItem;
        Log.e("thong.nv","list_Child_KeepFocusItem size" + list_Child_KeepFocusItem.size());
        for (ChildKeepFocusItem a_Child_KeepFocusItem : list_Child_KeepFocusItem) {
            Log.e("thong.nv","a_Child_KeepFocusItem" + a_Child_KeepFocusItem);
            if (a_Child_KeepFocusItem.isActive()) {
                if (flagBlock == MainUtils.NOTIFICATION_BLOCK) {
                    if (!a_Child_KeepFocusItem.getDayFocus().contains(day)) {
                        continue;
                    }
                    list_Child_TimeItem = a_Child_KeepFocusItem.getListTimeFocus();
                    if (list_Child_TimeItem.size() == 0) {
                        return true;
                    }
                    for (ChildTimeItem a_Child_TimeItem : list_Child_TimeItem) {
                        if (a_Child_TimeItem.checkInTime(hour, min))
                            return true;
                    }
                } else if (flagBlock == MainUtils.LAUNCHER_APP_BLOCK) {
                    Log.e("thong.nv","flagBlock == MainUtils.LAUNCHER_APP_BLOCK");
                    if (!a_Child_KeepFocusItem.getDayFocus().contains(day)) {
                        continue;
                    }
                    Log.e("thong.nv","flagBlock == MainUtils.LAUNCHER_APP_BLOCK 2");
                    list_Child_TimeItem = a_Child_KeepFocusItem.getListTimeFocus();
                    if (list_Child_TimeItem.size() == 0) {
                        return true;
                    }
                    for (ChildTimeItem a_Child_TimeItem : list_Child_TimeItem) {
                        if (a_Child_TimeItem.checkInTime(hour, min)) {
                            return true;
                        }
                    }
                    Log.e("thong.nv","false");
                }
            }
        }
        return false;
    }

    /*
     * Method get ChildAppItem Id which maybe block input : String[] packageName
     * output :ChildAppItem id if exist else -1
     */
    public int getAppItemIdByPackageName(String packageName) {
        int appId = -1;
        String query = "SELECT ai.app_id FROM tblAppItem AS ai WHERE ai.name_package = '"
                + packageName + "'";
        Cursor cursor = dbMain.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            appId = cursor.getInt(0);
        }
        return appId;
    }

    /*
     * Method query KeepFocus item via ChildAppItem id input : int ChildAppItem id output
     * : value of KeepFocus item if exist else -1
     */
    private ArrayList<ChildKeepFocusItem> getListFocusItemByAppItemId(int appId) {
        ArrayList<ChildKeepFocusItem> childKeepFocusItemList = new ArrayList<ChildKeepFocusItem>();
        String query = "SELECT kf.* FROM tblKeepFocus AS kf JOIN tblKeepfocusApp AS kfa ON kf.keep_focus_id = kfa.keep_focus_id WHERE kfa.app_id ="
                + appId;
        Cursor cursor = dbMain.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int keepFocusId = cursor.getInt(0);
                String dayFocus = cursor.getString(1);
                String nameFocus = cursor.getString(2);
                boolean isActive = cursor.getInt(3) == 1 ? true : false;
                int idProfileServer = cursor.getInt(4);
                ArrayList<ChildTimeItem> listTimeFocus = getListTimeById(keepFocusId);
                ChildKeepFocusItem a_Child_KeepFocusItem = new ChildKeepFocusItem();
                a_Child_KeepFocusItem.setKeepFocusId(keepFocusId);
                a_Child_KeepFocusItem.setDayFocus(dayFocus);
                a_Child_KeepFocusItem.setNameFocus(nameFocus);
                a_Child_KeepFocusItem.setActive(isActive);
                a_Child_KeepFocusItem.setListTimeFocus(listTimeFocus);
                a_Child_KeepFocusItem.setId_profile_server(idProfileServer);
                childKeepFocusItemList.add(a_Child_KeepFocusItem);
            } while (cursor.moveToNext());
        }
        return childKeepFocusItemList;
    }

    // =========================================================Need to save
    // Notification history for user see back when out of time
    // block=======================
    public void addNotificationHistoryItemtoDb(ChildNotificationItemMissHistory notiHistory) {
        dbMain = this.getWritableDatabase();
        String packageName = notiHistory.getPakageName();
        int app_id = getAppItemIdByPackageName(packageName);
        String title = notiHistory.getmNotiTitle();
        String text = notiHistory.getmNotiSumary();
        int date = notiHistory.getmNotiDate();
        ContentValues contentValue = new ContentValues();
        contentValue.put("id_app", app_id);
        contentValue.put("noti_title", title);
        contentValue.put("noti_sumary", text);
        contentValue.put("packageName", packageName);
        contentValue.put("noti_date", date);
        dbMain.insert(TABLE_NOTIFICATION_HISTORY, null, contentValue);
        dbMain.close();
    }

    public void deleteNotificationHistoryItemById(int aNotiHistoryItemId) {
        dbMain = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NOTIFICATION_HISTORY
                + " WHERE id_noti_history = " + aNotiHistoryItemId;
        dbMain.execSQL(query);
        dbMain.close();
    }

    // public void deleteNotificationHistoryItemByUninstallApp (String
    // packageName){
    // int app_id = getAppItemIdByPackageName(packageName);
    // if(app_id == -1){
    // return;
    // }
    // dbMain = this.getWritableDatabase();
    // String query = "DELETE FROM " + TABLE_NOTIFICATION_HISTORY +
    // " WHERE id_app = " + app_id;
    // dbMain.execSQL(query);
    // dbMain.close();
    // }

    public void deleteAllNotifications(ArrayList<ChildNotificationItemMissHistory> clearNotifications) {
        for (ChildNotificationItemMissHistory aNoti : clearNotifications) {
            deleteNotificationHistoryItemById(aNoti.getmNotiItem_id());
        }
    }

    public ArrayList<ChildNotificationItemMissHistory> getListNotificaionHistoryItem() {
        ArrayList<ChildNotificationItemMissHistory> list_NotificationHistory = new ArrayList<ChildNotificationItemMissHistory>();
        String query = "SELECT * FROM tblNotificationHistory";
        dbMain = this.getWritableDatabase();
        Cursor cursor = dbMain.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int noti_id = cursor.getInt(0);
                int app_id = cursor.getInt(1);
                String noti_Title = cursor.getString(2);
                String noti_Surmary = cursor.getString(3);
                String packageName = cursor.getString(4);
                int noti_Date = cursor.getInt(5);
                ChildNotificationItemMissHistory aNotiHistoryItem = new ChildNotificationItemMissHistory();
                aNotiHistoryItem.setmNotiItem_id(noti_id);
                aNotiHistoryItem.setmApp_id(app_id);
                aNotiHistoryItem.setmNotiTitle(noti_Title);
                aNotiHistoryItem.setmNotiSumary(noti_Surmary);
                aNotiHistoryItem.setPakageName(packageName);
                aNotiHistoryItem.setmNotiDate(noti_Date);
                list_NotificationHistory.add(aNotiHistoryItem);
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return list_NotificationHistory;
    }

    // =========================================================End of part save
    // Notification
    // history============================================================
    // Function for Children

    ///===================================================================================///
    // Function for parent

    //============================getData function=================================//
    public ArrayList<ParentGroupItem> getAllGroupItemParent() {
        ArrayList<ParentGroupItem> listGroupItem = new ArrayList<ParentGroupItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM tblGroupParent";
        dbMain = this.getWritableDatabase();
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                // get data by cursor
                int id_group = Integer.parseInt(cursor.getString(0));
                String group_name = cursor.getString(1);
                String group_code = cursor.getString(2);
                String create_date = cursor.getString(3);
                byte[] icon_uri = cursor.getBlob(4);
                int id_group_server = cursor.getInt(5);
                int is_restore = cursor.getInt(6);
                // make keep focus item
                ParentGroupItem groupItem = new ParentGroupItem();
                groupItem.setId_group(id_group);
                groupItem.setGroup_name(group_name);
                groupItem.setGroup_code(group_code);
                groupItem.setCreate_date(create_date);
                groupItem.setId_group_server(id_group_server);
                //
                groupItem.setListMember(getListMember(id_group));
                groupItem.setIs_restore(is_restore);
                groupItem.setIcon_arrarByte(icon_uri);
                //
                listGroupItem.add(groupItem);
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return listGroupItem;
    }


    public ParentGroupItem getGroupByCode(String code) {
        // Select All Query
        String selectQuery = "SELECT * FROM tblGroupParent";
        dbMain = this.getWritableDatabase();
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        ParentGroupItem groupItem = new ParentGroupItem();
        if (cursor.moveToFirst()) {
            do {
                String group_code = cursor.getString(2);
                if(group_code.equals(code)){
                    int id_group = Integer.parseInt(cursor.getString(0));
                    String group_name = cursor.getString(1);
                    String create_date = cursor.getString(3);
                    byte[] icon_uri = cursor.getBlob(4);
                    int id_group_server = cursor.getInt(5);
                    groupItem.setId_group(id_group);
                    groupItem.setGroup_name(group_name);
                    groupItem.setGroup_code(group_code);
                    groupItem.setCreate_date(create_date);
                    groupItem.setId_group_server(id_group_server);
                    //
                    groupItem.setListMember(getListMember(id_group));
                    groupItem.setIcon_arrarByte(icon_uri);
                }
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return groupItem;
    }

    public ArrayList<ParentMemberItem> getListMember(int idGroup) {
        ArrayList<ParentMemberItem> listMember = new ArrayList<ParentMemberItem>();
      //  if (dbMain == null) {
            dbMain = this.getWritableDatabase();
     //   }
        String selectQuery = "SELECT * FROM tblGroupMemberParent WHERE id_group = "
                + idGroup;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id_member = cursor.getInt(2);
                ParentMemberItem item = getMemberItemById(id_member);
                if (item != null) {
                    listMember.add(item);
                }
            } while (cursor.moveToNext());
        }
        dbMain.close();
     //   dbMain = null;
        return listMember;
    }

    public ParentMemberItem getMemberItemById(int id_member) {
        ParentMemberItem memberItem = new ParentMemberItem();
   //     if (dbMain == null) {
            dbMain = this.getWritableDatabase();
  //      }
        String selectQuery = "SELECT * FROM tblMemberParent WHERE id_member = " + id_member;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String name_member = cursor.getString(1);
            int type_member = cursor.getInt(2);
            byte[] icon_array_byte = cursor.getBlob(3);
            int id_member_server = cursor.getInt(4);
            int is_blockall = cursor.getInt(5);
            int is_alowall = cursor.getInt(6);
            int is_blocksettings = cursor.getInt(7);
            memberItem.setId_member(id_member);
            memberItem.setName_member(name_member);
            memberItem.setType_member(type_member);
            memberItem.setIcon_array_byte(icon_array_byte);
            memberItem.setId_member_server(id_member_server);
            memberItem.setIs_blockall(is_blockall);
            memberItem.setIs_alowall(is_alowall);
            memberItem.setIs_blocksettings(is_blocksettings);
            return memberItem;
        }
        dbMain.close();
   //     dbMain = null;
        return null;
    }

    public ParentMemberItem getMemberItemByIdServer(int id_member_server) {
        ParentMemberItem memberItem = new ParentMemberItem();
        //     if (dbMain == null) {
        dbMain = this.getWritableDatabase();
        //      }
        String selectQuery = "SELECT * FROM tblMemberParent WHERE id_member_server = " + id_member_server;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int id_member = cursor.getInt(0);
            String name_member = cursor.getString(1);
            int type_member = cursor.getInt(2);
            byte[] icon_array_byte = cursor.getBlob(3);
            int is_blockall = cursor.getInt(5);
            int is_alowall = cursor.getInt(6);
            int is_blocksettings = cursor.getInt(7);
            memberItem.setId_member(id_member);
            memberItem.setName_member(name_member);
            memberItem.setType_member(type_member);
            memberItem.setIcon_array_byte(icon_array_byte);
            memberItem.setId_member_server(id_member_server);
            memberItem.setIs_blockall(is_blockall);
            memberItem.setIs_alowall(is_alowall);
            memberItem.setIs_blocksettings(is_blocksettings);
            return memberItem;
        }
        dbMain.close();
        //     dbMain = null;
        return null;
    }

    public void makeListMemberInGroup(ParentGroupItem groupItem) {
        groupItem.setListMember(getListMember(groupItem.getId_group()));
        makeDetailOneGroupItemParent(groupItem);
    }

    public void makeDetailOneGroupItemParent(ParentGroupItem groupItem) {
        int size = groupItem.getListMember().size();
        dbMain = this.getWritableDatabase();
        for (int i = 0; i < size; i++) {
            int id_member = groupItem.getListMember().get(i).getId_member();
            groupItem.getListMember().get(i).setListProfile(getListProfileMember(id_member));
        }
        dbMain.close();
    }

    private ArrayList<ParentProfileItem> getListProfileMember(int idMember) {
        ArrayList<ParentProfileItem> listProfile = new ArrayList<ParentProfileItem>();
      //  if (dbMain == null) {
            dbMain = this.getWritableDatabase();
   //     }
        String selectQuery = "SELECT * FROM tblMemberProfileParent WHERE id_member = "
                + idMember;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id_profile = cursor.getInt(2);
                ParentProfileItem item = getProfileItemById(id_profile);
                if (item != null) {
                    listProfile.add(item);
                }
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return listProfile;
    }

    private ParentProfileItem getProfileItemById(int id_profile) {
        ParentProfileItem profileItem = new ParentProfileItem();
     //   if (dbMain == null) {
            dbMain = this.getWritableDatabase();
      //  }
        String selectQuery = "SELECT * FROM tblProfileParent WHERE id_profile = " + id_profile;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String day_profile = cursor.getString(1);
            String name_profile = cursor.getString(2);
            int is_active = cursor.getInt(3);
            int id_profile_server = cursor.getInt(4);
            profileItem.setId_profile(id_profile);
            profileItem.setDay_profile(day_profile);
            profileItem.setName_profile(name_profile);
            if (is_active == 1) {
                profileItem.setActive(true);
            } else {
                profileItem.setActive(false);
            }
            profileItem.setId_profile_server(id_profile_server);
            return profileItem;
        }
        dbMain.close();
        return null;
    }


    public void makeDetailOneMemberItemParent(ParentMemberItem memberItem) {
        int size = memberItem.getListProfile().size();
        dbMain = this.getWritableDatabase();
        for (int i = 0; i < size; i++) {
            int id_profile = memberItem.getListProfile().get(i).getId_profile();
            memberItem.getListProfile().get(i).setListAppBlock(getListAppParent(id_profile));
            //
            memberItem.getListProfile().get(i).setListTimer(getListTimeParentById(id_profile));
        }
        dbMain.close();
    }

    private ArrayList<ParentAppItem> getListAppParent(int id_profile) {
        ArrayList<ParentAppItem> listAppParent = new ArrayList<ParentAppItem>();
    //    if (dbMain == null) {
            dbMain = this.getWritableDatabase();
       // }
        String selectQuery = "SELECT * FROM tblProfileAppParent WHERE id_profile = "
                + id_profile;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id_app_parent = cursor.getInt(2);
                ParentAppItem item = getAppParentItemById(id_app_parent);
                if (item != null) {
                    listAppParent.add(item);
                }
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return listAppParent;
    }

    private ParentAppItem getAppParentItemById(int id_app_parent) {
        ParentAppItem appParentItem = new ParentAppItem();
       // if (dbMain == null) {
            dbMain = this.getWritableDatabase();
      //  }
        String selectQuery = "SELECT * FROM tblAppParent WHERE id_app_parent = " + id_app_parent;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String name_package = cursor.getString(1);
            String name_app = cursor.getString(2);
            String image_app = cursor.getString(3);
            appParentItem.setId_app_parent(id_app_parent);
            appParentItem.setNamePackage(name_package);
            appParentItem.setNameApp(name_app);
            appParentItem.setIconApp(image_app);
            return appParentItem;
        }
        dbMain.close();
        return null;
    }

    private ArrayList<ParentTimeItem> getListTimeParentById(int id_profile) {
        ArrayList<ParentTimeItem> listTime = new ArrayList<ParentTimeItem>();
    //    if (dbMain == null) {
            dbMain = this.getWritableDatabase();
    //    }
        String selectQuery = "SELECT * FROM tblTimeParent WHERE id_profile = "
                + id_profile;
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id_time_parent = Integer.parseInt(cursor.getString(0));
                int hour_begin = cursor.getInt(2);
                int minus_begin = cursor.getInt(3);
                int hour_end = cursor.getInt(4);
                int minus_end = cursor.getInt(5);
                int id_time_server = cursor.getInt(6);
                //
                ParentTimeItem parentTimeItem = new ParentTimeItem();
                parentTimeItem.setId_timer_parent(id_time_parent);
                parentTimeItem.setId_profile(id_profile);
                parentTimeItem.setHourBegin(hour_begin);
                parentTimeItem.setMinusBegin(minus_begin);
                parentTimeItem.setHourEnd(hour_end);
                parentTimeItem.setMinusEnd(minus_end);
                parentTimeItem.setId_time_server(id_time_server);
                listTime.add(parentTimeItem);
            } while (cursor.moveToNext());
        }
        dbMain.close();
        return listTime;
    }

    //============================getData function=================================//
    //============================addData function=================================//
    public int addGroupItemParent(ParentGroupItem groupItem) {
        dbMain = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_name", groupItem.getGroup_name());
        values.put("group_code", groupItem.getGroup_code());
        values.put("create_date", groupItem.getCreate_date());
        values.put("id_group_server", groupItem.getId_group_server());
        values.put("is_restore", groupItem.getIs_restore());
        values.put("icon_array_byte",groupItem.getIcon_arrarByte());
        int id_group = (int) dbMain.insert("tblGroupParent", null, values);
        groupItem.setId_group(id_group);
        dbMain.close();
        return id_group;
    }

    public int addMemberItemParent(ParentMemberItem parentMemberItem, int id_group) {
        int id_member = parentMemberItem.getId_member();
        dbMain = this.getWritableDatabase();
        if (id_member == -1) {
            ContentValues values3 = new ContentValues();
            values3.put("name_member", parentMemberItem.getName_member());
            values3.put("type_member", parentMemberItem.getType_member());
            values3.put("icon_array_byte", parentMemberItem.getIcon_array_byte());
            values3.put("id_member_server",parentMemberItem.getId_member_server());
            values3.put("is_blockall", parentMemberItem.getIs_blockall());
            values3.put("is_alowall",parentMemberItem.getIs_alowall());
            values3.put("is_blocksettings",parentMemberItem.getIs_blocksettings());
            id_member = (int) dbMain.insert("tblMemberParent", null, values3);
            parentMemberItem.setId_member(id_member);
        }
        // InsertTableTemp
        ContentValues values = new ContentValues();
        values.put("id_group", id_group);
        values.put("id_member", id_member);
        dbMain.insert("tblGroupMemberParent", null, values);
        dbMain.close();
        return id_member;
    }

    public int addProfileItemParent(ParentProfileItem parentProfileItem, int id_member) {
        int id_profile = parentProfileItem.getId_profile();
        dbMain = this.getWritableDatabase();
        if (id_profile == -1) {
            ContentValues values3 = new ContentValues();
            values3.put("day_profile", parentProfileItem.getDay_profile());
            values3.put("name_profile", parentProfileItem.getName_profile());
            if (parentProfileItem.isActive()) {
                values3.put("is_active", 1);
            } else {
                values3.put("is_active", 0);
            }
            values3.put("id_profile_server", parentProfileItem.getId_profile_server());
            id_profile = (int) dbMain.insert("tblProfileParent", null, values3);
            parentProfileItem.setId_profile(id_profile);
        }
        // InsertTableTemp
        ContentValues values = new ContentValues();
        values.put("id_member", id_member);
        values.put("id_profile", id_profile);
        dbMain.insert("tblMemberProfileParent", null, values);
        dbMain.close();
        // Insert timeItem
        for (int i = 0; i < parentProfileItem.getListTimer().size(); i++) {
            addTimeItemParent(parentProfileItem.getListTimer().get(i), id_profile);
        }
        return id_profile;
    }

    public int addAppItemParent(ParentAppItem parentAppItem, int id_profile) {
        int id_app_parent = -1;
        dbMain = this.getWritableDatabase();
        String selectQuery = "SELECT tblAppParent.id_app_parent FROM tblAppParent WHERE name_package = '"
                + parentAppItem.getNamePackage() + "'";
        Cursor cursor = dbMain.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            id_app_parent = cursor.getInt(0);
        }
        if (id_app_parent == -1) {
            ContentValues values3 = new ContentValues();
            values3.put("name_package", parentAppItem.getNamePackage());
            values3.put("name_app", parentAppItem.getNameApp());
            values3.put("image_app", parentAppItem.getIconApp());
            id_app_parent = (int) dbMain.insert("tblAppParent", null, values3);
            parentAppItem.setId_app_parent(id_app_parent);
        }
        // InsertTableTemp
        ContentValues values3 = new ContentValues();
        values3.put("id_profile", id_profile);
        values3.put("id_app_parent", id_app_parent);
        dbMain.insert("tblProfileAppParent", null, values3);
        dbMain.close();
        return id_app_parent;
    }

    public int addTimeItemParent(ParentTimeItem parentTimeItem, int id_profile) {
        dbMain = this.getWritableDatabase();
        parentTimeItem.setId_profile(id_profile);
        ContentValues values2 = new ContentValues();
        values2.put("id_profile", parentTimeItem.getId_profile());
        values2.put("hour_begin", parentTimeItem.getHourBegin());
        values2.put("minus_begin", parentTimeItem.getMinusBegin());
        values2.put("hour_end", parentTimeItem.getHourEnd());
        values2.put("minus_end", parentTimeItem.getMinusEnd());
        values2.put("id_time_server",parentTimeItem.getId_time_server());
        int id_time_parent = (int) dbMain.insert("tblTimeParent", null, values2);
        parentTimeItem.setId_timer_parent(id_time_parent);
        dbMain.close();
        return id_time_parent;
    }

    //============================addData function=================================//
    //============================deleteData function=================================//
    public void deleteGroupItemById(int id_group) {
        dbMain = this.getWritableDatabase();
        // Delete from tblGroupParent
        String deleteGroupTable = "DELETE FROM tblGroupParent WHERE id_group = "
                + id_group;
        dbMain.execSQL(deleteGroupTable);
        // Delete from tblGroupMemberParent
        String deleteGroupMember = "DELETE FROM tblGroupMemberParent WHERE id_group = "
                + id_group;
        dbMain.execSQL(deleteGroupMember);
        dbMain.close();
    }

    public void deleteMemberItemById(int id_member) {
        dbMain = this.getWritableDatabase();
        // Delete from tblMemberParent
        String deletblMemberParent = "DELETE FROM tblMemberParent WHERE id_member = "
                + id_member;
        dbMain.execSQL(deletblMemberParent);
        // Delete from tblGroupMemberParent
        String deleteGroupMember = "DELETE FROM tblGroupMemberParent WHERE id_member = "
                + id_member;
        dbMain.execSQL(deleteGroupMember);
        // Delete from tblMemberProfileParent
        String deletblMemberProfileParent = "DELETE FROM tblMemberProfileParent WHERE id_member = "
                + id_member;
        dbMain.execSQL(deletblMemberProfileParent);
        dbMain.close();
    }

    public void deleteProfileItemById(int id_profile) {
        dbMain = this.getWritableDatabase();
        // Delete from tblProfileParent
        String delettblProfileParent = "DELETE FROM tblProfileParent WHERE id_profile = "
                + id_profile;
        dbMain.execSQL(delettblProfileParent);
        // Delete from tblMemberProfileParent
        String deltblMemberProfileParent = "DELETE FROM tblMemberProfileParent WHERE id_profile = "
                + id_profile;
        dbMain.execSQL(deltblMemberProfileParent);
        // Delete from tblProfileAppParent
        String deltblProfileAppParent = "DELETE FROM tblProfileAppParent WHERE id_profile = "
                + id_profile;
        dbMain.execSQL(deltblProfileAppParent);
        // Delete from tblTimeParent
        String deltblTimeParent = "DELETE FROM tblTimeParent WHERE id_profile = "
                + id_profile;
        dbMain.execSQL(deltblTimeParent);
        dbMain.close();
    }

    public void deleteAppParentItemById(int id_app_parent) {
        dbMain = this.getWritableDatabase();
        // Delete from tblAppParent
        String deltblAppParent = "DELETE FROM tblAppParent WHERE id_app_parent = "
                + id_app_parent;
        dbMain.execSQL(deltblAppParent);
        // Delete from tblProfileAppParent
        String deltblProfileAppParent = "DELETE FROM tblProfileAppParent WHERE id_app_parent = "
                + id_app_parent;
        dbMain.execSQL(deltblProfileAppParent);
        dbMain.close();
    }

    public void deleteTimerParentItemById(int id_time_parent) {
        dbMain = this.getWritableDatabase();
        // Delete from tblTimeParent
        String deltblTimeParent = "DELETE FROM tblTimeParent WHERE id_time_parent = "
                + id_time_parent;
        dbMain.execSQL(deltblTimeParent);
        dbMain.close();
    }

    //============================deleteData function=================================//
    //============================updateData function=================================//
    public void updateGroupItem(ParentGroupItem parentGroupItem) {
        int id_group = parentGroupItem.getId_group();
        ContentValues values = new ContentValues();
        values.put("group_name", parentGroupItem.getGroup_name());
        values.put("group_code", parentGroupItem.getGroup_code());
        values.put("create_date", parentGroupItem.getCreate_date());
        values.put("id_group_server", parentGroupItem.getId_group_server());
        values.put("is_restore", parentGroupItem.getIs_restore());
        values.put("icon_array_byte",parentGroupItem.getIcon_arrarByte());
        dbMain = this.getWritableDatabase();
        dbMain.update("tblGroupParent", values, "id_group=" + id_group, null);
        dbMain.close();
    }

    public void updateMemberItem(ParentMemberItem parentMemberItem) {
        int id_member = parentMemberItem.getId_member();
        ContentValues values3 = new ContentValues();
        values3.put("name_member", parentMemberItem.getName_member());
        values3.put("type_member", parentMemberItem.getType_member());
        values3.put("icon_array_byte", parentMemberItem.getIcon_array_byte());
        values3.put("id_member_server",parentMemberItem.getId_member_server());
        values3.put("is_blockall", parentMemberItem.getIs_blockall());
        values3.put("is_alowall",parentMemberItem.getIs_alowall());
        values3.put("is_blocksettings",parentMemberItem.getIs_blocksettings());
        //
        dbMain = this.getWritableDatabase();
        dbMain.update("tblMemberParent", values3, "id_member=" + id_member, null);
        dbMain.close();
    }

    public void updateProfileItem(ParentProfileItem parentProfileItem) {
        int id_profile = parentProfileItem.getId_profile();
        String day_profile = "'" + parentProfileItem.getDay_profile() + "'";
        String name_profile = "'" + parentProfileItem.getName_profile() + "'";
        int is_active = parentProfileItem.isActive() ? 1 : 0;
        int id_profile_server =  parentProfileItem.getId_profile_server();
        //
        dbMain = this.getWritableDatabase();
        String update = "update tblProfileParent set day_profile = " + day_profile
                + ", name_profile = " + name_profile + ", is_active = " + is_active
                + ", id_profile_server = " + id_profile_server
                + " where id_profile = " + id_profile;
        dbMain.execSQL(update);
        dbMain.close();
    }

    public void updateTimeParentItem(ParentTimeItem parentTimeItem) {
        int id_time_parent = parentTimeItem.getId_timer_parent();
        int hour_begin = parentTimeItem.getHourBegin();
        int minus_begin = parentTimeItem.getMinusBegin();
        int hour_end = parentTimeItem.getHourEnd();
        int minus_end = parentTimeItem.getMinusEnd();
        int id_time_server = parentTimeItem.getId_time_server();
        //
        dbMain = this.getWritableDatabase();
        String update = "update tblTimeParent set hour_begin = " + hour_begin
                + ", minus_begin = " + minus_begin + ", hour_end = " + hour_end
                + ", minus_end = " + minus_end
                + ", id_time_server = " + id_time_server + " where id_time_parent = " + id_time_parent;
        dbMain.execSQL(update);
        dbMain.close();
    }
    //============================updateData function=================================//
    // Function for parent
}

package com.android.keepfocus.data;

/**
 * Created by nguyenthong on 9/6/2016.
 */

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.keepfocus.utils.MainUtils;

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
                + " is_active integer" + ")";
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
        int keep_focus_id = (int) dbMain.insert("tblKeepFocus", null, values);
        keepFocus.setKeepFocusId(keep_focus_id);
        dbMain.close();
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
        //
        dbMain = this.getWritableDatabase();
        String update = "update tblKeepFocus set day_focus = " + day_focus
                + ", name_focus = " + name_focus + ", is_active = " + is_active
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
    public boolean isAppOrNotifiBlock(String packageName, String day, int hour,
                                      int min, int flagBlock) {
        dbMain = this.getWritableDatabase();
        int appId = getAppItemIdByPackageName(packageName);
        if (appId == -1) {
            return false;
        }
        ArrayList<ChildKeepFocusItem> list_Child_KeepFocusItem = getListFocusItemByAppItemId(appId);
        ArrayList<ChildTimeItem> list_Child_TimeItem;
        for (ChildKeepFocusItem a_Child_KeepFocusItem : list_Child_KeepFocusItem) {
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
                    if (!a_Child_KeepFocusItem.getDayFocus().contains(day)) {
                        continue;
                    }
                    list_Child_TimeItem = a_Child_KeepFocusItem.getListTimeFocus();
                    if (list_Child_TimeItem.size() == 0) {
                        return true;
                    }
                    for (ChildTimeItem a_Child_TimeItem : list_Child_TimeItem) {
                        if (a_Child_TimeItem.checkInTime(hour, min)) {
                            return true;
                        }
                    }
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
                ArrayList<ChildTimeItem> listTimeFocus = getListTimeById(keepFocusId);
                ChildKeepFocusItem a_Child_KeepFocusItem = new ChildKeepFocusItem();
                a_Child_KeepFocusItem.setKeepFocusId(keepFocusId);
                a_Child_KeepFocusItem.setDayFocus(dayFocus);
                a_Child_KeepFocusItem.setNameFocus(nameFocus);
                a_Child_KeepFocusItem.setActive(isActive);
                a_Child_KeepFocusItem.setListTimeFocus(listTimeFocus);
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
    // Function for parent
}

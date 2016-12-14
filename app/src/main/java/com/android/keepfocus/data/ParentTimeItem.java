package com.android.keepfocus.data;

import android.util.Log;

/**
 * Created by nguyenthong on 9/13/2016.
 */
public class ParentTimeItem {
    private int id_timer_parent;
    private int id_profile;
    private int hourBegin;
    private int minusBegin;
    private int hourEnd;
    private int minusEnd;
    private int id_time_server;
    public static final int INTIME_TYPE = 1;
    public static final int OUTTIME_TYPE = 2;
    public static final String TAG = "ParentTimeItem";

    public ParentTimeItem() {
        this.id_timer_parent = -1;
        this.id_profile = -1;
        this.hourBegin = 8;
        this.minusBegin = 0;
        this.hourEnd = 10;
        this.minusEnd = 0;
        this.id_time_server = -1;
    }

    public int getId_timer_parent() {
        return id_timer_parent;
    }

    public void setId_timer_parent(int id_timer_parent) {
        this.id_timer_parent = id_timer_parent;
    }

    public int getId_profile() {
        return id_profile;
    }

    public void setId_profile(int id_profile) {
        this.id_profile = id_profile;
    }

    public int getHourBegin() {
        return hourBegin;
    }

    public void setHourBegin(int hourBegin) {
        this.hourBegin = hourBegin;
    }

    public int getMinusBegin() {
        return minusBegin;
    }

    public void setMinusBegin(int minusBegin) {
        this.minusBegin = minusBegin;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(int hourEnd) {
        this.hourEnd = hourEnd;
    }

    public int getMinusEnd() {
        return minusEnd;
    }

    public void setMinusEnd(int minusEnd) {
        this.minusEnd = minusEnd;
    }

    /*
         * This method used to convert time hour, minus to string , in oder to
         * display
         */
    public String getStringHour(int hour, int minus) {
        StringBuilder hourString = new StringBuilder();
        if (hour < 10) {
            hourString.append("0" + hour);
        } else {
            hourString.append("" + hour);
        }
        hourString.append(":");
        if (minus < 10) {
            hourString.append("0" + minus);
        } else {
            hourString.append("" + minus);
        }
        if (hour < 12){
            hourString.append(" AM");
        }else {
            hourString = new StringBuilder();
            if (hour > 12) {
                hour = hour - 12;
            }
            if (hour < 10) {
                hourString.append("0" + hour);
            } else {
                hourString.append("" + hour);
            }
            hourString.append(":");
            if (minus < 10) {
                hourString.append("0" + minus);
            } else {
                hourString.append("" + minus);
            }
            hourString.append(" PM");
        }
        return hourString.toString();
    }

    /*
     * This method used to check input time is in time focus. Example input time
     * 10:00 , focus time 8:10 - 22:00 , this method must return true; input
     * time 10:00 , focus time 11:00 - 10:30, this method must return true;
     * input time 10:00 , focus time 10:01 - 9:02, this method must return
     * false; .....
     */
    public boolean checkInTime(int hour, int minus) {
        int typeTime = getTypeTime();
        if (typeTime == ChildTimeItem.INTIME_TYPE) {
            if (compareTime(hourBegin, minusBegin, hour, minus) != 1
                    && compareTime(hour, minus, hourEnd, minusEnd) != 1) {
                Log.e(TAG, "INTIME_TYPE" + true);
                return true;
            } else {
                Log.e(TAG, "INTIME_TYPE" + false);
                return false;
            }
        } else {
            if (compareTime(hourEnd, minusEnd, hour, minus) == -1
                    && compareTime(hour, minus, hourBegin, minusBegin) == -1) {
                Log.e(TAG, "OUTTIME_TYPE" + false);
                return false;
            } else {
                Log.e(TAG, "OUTTIME_TYPE" + true);
                return true;
            }

        }
    }

    public int getTypeTime() {
        if (hourBegin < hourEnd) {
            return ChildTimeItem.INTIME_TYPE;
        } else if (hourBegin > hourEnd) {
            return ChildTimeItem.OUTTIME_TYPE;
        } else {
            if (minusBegin < minusEnd) {
                return ChildTimeItem.INTIME_TYPE;
            } else {
                return ChildTimeItem.OUTTIME_TYPE;
            }
        }
    }

    public int compareTime(int hour, int minus, int hour2, int minus2) {
        if (hour > hour2) {
            return 1;
        } else if (hour < hour2) {
            return -1;
        } else {
            if (minus > minus2) {
                return 1;
            } else if (minus < minus2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public int getId_time_server() {
        return id_time_server;
    }

    public void setId_time_server(int id_time_server) {
        this.id_time_server = id_time_server;
    }
}

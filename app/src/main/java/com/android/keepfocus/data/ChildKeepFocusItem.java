package com.android.keepfocus.data;

import java.util.ArrayList;

public class ChildKeepFocusItem {
    private int keepFocusId;
    private String dayFocus;
    private String nameFocus;
    private boolean isActive;
    private ArrayList<ChildTimeItem> listTimeFocus;
    private ArrayList<ChildAppItem> listAppFocus;

    public ChildKeepFocusItem() {
        this.keepFocusId = -1;
        this.dayFocus = "";
        this.nameFocus = "Unknow";
        this.isActive = true;
        this.listAppFocus = new ArrayList<ChildAppItem>();
        this.listTimeFocus = new ArrayList<ChildTimeItem>();
    }

    public int getKeepFocusId() {
        return keepFocusId;
    }

    public void setKeepFocusId(int keepFocusId) {
        this.keepFocusId = keepFocusId;
    }

    public String getDayFocus() {
        return dayFocus;
    }

    public void setDayFocus(String dayFocus) {
        this.dayFocus = dayFocus;
    }

    public String getNameFocus() {
        return nameFocus;
    }

    public void setNameFocus(String nameFocus) {
        this.nameFocus = nameFocus;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public ArrayList<ChildTimeItem> getListTimeFocus() {
        return listTimeFocus;
    }

    public void setListTimeFocus(ArrayList<ChildTimeItem> listTimeFocus) {
        this.listTimeFocus = listTimeFocus;
    }

    public ArrayList<ChildAppItem> getListAppFocus() {
        return listAppFocus;
    }

    public void setListAppFocus(ArrayList<ChildAppItem> listAppFocus) {
        this.listAppFocus = listAppFocus;
    }

    /*
     * Input a day , example : Mon (day = 2), Tue(day = 3) , Sun (day = 1) In
     * order to String value dayFocus , this method will return true or false if
     * input day is in dayFocus or not.
     */
    public boolean checkDayFocus(int day) {
        if (dayFocus == null) {
            return false; // return false if don't have any day is focused
        }
        // Need more code continue ...
        return false;
    }

}

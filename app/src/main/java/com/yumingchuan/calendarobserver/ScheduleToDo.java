package com.yumingchuan.calendarobserver;

import java.io.Serializable;


public class ScheduleToDo implements Serializable {

    public String id;
    public String pTitle;
    public String pNote;
    public String startDate;
    public String endDate;
    public String event;
    public String pDisplayOrder;
    public long localCalendarStartTime;
    public long localCalendarEndTime;

    private boolean isLocalCalendarSchedule;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpNote() {
        return pNote;
    }

    public void setpNote(String pNote) {
        this.pNote = pNote;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public boolean isLocalCalendarSchedule() {
        return isLocalCalendarSchedule;
    }

    public void setLocalCalendarSchedule(boolean localCalendarSchedule) {
        isLocalCalendarSchedule = localCalendarSchedule;
    }

    public String getpDisplayOrder() {
        return pDisplayOrder;
    }

    public void setpDisplayOrder(String pDisplayOrder) {
        this.pDisplayOrder = pDisplayOrder;
    }

    public long getLocalCalendarStartTime() {
        return localCalendarStartTime;
    }

    public void setLocalCalendarStartTime(long localCalendarStartTime) {
        this.localCalendarStartTime = localCalendarStartTime;
    }

    public long getLocalCalendarEndTime() {
        return localCalendarEndTime;
    }

    public void setLocalCalendarEndTime(long localCalendarEndTime) {
        this.localCalendarEndTime = localCalendarEndTime;
    }

    @Override
    public String toString() {
        return "   id=" + id + "   pTitle=" + pTitle + "   pNote=" + pNote + "   startDate=" + startDate + "   endDate=" + endDate;
    }
}


package com.yumingchuan.calendarobserver;

import java.util.List;

/**
 * Created by love on 2016/2/23.
 */
public class ScheduleListBean {
    private String type;
    private String date;
    private List<ScheduleToDo> data;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ScheduleToDo> getData() {
        return data;
    }

    public void setData(List<ScheduleToDo> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        return this.date.equals(((ScheduleListBean) obj).date);
    }


}

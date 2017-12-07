package com.yumingchuan.calendarobserver;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jimmy on 2016/10/11 0011.
 */
public class LocalCalendarAsyncTask extends BaseAsyncTask<List<ScheduleListBean>> {

    private static DateFormat dateFormat_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private static long ONE_DAY_TIME = 1000 * 60 * 60 * 24;
    private final Context context;
    private final List<ScheduleListBean> scheduleListBeanList;
    private long startTime = 0L;
    private long endTime = 0L;
    private final Date date;

    public LocalCalendarAsyncTask(Context context, String startDate, String endDate, OnTaskFinishedListener<List<ScheduleListBean>> onTaskFinishedListener) {
        super(context, onTaskFinishedListener);
        date = new Date();
        scheduleListBeanList = new ArrayList<>();
        this.context = context;
        try {
            startTime = dateFormat_yyyyMMdd.parse(startDate).getTime();
            endTime = dateFormat_yyyyMMdd.parse(endDate).getTime() + ONE_DAY_TIME;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    protected List<ScheduleListBean> doInBackground(Void... params) {
        for (long i = startTime; i <= endTime; i += ONE_DAY_TIME) {
            date.setTime(i);
            String dateStr = dateFormat_yyyyMMdd.format(date);
            LogUtils.i(dateStr);
            ScheduleListBean scheduleListBean = new ScheduleListBean();
            scheduleListBean.setDate(dateStr);
            scheduleListBean.setData(LocalCalendarEventUtils.getOneDayCalendarEvent(context, dateStr));
            scheduleListBeanList.add(scheduleListBean);
        }
        return scheduleListBeanList;
    }
}

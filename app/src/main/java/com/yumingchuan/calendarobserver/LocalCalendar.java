package com.yumingchuan.calendarobserver;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by yumingchuan on 2017/11/23.
 */

public class LocalCalendar {

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }


    private static String CALENDARS_NAME = "test";
    private static String CALENDARS_ACCOUNT_NAME = "test@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.exchange";
    private static String CALENDARS_DISPLAY_NAME = "测试账户";
    private static int ONE_HOUR = 1;


    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALANDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    //检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }


    public static void addCalendarEvent(Context context, String title, String description, long beginTime) {
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }

        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(beginTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + ONE_HOUR);//设置终止时间
        long end = mCalendar.getTime().getTime();

        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            return;
        }
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前10分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if (uri == null) {
            // 添加闹钟提醒失败直接返回
            return;
        }
    }

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static void deleteCalendarEvent(Context context, String title) {

        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null)//查询返回空值
                return;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    public static List<ScheduleToDo> getAllCalendarEvent(Context context) {


        Calendar calendar = Calendar.getInstance();
        dateFormat.format(calendar.getTime());

        long startTime = calendar.getTime().getTime();
        long endTime = calendar.getTime().getTime() + 1000 * 60 * 60 * 24;

        // String[] selectionArgs = {android.provider.CalendarContract.Events.DTSTART + ">" + 1, android.provider.CalendarContract.Events.DTEND + "<" + 1};
//        String selection = android.provider.CalendarContract.Events.DTSTART + "<" + 11119910011111L;
        String selection = android.provider.CalendarContract.Events.DTSTART + ">" + startTime + " and "
                + android.provider.CalendarContract.Events.DTEND + "<" + endTime;

        Log.i("temptemp", startTime + "    " + endTime);


        String testSelection = CalendarContract.Events.TITLE + "='1'";


        List<ScheduleToDo> calendarEvents = new ArrayList<>();

        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, selection, null, null);
        try {
            if (eventCursor == null)//查询返回空值
                return calendarEvents;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {

                    ScheduleToDo scheduleToDo = new ScheduleToDo();

                    scheduleToDo.setId(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID)) + "");
                    scheduleToDo.setpTitle(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.TITLE)));
                    scheduleToDo.setpNote(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
                    scheduleToDo.setStartDate(eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART)) + "");
                    scheduleToDo.setEndDate(eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTEND)) + "");
//                    scheduleToDo.setEvent(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.SYNC_EVENTS)));

                    calendarEvents.add(scheduleToDo);
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }

        return calendarEvents;
    }


    /**
     * 打开日历日程数据的详情
     */
    public static void openCalendarEventDetail(Context cnt) {

        //具体的一条日历数据
//        id=2   pTitle=1   pNote=null   startDate=1511488800000   endDate=1511492400000

        try {
            Intent t_intent = new Intent(Intent.ACTION_VIEW);
            t_intent.addCategory(Intent.CATEGORY_DEFAULT);
            t_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, 2);
            t_intent.setData(uri);
            cnt.startActivity(t_intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(cnt, "打开日历失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 打开日历应用
     */
    public static void gotoCalendarApp(Context cnt) {
        try {
            Intent t_intent = new Intent(Intent.ACTION_VIEW);
            t_intent.addCategory(Intent.CATEGORY_DEFAULT);
            t_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            t_intent.setDataAndType(Uri.parse("content://com.android.calendar/"), "time/epoch");
            cnt.startActivity(t_intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(cnt, "打开日历失败", Toast.LENGTH_SHORT).show();
        }
    }

}

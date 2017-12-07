
package com.yumingchuan.calendarobserver;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by yumingchuan on 2017/11/23.
 */

public class LocalCalendarEventUtils {

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


    /**
     * 获取所有日历的名字
     *
     * @param context
     */
    public static List<String[]> getAllCalendarNames(Context context) {
        List<String[]> calendarAccountNameAndDisplayNames = new ArrayList<>();
        String uri = "content://com.android.calendar/calendars";
//        查询手机日历：
        Uri calendars = Uri.parse(uri);
        Cursor managedCursor = context.getContentResolver().query(calendars, null, null, null, null);


//        String[] tempNames = managedCursor.getColumnNames();
//        for (int i = 0; i < tempNames.length; i++) {
//            LogUtils.i(tempNames[i]);
//        }

        if (managedCursor.moveToFirst()) {
            do {
                //Log.i("idid", managedCursor.getString(managedCursor.getColumnIndex("calendar_displayName")));
//                Log.i("idid", managedCursor.getString(managedCursor.getColumnIndex("account_name")));

                String[] tempCalendarAccountNameAndDisplayName = new String[3];

                tempCalendarAccountNameAndDisplayName[0] = managedCursor.getString(managedCursor.getColumnIndex(CalendarContract.Calendars._ID));
                tempCalendarAccountNameAndDisplayName[1] = managedCursor.getString(managedCursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
                tempCalendarAccountNameAndDisplayName[2] = managedCursor.getString(managedCursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));

                calendarAccountNameAndDisplayNames.add(tempCalendarAccountNameAndDisplayName);

            } while (managedCursor.moveToNext());
        }

        return calendarAccountNameAndDisplayNames;
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

    private static DateFormat dateFormat_yyyy_MM_dd_hh_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat dateFormat_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private static long ONE_DAY_TIME = 1000 * 60 * 60 * 24;


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


    /**
     * 获取所有日历的所有事件
     *
     * @param context
     * @return
     */
    public static List<ScheduleToDo> getAllCalendarEvent(Context context) {


        List<ScheduleToDo> calendarEvents = new ArrayList<>();

        try {

            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";

            String[] selectionArgs = new String[]{"sampleuser@gmail.com", "com.google"};


            long startTime = dateFormat_yyyyMMdd.parse(new SimpleDateFormat("yyyyMMdd").format(new Date()) + " 00:00:00").getTime();
            long endTime = startTime + 1000 * 60 * 60 * 24;

//            12-04 14:53:41.917 2052-2052/? I/idid: Local account
//            12-04 14:53:41.917 2052-2052/? I/idid: Local calendar
//            12-04 14:53:41.917 2052-2052/? I/idid: Birthday
//            12-04 14:53:41.917 2052-2052/? I/idid: Birthday

//            WHERE (lastSynced = 0 AND (dtstart>1512316800000 and dtend<1512403200000 and account_name= Local calendar))

            // String[] selectionArgs = {android.provider.CalendarContract.Events.DTSTART + ">" + 1, android.provider.CalendarContract.Events.DTEND + "<" + 1};
//        String selection = android.provider.CalendarContract.Events.DTSTART + "<" + 11119910011111L;
            String selection1 = android.provider.CalendarContract.Events.DTSTART + ">=" + startTime + " and "
                    + android.provider.CalendarContract.Events.DTEND + "<=" + endTime
                    + " and " + CalendarContract.Events.CALENDAR_ID + "= 1";


            Log.i("temptemp", startTime + "    " + endTime);

            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, selection1, null, null);

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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendarEvents;
    }


    /**
     * @param context
     * @param someDate 开始日期 yyyyMMdd
     * @return
     */
    public static List<ScheduleToDo> getOneDayCalendarEvent(Context context, String someDate) {
        List<ScheduleToDo> calendarEvents = new ArrayList<>();
        try {
            int calendarEventCount = -1000;
            Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, getOneDaySelection(someDate, someDate), null, null);
            try {
                if (eventCursor == null)//查询返回空值
                    return calendarEvents;
                if (eventCursor.getCount() > 0) {
                    //遍历所有事件，找到title跟需要查询的title一样的项
                    for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                        ScheduleToDo scheduleToDo = new ScheduleToDo();
                        scheduleToDo.setLocalCalendarSchedule(true);
                        scheduleToDo.setpDisplayOrder((calendarEventCount--) + "");
                        scheduleToDo.setId(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID)) + "");
                        scheduleToDo.setpTitle(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.TITLE)));
                        scheduleToDo.setpNote(eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));


                        scheduleToDo.setLocalCalendarStartTime(eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTSTART)));
                        scheduleToDo.setLocalCalendarEndTime(eventCursor.getLong(eventCursor.getColumnIndex(CalendarContract.Events.DTEND)));

                        scheduleToDo.setAllDayCalendarTask(eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Events.ALL_DAY)) == 1);


//                        String RDATE = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.RDATE));//CalendarContract.Events.RDATE
//
//                        LogUtils.i(RDATE);


                        String LAST_DATE = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.LAST_DATE));//CalendarContract.Events.RDATE


                        Calendar calendar = Calendar.getInstance();

                        calendar.setTimeInMillis(Long.parseLong(LAST_DATE));

                        LogUtils.i(LAST_DATE + "    :::    " + scheduleToDo.pTitle + "   :    " + dateFormat_yyyy_MM_dd_hh_mm_ss.format(calendar.getTime()));

                        calendarEvents.add(scheduleToDo);
                    }
                }
            } finally {
                if (eventCursor != null) {
                    eventCursor.close();
                }
            }

        } catch (Exception e) {

        }
        return calendarEvents;
    }

    private static String getOneDaySelection(String startDate, String endDate) {
        String tempStr = "";
        String selection = "";

        try {
            long startTime = dateFormat_yyyyMMdd.parse(startDate).getTime();
            long endTime = dateFormat_yyyyMMdd.parse(endDate).getTime() + ONE_DAY_TIME;

            selection = android.provider.CalendarContract.Events.DTSTART + ">=" + startTime + " and "
                    + android.provider.CalendarContract.Events.DTEND + "<=" + endTime;

            String calendarId = CalendarContract.Events.CALENDAR_ID;
            String containCalendarIds = SPUtils.getInstance().getString("containCalendarIds", "");
            String[] noContainCalendarIdArrays = EmptyUtils.isEmpty(containCalendarIds) ? null : containCalendarIds.split(",");

            if (!EmptyUtils.isEmpty(noContainCalendarIdArrays)) {
                for (int i = 0; i < noContainCalendarIdArrays.length; i++) {
                    if (i == 0) {
                        if (noContainCalendarIdArrays.length == 1) {
                            tempStr += " and " + calendarId + " = " + noContainCalendarIdArrays[i];
                        } else {
                            tempStr += " and ( " + calendarId + " = " + noContainCalendarIdArrays[i];
                        }
                    } else if (i == noContainCalendarIdArrays.length - 1) {
                        tempStr += " OR " + calendarId + " = " + noContainCalendarIdArrays[i] + " )";
                    } else {
                        tempStr += " OR " + calendarId + " = " + noContainCalendarIdArrays[i];
                    }
                }
            } else {
                // tempStr += " and " + calendarId + " = -1000";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            selection += tempStr;
        }


        LogUtils.i(selection);


//        return selection;
        return "calendar_id = 1";
    }


    private static String getOneDaySelection(String startDate, String endDate) {
        String tempStr = "";
        String selection1 = "";
        String selection2 = "";

        try {
            long startTime = dateFormat_yyyyMMdd.parse(startDate).getTime();
            long endTime = dateFormat_yyyyMMdd.parse(endDate).getTime() + ONE_DAY_TIME;

            selection1 = android.provider.CalendarContract.Events.DTSTART + " >= " + startTime + " and "
                    + android.provider.CalendarContract.Events.DTEND + " <= " + endTime;

            String calendarId = CalendarContract.Events.CALENDAR_ID;
            String containCalendarIds = SPUtils.getInstance().getString("containCalendarIds", "");
            String[] noContainCalendarIdArrays = EmptyUtils.isEmpty(containCalendarIds) ? null : containCalendarIds.split(",");

            if (!EmptyUtils.isEmpty(noContainCalendarIdArrays)) {
                for (int i = 0; i < noContainCalendarIdArrays.length; i++) {
                    if (i == 0) {
                        if (noContainCalendarIdArrays.length == 1) {
                            tempStr += " and " + calendarId + " = " + noContainCalendarIdArrays[i];
                        } else {
                            tempStr += " and ( " + calendarId + " = " + noContainCalendarIdArrays[i];
                        }
                    } else if (i == noContainCalendarIdArrays.length - 1) {
                        tempStr += " OR " + calendarId + " = " + noContainCalendarIdArrays[i] + " )";
                    } else {
                        tempStr += " OR " + calendarId + " = " + noContainCalendarIdArrays[i];
                    }
                }
            } else {
                //tempStr += " and " + calendarId + " = -1000";
            }
            selection2 = CalendarContract.Events.ALL_DAY + " =1 AND " + CalendarContract.Events.LAST_DATE + " >= " + (startTime + ONE_DAY_TIME) + " AND " + android.provider.CalendarContract.Events.LAST_DATE + " <= " + (endTime + ONE_DAY_TIME);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            selection1 += tempStr;
        }

        //LogUtils.i(" ( " + selection1 + " ) " + " OR" + " ( " + selection2 + " ) ");
//        dtstart>=1512576000000 and dtend<=1512662400000 and ( calendar_id = 1 OR calendar_id = 2 )
        return " ( " + selection1 + " ) " + " OR " + " ( " + selection2 + " ) ";
    }

    public static void getOneDayCalendarEvent(Context context, String someDate, QueryHandler.OnQueryEventCompleteListener onQueryEventCompleteListener) {
        try {
            QueryHandler queryHandler = new QueryHandler(context.getContentResolver());
            queryHandler.setOnQueryEventCompleteListener(onQueryEventCompleteListener);
            //执行查询语句，可以将adapter作为第二个参数传入
            queryHandler.startQuery(11122, null, Uri.parse(CALANDER_EVENT_URL), null, getOneDaySelection(someDate, someDate), null, null);
        } catch (Exception e) {
            onQueryEventCompleteListener.onQueryEventComplete(new ArrayList<ScheduleToDo>());
        }

    }


    /**
     * 打开日历日程数据的详情
     *
     * @param cnt
     * @param scheduleToDo
     */
    public static void openCalendarEventDetail(Context cnt, ScheduleToDo scheduleToDo) {

        //具体的一条日历数据
//        id=2   pTitle=1   pNote=null   startDate=1511488800000   endDate=1511492400000

        try {
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Integer.parseInt(scheduleToDo.id));
            Intent t_intent = new Intent(Intent.ACTION_VIEW);
//            t_intent.addCategory(Intent.CATEGORY_DEFAULT);
//            t_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            t_intent.setData(uri);
            t_intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, scheduleToDo.getLocalCalendarStartTime());
            t_intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, scheduleToDo.getLocalCalendarEndTime());
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


    public static void registerContentObserver(Activity activity) {
        //    在主类中，实例化并实施监听
        CalendarObserver calObserver = new CalendarObserver(activity, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /**当监听到改变时，做业务操作*/
                // EventBus.getDefault().post(new EventMessage(EventMessage.Schedule.UPDATE_SCHEDULE_LIST));
            }
        });

        activity.getContentResolver().unregisterContentObserver(calObserver);

        //注册日程事件监听
        activity.getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, calObserver);
    }


}

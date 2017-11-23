package com.yumingchuan.calendarobserver;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


//    String[] projection = new String[] { CalendarContract.Events._ID , CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION , CalendarContract.Events.RRULE ,CalendarContract.Events.DURATION };
//
//    String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.CALENDAR_ID + " = " + LOCAL_CAL_ACCOUNT_ID + " ) )";
//
//    Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSchedule = findViewById(R.id.btnSchedule);
        Button printSchedule = findViewById(R.id.printSchedule);
        Button openLocalCalendarScheduleDetail = findViewById(R.id.openLocalCalendarScheduleDetail);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalCalendar.addCalendarEvent(getApplicationContext(), "添加一天日程数据到日历", "日程内容", Calendar.getInstance().getTime().getTime());
            }
        });


        printSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printSchedule();
            }
        });


        openLocalCalendarScheduleDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Calendar beginTime = Calendar.getInstance();
//                beginTime.set(2017, 0, 13, 7, 30);
//                Calendar endTime = Calendar.getInstance();
//                endTime.set(20127, 0, 13, 8, 30);
////                Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
//                Intent intent = new Intent(CalendarContract.Events.CONTENT_URI)
//                        .setData(CalendarContract.Events.CONTENT_URI)
//                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                        .putExtra(CalendarContract.Events.TITLE, "Yoga")
//                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
//                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
//                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
//                startActivity(intent);

                gotoCalendarApp(getApplicationContext());
            }
        });


    }


    /**
     * 打开日历日程数据的详情
     */
    public void openCalendarEventDetail(Context cnt) {

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
    public void gotoCalendarApp(Context cnt) {
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


    private void printSchedule() {
        List<ScheduleToDo> temp = LocalCalendar.getAllCalendarEvent(getApplicationContext());
        for (int i = 0; i < temp.size(); i++) {
            Log.i("temptemp", temp.get(i).toString());
        }
    }


}

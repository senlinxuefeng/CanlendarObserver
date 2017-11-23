package com.yumingchuan.calendarobserver;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2017, 0, 13, 7, 30);
                Calendar endTime = Calendar.getInstance();
                endTime.set(20127, 0, 13, 8, 30);
                Intent intent = new Intent(Intent.CATEGORY_APP_CALENDAR)
                        .setData(CalendarContract.Events.CALANDER_EVENT_URL)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, "Yoga")
                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
                startActivity(intent);
            }
        });


    }

    private void printSchedule() {
        List<ScheduleToDo> temp = LocalCalendar.getAllCalendarEvent(getApplicationContext());
        for (int i = 0; i < temp.size(); i++) {
            Log.i("temptemp", temp.get(i).toString());
        }
    }


}

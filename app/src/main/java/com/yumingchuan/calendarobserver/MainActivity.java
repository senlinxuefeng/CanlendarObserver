package com.yumingchuan.calendarobserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BaseRecyclerViewAdapter baseRecyclerViewAdapter;


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


        addListener();

        initAdapter();
    }

    private void initAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        baseRecyclerViewAdapter = new BaseRecyclerViewAdapter<ScheduleToDo>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return LayoutInflater.from(getBaseContext()).inflate(R.layout.item_schedule, null);
            }

            @Override
            public void bindViewData(View itemView, ScheduleToDo scheduleToDo, int position) {
                TextView title = itemView.findViewById(R.id.title);
                TextView content = itemView.findViewById(R.id.content);
                title.setText(scheduleToDo.getpTitle());
                content.setText(scheduleToDo.getpNote() != null ? scheduleToDo.getpNote() : "");
            }
        };
        recyclerView.setAdapter(baseRecyclerViewAdapter);
    }

    private void addListener() {
        findViewById(R.id.btnSchedule).setOnClickListener(this);
        findViewById(R.id.printSchedule).setOnClickListener(this);
        findViewById(R.id.openLocalCalendarScheduleDetail).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSchedule:
                LocalCalendar.addCalendarEvent(getApplicationContext(), "添加第" + baseRecyclerViewAdapter.getItemCount() + "条日程数据到日历", "日程内容", Calendar.getInstance().getTime().getTime());
                break;
            case R.id.printSchedule:
                printSchedule();
                baseRecyclerViewAdapter.reloadData(LocalCalendar.getAllCalendarEvent(getApplicationContext()));
                break;
            case R.id.openLocalCalendarScheduleDetail:
                LocalCalendar.openCalendarEventDetail(getApplicationContext());
                break;

            default:

                break;
        }
    }

    private void printSchedule() {
        List<ScheduleToDo> temp = LocalCalendar.getAllCalendarEvent(getApplicationContext());
        for (int i = 0; i < temp.size(); i++) {
            Log.i("temptemp", temp.get(i).toString());
        }
    }


}

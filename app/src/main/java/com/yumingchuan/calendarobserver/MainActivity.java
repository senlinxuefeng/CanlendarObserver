package com.yumingchuan.calendarobserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSchedule = findViewById(R.id.btnSchedule);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalCalendar.addCalendarEvent(getApplicationContext(), "添加一天日程数据到日历", "日程内容", Calendar.getInstance().getTime().getTime());
            }
        });


        List<String> temp = LocalCalendar.getAllCalendarEvent(getApplicationContext());

        for (int i = 0; i < temp.size(); i++) {
            Log.i("temptemp", temp.get(i));
        }


    }
}

package com.yumingchuan.calendarobserver;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract;

import com.blankj.utilcode.util.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumingchuan on 2017/12/6.
 * 写一个异步查询类
 */

public final class QueryHandler extends AsyncQueryHandler {

    private final List<ScheduleToDo> calendarEvents;

    public interface OnQueryEventCompleteListener {
        void onQueryEventComplete(List<ScheduleToDo> calendarEvents);
    }

    private OnQueryEventCompleteListener onQueryEventCompleteListener;

    public void setOnQueryEventCompleteListener(OnQueryEventCompleteListener onQueryEventCompleteListener) {
        this.onQueryEventCompleteListener = onQueryEventCompleteListener;
    }

    public QueryHandler(ContentResolver cr) {
        super(cr);
        calendarEvents = new ArrayList<>();
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor eventCursor) {
        super.onQueryComplete(token, cookie, eventCursor);
        // 更新mAdapter的Cursor
        //changeCursor.changeCursor(cursor);
        int calendarEventCount = -1000;
        try {
            if (eventCursor != null) {
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

                        calendarEvents.add(scheduleToDo);
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
//                eventCursor.close();
            }
            if (!EmptyUtils.isEmpty(onQueryEventCompleteListener)) {
                onQueryEventCompleteListener.onQueryEventComplete(calendarEvents);
            }
        }
    }
    //CursorAdapter changeCursor = new CursorAdapter();
}
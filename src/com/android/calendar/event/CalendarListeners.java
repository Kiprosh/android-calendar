package com.android.calendar.event;

import android.text.format.Time;

import com.android.calendar.Event;


public interface CalendarListeners {

    void onEventClick(Time tapEvent, Event clickedEvent);

    void onDayChange(Time selectedDay);
}

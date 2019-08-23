package com.android.calendar;

import android.text.format.Time;


public interface CalendarListeners {

    void onEventClick(Time tapEvent, Event clickedEvent);

    void onDayChange(Time selectedDay);
}
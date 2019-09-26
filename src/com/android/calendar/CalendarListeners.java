package com.android.calendar;

import android.text.format.Time;


public interface CalendarListeners {

    void onEventClick(boolean isDayView, Time tapEvent, Event clickedEvent);

    void onDayChange(Time selectedStartDay, Time selectedEndDay, boolean isDayView);

    void onWeekViewHeaderDayClick(Time selectedDay);

    void callOtherCalendarViews(Time time);

    void updateToolbarTitleOnScroll(long dateInMilliseconds);

    void callAPIForEvents(long updatedTimeInMillis);
}
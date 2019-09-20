package com.android.calendar;

import android.text.format.Time;


public interface CalendarListeners {

    void onEventClick(Time tapEvent, Event clickedEvent);

    void onDayChange(Time selectedStartDay, Time selectedEndDay, boolean isDayView);

    void onWeekViewHeaderDayClick(Time selectedDay);


    public void callOtherCalendarViews(Time time);

    public void updateToolbarTitleOnScroll(long dateInMilliseconds);

    public void callAPIForEvents(long updatedTimeInMillis);
}
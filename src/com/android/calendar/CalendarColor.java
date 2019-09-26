package com.android.calendar;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CalendarColor {

    public static final int WEEK_TODAY = 1;
    public static final int WEEK_SATURDAY = 2;
    public static final int WEEK_SUNDAY = 3;
    public static final int CALENDAR_DATE_BANNER_TEXT_COLOR = 4;
    public static final int FUTURE_BG_COLOR = 5;
    public static final int BG_COLOR = 6;
    public static final int CALENDAR_HOUR_LABEL_COLOR = 7;
    public static final int CALENDAR_GRID_AREA_SELECTED = 8;
    public static final int CALENDAR_GRIDLINE_INNER_HORIZONTAL_COLOR = 9;
    public static final int CALENDAR_GRIDLINE_INNER_VERTICAL_COLOR = 10;
    public static final int PRESSED_COLOR = 11;
    public static final int CLICKED_COLOR = 12;
    public static final int EVENT_TEXT_COLOR = 13;
    public static final int MORE_EVENTS_TEXT_COLOR = 14;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WEEK_TODAY, WEEK_SATURDAY, WEEK_SUNDAY,
            CALENDAR_DATE_BANNER_TEXT_COLOR, FUTURE_BG_COLOR, BG_COLOR, CALENDAR_HOUR_LABEL_COLOR, CALENDAR_GRID_AREA_SELECTED,
            CALENDAR_GRIDLINE_INNER_HORIZONTAL_COLOR, CALENDAR_GRIDLINE_INNER_VERTICAL_COLOR, PRESSED_COLOR,
            CLICKED_COLOR, EVENT_TEXT_COLOR, MORE_EVENTS_TEXT_COLOR})
    public @interface Type {
    }
}
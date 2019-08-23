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


    public static final int MONTH_WEEK_NUM_COLOR = 15;
    public static final int MONTH_NUM_COLOR = 16;
    public static final int MONTH_NUM_OTHER_COLOR = 17;
    public static final int MONTH_NUM_TODAY_COLOR = 18;
    public static final int MONTH_EVENT_COLOR = 19;
    public static final int MONTH_DECLINED_EVENT_COLOR = 20;
    public static final int MONTH_DECLINED_EXTRAS_COLOR = 21;
    public static final int MONTH_EVENT_EXTRA_COLOR = 22;
    public static final int MONTH_EVENT_OTHER_COLOR = 23;
    public static final int MONTH_EVENT_EXTRA_OTHER_COLOR = 24;
    public static final int MONTH_BG_TODAY_COLOR = 25;
    public static final int MONTH_BG_FOCUS_MONTH_COLOR = 26;
    public static final int MONTH_BG_OTHER_COLOR = 27;
    public static final int MONTH_BG_COLOR = 28;
    public static final int DAY_SEPARATOR_INNER_COLOR = 29;
    public static final int TODAY_ANIMATE_COLOR = 30;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WEEK_TODAY, WEEK_SATURDAY, WEEK_SUNDAY,
            CALENDAR_DATE_BANNER_TEXT_COLOR, FUTURE_BG_COLOR, BG_COLOR, CALENDAR_HOUR_LABEL_COLOR, CALENDAR_GRID_AREA_SELECTED,
            CALENDAR_GRIDLINE_INNER_HORIZONTAL_COLOR, CALENDAR_GRIDLINE_INNER_VERTICAL_COLOR, PRESSED_COLOR,
            CLICKED_COLOR, EVENT_TEXT_COLOR, MORE_EVENTS_TEXT_COLOR, MONTH_WEEK_NUM_COLOR, MONTH_NUM_COLOR,
            MONTH_NUM_OTHER_COLOR, MONTH_NUM_TODAY_COLOR, MONTH_EVENT_COLOR, MONTH_DECLINED_EVENT_COLOR,
            MONTH_DECLINED_EXTRAS_COLOR, MONTH_EVENT_EXTRA_COLOR, MONTH_EVENT_OTHER_COLOR,
            MONTH_EVENT_EXTRA_OTHER_COLOR, MONTH_BG_TODAY_COLOR, MONTH_BG_FOCUS_MONTH_COLOR,
            MONTH_BG_OTHER_COLOR, MONTH_BG_COLOR, DAY_SEPARATOR_INNER_COLOR, TODAY_ANIMATE_COLOR})
    public @interface Type {
    }
}

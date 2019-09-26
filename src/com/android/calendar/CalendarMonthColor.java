package com.android.calendar;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CalendarMonthColor {

    public static final int MONTH_WEEK_NUM_COLOR = 1;
    public static final int MONTH_NUM_COLOR = 2;
    public static final int MONTH_NUM_OTHER_COLOR = 3;
    public static final int MONTH_NUM_TODAY_COLOR = 4;
    public static final int MONTH_EVENT_COLOR = 5;
    public static final int MONTH_DECLINED_EVENT_COLOR = 6;
    public static final int MONTH_DECLINED_EXTRAS_COLOR = 7;
    public static final int MONTH_EVENT_EXTRA_COLOR = 8;
    public static final int MONTH_EVENT_OTHER_COLOR = 9;
    public static final int MONTH_EVENT_EXTRA_OTHER_COLOR = 10;
    public static final int MONTH_BG_TODAY_COLOR = 11;
    public static final int MONTH_BG_FOCUS_MONTH_COLOR = 12;
    public static final int MONTH_BG_OTHER_COLOR = 13;
    public static final int MONTH_BG_COLOR = 14;
    public static final int DAY_SEPARATOR_INNER_COLOR = 15;
    public static final int TODAY_ANIMATE_COLOR = 16;
    public static final int BGCOLOR = 17;
    public static final int SELECTED_WEEK_BG_COLOR = 18;
    public static final int FOCUS_MONTH_COLOR = 19;
    public static final int OTHER_MONTH_COLOR = 20;
    public static final int DAY_SEPARATOR_COLOR = 21;
    public static final int TODAY_OUTLINE_COLOR = 22;
    public static final int WEEK_NUM_COLOR = 23;
    public static final int SATURDAY_COLOR = 24;
    public static final int SUNDAY_COLOR = 25;
    public static final int DAY_NAME_COLOR = 26;
    public static final int MONTH_CLICK_DAY_COLOR = 27;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MONTH_WEEK_NUM_COLOR, MONTH_NUM_COLOR,
            MONTH_NUM_OTHER_COLOR, MONTH_NUM_TODAY_COLOR, MONTH_EVENT_COLOR, MONTH_DECLINED_EVENT_COLOR,
            MONTH_DECLINED_EXTRAS_COLOR, MONTH_EVENT_EXTRA_COLOR, MONTH_EVENT_OTHER_COLOR,
            MONTH_EVENT_EXTRA_OTHER_COLOR, MONTH_BG_TODAY_COLOR, MONTH_BG_FOCUS_MONTH_COLOR,
            MONTH_BG_OTHER_COLOR, MONTH_BG_COLOR, DAY_SEPARATOR_INNER_COLOR, TODAY_ANIMATE_COLOR, BGCOLOR,
            SELECTED_WEEK_BG_COLOR, FOCUS_MONTH_COLOR, OTHER_MONTH_COLOR, DAY_SEPARATOR_COLOR,
            TODAY_OUTLINE_COLOR, WEEK_NUM_COLOR, SATURDAY_COLOR, SUNDAY_COLOR, DAY_NAME_COLOR, MONTH_CLICK_DAY_COLOR})

    public @interface Type {
    }

}

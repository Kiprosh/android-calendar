package com.android.calendar.helpers;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.calendar.DynamicTheme;

public class DayFieldColorHelper implements Parcelable {

    private int pressedColor;
    private int clickedColor;
    private int eventTextColor;
    private int moreEventsTextColor;
    private int weekTodayColor;
    private int weekSaturdayColor;
    private int weekSundayColor;
    private int calendarDateBannerTextColor;
    private int calendarGridAreaSelected;
    private int calendarGridLineInnerHorizontalColor;
    private int calendarGridLineInnerVerticalColor;
    private int futureBgColor;
    private int futureBgColorRes;
    private int bgColor;
    private int newEventHintColor;
    private int calendarHourLabelColor;

    public DayFieldColorHelper(Context context) {
        DynamicTheme dynTheme = new DynamicTheme();
        weekTodayColor = dynTheme.getColor(context, "week_today");
        weekSaturdayColor = dynTheme.getColor(context, "week_saturday");
        weekSundayColor = dynTheme.getColor(context, "week_sunday");
        calendarDateBannerTextColor = dynTheme.getColor(context, "calendar_date_banner_text_color");
        futureBgColorRes = dynTheme.getColor(context, "calendar_future_bg_color");
        bgColor = dynTheme.getColor(context, "calendar_hour_background");
        calendarHourLabelColor = dynTheme.getColor(context, "calendar_hour_label");
        calendarGridAreaSelected = dynTheme.getColor(context, "calendar_grid_area_selected");
        calendarGridLineInnerHorizontalColor = dynTheme.getColor(context, "calendar_grid_line_inner_horizontal_color");
        calendarGridLineInnerVerticalColor = dynTheme.getColor(context, "calendar_grid_line_inner_vertical_color");
        pressedColor = dynTheme.getColor(context, "pressed");
        clickedColor = dynTheme.getColor(context, "day_event_clicked_background_color");
        eventTextColor = dynTheme.getColor(context, "calendar_event_text_color");
        moreEventsTextColor = dynTheme.getColor(context, "month_event_other_color");
    }

    protected DayFieldColorHelper(Parcel in) {
        pressedColor = in.readInt();
        clickedColor = in.readInt();
        eventTextColor = in.readInt();
        moreEventsTextColor = in.readInt();
        weekTodayColor = in.readInt();
        weekSaturdayColor = in.readInt();
        weekSundayColor = in.readInt();
        calendarDateBannerTextColor = in.readInt();
        calendarGridAreaSelected = in.readInt();
        calendarGridLineInnerHorizontalColor = in.readInt();
        calendarGridLineInnerVerticalColor = in.readInt();
        futureBgColor = in.readInt();
        futureBgColorRes = in.readInt();
        bgColor = in.readInt();
        newEventHintColor = in.readInt();
        calendarHourLabelColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pressedColor);
        dest.writeInt(clickedColor);
        dest.writeInt(eventTextColor);
        dest.writeInt(moreEventsTextColor);
        dest.writeInt(weekTodayColor);
        dest.writeInt(weekSaturdayColor);
        dest.writeInt(weekSundayColor);
        dest.writeInt(calendarDateBannerTextColor);
        dest.writeInt(calendarGridAreaSelected);
        dest.writeInt(calendarGridLineInnerHorizontalColor);
        dest.writeInt(calendarGridLineInnerVerticalColor);
        dest.writeInt(futureBgColor);
        dest.writeInt(futureBgColorRes);
        dest.writeInt(bgColor);
        dest.writeInt(newEventHintColor);
        dest.writeInt(calendarHourLabelColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DayFieldColorHelper> CREATOR = new Creator<DayFieldColorHelper>() {
        @Override
        public DayFieldColorHelper createFromParcel(Parcel in) {
            return new DayFieldColorHelper(in);
        }

        @Override
        public DayFieldColorHelper[] newArray(int size) {
            return new DayFieldColorHelper[size];
        }
    };

    public int getPressedColor() {
        return pressedColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    public int getClickedColor() {
        return clickedColor;
    }

    public void setClickedColor(int clickedColor) {
        this.clickedColor = clickedColor;
    }

    public int getEventTextColor() {
        return eventTextColor;
    }

    public void setEventTextColor(int eventTextColor) {
        this.eventTextColor = eventTextColor;
    }

    public int getMoreEventsTextColor() {
        return moreEventsTextColor;
    }

    public void setMoreEventsTextColor(int moreEventsTextColor) {
        this.moreEventsTextColor = moreEventsTextColor;
    }

    public int getWeekTodayColor() {
        return weekTodayColor;
    }

    public void setWeekTodayColor(int weekTodayColor) {
        this.weekTodayColor = weekTodayColor;
    }

    public int getWeekSaturdayColor() {
        return weekSaturdayColor;
    }

    public void setWeekSaturdayColor(int weekSaturdayColor) {
        this.weekSaturdayColor = weekSaturdayColor;
    }

    public int getWeekSundayColor() {
        return weekSundayColor;
    }

    public void setWeekSundayColor(int weekSundayColor) {
        this.weekSundayColor = weekSundayColor;
    }

    public int getCalendarDateBannerTextColor() {
        return calendarDateBannerTextColor;
    }

    public void setCalendarDateBannerTextColor(int calendarDateBannerTextColor) {
        this.calendarDateBannerTextColor = calendarDateBannerTextColor;
    }

    public int getCalendarGridAreaSelected() {
        return calendarGridAreaSelected;
    }

    public void setCalendarGridAreaSelected(int calendarGridAreaSelected) {
        this.calendarGridAreaSelected = calendarGridAreaSelected;
    }

    public int getCalendarGridLineInnerHorizontalColor() {
        return calendarGridLineInnerHorizontalColor;
    }

    public void setCalendarGridLineInnerHorizontalColor(int calendarGridLineInnerHorizontalColor) {
        this.calendarGridLineInnerHorizontalColor = calendarGridLineInnerHorizontalColor;
    }

    public int getCalendarGridLineInnerVerticalColor() {
        return calendarGridLineInnerVerticalColor;
    }

    public void setCalendarGridLineInnerVerticalColor(int calendarGridLineInnerVerticalColor) {
        this.calendarGridLineInnerVerticalColor = calendarGridLineInnerVerticalColor;
    }

    public int getFutureBgColor() {
        return futureBgColor;
    }

    public void setFutureBgColor(int futureBgColor) {
        this.futureBgColor = futureBgColor;
    }

    public int getFutureBgColorRes() {
        return futureBgColorRes;
    }

    public void setFutureBgColorRes(int futureBgColorRes) {
        this.futureBgColorRes = futureBgColorRes;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getNewEventHintColor() {
        return newEventHintColor;
    }

    public void setNewEventHintColor(int newEventHintColor) {
        this.newEventHintColor = newEventHintColor;
    }

    public int getCalendarHourLabelColor() {
        return calendarHourLabelColor;
    }

    public void setCalendarHourLabelColor(int calendarHourLabelColor) {
        this.calendarHourLabelColor = calendarHourLabelColor;
    }
}

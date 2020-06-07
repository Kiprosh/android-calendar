package com.android.calendar.helpers;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.calendar.DynamicTheme;

public class MonthFieldColorHelper implements Parcelable {
    private int monthWeekNumColor;
    private int monthNumColor;
    private int monthNumOtherColor;
    private int monthNumTodayColor;
    private int monthEventColor;
    private int monthDeclinedEventColor;
    private int monthDeclinedExtrasColor;
    private int monthEventExtraColor;
    private int monthEventOtherColor;
    private int monthEventExtraOtherColor;
    private int monthBGTodayColor;
    private int monthBGFocusMonthColor;
    private int monthBGOtherColor;
    private int monthBGColor;
    private int daySeparatorInnerColor;
    private int todayAnimateColor;
    private int clickedDayColor;
    private int monthSaturdayColor;
    private int monthSundayColor;
    private int monthDayNameColor;

    public static final Creator<MonthFieldColorHelper> CREATOR = new Creator<MonthFieldColorHelper>() {
        @Override
        public MonthFieldColorHelper createFromParcel(Parcel in) {
            return new MonthFieldColorHelper(in);
        }

        @Override
        public MonthFieldColorHelper[] newArray(int size) {
            return new MonthFieldColorHelper[size];
        }
    };
    private int bGColor;
    private int selectedWeekBGColor;
    private int focusMonthColor;
    private int otherMonthColor;
    private int daySeparatorColor;
    private int todayOutlineColor;
    private int weekNumColor;

    public MonthFieldColorHelper(Context context) {
        DynamicTheme dynamicTheme = new DynamicTheme();
        monthWeekNumColor = dynamicTheme.getColor(context, "month_week_num_color");
        monthNumColor = dynamicTheme.getColor(context, "month_day_number");
        monthNumOtherColor = dynamicTheme.getColor(context, "month_day_number_other");
        monthNumTodayColor = dynamicTheme.getColor(context, "month_today_number");
        monthEventColor = dynamicTheme.getColor(context, "month_event_color");
        monthDeclinedEventColor = dynamicTheme.getColor(context, "agenda_item_declined_color");
        monthDeclinedExtrasColor = dynamicTheme.getColor(context, "agenda_item_where_declined_text_color");
        monthEventExtraColor = dynamicTheme.getColor(context, "month_event_extra_color");
        monthEventOtherColor = dynamicTheme.getColor(context, "month_event_other_color");
        monthEventExtraOtherColor = dynamicTheme.getColor(context, "month_event_extra_other_color");
        monthBGTodayColor = dynamicTheme.getColor(context, "month_today_bgcolor");
        monthBGFocusMonthColor = dynamicTheme.getColor(context, "month_focus_month_bgcolor");
        monthBGOtherColor = dynamicTheme.getColor(context, "month_other_bgcolor");
        monthBGColor = dynamicTheme.getColor(context, "month_bgcolor");
        daySeparatorInnerColor = dynamicTheme.getColor(context, "month_grid_lines");
        todayAnimateColor = dynamicTheme.getColor(context, "today_highlight_color");
        clickedDayColor = dynamicTheme.getColor(context, "day_clicked_background_color");

        bGColor = dynamicTheme.getColor(context, "month_bgcolor");
        selectedWeekBGColor = dynamicTheme.getColor(context, "month_selected_week_bgcolor");
        focusMonthColor = dynamicTheme.getColor(context, "month_mini_day_number");
        otherMonthColor = dynamicTheme.getColor(context, "month_other_month_day_number");
        daySeparatorColor = dynamicTheme.getColor(context, "month_grid_lines");
        todayOutlineColor = dynamicTheme.getColor(context, "mini_month_today_outline_color");
        weekNumColor = dynamicTheme.getColor(context, "month_week_num_color");
    }

    public MonthFieldColorHelper(Parcel in) {
        monthWeekNumColor = in.readInt();
        monthNumColor = in.readInt();
        monthNumOtherColor = in.readInt();
        monthNumTodayColor = in.readInt();
        monthEventColor = in.readInt();
        monthDeclinedEventColor = in.readInt();
        monthDeclinedExtrasColor = in.readInt();
        monthEventExtraColor = in.readInt();
        monthEventOtherColor = in.readInt();
        monthEventExtraOtherColor = in.readInt();
        monthBGTodayColor = in.readInt();
        monthBGFocusMonthColor = in.readInt();
        monthBGOtherColor = in.readInt();
        monthBGColor = in.readInt();
        daySeparatorInnerColor = in.readInt();
        todayAnimateColor = in.readInt();
        clickedDayColor = in.readInt();
        monthSaturdayColor = in.readInt();
        monthSundayColor = in.readInt();
        monthDayNameColor = in.readInt();
        bGColor = in.readInt();
        selectedWeekBGColor = in.readInt();
        focusMonthColor = in.readInt();
        otherMonthColor = in.readInt();
        daySeparatorColor = in.readInt();
        todayOutlineColor = in.readInt();
        weekNumColor = in.readInt();
    }

    public int getMonthSaturdayColor() {
        return monthSaturdayColor;
    }

    public void setMonthSaturdayColor(int monthSaturdayColor) {
        this.monthSaturdayColor = monthSaturdayColor;
    }

    public int getMonthSundayColor() {
        return monthSundayColor;
    }

    public void setMonthSundayColor(int monthSundayColor) {
        this.monthSundayColor = monthSundayColor;
    }

    public int getMonthDayNameColor() {
        return monthDayNameColor;
    }

    public void setMonthDayNameColor(int monthDayNameColor) {
        this.monthDayNameColor = monthDayNameColor;
    }

    public int getMonthWeekNumColor() {
        return monthWeekNumColor;
    }

    public void setMonthWeekNumColor(int monthWeekNumColor) {
        this.monthWeekNumColor = monthWeekNumColor;
    }

    public int getMonthNumColor() {
        return monthNumColor;
    }

    public void setMonthNumColor(int monthNumColor) {
        this.monthNumColor = monthNumColor;
    }

    public int getMonthNumOtherColor() {
        return monthNumOtherColor;
    }

    public void setMonthNumOtherColor(int monthNumOtherColor) {
        this.monthNumOtherColor = monthNumOtherColor;
    }

    public int getMonthNumTodayColor() {
        return monthNumTodayColor;
    }

    public void setMonthNumTodayColor(int monthNumTodayColor) {
        this.monthNumTodayColor = monthNumTodayColor;
    }

    public int getMonthEventColor() {
        return monthEventColor;
    }

    public void setMonthEventColor(int monthEventColor) {
        this.monthEventColor = monthEventColor;
    }

    public int getMonthDeclinedEventColor() {
        return monthDeclinedEventColor;
    }

    public void setMonthDeclinedEventColor(int monthDeclinedEventColor) {
        this.monthDeclinedEventColor = monthDeclinedEventColor;
    }

    public int getMonthDeclinedExtrasColor() {
        return monthDeclinedExtrasColor;
    }

    public void setMonthDeclinedExtrasColor(int monthDeclinedExtrasColor) {
        this.monthDeclinedExtrasColor = monthDeclinedExtrasColor;
    }

    public int getMonthEventExtraColor() {
        return monthEventExtraColor;
    }

    public void setMonthEventExtraColor(int monthEventExtraColor) {
        this.monthEventExtraColor = monthEventExtraColor;
    }

    public int getMonthEventOtherColor() {
        return monthEventOtherColor;
    }

    public void setMonthEventOtherColor(int monthEventOtherColor) {
        this.monthEventOtherColor = monthEventOtherColor;
    }

    public int getMonthEventExtraOtherColor() {
        return monthEventExtraOtherColor;
    }

    public void setMonthEventExtraOtherColor(int monthEventExtraOtherColor) {
        this.monthEventExtraOtherColor = monthEventExtraOtherColor;
    }

    public int getMonthBGTodayColor() {
        return monthBGTodayColor;
    }

    public void setMonthBGTodayColor(int monthBGTodayColor) {
        this.monthBGTodayColor = monthBGTodayColor;
    }

    public int getMonthBGFocusMonthColor() {
        return monthBGFocusMonthColor;
    }

    public void setMonthBGFocusMonthColor(int monthBGFocusMonthColor) {
        this.monthBGFocusMonthColor = monthBGFocusMonthColor;
    }

    public int getMonthBGOtherColor() {
        return monthBGOtherColor;
    }

    public void setMonthBGOtherColor(int monthBGOtherColor) {
        this.monthBGOtherColor = monthBGOtherColor;
    }

    public int getMonthBGColor() {
        return monthBGColor;
    }

    public void setMonthBGColor(int monthBGColor) {
        this.monthBGColor = monthBGColor;
    }

    public int getDaySeparatorInnerColor() {
        return daySeparatorInnerColor;
    }

    public void setDaySeparatorInnerColor(int daySeparatorInnerColor) {
        this.daySeparatorInnerColor = daySeparatorInnerColor;
    }

    public int getTodayAnimateColor() {
        return todayAnimateColor;
    }

    public void setTodayAnimateColor(int todayAnimateColor) {
        this.todayAnimateColor = todayAnimateColor;
    }

    public int getClickedDayColor() {
        return clickedDayColor;
    }

    public void setClickedDayColor(int clickedDayColor) {
        this.clickedDayColor = clickedDayColor;
    }

    public int getBgColor() {
        return bGColor;
    }

    public void setBgColor(int bGColor) {
        this.bGColor = bGColor;
    }

    public int getSelectedWeekBGColor() {
        return selectedWeekBGColor;
    }

    public void setSelectedWeekBGColor(int selectedWeekBGColor) {
        this.selectedWeekBGColor = selectedWeekBGColor;
    }

    public int getFocusMonthColor() {
        return focusMonthColor;
    }

    public void setFocusMonthColor(int focusMonthColor) {
        this.focusMonthColor = focusMonthColor;
    }

    public int getOtherMonthColor() {
        return otherMonthColor;
    }

    public void setOtherMonthColor(int otherMonthColor) {
        this.otherMonthColor = otherMonthColor;
    }

    public int getDaySeparatorColor() {
        return daySeparatorColor;
    }

    public void setDaySeparatorColor(int daySeparatorColor) {
        this.daySeparatorColor = daySeparatorColor;
    }

    public int getTodayOutlineColor() {
        return todayOutlineColor;
    }

    public void setTodayOutlineColor(int todayOutlineColor) {
        this.todayOutlineColor = todayOutlineColor;
    }

    public int getWeekNumColor() {
        return weekNumColor;
    }

    public void setWeekNumColor(int weekNumColor) {
        this.weekNumColor = weekNumColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(monthWeekNumColor);
        dest.writeInt(monthNumColor);
        dest.writeInt(monthNumOtherColor);
        dest.writeInt(monthNumTodayColor);
        dest.writeInt(monthEventColor);
        dest.writeInt(monthDeclinedEventColor);
        dest.writeInt(monthDeclinedExtrasColor);
        dest.writeInt(monthEventExtraColor);
        dest.writeInt(monthEventOtherColor);
        dest.writeInt(monthEventExtraOtherColor);
        dest.writeInt(monthBGTodayColor);
        dest.writeInt(monthBGFocusMonthColor);
        dest.writeInt(monthBGOtherColor);
        dest.writeInt(monthBGColor);
        dest.writeInt(daySeparatorInnerColor);
        dest.writeInt(todayAnimateColor);
        dest.writeInt(clickedDayColor);
        dest.writeInt(monthSaturdayColor);
        dest.writeInt(monthSundayColor);
        dest.writeInt(monthDayNameColor);
        dest.writeInt(bGColor);
        dest.writeInt(selectedWeekBGColor);
        dest.writeInt(focusMonthColor);
        dest.writeInt(otherMonthColor);
        dest.writeInt(daySeparatorColor);
        dest.writeInt(todayOutlineColor);
        dest.writeInt(weekNumColor);
    }
}

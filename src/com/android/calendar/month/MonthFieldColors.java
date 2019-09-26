package com.android.calendar.month;

import android.os.Parcel;
import android.os.Parcelable;

public class MonthFieldColors implements Parcelable {

    public static final Creator<MonthFieldColors> CREATOR = new Creator<MonthFieldColors>() {
        @Override
        public MonthFieldColors createFromParcel(Parcel in) {
            return new MonthFieldColors(in);
        }

        @Override
        public MonthFieldColors[] newArray(int size) {
            return new MonthFieldColors[size];
        }
    };
    int monthWeekNumColor;
    int monthNumColor;
    int monthNumOtherColor;
    int monthNumTodayColor;
    int monthEventColor;
    int monthDeclinedEventColor;
    int monthDeclinedExtrasColor;
    int monthEventExtraColor;
    int monthEventOtherColor;
    int monthEventExtraOtherColor;
    int monthBGTodayColor;
    int monthBGFocusMonthColor;
    int monthBGOtherColor;
    int monthBGColor;
    int daySeparatorInnerColor;
    int todayAnimateColor;
    int clickedDayColor;
    int monthSaturdayColor;
    int monthSundayColor;
    int monthDayNameColor;

    public MonthFieldColors() {
    }

    public MonthFieldColors(Parcel in) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(monthWeekNumColor);
        parcel.writeInt(monthNumColor);
        parcel.writeInt(monthNumOtherColor);
        parcel.writeInt(monthNumTodayColor);
        parcel.writeInt(monthEventColor);
        parcel.writeInt(monthDeclinedEventColor);
        parcel.writeInt(monthDeclinedExtrasColor);
        parcel.writeInt(monthEventExtraColor);
        parcel.writeInt(monthEventOtherColor);
        parcel.writeInt(monthEventExtraOtherColor);
        parcel.writeInt(monthBGTodayColor);
        parcel.writeInt(monthBGFocusMonthColor);
        parcel.writeInt(monthBGOtherColor);
        parcel.writeInt(monthBGColor);
        parcel.writeInt(daySeparatorInnerColor);
        parcel.writeInt(todayAnimateColor);
        parcel.writeInt(clickedDayColor);
        parcel.writeInt(monthSaturdayColor);
        parcel.writeInt(monthSundayColor);
        parcel.writeInt(monthDayNameColor);
    }
}

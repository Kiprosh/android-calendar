package com.android.calendar.helper;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.calendar.DynamicTheme;

public class DayFieldColorHelper implements Parcelable {
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
    private int bGColor;
    private int selectedWeekBGColor;
    private int focusMonthColor;
    private int otherMonthColor;
    private int daySeparatorColor;
    private int todayOutlineColor;
    private int weekNumColor;

    public DayFieldColorHelper(Context context) {
        DynamicTheme dynamicTheme = new DynamicTheme();
        bGColor = dynamicTheme.getColor(context, "month_bgcolor");
        selectedWeekBGColor = dynamicTheme.getColor(context, "month_selected_week_bgcolor");
        focusMonthColor = dynamicTheme.getColor(context, "month_mini_day_number");
        otherMonthColor = dynamicTheme.getColor(context, "month_other_month_day_number");
        daySeparatorColor = dynamicTheme.getColor(context, "month_grid_lines");
        todayOutlineColor = dynamicTheme.getColor(context, "mini_month_today_outline_color");
        weekNumColor = dynamicTheme.getColor(context, "month_week_num_color");
    }

    protected DayFieldColorHelper(Parcel in) {
        bGColor = in.readInt();
        selectedWeekBGColor = in.readInt();
        focusMonthColor = in.readInt();
        otherMonthColor = in.readInt();
        daySeparatorColor = in.readInt();
        todayOutlineColor = in.readInt();
        weekNumColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bGColor);
        dest.writeInt(selectedWeekBGColor);
        dest.writeInt(focusMonthColor);
        dest.writeInt(otherMonthColor);
        dest.writeInt(daySeparatorColor);
        dest.writeInt(todayOutlineColor);
        dest.writeInt(weekNumColor);
    }

    @Override
    public int describeContents() {
        return 0;
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
}

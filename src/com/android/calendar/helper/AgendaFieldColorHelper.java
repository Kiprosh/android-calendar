package com.android.calendar.helper;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.calendar.DynamicTheme;

public class AgendaFieldColorHelper implements Parcelable {
    public static final Creator<AgendaFieldColorHelper> CREATOR = new Creator<AgendaFieldColorHelper>() {
        @Override
        public AgendaFieldColorHelper createFromParcel(Parcel in) {
            return new AgendaFieldColorHelper(in);
        }

        @Override
        public AgendaFieldColorHelper[] newArray(int size) {
            return new AgendaFieldColorHelper[size];
        }
    };
    private int declinedColor;
    private int standardColor;
    private int whereColor;
    private int whereDeclinedColor;

    public AgendaFieldColorHelper(Context context) {
        DynamicTheme theme = new DynamicTheme();
        declinedColor = theme.getColor(context, "agenda_item_declined_color");
        standardColor = theme.getColor(context, "agenda_item_standard_color");
        whereDeclinedColor = theme.getColor(context, "agenda_item_where_declined_text_color");
        whereColor = theme.getColor(context, "agenda_item_where_text_color");
    }

    protected AgendaFieldColorHelper(Parcel in) {
        declinedColor = in.readInt();
        standardColor = in.readInt();
        whereColor = in.readInt();
        whereDeclinedColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(declinedColor);
        dest.writeInt(standardColor);
        dest.writeInt(whereColor);
        dest.writeInt(whereDeclinedColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getDeclinedColor() {
        return declinedColor;
    }

    public void setDeclinedColor(int declinedColor) {
        this.declinedColor = declinedColor;
    }

    public int getStandardColor() {
        return standardColor;
    }

    public void setStandardColor(int standardColor) {
        this.standardColor = standardColor;
    }

    public int getWhereColor() {
        return whereColor;
    }

    public void setWhereColor(int whereColor) {
        this.whereColor = whereColor;
    }

    public int getWhereDeclinedColor() {
        return whereDeclinedColor;
    }

    public void setWhereDeclinedColor(int whereDeclinedColor) {
        this.whereDeclinedColor = whereDeclinedColor;
    }
}

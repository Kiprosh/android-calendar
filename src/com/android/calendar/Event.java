/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract.Attendees;
import android.support.annotation.NonNull;

public class Event implements Cloneable, Parcelable, Comparable {

    private static final String TAG = "CalEvent";
    private static final boolean PROFILE = false;

    /**
     * The sort order is:
     * 1) events with an earlier start (begin for normal events, startday for allday)
     * 2) events with a later end (end for normal events, endday for allday)
     * 3) the title (unnecessary, but nice)
     * <p>
     * The start and end day is sorted first so that all day events are
     * sorted correctly with respect to events that are >24 hours (and
     * therefore show up in the allday area).
     */
    private static final String DISPLAY_AS_ALLDAY = "dispAllday";


    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    public long id;
    public int color;
    public String title;
    public CharSequence location;
    public boolean allDay;
    public String organizer;
    public boolean guestsCanModify;

    public int startDay;       // start Julian day
    public int endDay;         // end Julian day
    public int startTime;      // Start and end time are in minutes since midnight
    public int endTime;

    public long startMillis;   // UTC milliseconds since the epoch
    public long endMillis;     // UTC milliseconds since the epoch
    public boolean hasAlarm;
    public boolean isRepeating;
    public int selfAttendeeStatus;
    // The coordinates of the event rectangle drawn on the screen.
    public float left;
    public float right;
    public float top;
    public float bottom;
    // These 4 fields are used for navigating among events within the selected
    // hour in the Day and Week view.
    public Event nextRight;
    public Event nextLeft;
    public Event nextUp;
    public Event nextDown;
    public String aggregateId;
    public int mColumn;
    public int mMaxColumns;

    protected Event(Parcel in) {
        aggregateId = in.readString();
        id = in.readLong();
        color = in.readInt();
        allDay = in.readByte() != 0;
        organizer = in.readString();
        guestsCanModify = in.readByte() != 0;
        startDay = in.readInt();
        endDay = in.readInt();
        startTime = in.readInt();
        endTime = in.readInt();
        startMillis = in.readLong();
        endMillis = in.readLong();
        hasAlarm = in.readByte() != 0;
        isRepeating = in.readByte() != 0;
        selfAttendeeStatus = in.readInt();
        nextRight = in.readParcelable(Event.class.getClassLoader());
        nextLeft = in.readParcelable(Event.class.getClassLoader());
        nextUp = in.readParcelable(Event.class.getClassLoader());
        nextDown = in.readParcelable(Event.class.getClassLoader());
    }

    public static final Event newInstance() {
        Event e = new Event();
        e.id = 0;
        e.title = null;
        e.color = 0;
        e.location = null;
        e.allDay = true;
        e.startDay = 0;
        e.endDay = 0;
        e.startTime = 0;
        e.endTime = 0;
        e.startMillis = 0;
        e.endMillis = 0;
        e.hasAlarm = false;
        e.isRepeating = false;
        e.selfAttendeeStatus = Attendees.ATTENDEE_STATUS_NONE;
        return e;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Event() {
    }

     /*eventList.add(new Event(1059, -5242874, "Single session 1", "", true, "kavitamp19@googlemail.com", false
                                     , 2458729, 2458729, 990, 1050, 0, 0, false, false, 0, 0.0f, 0.0f,
                                     0.0f, 0.0f, null, null, null, null, 0, 0));*/


    public Event(long id, int color, String title, CharSequence location, boolean allDay, String organizer, boolean guestsCanModify, int startDay, int endDay, int startTime, int endTime, long startMillis, long endMillis, boolean hasAlarm, boolean isRepeating) {
        this.id = id;
        this.color = color;
        this.title = title;
        this.location = location;
        this.allDay = allDay;
        this.organizer = organizer;
        this.guestsCanModify = guestsCanModify;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.hasAlarm = hasAlarm;
        this.isRepeating = isRepeating;
    }

    public Event(long id, int color, String title, CharSequence location, boolean allDay, String organizer, boolean guestsCanModify, int startDay, int endDay, int startTime, int endTime, long startMillis, long endMillis, boolean hasAlarm, boolean isRepeating, int selfAttendeeStatus, float left, float right, float top, float bottom, Event nextRight, Event nextLeft, Event nextUp, Event nextDown, int mColumn, int mMaxColumns) {
        this.id = id;
        this.color = color;
        this.title = title;
        this.location = location;
        this.allDay = allDay;
        this.organizer = organizer;
        this.guestsCanModify = guestsCanModify;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.hasAlarm = hasAlarm;
        this.isRepeating = isRepeating;
        this.selfAttendeeStatus = selfAttendeeStatus;
        this.nextRight = nextRight;
        this.nextLeft = nextLeft;
        this.nextUp = nextUp;
        this.nextDown = nextDown;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    @Override
    public final Object clone() throws CloneNotSupportedException {
        super.clone();
        Event e = new Event();

        e.title = title;
        e.color = color;
        e.location = location;
        e.allDay = allDay;
        e.startDay = startDay;
        e.endDay = endDay;
        e.startTime = startTime;
        e.endTime = endTime;
        e.startMillis = startMillis;
        e.endMillis = endMillis;
        e.hasAlarm = hasAlarm;
        e.isRepeating = isRepeating;
        e.selfAttendeeStatus = selfAttendeeStatus;
        e.organizer = organizer;
        e.guestsCanModify = guestsCanModify;

        return e;
    }

    public final void copyTo(Event dest) {
        dest.id = id;
        dest.title = title;
        dest.color = color;
        dest.location = location;
        dest.allDay = allDay;
        dest.startDay = startDay;
        dest.endDay = endDay;
        dest.startTime = startTime;
        dest.endTime = endTime;
        dest.startMillis = startMillis;
        dest.endMillis = endMillis;
        dest.hasAlarm = hasAlarm;
        dest.isRepeating = isRepeating;
        dest.selfAttendeeStatus = selfAttendeeStatus;
        dest.organizer = organizer;
        dest.guestsCanModify = guestsCanModify;
    }

    public final boolean intersects(int julianDay, int startMinute,
                                    int endMinute) {
        if (endDay < julianDay) {
            return false;
        }

        if (startDay > julianDay) {
            return false;
        }

        if (endDay == julianDay) {
            if (endTime < startMinute) {
                return false;
            }
            // An event that ends at the start minute should not be considered
            // as intersecting the given time span, but don't exclude
            // zero-length (or very short) events.
            if (endTime == startMinute
                    && (startTime != endTime || startDay != endDay)) {
                return false;
            }
        }

        if (startDay == julianDay && startTime > endMinute) {
            return false;
        }

        return true;
    }


    public int getColumn() {
        return mColumn;
    }

    public void setColumn(int column) {
        mColumn = column;
    }

    public int getMaxColumns() {
        return mMaxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        mMaxColumns = maxColumns;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis;
    }

    public boolean drawAsAllday() {
        // Use >= so we'll pick up Exchange allday events
        return false;//|| endMillis - startMillis >= DateUtils.DAY_IN_MILLIS;
    }

    @Override
    public String toString() {
        return "Event{" +
                "aggregateId='" + aggregateId + '\'' +
                ", id=" + id +
                ", color=" + color +
                ", title=" + title +
                ", location=" + location +
                ", allDay=" + allDay +
                ", organizer='" + organizer + '\'' +
                ", guestsCanModify=" + guestsCanModify +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startMillis=" + startMillis +
                ", endMillis=" + endMillis +
                ", hasAlarm=" + hasAlarm +
                ", isRepeating=" + isRepeating +
                ", selfAttendeeStatus=" + selfAttendeeStatus +
                ", left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
                ", nextRight=" + nextRight +
                ", nextLeft=" + nextLeft +
                ", nextUp=" + nextUp +
                ", nextDown=" + nextDown +
                ", mColumn=" + mColumn +
                ", mMaxColumns=" + mMaxColumns +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(aggregateId);
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeInt(color);
        parcel.writeByte((byte) (allDay ? 1 : 0));
        parcel.writeString(organizer);
        parcel.writeByte((byte) (guestsCanModify ? 1 : 0));
        parcel.writeInt(startDay);
        parcel.writeInt(endDay);
        parcel.writeInt(startTime);
        parcel.writeInt(endTime);
        parcel.writeLong(startMillis);
        parcel.writeLong(endMillis);
        parcel.writeByte((byte) (hasAlarm ? 1 : 0));
        parcel.writeByte((byte) (isRepeating ? 1 : 0));
        parcel.writeInt(selfAttendeeStatus);
        parcel.writeParcelable(nextRight, i);
        parcel.writeParcelable(nextLeft, i);
        parcel.writeParcelable(nextUp, i);
        parcel.writeParcelable(nextDown, i);
    }


    @Override
    public int compareTo(@NonNull Object o) {
        if (this.getStartMillis() > ((Event) o).getStartMillis()) {
            return 1;
        }
        return 0;
    }
}
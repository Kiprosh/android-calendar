/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.calendar.month;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarListeners;
import com.android.calendar.DynamicTheme;
import com.android.calendar.Event;
import com.android.calendar.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ws.xsoh.etar.R;

public class MonthByWeekFragment extends SimpleDayPickerFragment implements CalendarController.EventHandler, MonthByWeekAdapter.CallbackForDayView {
    private static final String TAG = "MonthFragment";
    private static final String TAG_EVENT_DIALOG = "event_dialog";
    // Selection and selection args for adding event queries
    private static final String WHERE_CALENDARS_VISIBLE = Calendars.VISIBLE + "=1";
    private static final String INSTANCES_SORT_ORDER = Instances.START_DAY + ","
            + Instances.START_MINUTE + "," + Instances.TITLE;
    private static final int WEEKS_BUFFER = 1;
    // How long to wait after scroll stops before starting the loader
    // Using scroll duration because scroll state changes don't update
    // correctly when a scroll is triggered programmatically.
    private static final int LOADER_DELAY = 200;
    // The minimum time between requeries of the data if the db is
    // changing
    private static final int LOADER_THROTTLE_DELAY = 500;
    private static final int HANDLER_KEY = 0;
    protected static boolean mShowDetailsInMonth = false;
    private final Time mDesiredDay = new Time();
    static MonthFieldColors monthFieldColors;
    protected float mMinimumTwoMonthFlingVelocity;
    protected boolean mIsMiniMonth;
    protected boolean mHideDeclined;
    protected int mFirstLoadedJulianDay;
    protected int mLastLoadedJulianDay;
    private CursorLoader mLoader;
    private Uri mEventUri;
    private volatile boolean mShouldLoad = true;
    private boolean mUserScrolled = false;
    private int mEventsLoadingDelay;
    private boolean mShowCalendarControls;
    private boolean mIsDetached;
    static ArrayList<Event> events;
    static long initialTime;
    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            String tz = Utils.getTimeZone(mContext, mTZUpdater);
            mSelectedDay.timezone = tz;
            mSelectedDay.normalize(true);
            mTempTime.timezone = tz;
            mFirstDayOfMonth.timezone = tz;
            mFirstDayOfMonth.normalize(true);
            mFirstVisibleDay.timezone = tz;
            mFirstVisibleDay.normalize(true);
            if (mAdapter != null) {
                mAdapter.refresh();
            }
        }
    };
    private final String DATE_FORMAT_FOR_DAY_VIEW = "EEEE, dd MMMM yyyy";
    CalendarController controller;
    boolean isTitleUpdated = false;
    boolean isCalledAfterScroll;
    long updatedTimeInMilliseconds;

    public MonthByWeekFragment() {
        super(null, System.currentTimeMillis());
    }

    public MonthByWeekFragment(ArrayList<Event> eventList, MonthFieldColors monthFieldColors, long initialTime, boolean isMiniMonth) {
        super(monthFieldColors, initialTime);
        this.events = eventList;
        this.initialTime = initialTime;
        mIsMiniMonth = isMiniMonth;
        this.monthFieldColors = monthFieldColors;
    }

    /**
     * Updates the uri used by the loader according to the current position of
     * the listview.
     *
     * @return The new Uri to use
     */
    private Uri updateUri() {
        SimpleWeekView child = (SimpleWeekView) mListView.getChildAt(0);
        if (child != null) {
            int julianDay = child.getFirstJulianDay();
            mFirstLoadedJulianDay = julianDay;
        }
        // -1 to ensure we get all day events from any time zone
        mTempTime.setJulianDay(mFirstLoadedJulianDay - 1);
        long start = mTempTime.toMillis(true);
        mLastLoadedJulianDay = mFirstLoadedJulianDay + (mNumWeeks + 2 * WEEKS_BUFFER) * 7;
        // +1 to ensure we get all day events from any time zone
        mTempTime.setJulianDay(mLastLoadedJulianDay + 1);
        long end = mTempTime.toMillis(true);

        // Create a new uri with the updated times
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start);
        ContentUris.appendId(builder, end);
        return builder.build();
    }

    // Extract range of julian days from URI
    private void updateLoadedDays() {
        List<String> pathSegments = mEventUri.getPathSegments();
        int size = pathSegments.size();
        if (size <= 2) {
            return;
        }
        long first = Long.parseLong(pathSegments.get(size - 2));
        long last = Long.parseLong(pathSegments.get(size - 1));
        mTempTime.set(first);
        mFirstLoadedJulianDay = Time.getJulianDay(first, mTempTime.gmtoff);
        mTempTime.set(last);
        mLastLoadedJulianDay = Time.getJulianDay(last, mTempTime.gmtoff);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTZUpdater.run();
        if (mAdapter != null) {
            mAdapter.setSelectedDay(mSelectedDay);
        }
        mIsDetached = false;

        ViewConfiguration viewConfig = ViewConfiguration.get(activity);
        mMinimumTwoMonthFlingVelocity = viewConfig.getScaledMaximumFlingVelocity() / 2;
        Resources res = activity.getResources();
        mShowCalendarControls = false;
        // Synchronized the loading time of the month's events with the animation of the
        // calendar controls.
        if (mShowCalendarControls) {
            mEventsLoadingDelay = res.getInteger(R.integer.calendar_controls_animation_time);
        }
        mShowDetailsInMonth = res.getBoolean(R.bool.show_details_in_month);
    }

    @Override
    public void onDetach() {
        mIsDetached = true;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("eventListMonthFragment", events);
        outState.putParcelable("monthFieldColors", monthFieldColors);

    }

    @Override
    protected void setUpAdapter() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);

        HashMap<String, Integer> weekParams = new HashMap<String, Integer>();
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_NUM_WEEKS, mNumWeeks);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_SHOW_WEEK, mShowWeekNumber ? 1 : 0);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_WEEK_START, mFirstDayOfWeek);
        weekParams.put("mini_month", mIsMiniMonth ? 1 : 0);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_JULIAN_DAY,
                Time.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff));
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_DAYS_PER_WEEK, mDaysPerWeek);
        if (mAdapter == null) {
            mAdapter = new MonthByWeekAdapter(monthFieldColors, this, getActivity(), weekParams);
            mAdapter.registerDataSetObserver(mObserver);
        } else {
            mAdapter.updateParams(weekParams);
        }
        mAdapter.notifyDataSetChanged();
    }

    String getFormattedDate(long timeMili) {
        Date date = new Date();
        date.setTime(timeMili);
        return new SimpleDateFormat(DATE_FORMAT_FOR_DAY_VIEW).format(date);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        isCalledAfterScroll = true;
        if (savedInstanceState != null) {
            events = savedInstanceState.getParcelableArrayList("eventListMonthFragment");
            monthFieldColors = savedInstanceState.getParcelable("monthFieldColors");
        }

        v = inflater.inflate(R.layout.full_month_by_week, container, false);
        mDayNamesHeader = (ViewGroup) v.findViewById(R.id.day_names);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setSelector(new StateListDrawable());
        if (!mIsMiniMonth) {
            int backgroundColor;
            if (monthFieldColors != null) {
                backgroundColor = monthFieldColors.getMonthBGOtherColor() != 0 ? monthFieldColors.getMonthBGOtherColor()
                        : new DynamicTheme().getColor(getActivity(), "month_bgcolor");
            } else {
                backgroundColor = new DynamicTheme().getColor(getActivity(), "month_bgcolor");
            }
            mListView.setBackgroundColor(backgroundColor);
        }
        mAdapter.setListView(mListView);
    }

    @Override
    protected void setUpHeader() {
        if (mIsMiniMonth) {
            super.setUpHeader();
            return;
        }

        mDayLabels = new String[7];
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            mDayLabels[i - Calendar.SUNDAY] = DateUtils.getDayOfWeekString(i,
                    DateUtils.LENGTH_MEDIUM);
        }
    }


    public void loadData(ArrayList<Event> events) {
        this.events = events;

        mFirstLoadedJulianDay =
                Time.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff)
                        - (mNumWeeks * 7 / 2);
        mEventUri = updateUri();
        /*events.add(new Event(1058, getResources().getColor(R.color.colorGreenPrimary), "Feb Session", "", true, "kavitamp19@googlemail.com", false
                , 2458520, 2458520, 990, 1050, 1549348200000l, 1549326600000l, false, false));*/

        ((MonthByWeekAdapter) mAdapter).setEvents(mFirstLoadedJulianDay,
                mLastLoadedJulianDay - mFirstLoadedJulianDay + 1, events);
    }

    @Override
    public void doResumeUpdates() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);
        boolean prevHideDeclined = mHideDeclined;
        mHideDeclined = Utils.getHideDeclinedEvents(mContext);
        mDaysPerWeek = Utils.getMDaysPerWeek(mContext);
        //updateHeader();
        mAdapter.setSelectedDay(mSelectedDay);
        mTZUpdater.run();
        mTodayUpdater.run();
        goTo(mSelectedDay.toMillis(true), false, true, false);
    }

    @Override
    public void eventsChanged() {
        // TODO remove this after b/3387924 is resolved
        if (mLoader != null) {
            mLoader.forceLoad();
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return CalendarController.EventType.GO_TO | CalendarController.EventType.EVENTS_CHANGED | CalendarController.EventType.UPDATE_TITLE;
    }

    @Override
    public void handleEvent(CalendarController.EventInfo event) {
        if (event.eventType == CalendarController.EventType.GO_TO) {
            boolean animate = true;
            if (mDaysPerWeek * mNumWeeks * 2 < Math.abs(
                    Time.getJulianDay(event.selectedTime.toMillis(true), event.selectedTime.gmtoff)
                    - Time.getJulianDay(mFirstVisibleDay.toMillis(true), mFirstVisibleDay.gmtoff)
                    - mDaysPerWeek * mNumWeeks / 2)) {
                animate = false;
            }
            mDesiredDay.set(event.selectedTime);
            mDesiredDay.normalize(true);
            boolean animateToday = (event.extraLong & CalendarController.EXTRA_GOTO_TODAY) != 0;
            boolean delayAnimation = goTo(event.selectedTime.toMillis(true), animate, true, false);
            if (animateToday) {
                // If we need to flash today start the animation after any
                // movement from listView has ended.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MonthByWeekAdapter) mAdapter).animateToday();
                        mAdapter.notifyDataSetChanged();
                    }
                }, delayAnimation ? GOTO_SCROLL_DURATION : 0);
            }
        } else if (event.eventType == CalendarController.EventType.UPDATE_TITLE) {
            updatedTimeInMilliseconds = controller.getTime();
            isTitleUpdated = true;
            CalendarListeners listener = getParentFragmentObject();
            if (listener != null) {
                listener.updateToolbarTitleOnScroll(updatedTimeInMilliseconds);
            }
        }
    }

    @Override
    protected void setMonthDisplayed(Time time, boolean updateHighlight) {
        super.setMonthDisplayed(time, updateHighlight);
        if (!mIsMiniMonth) {
            boolean useSelected = false;
            if (time.year == mDesiredDay.year && time.month == mDesiredDay.month) {
                mSelectedDay.set(mDesiredDay);
                mAdapter.setSelectedDay(mDesiredDay);
                useSelected = true;
            } else {
                mSelectedDay.set(time);
                mAdapter.setSelectedDay(time);
            }
            controller = CalendarController.getInstance(mContext);
            if (mSelectedDay.minute >= 30) {
                mSelectedDay.minute = 30;
            } else {
                mSelectedDay.minute = 0;
            }
            long newTime = mSelectedDay.normalize(true);
            if (newTime != controller.getTime() && mUserScrolled) {
                long offset = useSelected ? 0 : DateUtils.WEEK_IN_MILLIS * mNumWeeks / 3;
                controller.setTime(newTime + offset);
            }
            long offsetMonthName = useSelected ? 0 : DateUtils.WEEK_IN_MILLIS * mNumWeeks / 3;
            long timeVal = newTime + offsetMonthName;

            controller.deregisterEventHandler(HANDLER_KEY);
            controller.registerFirstEventHandler(HANDLER_KEY, this);
            controller.sendEvent(this, CalendarController.EventType.UPDATE_TITLE, time, time, time, -1,
                    CalendarController.ViewType.CURRENT, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY
                            | DateUtils.FORMAT_SHOW_YEAR, null, null);
        }
    }

    public void updateMonth(ArrayList<Event> eventArrayList, Calendar calendar, Context context) {
        this.mContext = context;
        this.events = eventArrayList;
        initialTime = calendar.getTimeInMillis();
        controller = CalendarController.getInstance(mContext);
        String tz = Utils.getTimeZone(mContext, mTZUpdater);

        mSelectedDay.set(calendar.getTimeInMillis());
        long extras = CalendarController.EXTRA_GOTO_TIME | CalendarController.EXTRA_GOTO_DATE;
        controller.sendEvent(this, CalendarController.EventType.GO_TO, mSelectedDay,
                null, mSelectedDay, -1, CalendarController.ViewType.CURRENT, extras, null, null);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            mShouldLoad = false;
            mDesiredDay.setToNow();
        } else {
            mShouldLoad = true;
            loadData(events);
            if (isTitleUpdated && !isCalledAfterScroll) {
                // call API loading
                CalendarListeners listener = getParentFragmentObject();
                if (listener != null) {
                    listener.callAPIForEvents(updatedTimeInMilliseconds);
                }
            }
            isCalledAfterScroll = false;
            isTitleUpdated = false;
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            mUserScrolled = true;
        }
        mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    private CalendarListeners getParentFragmentObject() {
        CalendarListeners parentFragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) {
            return null;
        } else {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment instanceof CalendarListeners) {
                    parentFragment = (CalendarListeners) fragment;
                    break;
                }
            }
        }
        return parentFragment;
    }
    @Override
    public void callDayFragment(Time day) {
        CalendarListeners listener = getParentFragmentObject();
        if (listener != null) {
            listener.callOtherCalendarViews(day);
        }
    }
}

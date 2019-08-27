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
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.android.calendar.CalendarController;
import com.android.calendar.DayFragment;
import com.android.calendar.DynamicTheme;
import com.android.calendar.Event;
import com.android.calendar.Utils;

import java.util.ArrayList;
import java.util.Calendar;
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
    protected static boolean mShowDetailsInMonth = false;
    private final Time mDesiredDay = new Time();
    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            String tz = "Asia/Calcutta";
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
    private Handler mEventDialogHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final FragmentManager manager = getFragmentManager();
        }
    };


    public MonthByWeekFragment() {
        this(System.currentTimeMillis(), true);
    }

    public MonthByWeekFragment(long initialTime, boolean isMiniMonth) {
        super(initialTime);
        mIsMiniMonth = isMiniMonth;
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
            mAdapter = new MonthByWeekAdapter(this, getActivity(), weekParams, mEventDialogHandler);
            mAdapter.registerDataSetObserver(mObserver);
        } else {
            mAdapter.updateParams(weekParams);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.full_month_by_week, container, false);
        mDayNamesHeader = (ViewGroup) v.findViewById(R.id.day_names);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setSelector(new StateListDrawable());
        if (!mIsMiniMonth) {
            mListView.setBackgroundColor(new DynamicTheme().getColor(getActivity(), "month_bgcolor"));
        }
        mAdapter.setListView(mListView);
    }

    public void setMonthBackgroundColor(int colorId) {
        mListView.setBackgroundColor(getResources().getColor(colorId));
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
                    DateUtils.LENGTH_MEDIUM).toUpperCase();
        }
    }


    void loadingData() {
        mFirstLoadedJulianDay =
                Time.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff)
                        - (mNumWeeks * 7 / 2);
        mEventUri = updateUri();

        ArrayList<Event> events = new ArrayList<Event>();
        events.add(new Event(1058, getResources().getColor(R.color.colorGreenPrimary), "Feb Session", "", true, "kavitamp19@googlemail.com", false
                , 2458520, 2458520, 990, 1050, 1549348200000l, 1549326600000l, false, false));

        events.add(new Event(1078, -9522247, "Multi session", "", true, "kavitamp19@googlemail.com", false
                , 2458709, 2458717, 0, 1440, 1565654400000l, 1566432000000l, false, false));
        events.add(new Event(1059, -5242874, "Single session 1", "", true, "kavitamp19@googlemail.com", false
                , 2458711, 2458711, 990, 1050, 1565866800000l, 1565870400000l, false, false));

        events.add(new Event(1060, -5242874, "Single session 2", "", true, "kavitamp19@googlemail.com", false
                , 2458711, 2458711, 990, 1050, 1565866800000l, 1565870400000l, false, false));


        events.add(new Event(1061, -5242874, "Single session 3", "", true, "kavitamp19@googlemail.com", false
                , 2458711, 2458711, 990, 1050, 1565866800000l, 1565870400000l, false, false));


        events.add(new Event(1062, -5242874, "Single session 4", "", true, "kavitamp19@googlemail.com", false
                , 2458711, 2458711, 990, 1050, 1565866800000l, 1565870400000l, false, false));

        events.add(new Event(1063, -5242874, "Single session 5", "", true, "kavitamp19@googlemail.com", false
                , 2458711, 2458711, 990, 1050, 1565866800000l, 1565870400000l, false, false));

        events.add(new Event(1064, getResources().getColor(R.color.colorGreenPrimary), "Sept Event", "", true, "kavitamp19@googlemail.com", false
                , 2458739, 2458739, 990, 1050, 1565866800000l, 1565870400000l, false, false));

        events.add(new Event(1065, getResources().getColor(R.color.colorOrangeAccent), "Sept Event 2", "", true, "kavitamp19@googlemail.com", false
                , 2458739, 2458739, 990, 1050, 1565866800000l, 1565870400000l, false, false));

        events.add(new Event(1065, -5242874, "Sept Event 2", "", true, "kavitamp19@googlemail.com", false
                , 2458745, 2458745, 990, 1050, 1565866800000l, 1565870400000l, false, false));

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
        updateHeader();
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
        return CalendarController.EventType.GO_TO | CalendarController.EventType.EVENTS_CHANGED;
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
        } else if (event.eventType == CalendarController.EventType.EVENTS_CHANGED) {
            // eventsChanged();
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
            CalendarController controller = CalendarController.getInstance(mContext);
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
            controller.sendEvent(this, CalendarController.EventType.UPDATE_TITLE, time, time, time, -1,
                    CalendarController.ViewType.CURRENT, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY
                            | DateUtils.FORMAT_SHOW_YEAR, null, null);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            mShouldLoad = false;
            mDesiredDay.setToNow();
        } else {
            mShouldLoad = true;
            loadingData();
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            mUserScrolled = true;
        }

        mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    @Override
    public void callDayFragment(Time day) {
        Fragment fragment2 = new DayFragment(day.toMillis(false), 1);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_pane, fragment2);
        fragmentTransaction.commit();
    }
}

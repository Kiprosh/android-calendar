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
package com.android.calendar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ws.xsoh.etar.R;

/**
 * This is the base class for Day and Week Activities.
 */
public class DayFragment extends Fragment implements CalendarController.EventHandler, ViewFactory {
    protected static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";
    /**
     * The view id used for all the views we create. It's OK to have all child
     * views have the same ID. This ID is used to pick which view receives
     * focus when a view hierarchy is saved / restore
     */
    private static final int VIEW_ID = 1;
    protected ViewSwitcher mViewSwitcher;
    protected RelativeLayout rlDateDisplayDayView;
    LinearLayout llDividerVertical, llDateView;
    AppCompatTextView tvCurrentDate, tvCurrentDay;
    protected Animation mInAnimationForward;
    protected Animation mOutAnimationForward;
    protected Animation mInAnimationBackward;
    protected Animation mOutAnimationBackward;
    Time mSelectedDay = new Time();
    Time currentTime = new Time();
    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            if (!DayFragment.this.isAdded()) {
                return;
            }
            String tz = Utils.getTimeZone(getActivity(), mTZUpdater);
            mSelectedDay.timezone = tz;
            currentTime.timezone = tz;
            mSelectedDay.normalize(true);
        }
    };
    CalendarController mController;
    private int mNumDays;
    long savedTime = 0;
    ArrayList<Event> eventList;
    public DayFragment() {
    }

    private DayView dayView;

    public DayFragment(ArrayList<Event> eventList, long timeMillis, int numOfDays) {
        mNumDays = numOfDays;
        savedTime = timeMillis;
        this.eventList = eventList;
        currentTime.set(Calendar.getInstance().getTimeInMillis());
        if (timeMillis == 0) {
            mSelectedDay.setToNow();
        } else {
            mSelectedDay.set(timeMillis);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (icicle != null) {
            mNumDays = icicle.getInt("mNumDays");
            eventList = icicle.getParcelableArrayList("eventList");
            savedTime = icicle.getLong("savedTime");
        }

        Context context = getActivity();
        mInAnimationForward = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
        mOutAnimationForward = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        mInAnimationBackward = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
        mOutAnimationBackward = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);

    }

    @Override
    public View makeView() {
        mTZUpdater.run();
        mController = CalendarController.getInstance(getActivity());

        //set time to handle the orientation change
        if (savedTime > 0) {
            mSelectedDay.set(savedTime);
        }


        dayView = new DayView(savedTime, eventList, getActivity(), mController, mViewSwitcher, mNumDays,
                new CalendarListeners() {
            @Override
            public void onEventClick(Time tapEvent, Event clickedEvent) {
                CalendarListeners listener = getParentFragmentObject();
                if (listener != null) {
                    listener.onEventClick(tapEvent, clickedEvent);
                }
            }

            @Override
            public void onDayChange(Time selectedDay, Time selectedEndDay, boolean isDayView) {
                CalendarListeners calendarListeners = getParentFragmentObject();
                if (calendarListeners != null) {
                    calendarListeners.onDayChange(selectedDay, selectedEndDay, isDayView);
                }

                if (isDayView) {
                    updateDateOnDayView(selectedDay);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llDividerVertical.getLayoutParams();
                    params.width = (int) DayView.GRID_LINE_INNER_WIDTH;
                    params.setMargins((int) DayView.GRID_LINE_LEFT_MARGIN, 0, 0, 0);
                    llDividerVertical.setLayoutParams(params);

                    params = (RelativeLayout.LayoutParams) llDateView.getLayoutParams();
                    params.width = (int) DayView.GRID_LINE_LEFT_MARGIN;
                    llDateView.setLayoutParams(params);
                    rlDateDisplayDayView.setVisibility(View.VISIBLE);
                } else {
                    rlDateDisplayDayView.setVisibility(View.GONE);
                }
            }

                    @Override
                    public void onWeekViewHeaderDayClick(Time selectedDay) {
                        CalendarListeners weekDayListener = getParentFragmentObject();
                        if (weekDayListener != null) {
                            weekDayListener.onWeekViewHeaderDayClick(selectedDay);
                        }
                    }

                    @Override
                    public void callOtherCalendarViews(Time time) {

                    }

                    @Override
                    public void updateToolbarTitleOnScroll(long dateInMilliseconds) {

                    }

                    @Override
                    public void callAPIForEvents(long updatedTimeInMillis) {

                    }
                });
        dayView.setmIs24HourFormat(false);
        dayView.setEmptyCellClickEnable(false);
        dayView.setLongClickEnable(false);
        dayView.setId(VIEW_ID);
        dayView.setLayoutParams(new ViewSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSelectedDay.switchTimezone("US/Mountain");
        dayView.setSelected(mSelectedDay, true, false);
        return dayView;
    }

    Time setCurrentDayTime(Time currentTime) {
        currentTime.hour = 0;
        currentTime.minute = 0;
        currentTime.second = 0;
        return currentTime;
    }

    public void updateDateOnDayView(Time selectedTime) {
        Time currentTimeToCompare = setCurrentDayTime(currentTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedTime.toMillis(false));
        tvCurrentDate.setText("" + selectedTime.monthDay);

        selectedTime = setCurrentDayTime(selectedTime);

        if (currentTimeToCompare.toMillis(false) == selectedTime.toMillis(false)) {
            tvCurrentDate.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_current_date));
            tvCurrentDate.setTextColor(getResources().getColor(R.color.titleTextColor));
        } else {
            tvCurrentDate.setBackgroundDrawable(null);
            tvCurrentDate.setTextColor(getResources().getColor(R.color.calendar_hour_label));
        }
        tvCurrentDay.setText("" + dayView.mDayStrs[calendar.get(Calendar.DAY_OF_WEEK) - 1]);

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("View Created", mNumDays + " ");
    }

    public void setEvents(ArrayList<Event> eventLists, long selectedTimeTS) {
        eventList = eventLists;
        Collections.sort(eventLists, new Comparator<Event>() {
            @Override
            public int compare(Event previousEvent, Event currentEvent) {
                return Integer.compare(previousEvent.startDay, currentEvent.startDay);
            }
        });
        List<Event> events = dayView.getEvents();
        events.clear();
        events.addAll(eventLists);
        ((DayView) mViewSwitcher.getCurrentView()).setmEvents(eventLists);

        eventsChanged();
        savedTime = selectedTimeTS;

        if (selectedTimeTS > 0) {
            Time time = new Time();
            time.set(selectedTimeTS);
            ((DayView) mViewSwitcher.getCurrentView()).savedTime = savedTime;
            ((DayView) mViewSwitcher.getCurrentView()).setSelected(time, false, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.day_activity, null);
        rlDateDisplayDayView = v.findViewById(R.id.rl_date_display_day_view);
        tvCurrentDay = v.findViewById(R.id.tv_current_day);
        tvCurrentDate = v.findViewById(R.id.tv_current_date);
        llDividerVertical = v.findViewById(R.id.ll_divider_vertical);
        llDateView = v.findViewById(R.id.ll_date_view);
        //llDividerHorizontal = v.findViewById(R.id.ll_divider_horizontal);
        mViewSwitcher = v.findViewById(R.id.switcher);
        mViewSwitcher.setFactory(this);
        mViewSwitcher.getCurrentView().requestFocus();
        //((DayView) mViewSwitcher.getCurrentView()).updateTitle();
        int visibility = mNumDays == 1 ? View.VISIBLE : View.GONE;
        rlDateDisplayDayView.setVisibility(visibility);
        Time time = new Time();
        time.timezone = Utils.getTimeZone(getActivity(), mTZUpdater);
        time.set(CalendarController.savedTime);
        updateDateOnDayView(time);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTZUpdater.run();
        eventsChanged();
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.handleOnResume();
        view.restartCurrentTimeUpdates();
        view = (DayView) mViewSwitcher.getNextView();
        view.handleOnResume();
        view.restartCurrentTimeUpdates();

        if (eventList != null) {
            setEvents(eventList, CalendarController.savedTime);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        long time = getSelectedTimeInMillis();
        outState.putInt("mNumDays", mNumDays);
        outState.putParcelableArrayList("eventList", eventList);
        outState.putLong("savedTime", mSelectedDay.toMillis(false));
        if (time != -1) {
            outState.putLong(BUNDLE_KEY_RESTORE_TIME, time);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.cleanup();
        view = (DayView) mViewSwitcher.getNextView();
        view.cleanup();
        //mEventLoader.stopBackgroundThread();
        // Stop events cross-fade animation
        view.stopEventsAnimation();
        ((DayView) mViewSwitcher.getNextView()).stopEventsAnimation();
    }

    private void goTo(Time goToTime, boolean ignoreTime, boolean animateToday) {
        if (mViewSwitcher == null) {
            // The view hasn't been set yet. Just save the time and use it later.
            mSelectedDay.set(goToTime);
            return;
        }
        DayView currentView = (DayView) mViewSwitcher.getCurrentView();
        // How does goTo time compared to what's already displaying?
        int diff = currentView.compareToVisibleTimeRange(goToTime);
        if (diff == 0) {
            // In visible range. No need to switch view
            currentView.setSelected(goToTime, ignoreTime, animateToday);
        } else {
            // Figure out which way to animate
            if (diff > 0) {
                mViewSwitcher.setInAnimation(mInAnimationForward);
                mViewSwitcher.setOutAnimation(mOutAnimationForward);
            } else {
                mViewSwitcher.setInAnimation(mInAnimationBackward);
                mViewSwitcher.setOutAnimation(mOutAnimationBackward);
            }
            DayView next = (DayView) mViewSwitcher.getNextView();
            if (ignoreTime) {
                next.setFirstVisibleHour(currentView.getFirstVisibleHour());
            }
            next.setSelected(goToTime, ignoreTime, animateToday);
            next.reloadEvents();
            mViewSwitcher.showNext();
            next.requestFocus();
            next.updateTitle();
            next.restartCurrentTimeUpdates();
        }
    }

    /**
     * Returns the selected time in milliseconds. The milliseconds are measured
     * in UTC milliseconds from the epoch and uniquely specifies any selectable
     * time.
     *
     * @return the selected time in milliseconds
     */
    public long getSelectedTimeInMillis() {
        if (mViewSwitcher == null) {
            return -1;
        }
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        if (view == null) {
            return -1;
        }
        return view.getSelectedTimeInMillis();
    }

    public void eventsChanged() {
        if (mViewSwitcher == null) {
            return;
        }
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        view.clearCachedEvents();
        view.reloadEvents();
        view = (DayView) mViewSwitcher.getNextView();
        view.clearCachedEvents();
    }

    Event getSelectedEvent() {
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        return view.getSelectedEvent();
    }

    boolean isEventSelected() {
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        return view.isEventSelected();
    }

    Event getNewEvent() {
        DayView view = (DayView) mViewSwitcher.getCurrentView();
        return view.getNewEvent();
    }

    public DayView getNextView() {
        return (DayView) mViewSwitcher.getNextView();
    }

    public long getSupportedEventTypes() {
        return EventType.GO_TO | EventType.EVENTS_CHANGED;
    }

    public void handleEvent(EventInfo msg) {
        if (msg.eventType == EventType.GO_TO) {
            goTo(msg.selectedTime, (msg.extraLong & CalendarController.EXTRA_GOTO_DATE) != 0,
                    (msg.extraLong & CalendarController.EXTRA_GOTO_TODAY) != 0);
        } else if (msg.eventType == EventType.EVENTS_CHANGED) {
            eventsChanged();
        }
    }
}
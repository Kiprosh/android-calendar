package com.android.calendar;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.calendar.agenda.AgendaFragment;
import com.android.calendar.helpers.IntentKeys;
import com.android.calendar.helpers.MonthFieldColorHelper;
import com.android.calendar.month.MonthByWeekFragment;
import com.android.calendar.selectcalendars.SelectVisibleCalendarsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ws.xsoh.etar.R;

import static android.provider.CalendarContract.Attendees.ATTENDEE_STATUS;
import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

public class TestFragment extends AbstractCalendarActivity implements CalendarController.EventHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";
    private static final String BUNDLE_KEY_EVENT_ID = "key_event_id";
    private static final String BUNDLE_KEY_RESTORE_VIEW = "key_restore_view";
    private static final int HANDLER_KEY = 0;
    private static final int PERMISSIONS_REQUEST_WRITE_CALENDAR = 0;
    private static final String TAG = "AllInOneActivity";
    private static final boolean DEBUG = false;
    private static final String EVENT_INFO_FRAGMENT_TAG = "EventInfoFragment";
    private final DynamicTheme dynamicTheme = new DynamicTheme();
    MonthFieldColorHelper monthFieldColors;
    BroadcastReceiver mCalIntentReceiver;
    private CalendarController mController;
    // Create an observer so that we can update the views whenever a
    // Calendar event changes.
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            eventsChanged();
        }
    };
    private String mTimeZone;
    private QueryHandler mHandler;
    private final Runnable mHomeTimeUpdater = new Runnable() {
        @Override
        public void run() {
            mTimeZone = Utils.getTimeZone(TestFragment.this, mHomeTimeUpdater);
            TestFragment.this.invalidateOptionsMenu();
            Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater, mTimeZone);
        }
    };
    // runs every midnight/time changes and refreshes the today icon
    private final Runnable mTimeChangesUpdater = new Runnable() {
        @Override
        public void run() {
            mTimeZone = Utils.getTimeZone(TestFragment.this, mHomeTimeUpdater);
            TestFragment.this.invalidateOptionsMenu();
            Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater, mTimeZone);
        }
    };
    private boolean mShowCalendarControls;
    private boolean mShowEventInfoFullScreenAgenda;
    private boolean mShowEventInfoFullScreen;
    private int mWeekNum;
    private int mCalendarControlsAnimationTime;
    private int mControlsAnimateWidth;
    private int mControlsAnimateHeight;
    private long mViewEventId = -1;
    private long mIntentEventStartMillis = -1;
    private boolean mIntentAllDay = false;
    private long mIntentEventEndMillis = -1;
    private int mIntentAttendeeResponse = CalendarContract.Attendees.ATTENDEE_STATUS_NONE;
    private boolean mOnSaveInstanceStateCalled = false;
    private boolean mBackToPreviousView = false;
    private ContentResolver mContentResolver;
    private int mPreviousView;
    private int mCurrentView;
    private boolean mPaused = true;
    private boolean mUpdateOnResume = false;
    private boolean mHideControls = false;
    private boolean mShowSideViews = true;
    private boolean mShowWeekNum = false;

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        Log.d("shajsda", "onNewIntent().... TestFragment");
        if (DEBUG)
            Log.d(TAG, "New intent received " + intent.toString());
        // Don't change the date if we're just returning to the app's home
        if (Intent.ACTION_VIEW.equals(action)
                && !intent.getBooleanExtra(Utils.INTENT_KEY_HOME, false)) {
            long millis = parseViewAction(intent);
            if (millis == -1) {
                millis = Utils.timeFromIntentInMillis(intent);
            }
            if (millis != -1 && mViewEventId == -1 && mController != null) {
                Time time = new Time(mTimeZone);
                time.set(millis);
                time.normalize(true);
                mController.sendEvent(this, CalendarController.EventType.GO_TO, time, time, -1, CalendarController.ViewType.CURRENT);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("shajsda", "onResume().... TestFragment");
        dynamicTheme.onResume(this);

        // Must register as the first activity because this activity can modify
        // the list of event handlers in it's handle method. This affects who
        // the rest of the handlers the controller dispatches to are.
        mController.registerFirstEventHandler(HANDLER_KEY, this);
        mOnSaveInstanceStateCalled = false;

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            //If permission is not granted then just return.
            Log.d(TAG, "Manifest.permission.READ_CALENDAR is not granted");
            return;
        }

        mContentResolver.registerContentObserver(CalendarContract.Events.CONTENT_URI,
                true, mObserver);
        if (mUpdateOnResume) {
            initFragments(mController.getTime(), mController.getViewType(), null);
            mUpdateOnResume = false;
        }
        Time t = new Time(mTimeZone);
        t.set(mController.getTime());
        mController.sendEvent(this, CalendarController.EventType.UPDATE_TITLE, t, t, -1, CalendarController.ViewType.CURRENT,
                mController.getDateFlags(), null, null);

        mPaused = false;

        if (mViewEventId != -1 && mIntentEventStartMillis != -1 && mIntentEventEndMillis != -1) {
            long currentMillis = System.currentTimeMillis();
            long selectedTime = -1;
            if (currentMillis > mIntentEventStartMillis && currentMillis < mIntentEventEndMillis) {
                selectedTime = currentMillis;
            }
            mController.sendEventRelatedEventWithExtra(this, CalendarController.EventType.VIEW_EVENT, mViewEventId,
                    mIntentEventStartMillis, mIntentEventEndMillis, -1, -1,
                    CalendarController.EventInfo.buildViewExtraLong(mIntentAttendeeResponse, mIntentAllDay),
                    selectedTime);
            mViewEventId = -1;
            mIntentEventStartMillis = -1;
            mIntentEventEndMillis = -1;
            mIntentAllDay = false;
        }
        Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater, mTimeZone);
        // Make sure the today icon is up to date
        invalidateOptionsMenu();

        mCalIntentReceiver = Utils.setTimeChangesReceiver(this, mTimeChangesUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mController.deregisterEventHandler(HANDLER_KEY);
        mPaused = true;
        //mHomeTime.removeCallbacks(mHomeTimeUpdater);

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            //If permission is not granted then just return.
            Log.d(TAG, "Manifest.permission.WRITE_CALENDAR is not granted");
            return;
        }

        mContentResolver.unregisterContentObserver(mObserver);
        if (isFinishing()) {
            // Stop listening for changes that would require this to be refreshed
            SharedPreferences prefs = GeneralPreferences.getSharedPreferences(this);
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }
        // FRAG_TODO save highlighted days of the week;
        if (mController.getViewType() != CalendarController.ViewType.EDIT) {
            Utils.setDefaultView(this, mController.getViewType());
        }
        Utils.resetMidnightUpdater(mHandler, mTimeChangesUpdater);
        Utils.clearTimeChangesReceiver(this, mCalIntentReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mOnSaveInstanceStateCalled = true;
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_KEY_RESTORE_TIME, mController.getTime());
        outState.putInt(BUNDLE_KEY_RESTORE_VIEW, mCurrentView);
        if (mCurrentView == CalendarController.ViewType.EDIT) {
            outState.putLong(BUNDLE_KEY_EVENT_ID, mController.getEventId());
        } else if (mCurrentView == CalendarController.ViewType.AGENDA) {
            FragmentManager fm = getFragmentManager();
            Fragment f = fm.findFragmentById(R.id.main_pane);
            if (f instanceof AgendaFragment) {
                outState.putLong(BUNDLE_KEY_EVENT_ID, ((AgendaFragment) f).getLastShowEventId());
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(GeneralPreferences.KEY_WEEK_START_DAY) || key.equals(GeneralPreferences.KEY_DAYS_PER_WEEK)) {
            if (mPaused) {
                mUpdateOnResume = true;
            } else {
                initFragments(mController.getTime(), mController.getViewType(), null);
            }
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return CalendarController.EventType.GO_TO | CalendarController.EventType.VIEW_EVENT | CalendarController.EventType.UPDATE_TITLE;
    }

    @Override
    public void handleEvent(CalendarController.EventInfo event) {
        Log.d("shajsda", "handleEvent().... TestFragment");

        long displayTime = -1;
        if (event.eventType == CalendarController.EventType.GO_TO) {
            if ((event.extraLong & CalendarController.EXTRA_GOTO_BACK_TO_PREVIOUS) != 0) {
                mBackToPreviousView = true;
            } else if (event.viewType != mController.getPreviousViewType()
                    && event.viewType != CalendarController.ViewType.EDIT) {
                // Clear the flag is change to a different view type
                mBackToPreviousView = false;
            }
            Toast.makeText(getBaseContext(), "handleEvent GO_TO", Toast.LENGTH_SHORT).show();

            setMainPane(
                    null, R.id.main_pane, event.viewType, event.startTime.toMillis(false), false);

            if (mShowCalendarControls) {
                int animationSize = (false) ?
                        mControlsAnimateWidth : mControlsAnimateHeight;
                boolean noControlsView = event.viewType == CalendarController.ViewType.MONTH || event.viewType == CalendarController.ViewType.AGENDA;

                if (noControlsView || mHideControls) {
                    // hide minimonth and calendar frag
                    mShowSideViews = false;
                    if (!mHideControls) {
                        final ObjectAnimator slideAnimation = ObjectAnimator.ofInt(this,
                                "controlsOffset", 0, animationSize);
                        slideAnimation.setDuration(mCalendarControlsAnimationTime);
                        ObjectAnimator.setFrameDelay(0);
                        slideAnimation.start();
                    } else {/*
                        mMiniMonth.setVisibility(View.GONE);
                        mCalendarsList.setVisibility(View.GONE);
                        mMiniMonthContainer.setVisibility(View.GONE);*/
                    }
                } else {
                    // show minimonth and calendar frag
                    mShowSideViews = true;
                    /*mMiniMonth.setVisibility(View.VISIBLE);
                    mCalendarsList.setVisibility(View.VISIBLE);
                    mMiniMonthContainer.setVisibility(View.VISIBLE);*/
                    if (!mHideControls &&
                            (mController.getPreviousViewType() == CalendarController.ViewType.MONTH ||
                                    mController.getPreviousViewType() == CalendarController.ViewType.AGENDA)) {
                        final ObjectAnimator slideAnimation = ObjectAnimator.ofInt(this,
                                "controlsOffset", animationSize, 0);
                        slideAnimation.setDuration(mCalendarControlsAnimationTime);
                        ObjectAnimator.setFrameDelay(0);
                        slideAnimation.start();
                    }
                }
            }
            displayTime = event.selectedTime != null ? event.selectedTime.toMillis(true)
                    : event.startTime.toMillis(true);

        } else if (event.eventType == CalendarController.EventType.VIEW_EVENT) {

            // If in Agenda view and "show_event_details_with_agenda" is "true",
            // do not create the event info fragment here, it will be created by the Agenda
            // fragment
            Toast.makeText(getBaseContext(), "handleEvent VIEW_EVENT", Toast.LENGTH_SHORT).show();

            if (mCurrentView == CalendarController.ViewType.AGENDA) {
                if (event.startTime != null && event.endTime != null) {
                    // Event is all day , adjust the goto time to local time
                    if (event.isAllDay()) {
                        Utils.convertAlldayUtcToLocal(
                                event.startTime, event.startTime.toMillis(false), mTimeZone);
                        Utils.convertAlldayUtcToLocal(
                                event.endTime, event.endTime.toMillis(false), mTimeZone);
                    }
                    mController.sendEvent(this, CalendarController.EventType.GO_TO, event.startTime, event.endTime,
                            event.selectedTime, event.id, CalendarController.ViewType.AGENDA,
                            CalendarController.EXTRA_GOTO_TIME, null, null);
                } else if (event.selectedTime != null) {
                    mController.sendEvent(this, CalendarController.EventType.GO_TO, event.selectedTime,
                            event.selectedTime, event.id, CalendarController.ViewType.AGENDA);
                }
            } else {
                // TODO Fix the temp hack below: && mCurrentView !=
                // ViewType.AGENDA
                if (event.selectedTime != null && mCurrentView != CalendarController.ViewType.AGENDA) {
                    mController.sendEvent(this, CalendarController.EventType.GO_TO, event.selectedTime,
                            event.selectedTime, -1, CalendarController.ViewType.CURRENT);
                }
                int response = event.getResponse();
                if ((mCurrentView == CalendarController.ViewType.AGENDA && mShowEventInfoFullScreenAgenda) ||
                        ((mCurrentView == CalendarController.ViewType.DAY || (mCurrentView == CalendarController.ViewType.WEEK) ||
                                mCurrentView == CalendarController.ViewType.MONTH) && mShowEventInfoFullScreen)) {
                    // start event info as activity
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id);
                    intent.setData(eventUri);
                    intent.setClass(this, EventInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(EXTRA_EVENT_BEGIN_TIME, event.startTime.toMillis(false));
                    intent.putExtra(EXTRA_EVENT_END_TIME, event.endTime.toMillis(false));
                    intent.putExtra(ATTENDEE_STATUS, response);
                    startActivity(intent);
                } else {
                    // start event info as a dialog
                    EventInfoFragment fragment = new EventInfoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong(IntentKeys.KEY_EVENT_ID, event.id);
                    bundle.putLong(IntentKeys.KEY_START_TIME_MILLIS, event.startTime.toMillis(false));
                    bundle.putLong(IntentKeys.KEY_END_TIME_MILLIS, event.endTime.toMillis(false));
                    bundle.putInt(IntentKeys.KEY_ATTENDEE_RESPONSE, response);
                    bundle.putBoolean(IntentKeys.KEY_IS_DIALOG, true);
                    bundle.putInt(IntentKeys.KEY_WINDOW_STYLE, EventInfoFragment.DIALOG_WINDOW_STYLE);
                    bundle.putSerializable(IntentKeys.KEY_LIST_REMINDER_ENTRY, null);
                    fragment.setArguments(bundle);


                    fragment.setDialogParams(event.x, event.y, 50);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    // if we have an old popup replace it
                    Fragment fOld = fm.findFragmentByTag(EVENT_INFO_FRAGMENT_TAG);
                    if (fOld != null && fOld.isAdded()) {
                        ft.remove(fOld);
                    }
                    ft.add(fragment, EVENT_INFO_FRAGMENT_TAG);
                    ft.commit();
                }
            }
            displayTime = event.startTime.toMillis(true);
        } else if (event.eventType == CalendarController.EventType.UPDATE_TITLE) {
            setTitleInActionBar(event);
            /*if (!mIsTabletConfig) {
                refreshActionbarTitle(mController.getTime());
            }*/
        }
    }


    private void setTitleInActionBar(CalendarController.EventInfo event) {
        if (event.eventType != CalendarController.EventType.UPDATE_TITLE) {
            return;
        }

        final long start = event.startTime.toMillis(false /* use isDst */);
        final long end;
        if (event.endTime != null) {
            end = event.endTime.toMillis(false /* use isDst */);
        } else {
            end = start;
        }

        final String msg = Utils.formatDateRange(this, start, end, (int) event.extraLong);

        Toast.makeText(getBaseContext(), "handleEvent UPDATE Title-->" + msg, Toast.LENGTH_SHORT).show();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mController.getTime());
        Toast.makeText(getBaseContext(), "Title-->" + c.getTime(), Toast.LENGTH_SHORT).show();

        /*CharSequence oldDate = mDateRange.getText();
        mDateRange.setText(msg);
        updateSecondaryTitleFields(event.selectedTime != null ? event.selectedTime.toMillis(true)
                : start);
        if (!TextUtils.equals(oldDate, msg)) {
            mDateRange.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            if (mShowWeekNum && mWeekTextView != null) {
                mWeekTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }*/
    }

    @Override
    public void eventsChanged() {
        Log.d("shajsda", "eventsChanged()....");

        Toast.makeText(getBaseContext(), "eventsChanged", Toast.LENGTH_LONG).show();
        mController.sendEvent(this, CalendarController.EventType.EVENTS_CHANGED, null, null, -1, CalendarController.ViewType.CURRENT);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (Utils.getSharedPreference(this, OtherPreferences.KEY_OTHER_1, false)) {
            setTheme(R.style.CalendarTheme_WithActionBarWallpaper);
        }
        dynamicTheme.onCreate(this);

        // This needs to be created before setContentView
        mController = CalendarController.getInstance(this);

        // Check and ask for most needed permissions
        checkAppPermissions();

        // Get time from intent or icicle
        long timeMillis = -1;
        int viewType = -1;
        final Intent intent = getIntent();
        if (icicle != null) {
            timeMillis = icicle.getLong(BUNDLE_KEY_RESTORE_TIME);
            viewType = icicle.getInt(BUNDLE_KEY_RESTORE_VIEW, -1);
        } else {
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                // Open EventInfo later
                timeMillis = parseViewAction(intent);
            }

            if (timeMillis == -1) {
                timeMillis = Utils.timeFromIntentInMillis(intent);
            }
        }

        if (viewType == -1 || viewType > CalendarController.ViewType.MAX_VALUE) {
            viewType = Utils.getViewTypeFromIntentAndSharedPref(this);
        }
        mTimeZone = Utils.getTimeZone(this, mHomeTimeUpdater);
        Time t = new Time(mTimeZone);
        t.set(timeMillis);

        if (DEBUG) {
            if (icicle != null && intent != null) {
                Log.d(TAG, "both, icicle:" + icicle.toString() + "  intent:" + intent.toString());
            } else {
                Log.d(TAG, "not both, icicle:" + icicle + " intent:" + intent);
            }
        }
        setContentView(R.layout.activity_frame_layout);

        // Must register as the first activity because this activity can modify
        // the list of event handlers in it's handle method. This affects who
        // the rest of the handlers the controller dispatches to are.
        mController.registerFirstEventHandler(HANDLER_KEY, this);

        initFragments(timeMillis, CalendarController.ViewType.MONTH, icicle);

        // Listen for changes that would require this to be refreshed
        SharedPreferences prefs = GeneralPreferences.getSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        mContentResolver = getContentResolver();

        /*MonthByWeekFragment frag = new MonthByWeekFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(IntentKeys.KEY_TIME_IN_MILLIS, timeMillis);
        bundle.putBoolean(IntentKeys.KEY_IS_MINI_MONTH, false);
        bundle.putParcelable(IntentKeys.KEY_COLOR_HELPER, monthFieldColors);
        frag.setArguments(bundle);*/

    }

    private void initFragments(long timeMillis, int viewType, Bundle icicle) {
        Log.d("shajsda", "initFragments().... TestFragment");
        if (DEBUG) {
            Log.d(TAG, "Initializing to " + timeMillis + " for view " + viewType);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (mShowCalendarControls) {
            Fragment miniMonthFrag = new MonthByWeekFragment();
            ArrayList<Event> events = new ArrayList<Event>();
            events.add(new Event(1058, getResources().getColor(R.color.colorRedPrimaryDark), "Feb Session", "", true, "kavitamp19@googlemail.com", false
                    , 2458893, 2458893, 990, 1050, 1580619630000l, 1580623230000l, false, false));

            events.add(new Event(1059, getResources().getColor(R.color.colorOrangeAccent), "June Session", "", true, "kavitamp19@googlemail.com", false
                    , 2459014, 2459014, 990, 1050, 1592010030000l, 1592024430000l, false, false));

            monthFieldColors = setMonthViewColor();

            Bundle bundle = new Bundle();
            bundle.putLong(IntentKeys.KEY_TIME_IN_MILLIS, timeMillis);
            bundle.putBoolean(IntentKeys.KEY_IS_MINI_MONTH, true);
            //bundle.putParcelable(IntentKeys.KEY_COLOR_HELPER, monthFieldColors);
            bundle.putParcelableArrayList(IntentKeys.KEY_EVENT_LIST, events);
            miniMonthFrag.setArguments(bundle);
            ft.replace(R.id.mini_month, miniMonthFrag);
            mController.registerEventHandler(R.id.mini_month, (CalendarController.EventHandler) miniMonthFrag);

            Fragment selectCalendarsFrag = new SelectVisibleCalendarsFragment();
            ft.replace(R.id.calendar_list, selectCalendarsFrag);
            mController.registerEventHandler(
                    R.id.calendar_list, (CalendarController.EventHandler) selectCalendarsFrag);
        }

        CalendarController.EventInfo info = null;
        if (viewType == CalendarController.ViewType.EDIT) {
            mPreviousView = GeneralPreferences.getSharedPreferences(this).getInt(
                    GeneralPreferences.KEY_START_VIEW, GeneralPreferences.DEFAULT_START_VIEW);

            long eventId = -1;
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data != null) {
                try {
                    eventId = Long.parseLong(data.getLastPathSegment());
                } catch (NumberFormatException e) {
                    if (DEBUG) {
                        Log.d(TAG, "Create new event");
                    }
                }
            } else if (icicle != null && icicle.containsKey(BUNDLE_KEY_EVENT_ID)) {
                eventId = icicle.getLong(BUNDLE_KEY_EVENT_ID);
            }

            long begin = intent.getLongExtra(EXTRA_EVENT_BEGIN_TIME, -1);
            long end = intent.getLongExtra(EXTRA_EVENT_END_TIME, -1);
            info = new CalendarController.EventInfo();
            if (end != -1) {
                info.endTime = new Time();
                info.endTime.set(end);
            }
            if (begin != -1) {
                info.startTime = new Time();
                info.startTime.set(begin);
            }
            info.id = eventId;
            // We set the viewtype so if the user presses back when they are
            // done editing the controller knows we were in the Edit Event
            // screen. Likewise for eventId
            mController.setViewType(viewType);
            mController.setEventId(eventId);
        } else {
            mPreviousView = viewType;
        }

        setMainPane(ft, R.id.main_pane, viewType, timeMillis, true);
        ft.commit(); // this needs to be after setMainPane()

        Time t = new Time(mTimeZone);
        t.set(timeMillis);
        if (viewType == CalendarController.ViewType.AGENDA && icicle != null) {
            mController.sendEvent(this, CalendarController.EventType.GO_TO, t, null,
                    icicle.getLong(BUNDLE_KEY_EVENT_ID, -1), viewType);
        } else if (viewType != CalendarController.ViewType.EDIT) {
            mController.sendEvent(this, CalendarController.EventType.GO_TO, t, null, -1, viewType);
        }
    }

    private void setMainPane(
            FragmentTransaction ft, int viewId, int viewType, long timeMillis, boolean force) {
        if (mOnSaveInstanceStateCalled) {
            return;
        }
        if (!force && mCurrentView == viewType) {
            return;
        }

        // Remove this when transition to and from month view looks fine.
        boolean doTransition = viewType != CalendarController.ViewType.MONTH && mCurrentView != CalendarController.ViewType.MONTH;
        FragmentManager fragmentManager = getFragmentManager();
        // Check if our previous view was an Agenda view
        // TODO remove this if framework ever supports nested fragments
        if (mCurrentView == CalendarController.ViewType.AGENDA) {
            // If it was, we need to do some cleanup on it to prevent the
            // edit/delete buttons from coming back on a rotation.
            Fragment oldFrag = fragmentManager.findFragmentById(viewId);
            if (oldFrag instanceof AgendaFragment) {
                ((AgendaFragment) oldFrag).removeFragments(fragmentManager);
            }
        }

        if (viewType != mCurrentView) {
            // The rules for this previous view are different than the
            // controller's and are used for intercepting the back button.
            if (mCurrentView != CalendarController.ViewType.EDIT && mCurrentView > 0) {
                mPreviousView = mCurrentView;
            }
            mCurrentView = viewType;
        }
        // Create new fragment
        Fragment frag = null;
        Fragment secFrag = null;
        switch (viewType) {
            case CalendarController.ViewType.MONTH:
                monthFieldColors = setMonthViewColor();
                ArrayList<Event> events = new ArrayList<Event>();
                events.add(new Event(1058, getResources().getColor(R.color.colorRedPrimaryDark), "Feb Session", "", true, "kavitamp19@googlemail.com", false
                        , 2458893, 2458893, 990, 1050, 1580619630000l, 1580623230000l, false, false));

                events.add(new Event(1059, getResources().getColor(R.color.colorOrangeAccent), "June Session", "", true, "kavitamp19@googlemail.com", false
                        , 2459014, 2459014, 990, 1050, 1592010030000l, 1592024430000l, false, false));

                frag = new MonthByWeekFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(IntentKeys.KEY_TIME_IN_MILLIS, timeMillis);
                bundle.putBoolean(IntentKeys.KEY_IS_MINI_MONTH, false);
                bundle.putParcelableArrayList(IntentKeys.KEY_EVENT_LIST, events);
                //bundle.putParcelable(IntentKeys.KEY_COLOR_HELPER, monthFieldColors);
                frag.setArguments(bundle);
                break;
        }

        boolean doCommit = false;
        if (ft == null) {
            doCommit = true;
            ft = fragmentManager.beginTransaction();
        }

        if (doTransition) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        ft.replace(viewId, frag);
        if (DEBUG) {
            Log.d(TAG, "Adding handler with viewId " + viewId + " and type " + viewType);
        }
        // If the key is already registered this will replace it
        mController.registerEventHandler(viewId, (CalendarController.EventHandler) frag);
        if (secFrag != null) {
            mController.registerEventHandler(viewId, (CalendarController.EventHandler) secFrag);
        }

        if (doCommit) {
            if (DEBUG) {
                Log.d(TAG, "setMainPane AllInOne=" + this + " finishing:" + this.isFinishing());
            }
            ft.commit();
        }
    }

    private long parseViewAction(final Intent intent) {
        long timeMillis = -1;
        Uri data = intent.getData();
        if (data != null && data.isHierarchical()) {
            List<String> path = data.getPathSegments();
            if (path.size() == 2 && path.get(0).equals("events")) {
                try {
                    mViewEventId = Long.valueOf(data.getLastPathSegment());
                    if (mViewEventId != -1) {
                        mIntentEventStartMillis = intent.getLongExtra(EXTRA_EVENT_BEGIN_TIME, 0);
                        mIntentEventEndMillis = intent.getLongExtra(EXTRA_EVENT_END_TIME, 0);
                        mIntentAttendeeResponse = intent.getIntExtra(
                                ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_NONE);
                        mIntentAllDay = intent.getBooleanExtra(EXTRA_EVENT_ALL_DAY, false);
                        timeMillis = mIntentEventStartMillis;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if mViewEventId can't be parsed
                }
            }
        }
        return timeMillis;
    }

    private MonthFieldColorHelper setMonthViewColor() {
        /*monthFieldColors = new MonthFieldColorHelper(this);
        monthFieldColors.setMonthNumColor(getColorFromResource(R.color.calendar_number_text));
        monthFieldColors.setMonthNumTodayColor(getColorFromResource(R.color.calendar_selected_text));
        monthFieldColors.setMonthBGOtherColor(getColorFromResource(R.color.calendar_background));
        monthFieldColors.setMonthNumOtherColor(getColorFromResource(R.color.calendar_other_number_text));
        monthFieldColors.setMonthBGTodayColor(getColorFromResource(R.color.calendar_selected_background));
        monthFieldColors.setMonthDayNameColor(getColorFromResource(R.color.calendar_day_text));
        monthFieldColors.setMonthSaturdayColor(getColorFromResource(R.color.calendar_day_text));
        monthFieldColors.setMonthSundayColor(getColorFromResource(R.color.calendar_day_text));
        */
        return monthFieldColors;
    }

    private void checkAppPermissions() {
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {
                    Toast.makeText(getApplicationContext(), R.string.user_rejected_calendar_write_permission, Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

        // Clean up cached ics and vcs files - in case onDestroy() didn't run the last time
        cleanupCachedEventFiles();
    }

    /**
     * Cleans up the temporarily generated ics and vcs files in the cache directory
     * The files are of the format *.ics and *.vcs
     */
    private void cleanupCachedEventFiles() {
        if (!isExternalStorageWritable()) return;
        File cacheDir = getExternalCacheDir();
        File[] files = cacheDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            String filename = file.getName();
            if (filename.endsWith(".ics") || filename.endsWith(".vcs")) {
                file.delete();
            }
        }
    }

    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = GeneralPreferences.getSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        mController.deregisterAllEventHandlers();

        CalendarController.removeInstance(this);

        // Clean up cached ics and vcs files
        cleanupCachedEventFiles();
    }

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }
    }
}

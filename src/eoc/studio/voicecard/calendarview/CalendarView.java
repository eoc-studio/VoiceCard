package eoc.studio.voicecard.calendarview;

import java.util.Locale;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class CalendarView extends Fragment implements OnTouchListener, OnClickListener
{
	private static Context mContext;
	private static Locale locale;
	private ViewSwitcher calendarSwitcher;
	private static TextView currentMonth;
	private static CalendarAdapter calendarAdapter;
	private GestureDetector swipeDetector;
	private static GridView calendarGrid;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public CalendarView()
	{
		locale = Locale.getDefault();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onResume()
	{
		super.onResume();
		mContext = getActivity();
		updateCurrentMonth();
		// getNewView();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		DateProcess.mCalendar.setTimeInMillis(System.currentTimeMillis());
		calendarAdapter = null;
		SystemClock.sleep(200);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mContext = getActivity();
		final RelativeLayout calendarLayout = (RelativeLayout) inflater.inflate(
				R.layout.calendar_view, null);
		calendarGrid = (GridView) calendarLayout.findViewById(R.id.calendar_grid);
		final Button nextMonth = (Button) calendarLayout.findViewById(R.id.next_month);
		final Button prevMonth = (Button) calendarLayout.findViewById(R.id.previous_month);
		swipeDetector = new GestureDetector(getActivity(), new SwipeGesture(getActivity()));
		calendarSwitcher = (ViewSwitcher) calendarLayout.findViewById(R.id.calendar_switcher);
		currentMonth = (TextView) calendarLayout.findViewById(R.id.current_month);
		//
		nextMonth.setOnClickListener(this);
		prevMonth.setOnClickListener(this);
		getNewView();
		calendarGrid.setOnItemClickListener(new DayItemClickListener());
		calendarGrid.setOnTouchListener(this);
		return calendarLayout;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void getNewView()
	{
		calendarAdapter = new CalendarAdapter(mContext, DateProcess.mCalendar);
		updateCurrentMonth();
		calendarGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		calendarGrid.setAdapter(calendarAdapter);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return swipeDetector.onTouchEvent(event);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void updateCurrentMonth()
	{
		calendarAdapter.refreshDays();
		currentMonth.setText(DateProcess.getYear() + "-"
				+ String.format(locale, "%tB", DateProcess.mCalendar));
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final class DayItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Intent SetCalendarMainView = new Intent();
			SetCalendarMainView.setClass(getActivity(), SetCalendarMainView.class);
			Bundle bundle = new Bundle();
			bundle.putString(DataProcess.EVENT_DATE, calendarAdapter.setSelected(position));
			SetCalendarMainView.putExtras(bundle);
			getActivity().startActivity(SetCalendarMainView);
			getActivity().overridePendingTransition(0, 0);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final void onNextMonth()
	{
		calendarSwitcher.setInAnimation(getActivity(), R.anim.in_from_right);
		calendarSwitcher.setOutAnimation(getActivity(), R.anim.out_to_left);
		calendarSwitcher.showNext();
		DateProcess.setNextMonthCalendar();
		updateCurrentMonth();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final void onPreviousMonth()
	{
		calendarSwitcher.setInAnimation(getActivity(), R.anim.in_from_left);
		calendarSwitcher.setOutAnimation(getActivity(), R.anim.out_to_right);
		calendarSwitcher.showPrevious();
		DateProcess.setPreviousMonthCalendar();
		updateCurrentMonth();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.next_month:
		{
			onNextMonth();
		}
			break;
		case R.id.previous_month:
		{
			onPreviousMonth();
		}
			break;
		case R.id.btnBack:
		{
			System.out.println("*****************btnBack");
		}
			break;
		case R.id.btnAddFacebookFriends:
		{
			System.out.println("*****************btnAddFacebookFriends");
		}
			break;
		case R.id.btnBackMainMenu:
		{
			System.out.println("*****************btnBackMainMenu");
		}
			break;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final class SwipeGesture extends SimpleOnGestureListener
	{
		private final int swipeMinDistance;
		private final int swipeThresholdVelocity;

		public SwipeGesture(Context context)
		{
			final ViewConfiguration viewConfig = ViewConfiguration.get(context);
			swipeMinDistance = viewConfig.getScaledTouchSlop();
			swipeThresholdVelocity = viewConfig.getScaledMinimumFlingVelocity();
		}

		@Override
		public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
		{
			if (me1.getX() - me2.getX() > swipeMinDistance
					&& Math.abs(velocityX) > swipeThresholdVelocity)
			{
				onNextMonth();
			}
			else if (me2.getX() - me1.getX() > swipeMinDistance
					&& Math.abs(velocityX) > swipeThresholdVelocity)
			{
				onPreviousMonth();
			}
			return false;
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

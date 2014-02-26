package eoc.studio.voicecard.calendarview;

import java.util.ArrayList;
import java.util.Map;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

public class SetOfCalendarView extends Fragment implements OnTouchListener
{
	private static Context mContext;
	private static SetOfCalendarAdapter calendarAdapter;
	private static HorizontalScrollView horizontalScrollView = null;
	private static float moveStart = 0, moveEnd = 0;
	private static boolean isSmoothScroll = false;
	private int getPages = 0;
	private static ArrayList<Map<String, String>> mData;
	private static GridView calendarGrid;
	private static RelativeLayout hideView;
	private static final int DEFAULT_SCROOL_SITE = 0;
	private static final int DEFAULT_SCROOL_BUFF = 100;
	private static final int DEFAULT_NOITE_WIDTH = 435;
	private static final int ONE_PAGE = 1, THREE_PAGE = 3;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public SetOfCalendarView()
	{
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mContext = getActivity();
		CalendarIntentHelper.addVoiceCardCalendar(mContext);
		final RelativeLayout calendarLayout = (RelativeLayout) inflater.inflate(
				R.layout.set_of_calendar, null);
		hideView = (RelativeLayout) calendarLayout.findViewById(R.id.hideView);
		horizontalScrollView = (HorizontalScrollView) calendarLayout
				.findViewById(R.id.calendar_horizontal_scroll);
		calendarGrid = (GridView) calendarLayout.findViewById(R.id.calendar_grid);
		horizontalScrollView.setOnTouchListener(this);
		getNewView();
		return calendarLayout;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void getNewView()
	{
		mData = getEventData();
		calendarAdapter = new SetOfCalendarAdapter(mContext, mData);
		if (mData.size() == 1)
		{
			hideView.setVisibility(View.INVISIBLE);
		}
		else
		{
			hideView.setVisibility(View.GONE);
		}
		calendarGrid.setNumColumns(mData.size());
		calendarGrid.setLayoutParams(new FrameLayout.LayoutParams(535 * mData.size(), 510));
		calendarGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		calendarGrid.setOnItemClickListener(new DayItemClickListener());
		calendarGrid.setAdapter(calendarAdapter);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_MOVE && !isSmoothScroll)
		{
			isSmoothScroll = true;
			moveStart = event.getX();
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			isSmoothScroll = false;
			moveEnd = event.getX();
			if (moveStart > moveEnd)
			{
				if (getPages >= (mData.size() - 1)) { return true; }
				smoothScrollView((++getPages));
			}
			else
			{
				if (getPages < 1) { return false; }
				smoothScrollView((--getPages));
			}
		}
		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void smoothScrollView(final int getPage)
	{
		new Handler().postDelayed((new Runnable()
		{
			@Override
			public void run()
			{
				if (getPage == DEFAULT_SCROOL_SITE)
				{
					horizontalScrollView.smoothScrollTo(DEFAULT_SCROOL_SITE, DEFAULT_SCROOL_SITE);
				}
				else if (getPage == ONE_PAGE)
				{
					horizontalScrollView.smoothScrollTo(DEFAULT_NOITE_WIDTH, DEFAULT_SCROOL_SITE);
				}
				else if (getPage < THREE_PAGE)
				{
					horizontalScrollView.smoothScrollTo((DEFAULT_NOITE_WIDTH * (getPage))
							+ DEFAULT_SCROOL_BUFF, DEFAULT_SCROOL_SITE);
				}
				else
				{
					horizontalScrollView.smoothScrollTo(
							((DEFAULT_NOITE_WIDTH + DEFAULT_SCROOL_BUFF) * getPage)
									- DEFAULT_SCROOL_BUFF, DEFAULT_SCROOL_SITE);
				}
			}
		}), 5);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static ArrayList<Map<String, String>> getEventData()
	{
		String event = DataProcess.getSelectedEventDate();
		if (!event.equals("")) { return CalendarIntentHelper.readCalendarEvent(mContext,
				DataProcess.getSelectedEventDate()); }
		return null;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static final class DayItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			calendarAdapter.setSelected(position);
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

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
				new Handler().postDelayed((new Runnable()
				{
					@Override
					public void run()
					{
						if (getPages >= (mData.size() - 1)) { return; }
						++getPages;
						if (getPages == 1)
						{
							horizontalScrollView.smoothScrollTo(435, 0);
						}
						else if (getPages < 3)
						{
							horizontalScrollView.smoothScrollTo((435 * (getPages)) + 100, 0);
						}
						else
						{
							horizontalScrollView.smoothScrollTo((535 * getPages) - 100, 0);
						}
					}
				}), 5);
			}
			else
			{
				new Handler().postDelayed((new Runnable()
				{
					@Override
					public void run()
					{
						if (getPages < 1) { return; }
						--getPages;
						if (getPages == 0)
						{
							horizontalScrollView.smoothScrollTo(0, 0);
						}
						else if (getPages < 3)
						{
							horizontalScrollView.smoothScrollTo((435 * (getPages)) + 100, 0);
						}
						else
						{
							horizontalScrollView.smoothScrollTo((535 * getPages) - 100, 0);
						}
					}
				}), 5);
			}
		}
		return true;
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

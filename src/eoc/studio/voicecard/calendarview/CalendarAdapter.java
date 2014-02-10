package eoc.studio.voicecard.calendarview;

import java.util.Calendar;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CalendarAdapter extends BaseAdapter
{
	private static final int FIRST_DAY_OF_WEEK = Calendar.SUNDAY;
	private final Calendar calendar;
	private final CalendarItem today;
	private static int mSelected = -1;
	private final LayoutInflater inflater;
	private CalendarItem[] days;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public CalendarAdapter(Context context, Calendar monthCalendar)
	{
		mSelected = -1;
		calendar = monthCalendar;
		today = new CalendarItem(monthCalendar);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getCount()
	{
		return days.length;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Object getItem(int position)
	{
		return days[position];
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		if (view == null)
		{
			view = inflater.inflate(R.layout.calendar_item, null);
		}
		final RelativeLayout dayBackground = (RelativeLayout) view.findViewById(R.id.background);
		final RelativeLayout dayView = (RelativeLayout) view.findViewById(R.id.date);
		final CalendarItem currentItem = days[position];
		if (currentItem == null)
		{
			dayView.setBackgroundResource(DrawableProcess.getWeekDrawable(position));
			dayBackground.setClickable(false);
			dayBackground.setFocusable(false);
		}
		else
		{
			if (currentItem.equals(today))
			{
				dayBackground.setBackgroundResource(R.drawable.today_frame);
				dayView.setBackgroundResource(DrawableProcess.getTodayDrawable(Integer
						.parseInt(currentItem.text)));
				// view.setBackgroundResource(R.drawable.today_background);
			}
			else
			{
				dayBackground.setBackgroundResource(R.drawable.normal_frame);
				// view.setBackgroundResource(R.drawable.normal_background);
			}
			if (mSelected == position)
			{
				// dayBackground.setBackgroundResource(R.drawable.normal_background);
				// view.setBackgroundResource(R.drawable.selected_background);
			}
			// dayView.setText(currentItem.text);
			dayView.setBackgroundResource(DrawableProcess.getNormalDayDrawable(Integer
					.parseInt(currentItem.text)));
		}
		return view;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public final void setSelected(int position)
	{
		mSelected = position;
		notifyDataSetChanged();
		if (mSelected == position)
		{
			System.out.println("@position=================================" + position);
			final CalendarItem item = days[position];
			if (item != null)
			{
				System.out.println("@id===============================" + days[position].id);
				System.out.println("@year=============================" + days[position].year);
				System.out.println("@month============================" + days[position].month);
				System.out.println("@day==============================" + days[position].day);
				System.out.println("@text=============================" + days[position].text);
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public final void refreshDays()
	{
		DateProcess.getPreviousMonthdata();
		DateProcess.getNextMonthdata();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int firstDayOfMonthPosition = calendar.get(Calendar.DAY_OF_WEEK);
		final int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		final int lastMonthPosition;
		//
		final int getPY = DateProcess.getPreviousYear();
		final int getPM = DateProcess.getPreviousMonth();
		final int getPMAM = DateProcess.getPreviousMonthActualMaximum();
		//
		final int getNY = DateProcess.getNextYear();
		final int getNM = DateProcess.getNextMonth();
		final CalendarItem[] days = new CalendarItem[42];
		if (firstDayOfMonthPosition == FIRST_DAY_OF_WEEK)
		{
			lastMonthPosition = 7;
		}
		else
		{
			lastMonthPosition = firstDayOfMonthPosition - FIRST_DAY_OF_WEEK + 7;
		}
		int getNextMonthOfDay = 1;
		int getLastMonthOfDay = lastMonthPosition;
		for (int position = 7; position < days.length; position++)
		{
			if (position < lastMonthPosition)
			{
				days[position] = new CalendarItem(getPY, getPM, getPMAM - (--getLastMonthOfDay));
			}
			else if (position < (lastDayOfMonth + lastMonthPosition))
			{
				days[position] = new CalendarItem(year, month, (position - lastMonthPosition + 1));
			}
			else
			{
				days[position] = new CalendarItem(getNY, getNM, getNextMonthOfDay++);
			}
		}
		this.days = days;
		notifyDataSetChanged();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static class CalendarItem
	{
		public int year;
		public int month;
		public int day;
		public String text;
		public long id;

		public CalendarItem(Calendar calendar)
		{
			this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
					.get(Calendar.DAY_OF_MONTH));
		}

		public CalendarItem(int year, int month, int day)
		{
			this.year = year;
			this.month = month;
			this.day = day;
			this.text = String.valueOf(day);
			this.id = Long.valueOf(year + "" + month + "" + day);
		}

		@Override
		public boolean equals(Object o)
		{
			if (o != null && o instanceof CalendarItem)
			{
				final CalendarItem item = (CalendarItem) o;
				return item.year == year && item.month == month && item.day == day;
			}
			return false;
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

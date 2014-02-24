package eoc.studio.voicecard.calendarview;

import java.text.DecimalFormat;
import java.util.Calendar;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CalendarAdapter extends BaseAdapter
{
	private static Context mContext;
	private static final int FIRST_DAY_OF_WEEK = Calendar.SUNDAY;
	private final Calendar calendar;
	private final CalendarItem today;
	private static int mSelected = -1;
	private final LayoutInflater inflater;
	private CalendarItem[] days;
	private static Thread dataThread = null;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public CalendarAdapter(Context context, Calendar monthCalendar)
	{
		mContext = context;
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
		if (days != null) { return days.length; }
		return 0;
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
		final ImageView dayBackground = (ImageView) view.findViewById(R.id.background);
		final ImageView dayView = (ImageView) view.findViewById(R.id.date);
		final ImageView eventView = (ImageView) view.findViewById(R.id.date2);
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
			}
			else if (currentItem.month != calendar.get(Calendar.MONTH))
			{
				dayBackground.setBackgroundResource(R.drawable.disable_frame);
				dayView.setBackgroundResource(DrawableProcess.getNormalDayDrawable(Integer
						.parseInt(currentItem.text)));
			}
			else
			{
				dayBackground.setBackgroundResource(R.drawable.normal_frame);
				dayView.setBackgroundResource(DrawableProcess.getNormalDayDrawable(Integer
						.parseInt(currentItem.text)));
			}
			if (currentItem.event)
			{
				eventView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.setted));
			}
			else
			{
				eventView.setImageDrawable(null);
			}
		}
		return view;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final String setSelected(int position)
	{
		mSelected = position;
		notifyDataSetChanged();
		if (mSelected == position)
		{
			final CalendarItem item = days[position];
			if (item != null)
			{
				String getNewEventDate = (DataProcess.eventDateFormat(days[position].year,
						(days[position].month + 1), days[position].day))
						+ DataProcess.DEFAULT_EVENT_TIME;
				return getNewEventDate;
			}
		}
		return "";
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final void refreshDays()
	{
		dataThread = new Thread(new Runnable()
		{
			@Override
			public void run()
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
				final CalendarItem[] getNewdays = new CalendarItem[42];
				DecimalFormat df2 = (DecimalFormat) DecimalFormat.getInstance();
				df2.applyPattern("00");
				if (firstDayOfMonthPosition == FIRST_DAY_OF_WEEK)
				{
					lastMonthPosition = 7;
				}
				else
				{
					lastMonthPosition = firstDayOfMonthPosition - FIRST_DAY_OF_WEEK + 7;
				}
				int getNextMonthOfDay = 1;
				int getLastMonthOfDay = firstDayOfMonthPosition - FIRST_DAY_OF_WEEK;
				for (int position = 7; position < getNewdays.length; position++)
				{
					if (position < lastMonthPosition)
					{
						int getPreviousDay = getPMAM - (--getLastMonthOfDay);
						String eventDate = DataProcess.eventDateFormat(getPY, (getPM + 1),
								getPreviousDay);
						getNewdays[position] = new CalendarItem(getPY, getPM, getPreviousDay,
								CalendarIntentHelper.haveEvent(mContext, eventDate));
					}
					else if (position < (lastDayOfMonth + lastMonthPosition))
					{
						int getDay = (position - lastMonthPosition + 1);
						String eventDate = DataProcess.eventDateFormat(year, (month + 1), getDay);
						getNewdays[position] = new CalendarItem(year, month, getDay,
								CalendarIntentHelper.haveEvent(mContext, eventDate));
					}
					else
					{
						int getNextDay = getNextMonthOfDay++;
						String eventDate = DataProcess.eventDateFormat(getNY, (getNM + 1),
								getNextDay);
						getNewdays[position] = new CalendarItem(getNY, getNM, getNextDay,
								CalendarIntentHelper.haveEvent(mContext, eventDate));
					}
				}
				days = getNewdays;
				Message status = sendNotifyDataSetChanged.obtainMessage();
				sendNotifyDataSetChanged.sendMessage(status);
			}
		});
		dataThread.start();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	Handler sendNotifyDataSetChanged = new Handler()
	{
		public void handleMessage(Message msg)
		{
			notifyDataSetChanged();
		}
	};

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static class CalendarItem
	{
		public int year;
		public int month;
		public int day;
		public String text;
		public long id;
		public boolean event;

		public CalendarItem(Calendar calendar)
		{
			this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
					.get(Calendar.DAY_OF_MONTH), false);
		}

		public CalendarItem(int year, int month, int day, boolean haveEvent)
		{
			this.year = year;
			this.month = month;
			this.day = day;
			this.text = String.valueOf(day);
			this.id = Long.valueOf(year + "" + month + "" + day);
			this.event = haveEvent;
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

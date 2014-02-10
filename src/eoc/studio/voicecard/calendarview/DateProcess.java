package eoc.studio.voicecard.calendarview;

import java.util.Calendar;

public class DateProcess
{
	protected static final Calendar mCalendar = Calendar.getInstance();
	protected static final Calendar mPreviousMonthCalendar = Calendar.getInstance();
	protected static final Calendar mNextMonthCalendar = Calendar.getInstance();

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void getPreviousMonthdata()
	{
		if (mCalendar.get(Calendar.MONTH) == Calendar.JANUARY)
		{
			mPreviousMonthCalendar.set((getYear() - 1), Calendar.DECEMBER, 1);
		}
		else
		{
			mPreviousMonthCalendar.set(Calendar.MONTH, getMonth() - 1);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void getNextMonthdata()
	{
		if (mCalendar.get(Calendar.MONTH) == Calendar.DECEMBER)
		{
			mNextMonthCalendar.set((getYear() + 1), Calendar.JANUARY, 1);
		}
		else
		{
			mNextMonthCalendar.set(Calendar.MONTH, getMonth() + 1);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getYear()
	{
		return mCalendar.get(Calendar.YEAR);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getMonth()
	{
		return mCalendar.get(Calendar.MONTH);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static Calendar setPreviousMonthCalendar()
	{
		if (mCalendar.get(Calendar.MONTH) == Calendar.JANUARY)
		{
			mCalendar.set((mCalendar.get(Calendar.YEAR) - 1), Calendar.DECEMBER, 1);
		}
		else
		{
			mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) - 1);
		}
		getPreviousMonthdata();
		getNextMonthdata();
		return mCalendar;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static Calendar setNextMonthCalendar()
	{
		if (mCalendar.get(Calendar.MONTH) == Calendar.DECEMBER)
		{
			mCalendar.set((mCalendar.get(Calendar.YEAR) + 1), Calendar.JANUARY, 1);
		}
		else
		{
			mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) + 1);
		}
		getPreviousMonthdata();
		getNextMonthdata();
		return mCalendar;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getPreviousMonthActualMaximum()
	{
		return mPreviousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getPreviousYear()
	{
		return mPreviousMonthCalendar.get(Calendar.YEAR);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getPreviousMonth()
	{
		return mPreviousMonthCalendar.get(Calendar.MONTH);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getNextYear()
	{
		return mNextMonthCalendar.get(Calendar.YEAR);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getNextMonth()
	{
		return mNextMonthCalendar.get(Calendar.MONTH);
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

package eoc.studio.voicecard.calendarview;

import eoc.studio.voicecard.R;

public class DrawableProcess
{
	private static final int[] NORMAL_DAY_DRAWABLE = new int[] { R.drawable.normal_day_1,
			R.drawable.normal_day_2, R.drawable.normal_day_3, R.drawable.normal_day_4,
			R.drawable.normal_day_5, R.drawable.normal_day_6, R.drawable.normal_day_7,
			R.drawable.normal_day_8, R.drawable.normal_day_9, R.drawable.normal_day_10,
			R.drawable.normal_day_11, R.drawable.normal_day_12, R.drawable.normal_day_13,
			R.drawable.normal_day_14, R.drawable.normal_day_15, R.drawable.normal_day_16,
			R.drawable.normal_day_17, R.drawable.normal_day_18, R.drawable.normal_day_19,
			R.drawable.normal_day_20, R.drawable.normal_day_21, R.drawable.normal_day_22,
			R.drawable.normal_day_23, R.drawable.normal_day_24, R.drawable.normal_day_25,
			R.drawable.normal_day_26, R.drawable.normal_day_27, R.drawable.normal_day_28,
			R.drawable.normal_day_29, R.drawable.normal_day_30, R.drawable.normal_day_31 };
	private static final int[] TODAY_DRAWABLE = new int[] { R.drawable.today_1, R.drawable.today_2,
			R.drawable.today_3, R.drawable.today_4, R.drawable.today_5, R.drawable.today_6,
			R.drawable.today_7, R.drawable.today_8, R.drawable.today_9, R.drawable.today_10,
			R.drawable.today_11, R.drawable.today_12, R.drawable.today_13, R.drawable.today_14,
			R.drawable.today_15, R.drawable.today_16, R.drawable.today_17, R.drawable.today_18,
			R.drawable.today_19, R.drawable.today_20, R.drawable.today_21, R.drawable.today_22,
			R.drawable.today_23, R.drawable.today_24, R.drawable.today_25, R.drawable.today_26,
			R.drawable.today_27, R.drawable.today_28, R.drawable.today_29, R.drawable.today_30,
			R.drawable.today_31 };
	protected static final int[] WEEK_DRAWABLE = new int[] { R.drawable.week_7, R.drawable.week_1,
			R.drawable.week_2, R.drawable.week_3, R.drawable.week_4, R.drawable.week_5,
			R.drawable.week_6 };

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getWeekDrawable(int day)
	{
		return WEEK_DRAWABLE[(day)];
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getNormalDayDrawable(int day)
	{
		return NORMAL_DAY_DRAWABLE[(day - 1)];
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int getTodayDrawable(int day)
	{
		return TODAY_DRAWABLE[(day - 1)];
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

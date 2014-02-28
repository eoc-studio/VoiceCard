package eoc.studio.voicecard.calendarview;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

public class DataProcess
{
	public static final String EVENT_DATE = "eventDate";
	private static final String DEFAULT_YEAR = "1970";
	private static final String DEFAULT_DURATION = "P1D";
	public static final String DEFAULT_EVENT_TIME = "0800";
	private static final int EVENT_ALARM_12_HOURS = 60 * 12;
	private static final int IS_ALL_DAY = 1;
	private static final int HAS_ALARM = 1;
	private static String eventDateValue = "";

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void addEvent(final Context context, final String title, String date)
	{
		try
		{
			if (context == null) { return; }
			if (title == null || title.equals("") || title.equals("null")) { return; }
			if (date == null || date.equals("") || date.equals("null")) { return; }
			date = date + DEFAULT_EVENT_TIME;
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(Events.CALENDAR_ID, CalendarIntentHelper.getVoiceCardCalendar(context));
			values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
			values.put(Events.TITLE, title);
			values.put(Events.ALL_DAY, IS_ALL_DAY);
			values.put(Events.DTSTART, getDataMilliSeconds(date));
			values.put(Events.DURATION, DEFAULT_DURATION);
			values.put(Events.DESCRIPTION, "VoiceCard Note");
			values.put(Events.RRULE, CalendarIntentHelper.EVENT_TYPE_YEARLY);
			values.put(Events.HAS_ALARM, HAS_ALARM);
			Uri uri = cr.insert(Events.CONTENT_URI, values);
			//
			// add 12 hours reminder for the event
			ContentValues reminders = new ContentValues();
			reminders.put(Reminders.EVENT_ID, Long.parseLong(uri.getLastPathSegment()));
			reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
			reminders.put(Reminders.MINUTES, EVENT_ALARM_12_HOURS);
			cr.insert(Reminders.CONTENT_URI, reminders);
		}
		catch (SQLException e)
		{
			System.out.println("[DataProcess][addEvent]SQLException:" + e);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void updataEvent(Context context, String title, String eventID)
	{
		try
		{
			if (context == null) { return; }
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(Events.TITLE, title);
			Uri updataUri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.valueOf(eventID));
			cr.update(updataUri, values, null, null);
		}
		catch (SQLException e)
		{
			System.out.println("[DataProcess][updataEvent]SQLException:" + e);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void deleteEvent(Context context, String eventID)
	{
		try
		{
			if (context == null) { return; }
			ContentResolver cr = context.getContentResolver();
			Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, Long.valueOf(eventID));
			cr.delete(deleteUri, null, null);
		}
		catch (SQLException e)
		{
			System.out.println("[DataProcess][deleteEvent]SQLException:" + e);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static String getSelectedEventDate()
	{
		if (eventDateValue != null && !eventDateValue.equals("")) { return eventDateValue; }
		return "";
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void setSelectedEventDate(String evenDate)
	{
		eventDateValue = evenDate;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static String numberFormat(final int value)
	{
		DecimalFormat df2 = (DecimalFormat) DecimalFormat.getInstance();
		df2.applyPattern("00");
		return df2.format(value);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static String eventDateFormat(final int year, final int month, final int day)
	{
		String getNewEventDate = String.valueOf(numberFormat(year))
				+ String.valueOf(numberFormat(month)) + String.valueOf(numberFormat(day));
		return getNewEventDate;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static String getDataMilliSeconds(final String date)
	{
		return getDataMilliSeconds(date, "yyyyMMddHHmm");
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String getDataMilliSeconds(final String date, final String format)
	{
		if (date == null) return "";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setLenient(false);
		Date oldDate = null;
		try
		{
			oldDate = formatter.parse(date);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (oldDate == null) { return ""; }
		return String.valueOf(oldDate.getTime());
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String formatDate(final String milliSeconds, final String format)
	{
		if (milliSeconds != null && !milliSeconds.equals(""))
		{
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(Long.parseLong(milliSeconds));
			return formatter.format(calendar.getTime());
		}
		else
		{
			return "";
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String checkFacebookBirthdayFormats(final String date)
	{
		String formats = "";
		if (date.length() > 5)
		{
			return DataProcess.getDataMilliSeconds(date, "MM/dd/yyyy");
		}
		else if (date.length() == 5) { return DataProcess.getDataMilliSeconds(date + "/"
				+ DEFAULT_YEAR, "MM/dd/yyyy"); }
		return formats;
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

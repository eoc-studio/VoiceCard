package eoc.studio.voicecard.calendarview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class CalendarIntentHelper
{
	public static ArrayList<String> nameOfEvent = new ArrayList<String>();
	public static ArrayList<String> startDates = new ArrayList<String>();
	public static ArrayList<String> endDates = new ArrayList<String>();
	public static ArrayList<String> descriptions = new ArrayList<String>();
	private static Map<String, String> item;
	private static ArrayList<Map<String, String>> data;
	public static final String EVENT_ID = "calendar_id", EVENT_TITLE = "title",
			EVENT_DESCRIPTION = "description", EVENT_DT_START = "dtstart", EVENT_DT_END = "dtend",
			EVENT_LOCTION = "eventLocation";

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Map<String, String>> readCalendarEvent(Context context, String eventDate)
	{
		data = new ArrayList<Map<String, String>>();
		Cursor cursor = null;
		String where = null, selection[] = null;
		where = "dtstart =? ";
		selection = new String[] { String.valueOf(getDatamilliSeconds((eventDate + "0800"))) };
		try
		{
			cursor = context.getContentResolver().query(
					Uri.parse("content://com.android.calendar/events"),
					new String[] { EVENT_ID, EVENT_TITLE, EVENT_DESCRIPTION, EVENT_DT_START,
							EVENT_DT_END, EVENT_LOCTION }, where, selection, null);
			if (cursor != null && cursor.getCount() > 0)
			{
				while (cursor.moveToNext())
				{
					item = new HashMap<String, String>();
					item.put(EVENT_TITLE, (cursor.getString(1) != null) ? cursor.getString(1) : "");
					item.put(EVENT_DESCRIPTION, (cursor.getString(2) != null) ? cursor.getString(2)
							: "");
					item.put(EVENT_DT_START, (cursor.getString(3) != null) ? cursor.getString(3)
							: "");
					item.put(EVENT_DT_END, (cursor.getString(4) != null) ? cursor.getString(4) : "");
					data.add(item);
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("[CalendarIntentHelper][readCalendarEvent]SQLException:" + e);
		}
		catch (Exception ex)
		{
			System.out.println("[CalendarIntentHelper][readCalendarEvent]Exception:" + ex);
		}
		if (cursor != null && !cursor.isClosed())
		{
			cursor.close();
		}
		return data;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean haveEvent(Context context, String eventDate)
	{
		ArrayList<Map<String, String>> getEvent = CalendarIntentHelper.readCalendarEvent(context,
				eventDate);
		if (getEvent != null && !getEvent.isEmpty())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static long getDatamilliSeconds(String date)
	{
		if (date == null) return 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
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
		if (oldDate == null) { return 0; }
		return oldDate.getTime();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String formatDate(String milliSeconds)
	{
		if (milliSeconds == null) return "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(milliSeconds));
		return formatter.format(calendar.getTime());
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

package eoc.studio.voicecard.calendarview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.text.format.Time;

public class CalendarIntentHelper
{
	private static Map<String, String> item;
	private static ArrayList<Map<String, String>> data;
	//
	protected static final String EVENT_TYPE_YEARLY = "FREQ=YEARLY;WKST=SU";
	//
	private static final String CALENDAR_NAME = "VoiceCard";
	private static final int CALENDAR_ID_INDEX = 0, CALENDAR_ACCOUNT_NAME_INDEX = 1,
			CALENDAR_DISPLAY_NAME_INDEX = 2, CALENDAR_OWNER_ACCOUNT_INDEX = 3;
	public static final String[] CALENDAR_PROJECTION = new String[] { Calendars._ID,
			Calendars.ACCOUNT_NAME, Calendars.CALENDAR_DISPLAY_NAME, Calendars.OWNER_ACCOUNT };
	public static final int EVENT_ID_INDEX = 0, EVENT_TITLE_INDEX = 1,
			EVENTE_DESCRIPTION_INDEX = 2, EVENT_DTSTART_INDEX = 3, EVENT_DTEND_INDEX = 4,
			EVENT_LOCATION_INDEX = 4;
	public static final String[] EVENT_PROJECTION = new String[] { Events._ID, Events.TITLE,
			Events.DESCRIPTION, Events.DTSTART, Events.DTEND, Events.EVENT_LOCATION };
	public static final String[] INSTANCES_PROJECTION = new String[] { Instances.EVENT_ID,
			Instances.TITLE, Instances.START_DAY };
	public static final int INSTANCES_ID_INDEX = 0, INSTANCES_TITLE_INDEX = 1,
			INSTANCES_START_DAY_INDEX = 2;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void addVoiceCardCalendar(Context context)
	{
		if (getVoiceCardCalendar(context) == 0)
		{
			initCalendars(context);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static long getVoiceCardCalendar(Context context)
	{
		long calID = 0;
		Cursor cur = null;
		String where = null, selection[] = null;
		try
		{
			where = CALENDAR_PROJECTION[CALENDAR_DISPLAY_NAME_INDEX] + " = ?";
			selection = new String[] { CALENDAR_NAME };
			cur = context.getContentResolver().query(Calendars.CONTENT_URI, CALENDAR_PROJECTION,
					where, selection, null);
			if (cur != null && cur.getCount() > 0)
			{
				while (cur.moveToNext())
				{
					calID = cur.getLong(CALENDAR_ID_INDEX);
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("[CalendarIntentHelper][getVoiceCardCalendar]SQLException:" + e);
		}
		catch (Exception ex)
		{
			System.out.println("[CalendarIntentHelper][getVoiceCardCalendar]Exception:" + ex);
		}
		if (cur != null && !cur.isClosed())
		{
			cur.close();
		}
		return calID;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void initCalendars(Context activity)
	{
		TimeZone timeZone = TimeZone.getDefault();
		ContentValues value = new ContentValues();
		value.put(Calendars.OWNER_ACCOUNT, CALENDAR_NAME);
		value.put(Calendars.NAME, CALENDAR_NAME);
		value.put(Calendars.ACCOUNT_NAME, CALENDAR_NAME);
		value.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		value.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
		value.put(Calendars.VISIBLE, 1);
		value.put(Calendars.CALENDAR_COLOR, Color.RED);
		value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		value.put(Calendars.SYNC_EVENTS, 1);
		value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
		value.put(Calendars.OWNER_ACCOUNT, CALENDAR_NAME);
		value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);
		Uri calendarUri = Calendars.CONTENT_URI;
		calendarUri = calendarUri.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, CALENDAR_NAME)
				.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
				.build();
		activity.getContentResolver().insert(calendarUri, value);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean haveEvent(final Context context, final String eventDate)
	{
		boolean haveEvents = false;
		if (context != null && eventDate != null && !eventDate.equals(""))
		{
			Cursor cursor = null;
			final String getDataMilliSeconds = DataProcess.getDataMilliSeconds(eventDate
					+ DataProcess.DEFAULT_EVENT_TIME);
			//
			try
			{
				cursor = getCursor(context, getDataMilliSeconds);
				if (cursor != null && cursor.getCount() > 0)
				{
					haveEvents = true;
				}
			}
			catch (SQLException e)
			{
				System.out.println("[CalendarIntentHelper][haveEvent]SQLException:" + e);
			}
			catch (Exception ex)
			{
				System.out.println("[CalendarIntentHelper][haveEvent]Exception:" + ex);
			}
			if (cursor != null && !cursor.isClosed())
			{
				cursor.close();
			}
		}
		return haveEvents;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Map<String, String>> readCalendarEvent(final Context context,
			final String eventDate)
	{
		data = new ArrayList<Map<String, String>>();
		Cursor cursor = null;
		final String getDataMilliSeconds = DataProcess.getDataMilliSeconds(eventDate);
		try
		{
			cursor = getCursor(context, getDataMilliSeconds);
			if (cursor != null && cursor.getCount() > 0)
			{
				while (cursor.moveToNext())
				{
					item = new HashMap<String, String>();
					item.put(
							INSTANCES_PROJECTION[INSTANCES_ID_INDEX],
							(cursor.getString(INSTANCES_ID_INDEX) != null) ? cursor
									.getString(INSTANCES_ID_INDEX) : "");
					item.put(
							INSTANCES_PROJECTION[INSTANCES_TITLE_INDEX],
							(cursor.getString(INSTANCES_TITLE_INDEX) != null) ? cursor
									.getString(INSTANCES_TITLE_INDEX) : "");
					item.put(
							INSTANCES_PROJECTION[INSTANCES_START_DAY_INDEX],
							(cursor.getString(INSTANCES_START_DAY_INDEX) != null) ? cursor
									.getString(INSTANCES_START_DAY_INDEX) : "");
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
	private static Cursor getCursor(final Context context, final String getDataMilliSeconds)
	{
		Cursor cursor = null;
		String selection[] = null;
		final String where = INSTANCES_PROJECTION[INSTANCES_START_DAY_INDEX] + " = ?";
		//
		selection = new String[] { String.valueOf(Time.getJulianDay(
				Long.valueOf(getDataMilliSeconds), 0)) };
		cursor = context.getContentResolver().query(getInstancesUri(getDataMilliSeconds),
				INSTANCES_PROJECTION, where, selection, null);
		return cursor;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static Uri getInstancesUri(final String getDataMilliSeconds)
	{
		final Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		// StartDate
		ContentUris.appendId(builder, Long.valueOf(getDataMilliSeconds));
		// EndDate
		ContentUris.appendId(builder, Long.valueOf(getDataMilliSeconds));
		return builder.build();
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

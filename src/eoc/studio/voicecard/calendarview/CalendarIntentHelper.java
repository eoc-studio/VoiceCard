package eoc.studio.voicecard.calendarview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class CalendarIntentHelper
{
	private static Map<String, String> item;
	private static ArrayList<Map<String, String>> data;
	private static final boolean GET_SINGLE_DATA = true;
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
			EVENT_EVENT_LOCATION_INDEX = 4;
	public static final String[] EVENT_PROJECTION = new String[] { Events._ID, Events.TITLE,
			Events.DESCRIPTION, Events.DTSTART, Events.DTEND, Events.EVENT_LOCATION };

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
			where = CALENDAR_PROJECTION[CALENDAR_DISPLAY_NAME_INDEX] + " =? ";
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
		ArrayList<Map<String, String>> getEvent = CalendarIntentHelper.readCalendarEvent(context,
				eventDate + DataProcess.DEFAULT_EVENT_TIME, GET_SINGLE_DATA);
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
	public static ArrayList<Map<String, String>> readCalendarEvent(final Context context,
			final String eventDate)
	{
		return CalendarIntentHelper.readCalendarEvent(context, eventDate, !GET_SINGLE_DATA);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Map<String, String>> readCalendarEvent(final Context context,
			final String eventDate, final boolean getSingleData)
	{
		String getDataMilliSeconds = DataProcess.getDataMilliSeconds(eventDate);
		data = new ArrayList<Map<String, String>>();
		Cursor cursor = null;
		// String where = null, selection[] = null;
		// where = EVENT_PROJECTION[EVENT_DTSTART_INDEX] + " =? ";
		// selection = new String[] { getDataMilliSeconds };
		try
		{
			cursor = context.getContentResolver().query(Events.CONTENT_URI, EVENT_PROJECTION, null,
					null, null);
			if (cursor != null && cursor.getCount() > 0)
			{
				while (cursor.moveToNext())
				{
					if (DataProcess.formatDate(cursor.getString(EVENT_DTSTART_INDEX), "MMdd")
							.equals(DataProcess.formatDate(getDataMilliSeconds, "MMdd")))
					{
						item = new HashMap<String, String>();
						item.put(
								EVENT_PROJECTION[EVENT_ID_INDEX],
								(cursor.getString(EVENT_ID_INDEX) != null) ? cursor
										.getString(EVENT_ID_INDEX) : "");
						item.put(
								EVENT_PROJECTION[EVENT_TITLE_INDEX],
								(cursor.getString(EVENT_TITLE_INDEX) != null) ? cursor
										.getString(EVENT_TITLE_INDEX) : "");
						item.put(
								EVENT_PROJECTION[EVENTE_DESCRIPTION_INDEX],
								(cursor.getString(EVENTE_DESCRIPTION_INDEX) != null) ? cursor
										.getString(EVENTE_DESCRIPTION_INDEX) : "");
						item.put(
								EVENT_PROJECTION[EVENT_DTSTART_INDEX],
								(cursor.getString(EVENT_DTSTART_INDEX) != null) ? cursor
										.getString(EVENT_DTSTART_INDEX) : "");
						item.put(
								EVENT_PROJECTION[EVENT_EVENT_LOCATION_INDEX],
								(cursor.getString(EVENT_EVENT_LOCATION_INDEX) != null) ? cursor
										.getString(EVENT_EVENT_LOCATION_INDEX) : "");
						data.add(item);
						//
						if (getSingleData)
						{
							break;
						}
					}
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
}

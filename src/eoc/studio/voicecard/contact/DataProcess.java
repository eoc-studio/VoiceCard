package eoc.studio.voicecard.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eoc.studio.voicecard.R;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Event;

public class DataProcess
{
	private static ArrayList<Map<String, String>> data;
	private static Cursor cur = null;
	private static Cursor birthdayCur = null;
	private static Map<String, String> item;
	public final static String userId = "userId", userName = "userName", lookUpKey = "lookUpKey",
			birthdayDate = "birthdayDate";

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Map<String, String>> getContactNameAndBirthday(Context context)
	{
		return getContact(context, null, true);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Map<String, String>> getContact(Context context)
	{
		return getContact(context, null, false);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static ArrayList<Map<String, String>> getContact(Context context, String selectionName,
			boolean getBirthday)
	{
		data = null;
		if (context != null)
		{
			String where = null, selection[] = null, id = "", name = "", birthday = "", sort_key_alt = "", lookupKey = "";
			data = new ArrayList<Map<String, String>>();
			ContentResolver cr = context.getContentResolver();
			if (selectionName != null && !selectionName.equals(""))
			{
				where = "sort_key_alt like ? OR " + Contacts.DISPLAY_NAME + " like ?";
				selection = new String[] { selectionName + "%", selectionName + "%" };
			}
			else
			{
				where = null;
				selection = null;
			}
			try
			{
				cur = cr.query(Contacts.CONTENT_URI, null, where, selection, "sort_key_alt");
				if (cur != null && cur.getCount() > 0)
				{
					while (cur.moveToNext())
					{
						if (!ShowDialog.isShowLoadingDialog())
						{
							break;
						}
						id = cur.getString(cur.getColumnIndex(Contacts._ID));
						name = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME));
						sort_key_alt = cur.getString(cur.getColumnIndex("sort_key_alt"))
								.toUpperCase();
						lookupKey = cur.getString(cur.getColumnIndex(Contacts.LOOKUP_KEY));
						if (getBirthday)
						{
							birthday = getContactBirthday(context, id);
						}
						item = new HashMap<String, String>();
						item.put(userId, id);
						item.put(userName, name);
						item.put(lookUpKey, lookupKey);
						item.put(birthdayDate, birthday);
						data.add(item);
					}
				}
			}
			catch (SQLException e)
			{
				System.out.println("[DataProcess][getContact]SQLException:" + e);
			}
			catch (Exception ex)
			{
				System.out.println("[DataProcess][getContact]Exception:" + ex);
			}
			finally
			{
				if (cur != null && !cur.isClosed())
				{
					cur.close();
				}
			}
			if (data.isEmpty())
			{
				data = new ArrayList<Map<String, String>>();
				item = new HashMap<String, String>();
				item.put(userName, context.getResources().getString(R.string.noData));
				item.put(userId, "");
				data.add(item);
			}
		}
		return data;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static String getContactBirthday(Context context, String userId)
	{
		String where = null, birthday = "";
		ContentResolver cr = context.getContentResolver();
		try
		{
			String columns[] = { ContactsContract.CommonDataKinds.Event.START_DATE,
					ContactsContract.CommonDataKinds.Event.TYPE,
					ContactsContract.CommonDataKinds.Event.MIMETYPE };
			where = Event.TYPE + "=" + Event.TYPE_BIRTHDAY + " and " + Event.MIMETYPE + " = '"
					+ Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = "
					+ userId;
			birthdayCur = cr.query(ContactsContract.Data.CONTENT_URI, columns, where, null, null);
			if (birthdayCur != null && birthdayCur.getCount() > 0)
			{
				while (birthdayCur.moveToNext())
				{
					birthday = birthdayCur.getString(birthdayCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
				}
				if (birthdayCur != null && !birthdayCur.isClosed())
				{
					birthdayCur.close();
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("[DataProcess][getContactBirthday]SQLException:" + e);
		}
		catch (Exception ex)
		{
			System.out.println("[DataProcess][getContactBirthday]Exception:" + ex);
		}
		finally
		{
			if (birthdayCur != null && !birthdayCur.isClosed())
			{
				birthdayCur.close();
			}
		}
		return birthday;
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

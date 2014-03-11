package eoc.studio.voicecard.contact;

import android.provider.ContactsContract;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class DataProcess
{
    private static Cursor getPhoneTypeCur = null;

    public static final String USER_NAME_INDEX = "userName", PHONE_NUMBER_INDEX = "phoneNums",
            TEL_TYPE_INDEX = "telTypes", USER_ID_INDEX = "userId", LOOK_UP_KEY_INDEX = "lookUpKey";

    public static final int PROCESS_SELECTION_FRIEND = 99;
    public static final String PROCESS_SELECTION_FRIEND_LIST = "friendList";
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static final String[] getPhoneNumberAndType(Context context, String id)
    {
        String where = null, selection[] = null, getNumberAndType[] = null;
        String phoneNo1 = "", phoneNo2 = "", phoneNo3 = "", phoneNo4 = "";

        try
        {
            where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
            selection = new String[]
            { id };
            getPhoneTypeCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, where, selection, null);

            if (getPhoneTypeCur != null)
            {
                while (getPhoneTypeCur.moveToNext())
                {
                    int i = Integer.valueOf(getPhoneTypeCur.getString(getPhoneTypeCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                    if (i == 1)
                    {
                        phoneNo1 = getPhoneTypeCur.getString(getPhoneTypeCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo1 = DataProcess.removeInsignia(phoneNo1);
                    }
                    else if (i == 2)
                    {
                        phoneNo2 = getPhoneTypeCur.getString(getPhoneTypeCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo2 = DataProcess.removeInsignia(phoneNo2);
                    }
                    else if (i == 3)
                    {
                        phoneNo3 = getPhoneTypeCur.getString(getPhoneTypeCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo3 = DataProcess.removeInsignia(phoneNo3);
                    }
                    else if (i == 4)
                    {
                        phoneNo4 = getPhoneTypeCur.getString(getPhoneTypeCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo4 = DataProcess.removeInsignia(phoneNo4);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            System.out.println("[addressBookActivity][updataListData]SQLException:" + e);
        }
        catch (Exception ex)
        {
            System.out.println("[addressBookActivity][updataListData]Exception:" + ex);
        }
        finally
        {
            if (getPhoneTypeCur != null && !getPhoneTypeCur.isClosed())
            {
                getPhoneTypeCur.close();
            }
        }

        if (!phoneNo2.equals(""))
        {
            getNumberAndType = new String[]
            { phoneNo2, "CellPhone" };
        }
        else if (!phoneNo1.equals(""))
        {
            getNumberAndType = new String[]
            { phoneNo1, "Home" };
        }
        else if (!phoneNo3.equals(""))
        {
            getNumberAndType = new String[]
            { phoneNo3, "Office" };
        }
        else if (!phoneNo4.equals(""))
        {
            getNumberAndType = new String[]
            { phoneNo4, "Fax" };
        }
        return getNumberAndType;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static String removeInsignia(String number)
    {
        if (!number.equals("") && number.indexOf("-") != -1)
        {
            return number.replace("-", "");
        }
        return number;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
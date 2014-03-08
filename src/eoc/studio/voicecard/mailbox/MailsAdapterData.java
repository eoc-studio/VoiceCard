package eoc.studio.voicecard.mailbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MailsAdapterData
{
    private static final String TAG = "MailsAdapterData";
	private static final String DB_NAME = "mails.sqlite";

	private static final String DB_PATH = "/data/data/eoc.studio.voicecard/databases/";

	private static final String DATABASE_NAME = "mails.sqlite";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_TABLE = "mails";

	private static final String DATABASE_CREATE = "create table mails("
			+ "_id INTEGER PRIMARY KEY," + "card_id TEXT," + "owner_id TEXT," + "send_id TEXT," + "send_from TEXT,"
			+ "send_from_name TEXT," + "send_from_link TEXT," + "send_to TEXT," + "subject TEXT,"
			+ "body TEXT," + "font_size TEXT," + "font_color TEXT," + "img_link TEXT,"
			+ "img BLOB," + "speech TEXT," + "sign TEXT," + "send_time TEXT," + "new_state INTEGER"
			+ ");";

	public static final String ORDER_DESC = " desc";

	public static final String KEY_ROW_ID = "_id";
	
	public static final String KEY_CARD_ID = "card_id";
	
	public static final String KEY_OWNER_ID = "owner_id";

	public static final String KEY_SEND_ID = "send_id";

	public static final String KEY_SEND_FROM = "send_from";

	public static final String KEY_SEND_FROM_NAME = "send_from_name";

	public static final String KEY_SEND_FROM_LINK = "send_from_link";

	public static final String KEY_SEND_TO = "send_to";

	public static final String KEY_SUBJECT = "subject";

	public static final String KEY_BODY = "body";

	public static final String KEY_FONT_SIZE = "font_size";

	public static final String KEY_FONT_COLOR = "font_color";

	public static final String KEY_IMG_LINK = "img_link";

	public static final String KEY_IMG = "img";

	public static final String KEY_SPEECH = "speech";

	public static final String KEY_SIGN = "sign";

	public static final String KEY_SEND_TIME = "send_time";

	public static final String KEY_NEW_STATE = "new_state";

	public static final int NOTNEW = 0;

	public static final int NEW = 1;

	private Context context = null;

	private DatabaseHelper dbHelper;

	private SQLiteDatabase db;

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		Context context = null;
		public DatabaseHelper(Context context) throws IOException
		{

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
			createDatabase();
		}

		/**
		 * Creates empty database on system and rewrites it with existing
		 * database
		 * 
		 * @throws IOException
		 */
		public void createDatabase() throws IOException
		{

			boolean dbExist = checkDatabase();

			if (dbExist)
			{
				// do nothing, the database already exists;
				Log.e("DatabaseHelper", "createDatabase(): dbExist");
			}
			else
			{
				Log.e("DatabaseHelper", "createDatabase(): db is not Exist");
				this.getReadableDatabase();

				try
				{
					copyDatabase();
				}
				catch (IOException e)
				{
					Log.e("DatabaseHelper", "createDatabase(): Error copying database");
					throw new Error("Error copying database");
				}
			}
		}

		/**
		 * Check to see if a database exists
		 * 
		 * @return true if database exists, false otherwise;
		 */
		private boolean checkDatabase()
		{
			File dbFile = new File(DB_PATH + DB_NAME);
		    return dbFile.exists();
//			SQLiteDatabase checkDB = null;
//
//			try
//			{
//				String myPath = DB_PATH + DB_NAME;
//				File dbFile = context.getDatabasePath(DB_NAME);
//				
//				if (!dbFile.exists())
//				{
//					dbFile.getParentFile().mkdirs();
//				}
//
//				Log.v("DatabaseHelper", "Open sqlite db: " + dbFile.getAbsolutePath());
//
////				SQLiteDatabase mydb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
//				
//				
////				checkDB =SQLiteDatabase.openOrCreateDatabase(dbFile, null);
//				
//				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase. );
//			}
//			catch (SQLiteException e)
//			{
//				 e.printStackTrace();
//			}
//
//			if (checkDB != null) checkDB.close();
//
//			return checkDB != null ? true : false;
		}

		/**
		 * Copes database from assets-folder to system folder, where it can be
		 * accessed and handled. This is done by transferring byte-stream.
		 * 
		 * @throws IOException
		 */
		private void copyDatabase() throws IOException
		{

			// Open your local db as the input stream
			AssetManager assets = this.context.getAssets();
			InputStream myInput = assets.open(DB_NAME);

			String outFileName = DB_PATH + DB_NAME;

			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0)
				myOutput.write(buffer, 0, length);

			// close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		}

		public SQLiteDatabase openDataBase() throws SQLException
		{

			// Open the database
			String myPath = DB_PATH + DB_NAME;
			return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE );
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{

			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	/** Constructor */
	public MailsAdapterData(Context context)
	{

		this.context = context;
	}

	public MailsAdapterData open() throws SQLException
	{

		try
		{
			dbHelper = new DatabaseHelper(context);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		db = dbHelper.openDataBase();
		return this;
	}

	public void close()
	{

		if (dbHelper != null) dbHelper.close();
	}

	/** Get all items from database */
	public Cursor getAll()
	{

        return db.query(DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_CARD_ID, KEY_OWNER_ID, KEY_SEND_ID,
                KEY_SEND_FROM, KEY_SEND_FROM_NAME, KEY_SEND_FROM_LINK, KEY_SEND_TO, KEY_SUBJECT, KEY_BODY,
                KEY_FONT_SIZE, KEY_FONT_COLOR, KEY_IMG_LINK, KEY_IMG, KEY_SPEECH, KEY_SIGN, KEY_SEND_TIME,
                KEY_NEW_STATE }, null, null, null, null, KEY_SEND_TIME + ORDER_DESC);
    }

	/** Insert item to database */
    public long create(String cardId, String ownerId, String sendId, String sendFrom, String sendFromName,
            String sendFromLink, String sendTo, String subject, String body, String fontSize, String fontColor,
            String img_link, byte[] img, String speech, String sign, String send_time, int newState) {

        ContentValues args = new ContentValues();
        args.put(KEY_CARD_ID, cardId);
        args.put(KEY_OWNER_ID, ownerId);
        args.put(KEY_SEND_ID, sendId);
        args.put(KEY_SEND_FROM, sendFrom);
        args.put(KEY_SEND_FROM_NAME, sendFromName);
        args.put(KEY_SEND_FROM_LINK, sendFromLink);
        args.put(KEY_SEND_TO, sendTo);
        args.put(KEY_SUBJECT, subject);
        args.put(KEY_BODY, body);
        args.put(KEY_FONT_SIZE, fontSize);
        args.put(KEY_FONT_COLOR, fontColor);
        args.put(KEY_IMG_LINK, img_link);
        args.put(KEY_IMG, img);
        args.put(KEY_SPEECH, speech);
        args.put(KEY_SIGN, sign);
        args.put(KEY_SEND_TIME, send_time);
        args.put(KEY_NEW_STATE, newState);
        return db.insert(DATABASE_TABLE, null, args);
    }

	/** Delete one item from database */
	public boolean delete(String rowId)
	{

		if (db != null && db.isOpen())
			return db.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0;
		else return false;
	}

	/** Delete all item from database */
	public boolean delete()
	{

		if (db != null && db.isOpen())
			return db.delete(DATABASE_TABLE, null, null) > 0;
		else return false;
	}

	public boolean deleteSelected(Set<String> selectedMails)
	{

		if (db != null && db.isOpen())
		{
			int size = selectedMails.size();
			if (selectedMails != null && size > 0)
			{
				StringBuilder stb = new StringBuilder();
				stb.append(KEY_ROW_ID).append("=?");

				if (size > 1)
				{
					for (int i = 1; i < selectedMails.size(); i++)
					{
						stb.append(" OR ").append(KEY_ROW_ID).append("=?");
					}
				}

				db.delete(DATABASE_TABLE, stb.toString(), selectedMails.toArray(new String[size]));
				return true;
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	/** Query single entry */
	public Cursor get(String rowId) throws SQLException
	{

		if (db.isOpen())
		{
			Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_CARD_ID, KEY_OWNER_ID, KEY_SEND_ID,
					KEY_SEND_FROM, KEY_SEND_FROM_NAME, KEY_SEND_FROM_LINK, KEY_SEND_TO,
					KEY_SUBJECT, KEY_BODY, KEY_FONT_SIZE, KEY_FONT_COLOR, KEY_IMG_LINK, KEY_IMG,
					KEY_SPEECH, KEY_SIGN, KEY_SEND_TIME, KEY_NEW_STATE }, KEY_ROW_ID + "=" + rowId,
					null, null, null, null, null);
			if (cursor != null)
			{
			    return cursor;
			}
			return null;
		}
		else
		{
			return null;
		}
	}
	
	public Cursor getDatafromOwnerId(String ownerId) throws SQLException {
	       if (db.isOpen())
	        {
	            Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_CARD_ID, KEY_OWNER_ID, KEY_SEND_ID,
	                    KEY_SEND_FROM, KEY_SEND_FROM_NAME, KEY_SEND_FROM_LINK, KEY_SEND_TO,
	                    KEY_SUBJECT, KEY_BODY, KEY_FONT_SIZE, KEY_FONT_COLOR, KEY_IMG_LINK, KEY_IMG,
	                    KEY_SPEECH, KEY_SIGN, KEY_SEND_TIME, KEY_NEW_STATE }, KEY_OWNER_ID + "=" + ownerId,
	                    null, null, null, null, null);
	            if (cursor != null)
	            {
	                return cursor;
	            }
	            return null;
	        }
	        else
	        {
	            return null;
	        }
	}

	/** Update the database */
	public boolean update(String rowId, String cardId, String ownerId, String sendId, String sendFrom, String sendFromName,
			String sendFromLink, String sendTo, String subject, String body, String fontSize,
			String fontColor, String img_link, byte[] img, String speech, String sign,
			String send_time, int newState)
	{

		if (db.isOpen())
		{
			ContentValues args = new ContentValues();
			args.put(KEY_CARD_ID, cardId);
			args.put(KEY_OWNER_ID, ownerId);
			args.put(KEY_SEND_ID, sendId);
			args.put(KEY_SEND_FROM, sendFrom);
			args.put(KEY_SEND_FROM_NAME, sendFromName);
			args.put(KEY_SEND_FROM_LINK, sendFromLink);
			args.put(KEY_SEND_TO, sendTo);
			args.put(KEY_SUBJECT, subject);
			args.put(KEY_BODY, body);
			args.put(KEY_FONT_SIZE, fontSize);
			args.put(KEY_FONT_COLOR, fontColor);
			args.put(KEY_IMG_LINK, img_link);
			args.put(KEY_IMG, img);
			args.put(KEY_SPEECH, speech);
			args.put(KEY_SIGN, sign);
			args.put(KEY_SEND_TIME, send_time);
			args.put(KEY_NEW_STATE, newState);
			return db.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
		}
		else
		{
			return false;
		}
	}

	public boolean updateImg(String rowId, byte[] img)
	{

		if (db.isOpen())
		{
			ContentValues args = new ContentValues();
			args.put(KEY_IMG, img);
			return db.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
		}
		else
		{
			return false;
		}
	}
	
	public boolean updateImgfromOwerId(String owerId, byte[] img)
    {

        if (db.isOpen())
        {
            ContentValues args = new ContentValues();
            args.put(KEY_IMG, img);
            return db.update(DATABASE_TABLE, args, KEY_OWNER_ID + "=" + owerId, null) > 0;
        }
        else
        {
            return false;
        }
    }

	/** Query single entry with title and date */
	// For test
	public Cursor getTitleandDate(String subject, String sendTime) throws SQLException
	{

		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("SELECT * FROM mails WHERE " + KEY_SUBJECT + " = " + "\""
					+ subject + "\"" + " AND " + KEY_SEND_TIME + " = " + "\"" + sendTime + "\"",
					null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}
			return cursor;
		}
		else
		{
			return null;
		}
	}
	
	public int getLocalUnReadMailCount()throws SQLException{
		int count = 0;
		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("SELECT * FROM mails WHERE " + KEY_NEW_STATE + " = " + "1",
					null);
			if (cursor != null)
			{
				count = cursor.getCount();
				
			}
			return count;
		}
		else 
		{
			return count;
		}
	}
	
    public int getLocalUnReadMailCount(String owerId) throws SQLException {
        int count = 0;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT * FROM mails WHERE " + KEY_NEW_STATE + " = " + "1 AND " + KEY_OWNER_ID
                    + "=" + owerId, null);
            if (cursor != null) {
                count = cursor.getCount();

            }
            return count;
        } else {
            return count;
        }
    }
	
    public boolean updateNewState(String rowId, int state) {
        if (db.isOpen()) {
            ContentValues args = new ContentValues();
            args.put(KEY_NEW_STATE, state);
            return db.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
        } else {
            return false;
        }
    }
}

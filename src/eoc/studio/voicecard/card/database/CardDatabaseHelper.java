package eoc.studio.voicecard.card.database;

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

public class CardDatabaseHelper
{
	private static final String DB_NAME = "cards.sqlite";

	private static final String DB_PATH = "/data/data/eoc.studio.voicecard/databases/";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_TABLE_CARD = "card";

	private static final String DATABASE_TABLE_CATEGORY = "category";

	public static final String ORDER_DESC = " desc";

	public static final int DPI_MDPI = 0;

	public static final int DPI_HPPI = 1;

	public static final int DPI_XHDPI = 2;

	public static final int DPI_XXHDPI = 3;

	public static final String KEY_ROW_ID = "_id";

	public static final String CAT_ID = "cat_id";

	public static final String CAT_NAME = "cat_name";

	public static final String CAT_ENABLE = "cat_enable";

	public static final String CAT_IMG_MDPI_LOCAL_PATH = "cat_img_mdpi_local_path";

	public static final String CAT_IMG_HDPI_LOCAL_PATH = "cat_img_hdpi_local_path";

	public static final String CAT_IMG_XHDPI_LOCAL_PATH = "cat_img_xhdpi_local_path";

	public static final String CAT_IMG_XXHDPI_LOCAL_PATH = "cat_img_xxhdpi_local_path";

	public static final String CAT_IMG_MDPI = "cat_img_mdpi";

	public static final String CAT_IMG_HDPI = "cat_img_hdpi";

	public static final String CAT_IMG_XHDPI = "cat_img_xhdpi";

	public static final String CAT_IMG_XXHDPI = "cat_img_xxhdpi";

	public static final String CAT_SORT = "cat_sort";

	public static final String CAT_CREATED_DATE = "cat_created_date";

	private Context context = null;

	private DatabaseHelper dbHelper;

	private SQLiteDatabase db;

	/** Constructor */
	public CardDatabaseHelper(Context context)
	{

		this.context = context;
	}

	public CardDatabaseHelper open() throws SQLException
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

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		Context context = null;

		public DatabaseHelper(Context context) throws IOException
		{

			super(context, DB_NAME, null, DATABASE_VERSION);
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

		private boolean checkDatabase()
		{

			File dbFile = new File(DB_PATH + DB_NAME);
			return dbFile.exists();
		}

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
			return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{

			// do nothing
			// db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{

			// do nothing
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CARD);
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CATEGORY);
			onCreate(db);
		}

	}

	public long createCategoryRow(String catId, String catName, String catEnable,
			String catImgMdpi, String catImgHdpi, String catImgXdpi, String catImgXXdpi,
			String catImgMdpiLocalPath, String catImgHdpiLocalPath, String catImgXdpiLocalPath,
			String catImgXXdpiLocalPath)
	{

		ContentValues args = new ContentValues();
		if (catId != null) args.put(CAT_ID, Integer.valueOf(catId));
		if (catName != null) args.put(CAT_NAME, catName);

		if (catImgMdpi != null) args.put(CAT_IMG_MDPI, catImgMdpi);
		if (catImgHdpi != null) args.put(CAT_IMG_HDPI, catImgHdpi);
		if (catImgXdpi != null) args.put(CAT_IMG_XHDPI, catImgXdpi);
		if (catImgXXdpi != null) args.put(CAT_IMG_XXHDPI, catImgXXdpi);
		if (catImgMdpiLocalPath != null) args.put(CAT_IMG_MDPI_LOCAL_PATH, catImgMdpiLocalPath);
		if (catImgHdpiLocalPath != null) args.put(CAT_IMG_HDPI_LOCAL_PATH, catImgHdpiLocalPath);
		if (catImgXdpiLocalPath != null) args.put(CAT_IMG_XHDPI_LOCAL_PATH, catImgXdpiLocalPath);
		if (catImgXXdpiLocalPath != null)
			args.put(CAT_IMG_XXHDPI_LOCAL_PATH, catImgXXdpiLocalPath);

		if (catEnable != null)
		{

			if (catEnable.equals("Y"))
			{
				args.put(CAT_ENABLE, 1);
			}
			else if (catEnable.equals("N"))
			{
				args.put(CAT_ENABLE, 0);
			}
			else
			{
				args.put(CAT_ENABLE, 0);
			}

		}
		return db.insert(DATABASE_TABLE_CATEGORY, null, args);
	}

	public boolean updateCategoryRow(String catId, String catName, String catEnable,
			String catImgMdpi, String catImgHdpi, String catImgXdpi, String catImgXXdpi,
			String catImgMdpiLocalPath, String catImgHdpiLocalPath, String catImgXdpiLocalPath,
			String catImgXXdpiLocalPath)
	{

		if (db.isOpen())
		{
			ContentValues args = new ContentValues();
			if (catId != null) args.put(CAT_ID, Integer.valueOf(catId));
			if (catName != null) args.put(CAT_NAME, catName);

			if (catImgMdpi != null) args.put(CAT_IMG_MDPI, catImgMdpi);
			if (catImgHdpi != null) args.put(CAT_IMG_HDPI, catImgHdpi);
			if (catImgXdpi != null) args.put(CAT_IMG_XHDPI, catImgXdpi);
			if (catImgXXdpi != null) args.put(CAT_IMG_XXHDPI, catImgXXdpi);
			if (catImgMdpiLocalPath != null)
				args.put(CAT_IMG_MDPI_LOCAL_PATH, catImgMdpiLocalPath);
			if (catImgHdpiLocalPath != null)
				args.put(CAT_IMG_HDPI_LOCAL_PATH, catImgHdpiLocalPath);
			if (catImgXdpiLocalPath != null)
				args.put(CAT_IMG_XHDPI_LOCAL_PATH, catImgXdpiLocalPath);
			if (catImgXXdpiLocalPath != null)
				args.put(CAT_IMG_XXHDPI_LOCAL_PATH, catImgXXdpiLocalPath);

			if (catEnable != null)
			{

				if (catEnable.equals("Y"))
				{
					args.put(CAT_ENABLE, 1);
				}
				else if (catEnable.equals("N"))
				{
					args.put(CAT_ENABLE, 0);
				}
				else
				{
					args.put(CAT_ENABLE, 0);
				}

			}
			return db.update(DATABASE_TABLE_CATEGORY, args, CAT_ID + "=" + Integer.valueOf(catId),
					null) > 0;
		}
		else
		{
			return false;
		}
	}

	public String getCategoryImgLocalPath(String catId, int dpi) throws SQLException
	{

		String localPathColumn;
		switch (dpi)
		{
		case DPI_MDPI:
			localPathColumn = CAT_IMG_MDPI_LOCAL_PATH;
			break;
		case DPI_HPPI:
			localPathColumn = CAT_IMG_HDPI_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			localPathColumn = CAT_IMG_XXHDPI_LOCAL_PATH;
			break;
		default:
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		}

		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY,
					new String[] { localPathColumn }, CAT_ID + "=" + Integer.valueOf(catId), null,
					null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}
			return cursor.getString(cursor.getColumnIndexOrThrow(localPathColumn));

		}
		else
		{
			return null;
		}
	}

	public String getCategoryImgURL(String catId, int dpi) throws SQLException
	{
		String urlColumn;
		switch (dpi)
		{
		case DPI_MDPI:
			urlColumn = CAT_IMG_MDPI;
			break;
		case DPI_HPPI:
			urlColumn = CAT_IMG_HDPI;
			break;
		case DPI_XHDPI:
			urlColumn = CAT_IMG_XHDPI;
			break;
		case DPI_XXHDPI:
			urlColumn = CAT_IMG_XXHDPI;
			break;
		default:
			urlColumn = CAT_IMG_XHDPI;
			break;
		}

		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY,
					new String[] { urlColumn }, CAT_ID + "=" + Integer.valueOf(catId), null,
					null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}
			return cursor.getString(cursor.getColumnIndexOrThrow(urlColumn));

		}
		else
		{
			return null;
		}
	}
	
	
	/** Delete one item from database */
	public boolean deleteCardTableByRowId(String rowId)
	{

		if (db != null && db.isOpen())
			return db.delete(DATABASE_TABLE_CARD, KEY_ROW_ID + "=" + rowId, null) > 0;
		else return false;
	}

	public boolean deleteCategoryTableByRowId(String rowId)
	{

		if (db != null && db.isOpen())
			return db.delete(DATABASE_TABLE_CATEGORY, KEY_ROW_ID + "=" + rowId, null) > 0;
		else return false;
	}

	/** Delete all item from database */
	public boolean deleteCardTable()
	{

		if (db != null && db.isOpen())
			return db.delete(DATABASE_TABLE_CARD, null, null) > 0;
		else return false;
	}

	public boolean deleteCategoryTable()
	{

		if (db != null && db.isOpen())
			return db.delete(DATABASE_TABLE_CATEGORY, null, null) > 0;
		else return false;
	}

	public void close()
	{

		if (dbHelper != null) dbHelper.close();
	}

}

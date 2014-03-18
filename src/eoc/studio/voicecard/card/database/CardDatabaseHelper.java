package eoc.studio.voicecard.card.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

import eoc.studio.voicecard.card.Card;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.DisplayMetrics;
import android.util.Log;

public class CardDatabaseHelper
{
	private final static String TAG = "CardDatabaseHelper";

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

	// Table category
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

	public static final String CAT_EDITED_DATE = "cat_edited_date";

	public static final String CAT_EDITED_DATE_LOCAL = "cat_edited_date_local"; 

	// Table card
	public static final String CARD_MDPI_RIGHT_LOCAL_PATH = "mdpi_right_local_path";

	public static final String CARD_MDPI_OPEN_LOCAL_PATH = "mdpi_open_local_path";

	public static final String CARD_MDPI_LEFT_LOCAL_PATH = "mdpi_left_local_path";

	public static final String CARD_MDPI_CLOSE_LOCAL_PATH = "mdpi_close_local_path";

	public static final String CARD_MDPI_COVER_LOCAL_PATH = "mdpi_cover_local_path";

	public static final String CARD_HDPI_RIGHT_LOCAL_PATH = "hdpi_right_local_path";

	public static final String CARD_HDPI_OPEN_LOCAL_PATH = "hdpi_open_local_path";

	public static final String CARD_HDPI_LEFT_LOCAL_PATH = "hdpi_left_local_path";

	public static final String CARD_HDPI_COVER_LOCAL_PATH = "hdpi_cover_local_path";

	public static final String CARD_HDPI_CLOSE_LOCAL_PATH = "hdpi_close_local_path";

	public static final String CARD_XHDPI_RIGHT_LOCAL_PATH = "xhdpi_right_local_path";

	public static final String CARD_XHDPI_OPEN_LOCAL_PATH = "xhdpi_open_local_path";

	public static final String CARD_XHDPI_LEFT_LOCAL_PATH = "xhdpi_left_local_path";

	public static final String CARD_XHDPI_COVER_LOCAL_PATH = "xhdpi_cover_local_path";

	public static final String CARD_XHDPI_CLOSE_LOCAL_PATH = "xhdpi_close_local_path";

	public static final String CARD_XXHDPI_RIGHT_LOCAL_PATH = "xxhdpi_right_local_path";

	public static final String CARD_XXHDPI_OPEN_LOCAL_PATH = "xxhdpi_open_local_path";

	public static final String CARD_XXHDPI_LEFT_LOCAL_PATH = "xxhdpi_left_local_path";

	public static final String CARD_XXHDPI_COVER_LOCAL_PATH = "xxhdpi_cover_local_path";

	public static final String CARD_XXHDPI_CLOSE_LOCAL_PATH = "xxhdpi_close_local_path";

	public static final String CARD_CREATED_DATE = "card_created_date";

	public static final String CARD_XXHDPI_RIGHT = "xxhdpi_right";

	public static final String CARD_XXHDPI_OPEN = "xxhdpi_open";

	public static final String CARD_XXHDPI_LEFT = "xxhdpi_left";

	public static final String CARD_XXHDPI_COVER = "xxhdpi_cover";

	public static final String CARD_XXHDPI_CLOSE = "xxhdpi_close";

	public static final String CARD_MDPI_RIGHT = "mdpi_right";

	public static final String CARD_MDPI_OPEN = "mdpi_open";

	public static final String CARD_MDPI_LEFT = "mdpi_left";

	public static final String CARD_MDPI_COVER = "mdpi_cover";

	public static final String CARD_MDPI_CLOSE = "mdpi_close";

	public static final String CARD_HDPI_RIGHT = "hdpi_right";

	public static final String CARD_HDPI_OPEN = "hdpi_open";

	public static final String CARD_HDPI_LEFT = "hdpi_left";

	public static final String CARD_HDPI_COVER = "hdpi_cover";

	public static final String CARD_HDPI_CLOSE = "hdpi_close";

	public static final String CARD_XHDPI_RIGHT = "xhdpi_right";

	public static final String CARD_XHDPI_OPEN = "xhdpi_open";

	public static final String CARD_XHDPI_LEFT = "xhdpi_left";

	public static final String CARD_XHDPI_COVER = "xhdpi_cover";

	public static final String CARD_XHDPI_CLOSE = "xhdpi_close";

	public static final String CARD_FONT_COLOR = "card_font_color";

	public static final String CARD_ENABLE = "card_enable";

	public static final String CARD_ID = "card_id";

	public static final String CARD_NAME = "card_name";

	public static final String CARD_CAT_ID = "cat_id";

	public static final String CARD_FAVORITE_ENABLE = "favorite_enable";

	public static final String CARD_EDITED_DATE_LOCAL = "card_edited_date_local";

	public static final String CARD_EDITED_DATE = "card_edited_date";

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

	public Boolean setFavoriteCardByCardID(int cardID)
	{

		if (db.isOpen())
		{
			ContentValues args = new ContentValues();
			args.put(CARD_FAVORITE_ENABLE, 1);
			return db.update(DATABASE_TABLE_CARD, args, CARD_ID + "=" + cardID, null) > 0;
		}
		else
		{
			return false;
		}

	}

	public Boolean setNonFavoriteCardByCardID(int cardID)
	{

		if (db.isOpen())
		{
			ContentValues args = new ContentValues();
			args.put(CARD_FAVORITE_ENABLE, 0);
			return db.update(DATABASE_TABLE_CARD, args, CARD_ID + "=" + cardID, null) > 0;
		}
		else
		{
			return false;
		}

	}

	public Card getCardByCardID(int cardID, int dpi)
	{

		Log.d(TAG, "getCardByCardID()");
		CardAssistant cardAssistant;
		Card card;

		cardAssistant = getCardAssistantByCardID(cardID, dpi);

		int catID = cardAssistant.getCategoryID();
		Log.d(TAG, "cardAssistant.getCategoryID() " + cardAssistant.getCategoryID());

		CategoryAssistant categoryAssistant = getCategoryByCatId(catID, dpi);
		if (categoryAssistant != null)
		{
			Log.d(TAG, "if(categoryAssistant!=null):" + categoryAssistant.toString());
		}

		card = new Card(cardAssistant.getCardID(), getCategoryByCatId(catID, dpi),
				cardAssistant.getCardName(), cardAssistant.getCloseLocalPath(),
				cardAssistant.getOpenLocalPath(), cardAssistant.getCoverLocalPath(),
				cardAssistant.getLeftLocalPath(), cardAssistant.getRightLocalPath(),
				cardAssistant.getCardFontColor());

		if (card != null)
		{
			Log.d(TAG, "if(card!=null):" + card.toString());
		}
		return card;

	}

	public CardAssistant getCardAssistantByCardID(int cardID, int dpi)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}
		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
					CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR, closeURLColumn, closeLocalPathColumn,
					coverURLColumn, coverLocalPathColumn, leftURLColumn, leftLocalPathColumn,
					openURLColumn, openLocalPathColumn, rightURLColumn, rightLocalPathColumn },
					CARD_ENABLE + "=1 and " + CARD_ID + "=" + cardID, null, null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			CardAssistant cardAssistant = new CardAssistant();

			cardAssistant.setCardID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_ID)));
			cardAssistant.setCardFontColor(cursor.getInt(cursor
					.getColumnIndexOrThrow(CARD_FONT_COLOR)));
			cardAssistant.setCardName(cursor.getString(cursor.getColumnIndexOrThrow(CARD_NAME)));
			cardAssistant.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_CAT_ID)));
			cardAssistant.setCloseLocalPath(cursor.getString(cursor
					.getColumnIndexOrThrow(closeLocalPathColumn)));
			cardAssistant
					.setCloseURL(cursor.getString(cursor.getColumnIndexOrThrow(closeURLColumn)));
			cardAssistant.setCoverLocalPath(cursor.getString(cursor
					.getColumnIndexOrThrow(coverLocalPathColumn)));
			cardAssistant
					.setCoverURL(cursor.getString(cursor.getColumnIndexOrThrow(coverURLColumn)));
			cardAssistant.setLeftLocalPath(cursor.getString(cursor
					.getColumnIndexOrThrow(leftLocalPathColumn)));
			cardAssistant.setLeftURL(cursor.getString(cursor.getColumnIndexOrThrow(leftURLColumn)));
			cardAssistant.setOpenLocalPath(cursor.getString(cursor
					.getColumnIndexOrThrow(openLocalPathColumn)));
			cardAssistant.setOpenURL(cursor.getString(cursor.getColumnIndexOrThrow(openURLColumn)));
			cardAssistant.setRightLocalPath(cursor.getString(cursor
					.getColumnIndexOrThrow(rightLocalPathColumn)));
			cardAssistant
					.setRightURL(cursor.getString(cursor.getColumnIndexOrThrow(rightURLColumn)));

			if (cursor != null)
			{
				cursor.close();
			}
			return cardAssistant;
		}
		else
		{
			return null;
		}

	}

	public CategoryAssistant getCategoryByCatId(int CatId, int dpi)
	{

		ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();
		String urlColumn;
		String localPathColumn;
		switch (dpi)
		{
		case DPI_MDPI:
			urlColumn = CAT_IMG_MDPI;
			localPathColumn = CAT_IMG_MDPI_LOCAL_PATH;
			break;
		case DPI_HPPI:
			urlColumn = CAT_IMG_HDPI;
			localPathColumn = CAT_IMG_HDPI_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			urlColumn = CAT_IMG_XXHDPI;
			localPathColumn = CAT_IMG_XXHDPI_LOCAL_PATH;
			break;
		default:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		}

		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY, new String[] { CAT_ID,
					CAT_NAME, urlColumn, localPathColumn }, CAT_ENABLE + "=1 and " + CAT_ID + "="
					+ CatId, null, null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			CategoryAssistant categoryAssistant = new CategoryAssistant();
			categoryAssistant.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CAT_ID)));

			categoryAssistant.setCategoryName(cursor.getString(cursor
					.getColumnIndexOrThrow(CAT_NAME)));

			categoryAssistant.setCategoryURL(cursor.getString(cursor
					.getColumnIndexOrThrow(urlColumn)));

			categoryAssistant.setCategoryLoocalPath(cursor.getString(cursor
					.getColumnIndexOrThrow(localPathColumn)));

			categoryAssistantList.add(categoryAssistant);

			if (cursor != null)
			{
				cursor.close();
			}
			return categoryAssistant;
		}
		else
		{
			return null;
		}

	}

	public int getEnabledFavoriteCardListCount()
	{

		int count = 0;
		if (db.isOpen())
		{
			Cursor cursor = db.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
					CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR }, CARD_ENABLE + "=1 and "
					+ CARD_FAVORITE_ENABLE + "=1", null, null, null, null, null);
			if (cursor != null)
			{
				count = cursor.getCount();
				cursor.close();
			}
			return count;
		}
		else
		{
			return 0;
		}

	}

	public ArrayList<CardAssistant> getEnabledFavoriteCardList(int dpi)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}
		ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();
		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
					CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR, closeURLColumn, closeLocalPathColumn,
					coverURLColumn, coverLocalPathColumn, leftURLColumn, leftLocalPathColumn,
					openURLColumn, openLocalPathColumn, rightURLColumn, rightLocalPathColumn },
					CARD_ENABLE + "=1 and " + CARD_FAVORITE_ENABLE + "=1", null, null, null, null,
					null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CardAssistant cardAssistant = new CardAssistant();

				cardAssistant.setCardID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_ID)));
				cardAssistant.setCardFontColor(cursor.getInt(cursor
						.getColumnIndexOrThrow(CARD_FONT_COLOR)));
				cardAssistant
						.setCardName(cursor.getString(cursor.getColumnIndexOrThrow(CARD_NAME)));
				cardAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_CAT_ID)));
				cardAssistant.setCloseLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(closeLocalPathColumn)));
				cardAssistant.setCloseURL(cursor.getString(cursor
						.getColumnIndexOrThrow(closeURLColumn)));
				cardAssistant.setCoverLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(coverLocalPathColumn)));
				cardAssistant.setCoverURL(cursor.getString(cursor
						.getColumnIndexOrThrow(coverURLColumn)));
				cardAssistant.setLeftLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(leftLocalPathColumn)));
				cardAssistant.setLeftURL(cursor.getString(cursor
						.getColumnIndexOrThrow(leftURLColumn)));
				cardAssistant.setOpenLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(openLocalPathColumn)));
				cardAssistant.setOpenURL(cursor.getString(cursor
						.getColumnIndexOrThrow(openURLColumn)));
				cardAssistant.setRightLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(rightLocalPathColumn)));
				cardAssistant.setRightURL(cursor.getString(cursor
						.getColumnIndexOrThrow(rightURLColumn)));

				cardAssistantList.add(cardAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return cardAssistantList;
		}
		else
		{
			return null;
		}

	}

	public ArrayList<CardAssistant> getEnabledCardListByCategory(
			CategoryAssistant categoryAssistant, int dpi)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}
		ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();
		if (db.isOpen())
		{

			Cursor cursor = db
					.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
							CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR, closeURLColumn,
							closeLocalPathColumn, coverURLColumn, coverLocalPathColumn,
							leftURLColumn, leftLocalPathColumn, openURLColumn, openLocalPathColumn,
							rightURLColumn, rightLocalPathColumn }, CARD_ENABLE + "=1 and "
							+ CARD_CAT_ID + "=" + categoryAssistant.getCategoryID(), null, null,
							null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CardAssistant cardAssistant = new CardAssistant();

				cardAssistant.setCardID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_ID)));
				cardAssistant.setCardFontColor(cursor.getInt(cursor
						.getColumnIndexOrThrow(CARD_FONT_COLOR)));
				cardAssistant
						.setCardName(cursor.getString(cursor.getColumnIndexOrThrow(CARD_NAME)));
				cardAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_CAT_ID)));
				cardAssistant.setCloseLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(closeLocalPathColumn)));
				cardAssistant.setCloseURL(cursor.getString(cursor
						.getColumnIndexOrThrow(closeURLColumn)));
				cardAssistant.setCoverLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(coverLocalPathColumn)));
				cardAssistant.setCoverURL(cursor.getString(cursor
						.getColumnIndexOrThrow(coverURLColumn)));
				cardAssistant.setLeftLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(leftLocalPathColumn)));
				cardAssistant.setLeftURL(cursor.getString(cursor
						.getColumnIndexOrThrow(leftURLColumn)));
				cardAssistant.setOpenLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(openLocalPathColumn)));
				cardAssistant.setOpenURL(cursor.getString(cursor
						.getColumnIndexOrThrow(openURLColumn)));
				cardAssistant.setRightLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(rightLocalPathColumn)));
				cardAssistant.setRightURL(cursor.getString(cursor
						.getColumnIndexOrThrow(rightURLColumn)));

				cardAssistantList.add(cardAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return cardAssistantList;
		}
		else
		{
			return null;
		}

	}

	public ArrayList<CardAssistant> getEnabledCard(int dpi)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}

		ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();
		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
					CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR, closeURLColumn, closeLocalPathColumn,
					coverURLColumn, coverLocalPathColumn, leftURLColumn, leftLocalPathColumn,
					openURLColumn, openLocalPathColumn, rightURLColumn, rightLocalPathColumn },
					CARD_ENABLE + "=1", null, null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CardAssistant cardAssistant = new CardAssistant();

				cardAssistant.setCardID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_ID)));
				cardAssistant.setCardFontColor(cursor.getInt(cursor
						.getColumnIndexOrThrow(CARD_FONT_COLOR)));
				cardAssistant
						.setCardName(cursor.getString(cursor.getColumnIndexOrThrow(CARD_NAME)));
				cardAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_CAT_ID)));
				cardAssistant.setCloseLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(closeLocalPathColumn)));
				cardAssistant.setCloseURL(cursor.getString(cursor
						.getColumnIndexOrThrow(closeURLColumn)));
				cardAssistant.setCoverLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(coverLocalPathColumn)));
				cardAssistant.setCoverURL(cursor.getString(cursor
						.getColumnIndexOrThrow(coverURLColumn)));
				cardAssistant.setLeftLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(leftLocalPathColumn)));
				cardAssistant.setLeftURL(cursor.getString(cursor
						.getColumnIndexOrThrow(leftURLColumn)));
				cardAssistant.setOpenLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(openLocalPathColumn)));
				cardAssistant.setOpenURL(cursor.getString(cursor
						.getColumnIndexOrThrow(openURLColumn)));
				cardAssistant.setRightLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(rightLocalPathColumn)));
				cardAssistant.setRightURL(cursor.getString(cursor
						.getColumnIndexOrThrow(rightURLColumn)));

				cardAssistantList.add(cardAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return cardAssistantList;
		}
		else
		{
			return null;
		}

	}

	public ArrayList<CardAssistant> getEnabledAndLocalIsNullCard(int dpi)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}

		ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();
		if (db.isOpen())
		{

			String select = CARD_ENABLE + "=1 " + "AND (" + closeLocalPathColumn + " is null "
					+ "OR " + coverLocalPathColumn + " is null " + "OR " + leftLocalPathColumn
					+ " is null " + "OR " + openLocalPathColumn + " is null " + "OR "
					+ rightLocalPathColumn + " is null " + ")";
			Log.d(TAG, "getEnabledAndLocalIsNullCard() selection is: " + select);
			Cursor cursor = db.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
					CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR, closeURLColumn, closeLocalPathColumn,
					coverURLColumn, coverLocalPathColumn, leftURLColumn, leftLocalPathColumn,
					openURLColumn, openLocalPathColumn, rightURLColumn, rightLocalPathColumn },
					select, null, null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CardAssistant cardAssistant = new CardAssistant();

				cardAssistant.setCardID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_ID)));
				cardAssistant.setCardFontColor(cursor.getInt(cursor
						.getColumnIndexOrThrow(CARD_FONT_COLOR)));
				cardAssistant
						.setCardName(cursor.getString(cursor.getColumnIndexOrThrow(CARD_NAME)));
				cardAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_CAT_ID)));
				cardAssistant.setCloseLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(closeLocalPathColumn)));
				cardAssistant.setCloseURL(cursor.getString(cursor
						.getColumnIndexOrThrow(closeURLColumn)));
				cardAssistant.setCoverLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(coverLocalPathColumn)));
				cardAssistant.setCoverURL(cursor.getString(cursor
						.getColumnIndexOrThrow(coverURLColumn)));
				cardAssistant.setLeftLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(leftLocalPathColumn)));
				cardAssistant.setLeftURL(cursor.getString(cursor
						.getColumnIndexOrThrow(leftURLColumn)));
				cardAssistant.setOpenLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(openLocalPathColumn)));
				cardAssistant.setOpenURL(cursor.getString(cursor
						.getColumnIndexOrThrow(openURLColumn)));
				cardAssistant.setRightLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(rightLocalPathColumn)));
				cardAssistant.setRightURL(cursor.getString(cursor
						.getColumnIndexOrThrow(rightURLColumn)));

				cardAssistantList.add(cardAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return cardAssistantList;
		}
		else
		{
			return null;
		}
	}

	public ArrayList<CardAssistant> getLocalDateIsNullOrLocalPathIsNullCard(int dpi)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}

		ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();
		if (db.isOpen())
		{

			String select = "(" + closeLocalPathColumn + " is null " + "OR " + coverLocalPathColumn
					+ " is null " + "OR " + leftLocalPathColumn + " is null " + "OR "
					+ openLocalPathColumn + " is null " + "OR " + rightLocalPathColumn
					+ " is null " + "OR " + CARD_EDITED_DATE_LOCAL + "<>" + CARD_EDITED_DATE
					+ " OR " + CARD_EDITED_DATE_LOCAL + " is null " + ")";
			Log.d(TAG, "getLocalDateIsNullOrLocalPathIsNullCard() selection is: " + select);
			Cursor cursor = db.query(true, DATABASE_TABLE_CARD, new String[] { CARD_ID, CARD_NAME,
					CARD_CAT_ID, CARD_NAME, CARD_FONT_COLOR, closeURLColumn, closeLocalPathColumn,
					coverURLColumn, coverLocalPathColumn, leftURLColumn, leftLocalPathColumn,
					openURLColumn, openLocalPathColumn, rightURLColumn, rightLocalPathColumn,CARD_EDITED_DATE_LOCAL,CARD_EDITED_DATE},
					select, null, null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CardAssistant cardAssistant = new CardAssistant();

				cardAssistant.setCardID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_ID)));
				cardAssistant.setCardFontColor(cursor.getInt(cursor
						.getColumnIndexOrThrow(CARD_FONT_COLOR)));
				cardAssistant
						.setCardName(cursor.getString(cursor.getColumnIndexOrThrow(CARD_NAME)));
				cardAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_CAT_ID)));
				cardAssistant.setCloseLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(closeLocalPathColumn)));
				cardAssistant.setCloseURL(cursor.getString(cursor
						.getColumnIndexOrThrow(closeURLColumn)));
				cardAssistant.setCoverLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(coverLocalPathColumn)));
				cardAssistant.setCoverURL(cursor.getString(cursor
						.getColumnIndexOrThrow(coverURLColumn)));
				cardAssistant.setLeftLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(leftLocalPathColumn)));
				cardAssistant.setLeftURL(cursor.getString(cursor
						.getColumnIndexOrThrow(leftURLColumn)));
				cardAssistant.setOpenLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(openLocalPathColumn)));
				cardAssistant.setOpenURL(cursor.getString(cursor
						.getColumnIndexOrThrow(openURLColumn)));
				cardAssistant.setRightLocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(rightLocalPathColumn)));
				cardAssistant.setRightURL(cursor.getString(cursor
						.getColumnIndexOrThrow(rightURLColumn)));

				cardAssistant.setCardEditedDate(cursor.getString(cursor
						.getColumnIndexOrThrow(CARD_EDITED_DATE)));
				cardAssistant.setCardLocalEditedDate(cursor.getString(cursor
						.getColumnIndexOrThrow(CARD_EDITED_DATE_LOCAL)));
				
				
				cardAssistantList.add(cardAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return cardAssistantList;
		}
		else
		{
			return null;
		}
	}	
	
	
	public ArrayList<CategoryAssistant> getEnabledCategory(int dpi)
	{

		ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();
		String urlColumn;
		String localPathColumn;
		switch (dpi)
		{
		case DPI_MDPI:
			urlColumn = CAT_IMG_MDPI;
			localPathColumn = CAT_IMG_MDPI_LOCAL_PATH;
			break;
		case DPI_HPPI:
			urlColumn = CAT_IMG_HDPI;
			localPathColumn = CAT_IMG_HDPI_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			urlColumn = CAT_IMG_XXHDPI;
			localPathColumn = CAT_IMG_XXHDPI_LOCAL_PATH;
			break;
		default:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		}

		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY, new String[] { CAT_ID,
					CAT_NAME, urlColumn, localPathColumn }, CAT_ENABLE + "=1", null, null, null,
					null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CategoryAssistant categoryAssistant = new CategoryAssistant();
				categoryAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CAT_ID)));

				categoryAssistant.setCategoryName(cursor.getString(cursor
						.getColumnIndexOrThrow(CAT_NAME)));

				categoryAssistant.setCategoryURL(cursor.getString(cursor
						.getColumnIndexOrThrow(urlColumn)));

				categoryAssistant.setCategoryLoocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(localPathColumn)));

				categoryAssistantList.add(categoryAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return categoryAssistantList;
		}
		else
		{
			return null;
		}

	}

	public ArrayList<CategoryAssistant> getEnabledAndLocalIsNullCategory(int dpi)
	{

		ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();
		String urlColumn;
		String localPathColumn;
		switch (dpi)
		{
		case DPI_MDPI:
			urlColumn = CAT_IMG_MDPI;
			localPathColumn = CAT_IMG_MDPI_LOCAL_PATH;
			break;
		case DPI_HPPI:
			urlColumn = CAT_IMG_HDPI;
			localPathColumn = CAT_IMG_HDPI_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			urlColumn = CAT_IMG_XXHDPI;
			localPathColumn = CAT_IMG_XXHDPI_LOCAL_PATH;
			break;
		default:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		}

		if (db.isOpen())
		{

			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY, new String[] { CAT_ID,
					CAT_NAME, urlColumn, localPathColumn }, CAT_ENABLE + "=1 " + "and ("
					+ localPathColumn + " is null " + ")", null, null, null, null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CategoryAssistant categoryAssistant = new CategoryAssistant();
				categoryAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CAT_ID)));

				categoryAssistant.setCategoryName(cursor.getString(cursor
						.getColumnIndexOrThrow(CAT_NAME)));

				categoryAssistant.setCategoryURL(cursor.getString(cursor
						.getColumnIndexOrThrow(urlColumn)));

				categoryAssistant.setCategoryLoocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(localPathColumn)));

				categoryAssistantList.add(categoryAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return categoryAssistantList;
		}
		else
		{
			return null;
		}

	}

	public ArrayList<CategoryAssistant> getLocalDateIsNullOrLocalPathIsNullCategory(int dpi)
	{

		ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();
		String urlColumn;
		String localPathColumn;
		switch (dpi)
		{
		case DPI_MDPI:
			urlColumn = CAT_IMG_MDPI;
			localPathColumn = CAT_IMG_MDPI_LOCAL_PATH;
			break;
		case DPI_HPPI:
			urlColumn = CAT_IMG_HDPI;
			localPathColumn = CAT_IMG_HDPI_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			urlColumn = CAT_IMG_XXHDPI;
			localPathColumn = CAT_IMG_XXHDPI_LOCAL_PATH;
			break;
		default:
			urlColumn = CAT_IMG_XHDPI;
			localPathColumn = CAT_IMG_XHDPI_LOCAL_PATH;
			break;
		}

		if (db.isOpen())
		{

			String selectionString = "(" + localPathColumn + " is null " + "OR "
					+ CAT_EDITED_DATE_LOCAL + " is null " + "OR " + CAT_EDITED_DATE_LOCAL + "<>"
					+ CAT_EDITED_DATE + " )";
			Log.d(TAG, "getLocalDateIsNullOrLocalPathIsNullCategory() selectionString: "
					+ selectionString);
			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY, new String[] { CAT_ID,
					CAT_NAME, urlColumn, localPathColumn ,CAT_EDITED_DATE,CAT_EDITED_DATE_LOCAL}, selectionString, null, null, null,
					null, null);
			if (cursor != null)
			{
				cursor.moveToFirst();
			}

			if (cursor.getCount() == 0)
			{
				if (cursor != null)
				{
					cursor.close();
				}
				return null;
			}

			do
			{
				CategoryAssistant categoryAssistant = new CategoryAssistant();
				categoryAssistant
						.setCategoryID(cursor.getInt(cursor.getColumnIndexOrThrow(CAT_ID)));

				categoryAssistant.setCategoryName(cursor.getString(cursor
						.getColumnIndexOrThrow(CAT_NAME)));

				categoryAssistant.setCategoryURL(cursor.getString(cursor
						.getColumnIndexOrThrow(urlColumn)));

				categoryAssistant.setCategoryLoocalPath(cursor.getString(cursor
						.getColumnIndexOrThrow(localPathColumn)));

				categoryAssistant.setCategoryLocalEditedDate(cursor.getString(cursor
						.getColumnIndexOrThrow(CAT_EDITED_DATE_LOCAL)));

				categoryAssistant.setCategoryEditedDate(cursor.getString(cursor
						.getColumnIndexOrThrow(CAT_EDITED_DATE)));

				categoryAssistantList.add(categoryAssistant);
			}
			while (cursor.moveToNext());

			if (cursor != null)
			{
				cursor.close();
			}
			return categoryAssistantList;
		}
		else
		{
			return null;
		}

	}

	public Boolean isExistCategory(String catId)
	{

		if (db.isOpen())
		{
			boolean exists = false;
			Cursor cursor = db.rawQuery("select " + CAT_ID + " from " + DATABASE_TABLE_CATEGORY
					+ " where " + CAT_ID + "=" + catId, null);
			if (cursor == null)
			{
				Log.d(TAG, "isExistCategory " + catId + ": cursor == null");
			}

			if (cursor.moveToFirst())
			{
				exists = true;
			}
			else
			{
				exists = false;
			}

			// boolean exists = (cursor.getCount() > 0);
			Log.d(TAG, "isExistCategory " + catId + ": " + exists);
			if (cursor != null)
			{
				cursor.close();
			}

			return exists;

		}
		else
		{
			return false;
		}

	}

	public long createCategoryRow(String catId, String catName, String catEnable,
			String catImgMdpi, String catImgHdpi, String catImgXdpi, String catImgXXdpi,
			String catImgMdpiLocalPath, String catImgHdpiLocalPath, String catImgXdpiLocalPath,
			String catImgXXdpiLocalPath, String catEditedDate)
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
		if (catEditedDate != null) args.put(CAT_EDITED_DATE, catEditedDate);

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

	public void updateCategoryImgLocalPath(ArrayList<CategoryAssistant> list, int dpi)
	{

		for (int index = 0; index < list.size(); index++)
		{

			switch (dpi)
			{
			case DPI_MDPI:
				updateCategoryRow(String.valueOf(list.get(index).getCategoryID()), null, null,
						null, null, null, null, list.get(index).getCategoryLoocalPath(), null,
						null, null, null, list.get(index).getCategoryLocalEditedDate());
				break;
			case DPI_HPPI:
				updateCategoryRow(String.valueOf(list.get(index).getCategoryID()), null, null,
						null, null, null, null, null, list.get(index).getCategoryLoocalPath(),
						null, null, null, list.get(index).getCategoryLocalEditedDate());
				break;
			case DPI_XHDPI:
				updateCategoryRow(String.valueOf(list.get(index).getCategoryID()), null, null,
						null, null, null, null, null, null,
						list.get(index).getCategoryLoocalPath(), null, null, list.get(index)
								.getCategoryLocalEditedDate());
				break;
			case DPI_XXHDPI:
				updateCategoryRow(String.valueOf(list.get(index).getCategoryID()), null, null,
						null, null, null, null, null, null, null, list.get(index)
								.getCategoryLoocalPath(), null, list.get(index)
								.getCategoryLocalEditedDate());
				break;
			default:
				updateCategoryRow(String.valueOf(list.get(index).getCategoryID()), null, null,
						null, null, null, null, null, null,
						list.get(index).getCategoryLoocalPath(), null, null, list.get(index)
								.getCategoryLocalEditedDate());
				break;
			}

		}
	}

	public void updateCardImgLocalPath(ArrayList<CardAssistant> cardAssistantList, int dpi)
	{

		for (int index = 0; index < cardAssistantList.size(); index++)
		{
			updateCardRow(dpi, cardAssistantList.get(index).getCardID(), null, -1, null, null,
					null, null, null, null, null, cardAssistantList.get(index).getCloseLocalPath(),
					cardAssistantList.get(index).getCoverLocalPath(), cardAssistantList.get(index)
							.getLeftLocalPath(), cardAssistantList.get(index).getOpenLocalPath(),
					cardAssistantList.get(index).getRightLocalPath(), null,cardAssistantList.get(index).getCardLocalEditedDate());
		}
	}

	public long createCardRow(int dpi, int cardId, String cardName, int cat_id, String cardEnable,
			String cardFontColor, String closeURL, String coverURL, String leftURL, String openURL,
			String rightURL, String closeLocalPath, String coverLocalPath, String leftLocalPath,
			String openLocalPath, String rightLocalPath, int favoriteEnable, String cardEditedDate)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}

		ContentValues args = new ContentValues();
		if (cardId != -1) args.put(CARD_ID, cardId);

		if (cardName != null) args.put(CARD_NAME, cardName);

		if (cat_id != -1) args.put(CARD_CAT_ID, cat_id);
		if (cardEnable != null)
		{

			if (cardEnable.equals("Y"))
			{
				args.put(CARD_ENABLE, 1);
			}
			else if (cardEnable.equals("N"))
			{
				args.put(CARD_ENABLE, 0);
			}
			else
			{
				args.put(CARD_ENABLE, 0);
			}

		}
		
		try
		{
			if (cardFontColor != null ) args.put(CARD_FONT_COLOR, Long.valueOf(cardFontColor));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		

		if (closeURL != null) args.put(closeURLColumn, closeURL);
		if (coverURL != null) args.put(coverURLColumn, coverURL);
		if (leftURL != null) args.put(leftURLColumn, leftURL);
		if (openURL != null) args.put(openURLColumn, openURL);
		if (rightURL != null) args.put(rightURLColumn, rightURL);
		if (closeLocalPath != null) args.put(closeLocalPathColumn, closeLocalPath);
		if (coverLocalPath != null) args.put(coverLocalPathColumn, coverLocalPath);
		if (leftLocalPath != null) args.put(leftLocalPathColumn, leftLocalPath);
		if (openLocalPath != null) args.put(openLocalPathColumn, openLocalPath);
		if (rightLocalPath != null) args.put(rightLocalPathColumn, rightLocalPath);

		if (favoriteEnable != -1) args.put(CARD_FAVORITE_ENABLE, favoriteEnable);

		if (cardEditedDate != null) args.put(CARD_EDITED_DATE, cardEditedDate);

		return db.insert(DATABASE_TABLE_CARD, null, args);
	}

	public Boolean isExistCard(String cardId)
	{

		if (db.isOpen())
		{
			boolean exists = false;
			Cursor cursor = db.rawQuery("select " + CARD_ID + " from " + DATABASE_TABLE_CARD
					+ " where " + CARD_ID + "=" + cardId, null);
			if (cursor == null)
			{
				Log.d(TAG, "isExistCard " + cardId + ": cursor == null");
			}

			if (cursor.moveToFirst())
			{
				exists = true;
			}
			else
			{
				exists = false;
			}

			// boolean exists = (cursor.getCount() > 0);
			Log.d(TAG, "isExistCard " + cardId + ": " + exists);
			if (cursor != null)
			{
				cursor.close();
			}

			return exists;

		}
		else
		{
			return false;
		}

	}

	public boolean updateCardRow(int dpi, int cardId, String cardName, int cat_id,
			String cardEnable, String cardFontColor, String closeURL, String coverURL,
			String leftURL, String openURL, String rightURL, String closeLocalPath,
			String coverLocalPath, String leftLocalPath, String openLocalPath,
			String rightLocalPath, String cardEditedDate, String cardEditedDateLocal)
	{

		String closeURLColumn;
		String closeLocalPathColumn;
		String coverURLColumn;
		String coverLocalPathColumn;
		String leftURLColumn;
		String leftLocalPathColumn;
		String openURLColumn;
		String openLocalPathColumn;
		String rightURLColumn;
		String rightLocalPathColumn;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURLColumn = CARD_MDPI_CLOSE;
			closeLocalPathColumn = CARD_MDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_MDPI_COVER;
			coverLocalPathColumn = CARD_MDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_MDPI_LEFT;
			leftLocalPathColumn = CARD_MDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_MDPI_OPEN;
			openLocalPathColumn = CARD_MDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_MDPI_RIGHT;
			rightLocalPathColumn = CARD_MDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_HPPI:
			closeURLColumn = CARD_HDPI_CLOSE;
			closeLocalPathColumn = CARD_HDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_HDPI_COVER;
			coverLocalPathColumn = CARD_HDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_HDPI_LEFT;
			leftLocalPathColumn = CARD_HDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_HDPI_OPEN;
			openLocalPathColumn = CARD_HDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_HDPI_RIGHT;
			rightLocalPathColumn = CARD_HDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XHDPI:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		case DPI_XXHDPI:
			closeURLColumn = CARD_XXHDPI_CLOSE;
			closeLocalPathColumn = CARD_XXHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XXHDPI_COVER;
			coverLocalPathColumn = CARD_XXHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XXHDPI_LEFT;
			leftLocalPathColumn = CARD_XXHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XXHDPI_OPEN;
			openLocalPathColumn = CARD_XXHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XXHDPI_RIGHT;
			rightLocalPathColumn = CARD_XXHDPI_RIGHT_LOCAL_PATH;
			break;
		default:
			closeURLColumn = CARD_XHDPI_CLOSE;
			closeLocalPathColumn = CARD_XHDPI_CLOSE_LOCAL_PATH;
			coverURLColumn = CARD_XHDPI_COVER;
			coverLocalPathColumn = CARD_XHDPI_COVER_LOCAL_PATH;
			leftURLColumn = CARD_XHDPI_LEFT;
			leftLocalPathColumn = CARD_XHDPI_LEFT_LOCAL_PATH;
			openURLColumn = CARD_XHDPI_OPEN;
			openLocalPathColumn = CARD_XHDPI_OPEN_LOCAL_PATH;
			rightURLColumn = CARD_XHDPI_RIGHT;
			rightLocalPathColumn = CARD_XHDPI_RIGHT_LOCAL_PATH;
			break;
		}

		if (db.isOpen())
		{
			ContentValues args = new ContentValues();
			if (cardId != -1) args.put(CARD_ID, cardId);

			if (cardName != null) args.put(CARD_NAME, cardName);

			if (cat_id != -1) args.put(CARD_CAT_ID, cat_id);
			if (cardEnable != null)
			{

				if (cardEnable.equals("Y"))
				{
					args.put(CARD_ENABLE, 1);
				}
				else if (cardEnable.equals("N"))
				{
					args.put(CARD_ENABLE, 0);
				}
				else
				{
					args.put(CARD_ENABLE, 0);
				}

			}
			try
			{
				if (cardFontColor != null) args.put(CARD_FONT_COLOR, Integer.valueOf(cardFontColor));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}


			if (closeURL != null) args.put(closeURLColumn, closeURL);
			if (coverURL != null) args.put(coverURLColumn, coverURL);
			if (leftURL != null) args.put(leftURLColumn, leftURL);
			if (openURL != null) args.put(openURLColumn, openURL);
			if (rightURL != null) args.put(rightURLColumn, rightURL);
			if (closeLocalPath != null) args.put(closeLocalPathColumn, closeLocalPath);
			if (coverLocalPath != null) args.put(coverLocalPathColumn, coverLocalPath);
			if (leftLocalPath != null) args.put(leftLocalPathColumn, leftLocalPath);
			if (openLocalPath != null) args.put(openLocalPathColumn, openLocalPath);
			if (rightLocalPath != null) args.put(rightLocalPathColumn, rightLocalPath);
			if (cardEditedDate != null) args.put(CARD_EDITED_DATE, cardEditedDate);
			if (cardEditedDateLocal != null) args.put(CARD_EDITED_DATE_LOCAL, cardEditedDateLocal);

			return db.update(DATABASE_TABLE_CARD, args, CARD_ID + "=" + cardId, null) > 0;
		}
		else
		{
			return false;
		}

	}

	public boolean updateCategoryRow(String catId, String catName, String catEnable,
			String catImgMdpi, String catImgHdpi, String catImgXdpi, String catImgXXdpi,
			String catImgMdpiLocalPath, String catImgHdpiLocalPath, String catImgXHdpiLocalPath,
			String catImgXXdpiLocalPath, String catEditedDate, String catLocalEditedDate)
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
			if (catImgXHdpiLocalPath != null)
				args.put(CAT_IMG_XHDPI_LOCAL_PATH, catImgXHdpiLocalPath);
			if (catImgXXdpiLocalPath != null)
				args.put(CAT_IMG_XXHDPI_LOCAL_PATH, catImgXXdpiLocalPath);
			if (catEditedDate != null) args.put(CAT_EDITED_DATE, catEditedDate);

			if (catLocalEditedDate != null) args.put(CAT_EDITED_DATE_LOCAL, catLocalEditedDate);

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

			Cursor cursor = db.query(true, DATABASE_TABLE_CATEGORY, new String[] { urlColumn },
					CAT_ID + "=" + Integer.valueOf(catId), null, null, null, null, null);
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

	public int getSystemDPI(Context context)
	{

		int density = context.getResources().getDisplayMetrics().densityDpi;

		int result = DPI_XHDPI;
		switch (density)
		{
		case DisplayMetrics.DENSITY_LOW:
			result = DPI_XHDPI;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			result = DPI_XHDPI;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			result = DPI_XHDPI;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			result = DPI_XHDPI;
			break;
		case DisplayMetrics.DENSITY_TV:
			result = DPI_XXHDPI;
			break;
		default:
			result = DPI_XHDPI;
			break;
		}

		return result;

	}
}

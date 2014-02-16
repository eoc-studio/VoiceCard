package eoc.studio.voicecard.facebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FriendsAdapterData {
    private static final String DATABASE_NAME = "friends.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "friends";
    private static final String DATABASE_CREATE = "create table friends(" + "_id INTEGER PRIMARY KEY,"
            + "friend_id TEXT NOT NULL," + "friend_name TEXT," + "friend_birthday TEXT," + "friend_img_link TEXT,"
            + "friend_img BLOB," + "select_state INTEGER," + "install_state INTEGER" + ");";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_FRIEND_ID = "friend_id";
    public static final String KEY_FRIEND_NAME = "friend_name";
    public static final String KEY_FRIEND_BIRTHDAY = "friend_birthday";
    public static final String KEY_FRIEND_IMG_LINK = "friend_img_link";
    public static final String KEY_FRIEND_IMG = "friend_img";
    public static final String KEY_SELECT_STATE = "select_state";
    public static final String KEY_INSTALL_STATE = "install_state";
    
    public static final int UNSELECT = 0;
    public static final int SELECT = 1;
    public static final int NOTINSTALL = 0;
    public static final int INSTALL = 1;

    private Context context = null;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /** Constructor  */
    public FriendsAdapterData(Context context) {
        this.context = context;
    }

    public FriendsAdapterData open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (dbHelper != null)
            dbHelper.close();
    }

    /** Get all items from database*/
    public Cursor getAll() {
        return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_FRIEND_ID, KEY_FRIEND_NAME, KEY_FRIEND_BIRTHDAY,
                KEY_FRIEND_IMG_LINK, KEY_FRIEND_IMG, KEY_SELECT_STATE, KEY_INSTALL_STATE }, null, null, null, null,
                KEY_FRIEND_NAME);
    }

    /** Insert item to database */
    public long create(String friendId, String name, String birthday, String imgLink, byte[] img, int selectState,
            int installState) {
        ContentValues args = new ContentValues();
        args.put(KEY_FRIEND_ID, friendId);
        args.put(KEY_FRIEND_NAME, name);
        args.put(KEY_FRIEND_BIRTHDAY, birthday);
        args.put(KEY_FRIEND_IMG_LINK, imgLink);
        args.put(KEY_FRIEND_IMG, img);
        args.put(KEY_SELECT_STATE, selectState);
        args.put(KEY_INSTALL_STATE, installState);
        return db.insert(DATABASE_TABLE, null, args);
    }

    /** Delete one item from database */
    public boolean delete(String friendId) {
        if (db != null && db.isOpen())
            return db.delete(DATABASE_TABLE, KEY_FRIEND_ID + "=" + friendId, null) > 0;
        else
            return false;
    }

    /** Delete all item from database */
    public boolean delete() {
        if (db != null && db.isOpen())
            return db.delete(DATABASE_TABLE, null, null) > 0;
        else
            return false;
    }

    /** Query single entry */
    public Cursor get(String friendId) throws SQLException {
        if (db.isOpen()) {
            Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_FRIEND_ID, KEY_FRIEND_NAME,
                    KEY_FRIEND_BIRTHDAY, KEY_FRIEND_IMG_LINK, KEY_FRIEND_IMG, KEY_SELECT_STATE, KEY_INSTALL_STATE }, KEY_FRIEND_ID + "="
                    + friendId, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        } else {
            return null;
        }
    }
    
    public Cursor seachResult(String selectionName) throws SQLException {
        if (db.isOpen()) {
            String where = KEY_FRIEND_NAME + " like ?";
            String[] selection = new String[] { selectionName + "%" };
            Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_FRIEND_ID, KEY_FRIEND_NAME,
                    KEY_FRIEND_BIRTHDAY, KEY_FRIEND_IMG_LINK, KEY_FRIEND_IMG, KEY_SELECT_STATE, KEY_INSTALL_STATE },
                    where, selection, null, null, null, null);
            return cursor;
        } else {
            return null;
        }
    }
    
    public int getSelectedState(String friendId) throws SQLException {
        int state = 0;
        if (db.isOpen()) {
            Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_SELECT_STATE }, KEY_FRIEND_ID + "="
                    + friendId, null, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                state = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SELECT_STATE));
                cursor.close();
            }
        }
        return state;
    }
    
    public Cursor getSelectedFriend() throws SQLException {
        if (db.isOpen()) {
            Cursor cursor = db.query(true, DATABASE_TABLE, new String[] { KEY_FRIEND_ID, KEY_FRIEND_NAME,
                    KEY_FRIEND_BIRTHDAY }, KEY_SELECT_STATE + "=" + SELECT, null, null, null, null, null);
            return cursor;
        } else {
            return null;
        }
    }
    
    /** Update the database */
    public boolean update(String friendId, String name, String birthday, String imgLink, byte[] img,
            int selectState, int installState) {
        if (db.isOpen()) {
            ContentValues args = new ContentValues();
            args.put(KEY_FRIEND_NAME, name);
            args.put(KEY_FRIEND_BIRTHDAY, birthday);
            args.put(KEY_FRIEND_IMG_LINK, imgLink);
            args.put(KEY_FRIEND_IMG, img);
            args.put(KEY_SELECT_STATE, selectState);
            args.put(KEY_INSTALL_STATE, installState);
            return db.update(DATABASE_TABLE, args, KEY_FRIEND_ID + "=" + friendId, null) > 0;
        } else {
            return false;
        }
    }
    
    public boolean updateSelectedState(String friendId, int selectState) {
        if (db.isOpen()) {
            ContentValues args = new ContentValues();
            args.put(KEY_SELECT_STATE, selectState);
            return db.update(DATABASE_TABLE, args, KEY_FRIEND_ID + "=" + friendId, null) > 0;
        } else {
            return false;
        }
    }
    
    public boolean updateFriendImg(String friendId, byte[] friendImg) {
        if (db.isOpen()) {
            ContentValues args = new ContentValues();
            args.put(KEY_FRIEND_IMG, friendImg);
            return db.update(DATABASE_TABLE, args, KEY_FRIEND_ID + "=" + friendId, null) > 0;
        } else {
            return false;
        }
    }

    /** Query single entry with name and birthday*/
    //For test
    public Cursor getNameandBirthday(String name, String birthday) throws SQLException {
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT * FROM friends WHERE " + KEY_FRIEND_NAME + " = " + "\"" + name + "\""
                    + " AND " + KEY_FRIEND_BIRTHDAY + " = " + "\"" + birthday + "\"", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        } else {
            return null;
        }
    }
}

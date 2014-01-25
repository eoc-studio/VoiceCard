package eoc.studio.voicecard.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eoc.studio.voicecard.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ContactActivity extends Activity implements OnClickListener, OnItemClickListener,
		OnScrollListener
{
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///About search
	private EditText searchEditText;
	// ///About Dynamic Loading
	private boolean refreshFlag = false, selectItemsCheck = false, checkLoadingDBStatus = false,
			loadDBThreadCheck = false;
	private int lastVisibleListItemSite, firstVisibleListItemSite, maxLoadDataNo, minLoadDataNo;
	// ///About DB
	private Cursor cur = null, birthdayCur = null;
	private static Thread dataThread;
	private String selectItemUid, selectItemLookupKey;
	// ///About ListView
	private ListView listView;
	private int selectItem = -1;
	private MyAdapter groupListView;
	private Drawable listView_n, listView_f;
	private ArrayList<Map<String, String>> data;
	private Bitmap resizeBmp;
	public final static String userId = "userId", userName = "userName", lookUpKey = "lookUpKey",
			birthdayDate = "birthdayDate";
	private static Map<String, String> item;
	private static Bitmap photo;
	// ///About Button
	private Button backBtn;
	// //About Toast
	protected static Context mContext, mApplicationContext;
	private boolean mIsOneTouch = false;
	private long mOneTouchStartTime = 0, BACK_TIMEOUT = 500;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onResume()
	{
		super.onResume();
		if (refreshFlag)
		{
			loadDatabaseThread();
		}
		overridePendingTransition(0, 0);
		refreshFlag = false;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onPause()
	{
		super.onPause();
		ShowDialog.dismissLoadingDialog();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_contact);
		mContext = ContactActivity.this;
		mApplicationContext = getApplicationContext();
		findView();
		buttonFunction();
		if (!refreshFlag)
		{
			loadDatabaseThread();
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (cur != null && !cur.isClosed())
		{
			cur.close();
			cur = null;
		}
		if (birthdayCur != null && !birthdayCur.isClosed())
		{
			birthdayCur.close();
			birthdayCur = null;
		}
		if (groupListView != null && !groupListView.isEmpty())
		{
			if (groupListView.mList != null && !groupListView.mList.isEmpty())
			{
				groupListView.mList.clear();
				groupListView.mList = null;
			}
			if (groupListView.map != null && !groupListView.map.isEmpty())
			{
				groupListView.map.clear();
				groupListView.map = null;
			}
		}
		if (data != null && !data.isEmpty())
		{
			data.clear();
			data = null;
		}
		if (item != null && !item.isEmpty())
		{
			item.clear();
			item = null;
		}
		System.gc();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void findView()
	{
		backBtn = (Button) findViewById(R.id.back_btn);
		listView = (ListView) findViewById(R.id.contactsListView);
		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.dummy_contact);
		// Matrix matrix = new Matrix();
		// matrix.postScale((float) 50 / bitmap.getWidth(), (float) 50 /
		// bitmap.getWidth());
		// resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
		// bitmap.getHeight(),
		// matrix, true);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /Button Function
	public void buttonFunction()
	{
		backBtn.setOnClickListener(this);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(View view)
	{
		if (mIsOneTouch == false)
		{
			new CounterBackKeyThread().start();
		}
		else
		{
			if (mIsOneTouch) { return; }
		}
		switch (view.getId())
		{
		case R.id.back_btn:
		{
			if (loadDBThreadCheck) { return; }
			finish();
		}
			break;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void loadDatabaseThread()
	{
		loadDBThreadCheck = true;
		if (!checkLoadingDBStatus)
		{
			ShowDialog.showLoadingDialog(mContext);
			dataThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					loadDatabaseFunction();
					if (data.isEmpty())
					{
						data = new ArrayList<Map<String, String>>();
						selectItemsCheck = true;
						item = new HashMap<String, String>();
						item.put(userName, getResources().getString(R.string.noData));
						item.put(userId, "");
						data.add(item);
					}
					Message status = updaListViewHandler.obtainMessage();
					updaListViewHandler.sendMessage(status);
					ShowDialog.dismissLoadingDialog();
				}
			});
			dataThread.start();
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /Load Database
	public void loadDatabaseFunction()
	{
		if (!checkLoadingDBStatus)
		{
			String where = null, selection[] = null, id, name, sort_key_alt, lookupKey;
			data = new ArrayList<Map<String, String>>();
			ContentResolver cr = getContentResolver();
			where = null;
			selection = null;
			try
			{
				cur = cr.query(Contacts.CONTENT_URI, null, where, selection, "sort_key_alt");
				if (cur != null && cur.getCount() > 0)
				{
					selectItemsCheck = false;
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
						item = new HashMap<String, String>();
						item.put(userId, id);
						item.put(userName, name);
						item.put(lookUpKey, lookupKey);
						item.put(birthdayDate, "");
						data.add(item);
					}
				}
			}
			catch (SQLException e)
			{
				System.out.println("[addressBookActivity][loadDatabaseFunction]SQLException:" + e);
			}
			catch (Exception ex)
			{
				System.out.println("[addressBookActivity][loadDatabaseFunction]Exception:" + ex);
			}
			finally
			{}
		}
		checkLoadingDBStatus = true;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	Handler updaListViewHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			sendDataToMyAdapter();
			if (!selectItemsCheck && !refreshFlag)
			{
				if ((listView.getCount() > 0) && (listView.getCount() < 5))
				{
					maxLoadDataNo = 0;
					minLoadDataNo = 0;
					updataListData(0, listView.getCount());
				}
				else
				{
					maxLoadDataNo = 10;
					minLoadDataNo = 1;
					updataListData(1, 3);
				}
			}
			if (!groupListView.mList.get(0).get(userId).equals(""))
			{
				if ((listView.getCount() > 0) && (listView.getCount() < 5))
				{
					maxLoadDataNo = 0;
					minLoadDataNo = 0;
					updataListData(0, listView.getCount());
				}
				else
				{
					maxLoadDataNo = 10;
					minLoadDataNo = 1;
					updataListData(selectItem, selectItem);
				}
			}
		}
	};

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	public void sendDataToMyAdapter()
	{
		if (cur == null) { return; }
		if (cur.getCount() > 0)
		{
			groupListView = new MyAdapter(this, data, R.layout.activity_contact_list_item, new String[] {
					userName, birthdayDate, userId, lookUpKey }, new int[] { R.id.listView_name,
					R.id.listView_biryhdayDateView, R.id.listView_typePic });
			listView.setAdapter(groupListView);
			listView.setOnItemClickListener(this);
			listView.setOnScrollListener(this);
			listView.setSelectionFromTop(selectItem, 0);
		}
		else
		{
			groupListView = new MyAdapter(this, data, R.layout.activity_contact_list_item, new String[] {
					userName, birthdayDate, userId, lookUpKey }, new int[] { R.id.listView_name,
					R.id.listView_biryhdayDateView, R.id.listView_typePic });
			listView.setAdapter(groupListView);
		}
		if (cur != null && !cur.isClosed())
		{
			cur.close();
		}
		checkLoadingDBStatus = false;
		loadDBThreadCheck = false;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class MyAdapter extends SimpleAdapter
	{
		Map<Integer, Boolean> map = null;
		LayoutInflater mInflater;
		private List<? extends Map<String, ?>> mList;

		public MyAdapter(Context context, List<Map<String, String>> data, int resource,
				String[] from, int[] to)
		{
			super(context, data, resource, from, to);
			mInflater = LayoutInflater.from(context);
			if (data != null)
			{
				mList = data;
			}
			else
			{
				List<Map<String, String>> createData = new ArrayList<Map<String, String>>();
				selectItemsCheck = true;
				item = new HashMap<String, String>();
				item.put(userName, getResources().getString(R.string.noData));
				item.put(userId, "");
				createData.add(item);
				mList = createData;
				return;
			}
		}

		@Override
		public int getCount()
		{
			if (mList != null) { return mList.size(); }
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		public void setSelectItem(int sItem)
		{
			selectItem = sItem;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.activity_contact_list_item, null);
			}
			TextView listView_name = (TextView) convertView.findViewById(R.id.listView_name);
			ImageView listView_typePic = (ImageView) convertView
					.findViewById(R.id.listView_typePic);
			TextView listView_birthday = (TextView) convertView
					.findViewById(R.id.listView_biryhdayDateView);
			ImageView user_pic = (ImageView) convertView.findViewById(R.id.listView_Pic);
			listView_name.setText((String) mList.get(position).get(userName));
			listView_birthday.setText((String) mList.get(position).get(birthdayDate));
			if (mList == null) { return convertView; }
			if (listView.getCount() == selectItem)
			{
				selectItem = selectItem - 1;
			}
			if (position == selectItem)
			{
				selectItemUid = (String) mList.get(position).get(userId);
				selectItemLookupKey = (String) mList.get(position).get(lookUpKey);
				convertView.setBackgroundDrawable(listView_f);
				listView_birthday.setTextColor(0xffffffff);
				listView_name.setTextColor(0xffffffff);
			}
			else if ((selectItem == -1) && (selectItem == position))
			{
				selectItemUid = (String) mList.get(position).get(userId);
				selectItemLookupKey = (String) mList.get(position).get(lookUpKey);
				convertView.setBackgroundDrawable(listView_f);
				listView_birthday.setTextColor(0xffffffff);
				listView_name.setTextColor(0xffffffff);
			}
			else
			{
				convertView.setBackgroundDrawable(listView_n);
				listView_birthday.setTextColor(0xff000000);
				listView_name.setTextColor(0xff000000);
			}
			if (!selectItemsCheck)
			{
				Bitmap bmp = ImageProcess.getPhoto(getContentResolver(),
						Integer.parseInt((String) mList.get(position).get(userId)));
				if (bmp != null)
				{
					Matrix matrix = new Matrix();
					matrix.postScale((float) 50 / bmp.getWidth(), (float) 50 / bmp.getWidth());
					Bitmap resizeBmps = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
							bmp.getHeight(), matrix, true);
					user_pic.setImageBitmap(resizeBmps);
				}
				else
				{
					user_pic.setImageBitmap(resizeBmp);
				}
			}
			else
			{
				selectItem = -1;
				selectItemUid = null;
				selectItemLookupKey = null;
				convertView.setBackgroundColor(0xffcccccc);
				listView_birthday.setTextColor(0xff000000);
				listView_name.setTextColor(0xff000000);
			}
			return convertView;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		groupListView.setSelectItem(position);
		groupListView.notifyDataSetInvalidated();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount)
	{
		lastVisibleListItemSite = firstVisibleItem + visibleItemCount - 1;
		if (firstVisibleListItemSite == 0)
		{
			firstVisibleListItemSite = 1;
		}
		else
		{
			firstVisibleListItemSite = firstVisibleItem;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		if (minLoadDataNo == 1 && !refreshFlag)
		{
			String itemCheckA = "", itemCheckB = "", itemCheckC = "", itemCheckD = "";
			if (groupListView.getCount() <= 1)
			{
				itemCheckA = (String) groupListView.mList.get(firstVisibleListItemSite + 1).get(
						birthdayDate);
			}
			if (groupListView.getCount() <= 2)
			{
				itemCheckB = (String) groupListView.mList.get(firstVisibleListItemSite + 2).get(
						birthdayDate);
			}
			if (groupListView.getCount() <= 3)
			{
				itemCheckC = (String) groupListView.mList.get(firstVisibleListItemSite + 3).get(
						birthdayDate);
			}
			if (groupListView.getCount() <= 4)
			{
				itemCheckD = (String) groupListView.mList.get(firstVisibleListItemSite + 4).get(
						birthdayDate);
			}
			if (scrollState == 0)
			{
				if (itemCheckA.equals("") || itemCheckB.equals("") || itemCheckC.equals("")
						|| itemCheckD.equals(""))
				{
					updataListData(firstVisibleListItemSite, lastVisibleListItemSite);
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updataListData(int firstVisibleListItemSite, int lastVisibleListItemSite)
	{
		String where = null, selection[] = null;
		ContentResolver cr = getContentResolver();
		String birthday = "";
		lastVisibleListItemSite = lastVisibleListItemSite + maxLoadDataNo;
		firstVisibleListItemSite = firstVisibleListItemSite - minLoadDataNo;
		if ((listView.getCount() < lastVisibleListItemSite))
		{
			firstVisibleListItemSite = firstVisibleListItemSite - 4;
			lastVisibleListItemSite = listView.getCount();
		}
		if (firstVisibleListItemSite < 0)
		{
			firstVisibleListItemSite = 0;
		}
		for (int x = firstVisibleListItemSite; x < lastVisibleListItemSite; x++)
		{
			if ((x > (listView.getCount() - minLoadDataNo - 1)) && (x < 0))
			{
				break;
			}
			else
			{
				item = (HashMap<String, String>) groupListView.mList.get(x);
				try
				{
					String columns[] = { ContactsContract.CommonDataKinds.Event.START_DATE,
							ContactsContract.CommonDataKinds.Event.TYPE,
							ContactsContract.CommonDataKinds.Event.MIMETYPE };
					where = Event.TYPE + "=" + Event.TYPE_BIRTHDAY + " and " + Event.MIMETYPE
							+ " = '" + Event.CONTENT_ITEM_TYPE + "' and "
							+ ContactsContract.Data.CONTACT_ID + " = "
							+ groupListView.mList.get(x).get(userId).toString();
					birthdayCur = cr.query(ContactsContract.Data.CONTENT_URI, columns, where, null,
							null);
					if (birthdayCur != null && birthdayCur.getCount() > 0)
					{
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						while (birthdayCur.moveToNext())
						{
							birthday = birthdayCur
									.getString(birthdayCur
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
					System.out.println("[addressBookActivity][updataListData]SQLException:" + e);
				}
				catch (Exception ex)
				{
					System.out.println("[addressBookActivity][updataListData]Exception:" + ex);
				}
				finally
				{
					if (birthdayCur != null && !birthdayCur.isClosed())
					{
						birthdayCur.close();
					}
				}
				if (groupListView.mList.get(x).get(birthdayDate).equals(""))
				{
					System.out.println("3!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + birthday);
					item.put(birthdayDate, birthday);
				}
			}
		}
		groupListView.notifyDataSetChanged();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	class CounterBackKeyThread extends Thread
	{
		public void run()
		{
			mIsOneTouch = true;
			mOneTouchStartTime = System.currentTimeMillis();
			while (true)
			{
				long currentTime = System.currentTimeMillis();
				if (currentTime - mOneTouchStartTime >= BACK_TIMEOUT)
				{
					mIsOneTouch = false;
					break;
				}
				try
				{
					Thread.sleep(50);
				}
				catch (Exception ex)
				{}
			}
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

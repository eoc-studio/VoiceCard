package eoc.studio.voicecard.contact;

import eoc.studio.voicecard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ContactActivity extends Activity implements OnClickListener, OnItemClickListener, OnScrollListener
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////About search
    private EditText searchEditText;

    /////About main page view
    private static ImageView checkBox;

    /////About Dynamic Loading
    private boolean refreshFlag = false, selectItemsCheck = false, checkLoadingDBStatus = false,
            dataProcessFunStatus = false, loadDBThreadCheck = false;
    private int lastVisibleListItemSite, firstVisibleListItemSite, maxLoadDataNo, minLoadDataNo;

    /////About DB
    private Cursor cur = null;
    private static Thread dataThread;

    /////About ListView
    private int selectItem = -1;
    private String selectItemUid, selectItemLookupKey;
    private ListView listView;
    private MyAdapter mainListView;
    private ArrayList<Map<String, String>> data;
    private Bitmap resizeBmp, bitmap, bmp;

    private static Map<String, String> item;

    /////About Button
    private ImageView searchBtn, mOkBtn, backBtn;

    ////About Toast
    private static Context mContext;

    private static AlertDialog alertDialogBuilder;

    private boolean mIsOneTouch = false;
    private long mOneTouchStartTime = 0, BACK_TIMEOUT = 500;
    private static final int PHONE_NUMBER = 0, PHONE_TYPE = 1;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_friend);

        mContext = ContactActivity.this;

        findView();
        buttonFunction();

        if (!refreshFlag)
        {
            loadDatabaseThread();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPause()
    {
        super.onPause();

        ShowDialog.dismissLoadingDialog();
        ShowDialog.dismissCountDialog();

        if (alertDialogBuilder != null && alertDialogBuilder.isShowing())
        {
            alertDialogBuilder.dismiss();
            alertDialogBuilder = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (cur != null && !cur.isClosed())
        {
            cur.close();
            cur = null;
        }

        if (resizeBmp != null && !resizeBmp.isRecycled())
        {
            resizeBmp.recycle();
        }

        if (bitmap != null && !bitmap.isRecycled())
        {
            bitmap.recycle();
        }

        if (bmp != null && !bmp.isRecycled())
        {
            bmp.recycle();
        }

        if (mainListView != null && !mainListView.isEmpty())
        {
            if (mainListView.mList != null && !mainListView.mList.isEmpty())
            {
                mainListView.mList.clear();
                mainListView.mList = null;
            }

            if (mainListView.map != null && !mainListView.map.isEmpty())
            {
                mainListView.map.clear();
                mainListView.map = null;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///findView Function
    private void findView()
    {
        searchEditText = (EditText) findViewById(R.id.act_select_friend_et_search_bar);
        searchBtn = (ImageView) findViewById(R.id.act_select_friend_iv_search_button);
        mOkBtn = (ImageView) findViewById(R.id.act_select_friend_iv_button_ok);
        backBtn = (ImageView) findViewById(R.id.act_select_friend_iv_button_return);
        checkBox = (ImageView) findViewById(R.id.glb_selectfriend_list_item_check_icon);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
        Matrix matrix = new Matrix();
        matrix.postScale((float) 50 / bitmap.getWidth(), (float) 50 / bitmap.getWidth());
        resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///Button Function
    private void buttonFunction()
    {
        searchBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view)
    {
        if (mIsOneTouch == false)
        {
            new CounterBackKeyThread().start();
        }
        else
        {
            if (mIsOneTouch)
            {
                return;
            }
        }

        switch (view.getId())
        {
            case R.id.act_select_friend_iv_search_button:
            {
                if (loadDBThreadCheck)
                {
                    return;
                }

                loadDatabaseThread();
            }
                break;
            case R.id.act_select_friend_iv_button_ok:
            {
                if (loadDBThreadCheck)
                {
                    return;
                }
                dataProcessFunction();
            }
                break;
            case R.id.act_select_friend_iv_button_return:
            {
                if (loadDBThreadCheck)
                {
                    return;
                }
                finish();
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void loadDatabaseThread()
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
                        item.put(DataProcess.USER_NAME_INDEX, getResources().getString(R.string.noData));
                        item.put(DataProcess.USER_ID_INDEX, "");
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///Load Database
    private void loadDatabaseFunction()
    {
        if (!checkLoadingDBStatus)
        {
            String where, selection[], id, name, sort_key_alt, lookupKey;
            listView = (ListView) findViewById(R.id.act_select_friend_lv);
            data = new ArrayList<Map<String, String>>();
            ContentResolver cr = getContentResolver();

            if (searchEditText.getText().toString().equals(""))
            {
                where = null;
                selection = null;
            }
            else
            {
                where = "sort_key_alt like ? OR " + ContactsContract.Contacts.DISPLAY_NAME + " like ?";
                selection = new String[]
                { searchEditText.getText().toString() + "%", searchEditText.getText().toString() + "%" };
            }

            try
            {
                cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, where, selection, "sort_key_alt");

                if (cur != null && cur.getCount() > 0)
                {
                    selectItemsCheck = false;
                    while (cur.moveToNext())
                    {
                        if (!ShowDialog.isShowLoadingDialog())
                        {
                            break;
                        }

                        if (cur.getString(cur.getColumnIndex("sort_key_alt")) != null)
                        {
                            id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            sort_key_alt = cur.getString(cur.getColumnIndex("sort_key_alt")).toUpperCase();
                            lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            if (sort_key_alt.indexOf(searchEditText.getText().toString().toUpperCase()) != -1
                                    || name.indexOf(searchEditText.getText().toString()) != -1)
                            {
                                item = new HashMap<String, String>();
                                item.put(DataProcess.USER_ID_INDEX, id);
                                item.put(DataProcess.USER_NAME_INDEX, name);
                                item.put(DataProcess.LOOK_UP_KEY_INDEX, lookupKey);
                                item.put(DataProcess.PHONE_NUMBER_INDEX, "");
                                item.put(DataProcess.TEL_TYPE_INDEX, "");
                                data.add(item);
                            }
                        }
                    }
                }
            }
            catch (SQLException e)
            {
                System.out.println("[ContactActivity]SQLException" + e);
            }
            catch (Exception ex)
            {
                System.out.println("[ContactActivity]Exception" + ex);
            }
            finally
            {
            }
        }
        checkLoadingDBStatus = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////	
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

            if (!mainListView.mList.get(0).get(DataProcess.USER_ID_INDEX).equals(""))
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendDataToMyAdapter()
    {
        if (cur == null)
        {
            return;
        }

        if (cur.getCount() > 0)
        {
            mainListView = new MyAdapter(this, data, R.layout.select_friend_list_item, new String[]
            { DataProcess.USER_NAME_INDEX, DataProcess.PHONE_NUMBER_INDEX, DataProcess.TEL_TYPE_INDEX,
                    DataProcess.USER_ID_INDEX, DataProcess.LOOK_UP_KEY_INDEX }, new int[]
            { R.id.glb_selectfriend_list_item_header, R.id.glb_selectfriend_list_item_name,
                    R.id.glb_selectfriend_list_item_check_icon });
            listView.setAdapter(mainListView);
            listView.setOnItemClickListener(this);
            listView.setOnScrollListener(this);
            listView.setSelectionFromTop(selectItem, 0);
        }
        else
        {
            mainListView = new MyAdapter(this, data, R.layout.select_friend_list_item, new String[]
            { DataProcess.USER_NAME_INDEX, DataProcess.PHONE_NUMBER_INDEX, DataProcess.TEL_TYPE_INDEX,
                    DataProcess.USER_ID_INDEX, DataProcess.LOOK_UP_KEY_INDEX }, new int[]
            { R.id.glb_selectfriend_list_item_header, R.id.glb_selectfriend_list_item_name,
                    R.id.glb_selectfriend_list_item_check_icon });
            listView.setAdapter(mainListView);
        }

        if (cur != null && !cur.isClosed())
        {
            cur.close();
        }

        loadDBThreadCheck = false;
        checkLoadingDBStatus = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	 
    public class MyAdapter extends SimpleAdapter
    {
        Map<Integer, Boolean> map = null;
        LayoutInflater mInflater;
        private List<? extends Map<String, ?>> mList;

        public MyAdapter(Context context, List<Map<String, String>> data, int resource, String[] from, int[] to)
        {
            super(context, data, resource, from, to);
            map = new HashMap<Integer, Boolean>();
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
                item.put(DataProcess.USER_NAME_INDEX, getResources().getString(R.string.noData));
                item.put(DataProcess.USER_ID_INDEX, "");
                createData.add(item);
                mList = createData;
                return;
            }

            for (int i = 0; i < data.size(); i++)
            {
                map.put(i, false);
            }
        }

        @Override
        public int getCount()
        {
            if (mList != null)
            {
                return mList.size();
            }
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
                convertView = mInflater.inflate(R.layout.select_friend_list_item, null);
            }

            ImageView user_pic = (ImageView) convertView.findViewById(R.id.glb_selectfriend_list_item_header);

            TextView select_listView_name = (TextView) convertView.findViewById(R.id.glb_selectfriend_list_item_name);
            checkBox = (ImageView) convertView.findViewById(R.id.glb_selectfriend_list_item_check_icon);

            if (mList == null)
            {
                return convertView;
            }

            if (listView.getCount() == selectItem)
            {
                selectItem = selectItem - 1;
            }

            if (position == selectItem)
            {
                selectItemUid = (String) mList.get(position).get(DataProcess.USER_ID_INDEX);
                selectItemLookupKey = (String) mList.get(position).get(DataProcess.LOOK_UP_KEY_INDEX);
            }
            else if ((selectItem == -1) && (selectItem == position))
            {
                selectItemUid = (String) mList.get(position).get(DataProcess.USER_ID_INDEX);
                selectItemLookupKey = (String) mList.get(position).get(DataProcess.LOOK_UP_KEY_INDEX);
            }
            else
            {
            }

            if (!selectItemsCheck)
            {
                select_listView_name.setText((String) mList.get(position).get(DataProcess.USER_NAME_INDEX));

                bmp = ImageProcess.getPhoto(getContentResolver(),
                        Integer.parseInt((String) mList.get(position).get(DataProcess.USER_ID_INDEX)));
                if (bmp != null)
                {
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) 50 / bmp.getWidth(), (float) 50 / bmp.getWidth());
                    Bitmap resizeBmps = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    user_pic.setImageBitmap(resizeBmps);
                    bmp.recycle();
                    bmp = null;
                }
                else
                {
                    user_pic.setImageBitmap(resizeBmp);
                }

                if (map.get(position))
                {
                    checkBox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_checkbox_check));
                }
                else
                {
                    checkBox.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_checkbox));
                }
            }
            else
            {
                selectItem = -1;
                selectItemUid = null;
                selectItemLookupKey = null;
                select_listView_name.setText(getResources().getString(R.string.noData));
                checkBox.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        checkBox = (ImageView) view.findViewById(R.id.glb_selectfriend_list_item_check_icon);

        if (mainListView.map.get(position))
        {
            mainListView.map.put(position, false);
        }
        else
        {
            mainListView.map.put(position, true);
        }

        mainListView.notifyDataSetChanged();

        mainListView.setSelectItem(position);
        mainListView.notifyDataSetInvalidated();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (mainListView != null && mainListView.mList.size() > 1)
        {
            if (minLoadDataNo == 1 && !refreshFlag)
            {
                String itemCheckA = "", itemCheckB = "", itemCheckC = "", itemCheckD = "";

                if (mainListView.getCount() <= 1)
                {
                    itemCheckA = (String) mainListView.mList.get(firstVisibleListItemSite + 1).get(
                            DataProcess.PHONE_NUMBER_INDEX);
                }
                if (mainListView.getCount() <= 2)
                {
                    itemCheckB = (String) mainListView.mList.get(firstVisibleListItemSite + 2).get(
                            DataProcess.PHONE_NUMBER_INDEX);
                }
                if (mainListView.getCount() <= 3)
                {
                    itemCheckC = (String) mainListView.mList.get(firstVisibleListItemSite + 3).get(
                            DataProcess.PHONE_NUMBER_INDEX);
                }
                if (mainListView.getCount() <= 4)
                {
                    itemCheckD = (String) mainListView.mList.get(firstVisibleListItemSite + 4).get(
                            DataProcess.PHONE_NUMBER_INDEX);
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_UP)
        {
            switch (event.getKeyCode())
            {
                case KeyEvent.KEYCODE_BACK:
                    keyEventFunction();
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void keyEventFunction()
    {
        if (!dataProcessFunStatus)
        {
            finish();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void updataListData(int firstVisibleListItemSite, int lastVisibleListItemSite)
    {
        String PhoneNumberAndType[] = null;
        lastVisibleListItemSite = lastVisibleListItemSite + maxLoadDataNo;
        firstVisibleListItemSite = firstVisibleListItemSite - minLoadDataNo;

        if (firstVisibleListItemSite < 0)
        {
            firstVisibleListItemSite = 0;
        }

        if (listView.getCount() < lastVisibleListItemSite)
        {
            lastVisibleListItemSite = listView.getCount();
        }

        for (int x = firstVisibleListItemSite; x < lastVisibleListItemSite; x++)
        {
            if ((x > (listView.getCount() - minLoadDataNo - 1)) && (x < 0))
            {
                break;
            }
            else
            {
                item = (HashMap<String, String>) mainListView.mList.get(x);
                PhoneNumberAndType = DataProcess.getPhoneNumberAndType(mContext,
                        mainListView.mList.get(x).get(DataProcess.USER_ID_INDEX).toString());

                if (PhoneNumberAndType != null
                        && mainListView.mList.get(x).get(DataProcess.PHONE_NUMBER_INDEX).equals(""))
                {
                    item.put(DataProcess.TEL_TYPE_INDEX, PhoneNumberAndType[PHONE_TYPE]);
                    item.put(DataProcess.PHONE_NUMBER_INDEX, PhoneNumberAndType[PHONE_NUMBER]);
                }
            }
        }
        mainListView.notifyDataSetChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void dataProcessFunction()
    {
        dataProcessFunStatus = true;
        List<Map<String, Object>> newData = new ArrayList<Map<String, Object>>();
        Map<String, Object> newItem;
        for (int i = 0; i < mainListView.getCount(); i++)
        {
            if (mainListView.map.get(i))
            {
                newItem = new HashMap<String, Object>();
                newItem.put(DataProcess.USER_NAME_INDEX, mainListView.mList.get(i).get(DataProcess.USER_NAME_INDEX)
                        .toString());
                newItem.put(DataProcess.PHONE_NUMBER_INDEX,
                        mainListView.mList.get(i).get(DataProcess.PHONE_NUMBER_INDEX).toString());
                newData.add(newItem);
            }
        }

        for (int i = 0; i < newData.size(); i++)
        {
            System.out.println("NAME:" + newData.get(i).get(DataProcess.USER_NAME_INDEX).toString());
            System.out.println("TEL:" + newData.get(i).get(DataProcess.PHONE_NUMBER_INDEX).toString());
        }

        setResultIntent(newData);
        dataProcessFunStatus = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setResultIntent(List<Map<String, Object>> data)
    {
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        ArrayList bundlelist = new ArrayList();
        bundlelist.add(data);
        bundle.putParcelableArrayList(DataProcess.PROCESS_SELECTION_FRIEND_LIST, bundlelist);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                {
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

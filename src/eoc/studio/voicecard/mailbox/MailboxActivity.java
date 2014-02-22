package eoc.studio.voicecard.mailbox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.facebook.FriendsAdapterData;
import eoc.studio.voicecard.menu.ClearAllMail;
import eoc.studio.voicecard.menu.DeleteSelectedMail;
import eoc.studio.voicecard.utils.ListUtility;

public class MailboxActivity extends BaseActivity {
    private static final String TAG = "MailboxActivity";
    
    // Data
    private List<Mail> mails;
    private MailsAdapterData mailsAdapterData;
    private MailsAdapterView mailAdapterView;
    private int firstVisiblePosition = 0;
    private int currentListSize = 0;
    private int lastVisiblePosition = 0;
    
    // Views
    private TextView mailInfo, errorMsg;
    private ListView showMails;
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);
        
        findViews();
        mailsAdapterData = new MailsAdapterData(MailboxActivity.this);
        mailsAdapterData.open();
        CreateFakeDbThread createFakeDbThread = new CreateFakeDbThread();
        createFakeDbThread.start();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        showMailInfo(0);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        if (mailAdapterView != null) {
            mailAdapterView.setPause(true);
            if (mails != null) {
                mails.clear();
            }
            mailAdapterView.clearData();
        }
    }
    
    private void findViews() {
        mailInfo = (TextView) findViewById(R.id.act_mailbox_tv_title_bar);
        showMails = (ListView) findViewById(R.id.act_mailbox_lv);
        errorMsg = (TextView) findViewById(R.id.act_mailbox_tv_errorMsg);
        
        Button selectAll = (Button) findViewById(R.id.act_mailbox_bt_title_bar);
        
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mailAdapterView != null) {
                    mailAdapterView.selectAll();
                }
            }
        });
        
        DeleteSelectedMail deleteSelectedMail = (DeleteSelectedMail) findViewById(R.id.act_mailbox_iv_menu_deleteSelectedMail);
        
        deleteSelectedMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailAdapterView.setInterrupt(true);
                deleteSelectedMails();
            }
        });
        
        ClearAllMail clearAllMail = (ClearAllMail) findViewById(R.id.act_mailbox_iv_menu_clearAllMail);
        
        clearAllMail.setOnClickListener(new View.OnClickListener() {     
            @Override
            public void onClick(View v) {
                mailAdapterView.setInterrupt(true);
                clearAllMails();
            }
        });
        
        showMails.setOnScrollListener(listScrollListener);
    }
    
    private void createFakeDb() {
        if (mailsAdapterData != null) {
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.luxurylife.com.tw/images/item_tiny/rosendahl_kaycollection_dog_title_tiny.jpg", null,
                    "http://speech", "http://sign", "2014/02/13", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://i1.squidoocdn.com/resize/squidoo_images/60/lens18720752_1318951596camille_cat.jpg", null,
                    "http://speech", "http://sign", "2014/02/14", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.pavi.com.tw/work/ecard/ecard_ite_2012_xmas_small.jpg", null, "http://speech",
                    "http://sign", "2014/02/12", 0);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.luxurylife.com.tw/images/item_tiny/rosendahl_kaycollection_dog_title_tiny.jpg", null,
                    "http://speech", "http://sign", "2014/02/11", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://i1.squidoocdn.com/resize/squidoo_images/60/lens18720752_1318951596camille_cat.jpg", null,
                    "http://speech", "http://sign", "2014/02/13", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.pavi.com.tw/work/ecard/ecard_ite_2012_xmas_small.jpg", null, "http://speech",
                    "http://sign", "2014/02/14", 0);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.luxurylife.com.tw/images/item_tiny/rosendahl_kaycollection_dog_title_tiny.jpg", null,
                    "http://speech", "http://sign", "2014/03/14", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://i1.squidoocdn.com/resize/squidoo_images/60/lens18720752_1318951596camille_cat.jpg", null,
                    "http://speech", "http://sign", "2014/04/14", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.pavi.com.tw/work/ecard/ecard_ite_2012_xmas_small.jpg", null, "http://speech",
                    "http://sign", "2014/02/14", 0);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.luxurylife.com.tw/images/item_tiny/rosendahl_kaycollection_dog_title_tiny.jpg", null,
                    "http://speech", "http://sign", "2014/05/14", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://i1.squidoocdn.com/resize/squidoo_images/60/lens18720752_1318951596camille_cat.jpg", null,
                    "http://speech", "http://sign", "2014/06/14", 1);
            
            mailsAdapterData.create("0", "123456", "John", "Wang", "Test", "Test123", "15", "blue",
                    "http://www.pavi.com.tw/work/ecard/ecard_ite_2012_xmas_small.jpg", null, "http://speech",
                    "http://sign", "2014/07/14", 0);
        }
    }
    
    private boolean loadDb() {
        mails = new ArrayList<Mail>();

        String rowId, sendId, sendFrom, sendFromName, sendTo, subject, body, fontSize, fontColor, imgLink, speech, sign, sendTime;
        int checkState = 0, newState;
        byte[] img;

        Cursor cursor = mailsAdapterData.getAll();
        if (cursor != null) {
            int count = cursor.getCount();
            Log.d(TAG, "cursor size is " + count);
            if (count > 0) {
                while (cursor.moveToNext()) {
                    rowId = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_ROW_ID));
                    sendId = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SEND_ID));
                    sendFrom = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SEND_FROM));
                    sendFromName = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SEND_FROM_NAME));
                    sendTo = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SEND_TO));
                    subject = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SUBJECT));
                    body = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_BODY));
                    fontSize = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_FONT_SIZE));
                    fontColor = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_FONT_COLOR));
                    imgLink = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_IMG_LINK));
                    img = cursor.getBlob(cursor.getColumnIndex(MailsAdapterData.KEY_IMG));
                    speech = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SPEECH));
                    sign = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SIGN));
                    sendTime = cursor.getString(cursor.getColumnIndex(MailsAdapterData.KEY_SEND_TIME));
                    newState = cursor.getInt(cursor.getColumnIndex(MailsAdapterData.KEY_NEW_STATE));
                    
                    mails.add(new Mail(rowId, sendId, sendFrom, sendFromName, sendTo, subject, body, fontSize,
                            fontColor, imgLink, img, speech, sign, sendTime, checkState, newState));
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void updateView(boolean isEmpty) {
        if (mailAdapterView != null)
            mailAdapterView.setInterrupt(false);
        if (isEmpty) {
            errorMsg.setVisibility(View.VISIBLE);
            showMails.setVisibility(View.INVISIBLE);
        } else {
            errorMsg.setVisibility(View.INVISIBLE);
            showMails.setVisibility(View.VISIBLE);
            mailAdapterView = new MailsAdapterView(MailboxActivity.this, mails, mailsAdapterData, showMails);
            showMails.setAdapter(mailAdapterView);
        }
        dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
    }
    
    private void deleteSelectedMails() {
        if (mailAdapterView != null) {
            if (mailsAdapterData != null) {
                boolean success = mailsAdapterData.deleteSelected(mailAdapterView.getSelectedItem());
                
                if (success) {
                    clearData();
                }
            }
        }
    }
    
    private void clearAllMails() {
        if (mailsAdapterData != null) {
            boolean success = mailsAdapterData.delete();
            
            if (success) {
                clearData();
            }
        }
    }
    
    private void clearData() {
        mails.clear();
        mailAdapterView.clearData();
        showMailInfo(0);
        
        LoadDbThread loadDbThread = new LoadDbThread();
        loadDbThread.start();
    }
    
    public void showMailInfo(int count) {
        mailInfo.setText(getResources().getString(R.string.select_mails, count));
    }
    
    private void getMailsImgfromDB() {
        if (mailsAdapterData != null) {
            Log.d(TAG, "firstVisiblePosition is " + firstVisiblePosition);
            Log.d(TAG, "lastVisiblePosition is " + lastVisiblePosition);
            mailAdapterView.loadImagefromPosition(firstVisiblePosition, lastVisiblePosition);
        }
    }
            
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ListUtility.CREATE_DB_COMPLETE:
                LoadDbThread loadDbThread = new LoadDbThread();
                loadDbThread.start();
                break;
            case ListUtility.LOAD_DB_COMPLETE:
                updateView(false);
                break;
            case ListUtility.LOAD_DB_EMPTY:
                updateView(true);
                break;
            }
        }
    };
    
    private Handler dialogHandler = new Handler() {
        @Override  
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ListUtility.SHOW_WAITING_DIALOG:
                Log.d(TAG, "show waiting dialog ");
                progressDialog = ProgressDialog.show(MailboxActivity.this, "",
                        getResources().getString(R.string.file_process_loading));
                break;
            case ListUtility.DISMISS_WAITING_DIALOG:
                Log.d(TAG, "dismiss dialog ");
                if (progressDialog != null)
                    progressDialog.dismiss();
                break;
            }
        }
    };
    
    private Handler downloadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ListUtility.GET_THUMBNAIL:
                Log.d(TAG, "GET_THUMBNAIL ");
                mailAdapterView.setInterrupt(false);
                getMailsImgfromDB();
                break;
            }
        }
    };
    
    private AbsListView.OnScrollListener listScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            firstVisiblePosition = firstVisibleItem;
            lastVisiblePosition = firstVisiblePosition + visibleItemCount - 1;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.d(TAG, "scrollState " + scrollState);
            if (currentListSize != 0) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mailAdapterView != null) {
                    downloadHandler.sendEmptyMessageDelayed(ListUtility.GET_THUMBNAIL, 1500);
                }
                if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && mailAdapterView != null) {
                    downloadHandler.removeMessages(ListUtility.GET_THUMBNAIL);
                }
                mailAdapterView.setInterrupt(true);
            } else {
                Log.d(TAG, "currentListSize is zero ");
            }
        } 
    };
    
    private class CreateFakeDbThread extends Thread {        
        @Override
        public void run() {
            createFakeDb();
            uiHandler.sendEmptyMessage(ListUtility.CREATE_DB_COMPLETE);
        }
    }
    
    private class LoadDbThread extends Thread {
        @Override
        public void run() {
            dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
            boolean isSucess = loadDb();
            if (isSucess)
                uiHandler.sendEmptyMessage(ListUtility.LOAD_DB_COMPLETE);
            else
                uiHandler.sendEmptyMessage(ListUtility.LOAD_DB_EMPTY);
        }
    }
}
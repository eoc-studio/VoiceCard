package eoc.studio.voicecard.facebook;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.ListUtility;

public class SelectFriendActivity extends BaseActivity {
    private static final String TAG = "SelectFriendActivity";
    private FacebookManager facebookManager;
    private FriendsAdapterView friendsAdapterView;
    private FriendsAdapterData friendsAdapterData;
    
    private List<FriendInfo> friendList;
    private int firstVisiblePosition = 0;
    private int currentListSize = 0;
    private int lastVisiblePosition = 0;
    
    //Views
    private ListView showFriends;
    private EditText searchFriend;
    private TextView displayMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        facebookManager = new FacebookManager(SelectFriendActivity.this, savedInstanceState);
        friendsAdapterData = new FriendsAdapterData(SelectFriendActivity.this);
        findViews();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getFriendsfromWeb();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (friendList != null) {
            friendList.clear();
        }
        if (friendsAdapterView != null) {
            friendsAdapterView.setPause(true);
            friendsAdapterView.clearList();
        }
        
        if (friendsAdapterData != null) {
            friendsAdapterData.delete();
            friendsAdapterData.close();
        }
        
        facebookManager.dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    private void findViews() {
        searchFriend = (EditText) findViewById(R.id.act_select_friend_et_search_bar);
        showFriends = (ListView) findViewById(R.id.act_select_friend_lv);
        ImageView returnButton = (ImageView) findViewById(R.id.act_select_friend_iv_button_return);
        ImageView okButton = (ImageView) findViewById(R.id.act_select_friend_iv_button_ok);
        ImageView searchButton = (ImageView) findViewById(R.id.act_select_friend_iv_search_button);
        displayMessage = (TextView) findViewById(R.id.act_select_friend_tv_display_message);
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        showFriends.setOnItemClickListener(new UserListClickListener());
        showFriends.setOnScrollListener(listScrollListener);
        
        returnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmAction();
            }
        });
        
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchAction();
            }
        });
    }
    
    private void confirmAction() {
        String friendId, friendName, firendBirthday;
        ArrayList<FriendInfo> selectedFriendList = new ArrayList<FriendInfo>();
        
        Cursor cursor = friendsAdapterData.getSelectedFriend();
        if (cursor != null) {
            Log.d(TAG, "confirmAction() cursor size is " + cursor.getCount());
            while (cursor.moveToNext()) {
                friendId = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_ID));
                friendName = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_NAME));
                firendBirthday = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_BIRTHDAY));
                selectedFriendList.add(new FriendInfo(friendId, friendName, firendBirthday, "", null, 0, 0));
            }
        }
        cursor.close();
        
        Intent returnIntent = new Intent();
        Bundle returnBundle = new Bundle();
        returnBundle.putParcelableArrayList(FriendInfo.GET_FRIEND, selectedFriendList);
        returnIntent.putExtras(returnBundle);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    
    private void searchAction() {
        facebookManager.dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
        friendsAdapterView.setInterrupt(true);
        friendList.clear();
        LoadDbThread loadDbThread = new LoadDbThread(searchFriend.getText().toString());
        loadDbThread.start();
    }
    
    private void getFriendsfromWeb() {
        if (facebookManager != null) 
            facebookManager.getFriendList(new RequestGraphUserListCallback());
    }
    
    private void getFriendsImgfromDB() {
        if (friendsAdapterData != null) {
            Log.d(TAG, "firstVisiblePosition is " + firstVisiblePosition);
            Log.d(TAG, "lastVisiblePosition is " + lastVisiblePosition);
            friendsAdapterView.loadImagefromPosition(firstVisiblePosition, lastVisiblePosition);
        }
    }
    
    private void processUserListReponse(List<GraphUser> users) {
        if (users != null) {
            Log.d(TAG, "user list size is " + users.size());
            CreateDbThread createDbThread = new CreateDbThread(users);
            createDbThread.start();
        }
    }
    
    private void createDb(List<GraphUser> users) {
        if (friendsAdapterData != null) {
            friendsAdapterData.open();
            currentListSize = users.size();
            String friendId = "", friendName = "", firendBirthday = "", friendImgLink = "";
            friendList = new ArrayList<FriendInfo>();

            for (int i = 0; i < users.size(); i++) {
                try {
                    friendImgLink = users.get(i).getInnerJSONObject().getJSONObject(JSONTag.PICTURE)
                            .getJSONObject(JSONTag.DATA).getString(JSONTag.URL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                friendId = users.get(i).getId();
                friendName = users.get(i).getName();
                firendBirthday = users.get(i).getBirthday();
                
                friendsAdapterData.create(friendId, friendName, firendBirthday, friendImgLink, null,
                        FriendsAdapterData.UNSELECT, FriendsAdapterData.NOTINSTALL);
                
                friendList.add(new FriendInfo(friendId, friendName, firendBirthday, friendImgLink, null,
                        FriendsAdapterData.UNSELECT, FriendsAdapterData.NOTINSTALL));
            }
            Log.d(TAG, "Create Db finish() ");
        }
    }
    
    private void updateView() {
        if (friendList.size() > 0) {
            friendsAdapterView = new FriendsAdapterView(SelectFriendActivity.this, friendList, friendsAdapterData,
                    showFriends);
            showFriends.setVisibility(View.VISIBLE);
            showFriends.setAdapter(friendsAdapterView);
        } else {
            displayMessage.setText(getResources().getString(R.string.user_no_result));
            showFriends.setVisibility(View.INVISIBLE);
        }
        facebookManager.dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
    }
        
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ListUtility.CREATE_DB_COMPLETE:
                updateView();
                break;
            case ListUtility.SEARCH_COMPLETE:
                friendsAdapterView.clearList();
                updateView();
                break;
            }
        }
    };
    
    private Handler downloadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ListUtility.GET_THUMBNAIL:
                Log.d(TAG, "GET_THUMBNAIL ");
                friendsAdapterView.setInterrupt(false);
                getFriendsImgfromDB();
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
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && friendsAdapterView != null) {
                    downloadHandler.sendEmptyMessageDelayed(ListUtility.GET_THUMBNAIL, 1500);
                }
                if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && friendsAdapterView != null) {
                    downloadHandler.removeMessages(ListUtility.GET_THUMBNAIL);
                }
                friendsAdapterView.setInterrupt(true);
            } else {
                Log.d(TAG, "currentListSize is zero ");
            }
        } 
    };
        
    private class CreateDbThread extends Thread {
        List<GraphUser> users;
        public CreateDbThread(List<GraphUser> users) {
            this.users = users;
        }
        @Override
        public void run() {
            createDb(users);
            uiHandler.sendEmptyMessage(ListUtility.CREATE_DB_COMPLETE);
        }
    }
    
    private class LoadDbThread extends Thread {
        String keyword;
        public LoadDbThread(String keyword) {
            this.keyword = keyword;
        }
        @Override
        public void run() {
            friendList = new ArrayList<FriendInfo>();
            String friendId = "", friendName = "", firendBirthday = "", friendImgLink = "";
            byte[] friendImg = null;
            int selectState = 0, installState = 0;
            Cursor cursor = friendsAdapterData.seachResult(keyword);
            if (cursor != null) {
                Log.d(TAG, "cursor size is " + cursor.getCount());
                while (cursor.moveToNext()) {
                    friendId = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_ID));
                    friendName = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_NAME));
                    firendBirthday = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_BIRTHDAY));
                    friendImgLink = cursor.getString(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_IMG_LINK));
                    friendImg = cursor.getBlob(cursor.getColumnIndex(FriendsAdapterData.KEY_FRIEND_IMG));
                    selectState = cursor.getInt(cursor.getColumnIndex(FriendsAdapterData.KEY_SELECT_STATE));
                    installState = cursor.getInt(cursor.getColumnIndex(FriendsAdapterData.KEY_INSTALL_STATE));
                    friendList.add(new FriendInfo(friendId, friendName, firendBirthday, friendImgLink, friendImg,
                            selectState, installState));
                }
            }
            cursor.close();
            uiHandler.sendEmptyMessage(ListUtility.SEARCH_COMPLETE);
        }
    }
        
    private class RequestGraphUserListCallback implements Request.GraphUserListCallback {
        @Override
        public void onCompleted(List<GraphUser> users, Response response) {
            processUserListReponse(users);
        }
    }

    private class UserListClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int state = 0;
            if (friendsAdapterData != null) {
                state = friendsAdapterData.getSelectedState(((FriendInfo) friendsAdapterView.getItem(position))
                        .getFriendId());
                ImageView selectIcon = (ImageView) view.findViewById(R.id.glb_selectfriend_list_item_check_icon);
                Log.d(TAG, "state is === " + state);
                
                if (state == FriendsAdapterData.UNSELECT) {
                    friendsAdapterData.updateSelectedState(
                            ((FriendInfo) friendsAdapterView.getItem(position)).getFriendId(),
                            FriendsAdapterData.SELECT);
                    selectIcon.setImageDrawable(SelectFriendActivity.this.getResources().getDrawable(
                            R.drawable.icon_checkbox_check));
                    friendsAdapterView.updateSelectedState(position, FriendsAdapterData.SELECT);
                } else {
                    friendsAdapterData.updateSelectedState(
                            ((FriendInfo) friendsAdapterView.getItem(position)).getFriendId(),
                            FriendsAdapterData.UNSELECT);
                    selectIcon.setImageDrawable(SelectFriendActivity.this.getResources().getDrawable(
                            R.drawable.icon_checkbox));
                    friendsAdapterView.updateSelectedState(position, FriendsAdapterData.UNSELECT);
                }
            }
        }
    }
}

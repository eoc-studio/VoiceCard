package eoc.studio.voicecard.facebook;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.content.Context;
import android.content.Intent;
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
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.WebImageUtility;

public class SelectFriendActivity extends BaseActivity {
    private static final String TAG = "SelectFriendActivity";
    private static final int CREATE_DB_COMPLETE = 0;
    private static final int REFRESH = 1;
    private static final int GET_THUMBNAIL = 0;
    private FacebookManager facebookManager;
    private FriendsAdapterView friendsAdapterView;
    private FriendsAdapterData friendsAdapterData;
    
    private List<FriendInfo> friendList;
    private int position = 0;
    private int currentListSize = 0;
    
    //Views
    private ListView showFriends;
    
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
        getFriendList();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        friendsAdapterView.setPauseState(true);
        if (friendsAdapterData != null) {
            friendsAdapterData.delete();
            friendsAdapterData.close();
        }
        facebookManager.dialogHandler.sendEmptyMessage(FacebookManager.DISMISS_WAITING_DIALOG);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
    
    private void findViews() {
        EditText searchFriend = (EditText) findViewById(R.id.act_select_friend_search_bar);
        showFriends = (ListView) findViewById(R.id.act_select_friend_list);
        ImageView returnButton = (ImageView) findViewById(R.id.act_select_friend_iv_button_return);
        ImageView okButton = (ImageView) findViewById(R.id.act_select_friend_iv_button_ok);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        showFriends.setOnItemClickListener(new UserListClickListener());
        showFriends.setOnScrollListener(listScrollListener);
    }
    
    private void getFriendList() {
        if (facebookManager != null) 
            facebookManager.getFriendList(new RequestGraphUserListCallback());
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
                    friendImgLink = users.get(i).getInnerJSONObject().getJSONObject(FacebookManager.JSONTag.PICTURE)
                            .getJSONObject(FacebookManager.JSONTag.DATA).getString(FacebookManager.JSONTag.URL);
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
        
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case CREATE_DB_COMPLETE:
                friendsAdapterView = new FriendsAdapterView(SelectFriendActivity.this, friendList, friendsAdapterData,
                        showFriends);
                showFriends.setAdapter(friendsAdapterView);
                facebookManager.dialogHandler.sendEmptyMessage(FacebookManager.DISMISS_WAITING_DIALOG);
                break;
            case REFRESH:
                break;
            }
        }
    };
    
    private Handler downloadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GET_THUMBNAIL:
                Log.d(TAG, "GET_THUMBNAIL ");
                break;
            }
        }
    };
    
    private AbsListView.OnScrollListener listScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            position = firstVisibleItem;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.d(TAG, "scrollState " + scrollState);
            if (currentListSize != 0) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && friendsAdapterView != null) {
                    downloadHandler.sendEmptyMessageDelayed(GET_THUMBNAIL, 1500);
                }
                if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && friendsAdapterView != null) {
//                    mCameraListAdapter.interruptApiThread();
                    downloadHandler.removeMessages(GET_THUMBNAIL);
                }
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
            uiHandler.sendEmptyMessage(CREATE_DB_COMPLETE);
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
                ImageView selectIcon = (ImageView) view.findViewById(R.id.user_info_check_icon);
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

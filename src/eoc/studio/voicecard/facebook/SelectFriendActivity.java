package eoc.studio.voicecard.facebook;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;

public class SelectFriendActivity extends BaseActivity {
    private static final String TAG = "SelectFriendActivity";
    private FacebookManager facebookManager;
    private FriendsAdapter friendsAdapter;
    
    //Views
    private ListView showFriends;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        facebookManager = new FacebookManager(this, savedInstanceState);
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
    }
    
    private void findViews() {
        EditText searchFriend = (EditText) findViewById(R.id.act_select_friend_search_bar);
        showFriends = (ListView) findViewById(R.id.act_select_friend_list);
        ImageView returnButton = (ImageView) findViewById(R.id.act_select_friend_iv_button_return);
        ImageView okButton = (ImageView) findViewById(R.id.act_select_friend_iv_button_ok);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
    }
    
    private void getFriendList() {
        if (facebookManager != null) 
            facebookManager.getFriendList(new RequestGraphUserListCallback());
    }
    
    private void processUserListReponse(List<GraphUser> users)
    {
        if (users != null)
        {
            Log.d(TAG, "user list size is " + users.size());
            friendsAdapter = new FriendsAdapter(SelectFriendActivity.this, users);
            showFriends.setAdapter(friendsAdapter);
            showFriends.setOnItemClickListener(new UserListClickListener());
        }
    }
    
    private void showToast(GraphUser user)
    {
        if (user != null) {
            try {
                String userId = user.getId();
                JSONObject userJSON = user.getInnerJSONObject();
                Log.d(TAG, "user is " + userJSON);
                Toast.makeText(
                        SelectFriendActivity.this,
                        FacebookManager.ShowField.USER_ID
                                + userId
                                + FacebookManager.ShowField.USER_NAME
                                + user.getName()
                                + FacebookManager.ShowField.USER_BIRTHDAY
                                + user.getBirthday()
                                + FacebookManager.ShowField.USER_ICON
                                + userJSON.getJSONObject(FacebookManager.JSONTag.PICTURE)
                                        .getJSONObject(FacebookManager.JSONTag.DATA)
                                        .getString(FacebookManager.JSONTag.URL), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    private class RequestGraphUserListCallback implements Request.GraphUserListCallback
    {
        @Override
        public void onCompleted(List<GraphUser> users, Response response)
        {
            facebookManager.dialogHandler.sendEmptyMessage(FacebookManager.DISMISS_WAITING_DIALOG);
            processUserListReponse(users);
        }
    }
    
    private class UserListClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            showToast((GraphUser) friendsAdapter.getItem(position));
        }
    }
}

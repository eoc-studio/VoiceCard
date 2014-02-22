package eoc.studio.voicecard.facebook;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.model.GraphUser;

import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.TestMainActivity;

public class TestFacebookActivity extends BaseActivity
{
	private static final String TAG = "TestFacebookActivity";
	private static final String NAME = "Test";
	private static final String PICTURE = "http://upload.wikimedia.org/wikipedia/commons/2/26/YellowLabradorLooking_new.jpg";
	private static final String CAPTION = "CAPTION";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String LINK = "http://upload.wikimedia.org/wikipedia/commons/2/26/YellowLabradorLooking_new.jpg";
	private FacebookManager facebookManager;
	private FriendsAdapterView friendsAdapter;
	private String userId = "1442881101";
	private String owerId = "100007720118618";
	private String testId = "100007720118618";
//	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_facebook);
		facebookManager = new FacebookManager(this, savedInstanceState);
		findViews();
	}

	@Override
	public void onStart()
	{
		super.onStart();
//		Session.getActiveSession().addCallback(facebookManager.getSessionStatusCallBack());
	}

	@Override
	public void onStop()
	{
		super.onStop();
//		Session.getActiveSession().removeCallback(facebookManager.getSessionStatusCallBack());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState outState is " + outState);
		Session.saveSession(Session.getActiveSession(), outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "requestCode is " + requestCode);
		Log.d(TAG, "resultCode is " + resultCode);
		Log.d(TAG, "data is " + data);
		if (data != null) {
		    Log.d(TAG, "bundle is " + data.getExtras().toString());
		}
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        switch (requestCode) {
        case FriendInfo.GET_FRIEND_REQUEST_CODE:
            Log.d(TAG, "GET_FRIEND_REQUEST_CODE");
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    ArrayList<FriendInfo> friendList = bundle.getParcelableArrayList(FriendInfo.GET_FRIEND);
                    
                    if (friendList != null) {
                        for (int i = 0; i < friendList.size(); i++) {
                            Log.d(TAG, "ID is " + friendList.get(i).getFriendId());
                            Log.d(TAG, "Name is " + friendList.get(i).getFriendName());
                            Log.d(TAG, "Birthday is " + friendList.get(i).getFriendBirthday());
                        }
                    }
                }
            }
            break;
        }
	}

    private void findViews() {
        Button getFriendList = (Button) findViewById(R.id.getFriendList);
        Button inviteFriend = (Button) findViewById(R.id.inviteFriend);
        Button publishMe = (Button) findViewById(R.id.publishMe);
        Button publishFriend = (Button) findViewById(R.id.publishFriend);
        Button logout = (Button) findViewById(R.id.logout);

        getFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestFacebookActivity.this, SelectFriendActivity.class);
                startActivityForResult(intent, FriendInfo.GET_FRIEND_REQUEST_CODE);
            }
        });
		
		inviteFriend.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (facebookManager != null)
				{
					facebookManager.inviteFriend(testId, null);
				}
			}
		});
		
		publishMe.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (facebookManager != null)
                {
                    facebookManager.publishTimeline(owerId, NAME, PICTURE, CAPTION, DESCRIPTION, LINK);
                }
            }
        });
		
		publishFriend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (facebookManager != null)
                {
                    facebookManager.publishTimeline(testId, NAME, PICTURE, CAPTION, DESCRIPTION, LINK);
                }
            }
        });
		
		logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (facebookManager != null)
                {
                    facebookManager.logout();
                    TestFacebookActivity.this.finish();
                }
            }
        });
	}
}

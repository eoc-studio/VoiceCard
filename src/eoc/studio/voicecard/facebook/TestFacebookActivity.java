package eoc.studio.voicecard.facebook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.facebook.FacebookManager.RequestGraphUserCallback;
import eoc.studio.voicecard.facebook.enetities.FriendInfo;
import eoc.studio.voicecard.facebook.enetities.Photo;
import eoc.studio.voicecard.facebook.enetities.Publish;
import eoc.studio.voicecard.facebook.enetities.UserInfo;
import eoc.studio.voicecard.facebook.friends.SelectFriendActivity;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.utils.ListUtility;

public class TestFacebookActivity extends BaseActivity
{
	private static final String TAG = "TestFacebookActivity";
	private FacebookManager facebookManager;
	private String owerId = "100007720118618";
	private String[] testIdList = {"100007811983123", "100007720118618"};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_facebook);
		facebookManager = FacebookManager.getInstance(TestFacebookActivity.this);
		findViews();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Session.saveSession(Session.getActiveSession(), outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
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
                            Log.d(TAG, "ImgLink is " + friendList.get(i).getFriendImgLink());
                        }
                    }
                }
            }
            break;
        }
	}

    private void findViews() {
        Button getUserProfile = (Button) findViewById(R.id.getUserProfile);
        Button getFriendList = (Button) findViewById(R.id.getFriendList);
        Button inviteFriend = (Button) findViewById(R.id.inviteFriend);
        Button publishMe = (Button) findViewById(R.id.publishMe);
        Button publishFriend = (Button) findViewById(R.id.publishFriend);
        Button logout = (Button) findViewById(R.id.logout);
        Button upload = (Button) findViewById(R.id.upload);
        Button publishUserNoDialog = (Button) findViewById(R.id.publishUserFeed);
        Button publishNews = (Button) findViewById(R.id.publishNews);
        
        getUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facebookManager != null)
                {
                    facebookManager.getUserProfile(TestFacebookActivity.this, facebookManager.new RequestGraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                JSONObject userJSON = user.getInnerJSONObject();
                                if(userJSON != null) {
                                    UserInfo userInfo = new UserInfo(userJSON);
                                    
                                    Log.d(TAG, "userInfo id is " + userInfo.getId());
                                }
                            } else {
                                Log.d(TAG, "userInfo id is null ");
                            }
                        }
                    });
                }
            }
        });

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
                    File imgfile = new File("/storage/sdcard1/Download/jordan.jpg");
                    Uri imgUri = Uri.fromFile(imgfile);
                    
                    File signfile = new File("/storage/sdcard1/Download/jordan.jpg");
                    Uri signUri = Uri.fromFile(signfile);
                    
                    Card card = new Card(0, null, "Voice Card", 0, 0, 0, 0, 0, 0);
                    card.setImage(imgUri);
                    card.setSound(imgUri);
                    card.setMessage("Voice Card invite", 0, 0);
                    card.setSignDraftImage(signUri);
                    card.setName("Test");
                    facebookManager.inviteFriend(TestFacebookActivity.this, null, card);
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
                    facebookManager.publishTimeline(TestFacebookActivity.this, new Publish(owerId,
                            Publish.DEFAULT_NAME, Publish.DEFAULT_PICTURE, Publish.DEFAULT_CAPTION,
                            Publish.DEFAULT_DESCRIPTION, Publish.DEFAULT_LINK));
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
                    facebookManager.publishTimeline(TestFacebookActivity.this, new Publish(owerId,
                            Publish.DEFAULT_NAME, Publish.DEFAULT_PICTURE, Publish.DEFAULT_CAPTION,
                            Publish.DEFAULT_DESCRIPTION, Publish.DEFAULT_LINK));
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
		
		upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (facebookManager != null)
                {
                    facebookManager.upload(new Photo(Publish.DEFAULT_NAME, getPhoto()));
                }
            }
        });
		
		publishUserNoDialog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (facebookManager != null)
                {
                    facebookManager.publishUserFeed(TestFacebookActivity.this, new Publish(owerId,
                            Publish.DEFAULT_NAME, null, Publish.DEFAULT_CAPTION, Publish.DEFAULT_DESCRIPTION,
                            Publish.DEFAULT_LINK));
                }
            }
        });
		
		publishNews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (facebookManager != null)
                {
                    File file = new File("/storage/sdcard1/Download/jordan.jpg");
                    Uri fileUri = Uri.fromFile(file);
                    Log.d(TAG, "fileUri is " + fileUri);
                    facebookManager.publishNews(TestFacebookActivity.this, "100007811983123", fileUri);
                }
            }
        });
	}
    
    private byte[] getPhoto() {
        Drawable drawable = getResources().getDrawable(R.drawable.dog);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }
}

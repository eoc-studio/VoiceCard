package eoc.studio.voicecard.facebook;

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

public class TestFacebookActivity extends BaseActivity
{
	private static final String TAG = "MainActivity";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private FacebookManager facebookManager;
	private FriendsAdapter friendsAdapter;
	private ImageView showPicture;
	private ListView showFriends;
	private String userId = "1442881101";
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_facebook);
		facebookManager = new FacebookManager(this, statusCallback, savedInstanceState);
		findViews();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
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
	}

	private void findViews()
	{
		Button getUserProfile = (Button) findViewById(R.id.getUserProfile);
		Button getFriendList = (Button) findViewById(R.id.getFriendList);
		Button getUserPicture = (Button) findViewById(R.id.getPicture);
		showPicture = (ImageView) findViewById(R.id.showPicture);
		showFriends = (ListView) findViewById(R.id.showFriends);
		getUserProfile.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (facebookManager != null)
				{
					showProgressDialog(getResources().getString(R.string.get_user_profile));
					facebookManager.getUserProfile(new RequestGraphUserCallback());
				}
			}
		});
		getFriendList.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (facebookManager != null)
				{
					showProgressDialog(getResources().getString(R.string.get_friend_list));
					facebookManager.getFriendList(new RequestGraphUserListCallback());
				}
			}
		});
		getUserPicture.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (facebookManager != null)
				{
					showProgressDialog(getResources().getString(R.string.get_picture));
					facebookManager.getUserImg(true, new RequestUserPicture(), userId);
					// facebookManager.shareImage();
				}
			}
		});
	}

	private void processUserPictureResponse(ImageResponse response)
	{
		// First check if the response is for the right request. We may have:
		// 1. Sent a new request, thus super-ceding this one.
		// 2. Detached this view, in which case the response should be
		// discarded.
		if (response.getRequest() == facebookManager.getLastRequest())
		{
			facebookManager.setLastRequest(null);
			Bitmap responseImage = response.getBitmap();
			Exception error = response.getError();
			if (error != null)
			{
				Log.d(TAG, "error is " + error.toString());
			}
			else if (responseImage != null)
			{
				showPicture.setImageBitmap(responseImage);
			}
		}
	}

	private void processUserListReponse(List<GraphUser> users)
	{
		if (users != null)
		{
			Log.d(TAG, "user list size is " + users.size());
			friendsAdapter = new FriendsAdapter(TestFacebookActivity.this, users);
			showFriends.setAdapter(friendsAdapter);
			showFriends.setOnItemClickListener(new UserListClickListener());
		}
	}

	private void showToast(GraphUser user)
	{
		if (user != null)
		{
			try
			{
				userId = user.getId();
				JSONObject userJSON = user.getInnerJSONObject();
				Log.d(TAG, "user is " + userJSON);
				Toast.makeText(
						TestFacebookActivity.this,
						FacebookManager.ShowField.USER_ID
								+ userId
								+ FacebookManager.ShowField.USER_NAME
								+ user.getName()
								+ FacebookManager.ShowField.USER_BIRTHDAY
								+ user.getBirthday()
								+ FacebookManager.ShowField.USER_ICON
								+ userJSON.getJSONObject(FacebookManager.JSONTag.PICTURE)
										.getJSONObject(FacebookManager.JSONTag.DATA)
										.getString(FacebookManager.JSONTag.URL), Toast.LENGTH_SHORT)
						.show();
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void showProgressDialog(String title)
	{
		progressDialog = ProgressDialog.show(TestFacebookActivity.this, title, getResources()
				.getString(R.string.file_process_loading));
	}

	private void dismissProgressDialog()
	{
		if (progressDialog != null)
		{
			progressDialog.dismiss();
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback
	{
		@Override
		public void call(Session session, SessionState state, Exception exception)
		{
			if (session.isOpened())
			{}
		}
	}

	private class RequestGraphUserCallback implements Request.GraphUserCallback
	{
		@Override
		public void onCompleted(GraphUser user, Response response)
		{
			dismissProgressDialog();
			showToast(user);
		}
	}

	private class RequestGraphUserListCallback implements Request.GraphUserListCallback
	{
		@Override
		public void onCompleted(List<GraphUser> users, Response response)
		{
			dismissProgressDialog();
			processUserListReponse(users);
		}
	}

	private class RequestUserPicture implements ImageRequest.Callback
	{
		@Override
		public void onCompleted(ImageResponse response)
		{
			dismissProgressDialog();
			processUserPictureResponse(response);
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

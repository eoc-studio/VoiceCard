package eoc.studio.voicecard.facebook;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
import com.facebook.model.GraphUser;

import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.R.id;
import eoc.studio.voicecard.R.layout;

public class TestFacebookActivity extends BaseActivity
{
	private static final String TAG = "MainActivity";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private FacebookManager facebookManager;
	private Button getUserProfile, getFriendList, getUserPicture;
	private ImageView showPicture;

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
		getUserProfile = (Button) findViewById(R.id.getUserProfile);
		getFriendList = (Button) findViewById(R.id.getFriendList);
		getUserPicture = (Button) findViewById(R.id.getPicture);
		showPicture = (ImageView) findViewById(R.id.showPicture);
		getUserProfile.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (facebookManager != null)
				{
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
					facebookManager.getUserImg(true, new RequestUserPicture(), "1442881101");
					// facebookManager.shareImage();
				}
			}
		});
	}

	private void processResponse(ImageResponse response)
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
				// OnErrorListener listener = onErrorListener;
				// if (listener != null) {
				// listener.onError(new FacebookException(
				// "Error in downloading profile picture for profileId: " +
				// getProfileId(), error));
				// } else {
				// Logger.log(LoggingBehavior.REQUESTS, Log.ERROR, TAG,
				// error.toString());
				// }
			}
			else if (responseImage != null)
			{
				showPicture.setImageBitmap(responseImage);
				// if (response.isCachedRedirect()) {
				// sendImageRequest(false);
				// }
			}
		}
	}

	public class SessionStatusCallback implements Session.StatusCallback
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
			if (user != null)
			{
				Log.d(TAG, "user is " + user + " user id is " + user.getId() + " user name is "
						+ user.getName() + " user birthday is " + user.getBirthday());
			}
		}
	}

	private class RequestGraphUserListCallback implements Request.GraphUserListCallback
	{
		@Override
		public void onCompleted(List<GraphUser> users, Response response)
		{
			if (users != null)
			{
				Log.d(TAG, "user list size is " + users.size());
				for (int i = 0; i < users.size(); i++)
				{
					Log.d(TAG, "" + users.get(i));
				}
			}
		}
	}

	private class RequestUserPicture implements ImageRequest.Callback
	{
		@Override
		public void onCompleted(ImageResponse response)
		{
			processResponse(response);
		}
	}
}

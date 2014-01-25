package eoc.studio.voicecard.facebook;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;

import eoc.studio.voicecard.R;

public class FacebookManager
{
	private static final String TAG = "FacebookManager";
	private static final String[] PERMISSION = { "user_photos,publish_checkins,publish_actions,publish_stream" };
	private Context context;
	private ImageRequest lastRequest;
	private int queryHeight = 100;
	private int queryWidth = 100;

	public FacebookManager(Context context, Session.StatusCallback statusCallback,
			Bundle savedInstanceState)
	{
		this.context = context;
		initSession(statusCallback, savedInstanceState);
	}

	private void initSession(Session.StatusCallback statusCallback, Bundle savedInstanceState)
	{
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
		Log.d(TAG, "session is " + session);
		if (session == null)
		{
			if (savedInstanceState != null)
			{
				session = Session.restoreSession(context, null, statusCallback, savedInstanceState);
			}
			else
			{
				session = new Session(context);
			}
			Session.setActiveSession(session);
			Log.d(TAG, "session state is " + session.getState());
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)
					|| session.getState().equals(SessionState.CREATED))
			{
				session.openForRead(new Session.OpenRequest((Activity) context).setCallback(
						statusCallback).setPermissions(
						Arrays.asList("friends_birthday", "user_location", "user_birthday",
								"user_likes", "friends_photos")));
				// "publish_actions")));
				// session.requestNewPublishPermissions(new
				// Session.NewPermissionsRequest(
				// (Activity) mContext, PERMISSION));
			}
			Log.d(TAG, "session state is " + session.getPermissions());
		}
		else
		{}
	}

	public void printHashKey(String appPackage)
	{
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(appPackage,
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

	public void getUserProfile(Request.GraphUserCallback callback)
	{
		Session session = Session.getActiveSession();
		Log.d(TAG, "access token is " + session.getAccessToken());
		if (session.isOpened())
		{
			Request meRequest = Request.newMeRequest(session, callback);
			Bundle requestParams = meRequest.getParameters();
			requestParams.putString("fields", "name, birthday, picture");
			meRequest.setParameters(requestParams);
			meRequest.executeAsync();
		}
		else
		{
			Log.d(TAG, "session is closed");
		}
	}

	public void getFriendList(Request.GraphUserListCallback callback)
	{
		Session session = Session.getActiveSession();
		if (session.isOpened())
		{
			Request myFriendsRequest = Request.newMyFriendsRequest(session, callback);
			Bundle requestParams = myFriendsRequest.getParameters();
			requestParams.putString("fields", "name, birthday, picture");
			myFriendsRequest.setParameters(requestParams);
			myFriendsRequest.executeAsync();
		}
		else
		{
			Log.d(TAG, "session is closed");
		}
	}

	public void shareImage()
	{
		Session session = Session.getActiveSession();
		if (session.isOpened())
		{
			// session.requestNewPublishPermissions(new
			// Session.NewPermissionsRequest(
			// (Activity) context, PERMISSION));
			Bundle params = new Bundle();
			params.putString("name", "Test Again~~~~~");
			// params.putString("caption", "");
			// params.putString("description", "Test");
			params.putString("link", "http://test.test");
			// params.putByteArray("source", getByteArray(null));
			// params.putByteArray("picture", getByteArray(null));
			// params.putByteArray("image", getByteArray(null));
			params.putString("picture", "file://mnt/sdcard/dog.png");
			// Request shareRequest = Request.newPostRequest(session, "me/feed",
			// null, new Request.Callback() {
			// @Override
			// public void onCompleted(Response response) {
			// Log.d(TAG, "" response);
			// }
			// });
			// Request shareRequest =
			// Request.newUploadPhotoRequest(Session.getActiveSession(),
			// BitmapFactory.decodeResource(context.getResources(),
			// R.drawable.dog), new Request.Callback()
			// {
			// @Override
			// public void onCompleted(Response response)
			// {
			// Log.d(TAG, response.toString());
			// }
			// });
			// Request shareRequest =
			// Request.newRestRequest(Session.getActiveSession(),
			// "1845302303" "/feed", params, HttpMethod.POST);
			// shareRequest.setCallback(new Request.Callback(){
			// @Override
			// public void onCompleted(Response response){
			// Log.d(TAG, response.toString());
			// }
			// });
			Request shareRequest = new Request(session, "me/feed", params, HttpMethod.POST,
					new Request.Callback()
					{
						@Override
						public void onCompleted(Response response)
						{
							Log.d(TAG, "response === " + response);
						}
					});
			shareRequest.setParameters(params);
			Request.executeBatchAsync(shareRequest);
		}
		else
		{}
	}

	private byte[] getByteArray(Bitmap sharePhoto)
	{
		byte[] data = null;
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.dog);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		icon.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		data = baos.toByteArray();
		return data;
	}

	public void getUserImg(boolean allowCachedResponse, ImageRequest.Callback callback,
			String profileId)
	{
		try
		{
			ImageRequest.Builder requestBuilder = new ImageRequest.Builder(context,
					ImageRequest.getProfilePictureUrl(profileId, queryWidth, queryHeight));
			ImageRequest request = requestBuilder.setAllowCachedRedirects(allowCachedResponse)
					.setCallerTag(this).setCallback(callback).build();
			if (lastRequest != null)
			{
				ImageDownloader.cancelRequest(lastRequest);
			}
			lastRequest = request;
			ImageDownloader.downloadAsync(request);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
			Log.d(TAG, "URISyntaxException is error");
		}
	}

	public ImageRequest getLastRequest()
	{
		return lastRequest;
	}

	public void setLastRequest(ImageRequest request)
	{
		lastRequest = request;
	}
}

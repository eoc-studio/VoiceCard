package eoc.studio.voicecard.facebook;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import eoc.studio.voicecard.R;

public class FacebookManager
{
	private static final String TAG = "FacebookManager";
	private static final String[] PERMISSION = {
			"user_photos, publish_checkins, publish_actions, publish_stream, user_mobile_phone, user_address, user_about_me",
			"email" };
	private Context context;
	private ImageRequest lastRequest;
	private int queryHeight = 100;
	private int queryWidth = 100;

	static class ShowField
	{
		static final String USER_ID = "id: ";
		static final String USER_NAME = "name: ";
		static final String USER_BIRTHDAY = "birthday: ";
		static final String USER_ICON = "icon: ";
	}

	static class JSONTag
	{
		static final String PICTURE = "picture";	// For get user icon
		static final String DATA = "data";
		static final String URL = "url";
	}

	public FacebookManager(Context context, Session.StatusCallback statusCallback,
			Bundle savedInstanceState)
	{
		this.context = context;
		printHashKey("eoc.studio.voicecard");
		initSession(statusCallback, savedInstanceState);
	}

	private void initSession(Session.StatusCallback statusCallback, Bundle savedInstanceState)
	{
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		// Settings.getLoggingBehaviors()
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
			Log.d(TAG, "session permission is " + session.getPermissions());
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
				Log.d(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
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
			session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					(Activity) context, PERMISSION));
			Request meRequest = Request.newMeRequest(session, callback);
			Bundle requestParams = meRequest.getParameters();			// if not set field, will get all info(no phone number)
			requestParams.putString("fields", "name, birthday, picture, email");
			meRequest.setParameters(requestParams);
			meRequest.executeAsync();
		}
		else
		{
			Log.d(TAG, "session is closed");
			((TestFacebookActivity) context).dismissProgressDialog();
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
			((TestFacebookActivity) context).dismissProgressDialog();
		}
	}

	public void shareImage()
	{
		Session session = Session.getActiveSession();
		if (session.isOpened())
		{
			session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					(Activity) context, PERMISSION));
			Bundle params = new Bundle();
			params.putString("name", "Test Again~~~~~");
//			params.putString("caption", "");
//			params.putString("description", "Test");
//			params.putString("link", "http://test.test");
			params.putByteArray("source", getByteArray(null));
			params.putString("picture", "http://www.some-link.com/pic.png");
			params.putByteArray("image", getByteArray(null));
			params.putString("to", "100000133232978");
//			Request shareRequest = Request.newPostRequest(session, "me/feed", null,
//					new Request.Callback()
//					{
//						@Override
//						public void onCompleted(Response response)
//						{
//							Log.d(TAG, "" + response);
//						}
//					});
//			Request shareRequest = Request.newUploadPhotoRequest(
//					Session.getActiveSession(), // No need params, only post to me
//					BitmapFactory.decodeResource(context.getResources(), R.drawable.dog),
//					new Request.Callback()
//					{
//						@Override
//						public void onCompleted(Response response)
//						{
//							Log.d(TAG, response.toString());
//						}
//					});
//			shareRequest.setParameters(params);
//			Request.executeBatchAsync(shareRequest);
			try
			{
				WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context,
						Session.getActiveSession(), params)).setOnCompleteListener(
						new OnCompleteListener()
						{
							@Override
							public void onComplete(Bundle values, FacebookException error)
							{
								Log.d(TAG, "values are " + values);
								Log.d(TAG, "error is " + error);
							}
						}).build();
				feedDialog.show();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
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
	
	public void inviteFriend(String to, String message)
	{
		//100000133232978, 1118054263
		Bundle params = new Bundle();
		// params.putString("link","https://play.google.com/store/apps/details?id=com.facebook.android.friendsmash");
		if (message != null) {
			params.putString("message", message);
		} else {
			params.putString("message", "I want invite you! ");
		}
		params.putString("to", to);
		openInviteDialog(context, params);
	}
	
	private void openInviteDialog(Context context, Bundle params)
	{
		final WebDialog requestsDialog = new WebDialog.RequestsDialogBuilder(context,
				Session.getActiveSession(), params).setOnCompleteListener(
				new WebDialog.OnCompleteListener()
				{
					@Override
					public void onComplete(Bundle values, FacebookException error)
					{
						Log.d(TAG, "values is " + values);
					}
				}).build();
		Window dialogWindow = requestsDialog.getWindow();
		dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestsDialog.show();
	}
}

package eoc.studio.voicecard.facebook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.ListUtility;

public class FacebookManager
{
	private static final String TAG = "FacebookManager";
	private static final String USER_CANCELED_LOGIN = "User canceled login";
	private Context context;
	private Session.StatusCallback statusCallback = null;
//	private boolean isCanceledLogin = false;
	private ProgressDialog progressDialog;
	

	static class ShowField
	{
		static final String USER_ID = "id: ";
		static final String USER_NAME = "name: ";
		static final String USER_BIRTHDAY = "birthday: ";
		static final String USER_ICON = "icon: ";
	}

	static class JSONTag
	{
	    static final String DATA = "data";
		static final String PICTURE = "picture";	// For get user icon
		static final String URL = "url";
	}
	
	static class BundleTag
	{
	    static final String CAPTION = "caption";
	    static final String DESCRIPTION = "description";
	    static final String FIELDS = "fields";
	    static final String IMAGE = "image";
	    static final String LINK = "link";
	    static final String MESSAGE = "message";
	    static final String NAME = "name";
	    static final String PICTURE = "picture";
	    static final String SOURCE = "source";  
	    static final String TO = "to";  
	}
	
	public static class BundleParams
	{
		public static final String ID = "id";
		public static final String BIRTHDAY = "birthday";
		public static final String EMAIL = "email";
		public static final String NAME = "name";
		public static final String PICTURE = "picture";
		public static final String EDUCATION = "education";
		public static final String WORK = "work";
		public static final String GENDER = "gender";
		public static final String LINK = "link";
		public static final String HOMETOWN = "hometown";
		public static final String TIMEZONE = "timezone";
		public static final String LOCALE = "locale";
	    
	    // cannot get
		public static final String MOBILE = "mobile";
		public static final String TITLE = "title";
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {        
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            Log.d(TAG, "session is " + session);
            Log.d(TAG, "state is " + state);
            if (exception != null) {
                Log.d(TAG, "exception is " + exception.getMessage());
                if (exception.getMessage().equals(USER_CANCELED_LOGIN)) {
//                    isCanceledLogin = true;
                }
            } else {
                Log.d(TAG, "exception is null");
                getUserProfile(new RequestGraphUserCallback());
            }
        }
    }
    
    private class RequestGraphUserCallback implements Request.GraphUserCallback
    {
        @Override
        public void onCompleted(GraphUser user, Response response)
        {
            if (user != null) {
                JSONObject userJSON = user.getInnerJSONObject();
                if(userJSON != null) {
                    try {
                        Log.d(TAG, "id is " + userJSON.getString(BundleParams.ID));
                        Log.d(TAG, "email is " + userJSON.getString(BundleParams.EMAIL));
                        Log.d(TAG, "name is " + userJSON.getString(BundleParams.NAME));
                        Log.d(TAG, "gender is " + userJSON.getString(BundleParams.GENDER));
                        Log.d(TAG, "birthday is " + userJSON.getString(BundleParams.BIRTHDAY));
                        Log.d(TAG, "link is " + userJSON.getString(BundleParams.LINK));
                        Log.d(TAG, "timezone is " + userJSON.getInt(BundleParams.TIMEZONE));
                        Log.d(TAG, "locale is " + userJSON.getString(BundleParams.LOCALE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    try {
                        Log.d(TAG, "img is "
                                + userJSON.getJSONObject(BundleParams.PICTURE).getJSONObject("data").getString("url"));
                        
                        Log.d(TAG, "hometown is "
                                + userJSON.getJSONObject(BundleParams.HOMETOWN).getString(BundleParams.NAME));

                        Log.d(TAG, "work is "
                                + userJSON.getJSONArray(BundleParams.WORK).getJSONObject(0).getJSONObject("employer")
                                        .getString(BundleParams.NAME));
                        
                        Log.d(TAG, "education is "
                                + userJSON.getJSONArray(BundleParams.EDUCATION).getJSONObject(0).getJSONObject("school")
                                        .getString(BundleParams.NAME));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public Handler dialogHandler = new Handler() {
        @Override  
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ListUtility.SHOW_WAITING_DIALOG:
                Log.d(TAG, "show waiting dialog ");
                progressDialog = ProgressDialog.show(context, "", context.getResources()
                        .getString(R.string.file_process_loading));
                break;
            case ListUtility.DISMISS_WAITING_DIALOG:
                Log.d(TAG, "dismiss dialog ");
                if (progressDialog != null)
                    progressDialog.dismiss();
                break;
            }
        }
    };

	public FacebookManager(Context context, Bundle savedInstanceState)
	{
		this.context = context;
		statusCallback = new SessionStatusCallback();
		printHashKey("eoc.studio.voicecard");
		initSession(savedInstanceState);
	}
	    
    public Session.StatusCallback getSessionStatusCallBack() {
        return statusCallback;
    }

	private void initSession(Bundle savedInstanceState)
	{
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		Log.d(TAG, "session is " + session);
		Log.d(TAG, "savedInstanceState is " + savedInstanceState);
		if (session == null)
		{
			if (savedInstanceState != null)
			{
				session = Session.restoreSession(context, null, statusCallback, savedInstanceState);
			}
			else
			{
				session = new Session(context);
				
				Log.d(TAG, "session state is XXXXXXXXXXXXXXXXXXXXXXXX " + session.getState());
			}
			openSession(session);
		}
		else
		{
            SessionState sessionState = session.getState();
            Log.d(TAG, "The session is not null, but session state is " + sessionState);
//            Log.d(TAG, "isCanceledLogin is " + isCanceledLogin);
            if (session.getState().equals(SessionState.CLOSED_LOGIN_FAILED)
                    || session.getState().equals(SessionState.CLOSED)) {
//                if (isCanceledLogin) {
//                    session.closeAndClearTokenInformation();
//                    isCanceledLogin = false;
//                }
                session = new Session(context);
                openSession(session);
            }
		}
		Log.d(TAG, "session permission is " + session.getPermissions());
		session.addCallback(statusCallback);
	}
	
	private void openSession(Session session) {
        session.openForRead(new Session.OpenRequest((Activity) context).setCallback(statusCallback)
                .setPermissions(
                        Arrays.asList(Permissions.READ_PERMISSION)));
        Session.setActiveSession(session);
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
		Log.d(TAG, "session permission is " + session.getPermissions());
		if (session.isOpened())
		{
			Request meRequest = Request.newMeRequest(session, callback);
			Bundle requestParams = meRequest.getParameters();			// if not set field, will get all info(no phone number)
            StringBuilder queryString = new StringBuilder().append(BundleParams.NAME).append(", ")
                    .append(BundleParams.BIRTHDAY).append(", ").append(BundleParams.PICTURE).append(", ")
                    .append(BundleParams.EMAIL).append(", ").append(BundleParams.EDUCATION).append(", ")
                    .append(BundleParams.WORK).append(", ").append(BundleParams.GENDER).append(", ")
                    .append(BundleParams.LINK).append(", ").append(BundleParams.HOMETOWN).append(", ")
                    .append(BundleParams.TIMEZONE).append(", ").append(BundleParams.LOCALE);
            requestParams.putString(BundleTag.FIELDS, queryString.toString());
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
	    Log.d(TAG, "getFriendList");
	    dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
		Session session = Session.getActiveSession();
		if (session.isOpened())
		{
			Request myFriendsRequest = Request.newMyFriendsRequest(session, callback);
			Bundle requestParams = myFriendsRequest.getParameters();
            StringBuilder queryString = new StringBuilder().append(BundleParams.NAME).append(", ")
                    .append(BundleParams.BIRTHDAY).append(", ").append(BundleParams.PICTURE);
			requestParams.putString(BundleTag.FIELDS, queryString.toString());
			myFriendsRequest.setParameters(requestParams);
			myFriendsRequest.executeAsync();
		}
		else
		{
			Log.d(TAG, "session is closed");
			dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
		}
	}
	
	public void publishTimeline(String id, String name, String pictureUrl, String caption, String description, String link) {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            if(!session.getPermissions().contains(Permissions.PUBLISH_STREAM)) {
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                        (Activity) context, Permissions.PUBLISH_PERMISSION));
//                return;
            }
            Bundle params = new Bundle();
            params.putString(BundleTag.NAME, name);
            params.putString(BundleTag.PICTURE, pictureUrl);
            params.putString(BundleTag.TO, id);
            params.putString(BundleTag.CAPTION, caption);
            params.putString(BundleTag.DESCRIPTION, description);
            params.putString(BundleTag.LINK, link);
            
            try {
                WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context, Session.getActiveSession(), params))
                        .setOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(Bundle values, FacebookException error) {
                                Log.d(TAG, "values are " + values);
                                Log.d(TAG, "error is " + error);
                            }
                        }).build();
                feedDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	public void inviteFriend(String to, String message)
	{
		//100000133232978, 1118054263
		Bundle params = new Bundle();
		// params.putString(BundleTag.LINK,"https://play.google.com/store/apps/details?id=com.facebook.android.friendsmash");
		if (message != null) {
			params.putString(BundleTag.MESSAGE, message);
		} else {
			params.putString(BundleTag.MESSAGE, context.getResources().getString(R.string.invite_message));
		}
		params.putString(BundleTag.TO, to);
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
	
	public void logout()
    {
        Session session = Session.getActiveSession();
        if (session != null && !session.isClosed()) {
            session.closeAndClearTokenInformation();
            session.removeCallback(statusCallback);
        }
	    
//        new Request(Session.getActiveSession(), "me/permissions", null, HttpMethod.DELETE, callback).executeAsync();
    }
}

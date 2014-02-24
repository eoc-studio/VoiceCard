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
	private static final String USER_CANCELED_LOG_IN = "User canceled log in";
	private Context context;
	private Session.StatusCallback statusCallback = null;
	private ProgressDialog progressDialog;
	private Publish publish;
	private Bundle inviteBundle;
	private int managerState;
	
	public static class BundleTag
	{
		public static final String CAPTION = "caption";
	    public static final String DESCRIPTION = "description";
	    public static final String FIELDS = "fields";
	    public static final String IMAGE = "image";
	    public static final String LINK = "link";
	    public static final String MESSAGE = "message";
	    public static final String NAME = "name";
	    public static final String PICTURE = "picture";
	    public static final String SOURCE = "source";  
	    public static final String TO = "to"; 
	}
	
	static class ManagerState
	{
	    public static final int NORMAL = -1;
	    public static final int LOGIN = 0;
	    public static final int GET_FRIEND = 1;
	    public static final int INVITE = 2;
	    public static final int PUBLISH = 3;
	}
		
	private class SessionStatusCallback implements Session.StatusCallback {        
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            handleManagerState(session, state, exception);
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
                        Log.d(TAG, "id is " + userJSON.getString(JSONTag.ID));
                        Log.d(TAG, "email is " + userJSON.getString(JSONTag.EMAIL));
                        Log.d(TAG, "name is " + userJSON.getString(JSONTag.NAME));
                        Log.d(TAG, "gender is " + userJSON.getString(JSONTag.GENDER));
                        Log.d(TAG, "birthday is " + userJSON.getString(JSONTag.BIRTHDAY));
                        Log.d(TAG, "link is " + userJSON.getString(JSONTag.LINK));
                        Log.d(TAG, "timezone is " + userJSON.getInt(JSONTag.TIMEZONE));
                        Log.d(TAG, "locale is " + userJSON.getString(JSONTag.LOCALE));
                        Log.d(TAG, "img is "
                                + userJSON.getJSONObject(JSONTag.PICTURE).getJSONObject(JSONTag.DATA).getString(JSONTag.URL));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    try {
                        Log.d(TAG, "hometown is "
                                + userJSON.getJSONObject(JSONTag.HOMETOWN).getString(JSONTag.NAME));

                        Log.d(TAG, "work is "
                                + userJSON.getJSONArray(JSONTag.WORK).getJSONObject(0).getJSONObject(JSONTag.EMPLOYER)
                                        .getString(JSONTag.NAME));
                        
                        Log.d(TAG, "education is "
                                + userJSON.getJSONArray(JSONTag.EDUCATION).getJSONObject(0).getJSONObject(JSONTag.SCHOOL)
                                        .getString(JSONTag.NAME));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public Handler dialogHandler = new Handler()
    {
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
		managerState = ManagerState.LOGIN;
		printHashKey("eoc.studio.voicecard");
		initSession(savedInstanceState);
	}
	
	public void handleManagerState(Session session, SessionState state, Exception exception) {
        Log.d(TAG, "session is " + session);
        Log.d(TAG, "state is " + state);
        Log.d(TAG, "exception is " + exception);
        Log.d(TAG, "managerState is " + managerState);
        
	    switch (managerState) {
	    case ManagerState.LOGIN:
            if (exception == null) 
            {
                getUserProfile(new RequestGraphUserCallback());
            } 
            else 
            {
                if (exception.getMessage().equals(USER_CANCELED_LOGIN)) 
                {
                    ((TestFacebookActivity) context).finish();
                }
            }
	        break;
	    case ManagerState.GET_FRIEND:
	        break;
	    case ManagerState.INVITE:
	        if (state.equals(SessionState.OPENED)) {
	            openInviteDialog(context, inviteBundle);
	        }
	        break;
	    case ManagerState.PUBLISH:
	        if (exception != null) // user cancel publish permission
	        {
	           // the state is OPENED
	        }
	        else if (state.equals(SessionState.OPENED_TOKEN_UPDATED) || state.equals(SessionState.OPENED)) 
                // user accept publish permission then show dialog
            {
                publishTimeline();
            }
	        break;
	    }
	}
	    
    public Session.StatusCallback getSessionStatusCallBack() {
        return statusCallback;
    }

	private void initSession(Bundle savedInstanceState)
	{
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
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
			Log.d(TAG, "session state is " + session.getState());
			openSession(session);
		}
		else
		{
            SessionState sessionState = session.getState();
            Log.d(TAG, "The session is not null, but session state is " + sessionState);
            if (session.getState().equals(SessionState.CLOSED_LOGIN_FAILED)
                    || session.getState().equals(SessionState.CLOSED)) {
                session = new Session(context);
                openSession(session);
            }
		}
		Log.d(TAG, "session permission is " + session.getPermissions());
		session.addCallback(statusCallback);
	}
	
	private void openSession(Session session)
	{
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
            StringBuilder queryString = new StringBuilder().append(JSONTag.NAME).append(", ")
                    .append(JSONTag.BIRTHDAY).append(", ").append(JSONTag.PICTURE).append(", ")
                    .append(JSONTag.EMAIL).append(", ").append(JSONTag.EDUCATION).append(", ")
                    .append(JSONTag.WORK).append(", ").append(JSONTag.GENDER).append(", ")
                    .append(JSONTag.LINK).append(", ").append(JSONTag.HOMETOWN).append(", ")
                    .append(JSONTag.TIMEZONE).append(", ").append(JSONTag.LOCALE);
            requestParams.putString(BundleTag.FIELDS, queryString.toString());
			meRequest.setParameters(requestParams);
			meRequest.executeAsync();
//			RequestBatch rb = new RequestBatch();
//			rb.add(meRequest);
//			rb.addCallback(new RequestBatch.Callback() {
//
//                @Override
//                public void onBatchCompleted(RequestBatch batch) {
//                       
//                }
//			});
//			rb.setTimeout(300);
//			rb.executeAsync();
		}
		else
		{
			Log.d(TAG, "getUserProfile session is closed");
		}
	}

	public void getFriendList(Request.GraphUserListCallback callback)
	{
	    Log.d(TAG, "getFriendList");
	    managerState = ManagerState.GET_FRIEND;
	    dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
		Session session = Session.getActiveSession();
		if (session.isOpened())
		{
			Request myFriendsRequest = Request.newMyFriendsRequest(session, callback);
			Bundle requestParams = myFriendsRequest.getParameters();
            StringBuilder queryString = new StringBuilder().append(JSONTag.NAME).append(", ")
                    .append(JSONTag.BIRTHDAY).append(", ").append(JSONTag.PICTURE);
			requestParams.putString(BundleTag.FIELDS, queryString.toString());
			myFriendsRequest.setParameters(requestParams);
			myFriendsRequest.executeAsync();
		}
		else
		{
			Log.d(TAG, "getFriendList session is closed");
			dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
		}
	}
	
	public void getPublishedPermission(Publish publish)
	{
	    managerState = ManagerState.PUBLISH;
	    this.publish = publish;
	    Session session = Session.getActiveSession();
        if (session.isOpened()) {
            if(!session.getPermissions().contains(Permissions.PUBLISH_STREAM)) {
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
                        (Activity) context, Permissions.PUBLISH_PERMISSION));
            } else {
                publishTimeline();
            }
        } else {
            initSession(null);
        }
	}
		
	public void publishTimeline()
	{
        Bundle params = new Bundle();
        params.putString(BundleTag.NAME, publish.getName());
        params.putString(BundleTag.PICTURE, publish.getImgLink());
        params.putString(BundleTag.TO, publish.getId());
        params.putString(BundleTag.CAPTION, publish.getCaption());
        params.putString(BundleTag.DESCRIPTION, publish.getDescription());
        params.putString(BundleTag.LINK, publish.getLink());
        
        try {
            WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context, Session.getActiveSession(), params))
                    .setOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Bundle values, FacebookException error) {
                            Log.d(TAG, "publish values are " + values);
                            Log.d(TAG, "publish error is " + error);
                        }
                    }).build();
            feedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void inviteFriend(String to, String message)
	{
	    managerState = ManagerState.INVITE;
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
		inviteBundle = params;
	}
	
	private void openInviteDialog(Context context, Bundle params)
	{
        Session session = Session.getActiveSession();
        if (session.isOpened())
        {
            final WebDialog requestsDialog = new WebDialog.RequestsDialogBuilder(context, Session.getActiveSession(),
                    params).setOnCompleteListener(new WebDialog.OnCompleteListener()
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
        } else {
            initSession(null);
        }
	}
	
	public void logout()
    {
        Session session = Session.getActiveSession();
        if (session != null && !session.isClosed())
        {
            session.closeAndClearTokenInformation();
            session.removeCallback(statusCallback);
        }
    }
}

package eoc.studio.voicecard.facebook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.facebook.enetities.Photo;
import eoc.studio.voicecard.facebook.enetities.Publish;
import eoc.studio.voicecard.facebook.enetities.UserInfo;
import eoc.studio.voicecard.facebook.utils.BundleTag;
import eoc.studio.voicecard.facebook.utils.FacebookListener;
import eoc.studio.voicecard.facebook.utils.JSONTag;
import eoc.studio.voicecard.facebook.utils.Permissions;
import eoc.studio.voicecard.mailbox.MailsAdapterData;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.manager.PostMailListener;
import eoc.studio.voicecard.manager.UploadDiyListener;
import eoc.studio.voicecard.utils.ListUtility;

public class FacebookManager
{
	private static final String TAG = "FacebookManager";
	private static final int TIME_OUT_INTERVAL = 300000;
	private Context context;
	private Session.StatusCallback statusCallback = null;
	private ProgressDialog progressDialog;
	private Publish publish;
	private Bundle inviteBundle = null;
	private Photo photo;
	private int managerState;
	private int actionType;
	private FacebookListener facebookListener;
	private Request.GraphUserListCallback friendListCallback;
	private RequestGraphUserCallback userCallback;
	private RequestAsyncTask requestAsyncTask;
	private Card publishCard;
	private Uri fileUri;
	
	private volatile static FacebookManager facebookManager;
	
	static class ManagerState
	{
	    public static final int NORMAL = -1;
	    public static final int LOGIN = 0;
	    public static final int GET_USER_PROFILE = 1;
	    public static final int GET_FRIEND = 2;
	    public static final int INVITE = 3;
	    public static final int REQUEST_PUBLISH_PERMISSION = 4;
	    public static final int PUBLISH = 5;
	    public static final int UPLOAD = 6;
	    public static final int LOGOUT = 7;
	    public static final int PUBLISH_USER_FEED = 8;
	    public static final int PUBLISH_NEWS = 9;
	}
			
	private class SessionStatusCallback implements Session.StatusCallback {        
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            handleManagerState(session, state, exception);
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

	private FacebookManager(Context context)
	{
		this.context = context;
		statusCallback = new SessionStatusCallback();
		printHashKey("eoc.studio.voicecard");
	}
	
	public static FacebookManager getInstance(Context context)
	{
	    if (facebookManager == null)
	    {
	        synchronized(FacebookManager.class)
	        {
	            if (facebookManager == null)
	            {
	                facebookManager = new FacebookManager(context);
	            }
	        }
	    }
	    return facebookManager;
	}
	
	public void handleManagerState(Session session, SessionState state, Exception exception) {
        Log.d(TAG, "session is " + session);
        Log.d(TAG, "state is " + state);
        Log.d(TAG, "exception is " + exception);
        Log.d(TAG, "managerState is " + managerState);
        
	    switch (managerState) {
	    case ManagerState.LOGIN:
	    case ManagerState.REQUEST_PUBLISH_PERMISSION:
            if (exception == null) 
            {
                if (isLogin())
                    facebookListener.onSuccess();
            }
            else 
            {
                facebookListener.onError(exception.getMessage());
            }
	        break;
	    case ManagerState.GET_USER_PROFILE:
	    case ManagerState.GET_FRIEND:
	    case ManagerState.INVITE:
	    case ManagerState.PUBLISH:
	    case ManagerState.UPLOAD:
	    case ManagerState.PUBLISH_USER_FEED:
	        break;
	    }
	}
	    
    public Session.StatusCallback getSessionStatusCallBack() {
        return statusCallback;
    }

	public void login(Context context, FacebookListener facebookListener)
	{
	    this.context = context;
	    this.facebookListener = facebookListener;
	    managerState = ManagerState.LOGIN;
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null)
		{
			session = new Session(context);
			Log.d(TAG, "session state is " + session.getState());
			requestReadPermission(session);
		}
		else
		{
            SessionState sessionState = session.getState();
            Log.d(TAG, "The session is not null, but session state is " + sessionState);
            if (session.getState().equals(SessionState.CLOSED_LOGIN_FAILED)
                    || session.getState().equals(SessionState.CLOSED)) {
                session = new Session(context);
                requestReadPermission(session);
            }
		}
		Log.d(TAG, "session permission is " + session.getPermissions());
		session.addCallback(statusCallback);
	}
	
	public boolean isLogin()
    {
        Session session = Session.getActiveSession();
        if (session == null)
        {
            return false;
        } 
        else
        {
            if (session.isOpened())
            {
                return true;
            } 
            else 
            {
                return false;
            }
        }
    }
	
	private void checkLoginfromPublish() {
	    Log.d(TAG, "checkLoginfromPublish ");
        if (isLogin())
        {
            checkPublishPermission();
        } 
        else 
        {
            login(context, new LoginListener());
        }
	}
	
	public void requestReadPermission(Session session)
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

	public void getUserProfile(Context context, RequestGraphUserCallback callback)
	{
	    Log.d(TAG, "getUserProfile");
	    actionType = ManagerState.GET_USER_PROFILE;
	    userCallback = callback;
	    this.context = context;
	    
        if (isLogin())
        {
            sendUserInfoRequest();
        } 
        else 
        {
            login(context, new LoginListener());
        }
	}
	
	private void sendUserInfoRequest() {
	    Log.d(TAG, "sendUserInfoRequest");
//	    dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
        Session session = Session.getActiveSession();
        Log.d(TAG, "access token is " + session.getAccessToken());
        if (session.isOpened()) {
            RequestBatch rb = new RequestBatch();
            Request meRequest = Request.newMeRequest(session, userCallback);
            Bundle requestParams = meRequest.getParameters(); // if not set field, will get all info(no phone number)
            StringBuilder queryString = new StringBuilder().append(JSONTag.NAME).append(", ").append(JSONTag.BIRTHDAY)
                    .append(", ").append(JSONTag.PICTURE).append(", ").append(JSONTag.EMAIL).append(", ")
                    .append(JSONTag.EDUCATION).append(", ").append(JSONTag.WORK).append(", ").append(JSONTag.GENDER)
                    .append(", ").append(JSONTag.LINK).append(", ").append(JSONTag.HOMETOWN).append(", ")
                    .append(JSONTag.TIMEZONE).append(", ").append(JSONTag.LOCALE);
            requestParams.putString(BundleTag.FIELDS, queryString.toString());
            meRequest.setParameters(requestParams);
            rb.add(meRequest);
            rb.setTimeout(TIME_OUT_INTERVAL);
            requestAsyncTask = rb.executeAsync();
        } else {
            Log.d(TAG, "getUserProfile session is closed");
//            dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
        }
	}

	public void getFriendList(Context context, Request.GraphUserListCallback callback)
	{
	    Log.d(TAG, "getFriendList");
	    this.context = context;
	    managerState = ManagerState.GET_FRIEND;
	    actionType = ManagerState.GET_FRIEND;
	    friendListCallback = callback;
		
        if (isLogin())
        {
            sendFriendListRequest();
        } 
        else 
        {
            login(context, new LoginListener());
        }
	}
	
	private void sendFriendListRequest() {
	    Log.d(TAG, "sendFriendListRequest");
	    dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
	    Session session = Session.getActiveSession();
	    
	    if (session.isOpened())
        {
	        RequestBatch rb = new RequestBatch();
            Request myFriendsRequest = Request.newMyFriendsRequest(session, friendListCallback);
            Bundle requestParams = myFriendsRequest.getParameters();
            StringBuilder queryString = new StringBuilder().append(JSONTag.NAME).append(", ")
                    .append(JSONTag.BIRTHDAY).append(", ").append(JSONTag.PICTURE);
            requestParams.putString(BundleTag.FIELDS, queryString.toString());
            myFriendsRequest.setParameters(requestParams);
            
            rb.add(myFriendsRequest);
            rb.setTimeout(TIME_OUT_INTERVAL);
            requestAsyncTask = rb.executeAsync();
        }
        else
        {
            Log.d(TAG, "getFriendList session is closed");
            dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
        }
	}
	
	public boolean hasPublishPermission()
	{
	    Session session = Session.getActiveSession();
	    if (!session.getPermissions().contains(Permissions.PUBLISH_STREAM))
	    {
            return false;
	    } else {
	        return true;
	    }
	}
	
	private void getPublishPermission(Context context, FacebookListener facebookListener) {
	    Log.d(TAG, "getPublishPermission ");
        this.context = context;
        this.facebookListener = facebookListener;
	    managerState = ManagerState.REQUEST_PUBLISH_PERMISSION;
	    Session session = Session.getActiveSession();
        session.requestNewPublishPermissions(new Session.NewPermissionsRequest((Activity) context,
                Permissions.PUBLISH_PERMISSION));
	}
	
    private void checkPublishPermission() {
        Log.d(TAG, "checkPublishPermission ");
        if (hasPublishPermission())
        {
            switch (actionType) {
            case ManagerState.PUBLISH:
                openPublishDialog();
                break;
            case ManagerState.INVITE:
                openInviteDialog();
                break;
            case ManagerState.PUBLISH_USER_FEED:
                publishUserFeedImpl();
                break;
            }
        } else {
            getPublishPermission(context, new RequestPublishPermissionListener());
        }
    }
    
    public void publishNews(Context context, String sendId, Uri fileUri) {
        this.context = context;
        this.fileUri = fileUri;
        publish = new Publish(sendId, Publish.DEFAULT_NAME, null, Publish.DEFAULT_CAPTION, Publish.DEFAULT_DESCRIPTION,
                null);
        managerState = ManagerState.PUBLISH_NEWS;
        actionType = ManagerState.PUBLISH_NEWS;
        login(context, new LoginListener());
    }
		
	public void publishTimeline(Context context, Publish publish)
	{
        this.context = context;
        
        if (publish != null) {
            this.publish = publish;
        } else {
            this.publish = new Publish(Publish.DEFAULT_ID, Publish.DEFAULT_NAME, null, Publish.DEFAULT_CAPTION,
                    Publish.DEFAULT_DESCRIPTION, Publish.DEFAULT_LINK);
        }
        managerState = ManagerState.PUBLISH;
        actionType = ManagerState.PUBLISH;
        
        checkLoginfromPublish();
	}
	
	private void openPublishDialog() {
        Bundle params = new Bundle();
        params.putString(BundleTag.NAME, publish.getName());
        params.putString(BundleTag.PICTURE, publish.getImgLink());
        params.putString(BundleTag.TO, publish.getId());
        params.putString(BundleTag.CAPTION, publish.getCaption());
        params.putString(BundleTag.DESCRIPTION, publish.getDescription());
        params.putString(BundleTag.LINK, publish.getLink());
        
        try {
            WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(context, Session.getActiveSession(), params))
                    .setOnCompleteListener(new PublishListener()).build();
            feedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    public void upload(Photo photo) {
        this.photo = photo;
        managerState = ManagerState.UPLOAD;
        actionType = ManagerState.UPLOAD;

        if (isLogin())
        {
            uploadImpl();
        } 
        else 
        {
            login(context, new LoginListener());
        }
    }
    
    private void uploadImpl() {
        RequestBatch rb = new RequestBatch();
        Session session = Session.getActiveSession();
        Request request = new Request(session, "me/photos", photo.getBundle(), HttpMethod.POST, new UploadCallback());
        rb.add(request);
        rb.setTimeout(TIME_OUT_INTERVAL);
        requestAsyncTask = rb.executeAsync();
    }
    
    public void publishUserFeed(Context context, Publish publish) {
        this.context = context;
        this.publish = publish;
        managerState = ManagerState.PUBLISH_USER_FEED;
        actionType = ManagerState.PUBLISH_USER_FEED;
        
        checkLoginfromPublish();
    }
    
    private void publishUserFeedImpl() {
        RequestBatch rb = new RequestBatch();
        Bundle params = new Bundle();
        params.putString(BundleTag.NAME, publish.getName());
        if (publish.getImgLink() != null) {
            params.putString(BundleTag.PICTURE, publish.getImgLink());
        }
        params.putString(BundleTag.TO, publish.getId());
        params.putString(BundleTag.CAPTION, publish.getCaption());
        params.putString(BundleTag.DESCRIPTION, publish.getDescription());
        if (publish.getLink() != null) {
            params.putString(BundleTag.LINK, publish.getLink());
        }
        Request request = new Request(Session.getActiveSession(), "me/feed", params, HttpMethod.POST, new PublishUserFeed());
        rb.add(request);
        rb.setTimeout(TIME_OUT_INTERVAL);
        requestAsyncTask = rb.executeAsync();
    }
    
    public void inviteFriend(Context context, String message, Card publishCard) {
        Log.d(TAG, "inviteFriend invite all friend");
        managerState = ManagerState.INVITE;
        actionType = ManagerState.INVITE;
        this.context = context;
        this.publishCard = publishCard;
        Bundle params = new Bundle();
        if (message != null) {
            params.putString(BundleTag.MESSAGE, message);
        } else {
            params.putString(BundleTag.MESSAGE, context.getResources().getString(R.string.invite_message));
        }
        inviteBundle = params;
        
        if (isLogin())
        {
            openInviteDialog();
        } 
        else 
        {
            login(context, new LoginListener());
        }
    }
	
	public void inviteFriend(Context context, String to, String message)
	{
	    Log.d(TAG, "inviteFriend invite only specific friend");
	    managerState = ManagerState.INVITE;
	    actionType = ManagerState.INVITE;
	    this.context = context;
		Bundle params = new Bundle();
		if (message != null) {
			params.putString(BundleTag.MESSAGE, message);
		} else {
			params.putString(BundleTag.MESSAGE, context.getResources().getString(R.string.invite_message));
		}
		params.putString(BundleTag.TO, to);
		inviteBundle = params;
		
        if (isLogin())
        {
            openInviteDialog();
        } 
        else 
        {
            login(context, new LoginListener());
        }
	}
	
    public void inviteFriend(Context context, String[] suggestedFriends, String message) {
        Log.d(TAG, "inviteFriend invite several specific friends");
        managerState = ManagerState.INVITE;
        actionType = ManagerState.INVITE;
        this.context = context;
        Bundle params = new Bundle();

        if (message != null) {
            params.putString(BundleTag.MESSAGE, message);
        } else {
            params.putString(BundleTag.MESSAGE, context.getResources().getString(R.string.invite_message));
        }
        params.putString(BundleTag.SUGGESTIONS, TextUtils.join(",", suggestedFriends));
        inviteBundle = params;
        
        if (isLogin())
        {
            openInviteDialog();
        } 
        else 
        {
            login(context, new LoginListener());
        }
    }
	
	private void openInviteDialog()
	{
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            if (inviteBundle != null && context != null) {
                final WebDialog requestsDialog = new WebDialog.RequestsDialogBuilder(context,
                        Session.getActiveSession(), inviteBundle).setOnCompleteListener(new InviteListener()).build();
                Window dialogWindow = requestsDialog.getWindow();
                dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                requestsDialog.show();
            } else {

            }
        }
	}
	
	public void logout()
    {
	    actionType = ManagerState.LOGOUT;
        Session session = Session.getActiveSession();
        if (session != null && !session.isClosed())
        {
            session.closeAndClearTokenInformation();
            session.removeCallback(statusCallback);
        }
        
        // when logout need to clear db's img for another user to login
        MailsAdapterData mailsAdapterData = new MailsAdapterData(context); 
        mailsAdapterData.open();
    }
	
    private void showToast(String msg) {
        if (context != null) {
            Toast.makeText(context, context.getResources().getString(R.string.errorIs, msg), Toast.LENGTH_SHORT).show();
        }
    }
    
    public boolean cancelRequest() {
        if (requestAsyncTask != null) {
            return requestAsyncTask.cancel(true);
        }
        return false;
    }
    
    private ArrayList<String> fetchInvitedFriends(Bundle values)
    {
        ArrayList<String> friends = new ArrayList<String>();

        int size = values.size();
        int numOfFriends = size - 1;
        if (numOfFriends > 0)
        {
            for (int i = 0; i < numOfFriends; i++)
            {
                String key = String.format("to[%d]", i);
                String friendId = values.getString(key);
                Log.d(TAG, "friendId === " + friendId);
                if (friendId != null)
                {
                    friends.add(friendId);
                }
            }
        }

        return friends;
    }
    
    private void sendCardtoServer(ArrayList<String> friendList) {
        if (friendList != null) {
            HttpManager httpManager = new HttpManager();

            try {
                httpManager.postMailByList(context, friendList, publishCard.getImage(), publishCard.getSound(),
                        publishCard.getMessage(), publishCard.getSignDraftImage(),
                        String.valueOf(publishCard.getMessageTextSizeType()),
                        String.valueOf(publishCard.getMessageTextColor()), "thisCardName", new PostMailListener() {

                            @Override
                            public void onResult(Boolean isSuccess, String information) {

                                Log.e(TAG, "httpManager.postMailByList() isSuccess:" + isSuccess + ",information:"
                                        + information);

                                Toast.makeText(context, "httpManager.postMailByList() isSuccess:" + isSuccess,
                                        Toast.LENGTH_LONG).show();
                            }

                        });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void getNewsLinkfromServer() {
        Log.d(TAG, "getNewsLinkfromServer()");
        dialogHandler.sendEmptyMessage(ListUtility.SHOW_WAITING_DIALOG);
        HttpManager httpManager = new HttpManager();
        httpManager.uploadDIY(context, fileUri, new UploadDiyListener() {
            @Override
            public void onResult(Boolean isSuccess, String URL) {
                Log.e(TAG, "httpManager.uploadDIY() isSuccess:" + isSuccess + ",URL:" + URL);
                dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
                if (isSuccess) {
                    publish.setImgLink(URL);
                    publish.setLink(URL);
                    publishTimeline(FacebookManager.this.context, publish);
                } else {
                    // show error
                    showToast(URL);
                }
            }
        });
    }
	    
    private class LoginListener implements FacebookListener
    {
        @Override
        public void onSuccess() 
        {
            Log.d(TAG, "Login onSucess ");
            if (actionType != ManagerState.GET_USER_PROFILE) {
                userCallback = new RequestGraphUserCallback();
            }
            sendUserInfoRequest();
        }

        @Override
        public void onError(String errorMsg) 
        {
            Log.d(TAG, "Login onError actionType === " + actionType);
            if (actionType != ManagerState.GET_USER_PROFILE) {
                showToast(errorMsg);
            } else {
                userCallback.onCompleted(null, null);
            }
        }
    }
    
    public class RequestGraphUserCallback implements Request.GraphUserCallback
    {
        @Override
        public void onCompleted(GraphUser user, Response response)
        {
            if (user != null) {
                JSONObject userJSON = user.getInnerJSONObject();
                if(userJSON != null) {
                    UserInfo userInfo = new UserInfo(userJSON);
                    
                    Log.d(TAG, "userInfo id is " + userInfo.getId());
                    HttpManager httpManager = new HttpManager();
                    httpManager.init(context, userInfo.getId());
                }
            }
            dialogHandler.sendEmptyMessage(ListUtility.DISMISS_WAITING_DIALOG);
            
            switch (actionType) {
            case ManagerState.GET_USER_PROFILE:
                break;
            case ManagerState.GET_FRIEND:
                sendFriendListRequest();
                break;
            case ManagerState.INVITE:
                openInviteDialog();
                break;
            case ManagerState.PUBLISH:
            case ManagerState.UPLOAD:
            case ManagerState.PUBLISH_USER_FEED:
                checkPublishPermission();
                break;
            case ManagerState.PUBLISH_NEWS:
                getNewsLinkfromServer();
                break;
            }
        }
    }
    
    private class RequestPublishPermissionListener implements FacebookListener
    {
        @Override
        public void onSuccess() 
        {
            Log.d(TAG, "Request onSucess ");
            switch (actionType) {
            case ManagerState.PUBLISH:
                openPublishDialog();
                break;
            case ManagerState.INVITE:
                openInviteDialog();
                break;
            case ManagerState.UPLOAD:
                uploadImpl();
                break;
            case ManagerState.PUBLISH_USER_FEED:
                publishUserFeedImpl();
                break;
            }
        }

        @Override
        public void onError(String errorMsg) 
        {
            Log.d(TAG, "Request onError ");
            showToast(errorMsg);
        } 
    }
    
    private class PublishListener implements OnCompleteListener
    {
        @Override
        public void onComplete(Bundle values, FacebookException error) {
            if (error != null) {
                Log.d(TAG, "Publish had error is " + error.getMessage());
                if (error.getMessage() == null) {
                    if (context != null) {
                        showToast(context.getResources().getString(R.string.cancelPublish));
                    }
                } else {
                    showToast(error.getMessage());
                }
            } else {
                Log.d(TAG, "Publish no error ");
            }
        }
    }
    
    private class InviteListener implements OnCompleteListener
    {
        @Override
        public void onComplete(Bundle values, FacebookException error) {
            if (error != null) {
                Log.d(TAG, "Invite had error is " + error.getMessage());
                if (error.getMessage() == null) {
                    
                } else {
                    showToast(error.getMessage());
                }
            } else {
                Log.d(TAG, "Invite no error ");
                Log.d(TAG, "values " + values) ;
                sendCardtoServer(fetchInvitedFriends(values)); 
            }
        }        
    }
    
    private class UploadCallback implements Request.Callback
    {
        @Override
        public void onCompleted(Response response) {
            Log.d(TAG, "Upload response is " + response.getError());
        }
    }
    
    private class PublishUserFeed implements Request.Callback
    {
        @Override
        public void onCompleted(Response response) {
            Log.d(TAG, "PublishUserFeed response is " + response.getError());
        }       
    }
}

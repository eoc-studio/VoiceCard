package eoc.studio.voicecard.mainloading;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.facebook.JSONTag;
import eoc.studio.voicecard.facebook.Permissions;
import eoc.studio.voicecard.mailbox.MailboxActivity;
import eoc.studio.voicecard.mailbox.MailsAdapterData;
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import eoc.studio.voicecard.manager.GetMailListener;
import eoc.studio.voicecard.manager.GetRecommendListener;
import eoc.studio.voicecard.manager.GsonFacebookUser;
import eoc.studio.voicecard.manager.GsonRecommend;
import eoc.studio.voicecard.manager.GsonSend;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.manager.LoginListener;
import eoc.studio.voicecard.manager.MailCountListener;
import eoc.studio.voicecard.progresswheel.ProgressWheel;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;

public class MainLoadingActivity extends Activity
{

	private final static String TAG = "MainLoadingActivity";

	private final static String TAG_PROGRESS = "PROGRESS";

	private static final int FACEBOOK_ID_PROGRESS = 60;

	private static final int FACEBOOK_USER_PROFILE_PROGRESS = 60;

	private static final int INIT_HTTP_MANAGER_PROGRESS = 60;

	private static final int MAILBOX_COUNT_PROGRESS = 60;

	private static final int MAILBOX_RECEIVE_PROGRESS = 60;

	private static final int INIT_DATABASE_PROGRESS = 30;

	private static final int GET_RECOMMEND_PROGRESS = 30;

	private Context context;

	private ProgressWheel progressWheel;

	private int progress = 0;

	private String facebookUserID = null;

	MailsAdapterData mailsAdapterData;

	private int mailboxUnReadCount = 0;

	private String recommendBitmapUrl;
	private String recommendName;
	HttpManager httpManager;

	Handler progressHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			progressWheel.setProgress(progress);

			if (progress >= FACEBOOK_ID_PROGRESS + FACEBOOK_USER_PROFILE_PROGRESS
					+ INIT_HTTP_MANAGER_PROGRESS + MAILBOX_COUNT_PROGRESS
					+ MAILBOX_RECEIVE_PROGRESS + INIT_DATABASE_PROGRESS + GET_RECOMMEND_PROGRESS)
			{
				goToMainActivity();
				progress = 361;
			}
		}
	};

	private Session.StatusCallback statusCallback = new Session.StatusCallback()
	{
		@Override
		public void call(Session session, SessionState state, Exception exception)
		{

			processSessionStatus(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_loading);
		context = getApplicationContext();
		progressWheel = (ProgressWheel) findViewById(R.id.act_main_loading_progresswheel_main);
		progress = 0;

		httpManager = new HttpManager();
		startProgressWheel();
		initMailDataBase();
		getRecommendInfo(); 
		File dbFile = getDatabasePath("mails.db");
		Log.i(TAG, "dbFile.getAbsolutePath()" + dbFile.getAbsolutePath());
	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		progressWheel.stopSpinning();
		mailsAdapterData.close();

	}

	@Override
	protected void onResume()
	{

		super.onResume();
		Log.d(TAG, "onResume()");

		openFacebookSession();
		
	}

	private void initMailDataBase()
	{

		mailsAdapterData = new MailsAdapterData(context);
		mailsAdapterData.open();
		mailboxUnReadCount = mailboxUnReadCount + mailsAdapterData.getLocalUnReadMailCount();
		Log.d(TAG, "initMailDataBase() mailboxUnReadCount :" + mailboxUnReadCount);
		addProgressWheel(INIT_DATABASE_PROGRESS);
		Log.d(TAG_PROGRESS, "INIT_DATABASE_PROGRESS");
	}

	private void getRecommendInfo()
	{

		httpManager.getRecommend(context, new GetRecommendListener()
		{
			@Override
			public void onResult(Boolean isSuccess, ArrayList<GsonRecommend> recommends)
			{

				Log.e(TAG, "httpManager.getRecommend() isSuccess:" + isSuccess + ",mails:"
						+ recommends.toString());

				if (recommends != null && recommends.size() > 0)
				{
					Log.e(TAG, "recommends.get(0).getImg():"
							+ recommends.get(0).getImg()+"recommends.get(0).getName():"+recommends.get(0).getName());
					recommendBitmapUrl = recommends.get(0).getImg();
					recommendName = recommends.get(0).getName();
					addProgressWheel(GET_RECOMMEND_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_RECOMMEND_PROGRESS");
				}


			}

		});

	}

	public void openFacebookSession()
	{

		Session session = Session.getActiveSession();

		if (session != null && !session.isOpened() && !session.isClosed())
		{
			Log.d("FbLogin", "onResume() if(!session.isOpened() && !session.isClosed()) ");
			// session.requestNewReadPermissions(new
			// Session.NewPermissionsRequest(
			// MainLoadingActivity.this,
			// FacebookManager.Permissions.READ_PERMISSION));
			session.openForRead(new Session.OpenRequest(MainLoadingActivity.this)
					.setCallback(statusCallback)
					.setPermissions(Permissions.READ_PERMISSION)
					.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO));

		}
		else
		{

			if (session == null) Log.d("FbLogin", "onResume() iif (session==null)");
			if (session != null)
			{
				Log.d("FbLogin", "onResume() session.isOpened()" + session.isOpened());
				Log.d("FbLogin", "onResume() session.isClosed()" + session.isClosed());
			}
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	@Override
	public void onPause()
	{

		super.onPause();
		Log.d(TAG, "onPause()");
	}

	public void goToMainActivity()
	{

		Log.d(TAG, "goToMainActivity() ");
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
/*		bundle.putString("recommendBitmapUrl", recommendBitmapUrl);
		bundle.putString("recommendName", recommendName);
		bundle.putInt("mailboxUnReadCount", this.mailboxUnReadCount);*/
		intent.putExtras(bundle);
		intent.setClass(context, MainMenuActivity.class);
		
		String PREFS_FILENAME = "MAIN_MENU_SETTING";  
		SharedPreferences configPreferences = getSharedPreferences(PREFS_FILENAME, 0); 
		configPreferences.edit().putString("recommendBitmapUrl", recommendBitmapUrl).commit();
		configPreferences.edit().putString("recommendName", recommendName).commit();
		Log.d(TAG, "goToMainActivity() this.mailboxUnReadCount:"+this.mailboxUnReadCount);
		configPreferences.edit().putInt("mailboxUnReadCount", this.mailboxUnReadCount).commit();
		startActivity(intent);
		finish();
	}

	public void startProgressWheel()
	{

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				while (progress < 361)
				{

					try
					{
						progressHandler.sendMessage(progressHandler.obtainMessage());
						Thread.sleep(300);
					}
					catch (Throwable t)
					{}
				}
			}
		}).start();
	}

	public static void callFacebookLogout(Context context)
	{

		Log.d(TAG, "callFacebookLogout() ");
		Session session = Session.getActiveSession();
		if (session != null)
		{

			if (!session.isClosed())
			{
				session.closeAndClearTokenInformation();
				Session.setActiveSession(null);
				// clear your preferences if saved
			}
		}
		else
		{

			session = new Session(context);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			Session.setActiveSession(null);
			// clear your preferences if saved

		}

	}

	
	private  String getStringJsonObjectByCheck(JSONObject obj,String key)
	{

		try
		{
			return obj.getString(key);
			
		}
		catch (JSONException e)
		{
		
			e.printStackTrace();
			return "";
		}
	}
	


	public void processSessionStatus(Session session, SessionState state, Exception exception)
	{

		if (session != null && session.isOpened())
		{

			if (session.getPermissions().contains("email"))
			{

				Request getMe = Request.newMeRequest(session, new Request.GraphUserCallback()
				{

					@Override
					public void onCompleted(GraphUser user, Response response)
					{

						if (user != null)
						{
							Map<String, Object> responseMap = new HashMap<String, Object>();
							GraphObject graphObject = response.getGraphObject();
							responseMap = graphObject.asMap();
							Log.d("", "Response Map KeySet - " + responseMap.keySet());

							JSONObject userJSON = user.getInnerJSONObject();
							if (userJSON != null)
							{
								try
								{
									facebookUserID = userJSON.getString(JSONTag.ID);
									Log.d(TAG, "id:" + userJSON.getString(JSONTag.ID));
									Log.d(TAG, "email:" + userJSON.getString(JSONTag.EMAIL));
									Log.d(TAG, "name:" + userJSON.getString(JSONTag.NAME));
									Log.d(TAG, "gender:" + userJSON.getString(JSONTag.GENDER));
									Log.d(TAG,
											"birthday:" + userJSON.getString(JSONTag.BIRTHDAY));
									Log.d(TAG, "link:" + userJSON.getString(JSONTag.LINK));
									Log.d(TAG, "timezone:" + userJSON.getInt(JSONTag.TIMEZONE));
									Log.d(TAG, "locale:" + userJSON.getString(JSONTag.LOCALE));
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}

							addProgressWheel(FACEBOOK_ID_PROGRESS);
							Log.d(TAG_PROGRESS, "FACEBOOK_ID_PROGRESS");
							if (facebookUserID != null)
							{

								httpManager.init(context, facebookUserID);
								addProgressWheel(INIT_HTTP_MANAGER_PROGRESS);
								Log.d(TAG_PROGRESS, "INIT_HTTP_MANAGER_PROGRESS");
								GsonFacebookUser gsonFacebookUser = null;
								try
								{
									gsonFacebookUser = new GsonFacebookUser(facebookUserID,
											userJSON.getString(JSONTag.BIRTHDAY),
											"this is dummy picture link", userJSON
													.getString(JSONTag.LOCALE), userJSON
													.getString(JSONTag.LINK), userJSON
													.getJSONObject(JSONTag.HOMETOWN)
													.getString(JSONTag.NAME), userJSON
													.getString(JSONTag.TIMEZONE),
											"this is dummy title", userJSON
													.getString(JSONTag.EMAIL), userJSON
													.getString(JSONTag.NAME), userJSON
													.getString(JSONTag.GENDER), userJSON
													.getJSONArray(JSONTag.EDUCATION)
													.getJSONObject(0).getJSONObject("school")
													.getString(JSONTag.NAME), userJSON
													.getJSONArray(JSONTag.WORK)
													.getJSONObject(0).getJSONObject("employer")
													.getString(JSONTag.NAME),
											"this is dummy mobile");

								}
								catch (JSONException e)
								{
									e.printStackTrace();
								}

								httpManager.facebookLogin(context, gsonFacebookUser,
										new LoginListener()
										{
											@Override
											public void onResult(Boolean isSuccess,
													String information)
											{

												Log.e(TAG,
														"httpManager.fascebookLogin() isSuccess:"
																+ isSuccess + ",information:"
																+ information);

												addProgressWheel(FACEBOOK_USER_PROFILE_PROGRESS);
												Log.d(TAG_PROGRESS,
														"FACEBOOK_USER_PROFILE_PROGRESS");
											}

										});

								httpManager.getUnreadMailCount(context, new MailCountListener()
								{
									@Override
									public void onResult(Boolean isSuccess, int count)
									{

										Log.e(TAG,
												"httpManager.getUnreadMailCount() isSuccess:"
														+ isSuccess + ",information:"
														+ String.valueOf(count));

										mailboxUnReadCount = mailboxUnReadCount + count;
										Log.d(TAG, "getUnreadMailCount() mailboxUnReadCount :"
												+ mailboxUnReadCount);
										addProgressWheel(MAILBOX_COUNT_PROGRESS);
										Log.d(TAG_PROGRESS, "MAILBOX_COUNT_PROGRESS");
										if (count > 0)
										{
											// start get mails from server
											httpManager.getMails(context, new GetMailListener()
											{
												@Override
												public void onResult(Boolean isSuccess,
														ArrayList<GsonSend> mails)
												{

													Log.e(TAG,
															"httpManager.getMails() isSuccess:"
																	+ isSuccess + ",mails:"
																	+ mails.toString());
													for (GsonSend mail : mails)
													{
														if (mailsAdapterData != null)
														{
															mailsAdapterData.create(
																	mail.getSendID(),
																	mail.getSendFrom(),
																	mail.getSendFrom(), // replace
																						// this
																						// col
																						// with
																						// sendfromName
																	"fb_links_dummy",// replace
																						// this
																						// col
																						// with
																						// sendfromLink
																	mail.getSendTo(),
																	mail.getSubject(),
																	mail.getBody(),
																	mail.getFont_size(),
																	mail.getFont_color(),
																	mail.getImg(), null,
																	mail.getSpeech(),
																	mail.getSign(),
																	mail.getSend_time(), 1);
														}

													}

												}

											});
										}
										else
										{

										}
										addProgressWheel(MAILBOX_RECEIVE_PROGRESS);
										Log.d(TAG_PROGRESS, "MAILBOX_RECEIVE_PROGRESS");
									}

								});
							}
						}
						else
						{
							// Clear all session info & ask user to login
							// again
							Session session = Session.getActiveSession();
							if (session != null)
							{
								session.closeAndClearTokenInformation();
							}
						}
					}

				});
				
				getMe.executeAsync();
			}
			else
			{
				session.requestNewReadPermissions(new Session.NewPermissionsRequest(
						MainLoadingActivity.this, Permissions.READ_PERMISSION));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult() Result Code is - " + resultCode + "");
		if (Session.getActiveSession() != null)
		{
			Session.getActiveSession().onActivityResult(MainLoadingActivity.this, requestCode,
					resultCode, data);
		}
		else
		{
			// Session.openActiveSession(this, true, statusCallback);
		}
	}

	public synchronized void addProgressWheel(int newProgress)
	{

		progress = progress + newProgress;
		Log.d(TAG_PROGRESS, "progress:" + progress);

	}

	public void facebook()
	{

		if (!checkNetwork())
		{
			Toast.makeText(getApplicationContext(), "No active internet connection ...",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{

		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState() ");
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onStart()
	{

		super.onStart();
		// Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	protected void onStop()
	{

		Log.d(TAG, "onSaveInstanceState() ");
		super.onStop();
		if (Session.getActiveSession() != null)
			Session.getActiveSession().removeCallback(statusCallback);
	}

	private boolean checkNetwork()
	{

		boolean wifiAvailable = false;
		boolean mobileAvailable = false;
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();
		for (NetworkInfo netInfo : networkInfo)
		{
			if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
				if (netInfo.isConnected()) wifiAvailable = true;
			if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
				if (netInfo.isConnected()) mobileAvailable = true;
		}
		return wifiAvailable || mobileAvailable;
	}

	public Boolean checkFbInstalled()
	{

		PackageManager pm = getPackageManager();
		boolean flag = false;
		try
		{
			pm.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
			flag = true;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			flag = false;
		}
		return flag;
	}
	
	private String getHomeTown(JSONObject obj)
	{
		
		
		try
		{
			return obj
					.getJSONObject(JSONTag.HOMETOWN)
					.getString(JSONTag.NAME);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	private String getWork(JSONObject obj)
	{
		
		
		try
		{
			return 	obj
					.getJSONArray(JSONTag.WORK)
					.getJSONObject(0).getJSONObject("employer")
					.getString(JSONTag.NAME);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	
	private String getEducation(JSONObject obj)
	{
		
		
		try
		{
			return obj
					.getJSONArray(JSONTag.EDUCATION)
					.getJSONObject(0).getJSONObject("school")
					.getString(JSONTag.NAME);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	
	// private class RequestGraphUserCallback implements
	// Request.GraphUserCallback
	// {
	// @Override
	// public void onCompleted(GraphUser user, Response response)
	// {
	// if (user != null) {
	// JSONObject userJSON = user.getInnerJSONObject();
	// if(userJSON != null) {
	// try {
	// Log.d(TAG, "id is " +
	// userJSON.getString(FacebookManager.BundleParams.ID));
	// Log.d(TAG, "email is " + userJSON.getString(BundleParams.EMAIL));
	// Log.d(TAG, "name is " + userJSON.getString(BundleParams.NAME));
	// Log.d(TAG, "gender is " + userJSON.getString(BundleParams.GENDER));
	// Log.d(TAG, "birthday is " + userJSON.getString(BundleParams.BIRTHDAY));
	// Log.d(TAG, "link is " + userJSON.getString(BundleParams.LINK));
	// Log.d(TAG, "timezone is " + userJSON.getInt(BundleParams.TIMEZONE));
	// Log.d(TAG, "locale is " + userJSON.getString(BundleParams.LOCALE));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// Log.d(TAG, "img is "
	// +
	// userJSON.getJSONObject(BundleParams.PICTURE).getJSONObject("data").getString("url"));
	//
	// Log.d(TAG, "hometown is "
	// +
	// userJSON.getJSONObject(BundleParams.HOMETOWN).getString(BundleParams.NAME));
	//
	// Log.d(TAG, "work is "
	// +
	// userJSON.getJSONArray(BundleParams.WORK).getJSONObject(0).getJSONObject("employer")
	// .getString(BundleParams.NAME));
	//
	// Log.d(TAG, "education is "
	// +
	// userJSON.getJSONArray(BundleParams.EDUCATION).getJSONObject(0).getJSONObject("school")
	// .getString(BundleParams.NAME));
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }
}

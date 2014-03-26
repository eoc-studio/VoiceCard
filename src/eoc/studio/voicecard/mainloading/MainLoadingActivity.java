package eoc.studio.voicecard.mainloading;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.database.CardAssistant;
import eoc.studio.voicecard.card.database.CardDatabaseHelper;
import eoc.studio.voicecard.card.database.CategoryAssistant;
import eoc.studio.voicecard.facebook.FacebookManager;
import eoc.studio.voicecard.facebook.TestFacebookActivity;
import eoc.studio.voicecard.facebook.FacebookManager.RequestGraphUserCallback;
import eoc.studio.voicecard.facebook.enetities.UserInfo;
import eoc.studio.voicecard.facebook.utils.BundleTag;
import eoc.studio.voicecard.facebook.utils.JSONTag;
import eoc.studio.voicecard.facebook.utils.Permissions;
import eoc.studio.voicecard.mailbox.MailboxActivity;
import eoc.studio.voicecard.mailbox.MailsAdapterData;
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import eoc.studio.voicecard.manager.CardImages;
import eoc.studio.voicecard.manager.GetCardListener;
import eoc.studio.voicecard.manager.GetCategoryListener;
import eoc.studio.voicecard.manager.GetMailListener;
import eoc.studio.voicecard.manager.GetRecommendListener;
import eoc.studio.voicecard.manager.GsonCard;
import eoc.studio.voicecard.manager.GsonCategory;
import eoc.studio.voicecard.manager.GsonFacebookUser;
import eoc.studio.voicecard.manager.GsonRecommend;
import eoc.studio.voicecard.manager.GsonSend;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.manager.LoginListener;
import eoc.studio.voicecard.manager.MailCountListener;
import eoc.studio.voicecard.manager.NotifyMailReadListener;
import eoc.studio.voicecard.manager.NotifyPatchListener;
import eoc.studio.voicecard.manager.PatchStateListener;
import eoc.studio.voicecard.progresswheel.ProgressWheel;
import eoc.studio.voicecard.utils.FileUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
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

	private static final int FACEBOOK_ID_PROGRESS = 45;

	private static final int FACEBOOK_USER_PROFILE_PROGRESS = 45;

	private static final int INIT_HTTP_MANAGER_PROGRESS = 45;

	private static final int MAILBOX_COUNT_PROGRESS = 45;

	private static final int MAILBOX_RECEIVE_PROGRESS = 45;

	private static final int INIT_DATABASE_PROGRESS = 45;

	private static final int COPY_CARD_AND_CATEGORY = 30;

	private static final int GET_CATEGORY_INFO_PROGRESS = 15;

	private static final int GET_CARD_INFO_PROGRESS = 15;

	private static final int GET_CATEGORY_IMGS_PROGRESS = 15;

	private static final int GET_CARD_IMGS_PROGRESS = 15;

	private Context context;

	private ProgressWheel progressWheel;

	private int progress = 0;

	private String facebookUserID = null;

	private String userName = null;

	private MailsAdapterData mailsAdapterData;

	private CardDatabaseHelper cardDatabaseHelper;

	private int mailboxUnReadCount = 0;

	private String recommendBitmapUrl;

	private String recommendName;

	private HttpManager httpManager;

	private DownlaodCategoryAysncTaskListener downlaodCategoryAysncTaskListener;

	private DownlaodCardAysncTaskListener downlaodCardAysncTaskListener;

	private FacebookManager facebookManager;

	Handler progressHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			progressWheel.setProgress(progress);

			if (progress >= FACEBOOK_ID_PROGRESS + FACEBOOK_USER_PROFILE_PROGRESS
					+ INIT_HTTP_MANAGER_PROGRESS + MAILBOX_COUNT_PROGRESS
					+ MAILBOX_RECEIVE_PROGRESS + INIT_DATABASE_PROGRESS + COPY_CARD_AND_CATEGORY
					+ GET_CATEGORY_INFO_PROGRESS + GET_CARD_INFO_PROGRESS
					+ GET_CATEGORY_IMGS_PROGRESS + GET_CARD_IMGS_PROGRESS)
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

		facebookManager = FacebookManager.getInstance(MainLoadingActivity.this);

		if (facebookManager != null)
		{
			facebookManager.getUserProfile(MainLoadingActivity.this,
					facebookManager.new RequestGraphUserCallback()
					{
						@Override
						public void onCompleted(GraphUser user, Response response)
						{

							getFacebookUserProfile(user, response);

						}
					});
		}

		httpManager = new HttpManager(getApplicationContext());
		startProgressWheel();

		initCardDataBase();
		checkPatchAndUpdateFromServer();
		copyCardAndCategoryFromAsset();

	}

	private void checkPatchAndUpdateFromServer()
	{

		httpManager.getPatchState(context, new PatchStateListener()
		{

			@Override
			public void onResult(Boolean isSuccess, Boolean isNeedPatched)
			{

				Log.d(TAG, "getPatchState isSuccess: " + isSuccess + ",isNeedPatched: "
						+ isNeedPatched);
				if(isSuccess&isNeedPatched){
					Log.d(TAG, "getPatchState try to download card and category from server");

					getCategoryInfoFromServer();
					getCardInfoFromServer();
				}
				else
				{
					Log.d(TAG, "Direct pass the progress of [download card and category]");

					addProgressWheel(GET_CATEGORY_INFO_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CATEGORY_INFO_PROGRESS [pass]");
					addProgressWheel(GET_CARD_INFO_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CARD_INFO_PROGRESS [pass]");
					addProgressWheel(GET_CATEGORY_IMGS_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CATEGORY_IMGS_PROGRESS [pass]");
					addProgressWheel(GET_CARD_IMGS_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CARD_IMGS_PROGRESS [pass]");
				}
			}
		});

	}

	private void initCardDataBase()
	{

		cardDatabaseHelper = new CardDatabaseHelper(context);
		cardDatabaseHelper.open();

	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		progressWheel.stopSpinning();
		mailsAdapterData.close();
		cardDatabaseHelper.close();

	}

	@Override
	protected void onResume()
	{

		super.onResume();
		Log.d(TAG, "onResume()");

		// openFacebookSession();

	}

	@Override
	public void onBackPressed()
	{

		Log.d(TAG, "onBackPressed() diable back key !!!!");
	}

	private void copyCardAndCategoryFromAsset()
	{

		new Thread()
		{
			@Override
			public void run()
			{

				super.run();

				File cardFolderFile = new File(getFilesDir() + "/CardImages");
				File cardCategoryFolderFile = new File(getFilesDir() + "/CategoryImages");

				if (!cardFolderFile.exists() || !cardCategoryFolderFile.exists())
				{
					FileUtility.copyAssetFolder(getAssets(), "files", getFilesDir()
							.getAbsolutePath());

					Log.d(TAG, "copyCardAndCategoryFromAsset()");
				}
				else
				{
					Log.d(TAG,
							"cardFolderFile.exists(): " + cardFolderFile.exists()
									+ " ,cardCategoryFolderFile.exists(): "
									+ cardCategoryFolderFile.exists());
				}

				addProgressWheel(COPY_CARD_AND_CATEGORY);
				Log.d(TAG_PROGRESS, "COPY_CARD_AND_CATEGORY");

			}
		}.start();
	}

	private void downloadCategoryImages()
	{

		ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();
		// categoryAssistantList =
		// cardDatabaseHelper.getEnabledCategory(cardDatabaseHelper
		// .getSystemDPI(context));

		// categoryAssistantList = cardDatabaseHelper
		// .getEnabledAndLocalIsNullCategory(cardDatabaseHelper.getSystemDPI(context));

		categoryAssistantList = cardDatabaseHelper
				.getLocalDateIsNullOrLocalPathIsNullCategory(cardDatabaseHelper
						.getSystemDPI(context));

		Log.d(TAG, "onResume() getLocalDateIsNullOrLocalPathIsNullCategory categoryAssistantList :"
				+ categoryAssistantList);
		if (categoryAssistantList != null)
		{
			downlaodCategoryAysncTaskListener = new DownlaodCategoryAysncTaskListener()
			{

				@Override
				public void processFinish(ArrayList<CategoryAssistant> result)
				{

					cardDatabaseHelper.updateCategoryImgLocalPath(result,
							cardDatabaseHelper.getSystemDPI(context));

					addProgressWheel(GET_CATEGORY_IMGS_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CATEGORY_IMGS_PROGRESS");
				}

			};
			DownlaodCategoryAysncTask downlaodCategoryAysncTask = new DownlaodCategoryAysncTask(
					context, downlaodCategoryAysncTaskListener);
			downlaodCategoryAysncTask.execute(categoryAssistantList);
		}
		else
		{
			addProgressWheel(GET_CATEGORY_IMGS_PROGRESS);
			Log.d(TAG_PROGRESS, "GET_CATEGORY_IMGS_PROGRESS");
		}

	}

	private void downloadCradImages()
	{

		downlaodCardAysncTaskListener = new DownlaodCardAysncTaskListener()
		{
			@Override
			public void processFinish(ArrayList<CardAssistant> result)
			{

				Log.d(TAG, "downloadCradImages result is " + result);

				if(result!=null)
				{
					cardDatabaseHelper.updateCardImgLocalPath(result,
							cardDatabaseHelper.getSystemDPI(context));
				}				
				addProgressWheel(GET_CARD_IMGS_PROGRESS);
				Log.d(TAG_PROGRESS, "GET_CARD_IMGS_PROGRESS");

				notifyPatchIsDone();
			}
		};
		ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();
		/*
		 * cardAssistantList =
		 * cardDatabaseHelper.getEnabledCard(cardDatabaseHelper
		 * .getSystemDPI(context));
		 */
		// cardAssistantList =
		// cardDatabaseHelper.getEnabledAndLocalIsNullCard(cardDatabaseHelper
		// .getSystemDPI(context));
		//
		cardAssistantList = cardDatabaseHelper
				.getLocalDateIsNullOrLocalPathIsNullCard(cardDatabaseHelper.getSystemDPI(context));

		// cardAssistantList = cardDatabaseHelper
		// .getLocalDateIsNullOrLocalPathIsNullCard(CardDatabaseHelper.DPI_XHDPI);

		Log.d(TAG, "onResume() cardAssistantList :" + cardAssistantList);

		if (cardAssistantList != null)
		{

			for (int index = 0; index < cardAssistantList.size(); index++)
			{
				Log.d(TAG,
						"onResume() cardAssistantList getCardName:"
								+ cardAssistantList.get(index).getCardName());
			}

			DownlaodCardAysncTask downlaodCardAysncTask = new DownlaodCardAysncTask(context,
					downlaodCardAysncTaskListener);
			downlaodCardAysncTask.execute(cardAssistantList);
		}
		else
		{
			addProgressWheel(GET_CARD_IMGS_PROGRESS);
			Log.d(TAG_PROGRESS, "GET_CARD_IMGS_PROGRESS [pass] because (cardAssistantList == null)");

			notifyPatchIsDone();
		}
	}

	public void notifyPatchIsDone()
	{

		httpManager.notifyPatchUpdate(context, new NotifyPatchListener()
		{

			@Override
			public void onResult(Boolean isSuccess, String information)
			{

				Log.d(TAG, "notifyPatchUpdate() isSuccess: " + isSuccess + ",information is "
						+ information);

			}
		});
	}

	private void initMailDataBase(String owerId)
	{

		mailsAdapterData = new MailsAdapterData(context);
		mailsAdapterData.open();
		mailboxUnReadCount = mailboxUnReadCount + mailsAdapterData.getLocalUnReadMailCount(owerId);
		Log.d(TAG, "initMailDataBase() mailboxUnReadCount :" + mailboxUnReadCount);
		addProgressWheel(INIT_DATABASE_PROGRESS);
		Log.d(TAG_PROGRESS, "INIT_DATABASE_PROGRESS");
	}

	private void getCategoryInfoFromServer()
	{

		httpManager.getCategory(context, new GetCategoryListener()
		{
			@Override
			public void onResult(Boolean isSuccess, ArrayList<GsonCategory> gsonCategoryList)
			{

				if (isSuccess)
				{

					for (int index = 0; index < gsonCategoryList.size(); index++)
					{
						Log.d(TAG,
								"gsonCategoryList index:[" + index + "]"
										+ gsonCategoryList.get(index).toString());

					}
					// cardDatabaseHelper.deleteCategoryTable();
					// write to datebase
					for (int index = 0; index < gsonCategoryList.size(); index++)
					{

						if (cardDatabaseHelper.isExistCategory(gsonCategoryList.get(index)
								.getCategoryID()))
						{
							// if the category exist , update the row
							cardDatabaseHelper.updateCategoryRow(gsonCategoryList.get(index)
									.getCategoryID(),
									gsonCategoryList.get(index).getCategoryName(), gsonCategoryList
											.get(index).getCategoryEnable(),
									gsonCategoryList.get(index).getCategoryImageMDPI(),
									gsonCategoryList.get(index).getCategoryImageHDPI(),
									gsonCategoryList.get(index).getCategoryImageXHDPI(),
									gsonCategoryList.get(index).getCategoryImageXXHDPI(), null,
									null, null, null, gsonCategoryList.get(index)
											.getCategoryEditedDate(), null);
						}
						else
						{
							// if the cateory is not exist, insert the row
							cardDatabaseHelper.createCategoryRow(gsonCategoryList.get(index)
									.getCategoryID(),
									gsonCategoryList.get(index).getCategoryName(), gsonCategoryList
											.get(index).getCategoryEnable(),
									gsonCategoryList.get(index).getCategoryImageMDPI(),
									gsonCategoryList.get(index).getCategoryImageHDPI(),
									gsonCategoryList.get(index).getCategoryImageXHDPI(),
									gsonCategoryList.get(index).getCategoryImageXXHDPI(), null,
									null, null, null, gsonCategoryList.get(index)
											.getCategoryEditedDate());
						}

					}

					addProgressWheel(GET_CATEGORY_INFO_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CATEGORY_INFO_PROGRESS");

					downloadCategoryImages();
				}
			}
		});
	}

	private void getCardInfoFromServer()
	{

		httpManager.getCard(context, new GetCardListener()
		{
			@Override
			public void onResult(Boolean isSuccess, ArrayList<GsonCard> gsonCardList)
			{

				if (isSuccess)
				{

					for (int index = 0; index < gsonCardList.size(); index++)
					{
						Log.d(TAG, "gsonCardList index:[" + index + "]"
								+ gsonCardList.get(index).toString());
					}

					// cardDatabaseHelper.deleteCardTable();
					// write to datebase
					for (int index = 0; index < gsonCardList.size(); index++)
					{
						CardImages cardImages = new CardImages(gsonCardList.get(index),
								cardDatabaseHelper.getSystemDPI(context));

						if (cardDatabaseHelper.isExistCard(gsonCardList.get(index).getCardID()))
						{
							// if the card exist , update the row

							cardDatabaseHelper.updateCardRow(cardDatabaseHelper
									.getSystemDPI(context), Integer.valueOf(gsonCardList.get(index)
									.getCardID()), gsonCardList.get(index).getCardName(), Integer
									.valueOf(gsonCardList.get(index).getCategoryID()), gsonCardList
									.get(index).getCardEnable(), gsonCardList.get(index)
									.getCardFont(), cardImages.getCloseURL(), cardImages
									.getCoverURL(), cardImages.getLeftURL(), cardImages
									.getOpenURL(), cardImages.getRightURL(), null, null, null,
									null, null, gsonCardList.get(index).getCardEditedDate(), null);
						}
						else
						{
							// if the card is not exist, insert the row

							cardDatabaseHelper.createCardRow(cardDatabaseHelper
									.getSystemDPI(context), Integer.valueOf(gsonCardList.get(index)
									.getCardID()), gsonCardList.get(index).getCardName(), Integer
									.valueOf(gsonCardList.get(index).getCategoryID()), gsonCardList
									.get(index).getCardEnable(), gsonCardList.get(index)
									.getCardFont(), cardImages.getCloseURL(), cardImages
									.getCoverURL(), cardImages.getLeftURL(), cardImages
									.getOpenURL(), cardImages.getRightURL(), null, null, null,
									null, null, 0, gsonCardList.get(index).getCardEditedDate());
						}

					}
					addProgressWheel(GET_CARD_INFO_PROGRESS);
					Log.d(TAG_PROGRESS, "GET_CARD_INFO_PROGRESS");
					downloadCradImages();

				}

			}
		});
	}

	public String getMobile()
	{

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Log.d(TAG, "getMobile" + tm.getLine1Number());
		String phone = tm.getLine1Number();

		return phone;
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
					.setCallback(statusCallback).setPermissions(Permissions.READ_PERMISSION)
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
		/*
		 * bundle.putString("recommendBitmapUrl", recommendBitmapUrl);
		 * bundle.putString("recommendName", recommendName);
		 * bundle.putInt("mailboxUnReadCount", this.mailboxUnReadCount);
		 */
		intent.putExtras(bundle);
		intent.setClass(context, MainMenuActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		String PREFS_FILENAME = "MAIN_MENU_SETTING";
		SharedPreferences configPreferences = getSharedPreferences(PREFS_FILENAME, 0);
		Log.d(TAG, "goToMainActivity() this.mailboxUnReadCount:" + this.mailboxUnReadCount);
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

						getFacebookUserProfile(user, response);
					}

				});

				StringBuilder queryString = new StringBuilder().append(JSONTag.NAME).append(", ")
						.append(JSONTag.BIRTHDAY).append(", ").append(JSONTag.PICTURE).append(", ")
						.append(JSONTag.EMAIL).append(", ").append(JSONTag.EDUCATION).append(", ")
						.append(JSONTag.WORK).append(", ").append(JSONTag.GENDER).append(", ")
						.append(JSONTag.LINK).append(", ").append(JSONTag.HOMETOWN).append(", ")
						.append(JSONTag.TIMEZONE).append(", ").append(JSONTag.LOCALE);
				Bundle requestParams = getMe.getParameters();
				requestParams.putString(BundleTag.FIELDS, queryString.toString());
				getMe.setParameters(requestParams);

				getMe.executeAsync();
			}
			else
			{
				session.requestNewReadPermissions(new Session.NewPermissionsRequest(
						MainLoadingActivity.this, Permissions.READ_PERMISSION));
			}
		}
	}

	private void getFacebookUserProfile(GraphUser user, Response response)
	{

		if (user != null)
		{
			Map<String, Object> responseMap = new HashMap<String, Object>();
			GraphObject graphObject = response.getGraphObject();
			responseMap = graphObject.asMap();
			Log.d("", "Response Map KeySet - " + responseMap.keySet());
			UserInfo userInfo = null;

			JSONObject userJSON = user.getInnerJSONObject();
			if (userJSON != null)
			{
				userInfo = new UserInfo(userJSON);

				Log.d(TAG, "userInfo id is " + userInfo.getId());
				facebookUserID = userInfo.getId();
				Log.d(TAG, "userInfo getName is " + userInfo.getName());
				userName = userInfo.getName();
				addProgressWheel(FACEBOOK_ID_PROGRESS);
				Log.d(TAG_PROGRESS, "FACEBOOK_ID_PROGRESS");
			}

			if (facebookUserID != null)
			{
				initMailDataBase(facebookUserID);
				httpManager.init(context, facebookUserID, userName);
				Log.d(TAG, "httpManager.getUserName is " + httpManager.getUserName());
				addProgressWheel(INIT_HTTP_MANAGER_PROGRESS);
				Log.d(TAG_PROGRESS, "INIT_HTTP_MANAGER_PROGRESS");
				GsonFacebookUser gsonFacebookUser = null;
				try
				{

					gsonFacebookUser = new GsonFacebookUser(facebookUserID,
							userJSON.getString(JSONTag.BIRTHDAY), getPictureLink(userJSON),
							getStringJsonObjectByCheck(userJSON, JSONTag.LOCALE),
							getStringJsonObjectByCheck(userJSON, JSONTag.LINK),
							getHomeTown(userJSON), getStringJsonObjectByCheck(userJSON,
									JSONTag.TIMEZONE), userInfo.getTitle(),
							getStringJsonObjectByCheck(userJSON, JSONTag.EMAIL),
							getStringJsonObjectByCheck(userJSON, JSONTag.NAME),
							getStringJsonObjectByCheck(userJSON, JSONTag.GENDER),
							getEducation(userJSON), getWork(userJSON), getMobile());

				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}

				httpManager.facebookLogin(context, gsonFacebookUser, new LoginListener()
				{
					@Override
					public void onResult(Boolean isSuccess, String information)
					{

						Log.e(TAG, "httpManager.fascebookLogin() isSuccess:" + isSuccess
								+ ",information:" + information);

						addProgressWheel(FACEBOOK_USER_PROFILE_PROGRESS);
						Log.d(TAG_PROGRESS, "FACEBOOK_USER_PROFILE_PROGRESS");
					}

				});

				httpManager.getUnreadMailCount(context, new MailCountListener()
				{
					@Override
					public void onResult(Boolean isSuccess, int count)
					{

						Log.e(TAG, "httpManager.getUnreadMailCount() isSuccess:" + isSuccess
								+ ",information:" + String.valueOf(count));

						mailboxUnReadCount = mailboxUnReadCount + count;
						Log.d(TAG, "getUnreadMailCount() mailboxUnReadCount :" + mailboxUnReadCount);
						addProgressWheel(MAILBOX_COUNT_PROGRESS);
						Log.d(TAG_PROGRESS, "MAILBOX_COUNT_PROGRESS");
						if (count > 0)
						{
							// start get mails from server
							httpManager.getMails(context, new GetMailListener()
							{
								@Override
								public void onResult(Boolean isSuccess, ArrayList<GsonSend> mails)
								{

									Log.e(TAG, "httpManager.getMails() isSuccess:" + isSuccess
											+ ",mails:" + mails.toString());
									for (GsonSend mail : mails)
									{
										if (mailsAdapterData != null)
										{
											mailsAdapterData.create(mail.getCardID(),
													facebookUserID, mail.getSendID(),
													mail.getSendFrom(), mail.getSendFromName(),
													mail.getSendFromLink(), mail.getSendTo(),
													mail.getSubject(), mail.getBody(),
													mail.getFont_size(), mail.getFont_color(),
													mail.getImg(), null, mail.getSpeech(),
													mail.getSign(), mail.getSend_time(), 1);
										}

									}

									if (isSuccess)
									{
										// notify server mails
										// already got from user
										httpManager.notifyMailsRead(context,
												new NotifyMailReadListener()
												{
													@Override
													public void onResult(Boolean isSuccess,
															String information)
													{

														Log.e(TAG,
																"httpManager.notifyMailsRead() isSuccess:"
																		+ isSuccess
																		+ ",information:"
																		+ information);
													}
												});

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult() Result Code is - " + resultCode + "");
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
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

	private String getStringJsonObjectByCheck(JSONObject obj, String key)
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

	private String getHomeTown(JSONObject obj)
	{

		try
		{
			return obj.getJSONObject(JSONTag.HOMETOWN).getString(JSONTag.NAME);
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
			return obj.getJSONArray(JSONTag.WORK).getJSONObject(0).getJSONObject("employer")
					.getString(JSONTag.NAME);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	private String getPictureLink(JSONObject obj)
	{

		try
		{
			return obj.getJSONObject(JSONTag.PICTURE).getJSONObject("data").getString("url");
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
			return obj.getJSONArray(JSONTag.EDUCATION).getJSONObject(0).getJSONObject("school")
					.getString(JSONTag.NAME);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}

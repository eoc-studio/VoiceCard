package eoc.studio.voicecard.mainmenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.calendarview.MainCalendarView;
import eoc.studio.voicecard.calendarview.CalendarIntentHelper;
import eoc.studio.voicecard.calendarview.DataProcess;
import eoc.studio.voicecard.card.editor.CardCategorySelectorActivity;
import eoc.studio.voicecard.facebook.FacebookManager;
import eoc.studio.voicecard.facebook.enetities.UserInfo;
import eoc.studio.voicecard.facebook.enetities.Publish;
import eoc.studio.voicecard.mailbox.MailboxActivity;
import eoc.studio.voicecard.mainloading.MainLoadingActivity;
import eoc.studio.voicecard.manager.GetRecommendListener;
import eoc.studio.voicecard.manager.GsonRecommend;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.newspaper.NewspaperMainActivity;
import eoc.studio.voicecard.polling.PollingService;
import eoc.studio.voicecard.polling.PollingUtils;
import eoc.studio.voicecard.recommend.RecommendActivity;
import eoc.studio.voicecard.utils.NetworkUtility;

public class MainMenuActivity extends BaseActivity implements OnClickListener
{
	private static final String TAG = "MainMenu";

	private MailboxIconView mailbox;

	private ImageView fbShare;

	private ImageView cardEditor;

	private ImageView newsEditor;

	private ImageView memorialDayEditor;

	private ImageView facebookLogin;

	private ImageView facebookLogout;

	private MemorialDayNotificationView memorialDayNotification;

	private AdvertisementView advertisement;

	private Context context;

	private String recommendBitmapUrl;

	private String recommendName;

	public String PREFS_FILENAME = "MAIN_MENU_SETTING";
	
	public static final String PREFS_KEY_PHONE_NUMBER = "phoneNumber";
	
	public static final String PREFS_KEY_FIRST_RUN_APP = "firstRun";

	private SharedPreferences configPreferences;

	private FacebookManager facebookManager;

	private static boolean isFacebookLogin = false;

	private HttpManager httpManager;

	private static boolean exitApp = false;

	private EditText inputPhoneNumberEditText;
	
	private AlertDialog.Builder inputPhoneNumberAlertDialog ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		Log.d(TAG, "onCreate()");
		context = getApplicationContext();
		configPreferences = getSharedPreferences(PREFS_FILENAME, 0);
		facebookManager = FacebookManager.getInstance(context);
		httpManager = new HttpManager();
		initLayout();
		getRecommendInfo();
		super.onCreate(savedInstanceState);
	}

	private void getRecommendInfo()
	{

		if (NetworkUtility.isOnline(context))
		{
			httpManager.getRecommend(context, new GetRecommendListener()
			{
				@Override
				public void onResult(Boolean isSuccess, ArrayList<GsonRecommend> recommends)
				{

					// Log.e(TAG, "httpManager.getRecommend() isSuccess:" +
					// isSuccess + ",mails:"
					// + recommends.toString());

					if (recommends != null && recommends.size() > 0)
					{
						// Log.e(TAG, "recommends.get(0).getImg():" +
						// recommends.get(0).getImg()
						// + "recommends.get(0).getName():" +
						// recommends.get(0).getName());
						recommendBitmapUrl = recommends.get(0).getImg();
						recommendName = recommends.get(0).getName();
						if (recommendBitmapUrl != null && recommendName != null)
						{

							httpManager.getBitmapFromWeb(context, recommendBitmapUrl,
									new ImageLoader.ImageListener()
									{
										@Override
										public void onResponse(ImageLoader.ImageContainer response,
												boolean isImmediate)
										{

											Log.e(TAG,
													"httpManager.getBitmapFromWeb() isImmediate:"
															+ isImmediate);

											if (response.getBitmap() != null)
											{
												Log.e(TAG, "response.getBitmap() != null");
												advertisement.updateView(
														response.getBitmap().copy(
																Bitmap.Config.ARGB_8888, true),
														recommendName);
											}

										}

										@Override
										public void onErrorResponse(VolleyError e)
										{

											Log.e(TAG, "getBitmapFromWeb() error:" + e.getMessage());
										}
									});
						}
					}

				}

			});
		}
		else
		{
			Log.d(TAG, "Cant getRecommendInfo (because network is not enable)");
		}

	}

	private void updateNewMailBoxCount()
	{

		int mailboxUnReadCount = configPreferences.getInt("mailboxUnReadCount", 0);
		Log.d(TAG, "updateNewMailBoxCount mailboxUnReadCount:" + mailboxUnReadCount);
		mailbox.update(mailboxUnReadCount);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{

		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent()");
		setIntent(intent);
	}

	@Override
	protected void onResume()
	{

		Log.d(TAG, "onResume()");
		updateViewVisibility();
		updateNewMailBoxCount();
		setMemorialDayNotification();

		PollingUtils.startPollingService(this, 1800, PollingService.class, PollingService.ACTION);
		Log.d(TAG, "PollingUtils.startPollingService()");
		
		boolean isFirstRunApp = configPreferences.getBoolean(PREFS_KEY_FIRST_RUN_APP, true);
		if (isFirstRunApp)
		{ 			
			configPreferences.edit().putBoolean(PREFS_KEY_FIRST_RUN_APP, false).commit();
			showInputPhoneDialog();
		}
		
		
		super.onResume();
	}

	public void showInputPhoneDialog()
	{

		
		inputPhoneNumberAlertDialog = new AlertDialog.Builder(this);
		inputPhoneNumberAlertDialog.setTitle(getResources().getString(R.string.input_dialog_title));
		inputPhoneNumberAlertDialog.setMessage(getResources().getString(R.string.input_dialog_message));

		// Set an EditText view to get user input 
		inputPhoneNumberEditText = new EditText(this);
		inputPhoneNumberEditText.setRawInputType(Configuration.KEYBOARD_12KEY);
		inputPhoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);           
		inputPhoneNumberEditText.setKeyListener(new NumberKeyListener()
		{
			protected char[] getAcceptedChars()
			{

				char[] numberChars = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
				return numberChars;
			}

			@Override
			public int getInputType()
			{

				return android.text.InputType.TYPE_CLASS_PHONE;
			}
		});
		
		inputPhoneNumberAlertDialog.setView(inputPhoneNumberEditText);
		inputPhoneNumberAlertDialog.setCancelable(false);  // disable back button
		inputPhoneNumberAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{

				String phoneNumberFromUserInput = inputPhoneNumberEditText.getText().toString();
				Log.d(TAG, "phoneNumberFromUserInput is " + phoneNumberFromUserInput);

				if (phoneNumberFromUserInput.startsWith("0") || phoneNumberFromUserInput.length()<8)
				{
					
					Toast.makeText(context, getResources().getString(R.string.input_dialog_re_show), Toast.LENGTH_SHORT).show();
					showInputPhoneDialog();
				}
				else
				{
					phoneNumberFromUserInput = "+" + phoneNumberFromUserInput;
					Log.d(TAG, "After add plus char to phoneNumberFromUserInput is "
							+ phoneNumberFromUserInput);

					configPreferences.edit().putString(PREFS_KEY_PHONE_NUMBER, phoneNumberFromUserInput).commit();
				}

			}
		});

//		inputPhoneNumberAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//		  public void onClick(DialogInterface dialog, int whichButton) {
//		    // Canceled.
//		  }
//		});
		inputPhoneNumberAlertDialog.show();
		
	}

	private void setMemorialDayNotification()
	{

		Calendar now = Calendar.getInstance();
		// Calendar old = memorialDayNotification.getDate();
		// if (old == null || (now.get(Calendar.DAY_OF_MONTH) !=
		// old.get(Calendar.DAY_OF_MONTH)))
		// {
		memorialDayNotification.setDate(now);
		String event = getMemorialEvent(now);
		Log.d(TAG, "setMemorialDayNotification - event: " + event);
		memorialDayNotification.setEvent(event);
		// }
	}

	private String getMemorialEvent(Calendar calendar)
	{

		ArrayList<Map<String, String>> event = CalendarIntentHelper

		.readCalendarEvent(this,

		(DataProcess.formatDate(String.valueOf(System.currentTimeMillis()),

		"yyyyMMdd") + DataProcess.DEFAULT_EVENT_TIME));

		if (event != null && !event.isEmpty())

		{

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < event.size(); i++)

			{

				builder.append(event

				.get(i)

				.get(CalendarIntentHelper.EVENT_PROJECTION[CalendarIntentHelper.EVENT_TITLE_INDEX]));

				builder.append("\n");

			}
			builder.deleteCharAt(builder.length() - 1);
			return builder.toString();
		}

		else

		{

			// String title = "NO EVENT";

			// System.out.println(title);
			return null;

		}
	}

	@Override
	protected void onPause()
	{

		Log.d(TAG, "onPause()");
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{

		Log.d(TAG, "onDestroy()");
		PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);

		Log.d(TAG, "PollingUtils.stopPollingService()");
		super.onDestroy();
	}

	private void initLayout()
	{

		setContentView(R.layout.activity_mainmenu);
		findViews();
		setListeners();
	} 

	private void findViews()
	{

		fbShare = (ImageView) findViewById(R.id.act_mainmenu_iv_fb_share);
		mailbox = (MailboxIconView) findViewById(R.id.act_mainmenu_mbiv_mailbox);
		cardEditor = (ImageView) findViewById(R.id.act_mainmenu_iv_card_editor);
		newsEditor = (ImageView) findViewById(R.id.act_mainmenu_iv_news_editor);
		memorialDayEditor = (ImageView) findViewById(R.id.act_mainmenu_iv_memorial_day_editor);
		memorialDayNotification = (MemorialDayNotificationView) findViewById(R.id.act_mainmenu_mdnv_memorial_notification);
		advertisement = (AdvertisementView) findViewById(R.id.act_mainmenu_av_advertisement);
		facebookLogin = (ImageView) findViewById(R.id.act_mainmenu_iv_facebook_login);
		facebookLogout = (ImageView) findViewById(R.id.act_mainmenu_iv_facebook_logout);

		updateViewVisibility();
	}

	private void updateViewVisibility()
	{

		isFacebookLogin = facebookManager.isLogin();
		Log.d(TAG, "isFacebookLogin: " + isFacebookLogin + " ,httpManager.getFacebookID(): "
				+ httpManager.getFacebookID());
		if (isFacebookLogin && httpManager.getFacebookID() != null)
		{

			facebookLogin.setVisibility(View.INVISIBLE);
			facebookLogout.setVisibility(View.VISIBLE);

			fbShare.setVisibility(View.VISIBLE);
			mailbox.setVisibility(View.VISIBLE);
			cardEditor.setVisibility(View.VISIBLE);
			newsEditor.setVisibility(View.VISIBLE);
			memorialDayEditor.setVisibility(View.VISIBLE);
		}
		else
		{
			facebookLogin.setVisibility(View.VISIBLE);
			facebookLogout.setVisibility(View.INVISIBLE);
			fbShare.setVisibility(View.INVISIBLE);
			mailbox.setVisibility(View.INVISIBLE);
			cardEditor.setVisibility(View.GONE);
			newsEditor.setVisibility(View.GONE);
			memorialDayEditor.setVisibility(View.GONE);
		}
		facebookLogin.invalidate();
		facebookLogout.invalidate();
		fbShare.invalidate();
		mailbox.invalidate();
		cardEditor.invalidate();
		newsEditor.invalidate();
		memorialDayEditor.invalidate();

	}

	private void setListeners()
	{

		fbShare.setOnClickListener(this);
		mailbox.setOnClickListener(this);
		cardEditor.setOnClickListener(this);
		newsEditor.setOnClickListener(this);
		memorialDayEditor.setOnClickListener(this);
		memorialDayNotification.setOnClickListener(this);
		advertisement.setOnClickListener(this);
		facebookLogin.setOnClickListener(this);
		facebookLogout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{

		if (v == fbShare)
		{
			onFbShareClicked();
		}
		else if (v == mailbox)
		{
			onMailboxClicked();
		}
		else if (v == cardEditor)
		{
			onCardEditorClicked();
		}
		else if (v == newsEditor)
		{
			onNewsEditorClicked();
		}
		else if (v == memorialDayEditor)
		{
			onMemorialDayEditorClicked();
		}
		else if (v == memorialDayNotification)
		{
			onMemorialDayNotificationClicked();
		}
		else if (v == advertisement)
		{
			onAdvertisementClicked();
		}
		else if (v == facebookLogin)
		{
			onFacebookLogInClicked();
		}
		else if (v == facebookLogout)
		{
			onFacebookLogoutClicked();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult() Result Code is - " + resultCode + "");
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed()
	{

		if (exitApp)
			MainMenuActivity.this.finish();
		else
		{
			Toast.makeText(this, getResources().getString(R.string.exit_app_to_home), Toast.LENGTH_SHORT).show();
			exitApp = true;
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{

					exitApp = false;
				}
			}, 3 * 1000);

		}

	}

	private void onFacebookLogoutClicked()
	{

		Log.d(TAG, "Facebook LogOut");
		if (facebookManager != null)
		{
			httpManager.setFacebookID(null);
			facebookManager.logout();
			if (Build.VERSION.SDK_INT >= 11)
			{
				recreate();
			}
			else
			{
				Intent intent = getIntent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

				overridePendingTransition(0, 0);

				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
		}

	}

	private void onFacebookLogInClicked()
	{

		Log.d(TAG, "Facebook LogIn");
		facebookManager.getUserProfile(MainMenuActivity.this,
				facebookManager.new RequestGraphUserCallback()
				{
					@Override
					public void onCompleted(GraphUser user, Response response)
					{

						if (user != null)
						{
							JSONObject userJSON = user.getInnerJSONObject();
							if (userJSON != null)
							{
								UserInfo userInfo = new UserInfo(userJSON);

								Log.d(TAG, "userInfo id is " + userInfo.getId());

								Log.d(TAG, "Go to main loading");

								Intent intent = new Intent(MainMenuActivity.this,
										MainLoadingActivity.class);
								startActivity(intent);
								finish();
							}
						}
						else
						{
							Log.d(TAG, "userInfo id is null ");
						}
					}
				});

	}

	private void onAdvertisementClicked()
	{

		if (NetworkUtility.isOnline(context))
		{
			Log.d(TAG, "go to store");
			Intent intent = new Intent(this, RecommendActivity.class);
			startActivity(intent);
		}
		else
		{
			Log.d(TAG, "Cant go to store (because network is not enable)");
			Toast.makeText(MainMenuActivity.this, getString(R.string.network_connect_fail),
					Toast.LENGTH_LONG).show();
		}

	}

	private void onMemorialDayNotificationClicked()
	{

		Log.d(TAG, "show event detail?");

	}

	private void onMemorialDayEditorClicked()
	{

		Log.d(TAG, "go to memorial day editor");
		Intent intent = new Intent(this, MainCalendarView.class);
		startActivity(intent);
	}

	private void onNewsEditorClicked()
	{

		Log.d(TAG, "go to news editor");
		Intent intent = new Intent(this, NewspaperMainActivity.class);
		startActivity(intent);
	}

	private void onCardEditorClicked()
	{

		Intent intent = new Intent(this, CardCategorySelectorActivity.class);
		startActivity(intent);
	}

	private void onMailboxClicked()
	{

		Log.d(TAG, "go to mailbox");
		Intent intent = new Intent(this, MailboxActivity.class);
		startActivity(intent);
	}

	private void onFbShareClicked()
	{

		Log.d(TAG, "share on facebook");
		FacebookManager facebookManager = FacebookManager.getInstance(context);
		HttpManager httpManager = new HttpManager();
		facebookManager
				.publishUserFeed(
						MainMenuActivity.this,
						new Publish(httpManager.getFacebookID(), getResources().getString(
								R.string.share_app_name), null, getResources().getString(
								R.string.share_caption), getResources().getString(
								R.string.share_description), getResources().getString(
								R.string.share_link)));
	}
}

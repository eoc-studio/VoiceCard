package eoc.studio.voicecard.mainmenu;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.editor.CardCategorySelectorActivity;
import eoc.studio.voicecard.mailbox.MailboxActivity;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.recommend.RecommendActivity;

public class MainMenuActivity extends BaseActivity implements OnClickListener
{
	private static final String TAG = "MainMenu";

	private MailboxIconView mailbox;
	private ImageView fbShare;

	private ImageView cardEditor;
	private ImageView newsEditor;
	private ImageView memorialDayEditor;

	private MemorialDayNotificationView memorialDayNotification;
	private AdvertisementView advertisement;

	private Context context;

	private String recommendName;

	private String PREFS_FILENAME = "MAIN_MENU_SETTING";
	SharedPreferences configPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		context = getApplicationContext();
		configPreferences = getSharedPreferences(PREFS_FILENAME, 0);
		initLayout();
		updateNewMailBoxCount();
		updateAdvertisementInfo();

		super.onCreate(savedInstanceState);
	}

	private void updateAdvertisementInfo()
	{

		String recommendBitmapUrl = configPreferences.getString("recommendBitmapUrl", null);
		recommendName = configPreferences.getString("recommendName", null);

		if (recommendBitmapUrl != null && recommendName != null)
		{
			HttpManager httpManager = new HttpManager();
			httpManager.getBitmapFromWeb(context, recommendBitmapUrl,
					new ImageLoader.ImageListener()
					{
						@Override
						public void onResponse(ImageLoader.ImageContainer response,
								boolean isImmediate)
						{

							Log.e(TAG, "httpManager.getBitmapFromWeb() isImmediate:" + isImmediate);

							if (response.getBitmap() != null)
							{
								Log.e(TAG, "response.getBitmap() != null");
								advertisement.updateView(
										response.getBitmap().copy(Bitmap.Config.ARGB_8888, true),
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

	private void updateNewMailBoxCount()
	{

		int mailboxUnReadCount = configPreferences.getInt("mailboxUnReadCount", 0);
		Log.d(TAG, "updateNewMailBoxCount mailboxUnReadCount:" + mailboxUnReadCount);
		if (mailboxUnReadCount != 0) mailbox.update(mailboxUnReadCount);
	}

	@Override
	protected void onResume()
	{
		setMemorialDayNotification();
		super.onResume();
	}

	private void setMemorialDayNotification()
	{
		Calendar now = Calendar.getInstance();
		Calendar old = memorialDayNotification.getDate();
		if (old == null || (now.get(Calendar.DAY_OF_MONTH) != old.get(Calendar.DAY_OF_MONTH)))
		{
			memorialDayNotification.setDate(now);
			String event = getMemorialEvent(now);
			memorialDayNotification.setEvent(event);
		}
	}

	private String getMemorialEvent(Calendar calendar)
	{
		// TODO get event from Ryan
		switch (calendar.get(Calendar.DAY_OF_WEEK))
		{
		case Calendar.MONDAY:
			return "星期一猴子穿新衣";
		case Calendar.TUESDAY:
			return "星期二猴子肚子餓";
		case Calendar.WEDNESDAY:
			return "星期三猴子去爬山";
		case Calendar.THURSDAY:
			return "星期四猴子去考試";
		case Calendar.FRIDAY:
			return "星期五猴子去跳舞";
		case Calendar.SATURDAY:
			return "星期六猴子去斗六";
		case Calendar.SUNDAY:
		default:
			return null;
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
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
	}

	private void onAdvertisementClicked()
	{
		Log.d(TAG, "go to store");
		Intent intent = new Intent(this, RecommendActivity.class);
		startActivity(intent);

	}

	private void onMemorialDayNotificationClicked()
	{
		Log.d(TAG, "show event detail?");

	}

	private void onMemorialDayEditorClicked()
	{
		Log.d(TAG, "go to memorial day editor");
	}

	private void onNewsEditorClicked()
	{
		Log.d(TAG, "go to news editor");
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
	}
}

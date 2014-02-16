package eoc.studio.voicecard.mainmenu;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.editor.CardCategorySelectorActivity;

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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		initLayout();
		super.onCreate(savedInstanceState);
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
			return "�P���@�U�l��s��";
		case Calendar.TUESDAY:
			return "�P���G�U�l�{�l�j";
		case Calendar.WEDNESDAY:
			return "�P���T�U�l�h���s";
		case Calendar.THURSDAY:
			return "�P���|�U�l�h�Ҹ�";
		case Calendar.FRIDAY:
			return "�P�����U�l�h���R";
		case Calendar.SATURDAY:
			return "�P�����U�l�h�椻";
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
	}

	private void onFbShareClicked()
	{
		Log.d(TAG, "share on facebook");
	}
}

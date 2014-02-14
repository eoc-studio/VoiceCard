package eoc.studio.voicecard.mainmenu;

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

	private MemorialDayNotificationView memorialdayNotification;

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

		super.onResume();
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
		memorialdayNotification = (MemorialDayNotificationView) findViewById(R.id.act_mainmenu_mdnv_memorial_notification);
		advertisement = (AdvertisementView) findViewById(R.id.act_mainmenu_av_advertisement);
	}

	private void setListeners()
	{

		fbShare.setOnClickListener(this);
		mailbox.setOnClickListener(this);
		cardEditor.setOnClickListener(this);
		newsEditor.setOnClickListener(this);
		memorialDayEditor.setOnClickListener(this);
		memorialdayNotification.setOnClickListener(this);
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
		else if (v == memorialdayNotification)
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

package eoc.studio.voicecard.card.editor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import eoc.studio.voicecard.BaseActivity;

public class CardSenderActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND = "card_with_user_data_for_send";

	private static final String TAG = "CardSenderActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getCardFromIntent();
		initLayout();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	private void getCardFromIntent()
	{
		Intent intent = getIntent();
		Log.d(TAG, "CARD: " + intent.getParcelableExtra(EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND).toString());
	}

	private void initLayout()
	{
		// setContentView();
		// findViews();
		// setListener();
	}
}

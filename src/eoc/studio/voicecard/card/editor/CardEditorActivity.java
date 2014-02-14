package eoc.studio.voicecard.card.editor;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.FakeData;

public class CardEditorActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CARD_ID = "card_id";

	private static final String TAG = "CardEditor";

	private ImageView back;
	private ImageView innerPage;

	private static Card card;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getCard();
		initLayout();
		setInnerPage();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void getCard()
	{
		Intent intent = getIntent();
		int cardId = intent.getIntExtra(EXTRA_KEY_CARD_ID, -1);
		if (cardId != -1)
		{
			card = FakeData.getCard(cardId);
			assert card != null;

			Log.d(TAG, "EDIT : " + card.getName());
			Toast.makeText(this, "EDIT: " + card.getName(), Toast.LENGTH_LONG).show();
		}
		else
		{
			throw new RuntimeException("invalid intent, card id " + cardId);
		}
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_card_editor);
		findViews();
		setListener();
	}

	private void findViews()
	{
		back = (ImageView) findViewById(R.id.act_card_editor_iv_back);
		innerPage = (ImageView) findViewById(R.id.act_card_editor_iv_card_inner_page);

	}

	private void setListener()
	{
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}

		});

	}

	private void setInnerPage()
	{
		innerPage.setImageResource(card.getImage3dOpenResId());
	}
}

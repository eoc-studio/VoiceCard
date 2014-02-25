package eoc.studio.voicecard.card.editor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.animation.FlipView;
import eoc.studio.voicecard.animation.FlipView.FlipListener;
import eoc.studio.voicecard.card.Card;

public class CardSenderActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND = "card_with_user_data_for_send";

	private static final String TAG = "CardSenderActivity";

	private Card card;

	private FlipView flipShadow;

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
		card = intent.getParcelableExtra(EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND);
		Log.d(TAG, "Card to send : " + card);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_card_sender);
		initFlipShadow();
		findViews();
		// setListener();
	}

	private void findViews()
	{
		FrameLayout frame = (FrameLayout) findViewById(R.id.act_card_sender_flyt_card);
		frame.addView(flipShadow);

	}

	private void initFlipShadow()
	{
		flipShadow = new FlipView(this, 476, 560 / 2, -45f, 0f, 560 / 4);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(476 / 2, 560 / 2);

		ImageView front = new ImageView(this);
		front.setLayoutParams(params);
		front.setScaleType(ScaleType.FIT_XY);
		front.setImageResource(card.getImageCoverResId());
		ImageView back = new ImageView(this);
		back.setLayoutParams(params);
		back.setScaleType(ScaleType.FIT_XY);
		back.setImageResource(card.getImageInnerLeftResId());
		ImageView inner = new ImageView(this);
		inner.setLayoutParams(params);
		inner.setScaleType(ScaleType.FIT_XY);
		inner.setImageResource(card.getImageInnerRightResId());

		flipShadow.setFrontPage(front);
		flipShadow.setBackPage(back);
		flipShadow.setInnerPage(inner);
		flipShadow.setLockAfterOpened(true);
		flipShadow.setFlipListener(new FlipListener()
		{
			@Override
			public void onOpened()
			{

			}

			@Override
			public void onClosed()
			{

			}

			@Override
			public void onStartOpening()
			{

			}

			@Override
			public void onStartClosing()
			{

			}
		});
	}
}

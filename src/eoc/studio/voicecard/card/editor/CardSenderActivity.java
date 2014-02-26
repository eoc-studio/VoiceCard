package eoc.studio.voicecard.card.editor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
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

	private View back;
	private View receiverChooser;
	private View send;
	private TextView receiverCounter;

	private FrameLayout cardAnimationWrapper;
	private HorizontalScrollView scrollView;
	private FlipView flipView;
	private FrameLayout flipViewWrapper;
	private ImageView shadowOpen;
	private ImageView shadowClose;

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
		findViews();
		generateShadow();
		initFlipAndShadow();
		setListeners();
	}

	private void findViews()
	{
		back = findViewById(R.id.act_card_sender_iv_back);
		receiverChooser = findViewById(R.id.act_card_sender_iv_receiver_chooser);
		send = findViewById(R.id.act_card_sender_iv_send);
		receiverCounter = (TextView) findViewById(R.id.act_card_sender_tv_receiver_counter);

		cardAnimationWrapper = (FrameLayout) findViewById(R.id.act_card_sender_flyt_card_wrapper);
		flipViewWrapper = (FrameLayout) findViewById(R.id.glb_card_animation_flyt_card_wrapper);
		scrollView = (HorizontalScrollView) findViewById(R.id.glb_card_animation_hsv_root);
		shadowOpen = (ImageView) findViewById(R.id.glb_card_animation_iv_card_open_shadow);
		shadowClose = (ImageView) findViewById(R.id.glb_card_animation_iv_card_close_shadow);
	}

	private void generateShadow()
	{
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), card.getImage3dOpenResId());

		Bitmap halfShadow = Bitmap.createBitmap(bmp, bmp.getWidth() / 2 - 10, 0,
				bmp.getWidth() / 2, bmp.getHeight());
		shadowClose.setImageBitmap(halfShadow);
		bmp.recycle();

		shadowOpen.setImageResource(card.getImage3dOpenResId());
	}

	private void initFlipAndShadow()
	{
		cardAnimationWrapper.bringToFront();
		
		final int cardPageWidth = (int) (getResources().getDimensionPixelSize(
				R.dimen.card_open_width) / 2.f * 0.955f);
		final int cardPageHeight = (int) (getResources().getDimensionPixelSize(R.dimen.card_height) * 1.42f);

		shadowOpen.setVisibility(View.INVISIBLE);
		shadowClose.setVisibility(View.VISIBLE);

		flipView = new FlipView(this, cardPageWidth * 2, cardPageHeight, -10f, 0f,
				cardPageWidth * 0.5f);
		flipViewWrapper.addView(flipView);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
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

		flipView.setFrontPage(front);
		flipView.setBackPage(back);
		flipView.setInnerPage(inner);
		flipView.setDuration(300);
//		flipView.setLockAfterOpened(true);
		flipView.setFlipListener(new FlipListener()
		{
			@Override
			public void onOpened()
			{
				shadowOpen.setVisibility(View.VISIBLE);
				shadowClose.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onClosed()
			{
			}

			@Override
			public void onStartOpening()
			{
				scrollView.postDelayed(new Runnable()
				{
					public void run()
					{
						scrollView.smoothScrollTo(0, 0);
					}
				}, 200L);
			}

			@Override
			public void onStartClosing()
			{
				shadowOpen.setVisibility(View.INVISIBLE);
				shadowClose.setVisibility(View.VISIBLE);
				scrollView.postDelayed(new Runnable()
				{
					public void run()
					{
						scrollView.smoothScrollTo(cardPageWidth * 2, 0);
					}
				}, 200L);
			}
		});

		scrollView.postDelayed(new Runnable()
		{
			public void run()
			{
				scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 100L);
	}

	private void setListeners()
	{
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}

		});

		receiverChooser.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startReceiverChooser();
			}

		});
	}

	private void startReceiverChooser()
	{

	}

	private void onReceiverChooserResult()
	{

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
	}
}

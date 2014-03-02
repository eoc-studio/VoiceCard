package eoc.studio.voicecard.card.editor;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.animation.FlipView;
import eoc.studio.voicecard.animation.FlipView.FlipListener;
import eoc.studio.voicecard.card.Card;

public class CardSenderActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND = "card_with_user_data_for_send";
	public static final String EXTRA_KEY_CARD_LEFT_SCREENSHOT_FILEPATH = "card_left_screenshot_filepath";
	public static final String EXTRA_KEY_CARD_RIGHT_SCREENSHOT_FILEPATH = "card_right_screenshot_filepath";
	public static final String EXTRA_KEY_CARD_VOICE_DURATION_TEXT = "card_voice_duraiton";

	private static final String TAG = "CardSenderActivity";

	private Card card;
	private String leftScreenshotFilePath;
	private String rightScreenshotFilePath;
	private String voiceDurationText;

	private View back;
	private View receiverPicker;
	private View send;
	private TextView receiverCounter;

	private View rightBlock; // a workaround in order to push
								// cardWrapper to the middle at
								// beginning
	private FrameLayout cardWrapper;
	private HorizontalScrollView animationScrollView;
	private FlipView flipView;
	private FrameLayout flipViewWrapper;
	private ImageView shadowOpen;
	private ImageView shadowClose;

	private HorizontalScrollView cardScrollView;
	private ImageView cardInnerBackground;
	private ImageView cardImageView;
	private ImageView cardVoiceControl;
	private TextView cardVoiceText;
	private TextView cardTextView;
	private ImageView cardSignatureView;

	private MediaPlayer mediaPlayer;

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
		leftScreenshotFilePath = intent.getStringExtra(EXTRA_KEY_CARD_LEFT_SCREENSHOT_FILEPATH);
		rightScreenshotFilePath = intent.getStringExtra(EXTRA_KEY_CARD_RIGHT_SCREENSHOT_FILEPATH);
		voiceDurationText = intent.getStringExtra(EXTRA_KEY_CARD_VOICE_DURATION_TEXT);
		Log.d(TAG, "Card to send : " + card);
		Log.d(TAG, "leftScreenshotFilePath : " + leftScreenshotFilePath);
		Log.d(TAG, "rightScreenshotFilePath : " + rightScreenshotFilePath);
		Log.d(TAG, "voiceDurationText : " + voiceDurationText);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_card_sender);
		findViews();
		initCardView();
		generateShadow();
		initFlipAndShadow();
		setListeners();
	}

	private void findViews()
	{
		back = findViewById(R.id.act_card_sender_iv_back);
		receiverPicker = findViewById(R.id.act_card_sender_iv_receiver_chooser);
		send = findViewById(R.id.act_card_sender_iv_send);
		receiverCounter = (TextView) findViewById(R.id.act_card_sender_tv_receiver_counter);

		rightBlock = findViewById(R.id.act_card_sender_v_right_block);
		cardWrapper = (FrameLayout) findViewById(R.id.act_card_sender_flyt_card_wrapper);
		flipViewWrapper = (FrameLayout) findViewById(R.id.glb_card_animation_flyt_card_wrapper);
		animationScrollView = (HorizontalScrollView) findViewById(R.id.glb_card_animation_hsv_root);
		shadowOpen = (ImageView) findViewById(R.id.glb_card_animation_iv_card_open_shadow);
		shadowClose = (ImageView) findViewById(R.id.glb_card_animation_iv_card_close_shadow);

		cardScrollView = (HorizontalScrollView) findViewById(R.id.act_card_sender_hsv_card_scroll_view);
		cardInnerBackground = (ImageView) findViewById(R.id.act_card_sender_iv_card_inner_page);
		cardImageView = (ImageView) findViewById(R.id.glb_card_iv_editable_image);
		cardVoiceControl = (ImageView) findViewById(R.id.glb_card_iv_editable_voice_play_icon);
		cardVoiceText = (TextView) findViewById(R.id.glb_card_tv_editable_voice_play_text);
		cardTextView = (TextView) findViewById(R.id.glb_card_tv_editable_text);
		cardSignatureView = (ImageView) findViewById(R.id.glb_card_iv_editable_signature_image);

		Log.d(TAG, "hide tips");
		findViewById(R.id.glb_card_tv_editable_image_tip).setVisibility(View.INVISIBLE);
		findViewById(R.id.glb_card_tv_editable_signature_tip).setVisibility(View.INVISIBLE);
		findViewById(R.id.glb_card_tv_editable_text_tip).setVisibility(View.INVISIBLE);
		findViewById(R.id.glb_card_tv_editable_voice_tip).setVisibility(View.INVISIBLE);
	}

	private void initCardView()
	{
		cardInnerBackground.setBackgroundResource(card.getImage3dOpenResId());
		cardImageView.setImageURI(card.getImage());
		cardVoiceText.setText(voiceDurationText);
		cardTextView.setText(card.getMessage());
		cardTextView.setTextColor(card.getMessageTextColor());
		cardTextView
				.setTextSize(CardEditorActivity.getTextSizeByType(card.getMessageTextSizeType()));
		cardSignatureView.setImageURI(card.getSignDraftImage());

		findViewById(R.id.glb_card_llyt_editable_voice).setVisibility(View.VISIBLE);
		cardTextView.setVisibility(View.VISIBLE);
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
		cardWrapper.bringToFront();
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
		// back.setImageResource(card.getImageInnerLeftResId());
		back.setImageURI(Uri.fromFile(new File(leftScreenshotFilePath)));
		ImageView inner = new ImageView(this);
		inner.setLayoutParams(params);
		inner.setScaleType(ScaleType.FIT_XY);
		// inner.setImageResource(card.getImageInnerRightResId());
		inner.setImageURI(Uri.fromFile(new File(rightScreenshotFilePath)));

		flipView.setFrontPage(front);
		flipView.setBackPage(back);
		flipView.setInnerPage(inner);
		flipView.setDuration(300);
		flipView.setLockAfterOpened(true);
		flipView.setTouchFlipEnabled(false);
		flipView.setFlipListener(new FlipListener()
		{
			@Override
			public void onOpened()
			{
				// shadowOpen.setVisibility(View.VISIBLE);
				// shadowClose.setVisibility(View.INVISIBLE);

				flipView.setVisibility(View.INVISIBLE);
				animationScrollView.setVisibility(View.GONE);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardWrapper
						.getLayoutParams();
				params.setMargins(0, 0, 0, 0);
				cardWrapper.setLayoutParams(params);
				cardScrollView.setVisibility(View.VISIBLE);
				cardWrapper.bringToFront();
				cardScrollView.bringToFront();
			}

			@Override
			public void onClosed()
			{
			}

			@Override
			public void onStartOpening()
			{
				shadowClose.setVisibility(View.INVISIBLE);
				rightBlock.setVisibility(View.GONE);
				animationScrollView.postDelayed(new Runnable()
				{
					public void run()
					{
						animationScrollView.smoothScrollTo(0, 0);
					}
				}, 200L);
			}

			@Override
			public void onStartClosing()
			{
				shadowOpen.setVisibility(View.INVISIBLE);
				shadowClose.setVisibility(View.VISIBLE);
				animationScrollView.postDelayed(new Runnable()
				{
					public void run()
					{
						animationScrollView.smoothScrollTo(cardPageWidth * 2, 0);
					}
				}, 200L);
			}
		});

		animationScrollView.postDelayed(new Runnable()
		{
			public void run()
			{
				animationScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 100L);

		animationScrollView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (!flipView.isOpened())
				{
					flipView.requestFlip();
					return true;
				}
				return false;
			}

		});
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

		receiverPicker.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startReceiverPicker();
			}

		});

		send.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				sendCard();
			}

		});
	}

	private void startReceiverPicker()
	{
		// TODO ask user: from contact or facebook
	}

	private void onReceiverPickerResult()
	{

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void sendCard()
	{
		// TODO call HttpManager postMail()
	}
}

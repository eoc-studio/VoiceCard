package eoc.studio.voicecard.card.viewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookException;

import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.animation.FlipView;
import eoc.studio.voicecard.animation.FlipView.FlipListener;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.Constant;
import eoc.studio.voicecard.card.editor.CardCategorySelectorActivity;
import eoc.studio.voicecard.card.editor.CardEditorActivity;
import eoc.studio.voicecard.facebook.FacebookManager;
import eoc.studio.voicecard.mailbox.Mail;
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import eoc.studio.voicecard.utils.FileUtility;

public class CardViewerActivity extends BaseActivity
{
	public static final String EXTRA_KEY_MODE = "card_viewer_mode";
	public static final String MODE_VIEWER = "mode_viewer";
	public static final String MODE_SENDER = "mode_sender";

	public static final String EXTRA_KEY_MAIL = "mail_to_view";
	public static final String EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND = "card_with_user_data_for_send";
	public static final String EXTRA_KEY_CARD_LEFT_SCREENSHOT_FILEPATH = "card_left_screenshot_filepath";
	public static final String EXTRA_KEY_CARD_RIGHT_SCREENSHOT_FILEPATH = "card_right_screenshot_filepath";
	public static final String EXTRA_KEY_CARD_VOICE_DURATION_TEXT = "card_voice_duraiton";

	private static final String TAG = "CardViewerActivity";

	private FacebookManager facebookManager;
	private Mail mail;
	private Bitmap leftScreenshotBitmap;
	private Bitmap rightScreenshotBitmap;

	private Card card;
	private String sendBackId;
	private String leftScreenshotFilePath;
	private String rightScreenshotFilePath;
	private String voiceDurationText;

	private View back;
	private ImageView sendFacebook;
	private ImageView sendContact;
	private TextView mailInfo;
	private ImageView backToMailbox;
	private ImageView sendCardBack;

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
	private AudioMessageView cardVoice;
	private TextView cardTextView;
	private ImageView cardSignatureView;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		facebookManager = FacebookManager.getInstance(this);

		if (isSenderMode())
		{
			initSendBackId();
			initSenderModeLayout();
			getCardFromSenderModeIntent();
			setupSenderModeWithCardData();
		}
		else
		{
			initViewerModeLayout();
			getCardFromViewerModeIntent();
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy()
	{
		if (leftScreenshotBitmap != null && !leftScreenshotBitmap.isRecycled())
		{
			leftScreenshotBitmap.recycle();
		}
		if (rightScreenshotBitmap != null && !rightScreenshotBitmap.isRecycled())
		{
			rightScreenshotBitmap.recycle();
		}
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

	@Override
	public void onBackPressed()
	{
		finish();
		super.onBackPressed();
	}

	private boolean isSenderMode()
	{
		Intent intent = getIntent();
		String mode = intent.getStringExtra(EXTRA_KEY_MODE);
		Log.d(TAG, "mode: " + mode);
		return MODE_SENDER.equals(mode);
	}

	private void getCardFromSenderModeIntent()
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

	private void getCardFromViewerModeIntent()
	{
		Intent intent = getIntent();
		progressDialog = ProgressDialog.show(this, getString(R.string.processing),
				getString(R.string.please_wait), true, false);
		mail = intent.getParcelableExtra(EXTRA_KEY_MAIL);

		Log.d(TAG, "getCardFromViewerModeIntent : " + mail.toString());
		setMailInfoView();

		new Thread("getCardFromMail")
		{
			@Override
			public void run()
			{
				Log.d(TAG, "start getCardFromMail");
				card = Card.getCardFromMail(CardViewerActivity.this, mail);
				Log.d(TAG, "Card to view : " + card);

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						setupViewerModeWithCardData();
					}
				});
				progressDialog.dismiss();
			}
		}.start();
	}

	private void initSendBackId()
	{
		Intent intent = getIntent();
		sendBackId = intent.getStringExtra(Constant.EXTRA_KEY_SENDBACK_ID);
		if (sendBackId != null)
		{
			Log.d(TAG, "We are going to SEND BACK TO " + sendBackId);
		}
	}

	private void initSenderModeLayout()
	{
		setContentView(R.layout.activity_card_viewer);
		setViewsVisibilityForSenderMode();
		findViews();
		hideSendButtonAccordingToSendBackId();

	}

	private void initViewerModeLayout()
	{
		setContentView(R.layout.activity_card_viewer);
		setViewsVisibilityForViewerMode();
		findViews();
	}

	private void setupSenderModeWithCardData()
	{
		initCardView();
		generateShadow();
		initFlipAndShadow();
		setListenersForSenderMode();
	}

	private void setupViewerModeWithCardData()
	{
		initCardView();
		generateScreenshotBitmap();
		generateShadow();
		initFlipAndShadow();
		setListenersForViewerMode();
	}

	private void hideSendButtonAccordingToSendBackId()
	{
		if (sendBackId != null)
		{
			if (sendBackId.startsWith("0") && sendBackId.length() == 10)
			{
				Log.d(TAG, "hide Send Facebook button");
				sendFacebook.setVisibility(View.GONE);
			}
			else
			{
				Log.d(TAG, "hide Send Contact button");
				sendContact.setVisibility(View.GONE);
			}
		}
	}

	private void setMailInfoView()
	{
		mailInfo.setText(mail.getSendedTime() + " FROM : " + mail.getSendedFromName());
	}

	private void setViewsVisibilityForSenderMode()
	{
		findViewById(R.id.act_card_viewer_rlyt_header_for_sender).setVisibility(View.VISIBLE);
		findViewById(R.id.act_card_viewer_rlyt_header_for_viewer).setVisibility(View.INVISIBLE);

		findViewById(R.id.act_card_viewer_iv_tip_for_sender).setVisibility(View.VISIBLE);
		findViewById(R.id.act_card_viewer_tv_mail_info_for_viewer).setVisibility(View.INVISIBLE);

		findViewById(R.id.act_card_viewer_llyt_footer_for_sender).setVisibility(View.VISIBLE);
		findViewById(R.id.act_card_viewer_llyt_footer_for_viewer).setVisibility(View.INVISIBLE);
	}

	private void setViewsVisibilityForViewerMode()
	{
		findViewById(R.id.act_card_viewer_rlyt_header_for_sender).setVisibility(View.INVISIBLE);
		findViewById(R.id.act_card_viewer_rlyt_header_for_viewer).setVisibility(View.VISIBLE);

		findViewById(R.id.act_card_viewer_iv_tip_for_sender).setVisibility(View.INVISIBLE);
		findViewById(R.id.act_card_viewer_tv_mail_info_for_viewer).setVisibility(View.VISIBLE);

		findViewById(R.id.act_card_viewer_llyt_footer_for_sender).setVisibility(View.INVISIBLE);
		findViewById(R.id.act_card_viewer_llyt_footer_for_viewer).setVisibility(View.VISIBLE);
	}

	private void findViews()
	{
		back = findViewById(R.id.act_card_viewer_iv_back);
		sendFacebook = (ImageView) findViewById(R.id.act_card_viewer_iv_send_fb);
		sendContact = (ImageView) findViewById(R.id.act_card_viewer_iv_send_contact);

		mailInfo = (TextView) findViewById(R.id.act_card_viewer_tv_mail_info_for_viewer);
		backToMailbox = (ImageView) findViewById(R.id.act_card_viewer_iv_back_to_mailbox);
		sendCardBack = (ImageView) findViewById(R.id.act_card_viewer_iv_send_card_back);

		rightBlock = findViewById(R.id.act_card_viewer_v_right_block);
		cardWrapper = (FrameLayout) findViewById(R.id.act_card_viewer_flyt_card_wrapper);
		flipViewWrapper = (FrameLayout) findViewById(R.id.glb_card_animation_flyt_card_wrapper);
		animationScrollView = (HorizontalScrollView) findViewById(R.id.glb_card_animation_hsv_root);
		shadowOpen = (ImageView) findViewById(R.id.glb_card_animation_iv_card_open_shadow);
		shadowClose = (ImageView) findViewById(R.id.glb_card_animation_iv_card_close_shadow);

		cardScrollView = (HorizontalScrollView) findViewById(R.id.act_card_viewer_hsv_card_scroll_view);
		cardInnerBackground = (ImageView) findViewById(R.id.act_card_viewer_iv_card_inner_page);
		cardImageView = (ImageView) findViewById(R.id.glb_card_iv_editable_image);
		cardVoice = (AudioMessageView) findViewById(R.id.glb_card_amv_editable_voice);
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
		Bitmap img3dOpenBitmap = FileUtility.getBitmapFromPath(card.getImage3dOpenPath());

		FileUtility.setImageViewBackgroundWithBitmap(cardInnerBackground, img3dOpenBitmap);

		cardImageView.setImageURI(card.getImage());
		cardTextView.setText(card.getMessage());
		cardTextView.setTextColor(card.getMessageTextColor());
		cardTextView
				.setTextSize(CardEditorActivity.getTextSizeByType(card.getMessageTextSizeType()));
		cardTextView.setVisibility(View.VISIBLE);

		cardSignatureView.setImageURI(card.getSignDraftImage());

		Uri source = card.getSound();
		if (source != null)
		{
			cardVoice.setDurationText(voiceDurationText);
			cardVoice.setVisibility(View.VISIBLE);
			try
			{
				cardVoice.setPlayerSourcceAndPrepare(new MediaPlayer(), card.getSound(), false);
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void generateShadow()
	{
		// Bitmap bmp = BitmapFactory.decodeResource(getResources(),
		// card.getImage3dOpenResId());
		Bitmap bmp = FileUtility.getBitmapFromPath(card.getImage3dOpenPath());

		Bitmap halfShadow = Bitmap.createBitmap(bmp, bmp.getWidth() / 2 - 10, 0,
				bmp.getWidth() / 2, bmp.getHeight());
		shadowClose.setImageBitmap(halfShadow);
		bmp.recycle();

		// shadowOpen.setImageResource(card.getImage3dOpenResId());

		Bitmap img3dOpenBitmap = FileUtility.getBitmapFromPath(card.getImage3dOpenPath());
		FileUtility.setImageViewWithBitmap(shadowOpen, img3dOpenBitmap);
	}

	private void generateScreenshotBitmap()
	{
		View wholeCard = findViewById(R.id.act_card_viewer_rlyt_card);
		wholeCard.setDrawingCacheEnabled(true);

		// this is the important code :)
		// Without it the view will have a dimension of 0,0
		// and the bitmap will be null
		wholeCard.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		wholeCard.layout(0, 0, wholeCard.getMeasuredWidth(), wholeCard.getMeasuredHeight());
		wholeCard.buildDrawingCache(true);

		Bitmap wholeScreenshot = wholeCard.getDrawingCache();
		Log.d(TAG, "wholeScreenshot " + wholeScreenshot);
		int pageWidth = wholeScreenshot.getWidth() / 2;
		int pageHeight = wholeScreenshot.getHeight();
		leftScreenshotBitmap = Bitmap.createBitmap(wholeScreenshot, 0, 0, pageWidth, pageHeight);
		rightScreenshotBitmap = Bitmap.createBitmap(wholeScreenshot, pageWidth, 0, pageWidth,
				pageHeight);
		wholeCard.setDrawingCacheEnabled(false);
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
		Bitmap imgCoverBitmap = FileUtility.getBitmapFromPath(card.getImageCoverPath());
		FileUtility.setImageViewWithBitmap(front, imgCoverBitmap);

		ImageView back = new ImageView(this);
		back.setLayoutParams(params);
		back.setScaleType(ScaleType.FIT_XY);
		if (leftScreenshotBitmap != null)
		{
			back.setImageBitmap(leftScreenshotBitmap);
		}
		else
		{
			back.setImageURI(Uri.fromFile(new File(leftScreenshotFilePath)));
		}
		ImageView inner = new ImageView(this);
		inner.setLayoutParams(params);
		inner.setScaleType(ScaleType.FIT_XY);
		if (rightScreenshotBitmap != null)
		{
			inner.setImageBitmap(rightScreenshotBitmap);
		}
		else
		{
			inner.setImageURI(Uri.fromFile(new File(rightScreenshotFilePath)));
		}

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
				cardWrapper.setPadding(0, 0, 0, 0);
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

	private void setListenersForSenderMode()
	{
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}

		});

		sendFacebook.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (sendBackId == null)
				{
					startFacebookSender();
				}
				else
				{
					sendBack();
				}
			}

		});

		sendContact.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (sendBackId == null)
				{
					sendContactSender();
				}
				else
				{
					sendBack();
				}
			}

		});
	}

	private void setListenersForViewerMode()
	{
		backToMailbox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		sendCardBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CardViewerActivity.this,
						CardCategorySelectorActivity.class);
				intent.putExtra(Constant.EXTRA_KEY_SENDBACK_ID, mail.getSendedFrom());
				startActivity(intent);
				finish();
			}
		});
	}

	private void startFacebookSender()
	{
		Log.d(TAG, "startFacebookSender");
		facebookManager.inviteFriend(this, null, card, facebookManager.new InviteListener()
		{
			@Override
			public void onComplete(Bundle values, FacebookException error)
			{
				if (error != null)
				{
					Log.d(TAG, "Invite had error is " + error.getMessage());
				}
				else
				{
					Log.d(TAG, "Invite no error, send card to server");
					facebookManager.sendCardtoServer(facebookManager.fetchInvitedFriends(values));

					goBackToMainMenuAndFinish();
				}
			}
		});
	}

	private void sendContactSender()
	{
		// TODO to Ryan
	}

	private void sendBack()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(sendBackId);
		Log.d(TAG, "sendBack to " + sendBackId);
		Log.d(TAG, "sendBack card: " + card);
		facebookManager.sendCardtoServer(list,card);

		goBackToMainMenuAndFinish();
	}

	private void goBackToMainMenuAndFinish()
	{
		Log.d(TAG, "back to MainMenu and finish");
		Intent intent = new Intent(CardViewerActivity.this, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

}

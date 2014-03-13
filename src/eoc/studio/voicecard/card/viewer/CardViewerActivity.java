package eoc.studio.voicecard.card.viewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private LinearLayout shadowClose;
	private ImageView cardInnerSecondPage;

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case eoc.studio.voicecard.contact.DataProcess.PROCESS_SELECTION_FRIEND:
				onContactSelectorResult(data);
				break;
			}
		}
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
		initFlipAndShadow();
		setListenersForSenderMode();
	}

	private void setupViewerModeWithCardData()
	{
		initCardView();
		generateScreenshotBitmap();
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
		flipViewWrapper = (FrameLayout) findViewById(R.id.glb_card_animation_flyt_card_flipview_wrapper);
		animationScrollView = (HorizontalScrollView) findViewById(R.id.glb_card_animation_hsv_root);
		shadowOpen = (ImageView) findViewById(R.id.glb_card_animation_iv_card_open_shadow);
		shadowClose = (LinearLayout) findViewById(R.id.glb_card_animation_llyt_card_close_shadow);
		cardInnerSecondPage = (ImageView) findViewById(R.id.glb_card_animation_iv_card_inner_second_page);

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
		Bitmap img2dSecondPageBitmap = FileUtility.getBitmapFromPath(card.getImageInnerRightPath());
		FileUtility.setImageViewBackgroundWithBitmap(cardInnerSecondPage, img2dSecondPageBitmap);

		cardWrapper.bringToFront();
		final int cardOpenWidth = (int) getResources().getDimensionPixelOffset(
				R.dimen.card_open_page_width);
		final int cardOpenHeight = (int) getResources().getDimensionPixelSize(
				R.dimen.card_open_page_height);
		final int cardFlipViewHeight = (int) (cardOpenHeight * 7.7f / 5.f);
//		Toast.makeText(this,
//				"cardOpenHeight[" + cardOpenHeight + "]  height[" + cardFlipViewHeight + "]",
//				Toast.LENGTH_LONG).show();

		shadowOpen.setVisibility(View.INVISIBLE);
		shadowClose.setVisibility(View.VISIBLE);

		flipView = new FlipView(this, cardOpenWidth, cardFlipViewHeight, -10f, 0f,
				cardFlipViewHeight / 2.f);
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
				shadowOpen.setVisibility(View.VISIBLE);
				shadowClose.setVisibility(View.INVISIBLE);

				flipView.setVisibility(View.INVISIBLE);
				animationScrollView.setVisibility(View.GONE);
				cardScrollView.setVisibility(View.VISIBLE);
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
						animationScrollView.smoothScrollTo(cardOpenWidth * 2, 0);
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
					startFacebookInviter();
				}
				else
				{
					sendBack();
					goBackToMainMenuAndFinish();
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
					startContactSelector();
				}
				else
				{
					sendBack();
					goBackToMainMenuAndFinish();
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

	private void startFacebookInviter()
	{
		Log.d(TAG, "startFacebookSender");
		facebookManager.inviteFriend(this, null, card, facebookManager.new InviteListener()
		{
			@Override
			public void onComplete(Bundle values, FacebookException error)
			{
				onFacebookInviterResult(values, error);
			}

		});
	}

	private void onFacebookInviterResult(Bundle values, FacebookException error)
	{
		if (error != null)
		{
			Log.d(TAG, "Invite had error is " + error.getMessage());
		}
		else
		{
			Log.d(TAG, "Invite no error, send card to server");
			sendCardToServer(facebookManager.fetchInvitedFriends(values));
			goBackToMainMenuAndFinish();
		}
	}

	private void startContactSelector()
	{
		Intent SendContactSender = new Intent();
		SendContactSender.setClass(this, eoc.studio.voicecard.contact.ContactActivity.class);
		startActivityForResult(SendContactSender,
				eoc.studio.voicecard.contact.DataProcess.PROCESS_SELECTION_FRIEND);
		overridePendingTransition(0, 0);
	}

	private void onContactSelectorResult(Intent data)
	{
		if (data == null)
		{
			Log.d(TAG, "onContactSelectorResult - NO DATA");
			return;
		}
		Bundle extras = data.getExtras();
		if (extras == null)
		{
			Log.d(TAG, "onContactSelectorResult - NO BUNDLE");
			return;
		}
		ArrayList list = extras
				.getParcelableArrayList(eoc.studio.voicecard.contact.DataProcess.PROCESS_SELECTION_FRIEND_LIST);
		List<Map<String, Object>> lists = (List<Map<String, Object>>) list.get(0);
		if (lists == null || lists.isEmpty())
		{
			Log.d(TAG, "onContactSelectorResult - lists is null or empty");
		}
		else
		{
			Log.d(TAG, "onContactSelectorResult - lists size: " + lists.size());
			ArrayList<String> phoneList = new ArrayList<String>();
			Object phoneNum;
			for (Map<String, Object> map : lists)
			{
				phoneNum = map.get(eoc.studio.voicecard.contact.DataProcess.PHONE_NUMBER_INDEX);
				Log.d(TAG, "onContactSelectorResult - TEL: " + phoneNum);
				String phoneString = phoneNum.toString().replace(" ", "");
				Log.d(TAG, "onContactSelectorResult - (after remove white space)TEL: " + phoneString);
				
				phoneString = phoneNum.toString().replace("-", "");
				Log.d(TAG, "onContactSelectorResult - (after remove minus sign)TEL: " + phoneString);
				phoneList.add(phoneString);
			}

			if (!phoneList.isEmpty())
			{
				sendCardToServer(phoneList);
				goBackToMainMenuAndFinish();
			}
			else
			{
				Log.e(TAG, "onContactSelectorResult - phoneList empty");
			}
		}

		// for (int i = 0; i < lists.size(); i++)
		// {
		// Log.d(TAG,
		// "NAME: "
		// + lists.get(i)
		// .get(eoc.studio.voicecard.contact.DataProcess.USER_NAME_INDEX)
		// .toString());
		// Log.d(TAG,
		// "TEL: "
		// + lists.get(i)
		// .get(eoc.studio.voicecard.contact.DataProcess.PHONE_NUMBER_INDEX)
		// .toString());
		// }

	}

	private void sendBack()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(sendBackId);
		Log.d(TAG, "sendBack to " + sendBackId);
		sendCardToServer(list);
	}

	private void sendCardToServer(ArrayList<String> receivers)
	{
		Log.d(TAG, "!! sendCardToServer !!");
		Log.d(TAG, "card: " + card);
		Log.d(TAG, "receivers " + receivers.size());
		facebookManager.sendCardtoServer(receivers, card);
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

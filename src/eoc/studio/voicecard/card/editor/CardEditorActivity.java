package eoc.studio.voicecard.card.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.audio.AudioRecorderActivity;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.CardDraft;
import eoc.studio.voicecard.card.Constant;
import eoc.studio.voicecard.card.FakeData;
import eoc.studio.voicecard.card.viewer.AudioMessageView;
import eoc.studio.voicecard.card.viewer.CardViewerActivity;
import eoc.studio.voicecard.card.database.CardDatabaseHelper;
import eoc.studio.voicecard.manufacture.EditSignatureActivity;
import eoc.studio.voicecard.menu.OpenDraft;
import eoc.studio.voicecard.menu.SaveDraft;
import eoc.studio.voicecard.utils.FileUtility;

public class CardEditorActivity extends BaseActivity
{
	private static final String TAG = "CardEditor";

	private static final int REQ_PICK_IMAGE = 1;

	private static final int REQ_CROP_IMAGE = 2;

	private static final int REQ_RECORD_VOICE = 3;

	private static final int REQ_EDIT_TEXT = 4;

	private static final int REQ_EDIT_SIGNATURE = 5;

	public static final String EXTRA_KEY_CARD_ID = "card_id";

	public static final String EXTRA_KEY_CARD_DRAFT = "card_draft";

	private static final String EXTRA_KEY_USER_IMAGE = "user_image";

	private static final String EXTRA_KEY_USER_IMAGE_BITMAP = "user_image_bitmap";

	private static final String EXTRA_KEY_USER_VOICE = "user_voice";

	private static final String EXTRA_KEY_USER_VOICE_DURATION = "user_voice_duration";

	private static final String EXTRA_KEY_USER_TEXT_CONTENT = "user_text_content";

	private static final String EXTRA_KEY_USER_TEXT_SIZE_TYPE = "user_text_size_type";

	private static final String EXTRA_KEY_USER_TEXT_COLOR = "user_text_color";

	private static final String EXTRA_KEY_USER_SIGN_HANDWRITHING = "user_sign_handwriting";

	private static final String EXTRA_KEY_USER_SIGN_POSITION_INFO = "user_sign_position_info";

	private static final String EXTRA_KEY_USER_SIGN_DRAFT_IMAGE = "user_sign_draft_image";

	private static final float TEXT_SIZE_NORMAL = 12.8f;

	private static final float TEXT_SIZE_SMALL = TEXT_SIZE_NORMAL * 0.8f;

	private static final float TEXT_SIZE_LARGE = TEXT_SIZE_NORMAL * 1.2f;
	
	private static final int EDIT_COLOR = 0xFF594937;

	private static final int EDIT_BACKGROUD_COLOR = 0x90FFFFFF;
	private ImageView back;

	private ImageView next;

	private ImageView innerPage;

	private LinearLayout landscapeMenuOpenerWrapper;

	private ImageView landscapeMenuOpener;

	private RelativeLayout landscapeMenuModeScreenMask;

	private FrameLayout editableImageFrame;

	private FrameLayout editableVoiceFrame;

	private FrameLayout editableTextFrame;

	private FrameLayout editableSignatureFrame;

	private TextView editableImageTip;

	private TextView editableVoiceTip;

	private TextView editableTextTip;

	private TextView editableSignatureTip;

	private ImageView editableImage;

	private AudioMessageView editableVoice;

	private TextView editableText;

	private Card card;

	private Uri userImage;

	private Bitmap userImageBitmap;

	private Uri userVoice;

	private String userVoiceDuration;

	private String userTextContent;

	private int userTextSizeType;

	private int userTextColor;

	private Uri userSignHandwritingUri;

	private Uri userSignPositionInfoUri;

	private Uri userSignDraftImageUri;

	private ImageView editableSignImage;

	private SaveDraft saveDraft;

	private OpenDraft openDraft;

	private CardDraftManager cardDraftManager;

	private String screenshotLeftFilePath;
	private String screenshotRightFilePath;

	private ProgressDialog progressDialog;

	private CardDatabaseHelper cardDatabaseHelper;

	private String sendBackId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		initSendBackId();
		initCardDataBase();
		if (savedInstanceState != null)
		{
			Log.d(TAG, "from savedInstanceState");
			restoreUserData(savedInstanceState);
		}
		else
		{

			if (getIntent().getParcelableExtra(EXTRA_KEY_CARD_DRAFT) != null)
			{
				Log.d(TAG, "from intent have card draft");
				CardDraft cardDraft = getIntent().getParcelableExtra(EXTRA_KEY_CARD_DRAFT);
				card = getCardById(cardDraft.getCardId());
				updateFromCradDraft(cardDraft);
				saveCardInformation();
			}
			else
			{
				Log.d(TAG, "from intent");
				card = getEmptyCardFromIntent(getIntent());
			}
		}
		initCardDraftManager();
		initLayout();

		super.onCreate(savedInstanceState);
	}

	private void initSendBackId()
	{
		Intent intent = getIntent();
		sendBackId = intent.getStringExtra(Constant.EXTRA_KEY_SENDBACK_ID);
	}

	private void initCardDataBase()
	{

		cardDatabaseHelper = new CardDatabaseHelper(getApplicationContext());
		cardDatabaseHelper.open();
	}

	private void saveCardInformation()
	{

		card.setImage(userImage);
		card.setSound(userVoice);
		card.setMessage(userTextContent, userTextSizeType, userTextColor);
		card.setSignDraftImage(userSignDraftImageUri);
		card.setSignHandwriting(userSignPositionInfoUri);
		card.setSignPositionInfo(userSignDraftImageUri);
	}

	private void initCardDraftManager()
	{

		Log.d(TAG, "initCardDraftManager()");
		cardDraftManager = new CardDraftManager();
		cardDraftManager.init(getApplicationContext());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{

		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			Log.d(TAG, "LANDSCAPE");
		}
		else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			Log.d(TAG, "PORTRAIT");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{

		saveUserData(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
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
		setupCardView();
		setupUserData();
		super.onResume();
	}

	private void setupUserData()
	{

		updateAllRegion();

	}

	private void restoreUserData(Bundle savedInstanceState)
	{

		sendBackId = savedInstanceState.getString(Constant.EXTRA_KEY_SENDBACK_ID);

		card = getEmptyCardFromSavedInstanceState(savedInstanceState);
		userImage = savedInstanceState.getParcelable(EXTRA_KEY_USER_IMAGE);
		userImageBitmap = savedInstanceState.getParcelable(EXTRA_KEY_USER_IMAGE_BITMAP);
		userVoice = savedInstanceState.getParcelable(EXTRA_KEY_USER_VOICE);
		userVoiceDuration = savedInstanceState.getString(EXTRA_KEY_USER_VOICE_DURATION);
		userTextContent = savedInstanceState.getString(EXTRA_KEY_USER_TEXT_CONTENT);
		userTextSizeType = savedInstanceState.getInt(EXTRA_KEY_USER_TEXT_SIZE_TYPE);
		userTextColor = savedInstanceState.getInt(EXTRA_KEY_USER_TEXT_COLOR);

		userSignHandwritingUri = savedInstanceState.getParcelable(EXTRA_KEY_USER_SIGN_HANDWRITHING);
		userSignPositionInfoUri = savedInstanceState
				.getParcelable(EXTRA_KEY_USER_SIGN_POSITION_INFO);
		userSignDraftImageUri = savedInstanceState.getParcelable(EXTRA_KEY_USER_SIGN_DRAFT_IMAGE);

		Log.d(TAG, "restore sendBackId -- " + sendBackId);
		Log.d(TAG, "restore user data -- IMAGE URI: " + userImage);
		Log.d(TAG, "restore user data -- IMAGE BITMAP: " + userImageBitmap);
		Log.d(TAG, "restore user data -- VOICE URI: " + userVoice);
		Log.d(TAG, "restore user data -- VOICE DURATION: " + userVoiceDuration);
		Log.d(TAG, "restore user data -- TEXT CONTENT: " + userTextContent);
		Log.d(TAG, "restore user data -- TEXT SIZE TYPE: " + userTextSizeType);
		Log.d(TAG, "restore user data -- TEXT COLOR: " + userTextColor);
		Log.d(TAG, "restore user data -- SIGN HANDWRITHING: " + userSignHandwritingUri);
		Log.d(TAG, "restore user data -- SIGN POSITION: " + userSignPositionInfoUri);
		Log.d(TAG, "restore user data -- SIGN IAMGE: " + userSignDraftImageUri);

		saveCardInformation();
	}

	private void saveUserData(Bundle savedInstanceState)
	{
		if (sendBackId != null)
		{
			savedInstanceState.putString(Constant.EXTRA_KEY_SENDBACK_ID, sendBackId);
			Log.d(TAG, "save sendBackId -- " + sendBackId);
		}

		savedInstanceState.putInt(EXTRA_KEY_CARD_ID, card.getId());
		Log.d(TAG, "save user data -- card.getId(): " + card.getId());
		if (userImage != null)
		{
			savedInstanceState.putParcelable(EXTRA_KEY_USER_IMAGE, userImage);
			Log.d(TAG, "save user data -- IMAGE URI: " + userImage);
		}
		if (userImageBitmap != null)
		{
			savedInstanceState.putParcelable(EXTRA_KEY_USER_IMAGE_BITMAP, userImageBitmap);
			Log.d(TAG, "save user data -- IMAGE BITMAP: " + userImageBitmap);
		}
		if (userVoice != null)
		{
			savedInstanceState.putParcelable(EXTRA_KEY_USER_VOICE, userVoice);
			Log.d(TAG, "save user data -- VOICE URI: " + userVoice);
		}
		if (userVoiceDuration != null)
		{
			savedInstanceState.putString(EXTRA_KEY_USER_VOICE_DURATION, userVoiceDuration);
			Log.d(TAG, "save user data -- VOICE DURATION: " + userVoiceDuration);
		}
		if (userTextContent != null)
		{
			savedInstanceState.putString(EXTRA_KEY_USER_TEXT_CONTENT, userTextContent);
			savedInstanceState.putInt(EXTRA_KEY_USER_TEXT_SIZE_TYPE, userTextSizeType);
			savedInstanceState.putInt(EXTRA_KEY_USER_TEXT_COLOR, userTextColor);
			Log.d(TAG, "save user data --TEXT CONTENT: " + userTextContent);
			Log.d(TAG, "save user data --TEXT SIZE TYPE: " + userTextSizeType);
			Log.d(TAG, "save user data --TEXT COLOR: " + userTextColor);
		}

		if (userSignHandwritingUri != null)
		{
			savedInstanceState.putParcelable(EXTRA_KEY_USER_SIGN_HANDWRITHING,
					userSignHandwritingUri);
			Log.d(TAG, "save user data -- SIGN HANDWRITHING: " + userSignHandwritingUri);
		}

		if (userSignPositionInfoUri != null)
		{
			savedInstanceState.putParcelable(EXTRA_KEY_USER_SIGN_POSITION_INFO,
					userSignPositionInfoUri);
			Log.d(TAG, "save user data -- SIGN POSITION: " + userSignPositionInfoUri);
		}

		if (userSignDraftImageUri != null)
		{
			savedInstanceState
					.putParcelable(EXTRA_KEY_USER_SIGN_DRAFT_IMAGE, userSignDraftImageUri);
			Log.d(TAG, "save user data -- SIGN IAMGE: " + userSignDraftImageUri);
		}

	}

	private Card getEmptyCardFromIntent(Intent intent)
	{

		int cardId = intent.getIntExtra(EXTRA_KEY_CARD_ID, -1);
		return getCardById(cardId);
	}

	private Card getEmptyCardFromSavedInstanceState(Bundle savedInstanceState)
	{

		int cardId = savedInstanceState.getInt(EXTRA_KEY_CARD_ID, -1);
		return getCardById(cardId);
	}

	private Card getCardById(int id)
	{

		Card card;
		if (id != -1)
		{
			// card = FakeData.getCard(id);
			Log.d(TAG, "getCardById id : " + id);
			card = cardDatabaseHelper.getCardByCardID(id,
					cardDatabaseHelper.getSystemDPI(getApplicationContext()));
			assert card != null;

			Log.d(TAG, "EDIT : " + card.getName());
		}
		else
		{
			throw new RuntimeException("invalid id -1");
		}
		return card;
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
		next = (ImageView) findViewById(R.id.act_card_editor_iv_next);
		innerPage = (ImageView) findViewById(R.id.act_card_editor_iv_card_inner_page);

		landscapeMenuOpenerWrapper = (LinearLayout) findViewById(R.id.act_card_editor_landscape_llyt_menu_opener_wrapper);
		if (landscapeMenuOpenerWrapper != null)
		{
			landscapeMenuOpener = (ImageView) findViewById(R.id.act_card_editor_landscape_iv_menu_opener);
			landscapeMenuModeScreenMask = (RelativeLayout) findViewById(R.id.act_card_editor_landscape_rlyt_menu_mode_screen_mask);
		}

		editableImageFrame = (FrameLayout) findViewById(R.id.glb_card_flyt_editable_image_frame);
		editableVoiceFrame = (FrameLayout) findViewById(R.id.glb_card_flyt_editable_voice_frame);
		editableTextFrame = (FrameLayout) findViewById(R.id.glb_card_flyt_editable_text_frame);
		editableSignatureFrame = (FrameLayout) findViewById(R.id.glb_card_flyt_editable_signature_frame);

		editableImageTip = (TextView) findViewById(R.id.glb_card_tv_editable_image_tip);
		editableVoiceTip = (TextView) findViewById(R.id.glb_card_tv_editable_voice_tip);
		editableTextTip = (TextView) findViewById(R.id.glb_card_tv_editable_text_tip);
		editableSignatureTip = (TextView) findViewById(R.id.glb_card_tv_editable_signature_tip);

		editableImage = (ImageView) findViewById(R.id.glb_card_iv_editable_image);
		editableVoice = (AudioMessageView) findViewById(R.id.glb_card_amv_editable_voice);
		editableText = (TextView) findViewById(R.id.glb_card_tv_editable_text);
		editableSignImage = (ImageView) findViewById(R.id.glb_card_iv_editable_signature_image);

		saveDraft = (SaveDraft) findViewById(R.id.act_card_editor_iv_menu_save_draft);
		openDraft = (OpenDraft) findViewById(R.id.act_card_editor_iv_menu_open_draft);

	}

	private void setupCardView()
	{
		Bitmap img3dOpenBitmap = FileUtility.getBitmapFromPath(card.getImage3dOpenPath());
		FileUtility.setImageViewWithBitmap(innerPage, img3dOpenBitmap);

		/* innerPage.setImageResource(android.R.color.transparent); */
		setCardColor();
	}

	private void setCardColor()
	{

		int color = card.getTextColor();
		Log.d(TAG, "setCardColor() card.getTextColor() " + card.getTextColor());
		Resources res = getResources();
		int dashGap = res.getDimensionPixelSize(R.dimen.dash_border_stroke_dash_gap);
		int dashWidth = res.getDimensionPixelSize(R.dimen.dash_border_stroke_dash_width);
		int width = res.getDimensionPixelSize(R.dimen.dash_border_stroke_width);

		GradientDrawable dashBorderBackground = ((GradientDrawable) editableImageFrame
				.getBackground());
		if (dashBorderBackground == null)
		{
			editableImageFrame.setBackgroundResource(R.drawable.dash_border);
			editableVoiceFrame.setBackgroundResource(R.drawable.dash_border);
			editableTextFrame.setBackgroundResource(R.drawable.dash_border);
			editableSignatureFrame.setBackgroundResource(R.drawable.dash_border);
		}
		((GradientDrawable) editableImageFrame.getBackground()).setStroke(width,  EDIT_COLOR, dashWidth,
				dashGap);
		((GradientDrawable) editableVoiceFrame.getBackground()).setStroke(width, EDIT_COLOR, dashWidth,
				dashGap);
		((GradientDrawable) editableTextFrame.getBackground()).setStroke(width, EDIT_COLOR, dashWidth,
				dashGap);
		((GradientDrawable) editableSignatureFrame.getBackground()).setStroke(width, EDIT_COLOR,
				dashWidth, dashGap);
		editableImageTip.setTextColor(EDIT_COLOR);
		editableVoiceTip.setTextColor(EDIT_COLOR);
		editableTextTip.setTextColor(EDIT_COLOR);
		editableSignatureTip.setTextColor(EDIT_COLOR);
	}

	private void setListener()
	{

		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "back - finish");
				finish();
			}

		});
		next.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "next");
				if (isCardUserDataCompleted())
				{
					progressDialog = ProgressDialog.show(CardEditorActivity.this,
							getString(R.string.processing), getString(R.string.please_wait), true,
							false);
					startScreenshotThread();
				}
				else
				{
					Toast.makeText(CardEditorActivity.this, getString(R.string.data_incomplete),
							Toast.LENGTH_LONG).show();
				}
			}

		});
		if (landscapeMenuOpener != null)
		{
			landscapeMenuOpener.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					Log.d(TAG, "landscape menu opener clicked");
					openLandscapeMenu();
				}

			});
			landscapeMenuModeScreenMask.setOnTouchListener(new OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{

					Log.d(TAG, "landscape menu mode screen mask touched");
					closeLandscapeMenu();
					return true;
				}

			});
		}

		editableImageFrame.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "EDIT IMAGE");
				startImagePicker();
			}

		});
		editableVoiceFrame.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "EDIT VOICE");
				startVoiceRecorder();
			}

		});
		OnClickListener textEditorListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "EDIT TEXT");
				startTextEditor();
			}
		};
		editableTextFrame.setOnClickListener(textEditorListener);
		editableText.setOnClickListener(textEditorListener);
		editableTextTip.setOnClickListener(textEditorListener);
		editableSignatureFrame.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "EDIT SIGNATURE");
				startSignatureEditor();
			}
		});
		saveDraft.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "saveDraft - onClick()");
				boolean isSuccess = cardDraftManager.saveDraft(new CardDraft(card.getId(), userVoice,
						userVoiceDuration, userImage, userTextContent, userTextColor,
						userTextSizeType, userSignHandwritingUri, userSignPositionInfoUri,
						userSignDraftImageUri));
				
				if(isSuccess){
					Toast.makeText(CardEditorActivity.this, getString(R.string.save_draft_success),
							Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(CardEditorActivity.this, getString(R.string.save_draft_fail),
							Toast.LENGTH_LONG).show();
				}		
			}

		});

		openDraft.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "openDraft - onClick()");
				try
				{
					CardDraft cardDraft = cardDraftManager.openDraft();

					updateFromCradDraft(cardDraft);
					updateAllRegion();
					saveCardInformation();

					Log.d(TAG, "openDraft - onClick() user data -- IMAGE URI: " + userImage);
					Log.d(TAG, "openDraft - onClick() user data -- IMAGE BITMAP: "
							+ userImageBitmap);
					Log.d(TAG, "openDraft - onClick() user data -- VOICE URI: " + userVoice);
					Log.d(TAG, "openDraft - onClick() user data -- VOICE DURATION: "
							+ userVoiceDuration);
					Log.d(TAG, "openDraft - onClick() user data -- TEXT CONTENT: "
							+ userTextContent);
					Log.d(TAG, "openDraft - onClick() user data -- TEXT SIZE TYPE: "
							+ userTextSizeType);
					Log.d(TAG, "openDraft - onClick() user data -- TEXT COLOR: " + userTextColor);
					Log.d(TAG, "openDraft - onClick() user data -- SIGN HANDWRITHING: "
							+ userSignHandwritingUri);
					Log.d(TAG, "openDraft - onClick() user data -- SIGN POSITION: "
							+ userSignPositionInfoUri);
					Log.d(TAG, "openDraft - onClick() user data -- SIGN IAMGE: "
							+ userSignDraftImageUri);
					Toast.makeText(CardEditorActivity.this, getString(R.string.open_draft_success),
							Toast.LENGTH_LONG).show();
				}
				catch (Exception e)
				{
					Log.d(TAG, "openDraft - openDraft error:" + e.toString());
					Toast.makeText(CardEditorActivity.this, getString(R.string.open_draft_fail),
							Toast.LENGTH_LONG).show();
				}

			}

		});
	}

	private void updateFromCradDraft(CardDraft cardDraft)
	{

		userImage = cardDraft.getImageUri();
		userVoice = cardDraft.getSoundUri();
		userVoiceDuration = cardDraft.getSoundDuration();
		userTextContent = cardDraft.getMessage();
		userTextSizeType = cardDraft.getMessageTextSizeType();
		userTextColor = cardDraft.getMessageTextColor();
		userSignHandwritingUri = cardDraft.getSignHandwritingUri();
		userSignPositionInfoUri = cardDraft.getSignPositionInfoUri();
		userSignDraftImageUri = cardDraft.getSignDraftImageUri();
	}

	private void updateAllRegion()
	{

		updateImageRegion();
		updateVoiceRegion();
		updateTextRegion();
		updateSignRegion();
	}

	private void updateImageRegion()
	{
		Log.d(TAG, "updateImageRegion()");
		if (userImage != null && Bitmap.createBitmap(getBitmapFromUri(userImage)) != null)
		{
			
			userImageBitmap = Bitmap.createBitmap(getBitmapFromUri(userImage));
			editableImage.setImageBitmap(userImageBitmap);
			editableImage.invalidate();
			Log.d(TAG, "updateImageRegion()  editableImage.invalidate()");
		}
	}

	private void updateVoiceRegion()
	{

		if (userVoice != null)
		{
			editableVoice.setDurationText(userVoiceDuration);
			editableVoice.setVisibility(View.VISIBLE);
		}
	}

	private void updateTextRegion()
	{

		if (userTextContent != null)
		{
			editableText.setText(userTextContent);
			editableText.setTextSize(getTextSizeByType(userTextSizeType));
			editableText.setTextColor(userTextColor);
			editableText.setVisibility(View.VISIBLE);
		}

		if (editableText.getText().length() > 0)
		{
			editableTextTip.setVisibility(View.INVISIBLE);
		}
		else
		{
			editableTextTip.setVisibility(View.VISIBLE);
		}
	}

	private void openLandscapeMenu()
	{

		landscapeMenuOpenerWrapper.setVisibility(View.INVISIBLE);
		landscapeMenuModeScreenMask.setVisibility(View.VISIBLE);
	}

	private void closeLandscapeMenu()
	{

		landscapeMenuOpenerWrapper.setVisibility(View.VISIBLE);
		landscapeMenuModeScreenMask.setVisibility(View.INVISIBLE);
	}

	private void startImagePicker()
	{

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQ_PICK_IMAGE);
	}

	private void onImagePickerResult(int resultCode, Intent data)
	{

		Uri photoUri = data.getData();
		if (photoUri != null)
		{
			startImageCropper(photoUri);
		}
	}

	private void startImageCropper(Uri photoUri)
	{

		int w = editableImage.getWidth();
		int h = editableImage.getHeight();
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setData(photoUri);
		intent.putExtra("outputX", w);
		intent.putExtra("outputY", h);
		intent.putExtra("aspectX", w);
		intent.putExtra("aspectY", h);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQ_CROP_IMAGE);
	}

	private void onImageCropperResult(int resultCode, Intent data)
	{

		Bundle extras = data.getExtras();
		if (extras != null)
		{
			Bitmap cropped = extras.getParcelable("data");
			userImageBitmap = cropped;
			editableImage.setImageBitmap(cropped);
			new SaveCardImageThread(cropped).start();
		}
	}

	private void startVoiceRecorder()
	{
		File extDir = Environment.getExternalStorageDirectory();
		String fileName = FileUtility.getRandomSpeechName("3gp");
		String filePath = extDir.getAbsolutePath() + "/" + fileName;
		Log.d(TAG, "startVoiceRecorder - filePath: " + filePath);

		Intent intent = new Intent(this, AudioRecorderActivity.class);
		intent.putExtra(AudioRecorderActivity.EXTRA_KEY_FILEPATH, filePath);
		startActivityForResult(intent, REQ_RECORD_VOICE);
	}

	private void onVoiceRecorderResult(int resultCode, Intent data)
	{

		String filePath = data.getStringExtra(AudioRecorderActivity.EXTRA_KEY_FILEPATH);
		userVoice = Uri.fromFile(new File(filePath));

		int duration = data.getIntExtra(AudioRecorderActivity.EXTRA_KEY_DURATION_MILLISECOND, 0);
		int min = duration / 1000 / 60;
		int sec = duration / 1000 % 60;
		userVoiceDuration = min + ":" + String.format("%02d", sec);

		Log.d(TAG, "onVoiceRecorderResult - filePath: " + filePath);
		Log.d(TAG, "onVoiceRecorderResult - duration: " + duration + " = " + min + "m" + sec + "s");

		updateVoiceRegion();

		card.setSound(userVoice);
	}

	private void startTextEditor()
	{

		Intent intent = new Intent(this, CardTextEditorActivity.class);
		intent.putExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_LIMIT, 60);
		intent.putExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_CONTENT, card.getMessage());
		intent.putExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_COLOR, card.getMessageTextColor());
		intent.putExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_SIZE_TYPE,
				card.getMessageTextSizeType());
		startActivityForResult(intent, REQ_EDIT_TEXT);
	}

	private void onTextEditorResult(int resultCode, Intent data)
	{

		userTextContent = data.getStringExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_CONTENT);
		userTextSizeType = data.getIntExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_SIZE_TYPE,
				Card.DEFAULT_TEXT_SIZE_TYPE);
		userTextColor = data.getIntExtra(CardTextEditorActivity.EXTRA_KEY_TEXT_COLOR,
				Card.DEFAULT_TEXT_COLOR);

		updateTextRegion();

		card.setMessage(userTextContent, userTextSizeType, userTextColor);
	}

	private void startSignatureEditor()
	{

		Intent intent = new Intent(this, EditSignatureActivity.class);
		intent.putExtra(EXTRA_KEY_USER_SIGN_HANDWRITHING, userSignHandwritingUri);
		intent.putExtra(EXTRA_KEY_USER_SIGN_POSITION_INFO, userSignPositionInfoUri);
		intent.putExtra(EXTRA_KEY_USER_SIGN_DRAFT_IMAGE, userSignDraftImageUri);

		startActivityForResult(intent, REQ_EDIT_SIGNATURE);
	}

	private void onSignatureEditorResult(int resultCode, Intent data)
	{

		userSignHandwritingUri = data.getParcelableExtra(EXTRA_KEY_USER_SIGN_HANDWRITHING);
		userSignPositionInfoUri = data.getParcelableExtra(EXTRA_KEY_USER_SIGN_POSITION_INFO);
		userSignDraftImageUri = data.getParcelableExtra(EXTRA_KEY_USER_SIGN_DRAFT_IMAGE);

		if (userSignDraftImageUri != null)
		{

			Log.d(TAG, "onSignatureEditorResult, getBitmapFromUri(userSignDraftImageUri): "
					+ getBitmapFromUri(userSignDraftImageUri));
			editableSignImage.setImageBitmap(getBitmapFromUri(userSignDraftImageUri));
		}

		Log.d(TAG, "onSignatureEditorResult, userSignHandwritingUri: " + userSignHandwritingUri);
		Log.d(TAG, "onSignatureEditorResult, userSignPositionInfoUri: " + userSignPositionInfoUri);
		Log.d(TAG, "onSignatureEditorResult, useSignDraftImageUri:" + userSignDraftImageUri);

		updateSignRegion();

		card.setSignHandwriting(userSignHandwritingUri);
		card.setSignPositionInfo(userSignPositionInfoUri);
		card.setSignDraftImage(userSignDraftImageUri);

	}

	private void updateSignRegion()
	{

		if (userSignDraftImageUri != null && getBitmapFromUri(userSignDraftImageUri) != null)
		{
			editableSignatureTip.setVisibility(View.INVISIBLE);
			editableSignImage.setImageBitmap(getBitmapFromUri(userSignDraftImageUri));
		}
		else
		{
			editableSignatureTip.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);

		if (data == null)
		{
			Log.d(TAG, "onActivityResult, data null");
			return;
		}
		switch (requestCode)
		{
		case REQ_PICK_IMAGE:
			onImagePickerResult(resultCode, data);
			break;
		case REQ_CROP_IMAGE:
			onImageCropperResult(resultCode, data);
			break;
		case REQ_RECORD_VOICE:
			onVoiceRecorderResult(resultCode, data);
			break;
		case REQ_EDIT_TEXT:
			onTextEditorResult(resultCode, data);
			break;
		case REQ_EDIT_SIGNATURE:
			onSignatureEditorResult(resultCode, data);
			break;
		}
	}

	private class SaveCardImageThread extends Thread
	{
		private Bitmap bitmap;

		public SaveCardImageThread(Bitmap bitmap)
		{

			this.bitmap = bitmap;
		}

		@Override
		public void run()
		{

			String fileName = FileUtility.getRandomImageName("png");
			File file = new File(getCacheDir(), fileName);
			if (saveBitmapToFile(bitmap, file))
			{
				userImage = Uri.fromFile(file);
				card.setImage(userImage);
				Log.d(TAG, "Card image set:" + card.getImage().toString());

				CardEditorActivity.this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{

						updateImageRegion();
					}
				});

			}
			else
			{
				Log.e(TAG, "Failed to save bitmap");
			}
		}
	}

	/**
	 * 
	 * @param bitmap
	 * @param file
	 * @return true if save successfully
	 */
	private static boolean saveBitmapToFile(Bitmap bitmap, File file)
	{

		boolean result = false;
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, out);
			out.close();
			result = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static float getTextSizeByType(int type)
	{

		float size;
		switch (type)
		{
		case Card.TEXT_SIZE_TYPE_SMALL:
			size = TEXT_SIZE_SMALL;
			break;
		case Card.TEXT_SIZE_TYPE_LARGE:
			size = TEXT_SIZE_LARGE;
			break;
		case Card.TEXT_SIZE_TYPE_NORMAL:
		default:
			size = TEXT_SIZE_NORMAL;
			break;
		}
		return size;
	}

	private void startCardSender()
	{

		Intent intent = new Intent(this, CardViewerActivity.class);
		intent.putExtra(CardViewerActivity.EXTRA_KEY_MODE, CardViewerActivity.MODE_SENDER);
		intent.putExtra(CardViewerActivity.EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND, card);
		Log.d(TAG, "send card:: " + card);
		intent.putExtra(CardViewerActivity.EXTRA_KEY_CARD_LEFT_SCREENSHOT_FILEPATH,
				screenshotLeftFilePath);
		intent.putExtra(CardViewerActivity.EXTRA_KEY_CARD_RIGHT_SCREENSHOT_FILEPATH,
				screenshotRightFilePath);
		intent.putExtra(CardViewerActivity.EXTRA_KEY_CARD_VOICE_DURATION_TEXT, userVoiceDuration);
		intent.putExtra(Constant.EXTRA_KEY_SENDBACK_ID, sendBackId);
		Log.d(TAG, "screenshot left:: " + screenshotLeftFilePath);
		Log.d(TAG, "screenshot right:: " + screenshotRightFilePath);
		startActivity(intent);
	}

	private boolean isCardUserDataCompleted()
	{
		// card.setSignDraftImage(card.getImage());
		return card.getImage() != null && card.getMessage() != null && card.getSound() != null
				&& card.getSignDraftImage() != null;
	}

	private Bitmap getBitmapFromUri(Uri uri)
	{

		try
		{
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			return bitmap;
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
			Log.e(TAG, "uri：" + uri);
			e.printStackTrace();
			return null;
		}
	}

	private void hideFrameBorderAndTips()
	{
		editableImageFrame.setBackgroundResource(0);
		editableVoiceFrame.setBackgroundResource(0);
		editableTextFrame.setBackgroundResource(0);
		editableSignatureFrame.setBackgroundResource(0);
		editableImageTip.setVisibility(View.GONE);
		editableVoiceTip.setVisibility(View.GONE);
		editableTextTip.setVisibility(View.GONE);
		editableSignatureTip.setVisibility(View.GONE);
	}

	private void startScreenshotThread()
	{
		hideFrameBorderAndTips();
		View wholeCard = findViewById(R.id.act_card_editor_rlyt_card);
		wholeCard.setDrawingCacheEnabled(true);
		Bitmap wholeScreenshot = wholeCard.getDrawingCache();
		int pageWidth = wholeScreenshot.getWidth() / 2;
		int pageHeight = wholeScreenshot.getHeight();
		Bitmap left = Bitmap.createBitmap(wholeScreenshot, 0, 0, pageWidth, pageHeight);
		Bitmap right = Bitmap.createBitmap(wholeScreenshot, pageWidth, 0, pageWidth, pageHeight);
		wholeCard.setDrawingCacheEnabled(false);
		new SaveScreenshotBitmapThread(left, right).start();
	}

	private class SaveScreenshotBitmapThread extends Thread
	{
		Bitmap left;
		Bitmap right;

		public SaveScreenshotBitmapThread(Bitmap left, Bitmap right)
		{
			this.left = left;
			this.right = right;
		}

		@Override
		public void run()
		{
			File dir = CardEditorActivity.this.getCacheDir();
			long now = System.currentTimeMillis();
			File leftFile = new File(dir, now + "_left.png");
			File rightFile = new File(dir, now + "_right.png");
			if (saveBitmapToFile(left, leftFile) && saveBitmapToFile(right, rightFile))
			{
				screenshotLeftFilePath = leftFile.getAbsolutePath();
				screenshotRightFilePath = rightFile.getAbsolutePath();
			}

			startCardSender();
			progressDialog.dismiss();
		}
	}

}

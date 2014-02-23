package eoc.studio.voicecard.card.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
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
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.FakeData;
import eoc.studio.voicecard.manufacture.EditSignatureActivity;
import eoc.studio.voicecard.utils.FileUtility;

public class CardEditorActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CARD_ID = "card_id";

	private static final String TAG = "CardEditor";

	private static final int REQ_PICK_IMAGE = 1;
	private static final int REQ_CROP_IMAGE = 2;
	private static final int REQ_RECORD_VOICE = 3;
	private static final int REQ_EDIT_TEXT = 4;
	private static final int REQ_EDIT_SIGNATURE = 5;

	private static final String EXTRA_KEY_USER_IMAGE = "user_image";
	private static final String EXTRA_KEY_USER_IMAGE_BITMAP = "user_image_bitmap";
	private static final String EXTRA_KEY_USER_VOICE = "user_voice";
	private static final String EXTRA_KEY_USER_VOICE_DURATION = "user_voice_duration";
	private static final String EXTRA_KEY_USER_TEXT_CONTENT = "user_text_content";
	private static final String EXTRA_KEY_USER_TEXT_SIZE_TYPE = "user_text_size_type";
	private static final String EXTRA_KEY_USER_TEXT_COLOR = "user_text_color";

	private static final float TEXT_SIZE_NORMAL = 12.8f;
	private static final float TEXT_SIZE_SMALL = TEXT_SIZE_NORMAL * 0.8f;
	private static final float TEXT_SIZE_LARGE = TEXT_SIZE_NORMAL * 1.2f;

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
	private LinearLayout editableVoice;
	private TextView editableVoiceText;
	private TextView editableText;

	private Card card;
	private Uri userImage;
	private Bitmap userImageBitmap;
	private Uri userVoice;
	private String userVoiceDuration;
	private String userTextContent;
	private int userTextSizeType;
	private int userTextColor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			Log.d(TAG, "from savedInstanceState");
			restoreUserData(savedInstanceState);
		}
		else
		{
			Log.d(TAG, "from intent");
			card = getEmptyCardFromIntent(getIntent());
		}
		initLayout();
		setupCardView();
		setupUserData();
		super.onCreate(savedInstanceState);
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
		super.onResume();
	}

	private void setupUserData()
	{
		if (userImage != null)
		{
			editableImage.setImageBitmap(userImageBitmap);
		}
		if (userVoice != null)
		{
			editableVoice.setVisibility(View.VISIBLE);
			editableVoiceText.setText(userVoiceDuration);
		}
		if (userTextContent != null)
		{
			editableText.setText(userTextContent);
			editableText.setTextSize(getTextSizeByType(userTextSizeType));
			editableText.setTextColor(userTextColor);
			editableText.setVisibility(View.VISIBLE);

			if (editableText.getText().length() > 0)
			{
				editableTextTip.setVisibility(View.INVISIBLE);
			}
			else
			{
				editableTextTip.setVisibility(View.VISIBLE);
			}
		}
	}

	private void restoreUserData(Bundle savedInstanceState)
	{
		card = getEmptyCardFromSavedInstanceState(savedInstanceState);
		userImage = savedInstanceState.getParcelable(EXTRA_KEY_USER_IMAGE);
		userImageBitmap = savedInstanceState.getParcelable(EXTRA_KEY_USER_IMAGE_BITMAP);
		userVoice = savedInstanceState.getParcelable(EXTRA_KEY_USER_VOICE);
		userVoiceDuration = savedInstanceState.getString(EXTRA_KEY_USER_VOICE_DURATION);
		userTextContent = savedInstanceState.getString(EXTRA_KEY_USER_TEXT_CONTENT);
		userTextSizeType = savedInstanceState.getInt(EXTRA_KEY_USER_TEXT_SIZE_TYPE);
		userTextColor = savedInstanceState.getInt(EXTRA_KEY_USER_TEXT_COLOR);
		Log.d(TAG, "restore user data -- IMAGE URI: " + userImage);
		Log.d(TAG, "restore user data -- IMAGE BITMAP: " + userImageBitmap);
		Log.d(TAG, "restore user data -- VOICE URI: " + userVoice);
		Log.d(TAG, "restore user data -- VOICE DURATION: " + userVoiceDuration);
		Log.d(TAG, "restore user data -- TEXT CONTENT: " + userTextContent);
		Log.d(TAG, "restore user data -- TEXT SIZE TYPE: " + userTextSizeType);
		Log.d(TAG, "restore user data -- TEXT COLOR: " + userTextColor);
		card.setImage(userImage);
		card.setSound(userVoice);
		card.setMessage(userTextContent, userTextSizeType, userTextColor);
	}

	private void saveUserData(Bundle savedInstanceState)
	{
		savedInstanceState.putInt(EXTRA_KEY_CARD_ID, card.getId());
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
			card = FakeData.getCard(id);
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

		editableImageFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_image_frame);
		editableVoiceFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_voice_frame);
		editableTextFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_text_frame);
		editableSignatureFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_signature_frame);

		editableImageTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_image_tip);
		editableVoiceTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_voice_tip);
		editableTextTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_text_tip);
		editableSignatureTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_signature_tip);

		editableImage = (ImageView) findViewById(R.id.act_card_editor_iv_editable_image);
		editableVoice = (LinearLayout) findViewById(R.id.act_card_editor_llyt_editable_voice);
		editableVoiceText = (TextView) findViewById(R.id.act_card_editor_tv_editable_voice_play_text);
		editableText = (TextView) findViewById(R.id.act_card_editor_ret_editable_text);
	}

	private void setupCardView()
	{
		innerPage.setImageResource(card.getImage3dOpenResId());
		setCardColor();
	}

	private void setCardColor()
	{
		int color = card.getTextColor();
		Resources res = getResources();
		int dashGap = res.getDimensionPixelSize(R.dimen.dash_border_stroke_dash_gap);
		int dashWidth = res.getDimensionPixelSize(R.dimen.dash_border_stroke_dash_width);
		int width = res.getDimensionPixelSize(R.dimen.dash_border_stroke_width);
		((GradientDrawable) editableImageFrame.getBackground()).setStroke(width, color, dashWidth,
				dashGap);
		((GradientDrawable) editableVoiceFrame.getBackground()).setStroke(width, color, dashWidth,
				dashGap);
		((GradientDrawable) editableTextFrame.getBackground()).setStroke(width, color, dashWidth,
				dashGap);
		((GradientDrawable) editableSignatureFrame.getBackground()).setStroke(width, color,
				dashWidth, dashGap);
		editableImageTip.setTextColor(color);
		editableVoiceTip.setTextColor(color);
		editableTextTip.setTextColor(color);
		editableSignatureTip.setTextColor(color);
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
				startCardSender();
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
		// Intent intent = new Intent(this, AudioRecorderActivity.class);
		Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		startActivityForResult(intent, REQ_RECORD_VOICE);
	}

	private void onVoiceRecorderResult(int resultCode, Intent data)
	{
		Uri uri = data.getData();
		String filePath = convertAudioContentUriToFilePath(uri);
		String extName = filePath.substring(filePath.lastIndexOf(".") + 1);
		File oldFile = new File(filePath);

		filePath = filePath.substring(0, filePath.lastIndexOf("/"));
		String newFileName = FileUtility.getRandomSpeechName(extName);
		File newFile = new File(filePath, newFileName);
		try
		{
			copyFile(oldFile, newFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		uri = Uri.fromFile(newFile);
		userVoice = uri;
		card.setSound(uri);
		setVoiceMessageText(uri);
	}

	private void setVoiceMessageText(Uri uri)
	{
		MediaPlayer mp = new MediaPlayer();
		try
		{
			mp.setDataSource(this, uri);
			mp.setOnPreparedListener(new OnPreparedListener()
			{

				@Override
				public void onPrepared(final MediaPlayer mp)
				{

					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							int duration = mp.getDuration();
							int min = duration / 1000 / 60;
							int sec = duration / 1000 % 60;
							userVoiceDuration = getString(R.string.play_voice_message, min + ":"
									+ String.format("%02d", sec));
							editableVoiceText.setText(userVoiceDuration);
							editableVoice.setVisibility(View.VISIBLE);
							Log.d(TAG, "userVoiceDuration: " + userVoiceDuration);

							new Thread("ReleasePlayer")
							{
								@Override
								public void run()
								{
									Log.d(TAG, "Release MediaPlayer");
									mp.release();
								}
							}.start();
						}
					});
				}

			});
			mp.prepareAsync();
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

		editableText.setText(userTextContent);
		editableText.setTextSize(getTextSizeByType(userTextSizeType));
		editableText.setTextColor(userTextColor);
		editableText.setVisibility(View.VISIBLE);
		if (editableText.getText().length() > 0)
		{
			editableTextTip.setVisibility(View.INVISIBLE);
		}
		else
		{
			editableTextTip.setVisibility(View.VISIBLE);
		}

		card.setMessage(userTextContent, userTextSizeType, userTextColor);
	}

	private void startSignatureEditor()
	{
		Intent intent = new Intent(this, EditSignatureActivity.class);
		startActivityForResult(intent, REQ_EDIT_SIGNATURE);
	}

	private void onSignatureEditorResult(int resultCode, Intent data)
	{

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

	private String convertAudioContentUriToFilePath(Uri contentUri)
	{
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = this.getContentResolver().query(contentUri, projection, null, null, null);
		if (cursor == null) return null;
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		String s = cursor.getString(column_index);
		cursor.close();
		return s;
	}

	private static void copyFile(File src, File dest) throws IOException
	{
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);

		byte[] buf = new byte[1024];
		int len;

		while ((len = in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}

		in.close();
		out.close();

		Log.d(TAG, "Copy file successful.");
	}

	private static float getTextSizeByType(int type)
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
		Intent intent = new Intent(this, CardSenderActivity.class);
		intent.putExtra(CardSenderActivity.EXTRA_KEY_CARD_WITH_USER_DATA_FOR_SEND, card);
		Log.d(TAG, "send card:: " + card);
		startActivity(intent);
	}

}

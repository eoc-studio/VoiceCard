package eoc.studio.voicecard.card.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.FakeData;

public class CardEditorActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CARD_ID = "card_id";

	private static final String TAG = "CardEditor";

	private static final int REQ_PICK_IMAGE = 1;
	private static final int REQ_CROP_IMAGE = 2;

	private ImageView back;
	private ImageView innerPage;
	private FrameLayout editableImageFrame;
	private FrameLayout editableVoiceFrame;
	private FrameLayout editableTextFrame;
	private FrameLayout editableSignatureFrame;

	private TextView editableImageTip;
	private TextView editableVoiceTip;
	private TextView editableTextTip;
	private TextView editableSignatureTip;

	private ImageView editableImage;

	private static Card card;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getCard();
		initLayout();
		setupCard();
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
		editableImageFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_image_frame);
		editableVoiceFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_voice_frame);
		editableTextFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_text_frame);
		editableSignatureFrame = (FrameLayout) findViewById(R.id.act_card_editor_flyt_editable_signature_frame);

		editableImageTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_image_tip);
		editableVoiceTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_voice_tip);
		editableTextTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_text_tip);
		editableSignatureTip = (TextView) findViewById(R.id.act_card_editor_tv_editable_signature_tip);

		editableImage = (ImageView) findViewById(R.id.act_card_editor_iv_editable_image);
	}

	private void setupCard()
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
				finish();
			}

		});
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
			}

		});
		editableTextFrame.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Log.d(TAG, "EDIT TEXT");
			}

		});
		editableSignatureFrame.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.d(TAG, "EDIT SIGNATURE");
			}
		});
	}

	private void startImagePicker()
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQ_PICK_IMAGE);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQ_PICK_IMAGE)
		{
			Uri photoUri = intent.getData();
			if (photoUri != null)
			{
				startImageCropper(photoUri);
			}
		}
		else if (requestCode == REQ_CROP_IMAGE)
		{
			Bundle extras = intent.getExtras();
			if (extras != null)
			{
				Bitmap cropped = extras.getParcelable("data");
				setEditableImage(cropped);
			}
		}
	}

	private void setEditableImage(Bitmap bitmap)
	{
		editableImage.setImageBitmap(bitmap);

		new SaveCardImageThread(bitmap).start();

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
			String fileName = System.currentTimeMillis() + ".png";
			File file = new File(getCacheDir(), fileName);
			if (saveBitmapToFile(bitmap, file))
			{
				card.setImage(Uri.fromFile(file));
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
	private boolean saveBitmapToFile(Bitmap bitmap, File file)
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

}

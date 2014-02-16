package eoc.studio.voicecard.richtexteditor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.colorpickerview.dialog.ColorPickerDialog;

public class RichTextEditorActivity extends BaseActivity
{

	public static final String EXTRA_KEY_TEXT_LIMIT = "text_limit";
	public static final String EXTRA_KEY_SHARED_PREFERENCES_NAME = "shared_pref_name";

	private static final String TAG = "RichTextEditorActivity";

	private String sharedPreferencesName = null;
	private int textLengthLimit = 60;

	private RichEditText editText;
	private RelativeLayout smallSetter;
	private RelativeLayout normalSetter;
	private RelativeLayout largeSetter;
	private ImageView colorPicker;
	private ImageView eraser;

	private TextView textCounter;
	private TextView textLimitTip;

	private Button back;
	private Button ok;

	// private Button save;
	// private Button load;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getConfigFromIntent();
		initLayout();
		applyConfig();
		super.onCreate(savedInstanceState);
	}

	private void getConfigFromIntent()
	{
		Intent intent = getIntent();
		textLengthLimit = intent.getIntExtra(EXTRA_KEY_TEXT_LIMIT, textLengthLimit);
		sharedPreferencesName = intent.getStringExtra(EXTRA_KEY_SHARED_PREFERENCES_NAME);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_rich_text_editor);
		findViews();
		setListener();
		initColorPicker();
	}

	private void applyConfig()
	{
		editText.setTextLengthLimit(textLengthLimit);
		textLimitTip.setText(getString(R.string.text_limit, textLengthLimit));
		if (sharedPreferencesName != null)
		{
			editText.load(sharedPreferencesName);
		}
	}

	private void findViews()
	{
		editText = (RichEditText) findViewById(R.id.act_rich_text_editor_rte_editor);
		smallSetter = (RelativeLayout) findViewById(R.id.act_rich_editor_rlyt_text_size_small);
		normalSetter = (RelativeLayout) findViewById(R.id.act_rich_editor_rlyt_text_size_normal);
		largeSetter = (RelativeLayout) findViewById(R.id.act_rich_editor_rlyt_text_size_large);
		colorPicker = (ImageView) findViewById(R.id.act_rich_text_editor_iv_color_picker);
		eraser = (ImageView) findViewById(R.id.act_rich_text_editor_iv_eraser);

		textCounter = (TextView) findViewById(R.id.act_rich_text_editor_tv_text_counter);
		textLimitTip = (TextView) findViewById(R.id.act_rich_text_editor_tv_text_limit);

		back = (Button) findViewById(R.id.act_rich_text_editor_btn_return);
		ok = (Button) findViewById(R.id.act_rich_text_editor_btn_sure);
	}

	private void setListener()
	{
		editText.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable editable)
			{

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				textCounter.setText(String.valueOf(editText.getText().length()));
			}

		});
		colorPicker.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (editText.hasSelection())
				{
					// because the selection will be gone after dialog is opened
					editText.setMarkSelection();
				}
				colorPicker();
			}
		});

		smallSetter.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.setTextSizeSmall();
			}

		});
		normalSetter.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.setTextSizeNormal();
			}

		});
		largeSetter.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.setTextSizeLarge();
			}

		});
		eraser.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.clearFormat();
				initColorPicker();
			}
		});
		back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO a dialog to notify user that he/she will lost the text

				setResult(RESULT_CANCELED);
				finish();
			}
		});
		ok.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				returnTheResult();
			}
		});
	}

	private void initColorPicker()
	{
		colorPicker.setBackgroundColor(editText.getCurrentTextColor());
	}

	public void colorPicker()
	{

		ColorDrawable drawable = (ColorDrawable) colorPicker.getBackground();

		int initialValue = drawable.getColor();

		Log.d("mColorPicker", "initial value:" + initialValue);

		final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);

		colorDialog.setAlphaSliderVisible(false);
		colorDialog.setTitle("Pick a Color!");

		colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						Toast.makeText(RichTextEditorActivity.this,
								"Selected Color: " + colorToHexString(colorDialog.getColor()),
								Toast.LENGTH_LONG).show();
						colorPicker.setBackgroundColor(colorDialog.getColor());
						editText.setTextColor(colorDialog.getColor());
					}
				});

		colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						// Nothing to do here.
					}
				});

		colorDialog.show();
	}

	private String colorToHexString(int color)
	{

		return String.format("#%06X", 0xFFFFFFFF & color);
	}

	private void returnTheResult()
	{
		if (sharedPreferencesName == null)
		{
			sharedPreferencesName = generateSharedPreferencesName();
			Log.d(TAG, "New shared pref name: " + sharedPreferencesName);
		}
		editText.save(sharedPreferencesName);

		Intent intent = new Intent();
		intent.putExtra(EXTRA_KEY_SHARED_PREFERENCES_NAME, sharedPreferencesName);
		setResult(RESULT_OK, intent);
		finish();
	}

	private String generateSharedPreferencesName()
	{
		return TAG + "_" + System.currentTimeMillis();
	}

}

package eoc.studio.voicecard.card.editor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.colorpickerview.dialog.ColorPickerDialog;

public class CardTextEditorActivity extends BaseActivity
{

	public static final String EXTRA_KEY_TEXT_LIMIT = "text_limit";
	public static final String EXTRA_KEY_TEXT_CONTENT = "text_content";
	public static final String EXTRA_KEY_TEXT_SIZE_TYPE = "text_size_type";
	public static final String EXTRA_KEY_TEXT_COLOR = "text_color";

	private static final float TEXT_SIZE_NORMAL = 18;
	private static final float TEXT_SIZE_SMALL = TEXT_SIZE_NORMAL * 0.8f;
	private static final float TEXT_SIZE_LARGE = TEXT_SIZE_NORMAL * 1.2f;

	private static final String TAG = "CardTextEditorActivity";

	private int textLengthLimit = 60;
	private int textSizeType = Card.DEFAULT_TEXT_SIZE_TYPE;
	private int textColor = Card.DEFAULT_TEXT_COLOR;
	private String textContent;

	private EditText editText;
	private RelativeLayout smallSetter;
	private RelativeLayout normalSetter;
	private RelativeLayout largeSetter;
	private ImageView colorPicker;
	private ImageView eraser;

	private TextView textCounter;
	private TextView textLimitTip;

	private Button back;
	private Button ok;

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
		textSizeType = intent.getIntExtra(EXTRA_KEY_TEXT_SIZE_TYPE, textSizeType);
		textColor = intent.getIntExtra(EXTRA_KEY_TEXT_COLOR, textColor);
		textContent = intent.getStringExtra(EXTRA_KEY_TEXT_CONTENT);

		Log.d(TAG, "get config - limit:" + textLengthLimit + ", size type:" + textSizeType
				+ ", color:" + textColor + ", content:" + textContent);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_card_text_editor);
		findViews();
		setListener();
		initColorPicker();
	}

	private void applyConfig()
	{
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(textLengthLimit);
		editText.setFilters(filterArray);

		textLimitTip.setText(getString(R.string.text_limit, textLengthLimit));

		setTextSize(textSizeType);
		setTextColor(textColor);

		if (textContent != null)
		{
			editText.setText(textContent);
		}
	}

	private void findViews()
	{
		editText = (EditText) findViewById(R.id.act_rich_text_editor_rte_editor);
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
				colorPicker();
			}
		});

		smallSetter.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setTextSize(Card.TEXT_SIZE_TYPE_SMALL);
			}

		});
		normalSetter.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setTextSize(Card.TEXT_SIZE_TYPE_NORMAL);
			}

		});
		largeSetter.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setTextSize(Card.TEXT_SIZE_TYPE_LARGE);
			}

		});
		eraser.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setTextSize(Card.DEFAULT_TEXT_SIZE_TYPE);
				setTextColor(Card.DEFAULT_TEXT_COLOR);
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

		Log.d(TAG, "initial value:" + initialValue);

		final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);

		colorDialog.setAlphaSliderVisible(false);
		colorDialog.setTitle("Pick a Color!");

		colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						// Toast.makeText(CardTextEditorActivity.this,
						// "Selected Color: " +
						// colorToHexString(colorDialog.getColor()),
						// Toast.LENGTH_LONG).show();
						colorPicker.setBackgroundColor(colorDialog.getColor());
						setTextColor(colorDialog.getColor());
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

	private void setTextSize(int type)
	{
		textSizeType = type;
		editText.setTextSize(getTextSizeByType(type));
	}

	private void setTextColor(int color)
	{
		textColor = color;
		editText.setTextColor(color);
	}

	private void returnTheResult()
	{
		Intent intent = new Intent();
		intent.putExtra(EXTRA_KEY_TEXT_CONTENT, editText.getText().toString());
		intent.putExtra(EXTRA_KEY_TEXT_COLOR, textColor);
		intent.putExtra(EXTRA_KEY_TEXT_SIZE_TYPE, textSizeType);
		Log.d(TAG, "result - size type:" + textSizeType + ", color:" + textColor + ", content:"
				+ editText.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}
}

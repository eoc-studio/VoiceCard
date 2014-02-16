package eoc.studio.voicecard.richtexteditor;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.colorpickerview.dialog.ColorPickerDialog;

public class RichTextEditorActivity extends BaseActivity
{

//	public static final String EXTRA_KEY_TEXT_LIMIT = "text_limit";
//	public static final String EXTRA_KEY_RETURN_HTML_STRING = "return_html_string";
	private RichEditText editText;
	private Button setSmall;
	private Button setNormal;
	private Button setLarge;
	private ImageView setColor;

	private Button save;
	private Button load;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_rich_text_editor);

		editText = (RichEditText) findViewById(R.id.act_rich_text_editor_rte_editor);
		setSmall = (Button) findViewById(R.id.test_small);
		setNormal = (Button) findViewById(R.id.test_normal);
		setLarge = (Button) findViewById(R.id.test_big);
		setColor = (ImageView) findViewById(R.id.test_color);
		save = (Button) findViewById(R.id.test_save);
		load = (Button) findViewById(R.id.test_load);

		setColor.setBackgroundColor(editText.getCurrentTextColor());
		setColor.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (editText.hasSelection())
				{
					editText.setMarkSelection(); // because the selection will
													// be gone after dialog is
													// opened
				}
				colorPicker();
			}
		});

		setSmall.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.setTextSizeSmall();
			}

		});
		setNormal.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.setTextSizeNormal();
			}

		});
		setLarge.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.setTextSizeLarge();
			}

		});

		save.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.save();
				editText.setText("");
			}

		});
		load.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				editText.load();
			}

		});

		super.onCreate(savedInstanceState);
	}

	public void colorPicker()
	{

		ColorDrawable drawable = (ColorDrawable) setColor.getBackground();

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
						setColor.setBackgroundColor(colorDialog.getColor());
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

}

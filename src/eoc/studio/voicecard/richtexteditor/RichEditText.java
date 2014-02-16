package eoc.studio.voicecard.richtexteditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class RichEditText extends EditText
{

	private static final String SAVE_TAG = "RichEditText";
	private static final String KEY_TEXT = "text";
	private static final String KEY_COLOR_SPAN_COUNT = "color_span_count";
	private static final String KEY_COLOR_SPAN_COLOR = "color_span_color";
	private static final String KEY_COLOR_SPAN_START = "color_span_start";
	private static final String KEY_COLOR_SPAN_END = "color_span_end";
	private static final String KEY_SIZE_SPAN_COUNT = "size_span_count";
	private static final String KEY_SIZE_SPAN_SIZE = "size_span_size";
	private static final String KEY_SIZE_SPAN_START = "size_span_start";
	private static final String KEY_SIZE_SPAN_END = "size_span_end";

	private static final int SIZE_MODE_SMALL = 1;
	private static final int SIZE_MODE_NORMAL = 2;
	private static final int SIZE_MODE_LARGE = 3;

	private int normalTextSize;
	private int sizeMode = SIZE_MODE_NORMAL;

	private int color;

	public RichEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		normalTextSize = (int) getTextSize();
		color = getCurrentTextColor();
		this.addTextChangedListener(new TextWatcher()
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
				switch (sizeMode)
				{
				case SIZE_MODE_SMALL:
					setSizeSmall(start, start + count);
					break;
				case SIZE_MODE_NORMAL:
					setSizeNormal(start, start + count);
					break;
				case SIZE_MODE_LARGE:
					setSizeLarge(start, start + count);
					break;
				}

				setColor(start, start + count, color);
			}

		});
	}

	public void setTextSizeSmall()
	{
		sizeMode = SIZE_MODE_SMALL;
		if (hasSelection())
		{
			setSelectedSizeSmall();
		}
	}

	public void setTextSizeNormal()
	{
		sizeMode = SIZE_MODE_NORMAL;
		if (hasSelection())
		{
			setSelectedSizeNormal();
		}
	}

	public void setTextSizeLarge()
	{
		sizeMode = SIZE_MODE_LARGE;
		if (hasSelection())
		{
			setSelectedSizeLarge();
		}
	}

	public void setTextColor(int color)
	{
		if (hasSelection())
		{
			setSelectedColor(color);
		}
		else if (markSelectionStart < markSelectionEnd)
		{
			this.color = color;
			setColor(markSelectionStart, markSelectionEnd, color);
			clearMarkSelection();
		}
		else
		{
			this.color = color;
		}
	}

	private void setSelectedSizeSmall()
	{
		setSizeSmall(getSelectionStart(), getSelectionEnd());
	}

	private void setSelectedSizeNormal()
	{
		setSizeNormal(getSelectionStart(), getSelectionEnd());
	}

	private void setSelectedSizeLarge()
	{
		setSizeLarge(getSelectionStart(), getSelectionEnd());
	}

	private void setSelectedColor(int color)
	{
		setColor(getSelectionStart(), getSelectionEnd(), color);
	}

	private void setSizeSmall(int start, int end)
	{
		int size = normalTextSize / 2;
		if (size == 0)
		{
			size = 1;
		}
		this.getText().setSpan(new AbsoluteSizeSpan(size), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private void setSizeNormal(int start, int end)
	{
		this.getText().setSpan(new AbsoluteSizeSpan(normalTextSize), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private void setSizeLarge(int start, int end)
	{
		this.getText().setSpan(new AbsoluteSizeSpan(normalTextSize * 2), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private void setColor(int start, int end, int color)
	{
		this.getText().setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private int markSelectionStart;
	private int markSelectionEnd;

	/**
	 * mark will be cleared after setTextColor()
	 */
	public void setMarkSelection()
	{
		markSelectionStart = getSelectionStart();
		markSelectionEnd = getSelectionEnd();
	}

	private void clearMarkSelection()
	{
		markSelectionStart = 0;
		markSelectionEnd = 0;
	}

	public void save()
	{
		SharedPreferences pref = getContext().getSharedPreferences(SAVE_TAG, 0);
		Editor editor = pref.edit();
		SpannableStringBuilder stringBuilder = (SpannableStringBuilder) this.getText();
		editor.putString(KEY_TEXT, this.getText().toString());

		ForegroundColorSpan[] colorSpans = stringBuilder.getSpans(0, this.length(),
				ForegroundColorSpan.class);
		editor.putInt(KEY_COLOR_SPAN_COUNT, colorSpans.length);
		for (int i = 0; i < colorSpans.length; i++)
		{
			int col = colorSpans[i].getForegroundColor();
			int start = stringBuilder.getSpanStart(colorSpans[i]);
			int end = stringBuilder.getSpanEnd(colorSpans[i]);
			editor.putInt(KEY_COLOR_SPAN_COLOR + i, col);
			editor.putInt(KEY_COLOR_SPAN_START + i, start);
			editor.putInt(KEY_COLOR_SPAN_END + i, end);
		}

		AbsoluteSizeSpan[] sizeSpans = stringBuilder.getSpans(0, this.length(),
				AbsoluteSizeSpan.class);
		editor.putInt(KEY_SIZE_SPAN_COUNT, sizeSpans.length);
		for (int i = 0; i < sizeSpans.length; i++)
		{
			int size = sizeSpans[i].getSize();
			int start = stringBuilder.getSpanStart(sizeSpans[i]);
			int end = stringBuilder.getSpanEnd(sizeSpans[i]);
			editor.putInt(KEY_SIZE_SPAN_SIZE + i, size);
			editor.putInt(KEY_SIZE_SPAN_START + i, start);
			editor.putInt(KEY_SIZE_SPAN_END + i, end);
		}
		editor.commit();
	}

	public void load()
	{
		SharedPreferences pref = getContext().getSharedPreferences(SAVE_TAG, 0);
		String string = pref.getString(KEY_TEXT, "");
		this.setText(string);
		int colorSpansCount = pref.getInt(KEY_COLOR_SPAN_COUNT, 0);
		for (int i = 0; i < colorSpansCount; i++)
		{
			int color = pref.getInt(KEY_COLOR_SPAN_COLOR + i, 0);
			int start = pref.getInt(KEY_COLOR_SPAN_START + i, 0);
			int end = pref.getInt(KEY_COLOR_SPAN_END + i, 0);
			setColor(start, end, color);
		}

		int sizeSpansCount = pref.getInt(KEY_SIZE_SPAN_COUNT, 0);
		for (int i = 0; i < sizeSpansCount; i++)
		{
			int size = pref.getInt(KEY_SIZE_SPAN_SIZE + i, 0);
			int start = pref.getInt(KEY_SIZE_SPAN_START + i, 0);
			int end = pref.getInt(KEY_SIZE_SPAN_END + i, 0);
			this.getText().setSpan(new AbsoluteSizeSpan(size), start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
}

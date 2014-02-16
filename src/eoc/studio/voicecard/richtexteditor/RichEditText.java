package eoc.studio.voicecard.richtexteditor;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class RichEditText extends EditText
{

	private static final int SIZE_MODE_SMALL = 1;
	private static final int SIZE_MODE_NORMAL = 2;
	private static final int SIZE_MODE_LARGE = 3;

	private int normalTextSize;
	private int sizeMode = SIZE_MODE_NORMAL;

	private int color;
	private int defaultColor;

	private int textLengthLimit = -1;

	public RichEditText(Context context)
	{
		super(context);
		init();
	}

	public RichEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		normalTextSize = (int) getTextSize();
		color = getCurrentTextColor();
		defaultColor = color;
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

	public void setTextLengthLimit(int limit)
	{
		textLengthLimit = limit;
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(textLengthLimit);
		setFilters(filterArray);
	}

	/**
	 * 
	 * @return -1 means no limit
	 */
	public int getTextLengthLimit()
	{
		return textLengthLimit;
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

	public void clearFormat()
	{
		setTextSizeNormal();
		setTextColor(defaultColor);
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

	public void save(String sharedPreferencesName)
	{
		SharedPreferencesUtil.saveSpannables(this, sharedPreferencesName);
	}

	public void load(String sharedPreferencesName)
	{
		SharedPreferencesUtil.loadSpannables(this, sharedPreferencesName);
	}
}

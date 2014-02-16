package eoc.studio.voicecard.richtexteditor;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

class SharedPreferencesUtil
{
	private static final String KEY_TEXT = "text";
	private static final String KEY_COLOR_SPAN_COUNT = "color_span_count";
	private static final String KEY_COLOR_SPAN_COLOR = "color_span_color";
	private static final String KEY_COLOR_SPAN_START = "color_span_start";
	private static final String KEY_COLOR_SPAN_END = "color_span_end";
	private static final String KEY_SIZE_SPAN_COUNT = "size_span_count";
	private static final String KEY_SIZE_SPAN_SIZE = "size_span_size";
	private static final String KEY_SIZE_SPAN_START = "size_span_start";
	private static final String KEY_SIZE_SPAN_END = "size_span_end";

	public static void saveSpannables(RichEditText richEditText, String sharedPreferencesName)
	{
		SharedPreferences pref = richEditText.getContext().getSharedPreferences(
				sharedPreferencesName, 0);
		Editor editor = pref.edit();
		SpannableStringBuilder stringBuilder = (SpannableStringBuilder) richEditText.getText();
		editor.putString(KEY_TEXT, richEditText.getText().toString());

		ForegroundColorSpan[] colorSpans = stringBuilder.getSpans(0, richEditText.length(),
				ForegroundColorSpan.class);
		editor.putInt(KEY_COLOR_SPAN_COUNT, colorSpans.length);
		int arg, start, end;
		for (int i = 0; i < colorSpans.length; i++)
		{
			arg = colorSpans[i].getForegroundColor();
			start = stringBuilder.getSpanStart(colorSpans[i]);
			end = stringBuilder.getSpanEnd(colorSpans[i]);
			editor.putInt(KEY_COLOR_SPAN_COLOR + i, arg);
			editor.putInt(KEY_COLOR_SPAN_START + i, start);
			editor.putInt(KEY_COLOR_SPAN_END + i, end);
		}

		AbsoluteSizeSpan[] sizeSpans = stringBuilder.getSpans(0, richEditText.length(),
				AbsoluteSizeSpan.class);
		editor.putInt(KEY_SIZE_SPAN_COUNT, sizeSpans.length);
		for (int i = 0; i < sizeSpans.length; i++)
		{
			arg = sizeSpans[i].getSize();
			start = stringBuilder.getSpanStart(sizeSpans[i]);
			end = stringBuilder.getSpanEnd(sizeSpans[i]);
			editor.putInt(KEY_SIZE_SPAN_SIZE + i, arg);
			editor.putInt(KEY_SIZE_SPAN_START + i, start);
			editor.putInt(KEY_SIZE_SPAN_END + i, end);
		}
		editor.commit();
	}

	public static void loadSpannables(RichEditText richEditText, String sharedPreferencesName)
	{
		SharedPreferences pref = richEditText.getContext().getSharedPreferences(sharedPreferencesName,
				0);
		String string = pref.getString(KEY_TEXT, "");
		richEditText.setText(string);
		int colorSpansCount = pref.getInt(KEY_COLOR_SPAN_COUNT, 0);
		for (int i = 0; i < colorSpansCount; i++)
		{
			int color = pref.getInt(KEY_COLOR_SPAN_COLOR + i, 0);
			int start = pref.getInt(KEY_COLOR_SPAN_START + i, 0);
			int end = pref.getInt(KEY_COLOR_SPAN_END + i, 0);
			richEditText.getEditableText().setSpan(new ForegroundColorSpan(color), start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		int sizeSpansCount = pref.getInt(KEY_SIZE_SPAN_COUNT, 0);
		for (int i = 0; i < sizeSpansCount; i++)
		{
			int size = pref.getInt(KEY_SIZE_SPAN_SIZE + i, 0);
			int start = pref.getInt(KEY_SIZE_SPAN_START + i, 0);
			int end = pref.getInt(KEY_SIZE_SPAN_END + i, 0);
			richEditText.getEditableText().setSpan(new AbsoluteSizeSpan(size), start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
}

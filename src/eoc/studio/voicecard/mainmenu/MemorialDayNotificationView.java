package eoc.studio.voicecard.mainmenu;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eoc.studio.voicecard.R;

public class MemorialDayNotificationView extends RelativeLayout
{
	private ImageView month;

	private ImageView dateDigitOnes;

	private ImageView dateDigitTens;

	private TextView descriptionOfNormalDay;

	private TextView descriptionOfMemorialDay;

	public MemorialDayNotificationView(Context context, AttributeSet attrs)
	{

		super(context, attrs);
		initLayout();
	}

	private void initLayout()
	{

		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_memorial_day_notification, this, true);
		month = (ImageView) findViewById(R.id.glb_memorial_day_notification_iv_month);
		dateDigitOnes = (ImageView) findViewById(R.id.glb_memorial_day_notification_iv_date_digit_in_ones_place);
		dateDigitTens = (ImageView) findViewById(R.id.glb_memorial_day_notification_iv_date_digit_in_tens_place);
		descriptionOfNormalDay = (TextView) findViewById(R.id.glb_memorial_day_notification_tv_normal_description);
		descriptionOfMemorialDay = (TextView) findViewById(R.id.glb_memorial_day_notification_tv_special_description);
		
		//test only
		month.setImageLevel(Calendar.APRIL);
		dateDigitTens.setImageLevel(1);
		dateDigitOnes.setImageLevel(3);
	}
}

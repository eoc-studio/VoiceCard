package eoc.studio.voicecard.mainmenu;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eoc.studio.voicecard.R;

public class MailboxIconView extends RelativeLayout
{
	private static final int INDICATOR_NEW_MAIL_TEXTCOLOR = Color.RED;
	private TextView indicator;

	public MailboxIconView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initLayout();
		update(200); // TODO just for test
	}

	private void initLayout()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_mailbox_icon, this, true);
		indicator = (TextView) findViewById(R.id.glb_mailbox_icon_tv_indicator);
	}

	public void update(int numberOfNewMail)
	{
		String prefix = getContext().getString(R.string.mail);
		CharSequence message;
		if (numberOfNewMail > 0)
		{
			SpannableString messageWithColor = new SpannableString(prefix + "(" + numberOfNewMail
					+ ")");
			messageWithColor.setSpan(new ForegroundColorSpan(INDICATOR_NEW_MAIL_TEXTCOLOR),
					prefix.length(), messageWithColor.length(), 0);
			message = messageWithColor;
		}
		else
		{
			message = prefix;
		}
		indicator.setText(message);
	}
}

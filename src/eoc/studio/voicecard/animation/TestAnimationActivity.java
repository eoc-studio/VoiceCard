package eoc.studio.voicecard.animation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;

public class TestAnimationActivity extends BaseActivity
{
	private FlipView card;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_test_animation);
		
		FrameLayout frame = (FrameLayout) findViewById(R.id.card);
		card = new FlipView(this, 380, 250, -45f, 0f, 250/2);
		frame.addView(card);
		TextView front = generateTextView("front 1", Color.BLUE, 20, Color.CYAN);
		TextView back = generateTextView("back 2", Color.RED, 20, Color.MAGENTA);
		TextView inner = generateTextView("inner 3", Color.WHITE, 20, Color.BLACK);
		card.setFrontPage(front);
		card.setBackPage(back);
		card.setInnerPage(inner);
		super.onCreate(savedInstanceState);
	}

	private TextView generateTextView(String msg, int color, int textSize, int bgColor)
	{
		TextView tv = new TextView(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tv.setGravity(Gravity.CENTER);
		tv.setLayoutParams(params);
		tv.setText(msg);
		tv.setTextSize(textSize);
		tv.setTextColor(color);
		tv.setBackgroundColor(bgColor);
		return tv;
	}
}

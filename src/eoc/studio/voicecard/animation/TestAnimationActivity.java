package eoc.studio.voicecard.animation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.animation.FlipView.FlipListener;

public class TestAnimationActivity extends BaseActivity
{
	private static final String TAG = "TestAnimation";
	private FlipView flipShadow;
	private MultiTouchImageView dragZoomShadow;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		initLayout();
		initFlipShadow();
		super.onCreate(savedInstanceState);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_test_animation);
		FrameLayout frame = (FrameLayout) findViewById(R.id.card);
		dragZoomShadow = (MultiTouchImageView) findViewById(R.id.multi_touch_image);
		flipShadow = new FlipView(this, 380, 250, -45f, 0f, 250 / 2);
		frame.addView(flipShadow);
	}

	private void initFlipShadow()
	{
		TextView front = generateTextView("front 1", Color.BLUE, 20, Color.CYAN);
		TextView back = generateTextView("back 2", Color.RED, 20, Color.MAGENTA);
		TextView inner = generateTextView("inner 3", Color.WHITE, 20, Color.BLACK);
		flipShadow.setFrontPage(front);
		flipShadow.setBackPage(back);
		flipShadow.setInnerPage(inner);
		flipShadow.setLockAfterOpened(true);
		flipShadow.setFlipListener(new FlipListener()
		{
			@Override
			public void onOpened()
			{
				Log.d(TAG, "card opened");
				flipShadow.setDrawingCacheEnabled(true);
				initDragZoomShadow(createBitmapCopy(flipShadow.getDrawingCache()));
				flipShadow.setVisibility(View.GONE);
			}

			@Override
			public void onClosed()
			{
			}
		});
	}

	private void initDragZoomShadow(Bitmap bitmap)
	{
		dragZoomShadow.setImageBitmap(bitmap);
		dragZoomShadow.setVisibility(View.VISIBLE);
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

	private static Bitmap createBitmapCopy(Bitmap source)
	{
		return Bitmap.createScaledBitmap(source, source.getWidth(), source.getHeight(), false);
	}
}

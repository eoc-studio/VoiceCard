package eoc.studio.voicecard.animation;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.animation.FlipView.FlipListener;

public class TestAnimationActivity extends BaseActivity
{
	private static final String TAG = "TestAnimation";
	private HorizontalScrollView scrollView;
	private FlipView flipView;
	private FrameLayout flipViewWrapper;

	private int cardPageWidth;
	private int cardPageHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		initLayout();
		initFlipView();
		super.onCreate(savedInstanceState);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_test_animation);
		flipViewWrapper = (FrameLayout) findViewById(R.id.card);

		// Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.card08_cover);
		// cardPageWidth = bitmap.getWidth();
		// cardPageHeight = bitmap.getHeight();
		scrollView = (HorizontalScrollView) findViewById(R.id.act_test_animation_hsv_root);
		cardPageWidth = (int) (getResources().getDimensionPixelSize(R.dimen.card_open_page_width) / 2.f);
		cardPageHeight = (int) (getResources().getDimensionPixelSize(R.dimen.card_open_page_height) * 1.4f);
	}

	private void initFlipView()
	{

		flipView = new FlipView(this, cardPageWidth * 2, cardPageHeight, -12f, 0f,
				cardPageWidth / 2);
		flipViewWrapper.addView(flipView);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		ImageView front = new ImageView(this);
		front.setLayoutParams(params);
		front.setScaleType(ScaleType.FIT_XY);
		front.setImageResource(R.drawable.card08_cover);
		ImageView back = new ImageView(this);
		back.setLayoutParams(params);
		back.setScaleType(ScaleType.FIT_XY);
		back.setImageResource(R.drawable.card08_left);
		ImageView inner = new ImageView(this);
		inner.setLayoutParams(params);
		inner.setScaleType(ScaleType.FIT_XY);
		inner.setImageResource(R.drawable.card08_right);

		flipView.setFrontPage(front);
		flipView.setBackPage(back);
		flipView.setInnerPage(inner);
		// flipShadow.setLockAfterOpened(true);
		flipView.setFlipListener(new FlipListener()
		{
			@Override
			public void onOpened()
			{
			}

			@Override
			public void onClosed()
			{
			}

			@Override
			public void onStartOpening()
			{
				scrollView.postDelayed(new Runnable()
				{
					public void run()
					{
						scrollView.smoothScrollTo(0, 0);
					}
				}, 200L);
			}

			@Override
			public void onStartClosing()
			{
				scrollView.postDelayed(new Runnable()
				{
					public void run()
					{
						scrollView.smoothScrollTo(cardPageWidth * 2, 0);
					}
				}, 200L);
			}
		});

		scrollView.postDelayed(new Runnable()
		{
			public void run()
			{
				scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 100L);
	}

}

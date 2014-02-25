package eoc.studio.voicecard.animation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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
	private ImageView shadowOpen;
	private ImageView shadowClose;

	private int cardPageWidth;
	private int cardPageHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		initLayout();
		initFlipAndShadow();
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
		shadowOpen = (ImageView) findViewById(R.id.act_test_animation_iv_card_open_shadow);
		shadowClose = (ImageView) findViewById(R.id.act_test_animation_iv_card_close_shadow);
		cardPageWidth = (int) (getResources().getDimensionPixelSize(R.dimen.card_open_width) / 2.f * 0.955f);
		cardPageHeight = (int) (getResources().getDimensionPixelSize(R.dimen.card_height) * 1.42f);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.card08_open);

		Bitmap halfShadow = Bitmap.createBitmap(bmp, bmp.getWidth() / 2 - 10, 0,
				bmp.getWidth() / 2, bmp.getHeight());
		shadowClose.setImageBitmap(halfShadow);
		bmp.recycle();
	}

	private void initFlipAndShadow()
	{
		shadowOpen.setVisibility(View.INVISIBLE);
		shadowClose.setVisibility(View.VISIBLE);

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
				shadowOpen.setVisibility(View.VISIBLE);
				shadowClose.setVisibility(View.INVISIBLE);
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
				shadowOpen.setVisibility(View.INVISIBLE);
				shadowClose.setVisibility(View.VISIBLE);
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

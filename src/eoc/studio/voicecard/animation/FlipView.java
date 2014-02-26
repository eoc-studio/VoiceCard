package eoc.studio.voicecard.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FlipView extends RelativeLayout
{
	private static final String TAG = "FlipView";
	private static final long DEFAULT_FLIP_DURATION_MS = 400;
	private static final float DEFAULT_ROTATE_BEGIN = -45f;
	private static final float DEFAULT_ROTATE_END = 0;
	private final Interpolator ACCELERATOR = new AccelerateInterpolator();
	private final Interpolator DECELERATOR = new DecelerateInterpolator();
	private boolean isFlipping = false;
	private boolean isOpened = false;
	private boolean isLockAfterOpened = false;
	private FrameLayout frontPage;
	private FrameLayout backPage;
	private FrameLayout innerPage;
	private int pageWidth;
	private int pageHeight;
	private long flipDuration = DEFAULT_FLIP_DURATION_MS;
	private float pivotY;
	private final float[] ROTATE_FRONT_DEGREE1 = { DEFAULT_ROTATE_BEGIN, -90f };
	private final float[] ROTATE_FRONT_DEGREE2 = { 90f, DEFAULT_ROTATE_END };
	private final float[] ROTATE_BACK_DEGREE1 = { DEFAULT_ROTATE_END, 90f };
	private final float[] ROTATE_BACK_DEGREE2 = { -90f, DEFAULT_ROTATE_BEGIN };
	
	private void changeRotateDegree(float begin, float end) {
		ROTATE_FRONT_DEGREE1[0] = begin;
		ROTATE_FRONT_DEGREE2[1] = end;
		ROTATE_BACK_DEGREE1[0] = end;
		ROTATE_BACK_DEGREE2[1] = begin;
	}
	
	private final OnTouchListener touchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (!(isLockAfterOpened && isOpened))
			{
				flip(frontPage, backPage);
			}
			return false;
		}
	};
	private FlipListener mFlipListener = null;

	public interface FlipListener
	{
		public void onOpened();

		public void onClosed();

		public void onStartOpening();

		public void onStartClosing();
	}

	public FlipView(Context context, int width, int height, float rotationBegin, float rotationEnd,
			float pivotY)
	{
		super(context);
		changeRotateDegree(rotationBegin, rotationEnd);
		this.pivotY = pivotY;
		LayoutParams params = new LayoutParams(width, height);
		setLayoutParams(params);
		frontPage = new FrameLayout(context);
		backPage = new FrameLayout(context);
		innerPage = new FrameLayout(context);
		pageWidth = width / 2;
		pageHeight = height * 2 / 3;
		Log.d(TAG, "page w,h " + pageWidth + ", " + pageHeight);
		LayoutParams oddParams = new LayoutParams(pageWidth, pageHeight);
		oddParams.addRule(CENTER_VERTICAL);
		oddParams.addRule(ALIGN_PARENT_RIGHT);
		oddParams.leftMargin = pageWidth;
		frontPage.setLayoutParams(oddParams);
		innerPage.setLayoutParams(oddParams);
		LayoutParams evenParams = new LayoutParams(pageWidth, pageHeight);
		evenParams.addRule(CENTER_VERTICAL);
		evenParams.addRule(ALIGN_PARENT_LEFT);
		backPage.setLayoutParams(evenParams);
		backPage.setVisibility(INVISIBLE);
		frontPage.setOnTouchListener(touchListener);
		backPage.setOnTouchListener(touchListener);
		frontPage.setPivotY(pivotY);
		frontPage.setRotationY(rotationBegin);
		backPage.setPivotY(pivotY);
		backPage.setRotationY(rotationBegin);
		addView(innerPage);
		addView(backPage);
		addView(frontPage);
	}

	public void setFrontPage(View v)
	{
		frontPage.removeAllViews();
		frontPage.addView(v);
	}

	public View getFrontPage()
	{
		return frontPage.getChildAt(0);
	}

	public void setBackPage(View v)
	{
		backPage.removeAllViews();
		backPage.addView(v);
	}

	public View getBackPage()
	{
		return backPage.getChildAt(0);
	}

	public void setInnerPage(View v)
	{
		innerPage.removeAllViews();
		innerPage.addView(v);
	}

	public View getInnerPage()
	{
		return innerPage.getChildAt(0);
	}

	public void setDuration(long duration)
	{
		flipDuration = duration;
	}

	public long getDuration()
	{
		return flipDuration;
	}

	public boolean isOpened()
	{
		return isOpened;
	}

	public boolean isFlipping()
	{
		return isFlipping;
	}

	public void setLockAfterOpened(boolean lock)
	{
		isLockAfterOpened = lock;
	}

	public void setFlipListener(FlipListener listener)
	{
		mFlipListener = listener;
	}

	private void flip(final View front, final View back)
	{
		if (isFlipping) { return; }
		isFlipping = true;
		final View visiblePage, invisiblePage;
		float[] animation1Degree, animation2Degree;
		final boolean isOpening; // true: opening, false: closing
		if (front.getVisibility() == VISIBLE)
		{
			isOpening = true;
			animation1Degree = ROTATE_FRONT_DEGREE1;
			animation2Degree = ROTATE_FRONT_DEGREE2;
			visiblePage = front;
			invisiblePage = back;
			visiblePage.setPivotX(0);
			invisiblePage.setPivotX(pageWidth);
		}
		else
		{
			isOpening = false;
			animation1Degree = ROTATE_BACK_DEGREE1;
			animation2Degree = ROTATE_BACK_DEGREE2;
			visiblePage = back;
			invisiblePage = front;
			visiblePage.setPivotX(pageWidth);
			invisiblePage.setPivotX(0);
		}
		visiblePage.setPivotY(pivotY);
		invisiblePage.setPivotY(pivotY);
		ObjectAnimator animation1 = ObjectAnimator.ofFloat(visiblePage, "rotationY",
				animation1Degree);
		animation1.setDuration(flipDuration);
		animation1.setInterpolator(ACCELERATOR);
		final ObjectAnimator animation2 = ObjectAnimator.ofFloat(invisiblePage, "rotationY",
				animation2Degree);
		animation2.setDuration(flipDuration);
		animation2.setInterpolator(DECELERATOR);
		animation1.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator anim)
			{
				visiblePage.setVisibility(View.INVISIBLE);
				invisiblePage.setVisibility(View.VISIBLE);
				animation2.start();
			}
		});
		animation2.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator anim)
			{
				isFlipping = false;
				isOpened = isOpening;
				if (mFlipListener != null)
				{
					if (isOpened)
					{
						mFlipListener.onOpened();
					}
					else
					{
						mFlipListener.onClosed();
					}
				}
			}
		});
		animation1.start();
		if (mFlipListener != null)
		{
			if (isOpening)
			{
				mFlipListener.onStartOpening();
			}
			else
			{
				mFlipListener.onStartClosing();
			}
		}
	}
}

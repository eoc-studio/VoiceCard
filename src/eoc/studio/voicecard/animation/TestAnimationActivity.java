package eoc.studio.voicecard.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;

public class TestAnimationActivity extends BaseActivity implements OnTouchListener
{
	/*
	 * TODO 
	 * 1. bug on first flip
	 * 2. refine duplicate code
	 * 3. extends Layout to wrap these animations
	 */
	
	private Interpolator accelerator = new AccelerateInterpolator();
	private Interpolator decelerator = new DecelerateInterpolator();
	private boolean isFlipping = false;
	private View frontPage;
	private View backPage;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_test_animation);
		frontPage = findViewById(R.id.front);
		backPage = findViewById(R.id.back);
		frontPage.setOnTouchListener(this);
		backPage.setOnTouchListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (!isFlipping)
		{
			flip(frontPage, backPage);
		}
		return false;
	}

	private void flip(final View front, final View back)
	{
		isFlipping = true;
		if (front.getVisibility() == View.VISIBLE)
		{
			ObjectAnimator animation1 = ObjectAnimator.ofFloat(front, "rotationY", 0f, -90f);
			front.setRotationY(0);
			front.setPivotX(0);
			animation1.setDuration(400);
			animation1.setInterpolator(accelerator);
			final ObjectAnimator animation2 = ObjectAnimator.ofFloat(back, "rotationY", 90f, 0f);
			back.setPivotX(back.getWidth());
			animation2.setDuration(400);
			animation2.setInterpolator(decelerator);
			animation1.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator anim)
				{
					front.setVisibility(View.INVISIBLE);
					back.setRotationY(0);
					back.setVisibility(View.VISIBLE);
					animation2.start();
				}
			});
			animation2.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator anim)
				{
					isFlipping = false;
				}
			});
			animation1.start();
		}
		else
		{
			ObjectAnimator animation1 = ObjectAnimator.ofFloat(back, "rotationY", 0f, 90f);
			back.setPivotX(back.getWidth());
			animation1.setDuration(400);
			animation1.setInterpolator(accelerator);
			final ObjectAnimator animation2 = ObjectAnimator.ofFloat(front, "rotationY", -90f, 0f);
			front.setPivotX(0);
			animation2.setDuration(400);
			animation2.setInterpolator(decelerator);
			animation1.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator anim)
				{
					back.setVisibility(View.INVISIBLE);
					front.setRotationY(0);
					front.setVisibility(View.VISIBLE);
					animation2.start();
				}
			});
			animation2.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator anim)
				{
					isFlipping = false;
				}
			});
			animation1.start();
		}
	}
}

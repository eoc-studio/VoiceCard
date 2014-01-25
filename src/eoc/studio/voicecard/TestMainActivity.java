package eoc.studio.voicecard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import eoc.studio.voicecard.animation.TestAnimationActivity;
import eoc.studio.voicecard.facebook.TestFacebookActivity;

public class TestMainActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_main);
		findViewById(R.id.test_animation).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, TestAnimationActivity.class);
				startActivity(intent);
			}
		});
		
        findViewById(R.id.test_facebook).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(TestMainActivity.this, TestFacebookActivity.class);
                startActivity(intent);
            }
        });
	}
}
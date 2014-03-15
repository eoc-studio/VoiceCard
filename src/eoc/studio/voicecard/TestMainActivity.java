package eoc.studio.voicecard;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import eoc.studio.voicecard.animation.TestAnimationActivity;
import eoc.studio.voicecard.audio.AudioRecorderActivity;
import eoc.studio.voicecard.calendarview.MainCalendarView;
import eoc.studio.voicecard.contact.ContactActivity;
import eoc.studio.voicecard.facebook.TestFacebookActivity;
import eoc.studio.voicecard.mailbox.MailboxActivity;
import eoc.studio.voicecard.mainloading.MainLoadingActivity;
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import eoc.studio.voicecard.manufacture.EditSignatureActivity;
import eoc.studio.voicecard.recommend.RecommendActivity;
import eoc.studio.voicecard.utils.FileUtility;
import eoc.studio.voicecard.volley.test.JsonTestActivity;
import eoc.studio.voicecard.volley.test.PostCardTestActivity;

public class TestMainActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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
		findViewById(R.id.test_signature).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View test_signature)
			{
				Intent intent = new Intent(TestMainActivity.this, EditSignatureActivity.class);
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
		findViewById(R.id.test_web_api).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, JsonTestActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_contacts).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, ContactActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_memorial_day).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, MainCalendarView.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_main_menu).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, MainMenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_recommend).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, RecommendActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_mailbox).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, MailboxActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_mainloading).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, MainLoadingActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.test_post_card).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, PostCardTestActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_recording).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// File cacheDir = TestMainActivity.this.getCacheDir();
				// String fileName = FileUtility.getRandomSpeechName("3gp");
				File extDir = Environment.getExternalStorageDirectory();
				String fileName = "test_audio.3gp";
				Intent intent = new Intent(TestMainActivity.this, AudioRecorderActivity.class);
				intent.putExtra(AudioRecorderActivity.EXTRA_KEY_FILEPATH, extDir.getAbsolutePath()
						+ "/" + fileName);
				startActivityForResult(intent, 5566);
			}
		});

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 5566)
		{
			if (resultCode == RESULT_OK)
			{
				Toast.makeText(
						this,
						"record done: "
								+ data.getStringExtra(AudioRecorderActivity.EXTRA_KEY_FILEPATH)
								+ ", duration: "
								+ data.getIntExtra(AudioRecorderActivity.EXTRA_KEY_DURATION_MILLISECOND, 0)
								+ " ms", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this, "record cancelled", Toast.LENGTH_LONG).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}

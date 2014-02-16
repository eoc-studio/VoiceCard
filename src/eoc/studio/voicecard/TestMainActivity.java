package eoc.studio.voicecard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import eoc.studio.voicecard.animation.TestAnimationActivity;
import eoc.studio.voicecard.calendarview.CalendarViewTest;
import eoc.studio.voicecard.contact.ContactActivity;
import eoc.studio.voicecard.facebook.TestFacebookActivity;
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import eoc.studio.voicecard.manufacture.EditSignatureActivity;
import eoc.studio.voicecard.richtexteditor.RichTextEditorActivity;

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
				Intent intent = new Intent(TestMainActivity.this, CalendarViewTest.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_rich_text_editor).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, RichTextEditorActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.test_main_menu).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TestMainActivity.this, MainMenuActivity.class);
				startActivity(intent);
			}
		});
		super.onCreate(savedInstanceState);
	}
}

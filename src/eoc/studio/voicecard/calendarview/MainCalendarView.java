package eoc.studio.voicecard.calendarview;

import eoc.studio.voicecard.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainCalendarView extends FragmentActivity implements OnClickListener
{
	private static Button btnBack, btnAddFacebookFriendsBirthday, btnBackMainMenu;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_calendar_view);
		findView();
		buttonFunction();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void findView()
	{
		btnBack = (Button) findViewById(R.id.btnBack);
		btnAddFacebookFriendsBirthday = (Button) findViewById(R.id.btnAddFacebookFriends);
		btnBackMainMenu = (Button) findViewById(R.id.btnBackMainMenu);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void buttonFunction()
	{
		btnBack.setOnClickListener(this);
		btnAddFacebookFriendsBirthday.setOnClickListener(this);
		btnBackMainMenu.setOnClickListener(this);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.btnBack:
		{
			System.out.println("*****************btnBack");
		}
			break;
		case R.id.btnAddFacebookFriends:
		{
			System.out.println("*****************btnAddFacebookFriends");
		}
			break;
		case R.id.btnBackMainMenu:
		{
			System.out.println("*****************btnBackMainMenu");
		}
			break;
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

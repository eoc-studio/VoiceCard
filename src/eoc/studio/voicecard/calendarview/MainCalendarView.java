package eoc.studio.voicecard.calendarview;

import java.util.ArrayList;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.facebook.FriendInfo;
import eoc.studio.voicecard.facebook.SelectFriendActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainCalendarView extends FragmentActivity implements OnClickListener
{
	private static String TAG = "MainCalendarView";
	private static Context mContext;
	private static Button btnBack, btnAddFacebookFriendsBirthday, btnBackMainMenu;
    
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_calendar_view);
		mContext = this;
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
                addFacebookFriends();
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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
            case FriendInfo.GET_FRIEND_REQUEST_CODE:
                if (data != null)
                {
                    Bundle bundle = data.getExtras();
                    if (bundle != null)
                    {
                        ArrayList<FriendInfo> friendList = bundle
                        .getParcelableArrayList(FriendInfo.GET_FRIEND);
                        if (friendList != null)
                        {
                            for (int i = 0; i < friendList.size(); i++)
                            {
                                if (friendList.get(i).getFriendBirthday() != null
									&& !friendList.get(i).getFriendBirthday().equals("null"))
                                {
                                    String getNewFormatOfBirthday = DataProcess.formatDate(DataProcess
                                                                                           .getDataMilliSeconds(friendList.get(i).getFriendBirthday(),
                                                                                                                "MM/dd/yyyy"), "yyyyMMdd");
                                    DataProcess.addEvent(mContext, friendList.get(i).getFriendName()
                                                         + "'s Birthday", getNewFormatOfBirthday);
                                    //
                                    Log.d(TAG, "ID is " + friendList.get(i).getFriendId());
                                    Log.d(TAG, "Name is " + friendList.get(i).getFriendName());
                                    Log.d(TAG, "Birthday is " + friendList.get(i).getFriendBirthday());
                                    Log.d(TAG, "ImgLink is " + friendList.get(i).getFriendImgLink());
                                }
                            }
                        }
                    }
                }
                break;
		}
	}
    
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void addFacebookFriends()
	{
		Intent intent = new Intent(MainCalendarView.mContext, SelectFriendActivity.class);
		((Activity) mContext).startActivityForResult(intent, FriendInfo.GET_FRIEND_REQUEST_CODE);
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

package eoc.studio.voicecard.newspaper;

import java.util.ArrayList;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.facebook.FacebookManager;
import eoc.studio.voicecard.facebook.enetities.FriendInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NewspaperSendActivity extends Activity implements OnClickListener
{
    private static RelativeLayout mSaveButton, mSelectFriendButton, mSendButton;
    private static ImageView mMainImageView;
    private static Button mBackBtnView;

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_newspaper_view);
        findView();
        buttonFunction();
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findView()
    {
        mBackBtnView = (Button) findViewById(R.id.backBtnView);
        mMainImageView = (ImageView) findViewById(R.id.mainImageView);
        mSaveButton = (RelativeLayout) findViewById(R.id.saveButton);
        mSelectFriendButton = (RelativeLayout) findViewById(R.id.selectFriendButton);
        mSendButton = (RelativeLayout) findViewById(R.id.sendButton);

        if (ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE != null)
        {
            mMainImageView.setImageBitmap(ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE);
        }
        else
        {
            finish();
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void buttonFunction()
    {
        mBackBtnView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mSelectFriendButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.backBtnView:
            {
                if (ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE != null)
                {
                    ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE.recycle();
                    ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE = null;
                }
                finish();
            }
                break;
            case R.id.saveButton:
            {
                if (DrawableProcess.saveBitmap(this, ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE))
                {
                    Toast.makeText(this, getResources().getString(R.string.news_save_ok), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(this, getResources().getString(R.string.news_save_fail), Toast.LENGTH_LONG).show();
                }
            }
                break;
            case R.id.selectFriendButton:
            {
                addFacebookFriends();
                System.out.println("*****************magazineImageButton");
            }
                break;
            case R.id.sendButton:
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
                        ArrayList<FriendInfo> friendList = bundle.getParcelableArrayList(FriendInfo.GET_FRIEND);
//                        if (friendList != null)
//                        {
//                            int j = 1;
//                            Log.d(TAG, "********Total Count" + friendList.size());
//                            for (int i = 0; i < friendList.size(); i++)
//                            {
//                                if (friendList.get(i).getFriendBirthday() != null
//                                        && !friendList.get(i).getFriendBirthday().equals("null"))
//                                {
//                                    String getNewFormatOfBirthday = DataProcess.formatDate(DataProcess
//                                            .checkFacebookBirthdayFormats(friendList.get(i).getFriendBirthday()),
//                                            "yyyyMMdd");
//                                    DataProcess.addEvent(this, friendList.get(i).getFriendName() + "'s Birthday",
//                                            getNewFormatOfBirthday);
//                                }
//                            }
//                        }
                    }
                }
                break;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addFacebookFriends()
    {
        FacebookManager.getInstance(this);
    }
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

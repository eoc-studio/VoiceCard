package eoc.studio.voicecard.newspaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.facebook.Session;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.facebook.FacebookManager;
import eoc.studio.voicecard.facebook.enetities.FriendInfo;
import eoc.studio.voicecard.facebook.friends.SelectFriendActivity;
import eoc.studio.voicecard.utils.FileUtility;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Files;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NewspaperSendActivity extends Activity implements OnClickListener
{
    private static Context mContext;
    private static String TAG = "NewspaperSendActivity";
    private static RelativeLayout mSaveButton, mSelectFriendButton, mSendButton;
    private static ImageView mMainImageView;
    private static Button mBackBtnView;
    private static String mFiendId = "";

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
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
            DrawableProcess.saveBitmap(this, ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE);
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
                saveImage();
            }
                break;
            case R.id.selectFriendButton:
            {
                selectFacebookFriends();
            }
                break;
            case R.id.sendButton:
            {
                sendNews();
            }
                break;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        switch (requestCode)
        {
            case FriendInfo.GET_FRIEND_REQUEST_CODE:
                if (data != null)
                {
                    Bundle bundle = data.getExtras();
                    if (bundle != null)
                    {
                        ArrayList<FriendInfo> friendList = bundle.getParcelableArrayList(FriendInfo.GET_FRIEND);
                        if (friendList != null)
                        {
                            int j = 1;
                            for (int i = 0; i < friendList.size(); i++)
                            {
                                Log.d(TAG, "ID is " + friendList.get(i).getFriendId());
                                mFiendId = friendList.get(i).getFriendId();
                            }
                        }
                    }
                }
                break;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void saveImage()
    {
        if (DrawableProcess.saveBitmap(this, ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE,
                DrawableProcess.IS_USER_CALL_FUNCTION))
        {
            Toast.makeText(this, getResources().getString(R.string.news_save_ok), Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.news_save_fail), Toast.LENGTH_LONG).show();
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void selectFacebookFriends()
    {
        Intent intent = new Intent(this, SelectFriendActivity.class);
        startActivityForResult(intent, FriendInfo.GET_FRIEND_REQUEST_CODE);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendNews()
    {
        if (ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE != null && !mFiendId.equals(""))
        {
        	
        	Log.d(TAG, "sendNews()");
            File filePath = new File(DrawableProcess.IMAGE_CACHE_PATH);
            Log.d(TAG, "filePath: "+filePath.getPath());
            String renameDirString = getCacheDir() +"/News";
    		File pathCacheDir = new File(renameDirString);
    		if(!pathCacheDir.exists())pathCacheDir.mkdirs();
            
            File renamefilePath  = new File( renameDirString, FileUtility.getRandomNewsName("jpg"));
            
            try
			{
				copy(filePath,renamefilePath);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            if (renamefilePath.exists())
            {
            	Log.d(TAG, "renamefilePath: "+renamefilePath.getPath());
                FacebookManager.getInstance(mContext).publishNews(this, mFiendId,
                        Uri.parse(renamefilePath.getPath().toString()));
                mFiendId = "";
            }
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.news_no_selection_friend), Toast.LENGTH_LONG).show();
        }
    }
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}

package eoc.studio.voicecard.newspaper;

import eoc.studio.voicecard.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MagazineStyleMainActivity extends Activity implements OnClickListener
{
	private final static String TAG = "MagazineStyleMainActivity";
    private static RelativeLayout newspaperMainView;
    private static Button mBackBtn, mNextBtn;
    private static TextView mTitleTextView;
    private static ImageView mBtnMainPhotoView;

    private static RelativeLayout mMagazineSetBackgroundView;
    private static ImageView mBtnTitleView, mMagazineSetLeftBottomPhotoView;
    private static boolean mIsTitleSetted = false, mIsSetBackground = false, mIsSetLeftBottomPhoto = false;

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getContentView();
        findView();
        buttonFunction();
        super.onCreate(savedInstanceState);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getContentView()
    {
        setContentView(R.layout.activity_magazine_style_main_view);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findView()
    {
        newspaperMainView = (RelativeLayout) findViewById(R.id.newspaperLayoutOfMagazine);
        mBackBtn = (Button) findViewById(R.id.backBtnViewOfMagazine);
        mNextBtn = (Button) findViewById(R.id.nextBtnViewOfMagazine);
        mBtnTitleView = (ImageView) findViewById(R.id.imageTitleViewOfMagazine);
        mTitleTextView = (TextView) findViewById(R.id.titleTextViewOfMagazine);
        mBtnMainPhotoView = (ImageView) findViewById(R.id.imageMainPhotoViewOfMagazine);

        mMagazineSetBackgroundView = (RelativeLayout) findViewById(R.id.magazineSetBackgroundView);
        mMagazineSetLeftBottomPhotoView = (ImageView) findViewById(R.id.magazineSetLeftBottomPhotoView);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void buttonFunction()
    {
        mBackBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mBtnTitleView.setOnClickListener(this);
        mBtnMainPhotoView.setOnClickListener(this);

        ///
        mMagazineSetBackgroundView.setOnClickListener(this);
        mMagazineSetLeftBottomPhotoView.setOnClickListener(this);
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.backBtnViewOfMagazine:
            {
                finish();
            }
                break;
            case R.id.nextBtnViewOfMagazine:
            {
                if (mIsTitleSetted && mIsSetBackground && mIsSetLeftBottomPhoto)
                {
                    ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE = Bitmap.createBitmap(DrawableProcess
                            .takeScreenshot(newspaperMainView));
                    DrawableProcess.sendNews(this);
                }
                else
                {
                    Toast.makeText(this, getResources().getString(R.string.news_no_setted), Toast.LENGTH_LONG).show();
                }
            }
                break;
            case R.id.imageTitleViewOfMagazine:
            {
                DrawableProcess.getTitleView(this);
            }
                break;
            case R.id.imageMainPhotoViewOfMagazine:
            {
            	Log.d(TAG, "onClick() imageMainPhotoViewOfMagazine");
            	DrawableProcess.getImageForBigIcon(this, DrawableProcess.PROCESS_TYPE_MAIN_PHOTO,mBtnMainPhotoView.getWidth(),mBtnMainPhotoView.getHeight());
            }
                break;
            case R.id.magazineSetBackgroundView:
            {
            	Log.d(TAG, "onClick() magazineSetBackgroundView");
                DrawableProcess.getImageForBigIcon(this, DrawableProcess.PROCESS_TYPE_MAIN_PHOTO,mBtnMainPhotoView.getWidth(),mBtnMainPhotoView.getHeight());
            }
                break;
            case R.id.magazineSetLeftBottomPhotoView:
            {
                ValueCacheProcessCenter.callProcessingView = ValueCacheProcessCenter.EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW;
                DrawableProcess.runEditActivity(this, DrawableProcess.PROCESS_TYPE_LEFT_BOTTON_MAIN_VIEW);
            }
                break;
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case DrawableProcess.PROCESS_TYPE_TITLE:
                {
                    if (data == null)
                    {
                        return;
                    }
                    Bundle extras = data.getExtras();
                    if (extras == null)
                    {
                        return;
                    }
                    mTitleTextView.setText("");
                    mBtnTitleView
                            .setBackgroundResource(DrawableProcess.getMagazineDrawable(extras.getInt("titleView")));
                    mIsTitleSetted = true;
                }
                    break;
                case DrawableProcess.PROCESS_TYPE_MAIN_PHOTO:
                    if (DrawableProcess.imageUri != null)
                    {
                        ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE = DrawableProcess.decodeUriAsBitmap(this,
                                DrawableProcess.imageUri);
                        mBtnMainPhotoView.setBackgroundDrawable(DrawableProcess.getBitmapToDrawable(this,
                                ValueCacheProcessCenter.MAIN_PHOTO_BITMAP_CATCHE));
                        mIsSetBackground = true;
                    }
                    break;
                case DrawableProcess.PROCESS_TYPE_LEFT_BOTTON_MAIN_VIEW:
                    mMagazineSetLeftBottomPhotoView.setBackgroundDrawable(DrawableProcess.getBitmapToDrawable(this,
                            ValueCacheProcessCenter.LEFT_BOTTON_MAIN_BITMAP_CATCHE));
                    mIsSetLeftBottomPhoto = true;
                    break;
            }
        }
    }
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

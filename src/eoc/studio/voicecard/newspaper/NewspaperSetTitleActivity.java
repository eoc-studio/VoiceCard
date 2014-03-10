package eoc.studio.voicecard.newspaper;

import eoc.studio.voicecard.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NewspaperSetTitleActivity extends Activity implements OnClickListener, OnTouchListener
{
    private static ImageView mImageTitleView;
    private static ImageView mBackBtn, mOkBtn;
    private static TextView mCountView;
    private static boolean isSmoothScroll = false;
    private static float moveStart = 0, moveEnd = 0;
    private int getPages = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getContentView();
        findView();
        buttonFunction();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getContentView()
    {
        switch (ValueCacheProcessCenter.selectedStyleType)
        {
            case ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER:
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setContentView(R.layout.activity_newspaper_style_set_title_view);
            }
                break;
            case ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE:
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setContentView(R.layout.activity_magazine_style_set_title_view);
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findView()
    {
        switch (ValueCacheProcessCenter.selectedStyleType)
        {
            case ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER:
            {
                mCountView = (TextView) findViewById(R.id.countViewOfNewsPaper);
                mImageTitleView = (ImageView) findViewById(R.id.imageTitleViewOfNewsPaper);
                mBackBtn = (ImageView) findViewById(R.id.setTitleBackBtnViewOfNewsPaper);
                mOkBtn = (ImageView) findViewById(R.id.setTitleOkBtnViewOfNewsPaper);
                mCountView.setText(getCountString(getPages));
            }
                break;
            case ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE:
            {
                mCountView = (TextView) findViewById(R.id.countViewOfMagazine);
                mImageTitleView = (ImageView) findViewById(R.id.imageTitleViewOfMagazine);
                mBackBtn = (ImageView) findViewById(R.id.setTitleBackBtnViewOfMagazine);
                mOkBtn = (ImageView) findViewById(R.id.setTitleOkBtnViewOfMagazine);
                mCountView.setText(getCountString(getPages));
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void buttonFunction()
    {
        mBackBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mImageTitleView.setOnTouchListener(this);
        switch (ValueCacheProcessCenter.selectedStyleType)
        {
            case ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER:
            {
            }
                break;
            case ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE:
            {
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.setTitleBackBtnViewOfNewsPaper:
            case R.id.setTitleBackBtnViewOfMagazine:
            {
                finish();
            }
                break;
            case R.id.setTitleOkBtnViewOfNewsPaper:
            case R.id.setTitleOkBtnViewOfMagazine:
            {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("titleView", getPages);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_MOVE && !isSmoothScroll)
        {
            isSmoothScroll = true;
            moveStart = event.getX();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (getTitleTotleCount() == -1)
            {
                return true;
            }
            isSmoothScroll = false;
            moveEnd = event.getX();
            if (moveStart > moveEnd)
            {
                if (getPages >= (getTitleTotleCount() - 1))
                {
                    return true;
                }
                setImageView(++getPages);
            }
            else
            {
                if (getPages < 1)
                {
                    return false;
                }
                setImageView(--getPages);
            }
            mCountView.setText(getCountString(getPages));
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void setImageView(final int value)
    {
        switch (ValueCacheProcessCenter.selectedStyleType)
        {
            case ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER:
            {
                mImageTitleView.setImageResource(DrawableProcess.getNewspaperDrawable(value));
            }
                break;
            case ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE:
            {
                mImageTitleView.setImageResource(DrawableProcess.getMagazineDrawable(value));
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int getTitleTotleCount()
    {
        switch (ValueCacheProcessCenter.selectedStyleType)
        {
            case ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER:
                return DrawableProcess.NEWSPAPER_STYLE_TITLE_DRAWABLE.length;
            case ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE:
                return DrawableProcess.MAGAZINE_STYLE_TITLE_DRAWABLE.length;
        }
        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static String getCountString(final int value)
    {
        return value + 1 + "/" + getTitleTotleCount();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

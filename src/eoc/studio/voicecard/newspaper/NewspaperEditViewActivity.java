package eoc.studio.voicecard.newspaper;

import eoc.studio.voicecard.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewspaperEditViewActivity extends Activity implements OnClickListener
{
    private static RelativeLayout mSetLeftMainLayoutView, mSetLeftPhotoView;
    private static ImageView mSetLeftEditPhotoIconView, mSetLeftEditTextIconView;
    private static TextView mSetLeftEditTextView;
    private static Button mBackBtn, mOkBtn;

    ///
    private static RelativeLayout mSetLeftBottomMainLayoutView, mSetLeftBottomEditPhotoView1,
            mSetLeftBottomEditPhotoView2;
    private static ImageView mSetLeftBottomEditPhotoIconView1, mSetLeftBottomEditPhotoIconView2;
    private static ImageView mSetLeftBottomEditTextIconView, mSetLeftBottomEditTextIconView2,
            mSetLeftBottomEditTextIconView3, mSetLeftBottomEditTextIconView4;
    private static TextView mSetLeftBottomEditTextView, mSetLeftBottomEditTextView2, mSetLeftBottomEditTextView3,
            mSetLeftBottomEditTextView4;

    private static boolean leftPhotoView = false, leftBottonPhotoView = false, leftBottonPhotoView2 = false;

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
        switch (ValueCacheProcessCenter.callProcessingView)
        {
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_LEFT_MAIN_VIEW:
            {
                setContentView(R.layout.activity_newspaper_type_set_left_view);
            }
                break;
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                setContentView(R.layout.activity_newspaper_type_set_bottom_left_view);
            }
                break;
            case ValueCacheProcessCenter.EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                setContentView(R.layout.activity_magazine_type_set_bottom_left_view);
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void findView()
    {
        switch (ValueCacheProcessCenter.callProcessingView)
        {
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_LEFT_MAIN_VIEW:
            {
                mSetLeftMainLayoutView = (RelativeLayout) findViewById(R.id.setLeftMainLayoutView);
                mSetLeftPhotoView = (RelativeLayout) findViewById(R.id.setLeftPhotoView);
                mSetLeftEditPhotoIconView = (ImageView) findViewById(R.id.setLeftEditPhotoIconView);
                mSetLeftEditTextIconView = (ImageView) findViewById(R.id.setLeftEditTextIconView);
                mSetLeftEditTextView = (TextView) findViewById(R.id.setLeftEditTextView);
            }
                break;
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                mSetLeftBottomMainLayoutView = (RelativeLayout) findViewById(R.id.setLeftBottomMainLayoutView);
                mSetLeftBottomEditPhotoView1 = (RelativeLayout) findViewById(R.id.setLeftBottomEditPhotoView1);
                mSetLeftBottomEditPhotoView2 = (RelativeLayout) findViewById(R.id.setLeftBottomEditPhotoView2);
                mSetLeftBottomEditPhotoIconView1 = (ImageView) findViewById(R.id.setLeftBottomEditPhotoIconView1);
                mSetLeftBottomEditPhotoIconView2 = (ImageView) findViewById(R.id.setLeftBottomEditPhotoIconView2);
                mSetLeftBottomEditTextIconView = (ImageView) findViewById(R.id.setLeftBottomEditTextIconView);
                mSetLeftBottomEditTextView = (TextView) findViewById(R.id.setLeftBottomEditTextView);
            }
                break;
            case ValueCacheProcessCenter.EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                mSetLeftBottomMainLayoutView = (RelativeLayout) findViewById(R.id.setLeftBottomMainLayoutView);
                mSetLeftBottomEditPhotoView1 = (RelativeLayout) findViewById(R.id.setLeftBottomEditPhotoView1);
                mSetLeftBottomEditPhotoView2 = (RelativeLayout) findViewById(R.id.setLeftBottomEditPhotoView2);
                mSetLeftBottomEditPhotoIconView1 = (ImageView) findViewById(R.id.setLeftBottomEditPhotoIconView1);
                mSetLeftBottomEditPhotoIconView2 = (ImageView) findViewById(R.id.setLeftBottomEditPhotoIconView2);

                mSetLeftBottomEditTextIconView = (ImageView) findViewById(R.id.setLeftBottomEditTextIconView);
                mSetLeftBottomEditTextIconView2 = (ImageView) findViewById(R.id.setLeftBottomEditTextIconView2);
                mSetLeftBottomEditTextIconView3 = (ImageView) findViewById(R.id.setLeftBottomEditTextIconView3);
                mSetLeftBottomEditTextIconView4 = (ImageView) findViewById(R.id.setLeftBottomEditTextIconView4);

                mSetLeftBottomEditTextView = (TextView) findViewById(R.id.setLeftBottomEditTextView);
                mSetLeftBottomEditTextView2 = (TextView) findViewById(R.id.setLeftBottomEditTextView2);
                mSetLeftBottomEditTextView3 = (TextView) findViewById(R.id.setLeftBottomEditTextView3);
                mSetLeftBottomEditTextView4 = (TextView) findViewById(R.id.setLeftBottomEditTextView4);
            }
                break;
        }
        mBackBtn = (Button) findViewById(R.id.setButtonBackView);
        mOkBtn = (Button) findViewById(R.id.setButtonConfirmView);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void buttonFunction()
    {
        switch (ValueCacheProcessCenter.callProcessingView)
        {
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_LEFT_MAIN_VIEW:
            {
                mSetLeftPhotoView.setOnClickListener(this);
                mSetLeftEditTextView.setOnClickListener(this);
            }
                break;
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                mSetLeftBottomEditPhotoView1.setOnClickListener(this);
                mSetLeftBottomEditPhotoView2.setOnClickListener(this);
                mSetLeftBottomEditTextView.setOnClickListener(this);
            }
                break;
            case ValueCacheProcessCenter.EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                mSetLeftBottomEditPhotoView1.setOnClickListener(this);
                mSetLeftBottomEditPhotoView2.setOnClickListener(this);
                mSetLeftBottomEditTextView.setOnClickListener(this);
                mSetLeftBottomEditTextView2.setOnClickListener(this);
                mSetLeftBottomEditTextView3.setOnClickListener(this);
                mSetLeftBottomEditTextView4.setOnClickListener(this);
            }
                break;
        }
        mBackBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.setLeftPhotoView:
            {
                DrawableProcess.getImage(this, DrawableProcess.PROCESS_TYPE_LEFT_PHOTO_VIEW);
            }
                break;
            case R.id.setLeftEditTextView:
            {
                ShowDialog.showSetValueDialog(this, mSetLeftEditTextView, mSetLeftEditTextIconView, "", "",
                        mSetLeftEditTextView.getText().toString());
            }
                break;
            ///
            case R.id.setLeftBottomEditPhotoView1:
            {
                DrawableProcess.getImage(this, DrawableProcess.PROCESS_TYPE_LEFT_BOTTON_PHOTO_VIEW1);
            }
                break;
            case R.id.setLeftBottomEditPhotoView2:
            {
                DrawableProcess.getImage(this, DrawableProcess.PROCESS_TYPE_LEFT_BOTTON_PHOTO_VIEW2);
            }
                break;
            case R.id.setLeftBottomEditTextView:
            {
                ShowDialog.showSetValueDialog(this, mSetLeftBottomEditTextView, mSetLeftBottomEditTextIconView, "", "",
                        mSetLeftBottomEditTextView.getText().toString());
            }
                break;
            case R.id.setLeftBottomEditTextView2:
            {
                ShowDialog.showSetValueDialog(this, mSetLeftBottomEditTextView2, mSetLeftBottomEditTextIconView2, "",
                        "", mSetLeftBottomEditTextView2.getText().toString());
            }
                break;
            case R.id.setLeftBottomEditTextView3:
            {
                ShowDialog.showSetValueDialog(this, mSetLeftBottomEditTextView3, mSetLeftBottomEditTextIconView3, "",
                        "", mSetLeftBottomEditTextView3.getText().toString());
            }
                break;
            case R.id.setLeftBottomEditTextView4:
            {
                ShowDialog.showSetValueDialog(this, mSetLeftBottomEditTextView4, mSetLeftBottomEditTextIconView4, "",
                        "", mSetLeftBottomEditTextView4.getText().toString());
            }
                break;
            ///
            case R.id.setButtonBackView:
            {
                finish();
            }
                break;
            case R.id.setButtonConfirmView:
            {
                if (!leftPhotoView && !leftBottonPhotoView && !leftBottonPhotoView2)
                {
                    Toast.makeText(this, getResources().getString(R.string.news_no_setted), Toast.LENGTH_LONG).show();
                }
                else
                {
                    leftPhotoView = false;
                    leftBottonPhotoView = false;
                    leftBottonPhotoView2 = false;
                    runButtonConfirmFunction();
                }
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void runButtonConfirmFunction()
    {
        switch (ValueCacheProcessCenter.callProcessingView)
        {
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_LEFT_MAIN_VIEW:
            {
                ValueCacheProcessCenter.LEFT_MAIN_BITMAP_CATCHE = Bitmap.createBitmap(DrawableProcess
                        .takeScreenshot(mSetLeftMainLayoutView));
            }
                break;
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                ValueCacheProcessCenter.LEFT_BOTTON_MAIN_BITMAP_CATCHE = Bitmap.createBitmap(DrawableProcess
                        .takeScreenshot(mSetLeftBottomMainLayoutView));
            }
                break;
            case ValueCacheProcessCenter.EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                ValueCacheProcessCenter.LEFT_BOTTON_MAIN_BITMAP_CATCHE = Bitmap.createBitmap(DrawableProcess
                        .takeScreenshot(mSetLeftBottomMainLayoutView));
            }
                break;
        }
        setResultIntent();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setResultIntent()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case DrawableProcess.PROCESS_TYPE_LEFT_PHOTO_VIEW:
                    mSetLeftEditPhotoIconView.setVisibility(View.INVISIBLE);
                    if (DrawableProcess.imageUri != null)
                    {
                        ValueCacheProcessCenter.LEFT_PHOTO_BITMAP_CATCHE = DrawableProcess.decodeUriAsBitmap(this,
                                DrawableProcess.imageUri);
                        mSetLeftPhotoView.setBackgroundDrawable(DrawableProcess.getBitmapToDrawable(this,
                                ValueCacheProcessCenter.LEFT_PHOTO_BITMAP_CATCHE));
                        leftPhotoView = true;
                    }
                    break;
                case DrawableProcess.PROCESS_TYPE_LEFT_BOTTON_PHOTO_VIEW1:
                    mSetLeftBottomEditPhotoIconView1.setVisibility(View.INVISIBLE);
                    if (DrawableProcess.imageUri != null)
                    {
                        ValueCacheProcessCenter.LEFT_BOTTON_PHOTO_BITMAP_CATCHE1 = DrawableProcess.decodeUriAsBitmap(
                                this, DrawableProcess.imageUri);
                        mSetLeftBottomEditPhotoView1.setBackgroundDrawable(DrawableProcess.getBitmapToDrawable(this,
                                ValueCacheProcessCenter.LEFT_BOTTON_PHOTO_BITMAP_CATCHE1));
                        leftBottonPhotoView = true;
                    }
                    break;
                case DrawableProcess.PROCESS_TYPE_LEFT_BOTTON_PHOTO_VIEW2:
                    mSetLeftBottomEditPhotoIconView2.setVisibility(View.INVISIBLE);
                    if (DrawableProcess.imageUri != null)
                    {
                        ValueCacheProcessCenter.LEFT_BOTTON_PHOTO_BITMAP_CATCHE2 = DrawableProcess.decodeUriAsBitmap(
                                this, DrawableProcess.imageUri);
                        mSetLeftBottomEditPhotoView2.setBackgroundDrawable(DrawableProcess.getBitmapToDrawable(this,
                                ValueCacheProcessCenter.LEFT_BOTTON_PHOTO_BITMAP_CATCHE2));
                        leftBottonPhotoView2 = true;
                    }
                    break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

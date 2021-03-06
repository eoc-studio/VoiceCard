package eoc.studio.voicecard.newspaper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import eoc.studio.voicecard.R;

public class DrawableProcess
{
	private final static String TAG = "DrawableProcess";
    protected static final String IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory() + "/Screenshot.jpg";
	
	
    protected static final String IMAGE_DEFAULT_PATH = Environment.getExternalStorageDirectory() + "/DCIM/";
    protected static final int[] NEWSPAPER_STYLE_TITLE_DRAWABLE = new int[]
    { R.drawable.news_t01, R.drawable.news_t02, R.drawable.news_t03, R.drawable.news_t04, R.drawable.news_t05 };
    protected static final int[] MAGAZINE_STYLE_TITLE_DRAWABLE = new int[]
    { R.drawable.mag_t01, R.drawable.mag_t02, R.drawable.mag_t03, R.drawable.mag_t04, R.drawable.mag_t05 };

    protected static final int PROCESS_TYPE_TITLE = 1, PROCESS_TYPE_MAIN_PHOTO = 2, PROCESS_TYPE_LEFT_MAIN_VIEW = 3,
            PROCESS_TYPE_LEFT_PHOTO_VIEW = 4, PROCESS_TYPE_LEFT_BOTTON_MAIN_VIEW = 5,
            PROCESS_TYPE_LEFT_BOTTON_PHOTO_VIEW1 = 6, PROCESS_TYPE_LEFT_BOTTON_PHOTO_VIEW2 = 7;
    protected static final boolean IS_USER_CALL_FUNCTION = true, IS_NOT_USER_CALL_FUNCTION = false;

    protected static Uri imageUri;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static int getNewspaperDrawable(int value)
    {
        return NEWSPAPER_STYLE_TITLE_DRAWABLE[value];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static int getMagazineDrawable(int value)
    {
        return MAGAZINE_STYLE_TITLE_DRAWABLE[value];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static Bitmap takeScreenshot(final View view)
    {
        if (view == null)
        {
            return null;
        }

        ///reset
        view.setDrawingCacheEnabled(false);

        view.setDrawingCacheEnabled(true);
        view.refreshDrawableState();
        return Bitmap.createBitmap(checkNeedHideView(view).getDrawingCache());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static boolean saveBitmap(Context context, Bitmap bitmap, boolean userUse)
    {
        if (bitmap == null)
        {
            return false;
        }

        File imagePath = null;
        if (userUse)
        {
            imagePath = new File(IMAGE_DEFAULT_PATH + getDateTime() + ".jpg");
        }
        else
        {
            imagePath = new File(IMAGE_CACHE_PATH);
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close(); 
            if (userUse)
            {
                reScanMedia(context, imagePath);
            }
            return true;
        }
        catch (FileNotFoundException e)
        {
            return false;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static boolean saveBitmap(Context context, Bitmap bitmap)
    {
        return saveBitmap(context, bitmap, IS_NOT_USER_CALL_FUNCTION);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static BitmapDrawable getBitmapToDrawable(final Context context, final Bitmap bitmap)
    {
        BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
        return bd;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void getTitleView(Activity activity)
    {
        Intent SetTitleView = new Intent();
        SetTitleView.setClass(activity, NewspaperSetTitleActivity.class);
        activity.startActivityForResult(SetTitleView, PROCESS_TYPE_TITLE);
        activity.overridePendingTransition(0, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void getLeftMainView(Activity activity)
    {
        Intent SetLeftMainView = new Intent();
        SetLeftMainView.setClass(activity, NewspaperEditViewActivity.class);
        activity.startActivityForResult(SetLeftMainView, PROCESS_TYPE_LEFT_MAIN_VIEW);
        activity.overridePendingTransition(0, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void getBottomLeftMainView(Activity activity)
    {
        Intent SetBottomLeftMainView = new Intent();
        SetBottomLeftMainView.setClass(activity, NewspaperEditViewActivity.class);
        activity.startActivityForResult(SetBottomLeftMainView, PROCESS_TYPE_LEFT_BOTTON_MAIN_VIEW);
        activity.overridePendingTransition(0, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void sendNews(Activity activity)
    {
        Intent sendNews = new Intent();
        sendNews.setClass(activity, NewspaperSendActivity.class);
        activity.startActivity(sendNews);
        activity.overridePendingTransition(0, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void runEditActivity(Activity activity, int type)
    {
        Intent RunEditActivity = new Intent();
        RunEditActivity.setClass(activity, NewspaperEditViewActivity.class);
        activity.startActivityForResult(RunEditActivity, type);
        activity.overridePendingTransition(0, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void getImage(final Activity activity, final int requestCode)
    {
        imageUri = null;
        imageUri = Uri.fromFile(new File(IMAGE_CACHE_PATH));
        if (imageUri == null)
        {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", 520);
        intent.putExtra("outputY", 260);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(intent, requestCode);
    }

    //@bruce for fixed aspect ratio
    protected static void getImageForSmallIcon(final Activity activity, final int requestCode, int width, int height)
    {
    	Log.d(TAG, "getImage width: "+ width+",height: "+height);
        imageUri = null;
        imageUri = Uri.fromFile(new File(IMAGE_CACHE_PATH));
        if (imageUri == null)
        {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", width*6);
        intent.putExtra("outputY", height*6);
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(intent, requestCode);
    }
    
    protected static void getImageForBigIcon(final Activity activity, final int requestCode, int width, int height)
    {
    	Log.d(TAG, "getImage width: "+ width+",height: "+height);
        imageUri = null;
        imageUri = Uri.fromFile(new File(IMAGE_CACHE_PATH));
        if (imageUri == null)
        {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(intent, requestCode);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void getImage(final Activity activity, final int requestCode, Uri uri, int width, int height) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(intent, requestCode);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static Bitmap decodeUriAsBitmap(Context context, Uri uri)
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        return bitmap;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static View checkNeedHideView(final View view)
    {
        switch (ValueCacheProcessCenter.selectedStyleType)
        {
            case ValueCacheProcessCenter.NEWS_STYLE_NEWSPAPER:
            {
                return view;
            }
            case ValueCacheProcessCenter.NEWS_STYLE_MAGAZINE:
            {
                if (checkViewIsNull(view.findViewById(R.id.magazineSetBackgroundView)))
                {
                    view.findViewById(R.id.magazineSetBackgroundView).setVisibility(View.INVISIBLE);
                }
                return view;
            }
        }
        return view;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static boolean checkViewIsNull(View view)
    {
        return view != null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static String getDateTime()
    {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String strDate = sdFormat.format(date);
        return strDate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void reScanMedia(Context context, File file)
    {
        new IntentFilter("android.intent.action.MEDIA_SCANNER_STARTED").addDataScheme("file");
        if (file != null)
        {
            Uri localUri = Uri.fromFile(file);
            Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            localIntent.setData(localUri);
            context.sendBroadcast(localIntent);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

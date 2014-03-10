package eoc.studio.voicecard.newspaper;

import android.graphics.Bitmap;

public class ValueCacheProcessCenter
{
    ///
    protected static int selectedStyleType = -1;
    protected static int callProcessingView = -1;

    ///
    protected static final int NEWS_STYLE_NEWSPAPER = 0, NEWS_STYLE_MAGAZINE = 1;
    ///
    protected static final int EDIT_NEWSPAPER_STYTLE_LEFT_MAIN_VIEW = 0,
            EDIT_NEWSPAPER_STYTLE_BOTTON_LEFT_MAIN_VIEW = 1, EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW = 2;

    ///About Drawable Cache
    protected static Bitmap MAIN_PHOTO_BITMAP_CATCHE = null;
    ///
    protected static Bitmap LEFT_MAIN_BITMAP_CATCHE = null;
    protected static Bitmap LEFT_PHOTO_BITMAP_CATCHE = null;
    ///
    protected static Bitmap LEFT_BOTTON_MAIN_BITMAP_CATCHE = null;
    protected static Bitmap LEFT_BOTTON_PHOTO_BITMAP_CATCHE1 = null;
    protected static Bitmap LEFT_BOTTON_PHOTO_BITMAP_CATCHE2 = null;

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void clearALLCacheValue()
    {
        if (MAIN_PHOTO_BITMAP_CATCHE != null)
        {
            MAIN_PHOTO_BITMAP_CATCHE.recycle();
            MAIN_PHOTO_BITMAP_CATCHE = null;
        }

        if (LEFT_MAIN_BITMAP_CATCHE != null)
        {
            LEFT_MAIN_BITMAP_CATCHE.recycle();
            LEFT_MAIN_BITMAP_CATCHE = null;
        }

        if (LEFT_PHOTO_BITMAP_CATCHE != null)
        {
            LEFT_PHOTO_BITMAP_CATCHE.recycle();
            LEFT_PHOTO_BITMAP_CATCHE = null;
        }

        if (LEFT_BOTTON_MAIN_BITMAP_CATCHE != null)
        {
            LEFT_BOTTON_MAIN_BITMAP_CATCHE.recycle();
            LEFT_BOTTON_MAIN_BITMAP_CATCHE = null;
        }

        if (LEFT_BOTTON_PHOTO_BITMAP_CATCHE1 != null)
        {
            LEFT_BOTTON_PHOTO_BITMAP_CATCHE1.recycle();
            LEFT_BOTTON_PHOTO_BITMAP_CATCHE1 = null;
        }

        if (LEFT_BOTTON_PHOTO_BITMAP_CATCHE2 != null)
        {
            LEFT_BOTTON_PHOTO_BITMAP_CATCHE2.recycle();
            LEFT_BOTTON_PHOTO_BITMAP_CATCHE2 = null;
        }

        selectedStyleType = -1;
        callProcessingView = -1;
    }
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////
}

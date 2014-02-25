package eoc.studio.voicecard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FileUtility
{
	public static Bitmap clearBitmapBackgroudColor(Bitmap originBitmap, int originColor, int newColr)
	{

		int[] allpixels = new int[originBitmap.getHeight() * originBitmap.getWidth()];

		originBitmap.getPixels(allpixels, 0, originBitmap.getWidth(), 0, 0,
				originBitmap.getWidth(), originBitmap.getHeight());

		for (int i = 0; i < originBitmap.getHeight() * originBitmap.getWidth(); i++)
		{

			if (allpixels[i] == originColor) allpixels[i] = newColr;
		}

		originBitmap.setPixels(allpixels, 0, originBitmap.getWidth(), 0, 0,
				originBitmap.getWidth(), originBitmap.getHeight());

		return originBitmap;

	}
	
	public static Bitmap clearBitmapBackgroudColorReplace(Bitmap originBitmap, int originColor,
			int newColr)
	{

		int[] allpixels = new int[originBitmap.getHeight() * originBitmap.getWidth()];

		originBitmap.getPixels(allpixels, 0, originBitmap.getWidth(), 0, 0,
				originBitmap.getWidth(), originBitmap.getHeight());

		for (int i = 0; i < originBitmap.getHeight() * originBitmap.getWidth(); i++)
		{

			int alpha = allpixels[i] & 0xff000000;
			int r = (allpixels[i] >> 16) & 0xff;
			int g = (allpixels[i] >> 8) & 0xff;
			int b = allpixels[i] & 0xff;

			int origin_r = (originColor >> 16) & 0xff;
			int origin_g = (originColor >> 8) & 0xff;
			int origin_b = originColor & 0xff;

			if (r == origin_r && g == origin_g && b == origin_b)
			{
				allpixels[i] = newColr;
			}

		}

		originBitmap.setPixels(allpixels, 0, originBitmap.getWidth(), 0, 0,
				originBitmap.getWidth(), originBitmap.getHeight());

		return originBitmap;

	}
	
	
	
	public static void saveLayoutToFile(Context context, View layout, String savePath,
			String saveFileName)
	{

		// Without it the view will have a dimension of 0,0 and the bitmap
		// will be null
		layout.setDrawingCacheEnabled(true);

		Log.e("FileUtility", "saveLayoutToFile layout.getWidth() "+layout.getWidth()+",layout.getHeight() "+layout.getHeight());
		Bitmap screenshot = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(),
				Bitmap.Config.ARGB_8888);

//		Canvas c = new Canvas(clearBitmapBackgroudColor(screenshot,Color.TRANSPARENT,Color.GRAY));
		Canvas c = new Canvas(clearBitmapBackgroudColor(screenshot,Color.TRANSPARENT,Color.TRANSPARENT));
		
//		c.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
//		Canvas c = new Canvas(screenshot);
//		c.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
		layout.draw(c);
		// clear drawing cache
		layout.setDrawingCacheEnabled(false);

		File pathDir = new File(savePath);
		if(!pathDir.exists())pathDir.mkdirs();

		String fileName = saveFileName;
		File file = new File(pathDir, fileName);
		if (file.exists()) file.delete();
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			screenshot.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();

//			Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//
//			mediaScannerIntent.setData(Uri.parse("file://" + pathDir + "/" + fileName));
//			context.sendBroadcast(mediaScannerIntent);
//
//			Toast.makeText(context, "Your image is saved to sdcard", Toast.LENGTH_LONG).show();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void saveLayoutToFileWithoutScan(View layout, String savePath, String saveFileName)
	{

		// Without it the view will have a dimension of 0,0 and the bitmap
		// will be null
		layout.setDrawingCacheEnabled(true);

		Bitmap screenshot = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(screenshot);
		layout.draw(c);
		// clear drawing cache
		layout.setDrawingCacheEnabled(false);

		File pathDir = new File(savePath);
		pathDir.mkdirs();

		String fileName = saveFileName;
		File file = new File(pathDir, fileName);

		if (file.exists()) file.delete();

		try
		{
			FileOutputStream out = new FileOutputStream(file);
			screenshot.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static void saveTwoLayoutToFile(View layoutButtom,View layoutUp, String savePath, String saveFileName)
	{

		// Without it the view will have a dimension of 0,0 and the bitmap
		// will be null
		layoutButtom.setDrawingCacheEnabled(true);
		layoutUp.setDrawingCacheEnabled(true);
		Bitmap screenshotButtom = Bitmap.createBitmap(layoutButtom.getWidth(), layoutButtom.getHeight(),
				Bitmap.Config.ARGB_8888);
		
		Bitmap screenshotUp = Bitmap.createBitmap(layoutUp.getWidth(), layoutUp.getHeight(),
				Bitmap.Config.ARGB_8888);
		
		
		Canvas cv = new Canvas(screenshotButtom);
		layoutButtom.draw(cv);
		layoutUp.draw(cv);
/*		cv.drawBitmap(screenshotUp, 0, 0, null);*/
		cv.save( Canvas.ALL_SAVE_FLAG );
		cv.restore();//存儲

		// clear drawing cache
		layoutButtom.setDrawingCacheEnabled(false);
		layoutUp.setDrawingCacheEnabled(false);

		File pathDir = new File(savePath);
		pathDir.mkdirs();

		String fileName = saveFileName;
		File file = new File(pathDir, fileName);

		if (file.exists()) file.delete();

		try
		{
			FileOutputStream out = new FileOutputStream(file);
			screenshotButtom.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	
	public static String getRandomImageName(String filenameExtension) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        return sdf.format(date) + "_Image_"+random.nextInt(65535)+"."+filenameExtension;
    }
	
	public static String getRandomSpeechName(String filenameExtension) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        return sdf.format(date) + "_Speech_"+random.nextInt(65535)+"."+filenameExtension;
    }
	
	public static String getRandomSignPositionName(String filenameExtension) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        return sdf.format(date) + "_SignPosition"+random.nextInt(65535)+"."+filenameExtension;
    }
	
	public static String getRandomSignHandWritingName(String filenameExtension) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        return sdf.format(date) + "_SignWriting_"+random.nextInt(65535)+"."+filenameExtension;
    }
	
	public static String getRandomSignCompletedName(String filenameExtension) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random random = new Random();
        return sdf.format(date) + "_SignCompleted_"+random.nextInt(65535)+"."+filenameExtension;
    } 
	
	
	
	
}

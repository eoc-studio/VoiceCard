package eoc.studio.voicecard.utils;

import java.io.File;
import java.io.FileOutputStream;

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

		Canvas c = new Canvas(clearBitmapBackgroudColor(screenshot,Color.TRANSPARENT,Color.GRAY));
		
//		c.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
//		Canvas c = new Canvas(screenshot);
//		c.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
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

			Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

			mediaScannerIntent.setData(Uri.parse("file://" + pathDir + "/" + fileName));
			context.sendBroadcast(mediaScannerIntent);

			Toast.makeText(context, "Your image is saved to sdcard", Toast.LENGTH_LONG).show();

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

}
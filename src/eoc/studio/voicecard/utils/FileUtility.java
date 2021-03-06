package eoc.studio.voicecard.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

		Log.e("FileUtility", "saveLayoutToFile layout.getWidth() " + layout.getWidth()
				+ ",layout.getHeight() " + layout.getHeight());
		Bitmap screenshot = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(),
				Bitmap.Config.ARGB_8888);

		// Canvas c = new
		// Canvas(clearBitmapBackgroudColor(screenshot,Color.TRANSPARENT,Color.GRAY));
		Canvas c = new Canvas(clearBitmapBackgroudColor(screenshot, Color.TRANSPARENT,
				Color.TRANSPARENT));

		// c.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
		// Canvas c = new Canvas(screenshot);
		// c.drawColor(Color.TRANSPARENT,
		// android.graphics.PorterDuff.Mode.CLEAR);
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

	public static void saveTwoLayoutToFile(View layoutButtom, View layoutUp, String savePath,
			String saveFileName)
	{

		// Without it the view will have a dimension of 0,0 and the bitmap
		// will be null
		layoutButtom.setDrawingCacheEnabled(true);
		layoutUp.setDrawingCacheEnabled(true);
		Bitmap screenshotButtom = Bitmap.createBitmap(layoutButtom.getWidth(),
				layoutButtom.getHeight(), Bitmap.Config.ARGB_8888);

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

	public static String getRandomImageName(String filenameExtension)
	{

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		return sdf.format(date) + "_Image_" + random.nextInt(65535) + "." + filenameExtension;
	}

	public static String getRandomSpeechName(String filenameExtension)
	{

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		return sdf.format(date) + "_Speech_" + random.nextInt(65535) + "." + filenameExtension;
	}

	public static String getRandomSignPositionName(String filenameExtension)
	{

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		return sdf.format(date) + "_SignPosition" + random.nextInt(65535) + "." + filenameExtension;
	}

	public static String getRandomSignHandWritingName(String filenameExtension)
	{

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		return sdf.format(date) + "_SignWriting_" + random.nextInt(65535) + "." + filenameExtension;
	}

	public static String getRandomSignCompletedName(String filenameExtension)
	{

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		return sdf.format(date) + "_SignCompleted_" + random.nextInt(65535) + "."
				+ filenameExtension;
	}
	
	public static String getRandomNewsName(String filenameExtension)
	{

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random random = new Random();
		return sdf.format(date) + "_News_" + random.nextInt(65535) + "."
				+ filenameExtension;
	}
	
	

	public static Bitmap getBitmapFromUri(Uri uri, Context context)
	{

		try
		{
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			return bitmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap getBitmapFromPath(String localPath)
	{

		File imgFile = new File(localPath);
		Bitmap pathBitmap = null;
		if (imgFile.exists())
		{
			pathBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}

		return pathBitmap;
	}

	public synchronized static void setImageViewWithBitmap(ImageView iv, Bitmap bm)
	{

		if (bm != null)
		{
//			BitmapDrawable ob = new BitmapDrawable(bm);
//			iv.setBackgroundDrawable(ob);
			iv.setImageBitmap(bm);
		}
		else
		{
			iv.setImageResource(android.R.color.transparent);
		}
	}
	
	public synchronized static void setImageViewBackgroundWithBitmap(ImageView iv, Bitmap bm)
	{

		if (bm != null)
		{
			BitmapDrawable ob = new BitmapDrawable(bm);
			iv.setBackgroundDrawable(ob);
//			iv.setImageBitmap(bm);
		}
		else
		{
			iv.setBackgroundResource(android.R.color.transparent);
		}
	}
	
	public static boolean copyAssetFolder(AssetManager assetManager,
	            String fromAssetPath, String toPath) {
	        try {
	            String[] files = assetManager.list(fromAssetPath);
	            new File(toPath).mkdirs();
	            boolean res = true;
	            for (String file : files)
	                if (file.contains("."))
	                    res &= copyAsset(assetManager, 
	                            fromAssetPath + "/" + file,
	                            toPath + "/" + file);
	                else 
	                    res &= copyAssetFolder(assetManager, 
	                            fromAssetPath + "/" + file,
	                            toPath + "/" + file);
	            return res;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    private static boolean copyAsset(AssetManager assetManager,
	            String fromAssetPath, String toPath) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open(fromAssetPath);
	          new File(toPath).createNewFile();
	          out = new FileOutputStream(toPath);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	          return true;
	        } catch(Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    private static void copyFile(InputStream in, OutputStream out) throws IOException {
	        byte[] buffer = new byte[1024];
	        int read;
	        while((read = in.read(buffer)) != -1){
	          out.write(buffer, 0, read);
	        }
	    }
	    
	public static Uri downloadToLocal(Uri remoteUri, File dest)
	{
		long startTime = System.currentTimeMillis();
		String scheme = remoteUri.getScheme();
		if (scheme.equalsIgnoreCase("file") || scheme.equalsIgnoreCase("content"))
		{
			return remoteUri; // is already a local Uri
		}
		else
		{
			InputStream in = null;
			OutputStream out = null;
			try
			{
				URL url = new URL(remoteUri.toString());
				URLConnection conn = url.openConnection();
				conn.connect();
				in = new BufferedInputStream(conn.getInputStream());
				out = new FileOutputStream(dest);
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1)
				{
					out.write(buffer, 0, bytesRead);
				}
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}

		}
		Log.d("FileUtility", "time spent: " + (System.currentTimeMillis() - startTime) + ", from "
				+ remoteUri.toString() + " to " + dest.getAbsolutePath());
		if (!dest.exists() || dest.length() == 0)
		{
			Log.e("FileUtility", "download to local failed");
		}
		return Uri.fromFile(dest);
	}
	
	public static boolean copyFile(File src, File dest)
	{
		boolean result = false;
		if (src.exists())
		{
			InputStream in = null;
			OutputStream out = null;
			try
			{
				in = new FileInputStream(src);
				out = new FileOutputStream(dest);

				byte[] buf = new byte[1024];
				int len;

				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				result = true;
				Log.d("FileUtility", "Copy file successful.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{

				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			Log.e("FileUtility", "Copy file failed. Source file missing.");
		}
		return result;
	}
	
	public static String getFolderNameUsingSystemDPI(Context context)
	{

		int density = context.getResources().getDisplayMetrics().densityDpi;

		String result = "xhdpi";
		switch (density)
		{
		case DisplayMetrics.DENSITY_LOW:
			result = "hdpi";
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			result = "hdpi";
			break;
		case DisplayMetrics.DENSITY_HIGH:
			result = "hdpi";
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			result = "xhdpi";
			break;
		case DisplayMetrics.DENSITY_TV:
			result = "xhdpi";
			break;
		default:
			result = "xhdpi";
			break;
		}

		return result;

	}
	
	
	
}

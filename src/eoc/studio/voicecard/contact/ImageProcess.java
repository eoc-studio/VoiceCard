package eoc.studio.voicecard.contact;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class ImageProcess
{
	// /About user's pic
	private static final int PIC_WIDTH_MAX = 170, PIC_HEIGHT_MAX = 170;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static Bitmap addNewImage(final Uri uri, final ContentResolver cr)
	{
		Bitmap addNewBitmap = null;
		try
		{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			addNewBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, opt);
			opt.inSampleSize = computeSampleSize(opt, PIC_WIDTH_MAX * PIC_HEIGHT_MAX);
			opt.inJustDecodeBounds = false;
			addNewBitmap = resizeBmp(BitmapFactory.decodeStream(cr.openInputStream(uri), null, opt));
		}
		catch (OutOfMemoryError e)
		{
			System.out.println("[AddressBook][ImageProcess]addNewImage:" + e);
		}
		catch (FileNotFoundException e)
		{
			Log.e("Exception", e.getMessage(), e);
		}
		catch (Exception ex)
		{
			System.out.println("[AddressBook][ImageProcess]addNewImage:" + ex);
		}
		return addNewBitmap;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static Bitmap getPhoto(final ContentResolver contentResolver, final long contactId)
	{
		Bitmap photo = null;
		try
		{
			Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
			InputStream photoDataStream = Contacts.openContactPhotoInputStream(contentResolver,
					contactPhotoUri);
			photo = BitmapFactory.decodeStream(photoDataStream);
		}
		catch (OutOfMemoryError e)
		{
			System.out.println("[AddressBook][ImageProcess]getPhoto:" + e);
		}
		return photo;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static Bitmap resizeBmp(final Bitmap bitmap)
	{
		Matrix matrix = new Matrix();
		matrix.postScale((float) PIC_WIDTH_MAX / bitmap.getWidth(),
				(float) PIC_HEIGHT_MAX / bitmap.getHeight());
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
				true);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static int computeSampleSize(final BitmapFactory.Options options,
			final int maxNumOfPixels)
	{
		return computeSampleSize(options, -1, maxNumOfPixels);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int computeSampleSize(final BitmapFactory.Options options, int minSideLength,
			final int maxNumOfPixels)
	{
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8)
		{
			roundedSize = 1;
			while (roundedSize < initialSize)
			{
				roundedSize <<= 1;
			}
		}
		else
		{
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int computeInitialSampleSize(final BitmapFactory.Options options,
			final int minSideLength, final int maxNumOfPixels)
	{
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
				/ maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound)
		{
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1))
		{
			return 1;
		}
		else if (minSideLength == -1)
		{
			return lowerBound;
		}
		else
		{
			return upperBound;
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

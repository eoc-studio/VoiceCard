package eoc.studio.voicecard.facebook.enetities;

import java.lang.reflect.Field;

import eoc.studio.voicecard.facebook.utils.BundleTag;
import eoc.studio.voicecard.facebook.utils.Privacy;

import android.os.Bundle;

public class Photo
{
	private String message = null;
	private String placeId = null;

	private byte[] uploadPicture = null;
    private Privacy privacy = null;

	public Photo(String message, byte[] uploadPicture)
	{
	    this.message = message;
		this.uploadPicture = uploadPicture;
	}
	
	public Photo(String message, String placeId, byte[] uploadPicture, Privacy privacy)
    {
        this.message = message;
        this.placeId = placeId;
        this.uploadPicture = uploadPicture;
        this.privacy = privacy;
    }

	public Bundle getBundle()
	{
		Bundle bundle = new Bundle();

		// add description
		if (message != null)
		{
			bundle.putString(BundleTag.MESSAGE, message);
		}

		// add place
		if (placeId != null)
		{
			bundle.putString(BundleTag.PLACE, placeId);
		}

        // add privacy
        if (privacy != null) {
            bundle.putString(BundleTag.PRIVACY, privacy.getJSONString());
        }

		// add image
		if (uploadPicture != null)
		{
			bundle.putByteArray(BundleTag.PICTURE, uploadPicture);
		}

		return bundle;
	}

	public String toString()
    {

        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        // determine fields declared in this class only (no fields of
        // superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        // print field names paired with their values
        for (Field field : fields)
        {
            result.append("  ");
            try
            {
                result.append(field.getName());
                result.append(": ");
                // requires access to private field:
                result.append(field.get(this));
            }
            catch (IllegalAccessException ex)
            {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}

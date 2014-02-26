package eoc.studio.voicecard.facebook.enetities;

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

}

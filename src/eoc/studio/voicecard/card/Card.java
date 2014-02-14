package eoc.studio.voicecard.card;

import android.graphics.Bitmap;
import android.net.Uri;

public class Card
{
	private int id;
	private CardCategory category;
	private String name;
	private int image3dCoverResId;
	private int image3dOpenResId;
	private int imageCoverResId;
	private int imageInnerLeftResId;
	private int imageInnerRightResId;

	private Uri sound;
	private Uri image;
	private String message;
	private Bitmap signature;

	public Card(int id, CardCategory category, String name, int image3dCoverResId, int image3dOpenResId, int imageCoverResId,
			int imageInnerLeftResId, int imageInnerRightResId)
	{
		this.id = id;
		this.category = category;
		this.name = name;
		this.image3dCoverResId = image3dCoverResId;
		this.image3dOpenResId = image3dOpenResId;
		this.imageCoverResId = imageCoverResId;
		this.imageInnerLeftResId = imageInnerLeftResId;
		this.imageInnerRightResId = imageInnerRightResId;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getImage3dCoverResId()
	{
		return image3dCoverResId;
	}

	public void setImage3dCoverResId(int image3dCoverResId)
	{
		this.image3dCoverResId = image3dCoverResId;
	}

	public int getImage3dOpenResId()
	{
		return image3dOpenResId;
	}

	public void setImage3dOpenResId(int image3dOpenResId)
	{
		this.image3dOpenResId = image3dOpenResId;
	}

	public int getImageCoverResId()
	{
		return imageCoverResId;
	}

	public void setImageCoverResId(int imageCoverResId)
	{
		this.imageCoverResId = imageCoverResId;
	}

	public int getImageInnerLeftResId()
	{
		return imageInnerLeftResId;
	}

	public void setImageInnerLeftResId(int imageInnerLeftResId)
	{
		this.imageInnerLeftResId = imageInnerLeftResId;
	}

	public int getImageInnerRightResId()
	{
		return imageInnerRightResId;
	}

	public void setImageInnerRightResId(int imageInnerRightResId)
	{
		this.imageInnerRightResId = imageInnerRightResId;
	}

	public Uri getSound()
	{
		return sound;
	}

	public void setSound(Uri sound)
	{
		this.sound = sound;
	}

	public Uri getImage()
	{
		return image;
	}

	public void setImage(Uri image)
	{
		this.image = image;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Bitmap getSignature()
	{
		return signature;
	}

	public void setSignature(Bitmap signature)
	{
		this.signature = signature;
	}

	@Override
	public String toString()
	{
		return "Card [name=" + name + ", sound=" + sound + ", image=" + image + ", message="
				+ message + ", signature=" + signature + "]";
	}
}

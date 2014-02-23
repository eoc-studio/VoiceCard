package eoc.studio.voicecard.card;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable
{
	public static final int TEXT_SIZE_TYPE_SMALL = 0;
	public static final int TEXT_SIZE_TYPE_NORMAL = 1;
	public static final int TEXT_SIZE_TYPE_LARGE = 2;

	public static final int DEFAULT_TEXT_SIZE_TYPE = TEXT_SIZE_TYPE_NORMAL;
	public static final int DEFAULT_TEXT_COLOR = Color.BLACK;

	// original
	private int id;
	private CardCategory category;
	private String name;
	private int image3dCoverResId;
	private int image3dOpenResId;
	private int imageCoverResId;
	private int imageInnerLeftResId;
	private int imageInnerRightResId;
	private int textColor;

	// user input
	private Uri sound;
	private Uri image;
	private String message;
	private int messageTextColor = DEFAULT_TEXT_COLOR;
	private int messageTextSizeType = DEFAULT_TEXT_SIZE_TYPE;
	private Uri signature;

	public Card(int id, CardCategory category, String name, int image3dCoverResId,
			int image3dOpenResId, int imageCoverResId, int imageInnerLeftResId,
			int imageInnerRightResId, int textColor)
	{
		this.id = id;
		this.category = category;
		this.name = name;
		this.image3dCoverResId = image3dCoverResId;
		this.image3dOpenResId = image3dOpenResId;
		this.imageCoverResId = imageCoverResId;
		this.imageInnerLeftResId = imageInnerLeftResId;
		this.imageInnerRightResId = imageInnerRightResId;
		this.textColor = textColor;
	}

	public Card(Card card)
	{
		this.id = card.id;
		this.category = card.category;
		this.name = card.name;
		this.image3dCoverResId = card.image3dCoverResId;
		this.image3dOpenResId = card.image3dOpenResId;
		this.imageCoverResId = card.imageCoverResId;
		this.imageInnerLeftResId = card.imageInnerLeftResId;
		this.imageInnerRightResId = card.imageInnerRightResId;
		this.textColor = card.textColor;

		this.sound = card.sound;
		this.image = card.image;
		this.message = card.message;
		this.messageTextColor = card.messageTextColor;
		this.messageTextSizeType = card.messageTextSizeType;
		this.signature = card.signature;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public CardCategory getCategory()
	{
		return category;
	}

	public void setCategory(CardCategory category)
	{
		this.category = category;
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

	public int getMessageTextSizeType()
	{
		return messageTextSizeType;
	}

	public int getMessageTextColor()
	{
		return messageTextColor;
	}

	public void setMessage(String message, int textSizeType, int color)
	{
		this.message = message;
		this.messageTextSizeType = textSizeType;
		this.messageTextColor = color;
	}

	public Uri getSignature()
	{
		return signature;
	}

	public void setSignature(Uri signature)
	{
		this.signature = signature;
	}

	public int getTextColor()
	{
		return textColor;
	}

	public void setTextColor(int color)
	{
		this.textColor = color;
	}

	@Override
	public String toString()
	{
		return "Card [id=" + id + ", category=" + category + ", name=" + name
				+ ", image3dCoverResId=" + image3dCoverResId + ", image3dOpenResId="
				+ image3dOpenResId + ", imageCoverResId=" + imageCoverResId
				+ ", imageInnerLeftResId=" + imageInnerLeftResId + ", imageInnerRightResId="
				+ imageInnerRightResId + ", textColor=" + textColor + ", sound=" + sound
				+ ", image=" + image + ", message=" + message + ", messageTextColor="
				+ messageTextColor + ", messageTextSizeType=" + messageTextSizeType
				+ ", signature=" + signature + "]";
	}

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(id);
		out.writeString(category.name());
		out.writeString(name);
		out.writeInt(image3dCoverResId);
		out.writeInt(image3dOpenResId);
		out.writeInt(imageCoverResId);
		out.writeInt(imageInnerLeftResId);
		out.writeInt(imageInnerRightResId);
		out.writeInt(textColor);

		out.writeParcelable(sound, flags);
		out.writeParcelable(image, flags);
		out.writeString(message);
		out.writeInt(messageTextColor);
		out.writeInt(messageTextSizeType);
		out.writeParcelable(signature, flags);
	}

	public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>()
	{
		public Card createFromParcel(Parcel in)
		{
			return new Card(in);
		}

		public Card[] newArray(int size)
		{
			return new Card[size];
		}
	};

	private Card(Parcel in)
	{
		id = in.readInt();
		category = CardCategory.valueOf(in.readString());
		name = in.readString();
		image3dCoverResId = in.readInt();
		image3dOpenResId = in.readInt();
		imageCoverResId = in.readInt();
		imageInnerLeftResId = in.readInt();
		imageInnerRightResId = in.readInt();
		textColor = in.readInt();

		ClassLoader uriClazzLoader = Uri.class.getClassLoader();
		sound = in.readParcelable(uriClazzLoader);
		image = in.readParcelable(uriClazzLoader);
		message = in.readString();
		messageTextColor = in.readInt();
		messageTextSizeType = in.readInt();
		signature = in.readParcelable(uriClazzLoader);
	}
}

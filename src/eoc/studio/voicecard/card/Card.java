package eoc.studio.voicecard.card;

import java.io.File;
import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import eoc.studio.voicecard.card.database.CardDatabaseHelper;
import eoc.studio.voicecard.card.database.CategoryAssistant;
import eoc.studio.voicecard.mailbox.Mail;
import eoc.studio.voicecard.utils.FileUtility;

public class Card implements Parcelable
{
	public static final int TEXT_SIZE_TYPE_SMALL = 0;
	public static final int TEXT_SIZE_TYPE_NORMAL = 1;
	public static final int TEXT_SIZE_TYPE_LARGE = 2;

	public static final int DEFAULT_TEXT_SIZE_TYPE = TEXT_SIZE_TYPE_NORMAL;
	public static final int DEFAULT_TEXT_COLOR = Color.BLACK;

	// original
	private int id;
	// private CardCategory category;
	private CategoryAssistant category;
	private String name;
	// private int image3dCoverResId;
	// private int image3dOpenResId;
	// private int imageCoverResId;
	// private int imageInnerLeftResId;
	// private int imageInnerRightResId;

	private String image3dCoverPath;
	private String image3dOpenPath;
	private String imageCoverPath;
	private String imageInnerLeftPath;
	private String imageInnerRightPath;

	private int textColor;

	// user input
	private Uri sound;
	private Uri image;
	private String message;
	private int messageTextColor = DEFAULT_TEXT_COLOR;
	private int messageTextSizeType = DEFAULT_TEXT_SIZE_TYPE;
	private Uri signDraftImage;
	private Uri signHandwriting;
	private Uri signPositionInfo;

	public Card(int id, CategoryAssistant category, String name, String image3dCoverPath,
			String image3dOpenPath, String imageCoverPath, String imageInnerLeftPath,
			String imageInnerRightPath, int textColor)
	{
		this.id = id;
		this.category = category;
		this.name = name;
		this.image3dCoverPath = image3dCoverPath;
		this.image3dOpenPath = image3dOpenPath;
		this.imageCoverPath = imageCoverPath;
		this.imageInnerLeftPath = imageInnerLeftPath;
		this.imageInnerRightPath = imageInnerRightPath;
		this.textColor = textColor;
	}

	public Card(Card card)
	{
		this.id = card.id;
		this.category = card.category;
		this.name = card.name;
		this.image3dCoverPath = card.image3dCoverPath;
		this.image3dOpenPath = card.image3dOpenPath;
		this.imageCoverPath = card.imageCoverPath;
		this.imageInnerLeftPath = card.imageInnerLeftPath;
		this.imageInnerRightPath = card.imageInnerRightPath;
		this.textColor = card.textColor;

		this.sound = card.sound;
		this.image = card.image;
		this.message = card.message;
		this.messageTextColor = card.messageTextColor;
		this.messageTextSizeType = card.messageTextSizeType;
		this.signDraftImage = card.signDraftImage;
		this.signHandwriting = card.signHandwriting;
		this.signPositionInfo = card.signPositionInfo;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public CategoryAssistant getCategory()
	{
		return category;
	}

	public void setCategory(CategoryAssistant category)
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

	// public int getImage3dCoverResId()
	// {
	// return image3dCoverResId;
	// }
	//
	// public void setImage3dCoverResId(int image3dCoverResId)
	// {
	// this.image3dCoverResId = image3dCoverResId;
	// }
	//
	// public int getImage3dOpenResId()
	// {
	// return image3dOpenResId;
	// }
	//
	// public void setImage3dOpenResId(int image3dOpenResId)
	// {
	// this.image3dOpenResId = image3dOpenResId;
	// }
	//
	// public int getImageCoverResId()
	// {
	// return imageCoverResId;
	// }
	//
	// public void setImageCoverResId(int imageCoverResId)
	// {
	// this.imageCoverResId = imageCoverResId;
	// }
	//
	// public int getImageInnerLeftResId()
	// {
	// return imageInnerLeftResId;
	// }
	//
	// public void setImageInnerLeftResId(int imageInnerLeftResId)
	// {
	// this.imageInnerLeftResId = imageInnerLeftResId;
	// }
	//
	// public int getImageInnerRightResId()
	// {
	// return imageInnerRightResId;
	// }
	//
	// public void setImageInnerRightResId(int imageInnerRightResId)
	// {
	// this.imageInnerRightResId = imageInnerRightResId;
	// }

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

	public Uri getSignDraftImage()
	{
		return signDraftImage;
	}

	public void setSignDraftImage(Uri signature)
	{
		this.signDraftImage = signature;
	}

	public int getTextColor()
	{
		return textColor;
	}

	public void setTextColor(int color)
	{
		this.textColor = color;
	}

	// @Override
	// public String toString()
	// {
	// return "Card [id=" + id + ", category=" + category + ", name=" + name
	// + ", image3dCoverResId=" + image3dCoverResId + ", image3dOpenResId="
	// + image3dOpenResId + ", imageCoverResId=" + imageCoverResId
	// + ", imageInnerLeftResId=" + imageInnerLeftResId +
	// ", imageInnerRightResId="
	// + imageInnerRightResId + ", textColor=" + textColor + ", sound=" + sound
	// + ", image=" + image + ", message=" + message + ", messageTextColor="
	// + messageTextColor + ", messageTextSizeType=" + messageTextSizeType
	// + ", signDraftImage=" + signDraftImage + ", signHandwriting=" +
	// signHandwriting
	// + ", signPositionInfo=" + signPositionInfo + "]";
	// }
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

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(id);
		out.writeParcelable(category, flags);
		out.writeString(name);

		out.writeString(image3dCoverPath);
		out.writeString(image3dOpenPath);
		out.writeString(imageCoverPath);
		out.writeString(imageInnerLeftPath);
		out.writeString(imageInnerRightPath);

		// out.writeInt(image3dCoverResId);
		// out.writeInt(image3dOpenResId);
		// out.writeInt(imageCoverResId);
		// out.writeInt(imageInnerLeftResId);
		// out.writeInt(imageInnerRightResId);
		out.writeInt(textColor);

		out.writeParcelable(sound, flags);
		out.writeParcelable(image, flags);
		out.writeString(message);
		out.writeInt(messageTextColor);
		out.writeInt(messageTextSizeType);
		out.writeParcelable(signDraftImage, flags);
		out.writeParcelable(signHandwriting, flags);
		out.writeParcelable(signPositionInfo, flags);
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
		ClassLoader categoryAssistantClazzLoader = CategoryAssistant.class.getClassLoader();
		category = in.readParcelable(categoryAssistantClazzLoader);
		name = in.readString();

		this.image3dCoverPath = in.readString();
		this.image3dOpenPath = in.readString();
		this.imageCoverPath = in.readString();
		this.imageInnerLeftPath = in.readString();
		this.imageInnerRightPath = in.readString();

		// image3dCoverResId = in.readInt();
		// image3dOpenResId = in.readInt();
		// imageCoverResId = in.readInt();
		// imageInnerLeftResId = in.readInt();
		// imageInnerRightResId = in.readInt();
		textColor = in.readInt();

		ClassLoader uriClazzLoader = Uri.class.getClassLoader();
		sound = in.readParcelable(uriClazzLoader);
		image = in.readParcelable(uriClazzLoader);
		message = in.readString();
		messageTextColor = in.readInt();
		messageTextSizeType = in.readInt();
		signDraftImage = in.readParcelable(uriClazzLoader);
		signHandwriting = in.readParcelable(uriClazzLoader);
		signPositionInfo = in.readParcelable(uriClazzLoader);
	}

	public Uri getSignHandwriting()
	{

		return signHandwriting;
	}

	public void setSignHandwriting(Uri signHandwriting)
	{

		this.signHandwriting = signHandwriting;
	}

	public Uri getSignPositionInfo()
	{

		return signPositionInfo;
	}

	public void setSignPositionInfo(Uri signPositionInfo)
	{

		this.signPositionInfo = signPositionInfo;
	}

	public String getImage3dCoverPath()
	{

		return image3dCoverPath;
	}

	public void setImage3dCoverPath(String image3dCoverPath)
	{

		this.image3dCoverPath = image3dCoverPath;
	}

	public String getImage3dOpenPath()
	{

		return image3dOpenPath;
	}

	public void setImage3dOpenPath(String image3dOpenPath)
	{

		this.image3dOpenPath = image3dOpenPath;
	}

	public String getImageCoverPath()
	{

		return imageCoverPath;
	}

	public void setImageCoverPath(String imageCoverPath)
	{

		this.imageCoverPath = imageCoverPath;
	}

	public String getImageInnerLeftPath()
	{

		return imageInnerLeftPath;
	}

	public void setImageInnerLeftPath(String imageInnerLeftPath)
	{

		this.imageInnerLeftPath = imageInnerLeftPath;
	}

	public String getImageInnerRightPath()
	{

		return imageInnerRightPath;
	}

	public void setImageInnerRightPath(String imageInnerRightPath)
	{

		this.imageInnerRightPath = imageInnerRightPath;
	}

	/**
	 * time consuming
	 * 
	 * @param context
	 * @param mail
	 * @return
	 */
	public static Card getCardFromMail(Context context, Mail mail)
	{
		int id = Integer.parseInt(mail.getCardId());
		CardDatabaseHelper cardDatabaseHelper = new CardDatabaseHelper(context);
		cardDatabaseHelper.open();
		Card card = cardDatabaseHelper
				.getCardByCardID(id, cardDatabaseHelper.getSystemDPI(context));

		String mailImageLink = mail.getImgLink();
		String mailSoundLink = mail.getSpeech();
		String mailSignLink = mail.getSign();
		final String fileName = "cardFromMail_" + System.currentTimeMillis();

		String mailMessageBody = mail.getBody();
		String mailTextSize = mail.getFontSize();
		String mailTextColor = mail.getFontColor();
		Log.d("Card", "mailImageLink: " + mailImageLink);
		Log.d("Card", "mailSoundLink: " + mailSoundLink);
		Log.d("Card", "mailSignLink: " + mailSignLink);
		Log.d("Card", "mailMessageBody: " + mailMessageBody);
		Log.d("Card", "mailTextSize: " + mailTextSize);
		Log.d("Card", "mailTextColor: " + mailTextColor);

		if (mailImageLink != null)
		{
			Uri img = Uri.parse(mailImageLink);
			img = FileUtility.downloadToLocal(img, new File(context.getFilesDir(), fileName
					+ "_img.png"));
			card.setImage(img);
		}
		if (mailSoundLink != null)
		{
			Uri sound = Uri.parse(mailSoundLink);
			sound = FileUtility.downloadToLocal(sound, new File(context.getFilesDir(), fileName
					+ ".3gp"));
			card.setSound(sound);
		}
		if (mailSignLink != null)
		{
			Uri sign = Uri.parse(mail.getSign());
			sign = FileUtility.downloadToLocal(sign, new File(context.getFilesDir(), fileName
					+ "_sign.png"));
			card.setSignDraftImage(sign);
		}

		// mailTextSize =
		// CardEditorActivity.getTextSizeByType(Integer.parseInt(mailSize));
		card.setMessage(mailMessageBody, Integer.parseInt(mailTextSize),
				Integer.valueOf(mailTextColor));

		return card;
	}
}

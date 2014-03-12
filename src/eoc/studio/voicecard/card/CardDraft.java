package eoc.studio.voicecard.card;

import java.lang.reflect.Field;

import android.R.integer;
import android.graphics.Color;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardDraft implements Parcelable
{
	private static final String TAG = "CardDraft";
	public static final int TEXT_SIZE_TYPE_SMALL = 0;

	public static final int TEXT_SIZE_TYPE_NORMAL = 1;

	public static final int TEXT_SIZE_TYPE_LARGE = 2;

	public static final int DEFAULT_TEXT_SIZE_TYPE = TEXT_SIZE_TYPE_NORMAL;

	public static final int DEFAULT_TEXT_COLOR = Color.BLACK;

	public static final int DEFAULT_CARD_ID = 0;

	@Expose
	@SerializedName("cardId")
	private int cardId = DEFAULT_CARD_ID;

	@Expose
	@SerializedName("soundPath")
	private String soundPath;

	@Expose
	@SerializedName("imagePath")
	private String imagePath;

	@Expose
	@SerializedName("message")
	private String message;

	@Expose
	@SerializedName("messageTextColor")
	private int messageTextColor = DEFAULT_TEXT_COLOR;

	@Expose
	@SerializedName("messageTextSizeType")
	private int messageTextSizeType = DEFAULT_TEXT_SIZE_TYPE;

	@Expose
	@SerializedName("signHandwritingPath")
	private String signHandwritingPath;

	@Expose
	@SerializedName("signPositionInfoPath")
	private String signPositionInfoPath;

	@Expose
	@SerializedName("signDraftImagePath")
	private String signDraftImagePath;

	private Uri soundUri = null;
	
	@Expose
	@SerializedName("soundDuration")
	private String soundDuration;

	private Uri imageUri = null;

	private Uri signHandwritingUri = null;

	private Uri signPositionInfoUri = null;

	private Uri signDraftImageUri = null;

	public CardDraft()
	{

	}

	public CardDraft(int cardId, Uri sound, String soundDuration,Uri image, String message, int messageTextColor,
			int messageTextSizeType, Uri userSignHandwritingUri, Uri userSignPositionInfoUri,
			Uri userSignDraftImageUri)
	{

		super();
		this.cardId = cardId;
		this.message = message;
		 
		Log.d(TAG, "messageTextColor: "+messageTextColor);
		
		this.messageTextColor = (messageTextColor != 0) ? messageTextColor : DEFAULT_TEXT_COLOR;

		this.messageTextSizeType = (messageTextSizeType != 0) ? messageTextSizeType
				: DEFAULT_TEXT_SIZE_TYPE;

		this.soundUri = sound;
		this.soundDuration = soundDuration;
		this.imageUri = image;
		this.signHandwritingUri = userSignHandwritingUri;
		this.signPositionInfoUri = userSignPositionInfoUri;
		this.signDraftImageUri = userSignDraftImageUri;

		if (this.soundUri != null) soundPath = this.soundUri.getPath();

		if (this.imageUri != null) imagePath = this.imageUri.getPath();

		if (this.signHandwritingUri != null)
			signHandwritingPath = this.signHandwritingUri.getPath();

		if (this.signPositionInfoUri != null)
			signPositionInfoPath = this.signPositionInfoUri.getPath();

		if (this.signDraftImageUri != null) signDraftImagePath = this.signDraftImageUri.getPath();

	}

	public CardDraft(Parcel cardDraft)
	{

		ClassLoader uriClazzLoader = Uri.class.getClassLoader();
		this.cardId = cardDraft.readInt();
		this.soundUri = cardDraft.readParcelable(uriClazzLoader);
		this.soundDuration = cardDraft.readString(); 
		this.imageUri = cardDraft.readParcelable(uriClazzLoader);
		this.message = cardDraft.readString();
		this.messageTextColor = cardDraft.readInt();
		this.messageTextSizeType = cardDraft.readInt();
		this.signHandwritingUri = cardDraft.readParcelable(uriClazzLoader);
		this.signPositionInfoUri = cardDraft.readParcelable(uriClazzLoader);
		this.signDraftImageUri = cardDraft.readParcelable(uriClazzLoader);
	}

	@Override
	public int describeContents()
	{

		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{

		out.writeInt(this.cardId);
		
		out.writeParcelable(this.soundUri, flags);
		out.writeString(this.soundDuration);
		
		out.writeParcelable(this.imageUri, flags);

		out.writeString(this.message);
		out.writeInt(this.messageTextColor);
		out.writeInt(this.messageTextSizeType);

		out.writeParcelable(this.signHandwritingUri, flags);
		out.writeParcelable(this.signPositionInfoUri, flags);
		out.writeParcelable(this.signDraftImageUri, flags);

	}

	public static final Parcelable.Creator<CardDraft> CREATOR = new Parcelable.Creator<CardDraft>()
	{
		public CardDraft createFromParcel(Parcel in)
		{

			return new CardDraft(in);
		}

		public CardDraft[] newArray(int size)
		{

			return new CardDraft[size];
		}
	};

	@Override
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

	public String getSoundPath()
	{

		return soundPath;
	}

	public void setSoundPath(String soundPath)
	{

		this.soundPath = soundPath;
	}

	public String getImagePath()
	{

		return imagePath;
	}

	public void setImagePath(String imagePath)
	{

		this.imagePath = imagePath;
	}

	public String getMessage()
	{

		return message;
	}

	public void setMessage(String message)
	{

		this.message = message;
	}

	public int getMessageTextColor()
	{

		return messageTextColor;
	}

	public void setMessageTextColor(int messageTextColor)
	{

		this.messageTextColor = messageTextColor;
	}

	public int getMessageTextSizeType()
	{

		return messageTextSizeType;
	}

	public void setMessageTextSizeType(int messageTextSizeType)
	{

		this.messageTextSizeType = messageTextSizeType;
	}

	public String getSignHandwritingPath()
	{

		return signHandwritingPath;
	}

	public void setSignHandwritingPath(String signHandwritingPath)
	{

		this.signHandwritingPath = signHandwritingPath;
	}

	public String getSignPositionInfoPath()
	{

		return signPositionInfoPath;
	}

	public void setSignPositionInfoPath(String signPositionInfoPath)
	{

		this.signPositionInfoPath = signPositionInfoPath;
	}

	public String getSignDraftImagePath()
	{

		return signDraftImagePath;
	}

	public void setSignDraftImagePath(String signDraftImagePath)
	{

		this.signDraftImagePath = signDraftImagePath;
	}

	public Uri getSoundUri()
	{

		return soundUri;
	}

	public void setSoundUri(Uri soundUri)
	{

		this.soundUri = soundUri;
	}

	public Uri getImageUri()
	{

		return imageUri;
	}

	public void setImageUri(Uri imageUri)
	{

		this.imageUri = imageUri;
	}

	public Uri getSignHandwritingUri()
	{

		return signHandwritingUri;
	}

	public void setSignHandwritingUri(Uri signHandwritingUri)
	{

		this.signHandwritingUri = signHandwritingUri;
	}

	public Uri getSignPositionInfoUri()
	{

		return signPositionInfoUri;
	}

	public void setSignPositionInfoUri(Uri signPositionInfoUri)
	{

		this.signPositionInfoUri = signPositionInfoUri;
	}

	public Uri getSignDraftImageUri()
	{

		return signDraftImageUri;
	}

	public void setSignDraftImageUri(Uri signDraftImageUri)
	{

		this.signDraftImageUri = signDraftImageUri;
	}

	public int getCardId()
	{

		return cardId;
	}

	public void setCardId(int cardId)
	{

		this.cardId = cardId;
	}

	public String getSoundDuration()
	{
	
		return soundDuration;
	}

	public void setSoundDuration(String soundDuration)
	{
	
		this.soundDuration = soundDuration;
	}
}

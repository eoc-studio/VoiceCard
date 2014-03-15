package eoc.studio.voicecard.card.database;

import java.lang.reflect.Field;

import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.CardCategory;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class CategoryAssistant implements Parcelable
{
	int categoryID;

	String categoryName;

	String categoryURL;

	String categoryLocalPath;
	
	String categoryEditedDate;
	
	String categoryLocalEditedDate;

	public CategoryAssistant()
	{

	}
	
	// Constructor for favorite
	public CategoryAssistant (int categoryID){
		this.categoryID = -999;
		this.categoryName = "favorite";
		this.categoryURL = "favorite";
		this.categoryLocalPath = "favorite";
	}
	// Constructor
	public CategoryAssistant(int categoryID, String categoryName, String categoryURL,
			String categoryLocalPath)
	{

		this.categoryID = categoryID;
		this.categoryName = categoryName;
		this.categoryURL = categoryURL;
		this.categoryLocalPath = categoryLocalPath;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{

		dest.writeInt(categoryID);
		dest.writeString(categoryName);
		dest.writeString(categoryURL);
		dest.writeString(categoryLocalPath);

	}

	public static final Parcelable.Creator<CategoryAssistant> CREATOR = new Parcelable.Creator<CategoryAssistant>()
	{
		public CategoryAssistant createFromParcel(Parcel in)
		{

			return new CategoryAssistant(in);
		}

		public CategoryAssistant[] newArray(int size)
		{

			return new CategoryAssistant[size];
		}
	};

	private CategoryAssistant(Parcel in)
	{

		categoryID = in.readInt();
		categoryName = in.readString();
		categoryURL = in.readString();
		categoryLocalPath = in.readString();
	}

	public int getCategoryID()
	{

		return categoryID;
	}

	public void setCategoryID(int categoryID)
	{

		this.categoryID = categoryID;
	}

	public String getCategoryURL()
	{

		return categoryURL;
	}

	public void setCategoryURL(String categoryURL)
	{

		this.categoryURL = categoryURL;
	}

	public String getCategoryLoocalPath()
	{

		return categoryLocalPath;
	}

	public void setCategoryLoocalPath(String categoryLoocalPath)
	{

		this.categoryLocalPath = categoryLoocalPath;
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

	public String getCategoryName()
	{

		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{

		this.categoryName = categoryName;
	}

	@Override
	public int describeContents()
	{

		return 0;
	}

	public String getCategoryEditedDate()
	{
	
		return categoryEditedDate;
	}

	public void setCategoryEditedDate(String categoryEditedDate)
	{
	
		this.categoryEditedDate = categoryEditedDate;
	}

	public String getCategoryLocalEditedDate()
	{
	
		return categoryLocalEditedDate;
	}

	public void setCategoryLocalEditedDate(String categoryLocalEditedDate)
	{
	
		this.categoryLocalEditedDate = categoryLocalEditedDate;
	}

}

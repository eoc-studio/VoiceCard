package eoc.studio.voicecard.card.database;

import java.lang.reflect.Field;

public class CardAssistant
{
	int cardID;
	int categoryID;
	String cardName;
	int cardFontColor;
	String cardEnable;
	String closeURL;
	String coverURL;
	String leftURL;
	String openURL;
	String rightURL;
	String closeLocalPath;
	String coverLocalPath;
	String leftLocalPath;
	String openLocalPath;
	String rightLocalPath;
	
	String cardEditedDate;
	
	String cardLocalEditedDate;
	
	
	
	public int getCardID() 
	{
	
		return cardID;
	}
	public void setCardID(int cardID)
	{
	
		this.cardID = cardID;
	}
	public int getCategoryID()
	{
	
		return categoryID;
	}
	public void setCategoryID(int categoryID)
	{
	
		this.categoryID = categoryID;
	}
	public String getCardName()
	{
	
		return cardName;
	}
	public void setCardName(String cardName)
	{
	
		this.cardName = cardName;
	}
	public int getCardFontColor()
	{
	
		return cardFontColor;
	}
	public void setCardFontColor(int cardFontColor)
	{
	
		this.cardFontColor = cardFontColor;
	}
	public String getCloseURL()
	{
	
		return closeURL;
	}
	public void setCloseURL(String closeURL)
	{
	
		this.closeURL = closeURL;
	}
	public String getCoverURL()
	{
	
		return coverURL;
	}
	public void setCoverURL(String coverURL)
	{
	
		this.coverURL = coverURL;
	}
	public String getLeftURL()
	{
	
		return leftURL;
	}
	public void setLeftURL(String leftURL)
	{
	
		this.leftURL = leftURL;
	}
	public String getOpenURL()
	{
	
		return openURL;
	}
	public void setOpenURL(String openURL)
	{
	
		this.openURL = openURL;
	}
	public String getRightURL()
	{
	
		return rightURL;
	}
	public void setRightURL(String rightURL)
	{
	
		this.rightURL = rightURL;
	}
	public String getCloseLocalPath()
	{
	
		return closeLocalPath;
	}
	public void setCloseLocalPath(String closeLocalPath)
	{
	
		this.closeLocalPath = closeLocalPath;
	}
	public String getCoverLocalPath()
	{
	
		return coverLocalPath;
	}
	public void setCoverLocalPath(String coverLocalPath)
	{
	
		this.coverLocalPath = coverLocalPath;
	}
	public String getLeftLocalPath()
	{
	
		return leftLocalPath;
	}
	public void setLeftLocalPath(String leftLocalPath)
	{
	
		this.leftLocalPath = leftLocalPath;
	}
	public String getOpenLocalPath()
	{
	
		return openLocalPath;
	}
	public void setOpenLocalPath(String openLocalPath)
	{
	
		this.openLocalPath = openLocalPath;
	}
	public String getRightLocalPath()
	{
	
		return rightLocalPath;
	}
	public void setRightLocalPath(String rightLocalPath)
	{
	
		this.rightLocalPath = rightLocalPath;
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
	public String getCardEnable()
	{
	
		return cardEnable;
	}
	public void setCardEnable(String cardEnable)
	{
	
		this.cardEnable = cardEnable;
	}
	public String getCardEditedDate()
	{
	
		return cardEditedDate;
	}
	public void setCardEditedDate(String cardEditedDate)
	{
	
		this.cardEditedDate = cardEditedDate;
	}
	public String getCardLocalEditedDate()
	{
	
		return cardLocalEditedDate;
	}
	public void setCardLocalEditedDate(String cardLocalEditedDate)
	{
	
		this.cardLocalEditedDate = cardLocalEditedDate;
	}

}

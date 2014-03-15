package eoc.studio.voicecard.manager;

import java.lang.reflect.Field;

import com.google.gson.annotations.SerializedName;

public class GsonCard
{
	@SerializedName("card_id")
	private String cardID;

	@SerializedName("cat_id")
	private String categoryID;

	@SerializedName("card_name")
	private String cardName;

	@SerializedName("mdpi_close")
	private String mdpiClose;

	@SerializedName("mdpi_cover")
	private String mdpiCover;

	@SerializedName("mdpi_left")
	private String mdpiLeft;

	@SerializedName("mdpi_open")
	private String mdpiOpen;

	@SerializedName("mdpi_right")
	private String mdpiRight;

	@SerializedName("hdpi_close")
	private String hdpiClose;

	@SerializedName("hdpi_cover")
	private String hdpiCover;

	@SerializedName("hdpi_left")
	private String hdpiLeft;

	@SerializedName("hdpi_open")
	private String hdpiOpen;

	@SerializedName("hdpi_right")
	private String hdpiRight;

	@SerializedName("xhdpi_close")
	private String xhdpiClose;

	@SerializedName("xhdpi_cover")
	private String xhdpiCover;

	@SerializedName("xhdpi_left")
	private String xhdpiLeft;

	@SerializedName("xhdpi_open")
	private String xhdpiOpen;

	@SerializedName("xhdpi_right")
	private String xhdpiRight;

	@SerializedName("xxhdpi_close")
	private String xxhdpiClose;

	@SerializedName("xxhdpi_cover")
	private String xxhdpiCover;

	@SerializedName("xxhdpi_left")
	private String xxhdpiLeft;

	@SerializedName("xxhdpi_open")
	private String xxhdpiOpen;

	@SerializedName("xxhdpi_right")
	private String xxhdpiRight;

	@SerializedName("card_font")
	private String cardFont;

	@SerializedName("sort")
	private String sort;

	@SerializedName("card_enable")
	private String cardEnable;

	@SerializedName("edited_date")
	private String cardEditedDate;
	
	public String getCardID()
	{

		return cardID;
	}

	public void setCardID(String cardID)
	{

		this.cardID = cardID;
	}

	public String getCategoryID()
	{

		return categoryID;
	}

	public void setCategoryID(String categoryID)
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

	public String getMdpiClose()
	{

		return mdpiClose;
	}

	public void setMdpiClose(String mdpiClose)
	{

		this.mdpiClose = mdpiClose;
	}

	public String getMdpiCover()
	{

		return mdpiCover;
	}

	public void setMdpiCover(String mdpiCover)
	{

		this.mdpiCover = mdpiCover;
	}

	public String getMdpiLeft()
	{

		return mdpiLeft;
	}

	public void setMdpiLeft(String mdpiLeft)
	{

		this.mdpiLeft = mdpiLeft;
	}

	public String getMdpiOpen()
	{

		return mdpiOpen;
	}

	public void setMdpiOpen(String mdpiOpen)
	{

		this.mdpiOpen = mdpiOpen;
	}

	public String getMdpiRight()
	{

		return mdpiRight;
	}

	public void setMdpiRight(String mdpiRight)
	{

		this.mdpiRight = mdpiRight;
	}

	public String getHdpiClose()
	{

		return hdpiClose;
	}

	public void setHdpiClose(String hdpiClose)
	{

		this.hdpiClose = hdpiClose;
	}

	public String getHdpiCover()
	{

		return hdpiCover;
	}

	public void setHdpiCover(String hdpiCover)
	{

		this.hdpiCover = hdpiCover;
	}

	public String getHdpiLeft()
	{

		return hdpiLeft;
	}

	public void setHdpiLeft(String hdpiLeft)
	{

		this.hdpiLeft = hdpiLeft;
	}

	public String getHdpiOpen()
	{

		return hdpiOpen;
	}

	public void setHdpiOpen(String hdpiOpen)
	{

		this.hdpiOpen = hdpiOpen;
	}

	public String getHdpiRight()
	{

		return hdpiRight;
	}

	public void setHdpiRight(String hdpiRight)
	{

		this.hdpiRight = hdpiRight;
	}

	public String getXhdpiClose()
	{

		return xhdpiClose;
	}

	public void setXhdpiClose(String xhdpiClose)
	{

		this.xhdpiClose = xhdpiClose;
	}

	public String getXhdpiCover()
	{

		return xhdpiCover;
	}

	public void setXhdpiCover(String xhdpiCover)
	{

		this.xhdpiCover = xhdpiCover;
	}

	public String getXhdpiLeft()
	{

		return xhdpiLeft;
	}

	public void setXhdpiLeft(String xhdpiLeft)
	{

		this.xhdpiLeft = xhdpiLeft;
	}

	public String getXhdpiOpen()
	{

		return xhdpiOpen;
	}

	public void setXhdpiOpen(String xhdpiOpen)
	{

		this.xhdpiOpen = xhdpiOpen;
	}

	public String getXhdpiRight()
	{

		return xhdpiRight;
	}

	public void setXhdpiRight(String xhdpiRight)
	{

		this.xhdpiRight = xhdpiRight;
	}

	public String getXxhdpiClose()
	{

		return xxhdpiClose;
	}

	public void setXxhdpiClose(String xxhdpiClose)
	{

		this.xxhdpiClose = xxhdpiClose;
	}

	public String getXxhdpiCover()
	{

		return xxhdpiCover;
	}

	public void setXxhdpiCover(String xxhdpiCover)
	{

		this.xxhdpiCover = xxhdpiCover;
	}

	public String getXxhdpiLeft()
	{

		return xxhdpiLeft;
	}

	public void setXxhdpiLeft(String xxhdpiLeft)
	{

		this.xxhdpiLeft = xxhdpiLeft;
	}

	public String getXxhdpiOpen()
	{

		return xxhdpiOpen;
	}

	public void setXxhdpiOpen(String xxhdpiOpen)
	{

		this.xxhdpiOpen = xxhdpiOpen;
	}

	public String getXxhdpiRight()
	{

		return xxhdpiRight;
	}

	public void setXxhdpiRight(String xxhdpiRight)
	{

		this.xxhdpiRight = xxhdpiRight;
	}

	public String getCardFont()
	{

		return cardFont;
	}

	public void setCardFont(String cardFont)
	{

		this.cardFont = cardFont;
	}

	public String getSort()
	{

		return sort;
	}

	public void setSort(String sort)
	{

		this.sort = sort;
	}

	public String getCardEnable()
	{

		return cardEnable;
	}

	public void setCardEnable(String cardEnable)
	{

		this.cardEnable = cardEnable;
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

	public String getCardEditedDate()
	{
	 
		return cardEditedDate;
	}

	public void setCardEditedDate(String cardEditedDate)
	{
	
		this.cardEditedDate = cardEditedDate;
	}
}

package eoc.studio.voicecard.manager;

import java.lang.reflect.Field;

import com.google.gson.annotations.SerializedName;

public class GsonCategory
{
	@SerializedName("cat_id")
	private String categoryID;

	@SerializedName("cat_name")
	private String categoryName;

	@SerializedName("cat_img_mdpi")
	private String categoryImageMDPI;

	@SerializedName("cat_img_hdpi")
	private String categoryImageHDPI;

	@SerializedName("cat_img_xhdpi")
	private String categoryImageXHDPI;

	@SerializedName("cat_img_xxhdpi")
	private String categoryImageXXHDPI;

	@SerializedName("cat_enable")
	private String categoryEnable;

	public String getCategoryID()
	{

		return categoryID;
	}

	public void setCategoryID(String categoryID)
	{

		this.categoryID = categoryID;
	}

	public String getCategoryName()
	{

		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{

		this.categoryName = categoryName;
	}

	public String getCategoryImageMDPI()
	{

		return categoryImageMDPI;
	}

	public void setCategoryImageMDPI(String categoryImageMDPI)
	{

		this.categoryImageMDPI = categoryImageMDPI;
	}

	public String getCategoryImageHDPI()
	{

		return categoryImageHDPI;
	}

	public void setCategoryImageHDPI(String categoryImageHDPI)
	{

		this.categoryImageHDPI = categoryImageHDPI;
	}

	public String getCategoryImageXHDPI()
	{

		return categoryImageXHDPI;
	}

	public void setCategoryImageXHDPI(String categoryImageXHDPI)
	{

		this.categoryImageXHDPI = categoryImageXHDPI;
	}

	public String getCategoryImageXXHDPI()
	{

		return categoryImageXXHDPI;
	}

	public void setCategoryImageXXHDPI(String categoryImageXXHDPI)
	{

		this.categoryImageXXHDPI = categoryImageXXHDPI;
	}

	public String getCategoryEnable()
	{

		return categoryEnable;
	}

	public void setCategoryEnable(String categoryEnable)
	{

		this.categoryEnable = categoryEnable;
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

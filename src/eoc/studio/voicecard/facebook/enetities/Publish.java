package eoc.studio.voicecard.facebook.enetities;

import java.lang.reflect.Field;

public class Publish
{
	public static final String DEFAULT_PICTURE = "http://upload.wikimedia.org/wikipedia/commons/2/26/YellowLabradorLooking_new.jpg";
	private String id, name, imgLink, caption, description, link;

	public Publish(String id, String name, String imgLink, String caption, String description,
			String link)
	{
		this.id = id;
		this.name = name;
		this.imgLink = imgLink;
		this.caption = caption;
		this.description = description;
		this.link = link;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getImgLink()
	{
		return imgLink;
	}

	public void setImgLink(String imgLink)
	{
		this.imgLink = imgLink;
	}

	public String getCaption()
	{
		return caption;
	}

	public String getDescription()
	{
		return description;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
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

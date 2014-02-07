package eoc.studio.voicecard.manufacture;

import android.graphics.Bitmap;

public class StampItem
{
	Integer drawableID;

	String name;



	public StampItem(Integer drawableID, String name)
	{

		super();
		this.drawableID = drawableID;
		this.name = name;
	}

	public StampItem(Integer drawableID)
	{

		super();
		this.drawableID = drawableID;
	}
	
	public String getName()
	{

		return name;
	}

	public void setName(String name)
	{

		this.name = name;
	}

	public Integer getDrawableID()
	{

		return drawableID;
	}

	public void setDrawableID(Integer drawableID)
	{

		this.drawableID = drawableID;
	}
}

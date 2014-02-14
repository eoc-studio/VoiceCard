package eoc.studio.voicecard.manufacture;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

public class StampGson
{

	@SerializedName("ResouceID")
	private int resId;

	@SerializedName("Width")
	private int width;

	@SerializedName("Height")
	private int height;

	@SerializedName("CenterX")
	private float centerX;

	@SerializedName("CenterY")
	private float centerY;

	@SerializedName("ScaleX")
	private float scaleX;

	@SerializedName("ScaleY")
	private float scaleY;

	@SerializedName("Angle")
	private float angle;

	@SerializedName("DisplayWidth")
	private int displayWidth;

	@SerializedName("DisplayHeight")
	private int displayHeight;

	public int getResId()
	{

		return resId;
	}

	public void setResId(int resId)
	{

		this.resId = resId;
	}

	public int getWidth()
	{

		return width;
	}

	public void setWidth(int width)
	{

		this.width = width;
	}

	public int getHeight()
	{

		return height;
	}

	public void setHeight(int height)
	{

		this.height = height;
	}

	public float getCenterX()
	{

		return centerX;
	}

	public void setCenterX(float centerX)
	{

		this.centerX = centerX;
	}

	public float getCenterY()
	{

		return centerY;
	}

	public void setCenterY(float centerY)
	{

		this.centerY = centerY;
	}

	public float getScaleX()
	{

		return scaleX;
	}

	public void setScaleX(float scaleX)
	{

		this.scaleX = scaleX;
	}

	public float getScaleY()
	{

		return scaleY;
	}

	public void setScaleY(float scaleY)
	{

		this.scaleY = scaleY;
	}

	public float getAngle()
	{

		return angle;
	}

	public void setAngle(float angle)
	{

		this.angle = angle;
	}

	public int getDisplayWidth()
	{

		return displayWidth;
	}

	public void setDisplayWidth(int displayWidth)
	{

		this.displayWidth = displayWidth;
	}

	public int getDisplayHeight()
	{

		return displayHeight;
	}

	public void setDisplayHeight(int displayHeight)
	{

		this.displayHeight = displayHeight;
	}
}

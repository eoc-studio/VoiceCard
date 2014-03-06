package eoc.studio.voicecard.manager;

public class CardImages
{
	public static final int DPI_MDPI = 0;

	public static final int DPI_HPPI = 1;

	public static final int DPI_XHDPI = 2;

	public static final int DPI_XXHDPI = 3;

	GsonCard gsonCard;

	int dpi;

	public CardImages(GsonCard gsonCard, int dpi)
	{

		this.gsonCard = gsonCard;
		this.dpi = dpi;

		switch (dpi)
		{
		case DPI_MDPI:
			closeURL = gsonCard.getMdpiClose();

			coverURL = gsonCard.getMdpiCover();

			leftURL = gsonCard.getMdpiLeft();

			openURL = gsonCard.getMdpiOpen();

			rightURL = gsonCard.getMdpiRight();
			break;
		case DPI_HPPI:
			closeURL = gsonCard.getHdpiClose();

			coverURL = gsonCard.getHdpiCover();

			leftURL = gsonCard.getHdpiLeft();

			openURL = gsonCard.getHdpiOpen();

			rightURL = gsonCard.getHdpiRight();
			break;
		case DPI_XHDPI:
			closeURL = gsonCard.getXhdpiClose();

			coverURL = gsonCard.getXhdpiCover();

			leftURL = gsonCard.getXhdpiLeft();

			openURL = gsonCard.getXhdpiOpen();

			rightURL = gsonCard.getXhdpiRight();
			break;
		case DPI_XXHDPI:
			closeURL = gsonCard.getXxhdpiClose();

			coverURL = gsonCard.getXxhdpiCover();

			leftURL = gsonCard.getXxhdpiLeft();

			openURL = gsonCard.getXxhdpiOpen();

			rightURL = gsonCard.getXxhdpiRight();
			break;

		default:
			closeURL = gsonCard.getXhdpiClose();

			coverURL = gsonCard.getXhdpiCover();

			leftURL = gsonCard.getXhdpiLeft();

			openURL = gsonCard.getXhdpiOpen();

			rightURL = gsonCard.getXhdpiRight();
			break;
		}
	}


	private String closeURL;

	private String coverURL;

	private String leftURL;

	private String openURL;

	private String rightURL;

	public GsonCard getGsonCard()
	{
	
		return gsonCard;
	}

	public void setGsonCard(GsonCard gsonCard)
	{
	
		this.gsonCard = gsonCard;
	}

	public int getDpi()
	{
	
		return dpi;
	}

	public void setDpi(int dpi)
	{
	
		this.dpi = dpi;
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



}

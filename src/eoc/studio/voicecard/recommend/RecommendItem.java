package eoc.studio.voicecard.recommend;

public class RecommendItem {

	private int imageId;
    private String title;
    private String promotion;
    private Boolean isNew;
 
    public RecommendItem(int imageId, String title, String promotion,Boolean isNew) {
        this.imageId = imageId;
        this.title = title;
        this.promotion = promotion;
        this.isNew = isNew;
    }
    
    public int getImageId()
	{
	
		return imageId;
	}

	public void setImageId(int imageId)
	{
	
		this.imageId = imageId;
	}

	public String getTitle()
	{
	
		return title;
	}

	public void setTitle(String title)
	{
	
		this.title = title;
	}

	public String getPromotion()
	{
	
		return promotion;
	}

	public void setPromotion(String promotion)
	{
	
		this.promotion = promotion;
	}

	public Boolean getIsNew()
	{
	
		return isNew;
	}

	public void setIsNew(Boolean isNew)
	{
	
		this.isNew = isNew;
	}


/*    @Override
    public String toString() {
        return title + "\n" + desc;
    }*/
}
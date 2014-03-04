package eoc.studio.voicecard.facebook.enetities;

public class Publish
{
    public static final String DEFAULT_ID = "100007811983123";
    public static final String DEFAULT_NAME = "EOC";
    public static final String DEFAULT_PICTURE = "http://upload.wikimedia.org/wikipedia/commons/2/26/YellowLabradorLooking_new.jpg";
    public static final String DEFAULT_CAPTION = "CAPTION";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";
    public static final String DEFAULT_LINK = "http://www.charliefind.com/";
    
    private String id, name, imgLink, caption, description, link;
    
    public Publish(String id, String name, String imgLink, String caption, String description, String link)
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
    
    public void setImgLink(String imgLink) {
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
}

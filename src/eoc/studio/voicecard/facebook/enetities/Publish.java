package eoc.studio.voicecard.facebook.enetities;

public class Publish
{

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
}

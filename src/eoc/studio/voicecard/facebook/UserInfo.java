package eoc.studio.voicecard.facebook;

import org.json.JSONObject;

public class UserInfo 
{

    private JSONObject userJson;
    
    private String id = "", email = "", name = "", gender = "", birthday = "", link = "", locale = "", imgLink = "",
            hometown = "", work = "", education = "";
    private int timeZone = 0;
    
    public UserInfo(JSONObject userJson) 
    {
        this.userJson = userJson;
        setId(this.userJson);
        setEmail(this.userJson);
        setName(this.userJson);
        setGender(this.userJson);
        setBirthday(this.userJson);
        setLink(this.userJson);
        setLocale(this.userJson);
        setImgLink(this.userJson);
        setHometown(this.userJson);
        setWork(this.userJson);
        setEducation(this.userJson);
        setTimezone(this.userJson);
    }
    
    private void setId(JSONObject userJson) 
    {
        try 
        {
            id = userJson.getString(JSONTag.ID);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            id = "";
        }
    }
    
    public String getId() 
    {
        return id;
    }
    
    private void setEmail(JSONObject userJson) 
    {
        try 
        {
            email = userJson.getString(JSONTag.EMAIL);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            email = "";
        }
    }
    
    public String getEmail() 
    {
        return email;
    }
    
    private void setName(JSONObject userJson) 
    {
        try 
        {
            name = userJson.getString(JSONTag.NAME);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            name = "";
        }
    }
    
    public String getName() 
    {
        return name;
    }
    
    private void setGender(JSONObject userJson) 
    {
        try 
        {
            gender = userJson.getString(JSONTag.GENDER);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            gender = "";
        }
    }
    
    public String getGender() 
    {
        return gender;
    }
    
    private void setBirthday(JSONObject userJson) 
    {
        try 
        {
            birthday = userJson.getString(JSONTag.BIRTHDAY);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            birthday = "";
        }
    }
    
    public String getBirthday() 
    {
        return birthday;
    }
    
    private void setLink(JSONObject userJson) 
    {
        try 
        {
            link = userJson.getString(JSONTag.LINK);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            link = "";
        }
    }
    
    public String getLink() 
    {
        return link;
    }
    
    private void setLocale(JSONObject userJson) 
    {
        try 
        {
            locale = userJson.getString(JSONTag.LOCALE);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            locale = "";
        }
    }
    
    public String getLocale() 
    {
        return locale;
    }
    
    private void setImgLink(JSONObject userJson) 
    {
        try 
        {
            imgLink = userJson.getJSONObject(JSONTag.PICTURE).getJSONObject(JSONTag.DATA).getString(JSONTag.URL);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            imgLink = "";
        }
    }
    
    public String getImgLink() 
    {
        return imgLink;
    }
    
    private void setHometown(JSONObject userJson) 
    {
        try 
        {
            hometown = userJson.getJSONObject(JSONTag.HOMETOWN).getString(JSONTag.NAME);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            hometown = "";
        }
    }
    
    public String getHometown() 
    {
        return hometown;
    }
    
    private void setWork(JSONObject userJson) 
    {
        try 
        {
            work = userJson.getJSONArray(JSONTag.WORK).getJSONObject(0).getJSONObject(JSONTag.EMPLOYER)
                    .getString(JSONTag.NAME);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            work = "";
        }
    }
    
    public String getWork() 
    {
        return work;
    }
    
    private void setEducation(JSONObject userJson) 
    {
        try 
        {
            education = userJson.getJSONArray(JSONTag.EDUCATION).getJSONObject(0).getJSONObject(JSONTag.SCHOOL)
                    .getString(JSONTag.NAME);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            education = "";
        }
    }
    
    public String getEducation() 
    {
        return education;
    }
    
    private void setTimezone(JSONObject userJson) 
    {
        try 
        {
            timeZone = userJson.getInt(JSONTag.TIMEZONE);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            timeZone = 0;
        }
    }
    
    public int getTimezone() 
    {
        return timeZone;
    }
}

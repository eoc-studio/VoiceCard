package eoc.studio.voicecard.manager;


import java.lang.reflect.Field;

import com.google.gson.annotations.SerializedName;

public class GsonFacebookUser {
    @SerializedName("fb_id")
    private String facebookID;
    
    @SerializedName("birthday")
    private String birthday;

    @SerializedName("img")
    private String img;
    
    @SerializedName("locale")
    private String locale;
    
    @SerializedName("link")
    private String link;
 
    @SerializedName("reg_date")
    private String reg_date;
    
    @SerializedName("last_login")
    private String last_login;
    
    @SerializedName("country")
    private String country;
    
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("title")
    private String title;
    
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("edu")
    private String edu;
    
    @SerializedName("industry")
    private String industry;
    
    @SerializedName("mobile")
    private String mobile;

	public GsonFacebookUser(String facebookID, String birthday, String img,String locale, String link,
			String country, String timezone, String title, String email, String name,
			String gender, String edu, String industry, String mobile)
	{
		setFacebookID(facebookID);
		setBirthday(birthday);
		setImg(img);
		setLocale(locale);
		setLink(link);
		setCountry(country);
		setTimezone(timezone);
		setTitle(title);
		setEmail(email);
		setName(name);
		setGender(gender);
		setEdu(edu);
		setIndustry(industry);
		setMobile(mobile);
	}
    
    
	public String getFacebookID()
	{
	
		return facebookID;
	}

	public void setFacebookID(String facebookID)
	{
	
		this.facebookID = facebookID;
	}


	public String getLocale()
	{
	
		return locale;
	}

	public void setLocale(String locale)
	{
	
		this.locale = locale;
	}

	public String getLink()
	{
	
		return link;
	}

	public void setLink(String link)
	{
	
		this.link = link;
	}

	public String getReg_date()
	{
	
		return reg_date;
	}

	public void setReg_date(String reg_date)
	{
	
		this.reg_date = reg_date;
	}

	public String getLast_login()
	{
	
		return last_login;
	}

	public void setLast_login(String last_login)
	{
	
		this.last_login = last_login;
	}

	public String getCountry()
	{
	
		return country;
	}

	public void setCountry(String country)
	{
	
		this.country = country;
	}

	public String getTimezone()
	{
	
		return timezone;
	}

	public void setTimezone(String timezone)
	{
	
		this.timezone = timezone;
	}

	public String getTitle()
	{
	
		return title;
	}

	public void setTitle(String title)
	{
	
		this.title = title;
	}

	public String getEmail()
	{
	
		return email;
	}

	public void setEmail(String email)
	{
	
		this.email = email;
	}

	public String getName()
	{
	
		return name;
	}

	public void setName(String name)
	{
	
		this.name = name;
	}

	public String getGender()
	{
	
		return gender;
	}

	public void setGender(String gender)
	{
	
		this.gender = gender;
	}

	public String getEdu()
	{
	
		return edu;
	}

	public void setEdu(String edu)
	{
	
		this.edu = edu;
	}

	public String getIndustry()
	{
	
		return industry;
	}

	public void setIndustry(String industry)
	{
	
		this.industry = industry;
	}

	public String getMobile()
	{
	
		return mobile;
	}

	public void setMobile(String mobile)
	{
	
		this.mobile = mobile;
	}
	
	public String toString() {
		  StringBuilder result = new StringBuilder();
		  String newLine = System.getProperty("line.separator");

		  result.append( this.getClass().getName() );
		  result.append( " Object {" );
		  result.append(newLine);

		  //determine fields declared in this class only (no fields of superclass)
		  Field[] fields = this.getClass().getDeclaredFields();

		  //print field names paired with their values
		  for ( Field field : fields  ) {
		    result.append("  ");
		    try {
		      result.append( field.getName() );
		      result.append(": ");
		      //requires access to private field:
		      result.append( field.get(this) );
		    } catch ( IllegalAccessException ex ) {
		      System.out.println(ex);
		    }
		    result.append(newLine);
		  }
		  result.append("}");

		  return result.toString();
		}


	public String getBirthday()
	{
	
		return birthday;
	}


	public void setBirthday(String birthday)
	{
	
		this.birthday = birthday;
	}


	public String getImg()
	{
	
		return img;
	}


	public void setImg(String img)
	{
	
		this.img = img;
	}
}


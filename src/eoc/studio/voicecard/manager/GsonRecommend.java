package eoc.studio.voicecard.manager;

import java.lang.reflect.Field;

import com.google.gson.annotations.SerializedName;

public class GsonRecommend
{
	@SerializedName("id")
	private String id;

	@SerializedName("uid")
	private String uid;

	@SerializedName("name")
	private String name;

	@SerializedName("promotion")
	private String promotion;

	@SerializedName("img")
	private String img;

	@SerializedName("cost")
	private String cost;

	@SerializedName("premium")
	private String premium;
	
	@SerializedName("url")
	private String url;
	
	@SerializedName("arrival")
	private String arrival;

	public String getId()
	{

		return id;
	}

	public void setId(String id)
	{

		this.id = id;
	}

	public String getUid()
	{

		return uid;
	}

	public void setUid(String uid)
	{

		this.uid = uid;
	}

	public String getName()
	{

		return name;
	}

	public void setName(String name)
	{

		this.name = name;
	}

	public String getPromotion()
	{

		return promotion;
	}

	public void setPromotion(String promotion)
	{

		this.promotion = promotion;
	}

	public String getImg()
	{

		return img;
	}

	public void setImg(String img)
	{

		this.img = img;
	}

	public String getCost()
	{

		return cost;
	}

	public void setCost(String cost)
	{

		this.cost = cost;
	}

	public String getPremium()
	{

		return premium;
	}

	public void setPremium(String premium)
	{

		this.premium = premium;
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

	public String getUrl()
	{
	
		return url;
	}

	public void setUrl(String url)
	{
	
		this.url = url;
	}

	public String getArrival()
	{
	
		return arrival;
	}

	public void setArrival(String arrival)
	{
	
		this.arrival = arrival;
	}

	
}
// "id": "64",
// "uid": "152",
// "name":
// "%E8%8A%AD%E8%95%BE%21+%E8%8A%AD%E8%95%BE%21%21+%E9%9B%B6%E9%8C%A2%E5%8C%85",
// "promotion": "",
// "img":
// "http://www.charliefind.com/themes/uploads/Products/152/46243212752ae7883e4076.jpg",
// "cost": "450",
// "premium": "0"

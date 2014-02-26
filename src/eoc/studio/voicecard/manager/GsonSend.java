package eoc.studio.voicecard.manager;

import java.lang.reflect.Field;

import com.google.gson.annotations.SerializedName;

public class GsonSend
{

	@SerializedName("send_id")
	private String sendID;

	@SerializedName("send_from")
	private String sendFrom;

	@SerializedName("send_from_name")
	private String sendFromName;
	
	@SerializedName("send_from_link")
	private String sendFromLink;
	
	@SerializedName("send_to")
	private String sendTo;

	@SerializedName("subject")
	private String subject;

	@SerializedName("body")
	private String body;

	@SerializedName("font_size")
	private String font_size;

	@SerializedName("font_color")
	private String font_color;

	@SerializedName("img")
	private String img;

	@SerializedName("speech")
	private String speech;

	@SerializedName("sign")
	private String sign;

	@SerializedName("send_time")
	private String send_time;

	public String getSendID()
	{

		return sendID;
	}

	public void setSendID(String sendID)
	{

		this.sendID = sendID;
	}

	public String getSendFrom()
	{

		return sendFrom;
	}

	public void setSendFrom(String sendFrom)
	{

		this.sendFrom = sendFrom;
	}

	public String getSendTo()
	{

		return sendTo;
	}

	public void setSendTo(String sendTo)
	{

		this.sendTo = sendTo;
	}

	public String getSubject()
	{

		return subject;
	}

	public void setSubject(String subject)
	{

		this.subject = subject;
	}

	public String getBody()
	{

		return body;
	}

	public void setBody(String body)
	{

		this.body = body;
	}

	public String getFont_size()
	{

		return font_size;
	}

	public void setFont_size(String font_size)
	{

		this.font_size = font_size;
	}

	public String getFont_color()
	{

		return font_color;
	}

	public void setFont_color(String font_color)
	{

		this.font_color = font_color;
	}

	public String getImg()
	{

		return img;
	}

	public void setImg(String img)
	{

		this.img = img;
	}

	public String getSpeech()
	{

		return speech;
	}

	public void setSpeech(String speech)
	{

		this.speech = speech;
	}

	public String getSign()
	{

		return sign;
	}

	public void setSign(String sign)
	{

		this.sign = sign;
	}

	public String getSend_time()
	{

		return send_time;
	}

	public void setSend_time(String send_time)
	{

		this.send_time = send_time;
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

	public String getSendFromLink()
	{
	
		return sendFromLink;
	}

	public void setSendFromLink(String sendFromLink)
	{
	
		this.sendFromLink = sendFromLink;
	}

	public String getSendFromName()
	{
	
		return sendFromName;
	}

	public void setSendFromName(String sendFromName)
	{
	
		this.sendFromName = sendFromName;
	}

}
// send_id: "12"
// send_from: "1118054263"
// send_to: "0939918739"
// subject: "%E9%80%81%E4%BD%A0%E4%B8%80%E5%80%8B%E8%AE%9A"
// body: "%E5%B0%B1%E6%98%AF%E8%AE%9A"
// font_size: ""
// font_color: ""
// img: ""
// speech: ""
// sign: ""
// send_time: "2014-02-19 10:48:55"

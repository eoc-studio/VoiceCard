package eoc.studio.voicecard.mailbox;

import java.lang.reflect.Field;

import android.os.Parcel;
import android.os.Parcelable;

public class Mail implements Parcelable {
    public static final String GET_MAIL = "get_mail";
    private String rowId, cardId, ownerId, sendId, sendFrom, sendFromName, senderImgLink, sendTo, subject, body,
            fontSize, fontColor, imgLink, speech, sign, sendTime;
    private int checkState, newState;
    private byte[] senderImg;
        
    public Mail(String rowId, String cardId, String ownerId, String sendId, String sendFrom, String sendFromName,
            String senderImgLink, String sendTo, String subject, String body, String fontSize, String fontColor,
            String imgLink, byte[] senderImg, String speech, String sign, String sendTime, int checkState, int newState) {
        this.rowId = rowId;
        this.cardId = cardId;
        this.ownerId = ownerId;
        this.sendId = sendId;
        this.sendFrom = sendFrom;
        this.sendFromName = sendFromName;
        this.senderImgLink = senderImgLink;
        this.sendTo = sendTo;
        this.subject = subject;
        this.body = body;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.imgLink = imgLink;
        this.senderImg = senderImg;
        this.speech = speech;
        this.sign = sign;
        this.sendTime = sendTime;
        this.checkState = checkState;
        this.newState = newState;
    }
    
    public Mail(Parcel in) {
        super(); 
        readFromParcel(in);
    }
    
    public String getRowId() {
        return rowId;
    }
    
    public String getCardId() {
        return cardId;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public String getSendedId() {
        return sendId;
    }
    
    public String getSendedFrom() {
        return sendFrom;
    }
    
    public String getSendedFromName() {
        return sendFromName;
    }
    
    public String getSenderImgLink() {
        return senderImgLink;
    }
    
    public String getSendedTo() {
        return sendTo;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public String getFontSize() {
        return fontSize;
    }
    
    public String getFontColor() {
        return fontColor;
    }
    
    public String getImgLink() {
        return imgLink;
    }
    
    public byte[] getSenderImg() {
        return senderImg;
    }
    
    public void setSenderImg(byte[] senderImg) {
        this.senderImg = senderImg;
    }
    
    public String getSpeech() {
        return speech;
    }
    
    public String getSign() {
        return sign;
    }
    
    public String getSendedTime() {
        return sendTime;
    }
    
    public int getCheckState() {
        return checkState;
    }
    
    public void setCheckState(int checkState) {
        this.checkState = checkState;
    }
    
    public int getNewState() {
        return newState;
    }
    
    public static final Parcelable.Creator<Mail> CREATOR = new Parcelable.Creator<Mail>() {
        public Mail createFromParcel(Parcel in) {
            return new Mail(in);
        }

        @Override
        public Mail[] newArray(int size) {
            return new Mail[size];
        }
    };

    public void readFromParcel(Parcel in) {
        this.rowId = in.readString();
        this.cardId = in.readString();
        this.ownerId = in.readString();
        this.sendId = in.readString();
        this.sendFrom = in.readString();
        this.sendFromName = in.readString();
        this.senderImgLink = in.readString();
        this.sendTo = in.readString();
        this.subject = in.readString();
        this.body = in.readString();
        this.fontSize = in.readString();
        this.fontColor = in.readString();
        this.imgLink = in.readString();
        
        this.speech = in.readString();
        this.sign = in.readString();
        this.sendTime = in.readString();
        this.checkState = in.readInt();
        this.newState = in.readInt();
    }

    @Override
    public int describeContents() {
        
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rowId);
        dest.writeString(cardId);
        dest.writeString(ownerId);
        dest.writeString(sendId);
        dest.writeString(sendFrom);
        dest.writeString(sendFromName);
        dest.writeString(senderImgLink);
        dest.writeString(sendTo);
        dest.writeString(subject);
        dest.writeString(body);
        dest.writeString(fontSize);
        dest.writeString(fontColor);
        dest.writeString(imgLink);

        dest.writeString(speech);
        dest.writeString(sign);
        dest.writeInt(checkState);
        dest.writeInt(newState);
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
}

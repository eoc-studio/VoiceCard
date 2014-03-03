package eoc.studio.voicecard.mailbox;

public class Mail {
    private String rowId, cardId, ownerId, sendId, sendFrom, sendFromName, sendTo, subject, body, fontSize, fontColor,
            imgLink, speech, sign, sendTime;
    private int checkState, newState;
    private byte[] img;
        
    public Mail(String rowId, String cardId, String ownerId, String sendId, String sendFrom, String sendFromName,
            String sendTo, String subject, String body, String fontSize, String fontColor, String imgLink, byte[] img,
            String speech, String sign, String sendTime, int checkState, int newState) {
        this.rowId = rowId;
        this.cardId = cardId;
        this.ownerId = ownerId;
        this.sendId = sendId;
        this.sendFrom = sendFrom;
        this.sendFromName = sendFromName;
        this.sendTo = sendTo;
        this.subject = subject;
        this.body = body;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.imgLink = imgLink;
        this.img = img;
        this.speech = speech;
        this.sign = sign;
        this.sendTime = sendTime;
        this.checkState = checkState;
        this.newState = newState;
    }
    
    public String getRowId() {
        return rowId;
    }
    
    public String getCardId() {
        return ownerId;
    }
    
    public String getOwnerId() {
        return cardId;
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
    
    public byte[] getImg() {
        return img;
    }
    
    public void setImg(byte[] img) {
        this.img = img;
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
}

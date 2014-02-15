package eoc.studio.voicecard.facebook;

public class FriendInfo {
    private String friendId, friendName, friendBirthday, friendImgLink;
    byte[] friendImg;
    int selectedState, installState;
    
    public FriendInfo(String friendId, String friendName, String friendBirthday, String friendImgLink,
            byte[] friendImg, int selectedState, int installState) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendBirthday = friendBirthday;
        this.friendImgLink = friendImgLink;
        this.friendImg = friendImg;
        this.selectedState = selectedState;
        this.installState = installState;
    }
    
    public String getFriendId() {
        return friendId;
    }
    
    public String getFriendName() {
        return friendName;
    }
    
    public String getFriendBirthday() {
        return friendBirthday;
    }
    
    public String getFriendImgLink() {
        return friendImgLink;
    }
    
    public void setFriendImg(byte[] friendImg) {
        this.friendImg = friendImg;
    }
    
    public byte[] getFriendImg() {
        return friendImg;
    }
    
    public void setSelecedState(int selectedState) {
        this.selectedState = selectedState;
    }
    
    public int getSelectedState() {
        return selectedState;
    }
    
    public int getInstallState() {
        return installState;
    }
}

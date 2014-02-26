package eoc.studio.voicecard.facebook.enetities;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendInfo implements Parcelable {
    public static final String GET_FRIEND = "getFriend";
    public static final int GET_FRIEND_REQUEST_CODE = 0;
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
    
    public FriendInfo(Parcel in) {
        super(); 
        readFromParcel(in);
    }

    public static final Parcelable.Creator<FriendInfo> CREATOR = new Parcelable.Creator<FriendInfo>() {
        public FriendInfo createFromParcel(Parcel in) {
            return new FriendInfo(in);
        }

        public FriendInfo[] newArray(int size) {

            return new FriendInfo[size];
        }

    };

    public void readFromParcel(Parcel in) {
        friendId = in.readString();
        friendName = in.readString();
        friendBirthday = in.readString();
        friendImgLink = in.readString();
        
        if (in.readInt() > 0) {
            friendImg = new byte[in.readInt()]; 
            in.readByteArray(friendImg);
        }
        selectedState = in.readInt();
        installState = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(friendId);
        dest.writeString(friendName);
        dest.writeString(friendBirthday);
        dest.writeString(friendImgLink);
        if (friendImg != null)
            dest.writeInt(friendImg.length); 
        dest.writeByteArray(friendImg); 
        dest.writeInt(selectedState);
        dest.writeInt(installState);
    }
}

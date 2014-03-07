package eoc.studio.voicecard.facebook.enetities;

import java.lang.reflect.Field;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendInfo implements Parcelable {
    public static final String GET_FRIEND = "getFriend";
    public static final int GET_FRIEND_REQUEST_CODE = 0;
    private String friendId, friendName, friendBirthday, friendImgLink;
    byte[] friendImg;
    int selectedState;
    
    public FriendInfo(String friendId, String friendName, String friendBirthday, String friendImgLink,
            byte[] friendImg, int selectedState) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendBirthday = friendBirthday;
        this.friendImgLink = friendImgLink;
        this.friendImg = friendImg;
        this.selectedState = selectedState;
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
        if (friendImg != null) {
            dest.writeInt(friendImg.length); 
            dest.writeByteArray(friendImg);
        }
        dest.writeInt(selectedState);
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

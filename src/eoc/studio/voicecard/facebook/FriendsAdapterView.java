package eoc.studio.voicecard.facebook;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.WebImageUtility;

public class FriendsAdapterView extends BaseAdapter
{
	private static final String TAG = "FriendsAdapter";
	private FriendsAdapterData friendsAdapterData;
	private static final int BASE_INDEX = 1000;
	private LayoutInflater layoutInflater;
	private Context context;
	private List<FriendInfo> friends;
	private ListView showFriendView;
	private ViewTag viewTag;
	private boolean pauseState = false;

	FriendsAdapterView(Context context, List<FriendInfo> friends, FriendsAdapterData friendsAdapterData, ListView showFriendView)
	{
		this.context = context;
		this.friends = friends;
		this.friendsAdapterData = friendsAdapterData;
		this.showFriendView = showFriendView;
		layoutInflater = LayoutInflater.from(context);
		pauseState = false;
		
		DownlaodImageThread downlaodImageThread = new DownlaodImageThread(friends);
		downlaodImageThread.start();
	}

	@Override
	public int getCount()
	{
		return friends.size();
	}

	@Override
	public Object getItem(int position)
	{
		return friends.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.user_info, null);
            viewTag = new ViewTag((TextView) convertView.findViewById(R.id.user_info_not_install),
                    (ImageView) convertView.findViewById(R.id.user_info_header),
                    (TextView) convertView.findViewById(R.id.user_info_name),
                    (ImageView) convertView.findViewById(R.id.user_info_check_icon));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ViewTag) convertView.getTag();
        }
        convertView.setId(BASE_INDEX + position);
        
		viewTag.name.setText(friends.get(position).getFriendName());
		
		if (friends.get(position).getInstallState() == FriendsAdapterData.NOTINSTALL) {
		    viewTag.notInstall.setVisibility(View.VISIBLE);
		} else {
		    viewTag.notInstall.setVisibility(View.INVISIBLE);
		}
		
		if (friends.get(position).getSelectedState() == FriendsAdapterData.SELECT) {
		    viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
                    R.drawable.icon_checkbox_check));
		} else {
		    viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
                    R.drawable.icon_checkbox));
		}
		
		// set header
		byte[] img = friends.get(position).getFriendImg();
		if (img != null) {
		    viewTag.header.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
		} else {
		    viewTag.header.setImageDrawable(context.getResources().getDrawable(R.drawable.dummy));
		}
		
		return convertView;
	}
	
	public void updateSelectedState(int position, int selectedState) {
	    friends.get(position).setSelecedState(selectedState);
	    notifyDataSetChanged();
	}
	
	public void setPauseState(boolean pauseState) {
	    this.pauseState = pauseState;
	}
	
    private Handler showImgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "showImgHandler === msg.what === " + msg.what);

            if (friends.get(msg.what).getFriendImg() == null) {
                Log.d(TAG, "friends is null");
                if (showFriendView.findViewById(BASE_INDEX + msg.what) != null) {
                    viewTag = (ViewTag) showFriendView.findViewById(BASE_INDEX + msg.what).getTag();
                } else {
                    Log.d(TAG, "friends.get(msg.what) is null");
                }
            } else {
                Log.d(TAG, "friends not null");
                if (showFriendView.findViewById(BASE_INDEX + msg.what) != null) {
                    viewTag = (ViewTag) showFriendView.findViewById(BASE_INDEX + msg.what).getTag();
                    byte[] img = friends.get(msg.what).getFriendImg();
                    viewTag.header.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                } else {
                    Log.d(TAG, "friends.get(msg.what) not null but findView is null");
                }
            }
        }
    };

	class ViewTag
	{
	    TextView notInstall;
	    ImageView header;
		TextView name;
		ImageView checkIcon;

		public ViewTag(TextView notInstall, ImageView header, TextView name, ImageView checkIcon)
		{
		    this.notInstall = notInstall;
		    this.header = header;
			this.name = name;
			this.checkIcon = checkIcon;
		}
	}
	
    private class DownlaodImageThread extends Thread {
        List<FriendInfo> friendList;

        public DownlaodImageThread(List<FriendInfo> friendList) {
            this.friendList = friendList;
        }

        @Override
        public void run() {
            for (int i = 0; i < friendList.size(); i++) {
                if (!pauseState) {
                    FriendInfo friendInfo = friendList.get(i);
                    byte[] friendImg = friendInfo.getFriendImg();

                    if (friendImg == null) {
                        friendImg = WebImageUtility.getWebImage(friendInfo.getFriendImgLink());
                        friendsAdapterData.updateFriendImg(friendInfo.getFriendId(), friendImg);
                        friendList.get(i).setFriendImg(friendImg);
                    }
                    showImgHandler.sendMessage(showImgHandler.obtainMessage(i));
                } else {
                    break;
                }
            }
            Log.d(TAG, "Download img finish() ");
        }
    }
}

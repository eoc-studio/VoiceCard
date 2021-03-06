package eoc.studio.voicecard.facebook.friends;

import java.util.Iterator;
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
import eoc.studio.voicecard.facebook.enetities.FriendInfo;
import eoc.studio.voicecard.utils.ListUtility;
import eoc.studio.voicecard.utils.NetworkUtility;

public class FriendsAdapterView extends BaseAdapter
{
	private static final String TAG = "FriendsAdapter";
	private FriendsAdapterData friendsAdapterData;
	private LayoutInflater layoutInflater;
	private Context context;
	private List<FriendInfo> friends;
	private ListView showFriendView;
	private ViewTag viewTag;
	private boolean isPause = false;
	private boolean isInterrupt = false;

	FriendsAdapterView(Context context, List<FriendInfo> friends,
			FriendsAdapterData friendsAdapterData, ListView showFriendView)
	{
		this.context = context;
		this.friends = friends;
		this.friendsAdapterData = friendsAdapterData;
		this.showFriendView = showFriendView;
		layoutInflater = LayoutInflater.from(context);
		isPause = false;
		isInterrupt = false;
		DownlaodImageThread downlaodImageThread = new DownlaodImageThread(friends, 0,
				friends.size());
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
		if (convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.select_friend_list_item, null);
			viewTag = new ViewTag(
					(ImageView) convertView.findViewById(R.id.glb_selectfriend_list_item_header),
					(TextView) convertView.findViewById(R.id.glb_selectfriend_list_item_name),
					(ImageView) convertView
							.findViewById(R.id.glb_selectfriend_list_item_check_icon));
			convertView.setTag(viewTag);
		}
		else
		{
			viewTag = (ViewTag) convertView.getTag();
		}
		convertView.setId(ListUtility.BASE_INDEX + position);
		if (position < friends.size())
		{
			viewTag.name.setText(friends.get(position).getFriendName());
			if (friends.get(position).getSelectedState() == FriendsAdapterData.SELECT)
			{
				viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
						R.drawable.icon_checkbox_check));
			}
			else
			{
				viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
						R.drawable.icon_checkbox));
			}
			// set header
			byte[] img = friends.get(position).getFriendImg();
			if (img != null)
			{
				viewTag.header.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
			}
			else
			{
				viewTag.header.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.dummy));
			}
		}
		return convertView;
	}

	public void updateSelectedState(int position, int selectedState)
	{
		friends.get(position).setSelecedState(selectedState);
		notifyDataSetChanged();
	}

	public void changeSelectedState(int position, int selectedState)
	{
		for (FriendInfo friendInfo : friends)
		{
			friendInfo.setSelecedState(FriendsAdapterData.UNSELECT);
		}
		friends.get(position).setSelecedState(selectedState);
		notifyDataSetChanged();
	}

	public void setPause(boolean isPause)
	{
		this.isPause = isPause;
	}

	public void setInterrupt(boolean isInterrupt)
	{
		this.isInterrupt = isInterrupt;
	}

	public void loadImagefromPosition(int startPostion, int endPosition)
	{
		if (startPostion > 0 && endPosition <= friends.size())
		{
			DownlaodImageThread downlaodImageThread = new DownlaodImageThread(friends.subList(
					startPostion, endPosition), startPostion, endPosition);
			downlaodImageThread.start();
		}
	}

	private Handler showImgHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Log.d(TAG, "showImgHandler === msg.what === " + msg.what);
			if (msg.what < friends.size())
			{
				if (friends.get(msg.what).getFriendImg() == null)
				{
					Log.d(TAG, "friends is null");
					if (showFriendView.findViewById(ListUtility.BASE_INDEX + msg.what) != null)
					{
						viewTag = (ViewTag) showFriendView.findViewById(
								ListUtility.BASE_INDEX + msg.what).getTag();
					}
					else
					{
						Log.d(TAG, "friends.get(msg.what) is null");
					}
				}
				else
				{
					Log.d(TAG, "friends not null");
					if (showFriendView.findViewById(ListUtility.BASE_INDEX + msg.what) != null)
					{
						viewTag = (ViewTag) showFriendView.findViewById(
								ListUtility.BASE_INDEX + msg.what).getTag();
						byte[] img = friends.get(msg.what).getFriendImg();
						viewTag.header.setImageBitmap(BitmapFactory.decodeByteArray(img, 0,
								img.length));
					}
					else
					{
						Log.d(TAG, "friends.get(msg.what) not null but findView is null");
					}
				}
			}
		}
	};

	public void clearList()
	{
		for (Iterator it = friends.iterator(); it.hasNext();)
		{
			it.remove();
		}
		friends.clear();
	}

	class ViewTag
	{
		ImageView header;
		TextView name;
		ImageView checkIcon;

		public ViewTag(ImageView header, TextView name, ImageView checkIcon)
		{
			this.header = header;
			this.name = name;
			this.checkIcon = checkIcon;
		}
	}

	private class DownlaodImageThread extends Thread
	{
		List<FriendInfo> friendList;
		int startPosition;
		int endPosition;

		public DownlaodImageThread(List<FriendInfo> friendList, int startPosition, int endPosition)
		{
			this.friendList = friendList;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}

		@Override
		public void run()
		{
			Log.d(TAG, "friendList size === " + friendList.size());
			Log.d(TAG, "startPosition === " + startPosition);
			Log.d(TAG, "endPosition === " + endPosition);
			for (int i = startPosition; i < endPosition; i++)
			{
				if (isInterrupt)
				{
					break;
				}
				if (!isPause)
				{
					int position = i - startPosition;
					if (position < friendList.size())
					{
						FriendInfo friendInfo = friendList.get(position);
						byte[] friendImg = friendInfo.getFriendImg();
						if (friendImg == null)
						{
							friendImg = NetworkUtility.getWebImage(friendInfo.getFriendImgLink());
							friendsAdapterData.updateFriendImg(friendInfo.getFriendId(), friendImg);
							if (position < friendList.size())
								friendList.get(position).setFriendImg(friendImg);
						}
						showImgHandler.sendMessage(showImgHandler.obtainMessage(i));
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			Log.d(TAG, "Download img finish() ");
		}
	}
}

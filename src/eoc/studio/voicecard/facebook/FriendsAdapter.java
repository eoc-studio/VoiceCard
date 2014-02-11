package eoc.studio.voicecard.facebook;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.model.GraphUser;

import eoc.studio.voicecard.R;

public class FriendsAdapter extends BaseAdapter
{
	private static final String TAG = "FriendsAdapter";
	private LayoutInflater layoutInflater;
	private Context context;
	private List<GraphUser> users;

	FriendsAdapter(Context context, List<GraphUser> users)
	{
		this.context = context;
		this.users = users;
		layoutInflater = LayoutInflater.from(context);
		
		for (int i = 0; i < users.size(); i++) {
		    Log.d(TAG, "id is " + users.get(i).getId());
		    Log.d(TAG, "name is " + users.get(i).getName());
		    Log.d(TAG, "name is " + users.get(i).getBirthday());
		}
	}

	@Override
	public int getCount()
	{
		return users.size();
	}

	@Override
	public Object getItem(int position)
	{
		return users.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewTag viewTag;
		if (convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.user_info, null);
			viewTag = new ViewTag((TextView) convertView.findViewById(R.id.user_name));
			convertView.setTag(viewTag);
		}
		else
		{
			viewTag = (ViewTag) convertView.getTag();
		}
		viewTag.name.setText(users.get(position).getName());
		return convertView;
	}

	class ViewTag
	{
		TextView name;

		public ViewTag(TextView name)
		{
			this.name = name;
		}
	}
}

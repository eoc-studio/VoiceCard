package eoc.studio.voicecard.recommend;

import java.util.List;

import eoc.studio.voicecard.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendAdapter extends ArrayAdapter<RecommendItem>
{

	Context context;

	public RecommendAdapter(Context context, int resourceId, List<RecommendItem> items)
	{

		super(context, resourceId, items);
		this.context = context;
	}

	/* private view holder class */
	private class ViewHolder
	{

		ImageView recommnendImageView;

		ImageView newImageView;

		TextView titleTextView;

		TextView promotionTextView;

		Boolean isNew;

	}

	public View getView(int position, View convertView, ViewGroup parent)
	{

		ViewHolder holder = null;
		RecommendItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.view_recommend_listview_item, null);
			holder = new ViewHolder();
			holder.titleTextView = (TextView) convertView
					.findViewById(R.id.view_recommend_listview_item_tv_title);
			holder.promotionTextView = (TextView) convertView
					.findViewById(R.id.view_recommend_listview_item_tv_promotion);
			holder.recommnendImageView = (ImageView) convertView
					.findViewById(R.id.view_recommend_listview_item_iv_image);
			holder.newImageView = (ImageView) convertView
					.findViewById(R.id.view_recommend_listview_item_iv_new);
			convertView.setTag(holder);
		}
		else holder = (ViewHolder) convertView.getTag();

		holder.titleTextView.setText(rowItem.getTitle());
		holder.promotionTextView.setText(rowItem.getPromotion());
		holder.recommnendImageView.setImageResource(rowItem.getImageId());

		if(rowItem.getIsNew()){
			holder.newImageView.setImageResource(R.drawable.icon_new);
		}
		else{
			holder.newImageView.setImageResource(android.R.color.transparent);
		}
		
		return convertView;
	}
}

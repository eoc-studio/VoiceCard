package eoc.studio.voicecard.recommend;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.manager.GsonRecommend;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class GsonRecommendAdapter extends ArrayAdapter<GsonRecommend> {
    private ImageLoader mImageLoader;
    
    public GsonRecommendAdapter(Context context, 
                              int textViewResourceId, 
                              List<GsonRecommend> objects,
                              ImageLoader imageLoader
                              ) {
        super(context, textViewResourceId, objects);
        mImageLoader = imageLoader;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.view_recommend_listview_item, null);
        }
        
        ViewHolder holder = (ViewHolder) v.getTag(R.id.id_holder);       
        
        if (holder == null) {
            holder = new ViewHolder(v);
            v.setTag(R.id.id_holder, holder);
        }        
        
        GsonRecommend entry = getItem(position);
        
        if (entry.getImg()!=null) {
            holder.image.setImageUrl(entry.getImg(), mImageLoader);
        } else {
            holder.image.setImageResource(android.R.color.transparent);
        }
        
        holder.titleTextView.setText(entry.getName());
		holder.promotionTextView.setText(entry.getPromotion());
	
        return v;
    }
    
    
    private class ViewHolder {
        NetworkImageView image;

		ImageView newImageView;

		TextView titleTextView;

		TextView promotionTextView;

		Boolean isNew;
		
        public ViewHolder(View convertView) {

			image = (NetworkImageView) convertView
					.findViewById(R.id.view_recommend_listview_item_niv_image);

			titleTextView = (TextView) convertView
					.findViewById(R.id.view_recommend_listview_item_tv_title);

			promotionTextView = (TextView) convertView
					.findViewById(R.id.view_recommend_listview_item_tv_promotion);
			/*
			 * recommnendImageView = (ImageView) convertView
			 * .findViewById(R.id.view_recommend_listview_item_iv_image);
			 */
			newImageView = (ImageView) convertView
					.findViewById(R.id.view_recommend_listview_item_iv_new);

			convertView.setTag(this);
        }
    }
}

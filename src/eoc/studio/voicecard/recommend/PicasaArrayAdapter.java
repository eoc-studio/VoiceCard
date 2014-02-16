/**
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eoc.studio.voicecard.recommend;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import eoc.studio.voicecard.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PicasaArrayAdapter extends ArrayAdapter<PicasaEntry> {
    private ImageLoader mImageLoader;
    
    public PicasaArrayAdapter(Context context, 
                              int textViewResourceId, 
                              List<PicasaEntry> objects,
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
        
        PicasaEntry entry = getItem(position);
        if (entry.getThumbnailUrl() != null) {
            holder.image.setImageUrl(entry.getThumbnailUrl(), mImageLoader);
        } else {
            holder.image.setImageResource(android.R.color.transparent);
        }
        
        holder.titleTextView.setText(entry.getTitle());
		holder.promotionTextView.setText(entry.getTitle());
	
        return v;
    }
    
    
    private class ViewHolder {
        NetworkImageView image;
//        TextView title; 
        
//		ImageView recommnendImageView;

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

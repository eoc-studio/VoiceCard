package eoc.studio.voicecard.manufacture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eoc.studio.voicecard.R;


public class StampAdapter extends ArrayAdapter<StampItem> {
	Context context;
	int layoutResourceId;
	ArrayList<StampItem> dataList = new ArrayList<StampItem>();

	public StampAdapter(Context context, int layoutResourceId,
			ArrayList<StampItem> dataList) {
		super(context, layoutResourceId);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View gridView = convertView;
		StampHolder holder = null;

		if (gridView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			gridView = inflater.inflate(layoutResourceId, parent, false);

			holder = new StampHolder();
			holder.imageItem = (ImageView) gridView.findViewById(R.id.iv_stamp);
			gridView.setTag(holder);
		} else {
			holder = (StampHolder) gridView.getTag();
		}

		StampItem item = dataList.get(position);

		holder.imageItem.setImageResource(item.getDrawableID());
		holder.imageItem.setTag(item.getDrawableID());
		holder.imageItem.setOnTouchListener(new StampCreateListener(this));
		
		return gridView;

	}
	
	@Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public StampItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
	static class StampHolder {
		public ImageView imageItem;
	}
}



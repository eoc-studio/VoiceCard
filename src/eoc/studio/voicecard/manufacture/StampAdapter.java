package eoc.studio.voicecard.manufacture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
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
		holder.imageItem.setOnTouchListener(new StampCreateListener());
		
		return gridView;

	}
	
	class StampCreateListener implements OnTouchListener
	{
		public boolean onTouch(View view, MotionEvent motionEvent)
		{

			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
			{
				ClipData data = ClipData.newPlainText("stamp", "I am stamp");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				view.startDrag(data, shadowBuilder, view, 0);
				return true;
			}
			else
			{
				return false;
			}
		}
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


//public class StampAdapter extends BaseAdapter
//{
//
//	   private Context context;
//	    private final Integer[] mThumbIds;
//
//	    public StampAdapter(Context context, Integer[] mThumbIds) {
//	        this.context = context;
//	        this.mThumbIds = mThumbIds;
//	    }
//
//	    public View getView(int position, View convertView, ViewGroup parent) {
//
//	        LayoutInflater inflater = (LayoutInflater) context
//	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//	        View gridView;
//
//	        if (convertView == null) {
//
//	            gridView = new View(context);
//
//	            // get layout from mobile.xml
//	            gridView = inflater.inflate(R.layout.view_stamp_item, null);
//
//	            // set image based on selected text
//	            ImageView imageView = (ImageView) gridView
//	                    .findViewById(R.id.iv_stamp);
//
//
//	            imageView.setImageResource(mThumbIds[position]);
//
//
//	        } else {
//	            gridView = (View) convertView;
//	        }
//
//	        return gridView;
//	    }
//
//	    @Override
//	    public int getCount() {
//	        return mThumbIds.length;
//	    }
//
//	    @Override
//	    public Object getItem(int position) {
//	        return null;
//	    }
//
//	    @Override
//	    public long getItemId(int position) {
//	        return 0;
//	    }
//}

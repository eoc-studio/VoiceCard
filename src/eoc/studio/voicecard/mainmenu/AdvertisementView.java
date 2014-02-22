package eoc.studio.voicecard.mainmenu;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdvertisementView extends RelativeLayout
{
	ImageView firstAdvertisementImageView;
	TextView  firstAdvertisementTextView;
	public AdvertisementView(Context context, AttributeSet attrs)
	{

		super(context, attrs);
		initLayout();
	}
	
	private void initLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_advertisement, this, true);
		
		
		firstAdvertisementImageView = (ImageView) findViewById(R.id.glb_advertisement_iv_image);
		firstAdvertisementTextView =(TextView) findViewById(R.id.glb_advertisement_tv_title);
	}
	
	public void updateView(Bitmap bitmap,String name){
		firstAdvertisementImageView.setImageBitmap(bitmap);
		firstAdvertisementTextView.setText(name);
	}

}

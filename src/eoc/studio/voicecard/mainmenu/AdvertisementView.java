package eoc.studio.voicecard.mainmenu;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class AdvertisementView extends RelativeLayout
{

	public AdvertisementView(Context context, AttributeSet attrs)
	{

		super(context, attrs);
		initLayout();
	}
	
	private void initLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_advertisement, this, true);
	}

}

package eoc.studio.voicecard.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import eoc.studio.voicecard.R;

public class SaveDraft extends ImageView
{

	public SaveDraft(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setImageResource(R.drawable.menu_save);
/*		this.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

			}

		});*/

	}

}

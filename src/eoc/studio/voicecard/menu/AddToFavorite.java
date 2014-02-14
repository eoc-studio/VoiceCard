package eoc.studio.voicecard.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import eoc.studio.voicecard.R;

public class AddToFavorite extends ImageView
{

	public AddToFavorite(final Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setImageResource(R.drawable.menu_collect);
		this.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

			}

		});
	}

}

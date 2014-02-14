package eoc.studio.voicecard.menu;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class Index extends ImageView
{

	public Index(final Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setImageResource(R.drawable.menu_index);
		this.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, MainMenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}

		});
	}

}

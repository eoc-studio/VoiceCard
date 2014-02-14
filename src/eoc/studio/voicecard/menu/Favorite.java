package eoc.studio.voicecard.menu;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.CardCategory;
import eoc.studio.voicecard.card.editor.CardSelectorActivity;

public class Favorite extends ImageView
{

	public Favorite(final Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setImageResource(R.drawable.menu_folder);
		this.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, CardSelectorActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(CardSelectorActivity.EXTRA_KEY_CATEGORY, CardCategory.USER_FAVORITE);
				context.startActivity(intent);
			}

		});
	}

}

package eoc.studio.voicecard.menu;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class DeleteSelectedMail extends ImageView 
{

    public DeleteSelectedMail(Context context, AttributeSet attrs) 
    {
        super(context, attrs);

        this.setImageResource(R.drawable.menu_del);
    }
}

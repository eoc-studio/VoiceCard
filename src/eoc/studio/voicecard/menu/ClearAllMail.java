package eoc.studio.voicecard.menu;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ClearAllMail extends ImageView 
{

    public ClearAllMail(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        
        this.setImageResource(R.drawable.menu_clear);
    }
}

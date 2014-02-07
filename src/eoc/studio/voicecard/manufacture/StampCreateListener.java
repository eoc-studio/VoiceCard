package eoc.studio.voicecard.manufacture;

import eoc.studio.voicecard.utils.DragUtility;
import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;

class StampCreateListener implements OnTouchListener
{
	private static final String TAG = "StampCreateListener";
	private final StampAdapter stampAdapter;

	StampCreateListener(StampAdapter stampAdapter)
	{

		this.stampAdapter = stampAdapter;
	}

	public boolean onTouch(View view, MotionEvent motionEvent)
	{

		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
		{
			String dragKey = TAG;
			String dragContext = "I am stamp";
			DragUtility.dragView(view, dragKey, dragContext);
			return true;
		}
		else
		{
			return false;
		}
	}

}

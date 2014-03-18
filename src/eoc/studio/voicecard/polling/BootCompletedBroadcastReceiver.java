package eoc.studio.voicecard.polling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d("BootCompletedBroadcastReceiver", "startPollingService");
		PollingUtils.startPollingService(context, 20, PollingService.class, PollingService.ACTION);
	}

}

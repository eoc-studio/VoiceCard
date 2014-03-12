package eoc.studio.voicecard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkReceiver";

    private static boolean isOnline = false;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        isOnline = NetworkUtility.isOnline(context);
        Log.d(TAG, "The network is online ? " + isOnline);
    }
    
    public static boolean isOnline() {
        return isOnline;
    }
}
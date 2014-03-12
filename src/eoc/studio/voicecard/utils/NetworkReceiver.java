package eoc.studio.voicecard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager; 
import android.net.NetworkInfo.State;
import android.util.Log;
import android.widget.Toast; 

public class NetworkReceiver extends BroadcastReceiver {

    State wifiState = null;
    State mobileState = null;
    public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("Test", "intent is " + intent);
//        Log.d("Test", "action is " + intent.getAction());
//        if (ACTION.equals(intent.getAction())) {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//            if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
//                    && State.CONNECTED == mobileState) {
//                Toast.makeText(context, "手機網路連接成功！", Toast.LENGTH_SHORT).show();
//            } else if (wifiState != null && mobileState != null && State.CONNECTED == wifiState
//                    && State.CONNECTED != mobileState) {
//                Toast.makeText(context, "無線網路連接成功！", Toast.LENGTH_SHORT).show();
//            } else if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
//                    && State.CONNECTED != mobileState) {
//                Toast.makeText(context, "手機沒有任何網路...", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

}
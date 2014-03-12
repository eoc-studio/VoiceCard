package eoc.studio.voicecard;

import eoc.studio.voicecard.utils.NetworkUtility;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class BaseActivity extends Activity
{        
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }
    
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isOnline = NetworkUtility.isOnline(context);
            if (isOnline) {
                Toast.makeText(context, context.getResources().getString(R.string.network_connect_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.network_connect_fail),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}

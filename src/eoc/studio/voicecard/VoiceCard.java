package eoc.studio.voicecard;

import eoc.studio.voicecard.utils.NetworkReceiver;
import android.app.Application;
import android.util.Log;

public class VoiceCard extends Application {
    private static final String TAG = "VoiceCardApplication";

    @Override
    public void onCreate() 
    {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        NetworkReceiver.setOnline(getApplicationContext());
    }
    
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory()");
    }
    
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        Log.d(TAG, "onTerminate()");
    }
}

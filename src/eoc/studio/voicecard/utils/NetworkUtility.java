package eoc.studio.voicecard.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtility {

    public static byte[] getWebImage(String imgLink) {
        if (imgLink != null) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imgLink);
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    try {
                        return EntityUtils.toByteArray(entity);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
	public static boolean isOnline(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		State wifiState = null;
		State mobileState = null;
		if (wifiInfo != null)
		{
			wifiState = wifiInfo.getState();
		}
		if (mobileInfo != null)
		{
			mobileState = mobileInfo.getState();
		}

		if (wifiState != null && State.CONNECTED == wifiState)
		{
			return true;
		}
		else
		{
			if (mobileState != null && State.CONNECTED == mobileState)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}

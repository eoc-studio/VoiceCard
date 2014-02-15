package eoc.studio.voicecard.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WebImageUtility {

    public static byte[] getWebImage(String imgLink) {
        if (imgLink != null) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imgLink);
            HttpResponse httpResponse;
            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
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
}

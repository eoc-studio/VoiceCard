package eoc.studio.voicecard.volley.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import eoc.studio.voicecard.volley.toolbox.MultipartRequest;
import eoc.studio.voicecard.volley.toolbox.StringXORer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class JsonTestActivity extends Activity
{
	private final static String TAG = "JsonTestActivity";
	private static final String DRAFT_FOLDER_NAME = "VoiceCard_images";

	private static final String DRAFT_IMAGE_NAME = "signatureDraft.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		String timeStamp = new SimpleDateFormat("yyyyMMdd")
				.format(Calendar.getInstance().getTime());
		String fbID = "1118054263";
		Log.e(TAG, "timeStamp:" + timeStamp);
		Log.e(TAG, "encode:" + StringXORer.encode(fbID + "_" + timeStamp, "nu84x61w"));

		String auth = StringXORer.encode(fbID + "_" + timeStamp, "nu84x61w");
		RequestQueue queue = Volley.newRequestQueue(this);

        String uri_facebook = String
                .format("http://www.charliefind.com/api.php?op=facebook&auth=%1$s&id=%2$s",
auth,
fbID);
        Log.e(TAG, "uri:" + uri_facebook);
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, uri_facebook, null,
				new Response.Listener<JSONObject>()
				{

					@Override
					public void onResponse(JSONObject response)
					{

						Log.e(TAG, "response:" + response.toString());
					}
				}, new Response.ErrorListener()
				{

					@Override
					public void onErrorResponse(VolleyError error)
					{
						Log.e(TAG, "onErrorResponse:" + error.toString());
					}
				});
		
		queue.add(jsObjRequest);
		
		
		 String uri_facebook_update = String
	                .format("http://www.charliefind.com/api.php?op=facebook_update");
		
		HashMap<String, String> paramsFacebook_update = new HashMap<String, String>();
		paramsFacebook_update.put("auth", auth);
		paramsFacebook_update.put("fb_id", fbID);
		paramsFacebook_update.put("name", "name from app");
		paramsFacebook_update.put("industry", "industry from app");

		JsonObjectRequest req = new JsonObjectRequest(uri_facebook_update, new JSONObject(paramsFacebook_update),
		       new Response.Listener<JSONObject>() {
		           @Override
		           public void onResponse(JSONObject response) {
		               try {
		            	   Log.e(TAG,"Response:"+ response.toString(4));
		               } catch (JSONException e) {
		                   e.printStackTrace();
		               }
		           }
		       }, new Response.ErrorListener() {
		           @Override
		           public void onErrorResponse(VolleyError error) {
		        	   Log.e(TAG,"Error: "+ error.getMessage());
		           }
		       });
		queue.add(req);
		/*public MultipartRequest(String url, Response.ErrorListener errorListener,
        Response.Listener<String> listener, File file,
        Map<String, String> mStringPart) {
    super(Method.POST, url, errorListener);*/
		
		String root = Environment.getExternalStorageDirectory().toString();
		File tempDir = new File(root + "/" + DRAFT_FOLDER_NAME);

		File imagefile = new File(tempDir, DRAFT_IMAGE_NAME);
		
		String uri_uploadfile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", auth, fbID);
		MultipartRequest fileResuest = new MultipartRequest(uri_uploadfile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						Log.e(TAG, "fileResuest Response:" + response);
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "fileResuest Error: " + error.getMessage());
					}
				}, imagefile);
		
		queue.add(fileResuest);

	}

}

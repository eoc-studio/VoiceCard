package eoc.studio.voicecard.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
/*import org.apache.http.entity.mime.HttpMultipartMode;
 import org.apache.http.entity.mime.MultipartEntityBuilder;
 import org.apache.http.entity.mime.content.FileBody;*/

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

public class MultipartJsonObjectRequest extends Request<String>
{

	// private MultipartEntity entity = new MultipartEntity();

	MultipartEntityBuilder entity = MultipartEntityBuilder.create();

	HttpEntity httpentity;

	private static final String FILE_PART_NAME = "file";

	private final Response.Listener<String> mListener;

	private final File mFilePart;

	// private final Map<String, String> mStringPart;

	/*
	 * public MultipartRequest(String url, Response.ErrorListener errorListener,
	 * Response.Listener<String> listener, File file, Map<String, String>
	 * mStringPart) { super(Method.POST, url, errorListener);
	 */

	public MultipartJsonObjectRequest(String url, Response.Listener<String> listener,
			Response.ErrorListener errorListener, File file)
	{

		super(Method.POST, url, errorListener);
		mListener = listener;
		mFilePart = file;
		// this.mStringPart = mStringPart;
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		buildMultipartEntity();
	}

	/*
	 * public void addStringBody(String param, String value) {
	 * mStringPart.put(param, value); }
	 */

	private void buildMultipartEntity()
	{

		entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
		/*
		 * for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
		 * entity.addTextBody(entry.getKey(), entry.getValue()); }
		 */
	}

	@Override
	public String getBodyContentType()
	{

		return httpentity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError
	{

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			httpentity = entity.build();
			httpentity.writeTo(bos);
		}
		catch (IOException e)
		{
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response)
	{

		response.headers.put(HTTP.CONTENT_TYPE, "text/plain; charset=utf-8");
		String json = null;
		try
		{
			json = new String(response.data, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		json = URLDecoder.decode(URLDecoder.decode(json));

		return Response.success(json, getCacheEntry());
	}

	@Override
	protected void deliverResponse(String response)
	{

		mListener.onResponse(response);
	}
}

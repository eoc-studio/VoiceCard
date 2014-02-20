package eoc.studio.voicecard.volley.toolbox;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;



public class GsonListRequest<T> extends Request<T> {
	 
    private final Gson                 mGson = new Gson();
    private Class<T>                  classOfT;
    private Type                       mType;
    private final Response.Listener<T> mListener;
    public GsonListRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.classOfT = clazz;
        this.mListener = listener;
    }
 
public GsonListRequest(int method, String url, Type type, Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mType = type;
        this.mListener = listener;
    }
 
    @SuppressWarnings("unchecked")
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return classOfT != null ? Response.success(mGson.fromJson(json, classOfT), HttpHeaderParser.parseCacheHeaders(response)) : (Response<T>) Response.success(mGson.fromJson(json, mType),
                    HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
 
    @Override
    protected void deliverResponse(T response) {
        // TODO Auto-generated method stub
        if (mListener!=null)
            mListener.onResponse(response);
    }
}

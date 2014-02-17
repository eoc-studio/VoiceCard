package eoc.studio.voicecard.recommend;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import eoc.studio.voicecard.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RecommendActivity extends Activity
{

	private final static String TAG = "RecommendActivity";

	Boolean isDebug = true;

	Context context = null;

	public static final String[] titlesArray = new String[] { "Strawberry", "Banana", "Orange",
			"Mixed", "Orange", "Orange", "Orange", "Orange", "Orange" };

	public static final String[] promotionArray = new String[] {
			"It is an aggregate accessory fruit", "It is the largest herbaceous flowering plant",
			"Citrus Fruit", "Mixed Fruits", "Mixed Fruits", "Mixed Fruits", "Mixed Fruits",
			"Mixed Fruits", "Mixed Fruits" };

	public static final Integer[] imageIdArray = { R.drawable.recommed_dummy,
			R.drawable.recommed_dummy, R.drawable.recommed_dummy, R.drawable.recommed_dummy,
			R.drawable.recommed_dummy, R.drawable.recommed_dummy, R.drawable.recommed_dummy,
			R.drawable.recommed_dummy, R.drawable.recommed_dummy };

	public static Boolean[] isNewArray = { true, false, true, true, true, false, true, false, true };

	ListView recommendListView;

	List<RecommendItem> rowItems;

	// add for network data test
	private boolean mHasData = false;

	private boolean mInError = false;

	private static final int RESULTS_PAGE_SIZE =15;

	private ArrayList<PicasaEntry> mEntries = new ArrayList<PicasaEntry>();

	private PicasaArrayAdapter mAdapter;

	// add for network data test end
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		context = getApplicationContext();
		MyVolley.init(context);
		rowItems = new ArrayList<RecommendItem>();
		for (int i = 0; i < titlesArray.length; i++)
		{
			// (int imageId, String title, String promotion,Boolean isNew)
			RecommendItem item = new RecommendItem(imageIdArray[i], titlesArray[i],
					promotionArray[i], isNewArray[i]);
			rowItems.add(item);
		}

		recommendListView = (ListView) findViewById(R.id.act_recommend_lv_main);
		RecommendAdapter adapter = new RecommendAdapter(this,
				R.layout.view_recommend_listview_item, rowItems);
		// recommendListView.setAdapter(adapter);

		mAdapter = new PicasaArrayAdapter(this, 0, mEntries, MyVolley.getImageLoader());
		recommendListView.setAdapter(mAdapter);

		recommendListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
			{

				String url = mEntries.get(position).getThumbnailUrl();

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}
		});

	}

	@Override
	protected void onResume()
	{

		super.onResume();

		if (!mHasData && !mInError)
		{
			loadPage();
		}
	}

	private void loadPage()
	{

		RequestQueue queue = MyVolley.getRequestQueue();

		int startIndex = 1 + mEntries.size();
		JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
				"https://picasaweb.google.com/data/feed/api/all?q=gift&max-results="
						+ RESULTS_PAGE_SIZE + "&thumbsize=160&alt=json" + "&start-index="
						+ startIndex, null, createMyReqSuccessListener(),
				createMyReqErrorListener());

		queue.add(myReq);
	}

	private Response.Listener<JSONObject> createMyReqSuccessListener()
	{

		return new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{

				try
				{
					JSONObject feed = response.getJSONObject("feed");
					JSONArray entries = feed.getJSONArray("entry");
					JSONObject entry;
					for (int i = 0; i < entries.length(); i++)
					{
						entry = entries.getJSONObject(i);

						String url = null;

						JSONObject media = entry.getJSONObject("media$group");
						if (media != null && media.has("media$thumbnail"))
						{
							JSONArray thumbs = media.getJSONArray("media$thumbnail");
							if (thumbs != null && thumbs.length() > 0)
							{
								url = thumbs.getJSONObject(0).getString("url");
							}
						}

						mEntries.add(new PicasaEntry(entry.getJSONObject("title").getString("$t"),
								url));
					}
					mAdapter.notifyDataSetChanged();
				}
				catch (JSONException e)
				{
					showErrorDialog();
				}
			}
		};
	}

	private Response.ErrorListener createMyReqErrorListener()
	{

		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				showErrorDialog();
			}
		};
	}

	private void showErrorDialog()
	{

		mInError = true;

		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setMessage("Error occured");
		b.show();
	}
}
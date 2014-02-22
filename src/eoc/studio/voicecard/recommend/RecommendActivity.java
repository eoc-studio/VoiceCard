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
import eoc.studio.voicecard.mainmenu.MainMenuActivity;
import eoc.studio.voicecard.manager.GetMailListener;
import eoc.studio.voicecard.manager.GetRecommendListener;
import eoc.studio.voicecard.manager.GsonFacebookUser;
import eoc.studio.voicecard.manager.GsonRecommend;
import eoc.studio.voicecard.manager.GsonSend;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.manager.LoginListener;
import eoc.studio.voicecard.manager.MailCountListener;
import eoc.studio.voicecard.manager.NotifyMailReadListener;
import eoc.studio.voicecard.manager.PostMailListener;
import eoc.studio.voicecard.utils.FileUtility;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RecommendActivity extends Activity implements OnClickListener
{

	private final static String TAG = "RecommendActivity";

	private Boolean isDebug = true;

	private Context context = null;

	private ListView recommendListView;

	private ArrayList<PicasaEntry> mEntries = new ArrayList<PicasaEntry>();

	private GsonRecommendAdapter mAdapter;

	private HttpManager httpManager = new HttpManager();

	private ArrayList<GsonRecommend> recommendList = new ArrayList<GsonRecommend>();

	private ImageView moreRecommendImageView;

	private ImageView goToMainMenuImageView;

	// add for network data test end
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		context = getApplicationContext();
		findViews();
		setListeners();

	}

	private void findViews()
	{

		moreRecommendImageView = (ImageView) findViewById(R.id.act_recommend_iv_main_more);
		goToMainMenuImageView = (ImageView) findViewById(R.id.act_recommend_iv_main_menu);
		recommendListView = (ListView) findViewById(R.id.act_recommend_lv_main);
	}

	@Override
	protected void onResume()
	{

		super.onResume();

		getRecommenInfoFromServer();

		/*
		 * httpManager.init(context,"1475871733");// 1118054263(BRUCE)
		 * GsonFacebookUser user = new GsonFacebookUser("1118054263",
		 * "19900101", "this is img link", "this is locale", "this is link",
		 * "this is country", "this is timezone", "this is title",
		 * "this is email", "this is name", "male", "this is edu",
		 * "this is industry", "this is mobile");
		 * httpManager.facebookLogin(context, user, new LoginListener() {
		 * 
		 * @Override public void onResult(Boolean isSuccess, String information)
		 * {
		 * 
		 * Log.e(TAG, "httpManager.fascebookLogin() isSuccess:" + isSuccess +
		 * ",information:" + information); }
		 * 
		 * });
		 * 
		 * try { httpManager.postMail(context, "1118054263",
		 * Uri.parse("/storage/sdcard0/VoiceCard_images/Image-7833.jpg"),
		 * Uri.parse("/storage/sdcard0/MIUI/sound_recorder/speech000.mp3"),
		 * Uri.parse("/storage/sdcard0/Document/edittext001.txt"),
		 * Uri.parse("/storage/sdcard0/VoiceCard_images/Image-2736.jpg"),
		 * "fontSize", "fontColor", "thisCardName", new PostMailListener() {
		 * 
		 * @Override public void onResult(Boolean isSuccess, String information)
		 * {
		 * 
		 * Log.e(TAG, "httpManager.postMail() isSuccess:" + isSuccess +
		 * ",information:" + information); }
		 * 
		 * });
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// httpManager.init(context,"1475871733");
		// httpManager.getUnreadMailCount(context, new MailCountListener()
		// {
		// @Override
		// public void onResult(Boolean isSuccess, int count)
		// {
		//
		// Log.e(TAG, "httpManager.getUnreadMailCount() isSuccess:" + isSuccess
		// + ",information:" + String.valueOf(count));
		// }
		//
		// });
		//
		// httpManager.getMails(context, new GetMailListener()
		// {
		// @Override
		// public void onResult(Boolean isSuccess, ArrayList<GsonSend> mails)
		// {
		//
		// Log.e(TAG,
		// "httpManager.getMails() isSuccess:" + isSuccess + ",mails:"
		// + mails.toString());
		// }
		//
		// });
		//
		// httpManager.notifyMailsRead(context, new NotifyMailReadListener()
		// {
		//
		// @Override
		// public void onResult(Boolean isSuccess, String information)
		// {
		//
		// Log.e(TAG, "httpManager.notifyMailsRead() isSuccess:" + isSuccess +
		// ",information:"
		// + information);
		//
		// }
		//
		// });

		Log.e(TAG,
				"FileUtility.getRandomImageName(\"jpg\"):" + FileUtility.getRandomImageName("jpg"));
		Log.e(TAG,
				"FileUtility.getRandomSpeechName(\"mp3\"):"
						+ FileUtility.getRandomSpeechName("mp3"));
		Log.e(TAG, "FileUtility.getRandomSignName(\"jpg\"):" + FileUtility.getRandomSignName("jpg"));
	}

	private void getRecommenInfoFromServer()
	{

		httpManager.getRecommend(context, new GetRecommendListener()
		{
			@Override
			public void onResult(Boolean isSuccess, ArrayList<GsonRecommend> recommends)
			{

				Log.e(TAG, "httpManager.getRecommend() isSuccess:" + isSuccess + ",mails:"
						+ recommends.toString());

				if (recommends != null && recommends.size() > 0)
				{

					recommendList = recommends;
					Log.e(TAG, "recommends.get(0).getImg():" + recommends.get(0).getImg()
							+ "recommends.get(0).getName():" + recommends.get(0).getName());
					mAdapter = new GsonRecommendAdapter(context, 0, recommendList, httpManager
							.getImageLoader(context));
					recommendListView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
				}

			}

		});
	}

	private void showErrorDialog()
	{

		// mInError = true;

		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setMessage("Error occured");
		b.show();
	}

	private void setListeners()
	{

		goToMainMenuImageView.setOnClickListener(this);
		moreRecommendImageView.setOnClickListener(this);
		recommendListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
			{

				String url = recommendList.get(position).getUrl();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}
		});
	}

	@Override
	public void onClick(View v)
	{

		if (v == goToMainMenuImageView)
		{
			onGoToMainMenuClicked();
		}
		else if (v == moreRecommendImageView)
		{
			onMoreRecommendClicked();
		}

	}

	private void onGoToMainMenuClicked()
	{

		Intent intent = new Intent();
		intent.setClass(context, MainMenuActivity.class);
		startActivity(intent);
		finish();
	}

	private void onMoreRecommendClicked()
	{
		Log.d(TAG, "onMoreRecommendClicked()");
		String url = "http://www.charliefind.com/";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}
}

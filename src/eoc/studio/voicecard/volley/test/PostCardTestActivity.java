package eoc.studio.voicecard.volley.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.CardDraft;
import eoc.studio.voicecard.card.editor.CardDraftManager;
import eoc.studio.voicecard.mailbox.Mail;
import eoc.studio.voicecard.mailbox.MailboxActivity;
import eoc.studio.voicecard.mailbox.MailsAdapterData;
import eoc.studio.voicecard.mailbox.MailsAdapterView;
import eoc.studio.voicecard.manager.GetFacebookInfoListener;
import eoc.studio.voicecard.manager.GetMailListener;
import eoc.studio.voicecard.manager.GsonFacebookUser;
import eoc.studio.voicecard.manager.GsonSend;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.manager.MailCountListener;
import eoc.studio.voicecard.manager.PostMailListener;
import eoc.studio.voicecard.manager.UploadDiyListener;

public class PostCardTestActivity extends Activity
{
	private static final String TAG = "PostCardTestActivity";

	private Context context;

	private HttpManager httpManager = new HttpManager();

	private CardDraftManager cardDraftManager;

	private CardDraft cardDraft;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		httpManager.init(context, "100007811983123"); // eoc fb
		cardDraftManager = new CardDraftManager();
		cardDraftManager.init(getApplicationContext());
		try
		{

			try
			{
				cardDraftManager.resetOwner();
				cardDraft = cardDraftManager.openDraft();
			}
			catch (Exception e)
			{
				Toast.makeText(context, "open draft fail", Toast.LENGTH_LONG).show();
			}

			httpManager.getFacebookUserInformation(context, new GetFacebookInfoListener()
			{
				@Override
				public void onResult(Boolean isSuccess, ArrayList<GsonFacebookUser> facebookUserList)
				{

					if (isSuccess)
					{
						if (facebookUserList != null)
						{

							Log.d(TAG, "onResume()facebookUserList.get(0).getLink(): "
									+ facebookUserList.get(0).getLink());

						}
					}
				}
			});
			// httpManager.postMail(context, "1118054263", // bruce
			// cardDraft.getImageUri(),
			// cardDraft.getSoundUri(),
			// cardDraft.getMessage(),
			// cardDraft.getSignDraftImageUri(),
			// String.valueOf(cardDraft.getMessageTextSizeType()),
			// String.valueOf(cardDraft.getMessageTextColor()), "thisCardName",
			// new PostMailListener()
			// {
			//
			// @Override
			// public void onResult(Boolean isSuccess, String information)
			// {
			//
			// Log.e(TAG, "httpManager.postMail() isSuccess:" + isSuccess
			// + ",information:" + information);
			//
			// Toast.makeText(context, "httpManager.postMail() isSuccess:"+
			// isSuccess, Toast.LENGTH_LONG).show();
			// }
			//
			// });

			ArrayList<String> sendToList = new ArrayList<String>();
			sendToList.add("1118054263");// bruce
			sendToList.add("1845302303");// john
			sendToList.add("100003488626817");// Ryan
			sendToList.add("1475871733");// Steven

			httpManager.postMailByList(context, sendToList, cardDraft.getImageUri(),
					cardDraft.getSoundUri(), cardDraft.getMessage(),
					cardDraft.getSignDraftImageUri(),
					String.valueOf(cardDraft.getMessageTextSizeType()),
					String.valueOf(cardDraft.getMessageTextColor()), "7",
					new PostMailListener()
					{

						@Override
						public void onResult(Boolean isSuccess, String information)
						{

							Log.e(TAG, "httpManager.postMailByList() isSuccess:" + isSuccess
									+ ",information:" + information);

							Toast.makeText(context,
									"httpManager.postMailByList() isSuccess:" + isSuccess,
									Toast.LENGTH_LONG).show();
						}

					});

			httpManager.uploadDIY(context, cardDraft.getImageUri(), new UploadDiyListener()
			{

				@Override
				public void onResult(Boolean isSuccess, String URL)
				{

					Log.e(TAG, "httpManager.uploadDIY() isSuccess:" + isSuccess + ",URL:" + URL);

				}

			});

		}
		catch (Exception e)
		{ // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume()
	{

		super.onResume();
		Log.d(TAG, "onResume()");

	}

	@Override
	public void onPause()
	{

		super.onPause();
		Log.d(TAG, "onPause()");
		finish();
	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();
		Log.d(TAG, "onDestroy()");

	}
}

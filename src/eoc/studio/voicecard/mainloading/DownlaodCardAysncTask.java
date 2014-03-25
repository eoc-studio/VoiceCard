package eoc.studio.voicecard.mainloading;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import eoc.studio.voicecard.card.database.CardAssistant;
import eoc.studio.voicecard.menu.SaveDraft;
import eoc.studio.voicecard.utils.FileUtility;

import android.R.integer;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownlaodCardAysncTask extends
		AsyncTask<ArrayList<CardAssistant>, Void, ArrayList<CardAssistant>>
{
	private final static String TAG = "DownlaodCardAysncTask";

	public DownlaodCardAysncTaskListener delegate;

	Context context;

	ArrayList<CardAssistant> cardAssistantList = new ArrayList<CardAssistant>();

	public DownlaodCardAysncTask(Context context, DownlaodCardAysncTaskListener delegate)
	{

		super();
		this.context = context;
		this.delegate = delegate;
	}

	@Override
	protected void onPreExecute()
	{

		super.onPreExecute();
	}

	@Override
	protected ArrayList<CardAssistant> doInBackground(ArrayList<CardAssistant>... params)
	{

		cardAssistantList = (ArrayList<CardAssistant>) params[0];

		for (int index = 0; index < cardAssistantList.size(); index++)
		{
			Log.d(TAG, "cardAssistantList[" + index + "]: " + cardAssistantList.get(index));
		}

		for (int index = 0; index < cardAssistantList.size(); index++)
		{

			boolean isCloseURLNotNull = cardAssistantList.get(index).getCloseURL() != null  && !cardAssistantList.get(index).getCloseURL().equals("");
			boolean isCoverURLNotNull = cardAssistantList.get(index).getCoverURL() != null && !cardAssistantList.get(index).getCoverURL().equals("");
			boolean isLeftURLNotNull = cardAssistantList.get(index).getLeftURL() != null && !cardAssistantList.get(index).getLeftURL().equals("");
			boolean isOpenURLNotNull = cardAssistantList.get(index).getOpenURL() != null && !cardAssistantList.get(index).getOpenURL().equals("");
			boolean isRightURLNotNull = cardAssistantList.get(index).getRightURL() != null && !cardAssistantList.get(index).getRightURL().equals("");

			if (isCloseURLNotNull && isCoverURLNotNull && isLeftURLNotNull && isOpenURLNotNull
					&& isRightURLNotNull)
			{

				try
				{
					String closeLocalPath = downloadFile(
							cardAssistantList.get(index).getCloseURL(), cardAssistantList
									.get(index).getCardID());

					cardAssistantList.get(index).setCloseLocalPath(closeLocalPath);

					String coverLocalPath = downloadFile(
							cardAssistantList.get(index).getCoverURL(), cardAssistantList
									.get(index).getCardID());

					cardAssistantList.get(index).setCoverLocalPath(coverLocalPath);

					String leftLocalPath = downloadFile(cardAssistantList.get(index).getLeftURL(),
							cardAssistantList.get(index).getCardID());

					cardAssistantList.get(index).setLeftLocalPath(leftLocalPath);

					String openLocalPath = downloadFile(cardAssistantList.get(index).getOpenURL(),
							cardAssistantList.get(index).getCardID());

					cardAssistantList.get(index).setOpenLocalPath(openLocalPath);

					String rightLocalPath = downloadFile(
							cardAssistantList.get(index).getRightURL(), cardAssistantList
									.get(index).getCardID());

					cardAssistantList.get(index).setRightLocalPath(rightLocalPath);

					cardAssistantList.get(index).setCardLocalEditedDate(
							cardAssistantList.get(index).getCardEditedDate());

				}
				catch (Exception e)
				{
					Log.d(TAG, "doInBackground() Exception:" + e.getMessage());
					e.printStackTrace();

					return null;
				}

			}
		}
		return cardAssistantList;

	}

	private String downloadFile(String URL, int cardId) throws Exception
	{

		int count;
		URL url = new URL(URL);
		URLConnection conexion = url.openConnection();
		conexion.connect();

		InputStream input = new BufferedInputStream(url.openStream(), 512);
		OutputStream output = new FileOutputStream(getSaveFileName(cardId, URL));
		byte data[] = new byte[512];
		long total = 0;
		while ((count = input.read(data)) != -1)
		{
			total += count;
			// publishProgress(""+(int)((total*100)/lenghtOfFile));
			output.write(data, 0, count);
		}
		output.flush();
		output.close();
		input.close();

		return getSaveFileName(cardId, URL);
	}

	@Override
	protected void onPostExecute(ArrayList<CardAssistant> result)

	{

		delegate.processFinish(result);
	}

	public String getSaveFileName(int cardId, String urlLink)
	{

		String cardID = String.valueOf(cardId);
		String wholePath = urlLink;
		String name = null;

		int start, end;
		start = wholePath.lastIndexOf('/');
		end = wholePath.length();     // lastIndexOf('.');
		name = wholePath.substring((start + 1), end);
		String dpiFolderName = FileUtility.getFolderNameUsingSystemDPI(this.context);
		File pathDir = new File(context.getFilesDir().getPath() + "/CardImages/" + dpiFolderName
				+ "/" + cardID);
		if (!pathDir.exists()) pathDir.mkdirs();

		name = context.getFilesDir().getPath() + "/CardImages/" + dpiFolderName + "/" + cardID
				+ "/" + name;
		// File pathDir = new File(context.getFilesDir().getPath()
		// + "/CardImages/" + cardID);
		// if (!pathDir.exists()) pathDir.mkdirs();
		//
		// name = context.getFilesDir().getPath() + "/CardImages/" + cardID
		// + "/" + name;

		Log.d(TAG, "getFileName() return valuse:" + name);
		return name;
	}

}

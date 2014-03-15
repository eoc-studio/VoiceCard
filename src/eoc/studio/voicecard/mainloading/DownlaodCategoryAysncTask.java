package eoc.studio.voicecard.mainloading;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import eoc.studio.voicecard.card.database.CategoryAssistant;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownlaodCategoryAysncTask extends
		AsyncTask<ArrayList<CategoryAssistant>, Void, ArrayList<CategoryAssistant>>
{
	private final static String TAG = "DownlaodCategoryAysncTask";
	public DownlaodCategoryAysncTaskListener delegate;
	String fpath;

	Context context;

	ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();

	public DownlaodCategoryAysncTask(Context context,DownlaodCategoryAysncTaskListener delegate)
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
	protected ArrayList<CategoryAssistant> doInBackground(ArrayList<CategoryAssistant>... params)
	{

		categoryAssistantList = (ArrayList<CategoryAssistant>) params[0];

		for (int index = 0; index < categoryAssistantList.size(); index++)
		{
			try
			{
				int count;
				Log.d(TAG, "doInBackground() Current:"
						+ categoryAssistantList.get(index).toString());
				
				fpath = getFileName(categoryAssistantList.get(index));
				URL url = new URL(categoryAssistantList.get(index).getCategoryURL());
				URLConnection conexion = url.openConnection();
				conexion.connect();
				int lenghtOfFile = conexion.getContentLength();
				InputStream input = new BufferedInputStream(url.openStream(), 512);
				OutputStream output = new FileOutputStream(fpath);
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

				categoryAssistantList.get(index).setCategoryLoocalPath(fpath);
				
				//update the edit date to local
				categoryAssistantList.get(index).setCategoryLocalEditedDate(categoryAssistantList.get(index).getCategoryEditedDate());
			}
			catch (Exception e)
			{
				Log.d(TAG, "doInBackground() Exception:" + e.getMessage());
				e.printStackTrace();

				return null;
			}
		}
		return categoryAssistantList;

	}

	/*
	 * @Override protected void onProgressUpdate(String... progress) {
	 * prgBar1.setProgress(Integer.parseInt(progress[0])); if(show) { File dir =
	 * Environment.getExternalStorageDirectory(); File imgFile = new File(dir,
	 * getFileName(this.paths[(current-1)])); Bitmap bmp =
	 * BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	 * imgv1.setImageBitmap(bmp); show = false; } }
	 */

	@Override
	protected void onPostExecute(ArrayList<CategoryAssistant> result)

	{
		delegate.processFinish(result);
	}

	/*
	 * @Override protected Boolean
	 * doInBackground(ArrayList<CategoryAssistant>... params) {
	 * 
	 * 
	 * return null; }
	 */

	public String getFileName(CategoryAssistant categoryAssistant)
	{

		String catId = String.valueOf(categoryAssistant.getCategoryID());

		String wholePath = categoryAssistant.getCategoryURL();
		String name = null;

		int start, end;
		start = wholePath.lastIndexOf('/');
		end = wholePath.length();     // lastIndexOf('.');
		name = wholePath.substring((start + 1), end);

		Log.d(TAG, "getFileName() name:" + name);
		Log.d(TAG, "getFileName() context.getFilesDir().getPath():"
				+ this.context.getFilesDir().getPath());
		Log.d(TAG, "getFileName() catId:" + catId);

		File pathDir = new File(context.getFilesDir().getPath() + "/CategoryImages/" + catId);
		if (!pathDir.exists()) pathDir.mkdirs();

		name = context.getFilesDir().getPath() + "/CategoryImages/" + catId + "/" + name;

		Log.d(TAG, "getFileName() return valuse:" + name);

		return name;
	}

}

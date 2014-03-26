package eoc.studio.voicecard.polling;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.mainloading.MainLoadingActivity;
import eoc.studio.voicecard.manager.HttpManager;
import eoc.studio.voicecard.manager.MailCountListener;
import eoc.studio.voicecard.utils.NetworkUtility;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PollingService extends Service
{

	private final static String TAG = "PollingService";

	public static final String ACTION = "eoc.studio.voicecard.polling.PollingService";

	private Notification mNotification;

	private NotificationManager mManager;

	private static int unreadCount = 0;

	@Override
	public IBinder onBind(Intent intent)
	{

		return null;
	}

	@Override
	public void onCreate()
	{

		initNotifiManager();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{

		new PollingThread().start();
	}

	private void initNotifiManager()
	{

		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "New Message";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void showNotification()
	{

		mNotification.when = System.currentTimeMillis();
		// Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MainLoadingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
				Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		mNotification.setLatestEventInfo(this, getResources().getString(R.string.app_name),
				"You have new message!", pendingIntent);
		mManager.notify(0, mNotification);
	}

	class PollingThread extends Thread
	{
		@Override
		public void run()
		{

			Log.d(TAG, "PollingThread() Polling");
			try
			{
				if (NetworkUtility.isOnline(getApplicationContext()))
				{

					Log.d(TAG, "PollingThread() isOnline: true");
					HttpManager httpManager = new HttpManager();

					httpManager.getUnreadMailCount(getApplicationContext(), new MailCountListener()
					{
						@Override
						public void onResult(Boolean isSuccess, int count)
						{

							Log.e(TAG, "httpManager.getUnreadMailCount() isSuccess:" + isSuccess
									+ ",information:" + String.valueOf(count));

							Log.d(TAG, "PollingThread() unreadCount: " + unreadCount);
 
							if (count == 0)
							{
								unreadCount = count;
							}

							if (isSuccess && count > 0 && unreadCount != count)
							{
								unreadCount = count;
								showNotification();
							}
						}
					});
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onDestroy()
	{

		super.onDestroy();
		Log.d(TAG, "onDestroy");
		System.out.println("Service:onDestroy");
	}

}

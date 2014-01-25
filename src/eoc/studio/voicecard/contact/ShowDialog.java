package eoc.studio.voicecard.contact;

import eoc.studio.voicecard.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.WindowManager;

public class ShowDialog
{
	private static ProgressDialog mLoadingDialog;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void showLoadingDialog(final Context context)
	{
		mLoadingDialog = new ProgressDialog(context);
		mLoadingDialog.setMessage(context.getResources().getString(R.string.file_process_loading));
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		mLoadingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		mLoadingDialog.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		mLoadingDialog.setCancelable(false);
		mLoadingDialog.show();
		mLoadingDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				switch (keyCode)
				{
				case KeyEvent.KEYCODE_HOME:
					System.out.println("[UnZipFiles]unZipThread:KEYCODE_HOME");
					return true;
				case KeyEvent.KEYCODE_BACK:
					System.out.println("[UnZipFiles]unZipThread:KEYCODE_BACK");
					return true;
				}
				return false;
			}
		});
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void dismissLoadingDialog()
	{
		if (mLoadingDialog != null && mLoadingDialog.isShowing())
		{
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static boolean isShowLoadingDialog()
	{
		if (mLoadingDialog != null) { return mLoadingDialog.isShowing(); }
		return false;
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

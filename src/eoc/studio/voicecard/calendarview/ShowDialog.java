package eoc.studio.voicecard.calendarview;

import eoc.studio.voicecard.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ShowDialog
{
	private static ProgressDialog mLoadingDialog;
	private static AlertDialog alert;
	private static EditText edit_text_View;
	private static Button button_view_yes, button_view_cancel;
	private static View textEntryView;
	private static final String ACTIVITY_NAME_SET_CALENDAR_MAIN_VIEW = "eoc.studio.voicecard.calendarview.SetCalendarMainView";

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
	protected static void confirmDialog(final Context context, String title, String msg)
	{
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setIcon(null)
				.setMessage(msg)
				.setNegativeButton((String) context.getResources().getString(R.string.cancelBtn),
						null)
				.setPositiveButton((String) context.getResources().getString(R.string.yesBtn),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialoginterface, int x)
							{
							}
						}).show();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void showSetValueDialog(final Context context, final String date)
	{
		showSetValueDialog(context, "", date, "");
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected static void showSetValueDialog(final Context context, final String eventID,
			final String date, final String title)
	{
		if (getLayout(context) == -1) { return; }
		//
		alert = new AlertDialog.Builder(context, R.style.TranslucentDialog).create();
		edit_text_View = (EditText) textEntryView.findViewById(R.id.edit_text_view);
		button_view_yes = (Button) textEntryView.findViewById(R.id.yes_btn);
		button_view_cancel = (Button) textEntryView.findViewById(R.id.cancel_btn);
		//
		edit_text_View.setBackgroundResource(android.R.color.transparent);
		//
		if (!eventID.equals(""))
		{
			edit_text_View.setText(title);
		}
		//
		button_view_yes.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (eventID.equals(""))
				{
					DataProcess.addEvent(context, edit_text_View.getText().toString(), date);
				}
				else
				{
					DataProcess.updataEvent(context, edit_text_View.getText().toString(), eventID);
				}
				SetOfCalendarView.getNewView();
				alert.dismiss();
			}
		});
		button_view_cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				alert.dismiss();
			}
		});
		//
		alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		WindowManager.LayoutParams lp = alert.getWindow().getAttributes();
		lp.alpha = 0.9f;
		alert.getWindow().setAttributes(lp);
		alert.setCanceledOnTouchOutside(true);
		alert.setCancelable(false);
		alert.setView(textEntryView, 0, 0, 0, 0);
		alert.show();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int getLayout(final Context context)
	{
		if (context == null) { return -1; }
		if (ACTIVITY_NAME_SET_CALENDAR_MAIN_VIEW.equals(context.getClass().getName()))
		{
			LayoutInflater factory = LayoutInflater.from(context);
			textEntryView = factory.inflate(R.layout.dialog_of_item, null);
			return R.layout.set_of_calendar_item;
		}
		return -1;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void setEditText(final String value)
	{
		edit_text_View.setText(value);
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

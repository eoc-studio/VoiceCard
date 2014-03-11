package eoc.studio.voicecard.contact;

import eoc.studio.voicecard.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.WindowManager;

public class ShowDialog
{
    private static ProgressDialog loadingDialog, countDialog;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void showLoadingDialog(final Context context)
    {
        loadingDialog = new ProgressDialog(context);
        loadingDialog.setMessage(context.getResources().getString(R.string.file_process_loading));
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        loadingDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        loadingDialog.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        loadingDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener()
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void dismissLoadingDialog()
    {
        if (loadingDialog != null && loadingDialog.isShowing())
        {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static boolean isShowLoadingDialog()
    {
        if (loadingDialog != null)
        {
            return loadingDialog.isShowing();
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void dismissCountDialog()
    {
        if (countDialog != null && countDialog.isShowing())
        {
            countDialog.dismiss();
            countDialog = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void setCountDialogProgress(final int count)
    {
        if (countDialog != null && countDialog.isShowing())
        {
            countDialog.setProgress(count);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static boolean isShowCountDialog()
    {
        if (countDialog != null)
        {
            return countDialog.isShowing();
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

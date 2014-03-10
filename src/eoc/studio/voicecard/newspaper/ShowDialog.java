package eoc.studio.voicecard.newspaper;

import eoc.studio.voicecard.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowDialog
{
    private static AlertDialog alert;
    private static EditText input;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void showSetValueDialog(final Context context, final TextView view, final ImageView imgView,
            final String title, final String message, final String orderValue)
    {
        input = new EditText(context);
        input.setSingleLine();
        input.setTextSize(24);
        setEditText(orderValue);

        alert = new AlertDialog.Builder(context).create();
        alert.setCanceledOnTouchOutside(true);
        alert.setCancelable(false);
        alert.setTitle(title);

        if (!message.equalsIgnoreCase(""))
        {
            alert.setMessage(message);
        }

        alert.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.set_dilog_ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        view.setText(input.getText().toString());
                        view.setBackgroundColor(Color.RED);
                        imgView.setVisibility(View.INVISIBLE);
                    }
                });
        alert.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.set_dilog_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
        alert.setView(input);
        alert.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void setEditText(final String value)
    {
        input.setText(value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

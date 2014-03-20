package eoc.studio.voicecard.newspaper;

import eoc.studio.voicecard.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowDialog
{
    private static AlertDialog alert;
    private static EditText input;
    private static final int maxInputLength = 11;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void showSetValueDialog(final Context context, final TextView view, final ImageView imgView,
            final String title, final String message, final String orderValue)
    {
        input = new EditText(context);
        input.setSingleLine();
        input.setTextSize(24);
        input.setFilters(new InputFilter[]
        { new InputFilter.LengthFilter(maxInputLength) });

        setEditText(orderValue);

        alert = new AlertDialog.Builder(context).create();
        alert.setCanceledOnTouchOutside(true);
        alert.setCancelable(false);
        alert.setTitle(title);

        if (!message.equalsIgnoreCase(""))
        {
            alert.setMessage(message);
        }

        alert.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.set_dilog_cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });

        alert.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.set_dilog_ok),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        view.setText(input.getText().toString());
                        setTextViewBackground(view);
                        imgView.setVisibility(View.INVISIBLE);
                    }
                });
        alert.setView(input);
        alert.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void setTextViewBackground(final View view)
    {
        switch (ValueCacheProcessCenter.callProcessingView)
        {
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_LEFT_MAIN_VIEW:
            case ValueCacheProcessCenter.EDIT_NEWSPAPER_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                view.setBackgroundColor(Color.BLACK);
            }
                break;
            case ValueCacheProcessCenter.EDIT_MAGAZINE_STYTLE_BOTTON_LEFT_MAIN_VIEW:
            {
                view.setBackgroundColor(Color.RED);
            }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void setEditText(final String value)
    {
        input.setText(value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

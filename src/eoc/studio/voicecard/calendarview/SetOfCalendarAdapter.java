package eoc.studio.voicecard.calendarview;

import java.util.ArrayList;
import java.util.Map;

import eoc.studio.voicecard.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetOfCalendarAdapter extends BaseAdapter
{
	private static Context mContext;
	private ArrayList<Map<String, String>> mData = null;
	private static int mSelected = -1;
	private final LayoutInflater inflater;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public SetOfCalendarAdapter(Context context, ArrayList<Map<String, String>> data)
	{
		mContext = context;
		mSelected = -1;
		if (data != null)
		{
			mData = new ArrayList<Map<String, String>>(data);
		}
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getCount()
	{
		if (mData == null) { return 0; }
		return mData.size();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Object getItem(int position)
	{
		return position;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public View getView(final int position, View view, ViewGroup parent)
	{
		if (view == null)
		{
			view = inflater.inflate(R.layout.set_of_calendar_item, null);
		}
		final ImageView editView = (ImageView) view.findViewById(R.id.editView);
		final ImageView delView = (ImageView) view.findViewById(R.id.delView);
		final TextView titleTextView = (TextView) view.findViewById(R.id.eventTitle);
		editView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				ShowDialog
						.showSetValueDialog(
								mContext,
								mData.get(position)
										.get(CalendarIntentHelper.INSTANCES_PROJECTION[CalendarIntentHelper.INSTANCES_ID_INDEX]),
								DataProcess.formatDate(DataProcess.getDataMilliSeconds(DataProcess
										.getSelectedEventDate()), "yyyyMMdd"),
								mData.get(position)
										.get(CalendarIntentHelper.INSTANCES_PROJECTION[CalendarIntentHelper.INSTANCES_TITLE_INDEX]));
			}
		});
		delView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				confirmDialog(
						mData.get(position)
								.get(CalendarIntentHelper.INSTANCES_PROJECTION[CalendarIntentHelper.INSTANCES_ID_INDEX]),
						mData.get(position)
								.get(CalendarIntentHelper.INSTANCES_PROJECTION[CalendarIntentHelper.INSTANCES_TITLE_INDEX]));
			}
		});
		titleTextView
				.setText(mData
						.get(position)
						.get(CalendarIntentHelper.INSTANCES_PROJECTION[CalendarIntentHelper.INSTANCES_TITLE_INDEX]));
		return view;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final void confirmDialog(final String eventId, final String title)
	{
		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getResources().getString(R.string.delete_confirm_title))
				.setIcon(null)
				.setMessage(
						mContext.getResources().getString(R.string.delete_confirm_message) + " "
								+ title + "?")
				.setNegativeButton(mContext.getResources().getString(R.string.cancelBtn), null)
				.setPositiveButton(mContext.getResources().getString(R.string.yesBtn),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialoginterface, int x)
							{
								DataProcess.deleteEvent(mContext, eventId);
								SetOfCalendarView.getNewView();
								refreshDays();
							}
						}).show();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final String setSelected(int position)
	{
		mSelected = position;
		notifyDataSetChanged();
		return "";
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected final void refreshDays()
	{
		notifyDataSetChanged();
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

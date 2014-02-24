package eoc.studio.voicecard.calendarview;

import eoc.studio.voicecard.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SetCalendarMainView extends FragmentActivity implements OnClickListener
{
	private static Context mContext;
	private static TextView titleTextView;
	private static Button addButton, confirmButton;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bundle bundle = this.getIntent().getExtras();
		DataProcess.setSelectedEventDate((String) bundle.getString(DataProcess.EVENT_DATE)
				.toString());
		setContentView(R.layout.activity_set_calendar_main_view);
		findView();
		buttonFunction();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void findView()
	{
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		addButton = (Button) findViewById(R.id.buttonConfirmView);
		confirmButton = (Button) findViewById(R.id.buttonAddView);
		titleTextView.setText(DataProcess.formatDate(
				DataProcess.getDataMilliSeconds(DataProcess.getSelectedEventDate()),
				"yyyy-MM-dd(E)"));
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void buttonFunction()
	{
		addButton.setOnClickListener(this);
		confirmButton.setOnClickListener(this);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.buttonConfirmView:
		{
			finish();
		}
			break;
		case R.id.buttonAddView:
		{
			ShowDialog.showSetValueDialog(mContext,
					DataProcess.formatDate(
							DataProcess.getDataMilliSeconds(DataProcess.getSelectedEventDate()),
							"yyyyMMdd"));
		}
			break;
		}
	}
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}

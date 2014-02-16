package eoc.studio.voicecard.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;

public class AudioRecorderActivity extends BaseActivity
{
	private MediaRecorder mediaRecorder = new MediaRecorder();
	private Button start;
	private Button stop;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initLayout();
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_audio_record);
		findViews();
		setListener();
	}

	private void findViews()
	{
		start = (Button) findViewById(R.id.act_audio_recorder_btn_start);
		stop = (Button) findViewById(R.id.act_audio_recorder_btn_stop);
	}

	private void setListener()
	{
		start.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				v.setEnabled(false);
				startRecord();
			}

		});
		stop.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				v.setEnabled(false);
				stopRecord();
			}

		});
	}

	private void startRecord()
	{
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

		File file = new File(getCacheDir(), "AAA");
		if (file.exists())
		{
			file.delete();
		}
		mediaRecorder.setOutputFile(file.getPath());

		try
		{
			mediaRecorder.prepare();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		mediaRecorder.start();
	}

	private void stopRecord()
	{
		if (mediaRecorder != null)
		{
			mediaRecorder.stop();
			mediaRecorder.release();
		}
	}
}

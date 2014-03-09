package eoc.studio.voicecard.audio;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.FileUtility;

public class AudioRecorderActivity extends BaseActivity
{
	public static final String EXTRA_KEY_FILEPATH = "file_path";
	public static final String EXTRA_KEY_DURATION_MILLISECOND = "duration_millisecond";

	private static final String TAG = "AudioRecorderActivity";
	private static final int MAX_DURATION_MILLISECONDS = 1000 * 60 * 3;
	private static String outputFile = null;
	private static final String LAST_SAVED_FILE_NAME = "audio_record_last.3gp";
	private File lastSavedFileFolder;

	private TextView currentTime;
	private TextView duration;
	private TextView timeSeparator;

	private ImageView back;
	private ImageView ok;
	private TextView recordingSign;

	private LinearLayout initialBar;
	private LinearLayout playingBar;
	private LinearLayout recordingBar;
	private LinearLayout doneBar;

	private TextView record;
	private TextView open;
	private TextView pause;
	private TextView resume;
	private TextView stopRecording;
	private TextView play;
	private TextView save;
	private TextView delete;

	private enum UiState
	{
		INITIAL, PLAYING, RECORDING, DONE;
	}

	private UiState currentState = UiState.INITIAL;

	private MediaRecorder recorder = null;
	private MediaPlayer mediaPlayer = null;

	private int audioDuration = 0;
	private int recordingTime = 0;
	private Handler uiHandler = new Handler();

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		lastSavedFileFolder = getFilesDir();

		initFromIntent();
		initLayout();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (currentState != UiState.INITIAL)
		{
			deleteFile();
			resetTimeAndDuration();
		}
		setUiState(UiState.INITIAL);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		stopUpdateTime();
		stopRecording();
		stopPlaying();
	}

	private void initFromIntent()
	{
		outputFile = getIntent().getStringExtra(EXTRA_KEY_FILEPATH);
		Log.d(TAG, "initFromIntent - outputFile: " + outputFile);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_audio_recorder);
		findViews();
		setListeners();
	}

	private void findViews()
	{
		currentTime = (TextView) findViewById(R.id.act_audio_recorder_tv_current_time);
		duration = (TextView) findViewById(R.id.act_audio_recorder_tv_duration);
		timeSeparator = (TextView) findViewById(R.id.act_audio_recorder_tv_time_separator);

		back = (ImageView) findViewById(R.id.act_audio_recorder_iv_back);
		ok = (ImageView) findViewById(R.id.act_audio_recorder_iv_ok);
		recordingSign = (TextView) findViewById(R.id.act_audio_recorder_tv_recording_sign);

		initialBar = (LinearLayout) findViewById(R.id.act_audio_recorder_llyt_initial_bar);
		playingBar = (LinearLayout) findViewById(R.id.act_audio_recorder_llyt_playing_bar);
		recordingBar = (LinearLayout) findViewById(R.id.act_audio_recorder_llyt_recording_bar);
		doneBar = (LinearLayout) findViewById(R.id.act_audio_recorder_llyt_done_bar);

		record = (TextView) findViewById(R.id.act_audio_recorder_tv_record);
		open = (TextView) findViewById(R.id.act_audio_recorder_tv_open_file);
		pause = (TextView) findViewById(R.id.act_audio_recorder_tv_pause);
		resume = (TextView) findViewById(R.id.act_audio_recorder_tv_resume);
		stopRecording = (TextView) findViewById(R.id.act_audio_recorder_tv_stop_record);
		play = (TextView) findViewById(R.id.act_audio_recorder_tv_play);
		save = (TextView) findViewById(R.id.act_audio_recorder_tv_save_file);
		delete = (TextView) findViewById(R.id.act_audio_recorder_tv_delete);
	}

	private void setListeners()
	{
		back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (currentState)
				{
				case INITIAL:
					setResult(RESULT_CANCELED);
					deleteFile();
					finish();
					break;
				case PLAYING:
					break;
				case RECORDING:
					break;
				case DONE:
					deleteFile();
					resetTimeAndDuration();
					setUiState(UiState.INITIAL);
					break;
				}
			}
		});
		ok.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (currentState)
				{
				case INITIAL:
					break;
				case PLAYING:
					stopPlaying();
					stopUpdateTime();
					updateDuration(audioDuration);
					setUiState(UiState.DONE);
					break;
				case RECORDING:
					break;
				case DONE:
					Intent data = new Intent();
					data.putExtra(EXTRA_KEY_FILEPATH, outputFile);
					data.putExtra(EXTRA_KEY_DURATION_MILLISECOND, audioDuration);
					Log.d(TAG, "OK - filePath: " + outputFile);
					Log.d(TAG, "OK - duration: " + audioDuration);
					setResult(RESULT_OK, data);
					finish();
					break;
				}
			}
		});
		record.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.INITIAL)
				{
					startRecording();
					setUiState(UiState.RECORDING);
					startUpdateTime();
				}
			}
		});
		open.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.INITIAL)
				{
					openFile();
				}
			}
		});
		resume.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.PLAYING)
				{
					resume.setVisibility(View.GONE);
					resumePlaying();
					startUpdateTime();
					pause.setVisibility(View.VISIBLE);
				}
			}
		});
		pause.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.PLAYING)
				{
					pause.setVisibility(View.GONE);
					pausePlaying();
					stopUpdateTime();
					resume.setVisibility(View.VISIBLE);
				}
			}
		});
		stopRecording.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.RECORDING)
				{
					stopRecording();
					stopUpdateTime();
					audioDuration = recordingTime * 1000;
					updateDuration(audioDuration);
					setUiState(UiState.DONE);
				}
			}
		});
		play.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.DONE)
				{
					setUiState(UiState.PLAYING);
					startPlaying();
					startUpdateTime();
				}
			}
		});
		save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.DONE)
				{
					saveFile();
				}
			}
		});
		delete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (currentState == UiState.DONE)
				{
					deleteFile();
					deleteLastSavedFile();
					resetTimeAndDuration();
					setUiState(UiState.INITIAL);
				}
			}
		});
	}

	private void setUiState(UiState state)
	{
		currentState = state;
		Log.d(TAG, "UI State: " + state);
		switch (state)
		{
		case INITIAL:
			initialBar.setVisibility(View.VISIBLE);
			playingBar.setVisibility(View.GONE);
			recordingBar.setVisibility(View.GONE);
			doneBar.setVisibility(View.GONE);

			back.setVisibility(View.VISIBLE);
			ok.setVisibility(View.INVISIBLE);
			recordingSign.setVisibility(View.INVISIBLE);

			currentTime.setVisibility(View.VISIBLE);
			currentTime.setText("0:00");
			currentTime.setTextColor(getResources().getColor(R.color.audio_recorder_initial_time));
			timeSeparator.setVisibility(View.VISIBLE);
			timeSeparator.setTextColor(getResources().getColor(
					R.color.audio_recorder_duration_max_color));
			duration.setVisibility(View.VISIBLE);
			duration.setText("3:00");
			duration.setTextColor(getResources()
					.getColor(R.color.audio_recorder_duration_max_color));
			break;
		case PLAYING:
			playingBar.setVisibility(View.VISIBLE);
			initialBar.setVisibility(View.GONE);
			recordingBar.setVisibility(View.GONE);
			doneBar.setVisibility(View.GONE);

			back.setVisibility(View.INVISIBLE);
			ok.setVisibility(View.VISIBLE);
			recordingSign.setVisibility(View.INVISIBLE);

			currentTime.setVisibility(View.VISIBLE);
			currentTime.setText("0:00");
			currentTime.setTextColor(getResources().getColor(R.color.audio_recorder_recoding_time));
			timeSeparator.setVisibility(View.VISIBLE);
			timeSeparator.setTextColor(getResources().getColor(
					R.color.audio_recorder_duration_max_color));
			duration.setVisibility(View.VISIBLE);
			duration.setTextColor(getResources().getColor(R.color.audio_recorder_done_time));
			
			resume.setVisibility(View.GONE);
			pause.setVisibility(View.VISIBLE);
			break;
		case RECORDING:
			recordingBar.setVisibility(View.VISIBLE);
			initialBar.setVisibility(View.GONE);
			playingBar.setVisibility(View.GONE);
			doneBar.setVisibility(View.GONE);

			back.setVisibility(View.INVISIBLE);
			ok.setVisibility(View.INVISIBLE);
			recordingSign.setVisibility(View.VISIBLE);

			currentTime.setVisibility(View.VISIBLE);
			currentTime.setTextColor(getResources().getColor(R.color.audio_recorder_recoding_time));
			timeSeparator.setVisibility(View.VISIBLE);
			timeSeparator.setTextColor(getResources().getColor(
					R.color.audio_recorder_duration_max_color));
			duration.setVisibility(View.VISIBLE);
			duration.setText("3:00");
			duration.setTextColor(getResources()
					.getColor(R.color.audio_recorder_duration_max_color));
			break;
		case DONE:
			doneBar.setVisibility(View.VISIBLE);
			initialBar.setVisibility(View.GONE);
			playingBar.setVisibility(View.GONE);
			recordingBar.setVisibility(View.GONE);

			back.setVisibility(View.VISIBLE);
			ok.setVisibility(View.VISIBLE);
			recordingSign.setVisibility(View.INVISIBLE);

			currentTime.setVisibility(View.GONE);
			timeSeparator.setVisibility(View.GONE);
			duration.setVisibility(View.VISIBLE);
			duration.setTextColor(getResources().getColor(R.color.audio_recorder_done_time));
			break;
		}
	}

	private void openFile()
	{
		File lastSavedFile = new File(lastSavedFileFolder, LAST_SAVED_FILE_NAME);
		if (lastSavedFile.exists())
		{
			progressDialog = ProgressDialog.show(this, getString(R.string.processing),
					getString(R.string.please_wait), true, false);
			deleteFile();
			if (FileUtility.copyFile(lastSavedFile, new File(outputFile)))
			{
				setUiState(UiState.PLAYING);
				startPlaying();
				startUpdateTime();
			}
			else
			{
				Log.e(TAG, "openFile - file copy failed");
			}
			progressDialog.dismiss();
		}
		else
		{
			Toast.makeText(this, "Found no audio file", Toast.LENGTH_SHORT).show();
		}
	}

	private void saveFile()
	{
		File fileToSave = new File(outputFile);
		File lastSavedFile = new File(lastSavedFileFolder, LAST_SAVED_FILE_NAME);
		if (fileToSave.exists())
		{
			progressDialog = ProgressDialog.show(this, getString(R.string.processing),
					getString(R.string.please_wait), true, false);
			deleteLastSavedFile();
			if (FileUtility.copyFile(fileToSave, lastSavedFile))
			{
				Toast.makeText(this, "File is saved", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Log.e(TAG, "saveFile - file copy failed");
			}
			progressDialog.dismiss();
		}
		else
		{
			Log.e(TAG, "saveFile - nothing to save");
		}
	}

	private void deleteFile()
	{
		File file = new File(outputFile);
		if (file.exists())
		{
			if (file.delete())
			{
				Log.d(TAG, "deleteFile - file deleted");
			}
			else
			{
				Log.w(TAG, "deleteFile - file not deleted");
			}
		}
	}

	private void deleteLastSavedFile()
	{
		File lastSavedFile = new File(lastSavedFileFolder, LAST_SAVED_FILE_NAME);
		if (lastSavedFile.exists())
		{
			if (lastSavedFile.delete())
			{
				Log.d(TAG, "deleteLastFile - lastFile deleted");
			}
			else
			{
				Log.w(TAG, "deleteLastFile - lastFile not deleted");
			}
		}
	}

	private void startPlaying()
	{
		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(outputFile);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener()
			{
				@Override
				public void onPrepared(MediaPlayer mp)
				{
					mp.start();
					audioDuration = mp.getDuration();
					updateDuration(audioDuration);
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener()
			{

				@Override
				public void onCompletion(MediaPlayer mp)
				{
					setUiState(UiState.DONE);
					stopUpdateTime();
				}

			});
			mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
			{

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra)
				{
					Log.e(TAG, "media player error! " + what + ", " + extra);
					quitByError();
					return false;
				}
			});
		}
		catch (IOException e)
		{
			Log.e(TAG, "prepare() failed");
			quitByError();
		}
	}

	private void pausePlaying()
	{
		mediaPlayer.pause();
	}

	private void resumePlaying()
	{
		mediaPlayer.start();
	}

	private void stopPlaying()
	{
		if (mediaPlayer != null)
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	private void startRecording()
	{
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setOutputFile(outputFile);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
		recorder.setAudioEncodingBitRate(12200);
		recorder.setAudioSamplingRate(8000);
		recorder.setMaxDuration(MAX_DURATION_MILLISECONDS);
		recorder.setOnInfoListener(new OnInfoListener()
		{

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra)
			{
				switch (what)
				{
				case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
					stopRecording();
					stopUpdateTime();
					audioDuration = MAX_DURATION_MILLISECONDS;
					updateDuration(audioDuration);
					setUiState(UiState.DONE);
					break;
				}
			}

		});
		recorder.setOnErrorListener(new MediaRecorder.OnErrorListener()
		{

			@Override
			public void onError(MediaRecorder mr, int what, int extra)
			{
				Log.e(TAG, "media recorder error! " + what + ", " + extra);
				quitByError();
			}
		});

		try
		{
			recorder.prepare();
		}
		catch (IOException e)
		{
			Log.e(TAG, "prepare() failed");
		}

		recorder.start();
	}

	private void stopRecording()
	{
		if (recorder != null)
		{
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}

	private Runnable updateTimeAction = new Runnable()
	{

		@Override
		public void run()
		{
			if (currentState == UiState.RECORDING)
			{
				recordingTime++;
				int min = recordingTime / 60;
				int sec = recordingTime % 60;
				currentTime.setText(min + ":" + String.format("%02d", sec));
				uiHandler.postDelayed(updateTimeAction, 1000);
			}
			else if (currentState == UiState.PLAYING && mediaPlayer != null)
			{
				int ms = mediaPlayer.getCurrentPosition();
				int min = ms / 1000 / 60;
				int sec = ms / 1000 % 60;
				currentTime.setText(min + ":" + String.format("%02d", sec));
				uiHandler.postDelayed(updateTimeAction, 500);
			}
		}
	};

	private void startUpdateTime()
	{
		uiHandler.removeCallbacks(updateTimeAction);
		if (currentState == UiState.RECORDING)
		{
			uiHandler.postDelayed(updateTimeAction, 1000);
		}
		else if (currentState == UiState.PLAYING)
		{
			uiHandler.post(updateTimeAction);
		}
	}

	private void stopUpdateTime()
	{
		uiHandler.removeCallbacks(updateTimeAction);
	}

	private void updateDuration(int ms)
	{
		int min = ms / 1000 / 60;
		int sec = ms / 1000 % 60;
		String text = min + ":" + String.format("%02d", sec);
		duration.setText(text);
	}

	private void resetTimeAndDuration()
	{
		Log.d(TAG, "resetTimeAndDuration");
		audioDuration = 0;
		recordingTime = 0;
	}

	private void quitByError()
	{
		Log.e(TAG, "quit by error");
		deleteFile();
		setResult(RESULT_CANCELED);
		finish();
	}

}

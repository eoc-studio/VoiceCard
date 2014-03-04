package eoc.studio.voicecard.card.viewer;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eoc.studio.voicecard.R;

public class AudioMessageView extends RelativeLayout
{

	private ImageView controllerIcon;
	private TextView timeTextView;
	private String durationText;
	private boolean isPlayable = true;
	private MediaPlayer mediaPlayer;
	private OnTouchListener touchListener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
			{
				boolean isConsumed = false;
				if (isPlayable && isPrepared)
				{
					if (isPlaying())
					{
						pausePlaying();
						stopTimeUpdate();
						isConsumed = true;
					}
					else
					{
						resumePlaying();
						startTimeUpdate();
						isConsumed = true;
					}
				}
				return isConsumed;
			}
			return false;
		}

	};
	private boolean isPlaying = false;
	private boolean isPrepared = false;

	public AudioMessageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initLayout();
		setOnTouchListener(touchListener);
	}

	private void initLayout()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.view_audio_message, this);

		timeTextView = (TextView) findViewById(R.id.glb_audio_message_tv_time);
		controllerIcon = (ImageView) findViewById(R.id.glb_audio_message_iv_control_icon);
	}

	public void setDurationText(String durationText)
	{
		this.durationText = durationText;
		timeTextView.setText(durationText);
	}

	public void setDuration(int milliseconds)
	{
		int min = milliseconds / 1000 / 60;
		int sec = milliseconds / 1000 % 60;
		setDurationText(min + ":" + String.format("%02d", sec));
	}

	public void startTimeUpdate()
	{
		this.post(updateTimeAction);
	}

	public void stopTimeUpdate()
	{
		this.removeCallbacks(updateTimeAction);
	}

	public void setPlayingState(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
		if (isPlaying)
		{
			controllerIcon.setImageResource(R.drawable.icon_pause);
		}
		else
		{
			controllerIcon.setImageResource(R.drawable.icon_play);
		}
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public boolean isPrepared()
	{
		return isPrepared;
	}

	public void setPlayable(boolean playable)
	{
		isPlayable = playable;
	}

	public void setPlayerSourcceAndPrepare(MediaPlayer player, Uri source,
			final boolean playAtBeginning) throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException
	{
		this.mediaPlayer = player;

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(getContext(), source);
		mediaPlayer.prepare();

		mediaPlayer.setOnPreparedListener(new OnPreparedListener()
		{
			@Override
			public void onPrepared(MediaPlayer mp)
			{
				isPrepared = true;

				setDuration(mp.getDuration());

				if (playAtBeginning)
				{
					mp.start();
					setPlayingState(true);
				}
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener()
		{

			@Override
			public void onCompletion(MediaPlayer mp)
			{
				stopTimeUpdate();
				setDurationText(durationText);
				setPlayingState(false);
			}

		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
		{

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra)
			{
				return false;
			}
		});
	}

	private void pausePlaying()
	{
		mediaPlayer.pause();
		setPlayingState(false);
	}

	private void resumePlaying()
	{
		mediaPlayer.start();
		setPlayingState(true);
	}

	public void play()
	{
		if (isPlayable && isPrepared && !isPlaying())
		{
			resumePlaying();
			startTimeUpdate();
		}
	}

	public void pause()
	{
		if (isPlayable && isPrepared && isPlaying())
		{
			pausePlaying();
			stopTimeUpdate();
		}
	}

	private void releasePlayer()
	{
		try
		{
			if (mediaPlayer != null)
			{
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	private Runnable updateTimeAction = new Runnable()
	{

		@Override
		public void run()
		{
			int ms = mediaPlayer.getCurrentPosition();
			int min = ms / 1000 / 60;
			int sec = ms / 1000 % 60;
			timeTextView.setText(min + ":" + String.format("%02d", sec));
			AudioMessageView.this.postDelayed(updateTimeAction, 500);
		}
	};

	@Override
	protected void onDetachedFromWindow()
	{
		stopTimeUpdate();
		releasePlayer();
		super.onDetachedFromWindow();
	}

}

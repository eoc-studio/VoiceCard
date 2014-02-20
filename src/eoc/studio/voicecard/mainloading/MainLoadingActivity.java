package eoc.studio.voicecard.mainloading;



import eoc.studio.voicecard.R;
import eoc.studio.voicecard.progresswheel.ProgressWheel;
import android.app.Activity;
import android.os.Bundle;

public class MainLoadingActivity extends Activity
{
	
	ProgressWheel progressWheel;
	boolean running;
	int progress = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_loading);
		progressWheel = (ProgressWheel) findViewById(R.id.act_main_loading_progresswheel_main);
		
		
        final Runnable r = new Runnable() {
			public void run() {
				running = true;
				while(progress<361) {
					progressWheel.incrementProgress();
					progress++;
//					progressWheel.setText(String.valueOf(progress/360*100) + "%%");
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				running = false;
			}
        };
        
		progress = 0;
		progressWheel.resetCount();
		Thread s = new Thread(r);
		s.start();
        
	}
	
	@Override
	public void onPause() {
		super.onPause();
		progress = 361;
		progressWheel.stopSpinning();
		progressWheel.resetCount();
		progressWheel.setText("");
	}
	
	
}

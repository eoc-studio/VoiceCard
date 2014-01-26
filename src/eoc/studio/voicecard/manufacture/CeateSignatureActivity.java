package eoc.studio.voicecard.manufacture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.handwriting.HandWritingView;
import android.R.integer;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CeateSignatureActivity extends Activity
{
	Context context = null;

	PhotoSortrView photoSorter = null;

	HandWritingView handWritingView = null;

	ImageButton changeModeButton = null;

	ImageButton saveButton = null;

	RelativeLayout sketchpadLayout = null;

	private static final int MODE_WRITING = 1;

	private static final int MODE_DRAG = 2;

	private int uiMode = MODE_WRITING;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_create_signature);
		context = getApplicationContext();
		findViewById(R.id.Seal_01_imageView).setOnTouchListener(new MyTouchListener());
		findViewById(R.id.Seal_01_imageView).setTag(R.drawable.seal01);
		findViewById(R.id.Seal_02_imageView).setOnTouchListener(new MyTouchListener());
		findViewById(R.id.Seal_02_imageView).setTag(R.drawable.seal02);
		findViewById(R.id.sketchpad_RelativeLayout).setOnDragListener(new SketchpadDragListener());

		changeModeButton = (ImageButton) findViewById(R.id.changeWriteOrDragButton);
		changeModeButton.setOnClickListener(new ChangeModeClickListenr());
		changeModeButton.setBackgroundResource(R.drawable.draw_mode);

		saveButton = (ImageButton) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new SaveButtonClickListenr());
		saveButton.setBackgroundResource(R.drawable.save_button);

		handWritingView = new HandWritingView(this);

		photoSorter = new PhotoSortrView(this);

		sketchpadLayout = (RelativeLayout) findViewById(R.id.sketchpad_RelativeLayout);
		sketchpadLayout.addView(photoSorter);
		sketchpadLayout.addView(handWritingView);
	}

	class MyTouchListener implements OnTouchListener
	{
		public boolean onTouch(View view, MotionEvent motionEvent)
		{

			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
			{
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				view.startDrag(data, shadowBuilder, view, 0);
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	class ChangeModeClickListenr implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{

			if ((uiMode & MODE_WRITING) == 1)
			{
				Log.e("CeateSignatureActivity", "MODE_WRITING");

				sketchpadLayout.removeAllViews();
				sketchpadLayout.addView(handWritingView);
				sketchpadLayout.addView(photoSorter);

				uiMode = MODE_DRAG;
				changeModeButton.setBackgroundResource(R.drawable.drag_mode);
				Log.e("CeateSignatureActivity", "change to MODE_DRAG");
			}
			else
			{
				Log.e("CeateSignatureActivity", "MODE_DRAG");
				sketchpadLayout.removeAllViews();
				sketchpadLayout.addView(photoSorter);
				sketchpadLayout.addView(handWritingView);
				uiMode = MODE_WRITING;
				changeModeButton.setBackgroundResource(R.drawable.draw_mode);
				Log.e("CeateSignatureActivity", "change to MODE_WRITING");
			}

		}

	}

	class SaveButtonClickListenr implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{

			Log.e("CeateSignatureActivity", "SaveButtonClickListenr click!!");
			// Without it the view will have a dimension of 0,0 and the bitmap
			// will be null
			sketchpadLayout.setDrawingCacheEnabled(true);

			Bitmap screenshot = Bitmap.createBitmap(sketchpadLayout.getWidth(),
					sketchpadLayout.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(screenshot);
			sketchpadLayout.draw(c);
			// clear drawing cache
			sketchpadLayout.setDrawingCacheEnabled(false);

			String root = Environment.getExternalStorageDirectory().toString();
			File myDir = new File(root + "/VoiceCard_images");
			myDir.mkdirs();
			Random generator = new Random();
			int n = 10000;
			n = generator.nextInt(n);
			String fname = "Image-" + n + ".jpg";
			File file = new File(myDir, fname);
			if (file.exists()) file.delete();
			try
			{
				FileOutputStream out = new FileOutputStream(file);
				screenshot.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();

				Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

				mediaScannerIntent
						.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()
								+ "/VoiceCard_images/" + fname));
				context.sendBroadcast(mediaScannerIntent);

				Toast.makeText(context, "Your image is saved to sdcard", Toast.LENGTH_LONG).show();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}

	class SketchpadDragListener implements OnDragListener
	{

		@Override
		public boolean onDrag(View v, DragEvent event)
		{

			int action = event.getAction();
			switch (event.getAction())
			{
			case DragEvent.ACTION_DRAG_STARTED:

				Log.e("CeateSignatureActivity", "ACTION_DRAG_STARTED v.getRelativeTop()"
						+ getRelativeTop(v));
				Log.e("CeateSignatureActivity", "ACTION_DRAG_STARTED v.getRelativeLeft()"
						+ getRelativeLeft(v));

				Log.e("CeateSignatureActivity", "ACTION_DRAG_STARTED event.getX()" + event.getX());
				Log.e("CeateSignatureActivity", "ACTION_DRAG_STARTED event.getY()" + event.getY());

				// do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				break;
			case DragEvent.ACTION_DROP:

				Log.e("CeateSignatureActivity", "ACTION_DROP v.getRelativeTop()"
						+ getRelativeTop(v));
				Log.e("CeateSignatureActivity", "ACTION_DROP v.getRelativeLeft()"
						+ getRelativeLeft(v));

				Log.e("CeateSignatureActivity", "ACTION_DROP event.getX()" + event.getX());
				Log.e("CeateSignatureActivity", "ACTION_DROP event.getY()" + event.getY());

				Log.e("CeateSignatureActivity", "ACTION_DROP v.getId() " + v.getId());

				View view = (View) event.getLocalState();
				Log.e("CeateSignatureActivity", "ACTION_DROP v.getTag() " + view.getTag());

				photoSorter.loadOneImgage(context, (Integer) view.getTag(), event.getX(),
						event.getY(), 1, 1);
				photoSorter.invalidate();

				break;
			case DragEvent.ACTION_DRAG_ENDED:
			default:
				break;
			}
			return true;
		}
	}

	private int getRelativeLeft(View myView)
	{

		if (myView.getParent() == myView.getRootView())
			return myView.getLeft();
		else return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}

	private int getRelativeTop(View myView)
	{

		if (myView.getParent() == myView.getRootView())
			return myView.getTop();
		else return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
}

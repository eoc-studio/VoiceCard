package eoc.studio.voicecard.manufacture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.colorpickerview.dialog.ColorPickerDialog;
import eoc.studio.voicecard.handwriting.HandWritingView;
import eoc.studio.voicecard.utils.DragUtility;
import eoc.studio.voicecard.utils.FileUtility;
import eoc.studio.voicecard.utils.PaintUtility;
import eoc.studio.voicecard.utils.PaintUtility.PEN_SIZE_ENUM;

public class EditSignatureActivity extends Activity
{
	private final static String TAG = "EditSignatureActivity";

	Boolean isDebug = true;

	Context context = null;

	HandWritingView handWritingView = null;

	StampSortView stampSorterView = null;

	ImageButton changeModeButton = null;

	RelativeLayout sketchpadLayout = null;

	RadioGroup paintSizeRadioGroup = null;

	ToggleButton eraserToggleButton = null;

	ImageView chooseColorImageView = null;

	ImageView trashImageView = null;

	GridView gridStampView = null;

	StampAdapter stampAdapter = null;

	private ArrayList<StampItem> gridStampArray = null;

	private static final int MODE_WRITING = 1;

	private static final int MODE_DRAG = 2;

	private int uiMode = MODE_WRITING;

	private static final float TARGET_WIDTH_DP = 45;

	private static final float TARGET_HEIGHT_DP = 45;

	private static final String DRAFT_FOLDER_NAME = "VoiceCard_images";

	private static final String DRAFT_IMAGE_NAME = "signatureDraft.jpg";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_signature);
		context = getApplicationContext();

		initializeAllView();

	}

	@Override
	protected void onResume()
	{

		super.onResume();
		Log.d(TAG, "CeateSignatureActivity: onResume()");

		try
		{
			stampSorterView.loadImages(context);
			loadHandWrtingViewFromFile();
			
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		
		handWritingView.setPenColor(Color.BLACK);

	}

	public void loadHandWrtingViewFromFile()
	{

		String root = Environment.getExternalStorageDirectory().toString();
		File tempDir = new File(root + "/" + DRAFT_FOLDER_NAME);

		File imagefile = new File(tempDir, DRAFT_IMAGE_NAME);
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(imagefile);
			Bitmap draftBitmap = BitmapFactory.decodeStream(fis)
					.copy(Bitmap.Config.ARGB_8888, true);
			if (draftBitmap != null)
			{
				handWritingView.new1Bitmap = FileUtility.clearBitmapBackgroudColor(draftBitmap,
						Color.GRAY, Color.TRANSPARENT);
				handWritingView.new1Bitmap.setHasAlpha(true);

				handWritingView.invalidate();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			Log.d(TAG, "EditSignatureActivity: onResume() FileNotFoundException");
		}
		catch (Exception e)
		{
			Log.d(TAG, "EditSignatureActivity: onResume() Exception");
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause()
	{

		super.onPause();
		Log.d(TAG, "EditSignatureActivity: onPause()");
		String root = Environment.getExternalStorageDirectory().toString();
		File tempDir = new File(root + "/" + DRAFT_FOLDER_NAME);

		FileUtility.saveLayoutToFile(context, (View) handWritingView, tempDir.toString(),
				DRAFT_IMAGE_NAME);
		/*
		 * tempDraftBitmap =
		 * handWritingView.new1Bitmap.copy(handWritingView.new1Bitmap
		 * .getConfig(), true);
		 */

		// try to save seal info to gson
		stampSorterView.saveImageInfoToGson();

	}

	public void initializeAllView()
	{

		initSketchLayout();
		initTrashImageView();
		initChangeModeButton();
		initGridStampView();
		initPaintSizeButton();
		initEraserToggleButton();
		initChooseColorImageView();
	}

	public void initSketchLayout()
	{

		handWritingView = new HandWritingView(this);
		stampSorterView = new StampSortView(this);

		sketchpadLayout = (RelativeLayout) findViewById(R.id.act_edit_signature_rlyt_sketchpad_painting_area_with_backgroud);
		sketchpadLayout.setOnDragListener(new SketchpadDragListener());

		sketchpadLayout.addView(stampSorterView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		sketchpadLayout.addView(handWritingView);

		// stampSorterView.setBackgroundColor(R.color.sketchpad_color);
	}

	public void initTrashImageView()
	{

		trashImageView = (ImageView) findViewById(R.id.act_edit_signature_iv_trash);
		trashImageView.setOnDragListener(new TrashDragListener());
	}

	public void initChangeModeButton()
	{

		changeModeButton = (ImageButton) findViewById(R.id.act_edit_signature_ib_mode);
		changeModeButton.setOnClickListener(new ChangeModeClickListenr());
		changeModeButton.setBackgroundResource(R.drawable.draw_mode);
	}

	public void initGridStampView()
	{

		gridStampView = (GridView) findViewById(R.id.act_edit_signature_gv_stamps);

		gridStampArray = new ArrayList<StampItem>();
		gridStampArray.add(new StampItem(R.drawable.stamp_01, "Heart"));
		gridStampArray.add(new StampItem(R.drawable.stamp_02, "Cake"));
		gridStampArray.add(new StampItem(R.drawable.stamp_03, "Slipper"));
		gridStampArray.add(new StampItem(R.drawable.stamp_04, "Compass"));
		gridStampArray.add(new StampItem(R.drawable.stamp_05, "BasketBall"));
		gridStampArray.add(new StampItem(R.drawable.stamp_01, "Heart"));
		gridStampArray.add(new StampItem(R.drawable.stamp_02, "Cake"));
		gridStampArray.add(new StampItem(R.drawable.stamp_03, "Slipper"));
		gridStampArray.add(new StampItem(R.drawable.stamp_04, "Compass"));
		gridStampArray.add(new StampItem(R.drawable.stamp_05, "BasketBall"));
		gridStampArray.add(new StampItem(R.drawable.stamp_01, "Heart"));
		gridStampArray.add(new StampItem(R.drawable.stamp_02, "Cake"));

		StampAdapter stampAdapter = new StampAdapter(this, R.layout.view_stamp_item, gridStampArray);
		gridStampView.setAdapter(stampAdapter);
	}

	class TrashDragListener implements OnDragListener
	{

		@Override
		public boolean onDrag(View v, DragEvent event)
		{

			int action = event.getAction();
			switch (event.getAction())
			{
			case DragEvent.ACTION_DRAG_STARTED:
				Log.e(TAG, "ACTION_DRAG_STARTED");
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				Log.e(TAG, "ACTION_DRAG_ENTERED");
				trashImageView.setBackgroundResource(R.drawable.trash_here_effect);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				trashImageView.setBackgroundResource(R.drawable.trash);
				Log.e(TAG, "ACTION_DRAG_EXITED");
				stampSorterView.cancelStamp();
				break;
			case DragEvent.ACTION_DROP:
				trashImageView.setBackgroundResource(R.drawable.trash);
				ClipData dropClipDate = event.getClipData();
				if (dropClipDate.getItemAt(0).getText().equals(DragUtility.LABLE_TO_TRASH))
				{
					Log.e(TAG, "ACTION_DROP ===>Try to trash");
					Log.e(TAG, "ACTION_DROP event.getX()" + event.getX());
					Log.e(TAG, "ACTION_DROP event.getY()" + event.getY());
					stampSorterView.removeStamp();
				}
				break;

			case DragEvent.ACTION_DRAG_ENDED:
				break;
			default:
				break;
			}
			return true;
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
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				break;
			case DragEvent.ACTION_DROP:

				ClipData clipDate = event.getClipData();
				boolean isFromStamp = clipDate.getItemAt(0).getText()
						.equals(DragUtility.LABLE_STAMP);

				if (isFromStamp)
				{
					Log.e(TAG, "ACTION_DROP event.getX()" + event.getX());
					Log.e(TAG, "ACTION_DROP event.getY()" + event.getY());
					Log.e(TAG, "ACTION_DROP v.getId() " + v.getId());

					View view = (View) event.getLocalState();
					int drawbleId = (Integer) view.getTag();

					Drawable drawable = context.getResources().getDrawable(drawbleId);
					stampSorterView.loadOneImgage(context, (Integer) view.getTag(), event.getX(),
							event.getY(), calculateScaleX(drawable), calculateScaleY(drawable));
					stampSorterView.invalidate();
				}

				break;
			case DragEvent.ACTION_DRAG_ENDED:
			default:
				break;
			}
			return true;
		}

		public float calculateScaleY(Drawable drawable)
		{

			float scaleY = DragUtility.convertDpToPixel(TARGET_HEIGHT_DP, context)
					/ (float) drawable.getIntrinsicHeight();
			return scaleY;
		}

		public float calculateScaleX(Drawable drawable)
		{

			float scaleX = DragUtility.convertDpToPixel(TARGET_WIDTH_DP, context)
					/ (float) drawable.getIntrinsicWidth();
			return scaleX;
		}
	}

	class ChangeModeClickListenr implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{

			sketchpadLayout.removeAllViews();

			if ((uiMode & MODE_WRITING) == 1)
			{
				Log.e(TAG, "MODE_WRITING");

				// turn off hand writing function
				handWritingView.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{

						return true;
					}
				});

				addViewByOrder(handWritingView, stampSorterView);

				uiMode = MODE_DRAG;
				changeModeButton.setBackgroundResource(R.drawable.drag_mode);
				Log.e(TAG, "change to MODE_DRAG");
			}
			else
			{
				Log.e(TAG, "MODE_DRAG");

				// turn on hand writing function
				handWritingView.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{

						return false;
					}
				});
				addViewByOrder(stampSorterView, handWritingView);
				uiMode = MODE_WRITING;
				changeModeButton.setBackgroundResource(R.drawable.draw_mode);
				Log.e(TAG, "change to MODE_WRITING");
			}

		}

		private void addViewByOrder(View view1, View view2)
		{

			sketchpadLayout.addView(view1);
			sketchpadLayout.addView(view2);
		}

	}

	public void initChooseColorImageView()
	{

		chooseColorImageView = (ImageView) findViewById(R.id.act_edit_signature_iv_choose_color);
		chooseColorImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				Toast.makeText(EditSignatureActivity.this,
						"The favorite list would appear on clicking this icon", Toast.LENGTH_LONG)
						.show();
				colorPicker();
			}
		});
	}

	public void colorPicker()
	{

		ColorDrawable drawable = (ColorDrawable) chooseColorImageView.getBackground();

		int initialValue = drawable.getColor();

		Log.d("mColorPicker", "initial value:" + initialValue);

		final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);

		colorDialog.setAlphaSliderVisible(false);
		colorDialog.setTitle("Pick a Color!");

		colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						Toast.makeText(EditSignatureActivity.this,
								"Selected Color: " + colorToHexString(colorDialog.getColor()),
								Toast.LENGTH_LONG).show();
						chooseColorImageView.setBackgroundColor(colorDialog.getColor());
						handWritingView.setPenColor(colorDialog.getColor());
					}
				});

		colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						// Nothing to do here.
					}
				});

		colorDialog.show();
	}

	private String colorToHexString(int color)
	{

		return String.format("#%06X", 0xFFFFFFFF & color);
	}

	public void initEraserToggleButton()
	{

		eraserToggleButton = (ToggleButton) findViewById(R.id.act_edit_signature_tb_eraser);

		eraserToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked)
			{

				if (isChecked)
				{
					handWritingView.enableEraser();
				}
				else
				{
					handWritingView.disableEraser();
				}

				if (isDebug)
				{
					Toast.makeText(EditSignatureActivity.this, String.valueOf(isChecked),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void initPaintSizeButton()
	{

		paintSizeRadioGroup = (RadioGroup) findViewById(R.id.act_edit_signature_rgrp_paint_size);
		OnClickListener listener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				RadioButton rb = (RadioButton) v;

				PEN_SIZE_ENUM currentPenSize = PEN_SIZE_ENUM.valueOf(rb.getText().toString());
				switch (currentPenSize)
				{
				case SIZE_ONE:

					handWritingView.setstyle(PaintUtility.PEN_SIZE.SIZE_1);
					break;
				case SIZE_TWO:

					handWritingView.setstyle(PaintUtility.PEN_SIZE.SIZE_2);
					break;
				case SIZE_THREE:

					handWritingView.setstyle(PaintUtility.PEN_SIZE.SIZE_3);
					break;
				}

				if (isDebug)
				{
					Toast.makeText(EditSignatureActivity.this, rb.getText(), Toast.LENGTH_SHORT)
							.show();
				}

			}
		};

		RadioButton rbSizeOne = (RadioButton) findViewById(R.id.act_edit_signature_rb_paint_size_one);
		rbSizeOne.setOnClickListener(listener);

		RadioButton rbSizeTwo = (RadioButton) findViewById(R.id.act_edit_signature_rb_paint_size_two);
		rbSizeTwo.setOnClickListener(listener);

		RadioButton rbSizeThree = (RadioButton) findViewById(R.id.act_edit_signature_rb_paint_size_three);
		rbSizeThree.setOnClickListener(listener);
	}

	/*
	 * private int getRelativeLeft(View myView) {
	 * 
	 * if (myView.getParent() == myView.getRootView()) return myView.getLeft();
	 * else return myView.getLeft() + getRelativeLeft((View)
	 * myView.getParent()); }
	 * 
	 * private int getRelativeTop(View myView) {
	 * 
	 * if (myView.getParent() == myView.getRootView()) return myView.getTop();
	 * else return myView.getTop() + getRelativeTop((View) myView.getParent());
	 * }
	 */
}

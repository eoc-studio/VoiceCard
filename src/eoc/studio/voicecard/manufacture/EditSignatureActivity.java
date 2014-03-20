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
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
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

	private Boolean isDebug = true; 

	private Context context = null;

	private HandWritingView handWritingView = null;

	private StampSortView stampSorterView = null;

	private ImageButton changeModeButton = null;

	private RelativeLayout sketchpadLayout = null;

	private RadioGroup paintSizeRadioGroup = null;

	private ImageButton eraserToggleButton = null;

	private ImageView chooseColorImageView = null;

	private ImageView trashImageView = null;

	private ImageView arrowLeftImageView = null;

	private ImageView arrowRightImageView = null;

	private GridView gridStampView = null;

	private StampAdapter stampAdapter = null;

	private ImageButton returnImageButton;

	private ImageButton okButtonImageButton;

	private ArrayList<StampItem> gridStampArray = null;

	private ArrayList<StampItem> gridStampArrayIndex1 = null;

	private ArrayList<StampItem> gridStampArrayIndex2 = null;

	private ArrayList<StampItem> gridStampArrayIndex3 = null;

	private StampAdapter stampAdapter1 = null;

	private StampAdapter stampAdapter2 = null;

	private StampAdapter stampAdapter3 = null;

	private static final int MODE_WRITING = 1;

	private static final int MODE_DRAG = 2;

	private int uiMode = MODE_WRITING;

	private static final float TARGET_WIDTH_DP = 45;

	private static final float TARGET_HEIGHT_DP = 45;

	private static final String DRAFT_FOLDER_NAME = "VoiceCard_images";

	private static final String DRAFT_IMAGE_NAME = "signatureHandwritingDraft.jpg";

	private static final String DRAFT_COMPLETED_IMAGE_NAME = "signatureDraft.jpg";

	private static final String EXTRA_KEY_USER_SIGN_HANDWRITHING = "user_sign_handwriting";

	private static final String EXTRA_KEY_USER_SIGN_POSITION_INFO = "user_sign_position_info";

	private static final String EXTRA_KEY_USER_SIGN_DRAFT_IMAGE = "user_sign_draft_image";

	private static int stampGridviewIndex = 1;

	private Uri signPositonDraftUri;

	private Uri signHandWritingDraftUri;

	private Uri signCompletedDraftUri;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_signature);
		context = getApplicationContext();

		initializeAllView();
		getConfigFromIntent();
	}

	private void getConfigFromIntent()
	{

		Intent intent = getIntent();
		signHandWritingDraftUri = intent.getParcelableExtra(EXTRA_KEY_USER_SIGN_HANDWRITHING);
		signPositonDraftUri = intent.getParcelableExtra(EXTRA_KEY_USER_SIGN_POSITION_INFO);
		signCompletedDraftUri = intent.getParcelableExtra(EXTRA_KEY_USER_SIGN_DRAFT_IMAGE);

		Log.d(TAG, "getConfigFromIntent signHandWritingDraftUri:" + signHandWritingDraftUri);
		Log.d(TAG, "getConfigFromIntent signPositonDraftUri:" + signPositonDraftUri);
		Log.d(TAG, "getConfigFromIntent signCompletedDraftUri:" + signCompletedDraftUri);
	}

	@Override
	protected void onResume()
	{

		super.onResume();
		Log.d(TAG, "CeateSignatureActivity: onResume()");

		try
		{
			if (signPositonDraftUri != null)
			{
				stampSorterView.loadImages(context, signPositonDraftUri);
			}

			if (signHandWritingDraftUri != null)
			{
				loadHandWrtingViewFromFile(signHandWritingDraftUri);
			}

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		handWritingView.setPenColor(Color.BLACK);

	}

	public void loadHandWrtingViewFromFile(Uri loadUri)
	{

		Log.d(TAG, "EditSignatureActivity: onResume() loadUri: " + loadUri);
		// String root = Environment.getExternalStorageDirectory().toString();
		// File tempDir = new File(root + "/" + DRAFT_FOLDER_NAME);
		// File imagefile = new File(tempDir, DRAFT_IMAGE_NAME);
		File imagefile = new File(loadUri.getPath());
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

	}

	public void initializeAllView()
	{

		initSketchLayout();
		initTrashImageView();
		initChangeModeButton();
		initArrowVIews();
		initGridStampView();
		initPaintSizeButton();
		initEraserToggleButton();
		initChooseColorImageView();
		InitReturnButton();
		InitOkButton();
	}

	private void initArrowVIews()
	{

		stampGridviewIndex = 1;
		arrowLeftImageView = (ImageView) findViewById(R.id.act_edit_signature_iv_arrow_left);
		arrowRightImageView = (ImageView) findViewById(R.id.act_edit_signature_iv_arrow_right);

		arrowLeftImageView.setVisibility(View.INVISIBLE);
		arrowRightImageView.setVisibility(View.VISIBLE);

		arrowLeftImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (stampGridviewIndex != 1) stampGridviewIndex--;

				updateArrowView();

			}
		});

		arrowRightImageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (stampGridviewIndex != 3) stampGridviewIndex++;

				updateArrowView();
			}
		});

	}

	private void updateArrowView()
	{

		if (stampGridviewIndex == 1)
		{
			arrowLeftImageView.setVisibility(View.INVISIBLE);
			arrowRightImageView.setVisibility(View.VISIBLE);
			gridStampView.setAdapter(stampAdapter1);
			stampAdapter1.notifyDataSetChanged();

		}
		else if (stampGridviewIndex == 2)
		{
			arrowLeftImageView.setVisibility(View.VISIBLE);
			arrowRightImageView.setVisibility(View.VISIBLE);
			gridStampView.setAdapter(stampAdapter2);
			stampAdapter2.notifyDataSetChanged();
		}
		else if (stampGridviewIndex == 3)
		{
			arrowLeftImageView.setVisibility(View.VISIBLE);
			arrowRightImageView.setVisibility(View.INVISIBLE);
			gridStampView.setAdapter(stampAdapter3);
			stampAdapter2.notifyDataSetChanged();
		}

		gridStampView.invalidate();
	}

	public void InitOkButton()
	{

		okButtonImageButton = (ImageButton) findViewById(R.id.act_edit_signature_ib_button_ok);

		okButtonImageButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				Log.d(TAG, "okButtonImageButton getCacheDir().getPath():" + getFilesDir().getPath());

				String handWritingFileName = FileUtility.getRandomSignHandWritingName("jpg");

				String root = Environment.getExternalStorageDirectory().toString();
				File tempDir = new File(getCacheDir().getPath() + "/", handWritingFileName);
				FileUtility.saveLayoutToFile(context, (View) handWritingView, getCacheDir()
						.getPath(), handWritingFileName);
				signHandWritingDraftUri = Uri.fromFile(tempDir);

				// try to save seal info to gson
				signPositonDraftUri = stampSorterView.saveImageInfoToGson(getCacheDir().getPath(),
						FileUtility.getRandomSignPositionName("json"));

				sketchpadLayout.removeAllViews();
				addViewByOrder(handWritingView, stampSorterView);

				String handCompletedFileName = FileUtility.getRandomSignCompletedName("jpg");
				File tempDir2 = new File(getCacheDir().getPath() + "/", handCompletedFileName);
				// FileUtility.saveLayoutToFileWithoutScan((View)sketchpadLayout,tempDir2.getParent(),DRAFT_COMPLETED_IMAGE_NAME);
				FileUtility.saveTwoLayoutToFile(handWritingView, stampSorterView,
						tempDir2.getParent(), handCompletedFileName);
				signCompletedDraftUri = Uri.fromFile(tempDir2);

				Intent intent = new Intent();
				Log.d(TAG, "send signHandWritingDraftUri:" + signHandWritingDraftUri);
				Log.d(TAG, "send signPositonDraftUri:" + signPositonDraftUri);
				Log.d(TAG, "send signCompletedDraftUri:" + signCompletedDraftUri);
				intent.putExtra(EXTRA_KEY_USER_SIGN_HANDWRITHING, signHandWritingDraftUri);
				intent.putExtra(EXTRA_KEY_USER_SIGN_POSITION_INFO, signPositonDraftUri);
				intent.putExtra(EXTRA_KEY_USER_SIGN_DRAFT_IMAGE, signCompletedDraftUri);

				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	public void InitReturnButton()
	{

		returnImageButton = (ImageButton) findViewById(R.id.act_edit_signature_ib_button_return);
		returnImageButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

	public void initSketchLayout()
	{



		stampSorterView = new StampSortView(this);

		sketchpadLayout = (RelativeLayout) findViewById(R.id.act_edit_signature_rlyt_sketchpad_painting_area_with_backgroud);
		sketchpadLayout.setOnDragListener(new SketchpadDragListener());

		sketchpadLayout.addView(stampSorterView, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
/*		android:layout_width="297dp"
		android:layout_height="234dp"*/
		PaintUtility.SKETTCHPAD.setHEIGHT((int)DragUtility.convertDpToPixel(234, context));
		PaintUtility.SKETTCHPAD.setWIDTH((int)DragUtility.convertDpToPixel(297, context)); 
		handWritingView = new HandWritingView(this);
		handWritingView.disableEraser(); // turn off Eraser function
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
		changeModeButton.setBackgroundResource(R.drawable.icon_editor_draw);
	}

	public void initGridStampView()
	{

		gridStampView = (GridView) findViewById(R.id.act_edit_signature_gv_stamps);

		// gridStampArray = new ArrayList<StampItem>();
		// gridStampArray.add(new StampItem(R.drawable.stamp_01, "Heart"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_02, "Cake"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_03, "Slipper"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_04, "Compass"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_05, "BasketBall"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_01, "Heart"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_02, "Cake"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_03, "Slipper"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_04, "Compass"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_05, "BasketBall"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_01, "Heart"));
		// gridStampArray.add(new StampItem(R.drawable.stamp_02, "Cake"));

		gridStampArrayIndex1 = new ArrayList<StampItem>();
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_01, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_02, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_03, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_04, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_05, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_06, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_07, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_08, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_09, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_10, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_11, ""));
		gridStampArrayIndex1.add(new StampItem(R.drawable.stamp_12, ""));

		gridStampArrayIndex2 = new ArrayList<StampItem>();
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_13, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_14, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_15, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_16, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_17, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_18, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_19, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_20, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_21, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_22, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_23, ""));
		gridStampArrayIndex2.add(new StampItem(R.drawable.stamp_24, ""));

		gridStampArrayIndex3 = new ArrayList<StampItem>();
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_25, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_26, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_27, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_28, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_29, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_30, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_31, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_32, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_33, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_34, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_35, ""));
		gridStampArrayIndex3.add(new StampItem(R.drawable.stamp_36, ""));

		stampAdapter1 = new StampAdapter(this, R.layout.view_stamp_item, gridStampArrayIndex1);

		stampAdapter2 = new StampAdapter(this, R.layout.view_stamp_item, gridStampArrayIndex2);

		stampAdapter3 = new StampAdapter(this, R.layout.view_stamp_item, gridStampArrayIndex3);

		gridStampView.setAdapter(stampAdapter1);
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
				trashImageView.setBackgroundResource(R.drawable.trash2);
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
				changeModeButton.setBackgroundResource(R.drawable.icon_editor_drag);
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
				changeModeButton.setBackgroundResource(R.drawable.icon_editor_draw);
				Log.e(TAG, "change to MODE_WRITING");
			}

		}

	}

	private void addViewByOrder(View view1, View view2)
	{

		sketchpadLayout.addView(view1);
		sketchpadLayout.addView(view2);
	}

	public void initChooseColorImageView()
	{

		chooseColorImageView = (ImageView) findViewById(R.id.act_edit_signature_iv_choose_color);
		chooseColorImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

//				Toast.makeText(EditSignatureActivity.this,
//						"The favorite list would appear on clicking this icon", Toast.LENGTH_LONG)
//						.show();
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

//						Toast.makeText(EditSignatureActivity.this,
//								"Selected Color: " + colorToHexString(colorDialog.getColor()),
//								Toast.LENGTH_LONG).show();
						Log.d(TAG, "Selected Color:"+colorToHexString(colorDialog.getColor()));
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

		eraserToggleButton = (ImageButton) findViewById(R.id.act_edit_signature_tb_eraser);

		eraserToggleButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

				handWritingView.clear();
			}
		});

		// Bruce origin eraser function
		/*
		 * eraserToggleButton.setOnCheckedChangeListener(new
		 * OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton toggleButton,
		 * boolean isChecked) {
		 * 
		 * if (isChecked) { handWritingView.enableEraser(); } else {
		 * handWritingView.disableEraser(); }
		 * 
		 * if (isDebug) { Toast.makeText(EditSignatureActivity.this,
		 * String.valueOf(isChecked), Toast.LENGTH_SHORT).show(); } } });
		 */
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

/*				if (isDebug)
				{
					Toast.makeText(EditSignatureActivity.this, rb.getText(), Toast.LENGTH_SHORT)
							.show();
				}*/

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

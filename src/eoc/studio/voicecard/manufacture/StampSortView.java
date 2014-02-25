package eoc.studio.voicecard.manufacture;

import android.widget.AbsoluteLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.manufacture.MultiTouchController.MultiTouchObjectCanvas;
import eoc.studio.voicecard.manufacture.MultiTouchController.PointInfo;
import eoc.studio.voicecard.manufacture.MultiTouchController.PositionAndScale;
import eoc.studio.voicecard.utils.DragUtility;

import android.R.integer;
import android.R.string;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

public class StampSortView extends AbsoluteLayout implements
		MultiTouchObjectCanvas<StampSortView.Img>
{
	private final static String TAG = "StampSortView";

	private final static Boolean isDebug = true;

	private ArrayList<Integer> mImagesList = new ArrayList<Integer>();

	private  ArrayList<Img> mImages = new ArrayList<Img>();

	private MultiTouchController<Img> multiTouchController = new MultiTouchController<Img>(this);

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	private Paint mLinePaintTouchPointCircle = new Paint();

	ImageView dragImageView;

	private Drawable transparentDrawable;

	private float StampSortViewWidth;

	private float StampSortViewHeight;
	
	private float currentTouchY;


	PositionAndScale lastImgPosAndScale;

	PositionAndScale lastImgPosAndScaleBeforeTrashMode;

	PointInfo lastPointInfoBeforeTrashMode;

	public StampSortView(Context context)
	{

		this(context, null);
	}

	public StampSortView(Context context, AttributeSet attrs)
	{

		this(context, attrs, 0);
	}

	public StampSortView(Context context, AttributeSet attrs, int defStyle)
	{

		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{

		int count = getChildCount();

		int paddingL = getPaddingLeft();
		int paddingT = getPaddingTop();
		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child.getVisibility() != GONE)
			{

				AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) child
						.getLayoutParams();

				int childLeft = paddingL + lp.x;
				int childTop = paddingT + lp.y;
				/*
				 * int childLeft = mPaddingLeft + lp.x; int childTop =
				 * mPaddingTop + lp.y;
				 */
				child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop
						+ child.getMeasuredHeight());

			}
		}
	}

	public void init(Context context)
	{

		Resources res = context.getResources();
		for (int i = 0; i < mImagesList.size(); i++)
		{
			mImages.add(new Img(context, mImagesList.get(i), res));
		}
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.TRANSPARENT);

		StampSortViewHeight = 0;

		transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
	}

	public void loadOneImgage(Context context, int imageId, float cx, float cy, float sx, float sy)
	{

		Resources res = context.getResources();
		mImagesList.add(imageId);
		int size = mImagesList.size();

		Log.e("StampSortView", "size is " + size);
		if (size > 0)
		{

			Img img = new Img(context, mImagesList.get(size - 1), res);
			mImages.add(img);
			mImages.get(mImages.size() - 1).loadWithPosition(res, cx, cy, sx, sy);

			int w = mImages.get(mImages.size() - 1).getImgWidth();
			int h = mImages.get(mImages.size() - 1).getImgHeight();
			int left = (int) cx - w;
			int top = (int) cy - h;

			int x = (int) (cx - (w / 2));
			int y = (int) (cy - (h / 2));

			Log.e(TAG, "loadOneImgage() cx:" + cx + ", cy:" + cy);

			Log.e(TAG, "loadOneImgage() w:" + w + ", h:" + h + ", left:" + left + ", top:" + top);

			Log.e(TAG, "loadOneImgage() x:" + x + ", y:" + cy);
			if (dragImageView == null)
			{
				createDummyDragView(context, w, h, x, y);
			}
			else
			{
				updateDummyDragViewPosition(w, h, x, y);
			}

		}
	}

	public void updateDummyDragViewPosition(int w, int h, int x, int y)
	{

		AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(w, h, x, y);
		dragImageView.setLayoutParams(dragLp);
		dragImageView.setBackgroundDrawable(transparentDrawable);
		dragImageView.invalidate();
		this.updateViewLayout(dragImageView, dragLp);
	}

	public void createDummyDragView(Context context, int w, int h, int x, int y)
	{

		dragImageView = new ImageView(context);
		dragImageView.setScaleType(ImageView.ScaleType.MATRIX);
		dragImageView.setBackgroundDrawable(transparentDrawable);
		AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(w, h, x, y);
		dragImageView.setLayoutParams(dragLp);
		dragImageView.invalidate();
		this.addView(dragImageView);
		dragImageView.setClickable(false);
		dragImageView.setFocusable(false);
	}

	/** Called by activity's onResume() method to load the images **/
	public void loadImages(Context context,Uri loadUri)
	{

		/*
		 * Resources res = context.getResources(); int n = mImages.size(); for
		 * (int i = 0; i < n; i++) mImages.get(i).load(res);
		 */
        // get seals from json in file
		
		
/*		String root = Environment.getExternalStorageDirectory().toString();
		File tempDir = new File(root + "/VoiceCard_seals");

		File pathDir = new File(tempDir.toString());
		pathDir.mkdirs();

		String fname = "seal.json";
		File file = new File(tempDir + "/" + fname);*/
		
		
		File file = new File(loadUri.getPath());

		
		FileInputStream fIn;
		String json = ""; // Holds the text
		try
		{
			fIn = new FileInputStream(file);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";
			
			while ((aDataRow = myReader.readLine()) != null)
			{
				json += aDataRow;
			}
			myReader.close();
		}
		catch (IOException e)
		{
			
			e.printStackTrace();
		}

		Log.e(TAG, "loadImages read json from file is "+json);
		Gson gson = new Gson();
		ArrayList<StampGson> sealList= new ArrayList<StampGson>();
		sealList = gson.fromJson(json, new TypeToken<ArrayList<StampGson>>(){}.getType());
		int size = sealList.size();
		Resources res = context.getResources();
		mImages.clear();
		

//		mImages.get(mImages.size() - 1).loadWithPosition(res, cx, cy, sx, sy);
		for (int index = 0; index < size; index++)
		{
			Img img = new Img(context, sealList.get(index).getResId(), res);
			mImages.add(img);
			Log.e(TAG, "loadImages on resume reload the images index:" + index);
			mImages.get(index).loadWithPosition(res, sealList.get(index).getResId(),
					sealList.get(index).getCenterX(), sealList.get(index).getCenterY(),
					sealList.get(index).getScaleX(), sealList.get(index).getScaleY(),
					sealList.get(index).getAngle(), sealList.get(index).getDisplayWidth(),
					sealList.get(index).getDisplayHeight());
		
			if (index == size - 1)
			{

				int imageWidth = mImages.get(index).getWidth();
				int imageHeight = mImages.get(index).getHeight();
				int x_position = (int) (mImages.get(index).getCenterX() - (mImages.get(index)
						.getWidth() / 2));
				int y_position = (int) (mImages.get(index).getCenterY() - (mImages.get(index)
						.getHeight() / 2));

				if (dragImageView == null)
				{
					
					createDummyDragView(context, imageWidth, imageHeight, x_position, x_position);
//					dragImageView = new ImageView(context);
//					dragImageView.setScaleType(ImageView.ScaleType.MATRIX);
//					dragImageView.setBackgroundDrawable(transparentDrawable);
//					AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(
//							, imageHeight, x_position, y_position);
//					dragImageView.setLayoutParams(dragLp);
//					dragImageView.invalidate();
//					this.addView(dragImageView);
//					dragImageView.setClickable(false);
//					dragImageView.setFocusable(false);
				}
				else
				{
					
					updateDummyDragViewPosition(imageWidth, imageHeight, x_position, x_position);
//					AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(
//							imageWidth, imageHeight, x_position, y_position);
//					dragImageView.setLayoutParams(dragLp);
//					dragImageView.setBackgroundDrawable(transparentDrawable);
//					dragImageView.invalidate();
//					this.updateViewLayout(dragImageView, dragLp);
				}
			}
		}

//		Type listType = new TypeToken<ArrayList<YourClass>>() {
//        }.getType();
//List<YourClass> yourClassList = new Gson().fromJson(jsonArray, listType);
		
		
		
		// get seals from mImages
//		Resources res = context.getResources();
//		int size = mImages.size();
//		for (int index = 0; index < size; index++)
//		{
//			Log.e(TAG, "loadImages on resume reload the images index:" + index);
//			mImages.get(index).loadWithPosition(res, mImages.get(index).getCenterX(),
//					mImages.get(index).getCenterY(), mImages.get(index).getScaleX(),
//					mImages.get(index).getScaleY(), mImages.get(index).getAngle());
//
//			if (index == size - 1)
//			{
//
//				int imageWidth = mImages.get(index).getWidth();
//				int imageHeight = mImages.get(index).getHeight();
//				int x_position = (int) (mImages.get(index).getCenterX() - (mImages.get(index)
//						.getWidth() / 2));
//				int y_position = (int) (mImages.get(index).getCenterY() - (mImages.get(index)
//						.getHeight() / 2));
//
//				if (dragImageView == null)
//				{
//					dragImageView = new ImageView(context);
//					dragImageView.setScaleType(ImageView.ScaleType.MATRIX);
//					dragImageView.setBackgroundDrawable(transparentDrawable);
//					AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(
//							imageWidth, imageHeight, x_position, y_position);
//					dragImageView.setLayoutParams(dragLp);
//					dragImageView.invalidate();
//					this.addView(dragImageView);
//					dragImageView.setClickable(false);
//					dragImageView.setFocusable(false);
//				}
//				else
//				{
//					AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(
//							imageWidth, imageHeight, x_position, y_position);
//					dragImageView.setLayoutParams(dragLp);
//					dragImageView.setBackgroundDrawable(transparentDrawable);
//					dragImageView.invalidate();
//					this.updateViewLayout(dragImageView, dragLp);
//				}
//			}
//		}

		invalidate();  
	}	
//	public void loadImages(Context context)
//	{
//
//		/*
//		 * Resources res = context.getResources(); int n = mImages.size(); for
//		 * (int i = 0; i < n; i++) mImages.get(i).load(res);
//		 */
//
//		Resources res = context.getResources();
//		for (int index = 0; index < mImages.size(); index++)
//		{
//			Log.e(TAG, "loadImages on resume reload the images index:" + index);
//			mImages.get(index).loadWithPosition(res, mImages.get(index).getCenterX(),
//					mImages.get(index).getCenterY(), mImages.get(index).getScaleX(),
//					mImages.get(index).getScaleY(), mImages.get(index).getAngle());
//
//			int lastIndex = mImages.size() - 1;
//			if (index == lastIndex)
//			{
//
//				int imageWidth = mImages.get(index).getWidth();
//				int imageHeight = mImages.get(index).getHeight();
//				int x_position = (int) (mImages.get(index).getCenterX() - (mImages.get(index)
//						.getWidth() / 2));
//				int y_position = (int) (mImages.get(index).getCenterY() - (mImages.get(index)
//						.getHeight() / 2));
//
//				if (dragImageView == null)
//				{
//					createDummyDragView(context, imageWidth, imageHeight, x_position, y_position);
//				}
//				else
//				{
//					updateDummyDragViewPosition(imageWidth, imageHeight, x_position, y_position);
//				}
//			}
//		}
//
//		invalidate();
//	}
	
	public Uri saveImageInfoToGson(String savePath,String saveFileName)
	{
		Uri uriOfJsonPath;
		Log.e(TAG, "saveImageInfoToGson()");
		ArrayList<StampGson> sealList = new ArrayList<StampGson>();
		int size = mImages.size();

		for (int index = 0; index < size; index++)
		{
			StampGson seal = new StampGson();

			seal.setResId(mImages.get(index).getResId());
			seal.setAngle(mImages.get(index).getAngle());
			seal.setCenterX(mImages.get(index).getCenterX());
			seal.setCenterY(mImages.get(index).getCenterY());
			seal.setWidth(mImages.get(index).getImgWidth());
			seal.setHeight(mImages.get(index).getImgHeight());
			seal.setScaleX(mImages.get(index).getScaleX());
			seal.setScaleY(mImages.get(index).getScaleY());
			seal.setDisplayWidth(mImages.get(index).getDisplayWidth());
			seal.setDisplayHeight(mImages.get(index).getDisplayHeight());
			sealList.add(seal);
		}
		final GsonBuilder builder = new GsonBuilder();
		final Gson gson = builder.create();
		final String json = gson.toJson(sealList);

		File pathDir = new File(savePath);
		pathDir.mkdirs();

		String fileName = saveFileName;
		File file = new File(savePath + "/" + fileName);
		
		
		uriOfJsonPath = Uri.fromFile(file); 
		
		if (file.exists()) file.delete();
		try
		{

			FileWriter writer = null;
			try
			{
				writer = new FileWriter(file);

				/** Saving the contents to the file */
				writer.write(json);

				/** Closing the writer object */
				writer.close();

				// Toast.makeText(context, "Successfully saved",
				// Toast.LENGTH_SHORT).show();

			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return uriOfJsonPath;
	}
	
	

	@Override
	protected void onDraw(Canvas canvas)
	{

		Log.i("Tag", "width: " + getWidth() + ",height: " + getHeight());
		super.onDraw(canvas);
		this.StampSortViewWidth = getWidth();
		this.StampSortViewHeight = getHeight();

		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).draw(canvas);
		if (mShowDebugInfo) drawMultitouchDebugMarks(canvas);
	}

	public void trackballClicked()
	{

		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas)
	{

		if (currTouchPoint.isDown())
		{
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		}
	}

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		Log.e(TAG, "onTouchEvent() event.getX()" + event.getX() + ", event.getY()" + event.getY());
		currentTouchY = event.getY();

		return multiTouchController.onTouchEvent(event);
	}

	/**
	 * Get the image that is under the single-touch point, or return null
	 * (canceling the drag op) if none
	 */
	public Img getDraggableObjectAtPoint(PointInfo pt)
	{

		dragImageView.setBackgroundDrawable(transparentDrawable);
		dragImageView.invalidate();
		float x = pt.getX(), y = pt.getY();
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--)
		{
			Img im = mImages.get(i);
			if (im.containsPoint(x, y)) return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be
	 * under the point (non-null is returned by getDraggableObjectAtPoint()) and
	 * a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(Img img, PointInfo touchPoint)
	{

		dragImageView.setBackgroundColor(Color.TRANSPARENT);
		dragImageView.invalidate();
		// Bruce add : can't add log here, otherwise bug circle will keep
		// showing on the screen
		// Log.e(TAG,
		// "selectObject() mg.getCenterX()" + img.getCenterX() +
		// ",mg.getCenterY()"
		// + img.getCenterY());
		currTouchPoint.set(touchPoint);
		if (img != null)
		{
			// Move image to the top of the stack when selected
			mImages.remove(img);
			mImages.add(img);
		}
		else
		{
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	/**
	 * Get the current position and scale of the selected image. Called whenever
	 * a drag starts or is reset.
	 */
	public void getPositionAndScale(Img img, PositionAndScale objPosAndScaleOut)
	{

		dragImageView.setBackgroundDrawable(transparentDrawable);
		dragImageView.invalidate();

		// FIXME affine-izem (and fix the fact that the anisotropic_scale part
		// requires averaging the two scale factors)
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2,
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(), img.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(Img img, PositionAndScale newImgPosAndScale,
			PointInfo touchPoint)
	{

		float effectHeight = img.getImgHeight() * img.getScaleY();
		float effectWidth = img.getImgWidth() * img.getScaleX();
		int deviation = 10;
		img.setScreenMargin(effectHeight - deviation);

		float distanceOfStapSortView = (img.getY() + effectHeight - StampSortViewHeight);
		Log.e(TAG, "=== setPositionAndScale() distanceOfStapSortView is " + distanceOfStapSortView);

		// try to save last point that can not be drag to trash mode
		if (distanceOfStapSortView  >  0 && distanceOfStapSortView < deviation)
		{
			Log.e(TAG, "=== setPositionAndScale() save the last point");
			updateLastPointInfos(newImgPosAndScale);
		}

		currTouchPoint.set(touchPoint);

		boolean ok = img.setPos(newImgPosAndScale);

		updateDummyDragView(img, transparentDrawable);
		updateDummyDragViewPosition(img, effectHeight, effectWidth);

		
		if (currentTouchY > (StampSortViewHeight +effectHeight*0.3) && distanceOfStapSortView > 0 )
		{
			Log.e(TAG, "=== try to trash===  distanceOfStapSortView" + distanceOfStapSortView);

			updateDummyDragView(img, img.getDrawable());
			DragUtility.dragView(dragImageView, TAG, DragUtility.LABLE_TO_TRASH);
		}

		if (ok) invalidate();
		return ok;
	}

	public void updateDummyDragViewPosition(Img img, float effectHeight, float effectWidth)
	{

		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
				(int) effectWidth, (int) effectHeight, (int) img.getX(),
				(int) img.getY());
		this.updateViewLayout(dragImageView, lp);
	}

	public void updateLastPointInfos(PositionAndScale newImgPosAndScale)
	{

		lastImgPosAndScaleBeforeTrashMode = null;
		lastImgPosAndScaleBeforeTrashMode = new PositionAndScale();

		lastImgPosAndScaleBeforeTrashMode.set(newImgPosAndScale.getXOff(),
				newImgPosAndScale.getYOff(), true, newImgPosAndScale.getScale(), true,
				newImgPosAndScale.getScaleX(), newImgPosAndScale.getScaleY(), true,
				newImgPosAndScale.getAngle());

		lastPointInfoBeforeTrashMode = null;
		lastPointInfoBeforeTrashMode = new PointInfo();
		lastPointInfoBeforeTrashMode.set(currTouchPoint);
	}

	public void updateDummyDragView(Img img, Drawable backgroudDrawable)
	{

		Matrix viewMatrix = new Matrix();
		viewMatrix.postScale(img.getScaleX(), img.getScaleY());
		viewMatrix.postRotate(img.getAngle() * 180.0f / (float) Math.PI);
		dragImageView.setImageMatrix(viewMatrix);
		dragImageView.setRotation(img.getAngle());
		dragImageView.setBackgroundDrawable(backgroudDrawable);
		dragImageView.invalidate();
	}

	public void removeStamp()
	{
		if (mImages.size() > 0)
		{
			int lastIndex = mImages.size() - 1;
			mImages.remove(lastIndex);
			dragImageView.setBackgroundDrawable(transparentDrawable);
			dragImageView.invalidate();
			this.invalidate();
		}
	}

	public void cancelStamp()
	{

		dragImageView.setBackgroundDrawable(transparentDrawable);
		dragImageView.invalidate();
		Log.e(TAG, "=== after cancelSeal mImages.size():" + mImages.size());
		if (mImages.size() > 0)
		{
			if (lastImgPosAndScaleBeforeTrashMode != null)
			{
				Log.e(TAG, "=== after cancelSeal lastImgPosAndScaleBeforeTrashMode.getXOff()"
						+ lastImgPosAndScaleBeforeTrashMode.getXOff());
				Log.e(TAG, "=== after cancelSeal lastImgPosAndScaleBeforeTrashMode.getYOff()"
						+ lastImgPosAndScaleBeforeTrashMode.getYOff());

				Log.e(TAG, "=== after cancelSeal lastImgPosAndScaleBeforeTrashMode.getScale()"
						+ lastImgPosAndScaleBeforeTrashMode.getScale());
				Log.e(TAG, "=== after cancelSeal lastImgPosAndScaleBeforeTrashMode.getScaleX()"
						+ lastImgPosAndScaleBeforeTrashMode.getScaleX());
			}
			else{
				Log.e(TAG, "===  lastImgPosAndScaleBeforeTrashMode is null");
			}

			Img img = mImages.get(mImages.size() - 1);

			mImages.remove(img);

			Boolean ok = img.setPos(lastImgPosAndScaleBeforeTrashMode);

			mImages.add(img);
			if (ok && isDebug)
			{
				Log.e(TAG, "set new point ok");
				Log.e(TAG, "mg.getScaleX()" + img.getScaleX());
				Log.e(TAG, "mg.getX()" + img.getX() + ",mg.getY()" + img.getY());
				Log.e(TAG,
						"mg.getCenterX()" + img.getCenterX() + ",mg.getCenterY()"
								+ img.getCenterY());
			}

			invalidate();
		}
	}

	class Img extends View
	{
		private int resId;

		private Drawable drawable;

		private boolean firstLoad;

		private int width, height, displayWidth, displayHeight;



		private float centerX, centerY, scaleX, scaleY, angle;

		private float minX, maxX, minY, maxY;

		// private static final float SCREEN_MARGIN = 0;
		public float screenMargin = 50;
		// bruce add
		private float imageViewPositionX, imageViewPositionY;

		public Img(Context context, int resId, Resources res)
		{

			this(context, null, 0);
			init(resId, res);
		}

		public Img(Context context, AttributeSet attrs, int resId, Resources res)
		{

			this(context, attrs, 0);
			init(resId, res);
		}

		public Img(Context context, AttributeSet attrs, int defStyle)
		{

			super(context, attrs, defStyle);
			setWillNotDraw(false);

		}

		public void init(int resId, Resources res)
		{

			this.resId = resId;
			this.firstLoad = true;
			getMetrics(res);

			// bruce add
			this.setClickable(false);
			this.setFocusable(false);
			// SCREEN_MARGIN = width / 2;
		}

		private void getMetrics(Resources res)
		{

			DisplayMetrics metrics = res.getDisplayMetrics();
			// The DisplayMetrics don't seem to always be updated on screen
			// rotate, so we hard code a portrait
			// screen orientation for the non-rotated screen here...
			// this.displayWidth = metrics.widthPixels;
			// this.displayHeight = metrics.heightPixels;

			Log.e(TAG, "getMetrics() metrics.widthPixels:" + metrics.widthPixels
					+ ",metrics.heightPixels:" + metrics.heightPixels);
			this.displayWidth = (int) StampSortViewWidth;
			this.displayHeight = (int) StampSortViewHeight;
			// this.displayWidth = 276 * 2;
			// this.displayHeight = (int) ((273 * 2) - SCREEN_MARGIN);

			/*
			 * this.displayWidth = res.getConfiguration().orientation ==
			 * Configuration.ORIENTATION_LANDSCAPE ? Math
			 * .max(metrics.widthPixels, metrics.heightPixels) :
			 * Math.min(metrics.widthPixels, metrics.heightPixels);
			 * this.displayHeight = res.getConfiguration().orientation ==
			 * Configuration.ORIENTATION_LANDSCAPE ? Math
			 * .min(metrics.widthPixels, metrics.heightPixels) :
			 * Math.max(metrics.widthPixels, metrics.heightPixels);
			 */
		}

		public void loadWithPosition(Resources res, float cx, float cy, float sx, float sy)
		{

			getMetrics(res);
			this.drawable = res.getDrawable(resId);
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();

			// @bruce mark the original code
			// if (this.maxX < SCREEN_MARGIN)
			// {
			// cx = SCREEN_MARGIN;
			// }
			// else if (this.minX > displayWidth - SCREEN_MARGIN)
			// {
			// cx = displayWidth - SCREEN_MARGIN;
			// }
			//
			// if (this.maxY > SCREEN_MARGIN)
			// {
			// cy = SCREEN_MARGIN;
			// }
			// else if (this.minY > displayHeight - SCREEN_MARGIN)
			// {
			// cy = displayHeight - SCREEN_MARGIN;
			// }

			setPos(cx, cy, sx, sy, 0.0f);
		}

		public void loadWithPosition(Resources res, float cx, float cy, float sx, float sy,
				float angle)
		{

			getMetrics(res);
			this.drawable = res.getDrawable(resId);
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();
			setPos(cx, cy, sx, sy, angle);
		}
		
		public void loadWithPosition(Resources res, int resId ,float cx, float cy, float sx, float sy,
				float angle,int displayWidth, int displayHeight)
		{

			getMetrics(res);
			this.drawable = res.getDrawable(resId);
			this.resId = resId;
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();
			this.displayWidth = displayWidth;
			this.displayHeight = displayHeight;
			setPos(cx, cy, sx, sy, angle);
		}

		/** Called by activity's onResume() method to load the images */
		public void load(Resources res)
		{

			getMetrics(res);
			this.drawable = res.getDrawable(resId);
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();
			float cx, cy, sx, sy;
			if (firstLoad)
			{
				cx = 100;
				cy = 100;
				float sc = (float) 1.0;
				/*
				 * cx = SCREEN_MARGIN + (float) (Math.random() * (displayWidth -
				 * 2 * SCREEN_MARGIN)); cy = SCREEN_MARGIN + (float)
				 * (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
				 */
				// float sc = (float) (Math.max(displayWidth, displayHeight) /
				// (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
				sx = sy = sc;
				firstLoad = false;
			}
			else
			{
				// Reuse position and scale information if it is available
				// FIXME this doesn't actually work because the whole activity
				// is torn down and re-created on rotate
				cx = this.centerX;
				cy = this.centerY;
				sx = this.scaleX;
				sy = this.scaleY;
				// Make sure the image is not off the screen after a screen
				// rotation
				if (this.maxX < screenMargin)
					cx = screenMargin;
				else if (this.minX > displayWidth - screenMargin)
					cx = displayWidth - screenMargin;
				if (this.maxY > screenMargin)
					cy = screenMargin;
				else if (this.minY > displayHeight - screenMargin)
					cy = displayHeight - screenMargin;
			}
			setPos(cx, cy, sx, sy, 0.0f);
		}

		/**
		 * Called by activity's onPause() method to free memory used for loading
		 * the images
		 */
		public void unload()
		{

			this.drawable = null;
		}

		/** Set the position and scale of an image in screen coordinates */
		public boolean setPos(PositionAndScale newImgPosAndScale)
		{

			return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(),
					(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleX()
							: newImgPosAndScale.getScale(),
					(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY()
							: newImgPosAndScale.getScale(), newImgPosAndScale.getAngle());
			// FIXME: anisotropic scaling jumps when axis-snapping
			// FIXME: affine-ize
		}

		/** Set the position and scale of an image in screen coordinates */
		private boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle)
		{

			float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
			float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY
					+ hs;
			if (newMinX > displayWidth - screenMargin || newMaxX < screenMargin
					|| newMinY > displayHeight - screenMargin || newMaxY < screenMargin)
			{
				
				//for deg bug
//				Log.e(TAG, "!!!!!setPos() Position out of Display Screen");
//				Log.e(TAG, "!!!!!setPos() width:"+width);
//				Log.e(TAG, "!!!!!setPos() height:"+height);
//				Log.e(TAG, "!!!!!setPos() scaleX:"+scaleX);
//				Log.e(TAG, "!!!!!setPos() scaleY:"+scaleY);
//				Log.e(TAG, "!!!!!setPos() ws:"+ws);
//				Log.e(TAG, "!!!!!setPos() hs:"+hs);
//				Log.e(TAG, "!!!!!setPos() newMinX:"+newMinX);
//				Log.e(TAG, "!!!!!setPos() newMaxX:"+newMaxX);
//				Log.e(TAG, "!!!!!setPos() newMinY:"+newMinY);
//				Log.e(TAG, "!!!!!setPos() newMaxY:"+newMaxY);
//				Log.e(TAG, "!!!!!setPos() screenMargin:"+screenMargin);
//				Log.e(TAG, "!!!!!setPos() displayWidth:"+displayWidth);
//				Log.e(TAG, "!!!!!setPos() displayHeight:"+displayHeight);
				
				return false;
			}

			// bruce add
			setX(newMinX);
			setY(newMinY);
			// bruce add end

			this.centerX = centerX;
			this.centerY = centerY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.angle = angle;
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
			return true;
		}

		/** Return whether or not the given screen coords are inside this image */
		public boolean containsPoint(float scrnX, float scrnY)
		{

			// FIXME: need to correctly account for image rotation
			return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
		}

		public void draw(Canvas canvas)
		{

			canvas.save();
			float dx = (maxX + minX) / 2;
			float dy = (maxY + minY) / 2;
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			canvas.translate(dx, dy);
			canvas.rotate(angle * 180.0f / (float) Math.PI);
			canvas.translate(-dx, -dy);
			drawable.draw(canvas);
			canvas.restore();
		}

		public Drawable getDrawable()
		{

			return drawable;
		}

		public int getImgWidth()
		{

			return width;
		}

		public int getImgHeight()
		{

			return height;
		}

		public float getCenterX()
		{

			return centerX;
		}

		public void setCenterY(float cy)
		{

			centerY = cy;
		}

		public float getCenterY()
		{

			return centerY;
		}

		public float getScaleX()
		{

			return scaleX;
		}

		public float getScaleY()
		{

			return scaleY;
		}

		public float getAngle()
		{

			return angle;
		}

		// FIXME: these need to be updated for rotation
		public float getMinX()
		{

			return minX;
		}

		public float getMaxX()
		{

			return maxX;
		}

		public float getMinY()
		{

			return minY;
		}

		public float getMaxY()
		{

			return maxY;
		}
		

		public float getScreenMargin()
		{
		
			return screenMargin;
		}

		public void setScreenMargin(float screenMargin)
		{
		
			this.screenMargin = screenMargin;
		}
		
		public int getResId()
		{
		
			return resId;
		}

		public void setResId(int resId)
		{
		
			this.resId = resId;
		}
		
		public int getDisplayWidth()
		{
		
			return displayWidth;
		}

		public void setDisplayWidth(int displayWidth)
		{
		
			this.displayWidth = displayWidth;
		}

		public int getDisplayHeight()
		{
		
			return displayHeight;
		}

		public void setDisplayHeight(int displayHeight)
		{
		
			this.displayHeight = displayHeight;
		}
	}

}

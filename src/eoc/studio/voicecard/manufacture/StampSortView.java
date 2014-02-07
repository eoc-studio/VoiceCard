package eoc.studio.voicecard.manufacture;

import android.widget.AbsoluteLayout;


import java.util.ArrayList;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.manufacture.MultiTouchController.MultiTouchObjectCanvas;
import eoc.studio.voicecard.manufacture.MultiTouchController.PointInfo;
import eoc.studio.voicecard.manufacture.MultiTouchController.PositionAndScale;

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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
public class StampSortView extends AbsoluteLayout implements MultiTouchObjectCanvas<StampSortView.Img>
{
	private final static String TAG = "StampSortView";

	private ArrayList<Integer> mImagesList = new ArrayList<Integer>();

	private static ArrayList<Img> mImages = new ArrayList<Img>();

	private MultiTouchController<Img> multiTouchController = new MultiTouchController<Img>(this);

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = true;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	private Paint mLinePaintTouchPointCircle = new Paint();

	ImageView dragImageView;

	private Drawable transparentDrawable;

	private float borderXofTrash;
	private float borderYofTrash;
	

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
		
		borderYofTrash = 0;

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
			else
			{
				AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(w, h, x, y);
				dragImageView.setLayoutParams(dragLp);
				dragImageView.setBackgroundDrawable(transparentDrawable);
				dragImageView.invalidate();
				this.updateViewLayout(dragImageView, dragLp);
			}

		}
	}

	/** Called by activity's onResume() method to load the images **/
	public void loadImages(Context context)
	{

		/*
		 * Resources res = context.getResources(); int n = mImages.size(); for
		 * (int i = 0; i < n; i++) mImages.get(i).load(res);
		 */

		Resources res = context.getResources();
		int size = mImages.size();
		for (int index = 0; index < size; index++)
		{
			Log.e(TAG, "loadImages on resume reload the images index:" + index);
			mImages.get(index).loadWithPosition(res, mImages.get(index).getCenterX(),
					mImages.get(index).getCenterY(), mImages.get(index).getScaleX(),
					mImages.get(index).getScaleY(), mImages.get(index).getAngle());

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
					dragImageView = new ImageView(context);
					dragImageView.setScaleType(ImageView.ScaleType.MATRIX);
					dragImageView.setBackgroundDrawable(transparentDrawable);
					AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(
							imageWidth, imageHeight, x_position, y_position);
					dragImageView.setLayoutParams(dragLp);
					dragImageView.invalidate();
					this.addView(dragImageView);
					dragImageView.setClickable(false);
					dragImageView.setFocusable(false);
				}
				else
				{
					AbsoluteLayout.LayoutParams dragLp = new AbsoluteLayout.LayoutParams(
							imageWidth, imageHeight, x_position, y_position);
					dragImageView.setLayoutParams(dragLp);
					dragImageView.setBackgroundDrawable(transparentDrawable);
					dragImageView.invalidate();
					this.updateViewLayout(dragImageView, dragLp);
				}
			}
		}

		invalidate();
	}

	//
	// /**
	// * Called by activity's onPause() method to free memory used for loading
	// the
	// * images
	// */
	// public void unloadImages()
	// {
	//
	// int n = mImages.size();
	// for (int i = 0; i < n; i++)
	// mImages.get(i).unload();
	// }

	@Override
	protected void onDraw(Canvas canvas)
	{

		Log.i("Tag", "width: " + getWidth() + ",height: " + getHeight());
//		Log.i("Tag", "MeasuredWidth: " + getMeasuredWidth() + ",MeasuredHeight: "
//				+ getMeasuredHeight());
		super.onDraw(canvas);
		this.borderXofTrash = getWidth();
		this.borderYofTrash = getHeight();
		
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
			// Log.e(TAG, "drawMultitouchDebugMarks() currTouchPoint.isDown()");

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

	// ---------------------------------------------------------------------------------------------------
	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		Log.e(TAG, "onTouchEvent() event.getX()" + event.getX() + ", event.getY()" + event.getY());
		
		
//		if (event.getX() > borderXofTrash || event.getX() < 50 || event.getY() < 50) { return true; }
		
//		if (event.getY() > 860 || event.getX() >borderXofTrash || event.getX()< 0 ||  event.getY()< 0) { return true; }

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
		// Log.e(TAG, "getPositionAndScale() mg.getCenterX()" + img.getCenterX()
		// + ",mg.getCenterY()"
		// + img.getCenterY());
		// Log.e(TAG,
		// "=================getPositionAndScale() get the last point mg.getX()"
		// + img.getX()
		// + ",mg.getY()" + img.getY());
		// Log.e(TAG,
		// "=================getPositionAndScale() get the last point mg.getCenterX()"
		// + img.getCenterX() + ",mg.getCenterY()" + img.getCenterY());
		// lastImgPosAndScaleBeforeTrashMode = null;
		// lastImgPosAndScaleBeforeTrashMode = new PositionAndScale();
		// lastImgPosAndScaleBeforeTrashMode.set(objPosAndScaleOut.getXOff(),
		// objPosAndScaleOut.getYOff(), objPosAndScaleOut.getScale(),
		// objPosAndScaleOut.getScaleX(), objPosAndScaleOut.getScaleY(),
		// objPosAndScaleOut.getAngle());
		//
		//
		// lastPointInfoBeforeTrashMode = null;
		// lastPointInfoBeforeTrashMode = new PointInfo();
		// lastPointInfoBeforeTrashMode.set(currTouchPoint);

		// lastPointInfoBeforeTrashMode = touchPoint;
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

		img.SCREEN_MARGIN = img.getImgHeight() * img.getScaleY() - 10;
		
		Log.e(TAG, "@@@@@@@@@@setPositionAndScale()  newImgPosAndScale.getScale()"
				+ newImgPosAndScale.getScale());
		Log.e(TAG, "@@@@@@@@@@setPositionAndScale()  newImgPosAndScale.getScaleX()"
				+ newImgPosAndScale.getScaleX());
		float distanceOfBorder = (img.getY() + (img.getImgHeight() * img.getScaleY()) - borderYofTrash);
		Log.e(TAG, "=================setPositionAndScale() distanceOfBorder is " + distanceOfBorder);

		// try to save last point that can not be drag to trash mode
		if (distanceOfBorder > 0 && distanceOfBorder < 10)
		{

			// Log.e(TAG,
			// "=================setPositionAndScale() get the last point mg.getX()"
			// + img.getX() + ",mg.getY()" + img.getY());
			// Log.e(TAG,
			// "=================setPositionAndScale() get the last point mg.getCenterX()"
			// + img.getCenterX()+ ",mg.getCenterY()" + img.getCenterY());
			//

			// Log.e(TAG,
			// "=============setPositionAndScale() lastImgPosAndScaleBeforeTrashMode.getScaleX()"
			// + lastImgPosAndScaleBeforeTrashMode.getScaleX());
			lastImgPosAndScaleBeforeTrashMode = null;
			lastImgPosAndScaleBeforeTrashMode = new PositionAndScale();

			lastImgPosAndScaleBeforeTrashMode.set(newImgPosAndScale.getXOff(),
					newImgPosAndScale.getYOff(), true, newImgPosAndScale.getScale(), true,
					newImgPosAndScale.getScaleX(), newImgPosAndScale.getScaleY(), true,
					newImgPosAndScale.getAngle());

			Log.e(TAG,
					"@@@@@@@@@@setPositionAndScale()  lastImgPosAndScaleBeforeTrashMode.getScale()"
							+ lastImgPosAndScaleBeforeTrashMode.getScale());
			Log.e(TAG,
					"@@@@@@@@@@setPositionAndScale()  lastImgPosAndScaleBeforeTrashMode.getScaleX()"
							+ lastImgPosAndScaleBeforeTrashMode.getScaleX());

			lastPointInfoBeforeTrashMode = null;
			lastPointInfoBeforeTrashMode = new PointInfo();
			lastPointInfoBeforeTrashMode.set(currTouchPoint);
			dragImageView.setBackgroundDrawable(transparentDrawable);
			dragImageView.invalidate();
		}

		// if (touchPoint != null)
		// {
		currTouchPoint.set(touchPoint);
		// }
		boolean ok = img.setPos(newImgPosAndScale);

		// @bruce add
		// Log.e(TAG, "setPositionAndScale() mg.getX()" + img.getX() +
		// ",mg.getY()" + img.getY());

		Matrix viewMatrix = new Matrix();
		viewMatrix.postScale(img.getScaleX(), img.getScaleY());

		Log.e(TAG, "@@@@@@@@@@setPositionAndScale()  img.getAngle()" + img.getAngle());
		viewMatrix.postRotate(img.getAngle() * 180.0f / (float) Math.PI);
		dragImageView.setImageMatrix(viewMatrix);
		dragImageView.setBackgroundDrawable(transparentDrawable);
		dragImageView.invalidate();

		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
				(int) (img.getImgWidth() * img.getScaleX()),
				(int) (img.getImgHeight() * img.getScaleY()), (int) img.getX(), (int) img.getY());
		this.updateViewLayout(dragImageView, lp);
		// @bruce add end

		// Log.e(TAG, "============================borderYofTrash =" +
		// borderYofTrash);
		// Log.e(TAG,
		// "============================borderYofTrash variable=" + img.getY()
		// + (img.getImgHeight() / 2));

		float distanceTrashBorder = img.getY() + (img.getImgHeight() * img.getScaleY() )
				- borderYofTrash;
		
//		float distanceTrashBorder = img.getY() + (img.getImgHeight() * img.getScaleY() / 2)
//				- borderYofTrash;

		if (distanceTrashBorder > 0 && distanceTrashBorder < 10)
		{
			Log.e(TAG,
					"============================try to trash======================= distanceTrashBorder"
							+ distanceTrashBorder);

			ClipData data = ClipData.newPlainText("photoSortView", "Try to trash");
			// DragShadowBuilder shadowBuilder = new
			// View.DragShadowBuilder(dragImageView);

			dragImageView.setBackgroundDrawable(img.getDrawable());
			viewMatrix.postScale(img.getScaleX(), img.getScaleY());
			viewMatrix.postRotate(img.getAngle() * 180.0f / (float) Math.PI);
			dragImageView.setImageMatrix(viewMatrix);
			dragImageView.setRotation(img.getAngle());
			dragImageView.invalidate();

			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(dragImageView);
			/*
			 * DragShadowBuilder shadowBuilder = new
			 * View.DragShadowBuilder(dragImageView) {
			 * 
			 * @Override public void onDrawShadow(Canvas canvas) {
			 * 
			 * 
			 * canvas.scale(dragImageView.getScaleX(),
			 * dragImageView.getScaleY(), dragImageView.getWidth() ,
			 * dragImageView.getHeight() );
			 * 
			 * Log.e(TAG,
			 * "============================try to trash dragImageView.getRotation()"
			 * + dragImageView.getRotation());
			 * 
			 * 
			 * canvas.translate((canvas.getWidth() - dragImageView.getWidth()) /
			 * 2, (canvas.getHeight() - dragImageView.getHeight() ) / 2);
			 * 
			 * 
			 * canvas.rotate(dragImageView.getRotation() * 180.0f / (float)
			 * Math.PI); super.onDrawShadow(canvas); }
			 * 
			 * @Override public void onProvideShadowMetrics(Point shadowSize,
			 * Point shadowTouchPoint) {
			 * 
			 * shadowSize.set((int) (dragImageView.getWidth() *
			 * dragImageView.getScaleX()), (int)( dragImageView.getHeight() *
			 * dragImageView.getScaleY())); shadowTouchPoint.set(shadowSize.x /
			 * 2, shadowSize.y / 2); } };
			 */

			dragImageView.startDrag(data, shadowBuilder, dragImageView, 0);
		}

		if (ok) invalidate();
		return ok;
	}

	public void removeSeal()
	{

		int size = mImages.size();

		if (size > 0)
		{
			mImages.remove(size - 1);
			Log.e(TAG, "=============after remove mImages.size():" + mImages.size());
			dragImageView.setBackgroundDrawable(transparentDrawable);
			dragImageView.invalidate();
			this.invalidate();
		}

	}

	public void cancelSeal()
	{

		dragImageView.setBackgroundDrawable(transparentDrawable);
		dragImageView.invalidate();
		int size = mImages.size();
		Log.e(TAG, "=============after cancelSeal mImages.size():" + mImages.size());
		if (size > 0)
		{
			if (lastImgPosAndScaleBeforeTrashMode != null)
			{
				Log.e(TAG,
						"=============after cancelSeal lastImgPosAndScaleBeforeTrashMode.getXOff()"
								+ lastImgPosAndScaleBeforeTrashMode.getXOff());
				Log.e(TAG,
						"=============after cancelSeal lastImgPosAndScaleBeforeTrashMode.getYOff()"
								+ lastImgPosAndScaleBeforeTrashMode.getYOff());

				Log.e(TAG,
						"=============after cancelSeal lastImgPosAndScaleBeforeTrashMode.getScale()"
								+ lastImgPosAndScaleBeforeTrashMode.getScale());
				Log.e(TAG,
						"=============after cancelSeal lastImgPosAndScaleBeforeTrashMode.getScaleX()"
								+ lastImgPosAndScaleBeforeTrashMode.getScaleX());
			}

			Img img = mImages.get(size - 1);

			mImages.remove(img);
			// img.setCenterY((float) (img.getCenterY() - (img.getImgHeight() *
			// 1.5)));

			Boolean ok = img.setPos(lastImgPosAndScaleBeforeTrashMode);
			mImages.add(img);
			if (ok)
			{
				Log.e(TAG, "set new point ok");
				Log.e(TAG, "set new point ok mg.getScaleX()" + img.getScaleX());
				Log.e(TAG, "set new point ok mg.getX()" + img.getX() + ",mg.getY()" + img.getY());
				Log.e(TAG, "set new point ok mg.getCenterX()" + img.getCenterX()
						+ ",mg.getCenterY()" + img.getCenterY());
			}
			// currTouchPoint.set(lastPointInfoBeforeTrashMode);
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
		public float SCREEN_MARGIN = 50;

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
//			SCREEN_MARGIN = width / 2;
		}

		private void getMetrics(Resources res)
		{

			DisplayMetrics metrics = res.getDisplayMetrics();
			// The DisplayMetrics don't seem to always be updated on screen
			// rotate, so we hard code a portrait
			// screen orientation for the non-rotated screen here...
			// this.displayWidth = metrics.widthPixels;
			// this.displayHeight = metrics.heightPixels;

			
			Log.e(TAG, "getMetrics() metrics.widthPixels:"+metrics.widthPixels+",metrics.heightPixels:"+metrics.heightPixels);
			this.displayWidth = (int)borderXofTrash;
			this.displayHeight = (int)borderYofTrash; 
//			this.displayWidth = 276 * 2;
//			this.displayHeight = (int) ((273 * 2) - SCREEN_MARGIN);

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
//			if (this.maxX < SCREEN_MARGIN)
//			{
//				cx = SCREEN_MARGIN;
//			}
//			else if (this.minX > displayWidth - SCREEN_MARGIN)
//			{
//				cx = displayWidth - SCREEN_MARGIN;
//			}
//
//			if (this.maxY > SCREEN_MARGIN)
//			{
//				cy = SCREEN_MARGIN;
//			}
//			else if (this.minY > displayHeight - SCREEN_MARGIN)
//			{
//				cy = displayHeight - SCREEN_MARGIN;
//			}

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
				if (this.maxX < SCREEN_MARGIN)
					cx = SCREEN_MARGIN;
				else if (this.minX > displayWidth - SCREEN_MARGIN)
					cx = displayWidth - SCREEN_MARGIN;
				if (this.maxY > SCREEN_MARGIN)
					cy = SCREEN_MARGIN;
				else if (this.minY > displayHeight - SCREEN_MARGIN)
					cy = displayHeight - SCREEN_MARGIN;
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
			// return setPos(newImgPosAndScale.getXOff(),
			// newImgPosAndScale.getYOff(),
			// newImgPosAndScale.getScaleAnisotropicX(),
			// newImgPosAndScale.getScaleAnisotropicY(), 0.0f);
		}

		/** Set the position and scale of an image in screen coordinates */
		private boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle)
		{

			float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
			float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY
					+ hs;
			if (newMinX > displayWidth - SCREEN_MARGIN || newMaxX < SCREEN_MARGIN
					|| newMinY > displayHeight - SCREEN_MARGIN || newMaxY < SCREEN_MARGIN)
			{
				Log.e(TAG, "!!!!!setPos() Position out of Display Screen");

				return false;
			}

			// bruce add
			// setX(centerX - (ws / 2));
			// setY(centerY - (hs / 2));
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
	}

}

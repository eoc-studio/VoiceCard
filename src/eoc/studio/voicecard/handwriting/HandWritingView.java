package eoc.studio.voicecard.handwriting;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.PaintUtility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HandWritingView extends View
{
	private static String TAG ="HandWritingView";
	
	private Paint paint = null;

	private Canvas canvas = null;

	private static Bitmap originalBitmap = null;

	public static Bitmap new1Bitmap = null;

	private static Bitmap new2Bitmap = null;

	private static Bitmap tempBitmap;

	public static Bitmap saveImage = null;

	private float clickX = 0, clickY = 0;

	private float startX = 0, startY = 0;

	private boolean isClear = false;

	private static int color = Color.BLACK; // 設置畫筆的顏色

	private static float strokeWidth = (float)PaintUtility.PEN_SIZE.SIZE_1;

	private static boolean isClearMode = false;
	
	
	private Path path;

	public HandWritingView(Context context)
	{

		this(context, null);
		init();
	}

	public HandWritingView(Context context, AttributeSet attrs, int defStyle)
	{

		super(context, attrs, defStyle);
		init();
	}

	public HandWritingView(Context context, AttributeSet attrs)
	{

		super(context, attrs);
		init();

	}

	private void init()
	{

		path = new Path();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		options.outWidth = PaintUtility.SKETTCHPAD.WIDTH;
		options.outHeight = PaintUtility.SKETTCHPAD.HEIGHT;

		Bitmap tempBitmap = Bitmap.createBitmap(PaintUtility.SKETTCHPAD.WIDTH,
				PaintUtility.SKETTCHPAD.HEIGHT, Bitmap.Config.ARGB_8888);
		// original backgroupd color
		// tempBitmap.eraseColor(android.graphics.Color.WHITE);

		// using backgroupd color
		tempBitmap.eraseColor(Color.TRANSPARENT);

		tempBitmap.setHasAlpha(true);
		
		// tempBitmap = BitmapFactory.decodeResource(getResources(),

		originalBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		originalBitmap.setHasAlpha(true);

		new1Bitmap = Bitmap.createBitmap(originalBitmap);
		
		new1Bitmap.setHasAlpha(true);
	}

	public void clear()
	{

		isClear = true;
		// recyclingResources.recycleBitmap(new1Bitmap);
		new2Bitmap = Bitmap.createBitmap(originalBitmap);
		new2Bitmap.setHasAlpha(true);
		invalidate();
	}

	public Bitmap saveImage()
	{

		if (saveImage == null) { return null; }
		return saveImage;
	}

	public void setImge()
	{

		saveImage = null;
	}

	public void setstyle(float strokeWidth)
	{

		Log.e(TAG, "setstyle() strokeWidth is "+strokeWidth);
		this.strokeWidth = strokeWidth;
	}

	public void setPenColor(int color){
		this.color = color;
	}
	
	public void enableEraser(){
	
		isClearMode = true;
	}
	
	public void disableEraser(){
		
		isClearMode = false;
	}
	
	/**
	 * 功能：完成畫板的相關操作
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{

		super.onDraw(canvas);
		canvas.clipRect(0, 0, PaintUtility.SKETTCHPAD.WIDTH, PaintUtility.SKETTCHPAD.HEIGHT);// 控制畫板的區域坐標(x,y,x+width,y+high);
		// canvas.drawColor(Color.argb(150, 120, 120, 0));//控制畫板的背景顏色
		// canvas.drawColor(R.color.deeppink);

		canvas.drawBitmap(HandWriting(new1Bitmap), 0, 0, null);
		canvas.drawPath(path, paint);
	}

	/**
	 * 功能：完成畫筆的操作，並返回bitmap對像
	 * 
	 * @param originalBitmap
	 * @return
	 */
	@SuppressLint("HandlerLeak")
	public Bitmap HandWriting(Bitmap originalBitmap)
	{

		if (isClear)
		{
			canvas = new Canvas(new2Bitmap);
		}
		else
		{
			canvas = new Canvas(originalBitmap);
		}
		paint = new Paint();

		if(isClearMode){
			paint.setColor(Color.TRANSPARENT); 
			paint.setXfermode(new PorterDuffXfermode(
	                Mode.CLEAR));
			Log.e(TAG, "HandWriting() paint.setXfermode(CLEAR)");
		}else{
			paint.setColor(this.color); 	// 設置畫筆的顏色
//			Log.e(TAG, "HandWriting() this.color is"+this.color);
//			
//			Log.e(TAG, "HandWriting() paint.getXfermode(); is"+paint.getXfermode());
			
			paint.setXfermode(null);
		}

		paint.setStyle(Style.STROKE);
//		Log.e(TAG, "HandWriting() strokeWidth is"+strokeWidth);
		paint.setStrokeWidth(this.strokeWidth);	//設置畫筆的粗細
//		paint.setStrokeWidth(penSize);		// 設置畫筆的粗細
		getMaskFilter(PaintUtility.PEN_TYPE.PLAIN_PEN); 
		paint.setAntiAlias(true);	// 抗鋸齒
		paint.setDither(true);
		paint.setFilterBitmap(true);
		paint.setSubpixelText(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		if (isClear) { return new2Bitmap; }
		return originalBitmap;
	}

	/**
	 * 功能：完成對畫筆路徑的操作,為了保證畫筆效果的光滑性，採用貝爾曲線法
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		startX = event.getX();
		startY = event.getY();
		Log.e(TAG, "onTouchEvent() startX "+startX+",startY "+startY);
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			touchDown(event);
			return true;
		case MotionEvent.ACTION_MOVE:
			touchMove(event);
			return true;
		case MotionEvent.ACTION_UP:
			touchUp(event);
			return true;
		default:
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 功能：手指點下屏幕時調用
	 * 
	 * @param event
	 */
	private void touchDown(MotionEvent event)
	{

		clickX = startX;
		clickY = startY;
		// path繪製的繪製起點
		path.moveTo(startX, startY);
		invalidate();
	}

	/**
	 * 功能：手指在屏幕上滑動時調用
	 * 
	 * @param event
	 */
	private void touchMove(MotionEvent event)
	{

		// 二次貝塞爾，實現平滑曲線；clickX, clickY為操作點，(clickX+startX)/2,
		// (clickY+startY)/2為終點
		path.quadTo(clickX, clickY, (clickX + startX) / 2, (clickY + startY) / 2);
		// 第二次執行時，第一次結束調用的坐標值將作為第二次調用的初始坐標值
		clickX = startX;
		clickY = startY;
		invalidate();
	}

	/**
	 * 功能：手指離開屏幕時調用
	 * 
	 * @param event
	 */
	private void touchUp(MotionEvent event)
	{

		// 鼠標彈起保存最後狀態
		canvas.drawPath(path, paint);
		// 重置繪製路線，即隱藏之前繪製的軌跡
		path.reset();
	}

	/**
	 * 功能：畫板界面離開是調用
	 */
	@Override
	protected void onDetachedFromWindow()
	{

		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
	}

	/**
	 * 功能：設置畫筆風格
	 * 
	 * @param mPaintType
	 * @return
	 */
	private MaskFilter getMaskFilter(int mPaintType)
	{

		MaskFilter maskFilter = null;
		switch (mPaintType)
		{
		case PaintUtility.PEN_TYPE.PLAIN_PEN:// 簽字筆風格
			maskFilter = null;
			break;
		case PaintUtility.PEN_TYPE.BLUR:// 鉛筆模糊風格
			maskFilter = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
			break;
		case PaintUtility.PEN_TYPE.EMBOSS:// 毛筆浮雕風格
			maskFilter = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
			break;
		case PaintUtility.PEN_TYPE.TS_PEN:// 透明水彩風格
			maskFilter = null;
			paint.setAlpha(50);
			break;
		default:
			maskFilter = null;
			break;
		}
		paint.setMaskFilter(maskFilter);
		return maskFilter;
	}
}

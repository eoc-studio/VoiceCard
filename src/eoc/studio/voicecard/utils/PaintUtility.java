package eoc.studio.voicecard.utils;

import android.graphics.Color;

public class PaintUtility
{

	private PaintUtility() {

	}
    public static final class SKETTCHPAD{
    	public static final int WIDTH = 700;
    	public static final int HEIGHT = 500;
    }
	public static final class ERASER_SIZE {
		public static final int SIZE_1 = 5;
		public static final int SIZE_2 = 10;
		public static final int SIZE_3 = 15;
		public static final int SIZE_4 = 20;
		public static final int SIZE_5 = 30;
	}

	public static final class PEN_SIZE {
		public static final int SIZE_1 = 5;
		public static final int SIZE_2 = 10;
		public static final int SIZE_3 = 15;
		public static final int SIZE_4 = 20;
		public static final int SIZE_5 = 30;
	}

	public static final class SHAP {
		public static final int CURV = 1;
		public static final int LINE = 2;
		public static final int RECT = 3;
		public static final int CIRCLE = 4;
		public static final int OVAL = 5;
		public static final int SQUARE = 6;
	}

	public static final class PATH {
		public static final String SAVE_PATH_jpg= "/sdcard/Smemo";
		public static final String SAVE_PATH_png = "/sdcard/Smemo/.Smemo";
	}

	public static final class PEN_TYPE {
		public static final int PLAIN_PEN = 1;
		public static final int ERASER = 2;
		public static final int BLUR = 3;
		public static final int EMBOSS = 4;
		public static final int TS_PEN = 5;
	}


	public static final class DEFAULT{
		public static final int PEN_COLOR = Color.BLACK;
		public static final int BACKGROUND_COLOR = Color.WHITE;
	}


	public static final int UNDO_STACK_SIZE = 20;

	public static final int COLOR_VIEW_SIZE = 60;//48
	
	public static final int LOAD_ACTIVITY = 1;

	public static final int COLOR1 = Color.argb(255, 0, 0, 0);   //black
	public static final int COLOR2 = Color.argb(255, 255, 255, 0);
	public static final int COLOR3 = Color.argb(255, 0, 0, 225);
	public static final int COLOR4 = Color.argb(255, 0, 255, 0);
	public static final int COLOR5 = Color.argb(255, 160, 32, 240);
	public static final int COLOR6 = Color.argb(255, 0, 0, 255);
	public static final int COLOR7 = Color.argb(255, 255, 0, 0);
	public static final int COLOR8 = Color.argb(255, 0, 255, 255);
	public static final int COLOR9 = Color.argb(255, 255, 255, 255);
	public static final int COLOR10 = Color.argb(255, 182, 42, 42);
	public static final int COLOR11= Color.argb(255, 255, 125, 64);
	public static final int COLOR12= Color.argb(255, 124, 252, 0);
	public static final int COLOR13= Color.argb(255, 210, 105, 30);
	
	public enum PEN_SIZE_ENUM
	{

		SIZE_ONE, SIZE_TWO, SIZE_THREE

	}

}
package com.game.forcesofnature;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,SensorEventListener  {

	private GameThread mThread;
	private Context mContext;
	private Activity mActivity;
	private boolean mThreadIsRunning = false;
	
	private Paint mPaintWhite;
	private Paint mPaintRed;
	private Paint mPaintYellow;
	private Paint mPaintBlue;
	
	
	private Sensor mAccelerometer;
	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private WakeLock mWakeLock;
	private float[] mSensorX;
	private float[] mSensorY;
	private long mSensorTimeStamp;
	private long mCpuTimeStamp;
	
	/**Screen metrics**/
	private float mScreenCentreX;
	private float mScreenCentreY;
	private float mScreenRatio;
	private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;
	private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;
	private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;

	/*Obstacle metrics*/
	private static final int OBST_RECT_WIDTH = 80;
	private static final int OBST_RECT_HEIGHT = 40;
	
	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		mActivity = (Activity) context;
		
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
        
        initialiseVariables();
        
        /**We create the thread.
         * The thread will be started in surfaceCreated()
         */
        mThread = new GameThread(holder, mContext, new Handler() {
        	@Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true); 
    }
	
	private void initialiseVariables(){
		mPaintWhite = new Paint();
		mPaintWhite.setColor(0xFFFFFFFF);
		mPaintWhite.setStrokeWidth(1);
		mPaintRed = new Paint();
		mPaintRed.setColor(0xFFCC0000);
		mPaintRed.setStrokeWidth(1);
		mPaintYellow = new Paint();
		mPaintYellow.setColor(0xFFFFFF00);
		mPaintYellow.setStrokeWidth(1);
		mPaintBlue = new Paint();
		mPaintBlue.setColor(0xFF0000FF);
		mPaintBlue.setStrokeWidth(1);
		
		
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        mScreenRatio = metrics.densityDpi / 160f;
        
        mScreenCentreX = metrics.widthPixels / 2f;
        
        switch (metrics.densityDpi) {
        	case DisplayMetrics.DENSITY_HIGH:
        		mScreenCentreY = (metrics.heightPixels - HIGH_DPI_STATUS_BAR_HEIGHT) / 2f;
        		break;
        	case DisplayMetrics.DENSITY_MEDIUM:
        		mScreenCentreY = (metrics.heightPixels - MEDIUM_DPI_STATUS_BAR_HEIGHT) / 2f;
        		break;
        	case DisplayMetrics.DENSITY_LOW:
        		mScreenCentreY = (metrics.heightPixels - LOW_DPI_STATUS_BAR_HEIGHT) / 2f;
        		break;
        	default:
        		mScreenCentreY = (metrics.heightPixels - MEDIUM_DPI_STATUS_BAR_HEIGHT) / 2f;
        		break;
        }
        
        mSensorX = new float[10];
        mSensorX[0] = 0f;
        mSensorX[1] = 0f;
        mSensorX[2] = 0f;
        mSensorX[3] = 0f;
        mSensorX[4] = 0f;
        mSensorX[5] = 0f;
        mSensorX[6] = 0f;
        mSensorX[7] = 0f;
        mSensorX[8] = 0f;
        mSensorX[9] = 0f;
        mSensorY = new float[10];
        mSensorY[0] = 0f;
        mSensorY[1] = 0f;
        mSensorY[2] = 0f;
        mSensorY[3] = 0f;
        mSensorY[4] = 0f;
        mSensorY[5] = 0f;
        mSensorY[6] = 0f;
        mSensorY[7] = 0f;
        mSensorY[8] = 0f;
        mSensorY[9] = 0f;
    }
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mWakeLock.acquire();
        mThread.setRunning(true);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        if (!mThreadIsRunning){
        	mThread.start();
        } else {
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
        mThread.setRunning(false);
        mSensorManager.unregisterListener(this);
        mWakeLock.release();
        while (retry) {
            try {
                mThread.join();
                retry = false;
                setThreadIsRunning(false);
            } catch (InterruptedException e) {
            }
        }	
	}
	
	private boolean getThreadIsRunning(){
		return mThreadIsRunning;
	}
	
	
	private void setThreadIsRunning(boolean is){
		mThreadIsRunning = is;
	}
	
	public GameThread getThread() {
        return mThread;
    }
	
	public Activity getPlayActivity(){
		return mActivity;
	}
	
	public void setPlayActivity(Activity act){
		mActivity = act;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
		    return;
	    }
		/*
         * record the accelerometer data, the event's timestamp as well as
         * the current time. The latter is needed so we can calculate the
         * "present" time during rendering. In this application, we need to
         * take into account how the screen is rotated with respect to the
         * sensors (which always return data in a coordinate space aligned
         * to with the screen in its native orientation).
         */

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                addSensorValue(event.values[0],-event.values[1]);
            	break;
            case Surface.ROTATION_90:
            	addSensorValue(-event.values[1],event.values[0]);
            	break;
            case Surface.ROTATION_180:
            	addSensorValue(-event.values[0],-event.values[1]);
            	break;
            case Surface.ROTATION_270:
            	addSensorValue(event.values[1],-event.values[0]);
            	break;
        }
        mSensorTimeStamp = event.timestamp;
        mCpuTimeStamp = System.nanoTime();
	}
	
	private void addSensorValue(float x, float y){
		int length = mSensorX.length;
		int i = 0;
		while (i < length - 1){
			mSensorX[i] = mSensorX[i+1];
			mSensorY[i] = mSensorY[i+1];
			i += 1;
		}
		mSensorX[9] = x;
		mSensorY[9] = y;
	}

	public class GameThread extends Thread {
		
		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;
        private Canvas mCanvas;
        private boolean mRun = false;
         
        /**Variable and constants related to the state of the current game**/
        private int mRunningMode;
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        public static final int STATE_INSTRUCTIONS = 6;
        public static final int STATE_PAUSEINSTRUCTIONS = 7;
        
        /**Variables for the user circle**/
        private float mCircleCentreX = 0;
        private float mCircleCentreY = 0;
        private int mCircleRadius = 20;
        
        /**Variables for the red circles**/
        private float[] mRedObstacleX;
        private float[] mRedObstacleY;
//        private float[] mRedObstacleRadii;
        private float[] mRedObstacleXVariation;
        
        /**Variables for collision**/
        private boolean[] mCollidedCircles;
        
        /**Variables for winning**/
        private boolean mTarget = false;
        
		public GameThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            
            /**shouldn't be in here?**/
            mRunningMode = STATE_RUNNING;
            
            initialiseVariables();
        }
		
		private void initialiseVariables(){
			mRedObstacleX = new float[10];
	        mRedObstacleX[0] = 50f;
	        mRedObstacleX[1] = 100f;
	        mRedObstacleX[2] = 160f;
	        mRedObstacleX[3] = 10f;
	        mRedObstacleX[4] = 70f;
	        mRedObstacleX[5] = 210f;
	        mRedObstacleX[6] = 90f;
	        mRedObstacleX[7] = 120f;
	        mRedObstacleX[8] = 170f;
	        mRedObstacleX[9] = 30f;
	        
	        mRedObstacleY = new float[10];
	        mRedObstacleY[0] = 100f;
	        mRedObstacleY[1] = 200f;
	        mRedObstacleY[2] = 300f;
	        mRedObstacleY[3] = 400f;
	        mRedObstacleY[4] = 500f;
	        mRedObstacleY[5] = 600f;
	        mRedObstacleY[6] = 700f;
	        mRedObstacleY[7] = 800f;
	        mRedObstacleY[8] = 900f;
	        mRedObstacleY[9] = 1000f;
	        
	        mRedObstacleXVariation = new float[10];
	        mRedObstacleXVariation[0] = 70f;
	        mRedObstacleXVariation[1] = 200f;
	        mRedObstacleXVariation[2] = 90f;
	        mRedObstacleXVariation[3] = 10f;
	        mRedObstacleXVariation[4] = 10f;
	        mRedObstacleXVariation[5] = 10f;
	        mRedObstacleXVariation[6] = 10f;
	        mRedObstacleXVariation[7] = 10f;
	        mRedObstacleXVariation[8] = 10f;
	        mRedObstacleXVariation[9] = 10f;
	        
	        mCircleCentreX = mCircleRadius;
	        mCircleCentreY = mCircleRadius;
	        
	        mCollidedCircles = new boolean[10];
	        mCollidedCircles[0] = false;
	        mCollidedCircles[1] = false;
	        mCollidedCircles[2] = false;
	        mCollidedCircles[3] = false;
	        mCollidedCircles[4] = false;
	        mCollidedCircles[5] = false;
	        mCollidedCircles[6] = false;
	        mCollidedCircles[7] = false;
	        mCollidedCircles[8] = false;
	        mCollidedCircles[9] = false;
	     }
		
		@Override
        public void run() {
			while (mRun) {
            	try {
                    mCanvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mRunningMode == STATE_RUNNING){
                        	updatePhysics();
                        }
                        doDraw(mCanvas);
                    }
                } finally {
                    if (mCanvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
                }
            }
        }
		
		public void setRunning(boolean b) {
			/**This method sets the mRun variable
			 * which is used to make sure the thread is only active between the
			 * phases surfaceCreated() and surfaceDestroyed() of our 
			 * GameView SurfaceHolder cycle
			 */
            mRun = b;
        }
		
		/**the methods below take care of drawing on the screen**/
		private void doDraw(Canvas canvas) {
			/**This method draws the grid, numbers, score, next number
			 * and arrows on the canvas
			 */
			if(mCanvas!=null){
				mCanvas.save();
				mCanvas.restore();
				mCanvas.drawColor(0,PorterDuff.Mode.CLEAR);
				drawTarget();
<<<<<<< Updated upstream
				drawCollisions();
				drawOtherRectangles();
				drawUserCircle();
=======
				drawOtherRectangles();
				drawUserCircle();
				drawCollisions();
>>>>>>> Stashed changes
				canvas.save();
			}
		}
		
		private void drawUserCircle(){
			mCanvas.drawCircle(mCircleCentreX, mCircleCentreY, mCircleRadius, mPaintWhite);
		}
		
		private void drawOtherRectangles(){
			//draws the red rectangles
			int length = mRedObstacleX.length;
			int i = 0;
			while (i < length){
				mCanvas.drawRect(mRedObstacleX[i],mRedObstacleY[i] , mRedObstacleX[i] + OBST_RECT_WIDTH , mRedObstacleY[i] + OBST_RECT_HEIGHT, mPaintYellow);
				i +=1 ;
			}
		}
		
		private void drawCollisions(){
			// the part that changes red balls to have a yellow background.. MAYBE have a pop-up
			int length = mCollidedCircles.length;
			int i = 0;
			while (i < length){
				if (mCollidedCircles[i]){
<<<<<<< Updated upstream
					
=======
					//LOSE
					mCanvas.drawCircle(mCircleCentreX, mCircleCentreY, mCircleRadius, mPaintBlue);
					mRun = false;
>>>>>>> Stashed changes
				}
				i += 1;
			}
		}
		
		private void setWinningDialogBox() {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.winning_title);
			builder.setMessage(R.string.winning_dialog_message);
			builder.setPositiveButton(R.string.yes_share, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   showShareOptions();
			        	   dialog.dismiss();
			           }

			       });
			builder.setNegativeButton(R.string.no_dont_share, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
			AlertDialog dialog = builder.create();
			dialog.show();
		}

		private void showShareOptions() {
			//TODO
		}

		private void drawTarget(){
			//this is where the target becomes white from red!!
			if (mTarget){
				mCanvas.drawRect(mScreenCentreX * 2 - mCircleRadius * 2, mScreenCentreY * 2 - mCircleRadius * 2, mScreenCentreX * 2, mScreenCentreY * 2, mPaintWhite);
				mRunningMode = STATE_WIN;
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						setWinningDialogBox();
						
					}
				});
			} else {
				mCanvas.drawRect(mScreenCentreX * 2 - mCircleRadius * 2, mScreenCentreY * 2 - mCircleRadius * 2, mScreenCentreX * 2, mScreenCentreY * 2, mPaintRed);
			}
		}
		
		/**The methods below are helper methods for drawing on the screen**/
		private void updatePhysics() {
			
			int length = mSensorX.length;
			int i = 0;
			float totalSensorX = 0f;
			float totalSensorY = 0f;
			while (i < length){
				totalSensorX += mSensorX[i] * 1f;
				totalSensorY += mSensorY[i] * 1f;
				i += 1;
			}
			float averageSensorX = totalSensorX / length;
			float averageSensorY = totalSensorY / length;
			mCircleCentreX = mCircleCentreX - averageSensorX;
			if (mCircleCentreX < mCircleRadius){
				mCircleCentreX = mCircleRadius;
			} else if (mCircleCentreX > mScreenCentreX * 2 - mCircleRadius){
				mCircleCentreX = mScreenCentreX * 2 - mCircleRadius;
			}
			mCircleCentreY = mCircleCentreY - averageSensorY;
			if (mCircleCentreY < mCircleRadius){
				mCircleCentreY = mCircleRadius;
			} else if (mCircleCentreY > mScreenCentreY * 2 - mCircleRadius){
				mCircleCentreY = mScreenCentreY * 2 - mCircleRadius;
			}
			
			length = mRedObstacleX.length;
			i = 0;
			while (i < length){
				mRedObstacleX[i] += mRedObstacleXVariation[i];
<<<<<<< Updated upstream
				if (mRedObstacleX[i] - mRedObstacleY[i] < 1f || mRedObstacleX[i] + mRedObstacleY[i] > mScreenCentreX * 2){
=======
				if (mRedObstacleX[i] < 1f || mRedObstacleX[i]+OBST_RECT_WIDTH  > mScreenCentreX * 2){
>>>>>>> Stashed changes
					mRedObstacleXVariation[i] = mRedObstacleXVariation[i] * (-1f);
				}
				i += 1;
			}
			
			checkCollision();
			checkTarget();
			
		}
		
		private void checkCollision(){
			int length = mRedObstacleX.length;
			int i = 0;
			while (i < length){
				float distX = Math.min((Math.abs(mRedObstacleX[i] - mCircleCentreX)), (Math.abs(mRedObstacleX[i] + OBST_RECT_WIDTH - mCircleCentreX)));
				float distY = Math.min((Math.abs(mRedObstacleY[i] - mCircleCentreY)), (Math.abs(mRedObstacleY[i] + OBST_RECT_HEIGHT -mCircleCentreY)));
				
				if ((distX <= mCircleRadius) && (distY <= mCircleRadius)) {
					mCollidedCircles[i] = true;
					//mCanvas.drawCircle(mCircleCentreX, mCircleCentreY, mCircleRadius, mPaintBlue);
					break;
				}
				/*float centresDistanceSquared = (mCircleCentreX - mRedObstacleX[i]) * (mCircleCentreX - mRedObstacleX[i]) + (mCircleCentreY - mRedObstacleY[i]) * (mCircleCentreY - mRedObstacleY[i]);  
				float radiiDistanceSquared = (mCircleRadius) * (mCircleRadius);
				if (centresDistanceSquared <= radiiDistanceSquared){
					mCollidedCircles[i] = true;
				}*/
				i += 1;
			}
		}
		
		private void checkTarget(){
			if (mCircleCentreX > mScreenCentreX * 2 - mCircleRadius * 2 && mCircleCentreY > mScreenCentreY * 2 - mCircleRadius * 2){
				mTarget = true;
			}
		}
		
	}
}
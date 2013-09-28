package com.game.forcesofnature;

import com.game.forcesofnature.GameSurfaceView.GameThread;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

public class Home extends Activity {
    
	private GameSurfaceView mGameView;
	private GameThread mGameThread;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        createSurfaceView(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
	
	@Override
    protected void onResume(){
    	super.onResume();
    }
	
	private void createSurfaceView(Bundle savedInstanceState){
		setContentView(R.layout.main);
        
		mGameView = (GameSurfaceView) findViewById(R.id.gameview);
		mGameThread = mGameView.getThread();
        
        mGameView.setPlayActivity(this);
    }
	
	@Override
    protected void onStop() {
        super.onStop();
    }
	
	@Override
    protected void onRestart(){
    	super.onRestart();
    	createSurfaceView(null);
     }
	
	
}
package com.game.forcesofnature.level2;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.game.forcesofnature.R;
import com.game.forcesofnature.level2.LevelTwoSurfaceView.GameThread;

public class LevelTwoActivity extends Activity {
    
	private GameThread mGameThread;
	private LevelTwoSurfaceView mGameView;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        	createSurfaceView(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
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
		setContentView(R.layout.main_level_2);
        
		mGameView = (LevelTwoSurfaceView) findViewById(R.id.gameview);
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
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.exit_dialog_message);
		builder.setPositiveButton(R.string.yes_end, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   finish();
		           }
		       });
		builder.setNegativeButton(R.string.no_dont_end, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
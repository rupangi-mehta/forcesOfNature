package com.game.forcesofnature;


import java.util.HashMap;

import com.game.forcesofnature.level1.Home;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    

	private void setListeners() {
		Button playButton= (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent intent = new Intent(MainActivity.this, Home.class);
		    	startActivity(intent);
		    }
		});
		
		Button helpButton= (Button) findViewById(R.id.howToPlayButton);
	    helpButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Intent intent = new Intent(MainActivity.this, HelpGame.class);
		    	startActivity(intent);
		    }  
	    });
	}
    
}

package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *The splash/main page first seen when the app is launched
 *Provides buttons to play(move to the instructions page) or register(not yet implemented)
 *@author Eric Kuxhausen
 */
public class MainActivity extends Activity implements OnClickListener, OnSharedPreferenceChangeListener{
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        RpcClient.getInstance(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        //add default preferences if no preferences exist
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        if(!settings.contains("Theme")){
        	settings.edit().putString("Theme", getString(R.string.theme_preference));
        	settings.edit().commit();
        }	
        if(!settings.contains("GraphicsMode")){
        	settings.edit().putString("GraphicsMode", getString(R.string.graphics_mode_preference));
        	settings.edit().commit();
        }
        
        load();
    }
    
    /** Load performs many duties normally found in onCreate, but also needed when reloading after a Theme change 
     */
    public void load()
    {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	Boolean gameshow = false;
        //Load layout
        if(settings.getString("Theme", "").equals("game_show")){
        	gameshow= true;
        	setContentView(R.layout.gameshow_main);
        }
        else
        	setContentView(R.layout.main);
        
        //create the typeface to be used by all app text
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "archer_medium_pro.otf");
        
        Button playButton = (Button) findViewById(R.id.playButtonMain);
        playButton.setOnClickListener(this);
        if(gameshow)	
        	playButton.setTypeface(tf);
  
        Button loginButton = (Button) findViewById(R.id.loginButtonMain);
        loginButton.setOnClickListener(this);
        if(gameshow)
        	loginButton.setTypeface(tf);
        
        Button settingsButton = (Button) findViewById(R.id.settingsButtonMain);
        settingsButton.setOnClickListener(this);
        if(gameshow)
        	settingsButton.setTypeface(tf);
        
    }
    
	public void onClick(View v) {
    	switch(v.getId()){
       		case R.id.playButtonMain:  
       			if(RpcClient.getInstance(this).getOption("username")!=null)
       				startActivity(new Intent(this, InstructionsActivity.class)); 
   				else
   					createDialog("Wait!", "Please register or login before playing.", 
								new DialogInterface.OnClickListener(){
				 	           public void onClick(DialogInterface dialog, int id) {
					                dialog.dismiss();
					           }});
				break;
       		case R.id.settingsButtonMain: 
       			startActivity(new Intent(this, DevelopmentPreferencesActivity.class)); 
       			break;
       		case R.id.loginButtonMain: 
       			startActivity(new Intent(this, LoginActivity.class));
       			break;
    	}
	}
	
	
	public void createDialog(String title, String message, DialogInterface.OnClickListener click ){
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message)
    		   .setTitle(title)
    	       .setNeutralButton("Ok", click);
    	AlertDialog alert = builder.create();
    	alert.show();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals("Theme"))
		{
			//Reload layout
			load();
		}	
	}
}

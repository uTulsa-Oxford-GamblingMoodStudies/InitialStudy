package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
public class MainActivity extends Activity implements OnClickListener{
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        RpcClient.getInstance(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        //Load layout from main.xml
        setContentView(R.layout.main);
        
        //create the typeface to be used by all app text
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "archer_medium_pro.otf");
        
        Button playButton = (Button) findViewById(R.id.playButtonMain);
        playButton.setOnClickListener(this);
        playButton.setTypeface(tf);
  
        Button registerButton = (Button) findViewById(R.id.registerButtonMain);
        registerButton.setOnClickListener(this);
        registerButton.setTypeface(tf);
        
//        if(RpcClient.getInstance(this).getOption("username")==null){
        	playButton.setText("Login");
        	registerButton.setText("Register");
//        }
//        else{
        	playButton.setText("Play");
        	registerButton.setText("Logout");
//        }
    }
    
	public void onClick(View v) {
		
		
/*		if(RpcClient.getInstance(this).getOption("username")!=null){
*/	    	switch(v.getId()){
	       		case R.id.playButtonMain:  startActivity(new Intent(this, InstructionsActivity.class)); break;
/*	       		case R.id.registerButtonMain: 
	       			RpcClient.getInstance(this).deleteOptions(this, "username","password");
	       			Button playButton = (Button) findViewById(R.id.playButtonMain);
	       	        Button registerButton = (Button) findViewById(R.id.registerButtonMain);
	       			playButton.setText("Login");
	            	registerButton.setText("Register");
	       			break;
	    	}
		}else{
			switch(v.getId()){
       			case R.id.playButtonMain:  startActivity(new Intent(this, LoginActivity.class)); break;
       			case R.id.registerButtonMain: startActivity(new Intent(this, RegistrationActivity.class));
			}
*/		}
	}
}

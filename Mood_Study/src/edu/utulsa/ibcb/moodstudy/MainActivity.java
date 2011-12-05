package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.content.Intent;
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
        
        RpcClient.getInstance(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
        //Load layout from main.xml
        setContentView(R.layout.main);
        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        
        if(RpcClient.getInstance(this).getOption("username")==null){
        	playButton.setText("Test OpenGL");
        	registerButton.setText("Register");
        }
        else{
        	playButton.setText("Play");
        	registerButton.setText("Logout");
        }
    }
    
    
	public void onClick(View v) {
		if(RpcClient.getInstance(this).getOption("username")!=null){
	    	switch(v.getId()){
	       		case R.id.playButton:  startActivity(new Intent(this, InstructionsActivity.class)); break;
	       		case R.id.registerButton: break;
	    	}
		}else{
			switch(v.getId()){
       			case R.id.playButton:  startActivity(new Intent(this, OpenGLTestActivity.class)); break;
       			case R.id.registerButton: startActivity(new Intent(this, RegistrationActivity.class));;
			}
		}
	}
}

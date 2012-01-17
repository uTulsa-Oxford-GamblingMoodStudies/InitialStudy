package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;
import edu.utulsa.ibcb.moodstudy.opengl.CupEnvironment;
import edu.utulsa.ibcb.moodstudy.opengl.DiceRenderer;
import edu.utulsa.ibcb.moodstudy.opengl.DiceRollEnvironment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class DiceGameActivity extends Activity implements OnClickListener {
	
	DiceRenderer diceview;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        
        
        try{
	        GLSurfaceView drawSurface = new GLSurfaceView(this);
	        drawSurface.setOnClickListener(this);
	        
	        DiceRenderer.setContext(this);
	        diceview=DiceRenderer.getInstance();
	        
	        drawSurface.setRenderer(diceview);
	        setContentView(drawSurface);
	        
	        Intent mintent = getIntent();
	        int actual_roll = mintent.getIntExtra("actual", 6);
	        int force_level = 1;
	        
	        ((DiceRollEnvironment)diceview.getEnvironment("roll")).setupPlay(force_level, actual_roll);
	        
	        diceview.setEnvironment("cup");
	        
	        
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    int clicks = 0;

	public void onClick(View arg0) {
		
		if(clicks == 0){
			((CupEnvironment)diceview.getEnvironment("cup")).throwDie();
	        
			try{
		        Thread.sleep(1000);
			}catch(Exception e){
				e.printStackTrace();
			}
	        
	        diceview.setEnvironment("roll");
	        
			clicks++;
		}else{
			startActivity(new Intent(this,GamePromptActivity.class));
			clicks=0;
		}
	}
	
	public void onBackPressed() {
		startActivity(new Intent(this,FinalSurveyActivity.class));
	}
    
}
package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;
import edu.utulsa.ibcb.moodstudy.opengl.DiceRenderer;

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
import android.view.Window;
import android.view.WindowManager;

public class DiceGameActivity extends Activity {
	
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
	        diceview=new DiceRenderer(this);
	        drawSurface.setRenderer(diceview);
	        setContentView(drawSurface);
	        
	        Intent mintent = getIntent();
	        int t = mintent.getIntExtra("actual", 6);
	        
	        diceview.showNumber(t);
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
}
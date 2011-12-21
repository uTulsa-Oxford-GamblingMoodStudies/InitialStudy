package edu.utulsa.ibcb.moodstudy;

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
import android.widget.ScrollView;
import android.widget.TextView;

/**
 *Displays game instructions
 *Provides button for user to continue to the game's initial survey
 *@author Eric Kuxhausen
 */
public class InstructionsActivity extends Activity implements OnClickListener{
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //Load layout from instructions.xml
        setContentView(R.layout.instructions);
        
        //create the typeface to be used by all app text
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "archer_medium_pro.otf");
        
        TextView instructionView = (TextView) findViewById(R.id.instructionsTextView);
        instructionView.setTypeface(tf);
        //changing the typeface for this much text messes with the entire activity layout spacing
        
        
        Button continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        continueButton.setTypeface(tf);
        
    }

	public void onClick(View v) {
    	switch(v.getId()){
       		case R.id.continueButton:  startActivity(new Intent(this,InitialSurveyActivity.class)); break;
    	}
	}
}

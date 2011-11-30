package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


/**
*@author Eric Kuxhausen
*/
public class InitialSurveyActivity extends Activity implements OnClickListener{
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.initial_survey);
        
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
    }

	public void onClick(View v) {
    	switch(v.getId()){
       		case R.id.playButton:  startActivity(new Intent(this,GamePromptActivity.class)); break;
    	}
	}
	
}
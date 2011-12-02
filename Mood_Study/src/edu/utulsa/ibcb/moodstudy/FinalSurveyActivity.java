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
 *Survey user is presented upon game completion
 *@author Eric Kuxhausen
 */
public class FinalSurveyActivity extends Activity implements OnClickListener{
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //Load layout from final_survey.xml
        setContentView(R.layout.final_survey);
        
        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);
        
        Button replayButton = (Button) findViewById(R.id.replayButton);
        replayButton.setOnClickListener(this);
        
    }

	public void onClick(View v) {
		switch(v.getId()){
			case R.id.exitButton: finish(); break;
			case R.id.replayButton: startActivity(new Intent(this, GamePromptActivity.class));
		}
	}
	
}
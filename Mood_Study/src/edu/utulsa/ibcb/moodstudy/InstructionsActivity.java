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
        setContentView(R.layout.instructions);
        
        Button continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
    }

	public void onClick(View v) {
    	switch(v.getId()){
       		case R.id.continueButton:  startActivity(new Intent(this,InitialSurveyActivity.class)); break;
    	}
	}
}

package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class GameResultsActivity extends Activity implements OnClickListener{

	private boolean won;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //Load layout from final_survey.xml
		won = savedInstanceState.getBoolean("won");
		if(won)
        	setContentView(R.layout.gameshow_win);
		else
			setContentView(R.layout.gameshow_lose);
		
		Button replayButton = (Button) findViewById(R.id.replayButton);
        replayButton.setOnClickListener(this);
			
	}
	
	public void onClick(View v) {
    	if(won) //TODO provide more options to winner
    		startActivity(new Intent(this, GamePromptActivity.class));
    	else
    		startActivity(new Intent(this, GamePromptActivity.class));
	}

}

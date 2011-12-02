package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

/**
 *Prompts user to try to roll for a certain number.
 *Ensures user is aware of that number by asking user to click on the corresponding prize to continue
 *@author Eric Kuxhausen
 */
public class GamePromptActivity extends Activity implements OnClickListener {
	
	private int promptedRoll;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Load layout from game_prompt.xml
        setContentView(R.layout.game_prompt);
        
        Button Prize1Button = (Button) findViewById(R.id.prize1Button);
        Prize1Button.setOnClickListener(this);
        Button Prize2Button = (Button) findViewById(R.id.prize2Button);
        Prize2Button.setOnClickListener(this);
        Button Prize3Button = (Button) findViewById(R.id.prize3Button);
        Prize3Button.setOnClickListener(this);
        Button Prize4Button = (Button) findViewById(R.id.prize4Button);
        Prize4Button.setOnClickListener(this);
        Button Prize5Button = (Button) findViewById(R.id.prize5Button);
        Prize5Button.setOnClickListener(this);
        Button Prize6Button = (Button) findViewById(R.id.prize6Button);
        Prize6Button.setOnClickListener(this);
        
        
        
        
        
        
        setDicePrompt();
        Log.d("network", "loaded");
        ImageView PromptImage = (ImageView)findViewById(R.id.dicePromptImageView);
        switch(promptedRoll){
			case 1:  PromptImage.setImageDrawable(getResources().getDrawable(R.drawable.d1)); break;
			case 2:  PromptImage.setImageDrawable(getResources().getDrawable(R.drawable.d2)); break;
			case 3:  PromptImage.setImageDrawable(getResources().getDrawable(R.drawable.d3)); break;
			case 4:  PromptImage.setImageDrawable(getResources().getDrawable(R.drawable.d4)); break;
			case 5:  PromptImage.setImageDrawable(getResources().getDrawable(R.drawable.d5)); break;
			case 6:  PromptImage.setImageDrawable(getResources().getDrawable(R.drawable.d6)); break;
        }
        
        
    }

	public void onClick(View v) {
		Intent iDiceGame = new Intent(this, DiceGameActivity.class);
		iDiceGame.putExtra("prompt", promptedRoll);
		switch(v.getId()){
   			case R.id.prize1Button: startActivity(iDiceGame); break;
   			case R.id.prize2Button: startActivity(iDiceGame); break;
   			case R.id.prize3Button: startActivity(iDiceGame); break;
   			case R.id.prize4Button: startActivity(iDiceGame); break;
   			case R.id.prize5Button: startActivity(iDiceGame); break;
   			case R.id.prize6Button: startActivity(iDiceGame); break;
		}
		Log.d("input", "button pressed");
	}
	
	@Override
	public void onBackPressed() {
	    startActivity(new Intent(this,FinalSurveyActivity.class));
	}
	
	//TODO pull value from network?
	/**
	 * @return dice face value{1,2,3,4,5,6}
	 */
	private void setDicePrompt()
	{
		promptedRoll= (int) (1+Math.floor(6*Math.random()));
	}

}

package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class GameResultsActivity extends Activity implements OnClickListener {

	private boolean won;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		Boolean gameshow = false;
			setContentView(R.layout.final_survey);
		
		// Load layout from final_survey.xml
		won = getIntent().getExtras().getBoolean("won");
		if (won){
			if (settings.getString("Theme", "").equals("game_show")) {
				gameshow = true;
				setContentView(R.layout.gameshow_win);
			} else
				setContentView(R.layout.win);
		}
		else{
			if (settings.getString("Theme", "").equals("game_show")) {
				gameshow = true;
				setContentView(R.layout.gameshow_lose);
			} else
				setContentView(R.layout.lose);
		}

		Button replayButton = (Button) findViewById(R.id.replayButton);
		replayButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.exitButton:	startActivity(new Intent(this, FinalSurveyActivity.class)); break;
			case R.id.replayButton:	startActivity(new Intent(this, GamePromptActivity.class));
		}
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, FinalSurveyActivity.class));
	}

}

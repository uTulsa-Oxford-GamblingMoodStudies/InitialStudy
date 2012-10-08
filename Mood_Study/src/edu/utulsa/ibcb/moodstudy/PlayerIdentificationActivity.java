package edu.utulsa.ibcb.moodstudy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Displays game instructions Provides button for user to continue to the game's
 * initial survey
 * 
 * @author Eric Kuxhausen
 */
public class PlayerIdentificationActivity extends Activity implements
		OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Load layout
		setContentView(R.layout.player_identification);

		Button continueButton = (Button) findViewById(R.id.continueButton);
		continueButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continueButton:
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			EditText ed = (EditText) findViewById(R.id.pidInput);
			if (ed.getText() != null) {
				Editor editor = settings.edit();
				editor.putString("username", ed.getText().toString());
				editor.commit();
			}
			if (!settings.getString("username", "").equals("")) {
				startActivity(new Intent(this, RegistrationSurveyActivity.class));
			}
			break;
		}
	}
}

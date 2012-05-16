package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Initial survey of mood before playing the game
 * 
 * @author Eric Kuxhausen
 */
public class InitialSurveyActivity extends Activity implements OnClickListener {

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
		setContentView(R.layout.initial_survey);

		Button playButton = (Button) findViewById(R.id.playButton);
		playButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent iNext = new Intent(this, GamePromptActivity.class);

		int lucky = ((SeekBar) findViewById(R.id.moodSeekBar)).getProgress();

		try {
			Integer session_id = RpcClient.getInstance(this)
					.startSession(lucky);

			RpcClient.getInstance(this).setSession(session_id);

			// TODO pass survey results before leaving
			switch (v.getId()) {
			case R.id.playButton:
				startActivity(iNext);
				break;
			}
		} catch (XMLRPCException xrpc) {
			xrpc.printStackTrace();

			StackTraceElement[] stack = xrpc.getStackTrace();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Error:" + xrpc.getMessage() + "\nIn:"
							+ stack[stack.length - 1].getClassName())
					.setTitle("Error")
					.setNeutralButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									InitialSurveyActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

}
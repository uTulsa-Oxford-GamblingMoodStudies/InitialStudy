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
import android.widget.TextView;

/**
 * Questionnare of mood before playing the game
 * 
 * @author Eric Kuxhausen
 */
public class RegistrationSurveyActivity extends Activity implements
		OnClickListener {

	private int questionNumber = 0;
	private int[] responses = new int[3];
	TextView question;

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
		setContentView(R.layout.registration_survey);

		Button nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);
		Button previousButton = (Button) findViewById(R.id.previousButton);
		previousButton.setOnClickListener(this);

		question = (TextView) findViewById(R.id.instructionTextView);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previousButton:
			questionNumber--;
			question.setText("Question " + (questionNumber + 1));
			if (questionNumber < 0)
				onBackPressed();
			break;
		case R.id.nextButton:
			questionNumber++;
			question.setText("Question " + (questionNumber + 1));
			if (questionNumber == responses.length)
				onBackPressed();
		}

		/*
		 * int lucky = ((SeekBar) findViewById(R.id.moodSeekBar)).getProgress();
		 * 
		 * 
		 * Intent iNext = new Intent(this, GamePromptActivity.class);
		 * iNext.putExtra("luckyFeeling", lucky);
		 * 
		 * try { Integer session_id = RpcClient.getInstance(this)
		 * .startSession(lucky);
		 * 
		 * RpcClient.getInstance(this).setSession(session_id);
		 * 
		 * // TODO pass survey results before leaving switch (v.getId()) { case
		 * R.id.doneButton: startActivity(iNext); break; } } catch
		 * (XMLRPCException xrpc) { xrpc.printStackTrace();
		 * 
		 * StackTraceElement[] stack = xrpc.getStackTrace();
		 * 
		 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 * builder.setMessage( "Error:" + xrpc.getMessage() + "\nIn:" +
		 * stack[stack.length - 1].getClassName()) .setTitle("Error")
		 * .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		 * public void onClick(DialogInterface dialog, int id) {
		 * RegistrationSurveyActivity.this.finish(); } }); AlertDialog alert =
		 * builder.create(); alert.show(); }
		 */
	}

}

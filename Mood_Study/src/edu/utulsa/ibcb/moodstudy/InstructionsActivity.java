package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

/**
 * Displays game instructions Provides button for user to continue to the game's
 * initial survey
 * 
 * @author Eric Kuxhausen
 */
public class InstructionsActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Load layout
		setContentView(R.layout.instructions);

		TextView instructionView = (TextView) findViewById(R.id.instructionsTextView);
		Button continueButton = (Button) findViewById(R.id.continueButton);
		continueButton.setOnClickListener(this);

		/*
		 * // Increment session ID settings.edit().putInt("SID",
		 * settings.getInt("SID", -1) + 1); settings.edit().commit();
		 */

		// Get session ID
		try {
			int SID = RpcClient.getInstance(this).startSession(this);
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor edit = settings.edit();
			edit.putInt("SID", SID);
			edit.commit();
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
									InstructionsActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			;
			alert.show();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continueButton:
			startActivity(new Intent(this, InitialSurveyActivity.class));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}
}

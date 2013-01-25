package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

	public boolean loadedSID = false, triedToContinue = false;
	public ProgressDialog progDiag;

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
		new SessionDownloader().execute(this);

		progDiag = new ProgressDialog(this);
		progDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continueButton:
			if (loadedSID == false) {
				progDiag.show();
				triedToContinue = true;
			} else
				startActivity(new Intent(this, InitialSurveyActivity.class));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}

	private class SessionDownloader extends AsyncTask<Object, Void, Integer> {

		XMLRPCException xrpcE;
		Context cont;

		@Override
		protected Integer doInBackground(Object... params) {
			// Get session ID
			cont = (Context) params[0];
			Log.i("asyncTask", "doing");
			try {
				int SID = RpcClient.getInstance(cont).startSession(cont);
				SharedPreferences settings = PreferenceManager
						.getDefaultSharedPreferences(cont);
				Editor edit = settings.edit();
				edit.putInt("SID", SID);
				edit.commit();
			} catch (XMLRPCException xrpc) {
				xrpcE = xrpc;
				Log.i("asyncTask", "rpc failed");
			}
			Log.i("asyncTask", "finishing");
			return 1;
		}

		@Override
		protected void onPostExecute(Integer SID) {
			Log.i("asyncTask", "finished");
			if (xrpcE != null) {
				xrpcE.printStackTrace();

				StackTraceElement[] stack = xrpcE.getStackTrace();

				AlertDialog.Builder builder = new AlertDialog.Builder(cont);
				builder.setMessage(
						"Error:" + xrpcE.getMessage() + "\nIn:"
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
				alert.show();
			}
			loadedSID = true;
			if (triedToContinue) {
				startActivity(new Intent(cont, InitialSurveyActivity.class));
			}
		}

	}
}

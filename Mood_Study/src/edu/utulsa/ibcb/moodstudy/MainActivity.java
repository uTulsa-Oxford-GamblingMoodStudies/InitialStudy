package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import edu.utulsa.ibcb.moodstudy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The splash/main page first seen when the app is launched Provides buttons to
 * play(move to the instructions page) or register(not yet implemented)
 * 
 * @author Eric Kuxhausen
 */
public class MainActivity extends Activity implements OnClickListener,
		OnSharedPreferenceChangeListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// add default preferences if no preferences exist
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);

		if (!settings.contains("GraphicsMode")) {
			settings.edit().putString("GraphicsMode",
					getString(R.string.graphics_mode_preference));
			settings.edit().commit();
		}

		if (!settings.contains("initialSurveyActivityResult")) {
			settings.edit().putInt("initialSurveyActivityResult", -1);
			settings.edit().commit();
		}
		if (!settings.contains("SID")) {
			settings.edit().putInt("SID", -1);
			settings.edit().commit();
		}
		if (!settings.contains("username")) {
			settings.edit().putString("username", "");
			settings.edit().commit();
		}

		load();

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
	}

	/**
	 * Load performs many duties normally found in onCreate, but also needed
	 * when reloading after a Theme change
	 */
	public void load() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Load layout
		setContentView(R.layout.main);

		Button playButton = (Button) findViewById(R.id.playButtonMain);
		playButton.setOnClickListener(this);
		// playButton.setTypeface(tf);

		Button registerButton = (Button) findViewById(R.id.registerButtonMain);
		registerButton.setOnClickListener(this);
		// playButton.setTypeface(tf);


		Button settingsButton = (Button) findViewById(R.id.settingsButtonMain);
		settingsButton.setOnClickListener(this);
		
		
		try {
			int SID = RpcClient.getInstance(this).startSession(this);
			settings.edit().putInt("SID", SID);
			settings.edit().commit();
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
									MainActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			;
			alert.show();
		}
		

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playButtonMain:
			if (RpcClient.getInstance(this).getOption("username") != null)
				startActivity(new Intent(this, InstructionsActivity.class));
			else
				createDialog("Wait!", "Please register before playing.",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
			break;
		case R.id.registerButtonMain:
			startActivity(new Intent(this, PlayerIdentificationActivity.class));
			break;
		case R.id.settingsButtonMain:
			startActivity(new Intent(this, DevelopmentPreferencesActivity.class));
			break;

		}
	}

	public void createDialog(String title, String message,
			DialogInterface.OnClickListener click) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle(title)
				.setNeutralButton("Ok", click);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

	}
}

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Prompts user to try to roll for a certain number. Ensures user is aware of
 * that number by asking user to click on the corresponding prize to continue
 * 
 * @author Eric Kuxhausen
 */
public class GamePromptActivity extends Activity implements OnClickListener {

	private int promptedRoll;
	private int actualRoll;
	private int luckyFeeling;
	private boolean threeD;

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
		setContentView(R.layout.game_prompt);

		// Check graphics mode
		if (settings.getString("GraphicsMode", "").equals("3D"))
			threeD = true;

		luckyFeeling = getIntent().getExtras().getInt("luckyFeeling", -1);

		int winning = 0;// (int)(Math.ceil(6*Math.random()));//0
		int play = 0;// (int)(Math.ceil(6*Math.random()));//0
		try {
			int[] response = RpcClient.getInstance(this).play();
			winning = response[0];
			play = response[1];
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
									GamePromptActivity.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}

		promptedRoll = winning;
		actualRoll = play;

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

		Log.d("network", "loaded");
		ImageView PromptImage = (ImageView) findViewById(R.id.dicePromptImageView);
		switch (promptedRoll) {
		case 1:
			PromptImage.setImageDrawable(getResources().getDrawable(
					R.drawable.dice1));
			break;
		case 2:
			PromptImage.setImageDrawable(getResources().getDrawable(
					R.drawable.dice2));
			break;
		case 3:
			PromptImage.setImageDrawable(getResources().getDrawable(
					R.drawable.dice3));
			break;
		case 4:
			PromptImage.setImageDrawable(getResources().getDrawable(
					R.drawable.dice4));
			break;
		case 5:
			PromptImage.setImageDrawable(getResources().getDrawable(
					R.drawable.dice5));
			break;
		case 6:
			PromptImage.setImageDrawable(getResources().getDrawable(
					R.drawable.dice6));
			break;
		}
	}

	public void showWrongDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Sorry, that is not the winning die for this roll.")
				.setTitle("Try Again...")
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onClick(View v) {
		Intent iDiceGame;
		if (threeD)
			iDiceGame = new Intent(this, DiceGameActivity.class);
		else
			iDiceGame = new Intent(this, DiceGame2DActivity.class);
		iDiceGame.putExtra("prompt", promptedRoll);
		iDiceGame.putExtra("actual", actualRoll);
		iDiceGame.putExtra("luckyFeeling", luckyFeeling);

		Log.i("pa", promptedRoll + " " + actualRoll);

		switch (v.getId()) {
		case R.id.prize1Button:
			if (promptedRoll == 1) {
				setContentView(R.layout.placeholder);
				startActivity(iDiceGame);
			} else
				showWrongDialog();
			break;
		case R.id.prize2Button:
			if (promptedRoll == 2) {
				setContentView(R.layout.placeholder);
				startActivity(iDiceGame);
			} else
				showWrongDialog();
			break;
		case R.id.prize3Button:
			if (promptedRoll == 3) {
				setContentView(R.layout.placeholder);
				startActivity(iDiceGame);
			} else
				showWrongDialog();
			break;
		case R.id.prize4Button:
			if (promptedRoll == 4) {
				setContentView(R.layout.placeholder);
				startActivity(iDiceGame);
			} else
				showWrongDialog();
			break;
		case R.id.prize5Button:
			if (promptedRoll == 5) {
				setContentView(R.layout.placeholder);
				startActivity(iDiceGame);
			} else
				showWrongDialog();
			break;
		case R.id.prize6Button:
			if (promptedRoll == 6) {
				setContentView(R.layout.placeholder);
				startActivity(iDiceGame);
			} else
				showWrongDialog();
			break;
		}
		Log.d("input", "button pressed");
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, FinalSurveyActivity.class));
	}
}

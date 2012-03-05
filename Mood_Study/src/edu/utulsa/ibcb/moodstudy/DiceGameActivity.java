package edu.utulsa.ibcb.moodstudy;

import edu.utulsa.ibcb.moodstudy.R;
import edu.utulsa.ibcb.moodstudy.opengl.CupEnvironment;
import edu.utulsa.ibcb.moodstudy.opengl.DiceRenderer;
import edu.utulsa.ibcb.moodstudy.opengl.DiceRollEnvironment;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class DiceGameActivity extends Activity implements OnClickListener {

	DiceRenderer diceview;
	protected int actual;
	protected int prompt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		actual = getIntent().getExtras().getInt("actual", 0);
		prompt = getIntent().getExtras().getInt("prompt", 0);
		
		try {
			GLSurfaceView drawSurface = new GLSurfaceView(this);
			drawSurface.setOnClickListener(this);

			DiceRenderer.setContext(this);
			diceview = DiceRenderer.getInstance();

			drawSurface.setRenderer(diceview);
			setContentView(drawSurface);

			int force_level = 1;

			((DiceRollEnvironment) diceview.getEnvironment("roll")).setupPlay(
					force_level, actual);

			diceview.setEnvironment("cup");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int clicks = 0;

	public void onClick(View arg0) {

		if (clicks == 0) {
			((CupEnvironment) diceview.getEnvironment("cup")).throwDie();

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			diceview.setEnvironment("roll");

			clicks++;
		} else {
			startActivity(new Intent(this, GamePromptActivity.class));
			clicks = 0;
		}
	}

	public void onBackPressed() {
		Intent iOver = new Intent(this, GameResultsActivity.class);
		iOver.putExtra("won", actual == prompt);
		startActivity(iOver);
	}

}
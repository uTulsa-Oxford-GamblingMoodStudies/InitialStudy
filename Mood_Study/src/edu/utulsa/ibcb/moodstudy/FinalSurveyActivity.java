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
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Survey user is presented upon game completion
 * 
 * @author Eric Kuxhausen
 */
public class FinalSurveyActivity extends Activity implements OnClickListener {

	private int questionNumber = -1;
	private int[] responses;
	private TextView question, leftAnswer, rightAnswer;
	private String[] questions, leftAnswers, rightAnswers;
	private SeekBar visualAnalogScale;
	private Button exitButton, continueButton;

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
		setContentView(R.layout.final_survey);

		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(this);
		exitButton.setVisibility(TextView.INVISIBLE);

		continueButton = (Button) findViewById(R.id.continueButton);
		continueButton.setOnClickListener(this);

		question = (TextView) findViewById(R.id.questionTextView);
		leftAnswer = (TextView) findViewById(R.id.leftTextView);
		rightAnswer = (TextView) findViewById(R.id.rightTextView);

		visualAnalogScale = (SeekBar) findViewById(R.id.visualAnalogSeekBar);

		questions = getResources().getStringArray(R.array.vas_questions);
		leftAnswers = getResources().getStringArray(R.array.vas_answer_left);
		rightAnswers = getResources().getStringArray(R.array.vas_answer_right);
		responses = new int[questions.length];

		loadNextQuestion();
	}

	public void loadNextQuestion() {
		// Initialization case
		if(questionNumber == -1){
			questionNumber++;
			visualAnalogScale.setProgress(50);
			question.setText(questions[questionNumber]);
			leftAnswer.setText(leftAnswers[questionNumber]);
			rightAnswer.setText(rightAnswers[questionNumber]);
			return;
		}
		// Loading last question case
		else if(questionNumber + 2 == questions.length){
			responses[questionNumber] = visualAnalogScale.getProgress();
			questionNumber++;
			visualAnalogScale.setProgress(50);
			question.setText(questions[questionNumber]);
			leftAnswer.setText(leftAnswers[questionNumber]);
			rightAnswer.setText(rightAnswers[questionNumber]);
			continueButton.setVisibility(TextView.INVISIBLE);
			exitButton.setVisibility(TextView.VISIBLE);
			return;
		}
		else{
			responses[questionNumber] = visualAnalogScale.getProgress();
			questionNumber++;
			visualAnalogScale.setProgress(50);
			question.setText(questions[questionNumber]);
			leftAnswer.setText(leftAnswers[questionNumber]);
			rightAnswer.setText(rightAnswers[questionNumber]);
			return;
		}
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.continueButton:
			loadNextQuestion(); break;
		case R.id.exitButton:
			try {
				RpcClient.getInstance(this).uploadFinalSurveyData(null, null);//responses);//TODO
				finish();
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
										FinalSurveyActivity.this.finish();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}

			finish();
			break;
		}

	}

}
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Questionnare of mood before playing the game
 * 
 * @author Eric Kuxhausen
 */
public class RegistrationSurveyActivity extends Activity implements
		OnClickListener {

	private int questionNumber = 1;
	private int[] responses;
	TextView question;
	RadioGroup radioGroup;
	private String[] questions;
	private String[] answer1, answer2, answer3, answer4, answer5, answer6,
			answer7;

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
		radioGroup = (RadioGroup) findViewById(R.id.surveyRadioGroup);

		questions = getResources().getStringArray(R.array.survey_questions);
		answer1 = getResources().getStringArray(R.array.survey_answer_1);
		answer2 = getResources().getStringArray(R.array.survey_answer_2);
		answer3 = getResources().getStringArray(R.array.survey_answer_3);
		answer4 = getResources().getStringArray(R.array.survey_answer_4);
		answer5 = getResources().getStringArray(R.array.survey_answer_5);
		answer6 = getResources().getStringArray(R.array.survey_answer_6);
		answer7 = getResources().getStringArray(R.array.survey_answer_7);
		responses = new int[questions.length];

		loadQuestion();
	}

	private void loadQuestion() {
		question.setText(questions[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(0))
				.setText(answer1[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(1))
				.setText(answer2[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(2))
				.setText(answer3[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(3))
				.setText(answer4[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(4))
				.setText(answer5[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(5))
				.setText(answer6[questionNumber - 1]);
		((RadioButton) radioGroup.getChildAt(6))
				.setText(answer7[questionNumber - 1]);

		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if (((RadioButton) radioGroup.getChildAt(i)).getText().equals(""))
				((RadioButton) radioGroup.getChildAt(i))
						.setVisibility(RadioButton.INVISIBLE);
			else
				((RadioButton) radioGroup.getChildAt(i))
						.setVisibility(RadioButton.VISIBLE);

			if (responses[questionNumber - 1] == i + 1)
				((RadioButton) radioGroup.getChildAt(i)).setChecked(true);
			else
				((RadioButton) radioGroup.getChildAt(i)).setChecked(false);
		}
	}

	private void saveQuestion() {

		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if (((RadioButton) radioGroup.getChildAt(i)).isChecked())
				responses[questionNumber - 1] = i + 1;

		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previousButton:
			saveQuestion();
			questionNumber--;
			if (questionNumber < 1)
				onBackPressed();
			else
				loadQuestion();
			break;
		case R.id.nextButton:
			saveQuestion();
			questionNumber++;
			if (questionNumber > responses.length){
				try {
					RpcClient.getInstance(this).uploadSurveyData(this, null, null);//responses);//TODO
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
											RegistrationSurveyActivity.this.finish();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}
	
			
				onBackPressed();
			}
			else
				loadQuestion();
		}


	}

}

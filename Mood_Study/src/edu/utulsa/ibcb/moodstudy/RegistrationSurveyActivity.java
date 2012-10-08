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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
	private String[] questions, responseText;
	private String[][] answers;

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

		// Disabled back button
		// Button previousButton = (Button) findViewById(R.id.previousButton);
		// previousButton.setOnClickListener(this);

		question = (TextView) findViewById(R.id.instructionTextView);
		radioGroup = (RadioGroup) findViewById(R.id.surveyRadioGroup);

		questions = getResources().getStringArray(R.array.survey_questions);
		answers = new String[7][];
		answers[0] = getResources().getStringArray(R.array.survey_answer_1);
		answers[1] = getResources().getStringArray(R.array.survey_answer_2);
		answers[2] = getResources().getStringArray(R.array.survey_answer_3);
		answers[3] = getResources().getStringArray(R.array.survey_answer_4);
		answers[4] = getResources().getStringArray(R.array.survey_answer_5);
		answers[5] = getResources().getStringArray(R.array.survey_answer_6);
		answers[6] = getResources().getStringArray(R.array.survey_answer_7);
		responses = new int[questions.length];
		responseText = new String[questions.length];

		loadQuestion();
	}

	private void loadQuestion() {
		question.setText(questions[questionNumber - 1]);
		for (int i = 0; i < answers.length; i++) {
			((RadioButton) radioGroup.getChildAt(i))
					.setText(answers[i][questionNumber - 1]);
		}

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
			// responseText[questionNumber - 1 ] = answers[i][questionNumber-1];
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
			if (questionNumber > responses.length) {
				String[] responses = new String[questions.length];
				try {
					RpcClient.getInstance(this).uploadSurveyData(this,
							questions, responseText);
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
										public void onClick(
												DialogInterface dialog, int id) {
											RegistrationSurveyActivity.this
													.finish();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}

				onBackPressed();
			} else
				loadQuestion();
		}

	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}
}

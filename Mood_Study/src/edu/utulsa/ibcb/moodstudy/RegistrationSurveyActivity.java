package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Questionnaire of mood before playing the game
 * 
 * @author Eric Kuxhausen
 */
public class RegistrationSurveyActivity extends Activity implements
		OnClickListener {

	private int questionIndex = 0;
	private int[] responses;
	TextView question;
	RadioGroup radioGroup;
	private String[] questions, responseText;
	private String[][] answers;
	Button nextButton;
	ProgressDialog progDiag;

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

		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(this);

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
		for(int i = 0; i<responseText.length; i++)
		{
			responseText[i]="";
		}

		loadQuestion();
	}

	private void loadQuestion() {
		question.setText(questions[questionIndex]);
		for (int i = 0; i < answers.length; i++) {
			((RadioButton) radioGroup.getChildAt(i))
					.setText(answers[i][questionIndex]);
		}
		radioGroup.clearCheck();
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if (((RadioButton) radioGroup.getChildAt(i)).getText().equals(""))
				((RadioButton) radioGroup.getChildAt(i))
						.setVisibility(RadioButton.INVISIBLE);
			else
				((RadioButton) radioGroup.getChildAt(i))
						.setVisibility(RadioButton.VISIBLE);

			//if (responses[questionIndex] == i + 1)
			//	((RadioButton) radioGroup.getChildAt(i)).setChecked(true);
		}
		
	}

	private void saveQuestion() {

		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			if (((RadioButton) radioGroup.getChildAt(i)).isChecked())
				responses[questionIndex] = i + 1;
			responseText[questionIndex] = answers[i][questionIndex];
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previousButton:
			saveQuestion();
			questionIndex--;
			if (questionIndex < 0)
				onBackPressed();
			else
				loadQuestion();
			break;
		case R.id.nextButton:
			saveQuestion();
			questionIndex++;
			if (questionIndex >= responses.length) {
				String[] responses = new String[questions.length];
				
				//
				progDiag = new ProgressDialog(this);
				progDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progDiag.show();
				
				
				nextButton.setClickable(false);
				
				new SurveyUploader().execute(this,questions,responseText);
				
				//
				
			} else
				loadQuestion();
		}

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}
	
	
	private class SurveyUploader extends AsyncTask<Object, Void, Integer> {

		XMLRPCException xrpcE;
		Context cont;
		
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			
			cont = (Context)params[0];
			String[] questions = (String[])params[1];
			String[] responseText = (String[])params[2];
			Log.i("asyncTask","doing");
			try {
				RpcClient.getInstance(cont).uploadSurveyData(cont,
						questions, responseText);
			} catch (XMLRPCException xrpc) {
				xrpcE = xrpc;
			}
			Log.i("asyncTask","finishing");
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i("asyncTask","finished");
			if(xrpcE!=null){
				xrpcE.printStackTrace();

				StackTraceElement[] stack = xrpcE.getStackTrace();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(cont);
				builder.setMessage(
						"Error:" + xrpcE.getMessage() + "\nIn:"
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
			
			
			progDiag.dismiss();
			//
			onBackPressed();
	    }

	}
}

package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegistrationActivity extends Activity implements OnClickListener{
	private int session = -1;
	private String base64_img = null;
	private RpcClient callServer = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        callServer = RpcClient.getInstance(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.registration);
		
		Button register_button = (Button)findViewById(R.id.registrationSubmit);
		register_button.setOnClickListener(this);
        
		getCaptcha();
	}
	
	public void getCaptcha(){
		try{
			Object[] response = callServer.requestCaptcha();
			
			session = Integer.parseInt((String)response[0]);
			base64_img = (String)response[1];
			
			//decode base64_img
			
			base64_img = base64_img.substring("data:image/jpeg;base64,".length());
			
			byte[] img_data = Base64.decode(base64_img, Base64.DEFAULT);
			
			Bitmap bm = BitmapFactory.decodeByteArray(img_data, 0, img_data.length);
			
			System.out.println("Img:"+bm.getHeight()+"x"+bm.getWidth());
			// set to image
			ImageView captchaView = (ImageView) findViewById(R.id.captchaImage);
			captchaView.setImageBitmap(bm);
			
		}catch(XMLRPCException xrpc){
			xrpc.printStackTrace();
        	
        	StackTraceElement[] stack = xrpc.getStackTrace();
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Error:" + xrpc.getMessage() + "\nIn:" + stack[stack.length-1].getClassName())
        		   .setTitle("Error")
        	       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                RegistrationActivity.this.finish();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
		}
	}
	
	public void createDialog(String title, String message, DialogInterface.OnClickListener click ){
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message)
    		   .setTitle(title)
    	       .setNeutralButton("Ok", click);
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	public void onBackPressed(){
		Intent intent = getIntent();
		intent.putExtra("returnedData", "failed");
		setResult(RESULT_OK, intent);
		
		finish();
	}
	
	public void onClick(View v) {
		String user = ((TextView) findViewById(R.id.username_field)).getText().toString();
		String pass1 = ((TextView) findViewById(R.id.pass1_field)).getText().toString();
		String pass2 = ((TextView) findViewById(R.id.pass2_field)).getText().toString();
		String name = ((TextView) findViewById(R.id.realname_field)).getText().toString();
		String captcha = ((TextView) findViewById(R.id.captchaEntry)).getText().toString();
		
		if(!pass1.equals(pass2)){
			createDialog("Error","Passwords do not match!",new DialogInterface.OnClickListener(){
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.dismiss();
    	           }
    	       });
		}
		
		try{
			boolean success = callServer.register(user, pass1, name, captcha, ""+session);
			
			if(success){
				callServer.setOptions(this, RpcClient.USERNAME_OPTION, user, RpcClient.PASSWORD_OPTION, pass1);
				((TextView) findViewById(R.id.username_field)).setText("");
				((TextView) findViewById(R.id.pass1_field)).setText("");
				((TextView) findViewById(R.id.pass2_field)).setText("");
				((TextView) findViewById(R.id.realname_field)).setText("");
				((TextView) findViewById(R.id.captchaEntry)).setText("");
				
				Intent intent = getIntent();
				intent.putExtra("returnedData", "completed");
				setResult(RESULT_OK, intent);
				
				finish();
			}
		}catch(XMLRPCException xrpc){
			xrpc.printStackTrace();
        	StackTraceElement[] stack = xrpc.getStackTrace();
        	
        	createDialog("Error","Error:" + xrpc.getMessage() + "\nIn:" + stack[stack.length-1].getClassName(),new DialogInterface.OnClickListener() {
 	           public void onClick(DialogInterface dialog, int id) {
 	                dialog.dismiss();
 	           }
 	        });
        	
        	if(xrpc.getMessage().contains("Incorrect Captcha Code")){
        		getCaptcha();
        		((EditText) findViewById(R.id.captchaEntry)).getText().clear();
        	}
        	if(xrpc.getMessage().toLowerCase().contains("password")){
        		getCaptcha();
        		((EditText) findViewById(R.id.pass1_field)).getText().clear();
        		((EditText) findViewById(R.id.pass2_field)).getText().clear();
        	}
		}
	}
}

package edu.utulsa.ibcb.moodstudy;

import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener{
	
	private RpcClient callServer = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        callServer = RpcClient.getInstance(this);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.login);
		
		Button login_button = (Button)findViewById(R.id.loginSubmit);
		login_button.setOnClickListener(this);
        
	}
	
	
	public void createDialog(String title, String message, DialogInterface.OnClickListener click ){
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message)
    		   .setTitle(title)
    	       .setNeutralButton("Ok", click);
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	public void onClick(View v) {
		String user = ((TextView) findViewById(R.id.username_field)).getText().toString();
		String pass = ((TextView) findViewById(R.id.pass_field)).getText().toString();
		
		try{
			boolean success = callServer.login(user, pass);
			
			if(success){
				callServer.setOptions(this, RpcClient.USERNAME_OPTION, user, RpcClient.PASSWORD_OPTION, pass);
				((TextView) findViewById(R.id.username_field)).setText("");
				((TextView) findViewById(R.id.pass_field)).setText("");
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
        	
    		((EditText) findViewById(R.id.username_field)).getText().clear();
    		((EditText) findViewById(R.id.pass_field)).getText().clear();
        	
		}
	}
}

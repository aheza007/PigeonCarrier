package com.desireaheza.pigeon;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	protected TextView signUpLink;
	protected EditText mUserName;//userName
	protected EditText mPassword; 
	protected Button  loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		//hide the actionBar when this activity starts
		getActionBar().hide();
		mUserName=(EditText) findViewById(R.id.userNameId);
		mPassword=(EditText) findViewById(R.id.passwordId);
		loginButton=(Button) findViewById(R.id.loginButton);
		signUpLink=(TextView) findViewById(R.id.signUptext);
		
		signUpLink.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(LoginActivity.this , SignUpActivity.class);
				startActivity(intent);
				
			}
		});
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
							
				String userName=mUserName.getText().toString().trim(); 
				String password=mPassword.getText().toString().trim();
				
				if (userName.isEmpty() || password.isEmpty()){
					AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(R.string.login_error_message);
					builder.setTitle(R.string.login_dialogbox_title);
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog=builder.create();
					//start the dialog and show it on screen
					dialog.show();
				}
				else{// setting to let the user login Up in our Ribbit App
					setProgressBarIndeterminateVisibility(true);
					ParseUser.logInInBackground(userName, password, new LogInCallback() {
						
 						@Override
						public void done(ParseUser user, ParseException e ) {
 							setProgressBarIndeterminateVisibility(false);
							if (e == null) {
								 PigeonApplication.upDataParseInstallation(user);
							      // The user is logged in successfully 
								
								Intent intent=new Intent(LoginActivity.this,MainActivity.class);
								//	If set, this activity will become the start of a new task on this history stack. A task (from the activity
								//that started it to the next task activity) defines an atomic group of activities that the user can move to
								
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								/*
								 * this flag will cause any existing task that would be associated with the activity to be cleared before the 
								 * activity is started. That is, the activity becomes the new root of an otherwise empty task, and any old activities 
								 * are finished
								 */
								
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		
								startActivity(intent);
								
							    } else {
							      // Signup failed. Look at the ParseException to see what happened.
							    	AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
									builder.setMessage(e.getMessage());
									builder.setTitle(R.string.login_dialogbox_title);
									builder.setPositiveButton(android.R.string.ok, null);
									AlertDialog dialog=builder.create();
									//start the dialog and show it on screen
									dialog.show();
							    }
						}
					});
				}
	      }
     });
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

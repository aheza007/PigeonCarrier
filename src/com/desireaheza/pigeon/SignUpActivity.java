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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

	protected EditText mUserName;
	protected EditText mPassword;//userName 
	protected EditText mEmail;
	protected Button  mSignUpButton;
	protected Button  mCancelButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_sign_up);
		//hide the actionBar when this activity starts
		getActionBar().hide();
		mUserName=(EditText) findViewById(R.id.userNameId);
		mPassword=(EditText) findViewById(R.id.passwordId);
		mEmail=(EditText) findViewById(R.id.emailFieldId);
		mSignUpButton=(Button) findViewById(R.id.signUpButton);
	
		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//editText provide Editable object so we need to convert them into string and trim starting and ending space
				//this means that we have to copy this string by removing white space characters from the beginning and end of the string.
				String userName=mUserName.getText().toString().trim();
				String password=mPassword.getText().toString().trim();
				String email=mEmail.getText().toString().trim();
				if (userName.isEmpty()|| password.isEmpty() || email.isEmpty()){
					AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
					builder.setMessage(R.string.signUp_error_message);
					builder.setTitle(R.string.signUp_dialogbox_title);
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog=builder.create();
					//start the dialog and show it on screen
					dialog.show();
				}
				else{// setting to let the user sign Up @parse.com
					
					setProgressBarIndeterminateVisibility(true);
					
					ParseUser newUser=new ParseUser();
					newUser.setUsername(userName);
					newUser.setPassword(password);
					newUser.setEmail(email);
					
					
					
					newUser.signUpInBackground(new SignUpCallback() {
						
						@Override
						public void done(ParseException error) {
							
							setProgressBarIndeterminateVisibility(false);
							
							if(error==null){//means that signUP has been successful
								PigeonApplication.upDataParseInstallation(ParseUser.getCurrentUser());
								Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
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
							}
							else{
								
								AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
								builder.setMessage(error.getMessage());
								builder.setTitle(R.string.signUp_dialogbox_title);
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
		
		mCancelButton=(Button) findViewById(R.id.cancelButton);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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

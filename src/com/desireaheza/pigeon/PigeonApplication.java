package com.desireaheza.pigeon;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class PigeonApplication extends Application{

	@Override 
	public void onCreate() {
		  super.onCreate();
		  //the application ID and the Client ID that we need in order to access the backend
		  Parse.initialize(this, "WNOS1xyvyYivLqPeiwarzIyE1Xgig3lhJcmFUecr", "KIchRC4eUCNDpgNcf8OvKT3oQAaaqtKfBGAqYYk5");
		  PushService.setDefaultPushCallback(this, MainActivity.class);
		  
	}
	
	public static void upDataParseInstallation(ParseUser user){
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}
}

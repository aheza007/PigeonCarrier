package com.desireaheza.pigeon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipientsActivity extends Activity {

	protected final String TAG = RecipientsActivity.class.getSimpleName();

	protected ParseRelation<ParseUser> mFriendRelation;
	protected ParseUser mCurrentUser;
	protected List<ParseUser> mFriends;
	protected MenuItem menuItemSend;
	protected Uri mMediaUri;
	protected String mFileType;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);
		// show the up button in the action bar
		setupActionBar();
		mGridView = (GridView) findViewById(R.id.FriendsGridView);
		mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);

		mGridView.setOnItemClickListener(mOnItemClickListener);
		/*
		 * Retrieve data this intent is operating on. This URI specifies the
		 * name of the data; often it uses the content: scheme, specifying data
		 * in a content provider
		 */
		mMediaUri = getIntent().getData();
		mFileType = getIntent().getExtras().getString(
				ParseConstants.KEY_FILE_TYPE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCurrentUser = ParseUser.getCurrentUser();

		mFriendRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIEND_RELATION);

		setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = mFriendRelation.getQuery();

		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		// query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException error) {

				setProgressBarIndeterminateVisibility(false);

				if (error == null) {

					// success

					mFriends = friends;
					String[] users = new String[mFriends.size()];
					int i = 0;
					for (ParseUser usr : friends) {
						users[i] = usr.getUsername();
						i++;
					}
					if (mGridView.getAdapter() == null) {
						UserAdpater adapter = new UserAdpater(
								RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
					} else {
						((UserAdpater) mGridView.getAdapter())
								.refillAdapter(mFriends);
					}
					// addAndCheckFriends();
				} else {
					Log.e(TAG, error.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RecipientsActivity.this);
					builder.setMessage(error.getMessage());
					builder.setTitle(R.string.error_title);
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					// start the dialog and show it on screen
					dialog.show();
				}

			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipient, menu);
		menuItemSend = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// Activity, the Up button is shown.
			// Use NavUtils to allow users to navigate up on level in the
			// application structure.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_send:

			ParseObject message = createMessage();

			if (message == null) {
				// error
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.error_while_uploading_file);
				builder.setTitle("We're, Sorry!");
				builder.setPositiveButton(android.R.string.ok, null);

				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				send(message);
				finish();
			}
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void send(ParseObject message) {

		message.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// success
					Toast.makeText(RecipientsActivity.this, "Message Sent!",
							Toast.LENGTH_LONG).show();
					sendPushNotifications();
				} else {
					// error
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RecipientsActivity.this);
					builder.setMessage(R.string.error_sending_message);
					builder.setTitle("We're, Sorry!");
					builder.setPositiveButton(android.R.string.ok, null);

					AlertDialog dialog = builder.create();
					dialog.show();
				}

			}
		});

	}

	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser()
				.getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser()
				.getUsername());
		message.put(ParseConstants.KEY_RECIPIENTS_ID, getRecipients());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

		byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

		if (fileBytes == null) {
			// error App may crash
			return null;
		} else {
			if (mFileType.equals(ParseConstants.TYPE_IMAGE))
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);

			String fileName = FileHelper
					.getFileName(this, mMediaUri, mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);
			message.put(ParseConstants.KEY_FILE, file);

			return message;
		}
	}

	private ArrayList<String> getRecipients() {

		ArrayList<String> recipientsIDs = new ArrayList<String>();

		// ArrayList<String> recipientsUserName=new ArrayList<String>();

		for (int i = 0; i < mGridView.getCount(); i++) {

			if (mGridView.isItemChecked(i)) {

				recipientsIDs.add(mFriends.get(i).getObjectId());
				// recipientsUserName.add(mFriends.get(i).getUsername());
			}

		}
		return recipientsIDs;
	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (mGridView.getCheckedItemCount() > 0) {

				menuItemSend.setVisible(true);
			} else {
				menuItemSend.setVisible(false);
			}
			ImageView checkImageView = (ImageView) view
					.findViewById(R.id.checkImageView);

			if (mGridView.isItemChecked(position)) {
				// add a recipient locally and remotely
				
				checkImageView.setVisibility(View.VISIBLE);
			} else {
				// remove a recipient if not checked
				checkImageView.setVisibility(View.INVISIBLE);
			}
			
		}
	};
	private void sendPushNotifications(){
		
		ParseQuery<ParseInstallation>  query = ParseInstallation.getQuery();
		query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipients());
		
		//Send Purse PushNotifications
		
		ParsePush push=new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_notification, 
				ParseUser.getCurrentUser().getUsername()));
		push.sendInBackground();
	}
}

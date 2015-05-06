package com.desireaheza.pigeon;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditBuddyActivity extends Activity {

	protected final String TAG = EditBuddyActivity.class.getSimpleName();

	protected ParseRelation<ParseUser> mFriendRelation;
	protected ParseUser mCurrentUser;
	protected List<ParseUser> mUsers;
	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);
		setupActionBar();
		// this will allow multiple choice of ITEM on the list we will display
		mGridView = (GridView) findViewById(R.id.FriendsGridView);
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIEND_RELATION);
		setProgressBarIndeterminateVisibility(true);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> user, ParseException error) {

				setProgressBarIndeterminateVisibility(false);

				if (error == null) {
					// success
					mUsers = user;
					String[] users = new String[mUsers.size()];
					int i = 0;
					for (ParseUser usr : user) {
						users[i] = usr.getUsername();
						i++;
					}
					;

					if (mGridView.getAdapter() == null) {
						UserAdpater adapter = new UserAdpater(
								EditBuddyActivity.this, mUsers);
						mGridView.setAdapter(adapter);
					} else {
						((UserAdpater) mGridView.getAdapter())
								.refillAdapter(mUsers);
					}
					addAndCheckFriends();
				} else {
					Log.e(TAG, error.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							EditBuddyActivity.this);
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
	 * Set up the ActionBar
	 */

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity the Up button is shown. Use NavUtils to allow users to
			// navigate up
			// one level in the application structure. For more details, see the
			// Navigation
			// pattern on Android Design:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}

		// if (id == R.id.action_settings) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}

	
	private void addAndCheckFriends() {
		mFriendRelation.getQuery().findInBackground(
				new FindCallback<ParseUser>() {

					@Override
					public void done(List<ParseUser> friends, ParseException e) {
						if (e == null) {
							// query succeeded-look for a match
							for (int i = 0; i < mUsers.size(); i++) {
								ParseUser user = mUsers.get(i);
								for (ParseUser friend : friends) {
									if (user.getObjectId().equals(
											friend.getObjectId())) {
										// check the found item
										mGridView.setItemChecked(i, true);
									}

								}
							}

						}
					}
				});
	}

	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

			if (mGridView.isItemChecked(position)) {
				// add a friend locally and remotely
				mFriendRelation.add(mUsers.get(position));
				checkImageView.setVisibility(View.VISIBLE);
			} else {
				// remove the user if not checked
				mFriendRelation.remove(mUsers.get(position));
				checkImageView.setVisibility(View.INVISIBLE);
			}
			mCurrentUser.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e != null) {
						Log.e(TAG, e.getMessage());
					}

				}
			});

		}
	};

}
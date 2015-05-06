package com.desireaheza.pigeon;

import java.security.spec.MGF1ParameterSpec;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
/*
 * A fragment that displays a list of items by binding to a data source such as an
 * array or Cursor, and exposes event handlers when the user selects an item. 
 * 
 */
public class FriendsFragment extends Fragment {
	protected final String TAG=FriendsFragment.class.getSimpleName();
	protected ParseRelation<ParseUser> mFriendRelation;
	protected ParseUser mCurrentUser;
	protected List<ParseUser> mFriends;
	protected GridView mFriendGridView;
	@Override	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * this method is 1st that will be called when the fragment is opened
		 * we have to maintain our code here that will be displayed on the
		 * fragment.
		 * 
		 */
		View rootView = inflater.inflate(R.layout.user_grid, container,false);
		mFriendGridView=(GridView) rootView.findViewById(R.id.FriendsGridView);
		TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
		mFriendGridView.setEmptyView(emptyTextView);
		return rootView; 
	}
	@Override
	public void onResume() {
		super.onResume();
		//getActivity().setProgressBarIndeterminateVisibility(true);
		mCurrentUser=ParseUser.getCurrentUser();
		mFriendRelation=mCurrentUser.getRelation(ParseConstants.KEY_FRIEND_RELATION);
		ParseQuery<ParseUser> query=mFriendRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);	
				if(e==null){
					//success
					
					mFriends=friends;
					String[] users=new String[mFriends.size()];
					int i=0;
					for(ParseUser usr:mFriends){
						users[i]=usr.getUsername();
						i++;
					};
					if(mFriendGridView.getAdapter()==null){
						UserAdpater adapter= new UserAdpater(getActivity(), mFriends);
						mFriendGridView.setAdapter(adapter);
					}
					else{
						((UserAdpater)mFriendGridView.getAdapter()).refillAdapter(mFriends);
					}
					

				}
				else{
					Log.e(TAG,e.getMessage());
					AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
					builder.setMessage(e.getMessage());
					builder.setTitle(R.string.error_title);
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog=builder.create();
					//start the dialog and show it on screen
					dialog.show();
				}
			}
		});
	}
}

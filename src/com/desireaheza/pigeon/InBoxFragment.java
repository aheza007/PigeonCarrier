package com.desireaheza.pigeon;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/*
 * A fragment that displays a list of items by binding to a data source such as an
 * array or Cursor, and exposes event handlers when the user selects an item. 
 */
public class InBoxFragment extends ListFragment {
	protected final String TAG = InBoxFragment.class.getSimpleName();
	protected List<ParseObject> mMessages;
	protected SwipeRefreshLayout mSwipeRefrshLayout;

	@Override
	/*
	 * this method is 1st that will be called when the fragment is opened we
	 * have to maintain our code here that will be displayed on the fragment.
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		mSwipeRefrshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefrshLayout.setOnRefreshListener(mOnRefreshList);
		mSwipeRefrshLayout.setColorScheme(R.color.reflesh_color1,
				R.color.reflesh_color2, R.color.reflesh_color3,
				R.color.reflesh_color4);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setProgressBarIndeterminateVisibility(true);
		retrieveMessageFromParse();
	}

	private void retrieveMessageFromParse() {
		ParseQuery<ParseObject> querry = new ParseQuery<ParseObject>(
				ParseConstants.CLASS_MESSAGES);
		querry.whereEqualTo(ParseConstants.KEY_RECIPIENTS_ID, ParseUser
				.getCurrentUser().getObjectId());
		querry.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		querry.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> messages, ParseException error) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (mSwipeRefrshLayout.isRefreshing()) {
					mSwipeRefrshLayout.setRefreshing(false);
				}
				if (error == null) {
					// we have found messages
					mMessages = messages;
					String[] users = new String[mMessages.size()];
					int i = 0;
					for (ParseObject message : mMessages) {
						users[i] = message
								.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					if (getListView().getAdapter() == null) {
						MessageAdpater adapter = new MessageAdpater(
								getActivity(), mMessages);
						setListAdapter(adapter);
					} else {
						// refill the adapter
						((MessageAdpater) getListView().getAdapter())
								.refillAdapter(mMessages);
					}
				}

			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		Uri fileUri = Uri.parse(file.getUrl());

		if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
			// display IMAGE
			Intent viewImageIntent = new Intent(getActivity(),
					ImageViewActivity.class);
			viewImageIntent.setData(fileUri);
			startActivity(viewImageIntent);

		} else {
			// display Video
			Intent viewVideo = new Intent(Intent.ACTION_VIEW, fileUri);
			viewVideo.setDataAndType(fileUri, "video/*");
			startActivity(viewVideo);
		}

		List<String> ids = message.getList(ParseConstants.KEY_RECIPIENTS_ID);
		if (ids.size() == 1) {
			// delete the last recipient
			message.deleteInBackground();
		} else {
			// delete the recipient and then the message
			ids.remove(ParseUser.getCurrentUser().getObjectId());
			ArrayList<String> idsToRemove = new ArrayList<String>();
			idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
			message.removeAll(ParseConstants.KEY_RECIPIENTS_ID, idsToRemove);
			message.saveInBackground();
		}

	}

	protected OnRefreshListener mOnRefreshList = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			retrieveMessageFromParse();
		}
	};
}

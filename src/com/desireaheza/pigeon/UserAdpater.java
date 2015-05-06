package com.desireaheza.pigeon;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class UserAdpater extends ArrayAdapter<ParseUser> {

	protected List<ParseUser> mUsers;
	protected Context mContext;
	protected final String TAG = UserAdpater.class.getSimpleName();
	private ViewHolder holder;

	public UserAdpater(Context context, List<ParseUser> users) {

		super(context, R.layout.message_item, users);

		mContext = context;
		mUsers = users;

	}

	/*
	 * Get a View that displays the data at the specified position in the data
	 * set. You can either create a View manually or inflate it from an XML
	 * layout file.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// ViewHolder

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.user_item, null);
			holder.userImageView = (ImageView) convertView
					.findViewById(R.id.userImageView);
			holder.nameLabel = (TextView) convertView
					.findViewById(R.id.nameLabel);
			holder.checkImageView=(ImageView)convertView
					.findViewById(R.id.checkImageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ParseUser user = mUsers.get(position);
		String email=user.getEmail();
		
		if(email.equals("")){
			try {
				holder.userImageView.setImageResource(R.drawable.avatar_empty);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}else{
			String hash = MD5Util.md5Hex(email.toLowerCase());
			String gravatarImgRequestURL="http://www.gravatar.com/avatar/"+hash+
					"?s=204&d=404";
			Picasso.with(mContext)
				.load(gravatarImgRequestURL)
				.placeholder(R.drawable.avatar_empty)
				.into(holder.userImageView);
		}
		
		try {
			
				holder.nameLabel.setText(user.getUsername());
			
			 }catch (Exception e) {
			
				 Log.e(TAG, e.toString());
			
			 }
		GridView gridView=(GridView)parent;
		if(gridView.isItemChecked(position)){
			holder.checkImageView.setVisibility(View.VISIBLE);
		}else{
			holder.checkImageView.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private static class ViewHolder {

		ImageView userImageView;
		ImageView checkImageView;
		TextView nameLabel;

	}

	public void refillAdapter(List<ParseUser> users) {
		mUsers.clear();
		mUsers.addAll(users);
		/*
		 * Notifies the attached observers that the underlying data has been
		 * changed and any View reflecting the data set should refresh itself.
		 */
		notifyDataSetChanged();

	}

}
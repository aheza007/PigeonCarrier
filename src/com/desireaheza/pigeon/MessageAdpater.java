package com.desireaheza.pigeon;

import java.util.Date;
import java.util.List;

import android.content.Context;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

public class MessageAdpater extends ArrayAdapter<ParseObject> {

	protected List<ParseObject> mMessages;
	protected Context mContext;
	protected final String TAG = MessageAdpater.class.getSimpleName();
	private ViewHolder holder;

	public MessageAdpater(Context context, List<ParseObject> messages) {

		super(context, R.layout.message_item, messages);

		mContext = context;
		mMessages = messages;

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
					R.layout.message_item, null);
			holder.iconImage = (ImageView) convertView
					.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView) convertView
					.findViewById(R.id.senderLabel);
			holder.timeLabel = (TextView) convertView
					.findViewById(R.id.timeLabel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ParseObject message = mMessages.get(position);
		//get the correct time and attach it in the message view
		Date createdAt=message.getCreatedAt();
		long now=new Date().getTime();
		String createdAtFormated=DateUtils.getRelativeTimeSpanString(createdAt.getTime(), 
				now, DateUtils.SECOND_IN_MILLIS).toString();
		holder.timeLabel.setText(createdAtFormated);

				
		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(
				ParseConstants.TYPE_IMAGE)) {

			try {

				holder.iconImage.setImageResource(R.drawable.ic_action_picture);

			} catch (Exception e) {

				Log.e(TAG, e.toString());

			}

		} else {
			holder.iconImage
					.setImageResource(R.drawable.ic_action_play_over_video);
		}

		holder.nameLabel.setText(message
				.getString(ParseConstants.KEY_SENDER_NAME));

		return convertView;
	}

	private static class ViewHolder {

		ImageView iconImage;
		TextView nameLabel;
		TextView timeLabel;

	}

	public void refillAdapter(List<ParseObject> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		/*
		 * Notifies the attached observers that the underlying data has been
		 * changed and any View reflecting the data set should refresh itself.
		 */
		notifyDataSetChanged();

	}

}
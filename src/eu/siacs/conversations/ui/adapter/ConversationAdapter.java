package eu.siacs.conversations.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.ximp.ConversationActivity;
import com.ximp.R;
import com.ximp.XmppActivity;

import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.entities.Downloadable;
import eu.siacs.conversations.entities.Message;
import eu.siacs.conversations.utils.UIHelper;

public class ConversationAdapter extends ArrayAdapter<Conversation> {

	private XmppActivity activity;
/*	TextView convName;
	TextView mLastMessage;
	TextView mTimestamp;
	ImageView imagePreview;
	ImageView profilePicture;
	ImageView imageStatus;
*/	public ConversationAdapter(XmppActivity activity,
			List<Conversation> conversations) {
		super(activity, 0, conversations);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolderItem holder;
		if (view == null) {
			holder = new ViewHolderItem();
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.conversation_list_row,parent, false);
			 holder.mLastMessage = (TextView) view.findViewById(R.id.conversation_lastmsg);
			 holder.mTimestamp = (TextView) view.findViewById(R.id.conversation_lastupdate);
			 holder.imagePreview = (ImageView) view.findViewById(R.id.conversation_lastimage);
			 holder.profilePicture = (ImageView) view.findViewById(R.id.conversation_image);
			 holder.imageStatus = (ImageView) view.findViewById(R.id.imgStatus);

			view.setTag(holder);
		}
		else{
			holder = (ViewHolderItem) view.getTag();
		}
		Conversation conversation = getItem(position);
		if (this.activity instanceof ConversationActivity) {
			ConversationActivity activity = (ConversationActivity) this.activity;
			if (!activity.isConversationsOverviewHideable()) {
				if (conversation == activity.getSelectedConversation()) {
					view.setBackgroundColor(activity
							.getSecondaryBackgroundColor());
				} else {
					view.setBackgroundColor(Color.TRANSPARENT);
				}
			} else {
				view.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		 holder.convName = (TextView) view.findViewById(R.id.conversation_name);
		if (conversation.getMode() == Conversation.MODE_SINGLE || activity.useSubjectToIdentifyConference()) {
			holder.convName.setText(conversation.getName());
		} else {
			holder.convName.setText(conversation.getJid().toBareJid().toString());
		}

		Message message = conversation.getLatestMessage();

		if (!conversation.isRead()) {
			holder.convName.setTypeface(null, Typeface.BOLD);
		} else {
			holder.convName.setTypeface(null, Typeface.NORMAL);
		}

		if (message.getImageParams().width > 0
				&& (message.getDownloadable() == null
				|| message.getDownloadable().getStatus() != Downloadable.STATUS_DELETED)) {
			holder.mLastMessage.setVisibility(View.GONE);
			holder.imagePreview.setVisibility(View.VISIBLE);
			activity.loadBitmap(message, holder.imagePreview);
		} else {
			Pair<String,Boolean> preview = UIHelper.getMessagePreview(activity,message);
			holder.mLastMessage.setVisibility(View.VISIBLE);
			holder.imagePreview.setVisibility(View.GONE);
			holder.mLastMessage.setText(preview.first);
			if (preview.second) {
				if (conversation.isRead()) {
					holder.mLastMessage.setTypeface(null, Typeface.ITALIC);
				} else {
					holder.mLastMessage.setTypeface(null,Typeface.BOLD_ITALIC);
				}
			} else {
				if (conversation.isRead()) {
					holder.mLastMessage.setTypeface(null,Typeface.NORMAL);
				} else {
					holder.mLastMessage.setTypeface(null,Typeface.BOLD);
				}
			}
		}

		holder.mTimestamp.setText(UIHelper.readableTimeDifference(activity,conversation.getLatestMessage().getTimeSent()));
		 holder.imageStatus.setImageDrawable(conversation.getContact().getStatusImage(activity));

		 loadAvatar(conversation,holder.profilePicture);

		return view;
	}

	class BitmapWorkerTask extends AsyncTask<Conversation, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private Conversation conversation = null;

		public BitmapWorkerTask(ImageView imageView) {
			imageViewReference = new WeakReference<>(imageView);
		}

		@Override
		protected Bitmap doInBackground(Conversation... params) {
			return activity.avatarService().get(params[0], activity.getPixel(56));
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
					imageView.setBackgroundColor(0x00000000);
				}
			}
		}
	}

	public void loadAvatar(Conversation conversation, ImageView imageView) {
		if (cancelPotentialWork(conversation, imageView)) {
			final Bitmap bm = activity.avatarService().get(conversation, activity.getPixel(56), false);
			if (bm != null) {
				imageView.setImageBitmap(bm);
				imageView.setBackgroundColor(0x00000000);
			} else {
				imageView.setBackgroundColor(UIHelper.getColorForName(conversation.getName()));
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, task);
				imageView.setImageDrawable(asyncDrawable);
				try {
					task.execute(conversation);
				} catch (final RejectedExecutionException ignored) {
				}
			}
		}
	}

	public static boolean cancelPotentialWork(Conversation conversation, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final Conversation oldConversation = bitmapWorkerTask.conversation;
			if (oldConversation == null || conversation != oldConversation) {
				bitmapWorkerTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}
	static class ViewHolderItem {
		TextView convName;
		TextView mLastMessage;
		TextView mTimestamp;
		ImageView imagePreview;
		ImageView profilePicture;
		ImageView imageStatus;
	}
}
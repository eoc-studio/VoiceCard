package eoc.studio.voicecard.mailbox;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.ListUtility;
import eoc.studio.voicecard.utils.NetworkUtility;

public class MailsAdapterView extends BaseAdapter
{
	private static final String TAG = "MailsAdapterView";
	public static final int NOCHECKED = 0;
	public static final int CHECKED = 1;
	private Context context;
	private List<Mail> mails;
	private Set<String> selecedMail;
	private MailsAdapterData mailsAdapterData;
	private boolean isPause = false;
	private boolean isInterrupt = false;
	private int count = 0;
	// View
	private ListView showMails;
	private LayoutInflater layoutInflater;
	private ViewTag viewTag;

	public MailsAdapterView(Context context, List<Mail> mails, MailsAdapterData mailsAdapterData,
			ListView showMails)
	{
		this.context = context;
		this.mails = mails;
		this.mailsAdapterData = mailsAdapterData;
		this.showMails = showMails;
		layoutInflater = LayoutInflater.from(context);
		isPause = false;
		isInterrupt = false;
		selecedMail = new HashSet<String>();
		DownlaodImageThread downlaodImageThread = new DownlaodImageThread(mails, 0, mails.size());
		downlaodImageThread.start();
	}

	@Override
	public int getCount()
	{
		return mails.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mails.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.mailbox_list_item, null);
			viewTag = new ViewTag(
					(RelativeLayout) convertView.findViewById(R.id.glb_mailbox_list_item_rlyt),
					(ImageView) convertView.findViewById(R.id.glb_mailbox_list_item_check_icon),
					(ImageView) convertView.findViewById(R.id.glb_mailbox_list_item_img),
					(TextView) convertView.findViewById(R.id.glb_mailbox_list_item_tv_subject),
					(TextView) convertView.findViewById(R.id.glb_mailbox_list_item_tv_sendtime),
					(ImageView) convertView.findViewById(R.id.glb_mailbox_list_item_new_icon),
					(LinearLayout) convertView
							.findViewById(R.id.glb_mailbox_list_item_llyt_check_icon));
			convertView.setTag(viewTag);
		}
		else
		{
			viewTag = (ViewTag) convertView.getTag();
		}
		convertView.setId(ListUtility.BASE_INDEX + position);
		// checkIcon
		if (position < mails.size())
		{
			int checkState = mails.get(position).getCheckState();
			Log.d(TAG, "checkState is " + checkState);
			if (checkState == CHECKED)
			{
				viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
						R.drawable.icon_checkbox_check));
				viewTag.itemLayout.setBackgroundColor(context.getResources().getColor(
						R.color.mail_box_list_item_selected_bg_color));
			}
			else
			{
				viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
						R.drawable.icon_checkbox));
				viewTag.itemLayout.setBackgroundColor(context.getResources().getColor(
						R.color.transparent_text));
			}
			// itemImg
			byte[] img = mails.get(position).getSenderImg();
			if (img != null)
			{
				viewTag.itemImg.setBackgroundDrawable(new BitmapDrawable(context.getResources(),
						BitmapFactory.decodeByteArray(img, 0, img.length)));
			}
			else
			{
				viewTag.itemImg.setBackgroundDrawable(context.getResources().getDrawable(
						R.drawable.user_reflect));
			}
			// subject
			viewTag.subject.setText(mails.get(position).getSendedFromName());
			// sendTime
			viewTag.sendTime.setText(mails.get(position).getSendedTime());
			// newIcon
			int newState = mails.get(position).getNewState();
			if (newState == MailsAdapterData.NEW)
			{
				viewTag.newIcon.setVisibility(View.VISIBLE);
			}
			else
			{
				viewTag.newIcon.setVisibility(View.INVISIBLE);
			}
			setUIEvent(position, convertView, parent);
		}
		return convertView;
	}

	private void setUIEvent(final int position, View convertView, ViewGroup parent)
	{
		viewTag.checkIconLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int checkState = mails.get(position).getCheckState();
				Log.d(TAG, "click checkState is " + checkState);
				if (checkState == CHECKED)
				{
					viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
							R.drawable.icon_checkbox));
					viewTag.itemLayout.setBackgroundColor(context.getResources().getColor(
							R.color.mail_box_list_item_selected_bg_color));
					mails.get(position).setCheckState(NOCHECKED);
					selecedMail.remove(mails.get(position).getRowId());
					if (count > 0)
					{
						count--;
					}
					((MailboxActivity) context).showMailInfo(count);
					((MailboxActivity) context).setButtonString(false, R.string.select_all);
				}
				else
				{
					viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(
							R.drawable.icon_checkbox_check));
					viewTag.itemLayout.setBackgroundColor(context.getResources().getColor(
							R.color.transparent_text));
					mails.get(position).setCheckState(CHECKED);
					selecedMail.add(mails.get(position).getRowId());
					if (count < mails.size())
					{
						count++;
					}
					((MailboxActivity) context).showMailInfo(count);
					if (count == mails.size())
					{
						((MailboxActivity) context).setButtonString(true, R.string.deselect_all);
					}
				}
				MailsAdapterView.this.notifyDataSetChanged();
			}
		});
	}

	public void selectAll()
	{
		for (Mail mail : mails)
		{
			mail.setCheckState(CHECKED);
			selecedMail.add(mail.getRowId());
		}
		MailsAdapterView.this.notifyDataSetChanged();
		count = mails.size();
		((MailboxActivity) context).showMailInfo(count);
	}

	public void deselectAll()
	{
		for (Mail mail : mails)
		{
			mail.setCheckState(NOCHECKED);
		}
		selecedMail.clear();
		MailsAdapterView.this.notifyDataSetChanged();
		count = 0;
		((MailboxActivity) context).showMailInfo(count);
	}

	public Set<String> getSelectedItem()
	{
		return selecedMail;
	}

	public void clearData()
	{
		for (Iterator it = mails.iterator(); it.hasNext();)
		{
			it.remove();
		}
		mails.clear();
		selecedMail.clear();
	}

	public void setPause(boolean isPause)
	{
		this.isPause = isPause;
	}

	public void setInterrupt(boolean isInterrupt)
	{
		this.isInterrupt = isInterrupt;
	}

	public void loadImagefromPosition(int startPostion, int endPosition)
	{
		if (startPostion > 0 && endPosition <= mails.size())
		{
			DownlaodImageThread downlaodImageThread = new DownlaodImageThread(mails.subList(
					startPostion, endPosition), startPostion, endPosition);
			downlaodImageThread.start();
		}
	}

	private Handler showImgHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what < mails.size())
			{
				Log.d(TAG, "showImgHandler === msg.what === " + msg.what);
				if (mails.get(msg.what).getSenderImg() == null)
				{
					Log.d(TAG, "mails is null");
					if (showMails.findViewById(ListUtility.BASE_INDEX + msg.what) != null)
					{
						viewTag = (ViewTag) showMails.findViewById(
								ListUtility.BASE_INDEX + msg.what).getTag();
					}
					else
					{
						Log.d(TAG, "mails.get(msg.what) is null");
					}
				}
				else
				{
					Log.d(TAG, "mails not null");
					if (showMails.findViewById(ListUtility.BASE_INDEX + msg.what) != null)
					{
						viewTag = (ViewTag) showMails.findViewById(
								ListUtility.BASE_INDEX + msg.what).getTag();
						byte[] img = mails.get(msg.what).getSenderImg();
						viewTag.itemImg
								.setBackgroundDrawable(new BitmapDrawable(context.getResources(),
										BitmapFactory.decodeByteArray(img, 0, img.length)));
					}
					else
					{
						Log.d(TAG, "mails.get(msg.what) not null but findView is null");
					}
				}
			}
		}
	};

	class ViewTag
	{
		RelativeLayout itemLayout;
		ImageView checkIcon;
		ImageView itemImg;
		TextView subject;
		TextView sendTime;
		ImageView newIcon;
		LinearLayout checkIconLayout;

		public ViewTag(RelativeLayout itemLayout, ImageView checkIcon, ImageView itemImg,
				TextView subject, TextView sendTime, ImageView newIcon, LinearLayout checkIconLayout)
		{
			this.checkIcon = checkIcon;
			this.itemImg = itemImg;
			this.subject = subject;
			this.sendTime = sendTime;
			this.newIcon = newIcon;
			this.checkIconLayout = checkIconLayout;
			this.itemLayout = itemLayout;
		}
	}

	private class DownlaodImageThread extends Thread
	{
		List<Mail> mails;
		int startPosition;
		int endPosition;

		public DownlaodImageThread(List<Mail> mails, int startPosition, int endPosition)
		{
			this.mails = mails;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}

		@Override
		public void run()
		{
			Log.d(TAG, "mails size === " + mails.size());
			Log.d(TAG, "startPosition === " + startPosition);
			Log.d(TAG, "endPosition === " + endPosition);
			for (int i = startPosition; i < endPosition; i++)
			{
				if (isInterrupt)
				{
					break;
				}
				if (!isPause)
				{
					int position = i - startPosition;
					if (position < mails.size())
					{
						Mail mail = mails.get(position);
						byte[] mailImg = mail.getSenderImg();
						if (mailImg == null)
						{
							mailImg = NetworkUtility.getWebImage(mail.getSenderImgLink());
							mailsAdapterData.updateImg(mail.getRowId(), mailImg);
							if (position < mails.size()) mails.get(position).setSenderImg(mailImg);
						}
						showImgHandler.sendMessage(showImgHandler.obtainMessage(i));
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			Log.d(TAG, "Download img finish() ");
		}
	}
}

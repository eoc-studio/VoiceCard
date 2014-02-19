package eoc.studio.voicecard.mailbox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eoc.studio.voicecard.R;
import eoc.studio.voicecard.utils.ListUtility;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MailsAdapterView extends BaseAdapter {
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
    
    public MailsAdapterView(Context context, List<Mail> mails, MailsAdapterData mailsAdapterData, ListView showMails) {
        this.context = context;
        this.mails = mails;
        this.mailsAdapterData = mailsAdapterData;
        this.showMails = showMails;
        
        layoutInflater = LayoutInflater.from(context);
        isPause = false;
        isInterrupt = false;
        selecedMail = new HashSet<String>();
    }

    @Override
    public int getCount() {
        return mails.size();
    }

    @Override
    public Object getItem(int position) {
        return mails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.mailbox_list_item, null);
            viewTag = new ViewTag((ImageView) convertView.findViewById(R.id.glb_mailbox_list_item_check_icon),
                    (ImageView) convertView.findViewById(R.id.glb_mailbox_list_item_img),
                    (TextView) convertView.findViewById(R.id.glb_mailbox_list_item_tv_subject),
                    (TextView) convertView.findViewById(R.id.glb_mailbox_list_item_tv_sendtime),
                    (ImageView) convertView.findViewById(R.id.glb_mailbox_list_item_new_icon));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ViewTag) convertView.getTag();
        }
        convertView.setId(ListUtility.BASE_INDEX + position);
        
        //checkIcon
        int checkState = mails.get(position).getCheckState();
        Log.d(TAG, "checkState is " + checkState);
        
        if (checkState == CHECKED) {
            viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_checkbox_check));
        } else {
            viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_checkbox));
        }
                
        //itemImg
        byte[] img = mails.get(position).getImg();
        if (img != null) {
            viewTag.itemImg.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        } else {
            viewTag.itemImg.setImageDrawable(context.getResources().getDrawable(R.drawable.user_reflect));
        }
        //subject
        viewTag.subject.setText(mails.get(position).getSubject());
        
        //sendTime
        viewTag.sendTime.setText(mails.get(position).sendTime());
        
        //newIcon
        int newState = mails.get(position).getNewState();
        if(newState == MailsAdapterData.NEW) {
            viewTag.newIcon.setVisibility(View.VISIBLE);
        } else {
            viewTag.newIcon.setVisibility(View.INVISIBLE);
        }
        
        setUIEvent(position, convertView, parent);
        
        return convertView;
    }
    
    private void setUIEvent(final int position, View convertView, ViewGroup parent) {
        viewTag.checkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkState = mails.get(position).getCheckState();
                
                Log.d(TAG, "click checkState is " + checkState);
                if (checkState == CHECKED) {
                    viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_checkbox));
                    mails.get(position).setCheckState(NOCHECKED);
                    selecedMail.remove(mails.get(position).getRowId());
                    count--;
                    ((MailboxActivity)context).showMailInfo(count);
                } else {
                    viewTag.checkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_checkbox_check));
                    mails.get(position).setCheckState(CHECKED);
                    selecedMail.add(mails.get(position).getRowId());
                    count++;
                    ((MailboxActivity)context).showMailInfo(count);
                }
                MailsAdapterView.this.notifyDataSetChanged();
            }
        });
    }
    
    public void selectAll() {
        for(Mail mail:mails) {
            mail.setCheckState(CHECKED);
            selecedMail.add(mail.getRowId());
        }
        MailsAdapterView.this.notifyDataSetChanged();
        count = mails.size();
        
        ((MailboxActivity)context).showMailInfo(count);
    }
    
    public Set<String> getSelectedItem() {
        return selecedMail;
    }
    
    public void clearData() {
        mails.clear();
        selecedMail.clear();
    }
    
    class ViewTag
    {
        ImageView checkIcon;
        ImageView itemImg;
        TextView subject;
        TextView sendTime;
        ImageView newIcon;

        public ViewTag(ImageView checkIcon, ImageView itemImg, TextView subject, TextView sendTime, ImageView newIcon)
        {
            this.checkIcon = checkIcon;
            this.itemImg = itemImg;
            this.subject = subject;
            this.sendTime = sendTime;
            this.newIcon = newIcon;
        }
    }
}

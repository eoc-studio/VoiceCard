package eoc.studio.voicecard.mailbox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.menu.ClearAllMail;
import eoc.studio.voicecard.menu.DeleteSelectedMail;

public class MailboxActivity extends BaseActivity {
    private TextView mailInfo;
    private ListView showMails;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);
        
        findViews();
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    private void findViews() {
        mailInfo = (TextView) findViewById(R.id.act_mailbox_tv_title_bar);
        showMails = (ListView) findViewById(R.id.act_mailbox_lv);
        
        Button selectAll = (Button) findViewById(R.id.act_mailbox_bt_title_bar);
        DeleteSelectedMail deleteSelectedMail = (DeleteSelectedMail) findViewById(R.id.act_mailbox_iv_menu_deleteSelectedMail);
        ClearAllMail clearAllMail = (ClearAllMail) findViewById(R.id.act_mailbox_iv_menu_clearAllMail);
    }
}

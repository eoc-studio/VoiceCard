package eoc.studio.voicecard.manager;

import java.util.ArrayList;


public interface GetMailListener
{
	void onResult(Boolean isSuccess,ArrayList<GsonSend> mails);
}

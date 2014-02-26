package eoc.studio.voicecard.manager;

import java.util.ArrayList;

public interface GetFacebookInfoListener
{
	void onResult(Boolean isSuccess,ArrayList<GsonFacebookUser> facebookUserList);
}

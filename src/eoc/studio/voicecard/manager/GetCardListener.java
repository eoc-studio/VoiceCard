package eoc.studio.voicecard.manager;

import java.util.ArrayList;

public interface GetCardListener
{
	void onResult(Boolean isSuccess,ArrayList<GsonCard> recommendItemList);
}

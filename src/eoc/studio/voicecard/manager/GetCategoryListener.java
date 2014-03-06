package eoc.studio.voicecard.manager;

import java.util.ArrayList;

public interface GetCategoryListener
{
	void onResult(Boolean isSuccess,ArrayList<GsonCategory> recommendItemList);
}

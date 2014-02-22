package eoc.studio.voicecard.manager;

import java.util.ArrayList;

public interface GetRecommendListener
{
	void onResult(Boolean isSuccess,ArrayList<GsonRecommend> recommendItemList);
}

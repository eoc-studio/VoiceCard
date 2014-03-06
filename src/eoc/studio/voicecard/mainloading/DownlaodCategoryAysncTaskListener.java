package eoc.studio.voicecard.mainloading;

import java.util.ArrayList;

import eoc.studio.voicecard.card.database.CategoryAssistant;

public interface DownlaodCategoryAysncTaskListener
{

	void processFinish(ArrayList<CategoryAssistant> result);
	
	
	
}

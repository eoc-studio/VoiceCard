package eoc.studio.voicecard.mainloading;

import java.util.ArrayList;

import eoc.studio.voicecard.card.database.CardAssistant;
public interface DownlaodCardAysncTaskListener
{


	void processFinish(ArrayList<CardAssistant> result);

	
}

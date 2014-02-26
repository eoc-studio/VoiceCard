package eoc.studio.voicecard.card.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import eoc.studio.voicecard.card.CardDraft;
import eoc.studio.voicecard.manager.HttpManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class CardDraftManager
{
	private static final String TAG = "CardDraftManager";

	private static CardDraftManager instance = null;

	private CardDraft cardDraft;

	private final String DRAFT_NAME = "Draft.json";

	private Context context;

	private String owner = "noOwner";

	public CardDraftManager()
	{
		
	}

	public static CardDraftManager getInstance()
	{

		if (instance == null)
		{
			instance = new CardDraftManager();
		}
		return instance;
	}

	public void init(Context context)
	{

		this.context = context.getApplicationContext();
	}

	public void resetOwner(){
		owner = "noOwner";
	}
	public void saveDraft(CardDraft cardDraft)
	{

		this.cardDraft = cardDraft;
		if (HttpManager.getFacebookID() != null)
		{
			Log.e(TAG, "owner: " + HttpManager.getFacebookID());
			owner = HttpManager.getFacebookID();
		}
		else
		{
			Log.e(TAG, "owner: " + owner);
		}

		File draftFolder = new File(context.getFilesDir(), owner);
		if (!draftFolder.exists())
		{
			if (draftFolder.mkdir())
			;

		}
		Log.e(TAG, "saveDraft() getMessage(): " + cardDraft.getMessage());
		Log.e(TAG, "saveDraft() getMessageTextColor(): " + cardDraft.getMessageTextColor());
		Log.e(TAG, "saveDraft() getMessageTextSizeType(): " + cardDraft.getMessageTextSizeType());
		Log.e(TAG, "saveDraft() getImage(): " + cardDraft.getImagePath());
		Log.e(TAG, "saveDraft() getSignDraftImage(): " + cardDraft.getSignDraftImagePath());
		Log.e(TAG, "saveDraft() getSignHandwriting(): " + cardDraft.getSignHandwritingPath());
		Log.e(TAG, "saveDraft() getSignPositionInfo(): " + cardDraft.getSignPositionInfoPath());
		Log.e(TAG, "saveDraft() getSound(): " + cardDraft.getSoundPath());
		Log.e(TAG, "saveDraft() getSoundDuration(): " + cardDraft.getSoundDuration());

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(this.cardDraft);
		File file = new File(draftFolder.getPath() + "/" + DRAFT_NAME);
		if (file.exists()) file.delete();
		try
		{

			FileWriter writer = null;
			try
			{
				writer = new FileWriter(file);

				writer.write(json);

				writer.close();

			}
			catch (IOException e)
			{
				e.printStackTrace();

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}

	}

	public CardDraft openDraft() throws NullPointerException
	{

		if (HttpManager.getFacebookID() != null)
		{
			Log.e(TAG, "owner: " + HttpManager.getFacebookID());
			owner = HttpManager.getFacebookID();
		}
		else
		{
			Log.e(TAG, "owner: " + owner);
		}

		File draftFolder = new File(context.getFilesDir(), owner);
		File file = new File(draftFolder.getPath() + "/" + DRAFT_NAME);
		FileInputStream fIn;
		String json = ""; // Holds the text
		try
		{
			fIn = new FileInputStream(file);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";

			while ((aDataRow = myReader.readLine()) != null)
			{
				json += aDataRow;
			}
			myReader.close();
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		CardDraft cardDraft = gson.fromJson(json, CardDraft.class);
		Log.e(TAG, "openDraft() getCardId(): " + cardDraft.getCardId());
		Log.e(TAG, "openDraft() getMessage(): " + cardDraft.getMessage());
		Log.e(TAG, "openDraft() getMessageTextColor(): " + cardDraft.getMessageTextColor());
		Log.e(TAG, "openDraft() getMessageTextSizeType(): " + cardDraft.getMessageTextSizeType());
		Log.e(TAG, "openDraft() getImage(): " + cardDraft.getImagePath());
		Log.e(TAG, "openDraft() getSignDraftImage(): " + cardDraft.getSignDraftImagePath());
		Log.e(TAG, "openDraft() getSignHandwriting(): " + cardDraft.getSignHandwritingPath());
		Log.e(TAG, "openDraft() getSignPositionInfo(): " + cardDraft.getSignPositionInfoPath());
		Log.e(TAG, "openDraft() getSound(): " + cardDraft.getSoundPath());
		Log.e(TAG, "openDraft() getSoundDuration(): " + cardDraft.getSoundDuration());
		/*
		 * String ImagePath = "file://" + path; Uri uri = ;
		 */
		if (cardDraft.getImagePath() != null)
			cardDraft.setImageUri(Uri.parse("file://" + cardDraft.getImagePath()));
		if (cardDraft.getSignDraftImagePath() != null)
			cardDraft
					.setSignDraftImageUri(Uri.parse("file://" + cardDraft.getSignDraftImagePath()));
		if (cardDraft.getSignHandwritingPath() != null)
			cardDraft.setSignHandwritingUri(Uri.parse("file://"
					+ cardDraft.getSignHandwritingPath()));
		if (cardDraft.getSignPositionInfoPath() != null)
			cardDraft.setSignPositionInfoUri(Uri.parse("file://"
					+ cardDraft.getSignPositionInfoPath()));
		if (cardDraft.getSoundPath() != null)
			cardDraft.setSoundUri(Uri.parse("file://" + cardDraft.getSoundPath()));
		return cardDraft;
	}

	/*
	 * public class UriDeserializer implements JsonDeserializer<Uri> {
	 * 
	 * @Override public Uri deserialize(final JsonElement src, final Type
	 * srcType, final JsonDeserializationContext context) throws
	 * JsonParseException {
	 * 
	 * Log.e(TAG, "UriDeserializer src.getAsString(): "+src.getAsString());
	 * Log.e(TAG, "UriDeserializer srcType "+srcType); return
	 * Uri.parse(src.getAsString()); } }
	 */

}

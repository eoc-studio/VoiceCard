package eoc.studio.voicecard.card.editor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.CardCategory;
import eoc.studio.voicecard.card.Constant;
import eoc.studio.voicecard.card.FakeData;
import eoc.studio.voicecard.card.database.CardAssistant;
import eoc.studio.voicecard.card.database.CardDatabaseHelper;
import eoc.studio.voicecard.card.database.CategoryAssistant;
import eoc.studio.voicecard.menu.AddToFavorite;
import eoc.studio.voicecard.utils.FileUtility;

public class CardSelectorActivity extends BaseActivity
{
	private static final int INT_FAVORITE = -999;

	public static final String EXTRA_KEY_CATEGORY = "category";

	private static final String TAG = "CardSelector";

	private static Boolean isFavorite = false;

	// private CardCategory category;
	private CategoryAssistant category;

	private Card currentCenteredCard;

	private Gallery list;

	private ImageView empty;

	private ImageView title;

	private ImageView back;
	private TextView centerCardName;
	private AddToFavorite addToFavorite;

	private CardDatabaseHelper cardDatabaseHelper;

	private Context context;
	
	private String sendBackId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		context = getApplicationContext();
		getCategoryFromIntent();
		initSendBackId();
		initCardDataBase();
		initLayout();
		super.onCreate(savedInstanceState);
	}

	private void initCardDataBase()
	{

		cardDatabaseHelper = new CardDatabaseHelper(context);
		cardDatabaseHelper.open();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void getCategoryFromIntent()
	{

		Intent intent = getIntent();
		category = (CategoryAssistant) intent.getParcelableExtra(EXTRA_KEY_CATEGORY);

		isFavorite = (category.getCategoryID() == INT_FAVORITE) ? true : false;

		Log.d(TAG, "isFavorite: " + isFavorite);
		Log.d(TAG,
				"list card for category id: " + category.getCategoryID() + ",name: "
						+ category.getCategoryName());
		Toast.makeText(this, "CATEGORY: " + category.getCategoryName(), Toast.LENGTH_LONG).show();
	}
	
	private void initSendBackId()
	{
		Intent intent = getIntent();
		sendBackId = intent.getStringExtra(Constant.EXTRA_KEY_SENDBACK_ID);
	}

	private void initLayout()
	{

		setContentView(R.layout.activity_card_selector);
		findViews();
		updateByIsFavorite();
		initList();
		setListener();
	}

	private void setListener()
	{
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		list.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				currentCenteredCard = (Card) list.getItemAtPosition(position);
				centerCardName.setText(currentCenteredCard.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub

			}
		});
		list.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Card card = (Card) list.getItemAtPosition(position);
				if (card == currentCenteredCard)
				{
					startCardEditor(card);
				}
			}

		});
		addToFavorite.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				addToFavorite(currentCenteredCard);
			}

		});
	}

	private void findViews()
	{

		title = (ImageView) findViewById(R.id.act_card_selector_iv_title);
		back = (ImageView) findViewById(R.id.act_card_selector_iv_back);
		list = (Gallery) findViewById(R.id.act_card_selector_gl_list);
		empty = (ImageView) findViewById(R.id.act_card_selector_iv_empty);
		centerCardName = (TextView) findViewById(R.id.act_card_select_tv_name);
		addToFavorite = (AddToFavorite) findViewById(R.id.act_card_selector_adf_add_to_favorite);
	}

	private void updateByIsFavorite()
	{

		if (isFavorite)
		{
			title.setImageResource(R.drawable.title_collection);
			addToFavorite.setImageResource(R.drawable.menu_remove_col);
		}
		else
		{
			title.setImageResource(R.drawable.title_select_card);
			addToFavorite.setImageResource(R.drawable.menu_collect);
		}

	}

	private void initList()
	{

		// TODO get card list from Bruce
		List<Card> cards = getCartList(category);
		// List<Card> cards = FakeData.getCardList(category);
		Log.d(TAG, "get " + cards.size() + " cards from data provider");
		CardAdapter adapter = new CardAdapter(cards);
		list.setAdapter(adapter);
	}

	private List<Card> getCartList(CategoryAssistant category)
	{

		List<Card> cards = new ArrayList<Card>();
		ArrayList<CardAssistant> cardAssistantList;
		if (category.getCategoryID() == INT_FAVORITE)
		{
			cardAssistantList = cardDatabaseHelper.getEnabledFavoriteCardList(cardDatabaseHelper
					.getSystemDPI(context));
		}
		else
		{
			cardAssistantList = cardDatabaseHelper.getEnabledCardListByCategory(category,
					cardDatabaseHelper.getSystemDPI(context));
		}

		if (cardAssistantList != null)
		{
			for (int index = 0; index < cardAssistantList.size(); index++)
			{
				cards.add(new Card(cardAssistantList.get(index).getCardID(), category,
						cardAssistantList.get(index).getCardName(), cardAssistantList.get(index)
								.getCloseLocalPath(), cardAssistantList.get(index)
								.getOpenLocalPath(), cardAssistantList.get(index)
								.getCoverLocalPath(), cardAssistantList.get(index)
								.getLeftLocalPath(), cardAssistantList.get(index)
								.getRightLocalPath(), cardAssistantList.get(index)
								.getCardFontColor()));
			}
		}
		return cards;

	}

	private void addToFavorite(Card card)
	{

		if (isFavorite)
		{
			Log.d(TAG, "remove " + card.getId() + " from favorite");
			boolean isOK = cardDatabaseHelper.setNonFavoriteCardByCardID(card.getId());
			if (isOK)
			{
				List<Card> cards = getCartList(category);
				Log.d(TAG, "get " + cards.size() + " cards from data provider");
				if (cards.size() == 0)
				{
					list.setEmptyView(empty);
					list.setAdapter(null);

					centerCardName.setText("");
					centerCardName.invalidate();
				}
				else
				{
					CardAdapter adapter = new CardAdapter(cards);
					list.setAdapter(adapter);

				}
				list.invalidate();
			}
		}
		else
		{
			Log.d(TAG, "add " + card.getId() + " to favorite");
			boolean isOK = cardDatabaseHelper.setFavoriteCardByCardID(card.getId());
			if (isOK)
			{
				Log.d(TAG, "add " + card.getName() + " to favorite successful!");
				Toast.makeText(context, "add " + card.getName() + " to favorite successful!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void startCardEditor(Card card)
	{
		Log.d(TAG, "start editor for " + card.getName());
		Intent intent = new Intent(this, CardEditorActivity.class);

		Log.d(TAG, "start editor card.getId(): " + card.getId());
		intent.putExtra(CardEditorActivity.EXTRA_KEY_CARD_ID, card.getId());
		intent.putExtra(Constant.EXTRA_KEY_SENDBACK_ID, sendBackId);
		startActivity(intent);
	}

	private class CardAdapter extends BaseAdapter
	{

		private List<Card> list;

		public CardAdapter(List<Card> cards)
		{
			list = cards;
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Card getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return list.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				convertView = inflater.inflate(R.layout.view_card_list_item, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.glb_card_grid_item_iv_image);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			Card card = getItem(position);

			Bitmap img3dCoverBitmap = FileUtility.getBitmapFromPath(card.getImage3dCoverPath());

			FileUtility.setImageViewWithBitmap(holder.image, img3dCoverBitmap);

			return convertView;
		}

	}

	private class ViewHolder
	{
		ImageView image;
	}

}

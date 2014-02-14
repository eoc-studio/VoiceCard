package eoc.studio.voicecard.card.editor;

import java.util.List;

import android.content.Intent;
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
import eoc.studio.voicecard.card.FakeData;
import eoc.studio.voicecard.menu.AddToFavorite;

public class CardSelectorActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CATEGORY = "category";

	private static final String TAG = "CardSelector";

	private CardCategory category;
	private Card currentCenteredCard;

	private Gallery list;
	private ImageView back;
	private TextView centerCardName;
	private AddToFavorite addToFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getCategoryFromIntent();
		initLayout();
		super.onCreate(savedInstanceState);
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
		category = (CardCategory) intent.getSerializableExtra(EXTRA_KEY_CATEGORY);
		Log.d(TAG, "list card for category " + category.name());
		Toast.makeText(this, "CATEGORY: " + category.name(), Toast.LENGTH_LONG).show();
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_card_selector);
		findViews();
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
		back = (ImageView) findViewById(R.id.act_card_selector_iv_back);
		list = (Gallery) findViewById(R.id.act_card_selector_gl_list);
		centerCardName = (TextView) findViewById(R.id.act_card_select_tv_name);
		addToFavorite = (AddToFavorite) findViewById(R.id.act_card_selector_adf_add_to_favorite);
	}

	private void initList()
	{
		// TODO get card list from Bruce
		List<Card> cards = FakeData.getCardList(category);
		Log.d(TAG, "get " + cards.size() + " cards from data provider");
		CardAdapter adapter = new CardAdapter(cards);
		list.setAdapter(adapter);
	}

	private void addToFavorite(Card card)
	{
		// TODO tell Bruce to add card to favorite
		Log.d(TAG, "add " + card.getName() + " to favorite");
	}

	private void startCardEditor(Card card)
	{
		Log.d(TAG, "start editor for " + card.getName());
		Intent intent = new Intent(this, CardEditorActivity.class);
		intent.putExtra(CardEditorActivity.EXTRA_KEY_CARD_ID, card.getId());
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
			holder.image.setImageResource(card.getImage3dCoverResId());
			return convertView;
		}

	}

	private class ViewHolder
	{
		ImageView image;
	}

}

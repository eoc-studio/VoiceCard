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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.Card;
import eoc.studio.voicecard.card.CardCategory;
import eoc.studio.voicecard.card.FakeData;

public class CardSelectorActivity extends BaseActivity
{
	public static final String EXTRA_KEY_CATEGORY = "category";

	private static final String TAG = "CardSelector";

	private CardCategory category;

	private Gallery list;
	private ImageView back;
	private TextView centerCardName;

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
				centerCardName.setText(((Card) list.getItemAtPosition(position)).getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub

			}
		});
	}

	private void findViews()
	{
		back = (ImageView) findViewById(R.id.act_card_selector_iv_back);
		list = (Gallery) findViewById(R.id.act_card_selector_gl_list);
		centerCardName = (TextView) findViewById(R.id.act_card_select_tv_name);
	}

	private void initList()
	{
		// TODO get card list from Bruce
		List<Card> cards = FakeData.getCardList(category);
		CardAdapter adapter = new CardAdapter(cards);
		list.setAdapter(adapter);
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

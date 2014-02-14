package eoc.studio.voicecard.card.editor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.CardCategory;

public class CardCategorySelectorActivity extends BaseActivity implements OnItemClickListener
{
	private static final String TAG = "CardCategorySelector";
	private GridView categories;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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

	private void initLayout()
	{
		setContentView(R.layout.activity_card_category_selector);
		findViews();
		initCategories();
		setListener();
	}

	private void initCategories()
	{
		categories.setAdapter(new CategoryAdapter());
	}

	private void findViews()
	{
		categories = (GridView) findViewById(R.id.act_card_category_selector_gv_categories);
	}

	private void setListener()
	{
		categories.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		CardCategory category = (CardCategory) categories.getItemAtPosition(position);
		Log.d(TAG, "clicked: " + category.name());

		startCardSelector(category);
	}

	private class CategoryAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return CardCategory.values().length - 1; // don't contain
														// USER_FAVORITE
		}

		@Override
		public CardCategory getItem(int position)
		{
			return CardCategory.values()[position];
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;
			if (convertView == null)
			{
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				convertView = inflater.inflate(R.layout.view_card_category_grid_item, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.glb_card_category_grid_item_iv_image);
				holder.name = (TextView) convertView
						.findViewById(R.id.glb_card_category_grid_item_tv_name);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			CardCategory category = getItem(position);
			holder.image.setImageResource(category.getDrawableResource());
			holder.name.setText(category.getStringResource());
			return convertView;
		}

	}

	private class ViewHolder
	{
		ImageView image;
		TextView name;
	}

	private void startCardSelector(CardCategory category)
	{
		Intent intent = new Intent(this, CardSelectorActivity.class);
		intent.putExtra(CardSelectorActivity.EXTRA_KEY_CATEGORY, category);
		startActivity(intent);
	}

}

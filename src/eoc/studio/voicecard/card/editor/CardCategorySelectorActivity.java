package eoc.studio.voicecard.card.editor;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eoc.studio.voicecard.BaseActivity;
import eoc.studio.voicecard.R;
import eoc.studio.voicecard.card.CardDraft;
import eoc.studio.voicecard.card.Constant;
import eoc.studio.voicecard.card.database.CardDatabaseHelper;
import eoc.studio.voicecard.card.database.CategoryAssistant;
import eoc.studio.voicecard.menu.Favorite;
import eoc.studio.voicecard.menu.OpenDraft;
import eoc.studio.voicecard.utils.FileUtility;

public class CardCategorySelectorActivity extends BaseActivity implements OnItemClickListener
{
	private static final String TAG = "CardCategorySelector";
	private static final int INT_FAVORITE = -999;
	private Context context;
	private GridView categories;

	private CardDraftManager cardDraftManager;

	private OpenDraft openDraft;

	private Favorite favorite;

	private CardDatabaseHelper cardDatabaseHelper;

	ArrayList<CategoryAssistant> categoryAssistantList = new ArrayList<CategoryAssistant>();

	private String sendBackId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		context = getApplicationContext();

		initCardDataBase();
		categoryAssistantList = cardDatabaseHelper.getEnabledCategory(cardDatabaseHelper
				.getSystemDPI(context));
		Log.d(TAG, "categoryAssistantList: " + categoryAssistantList);
		initCardDraftManager();
		initSendBackId();
		initLayout();
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
	}

	@Override
	protected void onDestroy()
	{

		// TODO Auto-generated method stub
		super.onDestroy();
		cardDatabaseHelper.close();
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

	private void initCardDraftManager()
	{

		Log.d(TAG, "initCardDraftManager()");
		cardDraftManager = new CardDraftManager();
		cardDraftManager.init(getApplicationContext());
	}

	private void initCardDataBase()
	{

		cardDatabaseHelper = new CardDatabaseHelper(context);
		cardDatabaseHelper.open();
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

	private void initSendBackId()
	{
		Intent intent = getIntent();
		sendBackId = intent.getStringExtra(Constant.EXTRA_KEY_SENDBACK_ID);
	}

	private void findViews()
	{

		categories = (GridView) findViewById(R.id.act_card_category_selector_gv_categories);
		openDraft = (OpenDraft) findViewById(R.id.act_card_category_iv_menu_open_draft);
		favorite = (Favorite) findViewById(R.id.act_card_category_selector_iv_menu_favorite);
	}

	private void setListener()
	{

		categories.setOnItemClickListener(this);
		openDraft.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				try
				{
					CardDraft cardDraft = cardDraftManager.openDraft();
					Log.d(TAG, "openDraft - onClick()");
					Intent intent = new Intent(CardCategorySelectorActivity.this,
							CardEditorActivity.class);
					intent.putExtra(CardEditorActivity.EXTRA_KEY_CARD_DRAFT, cardDraft);
					intent.putExtra(CardEditorActivity.EXTRA_KEY_CARD_ID, cardDraft.getCardId());
					startActivity(intent);
				}
				catch (Exception e)
				{
					Log.d(TAG, "openDraft - openDraft error:" + e.toString());
				}
			}

		});

		favorite.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				try
				{
					if (cardDatabaseHelper.getEnabledFavoriteCardListCount() > 0)
					{
						Intent intent = new Intent(context, CardSelectorActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(CardSelectorActivity.EXTRA_KEY_CATEGORY,
								new CategoryAssistant(INT_FAVORITE));
						context.startActivity(intent);
					}
					else
					{
						Toast.makeText(context, getResources().getString(R.string.no_favorite_exist), Toast.LENGTH_LONG)
								.show();
					}

				}
				catch (Exception e)
				{
					Log.d(TAG, "favorite - openDraft error:" + e.toString());
				}
			}

		});
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{

		// CardCategory category = (CardCategory)
		// categories.getItemAtPosition(position);
		// Log.d(TAG, "clicked: " + category.name());

		CategoryAssistant category = (CategoryAssistant) categories.getItemAtPosition(position);
		Log.d(TAG, "clicked: " + category.getCategoryName());

		startCardSelector(category);
	}

	/*
	 * private class CategoryAdapter extends BaseAdapter {
	 * 
	 * @Override public int getCount() {
	 * 
	 * return CardCategory.values().length - 1; // don't contain //
	 * USER_FAVORITE }
	 * 
	 * @Override public CardCategory getItem(int position) {
	 * 
	 * return CardCategory.values()[position]; }
	 * 
	 * @Override public long getItemId(int position) {
	 * 
	 * return position; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) {
	 * 
	 * ViewHolder holder; if (convertView == null) { LayoutInflater inflater =
	 * LayoutInflater.from(getApplicationContext()); convertView =
	 * inflater.inflate(R.layout.view_card_category_grid_item, null); holder =
	 * new ViewHolder(); holder.image = (ImageView) convertView
	 * .findViewById(R.id.glb_card_category_grid_item_iv_image); holder.name =
	 * (TextView) convertView
	 * .findViewById(R.id.glb_card_category_grid_item_tv_name);
	 * convertView.setTag(holder); } else { holder = (ViewHolder)
	 * convertView.getTag(); } CardCategory category = getItem(position);
	 * holder.image.setImageResource(category.getDrawableResource());
	 * holder.name.setText(category.getStringResource()); return convertView; }
	 * 
	 * }
	 */

	private class CategoryAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{

			return categoryAssistantList.size(); // don't contain
													// USER_FAVORITE
		}

		@Override
		public CategoryAssistant getItem(int position)
		{

			return categoryAssistantList.get(position);
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

			CategoryAssistant category = getItem(position);
			Log.d(TAG, "category.getCategoryLoocalPath(): " + category.getCategoryLoocalPath());
			Log.d(TAG, "category.getCategoryName(): " + category.getCategoryName());

			Bitmap categoryBitmap = FileUtility.getBitmapFromPath(category.getCategoryLoocalPath());
			FileUtility.setImageViewWithBitmap(holder.image, categoryBitmap);

			holder.name.setText(category.getCategoryName());
			return convertView;
		}

	}

	private class ViewHolder
	{
		ImageView image;

		TextView name;
	}

	private void startCardSelector(CategoryAssistant category)
	{

		Intent intent = new Intent(this, CardSelectorActivity.class);
		intent.putExtra(CardSelectorActivity.EXTRA_KEY_CATEGORY, category);
		intent.putExtra(Constant.EXTRA_KEY_SENDBACK_ID, sendBackId);
		startActivity(intent);
	}

}

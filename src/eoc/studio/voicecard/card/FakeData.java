package eoc.studio.voicecard.card;

import java.util.ArrayList;
import java.util.List;

import eoc.studio.voicecard.R;

public class FakeData
{
	private static final List<Card> TEMP_DATA = initFakeData();
	private static final List<Integer> FAVORITE_CARDS_ID_LIST = new ArrayList<Integer>();

	private static List<Card> initFakeData()
	{
		List<Card> list = new ArrayList<Card>();
		list.add(new Card(0, CardCategory.BIRTHDAY, "�����L�ĥդ��", R.drawable.card02,
				R.drawable.card02_open, 0, 0, 0));
		list.add(new Card(1, CardCategory.BIRTHDAY, "�|��b�]����ı", R.drawable.card07,
				R.drawable.card07_open, 0, 0, 0));
		list.add(new Card(2, CardCategory.BIRTHDAY, "�����_�b�y���ڷR�A", R.drawable.card08,
				R.drawable.card08_open, 0, 0, 0));
		list.add(new Card(3, CardCategory.BIRTHDAY, "�t�Ϭy�P��", R.drawable.card11,
				R.drawable.card11_open, 0, 0, 0));
		list.add(new Card(4, CardCategory.BIRTHDAY, "���˸`�P�d", R.drawable.card14,
				R.drawable.card14_open, 0, 0, 0));
		return list;
	}

	public static List<Card> getCardList(CardCategory category)
	{
		return new ArrayList<Card>(TEMP_DATA);
	}

	public static Card getCard(int id)
	{
		return new Card(TEMP_DATA.get(id)); // because id equals to position now
	}

	public static boolean isFavorite(int cardId)
	{
		return FAVORITE_CARDS_ID_LIST.contains(cardId);
	}

	public static void addToFavorite(int cardId)
	{
		if (!isFavorite(cardId))
		{
			FAVORITE_CARDS_ID_LIST.add(cardId);
		}
	}

	public static void removeFromFavorite(int cardId)
	{
		FAVORITE_CARDS_ID_LIST.remove(Integer.valueOf(cardId));
	}
}

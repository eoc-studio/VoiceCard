package eoc.studio.voicecard.card;

import java.util.ArrayList;
import java.util.List;

import eoc.studio.voicecard.R;

public class FakeData
{
	public static List<Card> getCardList(CardCategory category)
	{
		List<Card> list = new ArrayList<Card>();
		list.add(new Card(0, CardCategory.BIRTHDAY, "飛牛無敵白日夢", R.drawable.card02,
				R.drawable.card02_open, 0, 0, 0));
		list.add(new Card(1, CardCategory.BIRTHDAY, "四更半夜不睡覺", R.drawable.card07,
				R.drawable.card07_open, 0, 0, 0));
		list.add(new Card(2, CardCategory.BIRTHDAY, "雙拼北半球之我愛你", R.drawable.card08,
				R.drawable.card08_open, 0, 0, 0));
		list.add(new Card(3, CardCategory.BIRTHDAY, "聖誕流星樹", R.drawable.card11,
				R.drawable.card11_open, 0, 0, 0));
		list.add(new Card(4, CardCategory.BIRTHDAY, "母親節賀卡", R.drawable.card14,
				R.drawable.card14_open, 0, 0, 0));
		return list;
	}
}

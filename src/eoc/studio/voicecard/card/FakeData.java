package eoc.studio.voicecard.card;

import java.util.ArrayList;
import java.util.List;

import eoc.studio.voicecard.R;

public class FakeData
{
	public static List<Card> getCardList(CardCategory category)
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
}

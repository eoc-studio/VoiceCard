package eoc.studio.voicecard.card;

import eoc.studio.voicecard.R;

public enum CardCategory
{

	MOTHERS_DAY, BIRTHDAY, VALENTINES_DAY, XMAS, HALLOWEEN, THANKS_NOTE, USER_FAVORITE;

	public int getDrawableResource()
	{
		switch (this)
		{
		case MOTHERS_DAY:
			return R.drawable.category_01;
		case BIRTHDAY:
			return R.drawable.category_02;
		case VALENTINES_DAY:
			return R.drawable.category_03;
		case XMAS:
			return R.drawable.category_04;
		case HALLOWEEN:
			return R.drawable.category_05;
		case THANKS_NOTE:
			return R.drawable.category_06;
		case USER_FAVORITE:
		default:
			return 0;
		}
	}

	public int getStringResource()
	{
		switch (this)
		{
		case MOTHERS_DAY:
			return R.string.mothers_day;
		case BIRTHDAY:
			return R.string.birthday;
		case VALENTINES_DAY:
			return R.string.valentines_day;
		case XMAS:
			return R.string.xmas;
		case HALLOWEEN:
			return R.string.halloween;
		case THANKS_NOTE:
			return R.string.thanks_note;
		case USER_FAVORITE:
		default:
			return 0;
		}
	}
}

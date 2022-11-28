package com.udn.hr.clock.test;

import com.udn.hr.clock.test.mylib.Constant;
import com.udn.hr.clock.test.mylib.DateUtil;
import com.udn.hr.clock.test.sqlite.CheckRec;

import java.util.ArrayList;
import java.util.Collections;

public class CheckRecList extends ArrayList<CheckRec>
	{
	private static final long serialVersionUID = 4716625119884483412L;

	boolean prtHasSeriallyCheckRecordOfThisDate(String checkDateYYYYMMDD, String checkType, int days)
		{
//		ApplicationEx.logD("######################################################");
//		ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): checkDateYYYYMMDD = " + checkDateYYYYMMDD);
//		ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): checkType = " + checkType);
//		ApplicationEx.logD("::");

		for (CheckRec checkRec : this)
			{
//			ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): checkRec.getCheckDate() = " + checkRec.getCheckDate());
//			ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): checkRec.getCheckType() = " + checkRec.getCheckType());
//			ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): checkRec.getSeriallyCheckDays() = " + checkRec.getSeriallyCheckDays());

			boolean b;

			switch (days)
				{
				case ApplicationEx.DAYS_5:
				case ApplicationEx.DAYS_6:
					b = checkRec.getCheckDate(DateUtil.FORMAT_YYYY_MM_DD_DASH).equals(checkDateYYYYMMDD) &&
						checkRec.getCheckType().equals(checkType);

					break;

//				case ApplicationEx.DAYS_6:
//					b = checkRec.getCheckDate(DateUtil.FORMAT_YYYY_MM_DD_DASH).equals(checkDateYYYYMMDD) &&
//					    checkRec.getCheckType().equals(checkType) &&
//					    checkRec.getSeriallyCheckDays() != ApplicationEx.DAYS_7;
//
//					break;

				default:
					b = false;
				}

			if (b)
//			if (checkRec.getCheckDate(DateUtil.FORMAT_YYYY_MM_DD_DASH).equals(checkDateYYYYMMDD) &&
//			    checkRec.getCheckType().equals(checkType) &&
//			    checkRec.getSeriallyCheckDays() != ApplicationEx.DAYS_7)
				{
//				ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): return true");

				return true;
				}
			}

//		ApplicationEx.logD("prtHasSeriallyCheckRecordOfThisDate(): return false");

		return false;
		}

	void sortWithCheckDate()
		{
		Collections.sort(this, new CheckRec.ComparatorCheckDate());
		}

//	public void sortWithCheckDateCheckType()
//		{
//		Collections.sort(this, new CheckRec.ComparatorCheckDateCheckType());
//		}

//	public void removeCheckInDay7Record()
//		{
//		List<CheckRec> removeList = new ArrayList<>();
//		boolean inCheckInDay7Process = false;
//
//		for (int i = size() - 1; i >= 0; i--)
//			{
//			CheckRec checkRec = get(i);
//
//			if (checkRec.getIsCheckInDay7() == DBManager.YES_1)
//				{
//				removeList.add(checkRec);
//
//				inCheckInDay7Process = true;
//				}
//			else if (inCheckInDay7Process)
//				{
//				if (checkRec.getCheckType().equals(DBManager.CHECK_TYPE_OUT))
//					removeList.add(checkRec);
//				else
//					inCheckInDay7Process = false;
//				}
//			}
//
//		for (CheckRec checkRec : removeList)
//			if (!remove(checkRec))
//				ApplicationEx.logE("!!!!!!!! removeCheckInDay7Record(): remove(checkRec) fail!");
//		}

	@Override
	public String toString()
		{
		String str = Constant.NULL_STRING;

		for (CheckRec checkRec : this)
			str += checkRec.toString() + Constant.LINE_FEED;

		return str;
		}
	}

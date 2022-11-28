package com.udn.hr.clock.test;

import android.content.Context;

import com.udn.hr.clock.test.mylib.DateUtil;
import com.udn.hr.clock.test.sqlite.CheckRec;
import com.udn.hr.clock.test.sqlite.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class CheckRecListInNDays extends CheckRecList
	{
	private static final long serialVersionUID = 3611455740937020963L;

	private class PrvCheckDateList extends ArrayList<String>
		{
		private static final long serialVersionUID = -5252641363220747916L;

		private final String mCheckType;

		PrvCheckDateList(String checkType, Date curCheckDate, int days)
			{
//			Util.logD("--------------------------------");

			mCheckType = checkType;

			for (int i = 1; i <= days; i++)
				{
				Date date = DateUtil.addDate(curCheckDate, Calendar.DATE, i * (-1));

				String checkDate = DateUtil.DATE_FORMATTER_YYYYMMDD_DASH.format(date);
//				String checkDate = CheckRec.CHECK_DATE_FORMATTER.format(date);

//				Util.logD("PrvCheckDateList(): checkDate = " + checkDate);

				add(checkDate);
				}
			}

		private boolean isSerially(int days)
			{
//			ApplicationEx.logD("isSerially(): size() = " + size());

			for (String checkDate : this)
				{
//				Util.logD("isSerially(): checkDate = " + checkDate);

				if (!prtHasSeriallyCheckRecordOfThisDate(checkDate, mCheckType, days))
//				if (!CheckRecListInNDays.this.prtHasSeriallyCheckRecordOfThisDate(mCheckType, checkDate))
					{
//					Util.logD("isSerially(): return false");

					return false;
					}
				}

//			Util.logD("isSerially(): return true");

			return true;
			}
		}

	private final String mCheckType;
	private final Date mCurCheckDate;

	private CheckRecListInNDays(Date curCheckDate, String checkType)
		{
//		ApplicationEx.logD("CheckRecListInNDays(): checkType = " + checkType);
//		ApplicationEx.logD("CheckRecListInNDays(): curCheckDate = " + CheckRec.CHECK_DATE_FORMATTER.format(curCheckDate));

		mCurCheckDate = curCheckDate;
		mCheckType = checkType;
		}

	static CheckRecListInNDays fromSqlite(Context context, String empId, Date curCheckDate, String checkType)
		{
//		ApplicationEx.logD("fromSqlite(): curCheckDate = " + CheckRec.CHECK_DATE_FORMATTER.format(curCheckDate));

		Date startDate = ApplicationEx.getStartDateOf7DaysList(curCheckDate);

//		Date date6 = DateUtil.addDate(curCheckDate, Calendar.DATE, ApplicationEx.DAYS_6 * -1);
//
//		Calendar calendar = Calendar.getInstance();
//
//		calendar.setTime(date6);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//
//		date6 = calendar.getTime();
//
//		ApplicationEx.logD("fromSqlite(): date6 = " + CheckRec.CHECK_DATE_FORMATTER.format(date6));

		CheckRecList checkRecList = DBManager.getInstance(context).getCheckRecListFor7DaysCheck(empId, checkType, CheckRec.CHECK_DATE_FORMATTER.format(startDate));
//		CheckRecList checkRecList = DBManager.getInstance(context).getCheckRecListFor7DaysCheck(empId, checkType, CheckRec.CHECK_DATE_FORMATTER.format(date6));

		CheckRecListInNDays checkRecListInNDays = new CheckRecListInNDays(curCheckDate, checkType);

		checkRecListInNDays.addAll(checkRecList);
		checkRecListInNDays.sortWithCheckDate();
//		checkRecListInNDays.sortWithCheckDateCheckType();

		return checkRecListInNDays;
		}

	boolean isSerially(int days)
		{
		return new PrvCheckDateList(mCheckType, mCurCheckDate, days).isSerially(days);
		}
	}

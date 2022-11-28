package com.udn.hr.clock.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.udn.hr.clock.test.mylib.Constant;
import com.udn.hr.clock.test.mylib.DateUtil;
import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.mylib.view.ImageViewEx;
import com.udn.hr.clock.test.mylib.view.ViewEx;
import com.udn.hr.clock.test.sqlite.CheckRec;
import com.udn.hr.clock.test.sqlite.CheckRecKey;
import com.udn.hr.clock.test.sqlite.DBManager;

import androidx.annotation.NonNull;

class CheckRecAdapter extends ArrayAdapter<CheckRec>
	{
	private static final int LAYOUT_ID = R.layout.list_item_check_rec_list;
	private static final int LAYOUT_ID_WITH_ADDR = R.layout.list_item_check_rec_list_with_addr;

    private final CheckRecList mList;
    private CheckRecKey mCheckRecKey;

	private final int LEN_5;

	CheckRecAdapter(Context context, CheckRecList list, CheckRecKey checkRecKey)
//	CheckRecAdapter(Context context, List<CheckRec> list, CheckRecKey checkRecKey)
    	{
    	super(context, LAYOUT_ID, list);

	    mList = list;

//	    Util.logD("CheckRecAdapter(): mList = \n" + mList);

//	    if (!Utility.getPrefIsDebug(getContext()))
////	    if (!Util.TEST_SHOW_CHECK_IN_DAY_7_RECORD)
//	        mList.removeCheckInDay7Record();

//    	mList.addAll(list);
    	mCheckRecKey = checkRecKey;

	    LEN_5 = Utility.dpsToPixels(context, 5);
    	}

    /* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount()
		{
    	if (mList == null)
    		return 0;
    	else
    		return mList.size();
		}
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@NonNull
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent)
    	{
//    	if (mList == null)
//    		return null;

    	View rootView;
    	
    	if (convertView == null)
    		{
    		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
    		    rootView = inflater.inflate(LAYOUT_ID, null);
		    else
			    rootView = inflater.inflate(LAYOUT_ID_WITH_ADDR, null);
    		}
    	else
    		rootView = convertView;
    	
    	CheckRec checkRec = mList.get(position);
    	
    	if (checkRec.getCheckType().equals(DBManager.CHECK_TYPE_IN))
    		((ImageView) rootView.findViewById(R.id.ivCheckType)).setImageResource(R.drawable.icon_gotowork);
    	else
    		((ImageView) rootView.findViewById(R.id.ivCheckType)).setImageResource(R.drawable.icon_gooffwork);
    	
    	((TextView) rootView.findViewById(R.id.tvCheckDate)).setText(checkRec.getCheckDate(DateUtil.FORMAT_YYYY_MM_DD_SLASH_HH_MM_24));
    	
    	final int HEIGHT_DP = 24;
    	final int HEIGHT_PX = Utility.dpsToPixels(getContext(), HEIGHT_DP);

	    if (!ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
		    {
		    new ImageViewEx((ImageView) rootView.findViewById(R.id.ivZZZUnusedAnchorList)).displayWithCustomizeHeight(getContext().getResources().getDrawable(R.drawable.icon_anchor_index), HEIGHT_PX);
		    ((TextView) rootView.findViewById(R.id.tvZZZUnusedCheckAddr)).setText(checkRec.composeDisplayLocationString(getContext()));
		    }

		TextView tvRemark = (TextView) rootView.findViewById(R.id.tvRemark);

	    String remark = checkRec.getRemark();

	    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
			if (remark.indexOf(CheckRec.REMARK_SEP) == 0)
				remark = remark.substring(CheckRec.REMARK_SEP.length());

		if (remark.isEmpty())
			tvRemark.setVisibility(View.GONE);
		else
			{
			tvRemark.setVisibility(View.VISIBLE);
			tvRemark.setText(remark);
			}

	    TextView tvIsSend = (TextView) rootView.findViewById(R.id.tvIsSend);
	    TextView tvIsCheckInDay7 = (TextView) rootView.findViewById(R.id.tvIsCheckInDay7);

		if (Utility.getPrefIsDebug(getContext()))
//    	if (MainActivity.getPrefIsDebug(getContext()).equals(Constant.YES))
    		{
    		final String FAIL_MARK = "X";
    		
    		String str;

		    switch(checkRec.getIsSend())
			    {
			    case DBManager.YES_1:
				    str = Constant.Y;

				    break;

			    case DBManager.NO_1:
				    str = Constant.N;

				    break;

			    case DBManager.FAIL_1:
				    str = FAIL_MARK;

				    break;

			    default:
				    str = Constant.QUESTION_MARK;
			    }

//    		if (checkRec.getIsSend() == DBManager.YES_1)
//    			str = Constant.YES;
//    		else if (checkRec.getIsSend() == DBManager.NO_1)
//    			str = Constant.NO;
//    		else if (checkRec.getIsSend() == DBManager.FAIL_1)
//    			str = FAIL_MARK;
//    		else
//    			str = Constant.QUESTION_MARK;

		    prvSetTextViewTextAndMarginLeft(tvIsSend, str, LEN_5);

//		    tvIsSend.setText(str);
//
//		    new ViewEx(tvIsSend).setMargins(LEN_5, 0, 0, 0);

//		    boolean isDays7;
//
//		    if (checkRec.getCheckType().equals(DBManager.CHECK_TYPE_IN))
//			    isDays7 = (checkRec.getSeriallyCheckInDays() == ApplicationEx.DAYS_7);
//		    else
//			    isDays7 = (checkRec.getSeriallyCheckOutDays() == ApplicationEx.DAYS_7);

		    switch (checkRec.getSeriallyCheckDays())
			    {
			    case ApplicationEx.DAYS_6:
				    final String MARK_6 = "6";

				    prvSetTextViewTextAndMarginLeft(tvIsCheckInDay7, MARK_6, LEN_5);

				    break;

			    case ApplicationEx.DAYS_7:
				    final String MARK_7 = "7";

				    prvSetTextViewTextAndMarginLeft(tvIsCheckInDay7, MARK_7, LEN_5);

				    break;

			    default:
				    prvSetTextViewTextAndMarginLeft(tvIsCheckInDay7, Constant.NULL_STRING, 0);
			    }

//		    if (checkRec.getSeriallyCheckDays() == ApplicationEx.DAYS_7)
////		    if (checkRec.getIsCheckInDay7() == DBManager.YES_1)
//			    {
//			    final String MARK_7 = "7";
//
//			    prvSetTextViewTextAndMarginLeft(tvIsCheckInDay7, MARK_7, LEN_5);
//			    }
//		    else
//			    prvSetTextViewTextAndMarginLeft(tvIsCheckInDay7, Constant.NULL_STRING, 0);
    		}
	    else
			{
			prvSetTextViewTextAndMarginLeft(tvIsSend, Constant.NULL_STRING, 0);

//			tvIsSend.setText(Constant.NULL_STRING);
//
//			new ViewEx(tvIsSend).setMargins(0, 0, 0, 0);

			prvSetTextViewTextAndMarginLeft(tvIsCheckInDay7, Constant.NULL_STRING, 0);
			}

		ImageView ivIsNew = (ImageView) rootView.findViewById(R.id.ivIsNew);
		
    	if ((mCheckRecKey != null) && (checkRec.getKey().equals(mCheckRecKey)))
			{
			ivIsNew.setVisibility(View.VISIBLE);

			new ImageViewEx(ivIsNew).displayWithCustomizeHeight(getContext().getResources().getDrawable(R.drawable.icon_new), HEIGHT_PX);
			}
    	else
			ivIsNew.setVisibility(View.GONE);
    	
    	return rootView;
    	}

	public static String removeRedundantAddress(String oldAddr)
		{
		final String TAIWAN = "台灣";
		
		int pos = oldAddr.indexOf(TAIWAN);
		
		if (pos < 0)
			return oldAddr;
		
		if (pos + TAIWAN.length() == oldAddr.length() )
			return oldAddr;

		return oldAddr.substring(pos + TAIWAN.length());
		}
	
	/*
	 * Private Methods
	 */

	private void prvSetTextViewTextAndMarginLeft(TextView textView, String text, int marginLeft)
		{
		textView.setText(text);

		new ViewEx(textView).setMargins(marginLeft, 0, 0, 0);
		}

	/*
	 * Private Methods End
	 */
	}

//package com.udn.hr.clock.test;
//
//import android.content.Context;
//
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//import com.udn.hr.clock.test.mylib.Constant;
//
//public class GoogleAnalyticsManager
//	{
//	public static final String TRACKING_ID = "UA-50134254-21";
//
//	//=============
//	// Screen Name
//	//=============
//	public static final String SCREEN_NAME_CHECK_REC = "/打卡紀錄";
//	public static final String SCREEN_NAME_CLOCK_FAIL = "/連線失敗提醒";
//	public static final String SCREEN_NAME_HOMEPAGE = "/首頁";
//	public static final String SCREEN_NAME_LOGIN_PAGE = "/登入頁";
//	//=================
//	// Screen Name End
//	//=================
//
//	//==========
//	// Category
//	//==========
//	public static final String CATEGORY_CLOCK = "打卡";
//	public static final String CATEGORY_MY_POS = "查詢位置";
//	//==============
//	// Category End
//	//==============
//
//	//========
//	// Action
//	//========
//	public static final String ACTION_CLOCK_IN_OUT = "上下班";
//	public static final String ACTION_CLOCK_LOC = "定位";
//
//	public static final String ACTION_MY_POS_LOC = "定位";
//	//============
//	// Action End
//	//============
//
//	//=======
//	// Label
//	//=======
//	public static final String LABEL_CLOCK_IN = "上班";
//	public static final String LABEL_CLOCK_OUT = "下班";
//
//	public static final String LABEL_LOC_SUCCESS = "定位成功/有地址";
//	public static final String LABEL_WITHOUT_ADDR = "定位成功/沒有地址";
//	public static final String LABEL_WITHOUT_LOC = "定位失敗";
//	//===========
//	// Label End
//	//===========
//
//	private static Tracker mTracker;
//
//	private GoogleAnalyticsManager(){}
//
//	public static String getLabelLocation(int locationStatus)
//		{
//		switch(locationStatus)
//			{
//			case ClockPara.LOCATION_STATUS_SUCCESS:
//				return LABEL_LOC_SUCCESS;
//
//			case ClockPara.LOCATION_STATUS_WITHOUT_ADDRESS:
//				return LABEL_WITHOUT_ADDR;
//
//			case ClockPara.LOCATION_STATUS_WITHOUT_LOCATION:
//				return LABEL_WITHOUT_LOC;
//
//			default:
//				return Constant.NULL_STRING; // Should not happened.
//			}
//		}
//
//	public static void sendGoogleAnalyticsPageView(Context context, String screenName)
//		{
////		MainActivity.logD("sendGoogleAnalyticsPageView(): " + screenName);
//
//		Tracker tracker = prvGetTracker(context);
//
//		tracker.setScreenName(screenName);
//		tracker.enableExceptionReporting(true);
//		tracker.enableAdvertisingIdCollection(true);
//
//		tracker.send(new HitBuilders.AppViewBuilder().build());
//		}
//
//	public static void sendGoogleAnalyticsEvent(Context context, String screenName, String category, String action)
//		{
//		sendGoogleAnalyticsEvent(context, screenName, category, action, Constant.NULL_STRING, 0);
//		}
//
//	public static void sendGoogleAnalyticsEvent(Context context, String screenName, String category, String action, String label)
//		{
//		sendGoogleAnalyticsEvent(context, screenName, category, action, label, 0);
//		}
//
//	public static void sendGoogleAnalyticsEvent(Context context, String screenName, String category, String action, String label, long value)
//		{
////		MainActivity.logD("sendGoogleAnalyticsEvent(): " + screenName + Constant.COMMA_SPACE1 +
////														   category + Constant.COMMA_SPACE1 +
////														   action + Constant.COMMA_SPACE1 +
////														   label + Constant.COMMA_SPACE1 +
////														   value);
//
//		Tracker tracker = prvGetTracker(context);
//
//		tracker.setScreenName(screenName);
//		tracker.enableExceptionReporting(true);
//		tracker.enableAdvertisingIdCollection(true);
//
//		tracker.send(new HitBuilders.EventBuilder().setCategory(category)
//												   .setAction(action)
//												   .setLabel(label)
//												   .setValue(value)
//												   .build());
//		}
//
//	private static Tracker prvGetTracker(Context context)
//		{
//		if (mTracker == null)
//			mTracker = GoogleAnalytics.getInstance(context).newTracker(TRACKING_ID);
//
//		return mTracker;
//		}
//	}

package com.udn.hr.clock.test;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;

import com.udn.hr.clock.test.mylib.Constant;
import com.udn.hr.clock.test.mylib.DateUtil;
import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.service.NotifyClockOutService;
import com.udn.hr.clock.test.service.NotifyClockOutService2;
import com.udn.hr.clock.test.service.UpdateService;

import java.util.Calendar;
import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDexApplication;

public class ApplicationEx extends MultiDexApplication
	{
	//=====================================================
	// Check These Symbolic Definition Before Distribution
	//=====================================================
	static final boolean TEST_CLOCK_FAIL = false;

	private static final long TEST_DELAY_CHECK_OUT_SECOND_9 = 0;
	private static final long TEST_DELAY_CHECK_OUT_SECOND_13 = 0;

	static final int TEST_LOGIN_INTERVAL = 0;
	static final int TEST_LOGIN_INTERVAL_UNIT = Calendar.HOUR;

	static final boolean TEST_NEED_LOGIN = false;

	//	static final boolean IMPLEMENT_CHECK_OVER_LOGIN_INTERVAL_WHEN_CLOCK_OUT_ONLY = true;

	@SuppressWarnings("FieldCanBeLocal")
	private static long TEST_NOTIFY_CLOCK_OUT_INTERVAL_9 = 0; // 10 * Constant.MILLIS_1_MINUTE;
	@SuppressWarnings("FieldCanBeLocal")
	private static long TEST_NOTIFY_CLOCK_OUT_INTERVAL_13 = 0; // 15 * Constant.MILLIS_1_MINUTE;

	//	public static boolean TEST_SHOW_CHECK_IN_DAY_7_RECORD = false;

	@SuppressWarnings("FieldCanBeLocal")
	private static long TEST_UPDATE_SERVICE_REPEAT_INTERVAL = 0; // 1 * Constant.MILLIS_1_MINUTE;
	static final boolean TEST_USE_FAKE_EMP_ID = false;
	//=========================================================
	// Check These Symbolic Definition Before Distribution End
	//=========================================================

	public static final int DAYS_5 = 5;
	public static final int DAYS_6 = 6;
	public static final int DAYS_7 = 7;
//	public static final int DAYS_UNKNOWN = -1;

	@SuppressWarnings("ConstantConditions")
	public static final long DELAY_CHECK_OUT_SECOND_9 = TEST_DELAY_CHECK_OUT_SECOND_9 != 0 ?
														TEST_DELAY_CHECK_OUT_SECOND_9 : (long) (9 * 60 * 60);

	@SuppressWarnings("ConstantConditions")
	public static final long DELAY_CHECK_OUT_SECOND_13 = TEST_DELAY_CHECK_OUT_SECOND_13 != 0 ?
														 TEST_DELAY_CHECK_OUT_SECOND_13 : (long) (13 * 60 * 60);

	static final boolean IMPLEMENT_HIDE_CHECK_ADDR = true;
	static final boolean IMPLEMENT_HIDE_POSITION_ICON = true;
	public static final boolean CHECK_REC_LIST_ACT_SHOW_7_DAYS_LIST = true;

	static final String FAKE_URL = "fake_url";

//	private static final String LOG_TAG_DEBUG = "Clock-Debug";
//	private static final String LOG_TAG_ERROR = "Clock-E";

	static final int LOGIN_INTERVAL = 30; // Days

	static final int NOTIFICATION_DEFAULT = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

	static final long NOTIFY_CLOCK_OUT_INTERVAL_9 = 9 * Constant.MILLIS_1_HOUR;
	static final long NOTIFY_CLOCK_OUT_INTERVAL_13 = 13 * Constant.MILLIS_1_HOUR;

	public static final String PREF_KEY_CHECK_IN_DAY_7_IMPLEMENT_DATE = "day7ImplementDate";
	public static final boolean TEST_DELETE_CHECK_REC_DURATION = false;
	static final String PREF_KEY_DO_NOT_ASK_LOCATION_PERMISSION = "doNotAskLocationPermission";
	static final String PREF_KEY_LOGIN_EMP_NAME = "loginEmpName";
	static final String PREF_KEY_LOGIN_EMP_NO = "loginEmpNo";
	static final String PREF_KEY_LOGIN_EMP_NO_LIST = "loginEmpNoList";
	static final String PREF_KEY_LOGIN_TIME = "loginTime";
	static final String PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13 = "notifyClockOutServiceBaseTimeMillis13";
	static final String PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9 = "notifyClockOutServiceBaseTimeMillis";
	static final String PREF_KEY_UPDATE_SERVICE_EXEC_LOG = "updateServiceExecLog";

	static final int REQUEST_CODE_NOTIFY_CLOCK_OUT_SERVICE_9 = 10000;
	static final int REQUEST_CODE_NOTIFY_CLOCK_OUT_SERVICE_13 = 10002;
	static final int REQUEST_CODE_UPDATE_SERVICE = 10001;

	public static final DateUtil.DateFormatterYYYYMMDD CHECK_IN_DAY_7_IMPLEMENT_DATE_FORMATTER = DateUtil.DATE_FORMATTER_YYYYMMDD;

	static void cancelNotifyClockOutService13(Context context)
		{
		Intent intent = new Intent(context, NotifyClockOutService2.class);
		PendingIntent pendingIntent;

		pendingIntent = PendingIntent.getService(context, REQUEST_CODE_NOTIFY_CLOCK_OUT_SERVICE_13, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		am.cancel(pendingIntent);

		MainActivity.removePrefNotifyClockOutServiceBaseTimeMillis13(context);
		}

	static void cancelNotifyClockOutService9(Context context)
		{
		Intent intent = new Intent(context, NotifyClockOutService.class);
		PendingIntent pendingIntent;

		pendingIntent = PendingIntent.getService(context, REQUEST_CODE_NOTIFY_CLOCK_OUT_SERVICE_9, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		am.cancel(pendingIntent);

		MainActivity.removePrefNotifyClockOutServiceBaseTimeMillis9(context);
		}

	public static void createNotification(Context context, String contentTitle, String contentText)
		{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			prvCreateNotification16(context, contentTitle, contentText);
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			prvCreateNotification11(context, contentTitle, contentText);
//		else
//			prvCreateNotification(context, contentTitle, contentText);
		}

//	public static void logD(String msg)
//		{
//		Log.e(LOG_TAG_DEBUG, msg);
//		}
//
//	public static void logE(String msg)
//		{
//		Log.e(LOG_TAG_ERROR, msg);
//		}

	@Override
	public void onCreate()
		{
//		Utility.logI("ApplicationEx.onCreate(): " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(new Date()));
//		Utility.logI("ApplicationEx.onCreate(): com.ericworking.ericlib.BuildConfig.VERSION_NAME = " + com.ericworking.ericlib.BuildConfig.VERSION_NAME);

		if (Utility.Preference.getInstance(this).getString(PREF_KEY_CHECK_IN_DAY_7_IMPLEMENT_DATE) == null)
			{
//			Utility.logD("onCreate(): PREF_KEY_CHECK_IN_DAY_7_IMPLEMENT_DATE = null");

			Utility.Preference.getInstance(this).putString(PREF_KEY_CHECK_IN_DAY_7_IMPLEMENT_DATE, CHECK_IN_DAY_7_IMPLEMENT_DATE_FORMATTER.format(new Date()));
			}

//		Utility.logD("onCreate(): PREF_KEY_CHECK_IN_DAY_7_IMPLEMENT_DATE = " + Utility.Preference.getInstance(this).getString(PREF_KEY_CHECK_IN_DAY_7_IMPLEMENT_DATE));

		super.onCreate();
		}

	public static Date getStartDateOf7DaysList(Date curDate)
		{
		Date date6 = DateUtil.addDate(curDate, Calendar.DATE, ApplicationEx.DAYS_6 * -1);

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date6);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
		}

	static void registerNotifyClockOutService13(Context context)
		{
//		Util.logD("registerNotifyClockOutService13(): Start");

		PendingIntent pendingIntent = prvPrepareClockOutService13PendingIntent(context);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

//		Util.logD("registerNotifyClockOutService13(): prvGetNotifyClockOutServiceIntervalMillis13() = " + prvGetNotifyClockOutServiceIntervalMillis13());

		long triggerAtMillis = SystemClock.elapsedRealtime() + prvGetNotifyClockOutServiceIntervalMillis13();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
		else
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);

		MainActivity.registerPrefNotifyClockOutServiceBaseTimeMillis13(context, System.currentTimeMillis());
		}

	static void registerNotifyClockOutService9(Context context)
		{
//		Util.logD("registerNotifyClockOutService9(): Start");

		PendingIntent pendingIntent = prvPrepareClockOutService9PendingIntent(context);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

//		Util.logD("registerNotifyClockOutService9(): prvGetNotifyClockOutServiceIntervalMillis9() = " + prvGetNotifyClockOutServiceIntervalMillis9());

		long triggerAtMillis = SystemClock.elapsedRealtime() + prvGetNotifyClockOutServiceIntervalMillis9();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
		else
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);

		MainActivity.registerPrefNotifyClockOutServiceBaseTimeMillis9(context, System.currentTimeMillis());
		}

	public static void registerNotifyClockOutServiceOnBoot13(Context context)
		{
		long baseMillis = MainActivity.getPrefNotifyClockOutServiceBaseTimeMillis13(context);

		if (baseMillis == 0)
			return;

		long intervalMillis = prvGetNotifyClockOutServiceIntervalMillis13();
		long pastMillis = System.currentTimeMillis() - baseMillis;

		intervalMillis -= pastMillis;

		if (intervalMillis >= Constant.MILLIS_1_MINUTE)
			{
			long triggerAtMillis;
			PendingIntent pendingIntent = prvPrepareClockOutService13PendingIntent(context);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

			triggerAtMillis = SystemClock.elapsedRealtime();

			triggerAtMillis += intervalMillis;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
				alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
			else
				alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
			}
		else
			{
			MainActivity.removePrefNotifyClockOutServiceBaseTimeMillis13(context);

			createNotification(context, context.getString(R.string.notify_clock_out_title_13), context.getString(R.string.notify_clock_out_content_13));
			}
		}

	public static void registerNotifyClockOutServiceOnBoot9(Context context)
		{
		long baseMillis = MainActivity.getPrefNotifyClockOutServiceBaseTimeMillis9(context);

		if (baseMillis == 0)
			return;

		long intervalMillis = prvGetNotifyClockOutServiceIntervalMillis9();
		long pastMillis = System.currentTimeMillis() - baseMillis;

		intervalMillis -= pastMillis;

		if (intervalMillis >= Constant.MILLIS_1_MINUTE)
			{
			long triggerAtMillis;
			PendingIntent pendingIntent = prvPrepareClockOutService9PendingIntent(context);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

			triggerAtMillis = SystemClock.elapsedRealtime();

			triggerAtMillis += intervalMillis;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
				alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
			else
				alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
			}
		else
			{
			MainActivity.removePrefNotifyClockOutServiceBaseTimeMillis9(context);

			createNotification(context, context.getString(R.string.notify_clock_out_title_9), context.getString(R.string.notify_clock_out_content_9));
			}
		}

	public static void registerUpdateService(Context context)
		{
		final long TRIGGER_AT_10_MINUTE = Constant.MILLIS_1_MINUTE * 10;
		final long INTERVAL_1_HOUR = Constant.MILLIS_1_HOUR;

		long triggerAtMillis;
		long intervalMillis;

		if (TEST_UPDATE_SERVICE_REPEAT_INTERVAL != 0)
			{
			triggerAtMillis = SystemClock.elapsedRealtime() + TEST_UPDATE_SERVICE_REPEAT_INTERVAL;
			intervalMillis = TEST_UPDATE_SERVICE_REPEAT_INTERVAL;
			}
		else
			{
			triggerAtMillis = SystemClock.elapsedRealtime() + TRIGGER_AT_10_MINUTE;
			intervalMillis = INTERVAL_1_HOUR;
			}

		PendingIntent pendingIntent = prvPrepareUpdateServicePendingIntent(context);

		((AlarmManager) context.getSystemService(ALARM_SERVICE)).setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
		}

	/*
	 * Private Methods
	 */

	static void prvCreateNotification11(Context context, String contentTitle, String contentText)
		{
		Intent intent = new Intent(context, MainActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

//		ApplicationEx.logD("prvCreateNotification11(): setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))");

		builder.setTicker(prvGetTicker(context))
			   .setAutoCancel(true)
			   .setDefaults(NOTIFICATION_DEFAULT)
			   .setSmallIcon(R.drawable.ic_launcher)
			   .setContentInfo(Constant.NULL_STRING)
			   .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
			   .setContentText(contentText)
			   .setContentTitle(contentTitle)
			   .setContentIntent(pendingIntent)
			   .setWhen(System.currentTimeMillis() + 10000);
//		builder.setTicker(prvGetTicker(context))
//				.setAutoCancel(true)
//				.setDefaults(NOTIFICATION_DEFAULT)
//				.setSmallIcon(R.drawable.ic_launcher)
//				.setContentInfo(Constant.NULL_STRING)
//				.setContentText(contentText)
//				.setContentTitle(contentTitle)
//				.setContentIntent(pendingIntent)
//				.setWhen(System.currentTimeMillis() + 10000);

		Notification notification = builder.build();

		NotificationManager mgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		mgr.notify(0, notification);
		}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	static void prvCreateNotification16(Context context, String contentTitle, String contentText)
		{
		Intent intent = new Intent(context, MainActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(intent);

		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher );

		Notification.Builder builder = new Notification.Builder(context);

//		ApplicationEx.logD("prvCreateNotification16(): setStyle(new Notification.BigTextStyle().bigText(contentText))");

		builder.setTicker(prvGetTicker(context))
			   .setAutoCancel(true)
			   .setDefaults(NOTIFICATION_DEFAULT)
			   .setSmallIcon(prvGetSmallIconResId())
			   .setLargeIcon(largeIcon)
			   .setContentInfo(Constant.NULL_STRING)
			   .setStyle(new Notification.BigTextStyle().bigText(contentText))
			   .setContentText(contentText)
			   .setContentTitle(contentTitle)
			   .setContentIntent(pendingIntent)
			   .setWhen(System.currentTimeMillis());
//		builder.setTicker(prvGetTicker(context))
//				.setAutoCancel(true)
//				.setDefaults(NOTIFICATION_DEFAULT)
//				.setSmallIcon(prvGetSmallIconResId())
//				.setLargeIcon(largeIcon)
//				.setContentInfo(Constant.NULL_STRING)
//				.setContentText(contentText)
//				.setContentTitle(contentTitle)
//				.setContentIntent(pendingIntent)
//				.setWhen(System.currentTimeMillis());

		Notification notification = builder.build();

		NotificationManager mgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		mgr.notify(0, notification);
		}

	static long prvGetNotifyClockOutServiceIntervalMillis13()
		{
		if (TEST_NOTIFY_CLOCK_OUT_INTERVAL_13 != 0)
			return TEST_NOTIFY_CLOCK_OUT_INTERVAL_13;
		else
			return NOTIFY_CLOCK_OUT_INTERVAL_13;
		}

	static long prvGetNotifyClockOutServiceIntervalMillis9()
		{
		if (TEST_NOTIFY_CLOCK_OUT_INTERVAL_9 != 0)
			return TEST_NOTIFY_CLOCK_OUT_INTERVAL_9;
		else
			return NOTIFY_CLOCK_OUT_INTERVAL_9;
		}

	static int prvGetSmallIconResId()
		{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.push : R.drawable.ic_launcher;
		}

	static String prvGetTicker(Context context)
		{
		return context.getString(R.string.app_name);
		}

	static PendingIntent prvPrepareClockOutService13PendingIntent(Context context)
		{
		Intent intent = new Intent(context, NotifyClockOutService2.class);

		return PendingIntent.getService(context, REQUEST_CODE_NOTIFY_CLOCK_OUT_SERVICE_13, intent, 0);
		}

	static PendingIntent prvPrepareClockOutService9PendingIntent(Context context)
		{
		Intent intent = new Intent(context, NotifyClockOutService.class);

		return PendingIntent.getService(context, REQUEST_CODE_NOTIFY_CLOCK_OUT_SERVICE_9, intent, 0);
		}

	static PendingIntent prvPrepareUpdateServicePendingIntent(Context context)
		{
		Intent intent = new Intent(context, UpdateService.class);

		return PendingIntent.getService(context, REQUEST_CODE_UPDATE_SERVICE, intent, 0);
		}

	/*
	 * Private Methods End
	 */
	}

//package com.udn.hr.clock.test;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.drawable.AnimationDrawable;
//import android.location.Location;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;
//import android.support.v4.content.ContextCompat;
//import android.text.InputType;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationServices;
//import com.udn.hr.clock.test.mylib.Constant;
//import com.udn.hr.clock.test.mylib.DateUtil;
//import com.udn.hr.clock.test.mylib.LocationController;
//import com.udn.hr.clock.test.mylib.OnClickCounter;
//import com.udn.hr.clock.test.mylib.StringEx;
//import com.udn.hr.clock.test.mylib.Utility;
//import com.udn.hr.clock.test.mylib.asynctask.AsyncGetAddressStatic;
//import com.udn.hr.clock.test.mylib.asynctask.AsyncHttpGetStatic;
//import com.udn.hr.clock.test.mylib.asynctask.AsyncHttpRequestResult;
//import com.udn.hr.clock.test.mylib.view.ViewEx;
//import com.udn.hr.clock.test.service.UpdateService;
//import com.udn.hr.clock.test.sqlite.CheckRec;
//import com.udn.hr.clock.test.sqlite.CheckRecKey;
//import com.udn.hr.clock.test.sqlite.DBManager;
//import com.udn.hr.clock.test.superior.GetSuperviseList;
//import com.udn.hr.clock.test.superior.SuperiorActivity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.lang.ref.WeakReference;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import Tools.ACache;
//
//public class MainActivity_Old extends Activity {
//    private static class PrvAsyncGetAddressClock extends AsyncGetAddressStatic<MainActivity_Old> {
//        private final Date mCheckDate;
//        private final ClockPara mClockPara;
//
//        private PrvAsyncGetAddressClock(MainActivity_Old act, Date checkDate, ClockPara clockPara) {
//            super(act);
//
//            this.mCheckDate = checkDate;
//            this.mClockPara = clockPara;
//
//            act.prvShowClockProgressDialog();
//        }
//
//        @Override
//        protected void onPostExecute(AsyncGetAddressResult result) {
////			super.onPostExecute(result);
//
//            MainActivity_Old act = mCRef.get();
//
//            if (act == null)
//                return;
//
//            if (result.isSuccess)
//                mClockPara.setCheckAddr(result.addr);
//            else
//                mClockPara.setCheckAddr(Constant.NULL_STRING);
//
//            if (mClockPara.getCheckType().equals(DBManager.CHECK_TYPE_IN))
//                act.prvDoClockIn(mCheckDate, mClockPara);
//            else
//                act.prvDoClockOut(mCheckDate, mClockPara);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // Don't show Progress-Dialog
//        }
//    }
//
//    private static class PrvAsyncGetAddressMyPos extends AsyncGetAddressStatic<Context> {
//        private PrvAsyncGetAddressMyPos(Context context) {
//            super(context);
//        }
//
//        @Override
//        protected void onPostExecute(AsyncGetAddressResult result) {
//            super.onPostExecute(result);
//
//            Context context = mCRef.get();
//
//            if (context == null)
//                return;
//
//            if (!result.isSuccess) {
//                Utility.showSimpleAlertDialog(context, context.getString(R.string.can_not_get_address_server_not_responded), null);
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(context, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_WITHOUT_ADDR);
//
//                return;
//            }
//
//            String title = context.getString(R.string.where_are_you);
//            String msg = result.addr + Constant.LINE_FEED + context.getString(R.string.location_fyi);
//
//            Utility.showSimpleAlertDialog(context, title, msg, null);
//
//            GoogleAnalyticsManager.sendGoogleAnalyticsEvent(context, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_LOC_SUCCESS);
//        }
//    }
//
//    private static class PrvAsyncHttpGet extends AsyncHttpGetStatic<MainActivity_Old> {
//        private final ClockPara mOriginClockPara;
//
//        private PrvAsyncHttpGet(int requestCode, MainActivity_Old act, ClockPara clockPara) {
//            super(requestCode, act);
////			super(requestCode, act, true, act.getString(R.string.please_wait));
//
//            mOriginClockPara = clockPara;
//
//            act.prvShowClockProgressDialog();
//        }
//
//        @Override
//        protected void onPostExecute(AsyncHttpRequestResult result) {
////			MainActivity.logD("onPostExecute(): Start");
////			MainActivity.logD("onPostExecute(): Start");
//            Log.d("PrvAsyncHttpGet", "result:" + result.isSuccess);
//            Log.d("PrvAsyncHttpGet", "content:" + result.content);
//
//            super.onPostExecute(result);
//
//            MainActivity_Old act = mCRef.get();
//
//            if (act == null)
//                return;
//
//            act.prvDismissClockProgressDialog();
//
//            if (result.isSuccess)
//                switch (mRequestCode) {
//                    case HTTP_REQUEST_CODE_CLOCK_IN:
//                    case HTTP_REQUEST_CODE_CLOCK_OUT:
//                        act.prvParseClockResult(result.content, mOriginClockPara);
//                        break;
//                }
//            else {
//                CheckRecKey checkRecKey = new CheckRecKey();
//
//                checkRecKey.setEmpId(mOriginClockPara.getEmpId());
//                checkRecKey.setCheckDate(CheckRec.formatCheckDate(mOriginClockPara.getCheckDateYYYYMMDDHHMMSS()));
//                checkRecKey.setCheckType(mOriginClockPara.getCheckType());
//                Log.d("PrvAsyncHttpGet", "STAR");
//                act.prvStartClockFailActivity(checkRecKey);
//            }
//        }
//    }
//
//    private class PrvConnectionCallbacks implements ConnectionCallbacks {
//        @Override
//        public void onConnected(Bundle connectionHint) {
////			Utility.logD("onConnected(): Start");
//
//            mLocationController.prepare(mGoogleApiClient, null, VALID_TIME_DELTA_MILLIS);
//            mLocationController.requestLocationUpdates();
//        }
//
//        @Override
//        public void onConnectionSuspended(int arg0) {
//        }
//    }
//
//    private static class PrvHandler extends Handler {
//        private static final int MSG_CLOCK = 0;
//
//        private WeakReference<MainActivity_Old> mMainActivityRef;
//
//        private PrvHandler(MainActivity_Old act) {
//            mMainActivityRef = new WeakReference<>(act);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            MainActivity_Old act = mMainActivityRef.get();
//
//            if (act == null)
//                return;
//
//            switch (msg.what) {
//                case MSG_CLOCK:
//                    prvDisplayClock(act.mTvClockDate, act.mTvClockTime);
//
//                    if (Utility.getPrefIsDebug(act)) {
//                        TextView tvUpdateServiceLog = act.findViewById(R.id.tvUpdateServiceLog);
//                        TextView tvLatLng = act.findViewById(R.id.tvLatLng);
//
//                        String text = act.getString(R.string.update_service_exe_log, Utility.Preference.getInstance(act).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG));
////						String text = act.getString(R.string.update_service_exe_log).replace("[updateServiceExeLog]", Utility.Preference.getInstance(act).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG));
//                        tvUpdateServiceLog.setText(text);
////						tvUpdateServiceLog.setText("UpdateService: " + Utility.Preference.getInstance(act).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG));
//
//                        Location loc = act.mLocationController.getCurrentLocation();
//
//                        if (loc == null)
//                            tvLatLng.setText(act.getString(R.string.unknown_location));
//                        else
//                            tvLatLng.setText(MainActivity_Old.composeLocationDisplayString(loc));
//                    }
//
//                    break;
//            }
//        }
//    }
//
//    private class PrvOnConnectionFailedListener implements OnConnectionFailedListener {
//        @Override
//        public void onConnectionFailed(ConnectionResult result) {
//        }
//    }
//
//    private class PrvTimer extends Timer {
//        private TimerTask mTimerTaskClock;
//
//        void cancelClock() {
//            if (mTimerTaskClock != null) {
//                mTimerTaskClock.cancel();
//                mTimerTaskClock = null;
//            }
//        }
//
//        void scheduleClock() {
//            cancelClock();
//
//            mTimerTaskClock = new TimerTask() {
//                @Override
//                public void run() {
//                    mHandler.sendEmptyMessage(PrvHandler.MSG_CLOCK);
//                }
//            };
//
//            schedule(mTimerTaskClock, Constant.MILLIS_1_SECOND, Constant.MILLIS_1_SECOND);
//        }
//    }
//
//    private static final int HTTP_REQUEST_CODE_CLOCK_IN = 1;
//    private static final int HTTP_REQUEST_CODE_CLOCK_OUT = 2;
//
//    private static final long VALID_TIME_DELTA_MILLIS = Constant.MILLIS_1_MINUTE * 10;
//
//    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
//
//    private static final int REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL = 1;
//    private static final int REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE = 2;
//    private static final int REQUEST_CODE_LOGIN_AFTER_LOGOUT = 3;
//    private static final int REQUEST_CODE_CHECK_REC_LIST = 4;
//    private static final int REQUEST_CODE_CLOCK_FAIL = 5;
//
//    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START = 1;
//    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION = 2;
//
//    //    private static final String URL_STR_LOGIN = "https://eip.udngroup.com/eip/sysuser/api/login.jsp?userId=[userId]&password=[password]";
//    private static final String URL_STR_LOGIN = "https://eip.udngroup.com/eip/sysuser/api/login-test.jsp?userId=[userId]&password=[password]";
//    private static final String URL_STR_CLOCK_IN_OUT = "http://lou.udn-device-dept.net/app-action-card/newMysqltest_get_info.php?SN=[SN]&device_type=92&empolyee_id=[employeeId]&check_date=[checkDate]&check_type=[checkType]&&device_id=[deviceId]&GPS_info=[GPSInfo]&check_address=[checkAddress]&remark=[remark]";
//
//    private GoogleApiClient mGoogleApiClient;
//    private LocationController mLocationController = new LocationController(MainActivity_Old.this);
//
//    //18*0823
//    private ImageView ivHelpItem;
//    private TextView mTvClockDate, tvClockDate1;
//    private TextView mTvClockTime;
//    private PrvTimer mTimer;
//    private PrvHandler mHandler;
//    private ProgressDialog mClockProg;
//    private ImageView employeeBT;
//    private ImageView supervisorBT;
//    //    private ScheduleAsyncTask scheduleAsyncTask;
//    private ScheduleAsyncTask1 scheduleAsyncTask1;
//    private GetSuperviseList getSuperviseList;
//    private PutLambdaRecive_Task putLambdaRecive_task;
//    private TextView today_schedule, today_schedule_from_api;
//
//    //19*0225
//    private WifiManager wifi;
//    private TextView wifiTest;
//    private ArrayList<String> wifiSSid = new ArrayList<>();
//    private ArrayList<HashMap<String, String>> arraylistScan = new ArrayList<HashMap<String, String>>();
//    //19*0306
//    private Date workInRange1;
//    private Date workInRange2;
//    private Date workOutRange1;
//    private Date workOutRange2;
//    private Date workOutRangeOverTime13;
//    //19*.0327
//    private TextView if_off_line, setEmpCounts;
//    private RelativeLayout lookSuperBt;
//    //19*.0506
//    private Boolean getNowDateIsTrue;
//
//    public static String composeHttpPostClockURL(ClockPara para) {
//        final String SN = "[SN]";//打卡機編碼
//        final String EMPLOYEE_ID = "[employeeId]";//員工編號
//        final String CHECK_DATE = "[checkDate]";//打卡時間
//        final String CHECK_TYPE = "[checkType]";//打卡型別
//        final String DEVICE_ID = "[deviceId]";//device_type
//        final String GPS_INFO = "[GPSInfo]";//GPS定位
//        final String CHECK_ADDRESS = "[checkAddress]";//地址打卡
//        final String REMARK = "[remark]";//備註
//
//        //todo : 新版打卡需要新增上傳的欄位(2019/02/20)
//        final String UNUSUAL_STATUS = "[unusualStatus]";//是否異常打卡 0=false 1=true
//        final String ADJUST_DATE = "[adjustDate]";//校正後打卡時間
//        final String ADJUST_COMMENT = "[adjustComment]";//打卡時間校正原因
//        final String WI_FISSID = "[Wi_FiSSID]";//使用WI-Fi打卡的SSID
//        final String SSID_CHECKIN = "[SSIDCheckin]";//是否使用公司Wi-Fi打卡 0=false 1=true
//
////		Utility.logD("composeHttpPostClockURL(): para.getGPSInfo() = " + para.getGPSInfo());
////		Utility.logD("composeHttpPostClockURL(): para.getCheckAddr() = " + para.getCheckAddr());
//
//        return URL_STR_CLOCK_IN_OUT.replace(SN, para.getSN())
//                .replace(EMPLOYEE_ID, para.getEmpId())
//                .replace(CHECK_DATE, Utility.encodeUrl(para.getCheckDateYYYYMMDDHHMMSS()))
//                .replace(CHECK_TYPE, para.getCheckType())
//                .replace(DEVICE_ID, para.getDeviceId())
//                .replace(GPS_INFO, para.getGPSInfo())
//                .replace(CHECK_ADDRESS, Utility.encodeUrl(para.getCheckAddr()))
//                .replace(REMARK, Utility.encodeUrl(para.getRemark()));
//    }
//
//    public static String composeHttpPostLoginURL(String userId, String password) {
//        final String USER_ID = "[userId]";
//        final String PASSWORD = "[password]";
//
//        return URL_STR_LOGIN.replace(USER_ID, Utility.encodeUrl(userId))
//                .replace(PASSWORD, Utility.encodeUrl(password));
//    }
//
//    public static String composeLocationDisplayString(Location loc) {
//        return loc.getLatitude() + Constant.COMMA_SPACE1 + loc.getLongitude();
//    }
//
//    public static LoginInfo getPrefLoginInfo(Context context) {
//        String empNo = Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO);
//
//        if (empNo == null)
//            return null;
//
//        String empName = Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NAME, Constant.NULL_STRING);
//        String time = Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_TIME, Constant.NULL_STRING);
//
//        Date loginDate = DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.parse(time);
//
//        if (loginDate == null)
//            return null;
//
//        LoginInfo info = new LoginInfo();
//
//        info.setEmpNo(empNo);
//        info.setEmpName(empName);
//        info.setLoginDate(loginDate);
//
//        return info;
//    }
//
//    public static long getPrefNotifyClockOutServiceBaseTimeMillis13(Context context) {
//        return Utility.Preference.getInstance(context).getLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13, 0);
//    }
//
//    public static long getPrefNotifyClockOutServiceBaseTimeMillis9(Context context) {
//        return Utility.Preference.getInstance(context).getLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9, 0);
//    }
//
//    public static void logout(Context context) {
//        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_LOGIN_EMP_NO);
//        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_LOGIN_EMP_NAME);
//        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_LOGIN_TIME);
//    }
//
//    public static ClockPara parseHttpPostClockResult(String content) {
//        final String DUPLICATE_ENTRY = "Duplicate entry";
//
//        ClockPara para = new ClockPara();
//
//        if (content.contains(DUPLICATE_ENTRY))
////		if (content.indexOf(DUPLICATE_ENTRY) >= 0)
//        {
//            para.setIsDuplicated(true);
//            return para;
//        }
//
//        try {
//            JSONObject jsonObj = new JSONObject(content);
//
//            para.setEmpId(jsonObj.getString("empolyee_id"));
//            para.setCheckDateYYYYMMDDHHMMSS(jsonObj.getString("check_date"));
//            para.setCheckType(jsonObj.getString("check_type"));
//            para.setDeviceId(jsonObj.getString("device_id"));
//            para.setGpsInfo(jsonObj.getString("GPS_info"));
//            para.setCheckAddr(jsonObj.getString("check_address"));
//            para.setRemark(jsonObj.getString("remark"));
//            //**0328
////            para.setmUnusualStatus();
//        } catch (JSONException e) {
//            Utility.logE("parseHttpPostClockResult(): " + e.toString());
//            return null;
//        }
//
//        return para;
//    }
//
//    public static void registerPrefUpdateServiceExecLog(Context context, String text) {
//        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG, text);
//    }
//
//    public static void registerPrefNotifyClockOutServiceBaseTimeMillis13(Context context, long millis) {
//        Utility.Preference.getInstance(context).putLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13, millis);
//    }
//
//    public static void registerPrefNotifyClockOutServiceBaseTimeMillis9(Context context, long millis) {
//        Utility.Preference.getInstance(context).putLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9, millis);
//    }
//
//    public static void registerPrefLoginEmpNoList(Context context, String empNo) {
//        StringEx strEx = new StringEx(Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO_LIST, Constant.NULL_STRING));
//
//        if (!strEx.hasThisString(empNo, Constant.COMMA))
//            Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO_LIST, strEx.add(empNo, Constant.COMMA).toString());
//    }
//
//    public static void registerPrefLoginInfo(Context context, String empNo, String empName) {
//        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO, empNo);
//        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_EMP_NAME, empName);
//        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_TIME, DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(new Date()));
//    }
//
//    public static void removePrefNotifyClockOutServiceBaseTimeMillis13(Context context) {
//        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13);
//    }
//
//    public static void removePrefNotifyClockOutServiceBaseTimeMillis9(Context context) {
//        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9);
//    }
//
//    public static void showFirstLoginAlertDialog(Context context, DialogInterface.OnClickListener posBtnListener) {
//        Utility.showSimpleAlertDialog(context, context.getString(R.string.first_login_msg_title), "有任何疑問，請洽人資室\n" +
//                "hr@udngroup.com", posBtnListener);
//    }
//
////	public static void startCheckRecListActivity(Activity act, boolean needLogin, CheckRecKey checkRecKey)
////		{
////		LoginInfo info = getPrefLoginInfo(act);
////
////		if (info == null)
////			return;
////
////		Intent intent = new Intent(act, CheckRecListActivity.class);
////
////		intent.putExtra(CheckRecListActivity.EXTRA_EMP_ID, info.getEmpNo());
////
////		if (checkRecKey != null)
////			intent.putExtra(CheckRecListActivity.EXTRA_CHECK_REC_KEY, checkRecKey);
////
////		intent.putExtra(CheckRecListActivity.EXTRA_NEED_LOGIN, needLogin);
////
////		act.startActivityForResult(intent, REQUEST_CODE_CHECK_REC_LIST);
////		}
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_CANCELED) {
//            switch (requestCode) {
//                case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL:
//                case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE:
//                case REQUEST_CODE_LOGIN_AFTER_LOGOUT:
//                    finish();
//                    break;
//
////				case REQUEST_CODE_LOGIN_FOR_OVER_INTERVAL:
////					if (!Util.IMPLEMENT_CHECK_OVER_LOGIN_INTERVAL_WHEN_CLOCK_OUT_ONLY)
////						finish();
////
////					break;
//            }
//
//            return;
//        }
//
//        switch (requestCode) {
//            case REQUEST_CODE_CHECK_REC_LIST:
//                prvOnActivityResultCheckRecList(data);
//                break;
//
//            case REQUEST_CODE_CLOCK_FAIL:
//                prvOnActivityResultClockFail(data);
//                break;
//
//            case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL:
//            case REQUEST_CODE_LOGIN_AFTER_LOGOUT:
////			case REQUEST_CODE_LOGIN_FOR_OVER_INTERVAL:
//                prvOnActivityResultLogin(false);
//                break;
//
//            case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE:
//                prvOnActivityResultLogin(true);
//                break;
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Window window = this.getWindow();
//// clear FLAG_TRANSLUCENT_STATUS flag:
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//// finally change the color
//        window.setStatusBarColor(ContextCompat.getColor(MainActivity_Old.this, R.color.Login_top_logo_bg));
//
//        //todo::測試DIALOG觸發用。
////        ImageView ivActivityTitle = findViewById(R.id.ivActivityTitle);
////        ivActivityTitle.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Dialog d = new ClockOutErrorDialog(MainActivity.this,  0, "調整班表", new ClockOutErrorDialog.PriorityListener2() {
////                    @Override
////                    public Date setChangeTime(Date s) {
////                        return null;
////                    }
////                });
////                d.show();
////            }
////        });
//
//        if_off_line = findViewById(R.id.if_off_line);
//        lookSuperBt = findViewById(R.id.lookSuperBt);
//        wifiTest = findViewById(R.id.get_wifi_ssid);
//        supervisorBT = findViewById(R.id.supervisorBT);
//        today_schedule = findViewById(R.id.today_schedule);
//        today_schedule_from_api = findViewById(R.id.today_schedule_from_api);
//        employeeBT = findViewById(R.id.employeeBT);
//        setEmpCounts = findViewById(R.id.setEmpCounts);
//        ivHelpItem = findViewById(R.id.ivHelpItem);
//        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        ivHelpItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFirstLoginAlertDialog(MainActivity_Old.this, null);
//            }
//        });
//        prvProcessView();
//        prvProcessControl();
//
//        if (!Utility.checkNetworkStatus(this)) {
//            if (ACache.get(this).getAsString("getWorkIn") != null && ACache.get(this).getAsString("getWorkOut") != null) {
//                String workIn = ACache.get(this).getAsString("getWorkIn");
//                String workOut = ACache.get(this).getAsString("getWorkOut");
//                //todo 判斷沒有網路後將上一次班表寫入。
//                today_schedule_from_api.setTextColor(getResources().getColor(R.color.Main_work_time_text));
//                today_schedule_from_api.setText(workIn + " - " + workOut);
//                getNowDateIsTrue = false;
//
//            } else {
//                today_schedule_from_api.setText("尚未取得班表資料");
//                getNowDateIsTrue = false;
//            }
//            if (ACache.get(this).getAsString("MainAnimation") != null) {
//                String s = ACache.get(this).getAsString("MainAnimation");
//                if (!s.equals("true")) {
//                    ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.btn_circle);
//                    ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
//                } else {
//                    ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
//                    ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.btn_circle);
//                }
//            }
//            Log.d("MainActivity", "目前網路狀態" + "沒網路");
//            if_off_line.setVisibility(View.VISIBLE);
//        } else {
//            Log.d("MainActivity", "目前網路狀態" + "有網路");
//            if_off_line.setVisibility(View.GONE);
//            getSchedule();
//
//
//        }
//        mHandler = new PrvHandler(this);
//
//        prvCheckLoginOnCreate();
//
////        checkWifiIsOpen();//確認歪壞有沒有開啟
////
////
////        if (wifi.isWifiEnabled() == true) {
//////            sCanWiFiResultsList();
////            if (sCanWiFiResultsList()) {
////                Log.d("MainActivity", "WiFi偵測在報系範圍內");
////                //todo::getWifiSSidIsInUDN
////            } else {
////                Log.d("MainActivity", "WiFi在報系範圍外");
////                //todo::getWifiSSidNotIsInUDN
////                reLoScanWiFiResults();
////            }
//
////        }
//
////		if (prvCheckLoginOnCreate())
////			if (!Util.IMPLEMENT_CHECK_OVER_LOGIN_INTERVAL_WHEN_CLOCK_OUT_ONLY)
////				prvCheckLoginInterval();
//
//        prvCheckStartUpdateService();
//
//        prvChangeAbnormal();
//        GoogleAnalyticsManager.sendGoogleAnalyticsPageView(this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE);
//        getWorkRange();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        Log.d("MainActivity", "啟動離線");
//        startService(new Intent(this, UpdateService.class));
//
//        super.onDestroy();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            };
//
//            DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                }
//            };
//
//            Utility.showSimpleAlertDialog(this, getString(R.string.confirm_exit), posListener, negListener);
//
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onPause() {
//        prvTimerDestroy();
//        Log.d("MainActivity", "啟動離線");
//        startService(new Intent(this, UpdateService.class));
//
//        super.onPause();
//    }
//
//    @SuppressLint("NewApi")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION:
//                if (Utility.checkRequestPermissionsResultGranted(grantResults)) {
//                    final ProgressDialog progDialog = Utility.showSimpleProgressDialog(this, true);
//
//                    mLocationController.afterGotPermission();
//                    mLocationController.requestLocationUpdates();
//
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            if (progDialog != null)
//                                progDialog.dismiss();
//
//                            prvOnClickIvMyPosition();
//                        }
//                    };
//
//                    new Handler().postDelayed(runnable, Constant.MILLIS_1_SECOND);
//                } else
////					{
////					mLocationController.setHasPermission(false);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                        if (!shouldShowRequestPermissionRationale(PERMISSION_LOCATION))
//                            Utility.Preference.getInstance(this).putBoolean(ApplicationEx.PREF_KEY_DO_NOT_ASK_LOCATION_PERMISSION, true);
////					}
//
//                break;
//
//            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START:
//                if (Utility.checkRequestPermissionsResultGranted(grantResults)) {
//                    mLocationController.afterGotPermission();
//                    mLocationController.requestLocationUpdates();
//                }
////				else
////					mLocationController.setHasPermission(false);
//
//                break;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        prvTimerCreate();
//        prvTimerScheduleClock();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        mLocationController.removeLocationUpdates();
//
//        mGoogleApiClient.disconnect();
//
//        super.onStop();
//    }
//
//    /*
//     * Private Methods
//     */
//
////	private void prvCheckCurrentLogin()
////		{
//////		final int INTERVAL_7 = 7;
////
////		LoginInfo info = getPrefLoginInfo(this);
////
////		if (info == null)
////			{
////			prvStartLoginActivity();
////			}
////		else
////			{
////			Date currentDate = new Date();
////			Date checkDate;
////
////			if (MainActivity.TEST_LOGIN_INTERVAL)
////				checkDate = DateUtil.addDate(info.getLoginDate(), Calendar.DATE, 1);
////			else
////				checkDate = DateUtil.addDate(info.getLoginDate(), Calendar.DATE, LOGIN_INTERVAL);
////
////			if (checkDate != null)
////				if (checkDate.compareTo(currentDate) > 0)
////					return;
////
////			DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////				{
////				@Override
////				public void onClick(DialogInterface dialog, int which)
////					{
////					prvStartLoginActivity();
////					}
////				};
////
////			Utility.showSimpleAlertDialog(this, getString(R.string.login_again), posListener);
////			}
////		}
//
//    private boolean prvCheckLoginOnCreate() {
//        LoginInfo info = getPrefLoginInfo(this);
//
//        if (info == null) {
//            prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE);
////			prvStartLoginActivity(REQUEST_CODE_LOGIN_1);
//
//            return false;
//        } else {
//            prvProcessControlLocationController();
//
//            return true;
//        }
//    }
//
//    private void checkWifiIsOpen() {
//        if (wifi.isWifiEnabled() == false) {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setMessage("歡迎使用聯合e打卡，開始使用內勤打卡前\n" +
//                    "建議您開啟 Wi-Fi 以便提供Wi-Fi 定位資訊");
//            dialog.setCancelable(false);
//            dialog.setNegativeButton("略過", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            dialog.setPositiveButton("開啟 Wi-Fi", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // TODO Auto-generated method stub
//                    wifi.setWifiEnabled(true);
//                    Log.d("MainActivity", "wifi開啟");
//                    //todo::測試用途。
//                    testPhoneNoNet();
//                }
//            });
//            dialog.show();
//        }
//    }
//
//    private boolean sCanWiFiResultsList() {
//        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context c, Intent intent) {
//                boolean success = intent.getBooleanExtra(
//                        WifiManager.EXTRA_RESULTS_UPDATED, false);
//                if (success) {
//                    scanSuccess();
//                } else {
//                    // scan failure handling
//                    scanFailure();
//                }
//            }
//        };
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        this.registerReceiver(wifiScanReceiver, intentFilter);
//        boolean success = wifi.startScan();
//        if (!success) {
//            // scan failure handling
//            scanFailure();
//        }
//        if (scanFailure()) {
//            return true;
//        }
//        return false;
//    }
//
//    private void scanSuccess() {
//        List<ScanResult> results = wifi.getScanResults();
//        Log.d("MainActivity", "scanSuccess_results:" + results);
//    }
//
//    private void testPhoneNoNet() {
//        //todo::測試機沒有網路，只能連無限，但是連了無限之後要等連上，所以寫了一個測試功能讓他連上後重新整理Activity...
//        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity_Old.this, "請稍後", "Please Wait...", true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(4000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    progressDialog.dismiss();
//                    Intent intent = new Intent(MainActivity_Old.this, MainActivity_Old.class);
//                    MainActivity_Old.this.startActivity(intent);
//                }
//            }
//        }).start();
//    }
//
//    private boolean scanFailure() {
//        // handle failure: new scan did NOT succeed
//        // consider using old scan results: these are the OLD results!
//        List<ScanResult> results = wifi.getScanResults();
////        Log.d("MainActivity", "scanFailure_results:" + results);
//        for (int i = 0; results.size() > i; i++) {
////            Log.d("MainActivity", "scanFailure_getSSid" + results.get(i).SSID);
//            switch (results.get(i).SSID) {
//                case "Udnpark-1F":
//                    wifiTest.setText("Udnpark-1F");
//                    return true;
//                case "Udnpark-2F":
//                    wifiTest.setText("Udnpark-2F");
//                    return true;
//                case "Udnpark-3F":
//                    wifiTest.setText("Udnpark-3F");
//                    return true;
//                case "Udnpark-4F":
//                    wifiTest.setText("Udnpark-4F");
//                    return true;
//                case "Udnpark-5F":
//                    wifiTest.setText("Udnpark-5F");
//                    return true;
//                case "Udnpark-6F":
//                    wifiTest.setText("Udnpark-6F");
//                    return true;
//                case "Udnpark-B1":
//                    wifiTest.setText("Udnpark-B1");
//                    return true;
//                case "LKUDN":
//                    wifiTest.setText("LKUDN");
//                    results.clear();
//                    return true;
//            }
//        }
//        wifiTest.setText("");
//        results.clear();
//        return false;
//    }
//
//    private void reLoScanWiFiResults() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setMessage("聯合e打卡在您目前位置未掃瞄到公司Wi-Fi \n" +
//                "請確認您的位置在公司內部Wi-Fi連線範圍內");
//        dialog.setCancelable(false);
//        dialog.setNegativeButton("略過", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        dialog.setPositiveButton("重新偵測", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//                if (wifi.isWifiEnabled()) {
//                    Intent intent = new Intent(MainActivity_Old.this, MainActivity_Old.class);
//                    MainActivity_Old.this.startActivity(intent);
//                }
//            }
//        });
//        dialog.show();
//    }
//
////	private boolean prvCheckLoginInterval()
////		{
////		LoginInfo info = getPrefLoginInfo(this);
////
////		if (info == null)
////			{
////			// Should not happened.
////
////			prvStartLoginActivity(REQUEST_CODE_LOGIN_FOR_OVER_INTERVAL);
////
////			return false;
////			}
////
////		Date currentDate = new Date();
////		Date checkDate;
////
////		if (Util.TEST_LOGIN_INTERVAL > 0)
//////		if (Util.TEST_LOGIN_INTERVAL)
////			{
////			checkDate = DateUtil.addDate(info.getLoginDate(), Util.TEST_LOGIN_INTERVAL_UNIT, Util.TEST_LOGIN_INTERVAL);
////			}
////		else
////			checkDate = DateUtil.addDate(info.getLoginDate(), Calendar.DATE, Util.LOGIN_INTERVAL);
////
////		MainActivity.logD("prvCheckLoginInterval(): info.getLoginDate() = " + DateUtil.DATE_FORMATER_YYYYMMDD_DASH_HHMMSS_24.format(info.getLoginDate()));
////		MainActivity.logD("prvCheckLoginInterval(): checkDate = " + DateUtil.DATE_FORMATER_YYYYMMDD_DASH_HHMMSS_24.format(checkDate));
////		MainActivity.logD("prvCheckLoginInterval(): currentDate = " + DateUtil.DATE_FORMATER_YYYYMMDD_DASH_HHMMSS_24.format(currentDate));
////
////		if (!Util.TEST_OVER_LOGIN_INTERVAL)
////			if (checkDate != null)
////				if (checkDate.compareTo(currentDate) > 0)
////					return true;
////
////		DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////			{
////			@Override
////			public void onClick(DialogInterface dialog, int which)
////				{
////				prvStartLoginActivity(REQUEST_CODE_LOGIN_FOR_OVER_INTERVAL);
////				}
////			};
////
////		Utility.showSimpleAlertDialog(this, getString(R.string.login_again), posListener);
////
////		return false;
////		}
//
//    private boolean prvCheckNeedLoginBeforeShowCheckRecListActivity() {
//        LoginInfo info = getPrefLoginInfo(this);
//
//        if (info == null) {
//            // Should not happened.
//
//            return true;
//        }
//
//        Date currentDate = new Date();
//        Date checkDate;
//
//        //noinspection ConstantConditions
//        if (ApplicationEx.TEST_LOGIN_INTERVAL > 0)
//            checkDate = DateUtil.addDate(info.getLoginDate(), ApplicationEx.TEST_LOGIN_INTERVAL_UNIT, ApplicationEx.TEST_LOGIN_INTERVAL);
//        else
//            checkDate = DateUtil.addDate(info.getLoginDate(), Calendar.DATE, ApplicationEx.LOGIN_INTERVAL);
//
////		Util.logD("prvCheckLoginInterval(): info.getLoginDate() = " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(info.getLoginDate()));
////		Util.logD("prvCheckLoginInterval(): checkDate = " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(checkDate));
////		Util.logD("prvCheckLoginInterval(): currentDate = " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(currentDate));
//
//        if (!ApplicationEx.TEST_NEED_LOGIN)
//            if (checkDate != null)
//                if (checkDate.compareTo(currentDate) > 0)
//                    return false;
//
////		prvLogout();
//
////		DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////			{
////			@Override
////			public void onClick(DialogInterface dialog, int which)
////				{
////				prvStartLoginActivity(REQUEST_CODE_LOGIN_FOR_OVER_INTERVAL);
////				}
////			};
////
////		Utility.showSimpleAlertDialog(this, getString(R.string.login_again), posListener);
//
//        return true;
//    }
//
//    private void prvCheckFirstLogin(final Runnable runThis) {
//        LoginInfo info = getPrefLoginInfo(this);
//
//        if (info != null) {
//            String empNoList = Utility.Preference.getInstance(this).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO_LIST, Constant.NULL_STRING);
//
//            if (!empNoList.contains(info.getEmpNo())) {
//                DialogInterface.OnClickListener onClickListener;
//
//                if (runThis != null)
//                    onClickListener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            runThis.run();
//                        }
//                    };
//                else
//                    onClickListener = null;
//
//                MainActivity_Old.showFirstLoginAlertDialog(MainActivity_Old.this, onClickListener);
//
//                registerPrefLoginEmpNoList(MainActivity_Old.this, info.getEmpNo());
//            } else if (runThis != null)
//                runThis.run();
//        }
//    }
//
//    private boolean prvCheckGPS() {
//        if (!mLocationController.checkPermission())
//            return false;
//
//        if (mLocationController.isLocationAvailable())
//            return true;
//        else {
//            Utility.showSimpleAlertDialog(this, getString(R.string.please_turn_on_gps), null);
//
//            return false;
//        }
//    }
//
//    private void prvCheckStartUpdateService() {
////		Util.logD("prvCheckStartUpdateService(): Start");
//
//        if (Utility.Preference.getInstance(this).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG) == null) {
////			Util.logD("prvCheckStartUpdateService(): Util.registerUpdateService(this)");
//
//            ApplicationEx.registerUpdateService(this);
//            Utility.Preference.getInstance(this).putString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG, Constant.NULL_STRING);
//        }
//    }
//
//    private String prvComposeClockDialogMsg(Date checkDate, ClockPara para, int seriallyCheckInDays, CheckRec curCheckInRec) {
//        String msg = DateUtil.formatDate(checkDate, DateUtil.FORMAT_YYYY_MM_DD_DASH_HH_MM_24);
//
//        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
//            switch (seriallyCheckInDays) {
//                case ApplicationEx.DAYS_7:
//                    msg += Constant.LINE_FEED_2 + getString(R.string.remind_days_7) + Constant.LINE_FEED;
//
//                    break;
//
//                case ApplicationEx.DAYS_6:
//                    msg += Constant.LINE_FEED_2 + getString(R.string.remind_days_6) + Constant.LINE_FEED;
//
//                    break;
//
//                default:
//                    msg += Constant.NULL_STRING;
//            }
//
//            if (curCheckInRec != null) {
//                Date checkInDate = CheckRec.CHECK_DATE_FORMATTER.parse(curCheckInRec.getCheckDate());
//
//                long secondDiff = DateUtil.dateDiff(checkInDate, para.getCheckDate(), Calendar.SECOND);
//
////				ApplicationEx.logD("prvComposeClockDialogMsg(): ApplicationEx.DELAY_CHECK_OUT_SECOND_9 = " + ApplicationEx.DELAY_CHECK_OUT_SECOND_9);
////				ApplicationEx.logD("prvComposeClockDialogMsg(): ApplicationEx.DELAY_CHECK_OUT_SECOND_13 = " + ApplicationEx.DELAY_CHECK_OUT_SECOND_13);
////				ApplicationEx.logD("prvComposeClockDialogMsg(): secondDiff = " + secondDiff);
//
//                if (secondDiff > ApplicationEx.DELAY_CHECK_OUT_SECOND_13)
//                    msg += Constant.LINE_FEED_2 + getString(R.string.clock_out_remind_string_13) + Constant.LINE_FEED;
//                else if (secondDiff > ApplicationEx.DELAY_CHECK_OUT_SECOND_9)
//                    msg += Constant.LINE_FEED_2 + getString(R.string.clock_out_remind_string_9) + Constant.LINE_FEED;
//            }
//        } else
//            switch (para.getLocationStatus()) {
//                case ClockPara.LOCATION_STATUS_SUCCESS:
//                    msg += Constant.LINE_FEED_2 + para.getCheckAddr() + getString(R.string.location_fyi);
//
//                    break;
//
//                case ClockPara.LOCATION_STATUS_WITHOUT_ADDRESS:
//                    msg += Constant.LINE_FEED_2 +
//                            MainActivity_Old.composeLocationDisplayString(para.getLoc()) + Constant.LINE_FEED +
//                            getString(R.string.can_not_get_address);
//
//                    break;
//
//                default:
//                    msg += Constant.LINE_FEED_2 + getString(R.string.unknown_location);
//            }
//
//        return msg;
//    }
//
//    private void prvDismissClockProgressDialog() {
////        if (mClockProg != null) {
//            mClockProg.dismiss();
//            mClockProg = null;
////        }
//    }
//
//    private static void prvDisplayClock(TextView tvClockDate, TextView tvClockTime) {
//        Date date = new Date();
//
////		tvClockDate.setText(DateUtil.formatDate(date, DateUtil.FORMAT_Y_M_D_SLASH));
//        tvClockDate.setText(getNowDate());
//        tvClockTime.setText(DateUtil.formatDate(date, DateUtil.FORMAT_HH_MM_24));
//    }
//
//    private static void prvDisplayClock1(TextView tvClockDate, TextView tvClockTime, TextView tvClockDate1) {
//        Date date = new Date();
//
////		tvClockDate.setText(DateUtil.formatDate(date, DateUtil.FORMAT_Y_M_D_SLASH));
//        tvClockDate.setText(getNowDate());
//        tvClockDate1.setText(getNowDate1());
//        tvClockTime.setText(DateUtil.formatDate(date, DateUtil.FORMAT_HH_MM_24));
//    }
//
//    private void prvDoClockIn(final Date checkDate, final ClockPara para) {
//        final int editTextId = ViewEx.generateViewId();
//        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_IN, para);
//
//        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                EditText editText = ((AlertDialog) dialog).findViewById(editTextId);
//
//                if (editText != null)
//                    para.setRemarkAddr(editText.getText().toString());
//
//                if (prvSaveCheckRec(para, seriallyCheckDays)) {
//                    prvHttpGetClockIn(para);
//                    prvProcessViewTvRecentClockInfo();
//
//                    ApplicationEx.cancelNotifyClockOutService9(MainActivity_Old.this);
//                    ApplicationEx.cancelNotifyClockOutService13(MainActivity_Old.this);
//
//                    ApplicationEx.registerNotifyClockOutService9(MainActivity_Old.this);
//                    ApplicationEx.registerNotifyClockOutService13(MainActivity_Old.this);
//                }
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_IN);
//
//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
//            }
//        };
//
//        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
//            }
//        };
//
////       todo : ************* WIFI打上班卡流程開始。*********************
//        Log.d("MainActivity", "執行上班按鈕:" + workInRange2);
//        Log.d("MainActivity", "執行上班按鈕:" + workOutRange2);
//        if (workInRange1 != null) {
//            if (wifi.isWifiEnabled()) {//<--todo:判斷有沒有開啟WIFI
//                if (sCanWiFiResultsList()) {//<--todo:判斷WIFI有沒有在公司範圍內
//                    if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {//todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
//                        Log.d("MainActivity", "WIFI有開+有在公司範圍內+上班時間範圍內");
//                        ACache.get(MainActivity_Old.this).put("unusualStatus", "0");//是否異常打卡0=f 1=t
//                        ACache.get(MainActivity_Old.this).put("SSIDCheckin", "1");
//                        String title = getString(R.string.confirm_clock_in);
//                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
//                        else
//                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                    } else {
//                        Log.d("MainActivity", "WIFI有開+有在公司範圍內+上班時間範圍外");
//                        Log.d("MainActivity", "目前要打卡的時間:" + checkDate);
//                        Log.d("MainActivity", "上班時間範圍:" + workInRange2);
//                        Log.d("MainActivity", "getNowDateIsTrue:" + getNowDateIsTrue);
//                        //todo: 進入異常原因選擇及時間調整流程。
//                        if (!getNowDateIsTrue){
//                            ACache.get(MainActivity_Old.this).put("SSIDCheckin", "1");
//                            ACache.get(MainActivity_Old.this).put("unusualStatus", "1");
//                            String title = getString(R.string.confirm_clock_in);
//                            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
//                            else
//                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                        }else {
//                            pvtOnWorkDialog();
//                            ACache.get(MainActivity_Old.this).put("SSIDCheckin", "1");
//                            ACache.get(MainActivity_Old.this).put("unusualStatus", "1");
//                        }
////                        pvtOnWorkDialog();
////                        ACache.get(MainActivity.this).put("SSIDCheckin", "1");
////                        ACache.get(MainActivity.this).put("unusualStatus", "1");
//
//                    }
//                } else {
//                    //todo::沒有在公司範圍內 啟動重選LOG
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setMessage("聯合e打卡未發現公司Wi-Fi \n" +
//                            "請確認您的位置在公司內部Wi-Fi連線範圍內");
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {//todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
//                                Log.d("MainActivity", "WIFI位置略過，上班時間範圍內");
//
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                                String title = getString(R.string.confirm_clock_in);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//
//
//                            } else {
//                                Log.d("MainActivity", "上班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOnWorkDialog();
//
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("重新偵測後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            if (wifi.isWifiEnabled()) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pvClockInTouch();//開啟之後在啟動打卡按鈕
//                                    }
//                                });
//                            }
//                        }
//                    });
//                    dialog.show();
//                }
//            } else {
//                //todo::沒有WIFI 啟動開啟diaLOG
////            checkWifiIsOpenDoClockIn();
//                if (!wifi.isWifiEnabled()) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setMessage("聯合e打卡發現，您目前並未開啟Wi-Fi\n" +
//                            "請開啟您的Wi-Fi並進行打卡");
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
////                        String title = getString(R.string.confirm_clock_in);
////                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
////
////                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
////                            Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
////                        else
////                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                            if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {//todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
//                                Log.d("MainActivity", "上班時間範圍內");
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                                String title = getString(R.string.confirm_clock_in);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//
//
//                            } else {
//                                Log.d("MainActivity", "上班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOnWorkDialog();
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            wifi.setWifiEnabled(true);
//                            Log.d("MainActivity", "wifi開啟");
//                            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity_Old.this, "請稍後", "稍等3秒鐘...", true);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(6000);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    } finally {
//                                        progressDialog.dismiss();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                pvClockInTouch();//開啟之後在啟動打卡按鈕
//                                            }
//                                        });
//
//                                    }
//                                }
//                            }).start();
//                        }
//                    });
//                    dialog.show();
//                }
//            }
//        } else {
//            getWorkRange();
//        }
////       todo : ************* WIFI打上班卡流程結束。*********************
//
////todo:0226改成WIFI用-先注解。
////        String title = getString(R.string.confirm_clock_in);
////        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
////
////        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
////            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
////        else
////            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//    }
//
////	private void prvDoClockIn(final Date checkDate, final ClockPara para)
////		{
//////		Util.logD("prvDoClockIn(): Start");
////
////		int seriallyCheckInDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_IN, para);
////
//////		CheckRecListInNDays checkRecListInNDays = CheckRecListInNDays.fromSqlite(MainActivity.this, para.getEmpId(), DBManager.CHECK_TYPE_IN, para.getCheckDate());
//////
//////		int seriallyCheckInDays;
//////
//////		if (checkRecListInNDays.isSerially(ApplicationEx.DAYS_6))
//////			seriallyCheckInDays = ApplicationEx.DAYS_7;
////////		else if (checkRecListInNDays.isSerially(ApplicationEx.DAYS_5))
////////			seriallyCheckInDays = ApplicationEx.DAYS_6;
//////		else
//////			seriallyCheckInDays = 0;
////
////		prvDoClockIn2(checkDate, para, seriallyCheckInDays);
//////		prvDoClockIn2(checkDate, para, -1, seriallyCheckInDays);
////		}
//
////	private void prvDoClockIn(final Date checkDate, final ClockPara para)
////		{
////		Util.logD("prvDoClockIn(): Start");
////
////		CheckRecListInNDays checkRecListInNDays = CheckRecListInNDays.fromSqlite(MainActivity.this, para.getEmpId(), para.getCheckDate());
////
////		if (checkRecListInNDays.isSerially(Util.DAYS_6))
////			{
////			Util.logD("prvDoClockIn(): checkRecListInNDays.isSerially(Util.DAYS_6)");
////
////			DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////				{
////				@Override
////				public void onClick(DialogInterface dialogInterface, int i)
////					{
////					prvDoClockIn2(checkDate, para, DBManager.YES_1);
////					}
////				};
////
////			prvDismissClockProgressDialog();
////
////			Utility.showSimpleAlertDialog(MainActivity.this, getString(R.string.remind_days_7), posListener);
////
////			return;
////			}
////
////		if (checkRecListInNDays.isSerially(Util.DAYS_5))
////			{
////			Util.logD("prvDoClockIn(): checkRecListInNDays.isSerially(Util.DAYS_5)");
////
////			DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////				{
////				@Override
////				public void onClick(DialogInterface dialogInterface, int i)
////					{
////					prvDoClockIn2(checkDate, para, DBManager.NO_1);
////					}
////				};
////
////			prvDismissClockProgressDialog();
////
////			Utility.showSimpleAlertDialog(MainActivity.this, getString(R.string.remind_days_6), posListener);
////
////			return;
////			}
////
////		prvDoClockIn2(checkDate, para, DBManager.NO_1);
////		}
//
////	private void prvDoClockIn2(Date checkDate, final ClockPara para)
//////	private void prvDoClockIn2(Date checkDate, final ClockPara para, final int isCheckInDay7, final int seriallyCheckInDays)
////		{
////		final int seriallyCheckInDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_IN, para);
////
////		final int editTextId = ViewEx.generateViewId();
////
////		DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////			{
////			@Override
////			public void onClick(DialogInterface dialog, int which)
////				{
////				EditText editText = (EditText) ((AlertDialog) dialog).findViewById(editTextId);
////
////				if (editText != null)
////					para.setRemarkAddr(editText.getText().toString());
////
//////				int isCheckInDay7 = seriallyCheckInDays == ApplicationEx.DAYS_7 ? DBManager.YES_1 : DBManager.NO_1;
////
////				if (prvSaveCheckRec(para, seriallyCheckInDays, 0))
//////				if (prvSaveCheckRec(para, isCheckInDay7))
////					{
////					prvHttpGetClockIn(para);
////					prvProcessViewTvRecentClockInfo();
////
////					ApplicationEx.cancelNotifyClockOutService9(MainActivity.this);
////					ApplicationEx.cancelNotifyClockOutService13(MainActivity.this);
////
////					ApplicationEx.registerNotifyClockOutService9(MainActivity.this);
////					ApplicationEx.registerNotifyClockOutService13(MainActivity.this);
////					}
////
////				GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_IN);
////
////				String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());
////
////				GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
////				}
////			};
////
////		DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener()
////			{
////			@Override
////			public void onClick(DialogInterface dialogInterface, int i)
////				{
////				prvDismissClockProgressDialog();
////				}
////			};
////
////		String title = getString(R.string.confirm_clock_in);
////		String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckInDays);
////
////		if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
////			Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
////		else
////			prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
////		}
//
//    private void prvDoClockOut(final Date checkDate, final ClockPara para) {
//        final int editTextId = ViewEx.generateViewId();
//        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_OUT, para);
//
//        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                EditText et = ((AlertDialog) dialog).findViewById(editTextId);
//
//                if (et != null)
//                    para.setRemarkAddr(et.getText().toString());
//
//                if (prvSaveCheckRec(para, seriallyCheckDays))
////				if (prvSaveCheckRec(para, DBManager.NO_1))
//                {
//                    prvHttpGetClockOut(para);
//                    prvProcessViewTvRecentClockInfo();
//
//                    ApplicationEx.cancelNotifyClockOutService9(MainActivity_Old.this);
//                    ApplicationEx.cancelNotifyClockOutService13(MainActivity_Old.this);
//                }
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_OUT);
//
//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
//            }
//        };
//
//        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
//            }
//        };
//        // todo: *************WIFI打下班卡流程開始。*********************
//        if (workOutRange2 != null) {
//            if (wifi.isWifiEnabled()) {
//                if (sCanWiFiResultsList()) {
//                    if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
//                        Log.d("MainActivity", "下班時間範圍外");
//                        //todo: 進入異常原因選擇及時間調整流程。
//                        if (!getNowDateIsTrue){
//                            ACache.get(MainActivity_Old.this).put("SSIDCheckin", "1");
//                            ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                            String title = getString(R.string.confirm_clock_out);
//                            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
//
//                            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, posListener, negListener);
//                            else
//                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                        }else {
//                            pvtOffWorkDialog();
//                            ACache.get(MainActivity_Old.this).put("SSIDCheckin", "1");
//                            ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                        }
//
//                    } else {
//                        Log.d("MainActivity", "下班時間範圍內");
//                        ACache.get(MainActivity_Old.this).put("SSIDCheckin", "1");
//                        ACache.get(MainActivity_Old.this).put("unusualStatus", "0");//是否異常打卡0=f 1=t
//                        //todo::正常下班流程。
//                        String title = getString(R.string.confirm_clock_out);
//                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
//
//                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                            Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, posListener, negListener);
//                        else
//                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//
//                    }
//                } else {
//                    //todo::沒有在公司範圍內 啟動重選LOG
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setMessage("聯合e打卡未發現公司Wi-Fi \n" +
//                            "請確認您的位置在公司內部Wi-Fi連線範圍內");
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                            if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
//                                Log.d("MainActivity", "下班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOffWorkDialog();
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                            } else {
//                                Log.d("MainActivity", "下班時間範圍內");
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                                //todo::正常下班流程。
//                                String title = getString(R.string.confirm_clock_out);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity_Old.this).getCurrentCheckInRec(para.getEmpId()));
//
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("重新偵測後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            if (wifi.isWifiEnabled()) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pvClockOutTouch();//開啟之後在啟動打卡按鈕
//                                    }
//                                });
//                            }
//                        }
//                    });
//                    dialog.show();
//                }
//            } else {
//                //todo::沒有WIFI 啟動開啟LOG
////            checkWifiIsOpenDoClockIn();
//                if (!wifi.isWifiEnabled()) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setMessage("聯合e打卡發現，您目前並未開啟Wi-Fi\n" +
//                            "請開啟您的Wi-Fi並進行打卡");
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                            if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
//                                Log.d("MainActivity", "下班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOffWorkDialog();
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                            } else {
//                                Log.d("MainActivity", "下班時間範圍內");
//                                ACache.get(MainActivity_Old.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                                //todo::正常下班流程。
//                                String title = getString(R.string.confirm_clock_out);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity_Old.this).getCurrentCheckInRec(para.getEmpId()));
//
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            wifi.setWifiEnabled(true);
//                            Log.d("MainActivity", "wifi開啟");
//                            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity_Old.this, "請稍後", "稍等3秒鐘...", true);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(5000);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    } finally {
//                                        progressDialog.dismiss();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                pvClockOutTouch();//開啟之後在啟動打卡按鈕
//                                            }
//                                        });
//                                    }
//                                }
//                            }).start();
//                        }
//                    });
//                    dialog.show();
//                }
//            }
//        } else {
//            getWorkRange();
//        }
////       todo : ************* WIFI打下班卡流程結束。*********************
////        String title = getString(R.string.confirm_clock_out);
////        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
////
////        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
////            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
////        else
////            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//    }
//
//    private int prvGetSeriallyCheckDays(String checkType, ClockPara para) {
//        CheckRecListInNDays checkRecListInNDays = CheckRecListInNDays.fromSqlite(MainActivity_Old.this, para.getEmpId(), para.getCheckDate(), checkType);
//
//        int seriallyCheckDays;
//
//        if (checkRecListInNDays.isSerially(ApplicationEx.DAYS_6)) {
////			Utility.logD("prvGetSeriallyCheckDays(): checkRecListInNDays.isSerially(ApplicationEx.DAYS_6)");
//
//            seriallyCheckDays = ApplicationEx.DAYS_7;
//        } else if (checkRecListInNDays.isSerially(ApplicationEx.DAYS_5)) {
////			Utility.logD("prvGetSeriallyCheckDays(): checkRecListInNDays.isSerially(ApplicationEx.DAYS_5)");
//
//            if (DateUtil.getWeekDay(para.getCheckDate()) == Calendar.SATURDAY) {
////				Utility.logD("prvGetSeriallyCheckDays(): DateUtil.getWeekDay(para.getCheckDate()) == Calendar.SATURDAY");
//
//                seriallyCheckDays = ApplicationEx.DAYS_6;
//            } else
//                seriallyCheckDays = 0;
//        } else
//            seriallyCheckDays = 0;
//
////		Utility.logD("prvGetSeriallyCheckDays(): seriallyCheckDays = " + seriallyCheckDays);
//
//        return seriallyCheckDays;
//    }
//
//
//    private void prvHttpGetClockIn(final ClockPara clockPara) {
////        String url;
////        if (ApplicationEx.TEST_CLOCK_FAIL)
////            url = ApplicationEx.FAKE_URL;
////        else
////            url = MainActivity.composeHttpPostClockURL(clockPara);
////        Utility.logD("prvHttpGetClockIn(): url = " + url);
////        AsyncHttpRequestPara para = new AsyncHttpRequestPara(url);
////        new PrvAsyncHttpGet(HTTP_REQUEST_CODE_CLOCK_IN, this, clockPara).execute(para);
//
////todo:新版打卡上班透過lambda。
//        if (Utility.checkNetworkStatus(this)) {
//            putLambdaRecive_task = (PutLambdaRecive_Task) new PutLambdaRecive_Task(MainActivity_Old.this, clockPara, new PutLambdaRecive_Task.GetSeverResult() {
//                @Override
//                public void setSeverResult(String result) {
//                    Log.d("MainActivity", "上班打卡有無成功:" + result);
//                    if (result.equals("打卡成功")) {
//
//                        boolean needLogin = clockPara.getCheckType().equals(DBManager.CHECK_TYPE_IN) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
//                        Log.d("MainActivity", "needLogin:" + needLogin);
//                        prvStartCheckRecListActivity(needLogin, null);
//
//                        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
//                        TextView textView = view.findViewById(R.id.setTextId);
//                        textView.setText("打卡成功");
//                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
//                        Toast toast = new Toast(MainActivity_Old.this);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setView(view);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                    } else {
//                        CheckRecKey checkRecKey = new CheckRecKey();
//                        checkRecKey.setEmpId(clockPara.getEmpId());
//                        checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
//                        checkRecKey.setCheckType(clockPara.getCheckType());
//                        Log.d("打卡失敗", "STAR");
//                        prvStartClockFailActivity(checkRecKey);
//
//                    }
//                }
//            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            CheckRecKey checkRecKey = new CheckRecKey();
//            checkRecKey.setEmpId(clockPara.getEmpId());
//            checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
//            checkRecKey.setCheckType(clockPara.getCheckType());
//            Log.d("離線打卡", "STAR");
//            getDBtestList(clockPara.getEmpId());
//
//            prvStartClockFailActivity(checkRecKey);
//        }
//    }
//
//    private void prvHttpGetClockOut(final ClockPara clockPara) {
////        String url;
////
////        if (ApplicationEx.TEST_CLOCK_FAIL)
////            url = ApplicationEx.FAKE_URL;
////        else
////            url = MainActivity.composeHttpPostClockURL(clockPara);
////
////        AsyncHttpRequestPara para = new AsyncHttpRequestPara(url);
////
////        new PrvAsyncHttpGet(HTTP_REQUEST_CODE_CLOCK_OUT, this, clockPara).execute(para);
//        //todo:下班打卡上傳。
//        //todo:新版打卡下班透過lambda。
//        if (Utility.checkNetworkStatus(this)) {
//            putLambdaRecive_task = (PutLambdaRecive_Task) new PutLambdaRecive_Task(MainActivity_Old.this, clockPara, new PutLambdaRecive_Task.GetSeverResult() {
//                @Override
//                public void setSeverResult(String result) {
//                    Log.d("MainActivity", "下班打卡有無成功:" + result);
//                    if (result.equals("打卡成功")) {
//                        boolean needLogin = clockPara.getCheckType().equals(DBManager.CHECK_TYPE_OUT) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
//                        Log.d("MainActivity", "needLogin:" + needLogin);
//                        prvStartCheckRecListActivity(needLogin, null);
//
//                        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
//                        TextView textView = view.findViewById(R.id.setTextId);
//                        textView.setText("打卡成功");
//                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
//                        Toast toast = new Toast(MainActivity_Old.this);
//                        toast.setDuration(Toast.LENGTH_LONG);
//                        toast.setView(view);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
//                    } else {
//                        CheckRecKey checkRecKey = new CheckRecKey();
//                        checkRecKey.setEmpId(clockPara.getEmpId());
//                        checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
//                        checkRecKey.setCheckType(clockPara.getCheckType());
//                        Log.d("打卡失敗", "STAR");
//                        prvStartClockFailActivity(checkRecKey);
//
//                    }
//                }
//            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
//            CheckRecKey checkRecKey = new CheckRecKey();
//            checkRecKey.setEmpId(clockPara.getEmpId());
//            checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
//            checkRecKey.setCheckType(clockPara.getCheckType());
//            Log.d("離線打卡", "STAR");
//            getDBtestList(clockPara.getEmpId());
//
//            prvStartClockFailActivity(checkRecKey);
//        }
//    }
//
//    private void prvOnActivityResultCheckRecList(Intent data) {
//        if (data == null)
//            return;
//
//        boolean needLogin = data.getBooleanExtra(CheckRecListActivity.EXTRA_NEED_LOGIN, false);
//
//        if (needLogin)
//            prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL);
////			prvStartLoginActivity(REQUEST_CODE_LOGIN_1);
//    }
//
//    private void prvOnActivityResultClockFail(Intent data) {
//        if (data == null)
//            return;
//
//        CheckRecKey checkRecKey = (CheckRecKey) data.getSerializableExtra(ClockFailActivity.EXTRA_CHECK_REC_KEY);
//
//        if (checkRecKey != null)
//            prvStartCheckRecListActivity(prvCheckNeedLoginBeforeShowCheckRecListActivity(), checkRecKey);
//    }
//
//    private void prvOnActivityResultLogin(boolean needProcessLocController) {
//        Runnable runThis;
//
//        if (needProcessLocController)
//            runThis = new Runnable() {
//                @Override
//                public void run() {
//                    prvProcessControlLocationController();
//                }
//            };
//        else
//            runThis = null;
//
//        prvProcessViewTvHello();
//        prvProcessViewIvClockInOut(Utility.getDisplayMetrics(this));
//        prvProcessViewTvRecentClockInfo();
//
//        prvCheckFirstLogin(runThis);
//        getSchedule();
//    }
//
//    private void prvOnClickIvMyPosition() {
//        if (!prvCheckGPS())
//            return;
//
//        final Location loc = mLocationController.getCurrentLocation();
//
//        if (loc == null) {
//            Utility.showSimpleAlertDialog(MainActivity_Old.this, getString(R.string.unknown_location), null);
//            GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_WITHOUT_LOC);
//            return;
//        }
//
//        if (!Utility.checkNetworkStatus(MainActivity_Old.this)) {
//            Utility.showSimpleAlertDialog(MainActivity_Old.this, getString(R.string.can_not_get_address_no_network), null);
//            GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_WITHOUT_ADDR);
//            return;
//        }
//
//        AsyncGetAddressStatic.AsyncGetAddressPara para = new AsyncGetAddressStatic.AsyncGetAddressPara(loc.getLatitude(), loc.getLongitude());
//        new PrvAsyncGetAddressMyPos(MainActivity_Old.this).execute(para);
//    }
//
//    private void prvParseClockResult(String content, final ClockPara originClockPara) {
////		MainActivity.logD("prvParseClockResult(): content = " + content);
//
//        ClockPara para = MainActivity_Old.parseHttpPostClockResult(content);
//
//        if (para == null)
//            return;
//
//        final CheckRec checkRec;
//
//        if (para.isDuplicated())
//            checkRec = DBManager.getInstance(this).getCheckRec(originClockPara.getEmpId(), CheckRec.formatCheckDate(originClockPara.getCheckDateYYYYMMDDHHMMSS()), originClockPara.getCheckType());
//        else
//            checkRec = DBManager.getInstance(this).getCheckRec(para.getEmpId(), CheckRec.formatCheckDate(para.getCheckDateYYYYMMDDHHMMSS()), para.getCheckType());
//
//        final StringBuffer errMsg = new StringBuffer();
//
//        if (checkRec == null) {
//            Utility.logE("prvParseClockResult(): checkRec == null");
//            return;
//        }
//
//        if (para.isDuplicated())
//            checkRec.setIsSend(DBManager.FAIL_1);
//        else
//            checkRec.setIsSend(DBManager.YES_1);
//
//        if (!DBManager.getInstance(this).saveCheckRec(checkRec, errMsg)) {
//            Utility.showToast(this, errMsg.toString());
//            Utility.logE("prvParseClockResult(): " + errMsg.toString());
//        }
//
//        DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////				MainActivity.logD("onClick(): checkRec.getKey() = " + checkRec.getKey());
//                boolean needLogin = originClockPara.getCheckType().equals(DBManager.CHECK_TYPE_OUT) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
//                Log.d("MainActivity", "needLogin:" + needLogin);
//                Log.d("MainActivity", "checkRec.getKey():" + checkRec.getKey());
//                prvStartCheckRecListActivity(needLogin, checkRec.getKey());
//            }
//        };
//
//        Utility.showSimpleAlertDialog(this, getString(R.string.clock_success), posListener);
//    }
//
//    private ClockPara prvPrepareClockPara(Date checkDate, String checkType) {
//        LoginInfo info = getPrefLoginInfo(this);
//        //寫入欄位第方。
//        String adjustComment;
//        String Wi_FiSSID;
//        String SSIDCheckin;
//        String unusualStatus;
//        String adjustDate;
//        if (ACache.get(MainActivity_Old.this).getAsString("unusualStatus") != null) {
//            unusualStatus = ACache.get(MainActivity_Old.this).getAsString("unusualStatus");
//        } else {
//            unusualStatus = "";
//        }
//        if (ACache.get(MainActivity_Old.this).getAsString("adjustDate") != null) {
//            adjustDate = ACache.get(MainActivity_Old.this).getAsString("adjustDate");
//        } else {
//            adjustDate = "";
//        }
//        if (ACache.get(MainActivity_Old.this).getAsString("adjustComment") != null) {
//            adjustComment = ACache.get(MainActivity_Old.this).getAsString("adjustComment");
//        } else {
//            adjustComment = "";
//        }
//        if (ACache.get(MainActivity_Old.this).getAsString("Wi_FiSSID") != null) {
//            Wi_FiSSID = ACache.get(MainActivity_Old.this).getAsString("Wi_FiSSID");
//        } else {
//            Wi_FiSSID = "";
//        }
//        if (ACache.get(MainActivity_Old.this).getAsString("SSIDCheckin") != null) {
//            SSIDCheckin = ACache.get(MainActivity_Old.this).getAsString("SSIDCheckin");
//        } else {
//            SSIDCheckin = "";
//        }
//        Date date = new Date();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String adjustReceiveDate = input.format(date);
//        Log.d("MainActivity", adjustReceiveDate);
//        if (info == null)
//            return null;
//
//        ClockPara para = new ClockPara();
//
//        para.setEmpId(info.getEmpNo());
//        para.setCheckDateYYYYMMDDHHMMSS(checkDate);
//        para.setCheckType(checkType);
//        //0328
//        para.setmUnusualStatus(unusualStatus);
//        para.setmAdjustDate(adjustDate);
//        para.setmAdjustComment(adjustComment);
//        para.setmAdjustReceiveDate(adjustReceiveDate);
//        para.setmWi_FiSSID(Wi_FiSSID);
//        para.setmSSIDCheckin(SSIDCheckin);
//
//        para.setDeviceId(Build.SERIAL);
//
//        Location loc = mLocationController.getCurrentLocation();
//
//        if (loc != null) {
//            para.setLoc(loc);
//
//            DecimalFormat decimalFormat = new DecimalFormat("0.00000");
//
//            para.setGpsInfo(decimalFormat.format(loc.getLatitude()) + Constant.COMMA + decimalFormat.format(loc.getLongitude()));
//        }
//
//        return para;
//    }
//
//    private void prvProcessControl() {
//        DBManager.getInstance(this);
//
//        prvProcessControlGoogleApiClient();
//        prvProcessControlIvMyPosition();
//        prvProcessControlTvLogout();
//        prvProcessControlIvClockIn();
//        prvProcessControlIvClockOut();
//        prvProcessControlIvViewLog();
//        prvProcessControlTvClockTime();
//
////		prvProcessControlLocationController();
//    }
//
//    private void prvProcessControlGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(new PrvConnectionCallbacks())
//                .addOnConnectionFailedListener(new PrvOnConnectionFailedListener())
//                .addApi(LocationServices.API)
//                .build();
//    }
//
//    private void prvProcessControlIvClockIn() {
//        ImageView ivClockIn = findViewById(R.id.ivClockIn);
//
//
//        ivClockIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pvClockInTouch();
////                Date checkDate = new Date();
////
////                final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_IN);
////
////                if (para == null) {
////                    Utility.logE("ivClockIn.onClick(): para == null");
////
////                    return;
////                }
////
////                if (DBManager.getInstance(MainActivity.this).getCheckRec(para.getEmpId(), CheckRec.formatCheckDate(para.getCheckDateYYYYMMDDHHMMSS()), para.getCheckType()) != null) {
////                    Utility.showToast(MainActivity.this, R.string.clock_time_duplicated);
////
////                    return;
////                }
////
////                if (para.getLoc() == null)
////                    prvDoClockIn(checkDate, para);
////                else if (Utility.checkNetworkStatus(MainActivity.this)) {
////                    AsyncGetAddressStatic.AsyncGetAddressPara getAddrPara = new AsyncGetAddressStatic.AsyncGetAddressPara(para.getLoc().getLatitude(), para.getLoc().getLongitude());
////
////                    new PrvAsyncGetAddressClock(MainActivity.this, checkDate, para).execute(getAddrPara);
////                } else
////                    prvDoClockIn(checkDate, para);
//            }
//        });
//    }
//
//    private void prvProcessControlIvClockOut() {
//        ImageView ivClockOut = findViewById(R.id.ivClockOut);
//
//        ivClockOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////				if (Util.IMPLEMENT_CHECK_OVER_LOGIN_INTERVAL_WHEN_CLOCK_OUT_ONLY)
////					if (!prvCheckLoginInterval())
////						return;
//                pvClockOutTouch();
//            }
//        });
//    }
//
//    private void prvProcessControlIvMyPosition() {
//        ImageView ivMyPosition = findViewById(R.id.ivMyPosition);
//
//        if (ApplicationEx.IMPLEMENT_HIDE_POSITION_ICON) {
//            ivMyPosition.setVisibility(View.GONE);
//            return;
//        }
//
//        ViewEx.registerSimpleOnTouchVisualEffect(ivMyPosition);
////		Utility.registerSimpleOnTouchVisualEffect(ivMyPosition);
//
//        ivMyPosition.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onClick(View v) {
//                if (Utility.checkPermission(MainActivity_Old.this, PERMISSION_LOCATION))
//                    prvOnClickIvMyPosition();
//                else {
//                    if (Utility.Preference.getInstance(MainActivity_Old.this).getBoolean(ApplicationEx.PREF_KEY_DO_NOT_ASK_LOCATION_PERMISSION, false)) {
//                        String title = getString(R.string.you_did_not_grand_permission_loc);
//                        String msg = getString(R.string.how_to_grand_permission_loc);
//
//                        Utility.showSimpleAlertDialog(MainActivity_Old.this, title, msg, null);
//
//                        return;
//                    }
//
////					mLocationController.setIsRequestingPermission(true);
//
//                    if (shouldShowRequestPermissionRationale(PERMISSION_LOCATION)) {
//                        requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION);
//////						final String EXPLANATION = "need access fine location";
////
////						DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////							{
////							@Override
////							public void onClick(DialogInterface dialog, int which)
////								{
////								requestPermissions(new String[] {PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION);
////								}
////							};
////
////						String msg = getString(R.string.permission_loc_explanation);
////
////						Utility.showSimpleAlertDialog(MainActivity.this, msg, posListener, null);
//                    } else
//                        requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION);
//                }
//            }
//        });
//    }
//
//    private void prvProcessControlIvViewLog() {
//        ImageView ivViewLog = findViewById(R.id.ivViewLog);
//
//        ViewEx.registerSimpleOnTouchVisualEffect(ivViewLog);
////		Utility.registerSimpleOnTouchVisualEffect(ivViewLog);
//
//        ivViewLog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                prvStartCheckRecListActivity(false, null);
//            }
//        });
//    }
//
//    @SuppressLint("NewApi")
//    private void prvProcessControlLocationController() {
//        if (Utility.checkPermission(this, PERMISSION_LOCATION)) {
//            mLocationController.afterGotPermission();
////			mLocationController.prepare(true);
//            mLocationController.requestLocationUpdates();
//        } else {
////			mLocationController.setIsRequestingPermission(true);
//
//            if (shouldShowRequestPermissionRationale(PERMISSION_LOCATION)) {
//                requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START);
//
////				final String EXPLANATION = "need access fine location";
////
////				DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
////					{
////					@Override
////					public void onClick(DialogInterface dialog, int which)
////						{
////						requestPermissions(new String[] {PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START);
////						}
////					};
////
////				Utility.showSimpleAlertDialog(this, EXPLANATION, posListener);
//            } else
//                requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START);
//        }
//    }
//
//    private void prvProcessControlTvClockTime() {
//        OnClickCounter onClickCounter = new OnClickCounter(new OnClickCounter.DebugModeOnClickListenerCaller() {
//            @Override
//            public void onCheckCountAchieved() {
//                TextView tvUpdateServiceLog = findViewById(R.id.tvUpdateServiceLog);
//                TextView tvLatLng = findViewById(R.id.tvLatLng);
//
//                if (Utility.getPrefIsDebug(MainActivity_Old.this)) {
//                    Utility.showToast(MainActivity_Old.this, R.string.disable_debug_mode);
//                    Utility.registerPrefIsDebug(MainActivity_Old.this, false);
//
//                    tvUpdateServiceLog.setVisibility(View.GONE);
//                    tvLatLng.setVisibility(View.GONE);
//                } else {
//                    Utility.showToast(MainActivity_Old.this, R.string.enable_debug_mode);
//                    Utility.registerPrefIsDebug(MainActivity_Old.this, true);
//
//                    tvUpdateServiceLog.setVisibility(View.VISIBLE);
//                    tvLatLng.setVisibility(View.VISIBLE);
//                }
//
//                findViewById(R.id.rlClock).requestLayout();
//            }
//        }, Constant.DEBUG_CLICK_7);
//
//        findViewById(R.id.tvClockTime).setOnClickListener(onClickCounter);
//    }
//
//    private void prvProcessControlTvLogout() {
//        findViewById(R.id.tvLogout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logout(MainActivity_Old.this);
//
////				Utility.Preference.getInstance(MainActivity.this).remove(PREF_KEY_LOGIN_EMP_NO);
////				Utility.Preference.getInstance(MainActivity.this).remove(PREF_KEY_LOGIN_EMP_NAME);
////				Utility.Preference.getInstance(MainActivity.this).remove(PREF_KEY_LOGIN_TIME);
//
//                prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_LOGOUT);
////				prvStartLoginActivity(REQUEST_CODE_LOGIN_1);
//            }
//        });
//    }
//
//    private void prvProcessView() {
//        DisplayMetrics dispMetrics = Utility.getDisplayMetrics(this);
//
//        prvProcessViewIvClockBackground(dispMetrics.widthPixels);
//        prvProcessViewTvClock();
//        prvProcessViewTvHello();
//        prvProcessViewIvClockInOut(dispMetrics);
//        prvProcessViewTvRecentClockInfo();
//        prvProcessViewIvViewLog(dispMetrics.widthPixels);
//        prvProcessViewTvVersionInfo();
//
//        if (Utility.getPrefIsDebug(this)) {
//            findViewById(R.id.tvUpdateServiceLog).setVisibility(View.VISIBLE);
//            findViewById(R.id.tvLatLng).setVisibility(View.VISIBLE);
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    private void prvProcessViewIvClockBackground(int screenWidth) {
//        ImageView ivClockBackground = findViewById(R.id.ivClockBackground);
//
////		new ImageViewEx(ivClockBackground).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.bg_index), screenWidth);
////		new ViewEx(ivClockBackground).setAspect(screenWidth, (int) (screenWidth * 0.4));
//    }
//
//    private void prvProcessViewIvClockInOut(final DisplayMetrics dispMetrics) {
//        final LinearLayout llClockInOut = findViewById(R.id.llClockInOut);
//
//        llClockInOut.post(new Runnable() {
//            //			@SuppressWarnings("deprecation")
//            @Override
//            public void run() {
//                int height = (int) (llClockInOut.getHeight() * 0.5);
//                int width = (int) (dispMetrics.widthPixels * 0.5);
//
//                if (width > height)
//                    //noinspection SuspiciousNameCombination
//                    width = height;
//
//                int padWidth = (dispMetrics.widthPixels - (width * 2)) / 3;
//
////                new ViewEx(findViewById(R.id.vClockInOutPadder1)).setAspect(padWidth, height);
////                new ImageViewEx((ImageView) findViewById(R.id.ivClockIn)).displayWithCustomizeWidth(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_work_on_64px), width);
////                ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.splse);
////                AnimationDrawable animationDrawable;
////                animationDrawable = (AnimationDrawable) ((ImageView) findViewById(R.id.ivClockIn)).getBackground();
////
////                animationDrawable.start();
//
//// new ImageViewEx((ImageView) findViewById(R.id.ivClockIn)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.selector_iv_clock_in), width);
//
////                new ViewEx(findViewById(R.id.vClockInOutPadder2)).setAspect(padWidth, height);
////                new ImageViewEx((ImageView) findViewById(R.id.ivClockOut)).displayWithCustomizeWidth(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_work_off_64px), width);
////                ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
//// new ImageViewEx((ImageView) findViewById(R.id.ivClockOut)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.selector_iv_clock_out), width);
//            }
//        });
//    }
//
//    @SuppressWarnings("deprecation")
//    private void prvProcessViewIvViewLog(int screenWidth) {
////		new ImageViewEx((ImageView) findViewById(R.id.ivViewLog)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.btn_inquiry), (int) (screenWidth * 0.9));
//    }
//
//    @SuppressWarnings("deprecation")
//    private void prvProcessViewTvClock() {
//        mTvClockDate = findViewById(R.id.tvClockDate);
//        mTvClockTime = findViewById(R.id.tvClockTime);
//        tvClockDate1 = findViewById(R.id.tvClockDate1);
//        final float DELTA = 2;
//        final float DELTA_XLARGE = 4;
//
//        float delta = Utility.isXLargeScreen(this) ? DELTA_XLARGE : DELTA;
//        delta = Utility.dpsToPixels(this, (int) delta);
//
////        mTvClockTime.setShadowLayer(delta, delta, delta, getResources().getColor(R.color.text_shadow_color_clock_time));
//
//        prvDisplayClock1(mTvClockDate, mTvClockTime, tvClockDate1);
//    }
//
//    private void prvProcessViewTvHello() {
//        LoginInfo info = getPrefLoginInfo(this);
//
//        if (info != null) {
//            String text = info.getEmpName() + Constant.SPACE1;
//            ((TextView) findViewById(R.id.tvHello)).setText(text);
//        }
//    }
//
//    private void prvProcessViewTvRecentClockInfo() {
//        LoginInfo info = getPrefLoginInfo(this);
//
//        if (info != null) {
//            CheckRec checkRec = DBManager.getInstance(this).getRecentCheckRec(info.getEmpNo());
//
//            if (checkRec != null) {
////                String text = getString(R.string.recent_clock_info);
//                String text = "";
//                text += checkRec.getCheckDate(DateUtil.FORMAT_YYYY_MM_DD_SLASH_HH_MM_24) + Constant.SPACE1;
//                ((TextView) findViewById(R.id.tvRecentClockInfo)).setVisibility(View.VISIBLE);
//                ((TextView) findViewById(R.id.tvRecentClockInfo1)).setVisibility(View.VISIBLE);
//                if (checkRec.getCheckType().equals(DBManager.CHECK_TYPE_IN)) {
//                    ((TextView) findViewById(R.id.tvRecentClockInfo)).setText(text);
//
//                    ((TextView) findViewById(R.id.tvRecentClockInfo1)).setText("上班");
//                } else {
//                    ((TextView) findViewById(R.id.tvRecentClockInfo)).setText(text);
//
//                    ((TextView) findViewById(R.id.tvRecentClockInfo1)).setText("下班");
//                }
////                ((TextView) findViewById(R.id.tvRecentClockInfo)).setText(text);
//            } else {
//                ((TextView) findViewById(R.id.tvRecentClockInfo)).setVisibility(View.GONE);
//                ((TextView) findViewById(R.id.tvRecentClockInfo1)).setVisibility(View.GONE);
//
//            }
//        }
//    }
//
//    private void prvProcessViewTvVersionInfo() {
//        ((TextView) findViewById(R.id.tvVersionInfo)).setText(getString(R.string.version_info, Utility.getVersionName(this)));
//    }
//
//    private boolean prvSaveCheckRec(ClockPara para, int seriallyCheckDays) {
//        CheckRec checkRec = new CheckRec();
//
//        int days;
//
//        if (seriallyCheckDays == ApplicationEx.DAYS_7)
//            days = seriallyCheckDays;
//        else
//            days = 0;
//
//        checkRec.setWithClockPara(para, DBManager.NO_1, days);
//
//        StringBuffer errMsg = new StringBuffer();
//
//        if (!DBManager.getInstance(this).saveCheckRec(checkRec, errMsg)) {
//            Utility.showToast(this, errMsg.toString());
//            Utility.logE("prvParseCheck(): " + errMsg.toString());
//
//            return false;
//        }
//
//        return true;
//    }
//
//    private void prvShowClockAlertDialogWithAddr(String title, String msg, DialogInterface.OnClickListener posBtnListener, int editTextId) {
//        final int MARGIN_10 = Utility.dpsToPixels(this, 10);
//        final int MARGIN_20 = Utility.dpsToPixels(this, 20);
//
//        final int TEXT_SIZE = 18;
//
//        LinearLayout llRootView = new LinearLayout(this);
//        llRootView.setOrientation(LinearLayout.VERTICAL);
//
//        LinearLayout.LayoutParams llPara;
//
//        llPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        llPara.setMargins(MARGIN_20, MARGIN_10, MARGIN_20, 0);
//
//        TextView tv = new TextView(this);
//        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
//        tv.setText(msg);
//
//        llRootView.addView(tv, llPara);
//
//        EditText et = new EditText(this);
//        et.setId(editTextId);
//        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
//        et.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
//        et.setHint(R.string.input_address_here);
//        et.setFocusable(true);
//
//        llPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        llPara.setMargins(MARGIN_20, MARGIN_10, MARGIN_20, MARGIN_10);
//
//        llRootView.addView(et, llPara);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle(title)
//                .setView(llRootView)
//                .setPositiveButton(getString(android.R.string.yes), posBtnListener)
//                .setNegativeButton(getString(android.R.string.no), null)
//                .show();
//    }
//
//    private void prvShowClockProgressDialog() {
//        if (mClockProg == null)
//            mClockProg = Utility.showSimpleProgressDialog(this, true, getString(R.string.please_wait));
//    }
//
//    private void prvStartCheckRecListActivity(boolean needLogin, CheckRecKey checkRecKey) {
//        LoginInfo info = getPrefLoginInfo(this);
//        getDBtestList(info.getEmpNo());
//        Intent intent = new Intent(MainActivity_Old.this, MainActivity_Old.class);
//        MainActivity_Old.this.startActivity(intent);
//        finish();
//        if (info == null)
//            return;
////todo::舊版打卡過後會跳進去看打卡記錄。
////        Intent intent = new Intent(this, CheckRecListActivity.class);
////
////        intent.putExtra(CheckRecListActivity.EXTRA_EMP_ID, info.getEmpNo());
////
////        if (checkRecKey != null)
////            intent.putExtra(CheckRecListActivity.EXTRA_CHECK_REC_KEY, checkRecKey);
////
////        intent.putExtra(CheckRecListActivity.EXTRA_NEED_LOGIN, needLogin);
////
////        this.startActivityForResult(intent, REQUEST_CODE_CHECK_REC_LIST);
//    }
//
//    private void prvStartClockFailActivity(CheckRecKey checkRecKey) {
//        LoginInfo info = getPrefLoginInfo(MainActivity_Old.this);
//
//        if (info == null)
//            return;
//        Log.d("MainActivity", "checkRecKey:" + checkRecKey.getEmpId());
//        Log.d("MainActivity", "checkRecKey:" + checkRecKey.getCheckDate());
//        Log.d("MainActivity", "checkRecKey:" + checkRecKey.getCheckType());
//        StringBuffer errMsg = new StringBuffer();
//        final CheckRec checkRec = DBManager.getInstance(MainActivity_Old.this).getCheckRec(checkRecKey.getEmpId(), checkRecKey.getCheckDate(), checkRecKey.getCheckType());
//        if (checkRec == null) {
//            Utility.logE("ivOK.onClick(): checkRec == null");
//            return;
//        }
//        if (!DBManager.getInstance(MainActivity_Old.this).saveCheckRec(checkRec, errMsg)) {
//            Utility.showToast(MainActivity_Old.this, errMsg.toString());
//            Utility.logE("ivOK.onClick(): " + errMsg.toString());
//        }
//        //todo:測試離現打卡成功。
//    }
//
//    private void prvStartLoginActivity(int reqCode) {
//        Intent intent = new Intent(this, LoginActivity.class);
//
//        startActivityForResult(intent, reqCode);
//    }
//
//    private void prvTimerCreate() {
//        if (mTimer == null)
//            mTimer = new PrvTimer();
//    }
//
//    private void prvTimerDestroy() {
//        if (mTimer != null) {
//            mTimer.cancel();
//            mTimer = null;
//        }
//    }
//
//    private void prvTimerScheduleClock() {
//        if (mTimer != null)
//            mTimer.scheduleClock();
//    }
//
//    /*
//     * Private Methods End
//     */
//    private void prvChangeAbnormal() {
//        //todo : employeeBT 員工查詢功能拿掉!!(2019/02/20)
//        employeeBT.setVisibility(View.GONE);
////        employeeBT.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Utility.showToast(MainActivity.this, "觸發員工異常按鈕");
//////				//異常打卡時的DIALOG//todo 0903
//////				Dialog d = new ClockInErrorDialog(MainActivity.this,0);
//////				d.show();
////
////                Intent intent = new Intent(MainActivity.this, EmployeeActivity.class);
////                startActivity(intent);
////            }
////        });
//        supervisorBT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Utility.showToast(MainActivity.this, "觸發部門異常按鈕");
//                Intent intent = new Intent(MainActivity_Old.this, SuperiorActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    public void getSchedule() {
//        LoginInfo info = getPrefLoginInfo(this);
////        Log.d("MainActivity","testGetEMP_name :"+ info.getEmpNo());
//        if (info == null)
//            return;
//        String longinGetEmpNo = info.getEmpNo();
//        Log.d("MainActivity", "取得班表:執行");
//        scheduleAsyncTask1 = new ScheduleAsyncTask1(MainActivity_Old.this, longinGetEmpNo, new ScheduleAsyncTask1.GetSchedule() {
//            @Override
//            public void setSchedule(String workIn, String workOut) {
//                Log.d("getSchedule", "workIn:" + workIn);
//                Log.d("getSchedule", "workOut:" + workOut);
//                today_schedule_from_api.setTextColor(getResources().getColor(R.color.Main_work_time_text));
//                if (workIn.equals("00:00") && workOut.equals("00:00")) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (ACache.get(MainActivity_Old.this).getAsString("getApiWorkIn") != null &&
//                                    ACache.get(MainActivity_Old.this).getAsString("getApiWorkOut") != null){
//                                String workIn1 = ACache.get(MainActivity_Old.this).getAsString("getApiWorkIn");
//                                String workOut1 = ACache.get(MainActivity_Old.this).getAsString("getApiWorkOut");
//                                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                SimpleDateFormat output = new SimpleDateFormat("HH:mm");
//                                try {
//                                    Date upWorkDate = input.parse(workIn1);
//                                    Date offWorkDate = input.parse(workOut1);
//                                    String upWD = output.format(upWorkDate);
//                                    String offWD = output.format(offWorkDate);
//                                    today_schedule_from_api.setText("無資料");
//                                    getNowDateIsTrue = false;
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                            }else {
//                                today_schedule_from_api.setText("無資料");
//                                getNowDateIsTrue = false;
//                            }
//                        }
//                    });
//                } else{
//                    today_schedule_from_api.setText(workIn + " - " + workOut);
//                    getNowDateIsTrue = true;
//                    ACache.get(MainActivity_Old.this).put("getWorkIn", workIn);
//                    ACache.get(MainActivity_Old.this).put("getWorkOut", workOut);
//                }
//
//            }
//        }, new ScheduleAsyncTask1.GetAnimation() {
//            @Override
//            public void setAnimation(Boolean s) {
//                Log.d("setAnimation", String.valueOf(s));
//                //s = true 閃下班
//                //s = false 閃上班
//                ACache.get(MainActivity_Old.this).put("MainAnimation", s);
//                if (!s) {
//                    ((RelativeLayout) findViewById(R.id.acg_workin_bg)).setBackgroundResource(R.drawable.splse);
//                    AnimationDrawable animationDrawable;
//                    animationDrawable = (AnimationDrawable) ((RelativeLayout) findViewById(R.id.acg_workin_bg)).getBackground();
//                    animationDrawable.start();
////                    ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.splse);
////                    AnimationDrawable animationDrawable;
////                    animationDrawable = (AnimationDrawable) ((ImageView) findViewById(R.id.ivClockIn)).getBackground();
////                    animationDrawable.start();
////                    ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
//                } else {
//                    ((RelativeLayout) findViewById(R.id.acg_workout_bg)).setBackgroundResource(R.drawable.splse);
//                    AnimationDrawable animationDrawable;
//                    animationDrawable = (AnimationDrawable) ((RelativeLayout) findViewById(R.id.acg_workout_bg)).getBackground();
//                    animationDrawable.start();
////                    ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.splse);
////                    AnimationDrawable animationDrawable;
////                    animationDrawable = (AnimationDrawable) ((ImageView) findViewById(R.id.ivClockOut)).getBackground();
////                    animationDrawable.start();
////                    ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.btn_circle);
//                }
//            }
//
//        });
////        scheduleAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        scheduleAsyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        Log.d("MainActivity", "取得是否為主管:執行");
//        getSuperviseList = new GetSuperviseList(MainActivity_Old.this, longinGetEmpNo, new GetSuperviseList.GetSuperBoss() {
//            @Override
//            public void setSuperBoss(Boolean superBoss) {
//                Log.d("MainActivity", "判斷是否是主管superBoss:" + superBoss);
//                if (!superBoss) {
//                    lookSuperBt.setVisibility(View.GONE);
//                    supervisorBT.setVisibility(View.GONE);
//                    setEmpCounts.setVisibility(View.GONE);
//                } else {
//                    lookSuperBt.setVisibility(View.VISIBLE);
//                    supervisorBT.setVisibility(View.VISIBLE);
//                    setEmpCounts.setVisibility(View.VISIBLE);
//                }
//            }
//        }, new GetSuperviseList.GetSuperEmpCounts() {
//            @Override
//            public void setSuperEmpCounts(String superEmpCounts) {
//                Log.d("MainActivity", "有多少筆異常superEmpCounts:" + superEmpCounts);
//                if (Integer.valueOf(superEmpCounts) >= 100) {
//                    setEmpCounts.setText("99+");
//                } else {
//                    setEmpCounts.setText(superEmpCounts);
//                }
//            }
//        });
//        getSuperviseList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//    }
//
//    private static String getNowDate() {
//        long time = System.currentTimeMillis();
//        Date nowDate = new Date(time);
//        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String nowDay;
//        nowDay = format.format(nowDate);
//        return nowDay;
//    }
//
//    private static String getNowDate1() {
//        long time = System.currentTimeMillis();
//        Date nowDate = new Date(time);
//        final SimpleDateFormat format = new SimpleDateFormat("EE");
//        String nowDay;
//        nowDay = format.format(nowDate);
//        return nowDay;
//    }
//
//    private void pvClockInTouch() {
//        Date checkDate = new Date();
//
//        final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_IN);
//
//        if (para == null) {
//            Utility.logE("ivClockIn.onClick(): para == null");
//
//            return;
//        }
//
//        if (DBManager.getInstance(MainActivity_Old.this).getCheckRec(para.getEmpId(), CheckRec.formatCheckDate(para.getCheckDateYYYYMMDDHHMMSS()), para.getCheckType()) != null) {
//            Utility.showToast(MainActivity_Old.this, R.string.clock_time_duplicated);
//
//            return;
//        }
//
//        if (para.getLoc() == null)
//            prvDoClockIn(checkDate, para);
//        else if (Utility.checkNetworkStatus(MainActivity_Old.this)) {
//            AsyncGetAddressStatic.AsyncGetAddressPara getAddrPara = new AsyncGetAddressStatic.AsyncGetAddressPara(para.getLoc().getLatitude(), para.getLoc().getLongitude());
//
//            new PrvAsyncGetAddressClock(MainActivity_Old.this, checkDate, para).execute(getAddrPara);
//        } else
//            prvDoClockIn(checkDate, para);
//    }
//
//    private void pvClockOutTouch() {
//        Date checkDate = new Date();
//
//        final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_OUT);
//
//        if (para == null) {
//            Utility.logE("ivClockOut.onClick(): para == null");
//            return;
//        }
//
//        if (DBManager.getInstance(MainActivity_Old.this).getCheckRec(para.getEmpId(), CheckRec.formatCheckDate(para.getCheckDateYYYYMMDDHHMMSS()), para.getCheckType()) != null) {
//            Utility.showToast(MainActivity_Old.this, R.string.clock_time_duplicated);
//            return;
//        }
//
//        if (para.getLoc() == null)
//            prvDoClockOut(checkDate, para);
//        else if (Utility.checkNetworkStatus(MainActivity_Old.this)) {
//            AsyncGetAddressStatic.AsyncGetAddressPara getAddrPara = new AsyncGetAddressStatic.AsyncGetAddressPara(para.getLoc().getLatitude(), para.getLoc().getLongitude());
//
//            new PrvAsyncGetAddressClock(MainActivity_Old.this, checkDate, para).execute(getAddrPara);
//        } else
//            prvDoClockOut(checkDate, para);
//    }
//
//    private void getWorkRange() {
//        if (ACache.get(this).getAsString("getApiWorkIn") != null &&
//                ACache.get(this).getAsString("getApiWorkOut") != null) {//<--todo:判斷上下班時間有沒有NULL
//            String workIn = ACache.get(this).getAsString("getApiWorkIn");
//            String workOut = ACache.get(this).getAsString("getApiWorkOut");
//            Log.d("MainActivity", "workInString:" + workIn);
//            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//                Date workInDate = input.parse(workIn);
//                Date workOutDate = input.parse(workOut);
//
//                workInRange1 = DateUtil.addDate(workInDate, Calendar.HOUR, 1);
//                workInRange2 = DateUtil.addDate(workInDate, Calendar.MINUTE, -30);//**todo:上班時間前半小時
//
//                workOutRange1 = DateUtil.addDate(workOutDate, Calendar.HOUR, 1);
//                workOutRange2 = DateUtil.addDate(workOutDate, Calendar.MINUTE, 30);//**todo:下班時間後半小時
////                 workOutRangeOverTime13 = DateUtil.addDate(workOutDate, Calendar.HOUR, +13);//**todo:下班時間超過13小時
//                Log.d("MainActivity", "上班時間前半小時:" + workInRange2);
//                Log.d("MainActivity", "下班時間後半小時:" + workOutRange2);
////                Log.d("MainActivity", "下班時間後13小時:" +workOutRangeOverTime13);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private Date pvtOnWorkDialog() {
//        ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
//            @Override
//            public void setChangeTest(String s) {
////                            searchErrorText.setText(s);
//                ACache.get(MainActivity_Old.this).put("adjustComment", s);
//                ACache.get(MainActivity_Old.this).put("Wi_FiSSID", wifiTest.getText().toString());
//                Log.d("MainActivity", "wifiTest.getText():" + wifiTest.getText());
//                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");
//                Log.d("MainActivity", "已取得打卡異常原因 :" + s);
//                Dialog d = new ClockInErrorDialog(MainActivity_Old.this, 0, s, new ClockInErrorDialog.PriorityListener1() {
//                    @Override
//                    public Date setChangeTime(Date s) {
//                        Log.d("MainActivity", "已取得修改後的上班時間 :" + s);
//                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String changeTime = input.format(s);
//                        Log.d("MainActivity", changeTime);
//                        ACache.get(MainActivity_Old.this).put("adjustDate", changeTime);
//                        final ClockPara para = prvPrepareClockPara(s, DBManager.CHECK_TYPE_IN);
//                        prvDoClockInToChangeTime(s, para);
//                        return s;
//                    }
//                });
//                d.show();
//            }
//        });
//        d.show();
//        return null;
//    }
//
//    private Date pvtOffWorkDialog() {
//        ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
//            @Override
//            public void setChangeTest(String s) {
//                ACache.get(MainActivity_Old.this).put("adjustComment", s);
//                ACache.get(MainActivity_Old.this).put("Wi_FiSSID", wifiTest.getText().toString());
//                ACache.get(MainActivity_Old.this).put("unusualStatus", "1");
//                Log.d("MainActivity", "已取得打卡異常原因 :" + s);
//                Log.d("MainActivity", "wifiTest.getText():" + wifiTest.getText());
//                Dialog d = new ClockOutErrorDialog(MainActivity_Old.this, 0, s, new ClockOutErrorDialog.PriorityListener2() {
//                    @Override
//                    public Date setChangeTime(Date s) {
//                        Log.d("MainActivity", "已取得修改後的下班時間 :" + s);
//                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String changeTime = input.format(s);
//                        Log.d("MainActivity", changeTime);
//                        ACache.get(MainActivity_Old.this).put("adjustDate", changeTime);
//                        final ClockPara para = prvPrepareClockPara(s, DBManager.CHECK_TYPE_OUT);
//                        prvDoClockOutToChangeTime(s, para);
//                        return s;
//                    }
//                });
//                d.show();
//            }
//        });
//        d.show();
//        return null;
//    }
//
//    private void prvDoClockInToChangeTime(final Date checkDate, final ClockPara para) {
//        final int editTextId = ViewEx.generateViewId();
//        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_IN, para);
//
//        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                EditText editText = ((AlertDialog) dialog).findViewById(editTextId);
//
//                if (editText != null)
//                    para.setRemarkAddr(editText.getText().toString());
//
//                if (prvSaveCheckRec(para, seriallyCheckDays)) {
//                    prvHttpGetClockIn(para);
//                    prvProcessViewTvRecentClockInfo();
//
//                    ApplicationEx.cancelNotifyClockOutService9(MainActivity_Old.this);
//                    ApplicationEx.cancelNotifyClockOutService13(MainActivity_Old.this);
//
//                    ApplicationEx.registerNotifyClockOutService9(MainActivity_Old.this);
//                    ApplicationEx.registerNotifyClockOutService13(MainActivity_Old.this);
//                }
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_IN);
//
//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
//            }
//        };
//
//        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
//            }
//        };
//
//
//        String title = getString(R.string.confirm_clock_in);
//        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//
//        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//            Utility.showSimpleAlertDialogNew(this, title, msg, posListener, negListener);
//        else
//            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//    }
//
//    private void prvDoClockOutToChangeTime(final Date checkDate, final ClockPara para) {
//        final int editTextId = ViewEx.generateViewId();
//        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_OUT, para);
//
//        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                EditText et = ((AlertDialog) dialog).findViewById(editTextId);
//
//                if (et != null)
//                    para.setRemarkAddr(et.getText().toString());
//
//                if (prvSaveCheckRec(para, seriallyCheckDays))
////				if (prvSaveCheckRec(para, DBManager.NO_1))
//                {
//                    prvHttpGetClockOut(para);
//                    prvProcessViewTvRecentClockInfo();
//
//                    ApplicationEx.cancelNotifyClockOutService9(MainActivity_Old.this);
//                    ApplicationEx.cancelNotifyClockOutService13(MainActivity_Old.this);
//                }
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_OUT);
//
//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());
//
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity_Old.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
//            }
//        };
//
//        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
//            }
//        };
//        String title = getString(R.string.confirm_clock_out);
//        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
//
//        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
//        else
//            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//    }
//
//    private void getDBtestList(String empNo) {
////        LoginInfo info = new LoginInfo();
//        CheckRecList list = DBManager.getInstance(this).getCheckRecList(empNo);
//        for (CheckRec checkRec : list) {
//            Log.d("MainActivity", "checkRec:" + checkRec.toString());
//        }
//
//
//    }
//
//todo::2019/05/20 備份於2.0.0出版測試後正常的mainActivity，此備份為新舊版本時期最終產物。現MainActivity於2019/05/20後更改。
//////////////////////////////////////////////////////////////////////////////////////////
//todo:2019/12/11上班按鈕重構，此為原檔。

//        if (workInRange1 != null && newDate.equals(checkAcacheDate)) {
//            if (wifi.isWifiEnabled() || isTagNfc) {//<--todo:判斷有沒有開啟WIFI
//                if (sCanWiFiResultsList() || isTagNfc) {//<--todo:判斷WIFI有沒有在公司範圍內
//                    if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {//todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
//                        Log.d("首頁執行確認_prvDoClockIn", "WIFI有開+有在公司範圍內+上班時間範圍內");
//                        Log.d("首頁執行確認_prvDoClockIn", "目前要打卡的時間:" + checkDate);
//                        Log.d("首頁執行確認_prvDoClockIn", "上班時間範圍:" + workInRange2);
//                        Log.d("首頁執行確認_prvDoClockIn", "取得當下日期班表:" + getNowDateIsTrue);
//                        ACache.get(MainActivity.this).put("unusualStatus", "0");//是否異常打卡0=f 1=t
//                        if (isTagNfc) {
//                            sWitch_Wifi_Nfc = "2";
//                        } else {
//                            sWitch_Wifi_Nfc = "1";
//                        }
//                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                        String title = getString(R.string.confirm_clock_in);
//                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
//                        else
//                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                    } else {
//                        Log.d("首頁執行確認_prvDoClockIn", "WIFI有開+有在公司範圍內+上班時間範圍外");
//                        Log.d("首頁執行確認_prvDoClockIn", "目前要打卡的時間:" + checkDate);
//                        Log.d("首頁執行確認_prvDoClockIn", "上班時間範圍:" + workInRange2);
//                        Log.d("首頁執行確認_prvDoClockIn", "取得當下日期班表:" + getNowDateIsTrue);
//                        //todo: 進入異常原因選擇及時間調整流程。
//                        if (getNowDateIsTrue != null && !getNowDateIsTrue) {
//                            if (isTagNfc) {
//                                sWitch_Wifi_Nfc = "2";
//                            } else {
//                                sWitch_Wifi_Nfc = "1";
//                            }
//                            ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                            ACache.get(MainActivity.this).put("unusualStatus", "1");
//                            String title = getString(R.string.confirm_clock_in);
//                            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
//                            else
//                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                        } else {
//                            if (checkDate.after(workOutRange2)) {
//                                Log.d("首頁執行確認_prvDoClockIn", "超過下班時間30分中按取上班紐");
//                                //todo 超過下班時間30分鐘按取上班按鈕。
//                                ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
//                                    @Override
//                                    public void setChangeTest(String s) {
//                                        if (isTagNfc) {
//                                            sWitch_Wifi_Nfc = "2";
//                                        } else {
//                                            sWitch_Wifi_Nfc = "1";
//                                        }
//                                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                                        ACache.get(MainActivity.this).put("unusualStatus", "1");
//                                        String title = getString(R.string.confirm_clock_in);
//                                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                            Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                                        else
//                                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                                    }
//
//                                });
//                                d.show();
//                            } else {
//                                pvtOnWorkDialog(checkDate);
//                                if (isTagNfc) {
//                                    sWitch_Wifi_Nfc = "2";
//                                } else {
//                                    sWitch_Wifi_Nfc = "1";
//                                }
//                                ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");
//                            }
////                            pvtOnWorkDialog();
////                            ACache.get(MainActivity.this).put("SSIDCheckin", "1");
////                            ACache.get(MainActivity.this).put("unusualStatus", "1");
//                        }
//                    }
//                } else {
//                    //todo::沒有在公司範圍內 啟動重選LOG
//                    String test;
//                    if (thisMobileHaveNfc) {
//                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
//                    } else {
//                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內並重新打卡";
//                    }
//                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setTitle("非公司Wi-Fi");
//                    dialog.setMessage(test);
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {//todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
//                                Log.d("首頁執行確認_prvDoClockIn", "WIFI位置略過，上班時間範圍內");
//
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                                String title = getString(R.string.confirm_clock_in);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//
//
//                            } else {
//                                Log.d("首頁執行確認_prvDoClockIn", "上班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOnWorkDialog(checkDate);
//
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("重新偵測後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            if (wifi.isWifiEnabled()) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pvClockInTouch();//開啟之後在啟動打卡按鈕
//                                    }
//                                });
//                            }
//                        }
//                    });
//                    dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.cancel();
////                            mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
////                            if (null == mNfcAdapter) {
////                                Toast.makeText(MainActivity.this, "本手機未支援NFC功能!", Toast.LENGTH_LONG).show();
////                            }
////                            if (!mNfcAdapter.isEnabled()) {
////                                Toast.makeText(MainActivity.this, "請開啟NFC功能!", Toast.LENGTH_LONG).show();
////                            }
//                        }
//                    });
//                    dialog.show();
//                }
//            } else {
//                //todo::沒有WIFI 啟動開啟diaLOG
//                if (!wifi.isWifiEnabled()) {
//                    String test;
//                    if (thisMobileHaveNfc) {
//                        test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
//                    } else {
//                        test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線並重新打卡";
//                    }
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setTitle("無Wi-Fi連線");
//                    dialog.setMessage(test);
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                            if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {//todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
//                                Log.d("首頁執行確認_prvDoClockIn", "WIFI略過+上班時間範圍內");
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                                String title = getString(R.string.confirm_clock_in);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//
//
//                            } else {
//                                Log.d("首頁執行確認_prvDoClockIn", "WIFI略過+上班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOnWorkDialog(checkDate);
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            wifi.setWifiEnabled(true);
//                            Log.d("首頁執行確認_prvDoClockIn", "wifi開啟");
//                            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "稍等3秒鐘...", true);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(6000);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    } finally {
//                                        progressDialog.dismiss();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                pvClockInTouch();//開啟之後在啟動打卡按鈕
//                                            }
//                                        });
//
//                                    }
//                                }
//                            }).start();
//                        }
//                    });
//                    dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.cancel();
////                            mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
////                            if (null == mNfcAdapter) {
////                                Toast.makeText(MainActivity.this, "本手機未支援NFC功能!", Toast.LENGTH_LONG).show();
////                            }
////                            if (!mNfcAdapter.isEnabled()) {
////                                Toast.makeText(MainActivity.this, "請開啟NFC功能!", Toast.LENGTH_LONG).show();
////                            }
//                        }
//                    });
//                    dialog.show();
//                }
//            }
//        } else {
//            getWorkRange();
//        }
//        //todo : 需要測試完全沒有班表資料的帳號是否能夠進來。
//        if (getNowDateIsTrue != null && !getNowDateIsTrue) {
//            Log.d("首頁執行確認_prvDoClockIn", "完全無班表資料的打上班卡");
//
//            //todo : 09/18班表若為無資料狀態，打卡時都視為異常打卡，選擇原因，但不選擇時間，已當下時間打卡
//            ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
//                @Override
//                public void setChangeTest(String s) {
//                    if (isTagNfc) {
//                        sWitch_Wifi_Nfc = "2";
//                    } else {
//                        sWitch_Wifi_Nfc = "1";
//                    }
//                    ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                    ACache.get(MainActivity.this).put("unusualStatus", "1");
//                    String title = getString(R.string.confirm_clock_in);
//                    String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
//                    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                        Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                    else
//                        prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                }
//
//            });
//            d.show();
//        }
///////////////////////////////////////////////////////////////////////////////////////////
//todo:此為更改前的下班流程。

//        if (workOutRange2 != null) {
//            if (wifi.isWifiEnabled() || isTagNfc) {
//                if (sCanWiFiResultsList() || isTagNfc) {
//                    if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
//                        Log.d("首頁執行確認_prvDoClockOut", "下班時間範圍外");
//                        //todo: 進入異常原因選擇及時間調整流程。
//                        if (getNowDateIsTrue != null && !getNowDateIsTrue) {
//                            if (isTagNfc) {
//                                sWitch_Wifi_Nfc = "2";
//                            } else {
//                                sWitch_Wifi_Nfc = "1";
//                            }
//                            ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                            ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                            String title = getString(R.string.confirm_clock_out);
//                            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
//
//                            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                            else
//                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                        } else {
//
//                            pvtOffWorkDialog(checkDate);
//                            if (isTagNfc) {
//                                sWitch_Wifi_Nfc = "2";
//                            } else {
//                                sWitch_Wifi_Nfc = "1";
//                            }
//                            ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                            ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                        }
//
//                    } else {
//                        Log.d("首頁執行確認_prvDoClockOut", "下班時間範圍內");
//                        if (checkDate.before(workInRange2)) {
//                            Log.d("首頁執行確認_prvDoClockOut", "上班時間按下班卡早於上班時間前半小時");
//                            //todo 超過下班時間30分鐘按取上班按鈕。
//                            ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
//                                @Override
//                                public void setChangeTest(String s) {
//                                    if (isTagNfc) {
//                                        sWitch_Wifi_Nfc = "2";
//                                    } else {
//                                        sWitch_Wifi_Nfc = "1";
//                                    }
//                                    ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                                    ACache.get(MainActivity.this).put("unusualStatus", "1");
//                                    String title = getString(R.string.confirm_clock_out);
//                                    String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
//                                    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                        Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                                    else
//                                        prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                                }
//
//                            });
//                            d.show();
//
//                        } else {
//                            Log.d("首頁執行確認_prvDoClockOut", "上班時間按下班卡晚於上班時間前半小時");
//                            if (isTagNfc) {
//                                sWitch_Wifi_Nfc = "2";
//                            } else {
//                                sWitch_Wifi_Nfc = "1";
//                            }
//                            ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                            ACache.get(MainActivity.this).put("unusualStatus", "0");//是否異常打卡0=f 1=t
//                            //todo::正常下班流程。
//                            String title = getString(R.string.confirm_clock_out);
//                            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
//
//                            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                            else
//                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//
//                        }
////                        //todo::正常下班流程。
////                        String title = getString(R.string.confirm_clock_out);
////                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
////
////                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
////                            Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
////                        else
////                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
////
//                    }
//                } else {
//                    //todo::沒有在公司範圍內 啟動重選LOG
//                    String test;
//                    if (thisMobileHaveNfc) {
//                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
//                    } else {
//                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內並重新打卡";
//                    }
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setTitle("非公司Wi-Fi");
//                    dialog.setMessage(test);
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                            if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
//                                Log.d("首頁執行確認_prvDoClockOut", "下班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOffWorkDialog(checkDate);
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                            } else {
//                                Log.d("首頁執行確認_prvDoClockOut", "下班時間範圍內");
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                                //todo::正常下班流程。
//                                String title = getString(R.string.confirm_clock_out);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
//
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("重新偵測後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            if (wifi.isWifiEnabled()) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pvClockOutTouch();//開啟之後在啟動打卡按鈕
//                                    }
//                                });
//                            }
//                        }
//                    });
//                    dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.cancel();
////                            mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
////                            if (null == mNfcAdapter) {
////                                Toast.makeText(MainActivity.this, "本手機未支援NFC功能!", Toast.LENGTH_LONG).show();
////                            }
////                            if (!mNfcAdapter.isEnabled()) {
////                                Toast.makeText(MainActivity.this, "請開啟NFC功能!", Toast.LENGTH_LONG).show();
////                            }
//                        }
//                    });
//                    dialog.show();
//                }
//            } else {
//                //todo::沒有WIFI 啟動開啟LOG
////            checkWifiIsOpenDoClockIn();
//                if (!wifi.isWifiEnabled()) {
//                    String test;
//                    if (thisMobileHaveNfc) {
//                        test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
//                    } else {
//                        test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線並重新打卡";
//                    }
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//                    dialog.setTitle("無Wi-Fi連線");
//                    dialog.setMessage(test);
//                    dialog.setCancelable(false);
//                    dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//
//                            if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
//                                Log.d("首頁執行確認_prvDoClockOut", "下班時間範圍外");
//                                //todo: 進入異常原因選擇及時間調整流程。
//                                pvtOffWorkDialog(checkDate);
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//
//                            } else {
//                                Log.d("首頁執行確認_prvDoClockOut", "下班時間範圍內");
//                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
//                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
//                                //todo::正常下班流程。
//                                String title = getString(R.string.confirm_clock_out);
//                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
//
//                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                                else
//                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                            }
//                        }
//                    });
//                    dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            wifi.setWifiEnabled(true);
//                            Log.d("首頁執行確認_prvDoClockOut", "wifi開啟");
//                            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "稍等3秒鐘...", true);
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(5000);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    } finally {
//                                        progressDialog.dismiss();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                pvClockOutTouch();//開啟之後在啟動打卡按鈕
//                                            }
//                                        });
//                                    }
//                                }
//                            }).start();
//                        }
//                    });
//                    dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.cancel();
////                            mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
////                            if (null == mNfcAdapter) {
////                                Toast.makeText(MainActivity.this, "本手機未支援NFC功能!", Toast.LENGTH_LONG).show();
////                            }
////                            if (!mNfcAdapter.isEnabled()) {
////                                Toast.makeText(MainActivity.this, "請開啟NFC功能!", Toast.LENGTH_LONG).show();
////                            }
//                        }
//                    });
//                    dialog.show();
//                }
//            }
//
//        } else {
//
//            getWorkRange();
//        }
//        //todo : 需要測試完全沒有班表資料的帳號是否能夠進來。
//        if (getNowDateIsTrue != null && !getNowDateIsTrue) {
//            Log.d("首頁執行確認_prvDoClockOut", "完全無班表資料的打下班卡");
//
////            ACache.get(MainActivity.this).put("SSIDCheckin", "1");
////            ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
////            String title = getString(R.string.confirm_clock_out);
////            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
////            //todo:因將離線拔掉，無法判斷是無資料還是離線，因此這邊在確認一次有無網路。
//////            if (Utility.checkNetworkStatus(this)) {
////            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
////                Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
////            } else {
////                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
////            }
////            }
//            //todo : 09/18班表若為無資料狀態，打卡時都視為異常打卡，選擇原因，但不選擇時間，已當下時間打卡
//            ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
//                @Override
//                public void setChangeTest(String s) {
//                    if (isTagNfc) {
//                        sWitch_Wifi_Nfc = "2";
//                    } else {
//                        sWitch_Wifi_Nfc = "1";
//                    }
//                    ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
//                    ACache.get(MainActivity.this).put("unusualStatus", "1");
//                    String title = getString(R.string.confirm_clock_out);
//                    String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
//                    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
//                        Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
//                    else
//                        prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
//                }
//
//            });
//            d.show();
//        }
//}

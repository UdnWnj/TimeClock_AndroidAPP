package com.udn.hr.clock.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.udn.hr.clock.test.FMService.FirebaseAnalyticsTool;
import com.udn.hr.clock.test.mylib.Constant;
import com.udn.hr.clock.test.mylib.DateUtil;
import com.udn.hr.clock.test.mylib.LocationController;
import com.udn.hr.clock.test.mylib.OnClickCounter;
import com.udn.hr.clock.test.mylib.StringEx;
import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.mylib.asynctask.AsyncGetAddressStatic;
import com.udn.hr.clock.test.mylib.view.ViewEx;
import com.udn.hr.clock.test.service.UpdateService;
import com.udn.hr.clock.test.sqlite.CheckRec;
import com.udn.hr.clock.test.sqlite.CheckRecKey;
import com.udn.hr.clock.test.sqlite.DBManager;
import com.udn.hr.clock.test.superior.GetNFCList;
import com.udn.hr.clock.test.superior.GetSuperviseList;
import com.udn.hr.clock.test.superior.GetWiFiSSIDList;
import com.udn.hr.clock.test.superior.SuperiorActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Tools.ACache;
import Tools.UpdateReminderDialog;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static class PrvAsyncGetAddressClock extends AsyncGetAddressStatic<MainActivity> {
        private final Date mCheckDate;
        private final ClockPara mClockPara;

        private PrvAsyncGetAddressClock(MainActivity act, Date checkDate, ClockPara clockPara) {
            super(act);

            this.mCheckDate = checkDate;
            this.mClockPara = clockPara;

//            act.prvShowClockProgressDialog();
        }

        @Override
        protected void onPostExecute(AsyncGetAddressResult result) {
//			super.onPostExecute(result);

            MainActivity act = mCRef.get();

            if (act == null)
                return;

            if (result.isSuccess)
                mClockPara.setCheckAddr(result.addr);
            else
                mClockPara.setCheckAddr(Constant.NULL_STRING);

            if (mClockPara.getCheckType().equals(DBManager.CHECK_TYPE_IN))
                act.prvDoClockIn(mCheckDate, mClockPara);
            else
                act.prvDoClockOut(mCheckDate, mClockPara);
        }

        @Override
        protected void onPreExecute() {
            // Don't show Progress-Dialog
        }
    }

    private static class PrvAsyncGetAddressMyPos extends AsyncGetAddressStatic<Context> {
        private PrvAsyncGetAddressMyPos(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(AsyncGetAddressResult result) {
            super.onPostExecute(result);

            Context context = mCRef.get();

            if (context == null)
                return;

            if (!result.isSuccess) {
                Utility.showSimpleAlertDialog(context, context.getString(R.string.can_not_get_address_server_not_responded), null);
//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(context, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_WITHOUT_ADDR);

                return;
            }

            String title = context.getString(R.string.where_are_you);
            String msg = result.addr + Constant.LINE_FEED + context.getString(R.string.location_fyi);

            Utility.showSimpleAlertDialog(context, title, msg, null);

//            GoogleAnalyticsManager.sendGoogleAnalyticsEvent(context, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_LOC_SUCCESS);
        }
    }

    private class PrvConnectionCallbacks implements ConnectionCallbacks {
        @Override
        public void onConnected(Bundle connectionHint) {
//			Utility.logD("onConnected(): Start");

            mLocationController.prepare(mGoogleApiClient, null, VALID_TIME_DELTA_MILLIS);
            mLocationController.requestLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int arg0) {
        }
    }

    private static class PrvHandler extends Handler {
        private static final int MSG_CLOCK = 0;

        private WeakReference<MainActivity> mMainActivityRef;

        private PrvHandler(MainActivity act) {
            mMainActivityRef = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity act = mMainActivityRef.get();

            if (act == null)
                return;

            switch (msg.what) {
                case MSG_CLOCK:
                    prvDisplayClock(act.mTvClockDate, act.mTvClockTime);

                    if (Utility.getPrefIsDebug(act)) {
                        TextView tvUpdateServiceLog = act.findViewById(R.id.tvUpdateServiceLog);
                        TextView tvUpdateTimeLog = act.findViewById(R.id.tvUpdateTimeLog);
                        TextView tvLatLng = act.findViewById(R.id.tvLatLng);

                        String text = act.getString(R.string.update_service_exe_log, Utility.Preference.getInstance(act).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG));
//						String text = act.getString(R.string.update_service_exe_log).replace("[updateServiceExeLog]", Utility.Preference.getInstance(act).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG));
                        tvUpdateServiceLog.setText(text);
//						tvUpdateServiceLog.setText("UpdateService: " + Utility.Preference.getInstance(act).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG));
                        tvUpdateTimeLog.setText("serverTimeLog = " + serverTimeLog);
                        Location loc = act.mLocationController.getCurrentLocation();

                        if (loc == null)
                            tvLatLng.setText(act.getString(R.string.unknown_location));
                        else
                            tvLatLng.setText(MainActivity.composeLocationDisplayString(loc));
                    }

                    break;
            }
        }
    }

    private class PrvOnConnectionFailedListener implements OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
        }
    }

    private class PrvTimer extends Timer {
        private TimerTask mTimerTaskClock;

        void cancelClock() {
            if (mTimerTaskClock != null) {
                mTimerTaskClock.cancel();
                mTimerTaskClock = null;
            }
        }

        void scheduleClock() {
            cancelClock();

            mTimerTaskClock = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(PrvHandler.MSG_CLOCK);
                }
            };

            schedule(mTimerTaskClock, Constant.MILLIS_1_SECOND, Constant.MILLIS_1_SECOND);
        }
    }

    private static final int HTTP_REQUEST_CODE_CLOCK_IN = 1;
    private static final int HTTP_REQUEST_CODE_CLOCK_OUT = 2;

    private static final long VALID_TIME_DELTA_MILLIS = Constant.MILLIS_1_MINUTE * 10;

    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final int REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL = 1;
    private static final int REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE = 2;
    private static final int REQUEST_CODE_LOGIN_AFTER_LOGOUT = 3;
    private static final int REQUEST_CODE_CHECK_REC_LIST = 4;
    private static final int REQUEST_CODE_CLOCK_FAIL = 5;

    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START = 1;
    private static final int PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION = 2;

    //    private static final String URL_STR_LOGIN = "https://eip.udngroup.com/eip/sysuser/api/login.jsp?userId=[userId]&password=[password]";
    private static final String URL_STR_LOGIN = "https://eip.udngroup.com/eip/sysuser/api/login-test.jsp?userId=[userId]&password=[password]";
    private static final String URL_STR_CLOCK_IN_OUT = "http://lou.udn-device-dept.net/app-action-card/newMysqltest_get_info.php?SN=[SN]&device_type=92&empolyee_id=[employeeId]&check_date=[checkDate]&check_type=[checkType]&&device_id=[deviceId]&GPS_info=[GPSInfo]&check_address=[checkAddress]&remark=[remark]";

    private GoogleApiClient mGoogleApiClient;
    private LocationController mLocationController = new LocationController(MainActivity.this);

    //18*0823
    private ImageView ivHelpItem;
    private TextView mTvClockDate, tvClockDate1;
    private TextView mTvClockTime;
    private PrvTimer mTimer;
    private PrvHandler mHandler;
    private ProgressDialog mClockProg;
    private ImageView nfc_Botton;
    private ImageView supervisorBT;
    //    private ScheduleAsyncTask scheduleAsyncTask;
    private ScheduleAsyncTask1 scheduleAsyncTask1;
    private GetSuperviseList getSuperviseList;
    private PutLambdaRecive_Task putLambdaRecive_task;
    private GetWiFiSSIDList getWiFiSSIDList;
    private TextView today_schedule, today_schedule_from_api;

    //19*0225
    private WifiManager wifi;
    private TextView wifiTest;
    private ArrayList<String> wifiSSid = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arraylistScan = new ArrayList<HashMap<String, String>>();
    //19*0306
    private Date workInRange1;
    private Date workInRange2;
    private Date workOutRange1;
    private Date workOutRange2;
    private Date workOutRange3;
    private Date workOutRangeOverTime13;
    //19*.0327
    private TextView if_off_line, setEmpCounts;
    private RelativeLayout lookSuperBt;
    //19*.0506
    private Boolean getNowDateIsTrue;
    private String forDialogUseTime;
    //19*1003系統重啟時間
    private static String serverTimeLog = "N";
    //19*1112
    private NfcAdapter mNfcAdapter;
    private Boolean isTagNfc = false;
    private Boolean thisMobileHaveNfc = false;
    private String NFCtext;
    private long openNFCtime;
    private Boolean isInUdnWIFI = false;
    public String sWitch_Wifi_Nfc = "0";
    private Boolean isInUDN_sCanTAG;
    //**todo::**公用機版本**
    public static Boolean forPublicUse = false;

    public static String composeLocationDisplayString(Location loc) {
        return loc.getLatitude() + Constant.COMMA_SPACE1 + loc.getLongitude();
    }

    public static LoginInfo getPrefLoginInfo(Context context) {
        String empNo = Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO);

        if (empNo == null)
            return null;

        String empName = Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NAME, Constant.NULL_STRING);
        String time = Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_TIME, Constant.NULL_STRING);

        Date loginDate = DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.parse(time);

        if (loginDate == null)
            return null;

        LoginInfo info = new LoginInfo();

        info.setEmpNo(empNo);
        info.setEmpName(empName);
        info.setLoginDate(loginDate);

        return info;
    }

    public static long getPrefNotifyClockOutServiceBaseTimeMillis13(Context context) {
        return Utility.Preference.getInstance(context).getLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13, 0);
    }

    public static long getPrefNotifyClockOutServiceBaseTimeMillis9(Context context) {
        return Utility.Preference.getInstance(context).getLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9, 0);
    }

    public static void logout(Context context) {
        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_LOGIN_EMP_NO);
        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_LOGIN_EMP_NAME);
        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_LOGIN_TIME);
    }

    public static ClockPara parseHttpPostClockResult(String content) {
        final String DUPLICATE_ENTRY = "Duplicate entry";

        ClockPara para = new ClockPara();

        if (content.contains(DUPLICATE_ENTRY))
//		if (content.indexOf(DUPLICATE_ENTRY) >= 0)
        {
            para.setIsDuplicated(true);
            return para;
        }

        try {
            JSONObject jsonObj = new JSONObject(content);

            para.setEmpId(jsonObj.getString("empolyee_id"));
            para.setCheckDateYYYYMMDDHHMMSS(jsonObj.getString("check_date"));
            para.setCheckType(jsonObj.getString("check_type"));
            para.setDeviceId(jsonObj.getString("device_id"));
            para.setGpsInfo(jsonObj.getString("GPS_info"));
            para.setCheckAddr(jsonObj.getString("check_address"));
            para.setRemark(jsonObj.getString("remark"));
            //**0328
//            para.setmUnusualStatus();
        } catch (JSONException e) {
            Utility.logE("parseHttpPostClockResult(): " + e.toString());
            return null;
        }

        return para;
    }

    public static void registerPrefUpdateServiceExecLog(Context context, String text) {
        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG, text);
    }

    public static void registerPrefNotifyClockOutServiceBaseTimeMillis13(Context context, long millis) {
        Utility.Preference.getInstance(context).putLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13, millis);
    }

    public static void registerPrefNotifyClockOutServiceBaseTimeMillis9(Context context, long millis) {
        Utility.Preference.getInstance(context).putLong(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9, millis);
    }

    public static void registerPrefLoginEmpNoList(Context context, String empNo) {
        StringEx strEx = new StringEx(Utility.Preference.getInstance(context).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO_LIST, Constant.NULL_STRING));

        if (!strEx.hasThisString(empNo, Constant.COMMA))
            Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO_LIST, strEx.add(empNo, Constant.COMMA).toString());
    }

    public static void registerPrefLoginInfo(Context context, String empNo, String empName) {
        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO, empNo);
        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_EMP_NAME, empName);
        Utility.Preference.getInstance(context).putString(ApplicationEx.PREF_KEY_LOGIN_TIME, DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(new Date()));
    }

    public static void removePrefNotifyClockOutServiceBaseTimeMillis13(Context context) {
        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_13);
    }

    public static void removePrefNotifyClockOutServiceBaseTimeMillis9(Context context) {
        Utility.Preference.getInstance(context).remove(ApplicationEx.PREF_KEY_NOTIFY_CLOCK_OUT_SERVICE_BASE_TIME_MILLIS_9);
    }

    public static void showFirstLoginAlertDialog(Context context, DialogInterface.OnClickListener posBtnListener) {
        Utility.showSimpleAlertDialog(context, context.getString(R.string.first_login_msg_title), "有任何疑問，請洽人資室\n" +
                "hr@udngroup.com", posBtnListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL:
                case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE:
                case REQUEST_CODE_LOGIN_AFTER_LOGOUT:
                    finish();
                    break;

            }

            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CHECK_REC_LIST:
                prvOnActivityResultCheckRecList(data);
                break;

            case REQUEST_CODE_CLOCK_FAIL:
                prvOnActivityResultClockFail(data);
                break;

            case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL:
            case REQUEST_CODE_LOGIN_AFTER_LOGOUT:
                prvOnActivityResultLogin(false);
                break;

            case REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE:
                prvOnActivityResultLogin(true);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1.15; //0.85 small size, 1 normal size, 1,15 big etc
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        configuration.densityDpi = (int) getResources().getDisplayMetrics().xdpi;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        isTimeAutomatic(this);
        Log.d("MainActivity", "isTimeAutomatic(this):" + isTimeAutomatic(this));
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.Login_top_logo_bg));
        }
        if_off_line = findViewById(R.id.if_off_line);
        lookSuperBt = findViewById(R.id.lookSuperBt);
        wifiTest = findViewById(R.id.get_wifi_ssid);

//        LoginInfo info = getPrefLoginInfo(this);
//        if (info != null){
////            forPublic60CsLogOut();
//        }
//        wifiTest.setText("目前無法驗證你的位置-點擊可開啟WIFI");
//        wifiTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!wifi.isWifiEnabled()) {
//                    wifi.setWifiEnabled(true);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(6000);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        sCanWiFiResultsList();
//                                    }
//                                });
//
//                            }
//                        }
//                    }).start();
//                }
//            }
//        });
        supervisorBT = findViewById(R.id.supervisorBT);
        today_schedule = findViewById(R.id.today_schedule);
        today_schedule_from_api = findViewById(R.id.today_schedule_from_api);
        nfc_Botton = findViewById(R.id.employeeBT);
        setEmpCounts = findViewById(R.id.setEmpCounts);
        ivHelpItem = findViewById(R.id.ivHelpItem);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ivHelpItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFirstLoginAlertDialog(MainActivity.this, null);
            }
        });
        prvProcessView();
        prvProcessControl();
        if (ACache.get(this).getAsString("getWorkIn") != null && ACache.get(this).getAsString("getWorkOut") != null) {
            String workIn = ACache.get(this).getAsString("getWorkIn");
            String workOut = ACache.get(this).getAsString("getWorkOut");
            //todo 判斷是網路延遲將上一次班表寫入。
            today_schedule_from_api.setTextColor(getResources().getColor(R.color.Main_work_time_text));
            today_schedule_from_api.setText(workIn + " - " + workOut);
        }
        if (!Utility.checkNetworkStatus(this)) {
            if (ACache.get(this).getAsString("getWorkIn") != null && ACache.get(this).getAsString("getWorkOut") != null) {
                String workIn = ACache.get(this).getAsString("getWorkIn");
                String workOut = ACache.get(this).getAsString("getWorkOut");
                //todo 判斷沒有網路後將上一次班表寫入。
                today_schedule_from_api.setTextColor(getResources().getColor(R.color.Main_work_time_text));
                today_schedule_from_api.setText(workIn + " - " + workOut);
                getNowDateIsTrue = false;

            } else {
                today_schedule_from_api.setText("尚未取得班表資料");
                getNowDateIsTrue = false;
            }
            if (ACache.get(this).getAsString("MainAnimation") != null) {
                String s = ACache.get(this).getAsString("MainAnimation");
                if (!s.equals("true")) {
                    ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.btn_circle);
                    ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
                } else {
                    ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
                    ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.btn_circle);
                }
            }
            Log.d("首頁執行確認_onCreate", "目前網路狀態" + "沒網路");
            if_off_line.setVisibility(View.VISIBLE);
            if_off_line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            });
        } else {
            Log.d("首頁執行確認_onCreate", "目前網路狀態" + "有網路");
            if_off_line.setVisibility(View.GONE);
            getSchedule();
        }
        mHandler = new PrvHandler(this);

        prvCheckLoginOnCreate();
        prvCheckStartUpdateService();
        prvChangeAbnormal();
//        GoogleAnalyticsManager.sendGoogleAnalyticsPageView(this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE);
        getWorkRange();
//        getThisMobileHaveNfc();
        try {
            if (ACache.get(this).getAsString("sever_Time") != null || !ACache.get(this).getAsString("sever_Time").equals("")) {
                getMainUI_Time();
            } else {
                prvProcessViewTvClock();
            }
        } catch (Exception e) {
            Log.d("MainActivity", "首頁時間狀態");
            prvProcessViewTvClock();
            e.printStackTrace();

        }
        if (getIntent() != null) {
//            processIntent(getIntent());
        }
        Log.d("MainActivity", "mNfcAdapter:" + mNfcAdapter);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        if (mNfcAdapter == null){
            FirebaseAnalyticsTool.sendUserProperty(this,"NFC","NO");
        }else {
            FirebaseAnalyticsTool.sendUserProperty(this,"NFC","YES");

        }
    }

    //檢查更新
    private void checkUpdate() {
        UpdateReminderDialog dialog = UpdateReminderDialog.newInstance(
                "https://set.a-p-i.io/app/hrclock/version/config_Android.json", BuildConfig.VERSION_CODE);
        dialog.setColorPrimary(ContextCompat.getColor(this, R.color.Color6));
        dialog.setContext(this);
        dialog.setStoreUrl(getString(R.string.apk_file_url));
        dialog.setButtonText("立即更新", "下次再說", "不再提醒");
        dialog.setSDKVersion(Build.VERSION.SDK_INT);
        dialog.show(getSupportFragmentManager(), "update_dialog");
    }

    @Override
    protected void onDestroy() {
        Log.d("首頁執行確認_onDestroy", "啟動離線");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, UpdateService.class));
//        } else {
//            startService(new Intent(this, UpdateService.class));
//        }
        super.onDestroy();
        startService(new Intent(this, UpdateService.class));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            };

            DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            };

            Utility.showSimpleAlertDialog(this, getString(R.string.confirm_exit), posListener, negListener);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        prvTimerDestroy();
        Log.d("首頁執行確認_onPause", "啟動離線");

        super.onPause();
//        startService(new Intent(this, UpdateService.class));
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION:
                if (Utility.checkRequestPermissionsResultGranted(grantResults)) {
                    final ProgressDialog progDialog = Utility.showSimpleProgressDialog(this, true);

                    mLocationController.afterGotPermission();
                    mLocationController.requestLocationUpdates();

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (progDialog != null)
                                progDialog.dismiss();

                            prvOnClickIvMyPosition();
                        }
                    };

                    new Handler().postDelayed(runnable, Constant.MILLIS_1_SECOND);
                } else
//					{
//					mLocationController.setHasPermission(false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        if (!shouldShowRequestPermissionRationale(PERMISSION_LOCATION))
                            Utility.Preference.getInstance(this).putBoolean(ApplicationEx.PREF_KEY_DO_NOT_ASK_LOCATION_PERMISSION, true);
//					}

                break;

            case PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START:
                if (Utility.checkRequestPermissionsResultGranted(grantResults)) {
                    mLocationController.afterGotPermission();
                    mLocationController.requestLocationUpdates();
                }
//				else
//					mLocationController.setHasPermission(false);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utility.checkNetworkStatus(this)) {
            getSchedule();
            Log.d("首頁執行確認_onResume", "背景回來取得班表在有網路時");
        }
        prvTimerCreate();
        prvTimerScheduleClock();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
//        Intent intent = getIntent();
//        String msg = intent.getStringExtra("msg");
//        if (msg != null)
//            Log.d("FCM", "msg:" + msg);
    }

    @Override
    protected void onStop() {
        mLocationController.removeLocationUpdates();

        mGoogleApiClient.disconnect();

        super.onStop();
    }

    private boolean prvCheckLoginOnCreate() {
        LoginInfo info = getPrefLoginInfo(this);

        if (info == null) {
            prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_ON_CREATE);
            return false;
        } else {
            prvProcessControlLocationController();
            return true;
        }
    }

    private boolean sCanWiFiResultsList() {
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
//                unregisterReceiver(this);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiScanReceiver, intentFilter);
//        Log.d("_WIFI_SSID::", wifi.getConnectionInfo().getSSID());
//        Log.d("_WIFI_BSSID::", wifi.getConnectionInfo().getBSSID());
        boolean success = wifi.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
        if (scanFailure()) {
            return true;
        }
        return false;
    }

    private void scanSuccess() {
        List<ScanResult> results = wifi.getScanResults();
        Log.d("首頁執行確認_onResume", "WIFI清單:" + results);
    }
    private boolean scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        final List<ScanResult> results = wifi.getScanResults();
        Log.d("MainActivity", "getWifiListInWorking(wifi):" + getWifiListInWorking(wifi));
        if (getWifiListInWorking(wifi)) {
            results.clear();
            return true;
        } else {
            results.clear();
            return false;
        }
    }
    private boolean prvCheckNeedLoginBeforeShowCheckRecListActivity() {
        LoginInfo info = getPrefLoginInfo(this);

        if (info == null) {
            // Should not happened.

            return true;
        }

        Date currentDate = new Date();
        Date checkDate;

        //noinspection ConstantConditions
        if (ApplicationEx.TEST_LOGIN_INTERVAL > 0)
            checkDate = DateUtil.addDate(info.getLoginDate(), ApplicationEx.TEST_LOGIN_INTERVAL_UNIT, ApplicationEx.TEST_LOGIN_INTERVAL);
        else
            checkDate = DateUtil.addDate(info.getLoginDate(), Calendar.DATE, ApplicationEx.LOGIN_INTERVAL);

//		Util.logD("prvCheckLoginInterval(): info.getLoginDate() = " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(info.getLoginDate()));
//		Util.logD("prvCheckLoginInterval(): checkDate = " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(checkDate));
//		Util.logD("prvCheckLoginInterval(): currentDate = " + DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24.format(currentDate));

        if (!ApplicationEx.TEST_NEED_LOGIN)
            if (checkDate != null)
                if (checkDate.compareTo(currentDate) > 0)
                    return false;

        return true;
    }

    private void prvCheckFirstLogin(final Runnable runThis) {
        LoginInfo info = getPrefLoginInfo(this);

        if (info != null) {
            String empNoList = Utility.Preference.getInstance(this).getString(ApplicationEx.PREF_KEY_LOGIN_EMP_NO_LIST, Constant.NULL_STRING);

            if (!empNoList.contains(info.getEmpNo())) {
                DialogInterface.OnClickListener onClickListener;

                if (runThis != null)
                    onClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runThis.run();
                        }
                    };
                else
                    onClickListener = null;

                MainActivity.showFirstLoginAlertDialog(MainActivity.this, onClickListener);

                registerPrefLoginEmpNoList(MainActivity.this, info.getEmpNo());
            } else if (runThis != null)
                runThis.run();
        }
    }

    private boolean prvCheckGPS() {
        if (!mLocationController.checkPermission())
            return false;

        if (mLocationController.isLocationAvailable())
            return true;
        else {
            Utility.showSimpleAlertDialog(this, getString(R.string.please_turn_on_gps), null);

            return false;
        }
    }

    private void prvCheckStartUpdateService() {
//		Util.logD("prvCheckStartUpdateService(): Start");

        if (Utility.Preference.getInstance(this).getString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG) == null) {
//			Util.logD("prvCheckStartUpdateService(): Util.registerUpdateService(this)");

            //todo:離線拔除的部分。
            ApplicationEx.registerUpdateService(this);
            Utility.Preference.getInstance(this).putString(ApplicationEx.PREF_KEY_UPDATE_SERVICE_EXEC_LOG, Constant.NULL_STRING);
        }
    }

    private String prvComposeClockDialogMsg(Date checkDate, ClockPara para, int seriallyCheckInDays, CheckRec curCheckInRec) {
        String msg = DateUtil.formatDate(checkDate, DateUtil.FORMAT_YYYY_MM_DD_DASH_HH_MM_24);

        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
            switch (seriallyCheckInDays) {
                case ApplicationEx.DAYS_7:
                    msg += Constant.LINE_FEED_2 + getString(R.string.remind_days_7) + Constant.LINE_FEED;

                    break;

                case ApplicationEx.DAYS_6:
                    msg += Constant.LINE_FEED_2 + getString(R.string.remind_days_6) + Constant.LINE_FEED;

                    break;

                default:
                    msg += Constant.NULL_STRING;
            }

            if (curCheckInRec != null) {
                Date checkInDate = CheckRec.CHECK_DATE_FORMATTER.parse(curCheckInRec.getCheckDate());

                long secondDiff = DateUtil.dateDiff(checkInDate, para.getCheckDate(), Calendar.SECOND);

//				ApplicationEx.logD("prvComposeClockDialogMsg(): ApplicationEx.DELAY_CHECK_OUT_SECOND_9 = " + ApplicationEx.DELAY_CHECK_OUT_SECOND_9);
//				ApplicationEx.logD("prvComposeClockDialogMsg(): ApplicationEx.DELAY_CHECK_OUT_SECOND_13 = " + ApplicationEx.DELAY_CHECK_OUT_SECOND_13);
//				ApplicationEx.logD("prvComposeClockDialogMsg(): secondDiff = " + secondDiff);

                if (secondDiff > ApplicationEx.DELAY_CHECK_OUT_SECOND_13)
                    msg += Constant.LINE_FEED_2 + getString(R.string.clock_out_remind_string_13) + Constant.LINE_FEED;
                else if (secondDiff > ApplicationEx.DELAY_CHECK_OUT_SECOND_9)
                    msg += Constant.LINE_FEED_2 + getString(R.string.clock_out_remind_string_9) + Constant.LINE_FEED;
            }
        } else
            switch (para.getLocationStatus()) {
                case ClockPara.LOCATION_STATUS_SUCCESS:
                    msg += Constant.LINE_FEED_2 + para.getCheckAddr() + getString(R.string.location_fyi);

                    break;

                case ClockPara.LOCATION_STATUS_WITHOUT_ADDRESS:
                    msg += Constant.LINE_FEED_2 +
                            MainActivity.composeLocationDisplayString(para.getLoc()) + Constant.LINE_FEED +
                            getString(R.string.can_not_get_address);

                    break;

                default:
                    msg += Constant.LINE_FEED_2 + getString(R.string.unknown_location);
            }

        return msg;
    }

//    private void prvDismissClockProgressDialog() {
//        if (mClockProg != null) {
//            mClockProg.dismiss();
//            mClockProg = null;
//        } else {
//            mClockProg = null;
//        }
//    }

    private static void prvDisplayClock(TextView tvClockDate, TextView tvClockTime) {
//        Date date = new Date();
//
////		tvClockDate.setText(DateUtil.formatDate(date, DateUtil.FORMAT_Y_M_D_SLASH));
//        tvClockDate.setText(getNowDate());
//        tvClockTime.setText(DateUtil.formatDate(date, DateUtil.FORMAT_HH_MM_24));
    }

    private static void prvDisplayClock1(TextView tvClockDate, TextView tvClockTime, TextView tvClockDate1) {
        Date date = new Date();

//		tvClockDate.setText(DateUtil.formatDate(date, DateUtil.FORMAT_Y_M_D_SLASH));
        tvClockDate.setText(getNowDate());
        tvClockDate1.setText(getNowDate1());
        tvClockTime.setText(DateUtil.formatDate(date, DateUtil.FORMAT_HH_MM_24));
        serverTimeLog = "N";
    }

    private static void prvDisplayClock3(TextView tvClockDate, TextView tvClockTime, TextView tvClockDate1, String clockDate
            , String clockTime, String clockEE) {
//        Date date = new Date();
//
////		tvClockDate.setText(DateUtil.formatDate(date, DateUtil.FORMAT_Y_M_D_SLASH));

        tvClockDate.setText(clockDate);
        tvClockDate1.setText(clockEE);
        tvClockTime.setText(clockTime);
        serverTimeLog = "Y";
    }


    private void prvDoClockIn(final Date checkDate, final ClockPara para) {
        final int editTextId = ViewEx.generateViewId();
        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_IN, para);

        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (Utility.checkNetworkStatus(MainActivity.this)) {
                EditText editText = ((AlertDialog) dialog).findViewById(editTextId);

                if (editText != null)
                    para.setRemarkAddr(editText.getText().toString());

                if (prvSaveCheckRec(para, seriallyCheckDays)) {
                    prvHttpGetClockIn(para);
                    prvProcessViewTvRecentClockInfo();

                    ApplicationEx.cancelNotifyClockOutService9(MainActivity.this);
                    ApplicationEx.cancelNotifyClockOutService13(MainActivity.this);

                    ApplicationEx.registerNotifyClockOutService9(MainActivity.this);
                    ApplicationEx.registerNotifyClockOutService13(MainActivity.this);
                }

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_IN);

//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
//                }else {
//                    //todo:按下確認但無4G網路
//                    pvClockInTouch();
//                }
            }
        };

        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
            }
        };

//       todo : ************* WIFI打上班卡流程開始。*********************
//        Log.d("首頁執行確認_prvDoClockIn", "執行上班按鈕:" + workInRange2);
//        Log.d("首頁執行確認_prvDoClockIn", "執行上班按鈕:" + workOutRange2);
//        Log.d("首頁執行確認_prvDoClockIn", "第一個判斷式:" + workInRange1);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        getMainUI_Time();
        Log.d("Mode:", "sWitch_Wifi_Nfc:" + sWitch_Wifi_Nfc);
        Log.d("NFC_clockIn", "isTagNfc:" + isTagNfc);
        Log.d("NFC_clockIn", "openNFCtime:" + openNFCtime);
        if (isTagNfc) {
            //todo:判斷如果從NFC進來，傳給server的值 = 2 ，如果是WIFI則 = 1
            long endNFCtime = System.currentTimeMillis();
            Log.d("NFC_clockOUT", "endNFCtime - openNFCtime:" + (endNFCtime - openNFCtime));
            long ans = endNFCtime - openNFCtime;
            if (ans > 6000) {
//                return;
                isTagNfc = false;
                sWitch_Wifi_Nfc = "1";
                showDialogMD("NFC驗證失敗", "聯合e打卡發現您的NFC驗證超過60秒未送出打卡，驗證已失效，請重新感應");
                return;
            } else {
                sWitch_Wifi_Nfc = "2";
            }
        } else {
            sWitch_Wifi_Nfc = "1";
        }
        if (workInRange1 == null) {
            getWorkRange();
            return;
        }
        String checkAcacheDate = input.format(workInRange1);
        String newDate = input.format(new Date());
        Log.d("首頁執行確認_prvDoClockIn", "本機記憶日期" + checkAcacheDate);
        Log.d("首頁執行確認_prvDoClockIn", "本機現在日期" + newDate);
        if (checkDate.after(workOutRange2)&&checkDate.before(workInRange2)) {
            Log.d("首頁執行確認_prvDoClockIn", "超過下班時間30分中按取上班紐");
            //todo 超過下班時間30分鐘按取上班按鈕。
            ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
                @Override
                public void setChangeTest(String s) {
                    if (isTagNfc) {
                        sWitch_Wifi_Nfc = "2";
                    } else {
                        sWitch_Wifi_Nfc = "1";
                    }
                    ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                    ACache.get(MainActivity.this).put("unusualStatus", "1");
                    String title = getString(R.string.confirm_clock_in);
                    String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
                    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                        Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                    } else {
                        prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                    }
                }

            });
            d.show();
            return;
        }
        //todo:上班時間不等於空值，上班時間等於今日時間。
        if (workInRange1 != null && newDate.equals(checkAcacheDate)) {
            //todo:判斷WIFI有無開啟，或是否從NFC進來
            if (wifi.isWifiEnabled() || isTagNfc) {
                //todo:判斷是否為公司WIFI，或是否為NFC進來
                if (sCanWiFiResultsList() || isTagNfc) {
                    //todo:判斷該時間點為上班前30分跟下班後30分內
                    if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {
                        //todo:(WorkIn0)此段為正常上班流程。
                        if (isTagNfc) {
                            sWitch_Wifi_Nfc = "2";
                        } else {
                            sWitch_Wifi_Nfc = "1";
                        }
                        ACache.get(MainActivity.this).put("unusualStatus", "0"); //是否異常打卡0=f 1=t
                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                        String title = getString(R.string.confirm_clock_in);
                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
                        } else {
                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                        }
                        //todo:(WorkIn0)此段為正常上班流程。
                    } else {
                        //todo:(WorkIn0-1)此段為時間異常上班流程。
                        if (isTagNfc) {
                            sWitch_Wifi_Nfc = "2";
                        } else {
                            sWitch_Wifi_Nfc = "1";
                        }
                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                        ACache.get(MainActivity.this).put("unusualStatus", "1");
                        pvtOnWorkDialog(checkDate);
                        //todo:(WorkIn0-1)此段為時間異常上班流程。
                    }
                } else {
                    //todo:此段為非公司WIFI
                    String test;
                    if (thisMobileHaveNfc) {
                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
                    } else {
                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內並重新打卡";
                    }
                    final AlertDialog.Builder notInWorkWifi_dialog = new AlertDialog.Builder(this);
                    notInWorkWifi_dialog.setTitle("非公司Wi-Fi");
                    notInWorkWifi_dialog.setMessage(test);
                    notInWorkWifi_dialog.setCancelable(false);
                    notInWorkWifi_dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            //todo:略過後判斷打卡時間有沒有在上班時間+30分之後為正常。
                            if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {
                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                                String title = getString(R.string.confirm_clock_in);
                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                                } else {
                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                                }
                            } else {
                                //todo:進入異常原因選擇及時間調整流程。
                                if (isTagNfc) {
                                    sWitch_Wifi_Nfc = "2";
                                } else {
                                    sWitch_Wifi_Nfc = "1";
                                }
                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                                pvtOnWorkDialog(checkDate);
                            }
                        }
                    });
                    notInWorkWifi_dialog.setPositiveButton("重新偵測後打卡", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (wifi.isWifiEnabled()) {
                                getWifiListInWorking(wifi);
                                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "稍等3秒鐘...", true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(6000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            progressDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pvClockInTouch();//開啟之後在啟動打卡按鈕
                                                }
                                            });

                                        }
                                    }
                                }).start();
                            }
                        }
                    });
                    notInWorkWifi_dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    notInWorkWifi_dialog.show();
                }
            } else {
                //todo:此段為WIFI沒有開啟
                //todo::沒有WIFI 啟動開啟diaLOG
                String test;
                if (thisMobileHaveNfc) {
                    test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
                } else {
                    test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線並重新打卡";
                }
                AlertDialog.Builder notOpenWifi_dialog = new AlertDialog.Builder(this);
                notOpenWifi_dialog.setTitle("無Wi-Fi連線");
                notOpenWifi_dialog.setMessage(test);
                notOpenWifi_dialog.setCancelable(false);
                notOpenWifi_dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //todo:判斷打卡時間有沒有在上班時間+30分之後為正常。
                        if (checkDate.after(workInRange2) && checkDate.before(workOutRange2)) {
                            ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
                            ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                            String title = getString(R.string.confirm_clock_in);
                            String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
                            if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                                Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                            } else {
                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                            }

                        } else {
                            //todo: 進入異常原因選擇及時間調整流程。
                            ACache.get(MainActivity.this).put("SSIDCheckin", "0");//是否使用公司Wi-Fi打卡 0=false 1=true
                            ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                            pvtOnWorkDialog(checkDate);
                        }
                    }
                });
                notOpenWifi_dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        wifi.setWifiEnabled(true);
                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "稍等3秒鐘...", true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(6000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pvClockInTouch();//開啟之後在啟動打卡按鈕
                                        }
                                    });

                                }
                            }
                        }).start();
                    }
                });
                notOpenWifi_dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                notOpenWifi_dialog.show();
            }

        } else {
            //todo:此段為上班時間錯誤
            getNowDateIsTrue = false;
            if (getNowDateIsTrue != null && !getNowDateIsTrue) {
                //todo : 班表若為無資料狀態，打卡時都視為異常打卡，選擇原因，但不選擇時間，已當下時間打卡
                ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
                    @Override
                    public void setChangeTest(String s) {
                        if (isTagNfc) {
                            sWitch_Wifi_Nfc = "2";
                        } else {
                            sWitch_Wifi_Nfc = "1";
                        }
                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                        ACache.get(MainActivity.this).put("unusualStatus", "1");
                        String title = getString(R.string.confirm_clock_in);
                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);
                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                            Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                        } else {
                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                        }
                    }
                });
                d.show();
            }
        }
//       todo : ************* WIFI打上班卡流程結束。*********************
    }

    private void prvDoClockOut(final Date checkDate, final ClockPara para) {
        final int editTextId = ViewEx.generateViewId();
        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_OUT, para);

        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (Utility.checkNetworkStatus(MainActivity.this)) {
                EditText et = ((AlertDialog) dialog).findViewById(editTextId);

                if (et != null)
                    para.setRemarkAddr(et.getText().toString());

                if (prvSaveCheckRec(para, seriallyCheckDays))
//				if (prvSaveCheckRec(para, DBManager.NO_1))
                {
                    prvHttpGetClockOut(para);
                    prvProcessViewTvRecentClockInfo();

                    ApplicationEx.cancelNotifyClockOutService9(MainActivity.this);
                    ApplicationEx.cancelNotifyClockOutService13(MainActivity.this);
                }

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_OUT);

//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
//                }else {
////                    Toast.makeText(MainActivity.this, "離線無4G", Toast.LENGTH_SHORT).show();
//                    //todo:按下確認之後因無4G所以在跑一遍。
//                    pvClockOutTouch();
//                }
            }
        };

        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
            }
        };
        // todo: *************WIFI打下班卡流程開始。*********************
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat input2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getMainUI_Time();
        if (workOutRange2 == null) {
            getWorkRange();
            return;
        }
        Log.d("Mode:", "sWitch_Wifi_Nfc:" + sWitch_Wifi_Nfc);
        Log.d("NFC_clockOUT", "isTagNfc:" + isTagNfc);
        Log.d("NFC_clockOUT", "openNFCtime:" + openNFCtime);
        if (isTagNfc) {
            long endNFCtime = System.currentTimeMillis();
            Log.d("NFC_clockOUT", "endNFCtime - openNFCtime:" + (endNFCtime - openNFCtime));
            long ans = endNFCtime - openNFCtime;
            if (ans > 300000) {
//                return;
                isTagNfc = false;
            }
        }
        String checkAcacheDate = input.format(workOutRange2);
        String checkAcacheDate1 = input.format(workOutRange3);

        long workoutEND = workOutRange2.getTime();
        long workoutST = workOutRange3.getTime();
        long workoutNow = new Date().getTime();
        Log.d("MainActivity", "workOutRange3:" + workoutEND + "::" + workoutST + "::" + workoutNow);
        String newDate = input.format(new Date());
        if (checkDate.before(workInRange2)) {
            //todo 超過下班時間30分鐘按取上班按鈕。
            ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
                @Override
                public void setChangeTest(String s) {
                    if (isTagNfc) {
                        sWitch_Wifi_Nfc = "2";
                    } else {
                        sWitch_Wifi_Nfc = "1";
                    }
                    ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                    ACache.get(MainActivity.this).put("unusualStatus", "1");
                    String title = getString(R.string.confirm_clock_out);
                    String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
                    if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
                        Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                    else
                        prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                }

            });
            d.show();
            return;
        }
        //todo:下班時間不是空值。
        if (workOutRange2 != null) {
            //todo:判斷WIFI開啟及是否NFC進入
            if (wifi.isWifiEnabled() || isTagNfc) {
                //todo:判斷公司WIFI及是否NFC進入
                if (sCanWiFiResultsList() || isTagNfc) {
                    //todo:判斷下班時間+30分鐘之前為正常
                    if (checkDate.before(workOutRange2)) {
                        //todo::正常下班流程。
                        if (isTagNfc) {
                            sWitch_Wifi_Nfc = "2";
                        } else {
                            sWitch_Wifi_Nfc = "1";
                        }
                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                        ACache.get(MainActivity.this).put("unusualStatus", "0");//是否異常打卡0=f 1=t
                        String title = getString(R.string.confirm_clock_out);
                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));
                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                            Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                        } else {
                            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                        }
                        //todo::正常下班流程。
                    } else {
                        //todo:判斷下班時間+30分鐘之後異常
                        if (isTagNfc) {
                            sWitch_Wifi_Nfc = "2";
                        } else {
                            sWitch_Wifi_Nfc = "1";
                        }
                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                        ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                        pvtOffWorkDialog(checkDate);
                    }
                } else {
                    //todo:WIFI不再公司範圍內
                    String test;
                    if (thisMobileHaveNfc) {
                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
                    } else {
                        test = "聯合e打卡未發現公司Wi-Fi，請確認您的位置在公司內部Wi-Fi連線範圍內並重新打卡";
                    }
                    AlertDialog.Builder notInWorkWifi_dialog = new AlertDialog.Builder(this);
                    notInWorkWifi_dialog.setTitle("非公司Wi-Fi");
                    notInWorkWifi_dialog.setMessage(test);
                    notInWorkWifi_dialog.setCancelable(false);
                    notInWorkWifi_dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
                                //todo: 進入異常原因選擇及時間調整流程。
                                pvtOffWorkDialog(checkDate);
                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                            } else {
                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                                //todo::正常下班流程。
                                String title = getString(R.string.confirm_clock_out);
                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                                } else {
                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                                }
                            }
                        }
                    });
                    notInWorkWifi_dialog.setPositiveButton("重新偵測後打卡", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (wifi.isWifiEnabled()) {
                                getWifiListInWorking(wifi);
                                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "稍等3秒鐘...", true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(6000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            progressDialog.dismiss();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pvClockInTouch();//開啟之後在啟動打卡按鈕
                                                }
                                            });

                                        }
                                    }
                                }).start();
                            }
                        }
                    });
                    notInWorkWifi_dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    notInWorkWifi_dialog.show();
                }
            }else {
                //todo:WIFI沒有開啟
                if (!wifi.isWifiEnabled()) {
                    String test;
                    if (thisMobileHaveNfc) {
                        test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線或手動開啟行動網路並改用NFC驗證，前往公司貼有NFC標籤處進行感應，並重新打卡";
                    } else {
                        test = "聯合e打卡發現，您目前未開啟Wi-Fi，建議依照您所在的公司內部位置，選擇正確的Wi-Fi連線並重新打卡";
                    }
                    AlertDialog.Builder notOpenWifi_dialog = new AlertDialog.Builder(this);
                    notOpenWifi_dialog.setTitle("無Wi-Fi連線");
                    notOpenWifi_dialog.setMessage(test);
                    notOpenWifi_dialog.setCancelable(false);
                    notOpenWifi_dialog.setNegativeButton("略過(無驗證)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (checkDate.after(workOutRange2)) {//todo:判斷下班時間+30分之後為異常
                                //todo: 進入異常原因選擇及時間調整流程。
                                pvtOffWorkDialog(checkDate);
                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t

                            } else {
                                ACache.get(MainActivity.this).put("SSIDCheckin", "0");
                                ACache.get(MainActivity.this).put("unusualStatus", "1");//是否異常打卡0=f 1=t
                                //todo::正常下班流程。
                                String title = getString(R.string.confirm_clock_out);
                                String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));

                                if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                                    Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                                } else {
                                    prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                                }
                            }
                        }
                    });
                    notOpenWifi_dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            wifi.setWifiEnabled(true);
                            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "稍等3秒鐘...", true);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        progressDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pvClockOutTouch();//開啟之後在啟動打卡按鈕
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    });
                    notOpenWifi_dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    notOpenWifi_dialog.show();
                }
            }
        }else {
            //todo:此段為下班時間錯誤
            getNowDateIsTrue = false;
            if (!getNowDateIsTrue) {
                //todo :表若為無資料狀態，打卡時都視為異常打卡，選擇原因，但不選擇時間，已當下時間打卡
                ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
                    @Override
                    public void setChangeTest(String s) {
                        if (isTagNfc) {
                            sWitch_Wifi_Nfc = "2";
                        } else {
                            sWitch_Wifi_Nfc = "1";
                        }
                        ACache.get(MainActivity.this).put("SSIDCheckin", sWitch_Wifi_Nfc);
                        ACache.get(MainActivity.this).put("unusualStatus", "1");
                        String title = getString(R.string.confirm_clock_out);
                        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(MainActivity.this).getCurrentCheckInRec(para.getEmpId()));
                        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR) {
                            Utility.showSimpleAlertDialog(MainActivity.this, title, msg, posListener, negListener);
                        }else{
                                prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
                            }
                        }

                });
                d.show();
            }
        }
//       todo : ************* WIFI打下班卡流程結束。*********************
    }

    private int prvGetSeriallyCheckDays(String checkType, ClockPara para) {
        CheckRecListInNDays checkRecListInNDays = CheckRecListInNDays.fromSqlite(MainActivity.this, para.getEmpId(), para.getCheckDate(), checkType);

        int seriallyCheckDays;

        if (checkRecListInNDays.isSerially(ApplicationEx.DAYS_6)) {
//			Utility.logD("prvGetSeriallyCheckDays(): checkRecListInNDays.isSerially(ApplicationEx.DAYS_6)");

            seriallyCheckDays = ApplicationEx.DAYS_7;
        } else if (checkRecListInNDays.isSerially(ApplicationEx.DAYS_5)) {
//			Utility.logD("prvGetSeriallyCheckDays(): checkRecListInNDays.isSerially(ApplicationEx.DAYS_5)");

            if (DateUtil.getWeekDay(para.getCheckDate()) == Calendar.SATURDAY) {
//				Utility.logD("prvGetSeriallyCheckDays(): DateUtil.getWeekDay(para.getCheckDate()) == Calendar.SATURDAY");

                seriallyCheckDays = ApplicationEx.DAYS_6;
            } else
                seriallyCheckDays = 0;
        } else
            seriallyCheckDays = 0;

//		Utility.logD("prvGetSeriallyCheckDays(): seriallyCheckDays = " + seriallyCheckDays);

        return seriallyCheckDays;
    }


    private void prvHttpGetClockIn(final ClockPara clockPara) {
//todo:新版打卡上班透過lambda。
        if (Utility.checkNetworkStatus(this)) {
            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "等待系統回應中", true);
            putLambdaRecive_task = (PutLambdaRecive_Task) new PutLambdaRecive_Task(MainActivity.this, clockPara, true, new PutLambdaRecive_Task.GetSeverResult() {
                @Override
                public void setSeverResult(String result) {
                    Log.d("首頁執行確認_prvClockIn", "上班打卡有無成功:" + result);
                    if (result.equals("打卡成功")) {
                        progressDialog.dismiss();
                        ACache.get(MainActivity.this).put("unusualStatus", "");
                        ACache.get(MainActivity.this).put("adjustDate", "");
                        ACache.get(MainActivity.this).put("adjustComment", "");
                        ACache.get(MainActivity.this).put("Wi_FiSSID", "");
                        ACache.get(MainActivity.this).put("SSIDCheckin", "");

                        boolean needLogin = clockPara.getCheckType().equals(DBManager.CHECK_TYPE_IN) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
                        Log.d("首頁執行確認_prvClockIn", "needLogin:" + needLogin);
                        prvStartCheckRecListActivity(needLogin, null);

                        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
                        TextView textView = view.findViewById(R.id.setTextId);
                        textView.setText("打卡成功");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
                        Toast toast = new Toast(MainActivity.this);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(view);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        CheckRecKey checkRecKey = new CheckRecKey();
                        checkRecKey.setEmpId(clockPara.getEmpId());
                        checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
                        checkRecKey.setCheckType(clockPara.getCheckType());
                        Log.d("打卡失敗", "STAR");
                        prvStartClockFailActivity(checkRecKey);

                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
//                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                        MainActivity.this.startActivity(intent);
                    }
                }
            }).start();
//            putLambdaRecive_task = (PutLambdaRecive_Task) new PutLambdaRecive_Task(MainActivity.this, clockPara, true, new PutLambdaRecive_Task.GetSeverResult() {
//                @Override
//                public void setSeverResult(String result) {
//                    Log.d("首頁執行確認_prvClockIn", "上班打卡有無成功:" + result);
//                    if (result.equals("打卡成功")) {
//                        ACache.get(MainActivity.this).put("unusualStatus", "");
//                        ACache.get(MainActivity.this).put("adjustDate", "");
//                        ACache.get(MainActivity.this).put("adjustComment", "");
//                        ACache.get(MainActivity.this).put("Wi_FiSSID", "");
//                        ACache.get(MainActivity.this).put("SSIDCheckin", "");
//
//                        boolean needLogin = clockPara.getCheckType().equals(DBManager.CHECK_TYPE_IN) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
//                        Log.d("首頁執行確認_prvClockIn", "needLogin:" + needLogin);
//                        prvStartCheckRecListActivity(needLogin, null);
//
//                        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
//                        TextView textView = view.findViewById(R.id.setTextId);
//                        textView.setText("打卡成功");
//                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
//                        Toast toast = new Toast(MainActivity.this);
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
        } else {
            CheckRecKey checkRecKey = new CheckRecKey();
            checkRecKey.setEmpId(clockPara.getEmpId());
            checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
            checkRecKey.setCheckType(clockPara.getCheckType());
            Log.d("離線打卡", "STAR");
            getDBtestList(clockPara.getEmpId());

            prvStartClockFailActivity(checkRecKey);
        }
    }

    private void prvHttpGetClockOut(final ClockPara clockPara) {
        //todo:下班打卡上傳。
        //todo:新版打卡下班透過lambda。
        if (Utility.checkNetworkStatus(this)) {
            final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "請稍後", "等待系統回應中", true);
            putLambdaRecive_task = (PutLambdaRecive_Task) new PutLambdaRecive_Task(MainActivity.this, clockPara, true, new PutLambdaRecive_Task.GetSeverResult() {
                @Override
                public void setSeverResult(String result) {
                    Log.d("首頁執行確認_prvClockOut", "下班打卡有無成功:" + result);
                    if (result.equals("打卡成功")) {
                        progressDialog.dismiss();
                        ACache.get(MainActivity.this).put("unusualStatus", "");
                        ACache.get(MainActivity.this).put("adjustDate", "");
                        ACache.get(MainActivity.this).put("adjustComment", "");
                        ACache.get(MainActivity.this).put("Wi_FiSSID", "");
                        ACache.get(MainActivity.this).put("SSIDCheckin", "");
                        boolean needLogin = clockPara.getCheckType().equals(DBManager.CHECK_TYPE_OUT) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
                        Log.d("首頁執行確認_prvClockOut", "needLogin:" + needLogin);
                        prvStartCheckRecListActivity(needLogin, null);

                        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
                        TextView textView = view.findViewById(R.id.setTextId);
                        textView.setText("打卡成功");
                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
                        Toast toast = new Toast(MainActivity.this);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(view);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        CheckRecKey checkRecKey = new CheckRecKey();
                        checkRecKey.setEmpId(clockPara.getEmpId());
                        checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
                        checkRecKey.setCheckType(clockPara.getCheckType());
                        Log.d("打卡失敗", "STAR");
                        prvStartClockFailActivity(checkRecKey);

                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(4000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }
                }
            }).start();
//            putLambdaRecive_task = (PutLambdaRecive_Task) new PutLambdaRecive_Task(MainActivity.this, clockPara, true, new PutLambdaRecive_Task.GetSeverResult() {
//                @Override
//                public void setSeverResult(String result) {
//                    Log.d("首頁執行確認_prvClockOut", "下班打卡有無成功:" + result);
//                    if (result.equals("打卡成功")) {
//                        ACache.get(MainActivity.this).put("unusualStatus", "");
//                        ACache.get(MainActivity.this).put("adjustDate", "");
//                        ACache.get(MainActivity.this).put("adjustComment", "");
//                        ACache.get(MainActivity.this).put("Wi_FiSSID", "");
//                        ACache.get(MainActivity.this).put("SSIDCheckin", "");
//                        boolean needLogin = clockPara.getCheckType().equals(DBManager.CHECK_TYPE_OUT) && prvCheckNeedLoginBeforeShowCheckRecListActivity();
//                        Log.d("首頁執行確認_prvClockOut", "needLogin:" + needLogin);
//                        prvStartCheckRecListActivity(needLogin, null);
//
//                        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
//                        TextView textView = view.findViewById(R.id.setTextId);
//                        textView.setText("打卡成功");
//                        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
//                        Toast toast = new Toast(MainActivity.this);
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
        } else {
            CheckRecKey checkRecKey = new CheckRecKey();
            checkRecKey.setEmpId(clockPara.getEmpId());
            checkRecKey.setCheckDate(CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()));
            checkRecKey.setCheckType(clockPara.getCheckType());
            Log.d("離線打卡", "STAR");
            getDBtestList(clockPara.getEmpId());

            prvStartClockFailActivity(checkRecKey);
        }
    }

    private void prvOnActivityResultCheckRecList(Intent data) {
        if (data == null)
            return;

        boolean needLogin = data.getBooleanExtra(CheckRecListActivity.EXTRA_NEED_LOGIN, false);

        if (needLogin)
            prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_CHECK_LOGIN_FOR_OVER_INTERVAL);
//			prvStartLoginActivity(REQUEST_CODE_LOGIN_1);
    }

    private void prvOnActivityResultClockFail(Intent data) {
        if (data == null)
            return;

        CheckRecKey checkRecKey = (CheckRecKey) data.getSerializableExtra(ClockFailActivity.EXTRA_CHECK_REC_KEY);

        if (checkRecKey != null)
            prvStartCheckRecListActivity(prvCheckNeedLoginBeforeShowCheckRecListActivity(), checkRecKey);
    }

    private void prvOnActivityResultLogin(boolean needProcessLocController) {
        Runnable runThis;

        if (needProcessLocController)
            runThis = new Runnable() {
                @Override
                public void run() {
                    prvProcessControlLocationController();
                }
            };
        else
            runThis = null;

        prvProcessViewTvHello();
        prvProcessViewIvClockInOut(Utility.getDisplayMetrics(this));
        prvProcessViewTvRecentClockInfo();

        prvCheckFirstLogin(runThis);
        getSchedule();
    }

    private void prvOnClickIvMyPosition() {
        if (!prvCheckGPS())
            return;

        final Location loc = mLocationController.getCurrentLocation();

        if (loc == null) {
            Utility.showSimpleAlertDialog(MainActivity.this, getString(R.string.unknown_location), null);
//            GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_WITHOUT_LOC);
            return;
        }

        if (!Utility.checkNetworkStatus(MainActivity.this)) {
            Utility.showSimpleAlertDialog(MainActivity.this, getString(R.string.can_not_get_address_no_network), null);
//            GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_MY_POS, GoogleAnalyticsManager.ACTION_MY_POS_LOC, GoogleAnalyticsManager.LABEL_WITHOUT_ADDR);
            return;
        }

        AsyncGetAddressStatic.AsyncGetAddressPara para = new AsyncGetAddressStatic.AsyncGetAddressPara(loc.getLatitude(), loc.getLongitude());
        new PrvAsyncGetAddressMyPos(MainActivity.this).execute(para);
    }

    private ClockPara prvPrepareClockPara(Date checkDate, String checkType) {
        LoginInfo info = getPrefLoginInfo(this);
        //寫入欄位第方。
        String adjustComment;
        String Wi_FiSSID;
        String SSIDCheckin;
        String unusualStatus;
        String adjustDate;
        if (ACache.get(MainActivity.this).getAsString("unusualStatus") != null) {
            unusualStatus = ACache.get(MainActivity.this).getAsString("unusualStatus");
        } else {
            unusualStatus = "";
        }
        if (ACache.get(MainActivity.this).getAsString("adjustDate") != null) {
            Log.d("setAdjustDate", ACache.get(MainActivity.this).getAsString("adjustDate"));

            adjustDate = ACache.get(MainActivity.this).getAsString("adjustDate");
        } else {
            adjustDate = "";
        }
        if (ACache.get(MainActivity.this).getAsString("adjustComment") != null) {
            adjustComment = ACache.get(MainActivity.this).getAsString("adjustComment");
        } else {
            adjustComment = "";
        }
        if (ACache.get(MainActivity.this).getAsString("Wi_FiSSID") != null) {
            Wi_FiSSID = ACache.get(MainActivity.this).getAsString("Wi_FiSSID");
        } else {
            Wi_FiSSID = "";
        }
        if (ACache.get(MainActivity.this).getAsString("SSIDCheckin") != null) {
            SSIDCheckin = ACache.get(MainActivity.this).getAsString("SSIDCheckin");
        } else {
            SSIDCheckin = "";
        }
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String adjustReceiveDate = input.format(date);
        Log.d("MainActivity", adjustReceiveDate);
        if (info == null)
            return null;

        ClockPara para = new ClockPara();

        para.setEmpId(info.getEmpNo());
        para.setCheckDateYYYYMMDDHHMMSS(checkDate);
        para.setCheckType(checkType);
        //0328
        para.setmUnusualStatus(unusualStatus);
        para.setmAdjustDate(adjustDate);
        para.setmAdjustComment(adjustComment);
        para.setmAdjustReceiveDate(adjustReceiveDate);
        para.setmWi_FiSSID(Wi_FiSSID);
        para.setmSSIDCheckin(SSIDCheckin);

        para.setDeviceId(Build.SERIAL);

        Location loc = mLocationController.getCurrentLocation();

        if (loc != null) {
            para.setLoc(loc);

            DecimalFormat decimalFormat = new DecimalFormat("0.00000");

            para.setGpsInfo(decimalFormat.format(loc.getLatitude()) + Constant.COMMA + decimalFormat.format(loc.getLongitude()));
        }

        return para;
    }

    private void prvProcessControl() {
        DBManager.getInstance(this);

        prvProcessControlGoogleApiClient();
//        prvProcessControlIvMyPosition();
        prvProcessControlTvLogout();
        prvProcessControlIvClockIn();
        prvProcessControlIvClockOut();
        prvProcessControlIvViewLog();
        prvProcessControlTvClockTime();

//		prvProcessControlLocationController();
    }

    private void prvProcessControlGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new PrvConnectionCallbacks())
                .addOnConnectionFailedListener(new PrvOnConnectionFailedListener())
                .addApi(LocationServices.API)
                .build();
    }

    private void prvProcessControlIvClockIn() {
        ImageView ivClockIn = findViewById(R.id.ivClockIn);


        ivClockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.checkNetworkStatus(MainActivity.this)) {
                    pvClockInTouch();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(R.string.network_error_title);
                    dialog.setMessage(R.string.network_error_message);
                    dialog.setCancelable(false);
//                    dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            wifi.setWifiEnabled(true);
//                        }
//                    });
                    dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    private void prvProcessControlIvClockOut() {
        ImageView ivClockOut = findViewById(R.id.ivClockOut);

        ivClockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.checkNetworkStatus(MainActivity.this)) {
                    pvClockOutTouch();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(R.string.network_error_title);
                    dialog.setMessage(R.string.network_error_message);
                    dialog.setCancelable(false);
//                    dialog.setPositiveButton("開啟 Wi-Fi後打卡", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            wifi.setWifiEnabled(true);
//                        }
//                    });
                    dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    private void prvProcessControlIvMyPosition() {
        ImageView ivMyPosition = findViewById(R.id.ivMyPosition);

        if (ApplicationEx.IMPLEMENT_HIDE_POSITION_ICON) {
            ivMyPosition.setVisibility(View.GONE);
            return;
        }

        ViewEx.registerSimpleOnTouchVisualEffect(ivMyPosition);
//		Utility.registerSimpleOnTouchVisualEffect(ivMyPosition);

        ivMyPosition.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (Utility.checkPermission(MainActivity.this, PERMISSION_LOCATION))
                    prvOnClickIvMyPosition();
                else {
                    if (Utility.Preference.getInstance(MainActivity.this).getBoolean(ApplicationEx.PREF_KEY_DO_NOT_ASK_LOCATION_PERMISSION, false)) {
                        String title = getString(R.string.you_did_not_grand_permission_loc);
                        String msg = getString(R.string.how_to_grand_permission_loc);

                        Utility.showSimpleAlertDialog(MainActivity.this, title, msg, null);

                        return;
                    }


                    if (shouldShowRequestPermissionRationale(PERMISSION_LOCATION)) {
                        requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION);

                    } else
                        requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_MY_POSITION);
                }
            }
        });
    }

    private void prvProcessControlIvViewLog() {
        ImageView ivViewLog = findViewById(R.id.ivViewLog);

        ViewEx.registerSimpleOnTouchVisualEffect(ivViewLog);
//		Utility.registerSimpleOnTouchVisualEffect(ivViewLog);

        ivViewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prvStartCheckRecListActivity(false, null);
            }
        });
    }

    @SuppressLint("NewApi")
    private void prvProcessControlLocationController() {

        //檢查更新
        checkUpdate();

        if (Utility.checkPermission(this, PERMISSION_LOCATION)) {
            mLocationController.afterGotPermission();
            mLocationController.requestLocationUpdates();
        } else {
            if (shouldShowRequestPermissionRationale(PERMISSION_LOCATION)) {
                requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START);
            } else {
                requestPermissions(new String[]{PERMISSION_LOCATION}, PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION_AT_START);
            }
        }
    }

    private void prvProcessControlTvClockTime() {
        OnClickCounter onClickCounter = new OnClickCounter(new OnClickCounter.DebugModeOnClickListenerCaller() {
            @Override
            public void onCheckCountAchieved() {
                TextView tvUpdateServiceLog = findViewById(R.id.tvUpdateServiceLog);
                TextView tvUpdateTimeLog = findViewById(R.id.tvUpdateTimeLog);

                TextView tvLatLng = findViewById(R.id.tvLatLng);
                final LoginInfo info = getPrefLoginInfo(MainActivity.this);
                tvUpdateServiceLog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, CheckRecListActivity.class);
                        intent.putExtra(CheckRecListActivity.EXTRA_EMP_ID, info.getEmpNo());
                        MainActivity.this.startActivityForResult(intent, REQUEST_CODE_CHECK_REC_LIST);
                    }
                });
                if (Utility.getPrefIsDebug(MainActivity.this)) {
                    Utility.showToast(MainActivity.this, R.string.disable_debug_mode);
                    Utility.registerPrefIsDebug(MainActivity.this, false);
                    tvUpdateTimeLog.setVisibility(View.GONE);
                    tvUpdateServiceLog.setVisibility(View.GONE);
                    tvLatLng.setVisibility(View.GONE);
                } else {
                    Utility.showToast(MainActivity.this, R.string.enable_debug_mode);
                    Utility.registerPrefIsDebug(MainActivity.this, true);

                    tvUpdateServiceLog.setVisibility(View.VISIBLE);
                    tvUpdateTimeLog.setVisibility(View.VISIBLE);
                    tvLatLng.setVisibility(View.VISIBLE);
                }

                findViewById(R.id.rlClock).requestLayout();
            }
        }, Constant.DEBUG_CLICK_10);

        findViewById(R.id.tvClockTime).setOnClickListener(onClickCounter);
    }

    private void prvProcessControlTvLogout() {
        findViewById(R.id.tvLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(MainActivity.this);

                prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_LOGOUT);
            }
        });
    }

    private void prvProcessView() {
        DisplayMetrics dispMetrics = Utility.getDisplayMetrics(this);

        prvProcessViewIvClockBackground(dispMetrics.widthPixels);
//        prvProcessViewTvClock();
        prvProcessViewTvHello();
        prvProcessViewIvClockInOut(dispMetrics);
        prvProcessViewTvRecentClockInfo();
        prvProcessViewIvViewLog(dispMetrics.widthPixels);
        prvProcessViewTvVersionInfo();

        if (Utility.getPrefIsDebug(this)) {
            findViewById(R.id.tvUpdateServiceLog).setVisibility(View.VISIBLE);
            findViewById(R.id.tvLatLng).setVisibility(View.VISIBLE);
            findViewById(R.id.tvUpdateTimeLog).setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("deprecation")
    private void prvProcessViewIvClockBackground(int screenWidth) {
        ImageView ivClockBackground = findViewById(R.id.ivClockBackground);

//		new ImageViewEx(ivClockBackground).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.bg_index), screenWidth);
//		new ViewEx(ivClockBackground).setAspect(screenWidth, (int) (screenWidth * 0.4));
    }

    private void prvProcessViewIvClockInOut(final DisplayMetrics dispMetrics) {
        final LinearLayout llClockInOut = findViewById(R.id.llClockInOut);

        llClockInOut.post(new Runnable() {
            //			@SuppressWarnings("deprecation")
            @Override
            public void run() {
                int height = (int) (llClockInOut.getHeight() * 0.5);
                int width = (int) (dispMetrics.widthPixels * 0.5);

                if (width > height)
                    //noinspection SuspiciousNameCombination
                    width = height;

                int padWidth = (dispMetrics.widthPixels - (width * 2)) / 3;

//                new ViewEx(findViewById(R.id.vClockInOutPadder1)).setAspect(padWidth, height);
//                new ImageViewEx((ImageView) findViewById(R.id.ivClockIn)).displayWithCustomizeWidth(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_work_on_64px), width);
//                ((ImageView) findViewById(R.id.ivClockIn)).setBackgroundResource(R.drawable.splse);
//                AnimationDrawable animationDrawable;
//                animationDrawable = (AnimationDrawable) ((ImageView) findViewById(R.id.ivClockIn)).getBackground();
//
//                animationDrawable.start();

// new ImageViewEx((ImageView) findViewById(R.id.ivClockIn)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.selector_iv_clock_in), width);

//                new ViewEx(findViewById(R.id.vClockInOutPadder2)).setAspect(padWidth, height);
//                new ImageViewEx((ImageView) findViewById(R.id.ivClockOut)).displayWithCustomizeWidth(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_work_off_64px), width);
//                ((ImageView) findViewById(R.id.ivClockOut)).setBackgroundResource(R.drawable.btn_circle);
// new ImageViewEx((ImageView) findViewById(R.id.ivClockOut)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.selector_iv_clock_out), width);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void prvProcessViewIvViewLog(int screenWidth) {
//		new ImageViewEx((ImageView) findViewById(R.id.ivViewLog)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.btn_inquiry), (int) (screenWidth * 0.9));
    }

    @SuppressWarnings("deprecation")
    private void prvProcessViewTvClock() {
        mTvClockDate = findViewById(R.id.tvClockDate);
        mTvClockTime = findViewById(R.id.tvClockTime);
        tvClockDate1 = findViewById(R.id.tvClockDate1);
        final float DELTA = 2;
        final float DELTA_XLARGE = 4;

        float delta = Utility.isXLargeScreen(this) ? DELTA_XLARGE : DELTA;
        delta = Utility.dpsToPixels(this, (int) delta);

//        mTvClockTime.setShadowLayer(delta, delta, delta, getResources().getColor(R.color.text_shadow_color_clock_time));

        prvDisplayClock1(mTvClockDate, mTvClockTime, tvClockDate1);
    }

    private void prvProcessViewTvHello() {
        LoginInfo info = getPrefLoginInfo(this);

        if (info != null) {
            String text = info.getEmpName() + Constant.SPACE1;
            ((TextView) findViewById(R.id.tvHello)).setText(text);
        }
    }

    private void prvProcessViewTvRecentClockInfo() {
        LoginInfo info = getPrefLoginInfo(this);

        if (info != null) {
            CheckRec checkRec = DBManager.getInstance(this).getRecentCheckRec(info.getEmpNo());

            if (checkRec != null) {
//                String text = getString(R.string.recent_clock_info);
                Log.d("showExWorkIngTime", checkRec.getmAdjustDate());
                String text = "";
                text += checkRec.getCheckDate(DateUtil.FORMAT_YYYY_MM_DD_SLASH_HH_MM_24) + Constant.SPACE1;
                ((TextView) findViewById(R.id.tvRecentClockInfo)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tvRecentClockInfo1)).setVisibility(View.VISIBLE);
                if (checkRec.getCheckType().equals(DBManager.CHECK_TYPE_IN)) {
                    ((TextView) findViewById(R.id.tvRecentClockInfo)).setText(text);

                    ((TextView) findViewById(R.id.tvRecentClockInfo1)).setText("上班");
                } else {
                    ((TextView) findViewById(R.id.tvRecentClockInfo)).setText(text);

                    ((TextView) findViewById(R.id.tvRecentClockInfo1)).setText("下班");
                }
//                ((TextView) findViewById(R.id.tvRecentClockInfo)).setText(text);
            } else {
                ((TextView) findViewById(R.id.tvRecentClockInfo)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.tvRecentClockInfo1)).setVisibility(View.GONE);

            }
        }
    }

    private void prvProcessViewTvVersionInfo() {
        ((TextView) findViewById(R.id.tvVersionInfo)).setText(getString(R.string.version_info, Utility.getVersionName(this)));
    }

    private boolean prvSaveCheckRec(ClockPara para, int seriallyCheckDays) {
        CheckRec checkRec = new CheckRec();

        int days;

        if (seriallyCheckDays == ApplicationEx.DAYS_7)
            days = seriallyCheckDays;
        else
            days = 0;

        checkRec.setWithClockPara(para, DBManager.NO_1, days);

        StringBuffer errMsg = new StringBuffer();

        if (!DBManager.getInstance(this).saveCheckRec(checkRec, errMsg)) {
            Utility.showToast(this, errMsg.toString());
            Utility.logE("prvParseCheck(): " + errMsg.toString());

            return false;
        }

        return true;
    }

    private void prvShowClockAlertDialogWithAddr(String title, String msg, DialogInterface.OnClickListener posBtnListener, int editTextId) {
        final int MARGIN_10 = Utility.dpsToPixels(this, 10);
        final int MARGIN_20 = Utility.dpsToPixels(this, 20);

        final int TEXT_SIZE = 18;

        LinearLayout llRootView = new LinearLayout(this);
        llRootView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams llPara;

        llPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llPara.setMargins(MARGIN_20, MARGIN_10, MARGIN_20, 0);

        TextView tv = new TextView(this);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        tv.setText(msg);

        llRootView.addView(tv, llPara);

        EditText et = new EditText(this);
        et.setId(editTextId);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        et.setHint(R.string.input_address_here);
        et.setFocusable(true);

        llPara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llPara.setMargins(MARGIN_20, MARGIN_10, MARGIN_20, MARGIN_10);

        llRootView.addView(et, llPara);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setView(llRootView)
                .setPositiveButton(getString(android.R.string.yes), posBtnListener)
                .setNegativeButton(getString(android.R.string.no), null)
                .show();
    }

    private void prvShowClockProgressDialog() {
        if (mClockProg == null)
            mClockProg = Utility.showSimpleProgressDialog(this, true, getString(R.string.please_wait));
    }

    private void prvStartCheckRecListActivity(boolean needLogin, CheckRecKey checkRecKey) {
        LoginInfo info = getPrefLoginInfo(this);
        getDBtestList(info.getEmpNo());
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        MainActivity.this.startActivity(intent);
        finish();
        if (info == null)
            return;
//todo::舊版打卡過後會跳進去看打卡記錄。
//        Intent intent = new Intent(this, CheckRecListActivity.class);
//
//        intent.putExtra(CheckRecListActivity.EXTRA_EMP_ID, info.getEmpNo());
//
//        if (checkRecKey != null)
//            intent.putExtra(CheckRecListActivity.EXTRA_CHECK_REC_KEY, checkRecKey);
//
//        intent.putExtra(CheckRecListActivity.EXTRA_NEED_LOGIN, needLogin);
//
//        this.startActivityForResult(intent, REQUEST_CODE_CHECK_REC_LIST);
    }

    private void prvStartClockFailActivity(CheckRecKey checkRecKey) {
        LoginInfo info = getPrefLoginInfo(MainActivity.this);

        if (info == null)
            return;
        Log.d("MainActivity", "checkRecKey:" + checkRecKey.getEmpId());
        Log.d("MainActivity", "checkRecKey:" + checkRecKey.getCheckDate());
        Log.d("MainActivity", "checkRecKey:" + checkRecKey.getCheckType());
        StringBuffer errMsg = new StringBuffer();
        final CheckRec checkRec = DBManager.getInstance(MainActivity.this).getCheckRec(checkRecKey.getEmpId(), checkRecKey.getCheckDate(), checkRecKey.getCheckType());
        if (checkRec == null) {
            Utility.logE("ivOK.onClick(): checkRec == null");
            return;
        }
        if (!DBManager.getInstance(MainActivity.this).saveCheckRec(checkRec, errMsg)) {
            Utility.showToast(MainActivity.this, errMsg.toString());
            Utility.logE("ivOK.onClick(): " + errMsg.toString());
        }
        //todo:測試離現打卡成功。
    }

    private void prvStartLoginActivity(int reqCode) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, reqCode);
    }

    private void prvTimerCreate() {
        if (mTimer == null)
            mTimer = new PrvTimer();
    }

    private void prvTimerDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void prvTimerScheduleClock() {
        if (mTimer != null)
            mTimer.scheduleClock();
    }

    /*
     * Private Methods End
     */
    private void prvChangeAbnormal() {
        //todo : nfc_Botton 員工查詢功能拿掉!!(2019/02/20)
//        nfc_Botton.setVisibility(View.GONE);
        nfc_Botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showToast(MainActivity.this, "觸發員工異常按鈕");
                mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
                if (mNfcAdapter == null) {
                    Toast.makeText(MainActivity.this, "本手機未支援NFC功能!", Toast.LENGTH_LONG).show();
                }
                if (mNfcAdapter == null||!mNfcAdapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "請開啟NFC功能!", Toast.LENGTH_LONG).show();
                } else {
                    Dialog bottomDialog = new Dialog(MainActivity.this, R.style.SheetDialog);
                    View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_content_circle, null);
                    bottomDialog.setContentView(contentView);
                    bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
                    bottomDialog.show();

                }
            }
        });
        supervisorBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utility.showToast(MainActivity.this, "觸發部門異常按鈕");
                Intent intent = new Intent(MainActivity.this, SuperiorActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getSchedule() {
        LoginInfo info = getPrefLoginInfo(this);
//        Log.d("MainActivity","testGetEMP_name :"+ info.getEmpNo());
        if (info == null)
            return;
        String longinGetEmpNo = info.getEmpNo();
        Log.d("首頁執行確認_getSchedule", "取得班表:執行");
        scheduleAsyncTask1 = new ScheduleAsyncTask1(MainActivity.this, longinGetEmpNo, new ScheduleAsyncTask1.GetSchedule() {
            @Override
            public void setSchedule(String workIn, String workOut) {
                Log.d("首頁執行確認_getSchedule", "workIn:" + workIn);
                Log.d("首頁執行確認_getSchedule", "workOut:" + workOut);
                today_schedule_from_api.setTextColor(getResources().getColor(R.color.Main_work_time_text));
                if (workIn.equals("99:00") && workOut.equals("99:00")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ACache.get(MainActivity.this).getAsString("getApiWorkIn") != null &&
                                    ACache.get(MainActivity.this).getAsString("getApiWorkOut") != null) {
                                String workIn1 = ACache.get(MainActivity.this).getAsString("getApiWorkIn");
                                String workOut1 = ACache.get(MainActivity.this).getAsString("getApiWorkOut");
                                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat output = new SimpleDateFormat("HH:mm");
                                try {
                                    Date upWorkDate = input.parse(workIn1);
                                    Date offWorkDate = input.parse(workOut1);
                                    String upWD = output.format(upWorkDate);
                                    String offWD = output.format(offWorkDate);
                                    today_schedule_from_api.setText("無資料");
                                    getNowDateIsTrue = false;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                today_schedule_from_api.setText("無資料");
                                getNowDateIsTrue = false;
                            }
                        }
                    });
                } else {
                    today_schedule_from_api.setText(workIn + " - " + workOut);
                    getNowDateIsTrue = true;
                    ACache.get(MainActivity.this).put("getWorkIn", workIn);
                    ACache.get(MainActivity.this).put("getWorkOut", workOut);
                }

            }
        }, new ScheduleAsyncTask1.GetAnimation() {
            @Override
            public void setAnimation(Boolean s) {
                Log.d("setAnimation", String.valueOf(s));
                //s = true 閃下班
                //s = false 閃上班
                ACache.get(MainActivity.this).put("MainAnimation", s);
                if (!s) {
                    ((RelativeLayout) findViewById(R.id.acg_workin_bg)).setBackgroundResource(R.drawable.splse);
                    AnimationDrawable animationDrawable;
                    animationDrawable = (AnimationDrawable) ((RelativeLayout) findViewById(R.id.acg_workin_bg)).getBackground();
                    animationDrawable.start();

                } else {
                    ((RelativeLayout) findViewById(R.id.acg_workout_bg)).setBackgroundResource(R.drawable.splse);
                    AnimationDrawable animationDrawable;
                    animationDrawable = (AnimationDrawable) ((RelativeLayout) findViewById(R.id.acg_workout_bg)).getBackground();
                    animationDrawable.start();
                }
            }

        }, new ScheduleAsyncTask1.GetSeverTime() {
            @Override
            public void setSeverTime(String severTime) {
                if (ACache.get(MainActivity.this).getAsString("sever_Time") == null || ACache.get(MainActivity.this).getAsString("sever_Time").equals("")) {
                    //st+upt0 = 開啟後第一次進APP計算。
                    ACache.get(MainActivity.this).put("sever_Time", severTime);
                    long uptime_mobileOpen = System.currentTimeMillis() - SystemClock.elapsedRealtime();
                    long uptime_UPT0 = SystemClock.elapsedRealtime();
                    ACache.get(MainActivity.this).put("uptime_UPT0", uptime_UPT0);
                    ACache.get(MainActivity.this).put("uptime_mobileOpen", uptime_mobileOpen);
                }

//                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                try {
//                    Date inputSeverTime = input.parse(severTime);
//                    String outPutSeverTime = output.format(inputSeverTime);
//                    String outPutClineTime = output.format(new Date());
//                    //todo:判斷手機時間與伺服器時間。
////                    showOnlineDialog(outPutSeverTime, outPutClineTime);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                getMainUI_Time();
            }
        });
        scheduleAsyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d("首頁執行確認_getSchedule", "取得是否為主管:執行");
        //todo:主管BUTTON關閉。
//        getSuperviseList = new GetSuperviseList(MainActivity.this, longinGetEmpNo, new GetSuperviseList.GetSuperBoss() {
//            @Override
//            public void setSuperBoss(Boolean superBoss) {
//                Log.d("首頁執行確認_getSchedule", "判斷是否是主管superBoss:" + superBoss);
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
//                Log.d("首頁執行確認_getSchedule", "有多少筆異常superEmpCounts:" + superEmpCounts);
//                if (Integer.valueOf(superEmpCounts) >= 100) {
//                    setEmpCounts.setText("99+");
//                } else {
//                    if (Integer.valueOf(superEmpCounts) == 0) {
//                        setEmpCounts.setVisibility(View.GONE);
//                    }
//                    setEmpCounts.setText(superEmpCounts);
//                }
//            }
//        });
//        getSuperviseList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private static String getNowDate() {
        long time = System.currentTimeMillis();
        Date nowDate = new Date(time);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay;
        nowDay = format.format(nowDate);
        return nowDay;
    }

    private static String getNowDate1() {
        long time = System.currentTimeMillis();
        Date nowDate = new Date(time);
        final SimpleDateFormat format = new SimpleDateFormat("EE");
        String nowDay;
        nowDay = format.format(nowDate);
        return nowDay;
    }

    private void pvClockInTouch() {
        Log.d("MainActivity", "pvClockInTouch::::" + forDialogUseTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date checkDate = null;
        try {
            checkDate = sdf.parse(forDialogUseTime);
            if (forDialogUseTime == null && forDialogUseTime.equals("")) {
                checkDate = new Date();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Date checkDate = new Date();

        final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_IN);

        if (para == null) {
            Utility.logE("ivClockIn.onClick(): para == null");

            return;
        }

        if (DBManager.getInstance(MainActivity.this).getCheckRec(para.getEmpId(), CheckRec.formatCheckDate(para.getCheckDateYYYYMMDDHHMMSS()), para.getCheckType()) != null) {
            Utility.showToast(MainActivity.this, R.string.clock_time_duplicated);

            return;
        }

        if (para.getLoc() == null)
            prvDoClockIn(checkDate, para);
        else if (Utility.checkNetworkStatus(MainActivity.this)) {
            AsyncGetAddressStatic.AsyncGetAddressPara getAddrPara = new AsyncGetAddressStatic.AsyncGetAddressPara(para.getLoc().getLatitude(), para.getLoc().getLongitude());

            new PrvAsyncGetAddressClock(MainActivity.this, checkDate, para).execute(getAddrPara);
        } else
            prvDoClockIn(checkDate, para);
    }

    private void pvClockOutTouch() {
        Log.d("MainActivity", "pvClockOutTouch::::" + forDialogUseTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date checkDate = null;
        try {
            checkDate = sdf.parse(forDialogUseTime);
            if (forDialogUseTime == null && forDialogUseTime.equals("")) {
                checkDate = new Date();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("MainActivity", "pvClockOutTouch11:" + checkDate);
        final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_OUT);

        if (para == null) {
            Utility.logE("ivClockOut.onClick(): para == null");
            return;
        }

        if (DBManager.getInstance(MainActivity.this).getCheckRec(para.getEmpId(), CheckRec.formatCheckDate(para.getCheckDateYYYYMMDDHHMMSS()), para.getCheckType()) != null) {
            Utility.showToast(MainActivity.this, R.string.clock_time_duplicated);
            return;
        }

        if (para.getLoc() == null)
            prvDoClockOut(checkDate, para);
        else if (Utility.checkNetworkStatus(MainActivity.this)) {
            AsyncGetAddressStatic.AsyncGetAddressPara getAddrPara = new AsyncGetAddressStatic.AsyncGetAddressPara(para.getLoc().getLatitude(), para.getLoc().getLongitude());

            new PrvAsyncGetAddressClock(MainActivity.this, checkDate, para).execute(getAddrPara);
        } else
            prvDoClockOut(checkDate, para);
    }

    private void getWorkRange() {
//        if (wifi.isWifiEnabled()) {
//            sCanWiFiResultsList();
//        } else {
//            wifiTest.setText("目前無法驗證你的位置-點擊可開啟WIFI");
//        }
        if (ACache.get(this).getAsString("getApiWorkIn") != null &&
                ACache.get(this).getAsString("getApiWorkOut") != null) {//<--todo:判斷上下班時間有沒有NULL
            String workIn = ACache.get(this).getAsString("getApiWorkIn");
            String workOut = ACache.get(this).getAsString("getApiWorkOut");
            Log.d("首頁執行確認_getSchedule", "workInString:" + workIn);
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date workInDate = input.parse(workIn);
                Date workOutDate = input.parse(workOut);

                workInRange1 = DateUtil.addDate(workInDate, Calendar.HOUR, 1);
                workInRange2 = DateUtil.addDate(workInDate, Calendar.MINUTE, -30);//**todo:上班時間前半小時

                workOutRange1 = DateUtil.addDate(workOutDate, Calendar.HOUR, 1);
                workOutRange3 = DateUtil.addDate(workOutDate, Calendar.MINUTE, 00);
                workOutRange2 = DateUtil.addDate(workOutDate, Calendar.MINUTE, 30);//**todo:下班時間後半小時
//                 workOutRangeOverTime13 = DateUtil.addDate(workOutDate, Calendar.HOUR, +13);//**todo:下班時間超過13小時
                Log.d("首頁執行確認_getSchedule", "上班時間前半小時:" + workInRange2);
                Log.d("首頁執行確認_getSchedule", "下班時間後半小時:" + workOutRange2);
//                Log.d("MainActivity", "下班時間後13小時:" +workOutRangeOverTime13);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (Utility.checkNetworkStatus(this)) {
                getSchedule();
            }
        }
    }

    private Date pvtOnWorkDialog(final Date checkDate) {
        ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
            @Override
            public void setChangeTest(String s) {
//                            searchErrorText.setText(s);
                ACache.get(MainActivity.this).put("adjustComment", s);
                ACache.get(MainActivity.this).put("Wi_FiSSID", wifiTest.getText().toString());
                Log.d("首頁執行確認_getSchedule", "wifiTest.getText():" + wifiTest.getText());
                ACache.get(MainActivity.this).put("unusualStatus", "1");
                Log.d("首頁執行確認_getSchedule", "已取得打卡異常原因 :" + s);
                Dialog d = new ClockInErrorDialog(MainActivity.this, 0, s, new ClockInErrorDialog.PriorityListener1() {
                    @Override
                    public Date setChangeTime(Date s) {
                        Log.d("首頁執行確認_getSchedule", "已取得修改後的上班時間 :" + s);
                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String changeTime = input.format(s);
                        Log.d("首頁執行確認_getSchedule", "adjustDate:" + changeTime);
                        ACache.get(MainActivity.this).put("adjustDate", changeTime);
//                        final ClockPara para = prvPrepareClockPara(s, DBManager.CHECK_TYPE_IN);
//                        prvDoClockInToChangeTime(s, para);
                        final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_IN);
                        prvDoClockInToChangeTime(s, para);
                        return s;
                    }
                });
                d.show();
            }
        });
        d.show();
        return null;
    }

    private Date pvtOffWorkDialog(final Date checkDate) {
        ScheduleErrorDialog d = new ScheduleErrorDialog(this, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
            @Override
            public void setChangeTest(String s) {
                ACache.get(MainActivity.this).put("adjustComment", s);
                ACache.get(MainActivity.this).put("Wi_FiSSID", wifiTest.getText().toString());
                ACache.get(MainActivity.this).put("unusualStatus", "1");
                Log.d("MainActivity", "已取得打卡異常原因 :" + s);
                Log.d("MainActivity", "wifiTest.getText():" + wifiTest.getText());
                Dialog d = new ClockOutErrorDialog(MainActivity.this, 0, s, new ClockOutErrorDialog.PriorityListener2() {
                    @Override
                    public Date setChangeTime(Date s) {
                        Log.d("MainActivity", "已取得修改後的下班時間 :" + s);
                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String changeTime = input.format(s);
                        Log.d("MainActivity", changeTime);
                        ACache.get(MainActivity.this).put("adjustDate", changeTime);
                        final ClockPara para = prvPrepareClockPara(checkDate, DBManager.CHECK_TYPE_OUT);
                        prvDoClockOutToChangeTime(s, para);
                        return s;
                    }
                });
                d.show();
            }
        });
        d.show();
        return null;
    }

    private void prvDoClockInToChangeTime(final Date checkDate, final ClockPara para) {
        final int editTextId = ViewEx.generateViewId();
        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_IN, para);

        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = ((AlertDialog) dialog).findViewById(editTextId);

                if (editText != null)
                    para.setRemarkAddr(editText.getText().toString());

                if (prvSaveCheckRec(para, seriallyCheckDays)) {
                    prvHttpGetClockIn(para);
                    prvProcessViewTvRecentClockInfo();

                    ApplicationEx.cancelNotifyClockOutService9(MainActivity.this);
                    ApplicationEx.cancelNotifyClockOutService13(MainActivity.this);

                    ApplicationEx.registerNotifyClockOutService9(MainActivity.this);
                    ApplicationEx.registerNotifyClockOutService13(MainActivity.this);
                }

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_IN);

//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
            }
        };

        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
            }
        };


        String title = getString(R.string.confirm_clock_in);
        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, null);

        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
            Utility.showSimpleAlertDialogNew(this, title, msg, posListener, negListener);
        else
            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
    }

    private void prvDoClockOutToChangeTime(final Date checkDate, final ClockPara para) {
        final int editTextId = ViewEx.generateViewId();
        final int seriallyCheckDays = prvGetSeriallyCheckDays(DBManager.CHECK_TYPE_OUT, para);

        final DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText et = ((AlertDialog) dialog).findViewById(editTextId);

                if (et != null)
                    para.setRemarkAddr(et.getText().toString());

                if (prvSaveCheckRec(para, seriallyCheckDays))
//				if (prvSaveCheckRec(para, DBManager.NO_1))
                {
                    prvHttpGetClockOut(para);
                    prvProcessViewTvRecentClockInfo();

                    ApplicationEx.cancelNotifyClockOutService9(MainActivity.this);
                    ApplicationEx.cancelNotifyClockOutService13(MainActivity.this);
                }

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_IN_OUT, GoogleAnalyticsManager.LABEL_CLOCK_OUT);

//                String label = GoogleAnalyticsManager.getLabelLocation(para.getLocationStatus());

//                GoogleAnalyticsManager.sendGoogleAnalyticsEvent(MainActivity.this, GoogleAnalyticsManager.SCREEN_NAME_HOMEPAGE, GoogleAnalyticsManager.CATEGORY_CLOCK, GoogleAnalyticsManager.ACTION_CLOCK_LOC, label);
            }
        };

        final DialogInterface.OnClickListener negListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                prvDismissClockProgressDialog();
            }
        };
        String title = getString(R.string.confirm_clock_out);
        String msg = prvComposeClockDialogMsg(checkDate, para, seriallyCheckDays, DBManager.getInstance(this).getCurrentCheckInRec(para.getEmpId()));

        if (ApplicationEx.IMPLEMENT_HIDE_CHECK_ADDR)
            Utility.showSimpleAlertDialog(this, title, msg, posListener, negListener);
        else
            prvShowClockAlertDialogWithAddr(title, msg, posListener, editTextId);
    }

    private void getDBtestList(String empNo) {
//        LoginInfo info = new LoginInfo();
        CheckRecList list = DBManager.getInstance(this).getCheckRecList(empNo);
        for (CheckRec checkRec : list) {
            Log.d("MainActivity", "checkRec:" + checkRec.toString());
        }


    }

    //    @Override
//    public Resources getResources() {
//        //todo 鎖首頁字型大小。
//        Resources resources = super.getResources();
//        Configuration configuration = new Configuration();
//        configuration.setToDefaults();
//        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//        return resources;
//    }
    private void showOnlineDialog(final String outPutSeverTime, final String outPutClineTime) {
        Log.d("showOnlineDialog", "getSeverTime:" + outPutSeverTime);
        Log.d("showOnlineDialog", "getClineTime:" + outPutClineTime);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!outPutSeverTime.equals(outPutClineTime)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("聯合e打卡發現您的手機日期時間與伺服器不一致，打卡時間認證將以Server時間為準");
                    dialog.setCancelable(false);
                    dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    private void getMainUI_Time() {

        mTvClockDate = findViewById(R.id.tvClockDate);
        mTvClockTime = findViewById(R.id.tvClockTime);
        tvClockDate1 = findViewById(R.id.tvClockDate1);

        long upt0 = (long) ACache.get(MainActivity.this).getAsObject("uptime_UPT0");
        long upt1 = SystemClock.elapsedRealtime();
        String svT = ACache.get(MainActivity.this).getAsString("sever_Time");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat inputDay = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat inputTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat inputEE = new SimpleDateFormat("EE");
        SimpleDateFormat inputWorkTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            Date d = input.parse(svT);
            long getST_milliseconds = d.getTime();
            long ans = upt1 - upt0 + getST_milliseconds;
            Log.d("getMainUI_Time", "upt1:" + String.valueOf(upt1));
            Log.d("getMainUI_Time", "upt0:" + String.valueOf(upt0));
            Log.d("getMainUI_Time", "severTime_ST:" + String.valueOf(getST_milliseconds));
            forDialogUseTime = input.format(new Date(ans));
            final String ansPase = inputWorkTime.format(new Date(ans));
            final String ansPaseToDay = inputDay.format(new Date(ans));
            final String ansPaseToTime = inputTime.format(new Date(ans));
            final String ansPaseToEE = inputEE.format(new Date(ans));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prvDisplayClock3(mTvClockDate, mTvClockTime, tvClockDate1, ansPaseToDay, ansPaseToTime, ansPaseToEE);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    prvProcessViewTvClock();
                }
            });
        }
    }

    private void processIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        byte[] extraId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        if (extraId != null) {
            Log.d("md5_scan_Input", encodeHexString(extraId));
            // Id

            md5_ED(encodeHexString(extraId));
        }
    }

    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte aByteArray : byteArray) {
            hexStringBuffer.append(byteToHex(aByteArray));
        }
        return hexStringBuffer.toString();
    }

    private Boolean getWifiListInWorking(WifiManager wifi) {
        getWiFiSSIDList = new GetWiFiSSIDList(MainActivity.this, wifi, new GetWiFiSSIDList.GetBSSID() {
            @Override
            public Boolean scheduleList(Boolean scheduleList,String getWifiID) {
                isInUdnWIFI = scheduleList;
                Log.d("getWifiListInWorking", "isInUdnWIFI:" + scheduleList);
                Log.d("getWifiListInWorking", "getWifiID:" + getWifiID);
                if (scheduleList){
                    ACache.get(MainActivity.this).put("SSIDCheckin", "1");
                }
//                wifiTest.setText(getWifiID);
                return scheduleList;
            }
        });
        getWiFiSSIDList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return isInUdnWIFI;
    }

    private void getNfcListInWorking(String hexString) {
        if (Utility.checkNetworkStatus(MainActivity.this)) {
            GetNFCList getNFCList = new GetNFCList(MainActivity.this, hexString, new GetNFCList.GetTAG() {
                @Override
                public void isInUDN_TAG(Boolean isInUDN_TAG) {
                    Log.d("md5_scan_sCanAns", "isInUDN_TAG:" + isInUDN_TAG);
                    if (isInUDN_TAG) {
                        isTagNfc = true;
                        openNFCtime = System.currentTimeMillis();
                        sWitch_Wifi_Nfc = "2";
                        showToastMD("NFC驗證成功\n 請繼續打卡");
                    } else {
                        isTagNfc = false;
                    }
                }
            });
            getNFCList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            isTagNfc = false;
            showDialogMD("無網路連線", "您目前未連線，請選擇所在位置的公司WI-FI連線，或手動開啟行動網路再打卡，提醒您，以公司WI-" +
                    "FI或NFC打卡驗證的資料，才紀錄為出勤值間");
        }
    }

    private void md5_ED(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            Log.d("md5_scan_sCanOutPut", hexString.toString().toUpperCase());
            getNfcListInWorking(hexString.toString().toUpperCase());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void showToastMD(String test) {
        View view = getLayoutInflater().inflate(R.layout.toast_layout, null);
        TextView textView = view.findViewById(R.id.setTextId);
        textView.setText(test);
//        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.icon_confirm_white);
        Toast toast = new Toast(MainActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void showDialogMD(String title, String test) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(test);
        dialog.setCancelable(false);
//        if (title.equals("無網路連線")) {
//            dialog.setPositiveButton("開啟 Wi-Fi", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    wifi.setWifiEnabled(true);
//                }
//            });
//            dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            dialog.show();
//        } else {
        dialog.setNegativeButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
//        }
    }

    private void getThisMobileHaveNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        if (mNfcAdapter == null) {
            Toast.makeText(MainActivity.this, "本手機未支援NFC功能!", Toast.LENGTH_LONG).show();
            thisMobileHaveNfc = false;
        } else {
            thisMobileHaveNfc = true;
        }
        if (mNfcAdapter == null||!mNfcAdapter.isEnabled()) {
            Toast.makeText(MainActivity.this, "請開啟NFC功能!", Toast.LENGTH_LONG).show();
        }
    }
    private void forPublic60CsLogOut(){
        long start = System.currentTimeMillis();
        final long end = start + 60 * 1000;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Log.e("forPublic60CsLogOut","此处实现倒计时，指定时长内，每隔1秒执行一次该任务");
            }
        }, 0, 1000);
        timer.schedule(new TimerTask() {
            public void run() {
                timer.cancel();
                logout(MainActivity.this);
                prvStartLoginActivity(REQUEST_CODE_LOGIN_AFTER_LOGOUT);

            }

        }, new Date(end));
    }
//todo::2019/05/20 出產於2.0.0出版測試後正常的mainActivity，此為新版本時期之開始。舊版本及2.0.0最終版本存於MainActivity_Old2019/05/20前更改。
}

package com.udn.hr.clock.test;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.sqlite.CheckRec;
import com.udn.hr.clock.test.sqlite.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Tools.ACache;
import lambda.RequestClass;
import lambda.getSyncResult;

import static com.udn.hr.clock.test.MainActivity.getPrefLoginInfo;

public class PutLambdaRecive_Task extends AsyncTask<String, Void,List<ScheduleBean>> {
    private Boolean isOnline;
    private Context context;
    private ClockPara clockPara;
    private String adjustComment;
    private String Wi_FiSSID;
    private String SSIDCheckin;
    private String unusualStatus;
    private String adjustDate;
    private String remark;
    List<ScheduleBean> scheduleList = new ArrayList<>();
    private ScheduleBean scheduleBean = new ScheduleBean();
    public interface GetSeverResult {

        void setSeverResult(String result);
    }
    private GetSeverResult getSeverResult;

    public PutLambdaRecive_Task(Context context, ClockPara clockPara,Boolean isOnline,GetSeverResult getSeverResult) {
        this.context = context;
        this.clockPara = clockPara;
        this.getSeverResult = getSeverResult;
        this.isOnline = isOnline;
    }

    @Override
    protected List<ScheduleBean> doInBackground(String... strings) {
        LoginInfo info = getPrefLoginInfo(context);
        if (info != null) {
            String AWS_Event_ID = "ap-northeast-1:915958ed-be86-4d4a-b9d3-3520b49470ac";

            String SN = clockPara.getSN();
            String device_type = "92";
            String empolyee_id = clockPara.getEmpId();
            String empolyee_name = info.getEmpName();
            String check_date = clockPara.getCheckDateYYYYMMDDHHMMSS();
            String check_type = clockPara.getCheckType();
            String device_id = clockPara.getDeviceId();
            String GPS_info = clockPara.getGPSInfo();
            String check_address = clockPara.getCheckAddr();
            unusualStatus =  ACache.get(context).getAsString("unusualStatus");
            adjustDate = clockPara.getmAdjustDate();
            adjustComment = clockPara.getmAdjustComment();
            Wi_FiSSID = clockPara.getmWi_FiSSID();
            SSIDCheckin = ACache.get(context).getAsString("SSIDCheckin");
            String adjustReceiveDate = clockPara.getmAdjustReceiveDate();
            if (isOnline){
//                remark = clockPara.getRemark();
                remark = Utility.getVersionName(context);
            }else{
                remark = "離線打卡補傳";
            }
            getSyncResult mResult = new getSyncResult(context, AWS_Event_ID);
            RequestClass mRequestClass = new RequestClass(SN, device_type, empolyee_id, empolyee_name, check_date, check_type, device_id, GPS_info,
                    check_address, unusualStatus, adjustDate, adjustComment, adjustReceiveDate, Wi_FiSSID, SSIDCheckin, remark);
            //Lambda 這邊要用JsonObject接
            String type = "3";
            JsonObject mResponse = mResult.getSyncResult(type, mRequestClass);

//            Log.d("PutLambda線上", "SN:" + SN);
//            Log.d("PutLambda線上", "device_type:" + device_type);
//            Log.d("PutLambda線上", "empolyee_id:" + empolyee_id);
//            Log.d("PutLambda線上", "empolyee_name:" + empolyee_name);
//            Log.d("PutLambda線上", "check_date:" + check_date);
//            Log.d("PutLambda線上", "check_type:" + check_type);
//            Log.d("PutLambda線上", "device_id:" + device_id);
//            Log.d("PutLambda線上", "GPS_info:" + GPS_info);
//            Log.d("PutLambda線上", "check_address:" + check_address);
            Log.d("PutLambda線上", "unusualStatus:" + unusualStatus);
//            Log.d("PutLambda線上", "adjustDate:" + adjustDate);
//            Log.d("PutLambda線上", "adjustComment:" + adjustComment);
//            Log.d("PutLambda線上", "adjustReceiveDate:" + adjustReceiveDate);
//            Log.d("PutLambda線上", "Wi_FiSSID:" + Wi_FiSSID);
            Log.d("PutLambda線上", "SSIDCheckin:" + SSIDCheckin);
            Log.d("PutLambda線上", "註記:" + remark);
            Log.d("PutLambda線上", "mResponse:" + String.valueOf(mResponse));
            if (mRequestClass == null){
                if (clockPara == null) {
                    return null;
                }
                final CheckRec checkRec;
                checkRec = DBManager.getInstance(context).getCheckRec(clockPara.getEmpId(), CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()), clockPara.getCheckType());
                if (checkRec == null) {
                    Utility.logE("prvParseClockResult(): checkRec == null");
                    return null;
                }
                checkRec.setIsSend(DBManager.NO_1);
                DBManager.getInstance(context).saveCheckRec(checkRec, null);
                return null;
            }else
            return getJsonData(String.valueOf(mResponse));
        }
        return null;
    }
    private List<ScheduleBean> getJsonData(String s) {
        scheduleList = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(s);
            scheduleBean.getReciveResult = jsonObject.getString("result");
            scheduleBean.getReciveDescript = jsonObject.getString("descript");
            scheduleList.add(scheduleBean);

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e("TAG", e.toString());
        }
        return scheduleList;
    }
    @Override
    protected void onPostExecute(List<ScheduleBean> scheduleList) {
        super.onPostExecute(scheduleList);
        if (scheduleList!=null)
        for (int i = 0; i < scheduleList.size(); i++) {
            Log.d("getReciveResult", scheduleList.get(i).getReciveResult);
            if (scheduleList.get(i).getReciveResult.equals("success")){
                getSeverResult.setSeverResult("打卡成功");

                if (clockPara == null) {
                    return;
                }
                final CheckRec checkRec;
                checkRec = DBManager.getInstance(context).getCheckRec(clockPara.getEmpId(), CheckRec.formatCheckDate(clockPara.getCheckDateYYYYMMDDHHMMSS()), clockPara.getCheckType());
                if (checkRec == null) {
                    Utility.logE("prvParseClockResult(): checkRec == null");
                    return;
                }
                checkRec.setIsSend(DBManager.YES_1);
                DBManager.getInstance(context).saveCheckRec(checkRec, null);
                Log.d("PutLambdaRecive_Task", "打卡回傳後資料庫改寫完畢");

            }else {
                getSeverResult.setSeverResult("打卡失敗");
            }
        }
    }
}

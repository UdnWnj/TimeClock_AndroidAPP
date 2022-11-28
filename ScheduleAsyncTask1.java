package com.udn.hr.clock.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.udn.hr.clock.test.employee.EmployeeRecycleViewAdapter;
import com.udn.hr.clock.test.mylib.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Tools.ACache;
import androidx.recyclerview.widget.RecyclerView;
import lambda.RequestClass;
import lambda.getSyncResult;

public class ScheduleAsyncTask1 extends AsyncTask<String, Void, List<ScheduleBean>> {
    private String channel_url;
    private EmployeeRecycleViewAdapter myAdapter;
    private Boolean getNowDateIsTrue;
    List<ScheduleBean> scheduleList = new ArrayList<>();
    private ScheduleBean scheduleBean = new ScheduleBean();

    private JSONObject employeeDataObject;
    private JSONArray shiftDataArray;

    private JSONArray tagjsonArray;
    private Activity mActivity;
    private RecyclerView recyclerView;
    private String classId;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private Boolean isChecked;

    public interface GetSeverTime {
        void setSeverTime(String severTime);
    }

    public interface GetSchedule {

        void setSchedule(String workIn, String workOut);
    }

    public interface GetAnimation {

        void setAnimation(Boolean s);
    }

    private GetSchedule getSchedule;
    private GetAnimation getAnimation;
    private GetSeverTime getSeverTime;
    private String getEmpNo;

    public ScheduleAsyncTask1(Activity mActivity, String longinGetEmpNo, final GetSchedule getSchedule, final GetAnimation getAnimation, final GetSeverTime getSeverTime) {
        this.mActivity = mActivity;
        this.getSchedule = getSchedule;
        this.getAnimation = getAnimation;
        this.getSeverTime = getSeverTime;
        this.getEmpNo = longinGetEmpNo;
    }

    @Override
    protected List<ScheduleBean> doInBackground(String... strings) {


        String AWS_Event_ID = "ap-northeast-1:915958ed-be86-4d4a-b9d3-3520b49470ac";

        String epID = getEmpNo;
        String dateST = "";
        String dateED = "";
        getSyncResult mResult = new getSyncResult(mActivity, AWS_Event_ID);
        RequestClass mRequestClass = new RequestClass(epID, dateST, dateED);
        //Lambda 這邊要用JsonObject接
        String type = "0";
        JsonObject mResponse = mResult.getSyncResult(type, mRequestClass);
        //        return getJsonData(channel_url);
//        return  AttendanceInfo.fromJson(String.valueOf(mResponse));
        Log.d("ScheduleAsyncTask1", String.valueOf(mResponse));
        if (mResponse == null) {
            getSchedule.setSchedule("99:00", "99:00");
            return null;
        }
        return getJsonData(String.valueOf(mResponse));
    }

    @Override
    protected void onPostExecute(List<ScheduleBean> scheduleList) {
        super.onPostExecute(scheduleList);
        //todo:抓取時間範圍，判斷是否顯示班表。
        if (scheduleList != null && scheduleList.size() != 0) {
            for (int i = 0; i < scheduleList.size(); i++) {
                if (scheduleList.get(i).date.equals(getNowDate())) {
                    String[] aaa = getOnWorDate(scheduleList.get(i).upWorkDate, scheduleList.get(i).offWorkDate);
                    String upWorkTime = aaa[0];
                    Log.d("666666:", upWorkTime);
                    String offWorkTime = aaa[1];
                    Log.d("66666:", offWorkTime);
                    if (!upWorkTime.equals("99:00") && !offWorkTime.equals("99:00")) {
                        Log.d("if:", upWorkTime);
                        Log.d("if:", offWorkTime);
                        getSchedule.setSchedule(upWorkTime, offWorkTime);
                    }
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat output = new SimpleDateFormat("HH:mm");
                    getNowDateIsTrue = true;
                    try {
                        Date upWorkDate = input.parse(scheduleList.get(i).upWorkDate);
                        Date offWorkDate = input.parse(scheduleList.get(i).offWorkDate);
//                        String upWD = output.format(upWorkDate);
//                        String offWD = output.format(offWorkDate);
//                        getSchedule.setSchedule(upWD, offWD);
                        //上班時間往前+6小時 ， 下班時間往後+6小時
                        Date date = new Date();
                        date.getTime();
                        Long upDate = Math.abs(date.getTime() - upWorkDate.getTime());
                        Long offDate = Math.abs(date.getTime() - offWorkDate.getTime());
                        if (upDate >= offDate) {
                            //下班
                            getAnimation.setAnimation(true);
                        } else {
                            //上班
                            getAnimation.setAnimation(false);
                        }
                        return;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else {
                    getNowDateIsTrue = false;
                }
            }
        } else {
            getNowDateIsTrue = false;
        }
//
//        if (scheduleList != null && scheduleList.size() != 0) {
//            for (int i = 0; i < scheduleList.size(); i++) {
//                if (scheduleList.get(i).date.equals(getNowDate())) {
////                    ACache.get(mActivity).put("getApiWorkIn", scheduleList.get(i).upWorkDate);
////                    ACache.get(mActivity).put("getApiWorkOut", scheduleList.get(i).offWorkDate);
//                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    SimpleDateFormat output = new SimpleDateFormat("HH:mm");
//                    getNowDateIsTrue = true;
//                    try {
//                        Date upWorkDate = input.parse(scheduleList.get(i).upWorkDate);
//                        Date offWorkDate = input.parse(scheduleList.get(i).offWorkDate);
////                        String upWD = output.format(upWorkDate);
////                        String offWD = output.format(offWorkDate);
////                        getSchedule.setSchedule(upWD, offWD);
//                        //上班時間往前+6小時 ， 下班時間往後+6小時
//                        Date date = new Date();
//                        date.getTime();
//                        Long upDate = Math.abs(date.getTime() - upWorkDate.getTime());
//                        Long offDate = Math.abs(date.getTime() - offWorkDate.getTime());
//                        if (upDate >= offDate) {
//                            //下班
//                            getAnimation.setAnimation(true);
//                        } else {
//                            //上班
//                            getAnimation.setAnimation(false);
//                        }
//                        return;
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    getNowDateIsTrue = false;
//                }
//            }
//        } else {
//            getNowDateIsTrue = false;
//        }

        Log.d("ScheduleAsyncTask1", "getNowDateIsTrue:" + getNowDateIsTrue);
        if (!getNowDateIsTrue) {
            getSchedule.setSchedule("99:00", "99:00");
        } else {
            getSchedule.setSchedule("99:00", "99:00");
        }
    }

    private List<ScheduleBean> getJsonData(String jsonString) {
        scheduleList = new ArrayList<>();
        //            String jsonString = readStream(new URL(url).openStream());
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            scheduleBean.result = jsonObject.getString("result");
            scheduleBean.descript = jsonObject.getString("descript");
            scheduleBean.statusCode = jsonObject.getString("statusCode");
            employeeDataObject = jsonObject.optJSONObject("employeeData");
            scheduleBean.employeeId = employeeDataObject.getString("employeeId");
            scheduleBean.updateTime = employeeDataObject.getString("updateTime");
            getSeverTime.setSeverTime(scheduleBean.updateTime);

            shiftDataArray = employeeDataObject.getJSONArray("shiftData");

            for (int i = 0; i < shiftDataArray.length(); i++) {
                JSONObject job = shiftDataArray.getJSONObject(i);
                scheduleBean = new ScheduleBean();
                scheduleBean.date = job.getString("date");
//                Log.d("ScheduleAsyncTask1", job.getString("date"));
                scheduleBean.isHoliday = job.getString("isHoliday");
                scheduleBean.upWorkDate = job.getString("upWorkDate");
                scheduleBean.offWorkDate = job.getString("offWorkDate");
                scheduleBean.dayOffType = job.getString("dayOffType");

                scheduleList.add(scheduleBean);
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e("TAG", e.toString());
        }
        return scheduleList;

    }

    private static String getNowDate() {
        long time = System.currentTimeMillis();
        Date nowDate = new Date(time);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay;
        nowDay = format.format(nowDate);
        return nowDay;
    }

    private String[] getOnWorDate(String OnWorkDate, String OffWorkDate) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("HH:mm");
        SimpleDateFormat forRange = new SimpleDateFormat("EE HH:mm");

        try {
            Date UpDate = DateUtil.addDate(input.parse(OnWorkDate), Calendar.HOUR, -6);
            Date OutDate = DateUtil.addDate(input.parse(OffWorkDate), Calendar.HOUR, 6);
            Long upDate = UpDate.getTime();
            Long outDate = OutDate.getTime();
            Date date = new Date();
            if (date.getTime() >= upDate && date.getTime() <= outDate) {
                String testUpDate = input.format(new Date(Long.parseLong(String.valueOf(upDate))));
                String testOutDate = input.format(new Date(Long.parseLong(String.valueOf(outDate))));
                Date showUpDate = DateUtil.addDate(input.parse(testUpDate), Calendar.HOUR, 6);
                Date showOffDate = DateUtil.addDate(input.parse(testOutDate), Calendar.HOUR, -6);
                String upWD = output.format(showUpDate);
                String offWD = output.format(showOffDate);
                String upDateRange = forRange.format(new Date(Long.parseLong(String.valueOf(upDate))));
                String offDateRange = forRange.format(new Date(Long.parseLong(String.valueOf(outDate))));
                ACache.get(mActivity).put("getApiWorkIn", input.format(showUpDate));
                ACache.get(mActivity).put("getApiWorkOut", input.format(showOffDate));
                Log.d("ScheduleAsyncTask1", "showTodayScheduleRange=" + upDateRange + "~" + offDateRange);
                Log.d("ScheduleAsyncTask1", "showTodaySchedule=" + upWD + "~" + offWD);
                return new String[]{upWD, offWD};
            } else {
                return new String[]{"99:00", "99:00"};
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new String[]{"99:00", "99:00"};
    }
}

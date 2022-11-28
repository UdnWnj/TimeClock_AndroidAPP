//package com.udn.hr.clock.test;
//
//import android.app.Activity;
//import android.os.AsyncTask;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//
//import com.udn.hr.clock.test.employee.EmployeeRecycleViewAdapter;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//public class ScheduleAsyncTask extends AsyncTask<String, Void, List<ScheduleBean>> {
//    private String channel_url;
//    private EmployeeRecycleViewAdapter myAdapter;
//    List<ScheduleBean> scheduleList = new ArrayList<>();
//    private ScheduleBean scheduleBean = new ScheduleBean();
//
//    private JSONObject employeeDataObject;
//    private JSONArray shiftDataArray;
//
//    private JSONArray tagjsonArray;
//    private Activity mActivity;
//    private RecyclerView recyclerView;
//    private String classId;
//    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//    private Boolean isChecked;
//
//    public ScheduleAsyncTask(String channel_url, EmployeeRecycleViewAdapter myAdapter, JSONArray tagjsonArray, Activity mActivity, RecyclerView recyclerView, String classId, Boolean isChecked) {
//        this.channel_url = channel_url;
//        this.myAdapter = myAdapter;
//        this.tagjsonArray = tagjsonArray;
//        this.mActivity = mActivity;
//        this.recyclerView = recyclerView;
//        this.classId = classId;
//        this.isChecked = isChecked;
//    }
//    public interface GetSchedule {
//
//        void setSchedule(String s);
//    }
//    private GetSchedule getSchedule;
//    public ScheduleAsyncTask(String channel_url, Activity mActivity,final GetSchedule getSchedule) {
//        this.channel_url = channel_url;
//        this.mActivity = mActivity;
//        this.getSchedule = getSchedule;
//    }
//
//    @Override
//    protected List<ScheduleBean> doInBackground(String... strings) {
//
//        return getJsonData(channel_url);
//    }
//
//    @Override
//    protected void onPostExecute(List<ScheduleBean> scheduleList) {
//        super.onPostExecute(scheduleList);
//        for (int i = 0; i<scheduleList.size();i++){
//            if(scheduleList.get(i).date.equals(getNowDate())){
//                Log.d("scheduleList","上班日期 :"+scheduleList.get(i).date);
//                Log.d("scheduleList","是否假日 :"+scheduleList.get(i).isHoliday);
//                Log.d("scheduleList","上班時間 :"+scheduleList.get(i).upWorkDate);
//                Log.d("scheduleList","下班時間 :"+scheduleList.get(i).offWorkDate);
//                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat output = new SimpleDateFormat("HH:mm");
//
//                try {
//                    Date upWorkDate =input.parse(scheduleList.get(i).upWorkDate);
//                    Date offWorkDate =input.parse(scheduleList.get(i).offWorkDate);
//
//                    String upWD = output.format(upWorkDate);
//                    String offWD = output.format(offWorkDate);
//                    getSchedule.setSchedule("今日班表 - "+upWD+" - "+offWD);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
////            getSchedule.setSchedule("等等");
//        }
//
//    }
//    private List<ScheduleBean> getJsonData(String url) {
//        scheduleList = new ArrayList<>();
//        try {
//            String jsonString = readStream(new URL(url).openStream());
//            JSONObject jsonObject;
//            try {
//                jsonObject = new JSONObject(jsonString);
//                employeeDataObject = jsonObject.optJSONObject("employeeData");
//                scheduleBean.employeeId = employeeDataObject.getString("employeeId");
//                scheduleBean.updateTime = employeeDataObject.getString("updateTime");
//
//                shiftDataArray = employeeDataObject.getJSONArray("shiftData");
//
//                for (int i = 0; i < shiftDataArray.length(); i++) {
//                    JSONObject job = shiftDataArray.getJSONObject(i);
//                    scheduleBean = new ScheduleBean();
//                    scheduleBean.date = job.getString("date");
//                    scheduleBean.isHoliday = job.getString("isHoliday");
//                    scheduleBean.upWorkDate = job.getString("upWorkDate");
//                    scheduleBean.offWorkDate = job.getString("offWorkDate");
//                    scheduleBean.dayOffType = job.getString("dayOffType");
//
//                    scheduleList.add(scheduleBean);
//                }
//
//            }catch (JSONException e) {
//
//                e.printStackTrace();
//                Log.e("TAG", e.toString());
//            }
//        }catch (IOException e) {
//            e.printStackTrace();
//        }
//        return scheduleList;
//
//    }
//    private String readStream(InputStream is) {
//        InputStreamReader isr;
//        String result = "";
//        try {
//            String line = "";
//            isr = new InputStreamReader(is, "utf-8");
//            BufferedReader br = new BufferedReader(isr);
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//    private static String getNowDate(){
//        long time = System.currentTimeMillis();
//        Date nowDate = new Date(time);
//        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String nowDay;
//        nowDay = format.format(nowDate);
//        return nowDay;
//    }
//
//}

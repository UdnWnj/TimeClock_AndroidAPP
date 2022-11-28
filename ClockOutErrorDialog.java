package com.udn.hr.clock.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.udn.hr.clock.test.mylib.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import Tools.ACache;
import androidx.annotation.NonNull;

public class ClockOutErrorDialog extends BottomSheetDialog {
    String H;
    String M;
    String HOLD;
    String MD;
    TextView textView, textView1, test_range, searchErrorText, nowTimeText, nowTimeData, sendOut, error_test_help;
    TimePicker picker;
    ImageView cancel_test;

    Date toDay;
    Date workOutRange1;
    Date workOutRange2;
    LinearLayout user_change_date, can_gone_layout;
    private SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat outputHH = new SimpleDateFormat("HH");
    private SimpleDateFormat outputMM = new SimpleDateFormat("mm");

    private SimpleDateFormat forDialog = new SimpleDateFormat("HH:mm(EE)");

    private Context mContext;
    private Date date2;
    private String errorTest;

    public interface PriorityListener2 {

        Date setChangeTime(Date s);

    }

    private PriorityListener2 listener;

    public ClockOutErrorDialog(@NonNull Context context, int themeResId, String errorTest, PriorityListener2 priorityListener2) {
        super(context, R.style.SheetDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        errorTest = errorTest;
        listener = priorityListener2;
        View view = View.inflate(mContext, R.layout.clock_in_error_dialog, null);
        setContentView(view);

        long time = System.currentTimeMillis();
        toDay = new Date(time);
        final SimpleDateFormat format = new SimpleDateFormat("M月d日 EE");
        final SimpleDateFormat formatToRange = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        MD = format.format(toDay);
        cancel_test = (ImageView) view.findViewById(R.id.cancel_test);
        error_test_help = (TextView) view.findViewById(R.id.error_test_help);
        searchErrorText = (TextView) view.findViewById(R.id.searchErrorText);
        sendOut = (TextView) view.findViewById(R.id.send_out);
        can_gone_layout = (LinearLayout) view.findViewById(R.id.can_gone_layout);
        user_change_date = (LinearLayout) view.findViewById(R.id.user_change_date);
        picker = (TimePicker) view.findViewById(R.id.picker);
        textView1 = (TextView) view.findViewById(R.id.text01);
        test_range = (TextView) findViewById(R.id.test_range);
        Log.d("ClockOutErrorDialog", errorTest);
        switch (errorTest) {
            case "個人因素":
//                error_test_help.setText("請記得前往聯８達差勤系統調整班表");
                error_test_help.setVisibility(View.GONE);
                break;
            case "調整班表":
                error_test_help.setText("請記得前往聯８達差勤系統調整班表");
                error_test_help.setVisibility(View.VISIBLE);

                break;
            case "計畫申請加班，但未完成程序":
                error_test_help.setText("加班需事先申請，請記得前往聯８達差勤系統補辦加班申請");
                error_test_help.setVisibility(View.VISIBLE);

                break;
        }

        if (ACache.get(mContext).getAsString("getApiWorkIn") != null &&
                ACache.get(mContext).getAsString("getApiWorkOut") != null) {//<--todo:判斷上下班時間有沒有NULL
            String workOut = ACache.get(mContext).getAsString("getApiWorkOut");
//            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            SimpleDateFormat outputHH = new SimpleDateFormat("HH");
//            SimpleDateFormat outputMM = new SimpleDateFormat("mm");
            try {
                Date workOutDate = input.parse(workOut);
                workOutRange1 = DateUtil.addDate(workOutDate, Calendar.MINUTE, 0);
                workOutRange2 = DateUtil.addDate(workOutDate, Calendar.MINUTE, 30);//**todo:下班時間後半小時
                Log.d("ClockOutErrorDialog", "下班時間workOut :" + workOut);
                Log.d("ClockOutErrorDialog", "下班時間(DATE)workOutRange1:" + workOutRange1);
                Log.d("ClockOutErrorDialog", "下班時間範圍(DATE)workOutRange2:" + workOutRange2);
                long aaa = workOutRange2.getTime() / 1000;
                long bbb = workOutRange1.getTime() / 1000;
                int Max = (int) aaa;
                int Min = (int) bbb;
                Random ans = new Random();
                int endTime = ans.nextInt((Max - Min) + 1) + Min;
                Log.d("ClockOutErrorDialog", "亂數下班時間範圍(Long)endTime:" + LongToDare(endTime));
                String newRangeTime = input.format(LongToDare(endTime));
                Date newRangeTime1 = input.parse(newRangeTime);
                Log.d("ClockOutErrorDialog", "亂數下班時間範圍(DATE)newRangeTime1:" + newRangeTime1);
                String HH = outputHH.format(newRangeTime1);
                String MM = outputMM.format(newRangeTime1);
                picker.setCurrentHour(Integer.valueOf(HH));
                Log.d("ClockOutErrorDialog", "亂數下班時間(小時):" + HH);
                picker.setCurrentMinute(Integer.valueOf(MM));
                Log.d("ClockOutErrorDialog", "亂數下班時間(分鐘):" + MM);

                MD = format.format(LongToDare(endTime));
                Log.d("ClockOutErrorDialog", "亂數下班時間(日期)" + MD);

                textView1.setText(MD);

                Calendar cal = Calendar.getInstance();
                cal.setTime(workOutRange2);

                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(HH));
                cal.set(Calendar.MINUTE, Integer.valueOf(MM));

                date2 = cal.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        textView1.setText(MD);
        picker.setIs24HourView(true);
        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                //取得 hour的值，透過TimeFix方法。轉換成String.並初始H。
                H = TimeFix(hourOfDay);
                //取得 minute的值，透過TimeFix方法。轉換成String.並初始M。
                M = TimeFix(minute);
                //將取得的資料設定到 textTime1
                if (HOLD != null) {
                    if (H.equals("00") && HOLD.equals("23")) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(toDay);
                        c.add(Calendar.DATE, 1);
                        toDay = c.getTime();
                        MD = format.format(toDay);
                        textView1.setText(MD);
                    } else if (H.equals("23") && HOLD.equals("00")) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(toDay);
                        c.add(Calendar.DATE, -1);
                        toDay = c.getTime();
                        MD = format.format(toDay);
                        textView1.setText(MD);

                    }
                }
                HOLD = H;
                test_range.setText(H + ":" + M);

                if (ACache.get(mContext).getAsString("getApiWorkIn") != null &&
                        ACache.get(mContext).getAsString("getApiWorkOut") != null) {//<--todo:判斷上下班時間有沒有NULL
                    String workOut = ACache.get(mContext).getAsString("getApiWorkOut");
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                        Date workOutDate = input.parse(workOut);

                        Date workOutRange2 = DateUtil.addDate(workOutDate, Calendar.MINUTE, 30);//**todo:下班時間後半小時

                        Log.d("ClockInErrorDialog", "workOut :" + workOut);
                        Log.d("ClockInErrorDialog", "workOutRange2:" + workOutRange2);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(workOutRange2);

                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(H));
                        cal.set(Calendar.MINUTE, Integer.parseInt(M));

                        date2 = cal.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


            }
        });
        cancel_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        searchErrorText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                searchListText.setVisibility(View.VISIBLE);
                        ScheduleErrorDialog d = new ScheduleErrorDialog(mContext, R.style.SheetDialog, new ScheduleErrorDialog.PriorityListener() {
                            @Override
                            public void setChangeTest(String s) {
                                searchErrorText.setText(s);
                            }
                        });
                        d.show();
                    }
                });
        sendOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("最後送出時間", "表訂下班時間:" + workOutRange1);
                Log.d("最後送出時間", "容許下班範圍:" + workOutRange2);
                Date outDate = new Date();
                if (date2.before(outDate) && date2.after(workOutRange1)) {
                    Log.d("最後送出時間", "範圍內:");
                    listener.setChangeTime(date2);
                    dismiss();
                } else {
                    String outDateOpen = forDialog.format(outDate);
                    String outDateEnd = forDialog.format(workOutRange1);
                    Log.d("ClockOutErrorDialog", "outDateOpen :::" + outDateOpen);
                    Log.d("ClockOutErrorDialog", "outDateEnd :::" + outDateEnd);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle("提醒");
                    dialog.setMessage("您僅能選擇" + outDateEnd + "到" + outDateOpen + "的時間區間");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                            .show();
                }

//                dismiss();
            }
        });
    }

    private static String TimeFix(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private String getNowDate() {
        long time = System.currentTimeMillis();
        Date nowDate = new Date(time);
        final SimpleDateFormat format = new SimpleDateFormat("MM月dd日 EEEE HH:mm");
        String nowDay;
        nowDay = format.format(nowDate);
        return nowDay;
    }

    public static Date LongToDare(long str) throws ParseException {
        return new Date(str * 1000);
    }
}

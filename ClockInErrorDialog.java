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

public class ClockInErrorDialog extends BottomSheetDialog {
    String H;
    String M;
    String HOLD;
    String MD;
    String yyyymmdd;
    TextView textView1, test_range, searchErrorText, sendOut, error_test_help;
    ImageView cancel_test;
    TimePicker picker;
    Date toDay;
    Date workInRange1;
    Date workInRange2;
    LinearLayout user_change_date, can_gone_layout;
    private SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat outputHH = new SimpleDateFormat("HH");
    private SimpleDateFormat outputMM = new SimpleDateFormat("mm");
    private SimpleDateFormat forDialog = new SimpleDateFormat("HH:mm(EE)");

    private Context mContext;
    private Date date2;
    private String errorTest;

    public interface PriorityListener1 {

        Date setChangeTime(Date s);

    }

    private PriorityListener1 listener;

    public ClockInErrorDialog(@NonNull Context context, int themeResId, String errorTest, PriorityListener1 priorityListener1) {
        super(context, R.style.SheetDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        errorTest = errorTest;
        listener = priorityListener1;
        View view = View.inflate(mContext, R.layout.clock_in_error_dialog, null);
        setContentView(view);

        long time = System.currentTimeMillis();
        toDay = new Date(time);
        final SimpleDateFormat format = new SimpleDateFormat("M???d??? EE");
        final SimpleDateFormat formatToRange = new SimpleDateFormat("yyyy-MM-dd");

        MD = format.format(toDay);
        yyyymmdd = formatToRange.format(toDay);
        searchErrorText = (TextView) view.findViewById(R.id.searchErrorText);
        sendOut = (TextView) view.findViewById(R.id.send_out);
//        sendOut.setVisibility(View.GONE);
        cancel_test = (ImageView) view.findViewById(R.id.cancel_test);
        error_test_help = (TextView) view.findViewById(R.id.error_test_help);
        can_gone_layout = (LinearLayout) view.findViewById(R.id.can_gone_layout);
        user_change_date = (LinearLayout) view.findViewById(R.id.user_change_date);
        picker = (TimePicker) view.findViewById(R.id.picker);
        textView1 = (TextView) view.findViewById(R.id.text01);
        test_range = (TextView) findViewById(R.id.test_range);
        Log.d("ClockInErrorDialog", errorTest);
        switch (errorTest) {
            case "????????????":
//                error_test_help.setText("????????????????????????????????????????????????");
                error_test_help.setVisibility(View.GONE);
                break;
            case "????????????":
                error_test_help.setText("????????????????????????????????????????????????");
                error_test_help.setVisibility(View.VISIBLE);

                break;
            case "???????????????????????????????????????":
                error_test_help.setText("??????????????????????????????????????????????????????????????????????????????");
                error_test_help.setVisibility(View.VISIBLE);

                break;
        }

        if (ACache.get(mContext).getAsString("getApiWorkIn") != null &&
                ACache.get(mContext).getAsString("getApiWorkOut") != null) {

            String workIn = ACache.get(mContext).getAsString("getApiWorkIn");
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputHH = new SimpleDateFormat("HH");
            SimpleDateFormat outputMM = new SimpleDateFormat("mm");
            try {
                Date workInDate = input.parse(workIn);
                workInRange1 = DateUtil.addDate(workInDate, Calendar.MINUTE, 0);
                workInRange2 = DateUtil.addDate(workInDate, Calendar.MINUTE, -30);//**todo:????????????????????????
                Log.d("ClockInErrorDialog", "????????????workIn :" + workIn);
                Log.d("ClockInErrorDialog", "????????????(DATE)workInRange1:" + workInRange1);
                Log.d("ClockInErrorDialog", "??????????????????(DATE)workInRange2:" + workInRange2);
                long aaa = workInRange1.getTime() / 1000;
                long bbb = workInRange2.getTime() / 1000;
                int Max = (int) aaa;
                int Min = (int) bbb;
                Random ans = new Random();
                int endTime = ans.nextInt((Max - Min) + 1) + Min;
                Log.d("ClockInErrorDialog", "????????????????????????(Long)endTime:" + LongToDare(endTime));
                String newRangeTime = input.format(LongToDare(endTime));
                Date newRangeTime1 = input.parse(newRangeTime);
                Log.d("ClockInErrorDialog", "????????????????????????(DATE)newRangeTime1:" + newRangeTime1);
                String HH = outputHH.format(newRangeTime1);
                Log.d("ClockInErrorDialog", "??????????????????(??????):" + HH);
                String MM = outputMM.format(newRangeTime1);
                Log.d("ClockInErrorDialog", "??????????????????(??????):" + MM);
                picker.setCurrentHour(Integer.valueOf(HH));
                picker.setCurrentMinute(Integer.valueOf(MM));
                MD = format.format(LongToDare(endTime));
                Log.d("ClockInErrorDialog", "??????????????????(??????)" + MD);
                textView1.setText(MD);

                Calendar cal = Calendar.getInstance();
                cal.setTime(workInRange2);
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
                //?????? hour???????????????TimeFix??????????????????String.?????????H???
                H = TimeFix(hourOfDay);
                //?????? minute???????????????TimeFix??????????????????String.?????????M???
                M = TimeFix(minute);
                //??????????????????????????? textTime1
                if (HOLD != null) {
                    if (H.equals("00") && HOLD.equals("23")) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(toDay);
                        c.add(Calendar.DATE, 1);
                        toDay = c.getTime();
                        MD = format.format(toDay);
                        yyyymmdd = formatToRange.format(toDay);

                        textView1.setText(MD);
                    } else if (H.equals("23") && HOLD.equals("00")) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(toDay);
                        c.add(Calendar.DATE, -1);
                        toDay = c.getTime();
                        MD = format.format(toDay);
                        yyyymmdd = formatToRange.format(toDay);
                        textView1.setText(MD);

                    }
                }
                HOLD = H;
                test_range.setText(H + ":" + M);
                if (ACache.get(mContext).getAsString("getApiWorkIn") != null &&
                        ACache.get(mContext).getAsString("getApiWorkOut") != null) {//<--todo:??????????????????????????????NULL
                    String workIn = ACache.get(mContext).getAsString("getApiWorkIn");
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat outputHH = new SimpleDateFormat("HH");
                    SimpleDateFormat outputMM = new SimpleDateFormat("mm");

                    try {
                        Date workInDate = input.parse(workIn);
                        Date workInRange2 = DateUtil.addDate(workInDate, Calendar.MINUTE, -30);//**todo:????????????????????????
                        String HH = outputHH.format(workInRange2);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(workInRange2);
                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(H));
                        cal.set(Calendar.MINUTE, Integer.parseInt(M));
                        date2 = cal.getTime();
                        Log.d("ClockInErrorDialog", "??????????????????date2:" + date2);


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
                                Log.d("ClockInErrorDialog", "???????????????????????????" + s);
                            }
                        });
                        d.show();
                    }
                });
        sendOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("??????????????????", "??????????????????:" + workInRange1);
                Log.d("??????????????????", "??????????????????:" + workInRange2);
                Date outDate = new Date();
                if (date2.before(workInRange1) && date2.after(outDate)) {
                    Log.d("??????????????????", "?????????:");
                    listener.setChangeTime(date2);
                    dismiss();
                } else {
                    Log.d("??????????????????", "?????????:");
                    String outDateOpen = forDialog.format(outDate);
                    String outDateEnd = forDialog.format(workInRange1);
                    Log.d("ClockOutErrorDialog", "outDateOpen :::" + outDateOpen);
                    Log.d("ClockOutErrorDialog", "outDateEnd :::" + outDateEnd);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle("??????");
                    dialog.setMessage("???????????????" + outDateOpen + "???" + outDateEnd + "???????????????");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                            .show();

                }

//                if (date2!=null){
//                    listener.setChangeTime(date2);
//                }else {
//                    date2 = new Date();
//                    listener.setChangeTime(date2);
//                }
//
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
        final SimpleDateFormat format = new SimpleDateFormat("MM???dd??? EEEE HH:mm");
        String nowDay;
        nowDay = format.format(nowDate);
        return nowDay;
    }

    public static Date LongToDare(long str) throws ParseException {
        return new Date(str * 1000);
    }

}

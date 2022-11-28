package com.udn.hr.clock.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;

public class ScheduleErrorDialog extends BottomSheetDialog {
    private Context mContext;
    private TextView errorList1, errorList2, errorList3, errorList4;
    private ImageView cancel_button;

    public interface PriorityListener {

        void setChangeTest(String s);

    }

    private PriorityListener changeNewOrOld;

    public ScheduleErrorDialog(@NonNull final Context context, int themeResId, final PriorityListener priorityListener) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mContext = context;
        this.changeNewOrOld = priorityListener;
        this.setCanceledOnTouchOutside(false);

        View view = View.inflate(mContext, R.layout.schedule_error_dialog, null);
        setContentView(view);
        cancel_button = view.findViewById(R.id.cancel_button);
        errorList1 = view.findViewById(R.id.errorList1);
        errorList2 = view.findViewById(R.id.errorList2);
        errorList3 = view.findViewById(R.id.errorList3);
        errorList4 = view.findViewById(R.id.errorList4);

        errorList1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityListener.setChangeTest("個人因素");
                dismiss();

            }
        });
        errorList2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityListener.setChangeTest("調整班表");
                dismiss();

            }
        });
        errorList3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("加班應事先申請");
                builder.setMessage("加班應事先申請並經過主管核准\n請先確認主管同意再加班");
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        priorityListener.setChangeTest("計畫申請加班，但未完成程序");
                        dismiss();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(14);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.DialogButtonTC));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(14);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.DialogButtonTC));
                try {
                    Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                    mAlert.setAccessible(true);
                    Object mAlertController = mAlert.get(dialog);
                    //通过反射修改title字体大小和颜色
                    Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                    mTitle.setAccessible(true);
                    TextView mTitleView = (TextView) mTitle.get(mAlertController);
                    mTitleView.setTextSize(24);
                    mTitleView.setTextColor(Color.BLACK);
                    //通过反射修改message字体大小和颜色
                    Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                    mMessage.setAccessible(true);
                    TextView mMessageView = (TextView) mMessage.get(mAlertController);
                    mMessageView.setTextSize(16);
                    mMessageView.setTextColor(getContext().getResources().getColor(R.color.DialogTestMassage));
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (NoSuchFieldException e2) {
                    e2.printStackTrace();
                }


//                priorityListener.setChangeTest("計畫申請加班，但未完成程序");
//                dismiss();


            }
        });
        errorList4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityListener.setChangeTest("其他");
                dismiss();


            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}

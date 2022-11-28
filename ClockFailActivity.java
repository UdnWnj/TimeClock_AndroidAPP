package com.udn.hr.clock.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.mylib.view.ImageViewEx;
import com.udn.hr.clock.test.mylib.view.ViewEx;
import com.udn.hr.clock.test.sqlite.CheckRec;
import com.udn.hr.clock.test.sqlite.CheckRecKey;
import com.udn.hr.clock.test.sqlite.DBManager;

public class ClockFailActivity extends Activity
	{
	public static final String EXTRA_CHECK_REC_KEY = "checkRecKey";
	
	private CheckRecKey mCheckRecKey;

	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock_fail);
		
		prvProcessIntent();
		
		prvProcessView();
		prvProcessControl();
		
//		GoogleAnalyticsManager.sendGoogleAnalyticsPageView(this, GoogleAnalyticsManager.SCREEN_NAME_CLOCK_FAIL);
		}

	/*
	 * Private Methods
	 */

	private void prvProcessControl()
		{
		prvProcessControlIvBack();
		prvProcessControlIvOk();
		}

	private void prvProcessControlIvBack()
		{
		ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
		
		ivBack.setOnClickListener(new View.OnClickListener()
			{
			@Override
			public void onClick(View v)
				{
				Utility.finishActivity(ClockFailActivity.this, RESULT_OK, null, 0);
				}
			});
		
		ViewEx.registerSimpleOnTouchVisualEffect(ivBack);
		}	
	
	private void prvProcessControlIvOk()
		{
		ImageView ivOK = (ImageView) findViewById(R.id.ivOK);

        ViewEx.registerSimpleOnTouchVisualEffect(ivOK);

		ivOK.setOnClickListener(new View.OnClickListener()
			{
			@Override
			public void onClick(View v)
				{
				StringBuffer errMsg = new StringBuffer();

				final CheckRec checkRec = DBManager.getInstance(ClockFailActivity.this).getCheckRec(mCheckRecKey.getEmpId(), mCheckRecKey.getCheckDate(), mCheckRecKey.getCheckType());
				
				if (checkRec == null)
					{
					Utility.logE("ivOK.onClick(): checkRec == null");
					return;
					}

				checkRec.setRemarkNetwork(((TextView)findViewById(R.id.etRemark)).getText().toString());
				
				if (!DBManager.getInstance(ClockFailActivity.this).saveCheckRec(checkRec, errMsg))
					{
					Utility.showToast(ClockFailActivity.this, errMsg.toString());
					Utility.logE("ivOK.onClick(): " + errMsg.toString());
					}

				Intent intent = new Intent();
//				Intent intent = getIntent();

				intent.putExtra(EXTRA_CHECK_REC_KEY, mCheckRecKey);
				
				Utility.finishActivity(ClockFailActivity.this, Activity.RESULT_OK, intent, 0);
				}
			});
		}

	private void prvProcessIntent()
		{
		Intent intent = getIntent();

		mCheckRecKey = (CheckRecKey) intent.getSerializableExtra(EXTRA_CHECK_REC_KEY);
		}

	private void prvProcessView()
		{
		DisplayMetrics dispMetrics = Utility.getDisplayMetrics(this);
		
		prvProcessViewIvOK(dispMetrics);
		prvProcessViewIvOops(dispMetrics);
		}

	@SuppressWarnings("deprecation")
	private void prvProcessViewIvOK(DisplayMetrics dispMetrics)
		{
		new ImageViewEx((ImageView) findViewById(R.id.ivOK)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.btn_confirm), (int) (dispMetrics.widthPixels * 0.9));
		}

	@SuppressWarnings("deprecation")
	private void prvProcessViewIvOops(DisplayMetrics dispMetrics)
		{
		new ImageViewEx((ImageView) findViewById(R.id.ivOops)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.icon_oops), (int) (dispMetrics.widthPixels * 0.5));
		}
	
	/*
	 * Private Methods End
	 */
	}

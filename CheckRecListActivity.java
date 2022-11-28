package com.udn.hr.clock.test;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.mylib.view.ImageViewEx;
import com.udn.hr.clock.test.mylib.view.ListViewExOnMeasure;
import com.udn.hr.clock.test.mylib.view.ViewEx;
import com.udn.hr.clock.test.sqlite.CheckRecKey;
import com.udn.hr.clock.test.sqlite.DBManager;

public class CheckRecListActivity extends Activity
	{
//	private class PrvList extends ArrayList<CheckRec>
//		{
//		private static final long serialVersionUID = 6527958750719116479L;
//
//		private PrvList(List<CheckRec> list)
//			{
//			this.addAll(list);
//			}
//		}
	
	public static final String EXTRA_EMP_ID = "empId";
	public static final String EXTRA_CHECK_REC_KEY = "checkRecKey";
	public static final String EXTRA_NEED_LOGIN = "needLogin";

	private static final boolean USE_CUSTOMIZE_FOOTER = false;
	
	private String mEmpId;
	private CheckRecKey mCheckRecKey;
	private boolean mNeedLogin;
	
	CheckRecAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		
		if (USE_CUSTOMIZE_FOOTER)
			{
			setContentView(R.layout.activity_check_rec_list_use_customize_footer);
			
			DisplayMetrics dispMetrics = Utility.getDisplayMetrics(this);
			
			int h = dispMetrics.heightPixels - getResources().getDimensionPixelSize(R.dimen.navigation_bar_height) - Utility.getStatusBarHeight(this);
			
			new ViewEx(findViewById(R.id.rlCheckRecListContainer)).setAspect(dispMetrics.widthPixels, h);

			prvCreateFooterView();
			}
		else
			setContentView(R.layout.activity_check_rec_list);
		
		prvProcessIntent();
		prvProcessView();
		prvProcessControl();

//		GoogleAnalyticsManager.sendGoogleAnalyticsPageView(this, GoogleAnalyticsManager.SCREEN_NAME_CHECK_REC);

		if (mNeedLogin)
			{
			MainActivity.logout(CheckRecListActivity.this);

			DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
				{
				@Override
				public void onClick(DialogInterface dialog, int which)
					{
					Intent intent = new Intent();

					intent.putExtra(EXTRA_NEED_LOGIN, mNeedLogin);

					Utility.finishActivity(CheckRecListActivity.this, Activity.RESULT_OK, intent, 0);
					}
				};

			Utility.showSimpleAlertDialog(this, getString(R.string.login_again), posListener);
			}

//		CheckRecList checkRecList = DBManager.getInstance(this).getCheckRecList(mEmpId);
//
//		Util.logD("onCreate(): checkRecList = " + checkRecList);
//
//		checkRecList.sortWithCheckDate();
//
//		Util.logD("onCreate(): checkRecList = " + checkRecList);
		}
	
	/*
	 * Private Methods
	 */

	@SuppressWarnings("deprecation")
	private void prvCreateFooterView()
		{
		ImageView ivFooter = (ImageView) findViewById(R.id.ivFooter);
		
		DisplayMetrics dispMetrics = Utility.getDisplayMetrics(this);
		new ImageViewEx(ivFooter).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.bg_index), dispMetrics.widthPixels);
		new ViewEx(ivFooter).setAspect(dispMetrics.widthPixels, dispMetrics.widthPixels / 4);
		}

	private void prvProcessControl()
		{
		prvProcessControlIvBack();
		prvProcessControlIvHelp();
		prvProcessControlListViewAdapter();
		}

	private void prvProcessControlIvBack()
		{
		ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
		
		ivBack.setOnClickListener(new View.OnClickListener()
			{
			@Override
			public void onClick(View v)
				{
				Utility.finishActivity(CheckRecListActivity.this, RESULT_OK, null, 0);
				}
			});
		
		ViewEx.registerSimpleOnTouchVisualEffect(ivBack);
		}

	private void prvProcessControlIvHelp()
		{
		ImageView ivHelp = (ImageView) findViewById(R.id.ivHelp);
		
		ivHelp.setOnClickListener(new View.OnClickListener()
			{
			@Override
			public void onClick(View v)
				{
				MainActivity.showFirstLoginAlertDialog(CheckRecListActivity.this, null);
				}
			});
		
		ViewEx.registerSimpleOnTouchVisualEffect(ivHelp);
		}
	
	private void prvProcessControlListView(CheckRecAdapter adpt)
		{
		final ListView lvCheckRecList;
		
		if (USE_CUSTOMIZE_FOOTER)
			lvCheckRecList = ((ListViewExOnMeasure) findViewById(R.id.lvxCheckRecList));
		else
			lvCheckRecList = ((ListView) findViewById(R.id.lvCheckRecList));
		
		lvCheckRecList.setAdapter(adpt);

		if (USE_CUSTOMIZE_FOOTER)
			{
			lvCheckRecList.post(new Runnable()
				{
				@Override
				public void run()
					{
					Utility.setListViewHeightBasedOnChildren(CheckRecListActivity.this, (ListViewExOnMeasure) lvCheckRecList);
					}
				});
			}
		}

	private void prvProcessControlListViewAdapter()
		{
//		PrvList list = new PrvList(DBManager.getInstance(this).getCheckRecList(mEmpId));

		mAdapter = new CheckRecAdapter(this, DBManager.getInstance(this).getCheckRecList(mEmpId), mCheckRecKey);
//		mAdapter = new CheckRecAdapter(this, list, mCheckRecKey);
		prvProcessControlListView(mAdapter);
		}
	
	private void prvProcessIntent()
		{
		Intent intent = getIntent();

		mEmpId = intent.getStringExtra(EXTRA_EMP_ID);
		mCheckRecKey = (CheckRecKey) intent.getSerializableExtra(EXTRA_CHECK_REC_KEY);
		mNeedLogin = intent.getBooleanExtra(EXTRA_NEED_LOGIN, false);
		}
	
	private void prvProcessView()
		{
		}
	/*
	 * Private Methods End
	 */
	}

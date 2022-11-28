package com.udn.hr.clock.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.udn.hr.clock.test.mylib.Utility;
import com.udn.hr.clock.test.mylib.asynctask.AsyncHttpPostStatic;
import com.udn.hr.clock.test.mylib.asynctask.AsyncHttpRequestResult;
import com.udn.hr.clock.test.mylib.view.ImageViewEx;
import com.udn.hr.clock.test.mylib.view.ImageViewEx.Ratio;
import com.udn.hr.clock.test.mylib.view.ViewEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends Activity {
    private static class PrvAsyncHttpPost extends AsyncHttpPostStatic<LoginActivity> {
        private PrvAsyncHttpPost(LoginActivity act, int requestCode) {
            super(requestCode, act, true);
        }

        @Override
        protected void onPostExecute(AsyncHttpRequestResult result) {
            super.onPostExecute(result);

            LoginActivity act = mCRef.get();

            if (act == null)
                return;

            if (result.isSuccess)
                switch (mRequestCode) {
                    case HTTP_POST_REQUEST_CODE_LOGIN:
                        PrvLoginResult loginResult = act.prvParseLogin(result.content);

                        if (loginResult.isSuccess && loginResult.isAllowClockIn)
                            act.prvLoginSucceed(loginResult);
                        else
                            Utility.showSimpleAlertDialog(act, loginResult.errMsg, null);

                        break;
                }
            else
                Utility.showSimpleAlertDialog(act, act.getString(R.string.network_not_available), null);
        }
    }

    private class PrvEtPasswordOnEditorActionListener implements OnEditorActionListener {
        @Override
        public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                prvDoLogin();

                return true;
            }

            return false;
        }
    }

    private class PrvLoginResult {
        boolean isSuccess = false;
        boolean isAllowClockIn = false;

        String empNo;
        String empName;

        String errMsg;
    }

    private static final int HTTP_POST_REQUEST_CODE_LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prvProcessView();
        prvProcessControl();

        findViewById(R.id.etEmpNo).requestFocus();

//        GoogleAnalyticsManager.sendGoogleAnalyticsPageView(this, GoogleAnalyticsManager.SCREEN_NAME_LOGIN_PAGE);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Utility.finishActivity(this, Activity.RESULT_CANCELED, null, 0);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /*
     * Private Methods
     */
    private void prvDoLogin() {
        String empNo = ((EditText) findViewById(R.id.etEmpNo)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();

        if (empNo.isEmpty()) {
            Utility.showToast(LoginActivity.this, R.string.please_input_emp_no);
            return;
        }

        if (password.isEmpty()) {
            Utility.showToast(LoginActivity.this, R.string.please_input_password);
            return;
        }
        //todo:test用，記得改掉。 上面要復原
//        empNo = "08893";
//        password = "B10007038";

        if (ApplicationEx.TEST_USE_FAKE_EMP_ID) {
            PrvLoginResult result = new PrvLoginResult();

            result.isSuccess = true;
            result.isAllowClockIn = true;
            result.empNo = "168888";
            result.empName = "王大發";

            prvLoginSucceed(result);
        } else
            prvHttpPostLogin(empNo, password);
    }

    private void prvHttpPostLogin(String userId, String password) {
        //todo: 2019/02/20 api 更新!!
        String url = "https://eip.udngroup.com/eip/sysuser/api/login.jsp?";
        String postString ="";
        try {
            postString = "userId=" + URLEncoder.encode(userId,"UTF-8") + "&" + "password=" + URLEncoder.encode(password,"UTF-8");;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        String postString = "userId=" + userId + "&" + "password=" + password;
        AsyncHttpPostStatic.AsyncHttpPostPara para = null;
//        try {
//            para = new AsyncHttpPostStatic.AsyncHttpPostPara(url, URLEncoder.encode(postString,"UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        para = new AsyncHttpPostStatic.AsyncHttpPostPara(url, postString);
        new PrvAsyncHttpPost(this, HTTP_POST_REQUEST_CODE_LOGIN).execute(para);
    }

    private void prvLoginSucceed(PrvLoginResult loginResult) {
        MainActivity.registerPrefLoginInfo(this, loginResult.empNo, loginResult.empName);

        Utility.showToast(this, R.string.login_succeed);
        Utility.finishActivity(LoginActivity.this, Activity.RESULT_OK, null, 0);
    }

    private PrvLoginResult prvParseLogin(String content) {
        final String TRUE = "true";
        final String Y = "Y";

        PrvLoginResult result = new PrvLoginResult();

        try {
            JSONObject jsonObj = new JSONObject(content);

            result.isSuccess = jsonObj.getString("success").equals(TRUE);

            if (result.isSuccess) {
                JSONObject jsonResult = jsonObj.getJSONObject("result");

                result.isAllowClockIn = jsonResult.getString("checkInStatus").equals(Y);

                result.empNo = jsonResult.getString("empNo");
                result.empName = jsonResult.getString("empName");

                if (!result.isAllowClockIn)
                    result.errMsg = getString(R.string.do_not_allow_clock_in);
            } else
                result.errMsg = jsonObj.getString("result");
        } catch (JSONException e) {
            Utility.logE("prvParseLogin(): " + e.toString());

            final String ERROR_CONT = "對不起，系統異常，目前無法登入！";

            if (content.contains(ERROR_CONT))
                result.errMsg = getString(R.string.login_fail_emp_id_password_not_activated);
            else
                result.errMsg = e.toString();
        }

        return result;
    }

    private void prvProcessControl() {
        prvProcessControlEtPassword();

        prvProcessControlTvForgetPassword();
        prvProcessControlIvLogin();
    }

    private void prvProcessControlEtPassword() {
        ((EditText) findViewById(R.id.etPassword)).setOnEditorActionListener(new PrvEtPasswordOnEditorActionListener());
    }

    private void prvProcessControlIvLogin() {
        findViewById(R.id.ivLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prvDoLogin();

//				String empNo = ((EditText) findViewById(R.id.etEmpNo)).getText().toString();
//				String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
//				
//				if (empNo.isEmpty())
//					{
//					Utility.showToast(LoginActivity.this, R.string.please_input_emp_no);
//					return;
//					}
//				
//				if (password.isEmpty())
//					{
//					Utility.showToast(LoginActivity.this, R.string.please_input_password);
//					return;
//					}
//
//				if (MainActivity.USE_FAKE_EMP_ID)
//					{
//					PrvLoginResult result = new PrvLoginResult();
//					
//					result.isSuccess = true;
//					result.isAllowClockIn = true;
//					result.empNo = "168888";
//					result.empName = "王大發";
//					
//					prvLoginSucceed(result);
//					}
//				else
//					prvHttpPostLogin(empNo, password);
            }
        });
    }

    private void prvProcessControlTvForgetPassword() {
        findViewById(R.id.tvForgetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = "https://eip.udngroup.com/eip/set/forgetpwd.jsp";

                Utility.startActivityIntentActionView(LoginActivity.this, url);
            }
        });
    }

    private void prvProcessView() {
        DisplayMetrics dispMetrics = Utility.getDisplayMetrics(this);

        prvProcessViewLlEmpNoPassword(dispMetrics);
        prvProcessViewIvLogout(dispMetrics);
    }

    @SuppressWarnings("deprecation")
    private void prvProcessViewIvLogout(DisplayMetrics dispMetrics) {
//        new ImageViewEx((ImageView) findViewById(R.id.ivLogin)).displayWithCustomizeWidth(getResources().getDrawable(R.drawable.btn_login), (int) (dispMetrics.widthPixels * 0.9));

    }

    @SuppressWarnings("deprecation")
    private void prvProcessViewLlEmpNoPassword(DisplayMetrics dispMetrics) {
        Ratio ratio = ImageViewEx.getAspectRatio(getResources().getDrawable(R.drawable.btn_login));

        int width = (int) (dispMetrics.widthPixels * 0.9);
        int height = (int) (width / ratio.getRatio());

        new ViewEx(findViewById(R.id.llEmpNo)).setAspect(width, height);
        new ViewEx(findViewById(R.id.llPassword)).setAspect(width, height);

        height *= 0.55;

        new ImageViewEx((ImageView) findViewById(R.id.ivEmpNo)).displayWithCustomizeHeight(height);
        new ImageViewEx((ImageView) findViewById(R.id.ivPassword)).displayWithCustomizeHeight(height);
    }
    /*
     * Private Methods End
     */
}

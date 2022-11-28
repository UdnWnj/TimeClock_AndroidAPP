package com.udn.hr.clock.test;

import android.location.Location;
import android.util.Log;

import com.udn.hr.clock.test.mylib.Constant;
import com.udn.hr.clock.test.mylib.DateUtil;
import com.udn.hr.clock.test.sqlite.CheckRec;

import java.util.Date;

public class ClockPara {
    public static final DateUtil.DateFormatterYYYYMMDDDashHHMMSS24 CHECK_DATE_FORMATTER = DateUtil.DATE_FORMATTER_YYYYMMDD_DASH_HHMMSS_24;
//	private static final String CHECK_DATE_FORMATb = DateUtil.FORMAT_YYYY_MM_DD_DASH_HH_MM_SS_24;

    public static final int LOCATION_STATUS_SUCCESS = 0;
    public static final int LOCATION_STATUS_WITHOUT_LOCATION = -1;
    public static final int LOCATION_STATUS_WITHOUT_ADDRESS = -2;

    private String mEmpId = Constant.NULL_STRING;
    private String mCheckDateYYYYMMDDHHMMSS = Constant.NULL_STRING;
    private String mCheckType = Constant.NULL_STRING;
    private String mDeviceId = Constant.NULL_STRING;
    private String mGPSInfo = Constant.NULL_STRING;
    private String mCheckAddr = Constant.NULL_STRING;
    private String mRemark = Constant.NULL_STRING;
    //0328*新增需上傳之欄位
    private String mAdjustComment = Constant.NULL_STRING;
    private String mWi_FiSSID = Constant.NULL_STRING;
    private String mSSIDCheckin = Constant.NULL_STRING;
    private String mUnusualStatus = Constant.NULL_STRING;
    private String mAdjustDate = Constant.NULL_STRING;
    private String mAdjustReceiveDate = Constant.NULL_STRING;

    private boolean mIsDuplicated = false;

    private Location mLoc;

    public void setWithCheckRec(CheckRec checkRec, String deviceId) {
        mEmpId = checkRec.getEmpId();
        mCheckDateYYYYMMDDHHMMSS = checkRec.getCheckDate(CHECK_DATE_FORMATTER.getFormat());
        mCheckType = checkRec.getCheckType();
        mDeviceId = deviceId;
        mGPSInfo = checkRec.getGPSInfo();
        mCheckAddr = checkRec.getCheckAddr();
        mRemark = checkRec.getRemark();
        //*0328
        mAdjustComment = checkRec.getmAdjustComment();
        mWi_FiSSID = checkRec.getmWi_FiSSID();
        mSSIDCheckin = checkRec.getmSSIDCheckin();
        mUnusualStatus = checkRec.getmUnusualStatus();
        mAdjustDate = checkRec.getmAdjustDate();
        mAdjustReceiveDate = checkRec.getmAdjustReceiveDate();
    }

    public String getEmpId() {
        return mEmpId;
    }

    public void setEmpId(String empId) {
        this.mEmpId = empId;
    }

    public Date getCheckDate() {
        return CHECK_DATE_FORMATTER.parse(mCheckDateYYYYMMDDHHMMSS);
    }

    public String getCheckDateYYYYMMDDHHMMSS() {
        return mCheckDateYYYYMMDDHHMMSS;
    }

    public void setCheckDateYYYYMMDDHHMMSS(Date checkDate) {
        this.mCheckDateYYYYMMDDHHMMSS = DateUtil.formatDate(checkDate, CHECK_DATE_FORMATTER.getFormat());
    }

    public void setCheckDateYYYYMMDDHHMMSS(String checkDateYYYYMMDDHHMMSS) {
        this.mCheckDateYYYYMMDDHHMMSS = checkDateYYYYMMDDHHMMSS;
    }

    public String getCheckType() {
        return mCheckType;
    }

    public void setCheckType(String checkType) {
        this.mCheckType = checkType;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
    }

    public String getGPSInfo() {
        return mGPSInfo;
    }

    public void setGpsInfo(String gpsInfo) {
        this.mGPSInfo = gpsInfo;
    }

    public String getCheckAddr() {
        return mCheckAddr;
    }

    public void setCheckAddr(String checkAddr) {
        this.mCheckAddr = checkAddr;
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        this.mRemark = remark;
    }

    public void setRemarkAddr(String remarkAddr) {
        mRemark = CheckRec.processSetRemarkAddr(mRemark, remarkAddr);
    }

    public void setRemarkNetwork(String remarkNetwork) {
        mRemark = CheckRec.processSetRemarkNetwork(mRemark, remarkNetwork);
    }

    public String getSN() {
        final String ANDROID_92 = "92";

//		Utility.logD("getSN(): return " + mCheckType + mEmpId + DateUtil.formatDate(mCheckDateYYYYMMDDHHMMSS, CHECK_DATE_FORMATTER.FORMAT, DateUtil.FORMAT_YYYYMMDDHHMM_24) + ANDROID_92);
        Log.d("ClockPara", "SN_AdjustDate :" + mAdjustDate);
        Log.d("ClockPara", "SN_CheckDate :" + mCheckDateYYYYMMDDHHMMSS);

        if (mAdjustDate != null && !mAdjustDate.equals("")) {
            Log.d("ClockPara", "SN_AdjustDate :" + mCheckType + mEmpId + DateUtil.formatDate(mAdjustDate, CHECK_DATE_FORMATTER.getFormat(), DateUtil.FORMAT_YYYYMMDDHHMM_24) + ANDROID_92);

            return mCheckType + mEmpId + DateUtil.formatDate(mAdjustDate, CHECK_DATE_FORMATTER.getFormat(), DateUtil.FORMAT_YYYYMMDDHHMM_24) + ANDROID_92;
            }else {
            Log.d("ClockPara", "SN_CheckDate :" + mCheckType + mEmpId + DateUtil.formatDate(mCheckDateYYYYMMDDHHMMSS, CHECK_DATE_FORMATTER.getFormat(), DateUtil.FORMAT_YYYYMMDDHHMM_24) + ANDROID_92);

            return mCheckType + mEmpId + DateUtil.formatDate(mCheckDateYYYYMMDDHHMMSS, CHECK_DATE_FORMATTER.getFormat(), DateUtil.FORMAT_YYYYMMDDHHMM_24) + ANDROID_92;

        }

//        return mCheckType + mEmpId + DateUtil.formatDate(mCheckDateYYYYMMDDHHMMSS, CHECK_DATE_FORMATTER.getFormat(), DateUtil.FORMAT_YYYYMMDDHHMM_24) + ANDROID_92;
    }

    public Location getLoc() {
        return mLoc;
    }

    public void setLoc(Location loc) {
        this.mLoc = loc;
    }

    public boolean isDuplicated() {
        return mIsDuplicated;
    }

    public void setIsDuplicated(boolean isDuplicated) {
        this.mIsDuplicated = isDuplicated;
    }

    public int getLocationStatus() {
        if (getLoc() == null)
            return LOCATION_STATUS_WITHOUT_LOCATION;
        else if (getCheckAddr().isEmpty())
            return LOCATION_STATUS_WITHOUT_ADDRESS;
        else
            return LOCATION_STATUS_SUCCESS;
    }

    public String getmAdjustComment() {
        return mAdjustComment;
    }

    public String getmWi_FiSSID() {
        return mWi_FiSSID;
    }

    public String getmSSIDCheckin() {
        return mSSIDCheckin;
    }

    public String getmUnusualStatus() {
        return mUnusualStatus;
    }

    public String getmAdjustDate() {
        return mAdjustDate;
    }

    public String getmAdjustReceiveDate() {
        return mAdjustReceiveDate;
    }

    public void setmAdjustComment(String mAdjustComment) {
        this.mAdjustComment = mAdjustComment;
    }

    public void setmWi_FiSSID(String mWi_FiSSID) {
        this.mWi_FiSSID = mWi_FiSSID;
    }

    public void setmSSIDCheckin(String mSSIDCheckin) {
        this.mSSIDCheckin = mSSIDCheckin;
    }

    public void setmUnusualStatus(String mUnusualStatus) {
        this.mUnusualStatus = mUnusualStatus;
    }

    public void setmAdjustDate(String mAdjustDate) {
        this.mAdjustDate = mAdjustDate;
    }

    public void setmAdjustReceiveDate(String mAdjustReceiveDate) {
        this.mAdjustReceiveDate = mAdjustReceiveDate;
    }

}

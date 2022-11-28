package com.udn.hr.clock.test;

import java.util.Date;

public class LoginInfo
	{
	private String mEmpNo;
	private String mEmpName;
	private Date mLoginDate;

	public String getEmpNo()
		{
		return mEmpNo;
		}
	
	public void setEmpNo(String empNo)
		{
		this.mEmpNo = empNo;
		}
	
	public String getEmpName()
		{
		return mEmpName;
		}
	
	public void setEmpName(String empName)
		{
		this.mEmpName = empName;
		}
	
	public Date getLoginDate()
		{
		return mLoginDate;
		}
	
	public void setLoginDate(Date loginDate)
		{
		this.mLoginDate = loginDate;
		}
	}

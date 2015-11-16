package com.bank.mb;

import java.sql.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class DateUtil {

	private String dateBack;
	
    public String getdateBack() {
        java.util.Date today = new java.util.Date();
        Date date = new java.sql.Date(today.getTime());        
        dateBack = date.toString().replaceAll("-", ".");
        return dateBack;
    }
}

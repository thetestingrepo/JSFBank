package com.bank.mb;

import java.util.Date;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class DateBean {

    String dateBack;

    public String getDateBack() {
        java.util.Date today = new java.util.Date();
        Date date = new java.sql.Date(today.getTime());        
        dateBack = date.toString().replaceAll("-", ".");
        return dateBack;
    }
    
    public DateBean() {
    }
    
}

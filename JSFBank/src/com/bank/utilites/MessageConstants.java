package com.bank.utilites;

public class MessageConstants {

	public final static String successTrans = "Transaction succesful"; 
	public final static String unSuccessTransNoRollback = "Trasaction unsuccesful - rolling back was unsuccesful!";
	public final static String unSuccessTransRollback = "Trasaction unsuccesful - rolling back done!";
	public final static String selfTransaction =  "Error - no money can be send for your same account";
	public final static String generalTransError = "Error - no transaction found...";
	public final static String noSuchUser = "Error - this user account doesn't exist in the DB!";
	
}

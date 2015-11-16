package com.bank.mb;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Account {
	private int userId;
	private int balance;
	private int accountId;
	private List<Transaction> previusTrans;
	
	public Account() {
		previusTrans = new ArrayList<Transaction>();
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}


	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}


	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public List<Transaction> getPreviusTrans() {
		return previusTrans;
	}

	public void setPreviusTrans(List<Transaction> previusTrans) {
		this.previusTrans = previusTrans;
	}
	
}

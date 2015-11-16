package com.bank.mb;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class BankController {
	private String loginName;
	private String password;
    private User user;
    private String message;
    private List<Transaction> unValidatedTransactions;
    private Boolean[] checkedItems;
    
    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	DbUtil dbUtil = null;
	public BankController() throws Exception {
		dbUtil = DbUtil.getInstance();
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String login() throws Exception {
		if(getLoginName() != null && getPassword() != null){
			user = dbUtil.login(getLoginName(), getPassword());
			if(user != null) {
				return "success";
			}else {
				return "failure";
			}
		}
		else {
			return "failure";
		}
	}
	
	public void newTransaction(Transaction transaction) {
		setMessage(dbUtil.newTransaction(transaction));
	}

	public List<Transaction> getUnValidatedTransactions() {
		
		return unValidatedTransactions;
	}

	public void setUnValidatedTransactions(List<Transaction> unValidatedTransactions) {
		this.unValidatedTransactions = unValidatedTransactions;
	}

	public Boolean[] getCheckedItems() {
		return checkedItems;
	}

	public void setCheckedItems(Boolean[] checkedItems) {
		this.checkedItems = checkedItems;
	} 
	
	
}

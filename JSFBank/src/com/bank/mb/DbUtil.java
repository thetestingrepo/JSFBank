package com.bank.mb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.bank.utilites.MessageConstants;


public class DbUtil {

	private static DataSource dataSource;
	private static DbUtil instance;
	private String jndiName = "java:comp/env/jdbc/Web_bank";
	private User user;
	
	public DbUtil() throws NamingException {
		super();
		dataSource = getDataSource();
	}
	
	public static DbUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new DbUtil();
		}
		return instance;
	}
	
	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		return theDataSource;
	}
	
	private Connection getConnection() throws Exception {
		Connection conn = dataSource.getConnection();
		return conn;
	}
	
	public User login(String loginName, String password) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		user = new User();
		conn = getConnection();
		
         if(loginName!=null&loginName.length()>0){
        	 String sqlLogin="select * from users where loginname=? and password =?";
        	 stmt = conn.prepareStatement(sqlLogin);
        	 
        	 stmt.setString(1,loginName);
        	 stmt.setString(2,password);
             rs=stmt.executeQuery();
             
             if(rs.next()){                        
                 user.setId(rs.getInt(1));
                 user.setLoginName(rs.getString(2));
                 user.setUserName(rs.getString(3));
                 user.setPassword(rs.getString(4));
                 user.setOperator(rs.getBoolean(5));
                 user.setAdmin(rs.getBoolean(6));
                 int userId = user.getId();
                 String sqlAccount = "select * from account where user_id=?";
                 PreparedStatement stmtAccount = conn.prepareStatement(sqlAccount);
                 stmtAccount.setInt(1, userId);
                 ResultSet rsAccount = stmtAccount.executeQuery();
                 Account account = null;
                 if(rsAccount.next()){
                	 account = new Account();
                	 account.setUserId(userId);
                	 account.setBalance(rsAccount.getInt(2));
                	 account.setAccountId(rsAccount.getInt(3));
                	 user.setAccount(account);
                 }else {
                	 return null; 
                 }
                 
                 String sqlTrans="select * from transaction where from_id=? " +
                         " or to_id=?";    
                 PreparedStatement stmtTrans = conn.prepareStatement(sqlTrans);
                 stmtTrans.setInt(1, account.getAccountId());
                 stmtTrans.setInt(2, account.getAccountId());
                 ResultSet rsTrans = stmtTrans.executeQuery();
                 while(rsTrans.next()) {
                	 Transaction transaction = new Transaction();
                	 transaction.setId(rsTrans.getInt(1));
                	 transaction.setDate(rsTrans.getDate(2));
                	 transaction.setValidated(rsTrans.getBoolean(3));
                	 transaction.setFrom(rsTrans.getInt(4));
                	 transaction.setTo(rsTrans.getInt(5));
                	 transaction.setTotal(rsTrans.getInt(6));
                	 user.getAccount().getPreviusTrans().add(transaction);
                 }
                 
                 rs.close();
                 stmt.close();
                 rsAccount.close();
                 stmtAccount.close();
                 conn.close();
                 return user;
             }
             conn.close();
             return null;                                        
         }else{
             return null;
         }
		
	}
	public String newTransaction(Transaction tr) {
		if(tr != null) {
			if(user.getId() == tr.getTo()) {
				//wants to send him/herself money
				return MessageConstants.selfTransaction;
			}
			
			java.util.Date today = new java.util.Date();
	        Date date = new java.sql.Date(today.getTime());        
	        tr.setDate(date);
	        tr.setValidated(false);
	        tr.setFrom(user.getId());
			Connection conn = null;
			PreparedStatement psInsert = null;
			PreparedStatement psUpdateCurrentUser = null;
			try{ 
                conn = getConnection();
				conn.setAutoCommit(false);
                String sqlInsert = "insert into transaction (date, validated,"
                        + "from_id, to_id, total) values(?,?,?,?,?)"; 
				
                psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setDate(1, tr.getDate());
                psInsert.setBoolean(2, false);
                psInsert.setInt(3, user.getId());
                psInsert.setInt(4, tr.getTo());
                psInsert.setInt(5, tr.getTotal());
                psInsert.execute();
                conn.commit();
                
                String sqlUpdateFromUser = "update account set balance = ? "
                		+ "where user_id=?";
                psUpdateCurrentUser = conn.prepareStatement(sqlUpdateFromUser);
                psUpdateCurrentUser.setInt(1, user.getAccount().getBalance() - tr.getTotal());
                psUpdateCurrentUser.setInt(2, user.getId());
                psUpdateCurrentUser.execute();
                conn.commit();
                
                String findToUser = "select balance from account where user_id =?";
                PreparedStatement psFindUser = conn.prepareStatement(findToUser);
                psFindUser.setInt(1, tr.getTo());
                ResultSet rsFindUser = psFindUser.executeQuery();
                int toBalance = 0; 
                if(rsFindUser.next()){ 
                	toBalance = rsFindUser.getInt(1);
                } else {
                	return MessageConstants.noSuchUser;
                }
                
                String sqlUpdateToUser = "update account set balance = ? "
                		+ "where user_id=?";
                PreparedStatement psUpdateToUser = conn.prepareStatement(sqlUpdateToUser);
                psUpdateToUser.setInt(1, toBalance + tr.getTotal());
                psUpdateToUser.setInt(2, tr.getTo());
                psUpdateToUser.execute();
                conn.commit();
                
                return MessageConstants.successTrans;
				
			}catch (SQLException ex){
				try {
					conn.rollback();
					return MessageConstants.unSuccessTransRollback;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return MessageConstants.unSuccessTransNoRollback;
			}finally {
				try {
					psInsert.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return MessageConstants.generalTransError;
		
	} 
	
	public List<Transaction> getUnValidatedTransactions() {
		List<Transaction> result = new ArrayList<Transaction>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{ 
            conn = getConnection();
			conn.setAutoCommit(false);
			String sql="select * from transaction where validated=false";
			
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();       
            
            while(rs.next()){
            	Transaction tr = new Transaction();
            	tr.setId(rs.getInt(1));
            	tr.setDate(rs.getDate(2));
            	tr.setValidated(false);
            	tr.setFrom(rs.getInt(4));
            	tr.setTo(rs.getInt(5));
            	tr.setTotal(rs.getInt(6));
            	result.add(tr);
            	
            }
            
		}catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result; 
	}
	
}

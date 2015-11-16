package com.bank.mb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
	public static void main(String[] args){
		Connection conn = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "root");
			statement = conn.createStatement();
			
			resultSet = statement.executeQuery("select * from employees");
			while(resultSet.next()){
				System.out.println(resultSet.getString("last_name")+" "+resultSet.getString("first_name"));
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.eddiedunn.greek.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.eddiedunn.util.CU;

public class RemoveDotDotDots {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;	 
        ResultSet result = null;
        try {

            	ArrayList<String> deleteStatements = new ArrayList<String>(); 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        result = stmt.executeQuery("SELECT versefilelineid FROM greektext.versefilelines where text like '%. . .%' AND isbasetext=1;");
	        while (result.next())
	            deleteStatements.add("delete from versefilelines where versefilelineid="+result.getInt(1)+";");
	          
	        for(String sql : deleteStatements){
	            stmt.executeUpdate(sql);
	        }
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        result.close();
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	    

    }

}

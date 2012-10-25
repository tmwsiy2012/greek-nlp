package com.eddiedunn.greek.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eddiedunn.util.CU;

public class RemoveRmptySquareBrackets {

    /**
     * @param args
     */
    public static void main(String[] args) {
	getUniqueManuscripts();

    }

    private static SortedMap<String,String> getUniqueManuscripts(){
	SortedMap<String,String> manuscripts = new TreeMap<String,String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;
 
        ResultSet result = null;
        try {

            ArrayList<String> deleteStatements = new ArrayList<String>(); 
            //SortedMap<Integer,String> updateStatements = new TreeMap<Integer,String>();
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();       
	        result = stmt.executeQuery("SELECT versefilelineid,text FROM versefilelines;"); // where text like '%(%';");

	        ArrayList<String> sqlUpdates = new ArrayList<String>();
	        while (result.next() ){
	            String sql = "update versefilelines set text='"+result.getString(2).replaceAll("/", "")+"' where versefilelineid="+result.getInt(1);
	            sqlUpdates.add(sql);
	            
	        }

	        for(String sql : sqlUpdates){
	            System.out.println(sql);
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
        return manuscripts;
    }    
}

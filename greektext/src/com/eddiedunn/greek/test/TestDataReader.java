package com.eddiedunn.greek.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.eddiedunn.greek.data.WordDataReader;
import com.eddiedunn.util.CU;

public class TestDataReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		resetTables();
		WordDataReader wdr = new WordDataReader();
		wdr.readDataFiles();
		
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		PopulateDatabaseFromVerseFileTables pdbfvt = new PopulateDatabaseFromVerseFileTables();
		pdbfvt.runPopulateVerses();

	}
    private static void resetTables(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;

        try {

	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        
	        
	        stmt.executeUpdate("truncate table versefiles;");	      
	        stmt.executeUpdate("truncate table versefilelines;");

	    
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	
    }
}

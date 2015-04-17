package com.eddiedunn.greek.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.SortedMap;
import java.util.TreeMap;



import com.eddiedunn.util.CU;


public class WordDataReader {

    

    private final String dataDirStr = System.getenv("USERPROFILE")+"\\Documents\\PJRawFinal\\";
    

    
    
    public WordDataReader(){
	//readDataFiles();
    }    

	public  void readDataFiles(){
	    int count = 0;
		File dataDir = new File(dataDirStr);
		String[] physicalVerseFiles = dataDir.list();
		System.out.println("processing "+physicalVerseFiles.length+" files");
		for (int i=0; i < physicalVerseFiles.length;i++) {
		//for (String verseFile : verseFiles) {
			if (!physicalVerseFiles[i].equalsIgnoreCase(".git")) {
			    count++;
			    System.out.println("Processing file: "+physicalVerseFiles[i]);
			    if( count % 25 == 0)
				System.out.println("processed "+count+" files");
			    
			    //verseFiles.add(CU.getVerseFile(CU.getWordDocument(dataDirStr+physicalVerseFiles[i]), physicalVerseFiles[i]));
			    populateVerseFile(CU.getVerseFile(CU.getWordDocument(dataDirStr+physicalVerseFiles[i]), physicalVerseFiles[i]));
			}
		}		
	}

	private void populateVerseFile(VerseFile vf){
	    
	    // insert db record for this verse file
	    int vfid = insertVerseFileEntry(vf);
	    vf.setVfid(vfid);
	    insertVerseFileLines(vf);
	    
	}
	private int insertVerseFileEntry(VerseFile vf){
	    int verseID = -1;
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	        } catch (ClassNotFoundException ex) {
	            ex.printStackTrace();
	        }
	        Connection con = null;
	        Statement stmt = null;
	        ResultSet rs = null;
	        try {

		 
		        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
		        stmt = con.createStatement();
		        String sqlToExecute = "INSERT INTO versefiles (documentid,versefilename,chapid,verseid) VALUES(1,'"+vf.getFileName() +"',"+vf.getChapNum()+","+vf.getVerseNum()+");";
		        //System.out.println(sqlToExecute);
		        stmt.executeUpdate(sqlToExecute, Statement.RETURN_GENERATED_KEYS);
		        rs = stmt.getGeneratedKeys();
		        if (rs.next()){
		            verseID=rs.getInt(1);
		        }

		    
		} catch (Exception e) {
		    // TODO: handle exception
		    e.printStackTrace();
		}finally{
		    try {
			rs.close();
		        con.close();	
		    } catch (Exception e2) {
			e2.printStackTrace();
		    }
		    
		}
	        return verseID;
	}

	
	public void insertVerseFileLines(VerseFile vf) {

		
		boolean matchedBaseText = false;

		    //System.out.println(vf.getDocumentText());
		    try {
			    int i=0,lineNumber=0;
			    BufferedReader documentTextReader = new BufferedReader(new StringReader(vf.getDocumentText()));
			    String line = documentTextReader.readLine();				    
			    while( null != line){
				String trimmedLine = line.trim();
				// ignore null, empty,comment and verse/file lines
				if( trimmedLine.length() == 0 || trimmedLine.startsWith("#") || trimmedLine.startsWith("CHAPTER") || CU.checkVerseString(trimmedLine)){
				    line = documentTextReader.readLine();
				    continue;
				}
				lineNumber++;
				
				boolean isBaseText = false;
				if( i < vf.getUnderlinedText().size() && trimmedLine.equals(vf.getUnderlinedText().get(i)) ){
				    isBaseText = true;
				    matchedBaseText = true;
				    //System.out.println("found base text: *"+line.trim()+"* line: "+lineNumber);
				    i++;
				}
				// write verseFileLine into DB
				//writeVerseLine
				insertVerseFileLine(vf.getVfid(),isBaseText,trimmedLine);
				
				// reset isBaseText for good measure :P
				isBaseText = false;
				
				line = documentTextReader.readLine();
			    }	
			    if( lineNumber == 0){
				System.err.println("Problem with file: "+vf.getFileName()+" no data was read.. Exiting");
				System.exit(0);
			    }
		    } catch (Exception e) {
			e.printStackTrace();
		    }

		if( ! matchedBaseText ){
			System.err.println("Problem with file: "+vf.getFileName()+" no base texts were matched.. Exiting");
			System.exit(0);
		}
		   
		
	}
	private void insertVerseFileLine(int vfid, boolean isBaseText, String lineText){
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
		        stmt.executeUpdate("INSERT INTO versefilelines (documentid,versefileid,isbasetext,text) VALUES(1,"+vfid +","+isBaseText+",'"+lineText+"');");


		    
		} catch (Exception e) {
		    // TODO: handle exception
		    e.printStackTrace();
		}finally{
		    try {
		        con.close();	
		    } catch (Exception e2) {
			e2.printStackTrace();
		    }
		    
		}	    
	}
	private SortedMap<String, String> getFamilies(){
	    SortedMap<String, String> returnValue = new TreeMap<String, String>(); 
	    
		File content = new File(System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\manuscriptIDtable.txt");
		String str = "";

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					content), "UTF8"));

			while ((str = in.readLine()) != null) {
			    String[] dataLine = str.split(",");
			    if( dataLine.length >= 2 )
				returnValue.put(dataLine[0].trim(), dataLine[1].trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e2) {
				System.err.println("Error: " + e2.getMessage());
			}
		}	    
	    
	    return returnValue;
	}	
	
	private void test(){
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

		 
		        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
		        stmt = con.createStatement();
		        result = stmt.executeQuery("SELECT * FROM document");
		        while (result.next())
		            System.out.println(result.getString(1) + " " + result.getString(2));
		    
		} catch (Exception e) {
		    // TODO: handle exception
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

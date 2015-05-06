package com.eddiedunn.greek.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import com.eddiedunn.greek.data.VerseFileLine;
import com.eddiedunn.util.CU;

public class PopulateDatabaseFromVerseFileTables {

    /**
     * @param args
     */
    public static void main(String[] args) {
		
    	runPopulateVerses();
	
    }   
    public static void runPopulateVerses(){
    	// successful outcome requires this reset to be ran each time!
    	resetTables();
    	
    	// populate manuscript table and return map of manuscript directory names and db key values
    	SortedMap<String,Integer> manuscriptNamesToManuscriptIDs =  populateManuscripts(getUniqueManuscripts());
    	
    	
    	
    	// populate chapter table and return map of chapterNumbers and a map of <Integer,Integer> containing manuscriptids and chapterids corresponding to chapternumber in key 
    	//SortedMap<Integer, SortedMap<Integer,Integer>> chapterNumbersToManuscriptIDsToChapterIDs = populateChapters(manuscriptNamesToManuscriptIDs);

    	
    	// populate verse table
    	populateVerses(manuscriptNamesToManuscriptIDs);    	
    }
    private static void populateVerses(SortedMap<String,Integer> manuscriptNamesToManuscriptIDs){
	

	for (int chapter = 1; chapter <= 25; chapter++) {
		System.out.println("Starting Chapter: "+chapter);
	    int numVerses = getNumVerses(chapter);
	    // get map of manuscriptids to chapterids for this chapter

	    //System.out.println(manuscriptIDsToChapterIds);
	    for(int verse = 1; verse<=numVerses;verse++){
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO: handle exception
			}

	    // now get versefilelines for this chapter and verse
	    ArrayList<VerseFileLine> verseFileLines = getVerseFileLines(chapter,verse);
	    
	    SortedMap<String, StringBuffer> manuscriptNameToVerseText = new TreeMap<String, StringBuffer>();
	    

	    for(VerseFileLine fl : verseFileLines){

			String textToApply = fl.getText();

			if( ! "[]".equals(textToApply) && ! "-".equals(textToApply)){
/*				if (fl.isAddition()){
					textToApply = fl.getCurrentBaseText() + " " + fl.getText();
				}
				// if we have a number line and previous was base text
				// then  we can grab the base text and if not IJ add bodmer to the list
				else*/ if ( textToApply.length() == 0  && fl.isPrevBaseText()){
					textToApply = fl.getCurrentBaseText();
					if ( ! CU.isIJ(chapter, verse))
						fl.addManuscript("031");
					//System.out.println("number only line: "+fl.getVerseFileLineid());
				}					
				//TODO: Need to handle case where there is bodmer no number line
				else if( textToApply.length() > 0 && fl.isPrevBaseText() && ! CU.isIJ(chapter, verse)){
					// need to add bodmer by itself?
					if (manuscriptNameToVerseText.containsKey("031")) {
						manuscriptNameToVerseText.get("031").append(fl.getCurrentBaseText() +' ');
					} else {
						manuscriptNameToVerseText.put("031", new StringBuffer());
						manuscriptNameToVerseText.get("031").append(fl.getCurrentBaseText() + ' ');
					}				
				}
/*				System.out.println("***************************************************");
				System.out.println("processing: chap:" + fl.getChapter() + " verse:" + fl.getVerse() + " vflid: " + fl.getVerseFileLineid());
				System.out.println("isBaseText? "+fl.isBaseText());
				System.out.println("isPrevBaseText? "+fl.isPrevBaseText());
				System.out.println("textToApply: *" + textToApply + "*");
				System.out.println("currentBase: "+ fl.getCurrentBaseText());
				System.out.print("manuscriptsToApply: " );
				CU.printStringArray(fl.getManuScriptNumbers());	*/
				
				
				
				for (String manuscriptNumber : fl.getManuScriptNumbers()) {
					if (manuscriptNameToVerseText.containsKey(manuscriptNumber.trim())) {
						//System.out.println("Manuscript: "+ manuscriptNumber.trim() +" Adding: "+textToApply);
						//System.out.println("To: "+manuscriptNameToVerseText.get(manuscriptNumber.trim()).toString());
						manuscriptNameToVerseText.get(manuscriptNumber.trim()).append(textToApply +' ');
					} else {
						manuscriptNameToVerseText.put(manuscriptNumber.trim(), new StringBuffer());
						//System.out.println("Manuscript: "+ manuscriptNumber.trim() +" Adding: "+textToApply+ " TO NEW");
						manuscriptNameToVerseText.get(manuscriptNumber.trim()).append(textToApply + ' ');
					}
				}
				//System.out.println("***************************************************");				
				}
	    }
	    
	    

	    // sanity check for tell tale bad chars indicating error
	    Pattern pattern = Pattern.compile(CU.checkForNumberOrEngChar);
	    Pattern pattern1 = Pattern.compile(CU.checkForNotNumbersOrc);
	    for( Map.Entry<String, StringBuffer> m : manuscriptNameToVerseText.entrySet()){
		Matcher matcher = pattern.matcher(m.getValue());
		Matcher m2 = pattern1.matcher(m.getKey());
		if( matcher.matches() )
		    System.out.println("english char or num in chap "+chapter+": "+m.getKey()+" "+m.getValue());
		
		if(m2.matches()){
		    System.out.println("non number or c in manuscript keys in chap:"+chapter+" verse: "+verse+" key: "+m.getKey());
		}
		
	    }
	    

		
	    // now we can insert verse records for each manuscript
	   // System.out.println(manuscriptNamesToManuscriptIDs);
	    for( Map.Entry<String, StringBuffer> m : manuscriptNameToVerseText.entrySet()){
			// need to get chapterid for this manuscript and chapter
			if( m.getValue().toString().length() > CU.minVerseLength){
			if( ! manuscriptNamesToManuscriptIDs.containsKey(m.getKey()) ){
			 
			    System.out.println("problem: *"+m.getKey()+"* chap: "+chapter+" verse: "+verse);
			    System.out.println(m.getValue().toString());
			}
			int currentManuscriptID = manuscriptNamesToManuscriptIDs.get(m.getKey());
			
	
			//System.out.println(m.getKey()+" manscrpid: "+currentManuscriptID+" chapterid: "+chapterid);
			insertVerseRecord(currentManuscriptID,chapter,verse,m.getValue().toString().replaceAll(" +", " ") );
		    }
	    }
	    } // end verse loop
	} // end chapter loop
	System.out.println("made it");
    }

    private static void insertVerseRecord(int manuscriptid,int chapternumber, int verseNumber,String verse_text){
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
	        
	        
	        stmt.executeUpdate("insert into verse(manuscriptid,chapternumber,versenumber,verse_text) values("+manuscriptid+","+chapternumber+","+verseNumber+",'"+verse_text+"');");

	    
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
    private static ArrayList<VerseFileLine> getVerseFileLines(int chapter, int verse){
	ArrayList<VerseFileLine> returnValue = new ArrayList<VerseFileLine>();
	Pattern newFormatBlock = Pattern.compile(CU.findLinesInNewFormatRegEx);

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
	        
	        
	        rs = stmt.executeQuery("select vfl.versefilelineid,vf.chapid,vf.verseid,vfl.isbasetext,vfl.text,vfl.ordertoprocess,vfl.prevbasetext from versefilelines vfl inner join versefiles vf on vfl.versefileid=vf.versefileid where vf.chapid="+chapter+" AND vf.verseid="+verse+" order by vfl.versefilelineid;");
	        String currentBaseText = "";
	        while(rs.next()){

	        	if( rs.getBoolean(4) ) { // is baseText 
	        		currentBaseText = rs.getString(5);
	        		// if it is not IJ then add

	        		//System.out.println("currentBaseText: "+currentBaseText);
	        		
	        	}else{ // not baseText
	        		Matcher newFormatBlocksMatcher = newFormatBlock.matcher(rs.getString(5));	        		
	        		if(newFormatBlocksMatcher.find() ){	  
	        			System.out.println("found weird block versefilelineid: "+rs.getInt(1)+" string: "+rs.getString(5));
	        			continue;	        			
	        		}
	        		
		        	String[] splitLine = splitLine(rs.getString(5)); 
		        	// if prevBaseText  then add bodmer to list
		        	// if prevBaseTExt and splitLine[0].length == 0 then
		        	returnValue.add(new VerseFileLine(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getBoolean(4), rs.getBoolean(7), splitLine[0],splitLine[1], currentBaseText));
	        	}
	            
	        }


	    
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	

	return returnValue;
    }
	private static String[] splitLine(String lineToSplit) {
		// System.out.println("splitline: "+lineToSplit);
		// find first index of a number character, decrement index by 1 and
		// split the line there
		String[] returnValue = new String[2];
		
		
		for (int i = 0; i < lineToSplit.length(); i++) {
			if (lineToSplit.substring(i, i + 1).matches("[0-9]")) {				
				returnValue[0] = lineToSplit.substring(0, i).trim();
				returnValue[1]= lineToSplit.substring(i, lineToSplit.length()).trim();
				break;
			}
		}
		return returnValue;
	}    
    private static int getNumVerses(int chapNum){
	int returnValue=-1;
	
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
	        result = stmt.executeQuery("select max(vf.verseid) as numVerses from versefilelines vfl inner join versefiles vf on vfl.versefileid=vf.versefileid where vf.chapid="+chapNum+";");
	        result.next();
	        

	        returnValue = result.getInt(1);

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
        return returnValue;
    }
    private static SortedMap<Integer, SortedMap<Integer,Integer>> populateChapters(SortedMap<String,Integer> manuscriptIDs){
	SortedMap<Integer, SortedMap<Integer,Integer>> returnValue = new TreeMap<Integer, SortedMap<Integer,Integer>>();
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
	        int chapid=1;
	        for( int i=1; i<=25; i++){
	            SortedMap<String,String> chapManuscripts = getUniqueManuscriptsChapter(i);
	            SortedMap<Integer,Integer> tmpChaps = new TreeMap<Integer,Integer>();
	            
	            for(Map.Entry<String, String> m : chapManuscripts.entrySet()){
	        	int manuscriptid = manuscriptIDs.get(m.getKey().trim());
	        	String sql = "INSERT INTO chapter(documentid,manuscriptid,chapternumber) VALUES(1,"+manuscriptid+","+ i +")";
	        	//System.out.println(sql);
	        	stmt.executeUpdate(sql);
	        	tmpChaps.put(manuscriptid, chapid);
	        	chapid++;
	            }
	            returnValue.put(i, tmpChaps);
	        }
	        
		//for(Map.Entry<String, String> m : globalManuscripts.entrySet()){
		    //System.out.println(m);
		//    
		//}	        
	        
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	
        return returnValue;
    }    
    private static SortedMap<String,Integer>  populateManuscripts(SortedMap<String,String> globalManuscripts){
	SortedMap<String,Integer> returnValue = new TreeMap<String,Integer>();
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
	        int manuscriptid=0;
		for(Map.Entry<String, String> m : globalManuscripts.entrySet()){
		    manuscriptid++;
		    returnValue.put(m.getKey().trim(), new Integer(manuscriptid));
		    //System.out.println(m);
		    stmt.executeUpdate("INSERT INTO manuscript(docid,name) VALUES(1,'"+m.getKey().trim()+"')");
		}	        
	        
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	
        return returnValue;
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
        ResultSet rs = null;	 
        ResultSet result = null;
        try {

	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        result = stmt.executeQuery("SELECT text FROM versefilelines");
	        StringBuffer buf = new StringBuffer();
	        while (result.next() ){
	            buf.append(result.getString(1)+" ");
	           //System.out.println(result.getString(1));
	        }
	        
	        Pattern pattern = Pattern.compile(CU.numberTokenMatch);       
	        Matcher matcher = pattern.matcher(buf.toString());
	        
	        while(matcher.find()){	          
	           ArrayList<String> tmpscriptNums = new ArrayList<String>();
	           String token = matcher.group().trim();
	           //System.out.println(token);
	               if( (token.endsWith("c")&& token.length() == 3) || token.length() == 2  ){
	        	   
	        	   token = "0"+token;
	        	   //System.out.println(token);
	               }

	               tmpscriptNums.add(token.trim());
	           
	            for( String m : tmpscriptNums){        		        	
		            if( ! manuscripts.containsKey(m.trim()) )
		        	manuscripts.put(m.trim(),m.trim() );
		            //System.out.println(matcher.group());	        	
	            }
	            //manually add in bodmer
	            manuscripts.put("031", "031");
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
    private static SortedMap<String,String> getUniqueManuscriptsChapter(int chapNum){
	SortedMap<String,String> manuscripts = new TreeMap<String,String>();
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
	        result = stmt.executeQuery("SELECT vfl.text FROM greektext.versefilelines vfl INNER JOIN greektext.versefiles vf on vf.versefileid=vfl.versefileid where  vf.chapid="+chapNum+";");
	        StringBuffer buf = new StringBuffer();
	        while (result.next() ){
	            buf.append(result.getString(1)+" ");
	           // System.out.println(result.getString(1));
	        }
	        
	        Pattern pattern = Pattern.compile(CU.numberTokenMatch);       
	        Matcher matcher = pattern.matcher(buf.toString());
	        
	        while(matcher.find()){	          
	           ArrayList<String> tmpscriptNums = new ArrayList<String>();
	           String token = matcher.group().trim();
	        	   
	               if( ((token.contains("c")&& token.length() == 3))  || token.length() == 2  ){
	        	   
	        	   token = "0"+token;
	        	   //System.out.println(token);
	               }
	               tmpscriptNums.add(token);
	           
	            for( String m : tmpscriptNums){
	        		        	
		            if( ! manuscripts.containsKey(m.trim()) )
		        	manuscripts.put(m.trim(),m.trim() );
		            //System.out.println(matcher.group());	        	
	            }

	        }
	        //System.out.println("manuscripts size: "+manuscripts.size());

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
	        
	        
	        stmt.executeUpdate("truncate table manuscript;");	      
	        stmt.executeUpdate("truncate table verse;");

	    
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

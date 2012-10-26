package com.eddiedunn.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.eddiedunn.greek.data.Manuscript;
import com.eddiedunn.greek.data.VerseFile;

public class CU {


	public static final String newline = System.getProperty("line.separator");
	public static final String selectAllManuscriptsSQL = "SELECT manuscriptid,name FROM manuscript where name not like '%c%' order by name";
	public static final String selectOldManuscriptsSQL = "SELECT manuscriptid,name FROM manuscript where name in ('031','041','061','081','083','091','092','093','094','001','002','003','004','005','006','101','102','103','104','105','106','107','108','109','110','111','112','113','114','115','116','117','118') order by name";
	
	public static final String db_connstr = "jdbc:mysql://localhost:3306/greektext";
	public static final String db_username = "tmwsiy";
	public static final String db_password = "password";
	public static final String findLinesInNewFormatRegEx = "[0-9]c*\\s*\\)|[0-9]c*\\s*\\]";
	public static final String metadataParenBlocks= "\\([^0-9\\(]+\\)";
	//public static final String metadataSquareBracketBlocks= "\\[[^0-9\\[]+\\]";
	public static final String metadataSquareBracketBlocks= "\\[[^\\[]+?\\]";
	public static final String checkForNumberOrEngChar = "[0-9a-zA-Z]+";
	public static final String checkForForwardSlash = "[\\/]+";
	public static final String checkForTeePee = "\\/.+?\\\\";
	public static final String checkForNotNumbersOrc = "[^0-9c]+";
	//public static final String numberTokenMatch = "\\s+[0-9]+\\s+|\\s+[0-9]+c\\s+|\\s+[0-9]+-[0-9]+\\s+|\\s+[0-9]+-[0-9]+c\\s+|\\s+[0-9]c+-[0-9]+c\\s+|\\s+[0-9]+-[0-9]+\\s+|\\s+[0-9]+$|\\s+[0-9]c$";
	public static final String numberTokenMatch = "\\s+[0-9]{2,3}c{0,1}\\s+|\\s+[0-9]{2,3}c{1}-[0-9]{2,3}c{0,1}\\s+";
	public static final int minVerseLength=3;
	public static final int minDocumentLength=75;

	public static final   int ngramMax=5; // 1 inherent min
	public static final  int chargramMin=2;
	public static final  int chargramMax=8;
	public static final int grandCompositeMinCount=1;
	public static final int grandCompositeMaxOffset=0;
	public static String Rexecutable= "C:\\Program Files\\R\\R-2.13.1\\bin\\x64\\Rscript.exe";

	public static  void writeMatrixToFile(double[][] matrixToWrite, String fileName){
		BufferedWriter out = null;
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
                //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
			for (int k = 0; k < matrixToWrite.length; k++) {
				StringBuffer line= new StringBuffer();
				for (int l = 0; l < matrixToWrite[0].length; l++) {
					line.append(matrixToWrite[k][l]+",");
				}
				out.write(line.substring(0, line.length()-1)+CU.newline);
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}		
	}	
	public static HWPFDocument getWordDocument(String fileName){
		HWPFDocument doc = null;
		try {
			//String fileName = System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\test\\PJ 06\\BDGZ 6 rev\\BDGZ 6 09\\BDGZ06 09 verse.doc";
			FileInputStream fis = new FileInputStream(fileName);
			POIFSFileSystem fs = new POIFSFileSystem(fis);
	        doc = new HWPFDocument(fs);			        
		}    catch(IOException e2){
	    	e2.printStackTrace();
	    }
		return doc;
	}
	public static VerseFile getVerseFile( HWPFDocument doc, String fileName){
		WordExtractor we = new WordExtractor(doc);
		VerseFile returnValue = new VerseFile();
		ArrayList<String> baseTexts = new ArrayList<String>();
		try{

      	
        for (int i = 0; i < we.getText().length() - 2; i++) {
            int startIndex = i;
            int endIndex = i + 1;
            Range range = new Range(startIndex, endIndex, doc);
            //System.out.println(we.getText().length()+" "+startIndex+" "+endIndex);
            CharacterRun cr = range.getCharacterRun(0);
            
            	//System.out.println("cr.text() "+cr.text()+ " : "+(int)cr.text().charAt(0));

            if (cr.getUnderlineCode() != 0) {
                while ((cr.getUnderlineCode() != 0) && (13 != (int)cr.text().charAt(0))) {
                    i++;
                    endIndex += 1;
                    range = new Range(endIndex, endIndex + 1, doc);
                    cr = range.getCharacterRun(0);
                }
                if(13 == (int)cr.text().charAt(0) )
                	range = new Range(startIndex, endIndex, doc);
                else
                	range = new Range(startIndex, endIndex - 1, doc);
               // System.out.println("*"+range.text()+"*");
                if( range.text().length() > 2)
                	baseTexts.add(range.text());
            } 

        }
	// This is not the best but this block should mimic the IllegalArgumentException catch block
	// below it hits there more often than not but it does what is necessary        
        int k=0;
        String verse = baseTexts.get(0);
        while( !CU.checkVerseString(verse)){
            System.out.println(verse);
            k++;
            verse = baseTexts.get(k);
        }
           System.out.println(verse);
           System.out.println("basetexts size before remove: "+baseTexts.size());
           ArrayList<String> realBaseTexts = new ArrayList<String>();
        for (int i = k+1; i < baseTexts.size(); i++) {
            realBaseTexts.add(baseTexts.get(i));
	}
        System.out.println("basetexts size after: "+realBaseTexts.size());
        for (int i = 0; i < realBaseTexts.size(); i++) {
	    System.out.println("*"+realBaseTexts.get(i)+"*");
	}
                
        returnValue = new VerseFile(verse, realBaseTexts, we.getText(),fileName);
       
    }
    catch (IndexOutOfBoundsException iobe) {
       iobe.printStackTrace();
    }
    catch(IllegalArgumentException e1){
    	//e1.printStackTrace();
	// This is not the best but this block should mimic the last bit of the associated try block
	// it hits here more often than not but it does what is necessary
        int k=0;
        String verse = baseTexts.get(0);
        while( !CU.checkVerseString(verse)){
           System.out.println(fileName);
            k++;
            verse = baseTexts.get(k);
        }
          // System.out.println(verse);
           //System.out.println("basetexts size before remove: "+baseTexts.size());
           ArrayList<String> realBaseTexts = new ArrayList<String>();
        for (int i = k+1; i < baseTexts.size(); i++) {
            realBaseTexts.add(baseTexts.get(i).trim());
	}
        //System.out.println("basetexts size after: "+realBaseTexts.size());
       // for (int i = 0; i < realBaseTexts.size(); i++) {
	//    System.out.println("*"+realBaseTexts.get(i)+"*");
	//}
                
        returnValue = new VerseFile(verse, realBaseTexts, we.getText(),fileName);
    }
		
		 return returnValue;
	}
public static boolean checkVerseString(String stringToCheck){
    boolean returnvalue = false;
    String[] tmp = stringToCheck.trim().split(":");
    if( tmp.length == 2 )
	returnvalue = true;
    
    return returnvalue;
}
	public static  void writeMatrixToFileWithHeaders(double[][] matrixToWrite,String[] colNames,String[] rownames, String fileName){
		BufferedWriter out = null;
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
            //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        StringBuffer headerLine = new StringBuffer();
	        headerLine.append("    ");
	        for (int i = 0; i < colNames.length; i++) {
		    //headerLine.append(colNames[i]+",");
	            headerLine.append((i+1)+",");
		}
	        
	        headerLine.append(CU.newline);
	        out.write(headerLine.toString());
			for (int k = 0; k < matrixToWrite.length; k++) {
				StringBuffer line= new StringBuffer();
				line.append(rownames[k]+",");
				for (int l = 0; l < matrixToWrite[0].length; l++) {
					line.append(matrixToWrite[k][l]+",");
				}
				out.write(line.substring(0, line.length()-1)+CU.newline);
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}		
	}	
	public static  void writeCountMapToFile(SortedMap<String, Integer> mapToWrite, String fileName){
		BufferedWriter out = null;
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
                //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
	        for (SortedMap.Entry<String, Integer> o : mapToWrite.entrySet()) {
	        	out.write(o.getKey()+","+o.getValue().intValue()+"\n");
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}		
	}
	public static  void writeManuscriptMapToFile(SortedMap<Integer, String> mapToWrite, String fileName){
		BufferedWriter out = null;
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
            //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
	        for (SortedMap.Entry<Integer, String> o : mapToWrite.entrySet()) {
	        	out.write(o.getKey()+","+o.getValue()+"\n");
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}		
	}		
	
	public static  void writeVectorToFile(String[] vectorToWrite, String fileName){
		BufferedWriter out = null;
		StringBuffer buf = new StringBuffer();
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
            //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
	        for(String s : vectorToWrite){
	        	buf.append(s+",");
	        }
	        out.write(buf.substring(0, buf.length()-1)+CU.newline);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}		
	}		
	public static void printStringArrayList(ArrayList<String> list){
		for(String str:list){
			System.out.print(str+" ");
		}
		System.out.println();
	}
	public static double[][] getNewMatrix(int size){
		double[][] matrix = new double[size][size];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				matrix[i][j]=0;
			}
		}		
		return matrix;
	}
	public static  String[] readManuscriptNames(boolean onlyOld){
		ArrayList<String> tmp = new ArrayList<String>();
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
	        String sql = selectAllManuscriptsSQL;
	        if( onlyOld )
	            sql = selectOldManuscriptsSQL;
	        result = stmt.executeQuery(sql);
	        
	        while (result.next()){
	        	if( checkManuscriptLength(result.getInt(1)))
	        		tmp.add(result.getString(2));
	        }
	            //System.out.println(result.getString(1) + " " + result.getString(2));
	    
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
        return tmp.toArray(new String[0]);
	}		
	public static boolean checkManuscriptLength(int manuscriptid){
		boolean returnValue = false;
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
	        result = stmt.executeQuery("select m.name,v.verse_text,v.chapternumber,v.versenumber from verse v inner join manuscript m on m.manuscriptid=v.manuscriptid where v.manuscriptid="+manuscriptid+" order by v.chapternumber,v.versenumber;");
	        StringBuffer buf = new StringBuffer();
	        String name = null;
	        while(result.next()){
	        	buf.append(result.getString(2)+' ');
	        	name = result.getString(1);
	        }
	        if( name != null && buf.toString().length() > CU.minDocumentLength)
	            returnValue =true;
	    
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
	public static  double[][] readMatrixFromFile(String fileName, int size){
		double[][] returnValue = new double[size][size];
		BufferedReader in = null;
		try {
	        in = new BufferedReader(new InputStreamReader
	        		(new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
                    //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
			for (int k = 0; k < size; k++) {				
				String line=in.readLine();
				
				String[] tokens = line.split(",");
				for (int i = 0; i < tokens.length; i++) {
					//System.out.println(k+": "+i+" tokens.length: "+tokens.length);
					returnValue[k][i] = (new Double(tokens[i])).doubleValue();
				}
				
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (Exception e2) {}
		}		
		return returnValue;
	}	



	public static void printMatrix(double[][] matrixToPrint){
		for (int k = 0; k < matrixToPrint.length; k++) {
			String line="";
			for (int l = 0; l < matrixToPrint[0].length; l++) {
				line += matrixToPrint[k][l]+",";
			}
			System.out.println(line.substring(0, line.length()-1));
		}
	}
	
}

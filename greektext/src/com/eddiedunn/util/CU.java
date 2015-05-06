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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.eddiedunn.greek.data.VerseFile;






public class CU {

	public static final String IJ = "18:03 18:04 18:05 18:06 18:07 18:08 18:09 18:10 18:11 18:12 19:02 19:03 19:04 19:05 19:06 19:07";
	public static final String newline = System.getProperty("line.separator");
	private static final String outliers = "'001','002','041','061','083','091','092','094','101','104','106','107','110','205','216','217','301','303','310','315','401','404','405','504','506','610','611','613','618','620','621','628','631','704','807'";
	private static final String outlierHalf = "'001','002','003','004','005','006','041','061','081','083','091','092','093','094','101','103','104','105','106','107','108','110','114','202','203','205','207','213','216','217','301','302','303','304','305','309','310','315','401','403','404','405','411','503','505','504','506','507','510','605','607','610','611','613','614','618','620','621','625','626','628','631','704','802','807'";
	public static final String selectAllManuscriptsRemoveOutliersSQL = "SELECT manuscriptid,name FROM manuscript where name not like '%c%' AND name NOT IN ("+outliers+") order by name";
	public static final String selectAllManuscriptsMostCorrolatedHalfSQL = "SELECT manuscriptid,name FROM manuscript where name not like '%c%' AND name NOT IN ("+outlierHalf+") order by name";
	public static final String selectAllManuscriptsONLYOutliersSQL = "SELECT manuscriptid,name FROM manuscript where  name IN ("+outliers+",'031') AND name NOT IN ('807') order by name";
	public static final String selectAllManuscriptsLeastCorrolatedHalfSQL = "SELECT manuscriptid,name FROM manuscript where  name IN ("+outlierHalf+",'031') AND name NOT IN ('807') order by name";
	public static final String selectAllManuscriptsSQL = "SELECT manuscriptid,name FROM manuscript where name not like '%c%' AND name NOT IN ('807') order by name";
	public static final String selectOldManuscriptsSQL = "SELECT manuscriptid,name FROM manuscript where name in ('031','041','061','081','083','091','092','093','094','001','002','003','004','005','006','101','102','103','104','105','106','107','108','109','110','111','112','113','114','115','116','117','118') order by name";
	public static final String selecttestSQL = "SELECT manuscriptid,name FROM manuscript where name in ('031','041','061','081','083','091','092','093','094') order by name";
	
	public static final String db_connstr = "jdbc:mysql://localhost:3306/greektext";
	public static final String db_username = "tmwsiy";
	public static final String db_password = "password";
	public static final String findLinesInNewFormatRegEx = "[0-9]c*\\s*\\)|[0-9]c*\\s*\\]";
	public static final String metadataParenBlocks= "\\([^0-9\\(]+\\)";
	//public static final String metadataSquareBracketBlocks= "\\[[^0-9\\[]+\\]";
	//public static final String metadataSquareBracketBlocks= "\\[[^\\[]+?\\]";
	public static final String metadataSquareBracketBlocks= "\\[+.*\\]+";
	public static final String checkForNumberOrEngChar = "[0-9a-zA-Z]+";
	public static final String checkForForwardSlash = "[\\/]+";
	public static final String checkForTeePee = "\\/.+?\\\\";
	public static final String checkForNotNumbersOrc = "[^0-9c]+";
	//public static final String numberTokenMatch = "\\s+[0-9]+\\s+|\\s+[0-9]+c\\s+|\\s+[0-9]+-[0-9]+\\s+|\\s+[0-9]+-[0-9]+c\\s+|\\s+[0-9]c+-[0-9]+c\\s+|\\s+[0-9]+-[0-9]+\\s+|\\s+[0-9]+$|\\s+[0-9]c$";
	public static final String numberTokenMatch = "[0-9]{2,3}c{0,1}";
	public static final int minVerseLength=3;
	public static final int minDocumentLength=75;
	public static final int minChapLength=10;

	public static final   int ngramMax=2; // 1 inherent min
	public static final  int chargramMin=2;
	public static final  int chargramMax=5;
	//public static final int grandCompositeMinCount=1;
	//public static final int grandCompositeMaxOffset=0;
	public static String Rexecutable= "C:\\Program Files\\R\\R-2.13.1\\bin\\x64\\Rscript.exe";

/*	public static void pruneMap(SortedMap<String, Integer> grams){
		
		// sort by string length
		ArrayList<String> bigToSmall = new ArrayList<String>();
		String biggest = "";
		int gramCount=0;
		int subGramCount=0;		
		for(String str : grams.keySet()){
			if( str.length() > biggest.length())
				biggest = str;
			bigToSmall.add(str);
		}
		TreeMapStringLengthIntegerValueComparator  comp = new TreeMapStringLengthIntegerValueComparator(biggest);
		java.util.Collections.sort(bigToSmall, comp);		

		for(String str: bigToSmall){
			if( str.length() > 1){
				for(int endIndex=str.length()-1; endIndex>=2;endIndex--){
					if( grams.containsKey(str.substring(0, endIndex)) && grams.containsKey(str) ){
						gramCount=grams.get(str).intValue();
						subGramCount=grams.get(str.substring(0, endIndex)).intValue() ;						
							if( subGramCount >= gramCount) {				
								grams.remove(str.substring(0, endIndex));		
								if(subGramCount > gramCount){
									//System.out.println("re add triggered token: *"+str.substring(0, endIndex)+"* this gram count: "+subGramCount+" supergram count: "+gramCount);
									grams.put(str.substring(0, endIndex), new Integer(subGramCount-gramCount));
								}
							}
					}
				}
			}else { // remove single chars 
				grams.remove(str);
			}
		}
		
		
	}	
	
	// maybe one day add ngram version of this
	public static void pruneMapWithoutReAdd(SortedMap<String, Integer> grams){
		
		// sort by string length
		ArrayList<String> bigToSmall = new ArrayList<String>();
		String biggest = "";
		int gramCount=0;
		int subGramCount=0;		
		for(String str : grams.keySet()){
			if( str.length() > biggest.length())
				biggest = str;
			bigToSmall.add(str);
		}
		TreeMapStringLengthIntegerValueComparator  comp = new TreeMapStringLengthIntegerValueComparator(biggest);
		java.util.Collections.sort(bigToSmall, comp);		

		for(String str: bigToSmall){
			if( str.length() > 1){
			for(int endIndex=str.length()-1; endIndex>=2;endIndex--){
				if( grams.containsKey(str.substring(0, endIndex)) && grams.containsKey(str) ){
					gramCount=grams.get(str).intValue();
					subGramCount=grams.get(str.substring(0, endIndex)).intValue() ;						
						if(subGramCount == gramCount) {				
							grams.remove(str.substring(0, endIndex));		
						}
				}
			}						
			}else { // remove single chars 
				grams.remove(str);
			}
		}
		
		
	}*/	
	
	public static boolean isIJ(int chap, int verse){
		String[] ij = CU.IJ.split(" ");
		for (String v : ij){
			String[] chap_verse = v.split(":");
			int c1 = Integer.valueOf(chap_verse[0]);
			int v1 = Integer.valueOf(chap_verse[1]);
			if ( c1 == chap && v1==verse )
				return true;
		}
		return false;
	}	
    public static void mergeMapCount(SortedMap<String, Integer> original,
    	    SortedMap<String, Integer> oneToAdd) {
    	for (Map.Entry<String, Integer> o : oneToAdd.entrySet()) {
    	    if (original.containsKey(o.getKey())) {
    		int tmp = original.get(o.getKey()).intValue()
    			+ 1;
    		original.put(o.getKey(), new Integer(tmp));
    	    } else {
    		original.put(o.getKey(), new Integer(1));
    	    }
    	}
        }
    public static void mergeMapSum(SortedMap<String, Integer> original,
    	    SortedMap<String, Integer> oneToAdd) {
    	for (Map.Entry<String, Integer> o : oneToAdd.entrySet()) {
    	    if (original.containsKey(o.getKey())) {
    		int tmp = original.get(o.getKey()).intValue()
    			+ oneToAdd.get(o.getKey()).intValue();
    		original.put(o.getKey(), new Integer(tmp));
    	    } else {
    		original.put(o.getKey(), oneToAdd.get(o.getKey()));
    	    }
    	}
        }     
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
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}	
	public static XWPFDocument getWordDocument(String fileName){
		XWPFDocument doc = null;
		try {
			//String fileName = System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\test\\PJ 06\\BDGZ 6 rev\\BDGZ 6 09\\BDGZ06 09 verse.doc";
			//System.out.println(fileName);
			FileInputStream fs = new FileInputStream(fileName);
			//POIFSFileSystem fs = new POIFSFileSystem(fis);
	        doc = new XWPFDocument(fs);			        
		}    catch(IOException e2){
	    	e2.printStackTrace();
	    }
		return doc;
	}
	public static VerseFile getVerseFile( XWPFDocument doc, String fileName){
		List<XWPFParagraph> paragraphs =  doc.getParagraphs();
		String chap  = fileName.split(" ")[1];
		String verse = fileName.split(" ")[2].substring(0, 2);
		String chap_verse = chap+":"+verse;
		//System.out.println(chap+":"+verse);
		
		boolean isIJoseph = false;
		if ( IJ.contains(chap_verse)){
			isIJoseph = true;
			//System.out.println("FOUND IJ doc");
		}
			
		String fullText = new String();
		ArrayList<String> baseTextLines = new ArrayList<String>();
		for (XWPFParagraph para : paragraphs){
			List<XWPFRun> runs =  para.getRuns();
			boolean bold =false;
			boolean underlined = false;
			ArrayList<String> paraLine = new ArrayList<String>();
			if( runs != null ) {
				for( XWPFRun r : runs ){
					UnderlinePatterns patt = r.getUnderline();
					if ( "SINGLE".equals(patt.toString())) {
						underlined = true;
					}
					if( r.isBold() ){
						bold=true;
					}
					
					paraLine.add(r.getText(0));
					//System.out.print(r.getText(0));
				}
				
				//System.out.println();
			
			}
			String lineToAdd = new String();

			if( ! isIJoseph ) { // "normal" verse file (no underlined base text)
				if ( ! underlined ) {
					for (String str : paraLine){
						lineToAdd= lineToAdd+str;
						//System.out.print(str);
					}
					if ( bold ){
						baseTextLines.add(lineToAdd.trim());
						//System.out.print("BASE: ");
					}			
					//System.out.println(lineToAdd);
//					if( lineToAdd.contains("[") ){
//						System.out.println("B: "+lineToAdd.trim());
//						System.out.println("A: "+lineToAdd.trim().replaceAll("\\[([^\\]]+)]+", ""));
//					}					
					fullText = fullText + "\n" + lineToAdd.trim().replaceAll("\\[([^\\]]+)]+", "");
				}
			} else { // is IJoseph
				for (String str : paraLine){
					lineToAdd= lineToAdd+str;
					//System.out.print(str);
				}
				if ( underlined  ){
					if ( ! (lineToAdd.startsWith("18") || lineToAdd.startsWith("19")) ){
						baseTextLines.add(lineToAdd.trim());
						//System.out.print("BASE: ");
					}
				}			
				//System.out.println(lineToAdd);

//				if( lineToAdd.contains("[") ){
//					System.out.println("B: "+lineToAdd.trim());
//					System.out.println("A: "+lineToAdd.trim().replaceAll("\\[([^\\]]+)]+", ""));
//				}
				
				fullText = fullText + "\n" + lineToAdd.trim().replaceAll("\\[([^\\]]+)]+", "");				
			}
		}
		//XWPFWordExtractor we = new XWPFWordExtractVerseFile returnValue = new VerseFile();
		// remove all [] blocks

		return new VerseFile(chap+":"+verse, baseTextLines, fullText,fileName);
		 
	}
public static boolean checkVerseString(String stringToCheck){
    boolean returnvalue = false;
    String[] tmp = stringToCheck.trim().split(":");
    if( tmp.length == 2 )
	returnvalue = true;
    
    return returnvalue;
}

public static boolean checkForOnlyNumbers(String stringToCheck){
    boolean returnvalue = false;
	String pattern = "^ *[0-9][0-9 ]*$";
	Pattern r = Pattern.compile(pattern);
	Matcher m = r.matcher(stringToCheck);
	

    if( m.find() )
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
		// resort by value of value
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);	
        for (SortedMap.Entry<String, Integer> o : mapToWrite.entrySet()) {
        	map.put(o.getKey(),o.getValue());
        }
        sorted_map.putAll(map);
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greek-nlp\\greektext\\output\\"+fileName+".txt")));
                //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
	        for (SortedMap.Entry<String, Integer> o : sorted_map.entrySet()) {
	        	if(o.getValue().intValue() > 1)
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
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greek-nlp\\greektext\\output\\"+fileName+".txt")));
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
		//System.out.println("vectorToWrite length: " + vectorToWrite.length + " Filename: " + fileName);
		BufferedWriter out = null;
		StringBuffer buf = new StringBuffer();
		try {
	        out = new BufferedWriter(new OutputStreamWriter
	        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greek-nlp\\greektext\\output\\"+fileName+".txt")));
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
	public static void printStringArray(String[] list){
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
	public static  String[] readManuscriptNames(String sql){
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
	public static  double[][] readDataFrameFromFile(String fileName, int numRows, int numCols){
		double[][] returnValue = new double[numRows][numCols];
		BufferedReader in = null;
		try {
	        in = new BufferedReader(new InputStreamReader
	        		(new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
                    //(new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));
	        
			for (int k = 0; k < numRows; k++) {				
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
	public static double[] readCountsVectorFromFile(String fileName){
		BufferedReader in = null;
		
		ArrayList<Double> returnValue = new ArrayList<Double>();
		try {
	        in = new BufferedReader(new InputStreamReader
	        		(new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
			String line=in.readLine();
			
			String[] tokens = line.split(",");
			returnValue.add(Double.parseDouble(tokens[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (Exception e2) {}
		}	
			
		return ArrayUtils.toPrimitive(returnValue.toArray(new Double[0]));
		
	}
	public static String[] readStringVectorFromFile(String fileName){
		BufferedReader in = null;
		String[] returnValue = new String[0];
		try {
	        in = new BufferedReader(new InputStreamReader
	        		(new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
			String line=in.readLine();
			
			String[] tokens = line.split(",");
			returnValue = new String[tokens.length];
			for (int i = 0; i < tokens.length; i++) {
				//System.out.println(k+": "+i+" tokens.length: "+tokens.length);
				returnValue[i] = tokens[i];
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

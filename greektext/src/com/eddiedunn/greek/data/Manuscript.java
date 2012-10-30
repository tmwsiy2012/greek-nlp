package com.eddiedunn.greek.data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.eddiedunn.util.CU;
import com.eddiedunn.util.TreeMapStringLengthIntegerValueComparator;


public class Manuscript {
	
	private String id;
	private int manuscriptID;
	private String family;
	private StringBuffer text;
	private SortedMap<Integer,String> chapText;
	private ArrayList<Verse> verses;
	private SortedMap<String, Double> nGramWeight;
	private SortedMap<String, Double> nGramUnitWeight;
	private SortedMap<String, Integer> compositeGrams;
	
	private SortedMap<Integer, SortedMap<String, Integer>> compositeGramsChap;
	private SortedMap<Integer, SortedMap<Integer,SortedMap<String, Integer>>> charGramsChap;
	private SortedMap<Integer, SortedMap<Integer,SortedMap<String, Integer>>> nGramsChap;
	private SortedMap<Integer,SortedMap<String, Integer>> nGrams;
	private SortedMap<Integer,SortedMap<String, Integer>> charGrams;
	private boolean loadChapters;
	private boolean writeDB;
	private int maxGramSize;
	

	


	
	public Manuscript(boolean writeDB,boolean loadChapters,int manuscriptID,String id, String text, String family, SortedMap<Integer, String> chapText){		
	    this.chapText=chapText;
	    this.writeDB=writeDB;
	    this.maxGramSize=0;
	    this.loadChapters=loadChapters;
	    this.manuscriptID = manuscriptID;
		this.id = id;
		// remove all multiple spaces and replace with one
		this.text = new StringBuffer(text.replaceAll("\\s{2,}", " ").trim());	
		this.nGramWeight = new TreeMap<String, Double>();
		this.nGramUnitWeight = new TreeMap<String, Double>();
		this.family = family;
		if( writeDB )
			resetValuesforThisManuscriptInDB();
		setNGrams();
		setCharGrams();
		//System.out.println(id+" Total ngrams: "+nGrams.size()+" charGrams: "+nGrams.size()+" chap text len:"+chapText.size());
		setCompositeGrams();
		//System.out.println(id+" Created");
	}
	public int getMaxGramSize(){
		return this.maxGramSize;
	}
/*	public void addText(String input){
		text.append(input);
	}
	*/
	
	public String getText(){
		return text.toString().trim();
	}
	public String getText(int chap){
		if( chapText.containsKey(new Integer(chap)))
		return chapText.get(new Integer(chap)).toString().trim();
		else
			return "";
	}	
	
	public int getManuscriptID() {
		return manuscriptID;
	}

	public SortedMap<Integer, String> getChapText() {
		return chapText;
	}

	public SortedMap<String, Double> getnGramWeight() {
		return nGramWeight;
	}
	public SortedMap<String, Double> getnGramUnitWeight() {
		return nGramUnitWeight;
	}	


	public ArrayList<Verse> getVerses() {
	    return verses;
	}

	public void resetNGramWeights(){
		this.nGramWeight = new TreeMap<String, Double>();
		this.nGramUnitWeight = new TreeMap<String, Double>();
	}
	public String getID(){
		return id;
	}

	public void writeFile(){
		BufferedWriter out = null;
		try {
	        out = new BufferedWriter(new OutputStreamWriter
                    (new FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\"+id+".txt"),"UTF8"));
	        
	        out.write(getText());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (Exception e2) {}
		}
	}
	private void setCompositeGrams(){
		SortedMap<String, Integer> globalGrams = new TreeMap<String, Integer>();
		
		SortedMap<Integer, SortedMap<String, Integer>> tmpCompositeGramsChap = new TreeMap<Integer, SortedMap<String, Integer>>();
		for (int i =CU.chargramMin; i <=CU.chargramMax; i++) {
			CU.mergeMapSum(globalGrams, getNCharGrams(i));
		}
		
		for (int i = 1; i <=CU.ngramMax; i++) {
			CU.mergeMapSum(globalGrams, getNGrams(i));
		}
		//CU.pruneMap(globalGrams);
		if( writeDB )
			writeGlobalGramsToDB(globalGrams);
		this.compositeGrams = globalGrams;
		// now populate chapter composite grams
		if( loadChapters){
		for( int chap=1; chap<=25;chap++){
			globalGrams = new TreeMap<String, Integer>();
			for (int i =CU.chargramMin; i <=CU.chargramMax; i++) {
				if( getNCharGrams(i, chap).size() > 0)
					CU.mergeMapSum(globalGrams, getNCharGrams(i, chap));
			}
			
			for (int i = 1; i <=CU.ngramMax; i++) {
				if( getNGrams(i, chap).size() > 0)
				CU.mergeMapSum(globalGrams, getNGrams(i, chap));
			}		
			//CU.pruneMap(globalGrams);
			if( writeDB )
				writeChapterGlobalGramsToDB(chap,globalGrams);		
			tmpCompositeGramsChap.put(chap, globalGrams);
		}
		this.compositeGramsChap = tmpCompositeGramsChap;
		}
	}
	private void resetValuesforThisManuscriptInDB(){

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
		        
		        
		        stmt.executeUpdate("delete from grams_by_manuscript where manuscriptid="+this.manuscriptID+";");
		        stmt.executeUpdate("delete from grams_by_chap where manuscriptid="+this.manuscriptID+";");
		
		    
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
	private void writeGlobalGramsToDB(SortedMap<String, Integer> globalGrams){
		
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
		        
		        for(Map.Entry<String, Integer> g : globalGrams.entrySet() ){
		        	stmt.executeUpdate("insert into grams_by_manuscript(manuscriptid,gram,count) values("+this.manuscriptID+",'"+g.getKey()+"',"+g.getValue().intValue()+");");
		        	if( g.getValue().intValue() > this.maxGramSize )
		        		this.maxGramSize = g.getValue().intValue();
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
	  
	}
	private void writeChapterGlobalGramsToDB(int chapter, SortedMap<String, Integer> globalGrams){
		
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
		        
		        for(Map.Entry<String, Integer> g : globalGrams.entrySet() )
		        	stmt.executeUpdate("insert into grams_by_chap(manuscriptid,chapternumber,gram,count) values("+this.manuscriptID+","+chapter+",'"+g.getKey()+"',"+g.getValue().intValue()+");");
		
		    
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
	public SortedMap<String, Integer> getCompositeGrams(){
		return this.compositeGrams;		

	}
	public SortedMap<String, Integer> getCompositeGrams(int chap){
		return this.compositeGramsChap.get(chap);		

	}	
	public boolean hasChapter(int chap){
		if(chapText.get(new Integer(chap)) != null && getText(chap).length() > 10 )
			return true;
		else
			return false;
	}
	// prune decompisitions with same count start with biggest ngrams \
	// and work backwards to word and then largest charSequence and work backward to smallest

	private void setNGrams(){
	    this.nGrams = new TreeMap<Integer,SortedMap<String, Integer>>();
		String[] arr = getText().split("\\s+");
		SortedMap<String, Integer> currentNGrams = new TreeMap<String, Integer>();
		for (int size = 1; size <= CU.ngramMax; size++) {
		    
		
		currentNGrams = new TreeMap<String, Integer>();
		for (int i = 0; i < arr.length; i++) {			
			if( i+(size-1) < arr.length ){		
				String currentNGram = getNGram(arr,i,size);
				if( currentNGram.length() > maxGramSize )
					maxGramSize = currentNGram.length();
				if( currentNGrams.containsKey(currentNGram)){
					int tmp = currentNGrams.get(currentNGram).intValue() + 1;
					currentNGrams.put(currentNGram, new Integer(tmp));					
				}else{
					currentNGrams.put(currentNGram, new Integer(1));
				}
			}			
		}
		//CU.pruneMap(currentNGrams);
		nGrams.put(new Integer(size), currentNGrams);
		}
		
		if( loadChapters){
		SortedMap<Integer, SortedMap<Integer,SortedMap<String, Integer>>> tmpNGramsChap = new TreeMap<Integer, SortedMap<Integer,SortedMap<String, Integer>>>();		
		for( int chap=1; chap<=25; chap++){
			if( ! hasChapter(chap) )
					continue;
			arr = getText(chap).split("\\s+");
			SortedMap<Integer,SortedMap<String, Integer>>	currentNGramsChap = new TreeMap<Integer, SortedMap<String, Integer>>();
		for (int size = 1; size <= CU.ngramMax; size++) {
		    
			
			currentNGrams = new TreeMap<String, Integer>();
			for (int i = 0; i < arr.length; i++) {			
				if( i+(size-1) < arr.length ){		
					String currentNGram = getNGram(arr,i,size);
					if( currentNGrams.containsKey(currentNGram)){
						int tmp = currentNGrams.get(currentNGram).intValue() + 1;
						currentNGrams.put(currentNGram, new Integer(tmp));					
					}else{
						currentNGrams.put(currentNGram, new Integer(1));
					}
				}
			}
			//CU.pruneMap(currentNGrams);
			currentNGramsChap.put(new Integer(size), currentNGrams);
			}	
		tmpNGramsChap.put(new Integer(chap), currentNGramsChap);
		}
		
		this.nGramsChap = tmpNGramsChap;
		
		}
	}
	private void setCharGrams(){
	    this.charGrams = new TreeMap<Integer,SortedMap<String, Integer>>();
		char[] arr = getText().toCharArray();
		for (int size = CU.chargramMin; size <= CU.chargramMax; size++) {
		    
		
		SortedMap<String, Integer> tmpCharGrams = new TreeMap<String, Integer>();
		for (int i = 0; i < arr.length - size; i++) {
		
				String currentNCharGram = getNCharGram(arr,i,size);
				if( currentNCharGram.length() != size){
					System.out.println("wrong size chargram: "+currentNCharGram+"  size: "+currentNCharGram.length()+"index:"+i+" manuscript: "+this.id);
				}
				if( tmpCharGrams.containsKey(currentNCharGram)){
					int tmp = tmpCharGrams.get(currentNCharGram).intValue() + 1;
					tmpCharGrams.put(currentNCharGram, new Integer(tmp));					
				}else{
					tmpCharGrams.put(currentNCharGram, new Integer(1));
				}
			
		}
		//CU.pruneMap(tmpCharGrams);
		charGrams.put(new Integer(size), tmpCharGrams);
		}    
		
		if( loadChapters){
		SortedMap<Integer, SortedMap<Integer,SortedMap<String, Integer>>> tmpCharGramsChap = new TreeMap<Integer, SortedMap<Integer,SortedMap<String, Integer>>>();
		for(int chap=1; chap<=25;chap++){
			if( ! hasChapter(chap) )
				continue;
			arr = getText(chap).toCharArray();
			SortedMap<Integer,SortedMap<String, Integer>>	currentNGramsChap = new TreeMap<Integer, SortedMap<String, Integer>>();
		for (int size = CU.chargramMin; size <= CU.chargramMax; size++) {
		    
			
		SortedMap<String, Integer> tmpCharGrams = new TreeMap<String, Integer>();
		for (int i = 0; i < arr.length - size; i++) {
		
				String currentNCharGram = getNCharGram(arr,i,size);
				if( currentNCharGram.length() != size){
					System.out.println("wrong size chargram: "+currentNCharGram+"  size: "+currentNCharGram.length()+"index:"+i+" manuscript: "+this.id);
				}
				if( tmpCharGrams.containsKey(currentNCharGram)){
					int tmp = tmpCharGrams.get(currentNCharGram).intValue() + 1;
					tmpCharGrams.put(currentNCharGram, new Integer(tmp));					
				}else{
					tmpCharGrams.put(currentNCharGram, new Integer(1));
				}
			
		}
		//CU.pruneMap(tmpCharGrams);
		currentNGramsChap.put(new Integer(size), tmpCharGrams);
		}  
		tmpCharGramsChap.put(new Integer(chap), currentNGramsChap);
		}
		charGramsChap = tmpCharGramsChap;
		}
	}
	public SortedMap<String, Integer> getNGrams(int size){
	    return this.nGrams.get(new Integer(size));
	}
	public SortedMap<String, Integer> getNGrams(int size, int chap){
		if( this.nGramsChap.containsKey(new Integer(chap)) && this.nGramsChap.get(new Integer(chap)).containsKey(new Integer(size)))
	    return this.nGramsChap.get(new Integer(chap)).get(new Integer(size));
		else
			return new TreeMap<String, Integer>();		
	}	
	public SortedMap<String, Integer> getNCharGrams(int size){
	    return this.charGrams.get(new Integer(size));
	}	
	public SortedMap<String, Integer> getNCharGrams(int size, int chap){
		if( this.charGramsChap.containsKey(new Integer(chap)) && this.charGramsChap.get(new Integer(chap)).containsKey(new Integer(size)))
	    return this.charGramsChap.get(new Integer(chap)).get(new Integer(size));
		else
			return new TreeMap<String, Integer>();
	}	
	private String getNCharGram(char[] arr, int start, int size) {
		StringBuffer retVal = new StringBuffer();
		for( int i=start;i<start+size;i++){
			retVal.append(arr[i]);
		}			
		return retVal.toString();		
	}		
	private String getNGram(String[] arr, int start, int size) {
		StringBuffer retVal = new StringBuffer();
		for( int i=start;i<start+size;i++){
			retVal.append(arr[i]+" ");
		}			
		return retVal.toString().trim();		
	}

	public boolean isCorrectFamily(String familyStringToTest, int levelsToMatch){
	    boolean returnValue = false;
	    String[] arrayToTest = familyStringToTest.split("\\.");
	    String[] acceptedArray = this.family.split("\\.");
	    //System.out.println(acceptedArray[0] +" "+arrayToTest[0]);
	    //System.out.println(this.family);
	    switch (levelsToMatch) {
	    case 0:
		System.out.println("PROBLEM: called manuscript.isCorrectFamily() manuscript: "+this.id+" with 0 levelsToMatch");		
		break;
	    case 1:
		if( arrayToTest.length >= 1 && acceptedArray.length >= 1){
		    if(arrayToTest[0].equals(acceptedArray[0])  )
			returnValue = true;
		}
		break;
	    case 2:
		if( arrayToTest.length >= 2 && acceptedArray.length >= 2){
		    if(arrayToTest[0].equals(acceptedArray[0]) && arrayToTest[1].equals(acceptedArray[1]) )
			returnValue = true;
		}		
		break;		
	    case 3:
		if( arrayToTest.length >= 3 && acceptedArray.length >= 3){
		    if(arrayToTest[0].equals(acceptedArray[0]) && arrayToTest[1].equals(acceptedArray[1]) && arrayToTest[2].equals(acceptedArray[2]))
			returnValue = true;
		}		
		break;			
	    default:
		System.out.println("PROBLEM: called manuscript.isCorrectFamily() manuscript: "+this.id+" with familyid: "+this.family+" and string to test: "+familyStringToTest+" and levels to match:"+levelsToMatch);
		break;
	    }
	    return returnValue;
	}

	public String getFamily() {
	    return this.family;
	}
	public String getFamily(int levelToMatch) {
		StringBuffer retVal = new StringBuffer();
		String[] tmpfam = this.family.split("\\.");
		for(int i=0;i<levelToMatch;i++){
			retVal.append(tmpfam[i]+".");
		}
	    return retVal.substring(0, retVal.length()-1);
	}	
}

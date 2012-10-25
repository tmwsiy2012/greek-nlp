package com.eddiedunn.greek.data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.eddiedunn.util.CU;

public class Manuscript {
	
	private String id;
	private String family;
	private StringBuffer text;
	private ArrayList<Verse> verses;
	private SortedMap<String, Double> nGramWeight;
	private SortedMap<String, Double> nGramUnitWeight;
	private SortedMap<String, Integer> compositeGrams;
	private SortedMap<Integer,SortedMap<String, Integer>> nGrams;
	private SortedMap<Integer,SortedMap<String, Integer>> charGrams;
	

	


	
	public Manuscript(String id, String text, String family){
	    
		this.id = id;
		// remove all multiple spaces and replace with one
		this.text = new StringBuffer(text.replaceAll("\\s{2,}", " ").trim());	
		this.nGramWeight = new TreeMap<String, Double>();
		this.nGramUnitWeight = new TreeMap<String, Double>();
		this.family = family;
		
		setNGrams();
		setCharGrams();
		
		setCompositeGrams();
	}

/*	public void addText(String input){
		text.append(input);
	}
	*/
	
	public String getText(){
		return text.toString().trim();
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
		SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
		for (int i =CU.chargramMin; i <=CU.chargramMax; i++) {
			mergeMapCount(returnValue, getNCharGrams(i));
		}
		
		for (int i = 1; i <=CU.ngramMax; i++) {
			mergeMapCount(returnValue, getNGrams(i));
		}
		this.compositeGrams = returnValue;
	}
	public SortedMap<String, Integer> getCompositeGrams(){
		return this.compositeGrams;		

	}
	private void setNGrams(){
	    this.nGrams = new TreeMap<Integer,SortedMap<String, Integer>>();
		String[] arr = getText().split("\\s+");
		for (int size = 1; size <= CU.ngramMax; size++) {
		    
		
		SortedMap<String, Integer> currentNGrams = new TreeMap<String, Integer>();
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
		nGrams.put(new Integer(size), currentNGrams);
		}
		    
	}
	private void setCharGrams(){
	    this.charGrams = new TreeMap<Integer,SortedMap<String, Integer>>();
		char[] arr = getText().toCharArray();
		for (int size = CU.chargramMin; size <= CU.chargramMax; size++) {
		    
		
		SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
		for (int i = 0; i < arr.length - size; i++) {
		
				String currentNCharGram = getNCharGram(arr,i,size);
				if( currentNCharGram.length() != size){
					System.out.println("wrong size chargram: "+currentNCharGram+"  size: "+currentNCharGram.length()+"index:"+i+" manuscript: "+this.id);
				}
				if( returnValue.containsKey(currentNCharGram)){
					int tmp = returnValue.get(currentNCharGram).intValue() + 1;
					returnValue.put(currentNCharGram, new Integer(tmp));					
				}else{
					returnValue.put(currentNCharGram, new Integer(1));
				}
			
		}
		charGrams.put(new Integer(size), returnValue);
		}    
	}
	public SortedMap<String, Integer> getNGrams(int size){
	    return this.nGrams.get(new Integer(size));
	}
	public SortedMap<String, Integer> getNCharGrams(int size){
	    return this.charGrams.get(new Integer(size));
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

    private void mergeMapCount(SortedMap<String, Integer> original,
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

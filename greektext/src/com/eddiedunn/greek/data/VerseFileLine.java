package com.eddiedunn.greek.data;

import java.util.ArrayList;

import com.eddiedunn.util.CU;

public class VerseFileLine {
    private int verseFileLineid;
    private int chapter;
    private int verse;
    private boolean isBaseText;
    private boolean isAddition;
    private boolean prevBaseText;
    private String text;
    private String currentBaseText;
    private String[] manuScriptNumbers;
    
    
    public VerseFileLine(int verseFileLineid,int chapter,int verse,boolean isBaseText, boolean prevBaseText, String rawText,String numberBlock, String currentBaseText){
	this.verseFileLineid = verseFileLineid;
	this.chapter = chapter;
	this.verse = verse;
	this.isBaseText = isBaseText;
	this.prevBaseText = prevBaseText;
	this.currentBaseText = currentBaseText;
	if ( rawText == null)
		System.out.println(chapter+" "+verse+" "+verseFileLineid);
	processRawLine(numberBlock, rawText);
    }

    private void processRawLine(String numberBlock, String rawText){
    	
    	
    	// need to see if its addition and assign manuscript[]
    	if(rawText.trim().startsWith("+")){
    		isAddition = true;
    		text = rawText.replace("+", "");
    	}else{
    		text = rawText;
    	}
    	
		
    	String[] scriptNumbers = numberBlock.split(" ");
		ArrayList<String> numbers = new ArrayList<String>();
		for( String str : scriptNumbers){
			String manuscriptNumber = str.trim();
	        	  
	        	   
			if(  (manuscriptNumber.endsWith("c")&& manuscriptNumber.length() == 3) || manuscriptNumber.length() == 2   )
				manuscriptNumber = "0"+manuscriptNumber;
			
		
			numbers.add(manuscriptNumber.trim());

		}
			
		this.manuScriptNumbers = numbers.toArray(new String[numbers.size()]);    	
    }
    public int getVerseFileLineid() {
        return verseFileLineid;
    }

    public int getChapter() {
        return chapter;
    }


	public int getVerse() {
        return verse;
    }

    public boolean isBaseText() {
        return isBaseText;
    }

    public String getText() {
        return text;
    }

	public boolean isAddition() {
		return isAddition;
	}

	public String getCurrentBaseText() {
		return currentBaseText;
	}

	public String[] getManuScriptNumbers() {
		return manuScriptNumbers;
	}
    
    public boolean isPrevBaseText() {
		return prevBaseText;
	}

	public void setPrevBaseText(boolean prevBaseText) {
		this.prevBaseText = prevBaseText;
	}

	public void addManuscript(String manuscript){
    	String[] newMScripts = new String[manuScriptNumbers.length+1];
    	
    	for(int i=0; i <manuScriptNumbers.length; i++ )
    		newMScripts[i] = manuScriptNumbers[i];
    	newMScripts[manuScriptNumbers.length] = manuscript;
    	
    	manuScriptNumbers = newMScripts;
    }
}

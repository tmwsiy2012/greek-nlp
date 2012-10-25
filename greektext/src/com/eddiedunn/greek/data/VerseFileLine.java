package com.eddiedunn.greek.data;

import java.util.ArrayList;

public class VerseFileLine {
    private int verseFileLineid;
    private int chapter;
    private int verse;
    private boolean isBaseText;
    private boolean isAddition;
    private String text;
    private String currentBaseText;
    private String[] manuScriptNumbers;
    
    
    public VerseFileLine(int verseFileLineid,int chapter,int verse,boolean isBaseText,String rawText,String numberBlock, String currentBaseText){
	this.verseFileLineid = verseFileLineid;
	this.chapter = chapter;
	this.verse = verse;
	this.isBaseText = isBaseText;
	this.currentBaseText = currentBaseText;
	processRawLine(numberBlock, rawText);
    }

    private void processRawLine(String numberBlock, String rawText){
    	// need to see if its addition and assign manuscript[]
    	if(rawText.startsWith("+")){
    		isAddition = true;
    		text = rawText.replace("+", "");
    	}else{
    		text = rawText;
    	}
    	
    	
    	String[] scriptNumbers = numberBlock.split(" ");
		ArrayList<String> numbers = new ArrayList<String>();
		for( String str : scriptNumbers){
			String manuscriptNumber = str.trim();
			if( manuscriptNumber.length()<2)
				continue;
			if( manuscriptNumber.contains("-")){
				//System.out.println(manuscriptNumber);
				String[] range = manuscriptNumber.split("-");

				
				if( range.length != 2){
					System.out.println("PROBLEM IN RANGE should not see this text: "+manuscriptNumber+" numBlock: "+numberBlock+" versefilelineid="+verseFileLineid);	
				}else { // it is equal to 2 and all is well
					int startNum = Integer.parseInt(range[0]);
					int endNum = Integer.parseInt(range[1]);
					for(int i=startNum;i<=endNum;i++){
						String numToAdd = String.format("%03d", i);
						if( numToAdd.equals("601c"))
							System.out.println("found 601c "+this.verseFileLineid);
						numbers.add(numToAdd);

					}
				}
			}else if( manuscriptNumber.length() == 3 || manuscriptNumber.length() == 2 || ( manuscriptNumber.length() == 4 && manuscriptNumber.contains("c")) ){ // single index all is well
	        	   // sanity check				   
	        	   String rawNum = manuscriptNumber.replaceAll("c", "");
	        	   try {
	        		   int tmpNum = Integer.parseInt(rawNum);
				} catch (Exception e) {
					System.out.println("could not parse "+manuscriptNumber+" as number, versefilelineid: "+this.verseFileLineid);
				}
	        	  
	        	   
				if( manuscriptNumber.length() == 2  )
					manuscriptNumber = "0"+manuscriptNumber;
				
				String numToAdd =  manuscriptNumber;
				
				if( numToAdd.equals("601c"))
					System.out.println("found 601c "+this.verseFileLineid);				
				numbers.add(manuscriptNumber.trim());
													
			}else { // need to write GZ baseText and BD baseText odd ball need to examine														
				System.out.println("should not see this text: "+manuscriptNumber+" numBlock: "+numberBlock+" versefilelineid="+verseFileLineid);
				//System.out.println("need to work on versefilelineid="+verseFileLineid);
			}
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
    
    
}

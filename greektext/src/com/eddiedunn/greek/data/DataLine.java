package com.eddiedunn.greek.data;

import java.util.ArrayList;

public class DataLine {
	
	boolean isAddition=false;
	boolean isMissing=false;
	boolean isBaseText=false;
	boolean safeToWrite=true;
	String  greekText;
	String  currentBaseText;
	String[] manuScriptNumbers;
	public boolean isAddition() {
		return isAddition;
	}
	public void setAddition(boolean isAddition) {
		this.isAddition = isAddition;
	}
	public String getGreekText() {
		return greekText;
	}
	public void setGreekText(String greekText) {
		this.greekText = greekText;
	}
	public String[] getManuScriptNumbers() {
		return manuScriptNumbers;
	}
	public void setManuScriptNumbers(String[] scriptNumbers) {
		ArrayList<String> numbers = new ArrayList<String>();
		for( String manuscriptNumber : scriptNumbers){
			if( manuscriptNumber.contains("-")){
				//System.out.println(manuscriptNumber);
				String[] range = manuscriptNumber.split("-");

				
				if( range.length != 2){
					System.out.println("should not see this text: "+manuscriptNumber);	
				}else { // it is equal to 2 and all is well
					int startNum = Integer.parseInt(range[0]);
					int endNum = Integer.parseInt(range[1]);
					for(int i=startNum;i<=endNum;i++){
						
						numbers.add(String.format("%03d", i));

					}
				}
			}else if( manuscriptNumber.length() == 3 || manuscriptNumber.length() == 2 ){ // single index all is well
				
				if( manuscriptNumber.length() == 2  )
					manuscriptNumber = "0"+manuscriptNumber;
				
				numbers.add(manuscriptNumber.trim());
													
			}else { // need to write GZ baseText and BD baseText odd ball need to examine														
				System.out.println("should not see this text: "+manuscriptNumber);													
			}
		}
			
		this.manuScriptNumbers = numbers.toArray(new String[numbers.size()]);
	}
	public boolean isMissing() {
		return isMissing;
	}
	public void setMissing(boolean isMissing) {
		this.isMissing = isMissing;
	}
	public boolean isBaseText() {
		return isBaseText;
	}
	public void setBaseText(boolean isBaseText) {
		this.isBaseText = isBaseText;
	}
	public String getCurrentBaseText() {
		return currentBaseText;
	}
	public void setCurrentBaseText(String currentBaseText) {
		this.currentBaseText = currentBaseText;
	}
	public boolean isSafeToWrite() {
		return safeToWrite;
	}
	public void setSafeToWrite(boolean safeToWrite) {
		this.safeToWrite = safeToWrite;
	}

}

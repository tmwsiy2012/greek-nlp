package com.eddiedunn.greek.data;

import java.util.ArrayList;

public class VerseFile {
	private String chapverse;
	private ArrayList<String> bodmerText;
	private String documentText;
	private String fileName;
	private int chapNum;
	private int verseNum;
	private int vfid;
	
	public VerseFile(){
		
	}
	public VerseFile(String chapverse, ArrayList<String> bodmerText, String documentText, String fileName){
		this.chapverse = chapverse;
		this.bodmerText = bodmerText;
		this.documentText = documentText;
		this.fileName = fileName;
		setChapAndVerse();
	}
	private void setChapAndVerse(){
	    String[] cv = this.chapverse.split(":");
	    chapNum = Integer.parseInt(cv[0].trim());
	    verseNum = Integer.parseInt(cv[1].trim());
	}
	
	public String getChapVerseString() {
		return chapverse;
	}
	public ArrayList<String> getUnderlinedText() {
		return bodmerText;
	}
	public String getDocumentText() {
		return documentText;
	}
	
	public int getVfid() {
	    return vfid;
	}
	public void setVfid(int vfid) {
	    this.vfid = vfid;
	}
	public int getChapNum() {
	    return chapNum;
	}
	public int getVerseNum() {
	    return verseNum;
	}
	public String getFileName() {
	    return fileName;
	}
	public void printVerseFile(){
		System.out.println("Verse: "+chapverse);
		for(String str: bodmerText){
			System.out.println("BaseText: "+ str);
		}
		System.out.println(documentText);
	}
	
}

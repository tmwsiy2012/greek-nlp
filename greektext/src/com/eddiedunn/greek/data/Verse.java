package com.eddiedunn.greek.data;

public class Verse {

    private int chapter;
    private int verseNumber;
    private String text;
    
    public Verse(int chap,int verseNumber, String text){
	this.chapter = chap;
	this.verseNumber = verseNumber;
	this.text = text;
    }

    public int getChapter() {
        return chapter;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public String getText() {
        return text;
    }
    
}

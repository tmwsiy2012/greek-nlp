package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.greek.data.Manuscript;
import com.eddiedunn.util.CU;

public class RunStats {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Corpus c = new Corpus(CU.selectAllManuscriptsSQL,true);
		
		double sum=0;
		for(Map.Entry<String, Manuscript> m : c.getManuScripts().entrySet() ){
			int currentDocLength = m.getValue().getText().length();
			sum += currentDocLength;
			System.out.println(m.getKey()+" size: "+CU.humanReadableByteCount(currentDocLength, true));
		}
		double avg = sum/c.getManuScripts().size();
		System.out.println("Average Doc Length: "+CU.humanReadableByteCount((long)avg, true));
		
		
		for (int chapter = 1; chapter <= 25; chapter++) {
			sum=0;
			SortedMap<String, String> chapTexts = c.getChapDocsName(chapter);
			for(Map.Entry<String, String> chapText : chapTexts.entrySet() ){
				int currentDocLength = chapText.getValue().length();
				sum += currentDocLength;
				System.out.println(chapText.getKey()+" chap:"+String.format("%02d", chapter)+" size: "+CU.humanReadableByteCount(currentDocLength, true));
			}
			avg = sum/c.getManuScripts().size();
			System.out.println("Average Doc Length Chapter: "+String.format("%02d", chapter)+" "+CU.humanReadableByteCount((long)avg, true));
		}
	}

}

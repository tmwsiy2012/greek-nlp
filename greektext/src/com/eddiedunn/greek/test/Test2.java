package com.eddiedunn.greek.test;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.greek.data.Manuscript;
import com.eddiedunn.util.CU;

public class Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Corpus c = new Corpus(false,false,false);
		
		double sum=0;
		for(Map.Entry<String, Manuscript> m : c.getManuScripts().entrySet() ){
			int currentDocLength = m.getValue().getText().length();
			sum += currentDocLength;
			System.out.println(m.getKey()+" size: "+CU.humanReadableByteCount(currentDocLength, true));
		}
		double avg = sum/c.getManuScripts().size();
		System.out.println("Average Doc Length: "+CU.humanReadableByteCount((long)avg, true));
		
		for (int chapter = 1; chapter <= 25; chapter++) {
			
		}
	}

}

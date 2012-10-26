package com.eddiedunn.greek.test;

import java.util.ArrayList;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.util.CU;

public class Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Corpus c = new Corpus(true,false);
		
		CU.writeVectorToFile(c.getManuscriptLabels(), "oldManuscriptLabels");
	}

}

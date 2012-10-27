package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;

import com.eddiedunn.util.CU;
import com.eddiedunn.util.StopWatch;

public class RDataGeneratorCompositeGrams {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StopWatch clock = new StopWatch("started Corpus creation");
		//public Corpus(boolean onlyOld, boolean loadChapters, boolean removeOutliers) {
		Corpus c = new Corpus(false,true,false);
		//Corpus c = new Corpus(false,true,false);
		System.out.println("created Corpus");
		clock.printElapsedTime();
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");
		
		SortedMap<String, Integer> tmpGrandCompositeGrams = c.getGrandCompositeGrams();
		System.out.println("totalTokens: "+tmpGrandCompositeGrams.size());
		
		runCompositeGramTF_IDFFeature(c, tmpGrandCompositeGrams);		
		for(int chap=1; chap<=25; chap++){				
			tmpGrandCompositeGrams = c.getGrandCompositeGrams(chap);
		
			System.out.println("chap "+chap+" totalTokens: "+tmpGrandCompositeGrams.size());
			runCompositeGramTF_IDFFeature(chap, c, tmpGrandCompositeGrams);
		}

	}
	private static void runCompositeGramTF_IDFFeature(int chap, Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsFullChap"+String.format("%02d", chap));
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorFullChap"+String.format("%02d", chap));
		CU.writeVectorToFile(c.getManuscriptLabels(chap), "compositeGramManuscriptNameVectorFullChap"+String.format("%02d", chap));
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams, chap);
		System.out.println("chap "+chap+" finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixFullChap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(chap,tmpGrandCompositeGrams);
		System.out.println("chap "+chap+" finished normalize");	
		c.writeCurrentCosineMatrix(chap,tmpGrandCompositeGrams, "CompositeGramCosineMatrixFullChap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished");		
	}	
	private static void runCompositeGramTF_IDFFeature(Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsFull");
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorFull");
		CU.writeVectorToFile(c.getManuscriptLabels(), "compositeGramManuscriptNameVectorFull");
    	
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixFull");
		System.out.println("finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(tmpGrandCompositeGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandCompositeGrams, "CompositeGramCosineMatrixFull");
		System.out.println("finished");
		
	}

}

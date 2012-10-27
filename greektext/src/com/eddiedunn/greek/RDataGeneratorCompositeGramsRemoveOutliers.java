package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;

import com.eddiedunn.util.CU;

public class RDataGeneratorCompositeGramsRemoveOutliers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//public Corpus(boolean onlyOld, boolean loadChapters, boolean removeOutliers) {
		Corpus c = new Corpus(false,true,true);
	
		System.out.println("created Corpus");
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
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsRemoveOutliersChap"+String.format("%02d", chap));
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorRemoveOutliersChap"+String.format("%02d", chap));
		CU.writeVectorToFile(c.getManuscriptLabels(chap), "compositeGramManuscriptNameVectorRemoveOutliersChap"+String.format("%02d", chap));
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams, chap);
		System.out.println("chap "+chap+" finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixRemoveOutliersChap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(chap,tmpGrandCompositeGrams);
		System.out.println("chap "+chap+" finished normalize");	
		c.writeCurrentCosineMatrix(chap,tmpGrandCompositeGrams, "CompositeGramCosineMatrixRemoveOutliersChap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished");		
	}	
	private static void runCompositeGramTF_IDFFeature(Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsRemoveOutliers");
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorRemoveOutliers");
		CU.writeVectorToFile(c.getManuscriptLabels(), "compositeGramManuscriptNameVectorRemoveOutliers");
    	
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixRemoveOutliers");
		System.out.println("finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(tmpGrandCompositeGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandCompositeGrams, "CompositeGramCosineMatrixRemoveOutliers");
		System.out.println("finished");
		
	}

}

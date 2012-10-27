package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.greek.data.Manuscript;
import com.eddiedunn.util.CU;

public class TestCompositeGramsByChapterRemoveOutliers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Corpus c = new Corpus(false,true,true);
		System.out.println("created Corpus");
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");		
		for(int chap=1; chap<=25; chap++){				
			SortedMap<String, Integer> tmpGrandCompositeGrams = c.getGrandCompositeGrams(chap);
		
			System.out.println("chap "+chap+" totalTokens: "+tmpGrandCompositeGrams.size());
			runCompositeGramTF_IDFFeature(chap, c, tmpGrandCompositeGrams);
		}
		
	}
	private static void runCompositeGramTF_IDFFeature(int chap, Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsFullRemoveOutliersChap"+String.format("%02d", chap));
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorFullRemoveOutliersChap"+String.format("%02d", chap));
		CU.writeVectorToFile(c.getManuscriptLabels(chap), "compositeGramManuscriptNameVectorFullRemoveOutliersChap"+String.format("%02d", chap));
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams, chap);
		System.out.println("chap "+chap+" finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixFullRemoveOutliersChap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(chap,tmpGrandCompositeGrams);
		System.out.println("chap "+chap+" finished normalize");	
		c.writeCurrentCosineMatrix(chap,tmpGrandCompositeGrams, "CompositeGramCosineMatrixFullRemoveOutliersChap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished");		
	}
}

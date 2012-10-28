package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.greek.data.Manuscript;
import com.eddiedunn.util.CU;

public class TestCompositeGramsOld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Corpus c = new Corpus(CU.selectOldManuscriptsSQL,false);
		System.out.println("created Corpus");
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");		
		SortedMap<String, Integer> tmpGrandCompositeGrams = c.getGrandCompositeGrams();

		System.out.println("totalTokens: "+tmpGrandCompositeGrams.size());
		runCompositeGramTF_IDFFeature(c, tmpGrandCompositeGrams);
	}
	private static void runCompositeGramTF_IDFFeature(Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsFullOld");
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorFullOld");		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixFullOld");
		System.out.println("finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(tmpGrandCompositeGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandCompositeGrams, "CompositeGramCosineMatrixFullOld");
		System.out.println("finished");		
	}
}

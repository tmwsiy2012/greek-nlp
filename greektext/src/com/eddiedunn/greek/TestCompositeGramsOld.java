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
		Corpus c = new Corpus(true);
		System.out.println("created Corpus");
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");		
		SortedMap<String, Integer> tmpGrandCompositeGrams = c.getGrandCompositeGrams();
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, "compositeGramGlobalCountsFullOld");
		CU.writeVectorToFile(tmp.toArray(new String[0]), "compositeGramFeatureVectorFullOld");
		
    	

		System.out.println("totalTokens: "+tmpGrandCompositeGrams.size());
		runCompositeGramTF_IDFFeature(c, tmpGrandCompositeGrams);
		//for( Map.Entry<String, Integer> f : tmpGrandCompositeGrams.entrySet() ){
			
				//System.out.println(f.getKey()+" count: "+f.getValue());
				//System.out.println("found non-size length feature: "+f.getKey());
			
		//}
		
	}
	private static void runCompositeGramTF_IDFFeature(Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams){
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, "CompositeGramIDFFeatureMatrixFullOld");
	}
}

package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;

import com.eddiedunn.util.CU;


public class RDataGeneratorOld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Corpus c = new Corpus(true,false);
		System.out.println("created Corpus");
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");
		generateData(c);


	}
	public static void generateData(Corpus c){
		for (int i = 1; i <= CU.ngramMax; i++) {
		    runNGramTF_IDFCosine(c,i);	
		    runNGramTF_IDFFeature(c,i);
		    System.out.println("finished ngram run i="+i);
		}
		for (int i = CU.chargramMin; i <= CU.chargramMax; i++) {
		    runNCharGramTF_IDFCosine(c, i);
		    runNCharGramTF_IDFFeature(c,i);
		    System.out.println("finished nChargram run i="+i);
		}
		
		
	}	
	public static void runNGramTF_IDFFeature(Corpus c, int size){
		SortedMap<String, Integer> tmpGrandNGrams = c.getGrandNGrams(size);
		c.calculateTF_IDF_NGramWeights(size, tmpGrandNGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandNGrams, size+"NGramIDFFeatureMatrixOld");
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandNGrams.keySet());
		CU.writeVectorToFile(tmp.toArray(new String[0]),size+"NGramIDFFeatureVectorOld");		
		
	}
	public static void runNCharGramTF_IDFFeature(Corpus c, int size){
		SortedMap<String, Integer> tmpGrandNCharGrams = c.getGrandNCharGrams(size);
		c.calculateTF_IDF_CharNGramWeights(size, tmpGrandNCharGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandNCharGrams, size+"charGramIDFFeatureMatrixOld");
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandNCharGrams.keySet());
		CU.writeVectorToFile(tmp.toArray(new String[0]),size+"charGramIDFFeatureVectorOld");			
	}	

	
	public static void runNCharGramTF_IDFCosine(Corpus c, int size){
		SortedMap<String, Integer> tmpGrandNCharGrams = c.getGrandNCharGrams(size);
	    // write basic stats about features for corpus to a file
	    	CU.writeCountMapToFile(c.getGrandNCharGrams(size), size+"charGramGlobalCountsOld");
		c.calculateTF_IDF_CharNGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished calculate");
		c.calculateNormalizedCharNGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandNCharGrams, size+"charGramCosineMatrixOld");
		System.out.println("finished");	    
	}
	public static void runNGramTF_IDFCosine(Corpus c, int size){
	    SortedMap<String, Integer> tmpGrandNCharGrams = c.getGrandNGrams(size);
	    	CU.writeCountMapToFile(c.getGrandNGrams(size), size+"NgramGlobalCountsOld");
		c.calculateTF_IDF_NGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished calculate");
		c.calculateNormalizednGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandNCharGrams, size+"NgramCosineMatrixOld");
		System.out.println("finished");	    
	}	

}

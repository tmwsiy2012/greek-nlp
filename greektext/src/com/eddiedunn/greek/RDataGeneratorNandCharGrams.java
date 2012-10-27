package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;

import com.eddiedunn.util.CU;

public class RDataGeneratorNandCharGrams {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Corpus c = new Corpus((new DataReader()).getManuScripts());
		//public Corpus(boolean onlyOld, boolean loadChapters, boolean removeOutliers) {
		Corpus c = new Corpus(false,true,false);
		//Corpus c = new Corpus(false,true,false);
		System.out.println("created Corpus");

		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");
		generateData(c);
		//runNCharGramTF_IDFCosine(c, 2);
		//runNGramTF_IDFFeature(c,1);

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
		System.out.println("finished TFIDF calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandNGrams, size+"NGramIDFFeatureMatrix");
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandNGrams.keySet());
		CU.writeVectorToFile(tmp.toArray(new String[0]),size+"NGramIDFFeatureVector");
	}
	public static void runNCharGramTF_IDFFeature(Corpus c, int size){
		SortedMap<String, Integer> tmpGrandNCharGrams = c.getGrandNCharGrams(size);
		c.calculateTF_IDF_CharNGramWeights(size, tmpGrandNCharGrams);
		System.out.println("finished TFIDF calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandNCharGrams, size+"charGramIDFFeatureMatrix");
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandNCharGrams.keySet());
		CU.writeVectorToFile(tmp.toArray(new String[0]),size+"charGramIDFFeatureVector");		
	}	

	
	public static void runNCharGramTF_IDFCosine(Corpus c, int size){
		SortedMap<String, Integer> tmpGrandNCharGrams = c.getGrandNCharGrams(size);
	    // write basic stats about features for corpus to a file
	    	CU.writeCountMapToFile(c.getGrandNCharGrams(size), size+"charGramGlobalCounts");
		c.calculateTF_IDF_CharNGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished calculate");
		c.calculateNormalizedCharNGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandNCharGrams, size+"charGramCosineMatrix");		
		System.out.println("finished");	    
	}
	public static void runNGramTF_IDFCosine(Corpus c, int size){
	    SortedMap<String, Integer> tmpGrandNCharGrams = c.getGrandNGrams(size);
	    	CU.writeCountMapToFile(c.getGrandNGrams(size), size+"NgramGlobalCounts");
		c.calculateTF_IDF_NGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished calculate");
		c.calculateNormalizednGramWeights(size,tmpGrandNCharGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandNCharGrams, size+"NgramCosineMatrix");
		System.out.println("finished");	    
	}	

}

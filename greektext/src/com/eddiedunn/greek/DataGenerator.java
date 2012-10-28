package com.eddiedunn.greek;

import java.util.ArrayList;
import java.util.SortedMap;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.util.CU;
import com.eddiedunn.util.StopWatch;

public class DataGenerator {

	
	public DataGenerator(String fileNameBase, String initalSQL){
		StopWatch clock = new StopWatch("started Corpus creation");
		//public Corpus(String selectSQL, boolean loadChapters)
		Corpus c = new Corpus(initalSQL,true);
		
		System.out.println("created Corpus");
		clock.printElapsedTime();
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");
		
		SortedMap<String, Integer> tmpGrandCompositeGrams = c.getGrandCompositeGrams();
		System.out.println("totalTokens: "+tmpGrandCompositeGrams.size());
		
		runCompositeGramTF_IDFFeature(c, tmpGrandCompositeGrams, fileNameBase);		
		for(int chap=1; chap<=25; chap++){				
			tmpGrandCompositeGrams = c.getGrandCompositeGrams(chap);
		
			System.out.println("chap "+chap+" totalTokens: "+tmpGrandCompositeGrams.size());
			runCompositeGramTF_IDFFeature(chap, c, tmpGrandCompositeGrams, fileNameBase);
		}
		clock.printElapsedTime();		
	}
	
	private static void runCompositeGramTF_IDFFeature(int chap, Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams,String fileNameBase){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, fileNameBase+"Chap"+String.format("%02d", chap));
		CU.writeVectorToFile(tmp.toArray(new String[0]), fileNameBase+"Chap"+String.format("%02d", chap));
		CU.writeVectorToFile(c.getManuscriptLabels(chap), fileNameBase+"Chap"+String.format("%02d", chap));
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams, chap);
		System.out.println("chap "+chap+" finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, fileNameBase+"Chap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(chap,tmpGrandCompositeGrams);
		System.out.println("chap "+chap+" finished normalize");	
		c.writeCurrentCosineMatrix(chap,tmpGrandCompositeGrams, fileNameBase+"Chap"+String.format("%02d", chap));
		System.out.println("chap "+chap+" finished");		
	}	
	private static void runCompositeGramTF_IDFFeature(Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams,String fileNameBase){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, fileNameBase+"GlobalCounts");
		CU.writeVectorToFile(tmp.toArray(new String[0]),fileNameBase+"FeatureVector");
		CU.writeVectorToFile(c.getManuscriptLabels(), fileNameBase+"ManuscriptNameVector");
    	
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams);
		System.out.println("finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, fileNameBase+"IDFFeatureMatrix");
		System.out.println("finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(tmpGrandCompositeGrams);
		System.out.println("finished normalize");
		c.writeCurrentCosineMatrix(tmpGrandCompositeGrams, fileNameBase+"CosineMatrix");
		System.out.println("finished");
		
	}	
}

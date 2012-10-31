package com.eddiedunn.greek.data;

import java.util.ArrayList;
import java.util.SortedMap;

import com.eddiedunn.util.CU;
import com.eddiedunn.util.StopWatch;

public class DataGenerator {

	private String initialSQL;
	private String fileNameBase;
	private boolean loadDB;
	private boolean loadChapters;
	private boolean loadRData;
	

	public DataGenerator(String fileNameBase, String initalSQL){
		this.fileNameBase=fileNameBase;
		this.initialSQL=initalSQL;
		this.loadDB = false;
		this.loadChapters=false;
		this.loadRData = false;
	}	
	
	public void runAndWriteResults(){
		StopWatch clock = new StopWatch("started Corpus creation");
		//public Corpus(String selectSQL, boolean loadChapters)
		Corpus c = new Corpus(initialSQL,this.loadChapters);
		
		System.out.println("created Corpus");
		if( loadDB)
		   c.setLoadGramsIntoDB();
		
		c.initializeDB();
		clock.printElapsedTime();
		System.out.println("considering "+c.getManuScripts().size()+" manuscripts");
		
		SortedMap<String, Integer> tmpGrandCompositeGrams = c.getGrandCompositeGramsSum();
		System.out.println("totalTokens: "+tmpGrandCompositeGrams.size());
		
		runCompositeGramTF_IDFFeature(c, tmpGrandCompositeGrams, fileNameBase);		
		if(loadChapters){
			for(int chap=1; chap<=25; chap++){				
				tmpGrandCompositeGrams = c.getGrandCompositeGramsSum(chap);
			
				System.out.println("chap "+chap+" totalTokens: "+tmpGrandCompositeGrams.size());
				runCompositeGramTF_IDFFeature(chap, c, tmpGrandCompositeGrams, fileNameBase);
			}
		}
		clock.printElapsedTime();
		if(loadRData){
			System.out.println("starting to load "+fileNameBase+" data into R");
			StopWatch Rclock = new StopWatch("started "+fileNameBase+" R data load");
			RDataLoader ldr = new RDataLoader(fileNameBase);
			if(loadChapters)
				ldr.setLoadChapters();			
			ldr.setLoadExistingWorkspace();
			ldr.setSaveChanges();
			ldr.readDataSetIntoR();
			System.out.println("finished load "+fileNameBase+" data into R");
			Rclock.printElapsedTime();
		}
	}
	private static void runCompositeGramTF_IDFFeature(int chap, Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams,String fileNameBase){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, fileNameBase+"Chap"+String.format("%02d", chap)+"GlobalCounts");
		CU.writeCountMapToFile(c.getGrandCompositeGramsCount(chap), fileNameBase+"Chap"+String.format("%02d", chap)+"GlobalIDFCounts");
		CU.writeVectorToFile(tmp.toArray(new String[0]), fileNameBase+"Chap"+String.format("%02d", chap)+"FeatureVector");
		CU.writeVectorToFile(c.getManuscriptLabels(chap), fileNameBase+"Chap"+String.format("%02d", chap)+"ManuscriptNameVector");
		
		c.calculateTF_IDF_CompositeGramWeights( tmpGrandCompositeGrams, chap);
		System.out.println("chap "+chap+" finished calculate");
		c.writeCurrentTFIDFFeatureMatrix(tmpGrandCompositeGrams, fileNameBase+"Chap"+String.format("%02d", chap)+"IDFFeatureMatrix");
		System.out.println("chap "+chap+" finished write feature matrix");
		c.calculateNormalizedCompositeGramWeights(chap,tmpGrandCompositeGrams);
		System.out.println("chap "+chap+" finished normalize");	
		c.writeCurrentCosineMatrix(chap,tmpGrandCompositeGrams, fileNameBase+"Chap"+String.format("%02d", chap)+"CosineMatrix");
		System.out.println("chap "+chap+" finished");		
	}	
	private static void runCompositeGramTF_IDFFeature(Corpus c, SortedMap<String, Integer> tmpGrandCompositeGrams,String fileNameBase){
		ArrayList<String> tmp = new ArrayList<String>(tmpGrandCompositeGrams.keySet());
		CU.writeCountMapToFile(tmpGrandCompositeGrams, fileNameBase+"GlobalCounts");
		CU.writeCountMapToFile(c.getGrandCompositeGramsCount(), fileNameBase+"GlobalIDFCounts");
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
	public void setLoadDB() {
		this.loadDB = true;
	}	
	public void setLoadChapters() {
		this.loadChapters = true;
	}
	
	public void setLoadRData() {
		this.loadRData = true;
	}
	public void unSetLoadDB() {
		this.loadDB = false;
	}	
	public void unSetLoadChapters() {
		this.loadChapters = false;
	}
	
	public void unSetLoadRData() {
		this.loadRData = false;
	}	
}

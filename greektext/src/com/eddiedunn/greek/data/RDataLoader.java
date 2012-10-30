package com.eddiedunn.greek.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import rcaller.RCaller;
import rcaller.RCode;

import com.eddiedunn.util.CU;
import com.eddiedunn.util.TreeMapArrayListLengthValueComparator;

public class RDataLoader {

	/**
	 * @param args
	 */
	String dataBase;
	boolean loadChapters;
	boolean loadExistingWorkspace;
	boolean saveChanges;
	
	
	public RDataLoader(String dataBase) {		
		this.dataBase = dataBase;
		this.loadChapters=false;
		this.loadExistingWorkspace=false;
		this.saveChanges=true;
	}


	public void readDataSetIntoR(){
    	try {

    	      RCaller caller = new RCaller();
    	      RCode code = new RCode();

    	      caller.setRscriptExecutable(CU.Rexecutable);
    	      
    	      String[] manuscriptLabelVector = CU.readStringVectorFromFile(dataBase+"ManuscriptNameVector");
    	      String[] featureLabelVector = CU.readStringVectorFromFile(dataBase+"FeatureVector");
    	      double[][] matrix = CU.readMatrixFromFile(dataBase+"CosineMatrix", manuscriptLabelVector.length);
    	      double[][] data = CU.readDataFrameFromFile(dataBase+"IDFFeatureMatrix", manuscriptLabelVector.length, featureLabelVector.length);
    	   


    	      code.addRCode("library(vegan)");
    	      if( loadExistingWorkspace )
    	    	  code.addRCode("load(\"C:/Users/tmwsiy/workspace/greektext/output/AllData.RData\")");    	      
    	      code.addDoubleMatrix(dataBase+".cosine", matrix);
    	      code.addDoubleMatrix(dataBase, data);
    	      
    	      code.addStringArray("rlabels", manuscriptLabelVector);    	     
    	      code.addRCode("clabels <- scan(\"c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"FeatureVector.txt\",what=character(),sep=\",\",nlines=1,encoding=\"UTF-8\")");
    	      
    	      code.addRCode("rownames("+dataBase+") <- rlabels");
    	      code.addRCode("colnames("+dataBase+") <- clabels");
    	      code.addRCode("rownames("+dataBase+".cosine) <- rlabels");
    	      code.addRCode("colnames("+dataBase+".cosine) <- rlabels");
    	      
    	      code.addRCode(dataBase+".dist <- vegdist("+dataBase+")");
    	      code.addRCode(dataBase+".mds0 <- monoMDS("+dataBase+".dist)");
    	      if(saveChanges)
    	    	  code.addRCode("save.image(\"C:/Users/tmwsiy/workspace/greektext/output/AllData.RData\")");
    	      
    	     

    	      caller.setRCode(code);
    	      caller.redirectROutputToConsole();
    	      System.out.println("calling R "+dataBase);
    	      caller.runOnly();
    	      System.out.println("finished R.");
    	      
    	      if(loadChapters)
    	    	  readChapters();
    	      

    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }			
	}
	private void readChapters(){
		for (int chapter = 1; chapter <= 25; chapter++) {
			// String.format("%02d", chapter)
			String chapDataBase = dataBase+"Chap"+String.format("%02d", chapter);
			try {
		
		      RCaller caller = new RCaller();
		      RCode code = new RCode();
		
		      caller.setRscriptExecutable(CU.Rexecutable);
		      
		      String[] manuscriptLabelVector = CU.readStringVectorFromFile(chapDataBase+"ManuscriptNameVector");
		      String[] featureLabelVector = CU.readStringVectorFromFile(chapDataBase+"FeatureVector");
		      double[][] matrix = CU.readMatrixFromFile(chapDataBase+"CosineMatrix", manuscriptLabelVector.length);
		      double[][] data = CU.readDataFrameFromFile(chapDataBase+"IDFFeatureMatrix", manuscriptLabelVector.length, featureLabelVector.length);
		   
		      if( loadExistingWorkspace )
		    	  code.addRCode("load.image(\"C:/Users/tmwsiy/workspace/greektext/output/AllData.RData\")");
		
		      code.addRCode("library(vegan)");
		      code.addDoubleMatrix(chapDataBase+".cosine", matrix);
		      code.addDoubleMatrix(chapDataBase, data);
		      
		      code.addStringArray("rlabels", manuscriptLabelVector);    	     
		      code.addRCode("clabels <- scan(\"c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"FeatureVector.txt\",what=character(),sep=\",\",nlines=1,encoding=\"UTF-8\")");
		      
		      code.addRCode("rownames("+chapDataBase+") <- rlabels");
		      code.addRCode("colnames("+chapDataBase+") <- clabels");
		      code.addRCode("rownames("+chapDataBase+".cosine) <- rlabels");
		      code.addRCode("colnames("+chapDataBase+".cosine) <- rlabels");
		      
		      code.addRCode(chapDataBase+".dist <- vegdist("+chapDataBase+")");
		      code.addRCode(chapDataBase+".mds0 <- monoMDS("+chapDataBase+".dist)");
		      code.addRCode(chapDataBase+".mds <- metaMDS("+chapDataBase+".dist)");
		      
		      
    	      if(saveChanges)
     	    	 code.addRCode("save.image(\"C:/Users/tmwsiy/workspace/greektext/output/AllData.RData\")");
		      
		     
		
		      caller.setRCode(code);
		      //caller.redirectROutputToConsole();
		      System.out.println("calling R "+chapDataBase);
		      caller.runOnly();
		      System.out.println("finished R.");
		      
		      if(loadChapters)
		    	  readChapters();
		      
		
		    } catch (Exception e) {
		      e.printStackTrace();
		    }		
		}
	}

	public void setLoadChapters() {
		this.loadChapters = true;
	}
	public void setLoadExistingWorkspace() {
		this.loadExistingWorkspace = true;
	}	
	
	public void unSetSaveChanges() {
		this.saveChanges = false;
	}


	public void setSaveChanges() {
		this.saveChanges = true;
	}


	public void UnsetLoadChapters() {
		this.loadChapters = false;
	}
	public void UnsetLoadExistingWorkspace() {
		this.loadExistingWorkspace = false;
	}	
	
	
}

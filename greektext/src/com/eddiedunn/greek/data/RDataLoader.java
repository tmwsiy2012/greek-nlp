package com.eddiedunn.greek.data;

import java.util.ArrayList;

import rcaller.RCaller;
import rcaller.RCode;

import com.eddiedunn.util.CU;

public class RDataLoader {

	/**
	 * @param args
	 */
	String dataBase;
	boolean loadChapters;
	boolean loadExistingWorkspace;
	boolean saveChanges;
	String saveChangesFileName;
	
	
	public RDataLoader(String dataBase) {		
		this.dataBase = dataBase;
		this.loadChapters=false;
		this.loadExistingWorkspace=false;
		this.saveChanges=false;
		this.saveChangesFileName = dataBase;
	}

	

	public void setSaveChangesFileName(String saveChangesFileName) {
		this.saveChangesFileName = saveChangesFileName;
	}



	public void readDataSetIntoR(){
    	try {
    		ArrayList<String> code = new ArrayList<String>();
 	      


    	      code.add("library(vegan)");
    	      if( loadExistingWorkspace )
    	    	  code.add("load('C:/Users/tmwsiy/workspace/greektext/output/"+saveChangesFileName+".RData')");    	          	      
    	      
    	      code.add("rlabels <- scan('c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"ManuscriptNameVector.txt',what=character(),sep=',',nlines=1)");
    	      code.add("clabels <- scan('c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"FeatureVector.txt',what=character(),sep=',',nlines=1,encoding='UTF-8')");
    	      code.add(dataBase+" <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"IDFFeatureMatrix.txt', header=FALSE)");
    	      code.add(dataBase+".cosine <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"CosineMatrix.txt', header=FALSE)");
    	      code.add("rownames("+dataBase+") <- rlabels");
    	      code.add("colnames("+dataBase+") <- clabels");
    	      code.add("rownames("+dataBase+".cosine) <- rlabels");
    	      code.add("colnames("+dataBase+".cosine) <- rlabels");
    	      
    	      
    	      code.add(dataBase+".gc <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"GlobalCounts.txt', header=FALSE,encoding='UTF-8')");	
    	      code.add("colnames("+dataBase+".gc) <- c('gram','count')");
    	      code.add(dataBase+".idfgc <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+dataBase+"GlobalIDFCounts.txt', header=FALSE,encoding='UTF-8')");
    	      code.add("colnames("+dataBase+".idfgc) <- c('gram','count')");
    	      
    	      code.add(dataBase+".dist <- vegdist("+dataBase+")");
    	      code.add(dataBase+".mds0 <- monoMDS("+dataBase+".dist)");
    	      code.add(dataBase+".mds <- metaMDS("+dataBase+".dist)");
    	      if(saveChanges)
    	    	  code.add("save('C:/Users/tmwsiy/workspace/greektext/output/"+saveChangesFileName+".RData')");
    	      
    	     
     	      RCaller caller = new RCaller();
     	      RCode rCode = new RCode();
     	      caller.setRscriptExecutable(CU.Rexecutable);  
     	      for(String rCommand: code){
     	    	  //System.out.println(rCommand);
     	    	  rCode.addRCode(rCommand);
     	      }
 		     caller.setRCode(rCode);
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
			String chapSaveChangesFileName = saveChangesFileName+"Chap"+String.format("%02d", chapter);
			if( ! dataBase.equalsIgnoreCase(saveChangesFileName))
				chapSaveChangesFileName = saveChangesFileName;
			try {
				// first build up arraylist of R commands
	    	      ArrayList<String> code = new ArrayList<String>();

		      if( loadExistingWorkspace )
		    	  code.add("load('C:/Users/tmwsiy/workspace/greektext/output/"+chapSaveChangesFileName+".RData')");
		
		      code.add("library(vegan)");  	     
		      code.add("clabels <- scan('c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"FeatureVector.txt',what=character(),sep=',',nlines=1,encoding='UTF-8')");		      
    	      code.add("rlabels <- scan('c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"ManuscriptNameVector.txt',what=character(),sep=',',nlines=1)");    	      
    	      code.add(chapDataBase+" <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"IDFFeatureMatrix.txt', header=FALSE)");
    	      code.add(chapDataBase+".cosine <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"CosineMatrix.txt', header=FALSE)");		      		      
		      code.add("rownames("+chapDataBase+") <- rlabels");
		      code.add("colnames("+chapDataBase+") <- clabels");
		      code.add("rownames("+chapDataBase+".cosine) <- rlabels");
		      code.add("colnames("+chapDataBase+".cosine) <- rlabels");
		      
    	      code.add(chapDataBase+".gc <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"GlobalCounts.txt', header=FALSE,encoding='UTF-8')");	
    	      code.add("colnames("+chapDataBase+".gc) <- c('gram','count')");
    	      code.add(chapDataBase+".idfgc <- read.csv('c:/users/tmwsiy/workspace/greektext/output/"+chapDataBase+"GlobalIDFCounts.txt', header=FALSE,encoding='UTF-8')");
    	      code.add("colnames("+chapDataBase+".idfgc) <- c('gram','count')");		      
		      
		      code.add(chapDataBase+".dist <- vegdist("+chapDataBase+")");
		      code.add(chapDataBase+".mds0 <- monoMDS("+chapDataBase+".dist)");
		      code.add(chapDataBase+".mds <- metaMDS("+chapDataBase+".dist)");
		      
		      
    	      if(saveChanges)
     	    	 code.add("save('C:/Users/tmwsiy/workspace/greektext/output/"+chapSaveChangesFileName+".RData')");
		      
		     
    	     // RCaller caller = new RCaller();
    	     // RCode rCode = new RCode();
    	      //caller.setRscriptExecutable(CU.Rexecutable);  
    	      for(String rCommand: code){
    	    	  System.out.println(rCommand);
    	    	  //rCode.addRCode(rCommand);
    	      }
		     // caller.setRCode(rCode);
		     // caller.redirectROutputToConsole();
		      //System.out.println("calling R "+chapDataBase);
		     // caller.runOnly();
		     // System.out.println("finished R.");		      
		      
		
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

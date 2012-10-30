package com.eddiedunn.greek.data;

import rcaller.RCaller;
import rcaller.RCode;

import com.eddiedunn.util.CU;

public class RImageGenerator {

	String dataBase;
	boolean doChapters;
	boolean loadExistingWorkspace;
	boolean saveChanges;	
	
	public RImageGenerator(String dataBase) {		
		this.dataBase = dataBase;
		this.doChapters=false;
		this.loadExistingWorkspace=false;
		this.saveChanges=false;
	}

	public void writeImageSet(){
    	try {

  	      RCaller caller = new RCaller();
  	      RCode code = new RCode();

  	      caller.setRscriptExecutable(CU.Rexecutable);
  	      


  	      code.addRCode("library(vegan)");
  	      if( loadExistingWorkspace )
  	    	  code.addRCode("load(\"C:/Users/tmwsiy/workspace/greektext/output/AllData.RData\")");    	      
   	     

  	      code.addRCode("rownames("+dataBase+") <- rlabels");
  	      code.addRCode("colnames("+dataBase+") <- clabels");
  	      code.addRCode("rownames("+dataBase+".cosine) <- rlabels");
  	      code.addRCode("colnames("+dataBase+".cosine) <- rlabels");
  	      
  	      code.addRCode(dataBase+".dist <- vegdist("+dataBase+")");
  	      code.addRCode(dataBase+".mds0 <- monoMDS("+dataBase+".dist)");
  	      
  	      
  	      if( saveChanges )
  	    	  code.addRCode("save.image(\"C:/Users/tmwsiy/workspace/greektext/output/AllData.RData\")");
  	      
  	     

  	      caller.setRCode(code);
  	      caller.redirectROutputToConsole();
  	      System.out.println("calling R "+dataBase);
  	      caller.runOnly();
  	      System.out.println("finished R.");
  	      
  	      if(doChapters)
  	    	  runChapters();
  	      

  	    } catch (Exception e) {
  	      e.printStackTrace();
  	    }				
	}
	private void runChapters(){
		
	}
	
	public void setDoChapters() {
		this.doChapters = true;
	}
	public void setLoadExistingWorkspace() {
		this.loadExistingWorkspace = true;
	}	
	public void UnsetDoChapters() {
		this.doChapters = false;
	}
	public void UnsetLoadExistingWorkspace() {
		this.loadExistingWorkspace = false;
	}		
	public void unSetSaveChanges() {
		this.saveChanges = false;
	}


	public void setSaveChanges() {
		this.saveChanges = true;
	}	
}

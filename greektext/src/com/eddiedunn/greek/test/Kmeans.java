package com.eddiedunn.greek.test;


import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import rcaller.RCaller;
import rcaller.RCode;

import com.eddiedunn.util.CU;


public class Kmeans {
	
	private SortedMap<String,String[]> results;
	private int maxClusterSize;
    
    public static void main(String[] args){
    	int numClusters=15;
    	String[] labels = CU.readManuscriptNames(false);    	
        new Kmeans(CU.readMatrixFromFile("4charGramCosineMatrix", labels.length),labels,numClusters );
    }
    
    public Kmeans(double[][] data, String[] labels, int numClusters){
    	try {
    		  results = new TreeMap<String,String[]>();
    		  maxClusterSize = 0;
    	      RCaller caller = new RCaller();
    	      RCode code = new RCode();

    	      caller.setRscriptExecutable("C:\\Program Files\\R\\R-2.15.1\\bin\\i386\\Rscript.exe");

    	      
    	      //double[][] data = CU.readMatrixFromFile("3charGramCosineMatrix", labels.length);
    	   

    	      code.addDoubleMatrix("data", data);
    	      
    	      code.addRCode("out <- kmeans(data,center="+numClusters+",nstart=10000)");
    	      code.addRCode("clusters <- out$cluster");

    	      caller.setRCode(code);
    	      caller.runAndReturnResult("clusters");
    	      
    	      int[] KMresults;
    	      SortedMap<String,ArrayList<String>> tmpdata = new TreeMap<String,ArrayList<String>>(); 
    	      KMresults = caller.getParser().getAsIntArray("clusters");
    	    for (int i = 0; i < labels.length; i++) {
    	    	String family = String.format("%02d", KMresults[i]);
    	    	if(! tmpdata.containsKey(family) ){
    	    		ArrayList<String> tmp = new ArrayList<String>();
    	    		tmp.add(labels[i]);
    	    		tmpdata.put(family, tmp );
    	    	}else{
    	    		ArrayList<String> tmp = tmpdata.get(family);
    	    		tmp.add(labels[i]);
    	    		tmpdata.put(family, tmp );    	    		
    	    	}
				
			  }

    	    for( SortedMap.Entry<String,ArrayList<String>> cluster : tmpdata.entrySet()) {
    	    	if( cluster.getValue().size() > maxClusterSize)
    	    		maxClusterSize = cluster.getValue().size();
    	    	results.put(cluster.getKey(), cluster.getValue().toArray(new String[0]));
    	    }

    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    }

	public SortedMap<String,String[]> getResults() {
		return results;
	}

	public int getMaxClusterSize() {
		return maxClusterSize;
	}
 

}


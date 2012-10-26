package com.eddiedunn.greek.test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import rcaller.RCaller;
import rcaller.RCode;

import com.eddiedunn.util.CU;
import com.eddiedunn.util.TreeMapArrayListLengthValueComparator;


public class Kmeans {
	
	private ArrayList<String[]> results;
	private int maxClusterSize;
    
    public static void main(String[] args){
    	int numClusters=15;
    	String[] labels = CU.readManuscriptNames(false);    	
        new Kmeans(CU.readMatrixFromFile("4charGramCosineMatrix", labels.length),labels,numClusters );
    }
    
    public Kmeans(double[][] data, String[] labels, int numClusters){
    	try {
    		  results = new ArrayList<String[]>();
    		  maxClusterSize = 0;
    	      RCaller caller = new RCaller();
    	      RCode code = new RCode();

    	      caller.setRscriptExecutable(CU.Rexecutable);

    	      
    	      //double[][] data = CU.readMatrixFromFile("3charGramCosineMatrix", labels.length);
    	   

    	      code.addDoubleMatrix("data", data);
    	      
    	      code.addRCode("out <- kmeans(data,center="+numClusters+",nstart=10000)");
    	      code.addRCode("clusters <- out$cluster");

    	      caller.setRCode(code);
    	      caller.runAndReturnResult("clusters");
    	      
    	      int[] KMresults;
    	      HashMap<String,ArrayList<String>> tmpdata = new HashMap<String,ArrayList<String>>(); 
      	      TreeMapArrayListLengthValueComparator tmalv = new TreeMapArrayListLengthValueComparator(tmpdata);
      	      SortedMap<String,ArrayList<String>> sortedTmpdata = new TreeMap<String,ArrayList<String>>(tmalv); 
      	    
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
    	    sortedTmpdata.putAll(tmpdata);
    	    
    	    for( Map.Entry<String,ArrayList<String>> cluster : sortedTmpdata.entrySet()) {
    	    	if( cluster.getValue().size() > maxClusterSize)
    	    		maxClusterSize = cluster.getValue().size();
    	    	System.out.println("cluster size after sort: "+cluster.getValue().size());
    	    	results.add(cluster.getValue().toArray(new String[0]));
    	    }

    	    } catch (Exception e) {
    	      e.printStackTrace();
    	    }
    }

	public ArrayList<String[]> getResults() {
		return results;
	}

	public int getMaxClusterSize() {
		return maxClusterSize;
	}
 

}


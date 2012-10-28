package com.eddiedunn.greek.test;


import rcaller.RCaller;
import rcaller.RCode;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.greek.data.DataReader;
import com.eddiedunn.util.CU;


public class SVMTest {
    
    public static void main(String[] args){
        new SVMTest();
    }
    
    public SVMTest(){
    	try {

    	      RCaller caller = new RCaller();
    	      RCode code = new RCode();

    	      caller.setRscriptExecutable("C:\\Program Files\\R\\R-2.15.1\\bin\\i386\\Rscript.exe");
    	      //code.addRCode("install.packages(\"Runiversal\", repos=\" http://cran.r-project.org\")");
    	      //code.addRCode("install.packages(\"e1071\", repos=\"http://cran.wustl.edu\")");
    	      
    	     
    	      //Corpus c = new Corpus((new DataReader()).getManuScripts());
    	      Corpus c = new Corpus(CU.selectAllManuscriptsSQL,false);
    	      boolean isWord =false; // true for ngrams false for char sequences
    	      int sizeOfFeature=2;
    	      int levelsToMatch=1; // levels deep to match DS numbers
    	      double[][] train =  c.getTrainingSetMatrix(isWord, sizeOfFeature, levelsToMatch);
    	      double[][] test =  c.getTestingSetMatrix(isWord, sizeOfFeature, levelsToMatch);
    	      String[] features = c.getTrainingFeatureLabels(isWord, sizeOfFeature);
    	      String[] scriptsTrain = c.getTrainingManuscriptLabels();
    	      String[] scriptsTest = c.getTestingManuscriptLabels();
    	      if( train[0].length != test[0].length){
    	    	  System.out.println("feature space not the same, exiting...");
    	    	  System.exit(0);
    	      }
    	      System.out.println("got data");
    	      
    	      //CU.printMatrix(train);
    	      CU.writeMatrixToFileWithHeaders(train, features, scriptsTrain, "trainingTest");
    	      CU.writeMatrixToFileWithHeaders(test, features, scriptsTest, "testingTest");
    	      //CU.writeMatrixToFile(train, "trainingTest");
    	      //CU.writeMatrixToFile(test, "testingTest");
    	      c.printFamilies(1);
    	      
/*    	      int classColumn = train[0].length;
    	      code.addDoubleMatrix("train", train);
    	      code.addDoubleMatrix("test", test);
    	      code.addStringArray("features", features);
    	      code.addStringArray("scriptsTrain", scriptsTrain);
    	      code.addStringArray("scriptsTest", scriptsTest);
    	      
    	      //code.addRCode("colnames(train) <- features");
    	      //code.addRCode("row.names(train) <- scriptsTrain");
    	      
    	     // code.addRCode("colnames(test) <- features");
    	     // code.addRCode("row.names(test) <- scriptsTest");    	      
    	      
    	      code.addRCode("svm.model <- svm(train[,-"+classColumn+"] ~ .,scale=TRUE,type=\"C-classification\",data=train,cost=10,gamma=.0001)");
    	      code.addRCode("svm.pred <- predict(svm.model, test[,-"+classColumn+"])");
    	      code.addRCode("outputt <- table(pred = svm.pred, true = test[,"+classColumn+"])");
    	      caller.setRCode(code);

    	      caller.runAndReturnResult("outputt");
    	      
    	      int[] results;
    	      String[] labels = CU.readManuscriptsKeyFromFile(103);
    	      
    	      for (int i = 0; i < labels.length; i++) {
				System.out.print(labels[i]+" ");
			  }
    	      System.out.println();
    	      results = caller.getParser().getAsIntArray("clusters");
    	      for (int i = 0; i < results.length; i++) {
				System.out.print(results[i]+"   ");
			  }
*/

    	    } catch (Exception e) {
    	      System.out.println(e.toString());
    	    }
    }
		
}


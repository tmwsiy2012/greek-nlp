package com.eddiedunn.old;

import com.eddiedunn.greek.data.Corpus;
import com.eddiedunn.util.CU;

public class RRawCountDataGenerator {

    /**
     * @param args
     */
    public static void main(String[] args) {
	
	boolean onlyOld = false;
	Corpus c = new Corpus(CU.selectOldManuscriptsSQL,false);
	
	 boolean isWord =true; // true for ngrams false for char sequences
    int sizeOfFeature=1;
    int levelsToMatch=1; // levels deep to match DS numbers
    double[][] matrix =  c.getDataSetMatrix(isWord, sizeOfFeature);
    String[] features = c.getFullDataSetFeatureLabels(isWord, sizeOfFeature);
    String[] labels = CU.readManuscriptNames(onlyOld);
    CU.writeMatrixToFileWithHeaders(matrix, features, labels, "1NgramCountMatrix");
    
    }

}

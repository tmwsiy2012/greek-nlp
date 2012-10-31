package com.eddiedunn.greek;

import com.eddiedunn.greek.data.OneOffDataGenerator;
import com.eddiedunn.util.CU;

public class RunOneOffDataGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OneOffDataGenerator dataWriter = null;
		String[] baseFileNameList = {"TEST","fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selecttestSQL,CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		for (int i = 1;  i<initialSQLList.length; i++) {
			dataWriter = new OneOffDataGenerator(baseFileNameList[i], initialSQLList[i]);
			dataWriter.setLoadChapters();
			//dataWriter.setLoadDB();
			//dataWriter.setLoadRData();
			dataWriter.runAndWriteResults();
		}
			
	}

}

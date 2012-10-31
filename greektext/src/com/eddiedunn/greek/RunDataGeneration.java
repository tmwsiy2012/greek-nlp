package com.eddiedunn.greek;

import com.eddiedunn.greek.data.DataGenerator;
import com.eddiedunn.util.CU;
import com.eddiedunn.util.StopWatch;

public class RunDataGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataGenerator dataWriter = null;
		String[] baseFileNameList = {"TEST","fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selecttestSQL,CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		StopWatch clock = new StopWatch("start of data generation run");
		for (int i = 1;  i<initialSQLList.length; i++) {
			dataWriter = new DataGenerator(baseFileNameList[i], initialSQLList[i]);
			dataWriter.setLoadChapters();
			//if( i == 1)
			//	dataWriter.setLoadDB();
			dataWriter.setLoadRData();			
			dataWriter.runAndWriteResults();			
		}
		clock.printElapsedTime();
	}

}

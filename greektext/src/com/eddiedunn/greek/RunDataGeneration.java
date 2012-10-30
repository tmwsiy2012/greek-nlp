package com.eddiedunn.greek;

import com.eddiedunn.greek.data.DataGenerator;
import com.eddiedunn.util.CU;

public class RunDataGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataGenerator gen = null;
		String[] baseFileNameList = {"fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		for (int i = 0;  i<initialSQLList.length; i++) {
			gen = new DataGenerator(baseFileNameList[i],initialSQLList[i]);
			gen.setLoadChapters();
			gen.setLoadRData();
			gen.runAndWriteResults();
		}
			
	}

}

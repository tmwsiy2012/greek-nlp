package com.eddiedunn.greek;

import com.eddiedunn.util.CU;
import com.eddiedunn.util.StopWatch;

public class RunKmeansExcel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KmeansExcelFileWriter dataWriter = null;
		String[] baseFileNameList = {"TEST","fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selecttestSQL,CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		StopWatch clock = new StopWatch("start of data generation run");
		for (int i = 2;  i<3/*baseFileNameList.length*/; i++) {
			dataWriter = new KmeansExcelFileWriter(baseFileNameList[i],initialSQLList[i]);
			dataWriter.runMe();
		}
		clock.printElapsedTime();
	}

}

package com.eddiedunn.greek;

import com.eddiedunn.greek.data.RImageGenerator;
import com.eddiedunn.util.CU;

public class RunDataGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RImageGenerator imageWriter = null;
		String[] baseFileNameList = {"fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		for (int i = 0;  i<initialSQLList.length; i++) {
			imageWriter = new RImageGenerator(baseFileNameList[i]);
			imageWriter.setDoChapters();
			imageWriter.setLoadExistingWorkspace();
			imageWriter.writeImageSet();
		}
			
	}

}

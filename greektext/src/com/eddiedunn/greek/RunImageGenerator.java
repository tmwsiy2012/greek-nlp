package com.eddiedunn.greek;

import com.eddiedunn.greek.data.RImageGenerator;
import com.eddiedunn.util.CU;

public class RunImageGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RImageGenerator ldr = null;
		String[] baseFileNameList = {"fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		for (int i = 0;  i<initialSQLList.length; i++) {
			ldr = new RImageGenerator(baseFileNameList[i]);
			ldr.setLoadExistingWorkspace();
			ldr.setDoChapters();	
			ldr.setSaveChanges();
			ldr.writeImageSet();
		}
	}

}

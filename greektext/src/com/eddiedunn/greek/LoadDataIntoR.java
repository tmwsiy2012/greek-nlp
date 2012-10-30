package com.eddiedunn.greek;

import com.eddiedunn.greek.data.RDataLoader;
import com.eddiedunn.util.CU;

public class LoadDataIntoR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RDataLoader ldr = null;
		String[] baseFileNameList = {"fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};
		String[] initialSQLList = {CU.selectAllManuscriptsSQL,CU.selectOldManuscriptsSQL,CU.selectAllManuscriptsRemoveOutliersSQL,CU.selectAllManuscriptsONLYOutliersSQL,CU.selectAllManuscriptsMostCorrolatedHalfSQL,CU.selectAllManuscriptsLeastCorrolatedHalfSQL};
		for (int i = 0;  i<initialSQLList.length; i++) {
			ldr = new RDataLoader(baseFileNameList[i]);
			ldr.setLoadChapters();
			ldr.setLoadExisting();		
			ldr.readDataSetIntoR();
		}
	}

}

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
		for (int i = 1; i<2 /*i<initialSQLList.length*/; i++) {
			ldr = new RDataLoader(baseFileNameList[i]);
			ldr.setLoadChapters();
			ldr.setSaveChangesFileName("PJdata");
			//ldr.setLoadExistingWorkspace();		
			ldr.setSaveChanges();
			ldr.readDataSetIntoR();
		}
	}

}

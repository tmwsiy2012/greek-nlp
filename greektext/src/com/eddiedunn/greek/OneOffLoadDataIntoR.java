package com.eddiedunn.greek;

import com.eddiedunn.greek.data.OneOffRDataLoader;
import com.eddiedunn.greek.data.RDataLoader;
import com.eddiedunn.util.CU;

public class OneOffLoadDataIntoR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OneOffRDataLoader ldr = null;
		String[] baseFileNameList = {"fullSet","onlyOld","removeOutliers","onlyOutliers","mostCorrolatedHalf","leastCorrolatedHalf"};		
		for (int i = baseFileNameList.length-1;i>=0; i--) {
			ldr = new OneOffRDataLoader(baseFileNameList[i]);
			ldr.setLoadChapters();
			//ldr.setSaveChangesFileName("PJdata");
			//ldr.setLoadExistingWorkspace();		
			//ldr.setSaveChanges();
			ldr.readDataSetIntoR();
		}
	}

}

package com.eddiedunn.greek.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.eddiedunn.util.CU;

public class TestExcel {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
		    Workbook wb = new HSSFWorkbook();
		    CreationHelper createHelper = wb.getCreationHelper();
		    FileOutputStream fileOut = new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\workbook.xls");
		    
		    Sheet sheet1 = wb.createSheet("new sheet");
		    

		    
	    	int numClusters=60;
	    	String[] labels = CU.readManuscriptNames(false);    	
	        Kmeans km = new Kmeans(CU.readMatrixFromFile("4charGramCosineMatrix", labels.length),labels,numClusters );
	        SortedMap<String,String[]> results = km.getResults();
	        System.out.println("max cluster size: "+km.getMaxClusterSize());
	        //ArrayList<Row> tmpRows = new ArrayList<Row>();
	        for (int i = 0; i < km.getMaxClusterSize(); i++) {
				//tmpRows.add(sheet1.createRow(i));
	        	Row thisRow = sheet1.createRow(i);
	        	//System.out.println("new row: "+i);
	        	int j =0;
	        	for(SortedMap.Entry<String,String[]> cluster: results.entrySet()){
	        		
		        	if(i < cluster.getValue().length ){
		        		thisRow.createCell(j).setCellValue(createHelper.createRichTextString("m"+cluster.getValue()[i]));
		        	}else{
		        		thisRow.createCell(j).setCellValue("");
		        	}	
		        	j++;
	        	}
			}
	        
	        
		    	
		    
		    wb.write(fileOut);
		    fileOut.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}

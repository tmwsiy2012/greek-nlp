package com.eddiedunn.greek;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.eddiedunn.greek.test.Kmeans;
import com.eddiedunn.greek.test.PAMKmeans;
import com.eddiedunn.util.CU;

public class WriteKmeansExcelFile {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
		    Workbook wb = new HSSFWorkbook();
		    CreationHelper createHelper = wb.getCreationHelper();
		    FileOutputStream fileOut = new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\kmeansResults.xls");
		    
		    Sheet sheet1 = wb.createSheet("Entire Doc");
		    		    
	    	int numClusters=33;
	    	
	    	// first run for entire document
	    	String[] labels = CU.readStringVectorFromFile("compositeGramManuscriptNameVectorFull");      	
	        PAMKmeans km = new PAMKmeans(CU.readMatrixFromFile("CompositeGramCosineMatrixFull", labels.length),labels,numClusters );
	        ArrayList<String[]> results = km.getResults();
	        System.out.println("max cluster size: "+km.getMaxClusterSize());
	        //ArrayList<Row> tmpRows = new ArrayList<Row>();
	        for (int i = 0; i < km.getMaxClusterSize(); i++) {
				//tmpRows.add(sheet1.createRow(i));
	        	Row thisRow = sheet1.createRow(i);
	        	//System.out.println("new row: "+i);
	        	int j =0;
	        	for(String[] cluster: results){
	        		
		        	if(i < cluster.length ){
		        		thisRow.createCell(j).setCellValue(createHelper.createRichTextString("m"+cluster[i]));
		        	}else{
		        		thisRow.createCell(j).setCellValue("");
		        	}	
		        	j++;
	        	}
			}
	        
	        for (int chapter = 1; chapter <= 25; chapter++) {
	        	sheet1 = wb.createSheet("Chapter "+String.format("%02d", chapter));
		    	labels = CU.readStringVectorFromFile("compositeGramManuscriptNameVectorFullChap"+String.format("%02d", chapter));   	
		        km = new PAMKmeans(CU.readMatrixFromFile("CompositeGramCosineMatrixFullChap"+String.format("%02d", chapter), labels.length),labels,numClusters );
		        results = km.getResults();
		        System.out.println("max cluster size: "+km.getMaxClusterSize());
		        //ArrayList<Row> tmpRows = new ArrayList<Row>();
		        for (int i = 0; i < km.getMaxClusterSize(); i++) {
					//tmpRows.add(sheet1.createRow(i));
		        	Row thisRow = sheet1.createRow(i);
		        	//System.out.println("new row: "+i);
		        	int j =0;
		        	for(String[] cluster: results){
		        		
			        	if(i < cluster.length ){
			        		thisRow.createCell(j).setCellValue(createHelper.createRichTextString("m"+cluster[i]));
			        	}else{
			        		thisRow.createCell(j).setCellValue("");
			        	}	
			        	j++;
		        	}
				}	        	
			}
		    	
		    
		    wb.write(fileOut);
		    fileOut.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}

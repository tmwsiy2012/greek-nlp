package com.eddiedunn.greek.test;

import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.POITextExtractor;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hwpf.extractor.WordExtractor;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.extractor.ExcelExtractor;

import com.eddiedunn.util.CU;

public class TestExtractWordFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
try {
	


		//FileInputStream fis = new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\PBV Pure 01 08 12.doc");
		FileInputStream fis = new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\PBV Pure 01 08 12.doc");


		//FileInputStream fis = new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\test\\PJ 06\\BDGZ 6 rev\\BDGZ 6 09\\BDGZ06 09 verse.doc");

		POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
		
		WordExtractor extract = new WordExtractor(fileSystem);


		Pattern squareBlocks = Pattern.compile(CU.metadataSquareBracketBlocks);
		Pattern teePeeBlocks = Pattern.compile(CU.checkForTeePee);
		String[] paragraphs = extract.getParagraphText();
		StringBuffer buf = new StringBuffer(extract.getText());
		
		String pbv = extract.getText();
		//System.out.println(extract.getText());
//		for (int i = 0; i < paragraphs.length; i++) {
			//Matcher sbm = squareBlocks.matcher(paragraphs[i]);
			//Matcher tpb = teePeeBlocks.matcher(paragraphs[i]);
			//while( sbm.find()){
				pbv = pbv.replaceAll(CU.metadataSquareBracketBlocks, "" );
				//System.out.println(pbv);System.out.println(pbv);
			//}
			//while(tpb.find()){
				pbv = pbv.replaceAll(CU.checkForTeePee, "" );
				
				//pbv = pbv.replaceAll("\\s{2,}", " ");
				//System.out.println(pbv);
			//}
		//}
		//for (int i = 0; i < paragraphs.length; i++) {
		    
		//}
		System.out.println(pbv.replace("\n", "").replace("\r", ""));

} catch (Exception e) {
	e.printStackTrace();
}		
}

}

package com.eddiedunn.greek.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class TestUnderlineDetection {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        try {		
		FileInputStream fis = new FileInputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\test\\PJ 06\\BDGZ 6 rev\\BDGZ 6 09\\BDGZ06 09 verse.doc");
		POIFSFileSystem fs = new POIFSFileSystem(fis);
        HWPFDocument doc = new HWPFDocument(fs);
        WordExtractor we = new WordExtractor(doc);
        ArrayList<String> titles = new ArrayList<String>();


            for (int i = 0; i < we.getText().length() - 2; i++) {
                int startIndex = i;
                int endIndex = i + 1;
                Range range = new Range(startIndex, endIndex, doc);
                //System.out.println(we.getText().length()+" "+startIndex+" "+endIndex);
                CharacterRun cr = range.getCharacterRun(0);

                if (cr.isBold() || cr.isItalic() || cr.getUnderlineCode() != 0) {
                    while (cr.isBold() || cr.isItalic() || cr.getUnderlineCode() != 0) {
                        i++;
                        endIndex += 1;
                        range = new Range(endIndex, endIndex + 1, doc);
                        cr = range.getCharacterRun(0);
                    }
                    range = new Range(startIndex, endIndex - 1, doc);
                    System.out.println(range.text());
                    titles.add(range.text());
                } 

            }
      
        }
        catch (IndexOutOfBoundsException iobe) {
           iobe.printStackTrace();
        }
        catch(IllegalArgumentException e1){
        	
        }
        catch(IOException e2){
        	
        }
	}

}

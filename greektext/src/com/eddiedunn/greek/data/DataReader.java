package com.eddiedunn.greek.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DataReader {
	String currentText = "";
	String currentBaseText = "";
	String currentChap = "";
	SortedMap<String, StringBuffer> documents = new TreeMap<String, StringBuffer>();
	String dataDirStr = System.getenv("USERPROFILE")
			+ "\\workspace\\greektext\\data\\text\\";
	int minDocSizeForConsideration = 500;

	ArrayList<String> rawData = new ArrayList<String>();
	ArrayList<String> flattenedData = new ArrayList<String>();

	public DataReader() {

		readData();
		flattenBaseTextLines();
		//System.out.println(flattenedData);
		parseAndWriteDataLines();
		//System.out.println(documents);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	private void parseAndWriteDataLines() {
		for (String line : flattenedData) {
			if( line.split("[0-9]").length > 1){
				boolean isBaseText = false;
				if (line.contains("<base")) {
					isBaseText = true;
				}
				//System.out.println("cleanString: " + cleanStr(line));
				writeDataLine(buildDataLineObject(cleanStr(line), isBaseText));
			}
		}
	}

	private void writeDataLine(DataLine writeMe) {
		if (writeMe.safeToWrite && writeMe.greekText != null
				&& !writeMe.isMissing) {
			String[] scripts = writeMe.getManuScriptNumbers();

			String textToApply = writeMe.greekText;
			if (writeMe.isAddition)
				textToApply = writeMe.currentBaseText + " " + writeMe.greekText;

			for (String manuscriptNumber : scripts) {
				if (documents.containsKey(manuscriptNumber.trim())) {
					documents.get(manuscriptNumber.trim()).append(textToApply);
				} else {
					documents.put(manuscriptNumber.trim(), new StringBuffer());
					documents.get(manuscriptNumber.trim()).append(textToApply);
				}
			}
		}
	}

	private void flattenBaseTextLines() {

		String[] tmpdata = new String[rawData.size()];
		for (int i = 0; i < rawData.size(); i++) {
			tmpdata[i] = rawData.get(i);
		}
		for (int i = 0; i < rawData.size(); i++) {
			if (tmpdata[i].contains("<base")) {
				flattenedData.add(tmpdata[i].trim() + " " + tmpdata[i + 1]);
				i++;
			} else {
				flattenedData.add(tmpdata[i].trim());
			}
		}

	}

	private String cleanStr(String cleanMe) {
		String str1 = cleanMe.replaceAll("<baseText>", "");
		String str2 = str1.replaceAll("</baseText>", "");
		String str3 = str2.replaceAll("<corrected>", "");
		String str4 = str3.replaceAll("</corrected>", "");
		String str5 = str4.replaceAll("<transposition>", "");
		String str6 = str5.replaceAll("</transposition>", "");
		String str7 = str6.replaceAll("\\[\\]", "");
		String str8 = str7.replaceAll(". . .", "");
		String str9 = str8.trim();
		return str9;
	}

	private DataLine splitLine(String lineToSplit, boolean isBaseText) {
		// System.out.println("splitline: "+lineToSplit);
		// find first index of a number character, decrement index by 1 and
		// split the line there
		DataLine returnValue = new DataLine();
		returnValue.setBaseText(isBaseText);
		for (int i = 0; i < lineToSplit.length(); i++) {
			if (lineToSplit.substring(i, i + 1).matches("[0-9]")) {
				returnValue.setGreekText(lineToSplit.substring(0, i).trim());
				returnValue.setManuScriptNumbers(lineToSplit
						.substring(i, lineToSplit.length()).trim().split(" "));
				break;
			}
			if (i == lineToSplit.length() - 1) {
				// System.out.println("No numbers found.. bad");
				returnValue.setSafeToWrite(false);
			}
		}
		return returnValue;
	}

	private DataLine buildDataLineObject(String str, boolean isBaseText) {

		DataLine returnValue = splitLine(str, isBaseText);
		returnValue.setGreekText(returnValue.greekText.trim());
		if (isBaseText) {
			//
			currentBaseText = returnValue.greekText;
		} else {
			// regular line
			if (str.startsWith("+")) {
				returnValue.setGreekText(returnValue.greekText.replace("+", "")
						.trim());
				returnValue.setAddition(true);
			}
			if (str.startsWith("-")) {
				returnValue.setGreekText(returnValue.greekText.replace("-", "")
						.trim());
				returnValue.setMissing(true);
			}
		}

		returnValue.setCurrentBaseText(currentBaseText);
		return returnValue;
	}

private void readData() {
		File dataDir = new File(dataDirStr);
		String[] chapters = dataDir.list();

		for (String chapter : chapters) {
			if (!chapter.equalsIgnoreCase(".svn")) {
				File tmpChap = new File(dataDirStr + chapter + "\\");
				String[] verses = tmpChap.list();
				for (String verse : verses) {
					if (!verse.equalsIgnoreCase(".svn")) {
						readFile(chapter, verse);
					}
				}
			}
		}
	}

	private void readFile(String chapter, String verse) {
		File content = new File(dataDirStr + chapter + "\\" + verse);
		String str = "";

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					content), "UTF8"));

			while ((str = in.readLine()) != null) {
				// first level GLOBAL filter stuff goes here anything where we
				// can throw out the entire line
				if (!str.startsWith("#") && str.length() > 0
						&& !str.contains("Chap") && !str.contains(":")
						&& !str.trim().equals("[] 110")
						&& !str.contains("<transpοsitiοn"))
					rawData.add(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e2) {
				System.err.println("Error: " + e2.getMessage());
			}
		}
	}

/*	public SortedMap<String, Manuscript> getManuScripts() {
		SortedMap<String, Manuscript> manuScripts = new TreeMap<String, Manuscript>();
		SortedMap<String, String> familys = this.getFamilies();
		for (Map.Entry<String, StringBuffer> m : documents.entrySet()) {

			// only consider those with
			if (m.getValue().toString().length() > minDocSizeForConsideration) {
				Manuscript tmpScript = new Manuscript(m.getKey(), m.getValue()
						.toString(), familys.get(m.getKey()));
				manuScripts.put(m.getKey(), tmpScript);
			} else {
				// System.out.println("thrown out for size doc: "+m.getKey()+" size:"+m.getValue().toString().length());
			}
		}

		return manuScripts;
	}*/
	private SortedMap<String, String> getFamilies(){
	    SortedMap<String, String> returnValue = new TreeMap<String, String>(); 
	    
		File content = new File(System.getenv("USERPROFILE")+"\\workspace\\greektext\\data\\manuscriptIDtable.txt");
		String str = "";

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					content), "UTF8"));

			while ((str = in.readLine()) != null) {
			    String[] dataLine = str.split(",");
			    if( dataLine.length >= 2 )
				returnValue.put(dataLine[0].trim(), dataLine[1].trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e2) {
				System.err.println("Error: " + e2.getMessage());
			}
		}	    
	    
	    return returnValue;
	}
	
}

package com.eddiedunn.greek.data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.eddiedunn.util.CU;
import com.mysql.jdbc.PreparedStatement;

public class Corpus {


    private boolean loadChapters;
    private boolean writeGramsToDB;
    private String initialSQL;
    private int maxGramSize;
    SortedMap<String, Manuscript> manuScripts;

    

    public Corpus(String initialSQL, boolean loadChapters) {
    this.loadChapters = loadChapters;
    this.writeGramsToDB=false;
    this.initialSQL=initialSQL;
	 this.maxGramSize=0;
    }
    public void setLoadGramsIntoDB(){
    	this.writeGramsToDB=true;
    }
    public void initializeDB(){
    	loadManuscriptsFromDB(getManuscriptIDs(initialSQL));
    	System.out.println("max gram size was: "+maxGramSize);    	
    	printDocumentStats();
    	if( writeGramsToDB ){
    		resetValuesforThisManuscriptInDB();
    		writeGlobalGramsToDB(this.getGrandCompositeGramsSum());
    		for (int chapter = 1; chapter <= 25; chapter++) {
				writeChapterGlobalGramsToDB(chapter, this.getGrandCompositeGramsSum(chapter));
			}
    	}
    }
    private void loadManuscriptsFromDB(ArrayList<Integer> manuscriptids){
    	manuScripts = new TreeMap<String, Manuscript>();    	
    	int count =0;
    	int testMod = manuscriptids.size()/4;
    	for(Integer mid: manuscriptids){
    		count++;
    		Manuscript tmp = getManuscript(mid);
    		if(tmp != null ){
    			if( tmp.getMaxGramSize() > maxGramSize)
    				maxGramSize = tmp.getMaxGramSize();
    		manuScripts.put(tmp.getID(),tmp);    		
    		}
    		if( count % testMod == 0 ){
    			System.out.println("loaded "+count+" manuscripts current: "+tmp.getID());    			
    		}    		
    	}
    }
    private void printDocumentStats(){
		double sum=0;
		for(Map.Entry<String, Manuscript> m : this.getManuScripts().entrySet() ){
			int currentDocLength = m.getValue().getText().length();
			sum += currentDocLength;
			//System.out.println(m.getKey()+" size: "+CU.humanReadableByteCount(currentDocLength, true));
		}
		double avg = sum/this.getManuScripts().size();
		System.out.println("Average Doc Length: "+CU.humanReadableByteCount((long)avg, true));
		
		if(this.loadChapters)
		for (int chapter = 1; chapter <= 25; chapter++) {
			sum=0;
			SortedMap<String, String> chapTexts = this.getChapDocsName(chapter);
			for(Map.Entry<String, String> chapText : chapTexts.entrySet() ){
				int currentDocLength = chapText.getValue().length();
				sum += currentDocLength;
				//System.out.println(chapText.getKey()+" chap:"+String.format("%02d", chapter)+" size: "+CU.humanReadableByteCount(currentDocLength, true));
			}
			avg = sum/this.getManuScripts().size();
			System.out.println("Average Doc Length Chapter: "+String.format("%02d", chapter)+" "+CU.humanReadableByteCount((long)avg, true));
		}    	
    }
    private Manuscript getManuscript(Integer manuscriptid){
    	Manuscript returnvalue = null;
    	
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;
        Connection con1 = null;
        Statement stmt1 = null;        
        ResultSet rs = null;	 
        ResultSet result = null;
        try {

	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        result = stmt.executeQuery("select m.name,v.verse_text,v.chapternumber,v.versenumber from verse v inner join manuscript m on m.manuscriptid=v.manuscriptid where v.manuscriptid="+manuscriptid+" order by v.chapternumber,v.versenumber;");
	        StringBuffer buf = new StringBuffer();
	        String name = null;
	        while(result.next()){
	        	buf.append(result.getString(2)+' ');
	        	name = result.getString(1);
	        }
	        TreeMap<Integer, String> tmpChapText = new TreeMap<Integer,String>();
	        
	        if(loadChapters && name != null && buf.toString().length() > CU.minDocumentLength){
	        	con1 = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        	tmpChapText = new TreeMap<Integer,String>();
	        	for(int chap=1; chap<=25;chap++){
	        		
	    	        stmt1 = con1.createStatement();	        		
	    	        rs = stmt1.executeQuery("select m.name,v.verse_text,v.chapternumber,v.versenumber from verse v inner join manuscript m on m.manuscriptid=v.manuscriptid where v.manuscriptid="+manuscriptid+" AND v.chapternumber="+chap+" order by v.chapternumber,v.versenumber;");
	    	        StringBuffer chapBuf = new StringBuffer();
	    	        
	    	        while(rs.next()){
	    	        	chapBuf.append(rs.getString(2)+' ');
	    	        	
	    	        }	
	    	       // System.out.println(chapBuf.toString());
	    	        if( chapBuf.toString().length() > CU.minChapLength )	    	        
	    	        tmpChapText.put(new Integer(chap), chapBuf.toString());
	        	}
	        	
	        	
	        	
	        		
	        }	        
	        returnvalue = new Manuscript(writeGramsToDB,loadChapters, manuscriptid,name,buf.toString(), "fam", tmpChapText);
	        
	} catch (Exception e) {
	   e.printStackTrace();
	   System.exit(0);
	}finally{
	    try {
	        result.close();
	        rs.close();
	        con1.close();
	        con.close();	
	    } catch (Exception e2) {
		
	    }
	    
	}	
        
    	return returnvalue;
    }
    
	public String getInitialSQL() {
		return initialSQL;
	}
	
	private void writeGlobalGramsToDB(SortedMap<String, Integer> globalGrams){
		
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	    Connection con = null;
	    Statement stmt = null;
	
	    try {
	
	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        
	        int count =0;
	        for(Map.Entry<String, Integer> g : globalGrams.entrySet() ){
	        	stmt.addBatch("insert into grams_global(gram,count) values('"+g.getKey()+"',"+g.getValue().intValue()+");");
	        	if( g.getValue().intValue() > this.maxGramSize )
	        		this.maxGramSize = g.getValue().intValue();
	        	count++;
	        	if( count % 10000 == 0)
	        		stmt.executeBatch();
	        }
	
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	
  
}
private void writeChapterGlobalGramsToDB(int chapter, SortedMap<String, Integer> globalGrams){
	
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	    Connection con = null;
	    Statement stmt = null;
	
	    try {
	
	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        int count =0;
	        for(Map.Entry<String, Integer> g : globalGrams.entrySet() ){
	        	stmt.addBatch("insert into grams_global_by_chap(chapternumber,gram,count) values("+chapter+",'"+g.getKey()+"',"+g.getValue().intValue()+");");
	        	if( count % 1000 == 0)
	        		stmt.executeBatch();	        	
	        }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	
  	
}	    
private void resetValuesforThisManuscriptInDB(){

    try {
        Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
    }
    Connection con = null;
    Statement stmt = null;

    try {

 
        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
        stmt = con.createStatement();
        
        
        stmt.executeUpdate("delete from grams_global;");
        stmt.executeUpdate("delete from grams_global_by_chap ;");

    
} catch (Exception e) {
    e.printStackTrace();
}finally{
    try {
        con.close();	
    } catch (Exception e2) {
	e2.printStackTrace();
    }
    
}	
}
    private ArrayList<Integer> getManuscriptIDs(String initialSQL){
    	ArrayList<Integer> manuscriptids = new ArrayList<Integer>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
        	
            ex.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;
         
        ResultSet result = null;
        try {

	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        String sql = initialSQL;
        
	        result = stmt.executeQuery(sql);	        
	        while (result.next()){
	            manuscriptids.add(result.getInt(1));	        	        
	        }
	        
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
	        result.close();
	        con.close();	
	    } catch (Exception e2) {
		e2.printStackTrace();
	    }
	    
	}	
        return manuscriptids;
    }



    public SortedMap<String, Manuscript> getManuScripts() {
	return manuScripts;
    }


    
    public void setManuScripts(SortedMap<String, Manuscript> manuScripts) {
	this.manuScripts = manuScripts;
    }

    public SortedMap<String, Integer> getSingleManuscriptNGrams(String mScript,
	    int size) {
	return manuScripts.get(mScript).getNGrams(size);
    }

    public SortedMap<String, Integer> getSingleManuscriptNCharGrams(
	    String mScript, int size) {
	return manuScripts.get(mScript).getNCharGrams(size);
    }

    public SortedMap<String, Integer> getGrandNGramsSum(int size) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    CU.mergeMapSum(tmp, m.getValue().getNGrams(size));
	}
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > 1  && ngramcount < this.manuScripts.size() )
		returnValue.put(gng.getKey(),gng.getValue());
	}	
	return returnValue;
    }

    
    public SortedMap<String, Integer> getGrandCompositeGramsSum() {
    	SortedMap<String, Integer> tmpCount = getGrandCompositeGramsCount();    	
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    CU.mergeMapSum(tmp, m.getValue().getCompositeGrams());
	}
	
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
		// getGrandCompositeGramsCoutnOnly returns grams that appear in more than one doc
		if( tmpCount.containsKey(gng.getKey()))
	    	returnValue.put(gng.getKey(),gng.getValue());
	}
	//CU.pruneMap(returnValue);
	return returnValue;
    }    
    
    public SortedMap<String, Integer> getGrandCompositeGramsCount() {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    CU.mergeMapCount(tmp, m.getValue().getCompositeGrams());
	}
	
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();	    
	    if( ngramcount > 1)
		returnValue.put(gng.getKey(),gng.getValue());
	    if( ngramcount > this.manuScripts.size() )
	    	System.out.println("Big Problem!");
	}
	//CU.pruneMap(returnValue);
	return returnValue;
    }      
    public SortedMap<String, Integer> getGrandCompositeGramsCount(int chap) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
		CU.mergeMapCount(tmp, m.getValue().getCompositeGrams(chap));
	}
	
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    //int ngramcount =  tmp.get(gng.getKey()).intValue();
	    //if( ngramcount >1 )
		returnValue.put(gng.getKey(),gng.getValue());
	}
	//CU.pruneMap(returnValue);
	return returnValue;
    }     
    public SortedMap<String, Integer> getGrandCompositeGramsSum(int chap) {
    	SortedMap<String, Integer> tmpCount = getGrandCompositeGramsCount(chap);   
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
		CU.mergeMapSum(tmp, m.getValue().getCompositeGrams(chap));
	}
	
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    //int ngramcount =  tmp.get(gng.getKey()).intValue();
	    //if( ngramcount >1 )
		if( tmpCount.containsKey(gng.getKey()))
			returnValue.put(gng.getKey(),gng.getValue());
	}
	//CU.pruneMap(returnValue);
	return returnValue;
    }  
    
    public SortedMap<String, Integer> getGrandNCharGramsSum(int size) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
		CU. mergeMapSum(tmp, m.getValue().getNCharGrams(size));
	}
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    //int ngramcount =  tmp.get(gng.getKey()).intValue();
	   // if( ngramcount > 1 )		
		returnValue.put(gng.getKey(),gng.getValue());
	}
	//CU.pruneMap(returnValue);
	return returnValue;
    }


    public void printManuscriptIDs() {
	int c1 = 1;
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    System.out.println(c1 + ": " + m.getValue().getID());
	    c1++;
	}
    }

    public void writeManuscriptFiles() {
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    BufferedWriter out = null;
	    try {
		out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(System.getenv("USERPROFILE")
				+ "\\Documents\\" + m.getKey() + ".txt")));
		// (new
		// FileOutputStream(System.getenv("USERPROFILE")+"\\Documents\\wordCount.csv"),"UTF8"));

		out.write(m.getValue().getText());
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		try {
		    out.close();
		} catch (Exception e2) {
		}
	    }
	}
    }

 
    public void calculateTF_IDF_CompositeGramWeights(SortedMap<String, Integer> tmpGrandCompositeGrams) {
	double totalDocs = this.manuScripts.size();
	resetTempWeights();
	
	double tf = 0;
	double idf = 0;
	double maxTF=0;
	double totalCountAcrossManuscripts = 0;		
	String maxFrequencyGram="";
	for (Map.Entry<String, Integer> grandCompositeGrams : tmpGrandCompositeGrams.entrySet()) {
		if( grandCompositeGrams.getValue().intValue() > maxTF){
			maxTF = grandCompositeGrams.getValue().intValue();
			maxFrequencyGram = grandCompositeGrams.getKey();
		}
	}
	System.out.println("Largest frequency: "+maxTF+" gram: *"+maxFrequencyGram+"*");
	SortedMap<String, Integer> grandCompositeGramsCount = getGrandCompositeGramsCount();
	for (Map.Entry<String, Integer> grandCompositeGrams : tmpGrandCompositeGrams.entrySet()) {
	    totalCountAcrossManuscripts = grandCompositeGramsCount.get(grandCompositeGrams.getKey()).doubleValue();	    
	    for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {		
		tf = 0;
		if (m.getValue().getCompositeGrams().containsKey(grandCompositeGrams.getKey())){
		    tf = m.getValue().getCompositeGrams().get(grandCompositeGrams.getKey()).doubleValue()/maxTF;		    
		}
		idf = Math.log(totalDocs / totalCountAcrossManuscripts)/ Math.log(2);
		m.getValue().getnGramWeight().put(grandCompositeGrams.getKey(), tf * idf);
	    }
	    
	}
    }
    public void calculateTF_IDF_CompositeGramWeights(SortedMap<String, Integer> tmpGrandCompositeGrams, int chap) {
    	SortedMap<Integer, String> chapText = getChapDocs(chap);
	double totalDocs = chapText.size();
	resetTempWeights();
	double tf = 0;
	double idf = 0;
	double maxTF=0;
	double totalCountAcrossManuscripts = 0;	
	String maxFrequencyGram="";
	for (Map.Entry<String, Integer> grandCompositeGrams : tmpGrandCompositeGrams.entrySet()) {
		if( grandCompositeGrams.getValue().intValue() > maxTF){
			maxTF = grandCompositeGrams.getValue().intValue();
			maxFrequencyGram = grandCompositeGrams.getKey();
		}
	}	
	System.out.println("Largest frequency: "+maxTF+" gram: *"+maxFrequencyGram+"*");
	SortedMap<String, Integer> grandCompositeGramsCount = getGrandCompositeGramsCount(chap);
	for (Map.Entry<String, Integer> grandCompositeGrams : tmpGrandCompositeGrams.entrySet()) {
	    totalCountAcrossManuscripts = grandCompositeGramsCount.get(grandCompositeGrams.getKey()).doubleValue();
	    
	    for (Map.Entry<Integer, String> manuscriptid : chapText.entrySet()) {
	    Manuscript	m = getManuscriptAfter(manuscriptid.getKey());
		
		tf = 0;
		if (m.getCompositeGrams(chap).containsKey(grandCompositeGrams.getKey())){
		    tf = m.getCompositeGrams(chap).get(grandCompositeGrams.getKey()).doubleValue()/maxTF;		    
		}
		idf = Math.log(totalDocs / totalCountAcrossManuscripts)/ Math.log(2);
		m.getnGramWeight().put(grandCompositeGrams.getKey(), tf * idf);
	    }
	    
	}
    }    
    public void calculateNormalizedCompositeGramWeights(int chap, SortedMap<String, Integer> tmpGrandCompositeGrams) {
    	double len2 = 0;
    	double len = 0;
    	SortedMap<Integer, String> chapText = getChapDocs(chap);
    	for (Map.Entry<Integer, String> manuscriptid : chapText.entrySet()) {
    		Manuscript	m = getManuscriptAfter(manuscriptid.getKey());
	    len2 = 0;
	   
	    for (Map.Entry<String, Integer> grandNCharGrams : tmpGrandCompositeGrams.entrySet()) {
		len2 += m.getnGramWeight().get(grandNCharGrams.getKey())
			.doubleValue()
			* m.getnGramWeight()
				.get(grandNCharGrams.getKey()).doubleValue();
	    }
	    len = Math.sqrt(len2);
	    for (Map.Entry<String, Integer> word : tmpGrandCompositeGrams.entrySet()) {
		

		    m.getnGramUnitWeight()
			    .put(word.getKey(),
				    m.getnGramWeight()
					    .get(word.getKey()).doubleValue()
					    / len);
		
	    }
	    
	}
    }    

    public void writeCurrentCosineMatrix(int chap,SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	BufferedWriter out = null;
    	try {
            out = new BufferedWriter(new OutputStreamWriter
            		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));	
	
	SortedMap<Integer, String> chapText = getChapDocs(chap);
	double charGramSum = 0;
	for (Map.Entry<Integer, String> manuscriptid : chapText.entrySet()) {
		Manuscript	mi = getManuscriptAfter(manuscriptid.getKey());
		 StringBuffer line= new StringBuffer(); 
			for (Map.Entry<Integer, String> manuscriptidj : chapText.entrySet()) {
				Manuscript	mj = getManuscriptAfter(manuscriptidj.getKey());
		charGramSum = 0;
		for (Map.Entry<String, Integer> word : tmpGrandNCharGrams.entrySet()) {
		    charGramSum += mi.getnGramUnitWeight()
			    .get(word.getKey())
			    * mj.getnGramUnitWeight()
				    .get(word.getKey());
		}
		line.append(charGramSum+",");
		
	    }
	    out.write(line.substring(0, line.length()-1)+CU.newline);
	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}finally{
    		try {
    			out.close();
    		} catch (Exception e2) {}
    	}	
    }       
    public void writeCurrentCosineMatrix(SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	BufferedWriter out = null;
    	try {
            out = new BufferedWriter(new OutputStreamWriter
            		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));	
	
	double charGramSum = 0;
	for (Map.Entry<String, Manuscript> mi : this.manuScripts.entrySet()) {
		 StringBuffer line= new StringBuffer(); 
	    for (Map.Entry<String, Manuscript> mj : this.manuScripts.entrySet()) {
		charGramSum = 0;
		for (Map.Entry<String, Integer> word : tmpGrandNCharGrams.entrySet()) {
		    charGramSum += mi.getValue().getnGramUnitWeight()
			    .get(word.getKey())
			    * mj.getValue().getnGramUnitWeight()
				    .get(word.getKey());
		}
		line.append(charGramSum+",");
		
	    }
	    out.write(line.substring(0, line.length()-1)+CU.newline);
	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}finally{
    		try {
    			out.close();
    		} catch (Exception e2) {}
    	}	
    }      
    private Manuscript getManuscriptAfter(int manuscriptid){
    	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
    		if( m.getValue().getManuscriptID() == manuscriptid)
    			return m.getValue();
    	}
    	return null;
    }
    public SortedMap<Integer, String> getChapDocs(int chap){
    	SortedMap<Integer, String> returnValue = new TreeMap<Integer, String>();
    	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
    		if( m.getValue().getText(chap) != null && m.getValue().getText(chap).length() > CU.minChapLength)
    			returnValue.put(m.getValue().getManuscriptID(), m.getValue().getText(chap));
    	}
    	return returnValue;
    }
    public SortedMap<String, String> getChapDocsName(int chap){
    	SortedMap<String, String> returnValue = new TreeMap<String, String>();
    	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
    		if( m.getValue().getText(chap) != null && m.getValue().getText(chap).length() > CU.minChapLength)
    			returnValue.put(m.getValue().getID(), m.getValue().getText(chap));
    	}
    	return returnValue;
    }
    public void calculateTF_IDF_NGramWeights(int size, SortedMap<String, Integer> tmpGrandNCharGrams) {
	double totalDocs = this.manuScripts.size();
	double tf = 0;
	double idf = 0;
	double totalCountAcrossManuscripts = 0;
	resetTempWeights();
	for (Map.Entry<String, Integer> grandNGrams : tmpGrandNCharGrams.entrySet()) {
	    totalCountAcrossManuscripts = grandNGrams.getValue().doubleValue();
	    // System.out.println("totalAcrossAll manuscripts: "+totalCountAcrossManuscripts);
	    for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
		// System.out.println(grandNGrams.getKey()+": "+ totalCountAcrossManuscripts);
		tf = 0;
		if (m.getValue().getNGrams(size).containsKey(grandNGrams.getKey())){
		    tf = m.getValue().getNGrams(size).get(grandNGrams.getKey()).doubleValue();
		    //System.out.println("ngram" +grandNGrams.getKey() + "occurs in manuscript: "+m.getValue().getID()+" "+tf+" times");
		}
		idf = Math.log(totalDocs / totalCountAcrossManuscripts)/ Math.log(2);
		m.getValue().getnGramWeight().put(grandNGrams.getKey(), tf * idf);
	    }
	    
	}
    }
    public void calculateTF_IDF_CharNGramWeights(int size, SortedMap<String, Integer> tmpGrandNCharGrams) {
	double totalDocs = this.manuScripts.size();
	double tf = 0;
	double idf = 0;
	resetTempWeights();
	 double totalCountAcrossManuscripts = 0;
	for (Map.Entry<String, Integer> grandCharNGrams : tmpGrandNCharGrams.entrySet()) {
	    totalCountAcrossManuscripts = grandCharNGrams.getValue().doubleValue();
	    // System.out.println("totalAcrossAll manuscripts: "+totalCountAcrossManuscripts);
	    for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
		// System.out.println(grandNGrams.getKey()+": "+ totalCountAcrossManuscripts);
		tf = 0;
		if (m.getValue().getNCharGrams(size).containsKey(grandCharNGrams.getKey())){
		    tf = m.getValue().getNCharGrams(size).get(grandCharNGrams.getKey()).doubleValue();
		    //System.out.println("ngram" +grandNGrams.getKey() + "occurs in manuscript: "+m.getValue().getID()+" "+tf+" times");
		}
		 idf = Math.log(totalDocs / totalCountAcrossManuscripts)/ Math.log(2);
		m.getValue().getnGramWeight().put(grandCharNGrams.getKey(), tf * idf);
	    }
	    
	}
    }
    public void calculateNormalizednGramWeights(int size, SortedMap<String, Integer> tmpGrandNCharGrams) {
    	double len2 = 0;
    	double len = 0;
	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
	    len2 = 0;
	   // System.out.println(m.getKey());
	    for (Map.Entry<String, Integer> grandNGrams : tmpGrandNCharGrams.entrySet()) {
		len2 += m.getValue().getnGramWeight().get(grandNGrams.getKey())
			.doubleValue()
			* m.getValue().getnGramWeight()
				.get(grandNGrams.getKey()).doubleValue();
	    }
	    len = Math.sqrt(len2);
	    for (Map.Entry<String, Integer> word : this.getGrandNGramsSum(size)
		    .entrySet()) {
		

		    m.getValue()
			    .getnGramUnitWeight()
			    .put(word.getKey(),
				    m.getValue().getnGramWeight()
					    .get(word.getKey()).doubleValue()
					    / len);
		
	    }
	    //System.out.println(m.getValue().getnGramUnitWeight());
	}
    }
    public void calculateNormalizedCharNGramWeights(int size, SortedMap<String, Integer> tmpGrandNCharGrams) {
    	 double len2 = 0;
    	 double len = 0;
	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
		len2 = 0;
	   // System.out.println(m.getKey());
	    for (Map.Entry<String, Integer> grandNCharGrams : tmpGrandNCharGrams.entrySet()) {
		len2 += m.getValue().getnGramWeight().get(grandNCharGrams.getKey())
			.doubleValue()
			* m.getValue().getnGramWeight()
				.get(grandNCharGrams.getKey()).doubleValue();
	    }
	     len = Math.sqrt(len2);
	    for (Map.Entry<String, Integer> word : tmpGrandNCharGrams.entrySet()) {
		

		    m.getValue()
			    .getnGramUnitWeight()
			    .put(word.getKey(),
				    m.getValue().getnGramWeight()
					    .get(word.getKey()).doubleValue()
					    / len);
		
	    }
	    //System.out.println(m.getValue().getnGramUnitWeight());
	}
    }    
    public void calculateNormalizedCompositeGramWeights(SortedMap<String, Integer> tmpGrandCompositeGrams) {

    	double len = 0;
    	 double len2 = 0;
	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
	    len2 = 0;
	   // System.out.println(m.getKey());
	    for (Map.Entry<String, Integer> grandNCharGrams : tmpGrandCompositeGrams.entrySet()) {
		len2 += m.getValue().getnGramWeight().get(grandNCharGrams.getKey())
			.doubleValue()
			* m.getValue().getnGramWeight()
				.get(grandNCharGrams.getKey()).doubleValue();
	    }
	    len = Math.sqrt(len2);
	    for (Map.Entry<String, Integer> word : tmpGrandCompositeGrams.entrySet()) {
		

		    m.getValue()
			    .getnGramUnitWeight()
			    .put(word.getKey(),
				    m.getValue().getnGramWeight()
					    .get(word.getKey()).doubleValue()
					    / len);
		
	    }	   
	}
    }    

    
    public void writeCurrentTFIDFFeatureMatrix( SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	
	BufferedWriter out = null;
	try {
        out = new BufferedWriter(new OutputStreamWriter
        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));

        
	
	 for (Map.Entry<String, Manuscript> manuscript : this.manuScripts.entrySet()) {	 
	     StringBuffer line= new StringBuffer(); 
	     
	for (Map.Entry<String, Integer> grandGrams : tmpGrandNCharGrams.entrySet()) {

	   if( manuscript.getValue().getnGramWeight().containsKey(grandGrams.getKey()) )	{
	       line.append(manuscript.getValue().getnGramWeight().get(grandGrams.getKey())+",");
	   }else
	       line.append(0+",");

	    }
	    out.write(line.substring(0, line.length()-1)+CU.newline);	    
	}
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		try {
			out.close();
		} catch (Exception e2) {}
	}
    }  
    
    public void writeCurrentTFIDFFeatureMatrix(int chap, SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	
	BufferedWriter out = null;
	try {
        out = new BufferedWriter(new OutputStreamWriter
        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));

        SortedMap<String,String> tmpChapDocs =  getChapDocsName(chap);
	
	 for (Map.Entry<String, Manuscript> manuscript : this.manuScripts.entrySet()) {	 
		 if( tmpChapDocs.containsKey(manuscript.getKey()) &&  manuscript.getValue().getText(chap).length() > CU.minChapLength ){
	     StringBuffer line= new StringBuffer(); 
	     
	for (Map.Entry<String, Integer> grandGrams : tmpGrandNCharGrams.entrySet()) {

	   if( manuscript.getValue().getnGramWeight().containsKey(grandGrams.getKey()) )	{
	       line.append(manuscript.getValue().getnGramWeight().get(grandGrams.getKey())+",");
	   }else
	       line.append(0+",");

	    }
	    out.write(line.substring(0, line.length()-1)+CU.newline);	    
	}
	 }
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		try {
			out.close();
		} catch (Exception e2) {}
	}
    }  
    
    public void writeCurrentTFIDFFeatureMatrixWithHeaders( SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	
	BufferedWriter out = null;
	try {
        out = new BufferedWriter(new OutputStreamWriter
        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
        StringBuffer colHeaders = new StringBuffer();
        
        for (Map.Entry<String, Integer> grandGrams : tmpGrandNCharGrams.entrySet()) {
        	colHeaders.append(grandGrams.getKey()+",");
        }
        out.write("    "+colHeaders.substring(0, colHeaders.length()-1)+CU.newline);
        
	
	 for (Map.Entry<String, Manuscript> manuscript : this.manuScripts.entrySet()) {
	     StringBuffer line= new StringBuffer(); 
	     line.append(manuscript.getKey()+",");
	for (Map.Entry<String, Integer> grandGrams : tmpGrandNCharGrams.entrySet()) {
	   if( manuscript.getValue().getnGramWeight().containsKey(grandGrams.getKey()) )	{
	       line.append(manuscript.getValue().getnGramWeight().get(grandGrams.getKey())+",");
	   }else
	       line.append(0+",");

	    }
	    out.write(line.substring(0, line.length()-1)+CU.newline);	    
	}
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		try {
			out.close();
		} catch (Exception e2) {}
	}
    }  
    
    
    private void resetTempWeights() {
	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
	    m.getValue().resetNGramWeights();
	}
    }

  
    public SortedMap<Integer, String> getManuScriptsKey(){
	int i1=0;
	SortedMap<Integer, String> returnValue = new TreeMap<Integer, String>();
	for (Map.Entry<String, Manuscript> mi : this.manuScripts.entrySet()) {	
	    	returnValue.put(new Integer(i1) ,mi.getValue().getID());
		//System.out.println(i1+" "+mi.getValue().getID());
		i1++;
	}
	return returnValue;
    }


    
    public String[] getManuscriptLabels(){
    	return this.manuScripts.keySet().toArray(new String[0]);    	
    }
    public String[] getManuscriptLabels(int chap){
    	//manuscript.getValue().getnGramWeight().containsKey(grandGrams.getKey()) 
    	SortedMap<String,String> tmpChapDocs =  getChapDocsName(chap);
    	ArrayList<String> returnValue = new ArrayList<String>();
    	for(Map.Entry<String, Manuscript> mj : this.manuScripts.entrySet()){
    		if( tmpChapDocs.containsKey(mj.getKey())  )
    			returnValue.add(mj.getKey());
    	}
    	return returnValue.toArray(new String[0]);
    }


    public String[] getCompositeFeatureLabels(){   	
    	ArrayList<String> tmp = new ArrayList<String>(getGrandCompositeGramsSum().keySet());
    	
    	//tmp.add("Family");
    	return tmp.toArray(new String[0]);    	
    }      
    public String[] getFeatureLabels(boolean isWord, int size){
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getGrandNGramsSum(size);    		
    	}else{
    		globalGrams = getGrandNCharGramsSum(size);    		
    	}    	
    	ArrayList<String> tmp = new ArrayList<String>(globalGrams.keySet());
    	
    	//tmp.add("Family");
    	return tmp.toArray(new String[0]);    	
    }    
    
 
        
    
    
}

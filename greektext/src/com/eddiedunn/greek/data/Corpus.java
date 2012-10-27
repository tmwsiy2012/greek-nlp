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
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.eddiedunn.util.CU;

public class Corpus {


    private SortedMap<String, Manuscript> manuScripts;
    private ArrayList<String> trainingSet;
    private ArrayList<String> testingSet;
    private ArrayList<String> fullDataSet;
    private boolean loadChapters;
    

    public Corpus(boolean onlyOld, boolean loadChapters) {
    this.loadChapters=loadChapters;
	loadManuscriptsFromDB(getManuscriptIDs(onlyOld));
	createTrainTestSets(.5); // percent training
    }
    
    private void loadManuscriptsFromDB(ArrayList<Integer> manuscriptids){
    	manuScripts = new TreeMap<String, Manuscript>();
    	fullDataSet = new ArrayList<String>();
    	for(Integer mid: manuscriptids){
    		//System.out.println("grabbing manuscript: "+mid);
    		Manuscript tmp = getManuscript(mid);
    		if(tmp != null ){
    		manuScripts.put(tmp.getID(),tmp);
    		fullDataSet.add(tmp.getID());
    		}
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
	    	        String Tname = null;	
	    	        while(rs.next()){
	    	        	chapBuf.append(rs.getString(2)+' ');
	    	        	Tname = rs.getString(1);
	    	        }	
	    	       // System.out.println(chapBuf.toString());
	    	        if( chapBuf.toString().length() > 10 )	    	        
	    	        tmpChapText.put(new Integer(chap), chapBuf.toString());
	        	}
	        	
	        	
	        	
	        		
	        }	        
	        returnvalue = new Manuscript(loadChapters, manuscriptid,name,buf.toString(), "fam", tmpChapText);
	        
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
    private ArrayList<Integer> getManuscriptIDs(boolean onlyOld){
    	ArrayList<Integer> manuscriptids = new ArrayList<Integer>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
        	
            ex.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;	 
        ResultSet result = null;
        try {

	 
	        con = DriverManager.getConnection(CU.db_connstr, CU.db_username, CU.db_password);
	        stmt = con.createStatement();
	        String sql = CU.selectAllManuscriptsSQL;
	        if(onlyOld)
	            sql = CU.selectOldManuscriptsSQL;	        
	        result = stmt.executeQuery(sql);
	        int count=0;
	        while (result.next()){
	            manuscriptids.add(result.getInt(1));
	            count++;
	            //System.out.println("loaded "+result.getString(2));
	        }
	        //System.out.println("loaded "+count+" manuscripts from db");
	    
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
    public SortedMap<String,Integer> getUniqueFamilies(int levelToMatch){
    	SortedMap<String,Integer> tmp = new TreeMap<String,Integer>();
    	for(Map.Entry<String, Manuscript> m : manuScripts.entrySet() ){
    		String key = m.getValue().getFamily(levelToMatch);
    		if(tmp.containsKey(key)){
    			int curr = tmp.get(key)+1;
    			tmp.put(key, new Integer(curr));
    		}else
    			tmp.put(key, new Integer(1));
    	}    	
    	
    	return tmp;
    }

    private void createTrainTestSets(double percentTrain){
    	// randomly choose training/test sets
    	int numTrain = ((new Double (this.manuScripts.size()*percentTrain)).intValue())+1;
    	int numTest = this.manuScripts.size() - numTrain;
    	String[] realIDs = new String[this.manuScripts.size()];
    	int i=0;
    	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
    		realIDs[i] = m.getKey();
    		i++;
    	}
    	trainingSet = new ArrayList<String>();
    	testingSet = new ArrayList<String>();
    	boolean needMore = true;
    	i = 1;
    	while( needMore ){
    		int nextItem = new Random().nextInt(this.manuScripts.size());
    		if(! trainingSet.contains(realIDs[nextItem]) ){
    			trainingSet.add(realIDs[nextItem]);
    			i++;
    		}
    		if( i == numTrain )
    			needMore =false;
    	}
    	i=1;
    	needMore = true;
    	while( needMore ){
    		int nextItem = new Random().nextInt(this.manuScripts.size());
    		if(! trainingSet.contains(realIDs[nextItem]) &&  ! testingSet.contains(realIDs[nextItem])){
    			testingSet.add(realIDs[nextItem]);
    			i++;
    		}
    		if( i == numTest )
    			needMore =false;
    	}

    }
/*    public Manuscript getM(int mscriptID, String str, SortedMap<Integer,String> chapText) {
	return getManuscript(mscriptID, str, chapText);
    }*/

/*    public Manuscript getManuscript(int mscriptID,String str, SortedMap<Integer,String> chapText) {
	if (!manuScripts.containsKey(str)) {
	    manuScripts.put(str, new Manuscript(mscriptID,str, "", "", chapText));
	}
	return manuScripts.get(str);
    }*/

    public SortedMap<String, Manuscript> getManuScripts() {
	return manuScripts;
    }

    public SortedMap<String, Manuscript> getManuScripts(int chapter) {
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

    public SortedMap<String, Integer> getGrandNGrams(int size) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    mergeMapCount(tmp, m.getValue().getNGrams(size));
	}
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > 1  && ngramcount < this.manuScripts.size() )
		returnValue.put(gng.getKey(),gng.getValue());
	}	
	return returnValue;
    }
    public SortedMap<String, Integer> getTrainingGrandNGrams(int size) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (String m : this.trainingSet) {
	    mergeMapCount(tmp, this.manuScripts.get(m).getNGrams(size));
	}
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > 1  && ngramcount < this.trainingSet.size() )
	    	returnValue.put(gng.getKey(),gng.getValue());
	}	
	return returnValue;
    }
    
    public SortedMap<String, Integer> getGrandCompositeGrams() {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    mergeMapCount(tmp, m.getValue().getCompositeGrams());
	}
	
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > CU.grandCompositeMinCount && ngramcount < (this.manuScripts.size()-CU.grandCompositeMaxOffset) )
		returnValue.put(gng.getKey(),gng.getValue());
	}
	return returnValue;
    }    
    public SortedMap<String, Integer> getGrandCompositeGrams(int chap) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    mergeMapCount(tmp, m.getValue().getCompositeGrams(chap));
	}
	
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > CU.grandCompositeMinCount && ngramcount < (this.manuScripts.size()-CU.grandCompositeMaxOffset) )
		returnValue.put(gng.getKey(),gng.getValue());
	}
	return returnValue;
    }  
    
    public SortedMap<String, Integer> getGrandNCharGrams(int size) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (Map.Entry<String, Manuscript> m : manuScripts.entrySet()) {
	    mergeMapCount(tmp, m.getValue().getNCharGrams(size));
	}
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > 1 && ngramcount < this.manuScripts.size())
		returnValue.put(gng.getKey(),gng.getValue());
	}
	return returnValue;
    }
    public SortedMap<String, Integer> getTrainingGrandNCharGrams(int size) {
	SortedMap<String, Integer> tmp = new TreeMap<String, Integer>();
	for (String m : this.trainingSet) {
	    mergeMapCount(tmp, this.manuScripts.get(m).getNCharGrams(size));
	}
	SortedMap<String, Integer> returnValue = new TreeMap<String, Integer>();
	for (Map.Entry<String, Integer> gng : tmp.entrySet()) {
	    int ngramcount =  tmp.get(gng.getKey()).intValue();
	    if( ngramcount > 1 && ngramcount < this.trainingSet.size())
		returnValue.put(gng.getKey(),gng.getValue());
	}
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

    private void mergeMap(SortedMap<String, Integer> original,
	    SortedMap<String, Integer> oneToAdd) {
	for (Map.Entry<String, Integer> o : oneToAdd.entrySet()) {
	    if (original.containsKey(o.getKey())) {
		int tmp = original.get(o.getKey()).intValue()
			+ o.getValue().intValue();
		original.put(o.getKey(), new Integer(tmp));
	    } else {
		original.put(o.getKey(), new Integer(1));
	    }
	}
    }
    private void mergeMapCount(SortedMap<String, Integer> original,
	    SortedMap<String, Integer> oneToAdd) {
	for (Map.Entry<String, Integer> o : oneToAdd.entrySet()) {
	    if (original.containsKey(o.getKey())) {
		int tmp = original.get(o.getKey()).intValue()
			+ 1;
		original.put(o.getKey(), new Integer(tmp));
	    } else {
		original.put(o.getKey(), new Integer(1));
	    }
	}
    }    
    public void calculateTF_IDF_CompositeGramWeights(SortedMap<String, Integer> tmpGrandCompositeGrams) {
	double totalDocs = this.manuScripts.size();
	resetTempWeights();
	int count=0;
	double tf = 0;
	double idf = 0;
	double totalCountAcrossManuscripts = 0;		
	for (Map.Entry<String, Integer> grandCompositeGrams : tmpGrandCompositeGrams.entrySet()) {
		//if( count % 25 ==0 )
			//System.out.println("Processed "+count+" features.");
	    totalCountAcrossManuscripts = grandCompositeGrams.getValue().doubleValue();
	    // System.out.println("totalAcrossAll manuscripts: "+totalCountAcrossManuscripts);
	    for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
		// System.out.println(grandNGrams.getKey()+": "+ totalCountAcrossManuscripts);
		tf = 0;
		if (m.getValue().getCompositeGrams().containsKey(grandCompositeGrams.getKey())){
		    tf = m.getValue().getCompositeGrams().get(grandCompositeGrams.getKey()).doubleValue();
		    //System.out.println("ngram" +grandNGrams.getKey() + "occurs in manuscript: "+m.getValue().getID()+" "+tf+" times");
		}
		idf = Math.log(totalDocs / totalCountAcrossManuscripts)/ Math.log(2);
		m.getValue().getnGramWeight().put(grandCompositeGrams.getKey(), tf * idf);
	    }
	    count++;
	}
    }
    public void calculateTF_IDF_CompositeGramWeights(SortedMap<String, Integer> tmpGrandCompositeGrams, int chap) {
    	SortedMap<Integer, String> chapText = getChapDocs(chap);
	double totalDocs = chapText.size();
	resetTempWeights();
	int count=0;
	double tf = 0;
	double idf = 0;
	double totalCountAcrossManuscripts = 0;	
	for (Map.Entry<String, Integer> grandCompositeGrams : tmpGrandCompositeGrams.entrySet()) {
		//if( count % 25 ==0 )
			//System.out.println("Processed "+count+" features.");
	    totalCountAcrossManuscripts = grandCompositeGrams.getValue().doubleValue();
	    // System.out.println("totalAcrossAll manuscripts: "+totalCountAcrossManuscripts);
	    for (Map.Entry<Integer, String> manuscriptid : chapText.entrySet()) {
	    Manuscript	m = getManuscriptAfter(manuscriptid.getKey());
		// System.out.println(grandNGrams.getKey()+": "+ totalCountAcrossManuscripts);
		tf = 0;
		if (m.getCompositeGrams(chap).containsKey(grandCompositeGrams.getKey())){
		    tf = m.getCompositeGrams(chap).get(grandCompositeGrams.getKey()).doubleValue();
		    //System.out.println("ngram" +grandNGrams.getKey() + "occurs in manuscript: "+m.getValue().getID()+" "+tf+" times");
		}
		idf = Math.log(totalDocs / totalCountAcrossManuscripts)/ Math.log(2);
		m.getnGramWeight().put(grandCompositeGrams.getKey(), tf * idf);
	    }
	    count++;
	}
    }    
    public void calculateNormalizedCompositeGramWeights(int chap, SortedMap<String, Integer> tmpGrandCompositeGrams) {
    	double len2 = 0;
    	double len = 0;
    	SortedMap<Integer, String> chapText = getChapDocs(chap);
    	for (Map.Entry<Integer, String> manuscriptid : chapText.entrySet()) {
    		Manuscript	m = getManuscriptAfter(manuscriptid.getKey());
	    len2 = 0;
	   // System.out.println(m.getKey());
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
	    //System.out.println(m.getValue().getnGramUnitWeight());
	}
    }    
    public void writeCurrentCosineMatrix(int chap,SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	BufferedWriter out = null;
    	try {
            out = new BufferedWriter(new OutputStreamWriter
            		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));	
	int i = 0, j = 0;
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
	int i = 0, j = 0;
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
    		if( m.getValue().getText(chap) != null && m.getValue().getText(chap).length() > 10)
    			returnValue.put(m.getValue().getManuscriptID(), m.getValue().getText(chap));
    	}
    	return returnValue;
    }
    public SortedMap<String, String> getChapDocsName(int chap){
    	SortedMap<String, String> returnValue = new TreeMap<String, String>();
    	for (Map.Entry<String, Manuscript> m : this.manuScripts.entrySet()) {
    		if( m.getValue().getText(chap) != null && m.getValue().getText(chap).length() > 10)
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
	    for (Map.Entry<String, Integer> word : this.getGrandNGrams(size)
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
	    //System.out.println(m.getValue().getnGramUnitWeight());
	}
    }    

/*    public void writeCurrentNGramCosineMatrix(int size, SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	BufferedWriter out = null;
    	try {
            out = new BufferedWriter(new OutputStreamWriter
            		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));
	int i = 0, j = 0;
	for (Map.Entry<String, Manuscript> mi : this.manuScripts.entrySet()) {
		StringBuffer line= new StringBuffer(); 
	
	    for (Map.Entry<String, Manuscript> mj : this.manuScripts.entrySet()) {
		double ngramSum = 0;
		for (Map.Entry<String, Integer> word : tmpGrandNCharGrams.entrySet()) {
		    ngramSum += mi.getValue().getnGramUnitWeight()
			    .get(word.getKey())
			    * mj.getValue().getnGramUnitWeight()
				    .get(word.getKey());
		}
		line.append(ngramSum+",");

		j++;
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
    }*/
    
  
    
    public void writeCurrentTFIDFFeatureMatrix( SortedMap<String, Integer> tmpGrandNCharGrams, String fileName) {
    	
	BufferedWriter out = null;
	try {
        out = new BufferedWriter(new OutputStreamWriter
        		(new FileOutputStream(System.getenv("USERPROFILE")+"\\workspace\\greektext\\output\\"+fileName+".txt")));

        
	int i = 0, j = 0;
	 for (Map.Entry<String, Manuscript> manuscript : this.manuScripts.entrySet()) {
			if( i % 25 == 0){
				//System.out.println("processing manuscript number: "+i+" catalogid: "+manuscript.getKey());
			}		 
	     StringBuffer line= new StringBuffer(); 
	     
	for (Map.Entry<String, Integer> grandGrams : tmpGrandNCharGrams.entrySet()) {

	   if( manuscript.getValue().getnGramWeight().containsKey(grandGrams.getKey()) )	{
	       line.append(manuscript.getValue().getnGramWeight().get(grandGrams.getKey())+",");
	   }else
	       line.append(0+",");

		j++;
	    }
	    i++;
	    j = 0;
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
        
	int i = 0, j = 0,count=0;
	 for (Map.Entry<String, Manuscript> manuscript : this.manuScripts.entrySet()) {
	     StringBuffer line= new StringBuffer(); 
	     line.append(manuscript.getKey()+",");
	for (Map.Entry<String, Integer> grandGrams : tmpGrandNCharGrams.entrySet()) {
		if( count % 25 == 0){
			//System.out.println("processing manuscript number: "+count+" catalogid: "+manuscript.getKey());
		}
	   if( manuscript.getValue().getnGramWeight().containsKey(grandGrams.getKey()) )	{
	       line.append(manuscript.getValue().getnGramWeight().get(grandGrams.getKey())+",");
	   }else
	       line.append(0+",");

		j++;
	    }
	    i++;
	    j = 0;
	    out.write(line.substring(0, line.length()-1)+CU.newline);
	    count++;
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

    private double[][] getNewMatrix(int size) {
	double[][] matrix = new double[size][size];
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		matrix[i][j] = 0;
	    }
	}
	return matrix;
    }
    private double[][] getNewFeatureMatrix(int corpusSize, int featureCount) {
	double[][] matrix = new double[corpusSize][featureCount];
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix.length; j++) {
		matrix[i][j] = 0;
	    }
	}
	return matrix;
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

	public ArrayList<String> getTrainingSet() {
		return trainingSet;
	}

	public ArrayList<String> getTestingSet() {
		return testingSet;
	}
    public double[][] getDataSetMatrix(boolean isWord, int size){
    	double[][] matrix = null;
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getTrainingGrandNGrams(size);
    		matrix = new double[this.fullDataSet.size()][globalGrams.size()];
    	}else{
    		globalGrams = getTrainingGrandNCharGrams(size);
    		matrix = new double[this.fullDataSet.size()][globalGrams.size()];
    	}
    	 
    	
    	for (int i = 0; i < matrix.length; i++) {    		
    		SortedMap<String, Integer> currentGrams = null;
    		if( isWord ){
    			currentGrams = this.manuScripts.get(this.fullDataSet.get(i)).getNGrams(size);
    		}else {
    			currentGrams = this.manuScripts.get(this.fullDataSet.get(i)).getNCharGrams(size);
    		}    		
    		int j=0;
    		for(Map.Entry<String, Integer> mj : globalGrams.entrySet()){
    			if( currentGrams.containsKey(mj.getKey())){
    				matrix[i][j] = currentGrams.get(mj.getKey());
    			}else{
    				matrix[i][j] = 0;
    			}    				
    			j++;
    		}
    		
		}
    	return matrix;
    }	
    public double[][] getTrainingSetMatrix(boolean isWord, int size, int levelToMatch){
    	double[][] matrix = null;
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getTrainingGrandNGrams(size);
    		matrix = new double[this.trainingSet.size()][globalGrams.size()+1];
    	}else{
    		globalGrams = getTrainingGrandNCharGrams(size);
    		matrix = new double[this.trainingSet.size()][globalGrams.size()+1];
    	}
    	 
    	
    	for (int i = 0; i < matrix.length; i++) {    		
    		SortedMap<String, Integer> currentGrams = null;
    		if( isWord ){
    			currentGrams = this.manuScripts.get(this.trainingSet.get(i)).getNGrams(size);
    		}else {
    			currentGrams = this.manuScripts.get(this.trainingSet.get(i)).getNCharGrams(size);
    		}    		
    		int j=0;
    		for(Map.Entry<String, Integer> mj : globalGrams.entrySet()){
    			if( currentGrams.containsKey(mj.getKey())){
    				matrix[i][j] = currentGrams.get(mj.getKey());
    			}else{
    				matrix[i][j] = 0;
    			}    				
    			j++;
    		}
    		matrix[i][j]= new Double(this.manuScripts.get(this.trainingSet.get(i)).getFamily(levelToMatch)).doubleValue();
		}
    	return matrix;
    }
    public double[][] getTestingSetMatrix(boolean isWord, int size, int levelToMatch){
    	double[][] matrix = null;
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getTrainingGrandNGrams(size);
    		matrix = new double[this.testingSet.size()][globalGrams.size()+1];
    	}else{
    		globalGrams = getTrainingGrandNCharGrams(size);
    		matrix = new double[this.testingSet.size()][globalGrams.size()+1];
    	}
    	 
    	
    	for (int i = 0; i < matrix.length; i++) {    		
    		SortedMap<String, Integer> currentGrams = null;
    		if( isWord ){
    			currentGrams = this.manuScripts.get(this.testingSet.get(i)).getNGrams(size);
    		}else {
    			currentGrams = this.manuScripts.get(this.testingSet.get(i)).getNCharGrams(size);
    		}    		
    		int j=0;
    		for(Map.Entry<String, Integer> mj : globalGrams.entrySet()){
    			if( currentGrams.containsKey(mj.getKey())){
    				matrix[i][j] = currentGrams.get(mj.getKey());
    			}else{
    				matrix[i][j] = 0;
    			}    				
    			j++;
    		}
    		matrix[i][j]= new Double(this.manuScripts.get(this.testingSet.get(i)).getFamily(levelToMatch)).doubleValue();
		}
    	return matrix;
    }    
    
    public String[] getManuscriptLabels(){
    	return this.manuScripts.keySet().toArray(new String[0]);    	
    }
    public String[] getManuscriptLabels(int chap){
    	ArrayList<String> returnValue = new ArrayList<String>();
    	for(Map.Entry<String, Manuscript> mj : this.manuScripts.entrySet()){
    		if( mj.getValue().getText(chap).length() > 10 )
    			returnValue.add(mj.getKey());
    	}
    	return returnValue.toArray(new String[0]);
    }

    public String[] getTrainingManuscriptLabels(){
    	return this.trainingSet.toArray(new String[0]);    	
    }
    
    public String[] getTestingManuscriptLabels(){
    	return this.testingSet.toArray(new String[0]);    	
    }
    public String[] getCompositeFeatureLabels(){   	
    	ArrayList<String> tmp = new ArrayList<String>(getGrandCompositeGrams().keySet());
    	
    	//tmp.add("Family");
    	return tmp.toArray(new String[0]);    	
    }      
    public String[] getFeatureLabels(boolean isWord, int size){
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getGrandNGrams(size);    		
    	}else{
    		globalGrams = getGrandNCharGrams(size);    		
    	}    	
    	ArrayList<String> tmp = new ArrayList<String>(globalGrams.keySet());
    	
    	//tmp.add("Family");
    	return tmp.toArray(new String[0]);    	
    }    
    
    public String[] getTrainingFeatureLabels(boolean isWord, int size){
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getTrainingGrandNGrams(size);    		
    	}else{
    		globalGrams = getTrainingGrandNCharGrams(size);    		
    	}    	
    	ArrayList<String> tmp = new ArrayList<String>(globalGrams.keySet());
    	
    	//tmp.add("Family");
    	return tmp.toArray(new String[0]);    	
    }  
    
    public String[] getFullDataSetFeatureLabels(boolean isWord, int size){
    	SortedMap<String, Integer> globalGrams = null;
    	if( isWord ){
    		globalGrams = getTrainingGrandNGrams(size);    		
    	}else{
    		globalGrams = getTrainingGrandNCharGrams(size);    		
    	}    	
    	ArrayList<String> tmp = new ArrayList<String>(globalGrams.keySet());
    	
    	
    	return tmp.toArray(new String[0]);    	
    }     
    
    public void printFamilies(int levelToMatch){
    	SortedMap<String,Integer> fams = getUniqueFamilies(levelToMatch);
    	SortedMap<String, ArrayList<String>> famlist = new TreeMap<String, ArrayList<String>>();    	
    	for(Map.Entry<String, Integer> mj : fams.entrySet()){
    		famlist.put(mj.getKey(), new ArrayList<String>());
    	}
    	
    	for(Map.Entry<String, Manuscript> mj : manuScripts.entrySet()){
    		if( famlist.containsKey(mj.getValue().getFamily(levelToMatch))){
    			ArrayList<String> tmplist = famlist.get(mj.getValue().getFamily(levelToMatch));
    			tmplist.add(mj.getKey());
    			famlist.put(mj.getValue().getFamily(levelToMatch), tmplist);
    		}else{
    			ArrayList<String> tmplist = new ArrayList<String>();
    			tmplist.add(mj.getKey());
    			System.out.println("famlist size: "+mj.getKey());
    			famlist.put(mj.getValue().getFamily(levelToMatch), tmplist);
    		}
    	}
    	
    	for(Map.Entry<String, ArrayList<String>> mj : famlist.entrySet()){
    		System.out.println(mj.getKey());
    		ArrayList<String> tmplist = mj.getValue();
    		for(String str : tmplist){
    			System.out.println("   "+str);
    		}
    	}
    }
    
}

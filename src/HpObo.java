import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class HpObo {
	
	private static File file;
	static final Path INDEX_DIR = Paths.get("index_hp.obo");

	public HpObo(File file) {
		HpObo.file = file;
	}
	
	static void extraction() throws IOException {
		    if (file.canRead() && !file.isDirectory()) {
			      // each line of the file is a new document
			      try{
				   	 InputStream       ips  = new FileInputStream(file);
				   	 InputStreamReader ipsr = new InputStreamReader(ips);
				   	 BufferedReader	br   = new BufferedReader(ipsr);
				   	 String line;
				   	 //initialization
				   	 String symptom_id     	= "";
				   	 String alt_symptom_id  = "";
				   	 String symptom  		= "";
				   	 ArrayList<String> symptom_synonyms = null;
		
				   	 while ((line=br.readLine())!=null){
				   		 // new drug
				   		 if(line.startsWith("id:")){
				   			 String[] fields = line.split(" ");
				   			 symptom_id  	 = fields[1];
				   		 }
				   		 
				   		 if(line.startsWith("name:")){
				  			 String[] fields = line.split(": ");
				  			 symptom       	 = fields[1];
				  		 }
				   		 
				   		if(line.startsWith("alt_id:")){
				  			 String[] fields = line.split(" ");
				  			 alt_symptom_id  = fields[1];
				  		 }
				   		 
				   		if(line.startsWith("synonym:")){
				 			 String[] fields = line.split(":");
				 			 String[] f = fields[1].split("EXACT");
				 			 symptom_synonyms.add(f[0]);
				 		 }
				   		
				   		if(line.startsWith("[Term]")){
				   			Symptom s = new Symptom(symptom_id, symptom, alt_symptom_id, symptom_synonyms);
				   			//System.out.println("symptom_id: "+symptom_id+"\tsymptom: "+symptom+"\tsymptom_synonyms: "+symptom_synonyms);
				   			System.out.println(s.toString()); 
				   			 //clean values
				   			 symptom_id     	= "";
				   			 symptom  			= "";
				   			 symptom_synonyms = new ArrayList<String>();
				   		 }   		 
				   	 }
				     	 br.close();
			      } catch (Exception e){
			    	  System.out.println(e.toString());
			      }
			   }  
		  }
	
	public void indexation() {
	      
	    boolean create = true;
	      
		if (INDEX_DIR.toFile().exists()) {
	  	System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
	  	System.exit(1);
		}
	    
		//final File file = new File("hp_mini.obo");
		if (!file.exists() || !file.canRead()) {
	  	System.out.println("File '" + file.getAbsolutePath() + "' does not exist or is not readable, please check the path");
	  	System.exit(1);
		}
	    
		Date start = new Date();
		try {
	   	 	Directory directory = FSDirectory.open(INDEX_DIR);
	    	Analyzer analyzer = new StandardAnalyzer();
	    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
	   	 
	    	if (create) {
	   		 // Create a new index in the directory, removing any
	   		 config.setOpenMode(OpenMode.CREATE);
	    	} else {
	   		 config.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    	}
	   	 
	   	 
	  	IndexWriter writer = new IndexWriter(directory, config);
	  	
	  	System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
	  	indexDoc(writer, file);
	  	writer.close();

	  	Date end = new Date();
	  	System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
	  	System.out.println(" caught a " + e.getClass() +
	   	"\n with message: " + e.getMessage());
		}
		
	  }

	public ArrayList<String> indexDoc(IndexWriter writer, File file) throws IOException {
	    int eltCount = 0;
		if (file.canRead() && !file.isDirectory()) {
	      // each line of the file is a new document
	      try{
	   	 InputStream       ips  = new FileInputStream(file);
	   	 InputStreamReader ipsr = new InputStreamReader(ips);
	   	 BufferedReader	br   = new BufferedReader(ipsr);
	   	 String line;
	   	 //initialization
	   	 String symptom_id     	= "";
	   	 String symptom  		= "";
	   	 ArrayList<String> symptom_synonyms = null;


	   	 while ((line=br.readLine())!=null){
	   		 // new drug
	   		 if(line.startsWith("id:")){
	   			 String[] fields = line.split(" ");
	   			 String[] f = fields[1].split(":");
	   			 symptom_id  	 = f[1];
	   		 }
	   		 
	   		 if(line.startsWith("name:")){
	  			 String[] fields = line.split(":");
	  			 symptom       	 = fields[1];
	  		 }
	   		 
	   		if(line.startsWith("synonym:")){
	 			 String[] fields = line.split(":");
	 			 String[] f = fields[1].split("EXACT");
	 			 symptom_synonyms.add(f[0]);
	 		 }


	   		 if(line.startsWith("[Term]")){
	   			 //write the index
	   			 // make a new, empty document
	   		 	Document doc = new Document();


	   		 	doc.add(new StoredField("Synonyms", symptom_synonyms.toString())); // stored not indexed
	   		 	doc.add(new TextField("Symptom_id", symptom_id,    	 Field.Store.YES)); // indexed 
	   		 	doc.add(new TextField("Symptom", symptom,    	 Field.Store.NO));
	  
	   		 	System.out.println(symptom_id+", "+symptom+", "+symptom_synonyms);
	   		 	
	   		 	if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	   				 System.out.println("adding " + file);
	   				  writer.addDocument(doc);
	   			 }else{
	   				 System.out.println("updating " + file);
	   				  writer.updateDocument(new Term("path", file.getPath()), doc);
	   			 }
	   			 
	   			 eltCount++;
	   			 //clean values
	   			 symptom_id     	= "";
	   			 symptom  			= "";
	   			 symptom_synonyms = new ArrayList<String>();
	   		 }   		 
	   	 }
	   	 
	     	 br.close();
	      }catch (Exception e){
	   	 System.out.println(e.toString());
	      }
		}
		System.out.println(eltCount+" elts have been added to the index " + System.getProperty("user.dir")+ "/" + INDEX_DIR);
		return null;   
	  }
	
	/**
	 * 
	 * @param searchedElement, his field in the indexation & the valueSearched
	 * @return symptom_id
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public ArrayList<String> search(String symptom, String field, String valueSearched) throws IOException, ParseException{
		
		Directory directory = null;
		try {
			directory = FSDirectory.open(INDEX_DIR);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		IndexSearcher searcher 	= new IndexSearcher(reader);
		Analyzer analyzer 	= new StandardAnalyzer();
		Query query			= new QueryParser(field, analyzer).parse(symptom);
		//Query query			= new QueryParser("Symptom", analyzer).createBooleanQuery("symptom_id", symptom, BooleanClause.Occur.MUST);
		
		//System.out.println("\nSearching for: " + query.toString());
		int hitsPerPage = 5000;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int numTotalHits = collector.getTotalHits();
		ScoreDoc[] results = collector.topDocs().scoreDocs;
		
		//System.out.println("Found "+ numTotalHits + " hits.");
		ArrayList<String> ListOfSymptom_id = new ArrayList<String>();
		for (int i=0; i<results.length;i++){
			int docId = results[i].doc;
			Document d = searcher.doc(docId);
			ListOfSymptom_id.add(d.get(valueSearched));
			//System.out.println(d.get(valueSearched));
		}
		return ListOfSymptom_id;
	}
}

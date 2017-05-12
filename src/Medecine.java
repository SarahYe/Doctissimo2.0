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
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
public class Medecine {
	
	private static File file;
	static final Path INDEX_DIR = Paths.get("index_medecine");

	public Medecine(File file) {
		Medecine.file = file;
	}
	
	
	static ArrayList<String> extractionMedicineID() throws IOException {
		ArrayList<String> ListOfATC_Code = new ArrayList<String>();
	    if (file.canRead() && !file.isDirectory()) {
		      // each line of the file is a new document
		      try{
			   	 InputStream       ips  = new FileInputStream(file);
			   	 InputStreamReader ipsr = new InputStreamReader(ips);
			   	 BufferedReader	br   = new BufferedReader(ipsr);
			   	 String line;
			   	 //initialization
			   	 String ATC_Code     	= "";
			   	 String medicine_name  	= "";


			   	 while ((line=br.readLine())!=null){
			   		 // new drug
			   		 if(line.startsWith("E")){
			   			 String[] fields = line.split(" ");
			   			 ATC_Code  	 = fields[8];
			   			 String[] si = ATC_Code.split(" ");
			   			 String medicine_id = si[0];
			   			//System.out.println(medicine_id);
			   			ListOfATC_Code.add(medicine_id);
			   			 for (int i = 9; i<fields.length; i++)
			   				 medicine_name += " "+fields[i];

			   			 	String[] s = medicine_name.split("\\[");
			   			 	medicine_name = s[0];
			   			    //System.out.println(medicine_name);
			   		 }
				     		 
			   	 }
			     	 br.close();
		      } catch (Exception e){
		    	  System.out.println(e.toString());
		      }
		   }
		return ListOfATC_Code;  
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

	public void indexDoc(IndexWriter writer, File file) throws IOException {
	    int eltCount = 0;
		if (file.canRead() && !file.isDirectory()) {
	      // each line of the file is a new document
	      try{
	   	 InputStream       ips  = new FileInputStream(file);
	   	 InputStreamReader ipsr = new InputStreamReader(ips);
	   	 BufferedReader	br   = new BufferedReader(ipsr);
	   	 String line;
	   	 //initialization
	   	 String ATC_Code     	= "";
	   	 String medicine_name  	= "";


	   	 while ((line=br.readLine())!=null){
	   		 // new drug
	   		 if(line.startsWith("E")){
	   			 String[] fields = line.split(" ");
	   			 ATC_Code  	 = fields[8];
	   			 for (int i = 9; i<fields.length; i++)
	   				 medicine_name += " "+fields[i];

	   			 	String[] s = medicine_name.split("\\[");
	   			 	medicine_name = s[0];
	   		 }

	   		 if(line.startsWith("F")){
	   			 //write the index
	   			 // make a new, empty document
	   		 	Document doc = new Document();


	   		 	//doc.add(new StoredField("symptom_id", symptom_id)); // stored not indexed
	   		 	doc.add(new TextField("ATC_Code", ATC_Code, Field.Store.YES)); // indexed and stored
	   		 	doc.add(new TextField("medicine_name", medicine_name, Field.Store.YES)); // indexed and stored
	   		 	
	   		 	System.out.println(ATC_Code+", "+ medicine_name);
	   		 	
	   		 	if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	   				 System.out.println("adding " + file);
	   				  writer.addDocument(doc);
	   			 }else{
	   				 System.out.println("updating " + file);
	   				  writer.updateDocument(new Term("path", file.getPath()), doc);
	   			 }
	   			 
	   			 eltCount++;
	   			 //clean values
	   			 ATC_Code     	= "";
	   			 medicine_name  		= "";
	   		 }   		 
	   	 }
	   	 
	     	 br.close();
	      }catch (Exception e){
	   	 System.out.println(e.toString());
	      }
		}
		System.out.println(eltCount+" elts have been added to the index " + System.getProperty("user.dir")+ "/" + INDEX_DIR);   
	  }
	
	/**
	 * Searching for a medecine
	 * @param medecine_id
	 * @return medicine
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String search(String medecine_id, String field, String Medecine) throws IOException, ParseException{
		
		Directory directory = null;
		IndexReader reader = null;
		String medecine = null;
		try {
			directory = FSDirectory.open(INDEX_DIR);
			reader = DirectoryReader.open(directory);
			
			IndexSearcher searcher 	= new IndexSearcher(reader);
			Analyzer analyzer 	= new StandardAnalyzer();
			Query query			= new QueryParser(field, analyzer).parse(medecine_id);
			//Query query			= new QueryParser("Symptom", analyzer).createBooleanQuery("symptom_id", symptom, BooleanClause.Occur.MUST);
			
			//System.out.println("\nSearching for: " + query.toString());
			int hitsPerPage = 10; //result is ordered with Lucene scored then true
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
			
			searcher.search(query, collector);
			
			int numTotalHits = collector.getTotalHits();
			ScoreDoc[] results = collector.topDocs().scoreDocs;
			ArrayList<String> ListOfMedecine = new ArrayList<String>();
			
			
			//System.out.println("Found "+ numTotalHits + " hits.");
			for (int i=0; i<results.length;i++){
				int docId = results[i].doc;
				Document d = searcher.doc(docId);
				
				medecine = d.get(Medecine);
			}	
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return medecine;
		
	}
}

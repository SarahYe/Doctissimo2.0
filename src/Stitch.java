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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Stitch {
	
	private static File file;
	static final Path INDEX_DIR = Paths.get("index_stitch.tsv");

	public Stitch(File file) {
		Stitch.file = file;
	}
	
	static ArrayList<String> extractionCID() throws IOException {
		ArrayList<String> ListOfCID = new ArrayList<String>();
	    if (file.canRead() && !file.isDirectory()) {
		      // each line of the file is a new document
		      try{
			   	 InputStream       ips  = new FileInputStream(file);
			   	 InputStreamReader ipsr = new InputStreamReader(ips);
			   	 BufferedReader	br   = new BufferedReader(ipsr);
			   	 String line;
			   	 int nbLines = 0;
			   	 //initialization
			   	 String ATC   	= "";
	
			   	while ((line=br.readLine())!=null){
			   		 
			   		 nbLines++;
			   		 
			   		 
			   		 if (nbLines>=10){
			   			//System.out.println(nbLines);
			   			String fields[] = line.split("\t");
			   			String chemical = fields[0];
			   			String alias = fields[1];
			   			String source = fields[2];
			   			
			   			String[] s1 = chemical.split("m");  //Transformation function
				   		chemical = s1[0]+s1[1];
				   		
				   		String[] s2 = alias.split("s"); //Transformation function
				   		alias = s2[0]+s2[1];
			  
				   		ATC = fields[3];
				   		if (source.equals("BindingDB")){
				   			System.out.println("Nombre de ATC: " + (nbLines - 10));
				   			break;
				   		}
				   		if (!ListOfCID.contains(chemical))
				   			ListOfCID.add(chemical);
				   		
				   		if (!ListOfCID.contains(alias))
				   			ListOfCID.add(alias);
				   	}
				     		 
			   	 }
			     	 br.close();
		      } catch (Exception e){
		    	  System.out.println(e.toString());
		      }
		   }
		return ListOfCID;  
	  }
	
	static ArrayList<String> extractionATC() throws IOException {
		ArrayList<String> ListOfATC = new ArrayList<String>();
	    if (file.canRead() && !file.isDirectory()) {
		      // each line of the file is a new document
		      try{
			   	 InputStream       ips  = new FileInputStream(file);
			   	 InputStreamReader ipsr = new InputStreamReader(ips);
			   	 BufferedReader	br   = new BufferedReader(ipsr);
			   	 String line;
			   	 int nbLines = 0;
			   	 //initialization
			   	 String ATC   	= "";
	
			   	while ((line=br.readLine())!=null){
			   		 
			   		 nbLines++;
			   		 
			   		 
			   		 if (nbLines>=10){
			   			//System.out.println(nbLines);
			   			String fields[] = line.split("\t");
			   			String source = fields[2];
				   		ATC = fields[3];
				   		if (source.equals("BindingDB")){
				   			System.out.println("Nombre de ATC: " + (nbLines - 10));
				   			break;
				   		}
				   		
				   		if (!ListOfATC.contains(ATC))
				   			ListOfATC.add(ATC);
				   	}
				     		 
			   	 }
			     	 br.close();
		      } catch (Exception e){
		    	  System.out.println(e.toString());
		      }
		   }
		return ListOfATC;  
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

	public void indexDoc(IndexWriter writer, File file) throws IOException, ArrayIndexOutOfBoundsException {
	    int eltCount = 0;
		if (file.canRead() && !file.isDirectory()) {
	      // each line of the file is a new document
	      try{
	   	 InputStream       ips  = new FileInputStream(file);
	   	 InputStreamReader ipsr = new InputStreamReader(ips);
	   	 BufferedReader	br   = new BufferedReader(ipsr);
	   	 String line;
	   	 int nbLines = 0;
	   	 String chemical    = "";
	   	 String alias 		= "";
	   	 String source	 	= "";
	   	 String medecine_id = "";
	   	 int i=0;


	   	 while ((line=br.readLine())!=null){
	   		 
	   		 nbLines++;
	   		 //System.out.println(line);
	   		 if (nbLines>=10){
	   			String fields[] = line.split("\t");
		   		 
		   		chemical = fields[0];
		   		
		   		alias = fields[1];
		   		
		   		source = fields[2];
		   		
		   		medecine_id = fields[3];
		   		 
		   		if (source.equals("ATC")){
			   		Document doc = new Document();
			   		
			   		String[] s1 = chemical.split("m");  //Transformation function
			   		chemical = s1[0]+s1[1];
			   		
			   		String[] s2 = alias.split("s"); //Transformation function
			   		alias = s2[0]+s2[1];
		
			   		doc.add(new TextField("Chemical", chemical.toLowerCase(), Field.Store.NO)); 
			   		doc.add(new TextField("Alias", alias.toLowerCase(), Field.Store.NO));
			   		doc.add(new TextField("Medecine_id", medecine_id, Field.Store.YES));
			   		 	
			   		 	System.out.println(chemical+", "+ alias+", "+ medecine_id);
			   		 	
			   		 	if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	   				 System.out.println("adding " + file);
			   				  writer.addDocument(doc);
			   			 }else{
			   				 System.out.println("updating " + file);
			   				  writer.updateDocument(new Term("path", file.getPath()), doc);
			   			 }
			   			 
			   			 eltCount++;
			   			 //clean values
			   			chemical     	= "";
			   			alias  			= "";
			   			source			= "";
			   			medecine_id		= "";   		 
		   		}
		   		
		   		if (source.equals("BindingDB")){
		   			break;
		   		}
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
	 * Searching for a medecine_id
	 * @param CID1, CID2
	 * @return medicine_id
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public ArrayList<String> search(String CID1, String Chemical, String CID2, String Alias, String ValueSearched) throws IOException, ParseException{
		
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
		//Query query			= new QueryParser(Chemical, analyzer).parse(CID1);
		
		//Query query			= new QueryParser(Chemical, analyzer).createBooleanQuery(Chemical, CID1, BooleanClause.Occur.MUST);
		
		String[] fields = {Chemical, Alias};
		String[] entries = {CID1, CID2};
		BooleanClause.Occur[] obligations = {BooleanClause.Occur.MUST, BooleanClause.Occur.MUST};
		Query query = MultiFieldQueryParser.parse(entries, fields, obligations, analyzer);
		
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
		ArrayList<String> ListOfMedecine_id = new ArrayList<String>();
		for (int i=0; i<results.length;i++){
			int docId = results[i].doc;
			Document d = searcher.doc(docId);
			ListOfMedecine_id.add(d.get(ValueSearched));
			//System.out.println((i+1)+ " " + d.get(ValueSearched));
		}
		return ListOfMedecine_id;
	}
	
	/**
	 * Searching for a medecine_id
	 * @param CID1
	 * @return medicine_id
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public ArrayList<String> searchByCID(String CID1, String Chemical, String Alias, String ValueSearched) throws IOException, ParseException{
		
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
		//Query query			= new QueryParser(Chemical, analyzer).parse(CID1);
		
		//Query query			= new QueryParser(Chemical, analyzer).createBooleanQuery(Chemical, CID1, BooleanClause.Occur.MUST);
		
		String[] fields = {Chemical, Alias};
		String[] entries = {CID1, CID1};
		BooleanClause.Occur[] obligations = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
		Query query = MultiFieldQueryParser.parse(entries, fields, obligations, analyzer);
		
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
		ArrayList<String> ListOfMedecine_id = new ArrayList<String>();
		for (int i=0; i<results.length;i++){
			int docId = results[i].doc;
			Document d = searcher.doc(docId);
			ListOfMedecine_id.add(d.get(ValueSearched));
			//System.out.println((i+1)+ " " + d.get(ValueSearched));
		}
		return ListOfMedecine_id;
	}
}

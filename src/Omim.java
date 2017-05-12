import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
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
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Omim {
	
	private String docsPath;
	private String indexPath = "index_omim.txt";
	static final Path INDEX_DIR = Paths.get("index_omim.txt");
	Boolean create = true;

/*	private String index  =  "indexOmim";
	private String queries  =  null;
	int repeat  =  0;
	boolean raw  =  false;
	private String queryString  =  null;
	int hitsPerPage  =  10;
*/
	public Omim(String file){
		this.docsPath  =  file;
	}

	public void indexation(){

		final Path docDir = Paths.get(docsPath);

		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		Date start  =  new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir  =  FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer  =  new StandardAnalyzer();
			IndexWriterConfig iwc  =  new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			
			IndexWriter writer  =  new IndexWriter(dir, iwc);    
			indexDocs(writer, docDir);
			writer.close();
			Date end  =  new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}

	static void indexDocs(final IndexWriter writer, Path path) throws IOException {

		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file);
					} catch (IOException ignore) {
						// don't index files that can't be read.
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path);
		}
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file) throws IOException {
		try (InputStream stream  =  Files.newInputStream(file)) {
			// make a new, empty document
			File file1  =  new File(file.toString());
			BufferedReader br  =  new BufferedReader (new FileReader(file1));
			String line  =  br.readLine();

			String bloc =  "";
			String disease_name = "";
			String disease_id = "";
			String symptom = "";
			
			Document doc = new Document();
			
			while(line !=  null){
				
				if (line.contains("*RECORD*")){
					doc  =  new Document();
				}
				
				else if (line.contains("*RECORD*") == false && line.contains("*FIELD* ED") == false){
					if (line.equals("*FIELD* NO")) {
						line = br.readLine();
						disease_id = line;
						doc.add(new StringField("Disease_id", disease_id, TextField.Store.NO));
						//System.out.println("Disease_id: "+ disease_id);
					}

					if (line.equals("*FIELD* TI")){
						bloc  = "";
						line = br.readLine();
						while (line.equals("*FIELD* TX") == false){
							int i;
							int index = 0;
							for (i = 0; i<line.length();i++){
								char ch = line.charAt(i);
								if ((ch >=  'a' && ch <=  'z') || (ch >=  'A' && ch <=  'Z')){
									index = i;
									break;
								}
							}
							line = line.substring(index);
							bloc = bloc+"\n"+line;
							line = br.readLine();
						}
						disease_name = bloc;
						doc.add(new TextField("Disease", disease_name,TextField.Store.YES));
						//System.out.println("Disease_name: "+ disease_name);
					}

					else if (line.equals("*FIELD* CS")){
						bloc  = "";
						line = br.readLine();
						while (line.contains("*FIELD*") == false){
							bloc = bloc+"\n"+line;
							line = br.readLine();
						}
						
						symptom = bloc;
						doc.add(new TextField("Symptom: ", symptom, TextField.Store.YES));
						//System.out.println("Symptom: " + symptom);

					}
				}
				
				else if (line.contains("*FIELD* ED")){
					if (writer.getConfig().getOpenMode()  ==  OpenMode.CREATE) {
						// New index, so we just add the document (no old document can be there):
						//System.out.println("adding " + file);
						System.out.println("**************************");
						System.out.println("Disease_id: "+disease_id+"\nDisease_name: "+ disease_name+"\nSymptom: "+ symptom);
						writer.addDocument(doc);       
						
						disease_id     	= "";
			   			disease_name  	= "";
			   			symptom		  	= "";
					} else {

						writer.updateDocument(new Term("path", file.toString()), doc);
					}
				}

				line = br.readLine();
			}
			br.close();  
		}
	}
	
	/**
	 * Searching for a disease_name
	 * @param symptom
	 * @return disease
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public void search(String symptom, String field, String ValueSearched) throws IOException, ParseException{
		
		Term term = new Term(field,symptom);
		Directory directory = null;
		IndexReader reader = null;
		
		try {
			directory = FSDirectory.open(INDEX_DIR);
			reader = DirectoryReader.open(directory);
			
			IndexSearcher searcher 	= new IndexSearcher(reader);
			Analyzer analyzer 	= new StandardAnalyzer();
			//Query query			= new QueryParser(field, analyzer).parse(symptom);
			Query query			= new QueryParser(field, analyzer).createBooleanQuery(field, symptom, BooleanClause.Occur.SHOULD);

			
			System.out.println("\nSearching for: " + query.toString());
			int hitsPerPage = 10;
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
			
			searcher.search(query, collector);
			
			int numTotalHits = collector.getTotalHits();
			ScoreDoc[] results = collector.topDocs().scoreDocs;
			
			System.out.println("Found "+ numTotalHits + " hits.");
			for (int i=0; i<results.length;i++){
				int docId = results[i].doc;
				Document d = searcher.doc(docId);
				
				System.out.println(d.get(ValueSearched));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
		
	/**
	 * Searching for a disease_name
	 * @param symptom
	 * @return disease
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void searchIndexWithTermQuery(String symptom, String field, String ValueSearched) throws IOException, ParseException {
		
		Directory directory = null;
		IndexReader reader = null;
		
		try {
			directory = FSDirectory.open(INDEX_DIR);
			reader = DirectoryReader.open(directory);
			IndexSearcher searcher 	= new IndexSearcher(reader);

			Term term = new Term(field, symptom);
			Query query = new TermQuery(term);
			
			System.out.println("\nSearching for '" + symptom + "' using TermQuery");

			int hitsPerPage = 10;
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);

			searcher.search(query, collector);

			int numTotalHits = collector.getTotalHits();
			ScoreDoc[] results = collector.topDocs().scoreDocs;
			
			System.out.println("Found "+ numTotalHits + " hits.");
			for (int i=0; i<results.length;i++){
				int docId = results[i].doc;
				Document d = searcher.doc(docId);
				
				System.out.println(d.get(ValueSearched));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

	
	
	

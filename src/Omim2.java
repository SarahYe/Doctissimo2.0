import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Omim2 {
	private String docsPath;
	private String indexPath="indexOmim";
	Boolean create1=true;

	private String index = "indexOmim";
	private String queries = null;
	int repeat = 0;
	boolean raw = false;
	private String queryString = null;
	int hitsPerPage = 10;

	public Omim2(String file){
		this.docsPath = file;
	}

	public void indexation(){

		final Path docDir=Paths.get(docsPath);

		if (!Files.isReadable(docDir)) {
			System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create1) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);    
			indexDocs(writer, docDir);
			writer.close();
			Date end = new Date();
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
		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document

			String debut="*RECORD*";
			String fin="*FIELD* ED";

			File File_file=new File(file.toString());
			BufferedReader  fichierWifi= new BufferedReader (new FileReader(File_file));
			String line=fichierWifi.readLine();

			String bloc="";
			Document doc=new Document();

			while(line!=null){
				if (line.contains(debut)){
					doc=new Document();
					String id1=line.substring(8);
					StoredField id = new StoredField("Doc_id", id1);
					doc.add(id);
				}
				else if (line.contains(fin)){
					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old document can be there):
						//System.out.println("adding " + file);
						writer.addDocument(doc);       
					} else {

						writer.updateDocument(new Term("path", file.toString()), doc);
					}
				}
				else if (line.contains(debut)==false && line.contains(fin)==false){
					if (line.equals("*FIELD* NO")) {
						line=fichierWifi.readLine();
						String diseaseOmimId=line;
						StringField DiseaseOmimId = new StringField("Disease_id", diseaseOmimId,TextField.Store.YES);
						doc.add(DiseaseOmimId);
						System.out.println("Disease_id: "+ DiseaseOmimId.toString());
					}

					if (line.equals("*FIELD* TI")){
						bloc ="";
						line=fichierWifi.readLine();
						while (line.equals("*FIELD* TX")==false){
							int i;
							int index=0;
							for (i=0; i<line.length();i++){
								char ch=line.charAt(i);
								if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')){
									index=i;
									break;
								}
							}
							line=line.substring(index);
							bloc=bloc+"\n"+line;
							line=fichierWifi.readLine();
						}
						String diseaseName=bloc;
						TextField Disease = new TextField("Disease", diseaseName,TextField.Store.YES);
						doc.add(Disease);
						System.out.println("Disease_name: "+ Disease.toString());
					}

					else if (line.equals("*FIELD* CS")){
						bloc ="";
						line=fichierWifi.readLine();
						while (line.contains("*FIELD*")==false){
							bloc=bloc+"\n"+line;
							line=fichierWifi.readLine();
						}
						String clinicalsigns=bloc;
						TextField clinicalSigns = new TextField("Symptom: ", clinicalsigns, TextField.Store.YES);
						doc.add(clinicalSigns);
						System.out.println("Symptom: " + clinicalSigns);

					}
				}

				line=fichierWifi.readLine();
			}
			fichierWifi.close();  
		}
	}



	public void searchOmim(String field) throws IOException, ParseException{

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();

		BufferedReader in = null;
		if (queries != null) {
			in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
		} else {
			in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		}
		QueryParser parser = new QueryParser(field, analyzer);
		while (true) {
			if (queries == null && queryString == null) {                  // prompt the user
				System.out.println("Enter query: ");
			}

			String line = queryString != null ? queryString : in.readLine();

			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim();
			if (line.length() == 0) {
				break;
			}

			Query query = parser.parse(line);
			System.out.println("Searching for "+ field +": " + query.toString(field));

			if (repeat > 0) {                           // repeat & time as benchmark
				Date start = new Date();
				for (int i = 0; i < repeat; i++) {
					searcher.search(query, 100);
				}
				Date end = new Date();
				System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
			}

			doPagingSearch(field, in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);

			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}

	public static void doPagingSearch(String field,BufferedReader in, IndexSearcher searcher, Query query, 
			int hitsPerPage, boolean raw, boolean interactive) throws IOException {

		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) {                              // output raw format
					System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get(field);
				if (path != null) {
					System.out.println((i+1) + ". " + path);
					// String title = doc.get("Disease");
					//   if (title !=null) {
					//     System.out.println("   Title: " + doc.get("title"));
					//   }
				} else {
					System.out.println((i+1) + ". " + "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");  
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0)=='q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start+=hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit) break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}
	
	
	
	/*	private static File file;
	static final Path INDEX_DIR = Paths.get("v2_index_omim.txt");

	public Omim(File file) {
		Omim.file = file;
	}
	
	public void indexation() {
	      
	    boolean create = true;
	      
		if (INDEX_DIR.toFile().exists()) {
	  	System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
	  	System.exit(1);
		}
	    
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
	   	 String disease_id     		= "";
	   	 String disease_name  		= "";
	   	 String symptom_name  		= "";


	   	 while ((line=br.readLine())!=null){
	   		 // new drug
	   		 if(line.startsWith("*FIELD* NO")){
	   			if((line=br.readLine())!=null)
	   				disease_id=line; //récupère la ligne suivante  
	   		 }
	   		 
	   		 if(line.startsWith("*FIELD* TI")){
	   			 String followingLine=br.readLine();
	   			while (followingLine!=null && !followingLine.contains("*FIELD*")){
	   				//if (followingLine.startsWith(".[0-9]")){
	   				//	String[] s = followingLine.split(".[0-9]");
	   					disease_name += followingLine;
	   				//} else {	//String[] s = followingLine.split(".[0-9]");
	   				//	disease_name += followingLine;
	   				//}
	   			    followingLine = br.readLine();
	   			}
	  		 }
	   		 
	   		if(line.startsWith("*FIELD* CS")){
	   			 String followingLine=br.readLine();
	   			while (followingLine!=null && !followingLine.contains("*FIELD*")){
	   					symptom_name += followingLine;
	   				followingLine = br.readLine();
	   			}
	  		 }
	   		 

	   		 if(line.startsWith("*FIELD* ED")){

	   			 if (symptom_name != null || disease_name != null) {
		   			 //write the index
		   			 // make a new, empty document
	   				Document doc = new Document();

		   		 	doc.add(new TextField("disease_id", disease_id, Field.Store.NO)); // indexed
		   		 	doc.add(new TextField("disease_name", disease_name, Field.Store.YES)); // indexed  and stored
		   		    doc.add(new TextField("symptom_name", symptom_name, Field.Store.NO)); // indexed  and stored
		   		    
		   		 	System.out.println(disease_id+", "+ disease_name+", "+ symptom_name);
		   		 	
		   		 	if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
		   				 System.out.println("adding " + file);
		   				  writer.addDocument(doc);
		   			 }else{
		   				 System.out.println("updating " + file);
		   				  writer.updateDocument(new Term("path", file.getPath()), doc);
		   			 }
		   			 
		   			 eltCount++;
		   			 //clean values
		   			 disease_id     	= "";
		   			 disease_name  		= "";
	   			 }
	   		 	
	   		 }   		 
	   	 }
	   	 
	     	 br.close();
	      }catch (Exception e){	
	   	 System.out.println(e.toString());
	      }
		}
		System.out.println(eltCount+" elts have been added to the index " + System.getProperty("user.dir")+ "/" + INDEX_DIR);   
	  }*/
}

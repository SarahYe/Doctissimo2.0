import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class OmimCSV {

	private static File file;
	static final Path INDEX_DIR = Paths.get("index_omim.csv");

	public OmimCSV(File file) {
		OmimCSV.file = file;
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
				//initialization
				String symptom_id     		= "";
				String symptom_name  		= "";
				String synonyms 	= "";
				String cui 		= "";


				while ((line=br.readLine())!=null){
					// new drug
					String fields[] = line.split(",");

					String[] url = fields[0].split("/");
					symptom_id = url[url.length-1];

					symptom_name = fields[1];

					synonyms = fields[2];

					cui = fields[5];

					Document doc = new Document();


					//doc.add(new StoredField("symptom_id", symptom_id)); // stored not indexed
					doc.add(new TextField("symptom_id", symptom_id, Field.Store.YES)); // indexed and stored
					doc.add(new TextField("symptom_name", symptom_name, Field.Store.YES)); // indexed
					doc.add(new TextField("synonyms", synonyms, Field.Store.YES)); // indexed
					doc.add(new TextField("cui", cui, Field.Store.NO)); // indexed

					System.out.println(symptom_id+", "+ symptom_name+", "+ synonyms+", "+ cui);

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
					symptom_name  	= "";
					synonyms= "";
					cui		= "";   		 
				}

				br.close();
			}catch (Exception e){
				System.out.println(e.toString());
			}
		}
		System.out.println(eltCount+" elts have been added to the index " + System.getProperty("user.dir")+ "/" + INDEX_DIR);   
	}

	/**
	 * Searching for a symptom_name
	 * @param cui
	 * @return medicine
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public ArrayList<String> searchByCui(String cui, String field, String ValueSearched) throws IOException, ParseException{

		ArrayList<String> ListOfSymptomSynonyms = new ArrayList<String>();
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
		//Query query			= new QueryParser(field, analyzer).parse(cui);
		Query query			= new QueryParser(field, analyzer).createBooleanQuery(field, cui, BooleanClause.Occur.SHOULD);

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
		for (int i=0; i<results.length;i++){
			int docId = results[i].doc;
			Document d = searcher.doc(docId);
			ListOfSymptomSynonyms.add(d.get(ValueSearched));
		}
		return ListOfSymptomSynonyms;
	}

	/**
	 * @return a list with all CUI in the big file not indexed
	 * @throws IOException
	 * @throws ParseException
	 */
	public static ArrayList<String> searchAllCui() throws IOException, ParseException{

		ArrayList<String> result = new ArrayList<String>();
		File file=new File("omim_onto.csv");
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		String line;
		while((line=reader.readLine())!=null){
			String[] texts=line.split(",");
			if(texts.length>5){
				if(!result.contains(texts[5]))
					result.add(texts[5]);
			}
		}
		reader.close();
		fr.close();
		//System.out.println("They are "+result.size()+" different CUI in the file .csv");
		return result;
	}
}

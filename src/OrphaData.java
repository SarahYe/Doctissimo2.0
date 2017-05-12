import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONValue;
import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import com.fourspaces.couchdb.ViewResults;

public class OrphaData {
	
		public OrphaData(){}
		
		static Session dbSession;

		static Database db;

		public static Document getDocument(String id, String name, String group, String designation, String language) {
			Document doc = new Document();
			doc.setId(id);
			doc.put("EmpNO", id);
			doc.put("Name", name);
			doc.put("Group", group);
			doc.put("Designation", designation);
			doc.put("Language", language);
			return doc;
		}

		public static void saveDocument(Document doc) {
			try {
				db.saveDocument(doc);
			} catch (Exception e) {
			}
		}

		public static int getTotalDocumentCount() {
			int count = db.getDocumentCount();
			System.out.println("Total Documents: " + count);
			return count;
		}

		public static void deleteDocument(String id) {
			Document d;
			try {
				d = db.getDocument(id);
				System.out.println("Document 1: " + d);
				db.deleteDocument(d);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static void viewAllDocuments() {
			ViewResults results = db.getAllDocuments();
			List<Document> documentsList = results.getResults();
			if (documentsList != null){
				for (Document doc : documentsList){
					System.out.println(doc.get("id") + " : " + doc);
				}
			}
		}

		public ArrayList<String> getDiseaseByClinicalSign(String key) {
			ArrayList<String> ListOfDisease = new ArrayList<String>();
			try {
				key = "\""+ key + "\"";
				key = key.replaceAll(" ", "%20");
				key = key.replaceAll("\"", "%22");
				//key = key.toLowerCase();
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpGet get = new HttpGet("http://couchdb.telecomnancy.univ-lorraine.fr/orphadatabase/_design/clinicalsigns/_view/GetDiseaseByClinicalSign?key="+ key );
				HttpResponse response = httpclient.execute(get);
				HttpEntity entity = response.getEntity();
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
				String strdata = null;
				String jsonString = "";
				
				while ((strdata = reader.readLine()) != null)
				{
					 //System.out.println(strdata);
					jsonString += strdata;
				}
				//System.out.println("Json String: " + jsonString);
				Map<String, Object> jsonMap = getMapFromJsonString(jsonString);
				if (jsonMap != null)
				{
					//System.out.println("total_rows: " + jsonMap.get("total_rows"));
					//System.out.println("offset: " + jsonMap.get("offset"));
					List<Map> rowsList = (List<Map>) jsonMap.get("rows");
					if (rowsList != null)
					{
						for (Map row : rowsList)
						{
							//System.out.println("----------------");
							//System.out.println("Symptom: " + row.get("key"));
							//System.out.println("Value: " + row.get("value"));
							//System.out.println("Disease: " + ((Map) row.get("value")).get("disease"));
							//System.out.println("_id: " + ((Map) row.get("value")).get("_id"));
							//System.out.println("Disease_id: " + ((Map) ((Map) row.get("value")).get("disease")).get("id"));
							ListOfDisease.add((String) ((Map) ((Map) ((Map) row.get("value")).get("disease")).get("Name")).get("text"));
							//System.out.println("Disease from OrphaData: " + ((Map) ((Map) ((Map) row.get("value")).get("disease")).get("Name")).get("text"));
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {

				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return ListOfDisease;
		}

		public static Map<String, Object> getMapFromJsonString(String jsonString) {
			Map<String, Object> jsonMap = (Map<String, Object>) JSONValue.parse(jsonString);
			//System.out.println("Json Map: " + jsonMap);
			return jsonMap;
		}
}

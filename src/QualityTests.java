import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;

public class QualityTests {

	
	public static void main(String args[]) throws IOException, ParseException, SQLException, ClassNotFoundException{
		
		ArrayList<String> CUISider=ConnexionSider.queryMeddraAllCui();
		ArrayList<String> CUIOmim=OmimCSV.searchAllCui();
		float i=0;
		for(String s : CUISider){
			if(CUIOmim.contains(s))
				i++;
		}
		System.out.println(i+" CUI are in common\n"+"ratio="+(i/CUISider.size()));
		
		System.out.println("******************************************************");
		ArrayList<String> sign_idSqlite=ConnexionSQLite.queryMeddraSymptomID();
		ArrayList<String> sign_idhpObo=HpObo.extractID();
		i=0;
		for(String s : sign_idSqlite){
			if(sign_idhpObo.contains(s))
				i++;
		}
		System.out.println(i+" symptom_id are in common\n"+"ratio="+(i/sign_idSqlite.size()));
		//OrphaData.viewAllDocuments();
	}
}

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.lucene.queryparser.classic.ParseException;

import com.mysql.jdbc.Statement;

public class MappingQuality {
	
	/************************************************
	 * INITIALIZING THE DATABASES
	 ***********************************************/

	static ConnexionSQLite connexionSQLite = new ConnexionSQLite("hpo_annotations.sqlite");
	static ConnexionSider connexionSider = new ConnexionSider();
	static OrphaData orpha = new OrphaData();

	final static File file1 = new File("hp.obo");
	static HpObo hpobo = new HpObo(file1);

	final static File file2 = new File("omim_onto.csv");
	static OmimCSV omimOnto = new OmimCSV(file2);

	final static File file3 = new File("br08303.keg");
	static Medecine medicine = new Medecine(file3);

	final static File file4 = new File("chemical.sources.v5.0.tsv");
	static Stitch stitch = new Stitch(file4);

	static Omim omim = new Omim("omim.txt");

	
	public static void main(String args[]) throws IOException, ParseException, SQLException, ClassNotFoundException{
	

		/*********************************************************************************************************************************
		 * CONNECTIONS
		 *********************************************************************************************************************************/

		try {
			connexionSQLite.connect();
			connexionSider.connect();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		/*********************************************************************************************************************************
		 * QUANTIFYING THE QUALITY OF MAPPINGS
		 *********************************************************************************************************************************/

		/***************************
		 * Correspondence CID/ATC
		 ***************************/
//		ArrayList<String> ListOfAllCID = new ArrayList<String>();
//		 ArrayList<String> ListOfATC = new ArrayList<String>();
//		 float correspondence = 0;
//		 ListOfAllCID = connexionSider.queryMeddraAllIndicationsCID();
//		 //System.out.println(ListOfAllCID.size());
//		for (String CID: ListOfAllCID){
//			String cid = CID.substring(0,3);  //Retrieves the first 3 characters of the string
//			String cidB = CID.substring(4,12); //Recovers characters from 4th to 11th
//
//			CID = cid+cidB;
//			try {
//				ListOfATC = stitch.searchByCID(CID, "Chemical", "Alias", "Medecine_id");
//				if (ListOfATC.size() != 0){
//					correspondence++;
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		//System.out.println(correspondence);
//		System.out.println("\nRatio CID/ATC: " + (correspondence/ListOfAllCID.size()));
//		System.out.println("******************************************************");
//
//		 /***************************
//		 * Correspondence ATC/CID
//		 ***************************/
//		ArrayList<String> ListOfAllCID2 = new ArrayList<String>();
//		ArrayList<String> ListOfAllCuiMatching = new ArrayList<String>();
//		float correspondenceATCCID = 0;
//		try {
//			ListOfAllCID2 = stitch.extractionCID();
//			//System.out.println(ListOfAllATC.size());
//			 for (String CID: ListOfAllCID2){
//
//				String cid = CID.substring(0,3);  //Retrieves the first 3 characters of the string
//				String cidB = CID.substring(3,11); //Recovers characters from 4th to 11th
//
//				CID=cid + "1"+ cidB;
//				//System.out.println(CID);
//				 ListOfAllCuiMatching = connexionSider.queryMeddraAllIndicationsByCID(CID);
//				 if (ListOfAllCuiMatching.size() != 0){
//					 correspondenceATCCID++;
//					 //System.out.println(correspondenceATCCID);
//				}
//			 }
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		//System.out.println(correspondenceATCCID);
//		System.out.println("Ratio ATC/CID: " + (correspondenceATCCID/3386));
//		System.out.println("******************************************************");
//
//		  /******************************
//		  * Correspondence ATC/Medecine
//		  ******************************/
//		float correspondenceATCMedicine = 0;
//		try {
//			ArrayList<String> ListOfAllMedicineId = stitch.extractionATC();
//
//			for (String medicine_id: ListOfAllMedicineId){
//				//System.out.println(medicine_id);
//				String medecine = medicine.search(medicine_id, "ATC_Code", "medicine_name");
//
//				if (medecine != null){
//					correspondenceATCMedicine++;
//					//System.out.println(correspondenceATCMedicine);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		//System.out.println(correspondenceATCMedicine);
//		System.out.println("Ratio medicine_id/medicine: " + (correspondenceATCMedicine/3386));
//		System.out.println("******************************************************");
//
//		   /******************************
//		   * Correspondence Medecine/ATC
//		   ******************************/
//		ArrayList<String> ListOfAllMedicineId = new ArrayList<String>();
//		float correspondenceMedicineATC = 0;
//		try {
//			ListOfAllMedicineId = medicine.extractionMedicineID();
//			ArrayList<String> ListOfATCStitch = stitch.extractionATC();
//			for (String medicine_id: ListOfAllMedicineId){
//				//System.out.println(medicine_id);
//				if (ListOfATCStitch.contains(medicine_id)){
//					correspondenceMedicineATC++;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		//System.out.println(correspondenceMedicineATC);
//		//System.out.println(ListOfAllMedicineId.size());
//		System.out.println("Ratio medicine/medicine_id: " + (correspondenceMedicineATC/ListOfAllMedicineId.size()));
//		System.out.println("******************************************************");
		
		 /**************************************
		   * Correspondence CUI in Sider & Omim
		   *************************************/
		
		ArrayList<String> CUISider = ConnexionSider.queryMeddraAllCui();
		ArrayList<String> CUIOmim = OmimCSV.searchAllCui();
		float i1 = 0,i2 = 0;
		for(String s : CUISider){
			if(CUIOmim.contains(s))
				i1++;
		}
		System.out.println("Ratio CUI from Sider to Omim: "+(i1/CUISider.size()));
		System.out.println("******************************************************");
		
		for(String s : CUIOmim){
			if(CUISider.contains(s))
				i2++;
		}
		System.out.println("Ratio CUI from Omim to Sider: "+(i2/CUIOmim.size()));
		System.out.println("******************************************************");
		
		/*************************************************************
		   * Correspondence symptom_id in hpoObo & Hpoannotations
//		   ***********************************************************/
//		ArrayList<String> sign_idSqlite = ConnexionSQLite.querySymptomID();
//		ArrayList<String> sign_idhpObo = HpObo.extractID();
//		float i=0;
//		for(String s : sign_idSqlite){
//			if(sign_idhpObo.contains(s))
//				i++;
//		}
//		System.out.println("Ratio symptom_id from hpo.sqlite to hpo.obo: "+(i/sign_idSqlite.size()));
//		System.out.println("******************************************************");
//		System.out.println("Ratio symptom_id from hpo.obo to hpo.sqlite: "+(i/sign_idhpObo.size()));
	}
}

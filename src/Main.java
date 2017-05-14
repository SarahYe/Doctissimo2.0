
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.lucene.queryparser.classic.ParseException;

public class Main {

	private static ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

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


	public static ArrayList<ArrayList<String>> doc(String args) throws SQLException, ParseException {

		/*********************************************************************************************************************************
		 * CONNECTIONS
		 *********************************************************************************************************************************/
		for(int i=0;i<3;i++){
			result.add(new ArrayList<String>());
		}

		try {
			connexionSQLite.connect();
			//connexionSQLite.query("SELECT * FROM phenotype_annotation");
			connexionSider.connect();
			//orpha.getDiseaseByClinicalSign("Round face");
			//orpha.getHTML().toString();
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
		/*ArrayList<String> ListOfAllCID = new ArrayList<String>();
		 ArrayList<String> ListOfATC = new ArrayList<String>();
		 float correspondence = 0;
		 ListOfAllCID = connexionSider.queryMeddraAllIndicationsCID();
		 //System.out.println(ListOfAllCID.size());
		for (String CID: ListOfAllCID){
			String cid = CID.substring(0,3);  //Retrieves the first 3 characters of the string
			String cidB = CID.substring(4,12); //Recovers characters from 4th to 11th

			CID = cid+cidB;
			try {
				ListOfATC = stitch.searchByCID(CID, "Chemical", "Alias", "Medecine_id");
				if (ListOfATC.size() != 0){
					correspondence++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(correspondence);
		System.out.println("Ratio CID/ATC: " + (correspondence/ListOfAllCID.size()));

		 *//***************************
		 * Correspondence ATC/CID
		 ***************************//*
		ArrayList<String> ListOfAllCID2 = new ArrayList<String>();
		ArrayList<String> ListOfAllCuiMatching = new ArrayList<String>();
		float correspondenceATCCID = 0;
		try {
			ListOfAllCID2 = stitch.extractionCID();
			//System.out.println(ListOfAllATC.size());
			 for (String CID: ListOfAllCID2){

				String cid = CID.substring(0,3);  //Retrieves the first 3 characters of the string
				String cidB = CID.substring(3,11); //Recovers characters from 4th to 11th

				CID=cid + "1"+ cidB;
				//System.out.println(CID);
				 ListOfAllCuiMatching = connexionSider.queryMeddraAllIndicationsByCID(CID);
				 if (ListOfAllCuiMatching.size() != 0){
					 correspondenceATCCID++;
					 //System.out.println(correspondenceATCCID);
				}
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(correspondenceATCCID);
		System.out.println("Ratio ATC/CID: " + (correspondenceATCCID/3386));

		  *//******************************
		  * Correspondence ATC/Medecine
		  ******************************//*
		float correspondenceATCMedicine = 0;
		try {
			ArrayList<String> ListOfAllMedicineId = stitch.extractionATC();

			for (String medicine_id: ListOfAllMedicineId){
				//System.out.println(medicine_id);
				String medecine = medicine.search(medicine_id, "ATC_Code", "medicine_name");

				if (medecine != null){
					correspondenceATCMedicine++;
					//System.out.println(correspondenceATCMedicine);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(correspondenceATCMedicine);
		System.out.println("Ratio medicine_id/medicine: " + (correspondenceATCMedicine/3386));

		   *//******************************
		   * Correspondence Medecine/ATC
		   ******************************//*
		ArrayList<String> ListOfAllMedicineId = new ArrayList<String>();
		float correspondenceMedicineATC = 0;
		try {
			ListOfAllMedicineId = medicine.extractionMedicineID();
			ArrayList<String> ListOfATCStitch = stitch.extractionATC();
			for (String medicine_id: ListOfAllMedicineId){
				//System.out.println(medicine_id);
				if (ListOfATCStitch.contains(medicine_id)){
					correspondenceMedicineATC++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(correspondenceMedicineATC);
		System.out.println(ListOfAllMedicineId.size());
		System.out.println("Ratio medicine/medicine_id: " + (correspondenceMedicineATC/ListOfAllMedicineId.size()));
		    */

		/******************************
		  Correspondence Meddra/OMIM
		 ******************************/

		/*public ArrayList<String> queryMeddraCUI() throws SQLException {
			/*Statement requete = con.createStatement();
			ResultSet res = requete.executeQuery("SELECT stitch_compound_id FROM meddra");																																																																																																																																																					
			ArrayList<String> ListOfAllCID = new ArrayList<String>();
			while (res.next()) {
				ListOfAllCID.add(res.getString("stitch_compound_id"));
			}
			if (res != null)
				res.close();
			if (requete != null)
				requete.close();

			return ListOfAllCUI;
		}
		
		ArrayList<String> ListOfCUI= connexionSider.querryMeddraByCui();
		ArrayList<String> ListOfCUI2= new ArrayList<String>();
		
		float correspondenceMedraOmim = 0;
		for (String CUI: ListOfALLCUI){
			//System.out.println(CUI 	);
			try {
				ListOFCUI= OmimCSV.searchByCui(CUI, field, ValueSearched);
				if (listOFCUI.size() != 0){
					correspondenceMedraOmim++;
					//System.out.println(correspondenceMedraOmim);
				}
			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
			//System.out.println(correspondenceMedraOmim);

			/******************************
		  Correspondence OMIM/MEDRA
			 ******************************/
/*
			float correspondenceOmimMedra = 0;
			ArrayList<String> ListOfCUI2 = new ArrayList<String>();
			ArrayList<String> ListOfALLCUI2 =new ArrayList<String>();

			ListOFCUI2= connexionSider.queryMeddraAllIndicationsByCui();
			for (String CUI2: ListOfCUI2){

				try {
					ListOfALLCUI2 = stitch.searchByCUI(CUI, ,);
					if (ListOfATC.size() != 0){
						correspondenceOmimMedra++;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
			//System.out.println(correspondenceOmimMedra);	
			//System.out.println("Ratio cuiMeddra/cuiOMIM: " + ());*/

			/****************************************************************************************************************************
			 *  INDEXING 
			 ****************************************************************************************************************************/

			//try {
			//hpobo.indexation(); // Indexing in "HPO.obo"
			//hpobo.extraction();
			//omim.indexation(); //indexing in "OMIM.txt"
			//medicine.indexation(); //indexing in "br08303.keg"
			//stitch.indexation(); //indexing in "chemical.sources.v5.0.tsv"
			//omimOnto.indexation(); //indexing in "omim_onto.csv"
			//} catch (IOException e){
			//	e.printStackTrace();
			//}





			char answer = 'Y';
			Scanner sc = new Scanner(System.in);

			while (answer =='Y'){
				//System.out.println("\nPlease enter a symptom name: ");
				//String symptom = sc.nextLine();

				if (args.contains("&")){
					System.out.println("Conjonction");

					/*String[] s = symptom.split("&");
				for (int i = 0; i<s.length; i++){
					doctissimo(s[i]);
				}*/

				}
				else if (args.contains("|")){
					System.out.println("Disjonction");
					//doctissimo(symptom);
				}
				else {
					result=doctissimo(args);
				}


				/******************
				 * Restarting
				 ******************/

				answer = ' ';

				//while(answer !='Y' && answer != 'N'){
				//System.out.println("Restart ? (Y/N)");
				//answer = sc.nextLine().charAt(0);
				//}
			}
			System.out.println("Hope we help you !");
			return result;
		}	


		public static ArrayList<ArrayList<String>> doctissimo (String symptom) throws SQLException, ParseException{
			/*************************************************************************************************************************************
			 * 
			 * Symptom to Disease & Medicine to cure
			 * 
			 *************************************************************************************************************************************/

			ArrayList<ArrayList<String>> results=new ArrayList<ArrayList<String>>();
			for(int i=0;i<3;i++){
				results.add(new ArrayList<String>());
			}

			ArrayList<String> ListOfCui = new ArrayList<String>();
			ArrayList<String> ListOfCuiViaMeddra = new ArrayList<String>();
			ArrayList<String> ListOfCuiViaHPO = new ArrayList<String>();
			ArrayList<String> ListOfSymptomId = new ArrayList<String>();
			ArrayList<String> ListeOfCID = new ArrayList<String>();
			ArrayList<String> ListOfMedecineId = new ArrayList<String>();
			ArrayList<String> ListOfMedecine = new ArrayList<String>();
			ArrayList<ArrayList<String>> ListOfDiseaseByHpoSqlLite = new ArrayList<ArrayList<String>>();
			ArrayList<String> FinalListOfDiseaseByHPOSQLlite = new ArrayList<String>();
			ArrayList<String> ListOfDiseaseFromOrphaData = new ArrayList<String>();
			ArrayList<String> ListOfDiseaseFromOmim = new ArrayList<String>();
			ArrayList<String> ListOfSynonymsFromOnto = new ArrayList<String>();
			ArrayList<String> ListOfSynonymsFromHPObo = new ArrayList<String>();

			try {
				ListOfSymptomId = hpobo.search(symptom, "Symptom", "Symptom_id");
				ListOfSynonymsFromHPObo = hpobo.search(symptom, "Symptom", "synomyms");
				ListOfCuiViaMeddra = connexionSider.queryMeddraByName(symptom);

				for (String symptom_id: ListOfSymptomId){
					symptom_id = "HP:" + symptom_id; //transformation function to match with HP:xxxxxxx in Hpo_annotations
					ListOfDiseaseByHpoSqlLite.add(connexionSQLite.queryBySymptomId(symptom_id));
					for (ArrayList<String> ListDisease: ListOfDiseaseByHpoSqlLite){
						for (String disease: ListDisease){
							//System.out.println(disease);
							if (!FinalListOfDiseaseByHPOSQLlite.contains(disease)){
								FinalListOfDiseaseByHPOSQLlite.add(disease);
							}
						}
						//System.out.println("ListDisease: "+ListDisease.size());
					}
				}


				ListOfDiseaseFromOrphaData = orpha.getDiseaseByClinicalSign(symptom);
				ListOfDiseaseFromOmim = omim.searchIndexWithPhraseQuery(symptom, "Symptom", "Disease");
				int i = 0;
				for (String disease: FinalListOfDiseaseByHPOSQLlite){
					disease = disease.toLowerCase();
					for (String diseaseFromOrpha : ListOfDiseaseFromOrphaData){
						diseaseFromOrpha.toLowerCase();
						for (String diseaseFromOmim : ListOfDiseaseFromOmim){
							diseaseFromOmim.toLowerCase();
							if ((diseaseFromOrpha.replaceAll(" ","").contains(disease.replaceAll(" ","")) ||disease.replaceAll(" ","").contains(diseaseFromOrpha.replaceAll(" ",""))) && (diseaseFromOmim.replaceAll(" ","").contains(disease.replaceAll(" ","")) || disease.replaceAll(" ","").contains(diseaseFromOmim.replaceAll(" ","")))){
								results.get(0).add("From HPO_annotations, Omim & OrphaData : "+ disease);
								System.out.println("Disease from HPO_annotations, Omim & OrphaData : " + disease); 
							}
						}	
					}
				}

				for (String disease: FinalListOfDiseaseByHPOSQLlite){
					disease = disease.toLowerCase();
					for (String diseaseFromOmim : ListOfDiseaseFromOmim){
						diseaseFromOmim = diseaseFromOmim.toLowerCase();
						if (diseaseFromOmim.replaceAll(" ","").contains(disease.replaceAll(" ","")) || disease.replaceAll(" ","").contains(diseaseFromOmim.replaceAll(" ",""))){
							//System.out.println(disease + "\n" + diseaseFromOmim);
							results.get(0).add("From HPO_annotations & OMIM: "+ disease);
							System.out.println("Disease from HPO_annotations & OMIM: " + disease);
						}
					}
				}

				for (String disease: FinalListOfDiseaseByHPOSQLlite){
					disease = disease.toLowerCase();
					for (String diseaseFromOrpha: ListOfDiseaseFromOrphaData){
						diseaseFromOrpha = diseaseFromOrpha.toLowerCase();
						if (diseaseFromOrpha.replaceAll(" ","").contains(disease.replaceAll(" ","")) || disease.replaceAll(" ","").contains(diseaseFromOrpha.replaceAll(" ",""))){
							results.get(0).add("From HPO_annotations & OrphaData: "+ disease);
							System.out.println("Disease from HPO_annotations & OrphaData: " + disease);
						}
					}
				}

				for (String disease: ListOfDiseaseFromOmim){
					disease = disease.toLowerCase();
					for (String diseaseFromOrpha: ListOfDiseaseFromOrphaData){
						diseaseFromOrpha = diseaseFromOrpha.toLowerCase();
						if (diseaseFromOrpha.replaceAll(" ","").contains(disease.replaceAll(" ","")) || disease.replaceAll(" ","").contains(diseaseFromOrpha.replaceAll(" ",""))){
							results.get(0).add("From OMIM & OrphaData: "+ disease);
							System.out.println("Disease from OMIM & OrphaData : " + diseaseFromOrpha);
						}
					}	
				}

				for (String disease: FinalListOfDiseaseByHPOSQLlite){
					disease = disease.toLowerCase();
					results.get(0).add("From HPO_annotations: "+disease);
					System.out.println("Disease from HPO_annotations: "+ disease);
				}

				for (String disease: ListOfDiseaseFromOmim){
					disease = disease.toLowerCase();
					results.get(0).add("From OMIM: "+ disease);
					System.out.println("\nDisease from OMIM: " + disease);
				}

				for (String disease: ListOfDiseaseFromOrphaData){
					disease = disease.toLowerCase();
					results.get(0).add("From OrphaData: " +disease);
					System.out.println("Disease from OrphaData: " + disease);
				}


				/************************************************
				 * Collect medecine to cure the symptom
				 ************************************************/
				//ListOfCuiViaHPO = connexionSider.queryMeddraByName(disease);
				ListOfCui.addAll(ListOfCuiViaHPO);
				for (String cui: ListOfCuiViaMeddra){
					if (ListOfCui.contains(cui) == false)
						ListOfCui.add(cui);
				}


				for(String cui: ListOfCui){
					ListeOfCID = connexionSider.queryMeddraAllIndicationsByCui(cui);

					for (String CID: ListeOfCID)
					{

						String cid = CID.substring(0,3);  //Retrieves the first 3 characters of the string
						String cidB = CID.substring(4,12); //Recovers characters from 4th to 11th

						CID = cid+cidB;

						ListOfMedecineId = stitch.search(CID, "Chemical", CID, "Alias", "Medecine_id");

						for (String medecine_id : ListOfMedecineId){

							String medecine = medicine.search(medecine_id, "ATC_Code", "medicine_name");

							if (!ListOfMedecine.contains(medecine)){
								ListOfMedecine.add(medecine);
							}
						}
						/******** End medecines searching *************/
					}

					/************************************************************************
					 * Collect possible diseases in OMIM via the synonym of the symptom
					 ***********************************************************************/

					ListOfSynonymsFromOnto = omimOnto.searchByCui(cui, "cui", "symptom_name");
					//ListOfSynonymsFromOnto = omimOnto.searchByCui(cui, "cui", "synonyms");
				}

				for (String medecine: ListOfMedecine){
					results.get(1).add(medecine);
					System.out.println("Medicine: "+medecine);
				}


				System.out.println("There is "+ (FinalListOfDiseaseByHPOSQLlite.size()+ ListOfDiseaseFromOrphaData.size() + ListOfDiseaseFromOmim.size()) + " possible diseases if you have "+ symptom);
				System.out.println("And "+ListOfMedecine.size() + " medicines to cure "+ symptom);
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*******************************************************************************************************************************
			 * 
			 * Check if the entered symptom is a side effect
			 * 
			 *******************************************************************************************************************************/

			ArrayList<ArrayList<String>> ListeOfCIDCoupleResponsible = connexionSider.queryMeddraAllSeBySideEffect(symptom);

			if (ListeOfCIDCoupleResponsible.size() !=0){
				int i=0;

				ArrayList<String> ListOfMedecineIdResponsible = new ArrayList<String>();
				ArrayList<String> ListOfMedecineResponsible = new ArrayList<String>();

				for (ArrayList<String> CIDCouple : ListeOfCIDCoupleResponsible)
				{
					String CID1 = CIDCouple.get(0);
					String CID2 = CIDCouple.get(1);

					String cid1 = CID1.substring(0,3);
					String cid1B = CID1.substring(4,12);

					String cid2 = CID2.substring(0,3);
					String cid2B = CID2.substring(4,12);

					CID1 = cid1+cid1B;
					CID2 = cid2+cid2B;
					//System.out.println((i++) + cid + " "+ cidB);

					try {
						ListOfMedecineIdResponsible = stitch.search(CID1, "Chemical", CID2, "Alias", "Medecine_id");

						for (String medecine_id : ListOfMedecineIdResponsible){

							String medecine = medicine.search(medecine_id, "ATC_Code", "medicine_name");

							if (!ListOfMedecineResponsible.contains(medecine)){
								ListOfMedecineResponsible.add(medecine);
							}
						}

					} catch (IOException e) {
						e.printStackTrace();
					} 
				}

				for (String medecine: ListOfMedecineResponsible){
					System.out.println("Responsible Medicine: " + medecine);
					results.get(2).add(medecine);
				}
				System.out.println(ListOfMedecineResponsible.size() + " medicines are responsible of " + symptom) ;	

			} else {
				System.out.println(symptom + " is not a side effect");
			}
			return results;
		}
	}

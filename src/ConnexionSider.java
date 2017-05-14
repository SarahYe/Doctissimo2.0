import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class ConnexionSider {

	static String DRIVER = "com.mysql.jdbc.Driver";
	static String HOST = "jdbc:mysql://neptune.telecomnancy.univ-lorraine.fr:3306/";
	static String DB = "gmd";
	static String LOGIN = "gmd-read";
	static String PWD = "esial";
	private static Connection con ;

	public ConnexionSider(){}

	public static boolean connect() throws ClassNotFoundException, IOException{
		try{
			Class.forName(DRIVER);
			con = DriverManager.getConnection(HOST+DB, LOGIN, PWD);
			System.out.println("Connection to Sider succeeded");
		}
		catch (ClassNotFoundException e){
			System.err.println("Could not load JDBC driver");
			System.out.println("Exception: "+ e);
			e.printStackTrace();
		}
		catch (SQLException ex){
			System.err.println("Error msg: "+ ex.getMessage());
			System.err.println("SQL State: "+ ex.getSQLState());
			System.err.println("error code: "+ ex.getErrorCode());
			ex.printStackTrace();
			ex = ex.getNextException();
		}
		return true;
	}

	/****************************************
	 * Search all in meddra
	 * @throws SQLException
	 *******************************************/

	public ResultSet queryMeddra() throws SQLException{
		Statement st = con.createStatement();
		ResultSet res = st.executeQuery("SELECT * FROM meddra");  

		while (res.next()){
			String cui = res.getString("cui");
			String concept_type = res.getString("concept_type");
			int meddra_id = res.getInt("meddra_id");
			String label = res.getString("label");


			System.out.println(cui + ", " + concept_type + ", " + meddra_id + ", " + label);
		}

		res.close();
		st.close();
		con.close();
		return res;
	}

	/****************************************
	 * Search all in meddra_all_indications
	 * @throws SQLException
	 *******************************************/

	public ResultSet queryMeddraAllIndications() throws SQLException{
		Statement st = con.createStatement();
		ResultSet res = st.executeQuery("SELECT * FROM meddra_all_indications");  

		while (res.next()){
			String cui = res.getString("cui");
			String symptom_name = res.getString("concept_name");
			String symptom_synonym = res.getString("meddra_concept_name");


			System.out.println(cui + ", " + symptom_name + ", " + symptom_synonym);
		}

		res.close();
		st.close();
		con.close();
		return res;
	}

	/****************************************
	 * Search all in meddra_all_se
	 * @throws SQLException
	 *******************************************/

	public ResultSet queryMeddraAllSe() throws SQLException{
		Statement st = con.createStatement();
		ResultSet res = st.executeQuery("SELECT * FROM meddra_all_se");  

		while (res.next()){
			String cui = res.getString("cui");
			String CID1 = res.getString("stitch_compound_id1");
			String CID2 = res.getString("stitch_compound_id2");
			String side_effect = res.getString("side_effect_name");


			System.out.println(cui + ", " + CID1 + ", " + CID2 + ", " + side_effect);
		}

		res.close();
		st.close();
		con.close();
		return res;
	}

	/****************************************
	 * Search by symptom name in meddra
	 * @throws SQLException
	 *******************************************/

	public ArrayList<String> queryMeddraByName(String label) throws SQLException {
		Statement requete = con.createStatement();
		ArrayList<String> ListOfCui = new ArrayList<String>();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra` WHERE `label`=\"" + label+'"');
		while (res.next()) {
			//System.out.println(res.getString("label") + ", symptom_id: " + res.getInt("meddra_id") + ", cui: "+ res.getString("cui"));
			ListOfCui.add(res.getString("cui"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return ListOfCui;
	}

	/****************************************
	 * Search by cui in meddra
	 * @throws SQLException
	 *******************************************/

	public ResultSet queryMeddraByCui(String cui) throws SQLException {
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra` WHERE `cui`=\"" + cui+'"');
		while (res.next()) {
			System.out.println(res.getString("cui") + ", symptom_id: " + res.getInt("meddra_id") + ", label: "+ res.getString("label"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return res;
	}

	/****************************************
	 * Search all CID in meddra_all_indications
	 * @throws SQLException
	 *******************************************/

	public ArrayList<String> queryMeddraAllIndicationsCID() throws SQLException {
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT stitch_compound_id FROM meddra_all_indications");
		ArrayList<String> ListOfAllCID = new ArrayList<String>();
		while (res.next()) {
			ListOfAllCID.add(res.getString("stitch_compound_id"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return ListOfAllCID;
	}

	/****************************************
	 * Search by side_effect in meddra_all_se
	 * @throws SQLException
	 *******************************************/

	public ArrayList<ArrayList<String>> queryMeddraAllSeBySideEffect(String symptom) throws SQLException {
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra_all_se` WHERE `side_effect_name`=\"" + symptom+'"');
		//HashMap<String, String> CIDcouple = new HashMap<String, String>();
		ArrayList<ArrayList<String>> ListeOfCIDCouple = new ArrayList<ArrayList<String>>();
		while (res.next()) {
			ArrayList<String> CIDCouple = new ArrayList<String>();
			CIDCouple.add(res.getString("stitch_compound_id1"));
			CIDCouple.add(res.getString("stitch_compound_id2"));
			ListeOfCIDCouple.add(CIDCouple);
			//System.out.println("CID1: " + res.getString("stitch_compound_id1") + ", CID2: " + res.getString("stitch_compound_id2") + ", cui: "+ res.getString("cui"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return ListeOfCIDCouple;
	}

	/****************************************
	 * Search by cui in meddra_all_se
	 * @throws SQLException
	 *******************************************/

	public ArrayList<ArrayList<String>> queryMeddraAllSeByCui(String cui) throws SQLException {
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra_all_se` WHERE `cui`=\"" + cui +'"');
		//HashMap<String, String> CIDcouple = new HashMap<String, String>();
		ArrayList<ArrayList<String>> ListeOfCIDCouple = new ArrayList<ArrayList<String>>();
		while (res.next()) {
			ArrayList<String> CIDCouple = new ArrayList<String>();
			CIDCouple.add(res.getString("stitch_compound_id1"));
			CIDCouple.add(res.getString("stitch_compound_id2"));
			ListeOfCIDCouple.add(CIDCouple);
			//System.out.println("CID1: " + res.getString("stitch_compound_id1") + ", CID2: " + res.getString("stitch_compound_id2") + ", cui: "+ res.getString("cui"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return ListeOfCIDCouple;
	}

	/*******************************************
	 * Search by cui in meddra_all_indiations
	 * @throws SQLException
	 *******************************************/

	public ArrayList<String> queryMeddraAllIndicationsByCui(String cui) throws SQLException {
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra_all_indications` WHERE `cui`=\"" + cui +'"');
		//HashMap<String, String> CIDcouple = new HashMap<String, String>();
		ArrayList<String> ListeOfCID = new ArrayList<String>();
		while (res.next()) {
			ListeOfCID.add(res.getString("stitch_compound_id"));
			//System.out.println("CID1: " + res.getString("stitch_compound_id1") + ", CID2: " + res.getString("stitch_compound_id2") + ", cui: "+ res.getString("cui"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return ListeOfCID;
	}

	/*******************************************
	 * Search by CID in meddra_all_indiations
	 * @throws SQLException
	 *******************************************/

	public ArrayList<String> queryMeddraAllIndicationsByCID(String cid) throws SQLException {
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra_all_indications` WHERE `stitch_compound_id`=\"" + cid +'"');
		//HashMap<String, String> CIDcouple = new HashMap<String, String>();
		ArrayList<String> ListeOfCUI = new ArrayList<String>();
		while (res.next()) {
			ListeOfCUI.add(res.getString("cui"));
			//System.out.println("CID1: " + res.getString("stitch_compound_id1") + ", CID2: " + res.getString("stitch_compound_id2") + ", cui: "+ res.getString("cui"));
		}
		if (res != null)
			res.close();
		if (requete != null)
			requete.close();

		return ListeOfCUI;
	}

	/**
	 * @param 
	 * @return all cui existing in meddra
	 * @throws SQLException
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static ArrayList<String> queryMeddraAllCui() throws SQLException, ClassNotFoundException, IOException {
		connect();
		ArrayList<String> result=new ArrayList<String>();
		Statement requete = con.createStatement();
		ResultSet res = requete.executeQuery("SELECT * FROM `meddra`");
		while (res.next()) {
			if(!result.contains(res.getString("cui")))
				result.add(res.getString("cui"));
		}
		res.close();
		requete.close();
		System.out.println("They are "+result.size()+" different CUI in the Meddra file");
		return result;
	}
}

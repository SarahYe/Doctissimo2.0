
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ConnexionSQLite {
	//private String DBPath = "./hpo_annotations.sqlite";
	private String DBPath = "";
	private Connection connection = null;
	private Statement statement = null;

	public ConnexionSQLite(String dBPath) {
		this.DBPath = dBPath;
	}

	public void connect() throws ClassNotFoundException, IOException{
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
			//statement = connection.createStatement();
			System.out.println("Connection to " + DBPath + " succeeded");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not load JDBC driver");
			System.out.println("Exception: "+ e);
			e.printStackTrace();
		} catch (SQLException ex){
			System.err.println("Error msg: "+ ex.getMessage());
			System.err.println("SQL State: "+ ex.getSQLState());
			System.err.println("error code: "+ ex.getErrorCode());
			ex.printStackTrace();
			ex = ex.getNextException();
		}
	}

	public ResultSet query(String requete) throws SQLException {
		ResultSet resultat = null;
		try {
			statement = connection.createStatement();
			resultat = statement.executeQuery(requete);
			
			while (resultat.next()){
				String disease_id = resultat.getString("disease_id");
				String disease_name = resultat.getString("disease_label");
				int sign_id = resultat.getInt("sign_id");
				
	      		//System.out.println(disease_id + ", " + disease_name + ", " + sign_id );
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur dans la requete : " + requete);
		}
		
		resultat.close();
		statement.close();
		connection.close();
		return resultat;

	}
	
	/****************************************
	 * Search by symptom id in hpo.sqlite
	 * @throws SQLException
	*******************************************/
	
	public ArrayList<String> queryBySymptomId(String symptom_id) throws SQLException {
				Statement requete = connection.createStatement();
				ArrayList<String> ListOfDisease = new ArrayList<String>();
				ResultSet res = requete.executeQuery("SELECT * FROM `phenotype_annotation` WHERE `sign_id`=\"" + symptom_id +'"');
				while (res.next()) {
					//System.out.println(res.getString("label") + ", symptom_id: " + res.getInt("meddra_id") + ", cui: "+ res.getString("cui"));
					ListOfDisease.add(res.getString("disease_label"));
				}
				if (res != null)
					res.close();
				if (requete != null)
					requete.close();
		
		return ListOfDisease;
	}

/*	public void addSymptom(Symptom Symptom) {
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("INSERT INTO Symptom VALUES(?,?,?,?,?,?)");
			preparedStatement.setString(1, Symptom.getSymptom_id());
			preparedStatement.executeUpdate();
			System.out.println("Insertion avec succes");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}

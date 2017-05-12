import java.util.ArrayList;

public class Symptom {
	
	private String symptom_id;
	private String symptom_name;
	private String alt_symptom_id; 
	private ArrayList<String> symptom_synonyms;

	public Symptom (String symptom_id, String symptom_name, String alt_symptom_id, ArrayList<String> symptom_synonyms){
		this.symptom_id			= symptom_id;
		this.symptom_name		= symptom_name;
		this.setAlt_symptom_id(alt_symptom_id);
		this.symptom_synonyms	= symptom_synonyms;
	}

	public String getSymptom_id() {
		return symptom_id;
	}

	public void setSymptom_id(String symptom_id) {
		this.symptom_id = symptom_id;
	}

	public String getSymptom_name() {
		return symptom_name;
	}

	public void setSymptom_name(String symptom_name) {
		this.symptom_name = symptom_name;
	}

	public ArrayList<String> getSymptom_synonyms() {
		return symptom_synonyms;
	}

	public String getAlt_symptom_id() {
		return alt_symptom_id;
	}

	public void setAlt_symptom_id(String alt_symptom_id) {
		this.alt_symptom_id = alt_symptom_id;
	}
	
	public void setSymptom_synonyms(ArrayList<String> symptom_synonyms) {
		this.symptom_synonyms = symptom_synonyms;
	}

	@Override
	public String toString() {
		return symptom_id + ", " + symptom_name + ", "
				+ alt_symptom_id + ", " + symptom_synonyms;
	}
	
}

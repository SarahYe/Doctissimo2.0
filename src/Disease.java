
public class Disease {
	
	private String disease_name;
	private String disease_id;
	private String symptom_id;

	public Disease(String disease_id, String disease_name, String symptom_id){
		this.disease_id 	= disease_id;
		this.disease_name	= disease_name;
		this.symptom_id 	= symptom_id;
	}

	public String getDisease_name() {
		return disease_name;
	}

	public void setDisease_name(String disease_name) {
		this.disease_name = disease_name;
	}

	public String getDisease_id() {
		return disease_id;
	}

	public void setDisease_id(String disease_id) {
		this.disease_id = disease_id;
	}

	public String getSymptom_id() {
		return symptom_id;
	}

	public void setSymptom_id(String symptom_id) {
		this.symptom_id = symptom_id;
	}

	@Override
	public String toString() {
		return "disease_name= " + disease_name + ", disease_id= " + disease_id + ", symptom_id= " + symptom_id;
	}
	
	
}

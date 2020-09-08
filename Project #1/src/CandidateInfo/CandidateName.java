package CandidateInfo;

public class CandidateName {
	int id; // id of candidate
	String name; // name of candidate
	
	public CandidateName(String candidateFileLine) { // divides information received to respective places
		String[] candidate = candidateFileLine.split(",");
		int converter = Integer.parseInt(candidate[1]);
		
		this.id = converter;
		this.name = candidate[0];
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

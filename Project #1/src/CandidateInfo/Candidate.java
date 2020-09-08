package CandidateInfo;

public class Candidate {
	int id;
	int rank;
	
	public Candidate(String candidateInfo) { // divides information received accordingly
		String[] ballotInfo = candidateInfo.split(":");
		
		this.id = Integer.parseInt(ballotInfo[0]);
		this.rank = Integer.parseInt(ballotInfo[1]);
	}

	public int getId() {
		return id;
	} 

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}

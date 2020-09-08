package ElectionSet;

import java.util.Iterator;

import CandidateInfo.Candidate;
import ElectionSet.DynamicSet;
 
public class Ballot {

		// dynamic set
		Candidate[] theArray;
		
		// current max capacity
		
		int ballotNumber; // ballotID
		int candiateId; // ID for each candidate
		int rank; // rank given to each candidate
		int ballotSize; // how many candidates were voted for in ballot

		public Ballot(int initialCapacity) {
			this.theArray = new Candidate[initialCapacity];
		} 
		
		public Ballot(int initialCapacity, int size, int id) { // this constructor is only used in MainSet class when we try to find a candidate to be eliminated;
			this.theArray = new Candidate[initialCapacity];
			this.ballotSize = size;
			this.ballotNumber = id; 
		}  
		
		public Ballot(int initialCapacity, String ballot) { // receives information from each line of ballots.csv and delivers it accordingly
			this.theArray = new Candidate[initialCapacity];
			
			String[] values = ballot.split(",");
	
			int convertInt = Integer.parseInt(values[0]);
			this.ballotNumber = convertInt;
			
			if(values.length > 1) { // after finding ballotID, we send everything else to Candidate class Constructor 
				for (int i = 1; i < values.length; i++) { 
					Candidate candidate = new Candidate(values[i]); 
					theArray[i - 1] = candidate;
					ballotSize ++;
				}  
			}
			
			else
				this.ballotSize = 1;

		}
		 

	public int getBallotNum() { // returns the ballot number
		return this.ballotNumber;
	}

	public int getRankByCandidate(int candidateId) { // rank for that candidate
		if(getBallotSize() > 1) {
			for (int i = 0; i < ballotSize; i++) {
				if(theArray[i].getId() == candidateId) 
					return theArray[i].getRank(); 
			}
		}
		return -1; 
	}
	
	public int getCandidateByRank(int rank) { // candidate with that rank
		if(getBallotSize() > 1) {
			for (int i = 0; i < ballotSize; i++) {
				if(theArray[i].getRank() == rank) 
					return theArray[i].getId(); 
			}
		}
		
		return -1; 
	}
	
	public int getBallotSize() {
		return ballotSize;
	}
	
	public void setBallotSize(int ballotSize) {
		this.ballotSize = ballotSize;
	}

	public boolean eliminate(int candidateId ) { // eliminates candidate with that Id
		int rank1 = 0; 
		boolean reduce = false;
		for (int i = 0; i < ballotSize - 2; i++) { 
			if(theArray[i].getId() == candidateId) {
				rank1 = theArray[i].getRank();
				for (int j = i; j < ballotSize; j++) {
					theArray[j] = theArray[j + 1]; 
					reduce = true;
				}
			}
			
			if(reduce && theArray[i].getRank() > rank1)
				theArray[i].setRank(theArray[i].getRank() - 1);
		}
		this.ballotSize --;
		return false; // returns false if candidate with that Id is not found
	}
	
	public boolean isValid() { // checks to see if ballot is valid
		if(repeatedRanks() || repeatedId() || !inOrder()) 
			return false;   
		return true; 
	}
	
	public boolean repeatedRanks() { //checks to see if the voter gave the same rank to more than one candidate
		for (int i = 0; i < ballotSize - 1; i++) {
			for (int j = i + 1; j < ballotSize; j++) {
				if(theArray[i].getRank() == theArray[j].getRank())
					return true;
			}  
		} 
		return false;   
	}
	
	public boolean repeatedId() { // checks to see if a candidate is repeated in the ballot
		for (int i = 0; i < ballotSize - 1; i++) {
			for (int j = i + 1; j < ballotSize; j++) {
				if(theArray[i].getId() == theArray[j].getId())
					return true;
			}
		}
		
		return false;
	}
	
	public boolean inOrder() { // checks to see if votes were given in order of 1 to n (1 to k if candidates were not voted for)
		for (int i = 0; i < ballotSize - 1; i++) {
			if(theArray[i + 1].getRank() - theArray[i].getRank() !=1)
				return false; 
		}
		
		return true;
	}
	
	public void eliminateLosers(MainSet losers) { // this method eliminates from ballot all candidates that had been previously eliminated
		for (int i = 0; i < ballotSize - 1; i++) { 
			for (BallotDynamicSet set : losers) {
				if(theArray[i].getId() == set.getIdNumber())
					this.eliminate(theArray[i].getId());
			} 
		}
	}

}
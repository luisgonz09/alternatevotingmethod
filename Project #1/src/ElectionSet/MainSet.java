package ElectionSet;

import java.util.Iterator;

import CandidateInfo.Candidate;

public class MainSet implements Set<BallotDynamicSet> {

	// static set
	private StaticSet<BallotDynamicSet> theSet;
	
	// current max capacity
	private int maxCapacity;

	private static final int DEFAULT_SET_SIZE = 10;

	public MainSet(int initialCapacity) {
		this.theSet = new StaticSet<BallotDynamicSet>(initialCapacity);
		this.maxCapacity = initialCapacity;
	}
	@Override
	public Iterator<BallotDynamicSet> iterator() {
		return theSet.iterator();
	}

	@Override
	public boolean add(BallotDynamicSet obj) {
		if (this.isMember(obj)) // Avoid extending the set unnecessarily
			return false;
		if (this.maxCapacity == this.theSet.size())
		{
			this.maxCapacity *= 2;
			StaticSet<BallotDynamicSet> newSet = new StaticSet<BallotDynamicSet>(this.maxCapacity);
			copySet(theSet, newSet);
			this.theSet = newSet;
		}
		return this.theSet.add(obj);
	}

	private void copySet(Set<BallotDynamicSet> src, Set<BallotDynamicSet> dst) {
		for (Object obj : src)
			dst.add((BallotDynamicSet) obj);
	}

	@Override
	public boolean isMember(BallotDynamicSet obj) {
		return this.theSet.isMember(obj);
	}

	@Override
	public boolean remove(BallotDynamicSet obj) {
		return this.theSet.remove(obj);
	}

	@Override
	public boolean isEmpty() {
		return this.theSet.isEmpty();
	}

	@Override
	public int size() {
		return this.theSet.size();
	}

	@Override
	public void clear() {
		this.theSet.clear();
	}

	@Override
	public Set<BallotDynamicSet> union(Set<BallotDynamicSet> S2) {
		Set<BallotDynamicSet> temp = this.theSet.union(S2);
		DynamicSet<BallotDynamicSet> result = new DynamicSet<BallotDynamicSet>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public Set<BallotDynamicSet> difference(Set<BallotDynamicSet> S2) {
		Set<BallotDynamicSet> temp = this.theSet.difference(S2);
		DynamicSet<BallotDynamicSet> result = new DynamicSet<BallotDynamicSet>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public Set<BallotDynamicSet> intersection(Set<BallotDynamicSet> S2) {
		Set<BallotDynamicSet> temp = this.theSet.intersection(S2); 
		DynamicSet<BallotDynamicSet> result = new DynamicSet<BallotDynamicSet>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;    
	}

	@Override
	public boolean isSubSet(Set<BallotDynamicSet> S2) {
		return this.theSet.isSubSet(S2); 
	}
	
	@Override
	public boolean equals(Set<BallotDynamicSet> S2) {
		int checker;
		for (Object set1: this) {
			checker = 0;
			for (Object set2: S2) {
				if(set1.equals(set2)) {
					checker = 1;
				}
 			}
			
			if(checker == 0)
				return false;
		}
			
		return true;
	}
	
	public static boolean checkDisjoint(Set<Integer>[] sets) {
		Set<Integer> intFind = new DynamicSet<>(sets.length);
		
		for (int i = 0; i < sets.length - 1; i++) {

				if(i == 0) {
					intFind = sets[i].intersection(sets[i + 1]); 	
				}
				
				else {
					intFind = intFind.intersection(sets[i + 1]);
				}
		}
		
		if(intFind.isEmpty())
			return true;
		
		return false;  
	}
	 
	@Override
	public Set<Set<BallotDynamicSet>> singletonSets() {
		Set<Set<BallotDynamicSet>> result = new StaticSet<Set<BallotDynamicSet>>(this.size());
		
		for (BallotDynamicSet set1 : this) {
			Set<BallotDynamicSet> set2 = new StaticSet<BallotDynamicSet>(this.size());
			set2.add(set1);
			result.add(set2);
		}
		
		 
		return result; 
	}
	
	public Ballot eliminateLeast(MainSet election, BallotDynamicSet ballotStore, int numberOfCandidates) { // if no winner is found, method finds and removes candidate with least amount of rank 1's (if there is not tie)
		int lowest = 0; // this variable stores lowest amount of rank 1's found
		Ballot eliminate = null; // ballot is able to store id and size of candidate found with lowest rank 1's
		  
		int first = 0; // indicates we're on first iteration
		for (BallotDynamicSet ballotSet : election) {  

			if(first == 0 || ballotSet.getFirstPlaceBallots() < lowest) { // here we assing value of lowest rank 1's found to lowest variable
				lowest = ballotSet.getFirstPlaceBallots(); 
				eliminate = new Ballot(10, ballotSet.getFirstPlaceBallots(), ballotSet.getIdNumber());
				first  = 1;
			}  
		}
		
		int repeat = 0; // if repeat is 2 or greater, it means we have more than one candidate with the same least amount of rank 1's
		BallotDynamicSet temp = new BallotDynamicSet(10); // stores sets of candidates with same least amount of rank 1's
		for (BallotDynamicSet ballotSet : election) { // looks to see if that amount of ranks is repeated more than once in set, if so, we have a tie!
			if(ballotSet.getFirstPlaceBallots() == lowest) { 
				repeat ++; 
				temp.add(new Ballot(10, ballotSet.getFirstPlaceBallots(), ballotSet.getIdNumber()));
			}
 		}
		
		if(repeat < 2) // if there is just one repetition, it means one candidate has the lowest amount and we can proceed to eliminate
			return eliminate;     
		
		else // if there is more than one candidate with same lowest amount of rank 1's, we call eliminateNext method, which takes care of that and return the person to be eliminated according to rules
			eliminate = eliminateNext(temp, ballotStore, numberOfCandidates);
		
		if(eliminate == null) // if we never find a winner based on lowest ranks rule (from 1 to n), we proceed to eliminate based on highest ID#
			eliminate = eliminatebyId(temp); 
		
		return eliminate; 
	} 
	
	public Ballot eliminateNext(BallotDynamicSet temp, BallotDynamicSet ballotStore, int numberOfCandidates) { // if there is a tie, this method is called to find who among those tied, has the least rank 2's
		for(int i = 2; i <= numberOfCandidates; i++) { // goes from 2 to n (# of candidates) and tries to find a winner
			
			for (Ballot ballot : temp) { // here we set ballotSize of each ballot to 0, in order to make it easier to know which candidate has the lowest amount of rank n (from 2 to n)
				ballot.setBallotSize(0);
			}
			
			for (Ballot ballot : ballotStore) { // ballotStore stores all ballots that are valid (independent of who got a rank of 1) 
				for (Ballot set : temp) { // here we look to see how many times candidates that were tied for lowest rank 1's, got a rank of n (we start with 2 and stop with n)
					if(ballot.getCandidateByRank(i) == set.getBallotNum())
						set.setBallotSize(set.getBallotSize() + 1);
				}
			}
			
			for (Ballot ballot : temp) { // if between those tied for least rank n - 1, there is at least one that has more rank n than the others, this one will be removed from possible losers
				if(ballot.getBallotSize() > findMinimal(temp)) 
					temp.remove(ballot);
			}
			
			Ballot eliminate = findLeast(temp); 
			if(!(eliminate == null))
				return eliminate;  
		}
		
		return null; 
	}
	
	public Ballot findLeast(BallotDynamicSet temp) { // this method finds the next person to be eliminated for eliminateNext method
		int leastOne = 0; // finds candidates with least amount of rank n's
		int first = 0; // indicates we're on first iteration
		Ballot eliminate = new Ballot(10);
		
		for (Ballot set : temp) { // here we look again for candidate with least amount of rank n's 
			if(first == 0 || (set.getBallotSize() < leastOne)) {
				eliminate = set;
				first ++;
				leastOne = set.getBallotSize();
			} 
		}
		
		int repeat = 0; // if greater than 1, we have a tie
		for (Ballot set : temp) { // here we look to see how many times a candidate was found with the same least amount of rank n's
			if(set.getBallotSize() == leastOne)
				repeat ++;
		}
		if(repeat  < 2) // if repeat is less than 2, there was only one candidate with that amount of rank n's and we can proceed to eliminate him
			return eliminate; 
		
		return null; // if there is a tie, method returns null and we continue on for loop in findLeast() until we find a single candidate with least amount of rank n's
	}
	
	public int findMinimal(BallotDynamicSet temp) { // this method returns the leastAmount of rank n's found in temp Set for eliminateNext(), this serves as a solution in case we have more than two ties and then one of those needs to be discarted from being eliminated
		int min = 0; // stores value of set with least rank n's
		int first = 1; // let's us know we're on first iteration
		for (Ballot set : temp) { // find candidate with least amount of rank n's, based on their sizes
			if(first == 1 || set.getBallotSize() < min)
				min = set.getBallotSize();
		}
		return min; // returns smallest size for future references in eliminateNext method
	}
	 
	public Ballot eliminatebyId(BallotDynamicSet temp) { // if we're unable to eliminate by rank, we use this method to eliminate by ID#
		int highest = 0; // stores value of candidate with highest ID found
		int first = 1; // indicates we're on first iteration
		
		for (Ballot ballot : temp) { // find highest ID #
			if(first == 1 || ballot.getBallotNum() > highest) {
				highest = ballot.getBallotNum();
				first ++;
			}
		}
		
		for (Ballot ballot : temp) { // find candidate with highest ID # and return his ballot to eliminateLeast(); this is last resource to eliminate someone if lowest ranks give no result  
			if(ballot.getBallotNum() == highest)
				return ballot;
		} 
		return null; 
	}
	
	public void reorganizeSet (int eliminatedCandidateId, MainSet losers) { // after candidate is removed, ballots are reorganized and current set is declared null 
		BallotDynamicSet temp = new BallotDynamicSet(10); // temporarily stores all ballots from that candidate being removed
		
		for (BallotDynamicSet ballotByRank : this) { // finds that candidate in the MainSet, stores all it's ballots in an array, eliminates candidate set
			if(ballotByRank.getIdNumber() == eliminatedCandidateId) { // find candidate with that ID #
				for (Ballot candidate : ballotByRank) { // when found, we eliminate him from each of his ballots
					candidate.eliminate(ballotByRank.getIdNumber());
					temp.add(candidate); 
				}
				theSet.remove(ballotByRank); // remove candidate from main set
			}
		}
		
		for (Ballot ballot : temp) { // this for loop calls on a method from ballot class than eliminates every candidate in ballots if candidate was previously eliminated
			ballot.eliminateLosers(losers);
		} 
		
		for (Ballot ballotByRank: temp) { // for each ballot, we find their respective candidates, add them to that Set and increase the number of first place ballots that candidate had
			for (BallotDynamicSet ballot : this) {
				if(ballotByRank.getCandidateByRank(1) == ballot.getIdNumber()) {
					ballot.add(ballotByRank);
					ballot.setFirstPlaceBallots(ballot.getFirstPlaceBallots() + 1);
				}
			}
		}
	}

}

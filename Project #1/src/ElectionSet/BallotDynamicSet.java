package ElectionSet;

import java.util.Iterator;

import CandidateInfo.Candidate;
import ElectionSet.Ballot;

public class BallotDynamicSet implements Set<Ballot>{ 

	// static set
	private StaticSet<Ballot> theSet;
	
	// current max capacity
	private int maxCapacity;
	
	int idNumber; // let's us know which candidate this set belongs to
	int firstPlaceBallots = 0; // counts number of ballots where candidate had a rank of 1

	private static final int DEFAULT_SET_SIZE = 10;
	
	public BallotDynamicSet(int initialCapacity, Ballot ballot) { // stores each ballots where this candidate got a rank of 1
		this.theSet = new StaticSet<Ballot>(initialCapacity);
		this.maxCapacity = initialCapacity;
		theSet.add(ballot);
		
	}
	
	public BallotDynamicSet(int initialCapacity) {
		this.theSet = new StaticSet<Ballot>(initialCapacity);
		this.maxCapacity = initialCapacity;
	} 
	
	@Override
	public Iterator<Ballot> iterator() {
		return theSet.iterator();
	}

	@Override
	public boolean add(Ballot obj) {
		if (this.isMember(obj)) // Avoid extending the set unnecessarily
			return false;
		if (this.maxCapacity == this.theSet.size())
		{
			this.maxCapacity *= 2;
			StaticSet<Ballot> newSet = new StaticSet<Ballot>(this.maxCapacity);
			copySet(theSet, newSet);
			this.theSet = newSet;
		} 
		return this.theSet.add(obj); 
	}

	private void copySet(Set<Ballot> src, Set<Ballot> dst) {
		for (Object obj : src)
			dst.add((Ballot) obj);
	}

	@Override
	public boolean isMember(Ballot obj) {
		return this.theSet.isMember(obj);
	}

	@Override
	public boolean remove(Ballot obj) {
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
	public Set<Ballot> union(Set<Ballot> S2) {
		Set<Ballot> temp = this.theSet.union(S2);
		DynamicSet<Ballot> result = new DynamicSet<Ballot>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public Set<Ballot> difference(Set<Ballot> S2) {
		Set<Ballot> temp = this.theSet.difference(S2);
		DynamicSet<Ballot> result = new DynamicSet<Ballot>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;
	}

	@Override
	public Set<Ballot> intersection(Set<Ballot> S2) {
		Set<Ballot> temp = this.theSet.intersection(S2); 
		DynamicSet<Ballot> result = new DynamicSet<Ballot>(DEFAULT_SET_SIZE);
		copySet(temp, result);
		return result;    
	}

	@Override
	public boolean isSubSet(Set<Ballot> S2) {
		return this.theSet.isSubSet(S2); 
	}
	
	@Override
	public boolean equals(Set<Ballot> S2) {
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
	public Set<Set<Ballot>> singletonSets() {
		Set<Set<Ballot>> result = new StaticSet<Set<Ballot>>(this.size());
		
		for (Ballot set1 : this) {
			Set<Ballot> set2 = new StaticSet<Ballot>(this.size());
			set2.add(set1);
			result.add(set2);
		}
		
		 
		return result; 
	}
	
	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(int idNumber) { // define which candidate the Set belongs too
		this.idNumber = idNumber;
	}

	public int getFirstPlaceBallots() {
		return firstPlaceBallots;
	}

	public void setFirstPlaceBallots(int firstPlaceBallots) {
		this.firstPlaceBallots = firstPlaceBallots;
	} 
	
	

}
package Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Scanner;
import java.util.Set;

import CandidateInfo.Candidate;
import CandidateInfo.CandidateName;
import ElectionSet.Ballot;
import ElectionSet.BallotDynamicSet;
import ElectionSet.DynamicSet;
import ElectionSet.MainSet;
import ElectionSet.StaticSet;

public class Election {

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		readFiles();
		
	}
	
	
	public static void readFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException {
				//reading the files 
				String ballots = "ballots.csv";
				String candidates = "candidates.csv";
				File file = new File(ballots);
				File file2 = new File(candidates);
				CandidateName[] candidateNames = new CandidateName[20];
				int numberOfCandidates = 0;
				
				try { // here we read the candidates.csv file and assign each line to the constructor of CandidateName class
					Scanner inputStream = new Scanner(file2);
					while(inputStream.hasNext()) {
						String data2 = inputStream.next() + " " + inputStream.next();
						String [] check = data2.split(",");
						CandidateName candidatesByName = new CandidateName(data2); // stores name of candidates
						candidateNames[numberOfCandidates] = candidatesByName; // stores all candidates with access to their ID's and names
						numberOfCandidates ++; // counts how many candidates we have
					}

					inputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int invalid = 0; // invalid ballots
				int empty = 0; // empty ballots
				int valid = 0; //valid ballots
				BallotDynamicSet ballotStore = new BallotDynamicSet(10); // temporary stores all ballots (later we will paste them into according sets)
				MainSet election = new MainSet(numberOfCandidates); // stores all sets of candidate rank 1 ballots
				 
				try { // here we read the ballots.csv file and assign each line to Ballot class constructor and then add each ballot to it's according set
					Scanner inputStream = new Scanner(file);
					while(inputStream.hasNext()) {
						String data = inputStream.next();
						Ballot ballot = new Ballot(10, data); // new ballot is created for each iteration (data is sent to constructor)
						
						if(!ballot.isValid()) // checks for valid ballots
							invalid ++;
						
						if(ballot.getBallotSize() == 1) // checks for empty ballots
							empty ++;
						
						if(ballot.isValid() && ballot.getBallotSize() > 1) // if ballot is not invalid nor empty, it's denominated "valid", meaning we're going to use it for the vote count
							valid ++;
						
						//Adding all ballots to set
						if(ballot.isValid())
							ballotStore.add(ballot);	
						
					}
					
					inputStream.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int i = 0;
				while(i < numberOfCandidates) { // here I created a position for each candidate on ballot, to later add their first place ballots accordingly (in for loop below)
					BallotDynamicSet candidate = new BallotDynamicSet(10);
					candidate.setIdNumber(candidateNames[i].getId());
					election.add(candidate);
					i ++; 
				}
				
				for (Ballot ballot : ballotStore) { // here I pasted each ballot to it's respective candidate where that candidate got a rank of 1
					for (BallotDynamicSet ballotSet : election) {
						if(ballot.getCandidateByRank(1) == ballotSet.getIdNumber()) {
							ballotSet.add(ballot);
							ballotSet.setFirstPlaceBallots(ballotSet.getFirstPlaceBallots() + 1);
						}
					}
				}	

				int round = 1; // used for knowing in which round each candidate was eliminated
				MainSet losers = new MainSet(10); // stores data for each candidate eliminated for future references
				String eliminated = null; // gets ID of eliminated candidate 
				while(!checkWinner(election, valid, candidateNames)) { // keeps looping until a winner is found
						Ballot temp = election.eliminateLeast(election, ballotStore, numberOfCandidates); // this method removes the candidate with least amount of rank 1s (if there is a tie, we move up a rank and so forth until we can eliminate a candidate); it also receives set with all ballots so we can iterate over it in case there's a tie

						for (BallotDynamicSet set : election) { // this for loop helps us add each loser to losers set and determine # of rank 1's for each candidate eliminated
							if(temp.getBallotNum() == set.getIdNumber()) {
								losers.add(set);
								
								if(round == 1) // store information about candidate eliminated 
									eliminated = "Round " + round + ": " + candidateName(temp.getBallotNum(), candidateNames) + " was eliminated with " + set.size() + " #1's\n";
								else 
									eliminated = eliminated + "Round " + round + ": " + candidateName(temp.getBallotNum(), candidateNames) + " was eliminated with " + set.size() + " #1's\n";
							}
						} 
						
						for (BallotDynamicSet ballotSet : election) { // here we find in election, which candidate we have to eliminate
							if(ballotSet.getIdNumber() == temp.getBallotNum()) {
								election.reorganizeSet(ballotSet.getIdNumber(), losers); // method reorganizes MainSet election
							}
						}
						
						round ++;
					   
				}
				
				int numberOfOnes = 0;
				for (BallotDynamicSet ballotSet : election) { // here we look for the number of first place votes the winner got
					if(ballotSet.getFirstPlaceBallots() > (valid / 2))
						numberOfOnes = ballotSet.getFirstPlaceBallots();
				}
				
				try (Writer writer = new BufferedWriter(new OutputStreamWriter( // this is the output file
						new FileOutputStream("results.txt"), "utf-8"))) {
					writer.write("There are: "+ (valid + invalid + empty) + " total ballots.\n"+ // here we are writing each line we want to display
							valid + " of those are valid ballots.\n"+
							invalid + " of those are invalid ballots.\n"+
							empty + " are empty ballots.\n"+
							eliminated+ 
							"Winner: " + winnerName(election, valid, candidateNames) + " wins with " + numberOfOnes + " 1's");   
				}
				
		
	}
	
	public static boolean checkWinner(MainSet election, int ballots, CandidateName[] candidates) { // if there is a winner, we return true
		
		for (BallotDynamicSet ballotSet : election) { // counts # of rank 1's for each candidate and determines if it greater than number of valid ballots / 2
			if(ballotSet.size() > (ballots / 2)) { 
				return true; 
			}			
		}
		return false; 
	}
	
	public static String winnerName(MainSet election, int ballots, CandidateName[] candidates) { // After we find out that there is a winner, we find his name and return it
		int idWinner = 0; // stores ID of winner to find his name later
		boolean found = false;
		for (BallotDynamicSet ballotSet : election) { // find winner
			if(ballotSet.size() > (ballots / 2)) {
				idWinner = ballotSet.getIdNumber();
				found = true;
			}			
		}
		
		if(found) { // if we found a winner, we look for his name in CandidatesbyName array
			
			for (int i = 0; i < candidates.length; i++) { // gets winner's name
				if(candidates[i].getId() == idWinner)
					return (candidates[i].getName());  
			}
		}
		
		return null;
	}
	
	public static String candidateName(int candidateId, CandidateName[] candidates) { // here we get name for any candidate (not just winner)
		for (int i = 0; i < candidates.length; i++) { // get winner's name
			if(candidates[i].getId() == candidateId)
				return (candidates[i].getName());  
		}
		
		return null; 
	}

}

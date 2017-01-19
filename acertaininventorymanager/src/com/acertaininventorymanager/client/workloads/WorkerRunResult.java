package com.acertaininventorymanager.client.workloads;

/**
 * 
 * WorkerRunResult class represents the result returned by a worker class after
 * running the workload interactions
 * 
 */
public class WorkerRunResult {
	private int successfulInteractions; // total number of successful interactions
	private int totalRuns; // total number of interactions run 
	private long elapsedTimeInNanoSecs; // total time taken to run all
										// interactions
	private int successfulFrequentInteractionRuns; // number of
															// successful
															// frequent interaction
															// runs
	private int totalFrequentInteractionRuns; // total number of frequent interaction runs

	public WorkerRunResult(int successfulInteractions, long elapsedTimeInNanoSecs,
			int totalRuns, int successfulFrequentInteractionRuns,
			int totalFrequentInteractionRuns) {
		this.setSuccessfulInteractions(successfulInteractions);
		this.setElapsedTimeInNanoSecs(elapsedTimeInNanoSecs);
		this.setTotalRuns(totalRuns);
		this.setSuccessfulFrequentBookStoreInteractionRuns(successfulFrequentInteractionRuns);
		this.setTotalFrequentInteractionRuns(totalFrequentInteractionRuns);
	}

	public int getTotalRuns() {
		return totalRuns;
	}

	public void setTotalRuns(int totalRuns) {
		this.totalRuns = totalRuns;
	}

	public int getSuccessfulInteractions() {
		return successfulInteractions;
	}

	public void setSuccessfulInteractions(int successfulInteractions) {
		this.successfulInteractions = successfulInteractions;
	}

	public long getElapsedTimeInNanoSecs() {
		return elapsedTimeInNanoSecs;
	}

	public void setElapsedTimeInNanoSecs(long elapsedTimeInNanoSecs) {
		this.elapsedTimeInNanoSecs = elapsedTimeInNanoSecs;
	}

	public int getSuccessfulFrequentBookStoreInteractionRuns() {
		return successfulFrequentInteractionRuns;
	}

	public void setSuccessfulFrequentBookStoreInteractionRuns(
			int successfulFrequentBookStoreInteractionRuns) {
		this.successfulFrequentInteractionRuns = successfulFrequentBookStoreInteractionRuns;
	}

	public int getTotalFrequentBookStoreInteractionRuns() {
		return totalFrequentInteractionRuns;
	}

	public void setTotalFrequentInteractionRuns(
			int totalFrequentBookStoreInteractionRuns) {
		this.totalFrequentInteractionRuns = totalFrequentBookStoreInteractionRuns;
	}

	public int getSuccessfulFrequentInteractionRuns() {
		return successfulFrequentInteractionRuns;
	}

	public int getTotalFrequentInteractionRuns() {
		return totalFrequentInteractionRuns;
	}

}

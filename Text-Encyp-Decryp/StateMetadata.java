package com.sample.projectsample;

/**
 * This is an object which holds the current RC4 state.
 * @author Sanjith
 *
 */

public class StateMetadata {

	int[] currentState;
	int i;
	int j;
	
	
	public StateMetadata(int[] currentState, int i, int j) {
		super();
		this.currentState = currentState;
		this.i = i;
		this.j = j;
	}
	public int[] getCurrentState() {
		return currentState;
	}
	public void setCurrentState(int[] currentState) {
		this.currentState = currentState;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	
	
}

package com.poker.iago.pokercounter.model;

/**
 * This class represents a Blind Level in a poker tournament
 * 
 * @author iago
 * 
 */
public class BlindsLevel {

	private int minutes;
	private int bb;
	private int sb;

	public BlindsLevel(int sb, int bb, int minutes) {
		super();
		this.minutes = minutes;
		this.bb = bb;
		this.sb = sb;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getBb() {
		return bb;
	}

	public void setBb(int bb) {
		this.bb = bb;
	}

	public int getSb() {
		return sb;
	}

	public void setSb(int sb) {
		this.sb = sb;
	}
}

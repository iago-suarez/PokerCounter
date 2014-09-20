package com.poker.iago.pokercounter.model;

import java.util.List;

/**
 * This interface represents a ordered Collection of blinds levels
 * 
 * @author iago
 * 
 */
public interface BlindsDistribution {
	/**
	 * 
	 * @return the name of the distribution, for example doubleDistribution
	 */
	public String getName();

	public List<BlindsLevel> getBlindsLevel();
}

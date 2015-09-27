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
	String getName();

	List<BlindsLevel> getBlindsLevels();

	void addLevel(BlindsLevel blindsLevel);

    void addBlindsDistributionListener(BlindsDistributionListener listener);

    void removeBlindsDistributionListener(BlindsDistributionListener listener);

}

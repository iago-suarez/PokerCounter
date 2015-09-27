package com.poker.iago.pokercounter.model;

import java.util.ArrayList;
import java.util.List;

public class IagoDistribution implements BlindsDistribution {

	private List<BlindsLevel> levels;
	private List<BlindsDistributionListener> listeners = new ArrayList<>();
	private String name = "IagoDistribution";
	
	public IagoDistribution(){
		levels = generateLevels();
		
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<BlindsLevel> getBlindsLevels() {
		return levels;
	}

	@Override
	public void addLevel(BlindsLevel blindsLevel) {
		levels.add(blindsLevel);
        for(BlindsDistributionListener listener : listeners){
            listener.updateBlindsLevels();
        }
	}

	@Override
	public void addBlindsDistributionListener(BlindsDistributionListener listener) {
        listeners.add(listener);
	}

	@Override
	public void removeBlindsDistributionListener(BlindsDistributionListener listener) {
        listeners.remove(listener);
	}

	private List<BlindsLevel> generateLevels(){
		List<BlindsLevel> L = new ArrayList<BlindsLevel>();
		
		L.add(new BlindsLevel(25, 50, 1));
		L.add(new BlindsLevel(50, 100, 2));
		L.add(new BlindsLevel(75, 150, 20));
		L.add(new BlindsLevel(100, 200, 18));
		L.add(new BlindsLevel(150, 300, 18));
		L.add(new BlindsLevel(200, 400, 18));
		L.add(new BlindsLevel(250, 500, 15));
		L.add(new BlindsLevel(300, 600, 15));
		L.add(new BlindsLevel(400, 800, 15));
		L.add(new BlindsLevel(500, 1000, 12));
		L.add(new BlindsLevel(700, 1400, 12));
		L.add(new BlindsLevel(1000,2000, 12));
		L.add(new BlindsLevel(1500,3000, 10));
		L.add(new BlindsLevel(2000,4000, 10));
		L.add(new BlindsLevel(3000,6000, 10));
		L.add(new BlindsLevel(4000,8000, 10));
		L.add(new BlindsLevel(5000,10000, 10));
		return L;
	}
}

package com.chord4js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ServiceFactory {
	
	List<String> layers = new ArrayList<String>();
	List<String> qos = new ArrayList<String>();
	
	public void addQos(List<String> qosList) {
		qos.addAll(qosList);
	}
	
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}
	
	@Override
	public String toString() {
		return "layers: " + layers + " qos: " + qos;
	}
	
	public Service createRandom(Random random) {
		Service identifier = new Service(null);
		identifier.setLayers(layers);
		
		if (qos.size() > 0) {
			// get a random qos
			String value = qos.get(random.nextInt(qos.size()));
			identifier.addQos(value);
		}
		identifier.setProvider(getRandomIP(random));
		
		return identifier;
	}
	
	/**
	 * Create a random IP address.
	 * 
	 * @param random
	 * 
	 * @return
	 */
	private String getRandomIP(Random random) {
		
		int w = getRandomOctet(random);
		int x = getRandomOctet(random);
		int y = getRandomOctet(random);
		int z = getRandomOctet(random);
		
		return w + "." + x + "." + y + "." + z;
		
	}
	
	private int getRandomOctet(Random random) {
		return random.nextInt(254) + 1;
	}
}

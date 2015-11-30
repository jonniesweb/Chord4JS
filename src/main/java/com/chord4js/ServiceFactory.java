package com.chord4js;

import java.util.List;
import java.util.Random;

public class ServiceFactory {
	
	private List<String> layers;
	private List<String> qos;
	
	/**
	 * @param layers
	 * @param qos
	 */
	public ServiceFactory(List<String> layers, List<String> qos) {
		if (layers == null) {
			throw new NullPointerException();
		}
		
		this.layers = layers;
		this.qos = qos;
	}
	
	@Override
	public String toString() {
		return "layers: " + layers + " qos: " + qos;
	}
	
	/**
	 * Create a Service with random Provider and QoS attributes.
	 * 
	 * @param random
	 * @return
	 */
	public Service createRandom(Random random) {
		
		ProviderId providerId = new ProviderId(layers.toArray(new String[] {}), getRandomIP(random));
		
		String value = null;
		if (qos != null && qos.size() > 0) {
			// get a random qos
			value = qos.get(random.nextInt(qos.size()));
		}
		
		return new Service(providerId, value);
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

	public ServiceId getServiceId() {
		return new ServiceId((String[]) layers.toArray());
	}
}

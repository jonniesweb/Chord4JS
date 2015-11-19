package com.chord4js;

import java.util.List;

/**
 * A Service contains a ProviderId with Quality-of-Service information.
 */
public class Service {
	
	private final ProviderId providerId;
	private final List<String> qos;
	
	/**
	 * Create a Service with the given ProviderId and QoS information.
	 * 
	 * @param providerId
	 * @param qos
	 */
	public Service(ProviderId providerId, List<String> qos) {
		this.providerId = providerId;
		this.qos = qos;
		
	}
	
	/**
	 * Create a Service with the ProviderId and no QoS information.
	 * 
	 * @param providerId
	 */
	public Service(ProviderId providerId) {
		this.providerId = providerId;
		qos = null;
	}
	
	@Override
	public String toString() {
		return getProviderId() + " qos:" + getQos();
	}
	
	public ProviderId getProviderId() {
		return providerId;
	}

	public List<String> getQos() {
		return qos;
	}
	
}

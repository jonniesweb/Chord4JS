package com.chord4js;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Service contains a ProviderId with Quality-of-Service information.
 */
public class Service implements Serializable {
	
	/**
   * 
   */
  private static final long serialVersionUID = -5922896053115608353L;
  private final ProviderId providerId;
	private final List<String> qos;
	
	/**
	 * Create a Service with the given ProviderId and many QoS values.
	 * 
	 * @param providerId
	 * @param qosList
	 */
	public Service(ProviderId providerId, List<String> qosList) {
		this.providerId = providerId;
		this.qos = qosList;
		
	}
	
	/**
	 * Create a Service with the given ProviderId and one Qos value.
	 * 
	 * @param providerId
	 * @param qos
	 */
	public Service(ProviderId providerId, String qos) {
		this.providerId = providerId;
		ArrayList<String> qosList = new ArrayList<String>();
		qosList.add(qos);
		this.qos = qosList;
		
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

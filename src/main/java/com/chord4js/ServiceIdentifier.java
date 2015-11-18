package com.chord4js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * A service identifier that provides a functional description of the service
 * and the location (provider) of that service. Quality-of-service information
 * is also kept.
 */
public class ServiceIdentifier implements Key {
	
	/**
	 * Number of layers (functional + provider) that make up a service
	 * identifier. Used in creating the hashed key.
	 */
	public static final int LAYERS = 5;
	
	private List<String> layers = new ArrayList<String>();
	private List<String> qos = new ArrayList<String>();
	private String provider;
	
	public void addQos(String qos2) {
		qos.add(qos2);
	}
	
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getProvider() {
		return provider;
		
	}
	
	@Override
	public String toString() {
		return layers + "" + provider + "" + qos;
	}
	
	/**
	 * Get the functional part of this identifier in string form. This
	 * corresponds to section 3.1 of the paper:
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public String getHashedIdentifier() throws ServiceException {
		if (layers.size() == 0) {
			throw new ServiceException("Not enough functional bits to construct data identifier");
		}
		
		// TODO: assume 5 parts to service identifier, pad sections with zeroes
		// if no functional layer exists. Always put provider bits as last
		// section
		
		// append all layers to the StringBuilder, hashing them at the same time
		// according to section 3.1 of the paper. The hashing algorithm outputs
		// a human readable sha1
		StringBuilder builder = new StringBuilder();
		for (String layer : layers) {
			builder.append(DigestUtils.sha1Hex(layer));
		}
		builder.append(DigestUtils.sha1Hex(provider));
		
		return builder.toString();
	}
	
	public byte[] getBytes() {
		return null;
	}
}

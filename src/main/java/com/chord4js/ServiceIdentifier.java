package com.chord4js;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * A service identifier that provides a functional description of the service
 * and the location (provider) of that service. Quality-of-service information
 * is also kept.
 * 
 * <br><br>
 * We assume that a service identifier has at least 1 and at most
 * <code>{@link #FUNCTIONAL_LAYERS}</code> worth of functional layers and 1
 * provider layer.
 */
public class ServiceIdentifier implements Key {
	
	public static final int FUNCTIONAL_LAYERS = 4;
	public static final int PROVIDER_LAYERS = 1;
	/**
	 * Number of layers (functional + provider) that make up a service
	 * identifier. Used in creating the hashed key.
	 */
	public static final int TOTAL_LAYERS = FUNCTIONAL_LAYERS + PROVIDER_LAYERS;
	
	/**
	 * Length of a SHA-1 hash in hex string form
	 */
	static final int SHA1_LENGTH = 40;
	
	/**
	 * The hash to put in place of a layer with no value
	 */
	static final String EMPTY_LAYER = StringUtils.repeat('0', SHA1_LENGTH);
	
	private List<String> layers = new ArrayList<String>();
	private List<String> qos = new ArrayList<String>();
	private String provider;
	
	public void addQos(String qos2) {
		qos.add(qos2);
	}
	
	public void setLayers(List<String> layers) {
		if (layers.size() > FUNCTIONAL_LAYERS) {
			throw new IllegalArgumentException("too many functional layers. Given " + layers.size()
					+ ", expected " + FUNCTIONAL_LAYERS + " or less");
		}
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
	public String getHashedIdentifier() {
		int layerSize = layers.size();
		int missingLayers = FUNCTIONAL_LAYERS - layerSize;
		if (layerSize == 0) {
			throw new IllegalArgumentException(
					"Not enough functional bits to construct data identifier");
		}
		
		// TODO: assume 5 parts to service identifier, pad sections with zeroes
		// if no functional layer exists. Always put provider bits as last
		// section
		
		// append all layers to the StringBuilder, hashing them at the same time
		// according to section 3.1 of the paper. The hashing algorithm outputs
		// a human readable sha1
		StringBuilder builder = new StringBuilder();
		for (String layer : layers) {
			builder.append(hash(layer));
		}
		
		// fill in zeroes for missing functional layers
		for (int i = 0; i < missingLayers; i++) {
			builder.append(EMPTY_LAYER);
		}
		
		builder.append(hash(provider));
		
		return builder.toString();
	}
	
	public static String hash(String data) {
		return DigestUtils.sha1Hex(data);
	}
	
	public byte[] getBytes() {
		return getHashedIdentifier().getBytes();
	}
	
}

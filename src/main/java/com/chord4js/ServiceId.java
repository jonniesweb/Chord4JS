package com.chord4js;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import de.uniba.wiai.lspi.chord.service.Key;

/**
 * Implementation simplification:
 * 
 * ASSUME: All service names are an N part semantic name + a provider id (all
 * part are strings)
 */
public class ServiceId implements Key {
	
	/**
	 * Length of the hash used in the hex string format
	 */
	static final int HASH_LENGTH = 40;
	
	/**
	 * The hash to put in place of a layer with no value
	 */
	static final String EMPTY_PART_HASH = StringUtils.repeat('0', HASH_LENGTH);
	
	/**
	 * Number of semantic parts for a service
	 */
	public static final int kPartsSemantic = 4;
	
	/**
	 * Number of provider parts for a service
	 */
	public static final int kPartsProvider = 1;
	
	/**
	 * Total number of parts for a service
	 */
	public static final int kPartsAll = kPartsSemantic + kPartsProvider;
	
	/**
	 * Underlying data structure of this class. Stores hashed parts of the
	 * service
	 */
	private final String[] hashedParts = new String[kPartsAll];
	
	ServiceId(final String[] semanticName) {
		// verify input
		if (semanticName.length > kPartsSemantic)
			throw new IllegalArgumentException("Semantic name has too many parts");
		if (semanticName.length == 0)
			throw new IllegalArgumentException("Semantic name has too few parts");
		
		// hash each part, placing it in the hashedParts array
		for (int i = 0; i < semanticName.length; ++i) {
			String part = semanticName[i];
			if (part != null) {
				getHashedParts()[i] = hash(part);
			} else {
				getHashedParts()[i] = null;
			}
		}
	}
	
	/**
	 * Hash the given data as a sha1 hex string
	 * 
	 * @param data
	 * @return
	 */
	protected static String hash(final String data) {
		return DigestUtils.sha1Hex(data);
	}
	
	@Override
	public byte[] getBytes() {
		
		return getHash().getBytes();
	}
	
	/**
	 * Get a string of all semantic and provider hashes appended together
	 * 
	 * @return all the hashes appended from semantic to provider
	 */
	String getHash() {
		StringBuilder builder = new StringBuilder();
		
		// iterate over each value in hashedPart
		for (String part : hashedParts) {
			
			if (part != null) {
				builder.append(part);
			} else {
				builder.append(EMPTY_PART_HASH);
			}
		}
		
		return builder.toString();
	}
	
	public String[] getHashedParts() {
		return hashedParts;
	}
}

package com.chord4js;

public class ProviderId extends ServiceId {
	
	/**
	 * A String representation of the provider part. Use this to know where the
	 * service is located.
	 */
	private final String providerPart;
	
	ProviderId(String[] semanticName, String providerId) {
		super(semanticName);
		this.providerPart = providerId;
		
		// hash the provider id and add it to the hashedParts array
		getHashedParts()[kPartsSemantic] = hash(providerId);
	}
	
	/**
	 * A string of the provider part
	 * 
	 * @return
	 */
	public String getProviderPart() {
		return providerPart;
	}
	
	@Override
	public String toString() {
		return getHash() + " providerPart: " + providerPart;
	}
	
}

package com.chord4js;

public class ProviderId extends ServiceId {
	
	ProviderId(String[] semanticName, String providerId) throws Exception {
		super(semanticName);
		
		// hash the provider id and add it to the hashedParts array
		getHashedParts()[kPartsSemantic] = hash(providerId);
	}
}

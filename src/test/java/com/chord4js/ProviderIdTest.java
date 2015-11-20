package com.chord4js;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ProviderIdTest {
	
	/**
	 * Verify that the provider ID is hashed correctly
	 * @throws Exception
	 */
	@Test
	public void testCreateProviderId() throws Exception {
		
		List<String> layers = Arrays.asList("media", "music", "converter", "mp3");
		
		String providerStr = "providerId";
		ProviderId providerId = new ProviderId(layers.toArray(new String[] {}), providerStr);
		
		String hash = providerId.getHash();
		assertEquals(ServiceId.hash(providerStr), hash.substring(ServiceId.HASH_LENGTH * ServiceId.kPartsSemantic));
		
	}
	
}

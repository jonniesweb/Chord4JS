package com.chord4js;

import static com.chord4js.ServiceId.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ServiceIdTest {
	
	@Test
	public void testGetHashedService() throws Exception {
		List<String> layers = Arrays.asList("media", "music", "converter", "mp3");
		String hashedService = createServiceHashFromLayers(layers.toArray(new String[] {}));
		
		// verify size of hash is correct
		assertEquals(kPartsAll * HASH_LENGTH, hashedService.length());
		
		// verify data is correctly hashed
		assertEquals(hash(layers.get(0)), hashedService.substring(0, HASH_LENGTH));
		
	}
	
	/**
	 * Verify that number of functional layers when less than
	 * {@link Service#FUNCTIONAL_LAYERS} are filled with the empty hash as
	 * padding
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHashedServiceFillEmptyLayers() throws Exception {
		List<String> layers = Arrays.asList("media", "music", "converter");
		String hashedIdentifier = createServiceHashFromLayers(layers.toArray(new String[] {}));
		
		// verify that 4th hash position is an empty layer, padded with zeroes
		String hash = hashedIdentifier.substring(HASH_LENGTH * 3, HASH_LENGTH * 4);
		assertEquals(EMPTY_PART_HASH, hash);
		
	}
	
	/**
	 * Test helper method for instantiating a default {@link Service}
	 * 
	 * @param layers
	 * @param semanticName
	 * @return
	 */
	static String createServiceHashFromLayers(String[] semanticName) {
		ServiceId id = new ServiceId(semanticName);
		String hashedIdentifier = id.getHash();
		return hashedIdentifier;
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooManySemanticLayers() throws Exception {
		List<String> layers = Arrays.asList("media", "music", "converter", "mp3", "flac");
		ServiceId id = new ServiceId((String[]) layers.toArray());
	}
	
	/**
	 * Must have at least one functional layer to create a hashed service
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNotEnoughLayersForHash() throws Exception {
		String hashFromLayers = createServiceHashFromLayers(new String[] {});
	}
}

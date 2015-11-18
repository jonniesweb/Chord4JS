package com.chord4js;

import static com.chord4js.ServiceIdentifier.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.uniba.wiai.lspi.chord.service.ServiceException;

public class ServiceIdentifierTest {
	
	@Test
	public void testGetHashedIdentifier() throws Exception {
		List<String> layers = Arrays.asList("media", "music", "converter", "mp3");
		String hashedIdentifier = createIdentifierHashFromLayers(layers);
		
		// verify size of hash is correct
		assertEquals(TOTAL_LAYERS * SHA1_LENGTH, hashedIdentifier.length());
		
		// verify data is correctly hashed
		assertEquals(hash(layers.get(0)), hashedIdentifier.substring(0, SHA1_LENGTH));
		
	}
	
	/**
	 * Verify that number of functional layers when less than
	 * {@link ServiceIdentifier#FUNCTIONAL_LAYERS} are filled with the empty
	 * hash as padding
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHashedIdentifierFillEmptyLayers() throws Exception {
		List<String> layers = Arrays.asList("media", "music", "converter");
		String hashedIdentifier = createIdentifierHashFromLayers(layers);
		
		// verify that 4th hash position is an empty layer, padded with zeroes
		String hash = hashedIdentifier.substring(SHA1_LENGTH * 3, SHA1_LENGTH * 4);
		assertEquals(EMPTY_LAYER, hash);
		
	}
	
	/**
	 * Test helper method for instantiating a default {@link ServiceIdentifier}
	 * 
	 * @param layers
	 * @return
	 * @throws ServiceException
	 */
	private String createIdentifierHashFromLayers(List<String> layers) throws ServiceException {
		ServiceIdentifier id = new ServiceIdentifier();
		id.setLayers(layers);
		id.setProvider("10.0.0.1");
		String hashedIdentifier = id.getHashedIdentifier();
		return hashedIdentifier;
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTooManyLayers() throws Exception {
		List<String> layers = Arrays.asList("media", "music", "converter", "mp3", "flac");
		ServiceIdentifier id = new ServiceIdentifier();
		id.setLayers(layers);
	}
	
	/**
	 * Must have at least one functional layer to create a hashed identifier
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNotEnoughLayersForHash() throws Exception {
		String hashFromLayers = createIdentifierHashFromLayers(new ArrayList<String>());
		
	}
}

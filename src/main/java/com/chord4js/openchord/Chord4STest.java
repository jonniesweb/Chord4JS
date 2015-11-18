package com.chord4js.openchord;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Set;

import de.uniba.wiai.lspi.chord.console.command.entry.Key;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.Chord4S;

/**
 * A demo of starting, inserting, retrieving and deleting elements of a Chord
 * network.
 */
public class Chord4STest {
	
	private static final String PROTOCOL = URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
	private URL bootstrapURL;
	private int port = 8080;
	
	public Chord4STest() throws Exception {
		
		try {
			
			// setup properties
			System.setProperty("chord.properties.file", "config/chord4S.properties");
			PropertiesLoader.loadPropertyFile();
			
			// create and start the first node
			Chord4S chord = new Chord4S();
			bootstrapURL = getNextURL();
			chord.create(bootstrapURL);
			
			// add a second node (required)
			addNewNode();
			
			// insert some test values
			System.out.println("insert key/values");
			chord.insert(new Key("test"), "hello world");
			chord.insert(new Key("test"), "hello world!");
			
			System.out.println(chord.printEntries());
			
			// retrieve values for a key
			System.out.println("retrieve value by key");
			Set<Serializable> value = chord.retrieve(new Key("test"));
			System.out.println("value: " + value);
			
			// remove a value
			System.out.println("removing value by key");
			chord.remove(new Key("test"), "hello world!");
			
			System.out.println(chord.printEntries());
			
			// leave the network
			chord.leave();
			
		} catch (ServiceException e) {
			e.printStackTrace();
			
		} finally {
			System.exit(0);
		}
		
	}
	
	/**
	 * Get an incrementing local URL. Required for a node to start.
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getNextURL() throws MalformedURLException {
		return new URL(PROTOCOL + "://localhost:" + port++ + "/");
	}
	
	/**
	 * Add a new node to the network of the {@link #bootstrapURL}
	 * 
	 * @throws Exception
	 */
	private void addNewNode() throws Exception {
		
		Chord4S chord = new Chord4S();
		chord.join(getNextURL(), bootstrapURL);
		
	}
	
	public static void main(String[] args) throws Exception {
		new Chord4STest();
	}
	
}

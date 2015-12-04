package com.chord4js.openchord;

import java.net.MalformedURLException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.chord4js.ProviderId;
import com.chord4js.QoSConstraints;
import com.chord4js.Service;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.C4SMsgRetrieve;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * A demo of starting, inserting, retrieving and deleting elements of a Chord
 * network.
 */
public class Chord4STest {
	
	private static final Logger log = Logger.getLogger(Chord4STest.class);
	
	private static final String PROTOCOL = URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
	private URL bootstrapURL;
	private int port = 8080;
	
	public Chord4STest() throws Exception {
		
		try {
			
			// setup properties
			System.setProperty("chord.properties.file", "config/chord4S.properties");
			PropertiesLoader.loadPropertyFile();
			
			// create and start the first node
			ChordImpl chord = new ChordImpl();
			bootstrapURL = getNextURL();
			chord.create(bootstrapURL);
			
			// add a second node (required)
			addNewNode();
			
			// create service
			String[] serviceID = new String[] { "a", "b", "c", "d" };
			ProviderId providerId = new ProviderId(serviceID, "e");
			Service service = new Service(providerId, "s");
			
			// insert some test values
			log.info("insert key/values");
			chord.insert(service);
			chord.insert(service);
			
			log.info(chord.printEntries());
			
			// retrieve values for a key
			log.info("retrieve value by key");
			C4SMsgRetrieve c4sMsgRetrieve = new C4SMsgRetrieve(providerId, new QoSConstraints(), 1);
			
			Set<Service> value = chord.retrieve(c4sMsgRetrieve);
			log.info("value: " + value);
			
			// remove a value
			log.info("removing value by key");
			chord.remove(providerId);
			
			log.info(chord.printEntries());
			
			// leave the network
			chord.leave();
			
		} catch (ServiceException e) {
			log.error("an error occurred. quitting", e);
			
		} finally {
		}
		
		System.exit(0);
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
		
		ChordImpl chord = new ChordImpl();
		chord.join(getNextURL(), bootstrapURL);
		
	}
	
	public static void main(String[] args) throws Exception {
		new Chord4STest();
	}
	
}

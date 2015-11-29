package com.chord4js.evaluation;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;

public class EvaluationControllerImpl implements EvaluationController {
	
	private static final Logger log = Logger.getLogger(EvaluationControllerImpl.class);
	
	private static final String PROTOCOL = URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
	private URL bootstrapURL;
	private int port = 8080;
	
	private Random random;
	
	public EvaluationControllerImpl(Random random) {
		this.random = random;
	}
	
	@Override
	public Set<Chord4SDriver> createChord4SNetwork(int numberOfNodes) throws ServiceException {
		
		HashSet<Chord4SDriver> nodes = new HashSet<>(numberOfNodes);
		
		// create first node for other nodes to bootstrap to
		ChordImpl chord = new ChordImpl();
		bootstrapURL = getNextURL();
		
		// encapsulate chord with a DriverAdapter and add it to the nodes set
		DriverAdapter driver = new DriverAdapter(chord);
		nodes.add(driver);
		
		// create the chord network
		chord.create(bootstrapURL);
		
		// create the other nodes and join them to the network
		for (int i = 0; i < numberOfNodes - 1; i++) {
			log.info("create node " + i);
			Chord4SDriver node = addNewNode();
			nodes.add(node);
		}
		
		return nodes;
	}
	
	/**
	 * Add a new node to the network of the {@link #bootstrapURL}
	 * 
	 * @throws ServiceException
	 * 
	 * @throws Exception
	 */
	private Chord4SDriver addNewNode() throws ServiceException {
		
		ChordImpl chord = new ChordImpl();
		chord.join(getNextURL(), bootstrapURL);
		
		return new DriverAdapter(chord);
	}
	
	@Override
	public Set<Chord4SDriver> crashPercentageOfNodes(Set<Chord4SDriver> chord4sDrivers,
			int percentage) {
		
		int size = chord4sDrivers.size();
		
		// calculate # of nodes to crash
		size = size * (percentage / 100);
		
		// create a list of the nodes for iterating over
		ArrayList<Chord4SDriver> nodes = new ArrayList<>(chord4sDrivers);
		
		// randomly get a node from the list and crash it, then remove it from
		// the list
		for (int i = 0; i < size; i++) {
			int randomNode = random.nextInt(nodes.size());
			Chord4SDriver driver = nodes.get(randomNode);
			driver.crash();
			nodes.remove(randomNode);
		}
		
		// return a set of the alive nodes
		return new HashSet<Chord4SDriver>(nodes);
	}
	
	/**
	 * Get an incrementing local URL. Required for a node to start.
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	private URL getNextURL() {
		try {
			return new URL(PROTOCOL + "://localhost:" + port++ + "/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}

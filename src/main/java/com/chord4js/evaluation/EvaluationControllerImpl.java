package com.chord4js.evaluation;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class EvaluationControllerImpl implements EvaluationController {
	
	private static final String PROTOCOL = URL.KNOWN_PROTOCOLS.get(URL.LOCAL_PROTOCOL);
	private URL bootstrapURL;
	private int port = 8080;
	
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
			System.out.println("create node " + i);
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
		// TODO Auto-generated method stub
		return null;
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

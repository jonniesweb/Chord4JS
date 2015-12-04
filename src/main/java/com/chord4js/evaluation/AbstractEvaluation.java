package com.chord4js.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.chord4js.Service;
import com.chord4js.ServiceFactory;
import com.chord4js.ServiceGenerator;

import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.util.logging.Logger;

public abstract class AbstractEvaluation {
	
	private static final Logger log = Logger.getLogger(AbstractEvaluation.class);
	
	// network sizes for evaluations
	public static final int NODES_2_7  = 1 <<  7;
	public static final int NODES_2_11 = 1 << 11;
	public static final int NODES_2_15 = 1 << 15;
	
	/**
	 * Number of services to generate for inserting into the network
	 */
	private static final int numberOfServices = 10000;
	
	protected Random random = new Random();
	
	/**
	 * ServiceIds that can be queried for in the network
	 */
	protected List<ServiceFactory> serviceFactories;
	
	/**
	 * Unique services that can be inserted into the network
	 */
	protected List<Service> services;
	private Set<Chord4SDriver> nodes;
	
	public AbstractEvaluation() throws ServiceException {
		configure();
		initServices();
	}
	
	protected void initServices() throws ServiceException {
		// generate services
		ServiceGenerator serviceGenerator;
		serviceGenerator = new ServiceGenerator(random);
		serviceFactories = serviceGenerator.getPossibleServices();
		services = serviceGenerator.getServices(numberOfServices);
	}
	
	protected void configure() {
		System.setProperty("chord.properties.file", "config/chord4S.properties");
		PropertiesLoader.loadPropertyFile();
	}
	
	/**
	 * Run the evaluation with the given number of nodes. Overridden by
	 * subclasses of {@link AbstractEvaluation}.
	 * 
	 * @param numberOfNodes
	 */
	public abstract void start(int numberOfNodes);
	
	/**
	 * Run the evaluation with differing sizes of networks. Calls
	 * {@link AbstractEvaluation#start(int)} with the network size.
	 */
	public void evaluate() {
		
		start(NODES_2_7);
		cleanupNodes();
		
		start(NODES_2_11);
		cleanupNodes();
		
		start(NODES_2_15);
		cleanupNodes();
	}
	
	protected void putServicesOnNodes(Set<Chord4SDriver> nodes) {
		// for each service, call put on a random node
		ArrayList<Chord4SDriver> nodesList = new ArrayList<Chord4SDriver>(nodes);
		for (Service service : services) {
			// get a random node from the nodeList
			Chord4SDriver driver = nodesList.get(random.nextInt(nodesList.size()));
			driver.put(service);
		}
	}
	
	/**
	 * Cleanup/remove all nodes. Must create a new network afterwards.
	 */
	public void cleanupNodes() {
	  if (nodes != null)
	    nodes.forEach((n) -> n.crash());
		
		// if performance is bad with calling leave() call crash() instead
		// nodes.forEach((n) -> n.crash());
		
		nodes = null;
	}
	
	/**
	 * @param numberOfNodes
	 * @param controller
	 * @return
	 */
	protected Set<Chord4SDriver> createNetwork(int numberOfNodes, EvaluationController controller) {
		try {
			setNodes(controller.createChord4SNetwork(numberOfNodes));
		} catch (ServiceException e) {
			log.fatal("unable to create the network", e);
			return null;
		}
		return getNodes();
	}
	
	public Set<Chord4SDriver> getNodes() {
		return nodes;
	}
	
	public void setNodes(Set<Chord4SDriver> nodes) {
		this.nodes = nodes;
	}
	
}

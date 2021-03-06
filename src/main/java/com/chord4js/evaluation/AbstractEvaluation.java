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
	
	private final Logger log = Logger.getLogger(AbstractEvaluation.class);
	
	// network sizes for evaluations
	public static final int NODES_2_7 = 1 << 7;
	public static final int NODES_2_9 = 1 << 9;
	public static final int NODES_2_11 = 1 << 11;
	
	/**
	 * Number of services to generate for inserting into the network
	 */
	private static final int numberOfServices = 500;
	
	/**
	 * Start with a seed to get the same results every time
	 */
	protected Random random = new Random(123456);
	
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
		initServices();
	}
	
	protected void initServices() throws ServiceException {
		// generate services
		ServiceGenerator serviceGenerator;
		serviceGenerator = new ServiceGenerator(random);
		serviceFactories = serviceGenerator.getPossibleServices();
		services = serviceGenerator.getServices(numberOfServices);
	}
	
	public static void configure() {
		System.setProperty("chord.properties.file", "config/chord4S.properties");
		
		// logging is configured in the chord.properties.file properties file
		// System.setProperty("log4j.properties.file",
		// "config/log4j.properties");
		PropertiesLoader.loadPropertyFile();
	}
	
	/**
	 * Run the evaluation with the given number of nodes. Overridden by
	 * subclasses of {@link AbstractEvaluation}.
	 * 
	 * Specify the number of maintenance rounds to run after network creation to
	 * update the finger table and find the predecessors.
	 * 
	 * @param numberOfNodes
	 * @param maintenanceRounds
	 */
	public abstract void start(int numberOfNodes, int maintenanceRounds);
	
	/**
	 * Run the evaluation with differing sizes of networks. Calls
	 * {@link AbstractEvaluation#start(int, int)} with the network size and
	 * number of maintenance rounds to run.
	 */
	public void evaluate() {
		
		start(NODES_2_7, 200);
		cleanupNodes();
		
		start(NODES_2_9, 500);
		cleanupNodes();
		
		start(NODES_2_11, 2000);
		cleanupNodes();
		
	}
	
	protected void putServicesOnNodes(Set<Chord4SDriver> nodes) {
		log.info("inserting services on nodes");
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
	protected Set<Chord4SDriver> createNetwork(int numberOfNodes, EvaluationController controller,
			int rounds) {
		try {
			setNodes(controller.createChord4SNetwork(numberOfNodes));
		} catch (ServiceException e) {
			log.fatal("unable to create the network", e);
			return null;
		}
		
		runMaintenanceTasks(nodes, rounds);
		
		return getNodes();
	}
	
	/**
	 * Run the maintenance task a few times for the given nodes
	 */
	protected void runMaintenanceTasks(Set<Chord4SDriver> nodes, int rounds) {
		log.info("running maintenance tasks");
		// run the maintenance tasks a few times
		for (int i = 0; i < rounds; i++) {
			log.debug("running maintenance tasks " + i);
			for (Chord4SDriver driver : nodes) {
				driver.runTasks();
			}
		}
		
		log.info("finished maintenance tasks");
	}
	
	public Set<Chord4SDriver> getNodes() {
		return nodes;
	}
	
	public void setNodes(Set<Chord4SDriver> nodes) {
		this.nodes = nodes;
	}
	
}

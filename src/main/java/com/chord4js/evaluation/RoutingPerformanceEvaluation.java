package com.chord4js.evaluation;

import java.util.Set;

import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.util.logging.Logger;

/**
 * Determine the average number of hops needed for a query requiring a certain
 * number of services.
 */
public class RoutingPerformanceEvaluation extends AbstractEvaluation {
	
	private static final int[] expectedResults = { 1, 2, 3, 4, 5, 6, 7, 8 };
	
	public RoutingPerformanceEvaluation() throws ServiceException {
		super();
	}
	
	private static final Logger log = Logger.getLogger(DataAvailabilityEvaluation.class);
	
	@Override
	public void start(int numberOfNodes) {
		EvaluationControllerImpl controller = new EvaluationControllerImpl(random);
		
		// create nodes
		Set<Chord4SDriver> nodes;
		try {
			nodes = controller.createChord4SNetwork(numberOfNodes);
		} catch (ServiceException e) {
			log.fatal("unable to create the network", e);
			return;
		}
		
		// add services to nodes
		putServicesOnNodes(nodes);
		
		// iterate over all expected results sizes, querying the nodes
		for (int expected : expectedResults) {
			double averageHops = queryNodesAndComputeAverage(expected);
			log.info("number of nodes: " + numberOfNodes + " expected number of results: "
					+ expected + " average hops: " + averageHops);
		}
		
	}
	
	/**
	 * Run random queries, counting the number of hops and returning the
	 * average.
	 * 
	 * @return the average number of hops for getting the specified number of
	 *         results
	 */
	private double queryNodesAndComputeAverage(int expectedResults) {
		int totalHops = 0;
		int queries = 0;
		
		/*
		 * Somehow count the number of hops. Maybe put a hop counter in
		 * C4SMsgRetrieve and create a special retrieve method which returns the
		 * # of hops and the requested services.
		 */
		
		return totalHops / (double) queries;
	}
	
	public static void main(String[] args) throws ServiceException {
		new RoutingPerformanceEvaluation().evaluate();
	}
	
}

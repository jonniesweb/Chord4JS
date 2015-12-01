package com.chord4js.evaluation;

import java.util.Set;

import com.chord4js.ServiceFactory;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.service.C4SRetrieveResponse;
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
		
		createNetwork(numberOfNodes, controller);
		
		// add services to nodes
		putServicesOnNodes(getNodes());
		
		// iterate over all expected results sizes, querying the nodes
		for (int expected : expectedResults) {
			double averageHops = queryNodesAndComputeAverage(getNodes(), expected);
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
	private double queryNodesAndComputeAverage(Set<Chord4SDriver> nodes, int requiredResults) {
		int totalHops = 0;
		int queries = 0;
		
		for (Chord4SDriver driver : nodes) {
			
			// get a random ServiceId in use
			ServiceFactory serviceFactory = serviceFactories.get(random.nextInt(serviceFactories
					.size()));
			ServiceId serviceId = serviceFactory.getServiceId();
			
			// update the totalHops and queries with hop information from the
			// C4SRetrieveResponse object
			C4SRetrieveResponse result = driver.lookupR(serviceId, requiredResults);
			totalHops += result.getNumberOfHops();
			queries++;
		}
		
		// calculate average
		return totalHops / (double) queries;
	}
	
	public static void main(String[] args) throws ServiceException {
		new RoutingPerformanceEvaluation().evaluate();
	}
	
}

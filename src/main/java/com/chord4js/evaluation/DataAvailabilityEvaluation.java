package com.chord4js.evaluation;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.chord4js.Service;
import com.chord4js.ServiceFactory;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.util.logging.Logger;

/**
 * Process:
 * 
 * <ol>
 * <li>Create network of nodes</li>
 * <li>set a percentage of those nodes to fail</li>
 * <li>the live nodes then issue queries to get n number of functionally
 * equivalent services where n is a random interval not specified that well from
 * section 6.1</li>
 * </ol>
 * 
 */
public class DataAvailabilityEvaluation extends AbstractEvaluation {
	
	private final Logger log = Logger.getLogger(DataAvailabilityEvaluation.class);
	
	private static final int[] testCrashPercentages = new int[] { 5, 10, 15, 20, 25, 30, 35, 40,
			45, 50, 55, 60 };
	
	/**
	 * Expect between 1 and this (inclusive) to be the number of services to
	 * find when querying the network.
	 */
	private static final int expectedServicesInterval = 20;
	private int crashPercent = 0;
	public DataAvailabilityEvaluation() throws Exception {
		
	}
	
	public void evaluate() {
	  // iterate over all crash percentages outside since we need to tear down the network after each %
	  for (int x : testCrashPercentages) {
	    crashPercent = x;
	    super.evaluate();
	  }
	}
	
	@Override
	public void start(int numberOfNodes, int maintenanceRounds) {
		
		
	  {
			EvaluationController controller = new EvaluationControllerImpl(random);
			
			log.info("creating nodes");
			createNetwork(numberOfNodes, controller, maintenanceRounds);
			
			log.info("inserting services on nodes");
			putServicesOnNodes(getNodes());
			
			log.info("crashing nodes");
			// crash a percentage of the nodes according to crashPercentage
			Set<Chord4SDriver> aliveNodes = controller.crashPercentageOfNodes(getNodes(), crashPercent);
			
			// rebuild the network
			runMaintenanceTasks(aliveNodes, maintenanceRounds);
			
			System.out.println("running random queries");
			// get nodes to query for services with random expected number of
			// results
			AggregateQueryResults results = runRandomQueries(aliveNodes);
			
			// display the results of the test
			log.info("number of nodes: " + numberOfNodes + " crash percentage: " + crashPercent
					+ " results: " + results);
			
		}
	}
	
	/**
	 * Iterate over all alive nodes to randomly query an unspecified number of
	 * times. Create an {@link AggregateQueryResults} and return it with the
	 * aggregate successes/failures of these queries.
	 * 
	 * Note: Each query expects a random number of entries back, or else the
	 * query fails.
	 * 
	 * Note: may want to use the async lookup call since it may be faster
	 * 
	 * Note: may want to run more queries than just the size of the aliveNodes
	 * 
	 * @param aliveNodes
	 * @return
	 */
	private AggregateQueryResults runRandomQueries(Set<Chord4SDriver> aliveNodes) {
		AggregateQueryResults queryResults = new AggregateQueryResults();
		
		// iterate over all the alive nodes
		for (Chord4SDriver driver : aliveNodes) {
			
			// get a random ServiceId in use
			ServiceFactory serviceFactory = serviceFactories.get(random.nextInt(serviceFactories
					.size()));
			ServiceId serviceId = serviceFactory.getServiceId();
			
			// lookup the result
			int requiredResults = 1 + random.nextInt(expectedServicesInterval);
			Set<Service> result = driver.lookup(serviceId, requiredResults);
			
			// mark query as a success if we receive enough results, fail
			// otherwise
			if (result != null && result.size() >= requiredResults) {
				queryResults.success();
				
			} else {
				queryResults.fail();
			}
		}
		
		return queryResults;
	}
	
	public static void main(String[] args) throws Exception  {
		AbstractEvaluation.configure();
		new DataAvailabilityEvaluation().evaluate();
	}
	
	/**
	 * A class to collect the results of successful and failed queries.
	 */
	public static class AggregateQueryResults {
		
		private AtomicInteger successfulQuery = new AtomicInteger();
		private AtomicInteger failedQuery = new AtomicInteger();
		private long startTime = System.currentTimeMillis();
		
		/**
		 * Mark that a query successfully completed.
		 */
		public void success() {
			synchronized (successfulQuery) {
				successfulQuery.incrementAndGet();
			}
		}
		
		/**
		 * Mark that a query failed.
		 */
		public void fail() {
			synchronized (failedQuery) {
				failedQuery.incrementAndGet();
			}
		}
		
		public int getSuccessfulQueries() {
			return successfulQuery.intValue();
		}
		
		public int getFailedQueries() {
			return failedQuery.intValue();
		}
		
		@Override
		public String toString() {
			return "results: " + " successful: " + successfulQuery + " failed: " + failedQuery + " time taken: " + (System.currentTimeMillis() - startTime);
		}
	}
}

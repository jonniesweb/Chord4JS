package com.chord4js.evaluation;

import java.util.Set;

import com.chord4js.Service;
import com.chord4js.ServiceId;

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
public class DataAvailabilityEvaluation {
	
	private int[] testCrashPercentages = new int[] { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60 };
	
	public DataAvailabilityEvaluation() {
		
	}
	
	public void start() {
		
		// iterate over all crash percentages
		for (int crashPercentage : testCrashPercentages) {
			
			EvaluationController controller = createTestEvaluationController();
			// Chord4SDriver driver = createTestDriver();
			
			// TODO: extract testing with multiple network sizes
			int numberOfNodes = EvaluationController.NODES_2_7;
			Set<Chord4SDriver> nodes = controller
					.createChord4SNetwork(numberOfNodes);
			
			// crash the crashPercentage of nodes
			Set<Chord4SDriver> aliveNodes = controller.crashPercentageOfNodes(nodes,
					crashPercentage);
			
			// get nodes to query for services with random expected number of
			// results
			AggregateQueryResults results = runRandomQueries(aliveNodes);
			
			// display the results of the test
			System.out.println("number of nodes: " + numberOfNodes + " crash percentage: " + crashPercentage + " results: " + results);
			
		}
	}
	
	private AggregateQueryResults runRandomQueries(Set<Chord4SDriver> aliveNodes) {
		// TODO Auto-generated method stub
		return null;
		
	}
	
	private EvaluationController createTestEvaluationController() {
		return new EvaluationController() {
			
			@Override
			public Set<Chord4SDriver> createChord4SNetwork(int numberOfNodes) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<Chord4SDriver> crashPercentageOfNodes(Set<Chord4SDriver> chord4sDrivers,
					int percentage) {
				return null;
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	private Chord4SDriver createTestDriver() {
		return new Chord4SDriver() {
			
			@Override
			public void put(Service service) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Set<Service> lookup(ServiceId serviceId) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void leave() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void join(String bootstrapURL) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void crash() {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	public static void main(String[] args) {
		new DataAvailabilityEvaluation().start();
	}
	
	/**
	 * A class to collect the results of successful and failed queries.
	 */
	public class AggregateQueryResults {
		
		private Integer successfulQuery = 0;
		private Integer failedQuery = 0;
		
		/**
		 * Mark that a query successfully completed.
		 */
		public void success() {
			synchronized (successfulQuery) {
				successfulQuery++;
			}
		}
		
		/**
		 * Mark that a query failed.
		 */
		public void fail() {
			synchronized (failedQuery) {
				failedQuery++;
			}
		}
		
		public Integer getSuccessfulQueries() {
			return successfulQuery;
		}
		
		public Integer getFailedQueries() {
			return failedQuery;
		}
		
		@Override
		public String toString() {
			return "results: " + " successful: " + successfulQuery + " failed: " + failedQuery;
		}
	}
}

package com.chord4js.evaluation;

import java.util.Set;

/**
 * Main interface for defining behaviour used to evaluate Chrord4JS.
 * 
 * <h2>Evaluation types:</h2>
 * 
 * <h3>Data availability - randomly failing nodes</h3>
 * 
 * <h3>Routing performance - number of hops needed for a query</h3>
 */
public interface EvaluationController {
	
	// network sizes for evaluations
	public static final int NODES_2_7 = 2 ^ 7;
	public static final int NODES_2_11 = 2 ^ 11;
	public static final int NODES_2_15 = 2 ^ 15;
	
	/**
	 * Create a network of Chord4S instances containing
	 * <code>numberOfNodes</code> nodes.
	 * 
	 * @param numberOfNodes
	 */
	Set<Chord4SDriver> createChord4SNetwork(int numberOfNodes);
	
	/**
	 * Crash a percentage of the nodes
	 * 
	 * @param chord4sDrivers
	 * @param percentage
	 */
	void crashPercentageOfNodes(Set<Chord4SDriver> chord4sDrivers, int percentage);
	
}

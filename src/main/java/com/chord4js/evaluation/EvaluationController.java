package com.chord4js.evaluation;

import java.util.Set;

/**
 * Main interface for defining behaviour used to evaluate Chrord4JS.
 * 
 * <h2>Evaluation types:</h2>
 * 
 * <li>Data availability - randomly failing nodes</li>
 * 
 * <li>Routing performance - number of hops needed for a query</li>
 */
public interface EvaluationController {
	
	/**
	 * Create a cluster of Chord4S instances containing
	 * <code>numberOfNodes</code> nodes in its network.
	 * 
	 * @param numberOfNodes
	 */
	Set<Chord4SDriver> startChord4SCluster(int numberOfNodes);
	
	
}

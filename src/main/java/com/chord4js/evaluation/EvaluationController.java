package com.chord4js.evaluation;

import java.util.Set;

import de.uniba.wiai.lspi.chord.service.ServiceException;

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
	
	/**
	 * Create a network of Chord4S instances containing
	 * <code>numberOfNodes</code> nodes.
	 * 
	 * @param numberOfNodes
	 * @throws ServiceException
	 */
	Set<Chord4SDriver> createChord4SNetwork(int numberOfNodes) throws ServiceException;
	
	/**
	 * Immediately crash a number of nodes, randomly from the set.
	 * 
	 * @param chord4sDrivers
	 * @param numberOfNodes
	 *            the number of nodes to immediately crash
	 * @return the nodes that are still alive
	 */
	Set<Chord4SDriver> crashNodes(Set<Chord4SDriver> chord4sDrivers, int numberOfNodes);
	
}

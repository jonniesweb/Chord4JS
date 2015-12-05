package com.chord4js.evaluation;

import java.util.Set;

import com.chord4js.Service;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.C4SRetrieveResponse;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * An interface for specifying the behaviour of what a Chord4S node offers. Eg.
 * put, lookup.
 *
 */
public interface Chord4SDriver {
	
	/**
	 * Put the given data into the Chord4S network, associating it with
	 * <code>identifier</code> as the lookup value.
	 * 
	 * @param service
	 */
	void put(Service service);
	
	/**
	 * Retrieve one Service if possible on the Chord4S network with the given
	 * identifier.
	 * 
	 * @param serviceId
	 * @return
	 */
	Set<Service> lookup(ServiceId serviceId);
	
	/**
	 * Retrieve <code>amount</code> number of Services if possible on the
	 * Chord4S network with the given identifier.
	 * 
	 * @param serviceId
	 * @param amount
	 * @return
	 */
	Set<Service> lookup(ServiceId serviceId, int amount);
	
	/**
	 * Same as {@link #lookup(ServiceId)}, but gets more info about the query.
	 * 
	 * @param serviceId
	 * @return
	 */
	C4SRetrieveResponse lookupR(ServiceId serviceId);
	
	/**
	 * Same as {@link #lookup(ServiceId, int)}, but gets more info about the
	 * query.
	 * 
	 * @param serviceId
	 * @param requiredResults
	 * @return
	 */
	C4SRetrieveResponse lookupR(ServiceId serviceId, int requiredResults);
	
	/**
	 * Join the network by connecting to an existing chord node at boostrapURL
	 * 
	 * @param bootstrapURL
	 * @throws ServiceException
	 */
	void join(URL bootstrapURL) throws ServiceException;
	
	/**
	 * Tell the node to leave the network gracefully.
	 */
	void leave();
	
	/**
	 * Crash this node. Don't leave the network gracefully.
	 */
	void crash();
	
	/**
	 * Run maintenance tasks manually.
	 */
	void runTasks();
	
}

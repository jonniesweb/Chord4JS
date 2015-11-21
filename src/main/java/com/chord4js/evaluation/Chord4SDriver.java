package com.chord4js.evaluation;

import java.util.Set;

import com.chord4js.Service;
import com.chord4js.ServiceId;

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
	 * Retrieve the data on the Chord4S network with the given identifier.
	 * 
	 * @param serviceId
	 * @return
	 */
	Set<Service> lookup(ServiceId serviceId);
}

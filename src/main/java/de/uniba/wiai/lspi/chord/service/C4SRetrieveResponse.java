package de.uniba.wiai.lspi.chord.service;

import java.io.Serializable;
import java.util.Set;

import com.chord4js.Service;

import de.uniba.wiai.lspi.chord.service.impl.NodeImpl;

/**
 * A response returned when retrieving data from the network. Contains the
 * {@link #services}, as well as information about the query, such as the number
 * of hops.
 */
public class C4SRetrieveResponse implements Serializable {
	
	private static final long serialVersionUID = -2327226889045018683L;
	
	public final Set<Service> services;
	
	/**
	 * A stupid way to automatically count the number of hops. Might not work.
	 * {@link NodeImpl} creates a new instance of this object and copies the
	 * data from the previous node into this node via the
	 * {@link #add(C4SRetrieveResponse)} method. Since <code>hops = 1</code>, we
	 * have free counting of hops.
	 * 
	 * Note: this may not work
	 */
	private int hops = 1;
	
	public C4SRetrieveResponse(Set<Service> services) {
		this.services = services;
	}
	
	public void incrementHop()       { ++hops; }
	public void incrementHop(int x)  { hops += x; }
	
	public int getNumberOfHops() {
		return hops;
	}
	
	/**
	 * Include the data from another {@link C4SRetrieveResponse} object into
	 * this one. Adds the services to this services set and adds up the hops
	 * 
	 * @param retrieveResponse
	 */
	public void add(C4SRetrieveResponse retrieveResponse) {
		services.addAll(retrieveResponse.services);
		hops += retrieveResponse.hops;
	}
	
	public void add(Set<Service> s) { services.addAll(s); }
	
	/**
	 * Get the number of services this response object contains
	 * 
	 * @return
	 */
	public int size() {
		return services.size();
	}
}

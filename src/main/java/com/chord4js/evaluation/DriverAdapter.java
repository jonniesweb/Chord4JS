package com.chord4js.evaluation;

import java.util.Set;

import com.chord4js.QoSConstraints;
import com.chord4js.Service;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.C4SMsgRetrieve;
import de.uniba.wiai.lspi.chord.service.C4SRetrieveResponse;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * A {@link Chord4SDriver} that uses the Adapter pattern to control
 * {@link ChordImpl}.
 */
public class DriverAdapter implements Chord4SDriver {
	
	private final ChordImpl chordImpl;
	
	public DriverAdapter(ChordImpl chordImpl) {
		this.chordImpl = chordImpl;
	}
	
	@Override
	public void put(Service service) {
		chordImpl.insert(service);
	}
	
	@Override
	public Set<Service> lookup(ServiceId serviceId) {
		return lookupR(serviceId).services;
	}
	
	@Override
	public Set<Service> lookup(ServiceId serviceId, int requiredResults) {
		return lookupR(serviceId, requiredResults).services;
	}
	
	@Override
	public C4SRetrieveResponse lookupR(ServiceId serviceId) {
		return chordImpl.retrieveR(new C4SMsgRetrieve(serviceId, null, 1));
	}

	@Override
	public C4SRetrieveResponse lookupR(ServiceId serviceId, int requiredResults) {
		return chordImpl.retrieveR(new C4SMsgRetrieve(serviceId, new QoSConstraints(), requiredResults));
	}

	@Override
	public void join(URL bootstrapURL) throws ServiceException {
		chordImpl.join(bootstrapURL);
	}
	
	@Override
	public void leave() {
		chordImpl.leave();
	}
	
	@Override
	public void crash() {
		chordImpl.crash();
	}
	
}

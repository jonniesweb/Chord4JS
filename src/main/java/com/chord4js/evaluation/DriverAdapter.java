package com.chord4js.evaluation;

import java.util.Set;

import com.chord4js.Service;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.C4SMsgRetrieve;
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
		return chordImpl.retrieve(new C4SMsgRetrieve(serviceId, null, 1));
	}
	
	@Override
	public Set<Service> lookup(ServiceId serviceId, int amount) {
		return chordImpl.retrieve(new C4SMsgRetrieve(serviceId, null, amount));
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

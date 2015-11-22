/***************************************************************************
 *                                                                         *
 *                      ChordRetrievalFutureImpl.java                      *
 *                            -------------------                          *
 *   date                 : 15.10.2005                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *   			    		karsten.loesing@uni-bamberg.de                 *
 *                                                                         *
 *                                                                         *
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   A copy of the license can be found in the license.txt file supplied   *
 *   with this software or at: http://www.gnu.org/copyleft/gpl.html        *
 *                                                                         *
 ***************************************************************************/
package de.uniba.wiai.lspi.chord.service.impl;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Executor;

import com.chord4js.QoSConstraints;
import com.chord4js.Service;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.service.C4SMsgRetrieve;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ChordRetrievalFuture;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.ServiceException;

/**
 * Implementation of {@link ChordRetrievalFuture}.
 * 
 * @author sven
 * @version 1.0.5
 * 
 */
class ChordRetrievalFutureImpl extends ChordFutureImpl implements
		ChordRetrievalFuture {

	/**
	 * The result of the retrieval request associated with this.
	 */
	private Set<Service> result;

	/**
	 * The chord instance used for the operation that is associated with this. 
	 */
	private Chord chord = null;

	/**
	 * The key to retrieve the associated entries for. 
	 */
	private C4SMsgRetrieve msg;

	/**
	 * 
	 * @param c
	 * @param k
	 * @param amount 
	 * @param qos 
	 */
	private ChordRetrievalFutureImpl(Chord c, C4SMsgRetrieve x) {
		super();
		this.chord = c;
		msg = x;
	}

	/**
	 * 
	 * @param r
	 */
	final void setResult(Set<Service> r) {
		this.result = r;
	}

	/**
	 * @see ChordRetrievalFuture
	 */
	public final Set<Service> getResult() throws ServiceException,
			InterruptedException {
		synchronized (this) {
			while (!this.isDone()) {
				this.wait();
			}
		}
		Throwable t = this.getThrowable();
		if (t != null) {
			throw new ServiceException(t.getMessage(), t);
		}
		return this.result;
	}

	/**
	 * 
	 * @return Runnable that performs the retrieve operation. 
	 */
	private Runnable getTask() {
		return new RetrievalTask(this.chord, msg);
	}

	/**
	 * Factory method to create an instance of this class. This method also
	 * prepares execution of the retrieval with help of the provided
	 * {@link Executor} <code>exec</code>.
	 * 
	 * @param exec
	 *            The executor that should asynchronously execute the retrieval
	 *            of entries with key <code>k</code>.
	 * @param c
	 *            The {@link Chord} instance to be used for retrieval.
	 * @param k
	 *            The {@link Key} for which the entries should be retrieved.
	 * @param amount 
	 * @param c2 
	 * @return An instance of this.
	 */
	final static ChordRetrievalFutureImpl create(Executor exec, Chord c, C4SMsgRetrieve x) {
		if (c == null) {
			throw new IllegalArgumentException(
					"ChordRetrievalFuture: chord instance must not be null!");
		}
		if (x == null) {
			throw new IllegalArgumentException(
					"ChordRetrievalFuture: key must not be null!");
		}

		ChordRetrievalFutureImpl future = new ChordRetrievalFutureImpl(c, x);
		exec.execute(future.getTask());
		return future;
	}

	/**
	 * Runnable to execute the retrieval of entries associated with key from
	 * chord.
	 * 
	 * @author sven
	 * @version 1.0
	 */
	private class RetrievalTask implements Runnable {

		/**
		 * The chord instance used for the operation that is associated with this. 
		 */
		private Chord chord = null;

		private C4SMsgRetrieve msg;
		
		/**
		 * @param chord
		 * @param key
		 */
		private RetrievalTask(Chord chord, C4SMsgRetrieve x) {
			this.chord = chord; 
			msg = x; 
		}

		public void run() {
			try {
				setResult(chord.retrieve(msg));
			} catch (Throwable t) {
				setThrowable(t);
			}
			setIsDone();
		}
	}

}

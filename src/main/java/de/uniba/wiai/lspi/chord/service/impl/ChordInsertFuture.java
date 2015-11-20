/***************************************************************************
 *                                                                         *
 *                          ChordInsertFuture.java                         *
 *                            -------------------                          *
 *   date                 : 15.10.2005                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *   			   			karsten.loesing@uni-bamberg.de                 *
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
import java.util.concurrent.Executor;

import com.chord4js.Service;

import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ChordFuture;
import de.uniba.wiai.lspi.chord.service.Key;

/**
 * Implementation of {@link ChordFuture} for
 * {@link ChordImpl#insertAsync(Key, Serializable)}.
 * 
 * @author sven
 * @version 1.0.5
 * 
 */
class ChordInsertFuture extends ChordFutureImpl {

	/**
	 * The instance of chord used for the invocation represented by this. 
	 */
	private Chord chord;

	
	/**
	 * The key used for the insertion. 
	 */
	private Key key;

	/**
	 * The entry to insert. 
	 */
	private Service svc;

	/**
	 * 
	 * @param c The instance of chord used for the invocation represented by this. 
	 * @param entry The entry to insert.
	 */
	private ChordInsertFuture(Chord c, Service s) {
		this.chord = c;
		this.svc = s;
	}

	/**
	 * Factory method to create an instance of this class. This method also
	 * prepares execution of the insertion with help of the provided
	 * {@link Executor} <code>exec</code>.
	 * 
	 * @param exec
	 *            The executor that should asynchronously execute the insertion
	 *            of <code>entry</code> with key <code>k</code>.
	 * 
	 * @param c
	 *            The instance of {@link Chord} that should be used to insert
	 *            <code>entry</code>.
	 * @param k
	 *            The {@link Key} for <code>entry</code>.
	 * @param entry
	 *            The entry to be inserted.
	 * @return Instance of this class.
	 */
	final static ChordInsertFuture create(Executor exec, Chord c, Service svc) {

		if (c == null) {
			throw new IllegalArgumentException(
					"ChordInsertFuture: chord instance must not be null!");
		}
		if (svc == null) {
			throw new IllegalArgumentException(
					"ChordInsertFuture: entry must not be null!");
		}

		ChordInsertFuture f = new ChordInsertFuture(c, svc);
		exec.execute(f.getTask());
		return f;
	}

	/**
	 * 
	 * @return A Runnable that executes the operation associated with this. 
	 */
	private final Runnable getTask() {
		return new InsertTask(this.chord, svc);
	}

	/**
	 * Runnable that executes the insertion.
	 * 
	 * @author sven
	 * @version 1.0
	 */
	private class InsertTask implements Runnable {

		/**
		 * The instance of chord used for the invocation represented by this. 
		 */
		private Chord chord;

		
		/**
		 * The key used for the insertion. 
		 */
		private Service svc;

		/**
		 * Private constructor. 
		 * @param chord 
		 * @param key 
		 * @param entry 
		 */
		InsertTask(Chord chord, Service s){
			this.chord = chord; 
			this.svc = s; 
		}
		
		public void run() {
			try {
				this.chord.insert(svc);
			} catch (Throwable t) {
				setThrowable(t);
			}
			setIsDone();
		}
	}

}

/***************************************************************************
 *                                                                         *
 *                               Entries.java                              *
 *                            -------------------                          *
 *   date                 : 28.02.2005                                     *
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.chord4js.ProviderId;
import com.chord4js.Service;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.C4SMsgRetrieve;
import de.uniba.wiai.lspi.util.logging.Logger;

/**
 * Stores entries for the local node in a local hash table and provides methods
 * for accessing them. It IS allowed, that multiple objects of type
 * {@link Entry} with same {@link ID} are stored!
 * 
 * @author Karsten Loesing, Sven Kaffille
 * @version 1.0.5
 * 
 */

/*
 * 23.12.2006. Fixed synchronization. The Map<ID, Set<Entry>> entries must be
 * synchronized with a synchronized statement, when executing several methods
 * that depend on each other. This would also apply to the internal Set<Entry>
 * if it were not only used in the same synchronized statements for entries,
 * which than functions as a synchronization point. It must also be locked by a
 * synchronized statement, when iterating over it. TODO: What about fairness?
 * sven
 */
final class Entries {

	/**
	 * Object logger.
	 */
	private final static Logger logger = Logger.getLogger(Entries.class);

	private final static boolean debugEnabled = logger
			.isEnabledFor(Logger.LogLevel.DEBUG);

	/**
	 * Local hash table for entries. Is synchronized, st. methods do not have to
	 * be synchronized.
	 */
	// \fixme replace with nested map of strings instead.
	// Java doesn't have type-aliases nor dependent types to ensure this matches up with #of semantic parts
	private Map<ProviderId, Service> entries = null;

	/**
	 * Creates an empty repository for entries.
	 */
	Entries(){ 
		this.entries = Collections
				.synchronizedMap(new TreeMap<ProviderId, Service>());
	}

	/**
	 * Stores a set of entries to the local hash table.
	 * 
	 * @param entriesToAdd
	 *            Set of entries to add to the repository.
	 * @throws NullPointerException
	 *             If set reference is <code>null</code>.
	 */
	final void addAll(Set<Service> entriesToAdd) {

		if (entriesToAdd == null) {
			NullPointerException e = new NullPointerException(
					"Set of entries to be added to the local hash table may "
							+ "not be null!");
			Entries.logger.error("Null pointer", e);
			throw e;
		}

		for (Service nextEntry : entriesToAdd) {
			this.add(nextEntry);
		}

		if (debugEnabled) {
			Entries.logger.debug("Set of entries of length "
					+ entriesToAdd.size() + " was added.");
		}
	}

	/**
	 * Stores one entry to the local hash table.
	 * 
	 * @param entryToAdd
	 *            Entry to add to the repository.
	 * @throws NullPointerException
	 *             If entry to add is <code>null</code>.
	 */
	final void add(Service svc) {
		
		if (svc == null) {
			NullPointerException e = new NullPointerException(
					"Entry to add may not be null!");
			Entries.logger.error("Null pointer", e);
			throw e;
		}

		synchronized (this.entries) {
			entries.put(svc.getProviderId(), svc);
		}
		
		if (debugEnabled) {
			Entries.logger.debug("Entry was added: " + svc);
		}
	}

	/**
	 * Removes the given entry from the local hash table.
	 * 
	 * @param entryToRemove
	 *            Entry to remove from the hash table.
	 * @throws NullPointerException
	 *             If entry to remove is <code>null</code>.
	 */
	final void remove(ProviderId svcId) {
		
		if (svcId == null) {
			NullPointerException e = new NullPointerException(
					"Entry to remove may not be null!");
			Entries.logger.error("Null pointer", e);
			throw e;
		}

		synchronized (this.entries) {
			this.entries.remove(svcId);
		}
		if (debugEnabled) {
			Entries.logger.debug("Entry was removed: " + svcId);
		}
	}

	/**
	 * Returns a set of entries matching the given ID. If no entries match the
	 * given ID, an empty set is returned.
	 * 
	 * @param id
	 *            ID of entries to be returned.
	 * @throws NullPointerException
	 *             If given ID is <code>null</code>.
	 * @return Set of matching entries. Empty Set if no matching entries are
	 *         available.
	 */
	final Set<Service> getEntries(C4SMsgRetrieve msg) {

		if (msg == null) {
			NullPointerException e = new NullPointerException(
					"ID to find entries for may not be null!");
			Entries.logger.error("Null pointer", e);
			throw e;
		}
		
		Set<Service> svcs = new HashSet<>();
		if (msg.amount > 0) {
  		for (Service s : getEntriesInInterval(msg.span.bgn, msg.span.endIncl)) {
  		  if (!msg.constraints.satisfies(s)) continue;
  		  svcs.add(s);
  		  
  		  if (msg.amount >= svcs.size()) break;
  		}
		}
		
		return svcs;
	}

	/**
	 * Returns all entries in interval, excluding lower bound, but including
	 * upper bound
	 * 
	 * @param fromID
	 *            Lower bound of IDs; entries matching this ID are NOT included
	 *            in result.
	 * @param toID
	 *            Upper bound of IDs; entries matching this ID ARE included in
	 *            result.
	 * @throws NullPointerException
	 *             If either or both of the given ID references have value
	 *             <code>null</code>.
	 * @return Set of matching entries.
	 */
	final Set<Service> getEntriesInInterval(ID fromID, ID toID) {

		if (fromID == null || toID == null) {
			NullPointerException e = new NullPointerException(
					"Neither of the given IDs may have value null!");
			Entries.logger.error("Null pointer", e);
			throw e;
		}

		Set<Service> result = new HashSet<>();
		synchronized (this.entries) {
		  // (or so it claims)
			for (ProviderId nextID : entries.keySet()) {
			  final ID id = new ID(nextID);
   			// add entries in [fromId, toId]
			  // isInInterval apparently only says [from, to)
			  // so explicitly check for equal on last
				if (id.isInInterval(fromID, toID) || id.equals(toID))
				  result.add(entries.get(nextID));
			}
		}
		
		return result;
	}

	/**
	 * Removes the given entries from the local hash table.
	 * 
	 * @param toRemove
	 *            Set of entries to remove from local hash table.
	 * @throws NullPointerException
	 *             If the given set of entries is <code>null</code>.
	 */
	final void removeAll(Set<ProviderId> toRemove) {

		if (toRemove == null) {
			NullPointerException e = new NullPointerException(
					"Set of entries may not have value null!");
			Entries.logger.error("Null pointer", e);
			throw e;
		}

		for (ProviderId nextEntry : toRemove) {
			this.remove(nextEntry);
		}

		if (debugEnabled) {
			Entries.logger.debug("Set of entries of length " + toRemove.size()
					+ " was removed.");
		}
	}

	/**
	 * Returns an unmodifiable map of all stored entries.
	 * 
	 * @return Unmodifiable map of all stored entries.
	 */
	final Map<ProviderId, Service> getEntries() {
		return Collections.unmodifiableMap(this.entries);
	}

	/**
	 * Returns the number of stored entries.
	 * 
	 * @return Number of stored entries.
	 */
	final int getNumberOfStoredEntries() {
		return this.entries.size();
	}

	/**
	 * Returns a formatted string of all entries stored in the local hash table.
	 * 
	 * @return String representation of all stored entries.
	 */
	public final String toString() {
		StringBuilder result = new StringBuilder("Entries:\n");
		for (Map.Entry<ProviderId, Service> entry : this.entries.entrySet()) {
			result.append("  key = " + entry.getKey().toString()
					+ ", value = " + entry.getValue() + "\n");
		}
		return result.toString();
	}
}
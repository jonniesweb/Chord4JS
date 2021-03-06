/***************************************************************************
 *                                                                         *
 *                             RemoteNode.java                             *
 *                            -------------------                          *
 *   date                 : 22.02.2008, 14:11:20                           *
 *   copyright            : (C) 2008 Distributed and                       *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
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
package de.uniba.wiai.lspi.chord.com.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import com.chord4js.PairS;
import com.chord4js.ProviderId;
import com.chord4js.Service;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.C4SMsgRetrieve;
import de.uniba.wiai.lspi.chord.service.C4SRetrieveResponse;

/**
 * 
 * @author sven
 * @version 1.0.5
 */
public interface RemoteNode extends Remote {

	/**
	 * 
	 * @param key
	 * @return
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public PairS<RemoteNodeInfo, Integer> findSuccessor(ID key) throws CommunicationException,
			RemoteException;

	/**
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public ID getNodeID() throws RemoteException;

	/**
	 * 
	 * @param entryToInsert
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public void insertEntry(Service entryToInsert) throws CommunicationException,
			RemoteException;

	/**
	 * 
	 * @param entries
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public void insertReplicas(Set<Service> entries)
			throws CommunicationException, RemoteException;

	/**
	 * 
	 * @param predecessor
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public void leavesNetwork(RemoteNodeInfo predecessor) throws CommunicationException,
			RemoteException;

	/**
	 * 
	 * @param potentialPredecessor
	 * @return
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public List<RemoteNodeInfo> notify(RemoteNodeInfo potentialPredecessor)
			throws CommunicationException, RemoteException;

	/**
	 * 
	 * @param potentialPredecessor
	 * @return
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public RemoteRefsAndEntries notifyAndCopyEntries(RemoteNodeInfo potentialPredecessor)
			throws CommunicationException, RemoteException;

	/**
	 * 
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public void ping() throws CommunicationException, RemoteException;

	/**
	 * 
	 * @param entryToRemove
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public void removeEntry(ProviderId entryToRemove) throws CommunicationException,
			RemoteException;

	/**
	 * 
	 * @param sendingNode
	 * @param replicasToRemove
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public void removeReplicas(ID sendingNode, Set<ProviderId> replicasToRemove)
			throws CommunicationException, RemoteException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws CommunicationException
	 * @throws RemoteException
	 */
	public C4SRetrieveResponse retrieveEntries(C4SMsgRetrieve msg) throws CommunicationException,
			RemoteException;
	
}

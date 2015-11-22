/***************************************************************************
 *                                                                         *
 *                                 ID.java                                 *
 *                            -------------------                          *
 *   date                 : 16.08.2004                                     *
 *   copyright            : (C) 2004-2008 Distributed and                  *
 *                              Mobile Systems Group                       *
 *                              Lehrstuhl fuer Praktische Informatik       *
 *                              Universitaet Bamberg                       *
 *                              http://www.uni-bamberg.de/pi/              *
 *   email                : sven.kaffille@uni-bamberg.de                   *
 *                          karsten.loesing@uni-bamberg.de                 *
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
package de.uniba.wiai.lspi.chord.data;

import java.io.Serializable;
import java.util.BitSet;

import com.chord4js.ProviderId;
import com.chord4js.ServiceId;

/**
 * Identifier for nodes and user-defined objects. New instances of this class
 * are created either when a node joins the network, or by the local node
 * inserting a user-defined object.
 * 
 * Once created, an ID instance is unmodifiable.
 * 
 * IDs of same length can be compared as this class implements
 * java.lang.Comparable. IDs of different length cannot be compared.
 * 
 * @author Sven Kaffille, Karsten Loesing
 * @version 1.0.5
 */
public final class ID implements Comparable<ID>, Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6860626236168125168L;
	
	// Expected total network size. This is important for calc'ing the ideal length
  // for the provider bits.
  public static final int kNetworkSizeExpected = 100000;
  // Maximum number of providers for a single semantic ident
  public static final int kProviderLimit       = 1024;
  public static final int kSemanticPartBitLen  = 6;
  public static final int kSemanticTotalBitLen = kSemanticPartBitLen * ServiceId.kPartsSemantic;
  public static final int kProviderBitLen      = ProviderMinimumBitLen();
  public static final int kTotalBitLen         = kSemanticTotalBitLen + kProviderBitLen;
  
  private static final ID idMin; // All 0 ID
  private static final ID idMax; // All 1 ID
  
  // From section 4.2 in paper:
  //   providerBitLen >= log2((numProviderMax - 1) / nodeNum * 2^semanticBitLen)
  // Which rewrites to:
  //   providerBitLen >= log2(numProviderMax - 1) + log2(2^semanticBitLen / nodeNum)
  //   providerBitLen >= log2(numProviderMax - 1) + semanticBitLen - log2(nodeNum)
  private static int ProviderMinimumBitLen() {
    final double lenMin  = log2(kProviderLimit - 1  )
                         - log2(kNetworkSizeExpected)
                         + (double)kSemanticTotalBitLen;
    return (int)Math.ceil(lenMin);
  }

  // Math has no log2.
  private static double log2(double x) { return Math.log10(x) / Math.log10(2); }

  static {
    idMin = new ID(new BitSet(kTotalBitLen));
    idMax = new ID(new BitSet(kTotalBitLen));
    idMax.id.set(0, idMax.id.length());
  }

	/**
	 * The bits representing the id. Big endian. (provider bits are low)
	 */
	public final BitSet id;

	/**
	 * Creates a new ID consisting of the given byte[] array. The ID is assumed
	 * to have (ID.length * 8) bits. It must have leading zeros if its value has
	 * fewer digits than its maximum length.
	 * 
	 * @param id1
	 *            Byte array containing the ID.
	 */

	public ID(ProviderId x) {
    id = new BitSet(kTotalBitLen);
    throw new Exception("Implement reduction from full hash to lower N bits for each part (highest entropy)");
  }
	
	public static class IdSpan implements Cloneable {
	  public ID bgn, endIncl;
	  protected IdSpan(BitSet a, BitSet b) { bgn = new ID(a); endIncl = new ID(b); }
	  
	  @Override
	  public IdSpan clone() { return new IdSpan(bgn.id, endIncl.id); }
	}
	
	public static IdSpan ServiceId(ServiceId svcId) {
	  throw new Exception("Implement wildcard span calc)");
	  int numParts;
	  for (numParts = 0; numParts < svcId.getHashedParts().length; ++numParts)
	    if (svcId.getHashedParts()[numParts] == null) break;
	  
	  
    BitSet a = new BitSet(kTotalBitLen);
    BitSet b = new BitSet(kTotalBitLen);
    return new IdSpan(a , b);
	}
	
	public static ID NodeId(final byte[] blob) {
	  final BitSet id = new BitSet(kTotalBitLen);
	  throw new Exception("Implement similar reduction here");
	  return new ID(id);
  }

	private ID(BitSet x) { id = x; }
	
	@Override
	public ID clone() { return new ID((BitSet)id.clone()); }
	
	public final String toString() { return toHexString(); }

	/**
	 * Returns a string of the hexadecimal representation of the first
	 * <code>n</code> bytes of this ID, including leading zeros.
	 * 
	 * @param numberOfBytes
	 * 
	 * @return Hex string of ID
	 */
	public final String toHexString(int numberOfBytes) {

		// number of displayed bytes must be in interval [1, this.id.length]
	  final byte[] ary = this.id.toByteArray();
		int displayBytes = Math.max(1, Math.min(numberOfBytes, ary.length));

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < displayBytes; i++) {

			String block = Integer.toHexString(ary[i] & 0xff).toUpperCase();

			// add leading zero to block, if necessary
			if (block.length() < 2) {
				block = "0" + block;
			}

			result.append(block + " ");
		}
		return result.toString();
	}

	/**
	 * Returns a string of the hexadecimal representation of this ID, including
	 * leading zeros.
	 * 
	 * @return Hex string of ID
	 */
	public final String toHexString() {
		return this.toHexString(Integer.MAX_VALUE);
	}

	/**
	 * Returns length of this ID measured in bits. ID length is determined by
	 * the length of the stored byte[] array, i.e. leading zeros have to be
	 * stored in the array.
	 * 
	 * @return Length of this ID measured in bits.
	 */
	public final int getLength() {
		return this.id.length();
	}

	/**
	 * Calculates the ID which is 2^powerOfTwo bits greater than the current ID
	 * modulo the maximum ID and returns it.
	 * 
	 * @param powerOfTwo
	 *            Power of two which is added to the current ID. Must be a value
	 *            of the interval [0, length-1], including both extremes.
	 * @return ID which is 2^powerOfTwo bits greater than the current ID modulo
	 *         the maximum ID.
	 */
	public final ID addPowerOfTwo(final int powerOfTwo) {
	  if ((powerOfTwo < 0) || (powerOfTwo >= id.length())) {
      throw new IllegalArgumentException(
          "The power of two is out of range! It must be in the interval [0, length-1]");
    }
	  
	  ID copy = clone();
	  
    // perform hand-rolled binary addition. recall that the bitset is defined as big-endian.
	  // i.e. (idx 0 is highest bit)
	  for (int i = powerOfTwo; i < id.length(); ++i) {
	    final int idx = id.length() - i - 1;
	    copy.id.flip(idx);
	    
	    // 'twas zero previous. We're done.
	    if (copy.id.get(idx)) break;
	  }
	  
	  return copy;
	}

	/**
	 * Checks the given object for equality with this {@link ID}.
	 * 
	 * @param equalsTo
	 *            Object to check equality with this {@link ID}.
	 */
	public final boolean equals(Object equalsTo) {

		// check if given object has correct type
		if (equalsTo == null || !(equalsTo instanceof ID)) {
			return false;
		}

		// check if both byte arrays are equal by using the compareTo method
		return (this.compareTo((ID) equalsTo) == 0);

	}

	/**
	 * Compare current ID with the given object. If either the object is not a
	 * ID or both IDs' lengths do not match, a ClassCastException is thrown.
	 * Otherwise both IDs are compared byte by byte.
	 * 
	 * @return -1, 0, or 1, if this ID is smaller, same size, or greater than
	 *         the given object, respectively.
	 */
	public final int compareTo(ID otherKey) throws ClassCastException {
	  
		if (id.length() != otherKey.id.length()) {
			throw new ClassCastException(
					"Only ID objects with same length can be "
							+ "compared! This ID is " + id.length()
							+ " bits long while the other ID is "
							+ otherKey.getLength() + " bits long.");
		}

		for (int i = 0; i < id.length(); ++i) {
		  if (id.get(i) != otherKey.id.get(i))
		    return id.get(i) ? 1 : -1;
		}
		
		return 0;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public final int hashCode() {
		return 19 + id.hashCode() * 13;
	}

	/**
	 * Checks if this ID is in the interval determined by the two given IDs.
	 * Neither of the boundary IDs is included in the interval. If both IDs
	 * match, the interval is assumed to span the whole ID ring.
	 * 
	 * @param fromID
	 *            Lower bound of interval.
	 * @param toID
	 *            Upper bound of interval.
	 * @return If this key is included in the given interval.
	 */
	public final boolean isInInterval(ID fromID, ID toID) {

		// both interval bounds are equal -> calculate out of equals
		if (fromID.equals(toID)) {
			// every ID is contained in the interval except of the two bounds
			return (!this.equals(fromID));
		}

		// interval does not cross zero -> compare with both bounds
		if (fromID.compareTo(toID) < 0) {
			return (compareTo(fromID) > 0) &&
			       (compareTo(toID  ) < 0);
		}
		
		// check both splitted intervals
		// \fixme THIS LOOKS FISHY. REVIEW 
    // first interval: (fromID, maxID]
		final boolean a = (!fromID.equals(idMax) ) &&
		                  (compareTo(fromID) >  0) &&
		                  (compareTo(idMax ) <= 0);
	// second interval: [minID, toID)
		final boolean b = (!idMin.equals(toID)   ) &&
		                  (compareTo(idMin ) >= 0) &&
		                  (compareTo(toID  ) <  0);
		return a || b;
	}
	
	public final boolean isInIntervalInclusive(ID fromID, ID toID) {
	  if (isInInterval(fromID, toID)) return true;
	  return equals(fromID) || equals(toID);
	}

}
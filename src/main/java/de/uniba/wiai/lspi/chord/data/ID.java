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
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;

import com.chord4js.ProviderId;
import com.chord4js.ServiceId;
import com.chord4js.Unit;

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

	private static BitSet hashString(final String s) {
    return BitSet.valueOf(DigestUtils.sha1(s));
  }
	
	// up-sizing results in 0 being padded at the end
	// \todo ensure we reduce using the highest entropy bits
	private static BitSet bitsetResize(final BitSet a, final int numBits) {
	  assert(numBits > 0);
	  final BitSet b = new BitSet(numBits);
    for (int i = 0; i < Math.min(a.size(), numBits); ++i)
      b.set(i, a.get(i));
    
    return b;
	}
	
	private static void partHashSet(final BitSet dst, final int dstOffset, final BitSet src) {
	  assert(dstOffset + src.length() <= dst.length());
	  for (int i = dstOffset; i < src.length(); ++i)
	    dst.set(dstOffset + i, src.get(i));
	}
	
	// This is fucking absurd Java, get your crap together.
	@FunctionalInterface
  interface Fn0<         _R> { public _R apply(); }
  @FunctionalInterface
  interface Fn1<A,       _R> { public _R apply(A a); }
	@FunctionalInterface
  interface Fn2<A, B,    _R> { public _R apply(A a, B b); }
	@FunctionalInterface
	interface Fn3<A, B, C, _R> { public _R apply(A a, B b, C c); }

	private static BitSet hashServiceId(ServiceId svcId) {
	  final BitSet bs = new BitSet(kTotalBitLen);
	  final Fn3<Integer, Integer, String, Unit> hashInto =
	      (Integer offset, Integer bitLen, String s) -> {
	    if (s != null)
  	    partHashSet(bs, offset, bitsetResize(hashString(s), bitLen));

	    return Unit.U;
	  };
	  
	  // hash semantic 
	  for (int i = 0; i < ServiceId.kPartsSemantic; ++i)
	    hashInto.apply(i * kSemanticPartBitLen, kSemanticPartBitLen
	                  ,svcId.parts[i]);

	  // hash provider
	  hashInto.apply(kSemanticTotalBitLen, kProviderBitLen
                  ,svcId.parts[ServiceId.kPartsSemantic]);
	  
    return bs;
	}
	
	private static BitSet bitsetArithPwrOf2(final BitSet _bs, final int pwrOf2, final boolean addition) {
    // The bitset is defined as big-endian unsigned, i.e. (idx 0 is highest bit)
    final BitSet bs = (BitSet)_bs.clone();
    assert(pwrOf2 < bs.length());
    for (int i = bs.length() - 1 - pwrOf2; i >= 0; --i) {
      bs.flip(i);
      // add/sub for unsigned binary is the same, except we bail if (set == addition) after flip
      if (bs.get(i) == addition) break;
    }
    
    return bs;
  }
	protected static BitSet bitsetSubPwrOf2(final BitSet _bs, final int pwrOf2)
	{ return bitsetArithPwrOf2(_bs, pwrOf2, false); }
	
	protected static BitSet bitsetAddPwrOf2(final BitSet _bs, final int pwrOf2)
  { return bitsetArithPwrOf2(_bs, pwrOf2, true); }
		
	public ID(ProviderId x) {
    id = hashServiceId(x);
  }
	
	

	public static class IdSpan {
	  public static final IdSpan kEmpty = new IdSpan(Optional.empty());
    public static final IdSpan kAll   = Inclusive(idMin, idMax);
    
    public static IdSpan Inclusive(final ID bgn, final ID end)
    { return new IdSpan(Optional.of(new SpanInclusive(bgn, end))); }

	  public boolean empty() { return !span.isPresent(); }

    public boolean contains(final ID x) {
      return span.map((SpanInclusive s) -> {
              final Fn2<ID, ID, Boolean> ltEq   = (ID a, ID b) -> a.compareTo(b) <= 0;
              final Fn2<ID, ID, Boolean> inSpan = (ID a, ID b) -> ltEq.apply(a, x) && ltEq.apply(x, b);
              if (ltEq.apply(s.bgn, s.end)) // non-wrapping
                return inSpan.apply(s.bgn, s.end);
              
              // wraps around, check both [bgn, max] and [min, end]
              return inSpan.apply(s.bgn, idMax) ||
                     inSpan.apply(idMin, s.end);
          }).orElse(false);
    }
    
    public boolean containsExcl(ID x) {
      return span.map((SpanInclusive s) -> {
          return (!(s.bgn.equals(x) || s.end.equals(x))) &&
                 contains(x);
        }).orElse(false);
    }
    
    public ID bgn() { return span.map((SpanInclusive s) -> s.bgn).orElse(null); } 
    public ID end() { return span.map((SpanInclusive s) -> s.end).orElse(null); }

    public IdSpan subsetMin(final ID newMin) {
      return span.map((SpanInclusive s) -> {
          return contains(newMin) ? Inclusive(newMin, s.end) : kEmpty;
        }).orElse(kEmpty);
    }
    
    private static class SpanInclusive {
      public final ID bgn, end;
      SpanInclusive(ID _bgn, ID _end) { bgn =  _bgn; end =  _end; }
    }
    
    private final Optional<SpanInclusive> span;
    private IdSpan(Optional<SpanInclusive> s) { span = s; }
	}
	
	public static IdSpan ServiceId(ServiceId svcId) {
	  final ID bgn = new ID(hashServiceId(svcId));
	  
	  final BitSet bsEndIncl = (BitSet)bgn.id.clone();
	  final Fn2<Integer, Integer, Unit> endFillBits = (Integer offset, Integer len) -> {
	    assert(offset + len <= bgn.id.length());
	    bsEndIncl.set(offset, offset + len);
	    return Unit.U;
	  };
	  
	  for (int i = svcId.partsGivenCount(); i < ServiceId.kPartsSemantic; ++i)
	    endFillBits.apply(i * kSemanticPartBitLen, kSemanticPartBitLen);

	  if (svcId.getProviderPart() == null)
	    endFillBits.apply(kSemanticTotalBitLen, kProviderBitLen);
	  
	  return IdSpan.Inclusive(bgn, new ID(bsEndIncl));
	}
	
	public static ID NodeId(final byte[] blob) {
	  return new ID(bitsetResize(BitSet.valueOf(blob), kTotalBitLen));
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

	  return new ID(bitsetAddPwrOf2(id, powerOfTwo));
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

	// this in (a, b)
	// NOTE: *not* [a, b], but (a, b)
  public boolean isInInterval(ID a, ID b)
  { return IdSpan.Inclusive(a, b).containsExcl(this); }

}
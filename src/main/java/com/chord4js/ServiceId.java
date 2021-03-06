package com.chord4js;

import java.io.Serializable;

/**
 * Implementation simplification:
 * 
 * ASSUME: All service names are an N part semantic name + a provider id (all
 * part are strings)
 */
public class ServiceId implements Serializable, Comparable<ServiceId> {
	
	/**
   * 
   */
  private static final long serialVersionUID = 4579761673840594766L;

	/**
	 * Number of semantic parts for a service
	 */
	public static final int kPartsSemantic = 4;

	/**
	 * Total number of parts for a service
	 */
	public static final int kPartsAll = kPartsSemantic + 1; // + 1 for provider part
	
	/**
	 * Underlying data structure of this class. Stores parts of the
	 * service identifier (semantic + provider)
	 */
	public final String[] parts = new String[kPartsAll];

	ServiceId(final String[] semanticName) {
		// verify input
		if (semanticName.length > kPartsSemantic)
			throw new IllegalArgumentException("Semantic name has too many parts");
		if (semanticName.length == 0)
			throw new IllegalArgumentException("Semantic name has too few parts");
		
		for (int i = 0; i < semanticName.length; ++i) {
		  assert(semanticName[i] != null);
		  parts[i] = semanticName[i];
		}
	}
	
	/**
   * A string of the provider part
   * 
   * @return
   */
  public String getProviderPart() {
    return parts[kPartsSemantic];
  }
  
  public String partsDisplay() {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parts.length; ++i) {
      sb.append((parts[i] != null) ? parts[i] : "");
      if ((i+1)!= parts.length) sb.append(".");
    }
    return sb.toString();
  }
  
  @Override
  public String toString() { return "serviceId: " + partsDisplay(); }
	
	public int partsGivenCount() {
	  assert(parts.length == kPartsAll);
	  for (int i = 0; i < parts.length; ++i)
	    if (parts[i] == null) return i;
	  
	  return parts.length;
	}

  @Override
  public int compareTo(ServiceId o) {
    for (int i = 0; i < parts.length; ++i) {
      final boolean aNull = parts[i] == null;
      final boolean bNull = parts[i] == null;
      if (aNull && bNull) continue;
      if (aNull) return -1;
      if (bNull) return  1;
      
      final int c = parts[i].compareTo(o.parts[i]);
      if (c != 0) return c;
    }

    return 0;
  }
}

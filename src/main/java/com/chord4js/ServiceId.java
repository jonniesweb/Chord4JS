package com.chord4js;

import java.util.Optional;

import de.uniba.wiai.lspi.chord.service.Key;

// Implementation simplification:
// ASSUME: All service names are an N part semantic name + a provider id (all part are strings)
public class ServiceId {
  public static final int kPartsSemantic  = 4;
  public static final int kPartsProvider  = 1;
  public static final int kPartsAll       = kPartsSemantic + kPartsProvider;
  
  public final int[] hashedParts = new int[kPartsAll];
  
  ServiceId(final String[] semanticName) throws Exception {
    if (semanticName.length > kPartsSemantic)
      throw new Exception("Semantic name has too many parts");
    
    for (int i = 0; i < semanticName.length; ++i)
      hashedParts[i] = semanticName[i].hashCode();
  }
}

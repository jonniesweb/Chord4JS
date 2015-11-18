package com.chord4js;

public class ProviderId extends ServiceId {
  ProviderId(String[] semanticName, String providerId) throws Exception {
    super(semanticName);
    hashedParts[kNamePartsSemantic] = providerId.hashCode();
  }
}

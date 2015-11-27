package com.chord4js;

public class ProviderId extends ServiceId {
	/**
   * 
   */
  private static final long serialVersionUID = -6150260522879395036L;

	public ProviderId(String[] semanticName, String providerId) {
		super(semanticName);

		// hash the provider id and add it to the hashedParts array
		parts[kPartsSemantic] = providerId;
		
		// SANCHK: no wild-card parts
    for (int i = 0; i < kPartsAll; ++i)
      assert(parts[i] != null);
	}

	@Override
	public String toString() {
		return "providerId: " + parts.toString();
	}
	
}

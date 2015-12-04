package de.uniba.wiai.lspi.chord.service;

import java.io.Serializable;

import com.chord4js.QoSConstraints;
import com.chord4js.ServiceId;

import de.uniba.wiai.lspi.chord.data.ID;

public class C4SMsgRetrieve implements Serializable {
  private static final long serialVersionUID = 7664956782272229101L;

  final public int            amount;
  final public QoSConstraints constraints;
  final public ServiceId      svcId;
  final public ID.IdSpan      span;
  
  public C4SMsgRetrieve(ServiceId a, QoSConstraints b, int amt) {
    svcId       = a;
    constraints = b;
    amount      = amt;
    span        = ID.ServiceId(a);
  }
  
  private C4SMsgRetrieve(ServiceId a, QoSConstraints b, int amt, ID.IdSpan s) {
    svcId       = a;
    constraints = b;
    amount      = amt;
    span        = s;
  }
  
  public C4SMsgRetrieve Subset(ID newMin, int amt) {
    return new C4SMsgRetrieve(svcId, constraints, amt, span.subsetMin(newMin));
  }
  
  @Override
  public String toString()
  { return "retrieve " + amount + " of " + svcId + "(" + span + ")" + "; qos: " + constraints; }
}

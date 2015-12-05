package com.chord4js;

import java.io.Serializable;

public class PairS<A extends Serializable, B extends Serializable>
    extends Pair<A, B>
    implements Serializable {
  private static final long serialVersionUID = -8914647164831651005L;
  
  public PairS(A a, B b) { super(a, b); }
  public PairS(Pair<A, B> p) { super(p.fst, p.snd); }
}

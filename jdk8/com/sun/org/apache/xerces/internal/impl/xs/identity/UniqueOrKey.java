package com.sun.org.apache.xerces.internal.impl.xs.identity;

public class UniqueOrKey extends IdentityConstraint {
   public UniqueOrKey(String namespace, String identityConstraintName, String elemName, short type) {
      super(namespace, identityConstraintName, elemName);
      this.type = type;
   }
}

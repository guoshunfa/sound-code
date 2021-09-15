package org.omg.PortableServer;

import org.omg.CORBA.PolicyOperations;

public interface LifespanPolicyOperations extends PolicyOperations {
   LifespanPolicyValue value();
}

package org.omg.PortableServer;

import org.omg.CORBA.PolicyOperations;

public interface ThreadPolicyOperations extends PolicyOperations {
   ThreadPolicyValue value();
}

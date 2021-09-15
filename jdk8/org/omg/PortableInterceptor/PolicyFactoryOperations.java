package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;

public interface PolicyFactoryOperations {
   Policy create_policy(int var1, Any var2) throws PolicyError;
}

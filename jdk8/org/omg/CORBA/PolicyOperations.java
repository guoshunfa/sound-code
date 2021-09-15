package org.omg.CORBA;

public interface PolicyOperations {
   int policy_type();

   Policy copy();

   void destroy();
}

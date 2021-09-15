package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;

public interface CurrentOperations extends org.omg.CORBA.CurrentOperations {
   Any get_slot(int var1) throws InvalidSlot;

   void set_slot(int var1, Any var2) throws InvalidSlot;
}

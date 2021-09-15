package org.omg.CORBA_2_3;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueFactory;

public abstract class ORB extends org.omg.CORBA.ORB {
   public ValueFactory register_value_factory(String var1, ValueFactory var2) {
      throw new NO_IMPLEMENT();
   }

   public void unregister_value_factory(String var1) {
      throw new NO_IMPLEMENT();
   }

   public ValueFactory lookup_value_factory(String var1) {
      throw new NO_IMPLEMENT();
   }

   public Object get_value_def(String var1) throws BAD_PARAM {
      throw new NO_IMPLEMENT();
   }

   public void set_delegate(java.lang.Object var1) {
      throw new NO_IMPLEMENT();
   }
}

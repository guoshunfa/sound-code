package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynValue;

public class DynValueImpl extends DynValueCommonImpl implements DynValue {
   private DynValueImpl() {
      this((ORB)null, (Any)null, false);
   }

   protected DynValueImpl(ORB var1, Any var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected DynValueImpl(ORB var1, TypeCode var2) {
      super(var1, var2);
   }
}

package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

public abstract class SpecialMethod {
   static SpecialMethod[] methods = new SpecialMethod[]{new IsA(), new GetInterface(), new NonExistent(), new NotExistent()};

   public abstract boolean isNonExistentMethod();

   public abstract String getName();

   public abstract CorbaMessageMediator invoke(Object var1, CorbaMessageMediator var2, byte[] var3, ObjectAdapter var4);

   public static final SpecialMethod getSpecialMethod(String var0) {
      for(int var1 = 0; var1 < methods.length; ++var1) {
         if (methods[var1].getName().equals(var0)) {
            return methods[var1];
         }
      }

      return null;
   }
}

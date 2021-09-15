package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Object;

public class ORBInitRefResolverImpl implements Resolver {
   Operation urlHandler;
   Map orbInitRefTable;

   public ORBInitRefResolverImpl(Operation var1, StringPair[] var2) {
      this.urlHandler = var1;
      this.orbInitRefTable = new HashMap();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         StringPair var4 = var2[var3];
         this.orbInitRefTable.put(var4.getFirst(), var4.getSecond());
      }

   }

   public Object resolve(String var1) {
      String var2 = (String)this.orbInitRefTable.get(var1);
      if (var2 == null) {
         return null;
      } else {
         Object var3 = (Object)this.urlHandler.operate(var2);
         return var3;
      }
   }

   public Set list() {
      return this.orbInitRefTable.keySet();
   }
}

package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;

public class ORBDefaultInitRefResolverImpl implements Resolver {
   Operation urlHandler;
   String orbDefaultInitRef;

   public ORBDefaultInitRefResolverImpl(Operation var1, String var2) {
      this.urlHandler = var1;
      this.orbDefaultInitRef = var2;
   }

   public Object resolve(String var1) {
      if (this.orbDefaultInitRef == null) {
         return null;
      } else {
         String var2;
         if (this.orbDefaultInitRef.startsWith("corbaloc:")) {
            var2 = this.orbDefaultInitRef + "/" + var1;
         } else {
            var2 = this.orbDefaultInitRef + "#" + var1;
         }

         return (Object)this.urlHandler.operate(var2);
      }
   }

   public Set list() {
      return new HashSet();
   }
}

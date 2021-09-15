package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.LocalResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Object;

public class LocalResolverImpl implements LocalResolver {
   Map nameToClosure = new HashMap();

   public synchronized Object resolve(String var1) {
      Closure var2 = (Closure)this.nameToClosure.get(var1);
      return var2 == null ? null : (Object)((Object)var2.evaluate());
   }

   public synchronized Set list() {
      return this.nameToClosure.keySet();
   }

   public synchronized void register(String var1, Closure var2) {
      this.nameToClosure.put(var1, var2);
   }
}

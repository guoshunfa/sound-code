package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.Set;
import org.omg.CORBA.Object;

public class SplitLocalResolverImpl implements LocalResolver {
   private Resolver resolver;
   private LocalResolver localResolver;

   public SplitLocalResolverImpl(Resolver var1, LocalResolver var2) {
      this.resolver = var1;
      this.localResolver = var2;
   }

   public void register(String var1, Closure var2) {
      this.localResolver.register(var1, var2);
   }

   public Object resolve(String var1) {
      return this.resolver.resolve(var1);
   }

   public Set list() {
      return this.resolver.list();
   }
}

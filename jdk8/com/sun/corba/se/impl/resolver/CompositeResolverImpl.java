package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;

public class CompositeResolverImpl implements Resolver {
   private Resolver first;
   private Resolver second;

   public CompositeResolverImpl(Resolver var1, Resolver var2) {
      this.first = var1;
      this.second = var2;
   }

   public Object resolve(String var1) {
      Object var2 = this.first.resolve(var1);
      if (var2 == null) {
         var2 = this.second.resolve(var1);
      }

      return var2;
   }

   public Set list() {
      HashSet var1 = new HashSet();
      var1.addAll(this.first.list());
      var1.addAll(this.second.list());
      return var1;
   }
}

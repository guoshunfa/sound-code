package com.sun.naming.internal;

import java.util.List;
import javax.naming.NamingException;

public final class FactoryEnumeration {
   private List<NamedWeakReference<Object>> factories;
   private int posn = 0;
   private ClassLoader loader;

   FactoryEnumeration(List<NamedWeakReference<Object>> var1, ClassLoader var2) {
      this.factories = var1;
      this.loader = var2;
   }

   public Object next() throws NamingException {
      synchronized(this.factories) {
         NamedWeakReference var2 = (NamedWeakReference)this.factories.get(this.posn++);
         Object var3 = var2.get();
         if (var3 != null && !(var3 instanceof Class)) {
            return var3;
         } else {
            String var4 = var2.getName();

            Object var10000;
            NamingException var6;
            try {
               if (var3 == null) {
                  Class var5 = Class.forName(var4, true, this.loader);
                  var3 = var5;
               }

               var3 = ((Class)var3).newInstance();
               var2 = new NamedWeakReference(var3, var4);
               this.factories.set(this.posn - 1, var2);
               var10000 = var3;
            } catch (ClassNotFoundException var8) {
               var6 = new NamingException("No longer able to load " + var4);
               var6.setRootCause(var8);
               throw var6;
            } catch (InstantiationException var9) {
               var6 = new NamingException("Cannot instantiate " + var3);
               var6.setRootCause(var9);
               throw var6;
            } catch (IllegalAccessException var10) {
               var6 = new NamingException("Cannot access " + var3);
               var6.setRootCause(var10);
               throw var6;
            }

            return var10000;
         }
      }
   }

   public boolean hasMore() {
      synchronized(this.factories) {
         return this.posn < this.factories.size();
      }
   }
}

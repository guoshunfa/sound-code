package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class BindingEnumeration implements NamingEnumeration<Binding> {
   private RegistryContext ctx;
   private final String[] names;
   private int nextName;

   BindingEnumeration(RegistryContext var1, String[] var2) {
      this.ctx = new RegistryContext(var1);
      this.names = var2;
      this.nextName = 0;
   }

   protected void finalize() {
      this.ctx.close();
   }

   public boolean hasMore() {
      if (this.nextName >= this.names.length) {
         this.ctx.close();
      }

      return this.nextName < this.names.length;
   }

   public Binding next() throws NamingException {
      if (!this.hasMore()) {
         throw new NoSuchElementException();
      } else {
         String var1 = this.names[this.nextName++];
         Name var2 = (new CompositeName()).add(var1);
         Object var3 = this.ctx.lookup(var2);
         String var4 = var2.toString();
         Binding var5 = new Binding(var4, var3);
         var5.setNameInNamespace(var4);
         return var5;
      }
   }

   public boolean hasMoreElements() {
      return this.hasMore();
   }

   public Binding nextElement() {
      try {
         return this.next();
      } catch (NamingException var2) {
         throw new NoSuchElementException("javax.naming.NamingException was thrown");
      }
   }

   public void close() {
      this.finalize();
   }
}

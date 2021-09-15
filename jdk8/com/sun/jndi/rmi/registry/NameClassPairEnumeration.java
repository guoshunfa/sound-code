package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class NameClassPairEnumeration implements NamingEnumeration<NameClassPair> {
   private final String[] names;
   private int nextName;

   NameClassPairEnumeration(String[] var1) {
      this.names = var1;
      this.nextName = 0;
   }

   public boolean hasMore() {
      return this.nextName < this.names.length;
   }

   public NameClassPair next() throws NamingException {
      if (!this.hasMore()) {
         throw new NoSuchElementException();
      } else {
         String var1 = this.names[this.nextName++];
         Name var2 = (new CompositeName()).add(var1);
         NameClassPair var3 = new NameClassPair(var2.toString(), "java.lang.Object");
         var3.setNameInNamespace(var1);
         return var3;
      }
   }

   public boolean hasMoreElements() {
      return this.hasMore();
   }

   public NameClassPair nextElement() {
      try {
         return this.next();
      } catch (NamingException var2) {
         throw new NoSuchElementException("javax.naming.NamingException was thrown");
      }
   }

   public void close() {
      this.nextName = this.names.length;
   }
}

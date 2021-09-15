package com.sun.jndi.dns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

abstract class BaseNameClassPairEnumeration<T> implements NamingEnumeration<T> {
   protected Enumeration<NameNode> nodes;
   protected DnsContext ctx;

   BaseNameClassPairEnumeration(DnsContext var1, Hashtable<String, NameNode> var2) {
      this.ctx = var1;
      this.nodes = var2 != null ? var2.elements() : null;
   }

   public final void close() {
      this.nodes = null;
      this.ctx = null;
   }

   public final boolean hasMore() {
      boolean var1 = this.nodes != null && this.nodes.hasMoreElements();
      if (!var1) {
         this.close();
      }

      return var1;
   }

   public final boolean hasMoreElements() {
      return this.hasMore();
   }

   public abstract T next() throws NamingException;

   public final T nextElement() {
      try {
         return this.next();
      } catch (NamingException var3) {
         NoSuchElementException var2 = new NoSuchElementException();
         var2.initCause(var3);
         throw var2;
      }
   }
}

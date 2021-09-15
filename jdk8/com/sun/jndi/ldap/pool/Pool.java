package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import javax.naming.NamingException;

public final class Pool {
   static final boolean debug;
   private static final ReferenceQueue<ConnectionsRef> queue;
   private static final Collection<Reference<ConnectionsRef>> weakRefs;
   private final int maxSize;
   private final int prefSize;
   private final int initSize;
   private final Map<Object, ConnectionsRef> map = new WeakHashMap();

   public Pool(int var1, int var2, int var3) {
      this.prefSize = var2;
      this.maxSize = var3;
      this.initSize = var1;
   }

   public PooledConnection getPooledConnection(Object var1, long var2, PooledConnectionFactory var4) throws NamingException {
      this.d("get(): ", var1);
      if (debug) {
         synchronized(this.map) {
            this.d("size: ", this.map.size());
         }
      }

      expungeStaleConnections();
      Connections var5;
      synchronized(this.map) {
         var5 = this.getConnections(var1);
         if (var5 == null) {
            this.d("get(): creating new connections list for ", var1);
            var5 = new Connections(var1, this.initSize, this.prefSize, this.maxSize, var4);
            ConnectionsRef var7 = new ConnectionsRef(var5);
            this.map.put(var1, var7);
            ConnectionsWeakRef var8 = new ConnectionsWeakRef(var7, queue);
            weakRefs.add(var8);
         }

         this.d("get(): size after: ", this.map.size());
      }

      return var5.get(var2, var4);
   }

   private Connections getConnections(Object var1) {
      ConnectionsRef var2 = (ConnectionsRef)this.map.get(var1);
      return var2 != null ? var2.getConnections() : null;
   }

   public void expire(long var1) {
      ArrayList var3;
      synchronized(this.map) {
         var3 = new ArrayList(this.map.values());
      }

      ArrayList var4 = new ArrayList();
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         ConnectionsRef var7 = (ConnectionsRef)var6.next();
         Connections var5 = var7.getConnections();
         if (var5.expire(var1)) {
            this.d("expire(): removing ", var5);
            var4.add(var7);
         }
      }

      synchronized(this.map) {
         this.map.values().removeAll(var4);
      }

      expungeStaleConnections();
   }

   private static void expungeStaleConnections() {
      ConnectionsWeakRef var0 = null;

      while((var0 = (ConnectionsWeakRef)queue.poll()) != null) {
         Connections var1 = var0.getConnections();
         if (debug) {
            System.err.println("weak reference cleanup: Closing Connections:" + var1);
         }

         var1.close();
         weakRefs.remove(var0);
         var0.clear();
      }

   }

   public void showStats(PrintStream var1) {
      var1.println("===== Pool start ======================");
      var1.println("maximum pool size: " + this.maxSize);
      var1.println("preferred pool size: " + this.prefSize);
      var1.println("initial pool size: " + this.initSize);
      synchronized(this.map) {
         var1.println("current pool size: " + this.map.size());
         Iterator var5 = this.map.entrySet().iterator();

         while(true) {
            if (!var5.hasNext()) {
               break;
            }

            Map.Entry var6 = (Map.Entry)var5.next();
            Object var2 = var6.getKey();
            Connections var3 = ((ConnectionsRef)var6.getValue()).getConnections();
            var1.println("   " + var2 + ":" + var3.getStats());
         }
      }

      var1.println("====== Pool end =====================");
   }

   public String toString() {
      synchronized(this.map) {
         return super.toString() + " " + this.map.toString();
      }
   }

   private void d(String var1, int var2) {
      if (debug) {
         System.err.println(this + "." + var1 + var2);
      }

   }

   private void d(String var1, Object var2) {
      if (debug) {
         System.err.println(this + "." + var1 + var2);
      }

   }

   static {
      debug = LdapPoolManager.debug;
      queue = new ReferenceQueue();
      weakRefs = Collections.synchronizedList(new LinkedList());
   }
}

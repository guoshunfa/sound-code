package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.naming.CommunicationException;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingException;

final class Connections implements PoolCallback {
   private static final boolean debug;
   private static final boolean trace;
   private static final int DEFAULT_SIZE = 10;
   private final int maxSize;
   private final int prefSize;
   private final List<ConnectionDesc> conns;
   private boolean closed = false;
   private Reference<Object> ref;

   Connections(Object var1, int var2, int var3, int var4, PooledConnectionFactory var5) throws NamingException {
      this.maxSize = var4;
      if (var4 > 0) {
         this.prefSize = Math.min(var3, var4);
         var2 = Math.min(var2, var4);
      } else {
         this.prefSize = var3;
      }

      this.conns = new ArrayList(var4 > 0 ? var4 : 10);
      this.ref = new SoftReference(var1);
      this.d("init size=", var2);
      this.d("max size=", var4);
      this.d("preferred size=", var3);

      for(int var7 = 0; var7 < var2; ++var7) {
         PooledConnection var6 = var5.createPooledConnection(this);
         this.td("Create ", var6, var5);
         this.conns.add(new ConnectionDesc(var6));
      }

   }

   synchronized PooledConnection get(long var1, PooledConnectionFactory var3) throws NamingException {
      long var5 = var1 > 0L ? System.currentTimeMillis() : 0L;
      long var7 = var1;
      this.d("get(): before");

      PooledConnection var4;
      while((var4 = this.getOrCreateConnection(var3)) == null) {
         if (var1 > 0L && var7 <= 0L) {
            throw new CommunicationException("Timeout exceeded while waiting for a connection: " + var1 + "ms");
         }

         try {
            this.d("get(): waiting");
            if (var7 > 0L) {
               this.wait(var7);
            } else {
               this.wait();
            }
         } catch (InterruptedException var11) {
            throw new InterruptedNamingException("Interrupted while waiting for a connection");
         }

         if (var1 > 0L) {
            long var9 = System.currentTimeMillis();
            var7 = var1 - (var9 - var5);
         }
      }

      this.d("get(): after");
      return var4;
   }

   private PooledConnection getOrCreateConnection(PooledConnectionFactory var1) throws NamingException {
      int var2 = this.conns.size();
      PooledConnection var3 = null;
      if (this.prefSize <= 0 || var2 >= this.prefSize) {
         for(int var5 = 0; var5 < var2; ++var5) {
            ConnectionDesc var4 = (ConnectionDesc)this.conns.get(var5);
            if ((var3 = var4.tryUse()) != null) {
               this.d("get(): use ", var3);
               this.td("Use ", var3);
               return var3;
            }
         }
      }

      if (this.maxSize > 0 && var2 >= this.maxSize) {
         return null;
      } else {
         var3 = var1.createPooledConnection(this);
         this.td("Create and use ", var3, var1);
         this.conns.add(new ConnectionDesc(var3, true));
         return var3;
      }
   }

   public synchronized boolean releasePooledConnection(PooledConnection var1) {
      ConnectionDesc var2;
      int var3 = this.conns.indexOf(var2 = new ConnectionDesc(var1));
      this.d("release(): ", var1);
      if (var3 < 0) {
         return false;
      } else {
         if (!this.closed && (this.prefSize <= 0 || this.conns.size() <= this.prefSize)) {
            this.d("release(): release ", var1);
            this.td("Release ", var1);
            var2 = (ConnectionDesc)this.conns.get(var3);
            var2.release();
         } else {
            this.d("release(): closing ", var1);
            this.td("Close ", var1);
            this.conns.remove(var2);
            var1.closeConnection();
         }

         this.notifyAll();
         this.d("release(): notify");
         return true;
      }
   }

   public synchronized boolean removePooledConnection(PooledConnection var1) {
      if (this.conns.remove(new ConnectionDesc(var1))) {
         this.d("remove(): ", var1);
         this.notifyAll();
         this.d("remove(): notify");
         this.td("Remove ", var1);
         if (this.conns.isEmpty()) {
            this.ref = null;
         }

         return true;
      } else {
         this.d("remove(): not found ", var1);
         return false;
      }
   }

   boolean expire(long var1) {
      ArrayList var3;
      synchronized(this) {
         var3 = new ArrayList(this.conns);
      }

      ArrayList var4 = new ArrayList();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         ConnectionDesc var6 = (ConnectionDesc)var5.next();
         this.d("expire(): ", var6);
         if (var6.expire(var1)) {
            var4.add(var6);
            this.td("expire(): Expired ", var6);
         }
      }

      synchronized(this) {
         this.conns.removeAll(var4);
         return this.conns.isEmpty();
      }
   }

   synchronized void close() {
      this.expire(System.currentTimeMillis());
      this.closed = true;
   }

   String getStats() {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      long var4 = 0L;
      synchronized(this) {
         int var6 = this.conns.size();

         for(int var9 = 0; var9 < var6; ++var9) {
            ConnectionDesc var8 = (ConnectionDesc)this.conns.get(var9);
            var4 += var8.getUseCount();
            switch(var8.getState()) {
            case 0:
               ++var2;
               break;
            case 1:
               ++var1;
               break;
            case 2:
               ++var3;
            }
         }

         return "size=" + var6 + "; use=" + var4 + "; busy=" + var2 + "; idle=" + var1 + "; expired=" + var3;
      }
   }

   private void d(String var1, Object var2) {
      if (debug) {
         this.d(var1 + var2);
      }

   }

   private void d(String var1, int var2) {
      if (debug) {
         this.d(var1 + var2);
      }

   }

   private void d(String var1) {
      if (debug) {
         System.err.println(this + "." + var1 + "; size: " + this.conns.size());
      }

   }

   private void td(String var1, Object var2, Object var3) {
      if (trace) {
         this.td(var1 + var2 + "[" + var3 + "]");
      }

   }

   private void td(String var1, Object var2) {
      if (trace) {
         this.td(var1 + var2);
      }

   }

   private void td(String var1) {
      if (trace) {
         System.err.println(var1);
      }

   }

   static {
      debug = Pool.debug;
      trace = LdapPoolManager.trace;
   }
}

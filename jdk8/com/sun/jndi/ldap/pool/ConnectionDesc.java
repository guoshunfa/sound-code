package com.sun.jndi.ldap.pool;

final class ConnectionDesc {
   private static final boolean debug;
   static final byte BUSY = 0;
   static final byte IDLE = 1;
   static final byte EXPIRED = 2;
   private final PooledConnection conn;
   private byte state = 1;
   private long idleSince;
   private long useCount = 0L;

   ConnectionDesc(PooledConnection var1) {
      this.conn = var1;
   }

   ConnectionDesc(PooledConnection var1, boolean var2) {
      this.conn = var1;
      if (var2) {
         this.state = 0;
         ++this.useCount;
      }

   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof ConnectionDesc && ((ConnectionDesc)var1).conn == this.conn;
   }

   public int hashCode() {
      return this.conn.hashCode();
   }

   synchronized boolean release() {
      this.d("release()");
      if (this.state == 0) {
         this.state = 1;
         this.idleSince = System.currentTimeMillis();
         return true;
      } else {
         return false;
      }
   }

   synchronized PooledConnection tryUse() {
      this.d("tryUse()");
      if (this.state == 1) {
         this.state = 0;
         ++this.useCount;
         return this.conn;
      } else {
         return null;
      }
   }

   synchronized boolean expire(long var1) {
      if (this.state == 1 && this.idleSince < var1) {
         this.d("expire(): expired");
         this.state = 2;
         this.conn.closeConnection();
         return true;
      } else {
         this.d("expire(): not expired");
         return false;
      }
   }

   public String toString() {
      return this.conn.toString() + " " + (this.state == 0 ? "busy" : (this.state == 1 ? "idle" : "expired"));
   }

   int getState() {
      return this.state;
   }

   long getUseCount() {
      return this.useCount;
   }

   private void d(String var1) {
      if (debug) {
         System.err.println("ConnectionDesc." + var1 + " " + this.toString());
      }

   }

   static {
      debug = Pool.debug;
   }
}

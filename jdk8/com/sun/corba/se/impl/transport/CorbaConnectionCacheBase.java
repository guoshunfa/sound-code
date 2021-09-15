package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaConnectionCache;
import java.util.Collection;
import java.util.Iterator;

public abstract class CorbaConnectionCacheBase implements ConnectionCache, CorbaConnectionCache {
   protected ORB orb;
   protected long timestamp = 0L;
   protected String cacheType;
   protected String monitoringName;
   protected ORBUtilSystemException wrapper;

   protected CorbaConnectionCacheBase(ORB var1, String var2, String var3) {
      this.orb = var1;
      this.cacheType = var2;
      this.monitoringName = var3;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
      this.registerWithMonitoring();
      this.dprintCreation();
   }

   public String getCacheType() {
      return this.cacheType;
   }

   public synchronized void stampTime(Connection var1) {
      var1.setTimeStamp((long)(this.timestamp++));
   }

   public long numberOfConnections() {
      synchronized(this.backingStore()) {
         return (long)this.values().size();
      }
   }

   public void close() {
      synchronized(this.backingStore()) {
         Iterator var2 = this.values().iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            ((CorbaConnection)var3).closeConnectionResources();
         }

      }
   }

   public long numberOfIdleConnections() {
      long var1 = 0L;
      synchronized(this.backingStore()) {
         Iterator var4 = this.values().iterator();

         while(var4.hasNext()) {
            if (!((Connection)var4.next()).isBusy()) {
               ++var1;
            }
         }

         return var1;
      }
   }

   public long numberOfBusyConnections() {
      long var1 = 0L;
      synchronized(this.backingStore()) {
         Iterator var4 = this.values().iterator();

         while(var4.hasNext()) {
            if (((Connection)var4.next()).isBusy()) {
               ++var1;
            }
         }

         return var1;
      }
   }

   public synchronized boolean reclaim() {
      boolean var4;
      try {
         long var1 = this.numberOfConnections();
         if (this.orb.transportDebugFlag) {
            this.dprint(".reclaim->: " + var1 + " (" + this.orb.getORBData().getHighWaterMark() + "/" + this.orb.getORBData().getLowWaterMark() + "/" + this.orb.getORBData().getNumberToReclaim() + ")");
         }

         if (var1 <= (long)this.orb.getORBData().getHighWaterMark() || var1 < (long)this.orb.getORBData().getLowWaterMark()) {
            boolean var3 = false;
            return var3;
         }

         Object var19 = this.backingStore();
         synchronized(var19) {
            for(int var5 = 0; var5 < this.orb.getORBData().getNumberToReclaim(); ++var5) {
               Connection var6 = null;
               long var7 = Long.MAX_VALUE;
               Iterator var9 = this.values().iterator();

               while(var9.hasNext()) {
                  Connection var10 = (Connection)var9.next();
                  if (!var10.isBusy() && var10.getTimeStamp() < var7) {
                     var6 = var10;
                     var7 = var10.getTimeStamp();
                  }
               }

               if (var6 == null) {
                  boolean var20 = false;
                  return var20;
               }

               try {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".reclaim: closing: " + var6);
                  }

                  var6.close();
               } catch (Exception var16) {
               }
            }

            if (this.orb.transportDebugFlag) {
               this.dprint(".reclaim: connections reclaimed (" + (var1 - this.numberOfConnections()) + ")");
            }
         }

         var4 = true;
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".reclaim<-: " + this.numberOfConnections());
         }

      }

      return var4;
   }

   public String getMonitoringName() {
      return this.monitoringName;
   }

   public abstract Collection values();

   protected abstract Object backingStore();

   protected abstract void registerWithMonitoring();

   protected void dprintCreation() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".constructor: cacheType: " + this.getCacheType() + " monitoringName: " + this.getMonitoringName());
      }

   }

   protected void dprintStatistics() {
      if (this.orb.transportDebugFlag) {
         this.dprint(".stats: " + this.numberOfConnections() + "/total " + this.numberOfBusyConnections() + "/busy " + this.numberOfIdleConnections() + "/idle (" + this.orb.getORBData().getHighWaterMark() + "/" + this.orb.getORBData().getLowWaterMark() + "/" + this.orb.getORBData().getNumberToReclaim() + ")");
      }

   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaConnectionCacheBase", var1);
   }
}

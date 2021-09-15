package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.util.Collection;
import java.util.Hashtable;

public class CorbaOutboundConnectionCacheImpl extends CorbaConnectionCacheBase implements OutboundConnectionCache {
   protected Hashtable connectionCache = new Hashtable();

   public CorbaOutboundConnectionCacheImpl(ORB var1, ContactInfo var2) {
      super(var1, var2.getConnectionCacheType(), ((CorbaContactInfo)var2).getMonitoringName());
   }

   public Connection get(ContactInfo var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".get: " + var1 + " " + var1.hashCode());
      }

      synchronized(this.backingStore()) {
         this.dprintStatistics();
         return (Connection)this.connectionCache.get(var1);
      }
   }

   public void put(ContactInfo var1, Connection var2) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".put: " + var1 + " " + var1.hashCode() + " " + var2);
      }

      synchronized(this.backingStore()) {
         this.connectionCache.put(var1, var2);
         var2.setConnectionCache(this);
         this.dprintStatistics();
      }
   }

   public void remove(ContactInfo var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".remove: " + var1 + " " + var1.hashCode());
      }

      synchronized(this.backingStore()) {
         if (var1 != null) {
            this.connectionCache.remove(var1);
         }

         this.dprintStatistics();
      }
   }

   public Collection values() {
      return this.connectionCache.values();
   }

   protected Object backingStore() {
      return this.connectionCache;
   }

   protected void registerWithMonitoring() {
      MonitoredObject var1 = this.orb.getMonitoringManager().getRootMonitoredObject();
      MonitoredObject var2 = var1.getChild("Connections");
      if (var2 == null) {
         var2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Connections", "Statistics on inbound/outbound connections");
         var1.addChild(var2);
      }

      MonitoredObject var3 = var2.getChild("Outbound");
      if (var3 == null) {
         var3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Outbound", "Statistics on outbound connections");
         var2.addChild(var3);
      }

      MonitoredObject var4 = var3.getChild(this.getMonitoringName());
      if (var4 == null) {
         var4 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.getMonitoringName(), "Connection statistics");
         var3.addChild(var4);
      }

      LongMonitoredAttributeBase var5 = new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections") {
         public Object getValue() {
            return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfConnections());
         }
      };
      var4.addAttribute(var5);
      var5 = new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections") {
         public Object getValue() {
            return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfIdleConnections());
         }
      };
      var4.addAttribute(var5);
      var5 = new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections") {
         public Object getValue() {
            return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfBusyConnections());
         }
      };
      var4.addAttribute(var5);
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaOutboundConnectionCacheImpl", var1);
   }
}

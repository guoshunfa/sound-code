package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import java.util.ArrayList;
import java.util.Collection;

public class CorbaInboundConnectionCacheImpl extends CorbaConnectionCacheBase implements InboundConnectionCache {
   protected Collection connectionCache = new ArrayList();
   private Acceptor acceptor;

   public CorbaInboundConnectionCacheImpl(ORB var1, Acceptor var2) {
      super(var1, var2.getConnectionCacheType(), ((CorbaAcceptor)var2).getMonitoringName());
      this.acceptor = var2;
      if (var1.transportDebugFlag) {
         this.dprint(": " + var2);
      }

   }

   public void close() {
      super.close();
      if (this.orb.transportDebugFlag) {
         this.dprint(".close: " + this.acceptor);
      }

      this.acceptor.close();
   }

   public Connection get(Acceptor var1) {
      throw this.wrapper.methodShouldNotBeCalled();
   }

   public Acceptor getAcceptor() {
      return this.acceptor;
   }

   public void put(Acceptor var1, Connection var2) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".put: " + var1 + " " + var2);
      }

      synchronized(this.backingStore()) {
         this.connectionCache.add(var2);
         var2.setConnectionCache(this);
         this.dprintStatistics();
      }
   }

   public void remove(Connection var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".remove: " + var1);
      }

      synchronized(this.backingStore()) {
         this.connectionCache.remove(var1);
         this.dprintStatistics();
      }
   }

   public Collection values() {
      return this.connectionCache;
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

      MonitoredObject var3 = var2.getChild("Inbound");
      if (var3 == null) {
         var3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Inbound", "Statistics on inbound connections");
         var2.addChild(var3);
      }

      MonitoredObject var4 = var3.getChild(this.getMonitoringName());
      if (var4 == null) {
         var4 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.getMonitoringName(), "Connection statistics");
         var3.addChild(var4);
      }

      LongMonitoredAttributeBase var5 = new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections") {
         public Object getValue() {
            return new Long(CorbaInboundConnectionCacheImpl.this.numberOfConnections());
         }
      };
      var4.addAttribute(var5);
      var5 = new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections") {
         public Object getValue() {
            return new Long(CorbaInboundConnectionCacheImpl.this.numberOfIdleConnections());
         }
      };
      var4.addAttribute(var5);
      var5 = new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections") {
         public Object getValue() {
            return new Long(CorbaInboundConnectionCacheImpl.this.numberOfBusyConnections());
         }
      };
      var4.addAttribute(var5);
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaInboundConnectionCacheImpl", var1);
   }
}

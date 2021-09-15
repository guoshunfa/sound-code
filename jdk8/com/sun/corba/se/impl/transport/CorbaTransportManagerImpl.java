package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CorbaTransportManagerImpl implements CorbaTransportManager {
   protected ORB orb;
   protected List acceptors;
   protected Map outboundConnectionCaches;
   protected Map inboundConnectionCaches;
   protected Selector selector;

   public CorbaTransportManagerImpl(ORB var1) {
      this.orb = var1;
      this.acceptors = new ArrayList();
      this.outboundConnectionCaches = new HashMap();
      this.inboundConnectionCaches = new HashMap();
      this.selector = new SelectorImpl(var1);
   }

   public ByteBufferPool getByteBufferPool(int var1) {
      throw new RuntimeException();
   }

   public OutboundConnectionCache getOutboundConnectionCache(ContactInfo var1) {
      synchronized(var1) {
         if (var1.getConnectionCache() == null) {
            Object var3 = null;
            synchronized(this.outboundConnectionCaches) {
               var3 = (OutboundConnectionCache)this.outboundConnectionCaches.get(var1.getConnectionCacheType());
               if (var3 == null) {
                  var3 = new CorbaOutboundConnectionCacheImpl(this.orb, var1);
                  this.outboundConnectionCaches.put(var1.getConnectionCacheType(), var3);
               }
            }

            var1.setConnectionCache((OutboundConnectionCache)var3);
         }

         return var1.getConnectionCache();
      }
   }

   public Collection getOutboundConnectionCaches() {
      return this.outboundConnectionCaches.values();
   }

   public InboundConnectionCache getInboundConnectionCache(Acceptor var1) {
      synchronized(var1) {
         if (var1.getConnectionCache() == null) {
            Object var3 = null;
            synchronized(this.inboundConnectionCaches) {
               var3 = (InboundConnectionCache)this.inboundConnectionCaches.get(var1.getConnectionCacheType());
               if (var3 == null) {
                  var3 = new CorbaInboundConnectionCacheImpl(this.orb, var1);
                  this.inboundConnectionCaches.put(var1.getConnectionCacheType(), var3);
               }
            }

            var1.setConnectionCache((InboundConnectionCache)var3);
         }

         return var1.getConnectionCache();
      }
   }

   public Collection getInboundConnectionCaches() {
      return this.inboundConnectionCaches.values();
   }

   public Selector getSelector(int var1) {
      return this.selector;
   }

   public synchronized void registerAcceptor(Acceptor var1) {
      if (this.orb.transportDebugFlag) {
         this.dprint(".registerAcceptor->: " + var1);
      }

      this.acceptors.add(var1);
      if (this.orb.transportDebugFlag) {
         this.dprint(".registerAcceptor<-: " + var1);
      }

   }

   public Collection getAcceptors() {
      return this.getAcceptors((String)null, (ObjectAdapterId)null);
   }

   public synchronized void unregisterAcceptor(Acceptor var1) {
      this.acceptors.remove(var1);
   }

   public void close() {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close->");
         }

         Iterator var1 = this.outboundConnectionCaches.values().iterator();

         Object var2;
         while(var1.hasNext()) {
            var2 = var1.next();
            ((ConnectionCache)var2).close();
         }

         var1 = this.inboundConnectionCaches.values().iterator();

         while(var1.hasNext()) {
            var2 = var1.next();
            ((ConnectionCache)var2).close();
            this.unregisterAcceptor(((InboundConnectionCache)var2).getAcceptor());
         }

         this.getSelector(0).close();
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close<-");
         }

      }
   }

   public Collection getAcceptors(String var1, ObjectAdapterId var2) {
      Iterator var3 = this.acceptors.iterator();

      while(var3.hasNext()) {
         Acceptor var4 = (Acceptor)var3.next();
         if (var4.initialize() && var4.shouldRegisterAcceptEvent()) {
            this.orb.getTransportManager().getSelector(0).registerForEvent(var4.getEventHandler());
         }
      }

      return this.acceptors;
   }

   public void addToIORTemplate(IORTemplate var1, Policies var2, String var3, String var4, ObjectAdapterId var5) {
      Iterator var6 = this.getAcceptors(var4, var5).iterator();

      while(var6.hasNext()) {
         CorbaAcceptor var7 = (CorbaAcceptor)var6.next();
         var7.addToIORTemplate(var1, var2, var3);
      }

   }

   protected void dprint(String var1) {
      ORBUtility.dprint("CorbaTransportManagerImpl", var1);
   }
}

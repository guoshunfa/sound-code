package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestDispatcherRegistryImpl implements RequestDispatcherRegistry {
   private ORB orb;
   protected int defaultId;
   private DenseIntMapImpl SDRegistry;
   private DenseIntMapImpl CSRegistry;
   private DenseIntMapImpl OAFRegistry;
   private DenseIntMapImpl LCSFRegistry;
   private Set objectAdapterFactories;
   private Set objectAdapterFactoriesView;
   private Map stringToServerSubcontract;

   public RequestDispatcherRegistryImpl(ORB var1, int var2) {
      this.orb = var1;
      this.defaultId = var2;
      this.SDRegistry = new DenseIntMapImpl();
      this.CSRegistry = new DenseIntMapImpl();
      this.OAFRegistry = new DenseIntMapImpl();
      this.LCSFRegistry = new DenseIntMapImpl();
      this.objectAdapterFactories = new HashSet();
      this.objectAdapterFactoriesView = Collections.unmodifiableSet(this.objectAdapterFactories);
      this.stringToServerSubcontract = new HashMap();
   }

   public synchronized void registerClientRequestDispatcher(ClientRequestDispatcher var1, int var2) {
      this.CSRegistry.set(var2, var1);
   }

   public synchronized void registerLocalClientRequestDispatcherFactory(LocalClientRequestDispatcherFactory var1, int var2) {
      this.LCSFRegistry.set(var2, var1);
   }

   public synchronized void registerServerRequestDispatcher(CorbaServerRequestDispatcher var1, int var2) {
      this.SDRegistry.set(var2, var1);
   }

   public synchronized void registerServerRequestDispatcher(CorbaServerRequestDispatcher var1, String var2) {
      this.stringToServerSubcontract.put(var2, var1);
   }

   public synchronized void registerObjectAdapterFactory(ObjectAdapterFactory var1, int var2) {
      this.objectAdapterFactories.add(var1);
      this.OAFRegistry.set(var2, var1);
   }

   public CorbaServerRequestDispatcher getServerRequestDispatcher(int var1) {
      CorbaServerRequestDispatcher var2 = (CorbaServerRequestDispatcher)((CorbaServerRequestDispatcher)this.SDRegistry.get(var1));
      if (var2 == null) {
         var2 = (CorbaServerRequestDispatcher)((CorbaServerRequestDispatcher)this.SDRegistry.get(this.defaultId));
      }

      return var2;
   }

   public CorbaServerRequestDispatcher getServerRequestDispatcher(String var1) {
      CorbaServerRequestDispatcher var2 = (CorbaServerRequestDispatcher)this.stringToServerSubcontract.get(var1);
      if (var2 == null) {
         var2 = (CorbaServerRequestDispatcher)((CorbaServerRequestDispatcher)this.SDRegistry.get(this.defaultId));
      }

      return var2;
   }

   public LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(int var1) {
      LocalClientRequestDispatcherFactory var2 = (LocalClientRequestDispatcherFactory)((LocalClientRequestDispatcherFactory)this.LCSFRegistry.get(var1));
      if (var2 == null) {
         var2 = (LocalClientRequestDispatcherFactory)((LocalClientRequestDispatcherFactory)this.LCSFRegistry.get(this.defaultId));
      }

      return var2;
   }

   public ClientRequestDispatcher getClientRequestDispatcher(int var1) {
      ClientRequestDispatcher var2 = (ClientRequestDispatcher)((ClientRequestDispatcher)this.CSRegistry.get(var1));
      if (var2 == null) {
         var2 = (ClientRequestDispatcher)((ClientRequestDispatcher)this.CSRegistry.get(this.defaultId));
      }

      return var2;
   }

   public ObjectAdapterFactory getObjectAdapterFactory(int var1) {
      ObjectAdapterFactory var2 = (ObjectAdapterFactory)((ObjectAdapterFactory)this.OAFRegistry.get(var1));
      if (var2 == null) {
         var2 = (ObjectAdapterFactory)((ObjectAdapterFactory)this.OAFRegistry.get(this.defaultId));
      }

      return var2;
   }

   public Set getObjectAdapterFactories() {
      return this.objectAdapterFactoriesView;
   }
}

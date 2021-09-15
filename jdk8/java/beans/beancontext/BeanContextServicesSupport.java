package java.beans.beancontext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TooManyListenersException;

public class BeanContextServicesSupport extends BeanContextSupport implements BeanContextServices {
   private static final long serialVersionUID = -8494482757288719206L;
   protected transient HashMap services;
   protected transient int serializable;
   protected transient BeanContextServicesSupport.BCSSProxyServiceProvider proxy;
   protected transient ArrayList bcsListeners;

   public BeanContextServicesSupport(BeanContextServices var1, Locale var2, boolean var3, boolean var4) {
      super(var1, var2, var3, var4);
      this.serializable = 0;
   }

   public BeanContextServicesSupport(BeanContextServices var1, Locale var2, boolean var3) {
      this(var1, var2, var3, true);
   }

   public BeanContextServicesSupport(BeanContextServices var1, Locale var2) {
      this(var1, var2, false, true);
   }

   public BeanContextServicesSupport(BeanContextServices var1) {
      this(var1, (Locale)null, false, true);
   }

   public BeanContextServicesSupport() {
      this((BeanContextServices)null, (Locale)null, false, true);
   }

   public void initialize() {
      super.initialize();
      this.services = new HashMap(this.serializable + 1);
      this.bcsListeners = new ArrayList(1);
   }

   public BeanContextServices getBeanContextServicesPeer() {
      return (BeanContextServices)this.getBeanContextChildPeer();
   }

   protected BeanContextSupport.BCSChild createBCSChild(Object var1, Object var2) {
      return new BeanContextServicesSupport.BCSSChild(var1, var2);
   }

   protected BeanContextServicesSupport.BCSSServiceProvider createBCSSServiceProvider(Class var1, BeanContextServiceProvider var2) {
      return new BeanContextServicesSupport.BCSSServiceProvider(var1, var2);
   }

   public void addBeanContextServicesListener(BeanContextServicesListener var1) {
      if (var1 == null) {
         throw new NullPointerException("bcsl");
      } else {
         synchronized(this.bcsListeners) {
            if (!this.bcsListeners.contains(var1)) {
               this.bcsListeners.add(var1);
            }
         }
      }
   }

   public void removeBeanContextServicesListener(BeanContextServicesListener var1) {
      if (var1 == null) {
         throw new NullPointerException("bcsl");
      } else {
         synchronized(this.bcsListeners) {
            if (this.bcsListeners.contains(var1)) {
               this.bcsListeners.remove(var1);
            }
         }
      }
   }

   public boolean addService(Class var1, BeanContextServiceProvider var2) {
      return this.addService(var1, var2, true);
   }

   protected boolean addService(Class var1, BeanContextServiceProvider var2, boolean var3) {
      if (var1 == null) {
         throw new NullPointerException("serviceClass");
      } else if (var2 == null) {
         throw new NullPointerException("bcsp");
      } else {
         synchronized(BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(var1)) {
               return false;
            } else {
               this.services.put(var1, this.createBCSSServiceProvider(var1, var2));
               if (var2 instanceof Serializable) {
                  ++this.serializable;
               }

               if (!var3) {
                  return true;
               } else {
                  BeanContextServiceAvailableEvent var5 = new BeanContextServiceAvailableEvent(this.getBeanContextServicesPeer(), var1);
                  this.fireServiceAdded(var5);
                  synchronized(this.children) {
                     Iterator var7 = this.children.keySet().iterator();

                     while(var7.hasNext()) {
                        Object var8 = var7.next();
                        if (var8 instanceof BeanContextServices) {
                           ((BeanContextServicesListener)var8).serviceAvailable(var5);
                        }
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   public void revokeService(Class var1, BeanContextServiceProvider var2, boolean var3) {
      if (var1 == null) {
         throw new NullPointerException("serviceClass");
      } else if (var2 == null) {
         throw new NullPointerException("bcsp");
      } else {
         synchronized(BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(var1)) {
               BeanContextServicesSupport.BCSSServiceProvider var5 = (BeanContextServicesSupport.BCSSServiceProvider)this.services.get(var1);
               if (!var5.getServiceProvider().equals(var2)) {
                  throw new IllegalArgumentException("service provider mismatch");
               } else {
                  this.services.remove(var1);
                  if (var2 instanceof Serializable) {
                     --this.serializable;
                  }

                  Iterator var6 = this.bcsChildren();

                  while(var6.hasNext()) {
                     ((BeanContextServicesSupport.BCSSChild)var6.next()).revokeService(var1, false, var3);
                  }

                  this.fireServiceRevoked(var1, var3);
               }
            }
         }
      }
   }

   public synchronized boolean hasService(Class var1) {
      if (var1 == null) {
         throw new NullPointerException("serviceClass");
      } else {
         synchronized(BeanContext.globalHierarchyLock) {
            if (this.services.containsKey(var1)) {
               return true;
            } else {
               BeanContextServices var3 = null;

               try {
                  var3 = (BeanContextServices)this.getBeanContext();
               } catch (ClassCastException var6) {
                  return false;
               }

               return var3 == null ? false : var3.hasService(var1);
            }
         }
      }
   }

   public Object getService(BeanContextChild var1, Object var2, Class var3, Object var4, BeanContextServiceRevokedListener var5) throws TooManyListenersException {
      if (var1 == null) {
         throw new NullPointerException("child");
      } else if (var3 == null) {
         throw new NullPointerException("serviceClass");
      } else if (var2 == null) {
         throw new NullPointerException("requestor");
      } else if (var5 == null) {
         throw new NullPointerException("bcsrl");
      } else {
         Object var6 = null;
         BeanContextServices var8 = this.getBeanContextServicesPeer();
         synchronized(BeanContext.globalHierarchyLock) {
            BeanContextServicesSupport.BCSSChild var7;
            synchronized(this.children) {
               var7 = (BeanContextServicesSupport.BCSSChild)this.children.get(var1);
            }

            if (var7 == null) {
               throw new IllegalArgumentException("not a child of this context");
            } else {
               BeanContextServicesSupport.BCSSServiceProvider var10 = (BeanContextServicesSupport.BCSSServiceProvider)this.services.get(var3);
               if (var10 != null) {
                  BeanContextServiceProvider var11 = var10.getServiceProvider();
                  var6 = var11.getService(var8, var2, var3, var4);
                  if (var6 != null) {
                     try {
                        var7.usingService(var2, var6, var3, var11, false, var5);
                     } catch (TooManyListenersException var14) {
                        var11.releaseService(var8, var2, var6);
                        throw var14;
                     } catch (UnsupportedOperationException var15) {
                        var11.releaseService(var8, var2, var6);
                        throw var15;
                     }

                     return var6;
                  }
               }

               if (this.proxy != null) {
                  var6 = this.proxy.getService(var8, var2, var3, var4);
                  if (var6 != null) {
                     try {
                        var7.usingService(var2, var6, var3, this.proxy, true, var5);
                     } catch (TooManyListenersException var16) {
                        this.proxy.releaseService(var8, var2, var6);
                        throw var16;
                     } catch (UnsupportedOperationException var17) {
                        this.proxy.releaseService(var8, var2, var6);
                        throw var17;
                     }

                     return var6;
                  }
               }

               return null;
            }
         }
      }
   }

   public void releaseService(BeanContextChild var1, Object var2, Object var3) {
      if (var1 == null) {
         throw new NullPointerException("child");
      } else if (var2 == null) {
         throw new NullPointerException("requestor");
      } else if (var3 == null) {
         throw new NullPointerException("service");
      } else {
         synchronized(BeanContext.globalHierarchyLock) {
            BeanContextServicesSupport.BCSSChild var4;
            synchronized(this.children) {
               var4 = (BeanContextServicesSupport.BCSSChild)this.children.get(var1);
            }

            if (var4 != null) {
               var4.releaseService(var2, var3);
            } else {
               throw new IllegalArgumentException("child actual is not a child of this BeanContext");
            }
         }
      }
   }

   public Iterator getCurrentServiceClasses() {
      return new BeanContextSupport.BCSIterator(this.services.keySet().iterator());
   }

   public Iterator getCurrentServiceSelectors(Class var1) {
      BeanContextServicesSupport.BCSSServiceProvider var2 = (BeanContextServicesSupport.BCSSServiceProvider)this.services.get(var1);
      return var2 != null ? new BeanContextSupport.BCSIterator(var2.getServiceProvider().getCurrentServiceSelectors(this.getBeanContextServicesPeer(), var1)) : null;
   }

   public void serviceAvailable(BeanContextServiceAvailableEvent var1) {
      synchronized(BeanContext.globalHierarchyLock) {
         if (!this.services.containsKey(var1.getServiceClass())) {
            this.fireServiceAdded(var1);
            Iterator var3;
            synchronized(this.children) {
               var3 = this.children.keySet().iterator();
            }

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (var4 instanceof BeanContextServices) {
                  ((BeanContextServicesListener)var4).serviceAvailable(var1);
               }
            }

         }
      }
   }

   public void serviceRevoked(BeanContextServiceRevokedEvent var1) {
      synchronized(BeanContext.globalHierarchyLock) {
         if (!this.services.containsKey(var1.getServiceClass())) {
            this.fireServiceRevoked(var1);
            Iterator var3;
            synchronized(this.children) {
               var3 = this.children.keySet().iterator();
            }

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (var4 instanceof BeanContextServices) {
                  ((BeanContextServicesListener)var4).serviceRevoked(var1);
               }
            }

         }
      }
   }

   protected static final BeanContextServicesListener getChildBeanContextServicesListener(Object var0) {
      try {
         return (BeanContextServicesListener)var0;
      } catch (ClassCastException var2) {
         return null;
      }
   }

   protected void childJustRemovedHook(Object var1, BeanContextSupport.BCSChild var2) {
      BeanContextServicesSupport.BCSSChild var3 = (BeanContextServicesSupport.BCSSChild)var2;
      var3.cleanupReferences();
   }

   protected synchronized void releaseBeanContextResources() {
      super.releaseBeanContextResources();
      Object[] var1;
      synchronized(this.children) {
         if (this.children.isEmpty()) {
            return;
         }

         var1 = this.children.values().toArray();
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         ((BeanContextServicesSupport.BCSSChild)var1[var2]).revokeAllDelegatedServicesNow();
      }

      this.proxy = null;
   }

   protected synchronized void initializeBeanContextResources() {
      super.initializeBeanContextResources();
      BeanContext var1 = this.getBeanContext();
      if (var1 != null) {
         try {
            BeanContextServices var2 = (BeanContextServices)var1;
            this.proxy = new BeanContextServicesSupport.BCSSProxyServiceProvider(var2);
         } catch (ClassCastException var3) {
         }

      }
   }

   protected final void fireServiceAdded(Class var1) {
      BeanContextServiceAvailableEvent var2 = new BeanContextServiceAvailableEvent(this.getBeanContextServicesPeer(), var1);
      this.fireServiceAdded(var2);
   }

   protected final void fireServiceAdded(BeanContextServiceAvailableEvent var1) {
      Object[] var2;
      synchronized(this.bcsListeners) {
         var2 = this.bcsListeners.toArray();
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ((BeanContextServicesListener)var2[var3]).serviceAvailable(var1);
      }

   }

   protected final void fireServiceRevoked(BeanContextServiceRevokedEvent var1) {
      Object[] var2;
      synchronized(this.bcsListeners) {
         var2 = this.bcsListeners.toArray();
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ((BeanContextServiceRevokedListener)var2[var3]).serviceRevoked(var1);
      }

   }

   protected final void fireServiceRevoked(Class var1, boolean var2) {
      BeanContextServiceRevokedEvent var4 = new BeanContextServiceRevokedEvent(this.getBeanContextServicesPeer(), var1, var2);
      Object[] var3;
      synchronized(this.bcsListeners) {
         var3 = this.bcsListeners.toArray();
      }

      for(int var5 = 0; var5 < var3.length; ++var5) {
         ((BeanContextServicesListener)var3[var5]).serviceRevoked(var4);
      }

   }

   protected synchronized void bcsPreSerializationHook(ObjectOutputStream var1) throws IOException {
      var1.writeInt(this.serializable);
      if (this.serializable > 0) {
         int var2 = 0;
         Iterator var3 = this.services.entrySet().iterator();

         while(var3.hasNext() && var2 < this.serializable) {
            Map.Entry var4 = (Map.Entry)var3.next();
            BeanContextServicesSupport.BCSSServiceProvider var5 = null;

            try {
               var5 = (BeanContextServicesSupport.BCSSServiceProvider)var4.getValue();
            } catch (ClassCastException var7) {
               continue;
            }

            if (var5.getServiceProvider() instanceof Serializable) {
               var1.writeObject(var4.getKey());
               var1.writeObject(var5);
               ++var2;
            }
         }

         if (var2 != this.serializable) {
            throw new IOException("wrote different number of service providers than expected");
         }
      }
   }

   protected synchronized void bcsPreDeserializationHook(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.serializable = var1.readInt();

      for(int var2 = this.serializable; var2 > 0; --var2) {
         this.services.put(var1.readObject(), var1.readObject());
      }

   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      this.serialize(var1, this.bcsListeners);
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.deserialize(var1, this.bcsListeners);
   }

   protected class BCSSProxyServiceProvider implements BeanContextServiceProvider, BeanContextServiceRevokedListener {
      private BeanContextServices nestingCtxt;

      BCSSProxyServiceProvider(BeanContextServices var2) {
         this.nestingCtxt = var2;
      }

      public Object getService(BeanContextServices var1, Object var2, Class var3, Object var4) {
         Object var5 = null;

         try {
            var5 = this.nestingCtxt.getService(var1, var2, var3, var4, this);
            return var5;
         } catch (TooManyListenersException var7) {
            return null;
         }
      }

      public void releaseService(BeanContextServices var1, Object var2, Object var3) {
         this.nestingCtxt.releaseService(var1, var2, var3);
      }

      public Iterator getCurrentServiceSelectors(BeanContextServices var1, Class var2) {
         return this.nestingCtxt.getCurrentServiceSelectors(var2);
      }

      public void serviceRevoked(BeanContextServiceRevokedEvent var1) {
         Iterator var2 = BeanContextServicesSupport.this.bcsChildren();

         while(var2.hasNext()) {
            ((BeanContextServicesSupport.BCSSChild)var2.next()).revokeService(var1.getServiceClass(), true, var1.isCurrentServiceInvalidNow());
         }

      }
   }

   protected static class BCSSServiceProvider implements Serializable {
      private static final long serialVersionUID = 861278251667444782L;
      protected BeanContextServiceProvider serviceProvider;

      BCSSServiceProvider(Class var1, BeanContextServiceProvider var2) {
         this.serviceProvider = var2;
      }

      protected BeanContextServiceProvider getServiceProvider() {
         return this.serviceProvider;
      }
   }

   protected class BCSSChild extends BeanContextSupport.BCSChild {
      private static final long serialVersionUID = -3263851306889194873L;
      private transient HashMap serviceClasses;
      private transient HashMap serviceRequestors;

      BCSSChild(Object var2, Object var3) {
         super(var2, var3);
      }

      synchronized void usingService(Object var1, Object var2, Class var3, BeanContextServiceProvider var4, boolean var5, BeanContextServiceRevokedListener var6) throws TooManyListenersException, UnsupportedOperationException {
         BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef var7 = null;
         if (this.serviceClasses == null) {
            this.serviceClasses = new HashMap(1);
         } else {
            var7 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef)this.serviceClasses.get(var3);
         }

         if (var7 == null) {
            var7 = new BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef(var3, var4, var5);
            this.serviceClasses.put(var3, var7);
         } else {
            var7.verifyAndMaybeSetProvider(var4, var5);
            var7.verifyRequestor(var1, var6);
         }

         var7.addRequestor(var1, var6);
         var7.addRef(var5);
         BeanContextServicesSupport.BCSSChild.BCSSCServiceRef var8 = null;
         Object var9 = null;
         if (this.serviceRequestors == null) {
            this.serviceRequestors = new HashMap(1);
         } else {
            var9 = (Map)this.serviceRequestors.get(var1);
         }

         if (var9 == null) {
            var9 = new HashMap(1);
            this.serviceRequestors.put(var1, var9);
         } else {
            var8 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceRef)((Map)var9).get(var2);
         }

         if (var8 == null) {
            var8 = new BeanContextServicesSupport.BCSSChild.BCSSCServiceRef(var7, var5);
            ((Map)var9).put(var2, var8);
         } else {
            var8.addRef();
         }

      }

      synchronized void releaseService(Object var1, Object var2) {
         if (this.serviceRequestors != null) {
            Map var3 = (Map)this.serviceRequestors.get(var1);
            if (var3 != null) {
               BeanContextServicesSupport.BCSSChild.BCSSCServiceRef var4 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceRef)var3.get(var2);
               if (var4 != null) {
                  BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef var5 = var4.getServiceClassRef();
                  boolean var6 = var4.isDelegated();
                  BeanContextServiceProvider var7 = var6 ? var5.getDelegateProvider() : var5.getServiceProvider();
                  var7.releaseService(BeanContextServicesSupport.this.getBeanContextServicesPeer(), var1, var2);
                  var5.releaseRef(var6);
                  var5.removeRequestor(var1);
                  if (var4.release() == 0) {
                     var3.remove(var2);
                     if (var3.isEmpty()) {
                        this.serviceRequestors.remove(var1);
                        var5.removeRequestor(var1);
                     }

                     if (this.serviceRequestors.isEmpty()) {
                        this.serviceRequestors = null;
                     }

                     if (var5.isEmpty()) {
                        this.serviceClasses.remove(var5.getServiceClass());
                     }

                     if (this.serviceClasses.isEmpty()) {
                        this.serviceClasses = null;
                     }
                  }

               }
            }
         }
      }

      synchronized void revokeService(Class var1, boolean var2, boolean var3) {
         if (this.serviceClasses != null) {
            BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef var4 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef)this.serviceClasses.get(var1);
            if (var4 != null) {
               Iterator var5 = var4.cloneOfEntries();
               BeanContextServiceRevokedEvent var6 = new BeanContextServiceRevokedEvent(BeanContextServicesSupport.this.getBeanContextServicesPeer(), var1, var3);

               BeanContextServiceRevokedListener var9;
               for(boolean var7 = false; var5.hasNext() && this.serviceRequestors != null; var9.serviceRevoked(var6)) {
                  Map.Entry var8 = (Map.Entry)var5.next();
                  var9 = (BeanContextServiceRevokedListener)var8.getValue();
                  if (var3) {
                     Object var10 = var8.getKey();
                     Map var11 = (Map)this.serviceRequestors.get(var10);
                     if (var11 != null) {
                        Iterator var12 = var11.entrySet().iterator();

                        while(var12.hasNext()) {
                           Map.Entry var13 = (Map.Entry)var12.next();
                           BeanContextServicesSupport.BCSSChild.BCSSCServiceRef var14 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceRef)var13.getValue();
                           if (var14.getServiceClassRef().equals(var4) && var2 == var14.isDelegated()) {
                              var12.remove();
                           }
                        }

                        if (var7 = var11.isEmpty()) {
                           this.serviceRequestors.remove(var10);
                        }
                     }

                     if (var7) {
                        var4.removeRequestor(var10);
                     }
                  }
               }

               if (var3 && this.serviceClasses != null) {
                  if (var4.isEmpty()) {
                     this.serviceClasses.remove(var1);
                  }

                  if (this.serviceClasses.isEmpty()) {
                     this.serviceClasses = null;
                  }
               }

               if (this.serviceRequestors != null && this.serviceRequestors.isEmpty()) {
                  this.serviceRequestors = null;
               }

            }
         }
      }

      void cleanupReferences() {
         if (this.serviceRequestors != null) {
            Iterator var1 = this.serviceRequestors.entrySet().iterator();

            while(var1.hasNext()) {
               Map.Entry var2 = (Map.Entry)var1.next();
               Object var3 = var2.getKey();
               Iterator var4 = ((Map)var2.getValue()).entrySet().iterator();
               var1.remove();

               while(var4.hasNext()) {
                  Map.Entry var5 = (Map.Entry)var4.next();
                  Object var6 = var5.getKey();
                  BeanContextServicesSupport.BCSSChild.BCSSCServiceRef var7 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceRef)var5.getValue();
                  BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef var8 = var7.getServiceClassRef();
                  BeanContextServiceProvider var9 = var7.isDelegated() ? var8.getDelegateProvider() : var8.getServiceProvider();
                  var8.removeRequestor(var3);
                  var4.remove();

                  while(var7.release() >= 0) {
                     var9.releaseService(BeanContextServicesSupport.this.getBeanContextServicesPeer(), var3, var6);
                  }
               }
            }

            this.serviceRequestors = null;
            this.serviceClasses = null;
         }
      }

      void revokeAllDelegatedServicesNow() {
         if (this.serviceClasses != null) {
            Iterator var1 = (new HashSet(this.serviceClasses.values())).iterator();

            while(true) {
               BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef var2;
               do {
                  if (!var1.hasNext()) {
                     if (this.serviceClasses.isEmpty()) {
                        this.serviceClasses = null;
                     }

                     if (this.serviceRequestors != null && this.serviceRequestors.isEmpty()) {
                        this.serviceRequestors = null;
                     }

                     return;
                  }

                  var2 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef)var1.next();
               } while(!var2.isDelegated());

               Iterator var3 = var2.cloneOfEntries();
               BeanContextServiceRevokedEvent var4 = new BeanContextServiceRevokedEvent(BeanContextServicesSupport.this.getBeanContextServicesPeer(), var2.getServiceClass(), true);
               boolean var5 = false;

               while(var3.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var3.next();
                  BeanContextServiceRevokedListener var7 = (BeanContextServiceRevokedListener)var6.getValue();
                  Object var8 = var6.getKey();
                  Map var9 = (Map)this.serviceRequestors.get(var8);
                  if (var9 != null) {
                     Iterator var10 = var9.entrySet().iterator();

                     while(var10.hasNext()) {
                        Map.Entry var11 = (Map.Entry)var10.next();
                        BeanContextServicesSupport.BCSSChild.BCSSCServiceRef var12 = (BeanContextServicesSupport.BCSSChild.BCSSCServiceRef)var11.getValue();
                        if (var12.getServiceClassRef().equals(var2) && var12.isDelegated()) {
                           var10.remove();
                        }
                     }

                     if (var5 = var9.isEmpty()) {
                        this.serviceRequestors.remove(var8);
                     }
                  }

                  if (var5) {
                     var2.removeRequestor(var8);
                  }

                  var7.serviceRevoked(var4);
                  if (var2.isEmpty()) {
                     this.serviceClasses.remove(var2.getServiceClass());
                  }
               }
            }
         }
      }

      class BCSSCServiceRef {
         BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef serviceClassRef;
         int refCnt = 1;
         boolean delegated = false;

         BCSSCServiceRef(BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef var2, boolean var3) {
            this.serviceClassRef = var2;
            this.delegated = var3;
         }

         void addRef() {
            ++this.refCnt;
         }

         int release() {
            return --this.refCnt;
         }

         BeanContextServicesSupport.BCSSChild.BCSSCServiceClassRef getServiceClassRef() {
            return this.serviceClassRef;
         }

         boolean isDelegated() {
            return this.delegated;
         }
      }

      class BCSSCServiceClassRef {
         Class serviceClass;
         BeanContextServiceProvider serviceProvider;
         int serviceRefs;
         BeanContextServiceProvider delegateProvider;
         int delegateRefs;
         HashMap requestors = new HashMap(1);

         BCSSCServiceClassRef(Class var2, BeanContextServiceProvider var3, boolean var4) {
            this.serviceClass = var2;
            if (var4) {
               this.delegateProvider = var3;
            } else {
               this.serviceProvider = var3;
            }

         }

         void addRequestor(Object var1, BeanContextServiceRevokedListener var2) throws TooManyListenersException {
            BeanContextServiceRevokedListener var3 = (BeanContextServiceRevokedListener)this.requestors.get(var1);
            if (var3 != null && !var3.equals(var2)) {
               throw new TooManyListenersException();
            } else {
               this.requestors.put(var1, var2);
            }
         }

         void removeRequestor(Object var1) {
            this.requestors.remove(var1);
         }

         void verifyRequestor(Object var1, BeanContextServiceRevokedListener var2) throws TooManyListenersException {
            BeanContextServiceRevokedListener var3 = (BeanContextServiceRevokedListener)this.requestors.get(var1);
            if (var3 != null && !var3.equals(var2)) {
               throw new TooManyListenersException();
            }
         }

         void verifyAndMaybeSetProvider(BeanContextServiceProvider var1, boolean var2) {
            BeanContextServiceProvider var3;
            if (var2) {
               var3 = this.delegateProvider;
               if (var3 == null || var1 == null) {
                  this.delegateProvider = var1;
                  return;
               }
            } else {
               var3 = this.serviceProvider;
               if (var3 == null || var1 == null) {
                  this.serviceProvider = var1;
                  return;
               }
            }

            if (!var3.equals(var1)) {
               throw new UnsupportedOperationException("existing service reference obtained from different BeanContextServiceProvider not supported");
            }
         }

         Iterator cloneOfEntries() {
            return ((HashMap)this.requestors.clone()).entrySet().iterator();
         }

         Iterator entries() {
            return this.requestors.entrySet().iterator();
         }

         boolean isEmpty() {
            return this.requestors.isEmpty();
         }

         Class getServiceClass() {
            return this.serviceClass;
         }

         BeanContextServiceProvider getServiceProvider() {
            return this.serviceProvider;
         }

         BeanContextServiceProvider getDelegateProvider() {
            return this.delegateProvider;
         }

         boolean isDelegated() {
            return this.delegateProvider != null;
         }

         void addRef(boolean var1) {
            if (var1) {
               ++this.delegateRefs;
            } else {
               ++this.serviceRefs;
            }

         }

         void releaseRef(boolean var1) {
            if (var1) {
               if (--this.delegateRefs == 0) {
                  this.delegateProvider = null;
               }
            } else if (--this.serviceRefs <= 0) {
               this.serviceProvider = null;
            }

         }

         int getRefs() {
            return this.serviceRefs + this.delegateRefs;
         }

         int getDelegateRefs() {
            return this.delegateRefs;
         }

         int getServiceRefs() {
            return this.serviceRefs;
         }
      }
   }
}

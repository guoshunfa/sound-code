package java.beans.beancontext;

import java.awt.Component;
import java.awt.Container;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class BeanContextSupport extends BeanContextChildSupport implements BeanContext, Serializable, PropertyChangeListener, VetoableChangeListener {
   static final long serialVersionUID = -4879613978649577204L;
   protected transient HashMap children;
   private int serializable;
   protected transient ArrayList bcmListeners;
   protected Locale locale;
   protected boolean okToUseGui;
   protected boolean designTime;
   private transient PropertyChangeListener childPCL;
   private transient VetoableChangeListener childVCL;
   private transient boolean serializing;

   public BeanContextSupport(BeanContext var1, Locale var2, boolean var3, boolean var4) {
      super(var1);
      this.serializable = 0;
      this.locale = var2 != null ? var2 : Locale.getDefault();
      this.designTime = var3;
      this.okToUseGui = var4;
      this.initialize();
   }

   public BeanContextSupport(BeanContext var1, Locale var2, boolean var3) {
      this(var1, var2, var3, true);
   }

   public BeanContextSupport(BeanContext var1, Locale var2) {
      this(var1, var2, false, true);
   }

   public BeanContextSupport(BeanContext var1) {
      this(var1, (Locale)null, false, true);
   }

   public BeanContextSupport() {
      this((BeanContext)null, (Locale)null, false, true);
   }

   public BeanContext getBeanContextPeer() {
      return (BeanContext)this.getBeanContextChildPeer();
   }

   public Object instantiateChild(String var1) throws IOException, ClassNotFoundException {
      BeanContext var2 = this.getBeanContextPeer();
      return Beans.instantiate(var2.getClass().getClassLoader(), var1, var2);
   }

   public int size() {
      synchronized(this.children) {
         return this.children.size();
      }
   }

   public boolean isEmpty() {
      synchronized(this.children) {
         return this.children.isEmpty();
      }
   }

   public boolean contains(Object var1) {
      synchronized(this.children) {
         return this.children.containsKey(var1);
      }
   }

   public boolean containsKey(Object var1) {
      synchronized(this.children) {
         return this.children.containsKey(var1);
      }
   }

   public Iterator iterator() {
      synchronized(this.children) {
         return new BeanContextSupport.BCSIterator(this.children.keySet().iterator());
      }
   }

   public Object[] toArray() {
      synchronized(this.children) {
         return this.children.keySet().toArray();
      }
   }

   public Object[] toArray(Object[] var1) {
      synchronized(this.children) {
         return this.children.keySet().toArray(var1);
      }
   }

   protected BeanContextSupport.BCSChild createBCSChild(Object var1, Object var2) {
      return new BeanContextSupport.BCSChild(var1, var2);
   }

   public boolean add(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else if (this.children.containsKey(var1)) {
         return false;
      } else {
         synchronized(BeanContext.globalHierarchyLock) {
            if (this.children.containsKey(var1)) {
               return false;
            } else if (!this.validatePendingAdd(var1)) {
               throw new IllegalStateException();
            } else {
               BeanContextChild var3 = getChildBeanContextChild(var1);
               BeanContextChild var4 = null;
               synchronized(var1) {
                  if (var1 instanceof BeanContextProxy) {
                     var4 = ((BeanContextProxy)var1).getBeanContextProxy();
                     if (var4 == null) {
                        throw new NullPointerException("BeanContextPeer.getBeanContextProxy()");
                     }
                  }

                  BeanContextSupport.BCSChild var6 = this.createBCSChild(var1, var4);
                  BeanContextSupport.BCSChild var7 = null;
                  synchronized(this.children) {
                     this.children.put(var1, var6);
                     if (var4 != null) {
                        this.children.put(var4, var7 = this.createBCSChild(var4, var1));
                     }
                  }

                  if (var3 != null) {
                     synchronized(var3) {
                        try {
                           var3.setBeanContext(this.getBeanContextPeer());
                        } catch (PropertyVetoException var16) {
                           synchronized(this.children) {
                              this.children.remove(var1);
                              if (var4 != null) {
                                 this.children.remove(var4);
                              }
                           }

                           throw new IllegalStateException();
                        }

                        var3.addPropertyChangeListener("beanContext", this.childPCL);
                        var3.addVetoableChangeListener("beanContext", this.childVCL);
                     }
                  }

                  Visibility var8 = getChildVisibility(var1);
                  if (var8 != null) {
                     if (this.okToUseGui) {
                        var8.okToUseGui();
                     } else {
                        var8.dontUseGui();
                     }
                  }

                  if (getChildSerializable(var1) != null) {
                     ++this.serializable;
                  }

                  this.childJustAddedHook(var1, var6);
                  if (var4 != null) {
                     var8 = getChildVisibility(var4);
                     if (var8 != null) {
                        if (this.okToUseGui) {
                           var8.okToUseGui();
                        } else {
                           var8.dontUseGui();
                        }
                     }

                     if (getChildSerializable(var4) != null) {
                        ++this.serializable;
                     }

                     this.childJustAddedHook(var4, var7);
                  }
               }

               this.fireChildrenAdded(new BeanContextMembershipEvent(this.getBeanContextPeer(), var4 == null ? new Object[]{var1} : new Object[]{var1, var4}));
               return true;
            }
         }
      }
   }

   public boolean remove(Object var1) {
      return this.remove(var1, true);
   }

   protected boolean remove(Object var1, boolean var2) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         synchronized(BeanContext.globalHierarchyLock) {
            if (!this.containsKey(var1)) {
               return false;
            } else if (!this.validatePendingRemove(var1)) {
               throw new IllegalStateException();
            } else {
               BeanContextSupport.BCSChild var4 = (BeanContextSupport.BCSChild)this.children.get(var1);
               BeanContextSupport.BCSChild var5 = null;
               Object var6 = null;
               synchronized(var1) {
                  if (var2) {
                     BeanContextChild var8 = getChildBeanContextChild(var1);
                     if (var8 != null) {
                        synchronized(var8) {
                           var8.removePropertyChangeListener("beanContext", this.childPCL);
                           var8.removeVetoableChangeListener("beanContext", this.childVCL);

                           try {
                              var8.setBeanContext((BeanContext)null);
                           } catch (PropertyVetoException var15) {
                              var8.addPropertyChangeListener("beanContext", this.childPCL);
                              var8.addVetoableChangeListener("beanContext", this.childVCL);
                              throw new IllegalStateException();
                           }
                        }
                     }
                  }

                  synchronized(this.children) {
                     this.children.remove(var1);
                     if (var4.isProxyPeer()) {
                        var5 = (BeanContextSupport.BCSChild)this.children.get(var6 = var4.getProxyPeer());
                        this.children.remove(var6);
                     }
                  }

                  if (getChildSerializable(var1) != null) {
                     --this.serializable;
                  }

                  this.childJustRemovedHook(var1, var4);
                  if (var6 != null) {
                     if (getChildSerializable(var6) != null) {
                        --this.serializable;
                     }

                     this.childJustRemovedHook(var6, var5);
                  }
               }

               this.fireChildrenRemoved(new BeanContextMembershipEvent(this.getBeanContextPeer(), var6 == null ? new Object[]{var1} : new Object[]{var1, var6}));
               return true;
            }
         }
      }
   }

   public boolean containsAll(Collection var1) {
      synchronized(this.children) {
         Iterator var3 = var1.iterator();

         do {
            if (!var3.hasNext()) {
               return true;
            }
         } while(this.contains(var3.next()));

         return false;
      }
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public void addBeanContextMembershipListener(BeanContextMembershipListener var1) {
      if (var1 == null) {
         throw new NullPointerException("listener");
      } else {
         synchronized(this.bcmListeners) {
            if (!this.bcmListeners.contains(var1)) {
               this.bcmListeners.add(var1);
            }
         }
      }
   }

   public void removeBeanContextMembershipListener(BeanContextMembershipListener var1) {
      if (var1 == null) {
         throw new NullPointerException("listener");
      } else {
         synchronized(this.bcmListeners) {
            if (this.bcmListeners.contains(var1)) {
               this.bcmListeners.remove(var1);
            }
         }
      }
   }

   public InputStream getResourceAsStream(String var1, BeanContextChild var2) {
      if (var1 == null) {
         throw new NullPointerException("name");
      } else if (var2 == null) {
         throw new NullPointerException("bcc");
      } else if (this.containsKey(var2)) {
         ClassLoader var3 = var2.getClass().getClassLoader();
         return var3 != null ? var3.getResourceAsStream(var1) : ClassLoader.getSystemResourceAsStream(var1);
      } else {
         throw new IllegalArgumentException("Not a valid child");
      }
   }

   public URL getResource(String var1, BeanContextChild var2) {
      if (var1 == null) {
         throw new NullPointerException("name");
      } else if (var2 == null) {
         throw new NullPointerException("bcc");
      } else if (this.containsKey(var2)) {
         ClassLoader var3 = var2.getClass().getClassLoader();
         return var3 != null ? var3.getResource(var1) : ClassLoader.getSystemResource(var1);
      } else {
         throw new IllegalArgumentException("Not a valid child");
      }
   }

   public synchronized void setDesignTime(boolean var1) {
      if (this.designTime != var1) {
         this.designTime = var1;
         this.firePropertyChange("designMode", !var1, var1);
      }

   }

   public synchronized boolean isDesignTime() {
      return this.designTime;
   }

   public synchronized void setLocale(Locale var1) throws PropertyVetoException {
      if (this.locale != null && !this.locale.equals(var1) && var1 != null) {
         Locale var2 = this.locale;
         this.fireVetoableChange("locale", var2, var1);
         this.locale = var1;
         this.firePropertyChange("locale", var2, var1);
      }

   }

   public synchronized Locale getLocale() {
      return this.locale;
   }

   public synchronized boolean needsGui() {
      BeanContext var1 = this.getBeanContextPeer();
      if (var1 != this) {
         if (var1 instanceof Visibility) {
            return var1.needsGui();
         }

         if (var1 instanceof Container || var1 instanceof Component) {
            return true;
         }
      }

      synchronized(this.children) {
         Iterator var3 = this.children.keySet().iterator();

         while(true) {
            if (var3.hasNext()) {
               Object var4 = var3.next();

               boolean var10000;
               try {
                  var10000 = ((Visibility)var4).needsGui();
               } catch (ClassCastException var7) {
                  if (!(var4 instanceof Container) && !(var4 instanceof Component)) {
                     continue;
                  }

                  return true;
               }

               return var10000;
            }

            return false;
         }
      }
   }

   public synchronized void dontUseGui() {
      if (this.okToUseGui) {
         this.okToUseGui = false;
         synchronized(this.children) {
            Iterator var2 = this.children.keySet().iterator();

            while(var2.hasNext()) {
               Visibility var3 = getChildVisibility(var2.next());
               if (var3 != null) {
                  var3.dontUseGui();
               }
            }
         }
      }

   }

   public synchronized void okToUseGui() {
      if (!this.okToUseGui) {
         this.okToUseGui = true;
         synchronized(this.children) {
            Iterator var2 = this.children.keySet().iterator();

            while(var2.hasNext()) {
               Visibility var3 = getChildVisibility(var2.next());
               if (var3 != null) {
                  var3.okToUseGui();
               }
            }
         }
      }

   }

   public boolean avoidingGui() {
      return !this.okToUseGui && this.needsGui();
   }

   public boolean isSerializing() {
      return this.serializing;
   }

   protected Iterator bcsChildren() {
      synchronized(this.children) {
         return this.children.values().iterator();
      }
   }

   protected void bcsPreSerializationHook(ObjectOutputStream var1) throws IOException {
   }

   protected void bcsPreDeserializationHook(ObjectInputStream var1) throws IOException, ClassNotFoundException {
   }

   protected void childDeserializedHook(Object var1, BeanContextSupport.BCSChild var2) {
      synchronized(this.children) {
         this.children.put(var1, var2);
      }
   }

   protected final void serialize(ObjectOutputStream var1, Collection var2) throws IOException {
      int var3 = 0;
      Object[] var4 = var2.toArray();

      int var5;
      for(var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5] instanceof Serializable) {
            ++var3;
         } else {
            var4[var5] = null;
         }
      }

      var1.writeInt(var3);

      for(var5 = 0; var3 > 0; ++var5) {
         Object var6 = var4[var5];
         if (var6 != null) {
            var1.writeObject(var6);
            --var3;
         }
      }

   }

   protected final void deserialize(ObjectInputStream var1, Collection var2) throws IOException, ClassNotFoundException {
      boolean var3 = false;
      int var4 = var1.readInt();

      while(var4-- > 0) {
         var2.add(var1.readObject());
      }

   }

   public final void writeChildren(ObjectOutputStream var1) throws IOException {
      if (this.serializable > 0) {
         boolean var2 = this.serializing;
         this.serializing = true;
         int var3 = 0;
         synchronized(this.children) {
            Iterator var5 = this.children.entrySet().iterator();

            while(true) {
               if (!var5.hasNext() || var3 >= this.serializable) {
                  break;
               }

               Map.Entry var6 = (Map.Entry)var5.next();
               if (var6.getKey() instanceof Serializable) {
                  try {
                     var1.writeObject(var6.getKey());
                     var1.writeObject(var6.getValue());
                  } catch (IOException var9) {
                     this.serializing = var2;
                     throw var9;
                  }

                  ++var3;
               }
            }
         }

         this.serializing = var2;
         if (var3 != this.serializable) {
            throw new IOException("wrote different number of children than expected");
         }
      }
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      this.serializing = true;
      synchronized(BeanContext.globalHierarchyLock) {
         try {
            var1.defaultWriteObject();
            this.bcsPreSerializationHook(var1);
            if (this.serializable > 0 && this.equals(this.getBeanContextPeer())) {
               this.writeChildren(var1);
            }

            this.serialize(var1, this.bcmListeners);
         } finally {
            this.serializing = false;
         }

      }
   }

   public final void readChildren(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var2 = this.serializable;

      while(var2-- > 0) {
         Object var3 = null;
         BeanContextSupport.BCSChild var4 = null;

         try {
            var3 = var1.readObject();
            var4 = (BeanContextSupport.BCSChild)var1.readObject();
         } catch (IOException var12) {
            continue;
         } catch (ClassNotFoundException var13) {
            continue;
         }

         synchronized(var3) {
            BeanContextChild var6 = null;

            try {
               var6 = (BeanContextChild)var3;
            } catch (ClassCastException var9) {
            }

            if (var6 != null) {
               try {
                  var6.setBeanContext(this.getBeanContextPeer());
                  var6.addPropertyChangeListener("beanContext", this.childPCL);
                  var6.addVetoableChangeListener("beanContext", this.childVCL);
               } catch (PropertyVetoException var10) {
                  continue;
               }
            }

            this.childDeserializedHook(var3, var4);
         }
      }

   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      synchronized(BeanContext.globalHierarchyLock) {
         var1.defaultReadObject();
         this.initialize();
         this.bcsPreDeserializationHook(var1);
         if (this.serializable > 0 && this.equals(this.getBeanContextPeer())) {
            this.readChildren(var1);
         }

         this.deserialize(var1, this.bcmListeners = new ArrayList(1));
      }
   }

   public void vetoableChange(PropertyChangeEvent var1) throws PropertyVetoException {
      String var2 = var1.getPropertyName();
      Object var3 = var1.getSource();
      synchronized(this.children) {
         if ("beanContext".equals(var2) && this.containsKey(var3) && !this.getBeanContextPeer().equals(var1.getNewValue())) {
            if (!this.validatePendingRemove(var3)) {
               throw new PropertyVetoException("current BeanContext vetoes setBeanContext()", var1);
            }

            ((BeanContextSupport.BCSChild)this.children.get(var3)).setRemovePending(true);
         }

      }
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      Object var3 = var1.getSource();
      synchronized(this.children) {
         if ("beanContext".equals(var2) && this.containsKey(var3) && ((BeanContextSupport.BCSChild)this.children.get(var3)).isRemovePending()) {
            BeanContext var5 = this.getBeanContextPeer();
            if (var5.equals(var1.getOldValue()) && !var5.equals(var1.getNewValue())) {
               this.remove(var3, false);
            } else {
               ((BeanContextSupport.BCSChild)this.children.get(var3)).setRemovePending(false);
            }
         }

      }
   }

   protected boolean validatePendingAdd(Object var1) {
      return true;
   }

   protected boolean validatePendingRemove(Object var1) {
      return true;
   }

   protected void childJustAddedHook(Object var1, BeanContextSupport.BCSChild var2) {
   }

   protected void childJustRemovedHook(Object var1, BeanContextSupport.BCSChild var2) {
   }

   protected static final Visibility getChildVisibility(Object var0) {
      try {
         return (Visibility)var0;
      } catch (ClassCastException var2) {
         return null;
      }
   }

   protected static final Serializable getChildSerializable(Object var0) {
      try {
         return (Serializable)var0;
      } catch (ClassCastException var2) {
         return null;
      }
   }

   protected static final PropertyChangeListener getChildPropertyChangeListener(Object var0) {
      try {
         return (PropertyChangeListener)var0;
      } catch (ClassCastException var2) {
         return null;
      }
   }

   protected static final VetoableChangeListener getChildVetoableChangeListener(Object var0) {
      try {
         return (VetoableChangeListener)var0;
      } catch (ClassCastException var2) {
         return null;
      }
   }

   protected static final BeanContextMembershipListener getChildBeanContextMembershipListener(Object var0) {
      try {
         return (BeanContextMembershipListener)var0;
      } catch (ClassCastException var2) {
         return null;
      }
   }

   protected static final BeanContextChild getChildBeanContextChild(Object var0) {
      try {
         BeanContextChild var1 = (BeanContextChild)var0;
         if (var0 instanceof BeanContextChild && var0 instanceof BeanContextProxy) {
            throw new IllegalArgumentException("child cannot implement both BeanContextChild and BeanContextProxy");
         } else {
            return var1;
         }
      } catch (ClassCastException var4) {
         try {
            return ((BeanContextProxy)var0).getBeanContextProxy();
         } catch (ClassCastException var3) {
            return null;
         }
      }
   }

   protected final void fireChildrenAdded(BeanContextMembershipEvent var1) {
      Object[] var2;
      synchronized(this.bcmListeners) {
         var2 = this.bcmListeners.toArray();
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ((BeanContextMembershipListener)var2[var3]).childrenAdded(var1);
      }

   }

   protected final void fireChildrenRemoved(BeanContextMembershipEvent var1) {
      Object[] var2;
      synchronized(this.bcmListeners) {
         var2 = this.bcmListeners.toArray();
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ((BeanContextMembershipListener)var2[var3]).childrenRemoved(var1);
      }

   }

   protected synchronized void initialize() {
      this.children = new HashMap(this.serializable + 1);
      this.bcmListeners = new ArrayList(1);
      this.childPCL = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1) {
            BeanContextSupport.this.propertyChange(var1);
         }
      };
      this.childVCL = new VetoableChangeListener() {
         public void vetoableChange(PropertyChangeEvent var1) throws PropertyVetoException {
            BeanContextSupport.this.vetoableChange(var1);
         }
      };
   }

   protected final Object[] copyChildren() {
      synchronized(this.children) {
         return this.children.keySet().toArray();
      }
   }

   protected static final boolean classEquals(Class var0, Class var1) {
      return var0.equals(var1) || var0.getName().equals(var1.getName());
   }

   protected class BCSChild implements Serializable {
      private static final long serialVersionUID = -5815286101609939109L;
      private Object child;
      private Object proxyPeer;
      private transient boolean removePending;

      BCSChild(Object var2, Object var3) {
         this.child = var2;
         this.proxyPeer = var3;
      }

      Object getChild() {
         return this.child;
      }

      void setRemovePending(boolean var1) {
         this.removePending = var1;
      }

      boolean isRemovePending() {
         return this.removePending;
      }

      boolean isProxyPeer() {
         return this.proxyPeer != null;
      }

      Object getProxyPeer() {
         return this.proxyPeer;
      }
   }

   protected static final class BCSIterator implements Iterator {
      private Iterator src;

      BCSIterator(Iterator var1) {
         this.src = var1;
      }

      public boolean hasNext() {
         return this.src.hasNext();
      }

      public Object next() {
         return this.src.next();
      }

      public void remove() {
      }
   }
}

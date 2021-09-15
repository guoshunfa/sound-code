package java.beans.beancontext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BeanContextChildSupport implements BeanContextChild, BeanContextServicesListener, Serializable {
   static final long serialVersionUID = 6328947014421475877L;
   public BeanContextChild beanContextChildPeer;
   protected PropertyChangeSupport pcSupport;
   protected VetoableChangeSupport vcSupport;
   protected transient BeanContext beanContext;
   protected transient boolean rejectedSetBCOnce;

   public BeanContextChildSupport() {
      this.beanContextChildPeer = this;
      this.pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
      this.vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
   }

   public BeanContextChildSupport(BeanContextChild var1) {
      this.beanContextChildPeer = (BeanContextChild)(var1 != null ? var1 : this);
      this.pcSupport = new PropertyChangeSupport(this.beanContextChildPeer);
      this.vcSupport = new VetoableChangeSupport(this.beanContextChildPeer);
   }

   public synchronized void setBeanContext(BeanContext var1) throws PropertyVetoException {
      if (var1 != this.beanContext) {
         BeanContext var2 = this.beanContext;
         BeanContext var3 = var1;
         if (!this.rejectedSetBCOnce) {
            if (this.rejectedSetBCOnce = !this.validatePendingSetBeanContext(var1)) {
               throw new PropertyVetoException("setBeanContext() change rejected:", new PropertyChangeEvent(this.beanContextChildPeer, "beanContext", var2, var1));
            }

            try {
               this.fireVetoableChange("beanContext", var2, var3);
            } catch (PropertyVetoException var5) {
               this.rejectedSetBCOnce = true;
               throw var5;
            }
         }

         if (this.beanContext != null) {
            this.releaseBeanContextResources();
         }

         this.beanContext = var1;
         this.rejectedSetBCOnce = false;
         this.firePropertyChange("beanContext", var2, var1);
         if (this.beanContext != null) {
            this.initializeBeanContextResources();
         }

      }
   }

   public synchronized BeanContext getBeanContext() {
      return this.beanContext;
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.pcSupport.addPropertyChangeListener(var1, var2);
   }

   public void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      this.pcSupport.removePropertyChangeListener(var1, var2);
   }

   public void addVetoableChangeListener(String var1, VetoableChangeListener var2) {
      this.vcSupport.addVetoableChangeListener(var1, var2);
   }

   public void removeVetoableChangeListener(String var1, VetoableChangeListener var2) {
      this.vcSupport.removeVetoableChangeListener(var1, var2);
   }

   public void serviceRevoked(BeanContextServiceRevokedEvent var1) {
   }

   public void serviceAvailable(BeanContextServiceAvailableEvent var1) {
   }

   public BeanContextChild getBeanContextChildPeer() {
      return this.beanContextChildPeer;
   }

   public boolean isDelegated() {
      return !this.equals(this.beanContextChildPeer);
   }

   public void firePropertyChange(String var1, Object var2, Object var3) {
      this.pcSupport.firePropertyChange(var1, var2, var3);
   }

   public void fireVetoableChange(String var1, Object var2, Object var3) throws PropertyVetoException {
      this.vcSupport.fireVetoableChange(var1, var2, var3);
   }

   public boolean validatePendingSetBeanContext(BeanContext var1) {
      return true;
   }

   protected void releaseBeanContextResources() {
   }

   protected void initializeBeanContextResources() {
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (!this.equals(this.beanContextChildPeer) && !(this.beanContextChildPeer instanceof Serializable)) {
         throw new IOException("BeanContextChildSupport beanContextChildPeer not Serializable");
      } else {
         var1.defaultWriteObject();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
   }
}

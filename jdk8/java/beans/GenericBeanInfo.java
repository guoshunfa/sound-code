package java.beans;

import java.awt.Image;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

class GenericBeanInfo extends SimpleBeanInfo {
   private BeanDescriptor beanDescriptor;
   private EventSetDescriptor[] events;
   private int defaultEvent;
   private PropertyDescriptor[] properties;
   private int defaultProperty;
   private MethodDescriptor[] methods;
   private Reference<BeanInfo> targetBeanInfoRef;

   public GenericBeanInfo(BeanDescriptor var1, EventSetDescriptor[] var2, int var3, PropertyDescriptor[] var4, int var5, MethodDescriptor[] var6, BeanInfo var7) {
      this.beanDescriptor = var1;
      this.events = var2;
      this.defaultEvent = var3;
      this.properties = var4;
      this.defaultProperty = var5;
      this.methods = var6;
      this.targetBeanInfoRef = var7 != null ? new SoftReference(var7) : null;
   }

   GenericBeanInfo(GenericBeanInfo var1) {
      this.beanDescriptor = new BeanDescriptor(var1.beanDescriptor);
      int var2;
      int var3;
      if (var1.events != null) {
         var2 = var1.events.length;
         this.events = new EventSetDescriptor[var2];

         for(var3 = 0; var3 < var2; ++var3) {
            this.events[var3] = new EventSetDescriptor(var1.events[var3]);
         }
      }

      this.defaultEvent = var1.defaultEvent;
      if (var1.properties != null) {
         var2 = var1.properties.length;
         this.properties = new PropertyDescriptor[var2];

         for(var3 = 0; var3 < var2; ++var3) {
            PropertyDescriptor var4 = var1.properties[var3];
            if (var4 instanceof IndexedPropertyDescriptor) {
               this.properties[var3] = new IndexedPropertyDescriptor((IndexedPropertyDescriptor)var4);
            } else {
               this.properties[var3] = new PropertyDescriptor(var4);
            }
         }
      }

      this.defaultProperty = var1.defaultProperty;
      if (var1.methods != null) {
         var2 = var1.methods.length;
         this.methods = new MethodDescriptor[var2];

         for(var3 = 0; var3 < var2; ++var3) {
            this.methods[var3] = new MethodDescriptor(var1.methods[var3]);
         }
      }

      this.targetBeanInfoRef = var1.targetBeanInfoRef;
   }

   public PropertyDescriptor[] getPropertyDescriptors() {
      return this.properties;
   }

   public int getDefaultPropertyIndex() {
      return this.defaultProperty;
   }

   public EventSetDescriptor[] getEventSetDescriptors() {
      return this.events;
   }

   public int getDefaultEventIndex() {
      return this.defaultEvent;
   }

   public MethodDescriptor[] getMethodDescriptors() {
      return this.methods;
   }

   public BeanDescriptor getBeanDescriptor() {
      return this.beanDescriptor;
   }

   public Image getIcon(int var1) {
      BeanInfo var2 = this.getTargetBeanInfo();
      return var2 != null ? var2.getIcon(var1) : super.getIcon(var1);
   }

   private BeanInfo getTargetBeanInfo() {
      if (this.targetBeanInfoRef == null) {
         return null;
      } else {
         BeanInfo var1 = (BeanInfo)this.targetBeanInfoRef.get();
         if (var1 == null) {
            var1 = (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(this.beanDescriptor.getBeanClass());
            if (var1 != null) {
               this.targetBeanInfoRef = new SoftReference(var1);
            }
         }

         return var1;
      }
   }
}

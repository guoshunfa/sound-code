package java.beans;

import java.lang.ref.Reference;

public class BeanDescriptor extends FeatureDescriptor {
   private Reference<? extends Class<?>> beanClassRef;
   private Reference<? extends Class<?>> customizerClassRef;

   public BeanDescriptor(Class<?> var1) {
      this(var1, (Class)null);
   }

   public BeanDescriptor(Class<?> var1, Class<?> var2) {
      this.beanClassRef = getWeakReference(var1);
      this.customizerClassRef = getWeakReference(var2);

      String var3;
      for(var3 = var1.getName(); var3.indexOf(46) >= 0; var3 = var3.substring(var3.indexOf(46) + 1)) {
      }

      this.setName(var3);
   }

   public Class<?> getBeanClass() {
      return this.beanClassRef != null ? (Class)this.beanClassRef.get() : null;
   }

   public Class<?> getCustomizerClass() {
      return this.customizerClassRef != null ? (Class)this.customizerClassRef.get() : null;
   }

   BeanDescriptor(BeanDescriptor var1) {
      super(var1);
      this.beanClassRef = var1.beanClassRef;
      this.customizerClassRef = var1.customizerClassRef;
   }

   void appendTo(StringBuilder var1) {
      appendTo(var1, "beanClass", this.beanClassRef);
      appendTo(var1, "customizerClass", this.customizerClassRef);
   }
}

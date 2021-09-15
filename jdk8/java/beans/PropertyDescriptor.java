package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

public class PropertyDescriptor extends FeatureDescriptor {
   private Reference<? extends Class<?>> propertyTypeRef;
   private final MethodRef readMethodRef;
   private final MethodRef writeMethodRef;
   private Reference<? extends Class<?>> propertyEditorClassRef;
   private boolean bound;
   private boolean constrained;
   private String baseName;
   private String writeMethodName;
   private String readMethodName;

   public PropertyDescriptor(String var1, Class<?> var2) throws IntrospectionException {
      this(var1, var2, "is" + NameGenerator.capitalize(var1), "set" + NameGenerator.capitalize(var1));
   }

   public PropertyDescriptor(String var1, Class<?> var2, String var3, String var4) throws IntrospectionException {
      this.readMethodRef = new MethodRef();
      this.writeMethodRef = new MethodRef();
      if (var2 == null) {
         throw new IntrospectionException("Target Bean class is null");
      } else if (var1 != null && var1.length() != 0) {
         if (!"".equals(var3) && !"".equals(var4)) {
            this.setName(var1);
            this.setClass0(var2);
            this.readMethodName = var3;
            if (var3 != null && this.getReadMethod() == null) {
               throw new IntrospectionException("Method not found: " + var3);
            } else {
               this.writeMethodName = var4;
               if (var4 != null && this.getWriteMethod() == null) {
                  throw new IntrospectionException("Method not found: " + var4);
               } else {
                  Class[] var5 = new Class[]{PropertyChangeListener.class};
                  this.bound = null != Introspector.findMethod(var2, "addPropertyChangeListener", var5.length, var5);
               }
            }
         } else {
            throw new IntrospectionException("read or write method name should not be the empty string");
         }
      } else {
         throw new IntrospectionException("bad property name");
      }
   }

   public PropertyDescriptor(String var1, Method var2, Method var3) throws IntrospectionException {
      this.readMethodRef = new MethodRef();
      this.writeMethodRef = new MethodRef();
      if (var1 != null && var1.length() != 0) {
         this.setName(var1);
         this.setReadMethod(var2);
         this.setWriteMethod(var3);
      } else {
         throw new IntrospectionException("bad property name");
      }
   }

   PropertyDescriptor(Class<?> var1, String var2, Method var3, Method var4) throws IntrospectionException {
      this.readMethodRef = new MethodRef();
      this.writeMethodRef = new MethodRef();
      if (var1 == null) {
         throw new IntrospectionException("Target Bean class is null");
      } else {
         this.setClass0(var1);
         this.setName(Introspector.decapitalize(var2));
         this.setReadMethod(var3);
         this.setWriteMethod(var4);
         this.baseName = var2;
      }
   }

   public synchronized Class<?> getPropertyType() {
      Class var1 = this.getPropertyType0();
      if (var1 == null) {
         try {
            var1 = this.findPropertyType(this.getReadMethod(), this.getWriteMethod());
            this.setPropertyType(var1);
         } catch (IntrospectionException var3) {
         }
      }

      return var1;
   }

   private void setPropertyType(Class<?> var1) {
      this.propertyTypeRef = getWeakReference(var1);
   }

   private Class<?> getPropertyType0() {
      return this.propertyTypeRef != null ? (Class)this.propertyTypeRef.get() : null;
   }

   public synchronized Method getReadMethod() {
      Method var1 = this.readMethodRef.get();
      if (var1 == null) {
         Class var2 = this.getClass0();
         if (var2 == null || this.readMethodName == null && !this.readMethodRef.isSet()) {
            return null;
         }

         String var3 = "get" + this.getBaseName();
         if (this.readMethodName == null) {
            Class var4 = this.getPropertyType0();
            if (var4 != Boolean.TYPE && var4 != null) {
               this.readMethodName = var3;
            } else {
               this.readMethodName = "is" + this.getBaseName();
            }
         }

         var1 = Introspector.findMethod(var2, this.readMethodName, 0);
         if (var1 == null && !this.readMethodName.equals(var3)) {
            this.readMethodName = var3;
            var1 = Introspector.findMethod(var2, this.readMethodName, 0);
         }

         try {
            this.setReadMethod(var1);
         } catch (IntrospectionException var5) {
         }
      }

      return var1;
   }

   public synchronized void setReadMethod(Method var1) throws IntrospectionException {
      this.readMethodRef.set(var1);
      if (var1 == null) {
         this.readMethodName = null;
      } else {
         this.setPropertyType(this.findPropertyType(var1, this.writeMethodRef.get()));
         this.setClass0(var1.getDeclaringClass());
         this.readMethodName = var1.getName();
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   public synchronized Method getWriteMethod() {
      Method var1 = this.writeMethodRef.get();
      if (var1 == null) {
         Class var2 = this.getClass0();
         if (var2 == null || this.writeMethodName == null && !this.writeMethodRef.isSet()) {
            return null;
         }

         Class var3 = this.getPropertyType0();
         if (var3 == null) {
            try {
               var3 = this.findPropertyType(this.getReadMethod(), (Method)null);
               this.setPropertyType(var3);
            } catch (IntrospectionException var7) {
               return null;
            }
         }

         if (this.writeMethodName == null) {
            this.writeMethodName = "set" + this.getBaseName();
         }

         Class[] var4 = var3 == null ? null : new Class[]{var3};
         var1 = Introspector.findMethod(var2, this.writeMethodName, 1, var4);
         if (var1 != null && !var1.getReturnType().equals(Void.TYPE)) {
            var1 = null;
         }

         try {
            this.setWriteMethod(var1);
         } catch (IntrospectionException var6) {
         }
      }

      return var1;
   }

   public synchronized void setWriteMethod(Method var1) throws IntrospectionException {
      this.writeMethodRef.set(var1);
      if (var1 == null) {
         this.writeMethodName = null;
      } else {
         this.setPropertyType(this.findPropertyType(this.getReadMethod(), var1));
         this.setClass0(var1.getDeclaringClass());
         this.writeMethodName = var1.getName();
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   void setClass0(Class<?> var1) {
      if (this.getClass0() == null || !var1.isAssignableFrom(this.getClass0())) {
         super.setClass0(var1);
      }
   }

   public boolean isBound() {
      return this.bound;
   }

   public void setBound(boolean var1) {
      this.bound = var1;
   }

   public boolean isConstrained() {
      return this.constrained;
   }

   public void setConstrained(boolean var1) {
      this.constrained = var1;
   }

   public void setPropertyEditorClass(Class<?> var1) {
      this.propertyEditorClassRef = getWeakReference(var1);
   }

   public Class<?> getPropertyEditorClass() {
      return this.propertyEditorClassRef != null ? (Class)this.propertyEditorClassRef.get() : null;
   }

   public PropertyEditor createPropertyEditor(Object var1) {
      Object var2 = null;
      Class var3 = this.getPropertyEditorClass();
      if (var3 != null && PropertyEditor.class.isAssignableFrom(var3) && ReflectUtil.isPackageAccessible(var3)) {
         Constructor var4 = null;
         if (var1 != null) {
            try {
               var4 = var3.getConstructor(Object.class);
            } catch (Exception var7) {
            }
         }

         try {
            if (var4 == null) {
               var2 = var3.newInstance();
            } else {
               var2 = var4.newInstance(var1);
            }
         } catch (Exception var6) {
         }
      }

      return (PropertyEditor)var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 != null && var1 instanceof PropertyDescriptor) {
            PropertyDescriptor var2 = (PropertyDescriptor)var1;
            Method var3 = var2.getReadMethod();
            Method var4 = var2.getWriteMethod();
            if (!this.compareMethods(this.getReadMethod(), var3)) {
               return false;
            }

            if (!this.compareMethods(this.getWriteMethod(), var4)) {
               return false;
            }

            if (this.getPropertyType() == var2.getPropertyType() && this.getPropertyEditorClass() == var2.getPropertyEditorClass() && this.bound == var2.isBound() && this.constrained == var2.isConstrained() && this.writeMethodName == var2.writeMethodName && this.readMethodName == var2.readMethodName) {
               return true;
            }
         }

         return false;
      }
   }

   boolean compareMethods(Method var1, Method var2) {
      if (var1 == null != (var2 == null)) {
         return false;
      } else {
         return var1 == null || var2 == null || var1.equals(var2);
      }
   }

   PropertyDescriptor(PropertyDescriptor var1, PropertyDescriptor var2) {
      super(var1, var2);
      this.readMethodRef = new MethodRef();
      this.writeMethodRef = new MethodRef();
      if (var2.baseName != null) {
         this.baseName = var2.baseName;
      } else {
         this.baseName = var1.baseName;
      }

      if (var2.readMethodName != null) {
         this.readMethodName = var2.readMethodName;
      } else {
         this.readMethodName = var1.readMethodName;
      }

      if (var2.writeMethodName != null) {
         this.writeMethodName = var2.writeMethodName;
      } else {
         this.writeMethodName = var1.writeMethodName;
      }

      if (var2.propertyTypeRef != null) {
         this.propertyTypeRef = var2.propertyTypeRef;
      } else {
         this.propertyTypeRef = var1.propertyTypeRef;
      }

      Method var3 = var1.getReadMethod();
      Method var4 = var2.getReadMethod();

      try {
         if (this.isAssignable(var3, var4)) {
            this.setReadMethod(var4);
         } else {
            this.setReadMethod(var3);
         }
      } catch (IntrospectionException var10) {
      }

      if (var3 != null && var4 != null && var3.getDeclaringClass() == var4.getDeclaringClass() && getReturnType(this.getClass0(), var3) == Boolean.TYPE && getReturnType(this.getClass0(), var4) == Boolean.TYPE && var3.getName().indexOf("is") == 0 && var4.getName().indexOf("get") == 0) {
         try {
            this.setReadMethod(var3);
         } catch (IntrospectionException var9) {
         }
      }

      Method var5 = var1.getWriteMethod();
      Method var6 = var2.getWriteMethod();

      try {
         if (var6 != null) {
            this.setWriteMethod(var6);
         } else {
            this.setWriteMethod(var5);
         }
      } catch (IntrospectionException var8) {
      }

      if (var2.getPropertyEditorClass() != null) {
         this.setPropertyEditorClass(var2.getPropertyEditorClass());
      } else {
         this.setPropertyEditorClass(var1.getPropertyEditorClass());
      }

      this.bound = var1.bound | var2.bound;
      this.constrained = var1.constrained | var2.constrained;
   }

   PropertyDescriptor(PropertyDescriptor var1) {
      super(var1);
      this.readMethodRef = new MethodRef();
      this.writeMethodRef = new MethodRef();
      this.propertyTypeRef = var1.propertyTypeRef;
      this.readMethodRef.set(var1.readMethodRef.get());
      this.writeMethodRef.set(var1.writeMethodRef.get());
      this.propertyEditorClassRef = var1.propertyEditorClassRef;
      this.writeMethodName = var1.writeMethodName;
      this.readMethodName = var1.readMethodName;
      this.baseName = var1.baseName;
      this.bound = var1.bound;
      this.constrained = var1.constrained;
   }

   void updateGenericsFor(Class<?> var1) {
      this.setClass0(var1);

      try {
         this.setPropertyType(this.findPropertyType(this.readMethodRef.get(), this.writeMethodRef.get()));
      } catch (IntrospectionException var3) {
         this.setPropertyType((Class)null);
      }

   }

   private Class<?> findPropertyType(Method var1, Method var2) throws IntrospectionException {
      Class var3 = null;

      try {
         Class[] var4;
         if (var1 != null) {
            var4 = getParameterTypes(this.getClass0(), var1);
            if (var4.length != 0) {
               throw new IntrospectionException("bad read method arg count: " + var1);
            }

            var3 = getReturnType(this.getClass0(), var1);
            if (var3 == Void.TYPE) {
               throw new IntrospectionException("read method " + var1.getName() + " returns void");
            }
         }

         if (var2 != null) {
            var4 = getParameterTypes(this.getClass0(), var2);
            if (var4.length != 1) {
               throw new IntrospectionException("bad write method arg count: " + var2);
            }

            if (var3 != null && !var4[0].isAssignableFrom(var3)) {
               throw new IntrospectionException("type mismatch between read and write methods");
            }

            var3 = var4[0];
         }

         return var3;
      } catch (IntrospectionException var5) {
         throw var5;
      }
   }

   public int hashCode() {
      byte var1 = 7;
      int var2 = 37 * var1 + (this.getPropertyType() == null ? 0 : this.getPropertyType().hashCode());
      var2 = 37 * var2 + (this.getReadMethod() == null ? 0 : this.getReadMethod().hashCode());
      var2 = 37 * var2 + (this.getWriteMethod() == null ? 0 : this.getWriteMethod().hashCode());
      var2 = 37 * var2 + (this.getPropertyEditorClass() == null ? 0 : this.getPropertyEditorClass().hashCode());
      var2 = 37 * var2 + (this.writeMethodName == null ? 0 : this.writeMethodName.hashCode());
      var2 = 37 * var2 + (this.readMethodName == null ? 0 : this.readMethodName.hashCode());
      var2 = 37 * var2 + this.getName().hashCode();
      var2 = 37 * var2 + (!this.bound ? 0 : 1);
      var2 = 37 * var2 + (!this.constrained ? 0 : 1);
      return var2;
   }

   String getBaseName() {
      if (this.baseName == null) {
         this.baseName = NameGenerator.capitalize(this.getName());
      }

      return this.baseName;
   }

   void appendTo(StringBuilder var1) {
      appendTo(var1, "bound", this.bound);
      appendTo(var1, "constrained", this.constrained);
      appendTo(var1, "propertyEditorClass", this.propertyEditorClassRef);
      appendTo(var1, "propertyType", this.propertyTypeRef);
      appendTo(var1, "readMethod", this.readMethodRef.get());
      appendTo(var1, "writeMethod", this.writeMethodRef.get());
   }

   private boolean isAssignable(Method var1, Method var2) {
      if (var1 == null) {
         return true;
      } else if (var2 == null) {
         return false;
      } else if (!var1.getName().equals(var2.getName())) {
         return true;
      } else {
         Class var3 = var1.getDeclaringClass();
         Class var4 = var2.getDeclaringClass();
         if (!var3.isAssignableFrom(var4)) {
            return false;
         } else {
            var3 = getReturnType(this.getClass0(), var1);
            var4 = getReturnType(this.getClass0(), var2);
            if (!var3.isAssignableFrom(var4)) {
               return false;
            } else {
               Class[] var5 = getParameterTypes(this.getClass0(), var1);
               Class[] var6 = getParameterTypes(this.getClass0(), var2);
               if (var5.length != var6.length) {
                  return true;
               } else {
                  for(int var7 = 0; var7 < var5.length; ++var7) {
                     if (!var5[var7].isAssignableFrom(var6[var7])) {
                        return false;
                     }
                  }

                  return true;
               }
            }
         }
      }
   }
}

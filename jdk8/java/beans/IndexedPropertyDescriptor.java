package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Method;

public class IndexedPropertyDescriptor extends PropertyDescriptor {
   private Reference<? extends Class<?>> indexedPropertyTypeRef;
   private final MethodRef indexedReadMethodRef;
   private final MethodRef indexedWriteMethodRef;
   private String indexedReadMethodName;
   private String indexedWriteMethodName;

   public IndexedPropertyDescriptor(String var1, Class<?> var2) throws IntrospectionException {
      this(var1, var2, "get" + NameGenerator.capitalize(var1), "set" + NameGenerator.capitalize(var1), "get" + NameGenerator.capitalize(var1), "set" + NameGenerator.capitalize(var1));
   }

   public IndexedPropertyDescriptor(String var1, Class<?> var2, String var3, String var4, String var5, String var6) throws IntrospectionException {
      super(var1, var2, var3, var4);
      this.indexedReadMethodRef = new MethodRef();
      this.indexedWriteMethodRef = new MethodRef();
      this.indexedReadMethodName = var5;
      if (var5 != null && this.getIndexedReadMethod() == null) {
         throw new IntrospectionException("Method not found: " + var5);
      } else {
         this.indexedWriteMethodName = var6;
         if (var6 != null && this.getIndexedWriteMethod() == null) {
            throw new IntrospectionException("Method not found: " + var6);
         } else {
            this.findIndexedPropertyType(this.getIndexedReadMethod(), this.getIndexedWriteMethod());
         }
      }
   }

   public IndexedPropertyDescriptor(String var1, Method var2, Method var3, Method var4, Method var5) throws IntrospectionException {
      super(var1, var2, var3);
      this.indexedReadMethodRef = new MethodRef();
      this.indexedWriteMethodRef = new MethodRef();
      this.setIndexedReadMethod0(var4);
      this.setIndexedWriteMethod0(var5);
      this.setIndexedPropertyType(this.findIndexedPropertyType(var4, var5));
   }

   IndexedPropertyDescriptor(Class<?> var1, String var2, Method var3, Method var4, Method var5, Method var6) throws IntrospectionException {
      super(var1, var2, var3, var4);
      this.indexedReadMethodRef = new MethodRef();
      this.indexedWriteMethodRef = new MethodRef();
      this.setIndexedReadMethod0(var5);
      this.setIndexedWriteMethod0(var6);
      this.setIndexedPropertyType(this.findIndexedPropertyType(var5, var6));
   }

   public synchronized Method getIndexedReadMethod() {
      Method var1 = this.indexedReadMethodRef.get();
      if (var1 == null) {
         Class var2 = this.getClass0();
         if (var2 == null || this.indexedReadMethodName == null && !this.indexedReadMethodRef.isSet()) {
            return null;
         }

         String var3 = "get" + this.getBaseName();
         if (this.indexedReadMethodName == null) {
            Class var4 = this.getIndexedPropertyType0();
            if (var4 != Boolean.TYPE && var4 != null) {
               this.indexedReadMethodName = var3;
            } else {
               this.indexedReadMethodName = "is" + this.getBaseName();
            }
         }

         Class[] var5 = new Class[]{Integer.TYPE};
         var1 = Introspector.findMethod(var2, this.indexedReadMethodName, 1, var5);
         if (var1 == null && !this.indexedReadMethodName.equals(var3)) {
            this.indexedReadMethodName = var3;
            var1 = Introspector.findMethod(var2, this.indexedReadMethodName, 1, var5);
         }

         this.setIndexedReadMethod0(var1);
      }

      return var1;
   }

   public synchronized void setIndexedReadMethod(Method var1) throws IntrospectionException {
      this.setIndexedPropertyType(this.findIndexedPropertyType(var1, this.indexedWriteMethodRef.get()));
      this.setIndexedReadMethod0(var1);
   }

   private void setIndexedReadMethod0(Method var1) {
      this.indexedReadMethodRef.set(var1);
      if (var1 == null) {
         this.indexedReadMethodName = null;
      } else {
         this.setClass0(var1.getDeclaringClass());
         this.indexedReadMethodName = var1.getName();
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   public synchronized Method getIndexedWriteMethod() {
      Method var1 = this.indexedWriteMethodRef.get();
      if (var1 == null) {
         Class var2 = this.getClass0();
         if (var2 == null || this.indexedWriteMethodName == null && !this.indexedWriteMethodRef.isSet()) {
            return null;
         }

         Class var3 = this.getIndexedPropertyType0();
         if (var3 == null) {
            try {
               var3 = this.findIndexedPropertyType(this.getIndexedReadMethod(), (Method)null);
               this.setIndexedPropertyType(var3);
            } catch (IntrospectionException var6) {
               Class var5 = this.getPropertyType();
               if (var5.isArray()) {
                  var3 = var5.getComponentType();
               }
            }
         }

         if (this.indexedWriteMethodName == null) {
            this.indexedWriteMethodName = "set" + this.getBaseName();
         }

         Class[] var4 = var3 == null ? null : new Class[]{Integer.TYPE, var3};
         var1 = Introspector.findMethod(var2, this.indexedWriteMethodName, 2, var4);
         if (var1 != null && !var1.getReturnType().equals(Void.TYPE)) {
            var1 = null;
         }

         this.setIndexedWriteMethod0(var1);
      }

      return var1;
   }

   public synchronized void setIndexedWriteMethod(Method var1) throws IntrospectionException {
      Class var2 = this.findIndexedPropertyType(this.getIndexedReadMethod(), var1);
      this.setIndexedPropertyType(var2);
      this.setIndexedWriteMethod0(var1);
   }

   private void setIndexedWriteMethod0(Method var1) {
      this.indexedWriteMethodRef.set(var1);
      if (var1 == null) {
         this.indexedWriteMethodName = null;
      } else {
         this.setClass0(var1.getDeclaringClass());
         this.indexedWriteMethodName = var1.getName();
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   public synchronized Class<?> getIndexedPropertyType() {
      Class var1 = this.getIndexedPropertyType0();
      if (var1 == null) {
         try {
            var1 = this.findIndexedPropertyType(this.getIndexedReadMethod(), this.getIndexedWriteMethod());
            this.setIndexedPropertyType(var1);
         } catch (IntrospectionException var3) {
         }
      }

      return var1;
   }

   private void setIndexedPropertyType(Class<?> var1) {
      this.indexedPropertyTypeRef = getWeakReference(var1);
   }

   private Class<?> getIndexedPropertyType0() {
      return this.indexedPropertyTypeRef != null ? (Class)this.indexedPropertyTypeRef.get() : null;
   }

   private Class<?> findIndexedPropertyType(Method var1, Method var2) throws IntrospectionException {
      Class var3 = null;
      Class[] var4;
      if (var1 != null) {
         var4 = getParameterTypes(this.getClass0(), var1);
         if (var4.length != 1) {
            throw new IntrospectionException("bad indexed read method arg count");
         }

         if (var4[0] != Integer.TYPE) {
            throw new IntrospectionException("non int index to indexed read method");
         }

         var3 = getReturnType(this.getClass0(), var1);
         if (var3 == Void.TYPE) {
            throw new IntrospectionException("indexed read method returns void");
         }
      }

      if (var2 != null) {
         var4 = getParameterTypes(this.getClass0(), var2);
         if (var4.length != 2) {
            throw new IntrospectionException("bad indexed write method arg count");
         }

         if (var4[0] != Integer.TYPE) {
            throw new IntrospectionException("non int index to indexed write method");
         }

         if (var3 != null && !var4[1].isAssignableFrom(var3)) {
            if (!var3.isAssignableFrom(var4[1])) {
               throw new IntrospectionException("type mismatch between indexed read and indexed write methods: " + this.getName());
            }
         } else {
            var3 = var4[1];
         }
      }

      Class var5 = this.getPropertyType();
      if (var5 == null || var5.isArray() && var5.getComponentType() == var3) {
         return var3;
      } else {
         throw new IntrospectionException("type mismatch between indexed and non-indexed methods: " + this.getName());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && var1 instanceof IndexedPropertyDescriptor) {
         IndexedPropertyDescriptor var2 = (IndexedPropertyDescriptor)var1;
         Method var3 = var2.getIndexedReadMethod();
         Method var4 = var2.getIndexedWriteMethod();
         if (!this.compareMethods(this.getIndexedReadMethod(), var3)) {
            return false;
         } else if (!this.compareMethods(this.getIndexedWriteMethod(), var4)) {
            return false;
         } else {
            return this.getIndexedPropertyType() != var2.getIndexedPropertyType() ? false : super.equals(var1);
         }
      } else {
         return false;
      }
   }

   IndexedPropertyDescriptor(PropertyDescriptor var1, PropertyDescriptor var2) {
      super(var1, var2);
      this.indexedReadMethodRef = new MethodRef();
      this.indexedWriteMethodRef = new MethodRef();
      IndexedPropertyDescriptor var3;
      Method var4;
      Method var5;
      if (var1 instanceof IndexedPropertyDescriptor) {
         var3 = (IndexedPropertyDescriptor)var1;

         try {
            var4 = var3.getIndexedReadMethod();
            if (var4 != null) {
               this.setIndexedReadMethod(var4);
            }

            var5 = var3.getIndexedWriteMethod();
            if (var5 != null) {
               this.setIndexedWriteMethod(var5);
            }
         } catch (IntrospectionException var7) {
            throw new AssertionError(var7);
         }
      }

      if (var2 instanceof IndexedPropertyDescriptor) {
         var3 = (IndexedPropertyDescriptor)var2;

         try {
            var4 = var3.getIndexedReadMethod();
            if (var4 != null && var4.getDeclaringClass() == this.getClass0()) {
               this.setIndexedReadMethod(var4);
            }

            var5 = var3.getIndexedWriteMethod();
            if (var5 != null && var5.getDeclaringClass() == this.getClass0()) {
               this.setIndexedWriteMethod(var5);
            }
         } catch (IntrospectionException var6) {
            throw new AssertionError(var6);
         }
      }

   }

   IndexedPropertyDescriptor(IndexedPropertyDescriptor var1) {
      super(var1);
      this.indexedReadMethodRef = new MethodRef();
      this.indexedWriteMethodRef = new MethodRef();
      this.indexedReadMethodRef.set(var1.indexedReadMethodRef.get());
      this.indexedWriteMethodRef.set(var1.indexedWriteMethodRef.get());
      this.indexedPropertyTypeRef = var1.indexedPropertyTypeRef;
      this.indexedWriteMethodName = var1.indexedWriteMethodName;
      this.indexedReadMethodName = var1.indexedReadMethodName;
   }

   void updateGenericsFor(Class<?> var1) {
      super.updateGenericsFor(var1);

      try {
         this.setIndexedPropertyType(this.findIndexedPropertyType(this.indexedReadMethodRef.get(), this.indexedWriteMethodRef.get()));
      } catch (IntrospectionException var3) {
         this.setIndexedPropertyType((Class)null);
      }

   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 37 * var1 + (this.indexedWriteMethodName == null ? 0 : this.indexedWriteMethodName.hashCode());
      var1 = 37 * var1 + (this.indexedReadMethodName == null ? 0 : this.indexedReadMethodName.hashCode());
      var1 = 37 * var1 + (this.getIndexedPropertyType() == null ? 0 : this.getIndexedPropertyType().hashCode());
      return var1;
   }

   void appendTo(StringBuilder var1) {
      super.appendTo(var1);
      appendTo(var1, "indexedPropertyType", this.indexedPropertyTypeRef);
      appendTo(var1, "indexedReadMethod", this.indexedReadMethodRef.get());
      appendTo(var1, "indexedWriteMethod", this.indexedWriteMethodRef.get());
   }
}

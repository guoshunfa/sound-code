package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodDescriptor extends FeatureDescriptor {
   private final MethodRef methodRef;
   private String[] paramNames;
   private List<WeakReference<Class<?>>> params;
   private ParameterDescriptor[] parameterDescriptors;

   public MethodDescriptor(Method var1) {
      this((Method)var1, (ParameterDescriptor[])null);
   }

   public MethodDescriptor(Method var1, ParameterDescriptor[] var2) {
      this.methodRef = new MethodRef();
      this.setName(var1.getName());
      this.setMethod(var1);
      this.parameterDescriptors = var2 != null ? (ParameterDescriptor[])var2.clone() : null;
   }

   public synchronized Method getMethod() {
      Method var1 = this.methodRef.get();
      if (var1 == null) {
         Class var2 = this.getClass0();
         String var3 = this.getName();
         if (var2 != null && var3 != null) {
            Class[] var4 = this.getParams();
            if (var4 == null) {
               for(int var5 = 0; var5 < 3; ++var5) {
                  var1 = Introspector.findMethod(var2, var3, var5, (Class[])null);
                  if (var1 != null) {
                     break;
                  }
               }
            } else {
               var1 = Introspector.findMethod(var2, var3, var4.length, var4);
            }

            this.setMethod(var1);
         }
      }

      return var1;
   }

   private synchronized void setMethod(Method var1) {
      if (var1 != null) {
         if (this.getClass0() == null) {
            this.setClass0(var1.getDeclaringClass());
         }

         this.setParams(getParameterTypes(this.getClass0(), var1));
         this.methodRef.set(var1);
      }
   }

   private synchronized void setParams(Class<?>[] var1) {
      if (var1 != null) {
         this.paramNames = new String[var1.length];
         this.params = new ArrayList(var1.length);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.paramNames[var2] = var1[var2].getName();
            this.params.add(new WeakReference(var1[var2]));
         }

      }
   }

   String[] getParamNames() {
      return this.paramNames;
   }

   private synchronized Class<?>[] getParams() {
      Class[] var1 = new Class[this.params.size()];

      for(int var2 = 0; var2 < this.params.size(); ++var2) {
         Reference var3 = (Reference)this.params.get(var2);
         Class var4 = (Class)var3.get();
         if (var4 == null) {
            return null;
         }

         var1[var2] = var4;
      }

      return var1;
   }

   public ParameterDescriptor[] getParameterDescriptors() {
      return this.parameterDescriptors != null ? (ParameterDescriptor[])this.parameterDescriptors.clone() : null;
   }

   private static Method resolve(Method var0, Method var1) {
      if (var0 == null) {
         return var1;
      } else if (var1 == null) {
         return var0;
      } else {
         return !var0.isSynthetic() && var1.isSynthetic() ? var0 : var1;
      }
   }

   MethodDescriptor(MethodDescriptor var1, MethodDescriptor var2) {
      super(var1, var2);
      this.methodRef = new MethodRef();
      this.methodRef.set(resolve(var1.methodRef.get(), var2.methodRef.get()));
      this.params = var1.params;
      if (var2.params != null) {
         this.params = var2.params;
      }

      this.paramNames = var1.paramNames;
      if (var2.paramNames != null) {
         this.paramNames = var2.paramNames;
      }

      this.parameterDescriptors = var1.parameterDescriptors;
      if (var2.parameterDescriptors != null) {
         this.parameterDescriptors = var2.parameterDescriptors;
      }

   }

   MethodDescriptor(MethodDescriptor var1) {
      super(var1);
      this.methodRef = new MethodRef();
      this.methodRef.set(var1.getMethod());
      this.params = var1.params;
      this.paramNames = var1.paramNames;
      if (var1.parameterDescriptors != null) {
         int var2 = var1.parameterDescriptors.length;
         this.parameterDescriptors = new ParameterDescriptor[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            this.parameterDescriptors[var3] = new ParameterDescriptor(var1.parameterDescriptors[var3]);
         }
      }

   }

   void appendTo(StringBuilder var1) {
      appendTo(var1, "method", this.methodRef.get());
      if (this.parameterDescriptors != null) {
         var1.append("; parameterDescriptors={");
         ParameterDescriptor[] var2 = this.parameterDescriptors;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ParameterDescriptor var5 = var2[var4];
            var1.append((Object)var5).append(", ");
         }

         var1.setLength(var1.length() - 2);
         var1.append("}");
      }

   }
}

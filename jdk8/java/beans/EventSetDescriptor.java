package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EventSetDescriptor extends FeatureDescriptor {
   private MethodDescriptor[] listenerMethodDescriptors;
   private MethodDescriptor addMethodDescriptor;
   private MethodDescriptor removeMethodDescriptor;
   private MethodDescriptor getMethodDescriptor;
   private Reference<Method[]> listenerMethodsRef;
   private Reference<? extends Class<?>> listenerTypeRef;
   private boolean unicast;
   private boolean inDefaultEventSet;

   public EventSetDescriptor(Class<?> var1, String var2, Class<?> var3, String var4) throws IntrospectionException {
      this(var1, var2, var3, new String[]{var4}, "add" + getListenerClassName(var3), "remove" + getListenerClassName(var3), "get" + getListenerClassName(var3) + "s");
      String var5 = NameGenerator.capitalize(var2) + "Event";
      Method[] var6 = this.getListenerMethods();
      if (var6.length > 0) {
         Class[] var7 = getParameterTypes(this.getClass0(), var6[0]);
         if (!"vetoableChange".equals(var2) && !var7[0].getName().endsWith(var5)) {
            throw new IntrospectionException("Method \"" + var4 + "\" should have argument \"" + var5 + "\"");
         }
      }

   }

   private static String getListenerClassName(Class<?> var0) {
      String var1 = var0.getName();
      return var1.substring(var1.lastIndexOf(46) + 1);
   }

   public EventSetDescriptor(Class<?> var1, String var2, Class<?> var3, String[] var4, String var5, String var6) throws IntrospectionException {
      this(var1, var2, var3, var4, var5, var6, (String)null);
   }

   public EventSetDescriptor(Class<?> var1, String var2, Class<?> var3, String[] var4, String var5, String var6, String var7) throws IntrospectionException {
      this.inDefaultEventSet = true;
      if (var1 != null && var2 != null && var3 != null) {
         this.setName(var2);
         this.setClass0(var1);
         this.setListenerType(var3);
         Method[] var8 = new Method[var4.length];

         for(int var9 = 0; var9 < var4.length; ++var9) {
            if (var4[var9] == null) {
               throw new NullPointerException();
            }

            var8[var9] = getMethod(var3, var4[var9], 1);
         }

         this.setListenerMethods(var8);
         this.setAddListenerMethod(getMethod(var1, var5, 1));
         this.setRemoveListenerMethod(getMethod(var1, var6, 1));
         Method var10 = Introspector.findMethod(var1, var7, 0);
         if (var10 != null) {
            this.setGetListenerMethod(var10);
         }

      } else {
         throw new NullPointerException();
      }
   }

   private static Method getMethod(Class<?> var0, String var1, int var2) throws IntrospectionException {
      if (var1 == null) {
         return null;
      } else {
         Method var3 = Introspector.findMethod(var0, var1, var2);
         if (var3 != null && !Modifier.isStatic(var3.getModifiers())) {
            return var3;
         } else {
            throw new IntrospectionException("Method not found: " + var1 + " on class " + var0.getName());
         }
      }
   }

   public EventSetDescriptor(String var1, Class<?> var2, Method[] var3, Method var4, Method var5) throws IntrospectionException {
      this((String)var1, (Class)var2, (Method[])var3, (Method)var4, (Method)var5, (Method)null);
   }

   public EventSetDescriptor(String var1, Class<?> var2, Method[] var3, Method var4, Method var5, Method var6) throws IntrospectionException {
      this.inDefaultEventSet = true;
      this.setName(var1);
      this.setListenerMethods(var3);
      this.setAddListenerMethod(var4);
      this.setRemoveListenerMethod(var5);
      this.setGetListenerMethod(var6);
      this.setListenerType(var2);
   }

   public EventSetDescriptor(String var1, Class<?> var2, MethodDescriptor[] var3, Method var4, Method var5) throws IntrospectionException {
      this.inDefaultEventSet = true;
      this.setName(var1);
      this.listenerMethodDescriptors = var3 != null ? (MethodDescriptor[])var3.clone() : null;
      this.setAddListenerMethod(var4);
      this.setRemoveListenerMethod(var5);
      this.setListenerType(var2);
   }

   public Class<?> getListenerType() {
      return this.listenerTypeRef != null ? (Class)this.listenerTypeRef.get() : null;
   }

   private void setListenerType(Class<?> var1) {
      this.listenerTypeRef = getWeakReference(var1);
   }

   public synchronized Method[] getListenerMethods() {
      Method[] var1 = this.getListenerMethods0();
      if (var1 == null) {
         if (this.listenerMethodDescriptors != null) {
            var1 = new Method[this.listenerMethodDescriptors.length];

            for(int var2 = 0; var2 < var1.length; ++var2) {
               var1[var2] = this.listenerMethodDescriptors[var2].getMethod();
            }
         }

         this.setListenerMethods(var1);
      }

      return var1;
   }

   private void setListenerMethods(Method[] var1) {
      if (var1 != null) {
         if (this.listenerMethodDescriptors == null) {
            this.listenerMethodDescriptors = new MethodDescriptor[var1.length];

            for(int var2 = 0; var2 < var1.length; ++var2) {
               this.listenerMethodDescriptors[var2] = new MethodDescriptor(var1[var2]);
            }
         }

         this.listenerMethodsRef = getSoftReference(var1);
      }
   }

   private Method[] getListenerMethods0() {
      return this.listenerMethodsRef != null ? (Method[])this.listenerMethodsRef.get() : null;
   }

   public synchronized MethodDescriptor[] getListenerMethodDescriptors() {
      return this.listenerMethodDescriptors != null ? (MethodDescriptor[])this.listenerMethodDescriptors.clone() : null;
   }

   public synchronized Method getAddListenerMethod() {
      return getMethod(this.addMethodDescriptor);
   }

   private synchronized void setAddListenerMethod(Method var1) {
      if (var1 != null) {
         if (this.getClass0() == null) {
            this.setClass0(var1.getDeclaringClass());
         }

         this.addMethodDescriptor = new MethodDescriptor(var1);
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   public synchronized Method getRemoveListenerMethod() {
      return getMethod(this.removeMethodDescriptor);
   }

   private synchronized void setRemoveListenerMethod(Method var1) {
      if (var1 != null) {
         if (this.getClass0() == null) {
            this.setClass0(var1.getDeclaringClass());
         }

         this.removeMethodDescriptor = new MethodDescriptor(var1);
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   public synchronized Method getGetListenerMethod() {
      return getMethod(this.getMethodDescriptor);
   }

   private synchronized void setGetListenerMethod(Method var1) {
      if (var1 != null) {
         if (this.getClass0() == null) {
            this.setClass0(var1.getDeclaringClass());
         }

         this.getMethodDescriptor = new MethodDescriptor(var1);
         this.setTransient((Transient)var1.getAnnotation(Transient.class));
      }
   }

   public void setUnicast(boolean var1) {
      this.unicast = var1;
   }

   public boolean isUnicast() {
      return this.unicast;
   }

   public void setInDefaultEventSet(boolean var1) {
      this.inDefaultEventSet = var1;
   }

   public boolean isInDefaultEventSet() {
      return this.inDefaultEventSet;
   }

   EventSetDescriptor(EventSetDescriptor var1, EventSetDescriptor var2) {
      super(var1, var2);
      this.inDefaultEventSet = true;
      this.listenerMethodDescriptors = var1.listenerMethodDescriptors;
      if (var2.listenerMethodDescriptors != null) {
         this.listenerMethodDescriptors = var2.listenerMethodDescriptors;
      }

      this.listenerTypeRef = var1.listenerTypeRef;
      if (var2.listenerTypeRef != null) {
         this.listenerTypeRef = var2.listenerTypeRef;
      }

      this.addMethodDescriptor = var1.addMethodDescriptor;
      if (var2.addMethodDescriptor != null) {
         this.addMethodDescriptor = var2.addMethodDescriptor;
      }

      this.removeMethodDescriptor = var1.removeMethodDescriptor;
      if (var2.removeMethodDescriptor != null) {
         this.removeMethodDescriptor = var2.removeMethodDescriptor;
      }

      this.getMethodDescriptor = var1.getMethodDescriptor;
      if (var2.getMethodDescriptor != null) {
         this.getMethodDescriptor = var2.getMethodDescriptor;
      }

      this.unicast = var2.unicast;
      if (!var1.inDefaultEventSet || !var2.inDefaultEventSet) {
         this.inDefaultEventSet = false;
      }

   }

   EventSetDescriptor(EventSetDescriptor var1) {
      super(var1);
      this.inDefaultEventSet = true;
      if (var1.listenerMethodDescriptors != null) {
         int var2 = var1.listenerMethodDescriptors.length;
         this.listenerMethodDescriptors = new MethodDescriptor[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            this.listenerMethodDescriptors[var3] = new MethodDescriptor(var1.listenerMethodDescriptors[var3]);
         }
      }

      this.listenerTypeRef = var1.listenerTypeRef;
      this.addMethodDescriptor = var1.addMethodDescriptor;
      this.removeMethodDescriptor = var1.removeMethodDescriptor;
      this.getMethodDescriptor = var1.getMethodDescriptor;
      this.unicast = var1.unicast;
      this.inDefaultEventSet = var1.inDefaultEventSet;
   }

   void appendTo(StringBuilder var1) {
      appendTo(var1, "unicast", this.unicast);
      appendTo(var1, "inDefaultEventSet", this.inDefaultEventSet);
      appendTo(var1, "listenerType", this.listenerTypeRef);
      appendTo(var1, "getListenerMethod", getMethod(this.getMethodDescriptor));
      appendTo(var1, "addListenerMethod", getMethod(this.addMethodDescriptor));
      appendTo(var1, "removeListenerMethod", getMethod(this.removeMethodDescriptor));
   }

   private static Method getMethod(MethodDescriptor var0) {
      return var0 != null ? var0.getMethod() : null;
   }
}

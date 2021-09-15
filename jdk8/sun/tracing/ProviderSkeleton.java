package sun.tracing;

import com.sun.tracing.Probe;
import com.sun.tracing.Provider;
import com.sun.tracing.ProviderName;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

public abstract class ProviderSkeleton implements InvocationHandler, Provider {
   protected boolean active = false;
   protected Class<? extends Provider> providerType;
   protected HashMap<Method, ProbeSkeleton> probes;

   protected abstract ProbeSkeleton createProbe(Method var1);

   protected ProviderSkeleton(Class<? extends Provider> var1) {
      this.providerType = var1;
      this.probes = new HashMap();
   }

   public void init() {
      Method[] var1 = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
         public Method[] run() {
            return ProviderSkeleton.this.providerType.getDeclaredMethods();
         }
      });
      Method[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method var5 = var2[var4];
         if (var5.getReturnType() != Void.TYPE) {
            throw new IllegalArgumentException("Return value of method is not void");
         }

         this.probes.put(var5, this.createProbe(var5));
      }

      this.active = true;
   }

   public <T extends Provider> T newProxyInstance() {
      return (Provider)AccessController.doPrivileged(new PrivilegedAction<T>() {
         public T run() {
            return (Provider)Proxy.newProxyInstance(ProviderSkeleton.this.providerType.getClassLoader(), new Class[]{ProviderSkeleton.this.providerType}, ProviderSkeleton.this);
         }
      });
   }

   public Object invoke(Object var1, Method var2, Object[] var3) {
      Class var4 = var2.getDeclaringClass();
      if (var4 != this.providerType) {
         try {
            if (var4 != Provider.class && var4 != Object.class) {
               throw new SecurityException();
            }

            return var2.invoke(this, var3);
         } catch (IllegalAccessException var6) {
            assert false;
         } catch (InvocationTargetException var7) {
            assert false;
         }
      } else {
         this.triggerProbe(var2, var3);
      }

      return null;
   }

   public Probe getProbe(Method var1) {
      return this.active ? (Probe)this.probes.get(var1) : null;
   }

   public void dispose() {
      this.active = false;
      this.probes.clear();
   }

   protected String getProviderName() {
      return getAnnotationString(this.providerType, ProviderName.class, this.providerType.getSimpleName());
   }

   protected static String getAnnotationString(AnnotatedElement var0, Class<? extends Annotation> var1, String var2) {
      String var3 = (String)getAnnotationValue(var0, var1, "value", var2);
      return var3.isEmpty() ? var2 : var3;
   }

   protected static Object getAnnotationValue(AnnotatedElement var0, Class<? extends Annotation> var1, String var2, Object var3) {
      Object var4 = var3;

      try {
         Method var5 = var1.getMethod(var2);
         Annotation var6 = var0.getAnnotation(var1);
         var4 = var5.invoke(var6);
      } catch (NoSuchMethodException var7) {
         assert false;
      } catch (IllegalAccessException var8) {
         assert false;
      } catch (InvocationTargetException var9) {
         assert false;
      } catch (NullPointerException var10) {
         assert false;
      }

      return var4;
   }

   protected void triggerProbe(Method var1, Object[] var2) {
      if (this.active) {
         ProbeSkeleton var3 = (ProbeSkeleton)this.probes.get(var1);
         if (var3 != null) {
            var3.uncheckedTrigger(var2);
         }
      }

   }
}

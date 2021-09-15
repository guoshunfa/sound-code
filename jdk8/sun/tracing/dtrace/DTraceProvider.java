package sun.tracing.dtrace;

import com.sun.tracing.ProbeName;
import com.sun.tracing.Provider;
import com.sun.tracing.dtrace.Attributes;
import com.sun.tracing.dtrace.DependencyClass;
import com.sun.tracing.dtrace.FunctionName;
import com.sun.tracing.dtrace.ModuleName;
import com.sun.tracing.dtrace.StabilityLevel;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import sun.misc.ProxyGenerator;
import sun.tracing.ProbeSkeleton;
import sun.tracing.ProviderSkeleton;

class DTraceProvider extends ProviderSkeleton {
   private Activation activation;
   private Object proxy;
   private static final Class[] constructorParams = new Class[]{InvocationHandler.class};
   private final String proxyClassNamePrefix = "$DTraceTracingProxy";
   static final String DEFAULT_MODULE = "java_tracing";
   static final String DEFAULT_FUNCTION = "unspecified";
   private static long nextUniqueNumber = 0L;

   private static synchronized long getUniqueNumber() {
      return (long)(nextUniqueNumber++);
   }

   protected ProbeSkeleton createProbe(Method var1) {
      return new DTraceProbe(this.proxy, var1);
   }

   DTraceProvider(Class<? extends Provider> var1) {
      super(var1);
   }

   void setProxy(Object var1) {
      this.proxy = var1;
   }

   void setActivation(Activation var1) {
      this.activation = var1;
   }

   public void dispose() {
      if (this.activation != null) {
         this.activation.disposeProvider(this);
         this.activation = null;
      }

      super.dispose();
   }

   public <T extends Provider> T newProxyInstance() {
      long var1 = getUniqueNumber();
      String var3 = "";
      String var4;
      if (!Modifier.isPublic(this.providerType.getModifiers())) {
         var4 = this.providerType.getName();
         int var5 = var4.lastIndexOf(46);
         var3 = var5 == -1 ? "" : var4.substring(0, var5 + 1);
      }

      var4 = var3 + "$DTraceTracingProxy" + var1;
      Class var10 = null;
      byte[] var6 = ProxyGenerator.generateProxyClass(var4, new Class[]{this.providerType});

      try {
         var10 = JVM.defineClass(this.providerType.getClassLoader(), var4, var6, 0, var6.length);
      } catch (ClassFormatError var9) {
         throw new IllegalArgumentException(var9.toString());
      }

      try {
         Constructor var7 = var10.getConstructor(constructorParams);
         return (Provider)var7.newInstance(this);
      } catch (ReflectiveOperationException var8) {
         throw new InternalError(var8.toString(), var8);
      }
   }

   protected void triggerProbe(Method var1, Object[] var2) {
      assert false : "This method should have been overridden by the JVM";

   }

   public String getProviderName() {
      return super.getProviderName();
   }

   String getModuleName() {
      return getAnnotationString(this.providerType, ModuleName.class, "java_tracing");
   }

   static String getProbeName(Method var0) {
      return getAnnotationString(var0, ProbeName.class, var0.getName());
   }

   static String getFunctionName(Method var0) {
      return getAnnotationString(var0, FunctionName.class, "unspecified");
   }

   DTraceProbe[] getProbes() {
      return (DTraceProbe[])this.probes.values().toArray(new DTraceProbe[0]);
   }

   StabilityLevel getNameStabilityFor(Class<? extends Annotation> var1) {
      Attributes var2 = (Attributes)getAnnotationValue(this.providerType, var1, "value", (Object)null);
      return var2 == null ? StabilityLevel.PRIVATE : var2.name();
   }

   StabilityLevel getDataStabilityFor(Class<? extends Annotation> var1) {
      Attributes var2 = (Attributes)getAnnotationValue(this.providerType, var1, "value", (Object)null);
      return var2 == null ? StabilityLevel.PRIVATE : var2.data();
   }

   DependencyClass getDependencyClassFor(Class<? extends Annotation> var1) {
      Attributes var2 = (Attributes)getAnnotationValue(this.providerType, var1, "value", (Object)null);
      return var2 == null ? DependencyClass.UNKNOWN : var2.dependency();
   }
}

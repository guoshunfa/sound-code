package sun.instrument;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

public class InstrumentationImpl implements Instrumentation {
   private final TransformerManager mTransformerManager = new TransformerManager(false);
   private TransformerManager mRetransfomableTransformerManager = null;
   private final long mNativeAgent;
   private final boolean mEnvironmentSupportsRedefineClasses;
   private volatile boolean mEnvironmentSupportsRetransformClassesKnown;
   private volatile boolean mEnvironmentSupportsRetransformClasses;
   private final boolean mEnvironmentSupportsNativeMethodPrefix;

   private InstrumentationImpl(long var1, boolean var3, boolean var4) {
      this.mNativeAgent = var1;
      this.mEnvironmentSupportsRedefineClasses = var3;
      this.mEnvironmentSupportsRetransformClassesKnown = false;
      this.mEnvironmentSupportsRetransformClasses = false;
      this.mEnvironmentSupportsNativeMethodPrefix = var4;
   }

   public void addTransformer(ClassFileTransformer var1) {
      this.addTransformer(var1, false);
   }

   public synchronized void addTransformer(ClassFileTransformer var1, boolean var2) {
      if (var1 == null) {
         throw new NullPointerException("null passed as 'transformer' in addTransformer");
      } else {
         if (var2) {
            if (!this.isRetransformClassesSupported()) {
               throw new UnsupportedOperationException("adding retransformable transformers is not supported in this environment");
            }

            if (this.mRetransfomableTransformerManager == null) {
               this.mRetransfomableTransformerManager = new TransformerManager(true);
            }

            this.mRetransfomableTransformerManager.addTransformer(var1);
            if (this.mRetransfomableTransformerManager.getTransformerCount() == 1) {
               this.setHasRetransformableTransformers(this.mNativeAgent, true);
            }
         } else {
            this.mTransformerManager.addTransformer(var1);
         }

      }
   }

   public synchronized boolean removeTransformer(ClassFileTransformer var1) {
      if (var1 == null) {
         throw new NullPointerException("null passed as 'transformer' in removeTransformer");
      } else {
         TransformerManager var2 = this.findTransformerManager(var1);
         if (var2 != null) {
            var2.removeTransformer(var1);
            if (var2.isRetransformable() && var2.getTransformerCount() == 0) {
               this.setHasRetransformableTransformers(this.mNativeAgent, false);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isModifiableClass(Class<?> var1) {
      if (var1 == null) {
         throw new NullPointerException("null passed as 'theClass' in isModifiableClass");
      } else {
         return this.isModifiableClass0(this.mNativeAgent, var1);
      }
   }

   public boolean isRetransformClassesSupported() {
      if (!this.mEnvironmentSupportsRetransformClassesKnown) {
         this.mEnvironmentSupportsRetransformClasses = this.isRetransformClassesSupported0(this.mNativeAgent);
         this.mEnvironmentSupportsRetransformClassesKnown = true;
      }

      return this.mEnvironmentSupportsRetransformClasses;
   }

   public void retransformClasses(Class<?>... var1) {
      if (!this.isRetransformClassesSupported()) {
         throw new UnsupportedOperationException("retransformClasses is not supported in this environment");
      } else {
         this.retransformClasses0(this.mNativeAgent, var1);
      }
   }

   public boolean isRedefineClassesSupported() {
      return this.mEnvironmentSupportsRedefineClasses;
   }

   public void redefineClasses(ClassDefinition... var1) throws ClassNotFoundException {
      if (!this.isRedefineClassesSupported()) {
         throw new UnsupportedOperationException("redefineClasses is not supported in this environment");
      } else if (var1 == null) {
         throw new NullPointerException("null passed as 'definitions' in redefineClasses");
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] == null) {
               throw new NullPointerException("element of 'definitions' is null in redefineClasses");
            }
         }

         if (var1.length != 0) {
            this.redefineClasses0(this.mNativeAgent, var1);
         }
      }
   }

   public Class[] getAllLoadedClasses() {
      return this.getAllLoadedClasses0(this.mNativeAgent);
   }

   public Class[] getInitiatedClasses(ClassLoader var1) {
      return this.getInitiatedClasses0(this.mNativeAgent, var1);
   }

   public long getObjectSize(Object var1) {
      if (var1 == null) {
         throw new NullPointerException("null passed as 'objectToSize' in getObjectSize");
      } else {
         return this.getObjectSize0(this.mNativeAgent, var1);
      }
   }

   public void appendToBootstrapClassLoaderSearch(JarFile var1) {
      this.appendToClassLoaderSearch0(this.mNativeAgent, var1.getName(), true);
   }

   public void appendToSystemClassLoaderSearch(JarFile var1) {
      this.appendToClassLoaderSearch0(this.mNativeAgent, var1.getName(), false);
   }

   public boolean isNativeMethodPrefixSupported() {
      return this.mEnvironmentSupportsNativeMethodPrefix;
   }

   public synchronized void setNativeMethodPrefix(ClassFileTransformer var1, String var2) {
      if (!this.isNativeMethodPrefixSupported()) {
         throw new UnsupportedOperationException("setNativeMethodPrefix is not supported in this environment");
      } else if (var1 == null) {
         throw new NullPointerException("null passed as 'transformer' in setNativeMethodPrefix");
      } else {
         TransformerManager var3 = this.findTransformerManager(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("transformer not registered in setNativeMethodPrefix");
         } else {
            var3.setNativeMethodPrefix(var1, var2);
            String[] var4 = var3.getNativeMethodPrefixes();
            this.setNativeMethodPrefixes(this.mNativeAgent, var4, var3.isRetransformable());
         }
      }
   }

   private TransformerManager findTransformerManager(ClassFileTransformer var1) {
      if (this.mTransformerManager.includesTransformer(var1)) {
         return this.mTransformerManager;
      } else {
         return this.mRetransfomableTransformerManager != null && this.mRetransfomableTransformerManager.includesTransformer(var1) ? this.mRetransfomableTransformerManager : null;
      }
   }

   private native boolean isModifiableClass0(long var1, Class<?> var3);

   private native boolean isRetransformClassesSupported0(long var1);

   private native void setHasRetransformableTransformers(long var1, boolean var3);

   private native void retransformClasses0(long var1, Class<?>[] var3);

   private native void redefineClasses0(long var1, ClassDefinition[] var3) throws ClassNotFoundException;

   private native Class[] getAllLoadedClasses0(long var1);

   private native Class[] getInitiatedClasses0(long var1, ClassLoader var3);

   private native long getObjectSize0(long var1, Object var3);

   private native void appendToClassLoaderSearch0(long var1, String var3, boolean var4);

   private native void setNativeMethodPrefixes(long var1, String[] var3, boolean var4);

   private static void setAccessible(final AccessibleObject var0, final boolean var1) {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            var0.setAccessible(var1);
            return null;
         }
      });
   }

   private void loadClassAndStartAgent(String var1, String var2, String var3) throws Throwable {
      ClassLoader var4 = ClassLoader.getSystemClassLoader();
      Class var5 = var4.loadClass(var1);
      Method var6 = null;
      NoSuchMethodException var7 = null;
      boolean var8 = false;

      try {
         var6 = var5.getDeclaredMethod(var2, String.class, Instrumentation.class);
         var8 = true;
      } catch (NoSuchMethodException var13) {
         var7 = var13;
      }

      if (var6 == null) {
         try {
            var6 = var5.getDeclaredMethod(var2, String.class);
         } catch (NoSuchMethodException var12) {
         }
      }

      if (var6 == null) {
         try {
            var6 = var5.getMethod(var2, String.class, Instrumentation.class);
            var8 = true;
         } catch (NoSuchMethodException var11) {
         }
      }

      if (var6 == null) {
         try {
            var6 = var5.getMethod(var2, String.class);
         } catch (NoSuchMethodException var10) {
            throw var7;
         }
      }

      setAccessible(var6, true);
      if (var8) {
         var6.invoke((Object)null, var3, this);
      } else {
         var6.invoke((Object)null, var3);
      }

      setAccessible(var6, false);
   }

   private void loadClassAndCallPremain(String var1, String var2) throws Throwable {
      this.loadClassAndStartAgent(var1, "premain", var2);
   }

   private void loadClassAndCallAgentmain(String var1, String var2) throws Throwable {
      this.loadClassAndStartAgent(var1, "agentmain", var2);
   }

   private byte[] transform(ClassLoader var1, String var2, Class<?> var3, ProtectionDomain var4, byte[] var5, boolean var6) {
      TransformerManager var7 = var6 ? this.mRetransfomableTransformerManager : this.mTransformerManager;
      return var7 == null ? null : var7.transform(var1, var2, var3, var4, var5);
   }

   static {
      System.loadLibrary("instrument");
   }
}

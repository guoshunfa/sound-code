package java.lang.instrument;

import java.util.jar.JarFile;

public interface Instrumentation {
   void addTransformer(ClassFileTransformer var1, boolean var2);

   void addTransformer(ClassFileTransformer var1);

   boolean removeTransformer(ClassFileTransformer var1);

   boolean isRetransformClassesSupported();

   void retransformClasses(Class<?>... var1) throws UnmodifiableClassException;

   boolean isRedefineClassesSupported();

   void redefineClasses(ClassDefinition... var1) throws ClassNotFoundException, UnmodifiableClassException;

   boolean isModifiableClass(Class<?> var1);

   Class[] getAllLoadedClasses();

   Class[] getInitiatedClasses(ClassLoader var1);

   long getObjectSize(Object var1);

   void appendToBootstrapClassLoaderSearch(JarFile var1);

   void appendToSystemClassLoaderSearch(JarFile var1);

   boolean isNativeMethodPrefixSupported();

   void setNativeMethodPrefix(ClassFileTransformer var1, String var2);
}

package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface LangReflectAccess {
   Field newField(Class<?> var1, String var2, Class<?> var3, int var4, int var5, String var6, byte[] var7);

   Method newMethod(Class<?> var1, String var2, Class<?>[] var3, Class<?> var4, Class<?>[] var5, int var6, int var7, String var8, byte[] var9, byte[] var10, byte[] var11);

   <T> Constructor<T> newConstructor(Class<T> var1, Class<?>[] var2, Class<?>[] var3, int var4, int var5, String var6, byte[] var7, byte[] var8);

   MethodAccessor getMethodAccessor(Method var1);

   void setMethodAccessor(Method var1, MethodAccessor var2);

   ConstructorAccessor getConstructorAccessor(Constructor<?> var1);

   void setConstructorAccessor(Constructor<?> var1, ConstructorAccessor var2);

   byte[] getExecutableTypeAnnotationBytes(Executable var1);

   int getConstructorSlot(Constructor<?> var1);

   String getConstructorSignature(Constructor<?> var1);

   byte[] getConstructorAnnotations(Constructor<?> var1);

   byte[] getConstructorParameterAnnotations(Constructor<?> var1);

   Method copyMethod(Method var1);

   Field copyField(Field var1);

   <T> Constructor<T> copyConstructor(Constructor<T> var1);
}

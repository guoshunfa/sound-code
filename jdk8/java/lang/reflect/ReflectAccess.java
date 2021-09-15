package java.lang.reflect;

import sun.reflect.ConstructorAccessor;
import sun.reflect.LangReflectAccess;
import sun.reflect.MethodAccessor;

class ReflectAccess implements LangReflectAccess {
   public Field newField(Class<?> var1, String var2, Class<?> var3, int var4, int var5, String var6, byte[] var7) {
      return new Field(var1, var2, var3, var4, var5, var6, var7);
   }

   public Method newMethod(Class<?> var1, String var2, Class<?>[] var3, Class<?> var4, Class<?>[] var5, int var6, int var7, String var8, byte[] var9, byte[] var10, byte[] var11) {
      return new Method(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public <T> Constructor<T> newConstructor(Class<T> var1, Class<?>[] var2, Class<?>[] var3, int var4, int var5, String var6, byte[] var7, byte[] var8) {
      return new Constructor(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public MethodAccessor getMethodAccessor(Method var1) {
      return var1.getMethodAccessor();
   }

   public void setMethodAccessor(Method var1, MethodAccessor var2) {
      var1.setMethodAccessor(var2);
   }

   public ConstructorAccessor getConstructorAccessor(Constructor<?> var1) {
      return var1.getConstructorAccessor();
   }

   public void setConstructorAccessor(Constructor<?> var1, ConstructorAccessor var2) {
      var1.setConstructorAccessor(var2);
   }

   public int getConstructorSlot(Constructor<?> var1) {
      return var1.getSlot();
   }

   public String getConstructorSignature(Constructor<?> var1) {
      return var1.getSignature();
   }

   public byte[] getConstructorAnnotations(Constructor<?> var1) {
      return var1.getRawAnnotations();
   }

   public byte[] getConstructorParameterAnnotations(Constructor<?> var1) {
      return var1.getRawParameterAnnotations();
   }

   public byte[] getExecutableTypeAnnotationBytes(Executable var1) {
      return var1.getTypeAnnotationBytes();
   }

   public Method copyMethod(Method var1) {
      return var1.copy();
   }

   public Field copyField(Field var1) {
      return var1.copy();
   }

   public <T> Constructor<T> copyConstructor(Constructor<T> var1) {
      return var1.copy();
   }
}

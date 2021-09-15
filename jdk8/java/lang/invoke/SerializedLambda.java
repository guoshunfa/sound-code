package java.lang.invoke;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;

public final class SerializedLambda implements Serializable {
   private static final long serialVersionUID = 8025925345765570181L;
   private final Class<?> capturingClass;
   private final String functionalInterfaceClass;
   private final String functionalInterfaceMethodName;
   private final String functionalInterfaceMethodSignature;
   private final String implClass;
   private final String implMethodName;
   private final String implMethodSignature;
   private final int implMethodKind;
   private final String instantiatedMethodType;
   private final Object[] capturedArgs;

   public SerializedLambda(Class<?> var1, String var2, String var3, String var4, int var5, String var6, String var7, String var8, String var9, Object[] var10) {
      this.capturingClass = var1;
      this.functionalInterfaceClass = var2;
      this.functionalInterfaceMethodName = var3;
      this.functionalInterfaceMethodSignature = var4;
      this.implMethodKind = var5;
      this.implClass = var6;
      this.implMethodName = var7;
      this.implMethodSignature = var8;
      this.instantiatedMethodType = var9;
      this.capturedArgs = (Object[])((Object[])Objects.requireNonNull(var10)).clone();
   }

   public String getCapturingClass() {
      return this.capturingClass.getName().replace('.', '/');
   }

   public String getFunctionalInterfaceClass() {
      return this.functionalInterfaceClass;
   }

   public String getFunctionalInterfaceMethodName() {
      return this.functionalInterfaceMethodName;
   }

   public String getFunctionalInterfaceMethodSignature() {
      return this.functionalInterfaceMethodSignature;
   }

   public String getImplClass() {
      return this.implClass;
   }

   public String getImplMethodName() {
      return this.implMethodName;
   }

   public String getImplMethodSignature() {
      return this.implMethodSignature;
   }

   public int getImplMethodKind() {
      return this.implMethodKind;
   }

   public final String getInstantiatedMethodType() {
      return this.instantiatedMethodType;
   }

   public int getCapturedArgCount() {
      return this.capturedArgs.length;
   }

   public Object getCapturedArg(int var1) {
      return this.capturedArgs[var1];
   }

   private Object readResolve() throws ReflectiveOperationException {
      try {
         Method var1 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               Method var1 = SerializedLambda.this.capturingClass.getDeclaredMethod("$deserializeLambda$", SerializedLambda.class);
               var1.setAccessible(true);
               return var1;
            }
         });
         return var1.invoke((Object)null, this);
      } catch (PrivilegedActionException var3) {
         Exception var2 = var3.getException();
         if (var2 instanceof ReflectiveOperationException) {
            throw (ReflectiveOperationException)var2;
         } else if (var2 instanceof RuntimeException) {
            throw (RuntimeException)var2;
         } else {
            throw new RuntimeException("Exception in SerializedLambda.readResolve", var3);
         }
      }
   }

   public String toString() {
      String var1 = MethodHandleInfo.referenceKindToString(this.implMethodKind);
      return String.format("SerializedLambda[%s=%s, %s=%s.%s:%s, %s=%s %s.%s:%s, %s=%s, %s=%d]", "capturingClass", this.capturingClass, "functionalInterfaceMethod", this.functionalInterfaceClass, this.functionalInterfaceMethodName, this.functionalInterfaceMethodSignature, "implementation", var1, this.implClass, this.implMethodName, this.implMethodSignature, "instantiatedMethodType", this.instantiatedMethodType, "numCaptured", this.capturedArgs.length);
   }
}

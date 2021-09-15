package java.lang.invoke;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

public interface MethodHandleInfo {
   int REF_getField = 1;
   int REF_getStatic = 2;
   int REF_putField = 3;
   int REF_putStatic = 4;
   int REF_invokeVirtual = 5;
   int REF_invokeStatic = 6;
   int REF_invokeSpecial = 7;
   int REF_newInvokeSpecial = 8;
   int REF_invokeInterface = 9;

   int getReferenceKind();

   Class<?> getDeclaringClass();

   String getName();

   MethodType getMethodType();

   <T extends Member> T reflectAs(Class<T> var1, MethodHandles.Lookup var2);

   int getModifiers();

   default boolean isVarArgs() {
      return MethodHandleNatives.refKindIsField((byte)this.getReferenceKind()) ? false : Modifier.isTransient(this.getModifiers());
   }

   static String referenceKindToString(int var0) {
      if (!MethodHandleNatives.refKindIsValid(var0)) {
         throw MethodHandleStatics.newIllegalArgumentException("invalid reference kind", var0);
      } else {
         return MethodHandleNatives.refKindName((byte)var0);
      }
   }

   static String toString(int var0, Class<?> var1, String var2, MethodType var3) {
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      return String.format("%s %s.%s:%s", referenceKindToString(var0), var1.getName(), var2, var3);
   }
}

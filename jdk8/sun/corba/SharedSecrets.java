package sun.corba;

import com.sun.corba.se.impl.io.ValueUtility;
import java.lang.reflect.Method;
import sun.misc.JavaOISAccess;
import sun.misc.Unsafe;

public class SharedSecrets {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static JavaCorbaAccess javaCorbaAccess;
   private static final Method getJavaOISAccessMethod;
   private static JavaOISAccess javaOISAccess;

   public static JavaOISAccess getJavaOISAccess() {
      if (javaOISAccess == null) {
         try {
            javaOISAccess = (JavaOISAccess)getJavaOISAccessMethod.invoke((Object)null);
         } catch (Exception var1) {
            throw new ExceptionInInitializerError(var1);
         }
      }

      return javaOISAccess;
   }

   public static JavaCorbaAccess getJavaCorbaAccess() {
      if (javaCorbaAccess == null) {
         unsafe.ensureClassInitialized(ValueUtility.class);
      }

      return javaCorbaAccess;
   }

   public static void setJavaCorbaAccess(JavaCorbaAccess var0) {
      javaCorbaAccess = var0;
   }

   static {
      try {
         Class var0 = Class.forName("sun.misc.SharedSecrets");
         getJavaOISAccessMethod = var0.getMethod("getJavaOISAccess");
      } catch (Exception var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}

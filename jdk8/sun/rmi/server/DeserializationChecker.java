package sun.rmi.server;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;

public interface DeserializationChecker {
   void check(Method var1, ObjectStreamClass var2, int var3, int var4);

   void checkProxyClass(Method var1, String[] var2, int var3, int var4);

   default void end(int var1) {
   }
}

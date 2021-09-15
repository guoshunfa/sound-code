package java.lang.instrument;

import java.security.ProtectionDomain;

public interface ClassFileTransformer {
   byte[] transform(ClassLoader var1, String var2, Class<?> var3, ProtectionDomain var4, byte[] var5) throws IllegalClassFormatException;
}

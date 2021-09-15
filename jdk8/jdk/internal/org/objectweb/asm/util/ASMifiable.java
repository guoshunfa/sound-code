package jdk.internal.org.objectweb.asm.util;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;

public interface ASMifiable {
   void asmify(StringBuffer var1, String var2, Map<Label, String> var3);
}

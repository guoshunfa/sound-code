package jdk.internal.org.objectweb.asm.util;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;

public interface Textifiable {
   void textify(StringBuffer var1, Map<Label, String> var2);
}

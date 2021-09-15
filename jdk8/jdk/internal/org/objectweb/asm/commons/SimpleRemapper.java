package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;

public class SimpleRemapper extends Remapper {
   private final Map<String, String> mapping;

   public SimpleRemapper(Map<String, String> var1) {
      this.mapping = var1;
   }

   public SimpleRemapper(String var1, String var2) {
      this.mapping = Collections.singletonMap(var1, var2);
   }

   public String mapMethodName(String var1, String var2, String var3) {
      String var4 = this.map(var1 + '.' + var2 + var3);
      return var4 == null ? var2 : var4;
   }

   public String mapFieldName(String var1, String var2, String var3) {
      String var4 = this.map(var1 + '.' + var2);
      return var4 == null ? var2 : var4;
   }

   public String map(String var1) {
      return (String)this.mapping.get(var1);
   }
}

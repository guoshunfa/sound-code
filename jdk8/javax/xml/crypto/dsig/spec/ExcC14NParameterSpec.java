package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExcC14NParameterSpec implements C14NMethodParameterSpec {
   private List<String> preList;
   public static final String DEFAULT = "#default";

   public ExcC14NParameterSpec() {
      this.preList = Collections.emptyList();
   }

   public ExcC14NParameterSpec(List var1) {
      if (var1 == null) {
         throw new NullPointerException("prefixList cannot be null");
      } else {
         ArrayList var2 = new ArrayList(var1);
         int var3 = 0;

         for(int var4 = var2.size(); var3 < var4; ++var3) {
            if (!(var2.get(var3) instanceof String)) {
               throw new ClassCastException("not a String");
            }
         }

         this.preList = Collections.unmodifiableList(var2);
      }
   }

   public List getPrefixList() {
      return this.preList;
   }
}

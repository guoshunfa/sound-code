package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XPathFilter2ParameterSpec implements TransformParameterSpec {
   private final List<XPathType> xPathList;

   public XPathFilter2ParameterSpec(List var1) {
      if (var1 == null) {
         throw new NullPointerException("xPathList cannot be null");
      } else {
         ArrayList var2 = new ArrayList(var1);
         if (var2.isEmpty()) {
            throw new IllegalArgumentException("xPathList cannot be empty");
         } else {
            int var3 = var2.size();

            for(int var4 = 0; var4 < var3; ++var4) {
               if (!(var2.get(var4) instanceof XPathType)) {
                  throw new ClassCastException("xPathList[" + var4 + "] is not a valid type");
               }
            }

            this.xPathList = Collections.unmodifiableList(var2);
         }
      }
   }

   public List getXPathList() {
      return this.xPathList;
   }
}

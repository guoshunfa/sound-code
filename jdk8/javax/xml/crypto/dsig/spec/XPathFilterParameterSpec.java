package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class XPathFilterParameterSpec implements TransformParameterSpec {
   private String xPath;
   private Map<String, String> nsMap;

   public XPathFilterParameterSpec(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.xPath = var1;
         this.nsMap = Collections.emptyMap();
      }
   }

   public XPathFilterParameterSpec(String var1, Map var2) {
      if (var1 != null && var2 != null) {
         this.xPath = var1;
         HashMap var3 = new HashMap(var2);
         Iterator var4 = var3.entrySet().iterator();

         Map.Entry var5;
         do {
            if (!var4.hasNext()) {
               this.nsMap = Collections.unmodifiableMap(var3);
               return;
            }

            var5 = (Map.Entry)var4.next();
         } while(var5.getKey() instanceof String && var5.getValue() instanceof String);

         throw new ClassCastException("not a String");
      } else {
         throw new NullPointerException();
      }
   }

   public String getXPath() {
      return this.xPath;
   }

   public Map getNamespaceMap() {
      return this.nsMap;
   }
}

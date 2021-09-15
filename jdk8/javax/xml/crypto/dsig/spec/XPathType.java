package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XPathType {
   private final String expression;
   private final XPathType.Filter filter;
   private Map<String, String> nsMap;

   public XPathType(String var1, XPathType.Filter var2) {
      if (var1 == null) {
         throw new NullPointerException("expression cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("filter cannot be null");
      } else {
         this.expression = var1;
         this.filter = var2;
         this.nsMap = Collections.emptyMap();
      }
   }

   public XPathType(String var1, XPathType.Filter var2, Map var3) {
      this(var1, var2);
      if (var3 == null) {
         throw new NullPointerException("namespaceMap cannot be null");
      } else {
         HashMap var4 = new HashMap(var3);
         Iterator var5 = var4.entrySet().iterator();

         Map.Entry var6;
         do {
            if (!var5.hasNext()) {
               this.nsMap = Collections.unmodifiableMap(var4);
               return;
            }

            var6 = (Map.Entry)var5.next();
         } while(var6.getKey() instanceof String && var6.getValue() instanceof String);

         throw new ClassCastException("not a String");
      }
   }

   public String getExpression() {
      return this.expression;
   }

   public XPathType.Filter getFilter() {
      return this.filter;
   }

   public Map getNamespaceMap() {
      return this.nsMap;
   }

   public static class Filter {
      private final String operation;
      public static final XPathType.Filter INTERSECT = new XPathType.Filter("intersect");
      public static final XPathType.Filter SUBTRACT = new XPathType.Filter("subtract");
      public static final XPathType.Filter UNION = new XPathType.Filter("union");

      private Filter(String var1) {
         this.operation = var1;
      }

      public String toString() {
         return this.operation;
      }
   }
}

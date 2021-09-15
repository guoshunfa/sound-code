package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.ParserAction;
import com.sun.corba.se.impl.orb.ParserActionFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyParser {
   private List actions = new LinkedList();

   public PropertyParser add(String var1, Operation var2, String var3) {
      this.actions.add(ParserActionFactory.makeNormalAction(var1, var2, var3));
      return this;
   }

   public PropertyParser addPrefix(String var1, Operation var2, String var3, Class var4) {
      this.actions.add(ParserActionFactory.makePrefixAction(var1, var2, var3, var4));
      return this;
   }

   public Map parse(Properties var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = this.actions.iterator();

      while(var3.hasNext()) {
         ParserAction var4 = (ParserAction)((ParserAction)var3.next());
         Object var5 = var4.apply(var1);
         if (var5 != null) {
            var2.put(var4.getFieldName(), var5);
         }
      }

      return var2;
   }

   public Iterator iterator() {
      return this.actions.iterator();
   }
}

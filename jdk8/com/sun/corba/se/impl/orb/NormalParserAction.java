package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import java.util.Properties;

public class NormalParserAction extends ParserActionBase {
   public NormalParserAction(String var1, Operation var2, String var3) {
      super(var1, false, var2, var3);
   }

   public Object apply(Properties var1) {
      String var2 = var1.getProperty(this.getPropertyName());
      return var2 != null ? this.getOperation().operate(var2) : null;
   }
}

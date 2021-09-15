package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.PropertyParser;
import java.util.Properties;

public class NormalParserData extends ParserDataBase {
   private String testData;

   public NormalParserData(String var1, Operation var2, String var3, Object var4, Object var5, String var6) {
      super(var1, var2, var3, var4, var5);
      this.testData = var6;
   }

   public void addToParser(PropertyParser var1) {
      var1.add(this.getPropertyName(), this.getOperation(), this.getFieldName());
   }

   public void addToProperties(Properties var1) {
      var1.setProperty(this.getPropertyName(), this.testData);
   }
}

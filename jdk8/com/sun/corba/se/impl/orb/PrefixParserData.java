package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.StringPair;
import java.util.Properties;

public class PrefixParserData extends ParserDataBase {
   private StringPair[] testData;
   private Class componentType;

   public PrefixParserData(String var1, Operation var2, String var3, Object var4, Object var5, StringPair[] var6, Class var7) {
      super(var1, var2, var3, var4, var5);
      this.testData = var6;
      this.componentType = var7;
   }

   public void addToParser(PropertyParser var1) {
      var1.addPrefix(this.getPropertyName(), this.getOperation(), this.getFieldName(), this.componentType);
   }

   public void addToProperties(Properties var1) {
      for(int var2 = 0; var2 < this.testData.length; ++var2) {
         StringPair var3 = this.testData[var2];
         String var4 = this.getPropertyName();
         if (var4.charAt(var4.length() - 1) != '.') {
            var4 = var4 + ".";
         }

         var1.setProperty(var4 + var3.getFirst(), var3.getSecond());
      }

   }
}

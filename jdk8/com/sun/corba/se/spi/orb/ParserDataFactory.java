package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.NormalParserData;
import com.sun.corba.se.impl.orb.PrefixParserData;

public class ParserDataFactory {
   public static ParserData make(String var0, Operation var1, String var2, Object var3, Object var4, String var5) {
      return new NormalParserData(var0, var1, var2, var3, var4, var5);
   }

   public static ParserData make(String var0, Operation var1, String var2, Object var3, Object var4, StringPair[] var5, Class var6) {
      return new PrefixParserData(var0, var1, var2, var3, var4, var5, var6);
   }
}

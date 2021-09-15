package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;

public class ParserActionFactory {
   private ParserActionFactory() {
   }

   public static ParserAction makeNormalAction(String var0, Operation var1, String var2) {
      return new NormalParserAction(var0, var1, var2);
   }

   public static ParserAction makePrefixAction(String var0, Operation var1, String var2, Class var3) {
      return new PrefixParserAction(var0, var1, var2, var3);
   }
}

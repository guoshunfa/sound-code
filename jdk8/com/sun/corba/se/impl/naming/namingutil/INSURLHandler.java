package com.sun.corba.se.impl.naming.namingutil;

public class INSURLHandler {
   private static INSURLHandler insURLHandler = null;
   private static final int CORBALOC_PREFIX_LENGTH = 9;
   private static final int CORBANAME_PREFIX_LENGTH = 10;

   private INSURLHandler() {
   }

   public static synchronized INSURLHandler getINSURLHandler() {
      if (insURLHandler == null) {
         insURLHandler = new INSURLHandler();
      }

      return insURLHandler;
   }

   public INSURL parseURL(String var1) {
      if (var1.startsWith("corbaloc:")) {
         return new CorbalocURL(var1.substring(9));
      } else {
         return var1.startsWith("corbaname:") ? new CorbanameURL(var1.substring(10)) : null;
      }
   }
}

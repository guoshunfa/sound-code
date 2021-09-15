package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.ArrayList;
import org.omg.CORBA.BAD_PARAM;

public class CorbanameURL extends INSURLBase {
   private static NamingSystemException wrapper = NamingSystemException.get("naming");

   public CorbanameURL(String var1) {
      String var2 = var1;

      try {
         var2 = Utility.cleanEscapes(var2);
      } catch (Exception var9) {
         this.badAddress(var9);
      }

      int var3 = var2.indexOf(35);
      String var4 = null;
      if (var3 != -1) {
         var4 = "corbaloc:" + var2.substring(0, var3) + "/";
      } else {
         var4 = "corbaloc:" + var2.substring(0, var2.length());
         if (!var4.endsWith("/")) {
            var4 = var4 + "/";
         }
      }

      try {
         INSURL var5 = INSURLHandler.getINSURLHandler().parseURL(var4);
         this.copyINSURL(var5);
         if (var3 > -1 && var3 < var1.length() - 1) {
            int var6 = var3 + 1;
            String var7 = var2.substring(var6);
            this.theStringifiedName = var7;
         }
      } catch (Exception var8) {
         this.badAddress(var8);
      }

   }

   private void badAddress(Throwable var1) throws BAD_PARAM {
      throw wrapper.insBadAddress(var1);
   }

   private void copyINSURL(INSURL var1) {
      this.rirFlag = var1.getRIRFlag();
      this.theEndpointInfo = (ArrayList)var1.getEndpointInfo();
      this.theKeyString = var1.getKeyString();
      this.theStringifiedName = var1.getStringifiedName();
   }

   public boolean isCorbanameURL() {
      return true;
   }
}

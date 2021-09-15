package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CorbalocURL extends INSURLBase {
   static NamingSystemException wrapper = NamingSystemException.get("naming.read");

   public CorbalocURL(String var1) {
      String var2 = var1;
      if (var1 != null) {
         try {
            var2 = Utility.cleanEscapes(var2);
         } catch (Exception var7) {
            this.badAddress(var7);
         }

         int var3 = var2.indexOf(47);
         if (var3 == -1) {
            var3 = var2.length();
         }

         if (var3 == 0) {
            this.badAddress((Throwable)null);
         }

         StringTokenizer var4 = new StringTokenizer(var2.substring(0, var3), ",");

         while(var4.hasMoreTokens()) {
            String var5 = var4.nextToken();
            IIOPEndpointInfo var6 = null;
            if (var5.startsWith("iiop:")) {
               var6 = this.handleIIOPColon(var5);
            } else if (var5.startsWith("rir:")) {
               this.handleRIRColon(var5);
               this.rirFlag = true;
            } else if (var5.startsWith(":")) {
               var6 = this.handleColon(var5);
            } else {
               this.badAddress((Throwable)null);
            }

            if (!this.rirFlag) {
               if (this.theEndpointInfo == null) {
                  this.theEndpointInfo = new ArrayList();
               }

               this.theEndpointInfo.add(var6);
            }
         }

         if (var2.length() > var3 + 1) {
            this.theKeyString = var2.substring(var3 + 1);
         }
      }

   }

   private void badAddress(Throwable var1) {
      throw wrapper.insBadAddress(var1);
   }

   private IIOPEndpointInfo handleIIOPColon(String var1) {
      var1 = var1.substring(4);
      return this.handleColon(var1);
   }

   private IIOPEndpointInfo handleColon(String var1) {
      var1 = var1.substring(1);
      String var2 = var1;
      StringTokenizer var3 = new StringTokenizer(var1, "@");
      IIOPEndpointInfo var4 = new IIOPEndpointInfo();
      int var5 = var3.countTokens();
      if (var5 == 0 || var5 > 2) {
         this.badAddress((Throwable)null);
      }

      if (var5 == 2) {
         String var6 = var3.nextToken();
         int var7 = var6.indexOf(46);
         if (var7 == -1) {
            this.badAddress((Throwable)null);
         }

         try {
            var4.setVersion(Integer.parseInt(var6.substring(0, var7)), Integer.parseInt(var6.substring(var7 + 1)));
            var2 = var3.nextToken();
         } catch (Throwable var10) {
            this.badAddress(var10);
         }
      }

      try {
         int var11 = var2.indexOf(91);
         if (var11 != -1) {
            String var12 = this.getIPV6Port(var2);
            if (var12 != null) {
               var4.setPort(Integer.parseInt(var12));
            }

            var4.setHost(this.getIPV6Host(var2));
            return var4;
         }

         var3 = new StringTokenizer(var2, ":");
         if (var3.countTokens() == 2) {
            var4.setHost(var3.nextToken());
            var4.setPort(Integer.parseInt(var3.nextToken()));
         } else if (var2 != null && var2.length() != 0) {
            var4.setHost(var2);
         }
      } catch (Throwable var9) {
         this.badAddress(var9);
      }

      Utility.validateGIOPVersion(var4);
      return var4;
   }

   private void handleRIRColon(String var1) {
      if (var1.length() != 4) {
         this.badAddress((Throwable)null);
      }

   }

   private String getIPV6Port(String var1) {
      int var2 = var1.indexOf(93);
      if (var2 + 1 != var1.length()) {
         if (var1.charAt(var2 + 1) != ':') {
            throw new RuntimeException("Host and Port is not separated by ':'");
         } else {
            return var1.substring(var2 + 2);
         }
      } else {
         return null;
      }
   }

   private String getIPV6Host(String var1) {
      int var2 = var1.indexOf(93);
      String var3 = var1.substring(1, var2);
      return var3;
   }

   public boolean isCorbanameURL() {
      return false;
   }
}

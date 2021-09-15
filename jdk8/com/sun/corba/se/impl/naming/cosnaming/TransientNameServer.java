package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;

public class TransientNameServer {
   private static boolean debug = false;
   static NamingSystemException wrapper = NamingSystemException.get("naming");

   public static void trace(String var0) {
      if (debug) {
         System.out.println(var0);
      }

   }

   public static void initDebug(String[] var0) {
      if (!debug) {
         for(int var1 = 0; var1 < var0.length; ++var1) {
            if (var0[var1].equalsIgnoreCase("-debug")) {
               debug = true;
               return;
            }
         }

         debug = false;
      }
   }

   private static Object initializeRootNamingContext(ORB var0) {
      java.lang.Object var1 = null;

      try {
         com.sun.corba.se.spi.orb.ORB var2 = (com.sun.corba.se.spi.orb.ORB)var0;
         TransientNameService var3 = new TransientNameService(var2);
         return var3.initialNamingContext();
      } catch (SystemException var4) {
         throw wrapper.transNsCannotCreateInitialNcSys((Throwable)var4);
      } catch (Exception var5) {
         throw wrapper.transNsCannotCreateInitialNc((Throwable)var5);
      }
   }

   public static void main(String[] var0) {
      initDebug(var0);
      boolean var1 = false;
      boolean var2 = false;
      int var3 = 0;

      try {
         trace("Transient name server started with args " + var0);
         Properties var4 = System.getProperties();
         var4.put("com.sun.CORBA.POA.ORBServerId", "1000000");
         var4.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");

         try {
            String var5 = System.getProperty("org.omg.CORBA.ORBInitialPort");
            if (var5 != null && var5.length() > 0) {
               var3 = Integer.parseInt(var5);
               if (var3 == 0) {
                  var2 = true;
                  throw wrapper.transientNameServerBadPort();
               }
            }

            String var6 = System.getProperty("org.omg.CORBA.ORBInitialHost");
            if (var6 != null) {
               var1 = true;
               throw wrapper.transientNameServerBadHost();
            }
         } catch (NumberFormatException var12) {
         }

         for(int var14 = 0; var14 < var0.length; ++var14) {
            if (var0[var14].equals("-ORBInitialPort") && var14 < var0.length - 1) {
               var3 = Integer.parseInt(var0[var14 + 1]);
               if (var3 == 0) {
                  var2 = true;
                  throw wrapper.transientNameServerBadPort();
               }
            }

            if (var0[var14].equals("-ORBInitialHost")) {
               var1 = true;
               throw wrapper.transientNameServerBadHost();
            }
         }

         if (var3 == 0) {
            var3 = 900;
            var4.put("org.omg.CORBA.ORBInitialPort", Integer.toString(var3));
         }

         var4.put("com.sun.CORBA.POA.ORBPersistentServerPort", Integer.toString(var3));
         ORB var15 = ORB.init(var0, var4);
         trace("ORB object returned from init: " + var15);
         Object var16 = initializeRootNamingContext(var15);
         ((com.sun.corba.se.org.omg.CORBA.ORB)var15).register_initial_reference("NamingService", var16);
         String var7 = null;
         if (var16 != null) {
            var7 = var15.object_to_string(var16);
         } else {
            NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", var3));
            NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
            System.exit(1);
         }

         trace("name service created");
         System.out.println(CorbaResourceUtil.getText("tnameserv.hs1", var7));
         System.out.println(CorbaResourceUtil.getText("tnameserv.hs2", var3));
         System.out.println(CorbaResourceUtil.getText("tnameserv.hs3"));
         java.lang.Object var8 = new java.lang.Object();
         synchronized(var8) {
            var8.wait();
         }
      } catch (Exception var13) {
         if (var1) {
            NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.invalidhostoption"));
         } else if (var2) {
            NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.orbinitialport0"));
         } else {
            NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", var3));
            NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
         }

         var13.printStackTrace();
      }

   }

   private TransientNameServer() {
   }
}

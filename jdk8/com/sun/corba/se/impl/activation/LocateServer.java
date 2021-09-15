package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class LocateServer implements CommandHandler {
   static final int illegalServerId = -1;

   public String getCommandName() {
      return "locate";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.locate"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.locate1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      int var4 = -1;
      String var5 = "IIOP_CLEAR_TEXT";

      try {
         int var7 = 0;

         while(var7 < var1.length) {
            String var6 = var1[var7++];
            if (var6.equals("-serverid")) {
               if (var7 >= var1.length) {
                  return true;
               }

               var4 = Integer.valueOf(var1[var7++]);
            } else if (var6.equals("-applicationName")) {
               if (var7 >= var1.length) {
                  return true;
               }

               var4 = ServerTool.getServerIdForAlias(var2, var1[var7++]);
            } else if (var6.equals("-endpointType") && var7 < var1.length) {
               var5 = var1[var7++];
            }
         }

         if (var4 == -1) {
            return true;
         }

         Locator var8 = LocatorHelper.narrow(var2.resolve_initial_references("ServerLocator"));
         ServerLocation var9 = var8.locateServer(var4, var5);
         var3.println(CorbaResourceUtil.getText("servertool.locate2", var9.hostname));
         int var10 = var9.ports.length;

         for(var7 = 0; var7 < var10; ++var7) {
            ORBPortInfo var11 = var9.ports[var7];
            var3.println("\t\t" + var11.port + "\t\t" + var5 + "\t\t" + var11.orbId);
         }
      } catch (NoSuchEndPoint var12) {
      } catch (ServerHeldDown var13) {
         var3.println(CorbaResourceUtil.getText("servertool.helddown"));
      } catch (ServerNotRegistered var14) {
         var3.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      return false;
   }
}

package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListORBs implements CommandHandler {
   static final int illegalServerId = -1;

   public String getCommandName() {
      return "orblist";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.orbidmap"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.orbidmap1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      int var4 = -1;

      try {
         if (var1.length == 2) {
            if (var1[0].equals("-serverid")) {
               var4 = Integer.valueOf(var1[1]);
            } else if (var1[0].equals("-applicationName")) {
               var4 = ServerTool.getServerIdForAlias(var2, var1[1]);
            }
         }

         if (var4 == -1) {
            return true;
         }

         Activator var5 = ActivatorHelper.narrow(var2.resolve_initial_references("ServerActivator"));
         String[] var6 = var5.getORBNames(var4);
         var3.println(CorbaResourceUtil.getText("servertool.orbidmap2"));

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var3.println("\t " + var6[var7]);
         }
      } catch (ServerNotRegistered var8) {
         var3.println("\tno such server found.");
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      return false;
   }
}

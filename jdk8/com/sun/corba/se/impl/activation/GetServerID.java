package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class GetServerID implements CommandHandler {
   public String getCommandName() {
      return "getserverid";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.getserverid"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.getserverid1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      if (var1.length == 2 && var1[0].equals("-applicationName")) {
         String var4 = var1[1];

         try {
            Repository var5 = RepositoryHelper.narrow(var2.resolve_initial_references("ServerRepository"));

            try {
               int var6 = var5.getServerID(var4);
               var3.println();
               var3.println(CorbaResourceUtil.getText("servertool.getserverid2", var4, Integer.toString(var6)));
               var3.println();
            } catch (ServerNotRegistered var7) {
               var3.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         return false;
      } else {
         return true;
      }
   }
}

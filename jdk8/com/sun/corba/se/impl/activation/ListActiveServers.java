package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListActiveServers implements CommandHandler {
   public String getCommandName() {
      return "listactive";
   }

   public void printCommandHelp(PrintStream var1, boolean var2) {
      if (!var2) {
         var1.println(CorbaResourceUtil.getText("servertool.listactive"));
      } else {
         var1.println(CorbaResourceUtil.getText("servertool.listactive1"));
      }

   }

   public boolean processCommand(String[] var1, ORB var2, PrintStream var3) {
      try {
         Repository var5 = RepositoryHelper.narrow(var2.resolve_initial_references("ServerRepository"));
         Activator var6 = ActivatorHelper.narrow(var2.resolve_initial_references("ServerActivator"));
         int[] var7 = var6.getActiveServers();
         var3.println(CorbaResourceUtil.getText("servertool.list2"));
         ListServers.sortServers(var7);

         for(int var8 = 0; var8 < var7.length; ++var8) {
            try {
               ServerDef var4 = var5.getServer(var7[var8]);
               var3.println("\t   " + var7[var8] + "\t\t" + var4.serverName + "\t\t" + var4.applicationName);
            } catch (ServerNotRegistered var10) {
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      return false;
   }
}
